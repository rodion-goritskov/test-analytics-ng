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


package com.google.testing.testify.risk.frontend.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;

import java.util.List;

/**
 * View on top of any user interface for visualizing a project's Risk or Risk Mitigations.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface RiskView {

  /**
   * Initializes user interface elements with the given components.
   */
  public void setComponents(List<Component> components);

  /**
   * Initializes user interface elements with the given attributes.
   */
  public void setAttributes(List<Attribute> attributes);

  /**
   * Notifies the view of all Project Components.
   */
  public void setCapabilities(List<Capability> capabilities);

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
}
