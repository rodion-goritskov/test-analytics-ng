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


package com.google.testing.testify.risk.frontend.testing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Utility class for simplifying Easy Mock unit tests.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public abstract class EasyMockUtils {
  private EasyMockUtils() {}

  /** Sets the return value for the last call to EasyMock. */
  public static <T> void setLastReturnValue(final T result) {
    EasyMock.expectLastCall().andReturn(result);
  }

  /**
   * Gets the {@link AsyncCallback} (last parameter of the previous function call) and ensures that
   * the onSuccess method gets called with the given result. This is needed because GWT's async RPC
   * calls return their value as part of an {@link AsyncCallback}, which is difficult to mock.
   */
  public static <T> void setLastAsyncCallbackSuccessWithResult(final T result) {
    EasyMock.expectLastCall().andAnswer(new IAnswer<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public T answer() throws Throwable {
        final Object[] arguments = EasyMock.getCurrentArguments();
        AsyncCallback<T> callback = (AsyncCallback<T>) arguments[arguments.length - 1];
        callback.onSuccess(result);
        return null;
      }
    });
  }

  /**
   * Gets the {@link AsyncCallback} (last parameter of the previous function call) and ensures that
   * the onFailure method gets called with the given {@code Throwable}.
   */
  public static <T> void setLastAsyncCallbackFailure(final Throwable exception) {
    EasyMock.expectLastCall().andAnswer(new IAnswer<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public T answer() throws Throwable {
        final Object[] arguments = EasyMock.getCurrentArguments();
        AsyncCallback<T> callback = (AsyncCallback<T>) arguments[arguments.length - 1];
        callback.onFailure(exception);
        return null;
      }
    });
  }
}
