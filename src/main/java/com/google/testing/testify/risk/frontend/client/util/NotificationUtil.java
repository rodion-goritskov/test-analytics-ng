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


package com.google.testing.testify.risk.frontend.client.util;

import com.google.gwt.user.client.ui.Label;
import com.google.testing.testify.risk.frontend.client.view.widgets.StandardDialogBox;

/**
 * Factory for displaying error messages on the client UI.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class NotificationUtil {

  /** Disable construction. */
  private NotificationUtil() {}

  /** Displays an error message generated from the given exception. */
  public static void displayErrorMessage(Throwable exception) {
    displayErrorMessage("Unhandled Exception", exception);
  }

  /** Displays an error message with the given text. */
  public static void displayErrorMessage(String errorText) {
    displayErrorMessage(errorText, null);
  }

  /** Displays an error message along with the provided exception information. */
  public static void displayErrorMessage(String errorMessage, Throwable exception) {
    StandardDialogBox widget = new StandardDialogBox();
    widget.setTitle("Oh snap! Test Analytics encountered an error.");
    widget.add(new Label(errorMessage));

    if (exception != null) {
      StringBuilder exceptionMessageText = new StringBuilder();
      exceptionMessageText.append("Exception of type: " + exception.getClass().getName());
      exceptionMessageText.append("\n");
      exceptionMessageText.append(exception.getMessage());
      widget.add(new Label(exceptionMessageText.toString()));
    }

    StandardDialogBox.showAsDialog(widget);
  }
}
