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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import java.util.List;

/**
 * Widget a constrained DataRequest parameter, where the key values are fixed.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ConstrainedParameterWidget extends Composite implements DataRequestParameterWidget {
  private final ListBox keyListBox = new ListBox();
  private final TextBox valueTextBox = new TextBox();
  private boolean isDeleted = false;
  private final Image removeParameterImage = new Image("/images/x.png");

  public ConstrainedParameterWidget(List<String> keyValues, String key, String value) {
    keyListBox.clear();
    for (String keyValue : keyValues) {
      keyListBox.addItem(keyValue);
    }

    int startingKeyIndex = -1;
    if (keyValues.contains(key)) {
      startingKeyIndex = keyValues.indexOf(key);
    } else {
      // Initialized with a key not in the key values? Likely a bug somewhere...
      startingKeyIndex = 0;
    }
    keyListBox.setSelectedIndex(startingKeyIndex);
    valueTextBox.setText(value);

    final HasHandlers self = this;
    removeParameterImage.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent arg0) {
          isDeleted = true;
          fireChangeEvent();
        }
      });
    HorizontalPanel panel = new HorizontalPanel();
    panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    panel.addStyleName("tty-DataRequestParameter");
    panel.add(keyListBox);
    panel.add(valueTextBox);
    panel.add(removeParameterImage);
    initWidget(panel);
  }

  private void fireChangeEvent() {
    NativeEvent event = Document.get().createChangeEvent();
    ChangeEvent.fireNativeEvent(event, this);
  }

  @Override
  public String getParameterKey() {
    if (isDeleted) {
      return null;
    }
    int selectedIndex = keyListBox.getSelectedIndex();
    return keyListBox.getItemText(selectedIndex);
  }

  @Override
  public String getParameterValue() {
    if (isDeleted) {
      return null;
    }
    return valueTextBox.getText();
  }

  @Override
  public boolean isDeleted() {
    return isDeleted;
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addHandler(handler, ChangeEvent.getType());
  }
}
