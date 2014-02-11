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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Label which turns into an editable TextArea widget on click.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class EditableLabel extends Composite implements HasText, HasValue<String> {

  interface EditableLabelUiBinder extends UiBinder<Widget, EditableLabel> {}
  private static final EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

  @UiField
  public Label label;

  @UiField
  public TextBox textArea;

  @UiField
  public DeckPanel deckPanel;

  @UiField
  public FocusPanel focusPanel;

  private static final int LABEL_INDEX = 0;
  private static final int TEXTAREA_INDEX = 1;
  private boolean isReadOnly = false;

  public EditableLabel() {
    initWidget(uiBinder.createAndBindUi(this));

    deckPanel.showWidget(LABEL_INDEX);

    // When we receive focus, switch to edit mode.
    focusPanel.addFocusHandler(
        new FocusHandler() {
          @Override
          public void onFocus(FocusEvent event) {
            switchToEdit();
          }
        });

    // When the label is clicked, switch to edit mode.
    label.addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            switchToEdit();
          }
        });

    // When focus leaves the text area, switch to display/readonly mode.
    textArea.addBlurHandler(
        new BlurHandler() {
          @Override
          public void onBlur(BlurEvent event) {
            switchToLabel();
          }
        });

    // On key Enter, commit the text and fire a change event.
    // On key Down, revert the text if the user presses escape.
    textArea.addKeyDownHandler(
      new KeyDownHandler() {
        @Override
        public void onKeyDown(KeyDownEvent event) {
          if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            switchToLabel();
          } else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
            textArea.setText(label.getText());
            switchToLabel();
          }
        }
      });
  }

  /**
   * Sets the widget's ReadOnly flag, which prevents editing.
   */
  public void setReadOnly(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
    deckPanel.showWidget(0);
  }

  /** Switches the widget into 'Edit' mode. */
  public void switchToEdit() {
    if (isReadOnly || deckPanel.getVisibleWidget() == TEXTAREA_INDEX) {
      return;
    }

    textArea.setText(getValue());
    deckPanel.showWidget(TEXTAREA_INDEX);
    textArea.setFocus(true);
  }

  /** Switches the widget into 'Readonly' mode. */
  public void switchToLabel() {
    if (deckPanel.getVisibleWidget() == LABEL_INDEX) {
      return;
    }
    // Fires the ValueChanged event.
    setValue(textArea.getText(), true);
    deckPanel.showWidget(LABEL_INDEX);
  }

  // Implementation of HasValue<String>, which adds notification of text changed events.
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  @Override
  public String getValue() {
    return getText();
  }

  @Override
  public void setValue(String value) {
    setText(value);
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    // Set the value before we fire the event, so that consumers can normalize the text in any event
    // handlers.
    String startingValue = getValue();
    setValue(value);

    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, startingValue, value);
    }
  }

  // Implementation of HasText, enabling you to set the default text when used in a UIBinder.
  @Override
  public void setText(String text) {
    label.setText(text);
    textArea.setText(text);
  }

  @Override
  public String getText() {
    return label.getText();
  }
}
