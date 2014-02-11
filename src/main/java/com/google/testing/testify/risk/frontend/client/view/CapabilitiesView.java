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

import java.util.Collection;
import java.util.List;

/**
 * View on top of any user interface for visualizing Capabilities.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface CapabilitiesView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {
    /**
     * Notify the Presenter that a new Capability has been added.
     */
    public void onAddCapability(Capability addedCapability);

    /**
     * Notify the Presenter that a Capability has been updated.
     */
    public void onUpdateCapability(Capability updatedCapability);

    /**
     * Notify the Presenter that a Capability has been removed.
     */
    public void onRemoveCapability(Capability removedCapability);

    /**
     * Notify the Presenter to update the order of capabilities.
     */
    public void reorderCapabilities(List<Long> ids);
    
    /**
     * Request the Presenter begin refreshing the View's Capabilities list. (From
     * multiple onAdd/onRemove calls.)
     */
    public void refreshView();
  }

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  public void setPresenter(Presenter presenter);

  /**
   * Initialize user interface elements with the given components.
   */
  public void setComponents(List<Component> components);

  /**
   * Initialize user interface elements with the given attributes.
   */
  public void setAttributes(List<Attribute> attributes);

  /**
   * Initialize user interface elements with the given capabilities.
   */
  public void setCapabilities(List<Capability> capabilities);

  public void setProjectLabels(Collection<String> labels);

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
  
  /**
   * Sets if this view is editable or not.
   * @param isEditable true if editable.
   */
  public void setEditable(boolean isEditable);
  
  /**
   * Adds a capability to the view.
   * 
   * @param capability capability to add.
   */
  public void addCapability(Capability capability);
}
