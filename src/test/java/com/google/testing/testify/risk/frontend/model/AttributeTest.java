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
 * Unit tests the Attribute class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class AttributeTest extends TestCase {

  public void testAttributeFields() {
    Long currentTime = 0 - System.currentTimeMillis();
    Attribute attribute = new Attribute(42L);
    assertEquals(42, attribute.getParentProjectId());
    attribute.setParentProjectId(43);
    assertEquals(43, attribute.getParentProjectId());
    assertEquals(null, attribute.getAttributeId());
    assertEquals(null, attribute.getId());
    attribute.setAttributeId(82);
    assertEquals(82, attribute.getAttributeId().longValue());
    assertEquals(82, attribute.getId().longValue());
    assertEquals(null, attribute.getName());
    attribute.setName("my favorite Attribute");
    assertEquals("my favorite Attribute", attribute.getName());
    assertTrue(attribute.getDisplayOrder() <= currentTime);
    attribute.setDisplayOrder(5);
    assertEquals(5, attribute.getDisplayOrder());
    assertEquals(AccElementType.ATTRIBUTE, attribute.getElementType());
    AccLabel label = new AccLabel();
    label.setLabelText("demo");
    attribute.addLabel(label);
    assertEquals(1, attribute.getAccLabels().size());
    assertEquals("", attribute.getDescription());
    attribute.setDescription("hello");
    assertEquals("hello", attribute.getDescription());
    attribute.setDescription("new hello");
    assertEquals("new hello", attribute.getDescription());
    assertEquals("demo", attribute.getAccLabels().get(0).getLabelText());
  }

  public void testRetrieveLabelById() {
    Attribute attribute = new Attribute();
    AccLabel label = new AccLabel();
    label.setLabelText("hello");
    attribute.addLabel(label);
    label = new AccLabel();
    label.setLabelText("is it me you're looking for");
    label.setId("2");
    attribute.addLabel(label);
    label = new AccLabel();
    label.setLabelText("i can see it in your smile");
    label.setId("3");
    attribute.addLabel(label);
    label = new AccLabel();
    label.setId("4");
    attribute.addLabel(label);

    label = attribute.getAccLabel("2");
    assertEquals(label.getLabelText(), "is it me you're looking for");
    label = attribute.getAccLabel("my endless love");
    assertEquals(null, label);

    attribute.updateLabel("i can see it in your smile", "i can see it in your eyes");
    assertEquals("i can see it in your eyes",
        attribute.getAccLabel("3").getLabelText());
  }

  public void testSetAccLabelsAndRemove() {
    AccLabel label = new AccLabel();
    label.setLabelText("one");
    AccLabel label2 = new AccLabel();
    label2.setLabelText("two");
    List<AccLabel> labels = Lists.newArrayList(
        label, label2);
    Attribute attribute = new Attribute();
    attribute.setAccLabels(labels);
    assertEquals(2, attribute.getAccLabels().size());

    AccLabel delete = new AccLabel();
    delete.setLabelText("two");
    attribute.removeLabel(delete);
    assertEquals(1, attribute.getAccLabels().size());
    assertEquals("one", attribute.getAccLabels().get(0).getLabelText());
    label2.setId("id");
    attribute.addLabel(label2);
    assertEquals(2, attribute.getAccLabels().size());
    delete.setLabelText(null);
    delete.setId("id");
    attribute.removeLabel(delete);
    assertEquals(1, attribute.getAccLabels().size());
    assertEquals("one", attribute.getAccLabels().get(0).getLabelText());
  }

  public void testLabels() {
    Attribute attribute = new Attribute(0L);
    assertEquals(null, attribute.getAttributeId());

    assertEquals(0, attribute.getAccLabels().size());
    attribute.addLabel("dev owner");
    assertEquals(1, attribute.getAccLabels().size());
    assertEquals("dev owner", attribute.getAccLabels().get(0).getLabelText());
  }

  public void testTwoStateLabel() {
    Attribute attribute = new Attribute(42L);
    assertEquals(null, attribute.getAttributeId());
    attribute.setAttributeId(82L);
    assertEquals(0, attribute.getAccLabels().size());
    attribute.addLabel("One State");
    attribute.addLabel("Two State-Hello");
    attribute.addLabel("Two State Separate", "Yes");
    assertEquals(3, attribute.getAccLabels().size());
    AccLabel label = attribute.getAccLabels().get(0);
    assertEquals(82, label.getElementId().longValue());
    assertEquals("One State", label.getLabelText());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(AccElementType.ATTRIBUTE, label.getElementType());
    assertEquals("Attribute", label.getElementType().getFriendlyName());
    label = attribute.getAccLabels().get(1);
    assertEquals("Two State-Hello", label.getLabelText());
    assertEquals("Two State", label.getName());
    assertEquals("Hello", label.getValue());
    assertEquals(42, label.getProjectId().longValue());
    assertEquals(82, label.getElementId().longValue());
    assertEquals(AccElementType.ATTRIBUTE, label.getElementType());
    label = attribute.getAccLabels().get(2);
    assertEquals("Two State Separate-Yes", label.getLabelText());
    assertEquals("Two State Separate", label.getName());
    assertEquals("Yes", label.getValue());

    attribute.removeLabel("doesn't exist");
    assertEquals(3, attribute.getAccLabels().size());
    attribute.removeLabel("Two State Separate-Yes");
    assertEquals(2, attribute.getAccLabels().size());

    attribute.removeLabel("doesn't", "exist");
    assertEquals(2, attribute.getAccLabels().size());
    attribute.removeLabel("Two State", "Hello");
    assertEquals(1, attribute.getAccLabels().size());

  }

  public void testEdgeCaseLabels() {
    Attribute attribute = new Attribute(42L);
    assertEquals(null, attribute.getAttributeId());

    assertEquals(0, attribute.getAccLabels().size());
    attribute.addLabel("");
    attribute.addLabel("-");
    attribute.addLabel("Two State-");
    assertEquals(3, attribute.getAccLabels().size());
    assertEquals("", attribute.getAccLabels().get(0).getLabelText());
    assertEquals("-", attribute.getAccLabels().get(1).getLabelText());
    assertEquals("Two State-", attribute.getAccLabels().get(2).getLabelText());
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
