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


package com.google.testing.testify.risk.frontend.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * General error page for unrecoverable errors.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class GeneralErrorPage extends Composite {

  /** Used to wire parent class to associated UI Binder. */
  interface GeneralErrorPageUiBinder extends UiBinder<Widget, GeneralErrorPage> {}
  private static final GeneralErrorPageUiBinder uiBinder =
      GWT.create(GeneralErrorPageUiBinder.class);

  @UiField
  public Label errorType;

  @UiField
  public Label errorText;

  public GeneralErrorPage() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setErrorType(String errorTypeText) {
    errorType.setText(errorTypeText);
  }

  public void setErrorText(String text) {
    this.errorText.setText(text);
  }
}
