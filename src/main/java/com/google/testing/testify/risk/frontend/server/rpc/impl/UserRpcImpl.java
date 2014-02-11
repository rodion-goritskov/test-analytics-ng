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


package com.google.testing.testify.risk.frontend.server.rpc.impl;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.LoginStatus;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc;

import java.util.List;

/**
 * Service allowing access to user / login data.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class UserRpcImpl extends RemoteServiceServlet implements UserRpc {

  private final UserService userService;

  @Inject
  public UserRpcImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public ProjectAccess getAccessLevel(long projectId) {
    return userService.getAccessLevel(projectId);
  }

  @Override
  public LoginStatus getLoginStatus(String returnUrl) {
    return userService.getLoginStatus(returnUrl);
  }

  @Override
  public List<Long> getStarredProjects() {
    return userService.getStarredProjects();
  }

  @Override
  public boolean hasAdministratorAccess() {
    return userService.hasAdministratorAccess();
  }

  @Override
  public boolean hasEditAccess(long projectId) {
    return userService.hasEditAccess(projectId);
  }

  @Override
  public boolean isDevMode() {
    return userService.isDevMode();
  }

  @Override
  public void starProject(long projectId) {
    userService.starProject(projectId);
  }

  @Override
  public void unstarProject(long projectId) {
    userService.unstarProject(projectId);
  }
}
