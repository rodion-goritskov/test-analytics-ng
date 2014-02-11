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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.testing.testify.risk.frontend.client.view.ComponentView;
import com.google.testing.testify.risk.frontend.client.view.ComponentView.Presenter;
import com.google.testing.testify.risk.frontend.client.view.ComponentsView;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.List;

/**
 * Unit tests for the ComponentPresenter class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentPresenterTest extends TestCase {

  // Check the handshake between Presenter and View
  @SuppressWarnings("unchecked")
  public void testInitialization() {
    // Test setup
    ProjectRpcAsync projServiceObserver = EasyMock.createMock(ProjectRpcAsync.class);
    ProjectSettingsView viewObserver = EasyMock.createMock(ProjectSettingsView.class);
    ComponentsView.Presenter mockParent = EasyMock.createMock(ComponentsView.Presenter.class);

    ComponentView mockComponentView = EasyMock.createMock(ComponentView.class);
    Component targetComponent = new Component(0L);
    targetComponent.setName("Target Component");
    targetComponent.setDescription("Test");

    // Verify this initialization sequence
    mockComponentView.setPresenter(EasyMock.isA(Presenter.class));
    mockComponentView.setComponentName(targetComponent.getName());
    mockComponentView.setDescription("Test");
    mockComponentView.setComponentLabels(EasyMock.isA(List.class));

    EasyMock.replay(mockComponentView, mockParent);

    ComponentPresenter testPresenter = new ComponentPresenter(
        targetComponent, mockComponentView, mockParent);

    EasyMock.verify(mockComponentView, mockParent);
  }
}
