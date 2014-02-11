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


package com.google.testing.testify.risk.frontend.client.riskprovider;

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;

import java.util.List;

/**
 * Interface for new ways to provide a 'Risk' view for the application.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface RiskProvider {

  /**
   * @return a one or two word description of the risk provider. E.g. "Defects" or "Test coverage".
   */
  public String getName();

  /**
   * Inform the risk provider of all available capabilities. This gives it a chance to establish
   * any baselines and/or query any external data sources.
   */
  public void initialize(List<CapabilityIntersectionData> projectData);

  /**
   * Calculates the risk for a given list of capabilities (all sharing the same parent Attribute
   * and Component.)
   *
   * @return risk value between -1.0 and 1.0. A value of 0.0 indicates negligible risk, -1.0 lots
   * of risk mitigation, and 1.0 indicates very high risk.
   */
  public double calculateRisk(CapabilityIntersectionData targetCell);

  /**
   * Surface any custom UI when a risk cell is clicked. Will be displayed on a dialog box with an
   * OK button.
   */
  public Widget onClick(CapabilityIntersectionData targetCell);
}
