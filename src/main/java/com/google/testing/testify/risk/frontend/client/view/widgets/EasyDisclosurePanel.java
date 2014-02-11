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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

/**
 * A disclosure panel that automatically switches the headers between a closed header and
 * an open header for you.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class EasyDisclosurePanel extends Composite implements HasWidgets {

  private final DisclosurePanel panel = new DisclosurePanel();

  private final Widget openedHeader;
  private final Widget closedHeader;
  private final HorizontalPanel header = new HorizontalPanel();

  private final Image expandedImage = new Image("images/expanded_12.png");
  private final Image collapsedImage = new Image("images/collapsed_12.png");

  /**
   * Constructs an Easy Disclosure Panel with the same widget for the closed / opened
   * header.  Easy Disclosure Panel will automatically change the expanded/collapsed
   * image.
   *
   * @param header widget to display along side the +/- zippy.
   */
  public EasyDisclosurePanel(Widget header) {
    this(header, null);
  }

  public EasyDisclosurePanel(Widget openedHeader, Widget closedHeader) {
    this.openedHeader = openedHeader;
    this.closedHeader = closedHeader;

    header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    header.setStyleName("tty-DisclosurePanelHeader");
    setHeaderOpened();

    panel.setAnimationEnabled(true);
    panel.addStyleName("tty-DisclosurePanel");
    panel.setOpen(true);
    panel.setHeader(header);
    panel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
      @Override
      public void onClose(CloseEvent<DisclosurePanel> event) {
        setHeaderClosed();
      }
    });
    panel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
      @Override
      public void onOpen(OpenEvent<DisclosurePanel> event) {
        setHeaderOpened();
      }
    });

    initWidget(panel);
  }

  private void setHeaderClosed() {
    header.clear();
    header.add(collapsedImage);
    header.setCellWidth(collapsedImage, "20px");
    header.add(closedHeader != null ? closedHeader : openedHeader);
  }

  private void setHeaderOpened() {
    header.clear();
    header.add(expandedImage);
    header.setCellWidth(expandedImage, "20px");
    header.add(openedHeader);
  }

  public void setOpen(boolean open) {
    panel.setOpen(open);
  }

  @Override
  public void add(Widget w) {
    panel.add(w);
  }

  @Override
  public void clear() {
    panel.clear();
  }

  @Override
  public Iterator<Widget> iterator() {
    return panel.iterator();
  }

  @Override
  public boolean remove(Widget w) {
    return panel.remove(w);
  }
}
