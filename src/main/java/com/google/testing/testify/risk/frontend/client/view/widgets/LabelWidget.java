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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;

import java.util.Collection;

/**
 * Displays a label with a little delete X as well.  You can click the X to delete the label.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class LabelWidget extends Composite implements HasValue<String>,
    HasValueChangeHandlers<String> {

  private HorizontalPanel contentPanel = new HorizontalPanel();
  private Image image = new Image();
  // The deck panel will have to entries -- the view mode, and the edit mode.  DeckPanel will
  // only make one visible at a time.
  private DeckPanel deckPanel = new DeckPanel();
  private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(" -");
  private SuggestBox inputBox = new SuggestBox(oracle);
  private Label label = new Label();

  private static final int VIEW_MODE = 0;
  private static final int EDIT_MODE = 1;

  private boolean canEdit = false;

  public LabelWidget(String text) {
    this(text, false);
  }

  /**
   * Constructs a label for a given object, allowing customized styling.  The constructed
   * label will use styles:
   *   tty-GenericLabel
   *   tty-GenericLabelRemoveLabelImage
   * @param text the textual representation for this label.
   * @param isAddWidget true if this is a "new label..." widget, false if not.
   */
  public LabelWidget(String text, final boolean isAddWidget) {
    DefaultSuggestionDisplay suggestionDisplay =
      (DefaultSuggestionDisplay) inputBox.getSuggestionDisplay();
    suggestionDisplay.setPopupStyleName("tty-SuggestBoxPopup");
    contentPanel.addStyleName("tty-RemovableLabel");

    if (isAddWidget) {
      // Craft a little plus image.
      image.setStyleName("tty-RemovableLabelAddImage");
      image.setUrl("images/collapsed_12.png");
      image.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            switchMode(EDIT_MODE);
          }
        });
      contentPanel.add(image);
    }

    // Craft the view mode.
    label.setText(text);
    label.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent arg0) {
        switchMode(EDIT_MODE);
      }
    });
    deckPanel.add(label);

    // Craft the edit mode.
    if (!isAddWidget) {
      inputBox.setText(text);
    }
    inputBox.getTextBox().addBlurHandler(new BlurHandler() {
        @Override
        public void onBlur(BlurEvent arg0) {
          switchMode(VIEW_MODE);
        }
      });
    inputBox.getTextBox().addKeyDownHandler(new KeyDownHandler() {
        @Override
        public void onKeyDown(KeyDownEvent event) {
          if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            switchMode(VIEW_MODE);
          } else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
            if (isAddWidget) {
              inputBox.setText("");
            } else {
              inputBox.setText(label.getText());
            }
            switchMode(VIEW_MODE);
          }
        }
      });
    // Explicitly does not call switchMode to avoid logic inside that function that would think
    // we have switched from edit mode to view mode.
    deckPanel.showWidget(VIEW_MODE);
    deckPanel.add(inputBox);

    contentPanel.add(deckPanel);

    if (!isAddWidget) {
      // Craft the delete button.
      image.setStyleName("tty-RemovableLabelDeleteImage");
      image.setUrl("images/x.png");
      image.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            setValue(null, true);
          }
        });
      contentPanel.add(image);
    }

    // Set some alignments to make the widget pretty.
    contentPanel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
    contentPanel.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
    initWidget(contentPanel);
  }

  private void switchMode(int mode) {
    // Just keep them in view mode if they can't edit this widget.
    if (!canEdit) {
      deckPanel.showWidget(VIEW_MODE);
      return;
    }
    if (mode == VIEW_MODE) {
      String text = inputBox.getText();
      if (!text.equals(label.getText())) {
        // Don't save an empty label.
        if (!"".equals(text)) {
          // We have updates to save.
          setValue(inputBox.getText(), true);
        }
      }
    }
    int width = label.getOffsetWidth();
    deckPanel.showWidget(mode);
    if (mode == EDIT_MODE) {
      inputBox.setWidth(String.valueOf(String.valueOf(width)) + "px");
      inputBox.setFocus(true);
    }
  }

  public void setEditable(boolean canEdit) {
    this.canEdit = canEdit;
    image.setVisible(canEdit);
  }

  /**
   * Sets the list of suggestions for the autocomplete box.
   * @param suggestions list of items to suggest off of.
   */
  public void setLabelSuggestions(Collection<String> suggestions) {
    oracle.clear();
    oracle.addAll(suggestions);
  }

  @Override
  public String getValue() {
    return label.getText();
  }

  @Override
  public void setValue(String value) {
    setValue(value, false);
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    String old = label.getText();
    label.setText(value);
    inputBox.setText(value);
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, old, value);
    }
  }

  /**
   * Will fire when value of text changes or delete is clicked (the value will be null if deleted).
   */
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
