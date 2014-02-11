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


package com.google.testing.testify.risk.frontend.client;

import com.google.testing.testify.risk.frontend.client.presenter.AttributePresenterTest;
import com.google.testing.testify.risk.frontend.client.presenter.AttributesPresenterTest;
import com.google.testing.testify.risk.frontend.client.presenter.CapabilitiesPresenterTest;
import com.google.testing.testify.risk.frontend.client.presenter.ComponentPresenterTest;
import com.google.testing.testify.risk.frontend.client.presenter.ComponentsPresenterTest;
import com.google.testing.testify.risk.frontend.client.presenter.ProjectSettingsPresenterTest;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.CheckinDirectoryTreeNodeTest;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.StaticRiskProviderTest;
import com.google.testing.testify.risk.frontend.client.view.impl.ProjectSettingsViewImplTest;
import com.google.testing.testify.risk.frontend.client.view.widgets.StandardDialogBoxTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * These are client testcases that can ONLY be executed through gwt_test. If possible, please
 * create your test so that it can be ran as a standard JUnit unit test. This enables the use of
 * EasyMock and has a big impact on test performance.
 */
public class ClientTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(AttributePresenterTest.class);
    suite.addTestSuite(AttributesPresenterTest.class);
    suite.addTestSuite(CapabilitiesPresenterTest.class);
    suite.addTestSuite(ComponentPresenterTest.class);
    suite.addTestSuite(ComponentsPresenterTest.class);
    suite.addTestSuite(ProjectSettingsPresenterTest.class);

    suite.addTestSuite(CheckinDirectoryTreeNodeTest.class);
    suite.addTestSuite(StaticRiskProviderTest.class);

    suite.addTestSuite(ProjectSettingsViewImplTest.class);

    suite.addTestSuite(StandardDialogBoxTest.class);
    return suite;
  }
}
