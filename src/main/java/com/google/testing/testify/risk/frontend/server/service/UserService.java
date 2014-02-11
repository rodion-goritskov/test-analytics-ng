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


package com.google.testing.testify.risk.frontend.server.service;

import com.google.testing.testify.risk.frontend.model.LoginStatus;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import java.util.List;

/**
 * Server service for accessing user details or permissions.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public interface UserService {
  public boolean isUserLoggedIn();
  public boolean isInternalUser();
  public String getEmail();
  public LoginStatus getLoginStatus(String returnUrl);
  public boolean isWhitelistingEnabled();
  public boolean isWhitelisted();

  public boolean isDevMode();

  public ProjectAccess getAccessLevel(long projectId);
  public ProjectAccess getAccessLevel(Project project);
  public ProjectAccess getAccessLevel(long projectId, String asEmail);

  public boolean hasAdministratorAccess();
  public boolean hasViewAccess(long projectId);
  public boolean hasViewAccess(Project project);
  public boolean hasEditAccess(long projectId);
  public boolean hasEditAccess(Project project);
  public boolean hasEditAccess(long projectId, String asEmail);
  public boolean hasOwnerAccess(long projectId);
  public boolean hasAccess(ProjectAccess accessLevel, long project);
  public boolean hasAccess(ProjectAccess accessLevel, long project, String asEmail);

  public List<Long> getStarredProjects();
  public void starProject(long projectId);
  public void unstarProject(long projectId);
}
