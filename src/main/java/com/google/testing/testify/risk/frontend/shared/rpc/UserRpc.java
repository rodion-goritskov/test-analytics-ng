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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.testing.testify.risk.frontend.model.LoginStatus;

import java.util.List;

/**
 * Interface for updating user information between client and server.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@RemoteServiceRelativePath("service/user")
public abstract interface UserRpc extends RemoteService {
  public boolean isDevMode();

  public LoginStatus getLoginStatus(String returnUrl);
  public ProjectAccess getAccessLevel(long projectId);
  public boolean hasAdministratorAccess();
  public boolean hasEditAccess(long projectId);
  
  public List<Long> getStarredProjects();
  public void starProject(long projectId);
  public void unstarProject(long projectId);

  public enum ProjectAccess {
    ADMINISTRATOR_ACCESS,
    OWNER_ACCESS,
    EDIT_ACCESS,
    EXPLICIT_VIEW_ACCESS,
    VIEW_ACCESS,
    NO_ACCESS;

    /** Returns whether or not this access level can perform the specified access type. */
    public boolean hasAccess(ProjectAccess testAccess) {
      return (this.ordinal() <= testAccess.ordinal());
    }
  }
}
