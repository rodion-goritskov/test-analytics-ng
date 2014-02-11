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


package com.google.testing.testify.risk.frontend.client.view.impl;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.presenter.DataRequestPresenter;
import com.google.testing.testify.risk.frontend.client.view.ConfigureDataView;
import com.google.testing.testify.risk.frontend.client.view.DataRequestView;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;

import java.util.List;
import java.util.Map;

/**
 * A widget for configuring a project's data sources.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ConfigureDataViewImpl extends Composite implements ConfigureDataView {

  interface ConfigureDataViewImplUiBinder extends UiBinder<Widget, ConfigureDataViewImpl> {}
  private static final ConfigureDataViewImplUiBinder uiBinder =
      GWT.create(ConfigureDataViewImplUiBinder.class);

  @UiField
  public ListBox standardDataSourcesListBox;

  @UiField
  public TextBox dataSourceTextBox;

  @UiField
  public Button addDataRequestButton;

  @UiField
  public VerticalPanel dataRequestsPanel;

  private List<DataRequest> dataRequests = null;
  private Map<String, DataSource> dataSources = null;

  private Presenter presenter;

  /**
   * Constructs a ConfigureDataView object.
   */
  public ConfigureDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    dataSourceTextBox.getElement().setAttribute("placeholder", "Name this data source...");
  }

  @UiHandler("standardDataSourcesListBox")
  public void handleStandardDataSourcesListBoxChanged(ChangeEvent event) {
    DataSource source = getSelectedSource();
    // If this source doesn't have any defined options, it's our "Other..." source which means
    // we need them to input the name they want.
    dataSourceTextBox.setVisible(source.getParameters().size() < 1);
  }

  private DataSource getSelectedSource() {
    int dataSourceIndex = standardDataSourcesListBox.getSelectedIndex();
    String selectedSourceName = standardDataSourcesListBox.getItemText(dataSourceIndex);
    return dataSources.get(selectedSourceName);
  }

  @UiHandler("addDataRequestButton")
  public void handleAddDataRequestButtonClicked(ClickEvent event) {
    if (presenter != null) {
      DataSource source = getSelectedSource();

      if (source != null) {
        DataRequest newRequest = new DataRequest();
        newRequest.setDataSourceName(source.getName());
        if (source.getParameters().size() < 1) {
          newRequest.setCustomName(dataSourceTextBox.getText());
        }
        presenter.addDataRequest(newRequest);
      }
    }
  }

  /**
   * Binds this View to the given Presenter.
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setDataRequests(List<DataRequest> dataRequests) {
    this.dataRequests = dataRequests;
    updateDataRequests();
  }

  @Override
  public void setDataSources(List<DataSource> dataSources) {
    this.dataSources = Maps.newHashMap();
    standardDataSourcesListBox.clear();
    for (DataSource dataSource : dataSources) {
      this.dataSources.put(dataSource.getName(), dataSource);
      standardDataSourcesListBox.addItem(dataSource.getName());
    }
    updateDataRequests();
  }

  private void updateDataRequests() {
    // We need both DataRequests and DataSources to be populated before doing this, so wait for
    // both to be non-null.
    if (dataSources != null && dataRequests != null) {
      dataRequestsPanel.clear();

      for (DataRequest request : dataRequests) {
        DataRequestView view = new DataRequestViewImpl();

        DataRequestPresenter presenter = new DataRequestPresenter(request,
            dataSources.get(request.getDataSourceName()), view, this.presenter);
        dataRequestsPanel.add(view.asWidget());
      }
    }
  }
}
