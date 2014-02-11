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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

/**
 * Customized VerticalPanel for a page. The following CSS classes are exposed by this
 * widget:
 *
 * tty-PageSectionVerticalPanel - The entire panel.
 * tty-PageSectionVerticalPanelHeader - The header text.
 * tty-PageSectionVerticalPanelItem - Each item in the panel.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class PageSectionVerticalPanel extends Composite implements HasWidgets {

  private final Label header = new Label();
  private final VerticalPanel content = new VerticalPanel();

  public PageSectionVerticalPanel() {
    header.addStyleName("tty-PageSectionVerticalPanelHeader");
    content.addStyleName("tty-PageSectionVerticalPanel");

    content.add(header);
    this.initWidget(content);
  }

  public void setHeaderText(String newTitle) {
    header.setText(newTitle);
  }
  @Override
  public void add(Widget w) {
    w.addStyleName("tty-PageSectionVerticalPanelItem");
    content.add(w);
  }

  @Override
  public void clear() {
    content.clear();
    content.add(header);
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
