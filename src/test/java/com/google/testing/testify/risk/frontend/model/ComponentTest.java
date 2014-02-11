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


package com.google.testing.testify.risk.frontend.model;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

import java.util.List;

/**
 * Unit tests for the Component class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ComponentTest extends TestCase {

  public void testComponentFields() {
    Long currentTime = 0 - System.currentTimeMillis();
    Component component = new Component(42L);
    assertEquals(42, component.getParentProjectId());
    component.setParentProjectId(43);
    assertEquals(43, component.getParentProjectId());
    assertEquals(null, component.getComponentId());
    assertEquals(null, component.getId());
    component.setComponentId(82);
    assertEquals(82, component.getComponentId().longValue());
    assertEquals(82, component.getId().longValue());
    assertEquals(null, component.getName());
    component.setName("my favorite component");
    assertEquals("my favorite component", component.getName());
    assertTrue(component.getDisplayOrder() <= currentTime);
    component.setDisplayOrder(5);
    assertEquals(5, component.getDisplayOrder());
    assertEquals(AccElementType.COMPONENT, component.getElementType());
    AccLabel label = new AccLabel();
    label.setLabelText("demo");
    component.addLabel(label);
    assertEquals(1, component.getAccLabels().size());
    assertEquals("", component.getDescription());
    component.setDescription("hello");
    assertEquals("hello", component.getDescription());
    component.setDescription("new hello");
    assertEquals("new hello", component.getDescription());
    assertEquals("demo", component.getAccLabels().get(0).getLabelText());
  }

  public void testRetrieveLabelById() {
    Component component = new Component();
    AccLabel label = new AccLabel();
    label.setLabelText("hello");
    component.addLabel(label);
    label = new AccLabel();
    label.setLabelText("is it me you're looking for");
    label.setId("2");
    component.addLabel(label);
    label = new AccLabel();
    label.setLabelText("i can see it in your smile");
    label.setId("3");
    component.addLabel(label);
    label = new AccLabel();
    label.setId("4");
    component.addLabel(label);

    label = component.getAccLabel("2");
    assertEquals(label.getLabelText(), "is it me you're looking for");
    label = component.getAccLabel("my endless love");
    assertEquals(null, label);

    component.updateLabel("i can see it in your smile", "i can see it in your eyes");
    assertEquals("i can see it in your eyes",
        component.getAccLabel("3").getLabelText());
  }

  public void testSetAccLabelsAndRemove() {
    AccLabel label = new AccLabel();
    label.setLabelText("one");
    AccLabel label2 = new AccLabel();
    label2.setLabelText("two");
    List<AccLabel> labels = Lists.newArrayList(
        label, label2);
    Component component = new Component();
    component.setAccLabels(labels);
    assertEquals(2, component.getAccLabels().size());

    AccLabel delete = new AccLabel();
    delete.setLabelText("two");
    component.removeLabel(delete);
    assertEquals(1, component.getAccLabels().size());
    assertEquals("one", component.getAccLabels().get(0).getLabelText());
    label2.setId("id");
    component.addLabel(label2);
    assertEquals(2, component.getAccLabels().size());
    delete.setLabelText(null);
    delete.setId("id");
    component.removeLabel(delete);
    assertEquals(1, component.getAccLabels().size());
    assertEquals("one", component.getAccLabels().get(0).getLabelText());
  }

  public void testLabels() {
    Component component = new Component(0L);
    assertEquals(null, component.getComponentId());

    assertEquals(0, component.getAccLabels().size());
    component.addLabel("dev owner");
    assertEquals(1, component.getAccLabels().size());
    assertEquals("dev owner", component.getAccLabels().get(0).getLabelText());
  }

  public void testTwoStateLabel() {
    Component component = new Component(42L);
    assertEquals(null, component.getComponentId());
    component.setComponentId(82L);
    assertEquals(0, component.getAccLabels().size());
    component.addLabel("One State");
    component.addLabel("Two State-Hello");
    component.addLabel("Two State Separate", "Yes");
    assertEquals(3, component.getAccLabels().size());
    AccLabel label = component.getAccLabels().get(0);
    assertEquals(82, label.getElementId().longValue());
    assertEquals("One State", label.getLabelText());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(AccElementType.COMPONENT, label.getElementType());
    assertEquals("Component", label.getElementType().getFriendlyName());
    label = component.getAccLabels().get(1);
    assertEquals("Two State-Hello", label.getLabelText());
    assertEquals("Two State", label.getName());
    assertEquals("Hello", label.getValue());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(82, label.getElementId().longValue());
    assertEquals(AccElementType.COMPONENT, label.getElementType());
    label = component.getAccLabels().get(2);
    assertEquals("Two State Separate-Yes", label.getLabelText());
    assertEquals("Two State Separate", label.getName());
    assertEquals("Yes", label.getValue());

    component.removeLabel("doesn't exist");
    assertEquals(3, component.getAccLabels().size());
    component.removeLabel("Two State Separate-Yes");
    assertEquals(2, component.getAccLabels().size());

    component.removeLabel("doesn't", "exist");
    assertEquals(2, component.getAccLabels().size());
    component.removeLabel("Two State", "Hello");
    assertEquals(1, component.getAccLabels().size());

  }

  public void testEdgeCaseLabels() {
    Component component = new Component(42L);
    assertEquals(null, component.getComponentId());

    assertEquals(0, component.getAccLabels().size());
    component.addLabel("");
    component.addLabel("-");
    component.addLabel("Two State-");
    assertEquals(3, component.getAccLabels().size());
    assertEquals("", component.getAccLabels().get(0).getLabelText());
    assertEquals("-", component.getAccLabels().get(1).getLabelText());
    assertEquals("Two State-", component.getAccLabels().get(2).getLabelText());
  }

  public void testLabelSetNameValueAndNullText() {
    AccLabel label = new AccLabel();
    label.setId("id");
    label.setName("name");
    label.setValue("value");
    assertEquals("id", label.getId());
    assertEquals("name", label.getName());
    assertEquals("value", label.getValue());
    label.setLabelText(null);
    assertEquals("id", label.getId());
    assertEquals(null, label.getName());
    assertEquals(null, label.getValue());
  }
}
