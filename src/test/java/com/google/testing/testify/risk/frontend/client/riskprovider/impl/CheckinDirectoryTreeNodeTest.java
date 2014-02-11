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


package com.google.testing.testify.risk.frontend.client.riskprovider.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.model.Checkin;

import junit.framework.TestCase;

import java.util.List;

/**
 * Unit tests for the CheckinDirectoryTreeNode type.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CheckinDirectoryTreeNodeTest extends TestCase {

  /** Returns a built checkin tree for testing purposes. */
  private CheckinDirectoryTreeNode getTestTree1() {
    CheckinDirectoryTreeNode root = new CheckinDirectoryTreeNode();
    root.addCheckin("alpha", new Checkin());
    root.addCheckin("alpha/beta", new Checkin());
    root.addCheckin("alpha/beta/gamma", new Checkin());
    // Forward and backslashes should be treated the same.
    root.addCheckin("alpha\\beta\\gamma", new Checkin());
    root.addCheckin("alpha/beta/delta", new Checkin());

    return root;
  }

  /** Get list of checkins to use for directory tree construction. */
  private List<Checkin> getTestCheckins() {
    Checkin checkin1 = new Checkin();
    checkin1.addDirectoryTouched("//depot/testify/risk/frontend/client/");
    checkin1.addDirectoryTouched("//depot/testify/risk/frontend/client/view/impl/");
    checkin1.addDirectoryTouched("//depot/testify/risk/frontend/public/");
    checkin1.addDirectoryTouched("//depot/testify/risk/frontend/public/images/");

    Checkin checkin2 = new Checkin();
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/dataprovider/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/dataprovider/impl/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/presenter/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/view/impl/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/client/view/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/model/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/server/impl/");
    checkin2.addDirectoryTouched("//depot/testify/risk/frontend/shared/");

    return Lists.newArrayList(checkin1, checkin2);
  }

  public void testCreation1() {
    CheckinDirectoryTreeNode root = new CheckinDirectoryTreeNode();

    List<Checkin> checkins = getTestCheckins();
    for (Checkin checkin : checkins) {
      for (String directoryTouched : checkin.getDirectoriesTouched()) {
        root.addCheckin(directoryTouched, checkin);
      }
    }

    ImmutableSet<Checkin> viewImplCheckins =
        root.getCheckinsUnder("//depot/testify/risk/frontend/client/view/impl/");
    assertEquals(2, viewImplCheckins.size());

    // Verify trailing slash isn't a problem
    ImmutableSet<Checkin> viewImplCheckins2 =
      root.getCheckinsUnder("//depot/testify/risk/frontend/client/view/impl");
    assertEquals(2, viewImplCheckins2.size());

    // Verify trailing slash isn't a problem
    ImmutableSet<Checkin> modelCheckins =
        root.getCheckinsUnder("//depot/testify/risk/frontend/model");
    assertEquals(1, modelCheckins.size());
  }

  public void testCreation2() {
    CheckinDirectoryTreeNode root = getTestTree1();

    ImmutableSet<Checkin> alpha = root.getCheckinsUnder("alpha");
    assertEquals(5, alpha.size());

    ImmutableSet<Checkin> beta = root.getCheckinsUnder("alpha/beta");
    assertEquals(4, beta.size());


    ImmutableSet<Checkin> gamma = root.getCheckinsUnder("alpha/beta/gamma");
    assertEquals(2, gamma.size());

    ImmutableSet<Checkin> delta = root.getCheckinsUnder("alpha/beta/delta");
    assertEquals(1, delta.size());
  }

  public void testLookup() {
    // Verify looking up non-existant nodes results in an empty set of checkins.
    CheckinDirectoryTreeNode root = getTestTree1();

    ImmutableSet<Checkin> test = root.getCheckinsUnder("totally / doesn't \\ exist");
    assertEquals(0, test.size());
  }
}
