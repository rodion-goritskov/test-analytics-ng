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


package com.google.testing.testify.risk.frontend.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import java.util.List;

/**
 * View on top of the ProjectSettings Widget.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface ProjectSettingsView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {

    /**
     * @return the ProjectID for the project the View is displaying.
     */
    long getProjectId();

    /**
     * @return a handle to the Presenter's ProjectService serverlet. Ideally this should be hidden.
     */
    ProjectRpcAsync getProjectService();

    /**
     * Updates the project name, summary, and description get updated in one batch action.
     */
    void onUpdateProjectInfoClicked(
            String projectName, String projectDescription,
            List<String> projectOwners, List<String> projectEditors, List<String> projectViewers,
            boolean isPublicalyVisible);

    /**
     * Deletes the Project. Proceed with caution.
     */
    public void removeProject();
  }

  /**
   * Shows the "project saved" panel.
   */
  public void showSaved();

  /**
   * Updates the view to enable editing of project data. (Security will still be checked in the
   * backend servlet.)
   */
  public void enableProjectEditing(ProjectAccess userAccessLevel);

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  public void setPresenter(Presenter presenter);

  /**
   * Initialize user interface elements with the given project settings.
   */
  public void setProjectSettings(Project projectSettings);

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
}
