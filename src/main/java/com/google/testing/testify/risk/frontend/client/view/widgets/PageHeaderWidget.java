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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.model.LoginStatus;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

/**
 * Header widget for all pages, containing the logo, login information, and so on.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class PageHeaderWidget extends Composite {
  interface PageHeaderWidgetUiBinder extends UiBinder<Widget, PageHeaderWidget> {}

  private static final PageHeaderWidgetUiBinder uiBinder =
      GWT.create(PageHeaderWidgetUiBinder.class);

  @UiField
  protected Label userEmailAddress;

  @UiField
  protected Label userEmailAddressDivider;

  @UiField
  protected Anchor signInOrOutLink;

  @UiField
  protected Anchor feedbackLink;

  private final UserRpcAsync userService;

  /**
   * Constructs a PageHeaderWidget instance.
   */
  public PageHeaderWidget(UserRpcAsync userService) {
    initWidget(uiBinder.createAndBindUi(this));
    this.userService = userService;
    initializeLoginBar();
  }

  @UiHandler("feedbackLink")
  protected void onFeedbackClick(ClickEvent event) {
    startFeedback();
  }

  private static native void startFeedback() /*-{
    $wnd.userfeedback.api.startFeedback({ productId : 69289 });
  }-*/;

  /**
   * Initializes the login bar with the current user's email address if applicable.
   */
  private void initializeLoginBar() {
    String returnUrl = Location.getHref();

    userService.getLoginStatus(returnUrl,
        new TaCallback<LoginStatus>("Querying Login Status") {
          @Override
          public void onSuccess(LoginStatus result) {
            refreshView(result);
          }
        });
  }

  /**
   * Refreshes UI elements based on the user's current login status.
   */
  public void refreshView(LoginStatus status) {
    userEmailAddress.setText(status.getEmail());
    signInOrOutLink.setHref(status.getUrl());

    if (status.getIsLoggedIn()) {
      userEmailAddressDivider.setVisible(true);
      signInOrOutLink.setText("Sign out");
    } else {
      userEmailAddressDivider.setVisible(false);
      signInOrOutLink.setText("Sign in");
    }
  }
}
