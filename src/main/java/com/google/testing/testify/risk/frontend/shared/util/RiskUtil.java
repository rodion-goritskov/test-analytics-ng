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

/**
 * Static utility class for calculating risk.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public final class RiskUtil {

  /** Prevent instantiation. */
  private RiskUtil() {}  // COV_NF_LINE

  /**
   * Determine the individual risk value of the Capability.
   *
   * @return an arbitrary double value. Higher means riskier, lower means safer. It's range is
   * only meaningful when compared with other Capabilities' risk.
   */
  public static double determineRisk(Capability capability) {
    double userImpact = capability.getUserImpact().getOrdinal() + 1;
    double failureRate = capability.getFailureRate().getOrdinal() + 1;
    if (userImpact < 0 || failureRate < 0) {
      return 0;
    }

    return (userImpact * failureRate) / 16.0;
  }

  // TODO(chrsmith): Think about and refactor this. Do we want to be more formal about our risk
  // calcluation? What is the underlying unit of risk?

  /**
   * Returns a string describing the estimated risk of the given capability.
   */
  public static String getRiskText(Capability capability) {
    double risk = determineRisk(capability);
    if (risk > 0.75) {
      return "High";
    } else if (risk > 0.25) {
      return "Medium";
    } else if (risk > 0) {
      return "Low";
    } else {
      return "n/a";
    }
  }

  public static String getRiskExplanation(Capability capability) {
    return "User Impact: " + capability.getUserImpact().getDescription() + "; Failure Rate: "
      + capability.getFailureRate().getDescription();
  }
}
