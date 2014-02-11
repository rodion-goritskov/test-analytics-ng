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

import com.google.common.collect.Sets;

import junit.framework.TestCase;

/**
 * Unit tests for the Bug class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class BugTest extends TestCase {

  public void testBugFields() {
    Bug testBug = new Bug();
    assertEquals(null, testBug.getParentProjectId());
    testBug.setParentProjectId(82L);
    assertEquals(82, testBug.getParentProjectId().longValue());
    assertEquals(0, testBug.getBugGroups().size());
    testBug.addBugGroup("group");
    assertEquals(1, testBug.getBugGroups().size());
    testBug.addBugGroup("group2");
    assertEquals(2, testBug.getBugGroups().size());
    assertEquals(null, testBug.getBugUrl());
    assertEquals(null, testBug.getLinkUrl());
    testBug.setBugUrl("http://example/");
    assertEquals("http://example/", testBug.getBugUrl());
    assertEquals("http://example/", testBug.getLinkUrl());
    assertEquals(DatumType.BUGS, testBug.getDatumType());
    assertEquals("Bugs", testBug.getDatumType().getPlural());
    assertEquals("Bug", testBug.getDatumType().getSingular());
    assertTrue(testBug.getDatumType().getFilterTypes().contains("Title"));
    assertTrue(testBug.getDatumType().getFilterTypes().contains("Path"));
    assertTrue(testBug.getDatumType().getFilterTypes().contains("Labels"));
    assertEquals(null, testBug.getExternalId());
    testBug.setExternalId(123L);
    assertEquals(123, testBug.getExternalId().longValue());
    assertEquals(null, testBug.getInternalId());
    testBug.setInternalId(456L);
    assertEquals(456, testBug.getInternalId().longValue());
    assertTrue(testBug.getToolTip().contains("bug is attached"));
    assertTrue(testBug.getToolTip().contains("group2"));
    assertEquals(null, testBug.getTitle());
    assertEquals(null, testBug.getLinkText());
    testBug.setTitle("title");
    assertEquals("title", testBug.getTitle());
    assertEquals("title", testBug.getLinkText());
    assertEquals(null, testBug.getSeverity());
    testBug.setSeverity(5L);
    assertEquals(5, testBug.getSeverity().longValue());
    assertEquals(null, testBug.getPriority());
    testBug.setPriority(4L);
    assertEquals(4, testBug.getPriority().longValue());
    assertEquals(null, testBug.getPath());
    testBug.setPath("project - sub project");
    assertEquals("project - sub project", testBug.getPath());
    assertEquals(null, testBug.getTargetAttributeId());
    assertEquals(null, testBug.getTargetComponentId());
    assertEquals(null, testBug.getTargetCapabilityId());
    assertFalse(testBug.isAttachedToAttribute());
    assertFalse(testBug.isAttachedToComponent());
    assertFalse(testBug.isAttachedToCapability());
    testBug.setTargetAttributeId(867L);
    testBug.setTargetComponentId(5309L);
    testBug.setTargetCapabilityId(555L);
    assertEquals(867, testBug.getTargetAttributeId().longValue());
    assertEquals(5309, testBug.getTargetComponentId().longValue());
    assertEquals(555, testBug.getTargetCapabilityId().longValue());
    assertTrue(testBug.isAttachedToAttribute());
    assertTrue(testBug.isAttachedToComponent());
    assertTrue(testBug.isAttachedToCapability());
    assertEquals(null, testBug.getState());
    assertEquals(null, testBug.getStateDate());
    testBug.setState("Open");
    testBug.setStateDate(123123123L);
    assertEquals("Open", testBug.getState());
    assertEquals(123123123, testBug.getStateDate().longValue());
  }

  public void testGroupRemoveAndSet() {
    Bug bug = new Bug();
    bug.addBugGroup("group1");
    bug.addBugGroup("group2");
    bug.addBugGroup("group3");
    assertEquals(3, bug.getBugGroups().size());
    bug.removeBugGroup("group2");
    assertEquals(2, bug.getBugGroups().size());
    assertTrue(!bug.getGroupsAsCommaSeparatedList().contains("group2"));

    bug.setBugGroups(Sets.newHashSet("new1", "new2", "new3"));
    assertTrue(!bug.getGroupsAsCommaSeparatedList().contains("group1"));
    assertTrue(!bug.getGroupsAsCommaSeparatedList().contains("group3"));
    assertTrue(bug.getGroupsAsCommaSeparatedList().contains("new1"));
    assertTrue(bug.getGroupsAsCommaSeparatedList().contains("new2"));
    assertTrue(bug.getGroupsAsCommaSeparatedList().contains("new3"));
    assertEquals(3, bug.getBugGroups().size());
  }

  public void testGetField() {
    Bug testBug = new Bug();
    testBug.setTitle("my title");
    testBug.setPath("my path");
    testBug.addBugGroup("my label1");
    testBug.addBugGroup("my label2");
    assertEquals("my title", testBug.getField("Title"));
    assertEquals("my path", testBug.getField("Path"));
    assertTrue(testBug.getField("Labels").contains("my label1"));
    assertTrue(testBug.getField("Labels").contains("my label2"));
    assertEquals(null, testBug.getField("nx field"));
  }

  public void testGroupsAsCommaSeparatedList() {
    Bug testBug = new Bug();
    testBug.addBugGroup("alpha");
    testBug.addBugGroup("beta");
    testBug.addBugGroup("gamma");

    // The order in which items are retrieved from a set is unpredictable, so we just look
    // for items individually.
    String groupList = testBug.getGroupsAsCommaSeparatedList();
    assertTrue(groupList.contains("alpha"));
    assertTrue(groupList.contains("beta"));
    assertTrue(groupList.contains("gamma"));
    assertEquals(3, groupList.split(",").length);
  }
}
