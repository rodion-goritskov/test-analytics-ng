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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView.Presenter;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;
import com.google.testing.testify.risk.frontend.testing.EasyMockUtils;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * Unit tests for the ProjectSettings presenter.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectSettingsPresenterTest extends TestCase {

  @SuppressWarnings("unchecked")
  public void testSetPresenter() {
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    ProjectRpcAsync projServiceObserver = EasyMock.createMock(ProjectRpcAsync.class);
    ProjectSettingsView viewObserver = EasyMock.createMock(ProjectSettingsView.class);
    Project project = new Project();
    project.setProjectId(42L);

    securityService.getAccessLevel(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(ProjectAccess.VIEW_ACCESS);
    viewObserver.setPresenter((Presenter) EasyMock.anyObject());
    viewObserver.setProjectSettings(project);
    viewObserver.enableProjectEditing(ProjectAccess.VIEW_ACCESS);
    EasyMock.replay(viewObserver, securityService);

    ProjectSettingsPresenter presenter = new ProjectSettingsPresenter(
        project, projServiceObserver, securityService, viewObserver, null);

    EasyMock.verify(viewObserver, securityService);
  }
}
