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

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.view.ConfigureDataView;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;

import java.util.List;

/**
 * Presenter for the Configure Data page.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ConfigureDataPresenter extends BasePagePresenter
    implements TaPagePresenter, ConfigureDataView.Presenter {

  private final Project project;
  private final DataRpcAsync dataService;
  private final ConfigureDataView view;

  public ConfigureDataPresenter(
      Project project, DataRpcAsync rpcService,
      ConfigureDataView view) {

    this.project = project;
    this.dataService = rpcService;
    this.view = view;

    refreshView();
  }

  @Override
  public void refreshView() {
    view.setPresenter(this);

    dataService.getDataSources(
        new TaCallback<List<DataSource>>("Getting data source options") {
          @Override
          public void onSuccess(List<DataSource> result) {
            view.setDataSources(result);
          }
        });

    dataService.getProjectRequests(project.getProjectId(),
        new TaCallback<List<DataRequest>>("Getting data requests") {
          @Override
          public void onSuccess(List<DataRequest> result) {
            view.setDataRequests(result);
          }
        });
  }

  @Override
  public void addDataRequest(final DataRequest newRequest) {
    newRequest.setParentProjectId(project.getProjectId());
    dataService.addDataRequest(newRequest,
        new TaCallback<Long>("Updating data request") {
          @Override
          public void onSuccess(Long result) {
            newRequest.setRequestId(result);
            refreshView();
          }
        });
  }

  @Override
  public void updateDataRequest(DataRequest requestToUpdate) {
    dataService.updateDataRequest(requestToUpdate, TaCallback.getNoopCallback());
  }

  @Override
  public void deleteDataRequest(DataRequest requestToDelete) {
    dataService.removeDataRequest(requestToDelete,
        new TaCallback<Void>("Deleting data request") {
          @Override
          public void onSuccess(Void result) {
            refreshView();
          }
        });
  }

  @Override
  public Widget getView() {
    return view.asWidget();
  }
}
