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

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.view.DataRequestView;
import com.google.testing.testify.risk.frontend.client.view.widgets.ConstrainedParameterWidget;
import com.google.testing.testify.risk.frontend.client.view.widgets.CustomParameterWidget;
import com.google.testing.testify.risk.frontend.client.view.widgets.DataRequestParameterWidget;
import com.google.testing.testify.risk.frontend.model.DataRequestOption;

import java.util.List;

// TODO(chrsmith): Provide a readonly mode for non-editor users.

/**
 * Widget for displaying a DataRequest.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class DataRequestViewImpl extends Composite implements DataRequestView {

  /** Wire parent class to associated UI Binder. */
  interface DataRequestViewImplUiBinder extends UiBinder<Widget, DataRequestViewImpl> {}
  private static final DataRequestViewImplUiBinder uiBinder =
              GWT.create(DataRequestViewImplUiBinder.class);

  @UiField
  protected Label dataRequestSourceName;

  @UiField
  protected VerticalPanel dataRequestParameters;

  @UiField
  protected Anchor addParameterLink;

  @UiField
  protected Button updateDataRequest;

  @UiField
  protected Button cancelUpdateDataRequest;

  @UiField
  protected Image deleteDataRequestImage;

  /** List of allowable values for a parameter key. null if arbitrary keys allowed. */
  private final List<String> parameterKeyConstraint = Lists.newArrayList();

  /** Presenter associated with this View */
  private Presenter presenter;

  public DataRequestViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  /** When the user clicks the 'add parameter' link, add a new parameter to the end of the list. */
  @UiHandler("addParameterLink")
  protected void handleAddParameterLinkClick(ClickEvent event) {
    dataRequestParameters.add((Widget) createRequestWidget("", ""));
  }

  @UiHandler("updateDataRequest")
  void onUpdateDataRequestClicked(ClickEvent event) {
    // Gather up all parameters and notify the presenter.
    List<DataRequestOption> newOptions = Lists.newArrayList();
    for (Widget widget : dataRequestParameters) {
      DataRequestParameterWidget parameterWidget = (DataRequestParameterWidget) widget;
      newOptions.add(new DataRequestOption(parameterWidget.getParameterKey(),
          parameterWidget.getParameterValue()));
    }

    presenter.onUpdate(newOptions);
  }

  @UiHandler("cancelUpdateDataRequest")
  void onCancelUpdateDataRequestClicked(ClickEvent event) {
    presenter.refreshView();
  }

  /**
   * Handler for the deleteComponentImage's click event, removing the Component.
   */
  @UiHandler("deleteDataRequestImage")
  void onDeleteComponentImageClicked(ClickEvent event) {
    String promptText = "Are you sure you want to remove this data request?";
    if (Window.confirm(promptText)) {
      presenter.onRemove();
    }
  }

  /**
   * Initialize this View's Presenter object. (For two-way communication.)
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  /**
   * Hides the given Widget, equivalent to setVisible(false).
   */
  @Override
  public void hide() {
    this.setVisible(false);
  }

  @Override
  public void setDataSourceName(String dataSourceName) {
    dataRequestSourceName.setText(dataSourceName);
  }

  private DataRequestParameterWidget createRequestWidget(String name, String value) {
    final DataRequestParameterWidget param;
    if (parameterKeyConstraint != null) {
      param = new ConstrainedParameterWidget(parameterKeyConstraint, name, value);
    } else {
      param = new CustomParameterWidget(name, value);
    }
    param.addChangeHandler(new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent arg0) {
          Widget w = (Widget) param;
          dataRequestParameters.remove(w);
        }
      });
    return param;
  }

  @Override
  public void setDataSourceParameters(List<String> keyValues, List<DataRequestOption> options) {
    parameterKeyConstraint.clear();
    parameterKeyConstraint.addAll(keyValues);
    dataRequestParameters.clear();

    for (DataRequestOption option : options) {
      dataRequestParameters.add((Widget) createRequestWidget(option.getName(), option.getValue()));
    }

    // If there are no parameters already, add a blank line to encourage them to add some.
    if (options.size() < 1) {
      handleAddParameterLinkClick(null);
    }
  }
}
