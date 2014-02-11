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
 * Test for sundry model classes that don't have much/any logic.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class BasicFieldsTest extends TestCase {

  public void testLoginStatus() {
    LoginStatus status = new LoginStatus();
    assertEquals(false, status.getIsLoggedIn());
    assertNull(status.getEmail());
    assertNull(status.getUrl());
    status = new LoginStatus(true, "http://", "bob@");
    assertTrue(status.getIsLoggedIn());
    assertEquals("http://", status.getUrl());
    assertEquals("bob@", status.getEmail());
    status.setIsLoggedIn(false);
    status.setEmail("joe@");
    status.setUrl("gopher://");
    assertFalse(status.getIsLoggedIn());
    assertEquals("gopher://", status.getUrl());
    assertEquals("joe@", status.getEmail());
  }

  public void testSignoff() {
    Signoff signoff = new Signoff();
    assertNull(signoff.getId());
    assertEquals(0, signoff.getParentProjectId());
    assertNull(signoff.getElementId());
    assertNull(signoff.getElementType());
    assertNull(signoff.getSignedOff());
    signoff.setId(123L);
    signoff.setParentProjectId(555L);
    signoff.setElementId(877L);
    signoff.setElementType(AccElementType.ATTRIBUTE);
    signoff.setSignedOff(true);
    assertTrue(signoff.getSignedOff().booleanValue());
    assertEquals(123L, signoff.getId().longValue());
    assertEquals(555L, signoff.getParentProjectId());
    assertEquals(877L, signoff.getElementId().longValue());
    assertEquals(AccElementType.ATTRIBUTE, signoff.getElementType());
  }

  public void testUserInfo() {
    UserInfo info = new UserInfo();
    assertNull(info.getUserId());
    assertFalse(info.getIsWhitelisted());
    assertNull(info.getCurrentEmail());
    assertEquals(0, info.getStarredProjects().size());
    info.setUserId("werwer");
    info.setIsWhitelisted(true);
    info.setCurrentEmail("bob@");
    assertEquals("werwer", info.getUserId());
    assertEquals(true, info.getIsWhitelisted().booleanValue());
    assertEquals("bob@", info.getCurrentEmail());
    info.starProject(1324L);
    assertEquals(1, info.getStarredProjects().size());
    info.unstarProject(444L);
    assertEquals(1, info.getStarredProjects().size());
    info.unstarProject(1324);
    assertEquals(0, info.getStarredProjects().size());
    info.starProject(1324L);
    assertEquals(1, info.getStarredProjects().size());
    info.setStarredProjects(Lists.newArrayList(444L));
    assertEquals(1, info.getStarredProjects().size());
    assertEquals(444L, info.getStarredProjects().get(0).longValue());
  }
}
