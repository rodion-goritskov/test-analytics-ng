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


package com.google.testing.testify.risk.frontend.server;

import com.google.testing.testify.risk.frontend.model.AttributeTest;
import com.google.testing.testify.risk.frontend.model.BasicFieldsTest;
import com.google.testing.testify.risk.frontend.model.BugTest;
import com.google.testing.testify.risk.frontend.model.CapabilityTest;
import com.google.testing.testify.risk.frontend.model.CheckinTest;
import com.google.testing.testify.risk.frontend.model.ComponentTest;
import com.google.testing.testify.risk.frontend.model.DataSourceTest;
import com.google.testing.testify.risk.frontend.model.FailureRateTest;
import com.google.testing.testify.risk.frontend.model.FilterTest;
import com.google.testing.testify.risk.frontend.model.ProjectTest;
import com.google.testing.testify.risk.frontend.model.UserImpactTest;
import com.google.testing.testify.risk.frontend.server.service.impl.ProjectServiceImplTest;
import com.google.testing.testify.risk.frontend.server.util.DataRequestDocumentGeneratorTest;
import com.google.testing.testify.risk.frontend.shared.util.RiskUtilTest;
import com.google.testing.testify.risk.frontend.shared.util.StringUtilTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests that are capable of being run as JUnit tests. Note that none of these can
 * call GWT.create, e.g., instantiate UI controls, RPC servers, etc. You should try to make as
 * few GWTTestCases as possible.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ServerTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(AttributeTest.class);
    suite.addTestSuite(BasicFieldsTest.class);
    suite.addTestSuite(BugTest.class);
    suite.addTestSuite(CapabilityTest.class);
    suite.addTestSuite(CheckinTest.class);
    suite.addTestSuite(ComponentTest.class);
    suite.addTestSuite(DataSourceTest.class);
    suite.addTestSuite(FailureRateTest.class);
    suite.addTestSuite(FilterTest.class);
    suite.addTestSuite(ProjectTest.class);
    suite.addTestSuite(UserImpactTest.class);

    suite.addTestSuite(ProjectServiceImplTest.class);

    suite.addTestSuite(DataRequestDocumentGeneratorTest.class);

    suite.addTestSuite(RiskUtilTest.class);
    suite.addTestSuite(StringUtilTest.class);
    return suite;
  }
}
