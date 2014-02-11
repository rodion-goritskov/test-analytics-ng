// Copyright 2011 Google Inc. All Rights Reseved.
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

/**
 * Tests for Filter and FilterOption.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class FilterTest extends TestCase {
  public void testFilterFields() {
    Filter filter = new Filter();
    filter.setFilterType(DatumType.BUGS);
    filter.setId(123L);
    filter.setFilterConjunction("and");
    FilterOption option = new FilterOption();
    option.setId("fo1");
    option.setType("Title");
    option.setValue("search");
    option.setFilter(filter);
    filter.setFilterOptions(Lists.newArrayList(option));
    filter.setParentProjectId(86L);

    assertEquals(null, filter.getTargetAttributeId());
    assertEquals(null, filter.getTargetCapabilityId());
    assertEquals(null, filter.getTargetComponentId());
    filter.setTargetAttributeId(54L);
    filter.setTargetCapabilityId(99L);
    filter.setTargetComponentId(23L);

    assertEquals("Bug Filter", filter.getTitle());
    assertEquals(DatumType.BUGS, filter.getFilterType());
    assertEquals(123L, filter.getId().longValue());
    assertEquals("and", filter.getFilterConjunction());
    assertEquals(1, filter.getFilterOptions().size());
    option = filter.getFilterOptions().get(0);
    assertEquals("fo1", option.getId());
    assertEquals("Title", option.getType());
    assertEquals("search", option.getValue());
    assertEquals(filter, option.getFilter());
    assertEquals(86L, filter.getParentProjectId());
    assertEquals(54L, filter.getTargetAttributeId().longValue());
    assertEquals(99L, filter.getTargetCapabilityId().longValue());
    assertEquals(23L, filter.getTargetComponentId().longValue());

    filter.addFilterOption("Title", "title search");
    assertEquals(2, filter.getFilterOptions().size());
  }

  private Filter makeFilter() {
    Filter filter = new Filter();
    filter.setFilterType(DatumType.BUGS);
    return filter;
  }

  public void testFilter_mismatch() {
    Filter filter = makeFilter();

    Checkin checkin = new Checkin();
    try {
      filter.apply(checkin);
      fail("didn't throw with mismatched filter");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("do not match"));
    }
  }

  public void testFilter_noOptions() {
    Filter filter = makeFilter();
    filter.setFilterConjunction("any");
    filter.setTargetAttributeId(123L);
    Bug bug = new Bug();
    filter.apply(bug);
    assertEquals(null, bug.getTargetAttributeId());
  }

  public void testAndFilter_matches() {
    Filter filter = makeFilter();
    filter.setFilterConjunction("and");
    filter.addFilterOption("Title", "security");
    filter.addFilterOption("Labels", "sec");
    Bug bug = new Bug();
    bug.setTitle("Error with security settings in IE6");
    bug.addBugGroup("dorp");
    bug.addBugGroup("sec");
    bug.addBugGroup("monday");
    filter.setTargetAttributeId(123L);
    filter.setTargetCapabilityId(456L);
    filter.setTargetComponentId(987L);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
    filter.apply(bug);
    assertEquals(bug.getTargetAttributeId().longValue(), 123L);
    assertEquals(bug.getTargetCapabilityId().longValue(), 456L);
    assertEquals(bug.getTargetComponentId().longValue(), 987L);
  }

  public void testAndFilter_noMatch() {
    Filter filter = makeFilter();
    filter.setFilterConjunction("and");
    filter.addFilterOption("Title", "security");
    filter.addFilterOption("Labels", "sec");
    filter.addFilterOption("Labels", "tuesday");
    Bug bug = new Bug();
    bug.setTitle("Error with security settings in IE6");
    bug.addBugGroup("dorp");
    bug.addBugGroup("sec");
    bug.addBugGroup("monday");
    filter.setTargetAttributeId(123L);
    filter.setTargetCapabilityId(456L);
    filter.setTargetComponentId(987L);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
    filter.apply(bug);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
  }

  public void testAnyFilter_matches() {
    Filter filter = makeFilter();
    filter.setFilterConjunction("any");
    filter.addFilterOption("Title", "security");
    filter.addFilterOption("Labels", "sec");
    filter.addFilterOption("Labels", "tuesday");
    Bug bug = new Bug();
    bug.setTitle("Error with security settings in IE6");
    bug.addBugGroup("dorp");
    bug.addBugGroup("sec");
    bug.addBugGroup("monday");
    filter.setTargetAttributeId(123L);
    filter.setTargetCapabilityId(456L);
    filter.setTargetComponentId(987L);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
    filter.apply(bug);
    assertEquals(bug.getTargetAttributeId().longValue(), 123L);
    assertEquals(bug.getTargetCapabilityId().longValue(), 456L);
    assertEquals(bug.getTargetComponentId().longValue(), 987L);
  }

  public void testAnyFilter_noMatch() {
    Filter filter = makeFilter();
    filter.setFilterConjunction("any");
    filter.addFilterOption("Title", "security");
    filter.addFilterOption("Labels", "sec");
    filter.addFilterOption("Labels", "tuesday");
    Bug bug = new Bug();
    bug.setTitle("Error with UI");
    bug.addBugGroup("dorp");
    bug.addBugGroup("ui");
    bug.addBugGroup("monday");
    filter.setTargetAttributeId(123L);
    filter.setTargetCapabilityId(456L);
    filter.setTargetComponentId(987L);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
    filter.apply(bug);
    assertEquals(bug.getTargetAttributeId(), null);
    assertEquals(bug.getTargetCapabilityId(), null);
    assertEquals(bug.getTargetComponentId(), null);
  }
}
