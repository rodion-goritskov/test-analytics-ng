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

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.util.NotificationUtil;
import com.google.testing.testify.risk.frontend.client.view.ComponentView;
import com.google.testing.testify.risk.frontend.client.view.widgets.EditableLabel;
import com.google.testing.testify.risk.frontend.client.view.widgets.LabelWidget;
import com.google.testing.testify.risk.frontend.model.AccLabel;

import java.util.Collection;
import java.util.List;

//TODO(chrsmith): Merge ComponentViewImpl and AttributeViewImpl into a single widget.

/**
 * Widget for displaying a Component.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentViewImpl extends Composite implements ComponentView {

  /**
   * Used to wire parent class to associated UI Binder.
   */
  interface ComponentViewImplUiBinder extends UiBinder<Widget, ComponentViewImpl> { }
  private static final ComponentViewImplUiBinder uiBinder =
              GWT.create(ComponentViewImplUiBinder.class);

  @UiField
  protected Label componentGripper;

  @UiField
  protected EditableLabel componentName;

  @UiField
  protected TextArea description;

  @UiField
  protected Label componentId;

  @UiField
  protected Image deleteComponentImage;

  @UiField
  protected FlowPanel labelsPanel;

  @UiField
  protected CheckBox signoffBox;

  private LabelWidget addLabelWidget;

  /** Presenter associated with this View */
  private Presenter presenter;
  private final Collection<String> labelSuggestions = Lists.newArrayList();

  private boolean editingEnabled;

  public ComponentViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    description.getElement().setAttribute("placeholder", "Enter description of this component...");
  }

  @UiHandler("signoffBox")
  protected void onSignoffClicked(ClickEvent event) {
    presenter.updateSignoff(signoffBox.getValue());
  }

  /** Wires renaming the Component in the UI to the backing presenter. */
  @UiHandler("componentName")
  protected void onComponentNameChanged(ValueChangeEvent<String> event) {
    if (presenter != null) {
      if (event.getValue().length() == 0) {
        NotificationUtil.displayErrorMessage("Invalid name for Component.");
        presenter.refreshView();
      } else {
        presenter.onRename(event.getValue());
      }
    }
  }

  @UiHandler("description")
  protected void onDescriptionChange(ChangeEvent event) {
    presenter.onDescriptionEdited(description.getText());
  }

  /**
   * Handler for the deleteComponentImage's click event, removing the Component.
   */
  @UiHandler("deleteComponentImage")
  protected void onDeleteComponentImageClicked(ClickEvent event) {
    String promptText = "Are you sure you want to remove " + componentName.getText() + "?";
    if (Window.confirm(promptText)) {
      presenter.onRemove();
    }
  }

  /**
   * Updates the label suggestions for all labels on this view.
   */
  public void setLabelSuggestions(Collection<String> labelSuggestions) {
    this.labelSuggestions.clear();
    this.labelSuggestions.addAll(labelSuggestions);
    for (Widget w : labelsPanel) {
      if (w instanceof LabelWidget) {
        LabelWidget l = (LabelWidget) w;
        l.setLabelSuggestions(this.labelSuggestions);
      }
    }
  }

  /**
   * Displays a new Component Label on this Widget.
   */
  public void displayLabel(final AccLabel label) {
    final LabelWidget labelWidget = new LabelWidget(label.getLabelText());
    labelWidget.setLabelSuggestions(labelSuggestions);
    labelWidget.setEditable(editingEnabled);
    labelWidget.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> event) {
        if (event.getValue() == null) {
          labelsPanel.remove(labelWidget);
          presenter.onRemoveLabel(label);
        } else {
          presenter.onUpdateLabel(label, event.getValue());
        }
      }
    });

    labelsPanel.add(labelWidget);
  }

  /**
   * Updates the UI with the given Component name.
   */
  @Override
  public void setComponentName(String name) {
    componentName.setText(name);
  }

  @Override
  public void setDescription(String description) {
    this.description.setText(description == null ? "" : description);

  }
  /**
   * Updates the UI with the given Component ID.
   */
  @Override
  public void setComponentId(Long id) {
    componentId.setText(id.toString());
  }

  /**
   * Initialize this View's Presenter object. (For two-way communication.)
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  /**
   * Hides the given Widget, equivalent to setVisible(false).
   */
  @Override
  public void hide() {
    this.setVisible(false);
  }

  /**
   * Updates the Widget's list of Component Labels.
   */
  @Override
  public void setComponentLabels(List<AccLabel> labels) {
    labelsPanel.clear();
    for (AccLabel label : labels) {
      displayLabel(label);
    }
    addBlankLabel();
  }

  private void addBlankLabel() {
    final String newText = "new label";
    addLabelWidget = new LabelWidget(newText, true);
    addLabelWidget.setLabelSuggestions(labelSuggestions);
    addLabelWidget.setVisible(editingEnabled);
    addLabelWidget.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> event) {
        presenter.onAddLabel(event.getValue());
        labelsPanel.remove(addLabelWidget);
        addBlankLabel();
      }
    });
    addLabelWidget.setEditable(true);
    labelsPanel.add(addLabelWidget);
  }

  @Override
  public void enableEditing() {
    editingEnabled = true;
    componentGripper.setVisible(true);
    componentName.setReadOnly(false);
    signoffBox.setEnabled(true);
    deleteComponentImage.setVisible(true);
    description.setEnabled(true);
    for (Widget widget : labelsPanel) {
      LabelWidget label = (LabelWidget) widget;
      label.setEditable(true);
    }
    if (addLabelWidget != null) {
      addLabelWidget.setVisible(true);
    }
  }

  @Override
  public void setSignedOff(boolean signedOff) {
    signoffBox.setValue(signedOff);
  }

  /** Returns the ID of the underlying Component if applicable. Otherwise returns -1. */
  public long getComponentId() {
    try {
      return Long.parseLong(componentId.getText());
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }
}
