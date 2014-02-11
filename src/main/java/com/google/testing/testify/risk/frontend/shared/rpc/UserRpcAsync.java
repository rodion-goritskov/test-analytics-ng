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


package com.google.testing.testify.risk.frontend.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.testing.testify.risk.frontend.model.LoginStatus;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import java.util.List;

/**
 * Async interface for updating user information between client and server.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface UserRpcAsync {
  public void isDevMode(AsyncCallback<Boolean> callback);

  public void getLoginStatus(String returnUrl, AsyncCallback<LoginStatus> callback);
  public void getAccessLevel(long projectId, AsyncCallback<ProjectAccess> callback);
  public void hasAdministratorAccess(AsyncCallback<Boolean> callback);
  public void hasEditAccess(long projectId, AsyncCallback<Boolean> callback);

  public void getStarredProjects(AsyncCallback<List<Long>> callback);
  public void starProject(long projectId, AsyncCallback<Void> callback);
  public void unstarProject(long projectId, AsyncCallback<Void> callback);
}