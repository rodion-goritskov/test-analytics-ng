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


package com.google.testing.testify.risk.frontend.shared.util;

import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.FailureRate;
import com.google.testing.testify.risk.frontend.model.UserImpact;

import junit.framework.TestCase;

/**
 * Unit tests for the RiskUtils class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class RiskUtilTest extends TestCase {
  public void testAllCombinationsInRange() {
    for (UserImpact impact : UserImpact.values()) {
      double lastRisk = 0;
      for (FailureRate rate : FailureRate.values()) {
        Capability capability = new Capability(42L, 0L, 0L);
        capability.setUserImpact(impact);
        capability.setFailureRate(rate);

        double risk = RiskUtil.determineRisk(capability);
        if (impact.getOrdinal() < 0 || rate.getOrdinal() < 0) {
          assertEquals(0.0, risk);
        } else {
          assertTrue(risk >= 0.0);
          assertTrue("risk: " + risk + " last risk: " + lastRisk, risk > lastRisk);
          assertTrue(risk <= 1.0);
          lastRisk = risk;
        }
      }
    }
  }

  public void testRiskText() {
    Capability c = new Capability(42L, 10L, 25L);
    assertEquals("n/a", RiskUtil.getRiskText(c));
    c.setUserImpact(UserImpact.MAXIMAL);
    assertEquals("n/a", RiskUtil.getRiskText(c));
    c.setFailureRate(FailureRate.OFTEN);
    assertEquals("High", RiskUtil.getRiskText(c));
    assertTrue(RiskUtil.getRiskExplanation(c).contains("Often"));
    assertTrue(RiskUtil.getRiskExplanation(c).contains("Maximal"));
    c.setUserImpact(UserImpact.SOME);
    assertEquals("Medium", RiskUtil.getRiskText(c));
    c.setFailureRate(FailureRate.SELDOM);
    assertEquals("Low", RiskUtil.getRiskText(c));
    c.setUserImpact(UserImpact.NA);
    assertEquals("n/a", RiskUtil.getRiskText(c));
  }
}
