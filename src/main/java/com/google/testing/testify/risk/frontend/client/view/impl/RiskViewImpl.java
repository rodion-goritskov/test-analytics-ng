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

package com.google.testing.testify.risk.frontend.client.view.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.client.view.RiskView;
import com.google.testing.testify.risk.frontend.client.view.widgets.EasyDisclosurePanel;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageSectionVerticalPanel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Base Widget for displaying Risk and/or Mitigation. (A glorified 2D heat map.) The control acts
 * as a repository of project Attributes, Components, and Capabilities so that future
 * risk providers can rely on that data to do visualization.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @author jimr@google.com (Jim Reardon)
 */
public abstract class RiskViewImpl extends Composite
    implements RiskView, HasValueChangeHandlers<Pair<Integer, Integer>> {

  /** Enum for tracking the required pieces of information to fully initialize a risk view. */
  private enum RequiredDataType {
    ATTRIBUTES,
    COMPONENTS,
    CAPABILITIES
  }

  /**
   * Used to wire parent class to associated UI Binder.
   */
  interface RiskViewImplUiBinder extends UiBinder<Widget, RiskViewImpl> { }
  private static final RiskViewImplUiBinder uiBinder = GWT.create(RiskViewImplUiBinder.class);

  @UiField
  public PageSectionVerticalPanel pageSectionPanel;

  @UiField
  public Label introTextLabel;

  @UiField
  public Grid baseGrid;

  /** Panel to hold custom content from a derived class. */
  @UiField
  public VerticalPanel content;

  /** Panel to hold custom content at the bottom of the widget. */
  @UiField
  public SimplePanel bottomContent;

  /**
   * Map of intersection key to data stored inside a CapabilityIntersectionData object.
   */
  private final Map<Integer, CapabilityIntersectionData> dataMap = Maps.newHashMap();

  /**
   * Map of intersection key to capability list.
   */
  private final Multimap<Integer, Capability> capabilityMap = HashMultimap.create();
  private final HashSet<RequiredDataType> initializedDataTypes = Sets.newHashSet();
  private final ArrayList<Component> components = Lists.newArrayList();
  private final ArrayList<Attribute> attributes = Lists.newArrayList();

  private Pair<Integer, Integer> selectedCell;

  /**
   * Constructs a new instance of the RiskViewImpl widget. For the UI to display something, call
   * {@link #setComponents(List)}, {@link #setAttributes(List)}, and {@link #setCapabilities(List)}
   * next.
   */
  public RiskViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    setPageText("", "");
  }

  @UiFactory
  public EasyDisclosurePanel createDisclosurePanel() {
    Label header = new Label("Risk displayed by Attribute and Component");
    header.addStyleName("tty-DisclosureHeader");

    return new EasyDisclosurePanel(header);
  }

  /**
   * Sets the risk page's introductory text.
   *
   * @param titleText the text displayed on the top, for example "Risk Factors".
   * @param introText the page text, explaining what the data illustrates.
   */
  protected void setPageText(String titleText, String introText) {
    pageSectionPanel.setHeaderText(titleText);
    introTextLabel.setText(introText);
  }

  @Override
  public void setComponents(List<Component> components) {
    this.components.clear();
    this.components.addAll(components);

    initializedDataTypes.add(RequiredDataType.COMPONENTS);
    initializeGrid();
    initializeRiskCells();
  }

  @Override
  public void setAttributes(List<Attribute> attributes) {
    this.attributes.clear();
    this.attributes.addAll(attributes);

    initializedDataTypes.add(RequiredDataType.ATTRIBUTES);
    initializeGrid();
    initializeRiskCells();
  }

  @Override
  public void setCapabilities(List<Capability> newCapabilities) {
    capabilityMap.clear();
    for (Capability capability : newCapabilities) {
      capabilityMap.put(capability.getCapabilityIntersectionKey(), capability);
    }

    initializedDataTypes.add(RequiredDataType.CAPABILITIES);
    initializeRiskCells();
  }

   /**
    * Called for derived classes once the risk view has been fully initilzied. (All Attributes,
    * Components, and Capabilities have been specified.)
    */
  protected abstract void onInitialized();

  @Override
  public Widget asWidget() {
    return this;
  }

  /**
   * Initializes the Risk grid headers.
   */
   void initializeGrid() {
    // We need both attributes and components for this control to make sense.
    if ((!initializedDataTypes.contains(RequiredDataType.ATTRIBUTES))
        || (!initializedDataTypes.contains(RequiredDataType.COMPONENTS))) {
      return;
    }

    baseGrid.clear();
    baseGrid.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          cellClicked(baseGrid.getCellForEvent(event));
        }
      });
    baseGrid.resize(components.size() + 1, attributes.size() + 1);

    CellFormatter formatter = baseGrid.getCellFormatter();
    formatter.setStyleName(0, 0, "tty-GridXHeaderCell");

    // Initialize the column and row headers.
    for (int cIndex = 0; cIndex < components.size(); cIndex++) {
      Label headerLabel = new Label(components.get(cIndex).getName());
      formatter.setStyleName(cIndex + 1, 0, "tty-GridXHeaderCell");
      baseGrid.setWidget(cIndex + 1, 0,  headerLabel);
    }
    for (int aIndex = 0; aIndex < attributes.size(); aIndex++) {
      Label headerLabel = new Label(attributes.get(aIndex).getName());
      formatter.setStyleName(0, aIndex + 1, "tty-GridYHeaderCell");
      baseGrid.setWidget(0, aIndex + 1, headerLabel);
    }

    // Initialize the data rows.
    for (int cIndex = 0; cIndex < components.size(); cIndex++) {
      for (int aIndex = 0; aIndex < attributes.size(); aIndex++) {
        HTML html = new HTML("&nbsp;");
        baseGrid.setWidget(cIndex + 1, aIndex + 1, html);
      }
    }
  }

  /**
   * Determines if all information necessary for the grid has been loaded from the server.
   *
   * @return true if fully loaded, false if not.
   */
  protected boolean isInitialized() {
    // Make sure all required pieces of data are available.
    for (RequiredDataType type : RequiredDataType.values()) {
      if (!initializedDataTypes.contains(type)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Executes on clicking a cell.
   *
   * @param cell the cell clicked on.
   */
  private void cellClicked(HTMLTable.Cell cell) {
    if (cell != null) {
      int row = cell.getRowIndex();
      int column = cell.getCellIndex();

      // Ignore headers.
      if (row > 0 && column > 0) {
        // Unhighlight currently selected cell.
        if (selectedCell != null) {
          baseGrid.getCellFormatter().removeStyleName(selectedCell.getFirst(),
              selectedCell.getSecond(), "tty-RiskCellSelected");
        }
        baseGrid.getCellFormatter().addStyleName(row, column, "tty-RiskCellSelected");
        selectedCell = new Pair<Integer, Integer>(row, column);
        ValueChangeEvent.fire(this, selectedCell);
      }
    }
  }

  /**
   * Returns the CapabilityIntersectionData for a given row and column.
   *
   * @param row the row of the table you're interested in.
   * @param column the column of the table you're interested in.
   * @return data.
   */
  protected CapabilityIntersectionData getDataForCell(int row, int column) {
    int cIndex = row - 1;
    int aIndex = column - 1;
    if (aIndex < 0 || aIndex >= attributes.size() || cIndex < 0 || cIndex >= components.size()) {
      return null;
    }

    Attribute attribute = attributes.get(aIndex);
    Component component = components.get(cIndex);
    Integer key = Capability.getCapabilityIntersectionKey(component, attribute);
    return dataMap.get(key);
  }

  /**
   * Initialize the riskProviderCells field. (Maybe called more than once as asynchronous calls get
   * returned.)
   */
  private void initializeRiskCells() {
    if (!isInitialized()) {
      return;
    }

    for (Attribute attribute : attributes) {
      for (Component component : components) {
        Integer key = Capability.getCapabilityIntersectionKey(component, attribute);

        CapabilityIntersectionData data = new CapabilityIntersectionData(
            attribute, component, capabilityMap.get(key));

        dataMap.put(key, data);
      }
    }

    // Notify derived classes the risk view has been fully initialized, and is ready for painting.
    onInitialized();
  }

  /**
   * Refreshes the risk data for all cells, based on the risk provided by the passed in risk
   * provider.
   *
   * @param provider provider that determines risk.
   */
  protected void refreshRiskCalculation(RiskProvider provider) {
    if (provider == null) {
      return;
    }
    refreshRiskCalculation(Lists.newArrayList(provider));
  }

  /**
   * Refreshes the risk data for all cells, based on the risk provided by the passed in risk
   * providers.
   *
   * @param providers providers that determines risk (risk is additive).
   */
  protected void refreshRiskCalculation(List<RiskProvider> providers) {
    for (int cIndex = 0; cIndex < components.size(); cIndex++) {
      for (int aIndex = 0; aIndex < attributes.size(); aIndex++) {
        int row = cIndex + 1;
        int column = aIndex + 1;
        Attribute attribute = attributes.get(aIndex);
        Component component = components.get(cIndex);
        Integer key = Capability.getCapabilityIntersectionKey(component, attribute);
        CapabilityIntersectionData data = dataMap.get(key);

        double risk = 0.0;
        double mitigations = 0.0;
        // TODO(jimr): RiskProvider would be better off exposing a type instead of doing it based
        // off the returned positive/negative.
        for (RiskProvider provider : providers) {
          double sourceRisk = provider.calculateRisk(data);
          if (sourceRisk < 0) {
            mitigations += sourceRisk;
          } else {
            risk += sourceRisk;
          }
        }

        updateCell(row, column, risk, mitigations);
      }
    }
  }

  /**
   * Updates a cell with a new risk value.
   *
   * @param row the cell's row.
   * @param column the cell's column.
   * @param risk the new risk value.
   * @param mitigations the new mitigation value.
   */
  private void updateCell(int row, int column, double risk, double mitigations) {
    // Mitigations and risk don't need to be separate, but this gives us flexibility in the future.
    double totalRisk = risk + mitigations;
    int intensity = (int) (totalRisk * 10.0) * 10;
    if (intensity > 100) {
      intensity = 100;
    } else if (intensity < -100) {
      intensity = -100;
    }

    String intensityCss = "tty-RiskIntensity_" + Integer.toString(intensity);
    baseGrid.getCellFormatter().setStyleName(row, column, "tty-GridCell");
    baseGrid.getCellFormatter().addStyleName(row, column, intensityCss);
  }

  /**
   * Retreive a list of data for all intersection points.
   *
   * @return list of CapabilityIntersectionData objects for all intersections on the grid.
   */
  protected List<CapabilityIntersectionData> getIntersectionData() {
    return Lists.newArrayList(dataMap.values());
  }

  @Override
  public HandlerRegistration addValueChangeHandler(
      ValueChangeHandler<Pair<Integer, Integer>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
