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
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * View on top of the Components page.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface ComponentsView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {
    /**
     * @return a handle to the Presenter's ProjectService serverlet. Ideally this should be hidden.
     */
    ProjectRpcAsync getProjectService();

    /**
     * @return the ProjectID for the project the View is displaying.
     */
    long getProjectId();

    /**
     * Notifies the Presenter that a new Component has been created.
     */
    public void createComponent(Component component);

    /**
     * Updates the given component in the database.
     */
    public void updateComponent(Component componentToUpdate);

    public void updateSignoff(Component attribute, boolean newSignoff);

    /**
     * Removes the given component from the Project.
     */
    public void removeComponent(Component componentToRemove);

    /**
     * Reorders the project's list of Components.
     */
    public void reorderComponents(List<Long> newOrder);
  }

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  public void setPresenter(Presenter presenter);

  /**
   * Initialize user interface elements with the given set of Components.
   */
  public void setProjectComponents(List<Component> components);

  public void refreshComponent(Component component);

  public void setProjectLabels(Collection<String> projectLabels);

  public void setSignoffs(List<Signoff> signoffs);
  /**
   * Updates the view to enable editing of component data.
   */
  public void enableEditing();

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
}
