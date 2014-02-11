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

import com.google.gwt.user.client.ui.RootPanel;
import com.google.testing.testify.risk.frontend.client.TaClientTest;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent.DialogResult;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedHandler;

import junit.framework.Assert;

/**
 * Unit tests for the StandardDialogBox widget.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class StandardDialogBoxTest extends TaClientTest {

  /**
   * Test implementation of the DialogClosedHandler.
   */
  public class TestDialogClosedHandler implements DialogClosedHandler {

    private DialogResult result = null;

    public void reset() {
      result = null;
    }

    public DialogResult getDialogResult() {
      return result;
    }

    @Override
    public void onDialogClosed(DialogClosedEvent event) {
      result = event.getResult();
    }
  }

  public void testOK() {
    TestDialogClosedHandler handlerTest = new TestDialogClosedHandler();
    StandardDialogBox widget = new StandardDialogBox();

    RootPanel.get().clear();
    RootPanel.get().add(widget);
    widget.addDialogClosedHandler(handlerTest);

    widget.okButton.click();
    Assert.assertEquals(DialogResult.OK, handlerTest.getDialogResult());

    RootPanel.get().clear();
  }

  public void testCancel() {
    TestDialogClosedHandler handlerTest = new TestDialogClosedHandler();
    StandardDialogBox widget = new StandardDialogBox();

    RootPanel.get().clear();
    RootPanel.get().add(widget);
    widget.addDialogClosedHandler(handlerTest);

    widget.cancelButton.click();
    Assert.assertEquals(DialogResult.Cancel, handlerTest.getDialogResult());

    RootPanel.get().clear();
  }
}
