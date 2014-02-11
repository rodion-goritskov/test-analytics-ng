// Copyright 2010 Google Inc. All Rights Reseved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.google.testing.testify.risk.frontend.server.util;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.testing.testify.risk.frontend.server.InsufficientPrivlegesException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility methods for servlets.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ServletUtils {
  private static final String URL =
      System.getProperty("com.google.testing.testify.risk.frontend.url");
  private static final Logger LOG = Logger.getLogger(ServletUtils.class.getName());

  private static final int QUEUE_RETRIES = 10;

  private ServletUtils() {}  // COV_NF_LINE

  /**
   * Throws an access exception if user doesn't have access to do the operation.  EG:
   * ServerletUtils.requireAccess(userService.hasAccess(...)).
   *
   * @param hasAccess
   */
  public static void requireAccess(boolean hasAccess) {
    // TODO(jimr): It'd be nice if this was less clunky.  Figure out a way to do that.
    if (!hasAccess) {
      throw new InsufficientPrivlegesException("You don't have access to perform that action.");
    }
  }

  /**
   * Lists returned from GAE's PersistanceManager are a private type which cannot be marshalled to
   * client-side GWT code. This method simply copies elements from whatever input List<T> into a
   * GWT-friendly detached ArrayList<T>.
   *
   * @param inputList the list to safenize.
   * @param pm the persistence manager.  This is needed in order to create a detached copy.
   * @return a copied, serialization-safe list.
   */
  public static <T> List<T> makeGwtSafe(List<T> inputList, PersistenceManager pm) {
    List<T> copiedList = new ArrayList<T>(inputList.size());
    for (T item : inputList) {
      copiedList.add(pm.detachCopy(item));
    }
    return copiedList;
  }

  /**
   * Lists returned from GAE's PersistanceManager are a private type which cannot be marshalled to
   * client-side GWT code. This method simply copies elements from whatever input <T> into a
   * GWT-friendly detached <T>.
   *
   * @param input the item to safenize.
   * @param pm the persistence manager.  This is needed in order to create a detached copy.
   * @return a copied, serialization-safe item.
   */
  public static <T> T makeGwtSafe(T input, PersistenceManager pm) {
    return pm.detachCopy(input);
  }

  public static boolean queueWithRetries(String queueName, TaskOptions task, String description) {
    Queue queue = QueueFactory.getQueue(queueName);
    for (int i = 0; i < QUEUE_RETRIES; i++) {
      try {
        queue.add(task);
        return true;
      } catch (TransientFailureException e) {
        LOG.warning("Retrying queue add for task due to queue failure: " + description);
      }
    }
    LOG.severe("Could not enqueue task after " + QUEUE_RETRIES + "retries: " + description);
    return false;
  }

  public static void notifyRemovedAccess(String from, List<String> emails, String accessType,
      String project, String projectId) {
    notifyAccessChanged(from, emails, accessType, project, projectId, false);
  }

  public static void notifyAddedAccess(String from, List<String> emails, String accessType,
      String project, String projectId) {
    notifyAccessChanged(from, emails, accessType, project, projectId, true);
  }

  private static void notifyAccessChanged(String from, List<String> emails, String accessType,
      String project, String projectId, boolean isAdded) {
    LOG.info("Trying to message users about change in " + accessType + " access.");
    if (emails.size() < 1) {
      LOG.warning("No emails specified to notify.");
      return;
    }
    try {
      String url = URL + "/#/" + projectId +"/project-settings";
      String verb = isAdded ? "added" : "removed";
      String change = "You have been " + verb + " as an " + accessType
          + " of Test Analytics Project: " + project;

      Session session = Session.getDefaultInstance(new Properties(), null);
      for (String email : emails) {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from, "Test Analytics on behalf of " + from));
        LOG.info("Sending email to " + email + "(" + change + ")");
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject(change);
        msg.setText(change + "\n\nThis Test Analytics project's URL:\n\n" + url
            + "\n\nIf you believe this was a mistake, contact " + from + " who made the change.");

        if (SystemProperty.environment.value().equals(
            SystemProperty.Environment.Value.Production)) {
          Transport.send(msg);
        } else {
          LOG.info("Not actually sending email; not in production environment.");
        }
      }
    } catch (UnsupportedEncodingException e) {  // COV_NF_START

      LOG.severe("Couldn't send email.");
    } catch (AddressException e) {
      LOG.severe("Couldn't send email.");
    } catch (MessagingException e) {
      LOG.severe("Couldn't send email.");
    }  // COV_NF_END
  }
}
