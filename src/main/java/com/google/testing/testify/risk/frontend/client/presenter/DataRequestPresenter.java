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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.testing.testify.risk.frontend.client.view.ConfigureDataView;
import com.google.testing.testify.risk.frontend.client.view.DataRequestView;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataRequestOption;
import com.google.testing.testify.risk.frontend.model.DataSource;

import java.util.List;

/**
 * Presenter for an individual DataRequest view.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class DataRequestPresenter implements DataRequestView.Presenter {

  private final DataRequest targetRequest;
  private final DataSource dataSource;
  private final DataRequestView view;
  private final ConfigureDataView.Presenter parentPresenter;

  public DataRequestPresenter(DataRequest request, DataSource dataSource, DataRequestView view,
        ConfigureDataView.Presenter parentPresenter) {

    this.dataSource = dataSource;
    this.targetRequest = request;
    this.view = view;
    this.parentPresenter = parentPresenter;

    refreshView();
  }

  /**
   * Refreshes UI elements of the View.
   */
  @Override
  public void refreshView() {
    view.setPresenter(this);
    String dataSourceName = targetRequest.getDataSourceName();
    view.setDataSourceName(dataSourceName);
    view.setDataSourceParameters(dataSource.getParameters(), targetRequest.getDataRequestOptions());
  }

  @Override
  public void onUpdate(List<DataRequestOption> newParameters) {
    targetRequest.setDataRequestOptions(newParameters);
    parentPresenter.updateDataRequest(targetRequest);
  }

  @Override
  public void onRemove() {
    view.hide();
    parentPresenter.deleteDataRequest(targetRequest);
  }
}
