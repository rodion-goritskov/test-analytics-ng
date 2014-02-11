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

import com.google.testing.testify.risk.frontend.client.view.AttributeView;
import com.google.testing.testify.risk.frontend.client.view.AttributesView;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;

/**
 * Presenter for a single Attribute.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class AttributePresenter implements AttributeView.Presenter {

  private Attribute targetAttribute;
  private final AttributeView view;
  private final AttributesView.Presenter parentPresenter;

  public AttributePresenter(Attribute targetAttribute, AttributeView view,
        AttributesView.Presenter parentPresenter) {

    this.targetAttribute = targetAttribute;
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
    view.setAttributeName(targetAttribute.getName());
    view.setDescription(targetAttribute.getDescription());
    if (targetAttribute.getAttributeId() != null) {
      view.setAttributeId(targetAttribute.getAttributeId());
    }
    view.setLabels(targetAttribute.getAccLabels());
  }

  @Override
  public void refreshView(Attribute attribute) {
    this.targetAttribute = attribute;
    refreshView();
  }

  @Override
  public long getAttributeId() {
    return targetAttribute.getAttributeId();
  }

  @Override
  public long getProjectId() {
    return targetAttribute.getParentProjectId();
  }

  @Override
  public void updateSignoff(boolean newValue) {
    parentPresenter.updateSignoff(targetAttribute, newValue);
  }

  @Override
  public void onDescriptionEdited(String description) {
    targetAttribute.setDescription(description);
    parentPresenter.updateAttribute(targetAttribute);
  }

  @Override
  public void onRename(String newAttributeName) {
    targetAttribute.setName(newAttributeName);
    parentPresenter.updateAttribute(targetAttribute);
  }

  @Override
  public void onRemove() {
    view.hide();
    parentPresenter.removeAttribute(targetAttribute);
  }

  @Override
  public void onAddLabel(String label) {
    targetAttribute.addLabel(label);
    parentPresenter.updateAttribute(targetAttribute);
  }

  @Override
  public void onUpdateLabel(AccLabel label, String newText) {
    label = targetAttribute.getAccLabel(label.getId());
    label.setLabelText(newText);
    parentPresenter.updateAttribute(targetAttribute);
  }

  @Override
  public void onRemoveLabel(AccLabel label) {
    targetAttribute.removeLabel(label);
    parentPresenter.updateAttribute(targetAttribute);
  }
}
