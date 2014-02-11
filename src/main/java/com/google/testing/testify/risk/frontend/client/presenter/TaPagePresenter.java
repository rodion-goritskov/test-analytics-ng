// Copyright 2010 Google Inc.
package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base interface for all Presenters for Test Analytics pages.
 *
 * @author chrsmith@google.com (Chris)
 */
public interface TaPagePresenter {

  /** Refreshes the current view. Typically by querying the datastore and updating UI elements. */
  public void refreshView();

  /** Allows data to be passed in while refreshing the view. */
  public void refreshView(String pageData);

  /** Returns the underlying View. */
  public Widget getView();
}
