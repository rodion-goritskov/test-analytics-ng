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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;

import java.util.HashSet;
import java.util.List;

/**
 * {@link RiskProvider} showing Risk due to outstanding code defects.
 *
 * For example, if outstanding bugs are spread across all existing Attribute x Component pairs, then
 * there is relatively low risk due to bugs. However, if one Attribute x Component pair has a large
 * concentration of bugs then it should be investigated.
 *
 * The higher the risk returned, the more risky that area is.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class BugRiskProvider implements RiskProvider {

  private final HashSet<Bug> unassignedBugs = new HashSet<Bug>();
  private final Multimap<Long, Bug> lookupByAttribute = HashMultimap.create();
  private final Multimap<Long, Bug> lookupByComponent = HashMultimap.create();
  private final Multimap<Long, Bug> lookupByCapability = HashMultimap.create();

  // Bugs not associated with an Attribute or Component. (General risk.)
  private static final double RISK_FROM_UNASSIGNED = 0.00;
  // Bugs associated with the Attribute.
  private static final double RISK_FROM_ATTRIBUTE = 0.25;
  // Bugs associated with the Component.
  private static final double RISK_FROM_COMPONENT = 0.25;
  // Bugs associated with the Capability.
  private static final double RISK_FROM_CAPABILITY = 1.0;

  @Override
  public String getName() {
    return "Bugs";
  }

  /**
   * Asynchronously loads project bug information.
   */
  @Override
  public void initialize(List<CapabilityIntersectionData> projectData) {
    DataRpcAsync bugService = GWT.create(DataRpc.class);

    long projectId = projectData.get(0).getParentComponent().getParentProjectId();
    bugService.getProjectBugsById(projectId,
        new TaCallback<List<Bug>>("Querying Bugs") {
          @Override
          public void onSuccess(List<Bug> result) {
            lookupByAttribute.clear();
            lookupByComponent.clear();
            lookupByCapability.clear();
            unassignedBugs.clear();

            for (Bug bug : result) {
              lookupByAttribute.put(bug.getTargetAttributeId(), bug);
              lookupByComponent.put(bug.getTargetComponentId(), bug);
              lookupByCapability.put(bug.getTargetCapabilityId(), bug);
              if (bug.getTargetAttributeId() == null &&
                  bug.getTargetComponentId() == null &&
                  bug.getTargetCapabilityId() == null) {
                unassignedBugs.add(bug);
              }
            }
          }
        });
  }

  /**
   * Returns the risk caused by outstanding bugs (based solely on bug count).
   */
  @Override
  public double calculateRisk(CapabilityIntersectionData targetCell) {
    long attributeId = targetCell.getParentAttribute().getAttributeId();
    long componentId = targetCell.getParentComponent().getComponentId();

    // Bugs attached to Attributes or Components add risk to the whole Attribute and the whole
    // Component. It is OK if we double-count risk.
    double riskFromBugs = 0.0;
    riskFromBugs += RISK_FROM_UNASSIGNED * unassignedBugs.size();
    riskFromBugs += RISK_FROM_ATTRIBUTE * lookupByAttribute.get(attributeId).size();
    riskFromBugs += RISK_FROM_COMPONENT * lookupByComponent.get(componentId).size();
    for (Capability capability : targetCell.getCapabilities()) {
      riskFromBugs += RISK_FROM_CAPABILITY
          * lookupByCapability.get(capability.getCapabilityId()).size();
    }

    return riskFromBugs;
  }

  /**
   * Returns a {@code Widget} to show when an Attribute x Component pair is clicked.
   */
  @Override
  public Widget onClick(CapabilityIntersectionData targetCell) {
    VerticalPanel content = new VerticalPanel();
    long attributeId = targetCell.getParentAttribute().getAttributeId();
    long componentId = targetCell.getParentComponent().getComponentId();

    for (Bug bug : lookupByComponent.get(componentId)) {
      String linkText = "Component - " + Long.toString(bug.getExternalId()) + ": " + bug.getTitle();
      Anchor anchor = new Anchor(linkText, bug.getBugUrl());
      content.add(anchor);
    }
    for (Bug bug : lookupByAttribute.get(attributeId)) {
      String linkText = "Attribute - " + Long.toString(bug.getExternalId()) + ": " + bug.getTitle();
      Anchor anchor = new Anchor(linkText, bug.getBugUrl());
      content.add(anchor);
    }
    for (Capability capability : targetCell.getCapabilities()) {
      long capabilityId = capability.getCapabilityId();
      for (Bug bug : lookupByCapability.get(capabilityId)) {
        String linkText =
            "Capability - " + Long.toString(bug.getExternalId()) + ": " + bug.getTitle();
        Anchor anchor = new Anchor(linkText, bug.getBugUrl());
        content.add(anchor);
      }
    }
    String labelText = "Unassigned - " + unassignedBugs.size();
    Label label = new Label(labelText);
    content.add(label);

    if (content.getWidgetCount() == 0) {
      content.add(new Label("No bugs associated with this cell."));
    }
    return content;
  }
}
