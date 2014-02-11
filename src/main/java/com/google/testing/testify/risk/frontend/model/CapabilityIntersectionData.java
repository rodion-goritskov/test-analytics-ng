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


package com.google.testing.testify.risk.frontend.model;

import java.util.Collection;

/**
 * Represents an Attribute x Component intersection for calculating risk.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilityIntersectionData {

  private final Attribute parentAttribute;
  private final Component parentComponent;

  private final Collection<Capability> cellCapabilities;

  public CapabilityIntersectionData(
      Attribute parentAttribute, Component parentComponent,
      Collection<Capability> cellCapabilities) {

    this.parentAttribute = parentAttribute;
    this.parentComponent = parentComponent;
    this.cellCapabilities = cellCapabilities;
  }

  public Attribute getParentAttribute() {
    return parentAttribute;
  }

  public Component getParentComponent() {
    return parentComponent;
  }

  /** Project Capabilities associated with the Attribute x Component. */
  public Collection<Capability> getCapabilities() {
    return cellCapabilities;
  }
}
