// Copyright 2011 Google Inc. All Rights Reseved.
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


package com.google.testing.testify.risk.frontend.server.api.impl;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.task.UploadDataTask;
import com.google.testing.testify.risk.frontend.server.util.ServletUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet accepts user data (JSON encoded) and passes it off to a task which does the actual
 * processing.  {@link UploadDataTask}
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class UploadApiImpl extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(UploadApiImpl.class.getName());

  private final UserService userService;

  @Inject
  public UploadApiImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    LOG.info("UploadDataServlet::GET called, returning unsupported exception.");
    error(resp, "<h1>GET is unsupported</h1>\nTo upload data, use POST.");
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    BufferedReader reader = req.getReader();
    StringBuilder input = new StringBuilder();
    while (reader.ready()) {
      input.append(req.getReader().readLine());
    }
    LOG.info("Input received: " + input.toString());
    JSONArray json;
    try {
      json = new JSONArray(input.toString());
    } catch (JSONException e) {
      LOG.warning("Couldn't parse JSON: " + e.toString());
      error(resp, "Malformed JSON could not be parsed: " + e.toString());
      return;
    }
    LOG.info("JSON received: " + json.toString());
    JSONObject o;
    TaskOptions task;
    String email = userService.getEmail();
    for (int i = 0; i < json.length(); i++) {
      try {
        o = json.getJSONObject(i);
        task = TaskOptions.Builder.withUrl(UploadDataTask.URL).method(Method.POST)
            .param("json", o.toString())
            .param("user", email);
        ServletUtils.queueWithRetries(UploadDataTask.QUEUE, task, "Processing data item upload");
      } catch (JSONException e) {
        LOG.warning("Couldn't parse item " + i + " in JSON array: " + e.toString());
        resp.getOutputStream().print("<p>Couldn't parse item " + i + "</p>\n");
      }
    }
  }

  private void error(HttpServletResponse resp, String errorText) throws IOException {
    resp.getOutputStream().print(errorText);
    resp.sendError(500);
    return;
  }
}
