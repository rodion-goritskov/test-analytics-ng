// Copyright 2010 Google Inc. All Rights Reserved

package com.google.testing.testify.risk.frontend.client.presenter;

/**
 * Base Presenter for all application pages.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public abstract class BasePagePresenter implements TaPagePresenter {

  /**
   * Refresh the view, including page data.
   *
   * By default, pages won't care about passed in page data and thus short circuit over to the
   * no-parameter refreshView unless explicitly implemented by the page presenter.
   *
   * @param pageData the parameter data for this page.
   * */
  @Override
  public void refreshView(String pageData) {
    refreshView();
  }
}
