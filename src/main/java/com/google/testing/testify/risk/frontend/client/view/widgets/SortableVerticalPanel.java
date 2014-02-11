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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.HasWidgetsReorderedHandler;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedEvent;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedHandler;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;

import java.util.Iterator;
import java.util.List;

/**
 * A VerticalPanel which supports reordering via drag and drop.
 *
 * @param <T> Type of the widget to display on the panel.
 * @author chrsmith@google.com (Chris Smith)
 */
public class SortableVerticalPanel<T extends Widget>  extends Composite
    implements HasWidgetsReorderedHandler, Iterable<Widget> {

  private final SimplePanel content = new SimplePanel();
  private VerticalPanel currentVerticalPanel = new VerticalPanel();

  public SortableVerticalPanel() {
    initWidget(content);
  }

  /**
   * Clears the vertical panel and adds the list of widgets. The provided function will be used to
   * get the widget's drag target (such as header text or a gripper image).
   *
   * @param widgets the list of widgets to display on the generated panel.
   * @param getDragTarget function for getting the 'dragable area' of any given widget. (Such as
   *                      a gripping area or header label.
   */
  public void setWidgets(List<T> widgets, Function<T, Widget> getDragTarget) {
    AbsolutePanel boundaryPanel = new AbsolutePanel();
    boundaryPanel.setSize("100%", "100%");
    boundaryPanel.clear();

    content.clear();
    content.add(boundaryPanel);

    // The VerticalPanel which actually holds the list of widgets.
    currentVerticalPanel = new VerticalPanel();

    // The VerticalPanelDropController handles DOM manipulation.
    final VerticalPanelDropController widgetDropController =
        new VerticalPanelDropController(currentVerticalPanel);

    boundaryPanel.add(currentVerticalPanel);
    DragHandler dragHandler = createDragHandler(currentVerticalPanel);

    PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);
    widgetDragController.setBehaviorMultipleSelection(false);
    widgetDragController.addDragHandler(dragHandler);
    widgetDragController.registerDropController(widgetDropController);

    // Add each widget to the VerticalPanel and enable dragging via its DragTarget.
    for (T widget : widgets) {
      currentVerticalPanel.add(widget);
      widgetDragController.makeDraggable(widget, getDragTarget.apply(widget));
    }
  }

  /**
   * Returns the number of Widgets on the VerticalPanel.
   */
  public int getWidgetCount() {
    return currentVerticalPanel.getWidgetCount();
  }

  /**
   * Returns the Widget at the specified index.
   */
  public Widget getWidget(int index) {
    return currentVerticalPanel.getWidget(index);
  }

  /**
   * Returns a generic DragHandler for notification of drag events.
   */
  private DragHandler createDragHandler(final VerticalPanel verticalPanel) {
    // TODO(chrsmith): Provide a way to hook into events. (Required to preserve ordering.)
    return new DragHandler() {
      @Override
      public void onDragEnd(DragEndEvent event) {
        List<Widget> widgetList = Lists.newArrayList();
        for (int index = 0; index < verticalPanel.getWidgetCount(); index++) {
          widgetList.add(verticalPanel.getWidget(index));
        }
        fireEvent(new WidgetsReorderedEvent(widgetList));
      }

      @Override
      public void onDragStart(DragStartEvent event) {}
      @Override
      public void onPreviewDragEnd(DragEndEvent event) {}
      @Override
      public void onPreviewDragStart(DragStartEvent event) {}
    };
  }

  @Override
  public HandlerRegistration addWidgetsReorderedHandler(WidgetsReorderedHandler handler) {
    return super.addHandler(handler, WidgetsReorderedEvent.getType());
  }

  @Override
  public Iterator<Widget> iterator() {
    return currentVerticalPanel.iterator();
  }
}
