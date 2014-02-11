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
import com.google.testing.testify.risk.frontend.client.presenter.AttributePresenter;
import com.google.testing.testify.risk.frontend.client.view.AttributesView;
import com.google.testing.testify.risk.frontend.client.view.widgets.SortableVerticalPanel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Signoff;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A widget for controlling the Attributes of a project.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class AttributesViewImpl extends Composite implements AttributesView {
  interface AttributesViewImplUiBinder extends UiBinder<Widget, AttributesViewImpl> {}
  private static final AttributesViewImplUiBinder uiBinder =
          GWT.create(AttributesViewImplUiBinder.class);

  @UiField
  public SortableVerticalPanel<AttributeViewImpl> attributesPanel;

  @UiField
  public HorizontalPanel addNewAttributePanel;

  @UiField
  public TextBox newAttributeName;

  @UiField
  public Button addNewAttributeButton;

  // Handle to the underlying Presenter corresponding to this View.
  private Presenter presenter;
  private boolean editingEnabled;
  private final Collection<String> projectLabels = Lists.newArrayList();
  private Map<Long, Boolean> signedOff = Maps.newHashMap();
  private Map<Long, AttributePresenter> childPresenters = Maps.newHashMap();

  /**
   * Constructs a ProjectSettings object.
   */
  public AttributesViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    // When the widget list is reordered, notify the presenter.
    attributesPanel.addWidgetsReorderedHandler(
        new WidgetsReorderedHandler() {
          @Override
          public void onWidgetsReordered(WidgetsReorderedEvent event) {
            List<Long> attributeIDs = Lists.newArrayList();
            for (Widget widget : event.getWidgetOrdering()) {
              attributeIDs.add(((AttributeViewImpl) widget).getAttributeId());
            }

            if (presenter != null) {
              presenter.reorderAttributes(attributeIDs);
            }
          }
        });

    // TODO(jimr): Update this when/if GWT supports setting attributes on textboxes directly.
    newAttributeName.getElement().setAttribute("placeholder", "Add a new attribute...");
  }

  /**
   * Handler for hitting enter in the new attribute text box.
   */
  @UiHandler("newAttributeName")
  void onAttributeNameEnter(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      addNewAttributeButton.click();
    }
  }

  /**
   * Handler for the addNewAttributeButton's click event. Adds a new project Attribute.
   */
  @UiHandler("addNewAttributeButton")
  void onAddNewAttributeButtonClicked(ClickEvent event) {
    if (newAttributeName.getText().trim().length() == 0) {
      Window.alert("Error: Please enter a name for the Attribute.");
      return;
    }

    // Create new Attribute and attach to UI
    Attribute newAttribute = new Attribute();
    newAttribute.setParentProjectId(presenter.getProjectId());
    newAttribute.setName(newAttributeName.getText());

    presenter.createAttribute(newAttribute);
    newAttributeName.setText("");
    // Don't display the new attribute widget. Instead, wait for a full refresh from the presenter.
  }

  private AttributeViewImpl createAttributeWidget(Attribute attribute) {
    AttributeViewImpl attributeView = new AttributeViewImpl();
    if (editingEnabled) {
      attributeView.enableEditing();
    }
    AttributePresenter attributePresenter = new AttributePresenter(
        attribute, attributeView, this.presenter);
    childPresenters.put(attribute.getAttributeId(), attributePresenter);
    Boolean checked = signedOff.get(attribute.getAttributeId());
    attributeView.setSignedOff(checked == null ? false : checked);
    attributeView.setLabelSuggestions(projectLabels);
    return attributeView;
  }

  /**
   * Binds this View to the given Presenter.
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Updates the UI to display the given set of Attributes.
   */
  @Override
  public void setProjectAttributes(List<Attribute> attributes) {
    List<AttributeViewImpl> attributeWidgets = Lists.newArrayList();
    for (Attribute attribute : attributes) {
      attributeWidgets.add(createAttributeWidget(attribute));
    }

    // Build the widgets list.
    attributesPanel.setWidgets(attributeWidgets,
        new Function<AttributeViewImpl, Widget>() {
          @Override
          public Widget apply(AttributeViewImpl arg) {
            return arg.attributeGripper;
          }
        });
  }

  @Override
  public void refreshAttribute(Attribute attribute) {
    AttributePresenter presenter = childPresenters.get(attribute.getAttributeId());
    presenter.refreshView(attribute);
  }

  @Override
  public void setSignoffs(List<Signoff> signoffs) {
    signedOff.clear();
    if (signoffs != null) {
      for (Signoff s : signoffs) {
        signedOff.put(s.getElementId(), s.getSignedOff());
      }
    }
    for (Widget w : attributesPanel) {
      AttributeViewImpl view = (AttributeViewImpl) w;
      Boolean checked = signedOff.get(view.getAttributeId());
      view.setSignedOff(checked == null ? false : checked);
    }
  }

  @Override
  public void setProjectLabels(Collection<String> projectLabels) {
    this.projectLabels.clear();
    this.projectLabels.addAll(projectLabels);
    for (Widget w : attributesPanel) {
      AttributeViewImpl view = (AttributeViewImpl) w;
      view.setLabelSuggestions(this.projectLabels);
    }
  }

  @Override
  public void enableEditing() {
    editingEnabled = true;
    addNewAttributePanel.setVisible(true);
    // Go through any existing attributes being displayed, and set their 'readwrite' flag.
    for (Widget widget : attributesPanel) {
      if (widget.getClass().equals(AttributeViewImpl.class)) {
        ((AttributeViewImpl) widget).enableEditing();
      }
    }
  }
}
