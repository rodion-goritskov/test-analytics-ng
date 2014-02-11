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


package com.google.testing.testify.risk.frontend.client.view.impl;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.testing.testify.risk.frontend.client.TaClientTest;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import junit.framework.Assert;

import java.util.List;

/**
 * Unit tests the ProjectSettingsViewImpl class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectSettingsViewImplTest extends TaClientTest {

  /**
   * Mock of the Presenter interface. Since this unit test touches UI it must run as a GWTTestcase,
   * and therefore cannot use EasyMock.
   */
  private class MockPresenter implements ProjectSettingsView.Presenter {

    public String lastProjectName;
    public String lastProjectDescription;
    public boolean lastPublicState;

    @Override
    public void onUpdateProjectInfoClicked(
            String projectName, String projectDescription,
            List<String> owners, List<String> editors, List<String> viewers,
            boolean isPublic) {
      lastProjectName = projectName;
      lastProjectDescription = projectDescription;
      lastPublicState = isPublic;
    }

    @Override
    public long getProjectId() {
      return 0;
    }

    @Override
    public void removeProject() {}

    @Override
    public ProjectRpcAsync getProjectService() {
      return null;
    }
  }

  // Verify that the UI is updated after setProjectSettings has been called.
  public void testProjectInformationIsDisplayedAfterBeingSet() {
    ProjectSettingsViewImpl view = new ProjectSettingsViewImpl();

    Assert.assertEquals("", view.projectName.getText());
    Assert.assertEquals("", view.projectDescription.getText());

    Project projectInformation = new Project();
    projectInformation.setProjectId(100L);
    projectInformation.setName("name");
    projectInformation.setDescription("desc");
    view.setProjectSettings(projectInformation);

    Assert.assertEquals("name", view.projectName.getText());
    Assert.assertEquals("desc", view.projectDescription.getText());
  }

  // Verify that the View's Presenter is notified when the update button is clicked.
  public void testPresenterNotifiedOnSave() {
    MockPresenter mockPresenter = new MockPresenter();

    Project project = new Project();
    project.addProjectOwner("test@example.com");

    ProjectSettingsViewImpl projectSettings = new ProjectSettingsViewImpl();
    projectSettings.setProjectSettings(project);
    projectSettings.setPresenter(mockPresenter);
    projectSettings.enableProjectEditing(ProjectAccess.OWNER_ACCESS);
    projectSettings.projectName.setText("name");
    projectSettings.projectDescription.setText("description");
    projectSettings.projectIsPublicCheckBox.setValue(true);

    // In order for events to fire you need to add the widget to the root panel.
    RootPanel.get().clear();
    RootPanel.get().add(projectSettings);
    projectSettings.updateProjectInfoButton.click();

    Assert.assertEquals("name", mockPresenter.lastProjectName);
    Assert.assertEquals("description", mockPresenter.lastProjectDescription);
    Assert.assertEquals(true, mockPresenter.lastPublicState);
  }
}
