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
import com.google.testing.testify.risk.frontend.model.DataRequestOption;

import java.util.List;

/**
 * View for a DataRequest widget.
 * {@See com.google.testing.testify.risk.frontend.model.DataRequest}
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface DataRequestView {

  /** Interface for notifying the Presenter about events arising from the View. */
  public interface Presenter {
    /** Called when the user updates the DataRequest. */
    public void onUpdate(List<DataRequestOption> newParameters);

    /** Called when the user deletes the given DataRequest. */
    public void onRemove();

    /** Requests the presenter refresh the view's controls. */
    public void refreshView();
  }

  /** Bind the view and the underlying presenter it communicates with. */
  public void setPresenter(Presenter presenter);

  /** Set the UI to display the Component's name. */
  public void setDataSourceName(String componentName);

  /**
   * Update the UI to display the data source parameters.
   * @param keyValues the set of allowable key values. null if arbitrary keys allowed.
   * @param parameters set of key/value pairs making up the data source's parameters.
   **/
  public void setDataSourceParameters(List<String> keyValues, List<DataRequestOption> parameters);

  /** Hides the Widget so it is no longer visible. (Typically after it has been deleted.) */
  public void hide();

  /** Converts the view into a GWT widget. */
  public Widget asWidget();
}
