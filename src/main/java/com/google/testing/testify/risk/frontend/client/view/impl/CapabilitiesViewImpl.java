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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedEvent;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedHandler;
import com.google.testing.testify.risk.frontend.client.view.CapabilitiesView;
import com.google.testing.testify.risk.frontend.client.view.widgets.CapabilitiesGridWidget;
import com.google.testing.testify.risk.frontend.client.view.widgets.EasyDisclosurePanel;
import com.google.testing.testify.risk.frontend.client.view.widgets.EditCapabilityWidget;
import com.google.testing.testify.risk.frontend.client.view.widgets.SortableVerticalPanel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Pair;

import java.util.Collection;
import java.util.List;

/**
 * Generic view on top of a Project's Capabilities. Note that this View has two alternate View
 * methods (List and Grid) both wired to the same Model, View, Presenter setup.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CapabilitiesViewImpl extends Composite implements CapabilitiesView {

  private static final String HEADER_TEXT = "Capabilities by Attribute and Component";

  /**
   * Used to wire parent class to associated UI Binder.
   */
  interface CapabilitiesViewImplUiBinder extends UiBinder<Widget, CapabilitiesViewImpl> {}

  private static final CapabilitiesViewImplUiBinder uiBinder =
      GWT.create(CapabilitiesViewImplUiBinder.class);

  @UiField
  public CapabilitiesGridWidget capabilitiesGrid;

  @UiField
  public VerticalPanel capabilitiesContainer;

  @UiField
  public Label capabilitiesContainerTitle;

  @UiField
  public HorizontalPanel addNewCapabilityPanel;

  @UiField
  public TextBox newCapabilityName;

  @UiField
  public Button addNewCapabilityButton;

  @UiField
  public SortableVerticalPanel<EditCapabilityWidget> capabilitiesPanel;

  private List<Capability> capabilities;
  private List<Component> components;
  private List<Attribute> attributes;
  private final Collection<String> projectLabels = Lists.newArrayList();

  private Pair<Component, Attribute> selectedIntersection;

  private boolean isEditable = false;

  private Presenter presenter;

  /**
   * Constructs a CapabilitiesViewImpl object.
   */
  public CapabilitiesViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    capabilitiesGrid.addValueChangeHandler(new ValueChangeHandler<Pair<Component,Attribute>>() {
        @Override
        public void onValueChange(ValueChangeEvent<Pair<Component, Attribute>> event) {
          selectedIntersection = event.getValue();
          newCapabilityName.setText("");
          tryToPopulateCapabilitiesPanel();
        }
      });

    capabilitiesPanel.addWidgetsReorderedHandler(
        new WidgetsReorderedHandler() {
          @Override
          public void onWidgetsReordered(WidgetsReorderedEvent event) {
            if (presenter != null) {
              List<Long> ids = Lists.newArrayList();
              for (Widget w : event.getWidgetOrdering()) {
                ids.add(((EditCapabilityWidget) w).getCapabilityId());
              }

              presenter.reorderCapabilities(ids);
            }
          }
        });

    newCapabilityName.getElement().setAttribute("placeholder", "Add new capability...");
  }

  @UiFactory
  public EasyDisclosurePanel createDisclosurePanel() {
    Label header = new Label(HEADER_TEXT);
    header.addStyleName("tty-DisclosureHeader");

    return new EasyDisclosurePanel(header);
  }

  /**
   * Handler for hitting enter in the new capability text box.
   */
  @UiHandler("newCapabilityName")
  void onComponentNameEnter(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      addNewCapabilityButton.click();
    }
  }

  @UiHandler("addNewCapabilityButton")
  protected void handleNewButton(ClickEvent e) {
    String name = newCapabilityName.getText().trim();
    if (name.length() == 0) {
      Window.alert("Please enter a name for the capability.");
      return;
    }

    long cId = selectedIntersection.getFirst().getComponentId();
    long aId = selectedIntersection.getSecond().getAttributeId();
    long pId = selectedIntersection.getSecond().getParentProjectId();
    Capability c = new Capability(pId, aId, cId);
    c.setName(name);

    presenter.onAddCapability(c);
    newCapabilityName.setText("");
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setCapabilities(List<Capability> capabilities) {
    this.capabilities = capabilities;
    capabilitiesGrid.setCapabilities(capabilities);
    tryToPopulateCapabilitiesPanel();
  }

  @Override
  public void setAttributes(List<Attribute> attributes) {
    this.attributes = attributes;
    capabilitiesGrid.setAttributes(attributes);
    tryToPopulateCapabilitiesPanel();
  }

  @Override
  public void setComponents(List<Component> components) {
    this.components = components;
    capabilitiesGrid.setComponents(components);
    tryToPopulateCapabilitiesPanel();
  }

  @Override
  public void setProjectLabels(Collection<String> labels) {
    projectLabels.clear();
    projectLabels.addAll(labels);
    for (Widget w : capabilitiesContainer) {
      EditCapabilityWidget capabilityWidget = (EditCapabilityWidget) w;
      capabilityWidget.setLabelSuggestions(projectLabels);
    }
  }

  private void updateCapability(Capability capability) {
    presenter.onUpdateCapability(capability);
    capabilitiesGrid.updateCapability(capability);
    tryToPopulateCapabilitiesPanel();
  }

  private void removeCapability(Capability capability) {
    presenter.onRemoveCapability(capability);
    capabilitiesGrid.deleteCapability(capability);
    capabilities.remove(capability);
    tryToPopulateCapabilitiesPanel();
  }

  /**
   * Populates the capabilities panel with capabilities widgets.  This requires data already be
   * loaded, so it will not show the panel if all data has not been loaded.
   */
  private void tryToPopulateCapabilitiesPanel() {
    if (selectedIntersection != null && attributes != null && components != null
          && capabilities != null) {
      Component component = selectedIntersection.getFirst();
      Attribute attribute = selectedIntersection.getSecond();

      capabilitiesContainer.setVisible(true);
      capabilitiesContainerTitle.setText(component.getName() + " is " + attribute.getName());

      List<EditCapabilityWidget> widgets = Lists.newArrayList();
      for (final Capability capability : capabilities) {
        // If we're interested in this capability (it matches our current filter).
        if (capability.getComponentId() == component.getComponentId()
            && capability.getAttributeId() == attribute.getAttributeId()) {
          // Create and populate a capability widget for this capability.
          final EditCapabilityWidget widget = new EditCapabilityWidget(capability);
          widget.addValueChangeHandler(new ValueChangeHandler<Capability>() {
              @Override
              public void onValueChange(ValueChangeEvent<Capability> event) {
                if (event.getValue() == null) {
                  // Since the value is null, we'll just use the old value to grab the ID.
                  removeCapability(capability);
                } else {
                  updateCapability(event.getValue());
                  widget.showSaved();
                }
              }
            });
          widget.setComponents(components);
          widget.setAttributes(attributes);
          widget.setLabelSuggestions(projectLabels);
          if (isEditable) {
            widget.makeEditable();
          }
          widgets.add(widget);
        }
      }

      capabilitiesPanel.setWidgets(widgets, new Function<EditCapabilityWidget, Widget>() {
        @Override
        public Widget apply(EditCapabilityWidget input) {
          return input.getCapabilityGripper();
        }
      });
    } else {
      capabilitiesContainer.setVisible(false);
    }
  }

  @Override
  public void setEditable(boolean isEditable) {
    this.isEditable = isEditable;
    for (Widget w : capabilitiesPanel) {
      if (isEditable) {
        ((EditCapabilityWidget) w).makeEditable();
      }
    }
    addNewCapabilityPanel.setVisible(isEditable);
  }

  @Override
  public void addCapability(Capability capability) {
    capabilitiesGrid.addCapability(capability);
    // Insert at top.
    capabilities.add(0, capability);
    // TODO (jimr): instead of a full refresh, we should just pop the new widget
    // on top.  However, the sortable panel doesn't really handle adding a single
    // widget, so a full refresh is the path of least resistance currently.
    tryToPopulateCapabilitiesPanel();
  }
}
