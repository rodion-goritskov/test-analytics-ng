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

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.client.util.NotificationUtil;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;

import java.util.HashSet;
import java.util.List;

/**
 * Provides the mitigation contributed by existing test coverage. (Testcases alone, not code
 * coverage or recent test results.)
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class TestCoverageRiskProvider implements RiskProvider {

  private final Multimap<Long, TestCase> testcaseByAttributeLookup = HashMultimap.create();
  private final Multimap<Long, TestCase> testcaseByComponentLookup = HashMultimap.create();
  private final Multimap<Long, TestCase> testcaseByCapabilityLookup = HashMultimap.create();

  @Override
  public String getName() {
    return "Test coverage";
  }

  @Override
  public void initialize(List<CapabilityIntersectionData> projectData) {
    DataRpcAsync dataService = GWT.create(DataRpc.class);

    long projectId = projectData.get(0).getParentComponent().getParentProjectId();
    dataService.getProjectTestCasesById(projectId,
        new TaCallback<List<TestCase>>("Querying TestCases") {
          @Override
          public void onSuccess(List<TestCase> result) {
            initializeTestCaseLookups(result);
          }
        });
  }

  /**
   * Initializes class fields related to looking up testcases by Attribute, Component, or
   * Capability IDs.
   */
  private void initializeTestCaseLookups(List<TestCase> testCases) {
    for (TestCase test : testCases) {
      for (String testcaseTag : test.getTags()) {
        // Search test case tags for IDs prefixed with descriptors such as "Component:13452"
        String[] parts = testcaseTag.split(":");
        if (parts.length != 2) {
          continue;
        }

        try {
          if (parts[0].equals("Attribute")) {
            testcaseByAttributeLookup.put(Long.parseLong(parts[1]), test);
          } else if (parts[0].equals("Capability")) {
            testcaseByCapabilityLookup.put(Long.parseLong(parts[1]), test);
          } else if (parts[0].equals("Component")) {
            testcaseByComponentLookup.put(Long.parseLong(parts[1]), test);
          }
        } catch (NumberFormatException nfe) {
          // Notify the user of a malformed testcase tag.
          StringBuilder errorMessage = new StringBuilder();
          errorMessage.append("Error processing a testcase with a malformed tag. ");
          errorMessage.append("Testcase tag: '");
          errorMessage.append(testcaseTag);
          errorMessage.append("' found on testcase '");
          errorMessage.append(test.getTitle());
          errorMessage.append("'");

          NotificationUtil.displayErrorMessage(errorMessage.toString());
        }
      }
    }
  }

  /**
   * Returns the list of test cases associated with a given risk cell.
   */
  public List<TestCase> getCellTestCases(CapabilityIntersectionData targetCell) {
    List<TestCase> testCases = Lists.newArrayList();

    long attributeId = targetCell.getParentAttribute().getAttributeId();
    if (testcaseByAttributeLookup.containsKey(attributeId)) {
      testCases.addAll(testcaseByAttributeLookup.get(attributeId));
    }

    long componentId = targetCell.getParentComponent().getComponentId();
    if (testcaseByComponentLookup.containsKey(componentId)) {
      testCases.addAll(testcaseByComponentLookup.get(componentId));
    }

    if (targetCell.getCapabilities() != null) {
      for (Capability capability : targetCell.getCapabilities()) {
        long capabilityId = capability.getCapabilityId();
        if (testcaseByCapabilityLookup.containsKey(capabilityId)) {
          testCases.addAll(testcaseByCapabilityLookup.get(capabilityId));
        }
      }
    }

    // Remove any duplicate entries.
    final HashSet<Long> testCaseIds = new HashSet<Long>();
    return Lists.newArrayList(Iterables.filter(testCases,
        new Predicate<TestCase>() {
          @Override
          public boolean apply(TestCase input) {
            if (testCaseIds.contains(input.getExternalId())) {
              return false;
            } else {
              testCaseIds.add(input.getExternalId());
              return true;
            }
          }
        }));
  }

  @Override
  public double calculateRisk(CapabilityIntersectionData targetCell) {
    List<TestCase> testCases = getCellTestCases(targetCell);
    // Test coverage mitigates risk, so the value returned is negative.
    return testCases.size() * -0.15;
  }

  @Override
  public Widget onClick(CapabilityIntersectionData targetCell) {
    List<TestCase> testCases = getCellTestCases(targetCell);

    VerticalPanel panel = new VerticalPanel();
    if (testCases.size() == 0) {
      panel.add(new Label("No test cases are associated with this cell."));
    } else {
      for (TestCase testCase : testCases) {
        panel.add(new Anchor(testCase.getTitle(), testCase.getTestCaseUrl()));
      }
    }

    return panel;
  }
}
