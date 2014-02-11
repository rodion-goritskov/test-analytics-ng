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
import com.google.testing.testify.risk.frontend.client.view.AttributesView;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * Presenter for the Attributes page.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class AttributesPresenter extends BasePagePresenter implements AttributesView.Presenter,
    TaPagePresenter {

  private final Project project;
  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private final DataRpcAsync dataService;
  private final AttributesView view;
  private final EventBus eventBus;

  private final Collection<String> projectLabels = Lists.newArrayList();

  /**
   * Constructs the Attributes page presenter.
   */
  public AttributesPresenter(
      Project project, ProjectRpcAsync projectService, UserRpcAsync userService,
      DataRpcAsync dataService, AttributesView view, EventBus eventBus) {

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
        new TaCallback<Boolean>("checking user access") {
          @Override
          public void onSuccess(Boolean result) {
            if (result) {
              view.enableEditing();
            }
          }
        });

    projectService.getProjectAttributes(project.getProjectId(),
        new TaCallback<List<Attribute>>("loading attributes") {
          @Override
          public void onSuccess(List<Attribute> result) {
            // If the project has no attributes, we need to broadcast it.
            if (result.size() == 0) {
              eventBus.fireEvent(
                  new ProjectHasNoElementsEvent(project, AccElementType.ATTRIBUTE));
            }
            view.setProjectAttributes(result);
          }
        });

    projectService.getLabels(project.getProjectId(),
        new TaCallback<List<AccLabel>>("loading labels") {
          @Override
          public void onSuccess(List<AccLabel> result) {
            for (AccLabel l : result) {
              projectLabels.add(l.getLabelText());
            }
            view.setProjectLabels(projectLabels);
          }
        });

    dataService.getSignoffsByType(project.getProjectId(), AccElementType.ATTRIBUTE,
        new TaCallback<List<Signoff>>("loading signoff details") {
          @Override
          public void onSuccess(List<Signoff> results) {
            view.setSignoffs(results);
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

  /**
   * Adds a new Attribute to the data store.
   */
  @Override
  public void createAttribute(final Attribute attribute) {
    projectService.createAttribute(attribute,
        new TaCallback<Long>("creating attribute") {
          @Override
          public void onSuccess(Long result) {
            attribute.setAttributeId(result);
            eventBus.fireEvent(new ProjectElementAddedEvent(attribute));
            refreshView();
          }
        });
  }

  /**
   * Updates the given attribute in the database.
   */
  @Override
  public void updateAttribute(Attribute attributeToUpdate) {
    projectService.updateAttribute(attributeToUpdate,
        new TaCallback<Attribute>("updating attribute") {
          @Override
          public void onSuccess(Attribute result) {
            view.refreshAttribute(result);
          }
        });
  }

  @Override
  public void updateSignoff(Attribute attribute, boolean newSignoff) {
    dataService.setSignedOff(attribute.getParentProjectId(), AccElementType.ATTRIBUTE,
        attribute.getAttributeId(), newSignoff, TaCallback.getNoopCallback());
  }

  /**
   * Removes the given attribute from the Project.
   */
  @Override
  public void removeAttribute(Attribute attributeToRemove) {
    projectService.removeAttribute(attributeToRemove,
        new TaCallback<Void>("deleting attribute") {
          @Override
          public void onSuccess(Void result) {
            refreshView();
          }
        });
  }

  @Override
  public void reorderAttributes(List<Long> newOrder) {
    projectService.reorderAttributes(
        project.getProjectId(), newOrder,
        new TaCallback<Void>("reordering attributes") {
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
}
