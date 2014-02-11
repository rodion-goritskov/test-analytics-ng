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
import java.util.Random;

/**
 * Unit tests the Capability class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilityTest extends TestCase {

  public void testCapabilityFields() {
    Long currentTime = 0 - System.currentTimeMillis();
    Capability capability = new Capability(42L, 1, 2);
    assertEquals(1, capability.getAttributeId());
    assertEquals(2, capability.getComponentId());
    capability.setAttributeId(3);
    capability.setComponentId(4);
    assertEquals(3, capability.getAttributeId());
    assertEquals(4, capability.getComponentId());
    assertEquals(42, capability.getParentProjectId());
    capability.setParentProjectId(43);
    assertEquals(43, capability.getParentProjectId());
    assertEquals(null, capability.getCapabilityId());
    assertEquals(null, capability.getId());
    capability.setCapabilityId(82);
    assertEquals(82, capability.getCapabilityId().longValue());
    assertEquals(82, capability.getId().longValue());
    assertEquals(null, capability.getName());
    capability.setName("my favorite capability");
    assertEquals("my favorite capability", capability.getName());
    assertTrue(capability.getDisplayOrder() <= currentTime);
    capability.setDisplayOrder(5);
    assertEquals(5, capability.getDisplayOrder());
    assertEquals(AccElementType.CAPABILITY, capability.getElementType());
    AccLabel label = new AccLabel();
    label.setLabelText("demo");
    capability.addLabel(label);
    assertEquals(1, capability.getAccLabels().size());
    assertEquals("", capability.getDescription());
    capability.setDescription("hello");
    assertEquals("hello", capability.getDescription());
    capability.setDescription("new hello");
    assertEquals("new hello", capability.getDescription());
    assertEquals("demo", capability.getAccLabels().get(0).getLabelText());
  }

  public void testRetrieveLabelById() {
    Capability capability = new Capability();
    AccLabel label = new AccLabel();
    label.setLabelText("hello");
    capability.addLabel(label);
    label = new AccLabel();
    label.setLabelText("is it me you're looking for");
    label.setId("2");
    capability.addLabel(label);
    label = new AccLabel();
    label.setLabelText("i can see it in your smile");
    label.setId("3");
    capability.addLabel(label);
    label = new AccLabel();
    label.setId("4");
    capability.addLabel(label);

    label = capability.getAccLabel("2");
    assertEquals(label.getLabelText(), "is it me you're looking for");
    label = capability.getAccLabel("my endless love");
    assertEquals(null, label);

    capability.updateLabel("i can see it in your smile", "i can see it in your eyes");
    assertEquals("i can see it in your eyes",
        capability.getAccLabel("3").getLabelText());
  }

  public void testSetAccLabelsAndRemove() {
    AccLabel label = new AccLabel();
    label.setLabelText("one");
    AccLabel label2 = new AccLabel();
    label2.setLabelText("two");
    List<AccLabel> labels = Lists.newArrayList(
        label, label2);
    Capability capability = new Capability();
    capability.setAccLabels(labels);
    assertEquals(2, capability.getAccLabels().size());

    AccLabel delete = new AccLabel();
    delete.setLabelText("two");
    capability.removeLabel(delete);
    assertEquals(1, capability.getAccLabels().size());
    assertEquals("one", capability.getAccLabels().get(0).getLabelText());
    label2.setId("id");
    capability.addLabel(label2);
    assertEquals(2, capability.getAccLabels().size());
    delete.setLabelText(null);
    delete.setId("id");
    capability.removeLabel(delete);
    assertEquals(1, capability.getAccLabels().size());
    assertEquals("one", capability.getAccLabels().get(0).getLabelText());
  }

  public void testLabels() {
    Capability capability = new Capability(0L, 1, 2);
    assertEquals(null, capability.getCapabilityId());

    assertEquals(0, capability.getAccLabels().size());
    capability.addLabel("dev owner");
    assertEquals(1, capability.getAccLabels().size());
    assertEquals("dev owner", capability.getAccLabels().get(0).getLabelText());
  }

  public void testTwoStateLabel() {
    Capability capability = new Capability(42L, 1, 2);
    assertEquals(null, capability.getCapabilityId());
    capability.setCapabilityId(82L);
    assertEquals(0, capability.getAccLabels().size());
    capability.addLabel("One State");
    capability.addLabel("Two State-Hello");
    capability.addLabel("Two State Separate", "Yes");
    assertEquals(3, capability.getAccLabels().size());
    AccLabel label = capability.getAccLabels().get(0);
    assertEquals(82, label.getElementId().longValue());
    assertEquals("One State", label.getLabelText());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(AccElementType.CAPABILITY, label.getElementType());
    assertEquals("Capability", label.getElementType().getFriendlyName());
    label = capability.getAccLabels().get(1);
    assertEquals("Two State-Hello", label.getLabelText());
    assertEquals("Two State", label.getName());
    assertEquals("Hello", label.getValue());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(82, label.getElementId().longValue());
    assertEquals(AccElementType.CAPABILITY, label.getElementType());
    label = capability.getAccLabels().get(2);
    assertEquals("Two State Separate-Yes", label.getLabelText());
    assertEquals("Two State Separate", label.getName());
    assertEquals("Yes", label.getValue());

    capability.removeLabel("doesn't exist");
    assertEquals(3, capability.getAccLabels().size());
    capability.removeLabel("Two State Separate-Yes");
    assertEquals(2, capability.getAccLabels().size());

    capability.removeLabel("doesn't", "exist");
    assertEquals(2, capability.getAccLabels().size());
    capability.removeLabel("Two State", "Hello");
    assertEquals(1, capability.getAccLabels().size());

  }

  public void testEdgeCaseLabels() {
    Capability capability = new Capability(42L, 1, 2);
    assertEquals(null, capability.getCapabilityId());

    assertEquals(0, capability.getAccLabels().size());
    capability.addLabel("");
    capability.addLabel("-");
    capability.addLabel("Two State-");
    assertEquals(3, capability.getAccLabels().size());
    assertEquals("", capability.getAccLabels().get(0).getLabelText());
    assertEquals("-", capability.getAccLabels().get(1).getLabelText());
    assertEquals("Two State-", capability.getAccLabels().get(2).getLabelText());
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

  /**
   * 'IntersectionKey' is a unique integer generated by a Capability's parent Component and
   * Attribute. It is used as an index into lookup tables.
   */
  public void testGetCapabilityIntersectionKey() {
    Random rng = new Random(0);

    // Keep components minimal to verify this is all keyed off of the IDs.
    Component compA = new Component(3L);
    compA.setComponentId(rng.nextLong());
    Component compB = new Component(3L);
    compB.setComponentId(rng.nextLong());

    Attribute attrA = new Attribute(3L);
    attrA.setAttributeId(rng.nextLong());
    Attribute attrB = new Attribute(3L);
    attrB.setAttributeId(rng.nextLong());

    Capability capabilityAA = new Capability(
        3L, attrA.getAttributeId(), compA.getComponentId());
    int keyAA = capabilityAA.getCapabilityIntersectionKey();

    Capability capabilityAB = new Capability(
        3L, attrA.getAttributeId(), compB.getComponentId());
    int keyAB = capabilityAB.getCapabilityIntersectionKey();

    Capability capabilityBA = new Capability(
        3L, attrB.getAttributeId(), compA.getComponentId());
    int keyBA = capabilityBA.getCapabilityIntersectionKey();

    Capability capabilityAA2 = new Capability(
        3L, attrA.getAttributeId(), compA.getComponentId());
    int keyAA2 = capabilityAA2.getCapabilityIntersectionKey();
    int keyAA3 = Capability.getCapabilityIntersectionKey(compA, attrA);

    assertTrue(keyAA != keyBA);
    assertTrue(keyAA != keyAB);
    assertTrue(keyAA != keyBA);

    assertTrue(keyAA == keyAA2);
    assertTrue(keyAA2 == keyAA3);
  }

  public void testCapabilityIntersectionData() {
    Attribute attribute = new Attribute();
    attribute.setAttributeId(52L);
    Component component = new Component();
    component.setComponentId(59L);
    List<Capability> capabilities = Lists.newArrayList(new Capability(), new Capability());
    CapabilityIntersectionData intersect = new CapabilityIntersectionData(attribute, component,
        capabilities);
    assertEquals(attribute, intersect.getParentAttribute());
    assertEquals(component, intersect.getParentComponent());
    assertEquals(2, intersect.getCapabilities().size());
    assertEquals(capabilities, intersect.getCapabilities());
  }
}
