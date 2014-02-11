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

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.testing.testify.risk.frontend.client.presenter.TaPagePresenter;

/**
 * Navigation link widget. This control is a wrapper on top of a GWT Hyperlink widget,
 * except that it enables you to disable the control as well as associate the link's target history
 * token with a given project.
 * <p>
 * The widget has three CSS properties:
 * tty-NavigationLink, which covers the entire widget.
 * tty-NavigationLinkText, which covers just the link text (even the disabled text).
 * tty-NavigationLinkTextDisabled, which covers the text when disabled.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class NavigationLink extends Composite implements HasText {

  private final DeckPanel panel;

  // The hyper link is displayed when the control is enabled; otherwise the fake link will be.
  private final Label fakeLink;
  private final Hyperlink realLink;

  private long projectId;
  private String targetHistoryToken;
  private TaPagePresenter presenter;
  private Function<Void, TaPagePresenter> createPresenterFunction;

  private static final int WIDGET_ID_ENABLED = 0;
  private static final int WIDGET_ID_DISABLED = 1;

  private static final String NAV_STYLE_UNSELECTED = "tty-LeftNavItem";
  private static final String NAV_STYLE_SELECTED = "tty-LeftNavItemSelected";
  private static final String NAV_STYLE_DISABLED = "tty-LeftNavItemDisabled";

  public NavigationLink() {
    this("", -1, "", null);
  }

  public NavigationLink(String text, long projectId, String targetHistoryToken,
      Function<Void, TaPagePresenter> createPresenterFunction) {
    this.targetHistoryToken = targetHistoryToken;
    this.projectId = projectId;
    this.createPresenterFunction = createPresenterFunction;

    panel = new DeckPanel();
    SimplePanel fakeLinkPanel = new SimplePanel();
    fakeLink = new Label(text);
    fakeLinkPanel.add(fakeLink);
    realLink = new Hyperlink(text, getHyperlinkTarget());
    panel.add(realLink);
    panel.add(fakeLinkPanel);
    enable();
    super.initWidget(panel);
  }

  public void setCreatePresenterFunction(Function<Void, TaPagePresenter> function) {
    this.createPresenterFunction = function;
  }

  public TaPagePresenter getPresenter() {
    if (presenter == null) {
      presenter = createPresenterFunction.apply(null);
    }
    return presenter;
  }

  @Override
  public void setText(String text) {
    fakeLink.setText(text);
    realLink.setText(text);
  }

  @Override
  public String getText() {
    return realLink.getText();
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
    realLink.setTargetHistoryToken(getHyperlinkTarget());
  }

  public void setTargetHistoryToken(String token) {
    this.targetHistoryToken = token;
    realLink.setTargetHistoryToken(getHyperlinkTarget());
  }

  public String getHistoryTokenName() {
    return targetHistoryToken;
  }

  /** Updates the hyperlink's target to encode project name and history token. */
  private String getHyperlinkTarget() {
    return "/" + projectId + "/" + targetHistoryToken;
  }

  public Hyperlink getHyperlink() {
    return realLink;
  }

  public void enable() {
    panel.setStyleName(NAV_STYLE_UNSELECTED);
    panel.showWidget(WIDGET_ID_ENABLED);
  }

  public void select() {
    panel.setStyleName(NAV_STYLE_SELECTED);
    panel.showWidget(WIDGET_ID_ENABLED);
  }

  public void unSelect() {
    enable();
  }

  public void disable() {
    panel.setStyleName(NAV_STYLE_DISABLED);
    panel.showWidget(WIDGET_ID_DISABLED);
  }
}
