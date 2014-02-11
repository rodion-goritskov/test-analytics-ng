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
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import junit.framework.TestCase;

/**
 * Unit tests the Project class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ProjectTest extends TestCase {
  public void testFields() {
    Project project = new Project();
    assertNull(project.getProjectId());
    assertNull(project.getName());
    assertNull(project.getDescription());
    assertFalse(project.getIsPubliclyVisible());
    project.setName("my project");
    project.setIsPubliclyVisible(true);
    project.setDescription("description of it");
    assertTrue(project.getIsPubliclyVisible());
    assertEquals("my project", project.getName());
    assertEquals("description of it", project.getDescription());

    assertEquals(0, project.getProjectOwners().size());
    project.addProjectOwner("jimr@");
    assertEquals(1, project.getProjectOwners().size());
    project.addProjectOwner("jimr@");
    assertEquals(1, project.getProjectOwners().size());
    project.removeProjectOwner("jimr");
    assertEquals(1, project.getProjectOwners().size());
    project.setProjectOwners(Lists.newArrayList("nobody@", "another@"));
    assertEquals(2, project.getProjectOwners().size());
    project.removeProjectOwner("another@");
    assertEquals(1, project.getProjectOwners().size());

    assertEquals(0, project.getProjectEditors().size());
    project.addProjectEditor("jimr@");
    assertEquals(1, project.getProjectEditors().size());
    project.addProjectEditor("jimr@");
    assertEquals(1, project.getProjectEditors().size());
    project.removeProjectEditor("jimr");
    assertEquals(1, project.getProjectEditors().size());
    project.setProjectEditors(Lists.newArrayList("nobody@", "another@"));
    assertEquals(2, project.getProjectEditors().size());
    project.removeProjectEditor("another@");
    assertEquals(1, project.getProjectEditors().size());

    assertEquals(0, project.getProjectViewers().size());
    project.addProjectView("jimr@");
    assertEquals(1, project.getProjectViewers().size());
    project.addProjectView("jimr@");
    assertEquals(1, project.getProjectViewers().size());
    project.removeProjectViewer("jimr");
    assertEquals(1, project.getProjectViewers().size());
    project.setProjectViewers(Lists.newArrayList("nobody@", "another@"));
    assertEquals(2, project.getProjectViewers().size());
    project.removeProjectViewer("another@");
    assertEquals(1, project.getProjectViewers().size());

    project.setCachedAccessLevel(ProjectAccess.EDIT_ACCESS);
    assertEquals(ProjectAccess.EDIT_ACCESS, project.getCachedAccessLevel());
  }
}
