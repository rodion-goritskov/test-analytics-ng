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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent.DialogResult;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedHandler;
import com.google.testing.testify.risk.frontend.client.event.HasDialogClosedHandler;

/**
 * Standard dialog box with OK/Cancel buttons.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class StandardDialogBox extends Composite implements HasDialogClosedHandler {
  interface StandardDialogBoxUiBinder extends UiBinder<Widget, StandardDialogBox> {}

  private static final StandardDialogBoxUiBinder uiBinder =
      GWT.create(StandardDialogBoxUiBinder.class);

  @UiField
  protected VerticalPanel dialogContent;

  @UiField
  protected Button okButton;

  @UiField
  protected Button cancelButton;

  /** If this is displayed, get a handle to the owning dialog box. */
  private DialogBox dialogBox;

  public StandardDialogBox() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  /**
   * @return the dialog's content. Consumers will add any custom widgets to the returned Panel.
   */
  public Panel getDialogContent() {
    return dialogContent;
  }

  /**
   * Displays the Dialog.
   */
  public static void showAsDialog(StandardDialogBox dialogWidget) {
    DialogBox dialogBox = new DialogBox();
    dialogWidget.dialogBox = dialogBox;

    dialogBox.addStyleName("tty-StandardDialogBox");
    dialogBox.setText(dialogWidget.getTitle());
    dialogBox.add(dialogWidget);
    dialogBox.center();
    dialogBox.show();
  }

  /**
   * Gets called whenever the OK Button is clicked.
   */
  @UiHandler("okButton")
  public void handleOkButtonClicked(ClickEvent event) {
    if (dialogBox != null) {
      dialogBox.hide();
    }
    fireEvent(new DialogClosedEvent(DialogResult.OK));
  }

  /**
   * Gets called whenever the Cancel Button is clicked.
   */
  @UiHandler("cancelButton")
  public void handleCancelButtonClicked(ClickEvent event) {
    if (dialogBox != null) {
      dialogBox.hide();
    }
    fireEvent(new DialogClosedEvent(DialogResult.Cancel));
  }

  @Override
  public HandlerRegistration addDialogClosedHandler(DialogClosedHandler handler) {
    return super.addHandler(handler, DialogClosedEvent.getType());
  }

  public void add(Widget w) {
    dialogContent.add(w);
  }
}
