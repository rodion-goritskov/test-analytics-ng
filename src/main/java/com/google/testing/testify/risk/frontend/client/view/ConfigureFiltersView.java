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


package com.google.testing.testify.risk.frontend.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Filter;

import java.util.List;
import java.util.Map;

/**
 * View on top of a page to configure filters.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public interface ConfigureFiltersView {

  /**
   * Interface for notifying the Presenter about events going down in the view.
   */
  public interface Presenter {
    void addFilter(Filter newFilter);
    void updateFilter(Filter filterToUpdate);
    void deleteFilter(Filter filterToDelete);
  }
  
  void setFilters(List<Filter> filters);
  void setAttributes(Map<String, Long> attributes);
  void setComponents(Map<String, Long> components);
  void setCapabilities(Map<String, Long> capabilities);
  void setPresenter(Presenter presenter);
  
  Widget asWidget();
}
