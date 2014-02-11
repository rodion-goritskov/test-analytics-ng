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

import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Capability;

/**
 * A widget that displays risk details to the user.  No editing is done via this widget, just
 * informational display.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public class RiskCapabilityWidget extends BaseCapabilityWidget {

  private final String risk;
  private Label riskLabel;
  
  public RiskCapabilityWidget(Capability capability, String risk) {
    super(capability);
    this.risk = risk;
    updateRiskLabel();
  }
  
  @Override
  public void makeEditable() {
    // Always disable editing, no matter what.  This is not a widget for editing, but for viewing.
  }

  private void updateRiskLabel() {
    riskLabel.setText("Risk: " + risk);
  }

  @UiFactory @Override
  public EasyDisclosurePanel createDisclosurePanel() {
    HorizontalPanel header = new HorizontalPanel();
    header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    header.setStyleName("tty-CapabilityRiskHeader");
    header.add(capabilityLabel);
    riskLabel = new Label();
    riskLabel.setStyleName("tty-CapabilityRiskValueHeader");
    updateRiskLabel();
    header.add(riskLabel);

    EasyDisclosurePanel panel = new EasyDisclosurePanel(header);
    panel.setOpen(false);
    return panel;
  }
  
  public void setRiskContent(Widget content) {
    disclosureContent.setWidget(content);
  }
}
