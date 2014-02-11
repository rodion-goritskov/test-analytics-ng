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


package com.google.testing.testify.risk.frontend.client.view.widgets;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.util.NotificationUtil;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.FailureRate;
import com.google.testing.testify.risk.frontend.model.UserImpact;

import java.util.Collection;
import java.util.List;

/**
 * A capability widget that allows you to edit the details of the capability.  Hook up
 * an update and delete listener to be notified of edits on this widget.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class EditCapabilityWidget extends Composite implements HasValue<Capability> {

  interface EditCapabilityWidgetUiBinder extends UiBinder<Widget, EditCapabilityWidget> {}
  private static final EditCapabilityWidgetUiBinder uiBinder =
          GWT.create(EditCapabilityWidgetUiBinder.class);

  private Capability capability;
  private final List<Attribute> attributes = Lists.newArrayList();
  private final List<Component> components = Lists.newArrayList();

  @UiField
  protected FlowPanel labelsPanel;
  @UiField
  protected HorizontalPanel failurePanel;
  @UiField
  protected HorizontalPanel impactPanel;
  @UiField
  protected ListBox attributeBox;
  @UiField
  protected ListBox componentBox;
  @UiField
  protected TextBox capabilityName;
  @UiField
  protected TextArea description;
  @UiField
  protected Label capabilityGripper;
  @UiField
  protected HorizontalPanel buttonPanel;
  @UiField
  public HorizontalPanel savedPanel;
  @UiField
  protected EasyDisclosurePanel disclosurePanel;
  @UiField
  protected Button cancelButton;
  @UiField
  protected Button saveButton;
  @UiField
  protected Image deleteImage;
  @UiField
  protected Label capabilityId;

  private final Label capabilityLabel = new Label();
  private LabelWidget addNewLabel;
  private Collection<String> labelSuggestions = Lists.newArrayList();
  private boolean isEditable = false;
  private boolean isDeletable = true;


  /**
   * Creates a widget which exposes the ability to edit the widget.
   *
   * @param capability the capability for this widget.
   */
  public EditCapabilityWidget(Capability capability) {
    this.capability = capability;
    initWidget(uiBinder.createAndBindUi(this));
    capabilityLabel.setText(capability.getName());
    description.getElement().setAttribute("placeholder", "Enter description of this capability...");

    refresh();
  }

  public Widget getCapabilityGripper() {
    return capabilityGripper;
  }

  public void expand() {
    disclosurePanel.setOpen(true);
  }

  public void setAttributes(List<Attribute> attributes) {
    this.attributes.clear();
    this.attributes.addAll(attributes);
    refresh();
  }

  public void setComponents(List<Component> components) {
    this.components.clear();
    this.components.addAll(components);
    refresh();
  }

  public long getCapabilityId() {
    return capability.getCapabilityId();
  }

  public void disableDelete() {
    isDeletable = false;
    deleteImage.setVisible(false);
  }

  @UiFactory
  public EasyDisclosurePanel createDisclosurePanel() {
    EasyDisclosurePanel panel = new EasyDisclosurePanel(capabilityLabel);
    panel.setOpen(false);
    return panel;
  }

  @UiHandler("deleteImage")
  protected void handleDelete(ClickEvent event) {
    String promptText = "Are you sure you want to remove " + capability.getName() + "?";
    if (Window.confirm(promptText)) {
      setValue(null, true);
    }
  }

  @UiHandler("cancelButton")
  protected void handleCancel(ClickEvent event) {
    refresh();
  }

  @UiHandler("saveButton")
  public void handleSave(ClickEvent event) {
    savedPanel.setVisible(false);
    long selectedAttribute;
    long selectedComponent;
    try {
      selectedAttribute =
          Long.parseLong(attributeBox.getValue(attributeBox.getSelectedIndex()));
      selectedComponent =
        Long.parseLong(componentBox.getValue(componentBox.getSelectedIndex()));
    } catch (NumberFormatException e) {
      NotificationUtil.displayErrorMessage("Couldn't save capability.  The attribute or"
          + " component ID was invalid.");
      return;
    }

    // Handle updates.
    capability.setName(capabilityName.getValue());
    capability.setDescription(description.getValue());
    capability.setFailureRate(
       FailureRate.fromDescription(getSelectedOptionInPanel(failurePanel)));
    capability.setUserImpact(
       UserImpact.fromDescription(getSelectedOptionInPanel(impactPanel)));
    capability.setAttributeId(selectedAttribute);
    capability.setComponentId(selectedComponent);

    if ((capability.getComponentId() != selectedComponent)
        || (capability.getAttributeId() != selectedAttribute)) {
      Window.alert("The capability " + capability.getName() + " will disappear from the "
          + " currently visible list because you have changed its attribute or component.");
    }

    // Tell the world that we've updated this capability.
    ValueChangeEvent.fire(this, capability);
  }

  public void showSaved() {
    // Show saved message.
    savedPanel.setVisible(true);
    Timer timer = new Timer() {
        @Override
        public void run() {
          savedPanel.setVisible(false);
        }
      };
    // Make the saved text disappear after 10 seconds.
    timer.schedule(5000);
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

  private void refresh() {
    capabilityLabel.setText(capability.getName());
    capabilityName.setText(capability.getName());
    createLabelsPanel();
    description.setText(capability.getDescription());
    createFailureBox();
    createImpactBox();
    createAttributeBox();
    createComponentBox();

    capabilityName.setEnabled(isEditable);
    capabilityGripper.setVisible(isEditable);
    description.setEnabled(isEditable);
    enableOrDisableAllRadioButtons(failurePanel, isEditable);
    enableOrDisableAllRadioButtons(impactPanel, isEditable);
    attributeBox.setEnabled(isEditable);
    componentBox.setEnabled(isEditable);
    buttonPanel.setVisible(isEditable);
  }

  private void createLabelsPanel() {
    labelsPanel.clear();
    for (AccLabel label : capability.getAccLabels()) {
      createLabel(label);
    }
    addBlankLabel();
  }

  private void createLabel(final AccLabel label) {
    final LabelWidget widget = new LabelWidget(label.getLabelText());
    widget.setLabelSuggestions(labelSuggestions);
    widget.setEditable(isEditable);
    widget.addValueChangeHandler(new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
          if (event.getValue() == null) {
            labelsPanel.remove(widget);
            capability.removeLabel(label);
          } else {
            label.setLabelText(event.getValue());
          }
        }
      });
    labelsPanel.add(widget);
  }

  private void addBlankLabel() {
    final String newText = "new label";
    addNewLabel = new LabelWidget(newText, true);
    addNewLabel.setLabelSuggestions(labelSuggestions);
    addNewLabel.setEditable(true);
    addNewLabel.addValueChangeHandler(new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
          labelsPanel.remove(addNewLabel);
          AccLabel label = capability.addLabel(event.getValue());
          createLabel(label);
          addBlankLabel();
        }
      });
    addNewLabel.setVisible(isEditable);
    labelsPanel.add(addNewLabel);
  }

  private void createFailureBox() {
    failurePanel.clear();
    RadioButton button;
    for (FailureRate rate : FailureRate.values()) {
      button = new RadioButton("failure" + capability.getCapabilityId().toString(),
          rate.getDescription());
      failurePanel.add(button);
      if (rate.equals(capability.getFailureRate())) {
        button.setValue(true);
      }
    }
  }

  private void createImpactBox() {
    impactPanel.clear();
    RadioButton button;
    for (UserImpact impact : UserImpact.values()) {
      button = new RadioButton("impact" + capability.getCapabilityId().toString(),
          impact.getDescription());
      impactPanel.add(button);
      if (impact.equals(capability.getUserImpact())) {
        button.setValue(true);
      }
    }
  }

  private void enableOrDisableAllRadioButtons(Panel panel, boolean enable) {
    for (Widget w : panel) {
      if (w instanceof RadioButton) {
        RadioButton b = (RadioButton) w;
        b.setEnabled(enable);
      }
    }
  }

  private String getSelectedOptionInPanel(Panel panel) {
    for (Widget w : panel) {
      if (w instanceof RadioButton) {
        RadioButton b = (RadioButton) w;
        if (b.getValue()) {
          return b.getText();
        }
      }
    }
    return null;
  }

  private void createAttributeBox() {
    attributeBox.clear();
    int i = 0;
    for (Attribute attribute : attributes) {
      attributeBox.addItem(attribute.getName(), attribute.getAttributeId().toString());
      if (attribute.getAttributeId() == capability.getAttributeId()) {
        attributeBox.setSelectedIndex(i);
      }
      i++;
    }
  }

  private void createComponentBox() {
    componentBox.clear();
    int i = 0;
    for (Component component : components) {
      componentBox.addItem(component.getName(), component.getComponentId().toString());
      if (component.getComponentId() == capability.getComponentId()) {
        componentBox.setSelectedIndex(i);
      }
      i++;
    }
  }

  public void makeEditable() {
    isEditable = true;
    deleteImage.setVisible(isDeletable);
    enableOrDisableAllRadioButtons(failurePanel, true);
    enableOrDisableAllRadioButtons(impactPanel, true);
    description.setEnabled(true);
    attributeBox.setEnabled(true);
    componentBox.setEnabled(true);
    capabilityName.setEnabled(true);
    capabilityGripper.setVisible(true);
    buttonPanel.setVisible(true);
    for (Widget widget : labelsPanel) {
      LabelWidget label = (LabelWidget) widget;
      label.setEditable(true);
    }
    addNewLabel.setVisible(true);
  }

  @Override
  public Capability getValue() {
    return capability;
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Capability> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  @Override
  public void setValue(Capability capability) {
    this.capability = capability;
  }

  @Override
  public void setValue(Capability capability, boolean fireEvents) {
    this.capability = capability;
    if (fireEvents) {
      ValueChangeEvent.fire(this, capability);
    }
  }
}
