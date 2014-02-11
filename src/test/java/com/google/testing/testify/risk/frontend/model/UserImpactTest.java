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

import junit.framework.TestCase;

/**
 * Unit tests for the UserImpact enum.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class UserImpactTest extends TestCase {
  public void testFromName() {
    assertEquals(UserImpact.NA, UserImpact.fromDescription("n/a"));
    assertEquals(UserImpact.CONSIDERABLE, UserImpact.fromDescription("Considerable"));
    assertEquals(UserImpact.MAXIMAL, UserImpact.fromDescription("Maximal"));
    assertEquals(UserImpact.MINIMAL, UserImpact.fromDescription("Minimal"));
    assertEquals(UserImpact.SOME, UserImpact.fromDescription("Some"));
    assertEquals(null, UserImpact.fromDescription("doesn't exist"));
  }
}
