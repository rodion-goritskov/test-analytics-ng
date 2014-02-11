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
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;

import java.util.List;

/**
 * View on top of a page for configuring project data sources.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface ConfigureDataView {

  /**
   * Interface for notifying the Presenter about events arising from the View.
   */
  public interface Presenter {
    /**
     * Adds a new data request.
     */
    void addDataRequest(DataRequest newRequest);

    /**
     * Update an existing data request.
     */
    void updateDataRequest(DataRequest requestToUpdate);

    /**
     * Removes a data request
     */
    void deleteDataRequest(DataRequest requestToDelete);
  }

  /**
   * Sets the list of enabled data providers.
   */
  void setDataRequests(List<DataRequest> dataRequests);

  /**
   * Provide the list of data sources.
   */
  void setDataSources(List<DataSource> dataSources);

  /**
   * Bind the view and the underlying presenter it communicates with.
   */
  void setPresenter(Presenter presenter);

  /**
   * Converts the view into a GWT widget.
   */
  Widget asWidget();
}
