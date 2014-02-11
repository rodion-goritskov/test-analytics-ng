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

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.event.ProjectElementAddedEvent;
import com.google.testing.testify.risk.frontend.client.event.ProjectHasNoElementsEvent;
import com.google.testing.testify.risk.frontend.client.view.ComponentsView;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * Presenter for the Components page.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentsPresenter extends BasePagePresenter
    implements TaPagePresenter, ComponentsView.Presenter {

  private final Project project;
  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private final DataRpcAsync dataService;
  private final ComponentsView view;
  private final EventBus eventBus;

  private final Collection<String> projectLabels = Lists.newArrayList();

  /**
   * Constructs the presenter.
   */
  public ComponentsPresenter(
        Project project, ProjectRpcAsync projectService, UserRpcAsync userService,
        DataRpcAsync dataService, ComponentsView view, EventBus eventBus) {

    this.project = project;
    this.projectService = projectService;
    this.userService = userService;
    this.dataService = dataService;
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

    userService.hasEditAccess(project.getProjectId(),
      new TaCallback<Boolean>("Checking User Access") {
        @Override
        public void onSuccess(Boolean result) {
          // Assume the user already has VIEW access, otherwise the RPC service wouldn't have
          // served the Project object in the first place.
          if (result) {
            view.enableEditing();
          }
        }
      });

    projectService.getProjectComponents(project.getProjectId(),
        new TaCallback<List<Component>>("Querying Components") {
          @Override
          public void onSuccess(List<Component> result) {
            if (result.size() == 0) {
              eventBus.fireEvent(
                  new ProjectHasNoElementsEvent(project, AccElementType.COMPONENT));
            }
            view.setProjectComponents(result);
          }
        });

    dataService.getSignoffsByType(project.getProjectId(), AccElementType.COMPONENT,
        new TaCallback<List<Signoff>>("Retrieving signoff data") {
          @Override
          public void onSuccess(List<Signoff> results) {
            view.setSignoffs(results);
          }
        });

    projectService.getLabels(project.getProjectId(),
        new TaCallback<List<AccLabel>>("Loading labels") {
          @Override
          public void onSuccess(List<AccLabel> result) {
            for (AccLabel l : result) {
              projectLabels.add(l.getLabelText());
            }
            view.setProjectLabels(projectLabels);
          }
        });
  }

  /** Returns the underlying view. */
  @Override
  public Widget getView() {
    return view.asWidget();
  }

  @Override
  public long getProjectId() {
    return project.getProjectId();
  }

  @Override
  public void createComponent(final Component component) {
    projectService.createComponent(component,
        new TaCallback<Long>("Creating Component") {
          @Override
          public void onSuccess(Long result) {
            component.setComponentId(result);
            refreshView();
            eventBus.fireEvent(new ProjectElementAddedEvent(component));
          }
        });
  }

  /**
   * Updates the given component in the database.
   */
  @Override
  public void updateComponent(Component componentToUpdate) {
    projectService.updateComponent(componentToUpdate,
        new TaCallback<Component>("updating component") {
          @Override
          public void onSuccess(Component result) {
            view.refreshComponent(result);
          }
        });
  }

  @Override
  public void updateSignoff(Component component, boolean newSignoff) {
    dataService.setSignedOff(component.getParentProjectId(), AccElementType.COMPONENT,
        component.getComponentId(), newSignoff, TaCallback.getNoopCallback());
  }

  /**
   * Removes the given component from the Project.
   */
  @Override
  public void removeComponent(Component componentToRemove) {
    projectService.removeComponent(componentToRemove,
        new TaCallback<Void>("Removing Component") {
          @Override
          public void onSuccess(Void result) {
            refreshView();
          }
        });
  }

  @Override
  public ProjectRpcAsync getProjectService() {
    return projectService;
  }

  @Override
  public void reorderComponents(List<Long> newOrder) {
    projectService.reorderComponents(
        project.getProjectId(), newOrder,
        new TaCallback<Void>("Reordering Comonents") {
          @Override
          public void onSuccess(Void result) {
            refreshView();
          }
        });
  }
}
