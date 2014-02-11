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
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Project;

/**
 * Event type fired when the current project does not contain any elements of a specific
 * type. Note that this element is reused whenever the project is out of _any_ element type, so
 * consumers will have to filter appropriately by calling the projectHasNoXXX() method.
 * appropriately.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectHasNoElementsEvent extends GwtEvent<ProjectHasNoElementsHandler> {
  private static final Type<ProjectHasNoElementsHandler> TYPE =
      new Type<ProjectHasNoElementsHandler>();

  // Default to false, implying the project HAS elements of the given types.
  private final AccElementType accElementType;
  private final Project project;

  public ProjectHasNoElementsEvent(Project project, AccElementType accElementType) {
    this.project = project;
    this.accElementType = accElementType;
  }

  public Project getProject() {
    return project;
  }

  public boolean projectHasNoAttributes() {
    return accElementType.equals(AccElementType.ATTRIBUTE);
  }

  public boolean projectHasNoComponents() {
    return accElementType.equals(AccElementType.COMPONENT);
  }

  public boolean projectHasNoCapabilities() {
    return accElementType.equals(AccElementType.CAPABILITY);
  }

  public static Type<ProjectHasNoElementsHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ProjectHasNoElementsHandler handler) {
    handler.onProjectHasNoElements(this);
  }

  @Override
  public Type<ProjectHasNoElementsHandler> getAssociatedType() {
    return TYPE;
  }
}
