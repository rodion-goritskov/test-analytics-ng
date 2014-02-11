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
import com.google.testing.testify.risk.frontend.model.FilterOption;

import java.util.List;
import java.util.Map;

/**
 * View for a Filter.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public interface FilterView {

  /** Interface for notifying the presenter about changes to this view. */
  public interface Presenter {
    /** Called when the options for this filter are updated. */
    public void onUpdate(List<FilterOption> newOptions, String conjunction, Long attribute,
        Long component, Long capability);
    /** Called when this filter is deleted. */
    public void onRemove();
    /** Called to refresh the view. */
    public void refreshView();
  }

  /** Binds the view to the presenter. */
  public void setPresenter(Presenter presenter);
  /** Set the title to display for this filter. */
  public void setFilterTitle(String title);
  /** Set the available options for this filter. */
  public void setFilterOptionChoices(List<String> filterOptionChoices);
  /** Set the list of current configuration of this filter: current options filtered on and
   * to what things are filtered.. */
  public void setFilterSettings(List<FilterOption> options, String conjunction, Long attribute,
      Long component, Long capability);
  /** Set current list of ACC parts. */
  public void setAttributes(Map<String, Long> attributes);
  public void setComponents(Map<String, Long> components);
  public void setCapabilities(Map<String, Long> capabilities);
  /** Makes the widget invisible. */
  public void hide();
  /** View as a GWT widget. */
  public Widget asWidget();
}
