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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A widget for displaying a set of capabilities in a Grid.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilitiesGridWidget extends Composite
    implements HasValueChangeHandlers<Pair<Component, Attribute>> {

  private final List<Attribute> attributes = Lists.newArrayList();
  private final List<Component> components = Lists.newArrayList();

  // Map from intersection key to capability list.
  private final Multimap<Integer, Capability> capabilityMap = HashMultimap.create();
  // Map from intersection key to table cell.
  private final Map<Integer, HTML> cellMap = Maps.newHashMap();

  private final Grid grid = new Grid();

  private int highlightedCellRow = -1;
  private int highlightedCellColumn = -1;

  public CapabilitiesGridWidget() {
    grid.addStyleName("tty-ComponentAttributeGrid");
    grid.addStyleName("tty-CapabilitiesGrid");

    initWidget(grid);
  }

  /**
   * Adds a single capability without clearing out prior capabilities.
   *
   * @param capability capability to add.
   */
  public void addCapability(Capability capability) {
    Integer key = capability.getCapabilityIntersectionKey();

    // Add the capability.
    addCapabilityWithoutUpdate(capability);

    // Update relevant UI field.
    updateGridCell(key);
  }

  public void deleteCapability(Capability capability) {
    for (Integer key : capabilityMap.keySet()) {
      Collection<Capability> c = capabilityMap.get(key);
      if (c.contains(capability)) {
        c.remove(capability);
        updateGridCell(key);
      }
    }
  }

  public void updateCapability(Capability capability) {
    deleteCapability(capability);
    addCapability(capability);
  }

  public void setAttributes(List<Attribute> attributes) {
    this.attributes.clear();
    this.attributes.addAll(attributes);
    redrawGrid();
  }

  public void setComponents(List<Component> components) {
    this.components.clear();
    this.components.addAll(components);
    redrawGrid();
  }

  public void setCapabilities(List<Capability> capabilities) {
    capabilityMap.clear();
    for (Capability capability : capabilities) {
      addCapabilityWithoutUpdate(capability);
    }

    updateGridCells();
  }

  private void addCapabilityWithoutUpdate(Capability capability) {
    Integer key = capability.getCapabilityIntersectionKey();
    capabilityMap.put(key, capability);
  }

  /**
   * Completely recreates the grid then calls updateGridCells() to update contents of grid.
   * This should be called if the list of components or attributes changes.
   */
  private void redrawGrid() {
    grid.clear();
    cellMap.clear();

    if (components.size() < 1 || attributes.size() < 1) {
      return;
    }

    grid.resize(components.size() + 1, attributes.size() + 1);
    grid.getCellFormatter().setStyleName(0, 0, "tty-GridXHeaderCell");

    // Add component headers.
    for (int i = 0; i < components.size(); i++) {
      Label label = new Label(components.get(i).getName());
      grid.getCellFormatter().setStyleName(i + 1, 0, "tty-GridXHeaderCell");
      grid.setWidget(i + 1, 0, label);
    }

    // Add attribute headers.
    for (int i = 0; i < attributes.size(); i++) {
      Label label = new Label(attributes.get(i).getName());
      grid.getCellFormatter().setStyleName(0, i + 1, "tty-GridYHeaderCell");
      grid.setWidget(0, i + 1, label);
    }

    // Fill up the rest of the grid with labels.
    for (int cIndex = 0; cIndex < components.size(); cIndex++) {
      for (int aIndex = 0; aIndex < attributes.size(); aIndex++) {
        int row = cIndex + 1;
        int column = aIndex + 1;

        Component component = components.get(cIndex);
        Attribute attribute = attributes.get(aIndex);
        Integer key = Capability.getCapabilityIntersectionKey(component, attribute);

        grid.getCellFormatter().setStyleName(row, column, "tty-GridCell");
        if (row == highlightedCellRow && column == highlightedCellColumn) {
          grid.getCellFormatter().addStyleName(row, column, "tty-GridCellSelected");
        }

        HTML cell = new HTML();
        cell.addClickHandler(cellClickHandler(row, column));

        cell.addMouseOverHandler(createMouseOverHandler(row, column));
        cell.addMouseOutHandler(createMouseOutHandler(row, column));

        cellMap.put(key, cell);
        grid.setWidget(row, column, cell);
      }
    }

    updateGridCells();
  }

  /**
   * Creates a mouse over handler for a specific row and column.
   *
   * @param row the row number.
   * @param column the column number.
   * @return the mouse over handler.
   */
  private MouseOverHandler createMouseOverHandler(final int row, final int column) {
    return new MouseOverHandler() {
      @Override
      public void onMouseOver(MouseOverEvent event) {
        mouseOver(row, column);
      }
    };
  }

  /**
   * Generates a mouse out handler for a specific row and column.
   *
   * @param row the row.
   * @param column the column.
   * @return a mouse out handler.
   */
  private MouseOutHandler createMouseOutHandler(final int row, final int column) {
    return new MouseOutHandler() {
      @Override
      public void onMouseOut(MouseOutEvent event) {
        mouseOut(row, column);
      }
    };
  }

  /**
   * Handles a mouse out event by removing the formatting on cells that were once highlighted due
   * to a mouse over event.
   *
   * @param row the row that lost mouse over.
   * @param column the column that lost mouse over.
   */
  private void mouseOut(int row, int column) {
    CellFormatter formatter = grid.getCellFormatter();
    // Remove highlighting from cell.
    formatter.removeStyleName(row, column, "tty-GridCellHighlighted");

    // Remove column highlighting.
    for (int i = 1; i < grid.getRowCount(); i++) {
      formatter.removeStyleName(i, column, "tty-GridColumnHighlighted");
    }

    // Remove row highlighting.
    for (int j = 1; j < grid.getColumnCount(); j++) {
      formatter.removeStyleName(row, j, "tty-GridRowHighlighted");
    }
  }

  /**
   * Handles a mouse over event by highlighting the moused over cell and adding style to the
   * row and column that is also to be highlighted.
   *
   * @param row the row that gained mouse over.
   * @param column the column that gained mouse over.
   */
  private void mouseOver(int row, int column) {
    CellFormatter formatter = grid.getCellFormatter();
    // Add highlighting to cell.
    formatter.addStyleName(row, column, "tty-GridCellHighlighted");

    // Add column highlighting.
    for (int i = 1; i < grid.getRowCount(); i++) {
      if (i != row) {
        formatter.addStyleName(i, column, "tty-GridColumnHighlighted");
      }
    }

    // Add row highlighting.
    for (int j = 1; j < grid.getColumnCount(); j++) {
      if (j != column) {
        formatter.addStyleName(row, j, "tty-GridRowHighlighted");
      }
    }
  }

  /**
   * This updates the contents of the grid based off the current capability data.
   */
  private void updateGridCells() {
    for (Integer key : cellMap.keySet()) {
      updateGridCell(key);
    }
  }

  private void updateGridCell(Integer key) {
    int size = capabilityMap.get(key).size();

    String text = "&nbsp;";

    if (size > 0) {
      text = Integer.toString(size);
    }

    cellMap.get(key).setHTML(text);
  }

  private ClickHandler cellClickHandler(final int row, final int column) {
    final HasValueChangeHandlers<Pair<Component, Attribute>> self = this;
    return new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        cellClicked(row, column);
      }
    };
  }

  private void cellClicked(int row, int column) {
    final Component component = components.get(row - 1);
    final Attribute attribute = attributes.get(column - 1);

    if (highlightedCellRow != -1 && highlightedCellColumn != -1) {
      grid.getCellFormatter().removeStyleName(
          highlightedCellRow, highlightedCellColumn, "tty-GridCellSelected");
    }
    grid.getCellFormatter().addStyleName(row, column, "tty-GridCellSelected");
    highlightedCellRow = row;
    highlightedCellColumn = column;
    ValueChangeEvent.fire(this, new Pair<Component, Attribute>(component, attribute));
  }

  @Override
  public HandlerRegistration addValueChangeHandler(
      ValueChangeHandler<Pair<Component, Attribute>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
