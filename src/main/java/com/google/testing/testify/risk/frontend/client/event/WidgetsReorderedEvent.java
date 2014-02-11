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


package com.google.testing.testify.risk.frontend.client.event;

import com.google.common.collect.ImmutableList;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * Event type fired when a list of widgets has been reordered.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class WidgetsReorderedEvent extends GwtEvent<WidgetsReorderedHandler> {
  private static final Type<WidgetsReorderedHandler> TYPE = new Type<WidgetsReorderedHandler>();

  private final ImmutableList<Widget> widgets;

  public WidgetsReorderedEvent(List<Widget> widgets) {
    this.widgets = ImmutableList.copyOf(widgets);
  }

  public ImmutableList<Widget> getWidgetOrdering() {
    return widgets;
  }

  public static Type<WidgetsReorderedHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(WidgetsReorderedHandler handler) {
    handler.onWidgetsReordered(this);
  }

  @Override
  public Type<WidgetsReorderedHandler> getAssociatedType() {
    return TYPE;
  }
}
