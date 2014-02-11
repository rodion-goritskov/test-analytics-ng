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
import com.google.testing.testify.risk.frontend.client.event.ProjectElementAddedEvent;
import com.google.testing.testify.risk.frontend.client.event.ProjectHasNoElementsEvent;
import com.google.testing.testify.risk.frontend.client.view.AttributesView;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Attribute;
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

/**
 * Unit tests for the AttributePresenter class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class AttributesPresenterTest extends TestCase {

  // Check the handshake between Presenter and View
  @SuppressWarnings("unchecked")
  public void testInitialization() {

    // Test setup
    ProjectRpcAsync mockProjService = EasyMock.createMock(ProjectRpcAsync.class);
    AttributesView mockView = EasyMock.createMock(AttributesView.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    DataRpcAsync dataService = EasyMock.createMock(DataRpcAsync.class);

    ArrayList<Attribute> attributes = Lists.newArrayList();
    ArrayList<Signoff> signoffs = Lists.newArrayList();
    Collection<String> labels = Lists.newArrayList();

    Project parentProject = new Project();
    parentProject.setProjectId(42L);

    // Verify this initialization sequence
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(true);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(attributes);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);

    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockView.setPresenter((AttributesView.Presenter) EasyMock.anyObject());
    mockView.enableEditing();
    mockView.setProjectAttributes(attributes);
    mockView.setSignoffs(signoffs);
    mockView.setProjectLabels(labels);

    EasyMock.replay(mockView, mockProjService, securityService, dataService);

    AttributesPresenter testPresenter = new AttributesPresenter(parentProject, mockProjService,
        securityService, dataService, mockView, new SimpleEventBus());

    EasyMock.verify(mockView, mockProjService, securityService, dataService);
  }

  @SuppressWarnings("unchecked")
  public void testProjectHasNoAttributesEvent() {
    // Test setup
    ProjectRpcAsync mockProjService = EasyMock.createMock(ProjectRpcAsync.class);
    AttributesView mockView = EasyMock.createMock(AttributesView.class);
    SimpleEventBus mockEventBus = EasyMock.createMock(SimpleEventBus.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    DataRpcAsync dataService = EasyMock.createMock(DataRpcAsync.class);

    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    ArrayList<Signoff> signoffs = new ArrayList<Signoff>();
    Collection<String> labels = Lists.newArrayList();

    Project parentProject = new Project();
    parentProject.setProjectId(42L);

    // Test #1 - Verify the ProjectHasNoEvents event get fired.
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(false);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(attributes);
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);

    mockEventBus.fireEvent(EasyMock.isA(ProjectHasNoElementsEvent.class));

    mockView.setPresenter((AttributesView.Presenter) EasyMock.anyObject());
    mockView.setProjectAttributes(attributes);
    mockView.setSignoffs(signoffs);
    mockView.setProjectLabels(labels);

    EasyMock.replay(mockEventBus, mockProjService, mockView, dataService);

    AttributesPresenter testPresenter = new AttributesPresenter(
        parentProject, mockProjService, securityService, dataService, mockView,
        mockEventBus);

    EasyMock.verify(mockEventBus, mockProjService, mockView, dataService);

    // Test #2 - Verify the ProjectHasNoEvents event does not fire.
    EasyMock.reset(mockEventBus, mockProjService, mockView, securityService, dataService);
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(false);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(attributes);
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);
    Attribute att = new Attribute();
    att.setParentProjectId(42L);
    attributes.add(att);

    mockView.setPresenter((AttributesView.Presenter) EasyMock.anyObject());
    mockView.setProjectAttributes(attributes);
    mockView.setSignoffs(signoffs);
    mockView.setProjectLabels(labels);

    EasyMock.replay(mockEventBus, mockProjService, mockView, securityService, dataService);

    AttributesPresenter testPresenter2 = new AttributesPresenter(
        parentProject, mockProjService, securityService, dataService, mockView,
        mockEventBus);

    EasyMock.verify(mockEventBus, mockProjService, mockView, securityService, dataService);
  }

  @SuppressWarnings("unchecked")
  public void testAttributeAddedEventOnInitialization() {
    ProjectRpcAsync mockProjService = EasyMock.createMock(ProjectRpcAsync.class);
    AttributesView mockView = EasyMock.createMock(AttributesView.class);
    // Since HandlerManager is a concrete type, must import 'org.easymock.EasyMock'
    SimpleEventBus mockEventBus = EasyMock.createMock(SimpleEventBus.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    DataRpcAsync dataService = EasyMock.createMock(DataRpcAsync.class);

    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    Attribute att = new Attribute();
    att.setAttributeId(42L);
    attributes.add(att);
    ArrayList<Signoff> signoffs = new ArrayList<Signoff>();
    Collection<String> labels = Lists.newArrayList();

    Project parentProject = new Project();
    parentProject.setProjectId(42L);

    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(true);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(attributes);
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);
    EasyMock.replay(mockEventBus, mockProjService, securityService, dataService);

    AttributesPresenter testPresenter = new AttributesPresenter(
        parentProject, mockProjService, securityService, dataService, mockView,
        mockEventBus);

    EasyMock.verify(mockEventBus, mockProjService, securityService, dataService);
  }

  @SuppressWarnings("unchecked")
  public void testAttributeAddedEvent() {

    ProjectRpcAsync mockProjService = EasyMock.createMock(ProjectRpcAsync.class);
    AttributesView mockView = EasyMock.createMock(AttributesView.class);
    // Since HandlerManager is a concrete type, must import 'org.easymock.EasyMock'
    SimpleEventBus mockEventBus = EasyMock.createMock(SimpleEventBus.class);
    UserRpcAsync securityService = EasyMock.createMock(UserRpcAsync.class);
    DataRpcAsync dataService = EasyMock.createMock(DataRpcAsync.class);
    ArrayList<Signoff> signoffs = new ArrayList<Signoff>();
    Collection<String> labels = Lists.newArrayList();

    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    Attribute att = new Attribute();
    att.setParentProjectId(42L);
    attributes.add(att);

    Project parentProject = new Project();
    parentProject.setProjectId(42L);

    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(false);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(attributes);
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);

    AttributesPresenter testPresenter = new AttributesPresenter(
      parentProject, mockProjService, securityService, dataService, mockView,
      mockEventBus);

    EasyMock.reset(mockEventBus, mockProjService, securityService, dataService);

    Attribute newAttribute = new Attribute();
    newAttribute.setParentProjectId(42L);
    mockProjService.createAttribute(EasyMock.eq(newAttribute), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(1L);

    mockEventBus.fireEvent(EasyMock.isA(ProjectElementAddedEvent.class));

    // Attributes queried due to the view refresh.
    securityService.hasEditAccess(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(false);
    mockProjService.getProjectAttributes(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    dataService.getSignoffsByType(EasyMock.eq(42L), EasyMock.eq(AccElementType.ATTRIBUTE),
        EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(signoffs);
    mockProjService.getLabels(EasyMock.eq(42L), EasyMock.isA(AsyncCallback.class));
    EasyMockUtils.setLastAsyncCallbackSuccessWithResult(labels);

    EasyMock.replay(mockEventBus, mockProjService, securityService, dataService);

    testPresenter.createAttribute(newAttribute);

    EasyMock.verify(mockEventBus, mockProjService, securityService, dataService);
  }
}
