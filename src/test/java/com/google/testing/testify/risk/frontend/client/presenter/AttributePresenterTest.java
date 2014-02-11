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

import com.google.testing.testify.risk.frontend.client.view.AttributeView;
import com.google.testing.testify.risk.frontend.client.view.AttributeView.Presenter;
import com.google.testing.testify.risk.frontend.client.view.AttributesView;

import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.ArrayList;

/**
 * Unit tests for the AttributePresenter class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class AttributePresenterTest extends TestCase {

  // Check the handshake between Presenter and View
  @SuppressWarnings("unchecked")
  public void testInitialization() {

    // Test setup
    ProjectRpcAsync projServiceObserver = EasyMock.createMock(ProjectRpcAsync.class);
    AttributesView.Presenter mockParent = EasyMock.createMock(AttributesView.Presenter.class);

    AttributeView mockAttributeView = EasyMock.createMock(AttributeView.class);
    Attribute targetAttribute = new Attribute();
    targetAttribute.setParentProjectId(0L);
    targetAttribute.setAttributeId(1234L);
    targetAttribute.setDescription("Test");

    // Verify this initialization sequence
    mockAttributeView.setPresenter((Presenter) EasyMock.anyObject());
    mockAttributeView.setAttributeName(targetAttribute.getName());
    mockAttributeView.setDescription("Test");
    mockAttributeView.setAttributeId(1234L);
    mockAttributeView.setLabels(new ArrayList<AccLabel>());

    EasyMock.replay(mockAttributeView, mockParent);

    AttributePresenter testPresenter = new AttributePresenter(
        targetAttribute, mockAttributeView, mockParent);

    EasyMock.verify(mockAttributeView, mockParent);
  }
}
