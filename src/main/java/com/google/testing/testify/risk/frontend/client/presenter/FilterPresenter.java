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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.testing.testify.risk.frontend.client.view.ConfigureFiltersView;
import com.google.testing.testify.risk.frontend.client.view.FilterView;
import com.google.testing.testify.risk.frontend.model.Filter;
import com.google.testing.testify.risk.frontend.model.FilterOption;

import java.util.List;
import java.util.Map;

/**
 * Presenter for an individual Filter.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public class FilterPresenter implements FilterView.Presenter {

  private final Filter filter;
  private final FilterView view;
  private final ConfigureFiltersView.Presenter presenter;
  
  public FilterPresenter(Filter filter, Map<String, Long> attributes, Map<String, Long> components,
      Map<String, Long> capabilities, FilterView view, ConfigureFiltersView.Presenter presenter) {
    this.filter = filter;
    this.view = view;
    this.presenter = presenter;
    view.setPresenter(this);
    view.setAttributes(attributes);
    view.setComponents(components);
    view.setCapabilities(capabilities);
    refreshView();
  }
  
  @Override
  public void refreshView() {
    view.setFilterTitle(filter.getTitle());
    view.setFilterOptionChoices(filter.getFilterType().getFilterTypes());
    view.setFilterSettings(filter.getFilterOptions(), filter.getFilterConjunction(),
        filter.getTargetAttributeId(), filter.getTargetComponentId(),
        filter.getTargetCapabilityId());
  }

  @Override
  public void onUpdate(List<FilterOption> newOptions, String conjunction, Long attribute,
      Long component, Long capability) {
    filter.setFilterOptions(newOptions);
    filter.setFilterConjunction(conjunction);
    filter.setTargetAttributeId(attribute);
    filter.setTargetCapabilityId(capability);
    filter.setTargetComponentId(component);
    presenter.updateFilter(filter);
  }

  @Override
  public void onRemove() {
    view.hide();
    presenter.deleteFilter(filter);
  }
}
