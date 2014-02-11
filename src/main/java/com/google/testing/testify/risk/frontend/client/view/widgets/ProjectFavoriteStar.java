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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

/**
 * Project favorite star. Used to track user favorites. Note that the project favorite star defines
 * the CSS style tty-ProjectFavoriteStar.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectFavoriteStar extends Composite {

  private final UserRpcAsync userService;
  private boolean isStarred = false;
  private final Image starImage = new Image();

  private static final String STAR_ON_URL = "images/star-on.png";
  private static final String STAR_OFF_URL = "images/star-off.png";

  public ProjectFavoriteStar() {
    userService = GWT.create(UserRpc.class);

    starImage.setUrl(STAR_OFF_URL);
    starImage.addStyleName("tty-ProjectFavoriteStar");

    initWidget(starImage);
  }

  /** Set's the widget's Starred status. */
  public void setStarredStatus(boolean isStarred) {
    this.isStarred = isStarred;
    if (isStarred) {
      starImage.setUrl(STAR_ON_URL);
    } else {
      starImage.setUrl(STAR_OFF_URL);
    }
  }

  /**
   * Attach the current favorite star to the given Project ID. When this widget is clicked, it will
   * make an RPC call to add the project as a favorite of the user.
   */
  public void attachToProject(final long projectId) {
    // Add 'click status'.
    starImage.addClickHandler(
      new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          setStarredStatus(!isStarred);
          if (isStarred) {
            userService.starProject(projectId, TaCallback.getNoopCallback());
          } else {
            userService.unstarProject(projectId, TaCallback.getNoopCallback());
          }
        }
      });
  }
}
