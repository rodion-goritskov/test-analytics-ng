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
 * Unit tests for the Checkin class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CheckinTest extends TestCase {

  public void testCheckinFields() {
    Checkin testCheckin = new Checkin();
    assertEquals(null, testCheckin.getParentProjectId());
    testCheckin.setParentProjectId(82L);
    assertEquals(82, testCheckin.getParentProjectId().longValue());
    assertEquals(0, testCheckin.getDirectoriesTouched().size());
    testCheckin.addDirectoryTouched("group");
    assertEquals(1, testCheckin.getDirectoriesTouched().size());
    testCheckin.addDirectoryTouched("group2");
    assertEquals(2, testCheckin.getDirectoriesTouched().size());
    assertEquals(null, testCheckin.getChangeUrl());
    assertEquals(null, testCheckin.getLinkUrl());
    testCheckin.setChangeUrl("http://example/");
    assertEquals("http://example/", testCheckin.getChangeUrl());
    assertEquals("http://example/", testCheckin.getLinkUrl());
    assertEquals(DatumType.CHECKINS, testCheckin.getDatumType());
    assertEquals(null, testCheckin.getExternalId());
    assertEquals("Checkin (no id)", testCheckin.getLinkText());
    testCheckin.setExternalId(123L);
    assertEquals("Checkin #123", testCheckin.getLinkText());
    assertEquals(123, testCheckin.getExternalId().longValue());
    assertEquals(null, testCheckin.getInternalId());
    testCheckin.setInternalId(456L);
    assertEquals(456, testCheckin.getInternalId().longValue());
    assertTrue(testCheckin.getToolTip().contains("directories were touched"));
    assertTrue(testCheckin.getToolTip().contains("group2"));
    assertEquals(null, testCheckin.getSummary());
    testCheckin.setSummary("title");
    assertEquals("title", testCheckin.getSummary());
    assertEquals(null, testCheckin.getTargetAttributeId());
    assertEquals(null, testCheckin.getTargetComponentId());
    assertEquals(null, testCheckin.getTargetCapabilityId());
    assertFalse(testCheckin.isAttachedToAttribute());
    assertFalse(testCheckin.isAttachedToComponent());
    assertFalse(testCheckin.isAttachedToCapability());
    testCheckin.setTargetAttributeId(867L);
    testCheckin.setTargetComponentId(5309L);
    testCheckin.setTargetCapabilityId(555L);
    assertEquals(867, testCheckin.getTargetAttributeId().longValue());
    assertEquals(5309, testCheckin.getTargetComponentId().longValue());
    assertEquals(555, testCheckin.getTargetCapabilityId().longValue());
    assertTrue(testCheckin.isAttachedToAttribute());
    assertTrue(testCheckin.isAttachedToComponent());
    assertTrue(testCheckin.isAttachedToCapability());
    assertEquals(null, testCheckin.getState());
    assertEquals(null, testCheckin.getStateDate());
    testCheckin.setState("Open");
    testCheckin.setStateDate(123123123L);
    assertEquals("Open", testCheckin.getState());
    assertEquals(123123123, testCheckin.getStateDate().longValue());
  }

  public void testDirectoryRemoveAndSet() {
    Checkin checkin = new Checkin();
    checkin.addDirectoryTouched("group1");
    checkin.addDirectoryTouched("group2");
    checkin.addDirectoryTouched("group3");
    assertEquals(3, checkin.getDirectoriesTouched().size());
    checkin.removeDirectoryTouched("group2");
    assertEquals(2, checkin.getDirectoriesTouched().size());
    assertTrue(!checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("group2"));

    checkin.setDirectoriesTouched(Sets.newHashSet("new1", "new2", "new3"));
    assertTrue(!checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("group1"));
    assertTrue(!checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("group3"));
    assertTrue(checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("new1"));
    assertTrue(checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("new2"));
    assertTrue(checkin.getDirectoriesTouchedAsCommaSeparatedList().contains("new3"));
    assertEquals(3, checkin.getDirectoriesTouched().size());
  }

  public void testGetField() {
    Checkin testCheckin = new Checkin();
    testCheckin.setSummary("my summary");
    testCheckin.addDirectoryTouched("my label1");
    testCheckin.addDirectoryTouched("my label2");
    assertEquals("my summary", testCheckin.getField("Summary"));
    assertTrue(testCheckin.getField("Directories").contains("my label1"));
    assertTrue(testCheckin.getField("Directories").contains("my label2"));
    assertEquals(null, testCheckin.getField("nx field"));
  }

  public void testGroupsAsCommaSeparatedList() {
    Checkin testCheckin = new Checkin();
    testCheckin.addDirectoryTouched("alpha");
    testCheckin.addDirectoryTouched("beta");
    testCheckin.addDirectoryTouched("gamma");

    // The order in which items are retrieved from a set is unpredictable, so we just look
    // for items individually.
    String groupList = testCheckin.getDirectoriesTouchedAsCommaSeparatedList();
    assertTrue(groupList.contains("alpha"));
    assertTrue(groupList.contains("beta"));
    assertTrue(groupList.contains("gamma"));
    assertEquals(3, groupList.split(",").length);
  }
}
