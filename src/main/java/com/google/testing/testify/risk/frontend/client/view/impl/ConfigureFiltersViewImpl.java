// Copyright 2011 Google Inc. All Rights Reseved.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.presenter.FilterPresenter;
import com.google.testing.testify.risk.frontend.client.view.ConfigureFiltersView;
import com.google.testing.testify.risk.frontend.client.view.FilterView;
import com.google.testing.testify.risk.frontend.model.DatumType;
import com.google.testing.testify.risk.frontend.model.Filter;

import java.util.List;
import java.util.Map;

/**
 * View that shows all filters and lets you create a new one.
 *
 * A {@link Filter} will automatically assign data uploaded to specific ACC pieces.  For example,
 * a Filter may say "assign any test labeled with 'Security' to the Security Attribute.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ConfigureFiltersViewImpl extends Composite implements ConfigureFiltersView {

  interface ConfigureFiltersImplUiBinder extends UiBinder<Widget, ConfigureFiltersViewImpl> {}
  private static final ConfigureFiltersImplUiBinder uiBinder =
      GWT.create(ConfigureFiltersImplUiBinder.class);

  @UiField
  public VerticalPanel filtersPanel;

  @UiField
  public ListBox filterTypeBox;

  @UiField
  public Button addFilterButton;

  private List<Filter> filters;
  // These are needed as options for the Filter widgets.
  private Map<String, Long> attributes;
  private Map<String, Long> components;
  private Map<String, Long> capabilities;

  private Presenter presenter;

  public ConfigureFiltersViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    filterTypeBox.clear();
    for (DatumType type : DatumType.values()) {
      filterTypeBox.addItem(type.getPlural(), type.name());
    }
  }

  @UiHandler("addFilterButton")
  public void handleAddFilterButtonClicked(ClickEvent event) {
    if (presenter != null) {
      Filter filter = new Filter();

      String selected = filterTypeBox.getValue(filterTypeBox.getSelectedIndex());
      filter.setFilterType(DatumType.valueOf(selected));
      presenter.addFilter(filter);
    }
  }

  @Override
  public void setFilters(List<Filter> filters) {
    this.filters = filters;
    updateFilters();
  }

  private void updateFilters() {
    if (attributes != null && components != null && capabilities != null) {
      filtersPanel.clear();
      for (Filter filter : filters) {
        FilterView view = new FilterViewImpl();
        FilterPresenter filterPresenter = new FilterPresenter(filter, attributes, components,
            capabilities, view, presenter);
        filtersPanel.add(view.asWidget());
      }
    }
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setAttributes(Map<String, Long> attributes) {
    this.attributes = attributes;
    updateFilters();
  }

  @Override
  public void setCapabilities(Map<String, Long> capabilities) {
    this.capabilities = capabilities;
    updateFilters();
  }

  @Override
  public void setComponents(Map<String, Long> components) {
    this.components = components;
    updateFilters();
  }
}
