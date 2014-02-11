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
import com.google.testing.testify.risk.frontend.client.view.ComponentsView;
import com.google.testing.testify.risk.frontend.client.view.ComponentsView.Presenter;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;
import com.google.testing.testify.risk.frontend.testing.EasyMockUtils;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Unit tests for the ComponentsPresenter class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ComponentsPresenterTest extends TestCase {

  // Check the handshake between Presenter and View
  @SuppressWarnings("unchecked")
  public void testInitialization() {

    // Test setup
    ProjectRpcAsync mockProjService = EasyMock.createMock(ProjectRpcAsync.class);
    ComponentsView mockView = EasyMock.createMock(ComponentsView.class);
    SimpleEventBus mockEventBus = EasyMock.createMock(SimpleEventBus.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    DataRpcAsync dataService = EasyMock.createMock(DataRpcAsync.class);

    List<Component> components = new ArrayList<Component>();
    List<Signoff> signoffs = new ArrayList<Signoff>();
    Collection<String> labels = Lists.newArrayList();

    Project parentProject = new Project();
    parentProject.setProjectId(42L);

    // Verify this initialization sequence
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(true);
    mockProjService.getProjectComponents(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(components);
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.COMPONENT),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);
    mockView.enableEditing();
    mockView.setPresenter((Presenter) EasyMock.anyObject());
    mockView.setProjectComponents(components);
    mockView.setSignoffs(signoffs);
    mockView.setProjectLabels(labels);
    EasyMock.replay(mockView, mockProjService, securityService, dataService);

    ComponentsPresenter testPresenter = new ComponentsPresenter(
        parentProject, mockProjService, securityService, dataService, mockView,
        mockEventBus);

    EasyMock.verify(mockView, mockProjService, securityService, dataService);
  }
}
