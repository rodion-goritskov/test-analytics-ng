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


package com.google.testing.testify.risk.frontend.server.service.impl;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.LoginStatus;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.UserInfo;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Implementation of UserService. Returns user and security information.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class UserServiceImpl implements UserService {

  private static final String LOCAL_DOMAIN =
      System.getProperty("com.google.testing.testify.risk.frontend.localdomain");
  private static final boolean WHITELISTING_ENABLED =
      Boolean.valueOf(System.getProperty("com.google.testing.testify.risk.frontend.whitelisting"));

  private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());
  private final PersistenceManagerFactory pmf;
  private final com.google.appengine.api.users.UserService userService;

  @Inject
  public UserServiceImpl(PersistenceManagerFactory pmf) {
    this.pmf = pmf;
    // TODO(jimr): Inject this.
    this.userService = UserServiceFactory.getUserService();
  }

  @Override
  public boolean isUserLoggedIn() {
    return getEmail() != null;
  }

  @Override
  public String getEmail() {
    User user = userService.getCurrentUser();
    return user == null ? null : user.getEmail();
  }

  @Override
  public boolean isWhitelistingEnabled() {
    return WHITELISTING_ENABLED;
  }

  @Override
  public boolean isWhitelisted() {
    if (isInternalUser()) {
      return true;
    }
    UserInfo user = getCurrentUserInfo(pmf.getPersistenceManager(), false);
    if (user == null) {
      return false;
    }
    return user.getIsWhitelisted();
  }

  @Override
  public boolean isInternalUser() {
    if (isDevMode()) {
      return true;
    }
    String email = getEmail();
    if (email == null || LOCAL_DOMAIN == null) {
      return false;
    }
    return email.endsWith(LOCAL_DOMAIN);
  }

  @Override
  public LoginStatus getLoginStatus(String returnUrl) {
    User user = userService.getCurrentUser();
    String email = null;
    String url;

    if (user == null) {
      email = "";
      url = userService.createLoginURL(returnUrl);
    } else {
      email = user.getEmail();
      url = userService.createLogoutURL(returnUrl);
    }
    return new LoginStatus(user != null, url, email);
  }

  @Override
  public boolean hasAdministratorAccess() {
    return userService.isUserAdmin();
  }

  @Override
  public boolean hasViewAccess(long projectId) {
    return hasAccess(ProjectAccess.VIEW_ACCESS, projectId);
  }

  @Override
  public boolean hasViewAccess(Project project) {
    return hasAccess(ProjectAccess.VIEW_ACCESS, project, getEmail());
  }

  @Override
  public boolean hasEditAccess(long projectId) {
    return hasEditAccess(projectId, getEmail());
  }

  @Override
  public boolean hasEditAccess(Project project) {
    return hasAccess(ProjectAccess.VIEW_ACCESS, project, getEmail());
  }

  @Override
  public boolean hasEditAccess(long projectId, String asEmail) {
    return hasAccess(ProjectAccess.EDIT_ACCESS, projectId, asEmail);
  }

  @Override
  public boolean hasOwnerAccess(long projectId) {
    return hasAccess(ProjectAccess.OWNER_ACCESS, projectId);
  }

  @Override
  public boolean hasAccess(ProjectAccess accessLevel, long projectId) {
    return hasAccess(accessLevel, projectId, getEmail());
  }

  @Override
  public boolean hasAccess(ProjectAccess accessLevel, long projectId, String asEmail) {
    return hasAccess(accessLevel, getProject(projectId), asEmail);
  }

  private boolean hasAccess(ProjectAccess accessLevel, Project project, String asEmail) {
    if (project == null) {
      log.warning("Call to hasAccess with a null project.");
      return false;
    }

    ProjectAccess accessHas = getAccessLevel(project, asEmail);
    log.info("Access has: " + accessHas.name() + " Access desired: " + accessLevel.name());
    return accessHas.hasAccess(accessLevel);
  }

  @Override
  public ProjectAccess getAccessLevel(long projectId) {
    return getAccessLevel(getProject(projectId), getEmail());
  }

  @Override
  public ProjectAccess getAccessLevel(Project project) {
    return getAccessLevel(project, getEmail());
  }

  @Override
  public ProjectAccess getAccessLevel(long projectId, String asEmail) {
    return getAccessLevel(getProject(projectId), asEmail);
  }

  private ProjectAccess getAccessLevel(Project project, String asEmail) {
    if (project == null) {
      log.warning("Call to getAccessLevel with a null project.");
      return ProjectAccess.NO_ACCESS;
    }

    if (asEmail == null) {
      if (project.getIsPubliclyVisible()) {
        return ProjectAccess.VIEW_ACCESS;
      } else {
        return ProjectAccess.NO_ACCESS;
      }
    } else {
      if (hasAdministratorAccess()) {
        return ProjectAccess.ADMINISTRATOR_ACCESS;
      } else if (project.getProjectOwners().contains(asEmail)) {
        return ProjectAccess.OWNER_ACCESS;
      } else if (project.getProjectEditors().contains(asEmail)) {
        return ProjectAccess.EDIT_ACCESS;
      } else if (project.getProjectViewers().contains(asEmail)) {
        return ProjectAccess.EXPLICIT_VIEW_ACCESS;
      } else if (project.getIsPubliclyVisible()) {
        return ProjectAccess.VIEW_ACCESS;
      } else {
        return ProjectAccess.NO_ACCESS;
      }
    }
  }

  @Override
  public List<Long> getStarredProjects() {
    log.info("Getting starred projects for current user.");

    PersistenceManager pm = pmf.getPersistenceManager();
    List<Long> starredProjects = Lists.newArrayList();
    try {
      UserInfo userInfo = getCurrentUserInfo(pm, true);

      // Copy list items over since we cannot return the server-side list type to client-side code.
      if (userInfo != null) {
        for (long projectId : userInfo.getStarredProjects()) {
          starredProjects.add(projectId);
        }
      }
    } finally  {
      pm.close();
    }

    return starredProjects;
  }

  @Override
  public void starProject(long projectId) {
    log.info("Starring project: " + projectId);

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      UserInfo userInfo = getCurrentUserInfo(pm, true);
      if (userInfo != null) {
        userInfo.starProject(projectId);
        pm.makePersistent(userInfo);
      }
    } finally  {
      pm.close();
    }
  }

  @Override
  public void unstarProject(long projectId) {
    log.info("Unstarring project: " + projectId);

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      UserInfo userInfo = getCurrentUserInfo(pm, true);
      if (userInfo != null) {
        userInfo.unstarProject(projectId);
        pm.makePersistent(userInfo);
      }
    } finally  {
      pm.close();
    }
  }

  @Override
  public boolean isDevMode() {
    Value env = SystemProperty.environment.value();
    log.info("System environment: " + env.toString());
    return env.equals(SystemProperty.Environment.Value.Development);
  }

  /**
   * Returns all information Testify knows about the currently logged in user. If the logged in user
   * is not currently in Testify, a new UserInfo entry will be created. Also, will return null if
   * the user is not currently logged in.
   *
   * @param pm The PersistenceManager session for which the UserInfo object was returned.
   */
  private UserInfo getCurrentUserInfo(PersistenceManager pm, boolean createIfMissing) {
    User appEngineUser = userService.getCurrentUser();

    if (appEngineUser == null) {
      log.info("Unable to get user info. User is not logged in.");
      return null;
    }

    // If they are logged in, get the Testify UserInfo record on file. If unavailable, create a new
    // entry.
    String loggedInUserId = appEngineUser.getUserId();
    String currentEmail = appEngineUser.getEmail();

    Query jdoQuery = pm.newQuery(UserInfo.class);
    jdoQuery.declareParameters("String userIdParam");
    jdoQuery.setFilter("userId == userIdParam");

    log.info("Querying for user " + currentEmail + " by id " + loggedInUserId);
    UserInfo user = queryAndReturnFirst(jdoQuery, loggedInUserId);
    if (user != null) {
      // Update email address if missing or different.
      if (!currentEmail.equals(user.getCurrentEmail())) {
        log.info("Adding or updating email for " + currentEmail + "  id " + loggedInUserId);
        user.setCurrentEmail(currentEmail);
        pm.makePersistent(user);
      }
    } else {
      // Try to find by email instead.
      log.info("Querying for user " + currentEmail + " by email instead.");
      jdoQuery = pm.newQuery(UserInfo.class);
      jdoQuery.declareParameters("String currentEmailParam");
      jdoQuery.setFilter("currentEmail == currentEmailParam");

      user = queryAndReturnFirst(jdoQuery, currentEmail);
      if (user != null) {
        // Add the user's user ID since we now know it.
        if (user.getUserId() == null || user.getUserId().equals("")) {
          log.info("Adding user ID for " + currentEmail + ".");
          user.setUserId(appEngineUser.getUserId());
          pm.makePersistent(user);
        } else {
          log.severe("Found a user by email but user ID was set to a different ID.");
          user = null;
        }
      }
    }
    if (user == null) {
      log.info("Tried to get info for " + loggedInUserId + " but no entry was found.");
      if (createIfMissing) {
        log.info("Creating new UserInfo for user: " + loggedInUserId);
        user = new UserInfo();
        user.setUserId(loggedInUserId);
        user.setCurrentEmail(currentEmail);
        pm.makePersistent(user);
      }
    }
    return user;
  }

  @SuppressWarnings("unchecked")
  private UserInfo queryAndReturnFirst(Query query, String param) {
    List<UserInfo> users = (List<UserInfo>) query.execute(param);
    if (users.size() > 0) {
      return users.get(0);
    }
    return null;
  }

  /**
   * Loads a project.  This isn't done using ProjectServiceImpl because it would create a circular
   * dependency.
   *
   * @param id projectId to load.
   * @return the loaded project.
   */
  private Project getProject(long id) {
    // TODO(jimr): To reduce this code duplication, project loading should be done at a lower level
    // so that object can be injected both here and into project service.
    log.info("Getting project: " + Long.toString(id));

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      return pm.getObjectById(Project.class, id);
    } finally {
      pm.close();
    }
  }
}
