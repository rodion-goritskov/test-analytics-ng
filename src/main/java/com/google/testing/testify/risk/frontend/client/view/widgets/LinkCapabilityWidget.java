// Copyright 2011 Google Inc. All Rights Reseved.
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaApplication;
import com.google.testing.testify.risk.frontend.model.Capability;

/**
 * Displays a Capability which, upon click, takes a user to another page.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class LinkCapabilityWidget extends Composite {

  interface CapabilityWidgetBinder extends UiBinder<Widget, LinkCapabilityWidget> { }
  private static final CapabilityWidgetBinder uiBinder = GWT.create(CapabilityWidgetBinder.class);

  @UiField
  public Label capabilityId;

  @UiField
  public Label capabilityLabel;

  private final Capability capability;

  /**
   * @param capability
   */
  public LinkCapabilityWidget(Capability capability) {
    initWidget(uiBinder.createAndBindUi(this));

    this.capability = capability;
    initializeWidget();
  }

  private void initializeWidget() {
    capabilityLabel.setText(capability.getName());
    capabilityId.setText(Long.toString(capability.getCapabilityId()));

    capabilityLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem("/" + capability.getParentProjectId()
            + "/" + TaApplication.PAGE_HISTORY_TOKEN_CAPABILITY_DETAILS
            + "/" + capability.getCapabilityId());
      }
    });
  }
}