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


package com.google.testing.testify.risk.frontend.server.rpc.impl;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.DataSource;
import com.google.testing.testify.risk.frontend.model.FailureRate;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.model.UserImpact;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.util.ServletUtils;
import com.google.testing.testify.risk.frontend.shared.rpc.TestProjectCreatorRpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * A simple servlet that allows an admin to create a test project populated with some data.
 * Useful for testing purposes and to make sure everything is working how it should.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class TestProjectCreatorRpcImpl extends RemoteServiceServlet
    implements TestProjectCreatorRpc {

  private static final Logger LOG = Logger.getLogger(TestProjectCreatorRpcImpl.class.getName());

  private final ProjectService projectService;
  private final UserService userService;
  private final DataService dataService;
  private final PersistenceManagerFactory pmf;

  @Inject
  public TestProjectCreatorRpcImpl(ProjectService projectService, UserService userService,
        DataService dataService, PersistenceManagerFactory pmf) {
    this.projectService = projectService;
    this.userService = userService;
    this.dataService = dataService;
    this.pmf = pmf;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void createStandardDataSources() {
    LOG.info("Injecting standard datasources.");
    boolean admin = userService.hasAdministratorAccess();
    boolean devMode = userService.isDevMode();
    ServletUtils.requireAccess(admin || devMode);

    DataSource bugSource = new DataSource();
    bugSource.setInternalOnly(true);
    bugSource.setName("Bug Database");
    bugSource.setParameters(Lists.newArrayList("Path", "Hotlist"));

    DataSource testManager = new DataSource();
    testManager.setInternalOnly(true);
    testManager.setName("Test Database");
    testManager.setParameters(Lists.newArrayList("Label", "ProjectID", "SavedSearchID"));

    DataSource perforce = new DataSource();
    perforce.setInternalOnly(true);
    perforce.setName("Perforce");
    perforce.setParameters(Lists.newArrayList("Path"));

    DataSource issueTracker = new DataSource();
    issueTracker.setInternalOnly(false);
    issueTracker.setName("Issue Tracker");
    issueTracker.setParameters(Lists.newArrayList("Project", "Label", "Owner"));

    DataSource other = new DataSource();
    other.setInternalOnly(false);
    other.setName("Other...");
    other.setParameters(new ArrayList<String>());

    List<DataSource> all = Lists.newArrayList(
        bugSource, testManager, perforce, issueTracker, other);
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      // Remove any data source from what we will persist if it already exists.
      DataSource source;
      Iterator<DataSource> i = all.iterator();
      while (i.hasNext()) {
        source = i.next();
        Query query = pm.newQuery(DataSource.class);
        query.declareParameters("String nameParam");
        query.setFilter("name == nameParam");
        if (((List<DataSource>) query.execute(source.getName())).size() > 0) {
          i.remove();
        }
      }
      pm.makePersistentAll(all);
    } finally {
      pm.close();
    }
  }

  @Override
  public Project createTestProject() {
    LOG.info("Starting to create a sample project...");
    boolean admin = userService.hasAdministratorAccess();
    boolean devMode = userService.isDevMode();
    ServletUtils.requireAccess(admin || devMode);

    // Project.
    LOG.info("Creating project.");
    Project project = new Project();
    String testDescription = "An example project created by TestProjectCreator.";
    project.setDescription(testDescription);
    project.setName("Test Project " + System.currentTimeMillis());
    Long projectId = projectService.createProject(project);

    // Attributes.
    LOG.info("Creating attributes.");
    List<Attribute> attributes = Lists.newArrayList();
    Attribute attr1 = new Attribute();
    attr1.setParentProjectId(projectId);
    attr1.setName("Fast");
    attr1.setDescription("This should be speedy.");
    attr1.addLabel("owner: alaska@example");
    attr1.addLabel("pm: will@example");
    attr1.setAttributeId(projectService.createAttribute(attr1));
    attributes.add(attr1);

    Attribute attr2 = new Attribute();
    attr2.setParentProjectId(projectId);
    attr2.setName("Simple");
    attr2.addLabel("owner: alaska@example");
    attr2.setAttributeId(projectService.createAttribute(attr2));
    attributes.add(attr2);

    Attribute attr3 = new Attribute();
    attr3.setParentProjectId(projectId);
    attr3.setName("Secure");
    attr3.addLabel("owner: margo@example");
    attr3.addLabel("tester: quentin@example");
    attr3.setAttributeId(projectService.createAttribute(attr3));
    attributes.add(attr3);

    // Components.
    LOG.info("Creating components.");
    ArrayList<Component> components = Lists.newArrayList();
    Component comp1 = new Component(projectId);
    comp1.setName("Shopping Cart");
    comp1.addLabel("dev lead: miles@example");
    comp1.addLabel("tester: katherine@example");
    comp1.setDescription("Contains items people want to buy.");
    comp1.setComponentId(projectService.createComponent(comp1));
    components.add(comp1);

    Component comp2 = new Component(projectId);
    comp2.setName("Sales Channel");
    comp2.addLabel("dev lead: colin@example");
    comp2.setComponentId(projectService.createComponent(comp2));
    components.add(comp2);

    Component comp3 = new Component(projectId);
    comp3.setName("Social");
    comp3.addLabel("owner: alaska@example");
    comp3.setComponentId(projectService.createComponent(comp3));
    components.add(comp3);

    Component comp4 = new Component(projectId);
    comp4.setName("Search");
    comp4.setComponentId(projectService.createComponent(comp4));
    components.add(comp4);

    // Capabilities.
    LOG.info("Creating capabilities.");
    ArrayList<Capability> capabilities = Lists.newArrayList();
    Capability capa1 = new Capability(
        projectId, attributes.get(0).getAttributeId(), components.get(1).getComponentId());
    capa1.setName("Credit card processing takes less than 5 seconds");
    capa1.setFailureRate(FailureRate.OFTEN);
    capa1.addLabel("external");
    capa1.addLabel("load test");
    capa1.setDescription("Order is completed from clicking 'ORDER NOW' to success page.");
    capa1.setUserImpact(UserImpact.MINIMAL);
    capa1.setCapabilityId(projectService.createCapability(capa1).getCapabilityId());
    capabilities.add(capa1);

    Capability capa2 = new Capability(
        projectId, attributes.get(0).getAttributeId(), components.get(1).getComponentId());
    capa2.setName("Saved addresses and credit cards appear quickly");
    capa2.setFailureRate(FailureRate.OCCASIONALLY);
    capa2.setUserImpact(UserImpact.MAXIMAL);
    capa2.setCapabilityId(projectService.createCapability(capa2).getCapabilityId());
    capabilities.add(capa2);

    Capability capa3 = new Capability(
        projectId, attributes.get(2).getAttributeId(), components.get(1).getComponentId());
    capa3.setName("All traffic is sent over https");
    capa3.addLabel("ssl");
    capa3.setFailureRate(FailureRate.NA);
    capa3.setUserImpact(UserImpact.MAXIMAL);
    capa3.setCapabilityId(projectService.createCapability(capa3).getCapabilityId());
    capabilities.add(capa3);

    Capability capa4 = new Capability(
        projectId, attributes.get(2).getAttributeId(), components.get(3).getComponentId());
    capa4.setName("Items removed from inventory do not appear in search");
    capa4.setFailureRate(FailureRate.VERY_RARELY);
    capa4.setUserImpact(UserImpact.NA);
    capa4.setCapabilityId(projectService.createCapability(capa4).getCapabilityId());
    capabilities.add(capa4);

    // Bugs.
    LOG.info("Creating bugs.");
    List<Bug> bugs = Lists.newArrayList();
    Bug bug1 = new Bug();
    bug1.setExternalId(42L);
    bug1.setTitle("SSL certificate error in some browsers.");
    bug1.addBugGroup("security");
    bug1.addBugGroup("checkout");
    bug1.setParentProjectId(projectId);
    bug1.setPriority(1L);
    bug1.setSeverity(2L);
    bug1.setState("Open");
    bug1.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
    bug1.setStateDate(System.currentTimeMillis() - (84000000 * 4));
    bug1.setBugUrl("http://example/42");
    dataService.addBug(bug1);
    bugs.add(bug1);

    Bug bug2 = new Bug();
    bug2.setExternalId(123L);
    bug2.setTitle("+1 button is not showing on products with prime number IDs.");
    bug2.addBugGroup("social");
    bug2.addBugGroup("browsing");
    bug2.setParentProjectId(projectId);
    bug2.setPriority(1L);
    bug2.setSeverity(2L);
    bug2.setState("Open");
    bug2.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
    bug2.setStateDate(System.currentTimeMillis() - (84000000 * 2));
    bug2.setBugUrl("http://example/123");
    dataService.addBug(bug2);
    bugs.add(bug2);

    Bug bug3 = new Bug();
    bug3.setExternalId(122L);
    bug3.setTitle("Search results always return developer's favorite item");
    bug3.addBugGroup("search");
    bug3.addBugGroup("accuracy");
    bug3.setParentProjectId(projectId);
    bug3.setPriority(4L);
    bug3.setSeverity(4L);
    bug3.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
    bug3.setState("Closed");
    bug3.setStateDate(System.currentTimeMillis() - (84000000 * 10));
    bug3.setBugUrl("http://example/34121");
    dataService.addBug(bug3);
    bugs.add(bug3);

    // Checkins.
    LOG.info("Creating checkins.");
    Checkin checkin1 = new Checkin();
    checkin1.setChangeUrl("http://example/code/16358580");
    checkin1.setExternalId(16358580L);
    checkin1.addDirectoryTouched("java/com/example/DoorKnobFactoryFactory");
    checkin1.setParentProjectId(projectId);
    checkin1.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
    checkin1.setStateDate(System.currentTimeMillis() - (84000000 * 2));
    checkin1.setSummary("Add factory to create a factory to provide Door Knob objects.");
    dataService.addCheckin(checkin1);

    Checkin checkin2 = new Checkin();
    checkin2.setChangeUrl("http://example/code/16358581");
    checkin2.setExternalId(16358581L);
    checkin2.addDirectoryTouched("java/com/example/Search");
    checkin2.setParentProjectId(projectId);
    checkin2.setStateDate(System.currentTimeMillis() - (84000000 * 7));
    checkin2.setSummary("Search engine optimizations");
    dataService.addCheckin(checkin2);

    // Tests.
    LOG.info("Creating tests.");
    TestCase testcase1 = new TestCase();
    testcase1.setParentProjectId(projectId);
    testcase1.setExternalId(1042L);
    testcase1.setTestCaseUrl("http://example/test/1042");
    testcase1.setTitle("Search for 'dogfood'");
    testcase1.setTargetAttributeId(attributes.get(1).getAttributeId());
    testcase1.setTargetComponentId(components.get(1).getComponentId());
    dataService.addTestCase(testcase1);

    TestCase testcase2 = new TestCase();
    testcase2.setParentProjectId(projectId);
    testcase2.setExternalId(1043L);
    testcase2.setTestCaseUrl("http://example/test/1043");
    testcase2.setTitle("Post an item to your favorite social network via sharing functions");
    testcase2.setTargetCapabilityId(capabilities.get(3).getCapabilityId());
    dataService.addTestCase(testcase2);

    for (int i = 0; i < 6; i++) {
      TestCase testcase3 = new TestCase();
      testcase3.setParentProjectId(projectId);
      testcase3.setExternalId(Long.valueOf(1044 + i * 100));
      testcase3.setTestCaseUrl("http://example/test/" + i);
      testcase3.setTitle("Some Random Automated Test " + i);
      testcase3.setState("Passed");
      testcase3.setStateDate(System.currentTimeMillis() - (84000000L * i));
      testcase3.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
      dataService.addTestCase(testcase3);
    }

    for (int i = 0; i < 4; i++) {
      TestCase testcase4 = new TestCase();
      testcase4.setParentProjectId(projectId);
      testcase4.setExternalId(Long.valueOf(1045 + i * 100));
      testcase4.setTestCaseUrl("http://example/test/" + i);
      testcase4.setTitle("Some Random Manual Test " + i);
      testcase4.setState("Failed");
      testcase4.setStateDate(System.currentTimeMillis() - (84000000L * i));
      testcase4.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
      dataService.addTestCase(testcase4);
    }

    for (int i = 0; i < 2; i++) {
      TestCase testcase5 = new TestCase();
      testcase5.setParentProjectId(projectId);
      testcase5.setExternalId(Long.valueOf(1046 + i * 100));
      testcase5.setTestCaseUrl("http://example/test/" + i);
      testcase5.setTitle("Test We Never Run " + i);
      testcase5.setTargetCapabilityId(capabilities.get(0).getCapabilityId());
      dataService.addTestCase(testcase5);
    }

    LOG.info("Done.  Returning created project.");
    return project;
  }
}
