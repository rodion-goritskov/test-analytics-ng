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

import com.google.gwt.event.shared.GwtEvent;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;

/**
 * Event type fired when the current project adds a new project element. (Attribute,
 * Component, or Capability.). Note that this event is used whenever _any_ project elements of any
 * type are created. Consumers will need to call the isXXXAddedEvent() methods and filter
 * appropriately.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectElementAddedEvent extends GwtEvent<ProjectElementAddedHandler> {
  private static final Type<ProjectElementAddedHandler> TYPE =
    new Type<ProjectElementAddedHandler>();

  private final Attribute attribute;
  private final Component component;
  private final Capability capability;

  public ProjectElementAddedEvent(Attribute attribute) {
    this.attribute = attribute;
    this.component = null;
    this.capability = null;
  }

  public ProjectElementAddedEvent(Component component) {
    this.attribute = null;
    this.component = component;
    this.capability = null;
  }

  public ProjectElementAddedEvent(Capability capability) {
    this.attribute = null;
    this.component = null;
    this.capability = capability;
  }

  /** Returns whether or not a new Attribute is associated with this event. */
  public boolean isAttributeAddedEvent() {
    return attribute != null;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  /** Returns whether or not a new Component is associated with this event. */
  public boolean isComponentAddedEvent() {
    return component != null;
  }

  public Component getComponent() {
    return component;
  }

  /** Returns whether or not a new Capability is associated with this event. */
  public boolean isCapabilityAddedEvent() {
    return capability != null;
  }

  public Capability getCapability() {
    return capability;
  }

  public static Type<ProjectElementAddedHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ProjectElementAddedHandler handler) {
    handler.onProjectElementAdded(this);
  }

  @Override
  public Type<ProjectElementAddedHandler> getAssociatedType() {
    return TYPE;
  }
}
