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


package com.google.testing.testify.risk.frontend.client.riskprovider.impl;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.client.view.widgets.RiskCapabilityWidget;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.shared.util.RiskUtil;

import java.util.List;

/**
 * Provides a Risk calculation based on static project data. (Risk associated with individual
 * Capabilities.)
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class StaticRiskProvider implements RiskProvider {

  @Override
  public String getName() {
    return "Inherent risk";
  }

  @Override
  public void initialize(List<CapabilityIntersectionData> projectData) {
  }

  @Override
  public double calculateRisk(CapabilityIntersectionData targetCell) {
    double localRisk = 0.0f;
    // Calculate the 'risk' associated with these Capabilities.
    for (Capability capability : targetCell.getCapabilities()) {
      localRisk += RiskUtil.determineRisk(capability);
    }

    return localRisk;
  }

  @Override
  public Widget onClick(CapabilityIntersectionData targetCell) {
    VerticalPanel panel = new VerticalPanel();
    panel.setStyleName("tty-CapabilitiesContainer");

    String aName = targetCell.getParentAttribute().getName();
    String cName = targetCell.getParentComponent().getName();
    Label name = new Label(cName + " is " + aName);
    name.setStyleName("tty-CapabilitiesContainerTitle");
    
    panel.add(name);
    for (Capability capability : targetCell.getCapabilities()) {
      RiskCapabilityWidget widget = new RiskCapabilityWidget(capability,
          RiskUtil.getRiskText((capability)));
      widget.setRiskContent(new Label(RiskUtil.getRiskExplanation(capability)));
      panel.add(widget);
    }
    return panel;
  }
}
