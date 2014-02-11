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

import com.google.testing.testify.risk.frontend.client.view.ComponentView;
import com.google.testing.testify.risk.frontend.client.view.ComponentsView;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Component;

/**
 * Presenter for a single Component.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentPresenter implements ComponentView.Presenter {

  private Component targetComponent;
  private final ComponentView view;
  private final ComponentsView.Presenter parentPresenter;

  public ComponentPresenter(Component component, ComponentView view,
        ComponentsView.Presenter parentPresenter) {

    this.targetComponent = component;
    this.view = view;
    this.parentPresenter = parentPresenter;

    view.setPresenter(this);
    refreshView();
  }

  /**
   * Refreshes UI elements of the View.
   */
  @Override
  public void refreshView() {
    view.setComponentName(targetComponent.getName());
    view.setDescription(targetComponent.getDescription());
    if (targetComponent.getComponentId() != null) {
      view.setComponentId(targetComponent.getComponentId());
    }
    view.setComponentLabels(targetComponent.getAccLabels());
  }
  
  @Override
  public void refreshView(Component component) {
    this.targetComponent = component;
    refreshView();
  }

  @Override
  public long getComponentId() {
    return targetComponent.getComponentId();
  }

  @Override
  public long getProjectId() {
    return targetComponent.getParentProjectId();
  }
  
  @Override
  public void updateSignoff(boolean newValue) {
    parentPresenter.updateSignoff(targetComponent, newValue);
  }

  @Override
  public void onDescriptionEdited(String description) {
    targetComponent.setDescription(description);
    parentPresenter.updateComponent(targetComponent);
  }

  @Override
  public void onRename(String newComponentName) {
    targetComponent.setName(newComponentName);
    parentPresenter.updateComponent(targetComponent);
  }

  @Override
  public void onRemove() {
    view.hide();
    parentPresenter.removeComponent(targetComponent);
  }

  @Override
  public void onAddLabel(String label) {
    targetComponent.addLabel(label);
    parentPresenter.updateComponent(targetComponent);
  }

  @Override
  public void onUpdateLabel(AccLabel label, String newText) {
    label = targetComponent.getAccLabel(label.getId());
    label.setLabelText(newText);
    parentPresenter.updateComponent(targetComponent);
  }

  @Override
  public void onRemoveLabel(AccLabel label) {
    targetComponent.removeLabel(label);
    parentPresenter.updateComponent(targetComponent);
  }
}
