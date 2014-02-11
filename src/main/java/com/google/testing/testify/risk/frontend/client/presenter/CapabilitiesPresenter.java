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
import com.google.testing.testify.risk.frontend.client.view.CapabilitiesView;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * Presenter for the CapabilitiesGrid widget.
 * {@See com.google.testing.testify.risk.frontend.client.view.CapabilitiesGridView}
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilitiesPresenter
    extends BasePagePresenter implements CapabilitiesView.Presenter, TaPagePresenter {

  private final Project project;
  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private final CapabilitiesView view;
  private final EventBus eventBus;

  public CapabilitiesPresenter(
      Project project, ProjectRpcAsync projectService, UserRpcAsync userService,
      CapabilitiesView view, EventBus eventBus) {

    this.project = project;
    this.projectService = projectService;
    this.userService = userService;
    this.view = view;
    this.eventBus = eventBus;

    refreshView();
  }

  /**
   * Refreshes the view based on data obtained from the Project Service.
   */
  @Override
  public void refreshView() {
    view.setPresenter(this);

    userService.hasEditAccess(project.getProjectId(),
      new TaCallback<Boolean>("checking user access") {
        @Override
        public void onSuccess(Boolean result) {
          // Assume the user already has VIEW access, otherwise the RPC service wouldn't have
          // served the Project object in the first place.
          view.setEditable(result);
        }
      });

    projectService.getProjectAttributes(project.getProjectId(),
        new TaCallback<List<Attribute>>("querying attributes") {
          @Override
          public void onSuccess(List<Attribute> result) {
            view.setAttributes(result);
          }
        });

    projectService.getProjectComponents(project.getProjectId(),
        new TaCallback<List<Component>>("querying components") {
          @Override
          public void onSuccess(List<Component> result) {
            view.setComponents(result);
          }
        });

    projectService.getProjectCapabilities(project.getProjectId(),
        new TaCallback<List<Capability>>("querying capabilities") {
          @Override
          public void onSuccess(List<Capability> result) {
            view.setCapabilities(result);
          }
        });

    projectService.getLabels(project.getProjectId(),
        new TaCallback<List<AccLabel>>("querying components") {
          @Override
          public void onSuccess(List<AccLabel> result) {
            Collection<String> labels = Lists.newArrayList();
            for (AccLabel l : result) {
              labels.add(l.getLabelText());
            }
            view.setProjectLabels(labels);
          }
        });
  }

  @Override
  public void onAddCapability(final Capability capabilityToAdd) {
    projectService.createCapability(capabilityToAdd,
        new TaCallback<Capability>("creating capability") {
          @Override
          public void onSuccess(Capability result) {
            eventBus.fireEvent(
                new ProjectElementAddedEvent(result));
            view.addCapability(result);
          }
        });
  }

  @Override
  public void onUpdateCapability(Capability capabilityToUpdate) {
    projectService.updateCapability(capabilityToUpdate, TaCallback.getNoopCallback());
  }

  @Override
  public void onRemoveCapability(Capability capabilityToRemove) {
    projectService.removeCapability(capabilityToRemove, TaCallback.getNoopCallback());
  }

  @Override
  public void reorderCapabilities(List<Long> ids) {
    projectService.reorderCapabilities(project.getProjectId(), ids,
        TaCallback.getNoopCallback());
  }

  /** Returns the underlying view. */
  @Override
  public Widget getView() {
    return view.asWidget();
  }
}
