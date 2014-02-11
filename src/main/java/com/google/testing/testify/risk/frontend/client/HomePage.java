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


package com.google.testing.testify.risk.frontend.client;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.view.widgets.ProjectFavoriteStar;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageHeaderWidget;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.TestProjectCreatorRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.TestProjectCreatorRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.List;

/**
 * Home page widget for Test Analytics.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class HomePage extends Composite {

  /**
   * Used to wire parent class to associated UI Binder.
   */
  interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}
  private static final HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

  @UiField
  protected ListBox userProjectsListBox;

  @UiField
  protected CheckBox justMyProjectsCheckbox;

  @UiField
  protected Grid projectsGrid;

  @UiField
  protected Button createProjectButton;

  @UiField
  protected Button createTestProjectButton;

  @UiField
  protected Button createDataSourcesButton;

  @UiField
  protected DeckPanel newProjectPanel;

  @UiField
  protected TextBox newProjectName;

  @UiField
  protected Button newProjectOkButton;

  @UiField
  protected Button newProjectCancelButton;

  @UiField
  public VerticalPanel adminPanel;

  private List<Project> allProjects;
  private List<Long> starredProjects;

  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private TestProjectCreatorRpcAsync creatorService = null;
  private static final int GRID_COLUMNS = 3;

  // The widgets that are a part of the DeckPanel are created through the UI binder, in this order.
  private static final int DECK_WIDGET_NEW_BUTTON = 0;
  private static final int DECK_WIDGET_NAME_PANEL = 1;

  /**
   * Constructs a HomePage object.
   */
  public HomePage(ProjectRpcAsync projectService, UserRpcAsync userService) {
    this.projectService = projectService;
    this.userService = userService;
    initWidget(uiBinder.createAndBindUi(this));

    newProjectPanel.setAnimationEnabled(true);
    // The widgets that are a part of the DeckPanel are created through the UI binder.
    newProjectPanel.showWidget(DECK_WIDGET_NEW_BUTTON);

    justMyProjectsCheckbox.setValue(false);
    loadProjectList();
    setAdminPanelVisibleIfAdmin();
  }

  @UiFactory
  public PageHeaderWidget createPageHeaderWidget() {
    return new PageHeaderWidget(userService);
  }

  @UiHandler("createDataSourcesButton")
  public void onCreateDataSourcesClicked(ClickEvent event) {
    if (creatorService == null) {
      creatorService = GWT.create(TestProjectCreatorRpc.class);
    }
    creatorService.createStandardDataSources(TaCallback.getNoopCallback());
  }

  @UiHandler("justMyProjectsCheckbox")
  void onJustMyProjectsCheckboxClicked(ClickEvent event) {
    updateProjectsList();
  }

  @UiHandler("newProjectName")
  protected void onNewProjectEnter(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      doCreateProject(newProjectName.getText());
    }
  }

  @UiHandler("newProjectOkButton")
  protected void onNewProjectOkClicked(ClickEvent event) {
    doCreateProject(newProjectName.getText());
  }

  @UiHandler("newProjectCancelButton")
  protected void onNewProjectCancelClicked(ClickEvent event) {
    newProjectName.setText("");
    newProjectPanel.showWidget(DECK_WIDGET_NEW_BUTTON);
  }

  @UiHandler("createProjectButton")
  void onCreateProjectButtonClicked(ClickEvent event) {
    newProjectPanel.showWidget(DECK_WIDGET_NAME_PANEL);
    newProjectName.setFocus(true);
  }

  @UiHandler("userProjectsListBox")
  void onSelectNewProject(ChangeEvent event) {
    int selectedIndex = userProjectsListBox.getSelectedIndex();
    gotoProject(userProjectsListBox.getValue(selectedIndex));
  }

  private void loadProjectList() {
    userService.getStarredProjects(
        new TaCallback<List<Long>>("querying starred projects") {
          @Override
          public void onSuccess(List<Long> result) {
            starredProjects = result;
            updateProjectsList();
          }
        });

    displayMessageInGrid("Loading project list...");
    projectService.query("",
      new TaCallback<List<Project>>("querying projects") {
      @Override
      public void onSuccess(List<Project> result) {
        allProjects = result;
        updateProjectsList();
      }
    });
  }

  private void setAdminPanelVisibleIfAdmin() {
    userService.hasAdministratorAccess(new AsyncCallback<Boolean>() {
      @Override
      public void onSuccess(Boolean result) {
        if (result) {
          adminPanel.setVisible(true);
        }
      }

      @Override
      public void onFailure(Throwable caught) {
        return;
      }
    });
    userService.isDevMode(new AsyncCallback<Boolean>() {
      @Override
      public void onSuccess(Boolean result) {
        if (result) {
          adminPanel.setVisible(true);
        }
      }

      @Override
      public void onFailure(Throwable caught) {
        return;
      }
    });
  }

  private void doCreateProject(String projectName) {
    projectName = projectName.trim();
    if (projectName.length() < 1) {
      Window.alert("Enter a valid project name.");
      return;
    }

    final Project newProject = new Project();
    newProject.setName(projectName);

    projectService.createProject(newProject,
        new TaCallback<Long>("creating new project") {
          @Override
          public void onSuccess(Long result) {
            newProject.setProjectId(result);
            gotoProject(result.toString());
          }
        });
  }

  @UiHandler("createTestProjectButton")
  void onCreateTestProjectButtonClicked(ClickEvent event) {
    if (creatorService == null) {
      creatorService = GWT.create(TestProjectCreatorRpc.class);
    }
    creatorService.createTestProject(
        new TaCallback<Project>("creating new project") {
          @Override
          public void onSuccess(Project result) {
            gotoProject(result.getProjectId().toString());
          }
        });
  }

  /**
   * Displays a single message in place of the projects grid.
   */
  private void displayMessageInGrid(String message) {
    projectsGrid.clear();
    projectsGrid.resize(1, 1);
    projectsGrid.setWidget(0, 0, new Label(message));
  }

  /**
   * Fills the list of projects on the HomePage widget with the given projects. Also replaces the
   * label describing the projects with the provided text.
   */
  private void updateProjectsList() {
    if (allProjects != null && starredProjects != null) {
      List<Project> userProjects = Lists.newArrayList();
      List<Project> publicProjects = Lists.newArrayList();
      for (Project p : allProjects) {
        if (p.getCachedAccessLevel().hasAccess(ProjectAccess.EXPLICIT_VIEW_ACCESS)) {
          userProjects.add(p);
        } else if (starredProjects.contains(p.getProjectId())) {
          userProjects.add(p);
        } else {
          publicProjects.add(p);
        }
      }

      // Update drop-down.
      userProjectsListBox.clear();
      userProjectsListBox.addItem("", "");
      for (Project p : userProjects) {
        userProjectsListBox.addItem(p.getName(), p.getProjectId().toString());
      }
      userProjectsListBox.setSelectedIndex(0);

      // Update the grid.
      if (justMyProjectsCheckbox.getValue().equals(false)) {
        userProjects.addAll(publicProjects);
      }

      // Special case for zero projects.
      if (userProjects.size() < 1) {
        displayMessageInGrid("No projects to display.");
        return;
      }

      projectsGrid.clear();
      int rows = ((userProjects.size() - 1) / GRID_COLUMNS) + 1;
      projectsGrid.resize(rows, GRID_COLUMNS);


      for (int projectIndex = 0; projectIndex < userProjects.size(); projectIndex++) {
        final Project project = userProjects.get(projectIndex);

        ProjectFavoriteStar favoriteStar = new ProjectFavoriteStar();
        favoriteStar.attachToProject(project.getProjectId());
        if (starredProjects.contains(project.getProjectId())) {
          favoriteStar.setStarredStatus(true);
        }

        Anchor nameWidget = new Anchor(project.getName());
        nameWidget.addStyleName("tty-HomePageProjectsGridProjectName");

        HorizontalPanel projectWidget = new HorizontalPanel();
        projectWidget.add(favoriteStar);
        projectWidget.add(nameWidget);
        projectWidget.addStyleName("tty-HomePageProjectsGridProject");

        nameWidget.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            gotoProject(project.getProjectId().toString());
          }
        });

        int targetColumn = projectIndex % GRID_COLUMNS;
        int targetRow = projectIndex / GRID_COLUMNS;
        projectsGrid.setWidget(targetRow, targetColumn, projectWidget);
      }
    }
  }

  private void gotoProject(String projectId) {
    History.newItem("/" + projectId + "/project-details");
  }
}
