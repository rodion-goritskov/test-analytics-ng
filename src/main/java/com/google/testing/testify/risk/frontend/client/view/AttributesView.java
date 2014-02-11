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
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * View on top of the ProjectAttributes page.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface AttributesView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {

    /**
     * @return the ProjectID for the project the View is displaying.
     */
    long getProjectId();

    /**
     * Notifies the Presenter that a new Attribute has been created.
     */
    public void createAttribute(Attribute attribute);

    /**
     * Updates the given Attribute in the database.
     */
    public void updateAttribute(Attribute attributeToUpdate);

    public void updateSignoff(Attribute attribute, boolean newSignoff);

    /**
     * Removes the given Attribute from the Project.
     */
    public void removeAttribute(Attribute attributeToRemove);

    /**
     * Reorders the project's list of Attributes.
     */
    public void reorderAttributes(List<Long> newOrder);

    /** Get the ProjectService associated with the presenter. */
    public ProjectRpcAsync getProjectService();
  }

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  public void setPresenter(Presenter presenter);

  /**
   * Initialize user interface elements with the given set of Attributes.
   */
  public void setProjectAttributes(List<Attribute> attributes);

  /** Update a single attribute */
  public void refreshAttribute(Attribute attribute);

  /** All of this project's labels.  Used for autocomplete drop-down. */
  public void setProjectLabels(Collection<String> projectLabels);

  /**
   * Data on which elements have been signed off.
   */
  public void setSignoffs(List<Signoff> signoffs);

  /**
   * Updates the view to enable editing of attribute data.
   */
  public void enableEditing();

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
}
