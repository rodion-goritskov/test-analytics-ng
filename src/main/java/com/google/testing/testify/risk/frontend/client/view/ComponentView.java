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
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Component;

import java.util.List;

/**
 * View for a Component widget.
 * {@See com.google.testing.testify.risk.frontend.model.Component}
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface ComponentView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {
    /**
     * Returns the project ID of the target Component.
     */
    public long getProjectId();

    /**
     * Returns the Component ID of the target Component.
     */
    public long getComponentId();

    public void updateSignoff(boolean newSignoff);

    public void onDescriptionEdited(String description);

    /**
     * Called when the user renames the given component.
     */
    public void onRename(String newComponentName);

    /**
     * Called when the user deletes the given Component.
     */
    public void onRemove();

    /**
     * Called when the user updates a label.
     */
    public void onUpdateLabel(AccLabel label, String newText);

    /**
     * Called when the user adds a new Label.
     */
    public void onAddLabel(String label);

    /**
     * Called when the user removes a label.
     */
    public void onRemoveLabel(AccLabel label);

    public void refreshView(Component component);

    /** Requests the presenter refresh the view's controls. */
    public void refreshView();
  }

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  public void setPresenter(Presenter presenter);

  /**
   * Set the UI to display the Component's name.
   */
  public void setComponentName(String componentName);

  public void setDescription(String description);

  public void setSignedOff(boolean signedOff);

  /**
   * Set the UI to display the Component's ID.
   */
  public void setComponentId(Long componentId);

  /**
   * Set the UI to display the given Component labels.
   */
  public void setComponentLabels(List<AccLabel> componentLabels);

  /**
   * Updates the view to enable editing of attribute data.
   */
  public void enableEditing();

  /**
   * Hides the Widget so it is no longer visible. (Typically after the Component has been deleted.)
   */
  public void hide();

  /**
   * Converts the view into a GWT widget.
   */
  public Widget asWidget();
}
