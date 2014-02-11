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
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedEvent;
import com.google.testing.testify.risk.frontend.client.event.WidgetsReorderedHandler;
import com.google.testing.testify.risk.frontend.client.presenter.ComponentPresenter;
import com.google.testing.testify.risk.frontend.client.view.ComponentsView;
import com.google.testing.testify.risk.frontend.client.view.widgets.SortableVerticalPanel;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Signoff;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A widget for controlling the Components page of a project.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentsViewImpl extends Composite implements ComponentsView {

  interface ComponentsViewImplUiBinder extends UiBinder<Widget, ComponentsViewImpl> {}
  private static final ComponentsViewImplUiBinder uiBinder =
          GWT.create(ComponentsViewImplUiBinder.class);

  @UiField
  public SortableVerticalPanel<ComponentViewImpl> componentsPanel;

  @UiField
  public HorizontalPanel addNewComponentPanel;

  @UiField
  public TextBox newComponentName;

  @UiField
  public Button addNewComponentButton;

  // Handle to the underlying Presenter corresponding to this View.
  private Presenter presenter;
  private boolean editingEnabled;
  private final Collection<String> projectLabels = Lists.newArrayList();
  private Map<Long, Boolean> signedOff = Maps.newHashMap();
  private Map<Long, ComponentPresenter> childPresenters = Maps.newHashMap();

  /**
   * Constructs a ProjectSettings object.
   */
  public ComponentsViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    // When the widget list is reordered, notify the presenter.
    componentsPanel.addWidgetsReorderedHandler(
        new WidgetsReorderedHandler() {
          @Override
          public void onWidgetsReordered(WidgetsReorderedEvent event) {
            List<Long> attributeIDs = Lists.newArrayList();
            for (Widget widget : event.getWidgetOrdering()) {
              attributeIDs.add(((ComponentViewImpl) widget).getComponentId());
            }

            if (presenter != null) {
              presenter.reorderComponents(attributeIDs);
            }
          }
        });

    // TODO(jimr): Update this when/if GWT supports setting attributes on textboxes directly.
    newComponentName.getElement().setAttribute("placeholder", "Add a new component...");
  }

  /**
   * Returns a new Component widget to be displayed on the componentsList.
   */
  private ComponentViewImpl createComponentWidget(Component component) {
    ComponentViewImpl componentView = new ComponentViewImpl();
    if (editingEnabled) {
      componentView.enableEditing();
    }
    Boolean checked = signedOff.get(component.getComponentId());
    componentView.setSignedOff(checked == null ? false : checked);
    componentView.setLabelSuggestions(projectLabels);
    ComponentPresenter componentPresenter = new ComponentPresenter(
        component, componentView, this.presenter);
    childPresenters.put(component.getComponentId(), componentPresenter);
    return componentView;
  }

  /**
   * Handler for hitting enter in the new component text box.
   */
  @UiHandler("newComponentName")
  protected void onComponentNameEnter(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      addNewComponentButton.click();
    }
  }

  /**
   * Handler for the addNewComponentButton's click event. Adds a new project Component.
   */
  @UiHandler("addNewComponentButton")
  protected void onAddNewComponentButtonClicked(ClickEvent event) {
    if (newComponentName.getText().trim().length() == 0) {
      Window.alert("Error: Please enter a Component name.");
      return;
    }

    // Create new Component and attach to UI
    Component newComponent = new Component(presenter.getProjectId());
    newComponent.setName(newComponentName.getText());

    presenter.createComponent(newComponent);
    // Upon creation the presenter will do a full refresh. No need to rebuild the Component list.
    newComponentName.setText("");
  }

  /**
   * Binds this View to the given Presenter.
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Updates the UI to display the given set of Components.
   */
  @Override
  public void setProjectComponents(List<Component> components) {
    List<ComponentViewImpl> componentWidgets = Lists.newArrayList();
    for (Component component : components) {
      componentWidgets.add(createComponentWidget(component));
    }

    // Build the widgets list.
    componentsPanel.setWidgets(componentWidgets,
        new Function<ComponentViewImpl, Widget>() {
          @Override
          public Widget apply(ComponentViewImpl arg) {
            return arg.componentGripper;
          }
        });
  }

  @Override
  public void refreshComponent(Component component) {
    ComponentPresenter childPresenter = childPresenters.get(component.getComponentId());
    childPresenter.refreshView(component);
  }

  @Override
  public void setSignoffs(List<Signoff> signoffs) {
    signedOff.clear();
    if (signoffs != null) {
      for (Signoff s : signoffs) {
        signedOff.put(s.getElementId(), s.getSignedOff());
      }
    }
    for (Widget w : componentsPanel) {
      ComponentViewImpl view = (ComponentViewImpl) w;
      Boolean checked = signedOff.get(view.getComponentId());
      view.setSignedOff(checked == null ? false : checked);
    }
  }

  @Override
  public void setProjectLabels(Collection<String> projectLabels) {
    this.projectLabels.clear();
    this.projectLabels.addAll(projectLabels);
    for (Widget w : componentsPanel) {
      AttributeViewImpl view = (AttributeViewImpl) w;
      view.setLabelSuggestions(this.projectLabels);
    }
  }

  @Override
  public void enableEditing() {
    editingEnabled = true;
    addNewComponentPanel.setVisible(true);

    // Go through any existing components being displayed, and set their 'readwrite' flag.
    for (Widget widget : componentsPanel) {
      if (widget.getClass().equals(ComponentViewImpl.class)) {
        ((ComponentViewImpl) widget).enableEditing();
      }
    }
  }
}
