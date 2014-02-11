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
 * Tests for DataSource.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class DataSourceTest extends TestCase {
  public void testFields() {
    DataSource source = new DataSource();
    assertEquals(null, source.getName());
    assertEquals(null, source.isInternalOnly());
    assertEquals(null, source.getParameters());
    source.setName("the name");
    source.setInternalOnly(true);
    source.setParameters(Lists.newArrayList("one", "two"));
    assertEquals("the name", source.getName());
    assertEquals(true, source.isInternalOnly().booleanValue());
    assertEquals(2, source.getParameters().size());
    assertTrue(source.getParameters().contains("one"));
    assertTrue(source.getParameters().contains("two"));
  }
}
