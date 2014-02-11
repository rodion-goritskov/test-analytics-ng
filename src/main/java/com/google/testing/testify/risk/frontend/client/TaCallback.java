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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.testing.testify.risk.frontend.client.util.NotificationUtil;

/**
 * General purpose async callback which routes failures to an error dialog.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @param <T> The return type of the async callback.
 */
public class TaCallback<T> implements AsyncCallback<T> {
  private String asyncCallPurpose;

  public static TaCallback<Void> getNoopCallback() {
    return new TaCallback<Void>("generic action");
  }

  /**
   * General purpose callback which handles failures by showing an error dialog.
   *
   * @param asyncCallPurpose the purpose of this call; showed in case of error, ie:
   *   "Error " + asyncCallPurpose + "." followed by exception detail.
   */
  public TaCallback(String asyncCallPurpose) {
    this.asyncCallPurpose = asyncCallPurpose;
  }

  /** On asynchronous failure, displays an error message to the user. */
  @Override
  public void onFailure(Throwable caught) {
    NotificationUtil.displayErrorMessage(
      "Error " + asyncCallPurpose + ".", caught);
  }

  @Override
  public void onSuccess(T result) {
  }
}
