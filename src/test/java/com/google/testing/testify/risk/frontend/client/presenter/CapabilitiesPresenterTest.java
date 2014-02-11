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

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.testing.testify.risk.frontend.client.view.CapabilitiesView;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;
import com.google.testing.testify.risk.frontend.testing.EasyMockUtils;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Unit tests for the CapabilitiesPresenter type.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CapabilitiesPresenterTest extends TestCase {

  // Unit test the handshake between View and Presenter.
  @SuppressWarnings("unchecked")
  public void testInitialization() {
    CapabilitiesView mockView = EasyMock.createMock(CapabilitiesView.class);
    ProjectRpcAsync projectService = EasyMock.createMock(ProjectRpcAsync.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    SimpleEventBus mockEventBus = EasyMock.createMock(SimpleEventBus.class);
    Project testProject = new Project();
    testProject.setProjectId(42L);

    ArrayList<Component> components = Lists.newArrayList();
    ArrayList<Attribute> attributes = Lists.newArrayList();
    ArrayList<Capability> capabilities = Lists.newArrayList();
    Collection<String> labels = Lists.newArrayList();
    
    // Verify the initialization steps, ensure the async callbacks return the expected collections.
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(true);

    projectService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(capabilities);

    projectService.getProjectComponents(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(components);

    projectService.getProjectCapabilities(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(capabilities);

    projectService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);

    mockView.setEditable(true);
    mockView.setPresenter(EasyMock.isA(CapabilitiesPresenter.class));
    mockView.setComponents(components);
    mockView.setAttributes(attributes);
    mockView.setCapabilities(capabilities);
    mockView.setProjectLabels(labels);

    EasyMock.replay(mockView, projectService, securityService);

    CapabilitiesPresenter testPresenter = null;
    testPresenter = new CapabilitiesPresenter(testProject, projectService, securityService,
        mockView, mockEventBus);

    EasyMock.verify(mockView, projectService, securityService);
  }
}
