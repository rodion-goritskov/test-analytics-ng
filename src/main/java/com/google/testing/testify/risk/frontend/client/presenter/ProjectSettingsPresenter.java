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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.event.ProjectChangedEvent;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.List;

/**
 * Presenter for the ProjectSettings widget.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectSettingsPresenter
    extends BasePagePresenter
    implements ProjectSettingsView.Presenter, TaPagePresenter {

  private final Project project;
  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private final ProjectSettingsView view;
  private final EventBus eventBus;

  /**
   * Constructs the presenter.
   */
  public ProjectSettingsPresenter(Project project, ProjectRpcAsync projectService,
      UserRpcAsync userService, ProjectSettingsView view, EventBus eventBus) {

    this.project = project;
    this.projectService = projectService;
    this.userService = userService;
    this.view = view;
    this.eventBus = eventBus;

    refreshView();
  }

  /**
   * Query the database for project information and populate UI fields.
   */
  @Override
  public void refreshView() {
    view.setPresenter(this);
    view.setProjectSettings(project);

    userService.getAccessLevel(project.getProjectId(),
        new TaCallback<ProjectAccess>("Checking User Access") {
          @Override
          public void onSuccess(ProjectAccess result) {
            view.enableProjectEditing(result);
          }
        });
  }

  /** Returns the underlying view. */
  @Override
  public Widget getView() {
    return view.asWidget();
  }

  @Override
  public ProjectRpcAsync getProjectService() {
    return projectService;
  }

  @Override
  public long getProjectId() {
    return project.getProjectId();
  }

  /**
   * To be called by the View when the user performs the updateProjectInfo action.
   */
  @Override
  public void onUpdateProjectInfoClicked(
      String name, String description,
      List<String> projectOwners, List<String> projectEditors, List<String> projectViewers,
      boolean isPublicalyVisible) {

    project.setName(name);
    project.setDescription(description);
    project.setIsPubliclyVisible(isPublicalyVisible);
    project.setProjectOwners(projectOwners);
    project.setProjectEditors(projectEditors);
    project.setProjectViewers(projectViewers);

    projectService.updateProject(project,
        new TaCallback<Void>("Updating Project") {
          @Override
          public void onSuccess(Void result) {
            view.showSaved();
            eventBus.fireEvent(new ProjectChangedEvent(project));
          }
        });
  }

  @Override
  public void removeProject() {
    projectService.removeProject(project, TaCallback.getNoopCallback());
  }
}
