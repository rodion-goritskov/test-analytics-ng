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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.view.FilterView;
import com.google.testing.testify.risk.frontend.client.view.widgets.ConstrainedParameterWidget;
import com.google.testing.testify.risk.frontend.model.FilterOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of FilterView.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public class FilterViewImpl extends Composite implements FilterView {

  interface FilterViewImplUiBinder extends UiBinder<Widget, FilterViewImpl> {}
  private static final FilterViewImplUiBinder uiBinder =
              GWT.create(FilterViewImplUiBinder.class);

  @UiField
  protected Label filterName;
  
  @UiField
  protected ListBox anyOrAllBox;

  @UiField
  protected VerticalPanel filterOptions;
  
  @UiField
  protected Anchor addOptionLink;
  
  @UiField
  protected Button updateFilter;
  
  @UiField
  protected Button cancelUpdateFilter;
  
  @UiField
  protected Image deleteFilterImage;

  @UiField
  protected ListBox attributeBox;

  @UiField
  protected ListBox componentBox;

  @UiField
  protected ListBox capabilityBox;

  private Presenter presenter;
  private List<String> filterOptionChoices = Lists.newArrayList();
  private Long attribute;
  private Long component;
  private Long capability;
  
  private static final String NONE_VALUE = "<< none >>";

  public FilterViewImpl() {
    List<String> items = Lists.newArrayList();
    initWidget(uiBinder.createAndBindUi(this));

    anyOrAllBox.addItem("any", "any");
    anyOrAllBox.addItem("all", "all");
  }

  /** When the user clicks the 'add option' link, add a new option input to the end of the list. */
  @UiHandler("addOptionLink")
  protected void handleAddOptionLinkClick(ClickEvent event) {
    filterOptions.add(createRequestWidget("", ""));
  }

  @UiHandler("updateFilter")
  void onUpdateFilterClicked(ClickEvent event) {
    ArrayList<FilterOption> options = Lists.newArrayList();
    // Iterate through each widget on the Filter Options Vertical Panel, as each of those will be
    // a parameter to the filter.
    for (Widget widget : filterOptions) {
      ConstrainedParameterWidget param = (ConstrainedParameterWidget) widget;
      String type = param.getParameterKey();
      String value = param.getParameterValue();
      FilterOption option = new FilterOption(type, value);
      options.add(option);
    }
    String conjunction = anyOrAllBox.getValue(anyOrAllBox.getSelectedIndex());
    attribute = stringToId(attributeBox.getValue(attributeBox.getSelectedIndex()));
    component = stringToId(componentBox.getValue(componentBox.getSelectedIndex()));
    capability = stringToId(capabilityBox.getValue(capabilityBox.getSelectedIndex()));
    presenter.onUpdate(options, conjunction, attribute, component, capability);
  }

  private ConstrainedParameterWidget createRequestWidget(String name, String value) {
    final ConstrainedParameterWidget param = new ConstrainedParameterWidget(
        filterOptionChoices, name, value);

    param.addChangeHandler(new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent arg0) {
          filterOptions.remove(param);
        }
      });
    return param;
  }

  // Will return NULL if the user has selected the NONE option, otherwise the string is parsed
  // into a Long.
  private Long stringToId(String string) {
    if (NONE_VALUE.equals(string)) {
      return null;
    }
    return Long.parseLong(string);
  }

  @UiHandler("cancelUpdateFilter")
  void onCancelUpdateFilterClicked(ClickEvent event) {
    presenter.refreshView();
  }

  @UiHandler("deleteFilterImage")
  void onDeleteFilterImageClicked(ClickEvent event) {
    String promptText = "Are you sure you want to remove this filter?";
    if (Window.confirm(promptText)) {
      presenter.onRemove();
    }
  }

  @Override
  public void setFilterSettings(List<FilterOption> options, String conjunction, Long attribute,
      Long component, Long capability) {
    // Select the conjunctor they have set.
    selectInListBoxByValue(anyOrAllBox, conjunction);

    // Store the selected ACC parts.
    this.attribute = attribute;
    selectInListBoxByValue(attributeBox, attribute);
    this.component = component;
    selectInListBoxByValue(componentBox, component);
    this.capability = capability;
    selectInListBoxByValue(capabilityBox, capability);

    filterOptions.clear();

    for (FilterOption option : options) {
      filterOptions.add(createRequestWidget(option.getType(), option.getValue()));
    }

    // If there's not a filter option yet, show an empty blank.
    if (options.size() < 1) {
      handleAddOptionLinkClick(null);
    }
  }

  private void selectInListBoxByValue(ListBox box, Long value) {
    selectInListBoxByValue(box, value == null ? null : value.toString());
  }

  private void selectInListBoxByValue(ListBox box, String value) {
    // Default to first item, which in many boxes will be the none value.
    box.setSelectedIndex(0);
    for (int i = 0; i < box.getItemCount(); i++) {
      if (box.getValue(i).equals(value)) {
        box.setSelectedIndex(i);
      }
    }
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void hide() {
    setVisible(false);
  }

  @Override
  public void setFilterTitle(String title) {
    filterName.setText(title);
  }

  @Override
  public void setFilterOptionChoices(List<String> filterOptionChoices) {
    this.filterOptionChoices = filterOptionChoices;
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  private void populateAccBox(ListBox box, Map<String, Long> items) {
    box.clear();
    box.addItem(NONE_VALUE, NONE_VALUE);
    for (String key : items.keySet()) {
      Long id = items.get(key);
      box.addItem(key, id.toString());
    }
  }
  @Override
  public void setAttributes(Map<String, Long> attributes) {
    populateAccBox(attributeBox, attributes);
    selectInListBoxByValue(attributeBox, attribute);
  }

  @Override
  public void setCapabilities(Map<String, Long> capabilities) {
    populateAccBox(capabilityBox, capabilities);
    selectInListBoxByValue(capabilityBox, capability);
  }

  @Override
  public void setComponents(Map<String, Long> components) {
    populateAccBox(componentBox, components);
    selectInListBoxByValue(componentBox, component);
  }
}
