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


package com.google.testing.testify.risk.frontend.client.view.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

/**
 * Organization unit for a navigation pane.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class NavSectionPanel extends Composite implements HasWidgets {
  /** Used to wire parent class to associated UI Binder. */
  interface NavSectionPanelUiBinder extends UiBinder<Widget, NavSectionPanel> {}
  private static final NavSectionPanelUiBinder uiBinder = GWT.create(NavSectionPanelUiBinder.class);

  @UiField
  public Label sectionTitle;

  @UiField
  public VerticalPanel content;

  public NavSectionPanel() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public String getSectionTitle() {
    return sectionTitle.getText();
  }

  public void setSectionTitle(String newTitle) {
    sectionTitle.setText(newTitle);
  }

  @Override
  public void add(Widget w) {
    content.add(w);
  }

  @Override
  public void clear() {
    content.clear();
  }

  @Override
  public Iterator<Widget> iterator() {
    return content.iterator();
  }

  @Override
  public boolean remove(Widget w) {
    return content.remove(w);
  }
}
