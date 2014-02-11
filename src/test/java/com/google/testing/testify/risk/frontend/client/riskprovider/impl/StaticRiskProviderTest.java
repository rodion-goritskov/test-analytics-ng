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

import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.model.Component;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Unit tests for the StaticRiskProvider class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class StaticRiskProviderTest extends TestCase {
  public void testWhenNoCapabilities() {
    StaticRiskProvider testProvider = new StaticRiskProvider();
    testProvider.initialize(new ArrayList<CapabilityIntersectionData>());

    Attribute testAttribute = new Attribute();
    testAttribute.setParentProjectId(42L);
    testAttribute.setAttributeId(1L);
    Component testComponent = new Component(42L);
    testComponent.setComponentId(1L);

    CapabilityIntersectionData cell =
        new CapabilityIntersectionData(testAttribute, testComponent, new ArrayList<Capability>());
    double riskResult = testProvider.calculateRisk(cell);
    assertTrue(Math.abs(riskResult) < 0.001);
  }
}
