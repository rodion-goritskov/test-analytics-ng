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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Capability;

/**
 * Base display for a capability.  This widget is intended to be extended to supply content
 * in the disclosure panel depending on view (for example, on the Capabilities page, the ability
 * to edit capability details would be contained there.  On a risk page, details on the risk
 * assessment would be contained there.  And so on).
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class BaseCapabilityWidget extends Composite implements HasValueChangeHandlers<Capability> {

  interface CapabilityWidgetBinder extends UiBinder<Widget, BaseCapabilityWidget> { }
  private static final CapabilityWidgetBinder uiBinder = GWT.create(CapabilityWidgetBinder.class);

  @UiField
  public Image capabilityGripper;

  @UiField
  public EasyDisclosurePanel disclosurePanel;

  @UiField
  public SimplePanel disclosureContent;

  @UiField
  public Label capabilityId;

  @UiField
  public Image deleteCapabilityImage;

  protected final Label capabilityLabel = new Label();
  protected final Capability capability;

  // If this widget allows editing.
  protected boolean isEditable = false;

  public BaseCapabilityWidget(Capability capability) {
    initWidget(uiBinder.createAndBindUi(this));
    this.capability = capability;

    initializeWidget();
  }

  @UiFactory
  public EasyDisclosurePanel createDisclosurePanel() {
    EasyDisclosurePanel panel = new EasyDisclosurePanel(capabilityLabel);
    panel.setOpen(false);
    return panel;
  }

  private void initializeWidget() {
    capabilityLabel.setStyleName("tty-CapabilityName");

    deleteCapabilityImage.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ValueChangeEvent.fire(BaseCapabilityWidget.this, null);
      }
    });

    capabilityLabel.setText(capability.getName());
    capabilityId.setText(Long.toString(capability.getCapabilityId()));
  }

  public void makeEditable() {
    isEditable = true;
    capabilityGripper.setVisible(true);
    deleteCapabilityImage.setVisible(true);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Capability> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
