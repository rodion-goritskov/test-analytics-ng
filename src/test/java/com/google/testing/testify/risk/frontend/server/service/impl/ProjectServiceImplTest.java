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


package com.google.testing.testify.risk.frontend.server.service.impl;

import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.service.impl.ProjectServiceImpl;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Tests for ProjectServiceImpl.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ProjectServiceImplTest extends TestCase {

  // Common mocks.
  private final PersistenceManagerFactory pmf =
    EasyMock.createMock(PersistenceManagerFactory.class);
  private final PersistenceManager pm = EasyMock.createMock(PersistenceManager.class);
  private final UserService userService = EasyMock.createMock(UserService.class);
  private ProjectService service;

  @Override
  public void setUp() {
    // Typical calls.
    pmf.getPersistenceManager();
    EasyMock.expectLastCall().andReturn(pm);

    service = new ProjectServiceImpl(pmf, userService);
  }

  public void testQueryOnlyReturnsViewProjects() {
    List<Project> projects = Lists.newArrayList(
        newProject("yes 1", 1),
        newProject("no 1", 2),
        newProject("yes 2", 3),
        newProject("yes 3", 4),
        newProject("no 2", 5));

    // Query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    for (Project p : projects) {
      boolean result = p.getName().startsWith("yes");
      EasyMock.expect(userService.hasViewAccess(p)).andReturn(result);
      if (result) {
        EasyMock.expect(pm.detachCopy(p)).andReturn(p);
        EasyMock.expect(userService.getAccessLevel(p)).andReturn(ProjectAccess.EDIT_ACCESS);
      }
    }

    EasyMock.replay(pmf, pm, userService, query);

    List<Project> actual = service.query("");
    assertEquals(actual.size(), 3);
    assertEquals(actual.get(0).getName(), "yes 1");
    assertEquals(actual.get(1).getName(), "yes 2");
    assertEquals(actual.get(2).getName(), "yes 3");
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryUserProjects_notLoggedIn() {
    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(false);

    // Override the setUp actions.  Those are used in almost all cases, just not this one.
    EasyMock.reset(pmf);
    EasyMock.replay(userService, pmf, pm);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 0);
    EasyMock.verify(userService, pmf, pm);
  }

  public void testQueryUserProjects_empty() {
    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, Lists.<Project>newArrayList());

    // Getting starred query.
    expectGetStarred(Lists.<Long>newArrayList());

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 0);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryProjects_noAccess() {
    List<Project> projects = Lists.newArrayList(
        newProject("no1 access", 1),
        newProject("view access", 2),
        newProject("no2 access", 3),
        newProject("no3 access", 4),
        newProject("view2 access", 5));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    EasyMock.expect(userService.getAccessLevel(projects.get(0))).andReturn(ProjectAccess.NO_ACCESS);
    EasyMock.expect(userService.getAccessLevel(projects.get(1))).andReturn(
        ProjectAccess.VIEW_ACCESS);
    EasyMock.expect(userService.getAccessLevel(projects.get(2))).andReturn(
        ProjectAccess.NO_ACCESS);
    EasyMock.expect(userService.getAccessLevel(projects.get(3))).andReturn(
        ProjectAccess.NO_ACCESS);
    EasyMock.expect(userService.getAccessLevel(projects.get(4))).andReturn(
        ProjectAccess.VIEW_ACCESS);

    // Getting starred query.
    expectGetStarred(Lists.<Long>newArrayList());

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 0);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryProjects_noStarred() {
    List<Project> projects = Lists.newArrayList(
        newProject("no access", 1),
        newProject("view access", 2),
        newProject("explicit view access", 3),
        newProject("edit access", 4),
        newProject("owner access", 5));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    expectProjectCheck(projects.get(0), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(1), ProjectAccess.VIEW_ACCESS, false);
    expectProjectCheck(projects.get(2), ProjectAccess.EXPLICIT_VIEW_ACCESS, true);
    expectProjectCheck(projects.get(3), ProjectAccess.EDIT_ACCESS, true);
    expectProjectCheck(projects.get(4), ProjectAccess.OWNER_ACCESS, true);

    // Getting starred query.
    expectGetStarred(Lists.<Long>newArrayList());

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 3);
    assertEquals(actual.get(0).getProjectId().longValue(), 3);
    assertEquals(actual.get(1).getProjectId().longValue(), 4);
    assertEquals(actual.get(2).getProjectId().longValue(), 5);
    EasyMock.verify(pmf, pm, userService, query);
  }

  private void expectProjectCheck(Project p, ProjectAccess access, boolean willReturn) {
    EasyMock.expect(userService.getAccessLevel(p)).andReturn(access);
    if (willReturn) {
      EasyMock.expect(pm.detachCopy(p)).andReturn(p);
      EasyMock.expect(userService.getAccessLevel(p)).andReturn(access);
    }
  }

  public void testQueryProjects_starredWithAccess() {
    List<Project> projects = Lists.newArrayList(
        newProject("no access", 1),
        newProject("view access", 2),
        newProject("explicit view access", 3),
        newProject("edit access", 4),
        newProject("owner access", 5));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    expectProjectCheck(projects.get(0), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(1), ProjectAccess.VIEW_ACCESS, true);
    expectProjectCheck(projects.get(2), ProjectAccess.EXPLICIT_VIEW_ACCESS, true);
    expectProjectCheck(projects.get(3), ProjectAccess.EDIT_ACCESS, true);
    expectProjectCheck(projects.get(4), ProjectAccess.OWNER_ACCESS, true);

    // Getting starred query.
    expectGetStarred(Lists.newArrayList(2L));

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 4);
    assertEquals(actual.get(0).getProjectId().longValue(), 2);
    assertEquals(actual.get(1).getProjectId().longValue(), 3);
    assertEquals(actual.get(2).getProjectId().longValue(), 4);
    assertEquals(actual.get(3).getProjectId().longValue(), 5);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryProjects_starredWithoutAccess() {
    List<Project> projects = Lists.newArrayList(
        newProject("no access", 1),
        newProject("view access", 2),
        newProject("explicit view access", 3),
        newProject("edit access", 4),
        newProject("owner access", 5));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    expectProjectCheck(projects.get(0), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(1), ProjectAccess.VIEW_ACCESS, false);
    expectProjectCheck(projects.get(2), ProjectAccess.EXPLICIT_VIEW_ACCESS, true);
    expectProjectCheck(projects.get(3), ProjectAccess.EDIT_ACCESS, true);
    expectProjectCheck(projects.get(4), ProjectAccess.OWNER_ACCESS, true);

    // Getting starred query.
    expectGetStarred(Lists.newArrayList(1L));

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryUserProjects();
    assertEquals(actual.size(), 3);
    assertEquals(actual.get(0).getProjectId().longValue(), 3);
    assertEquals(actual.get(1).getProjectId().longValue(), 4);
    assertEquals(actual.get(2).getProjectId().longValue(), 5);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryProjectsEdit_some() {
    List<Project> projects = Lists.newArrayList(
        newProject("no1 access", 1),
        newProject("view access", 2),
        newProject("edit access", 3),
        newProject("no2 access", 4),
        newProject("explicit view access", 5),
        newProject("owner access", 6));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);
    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    expectProjectCheck(projects.get(0), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(1), ProjectAccess.VIEW_ACCESS, true);
    expectProjectCheck(projects.get(2), ProjectAccess.EDIT_ACCESS, true);
    expectProjectCheck(projects.get(3), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(4), ProjectAccess.EXPLICIT_VIEW_ACCESS, true);
    expectProjectCheck(projects.get(5), ProjectAccess.OWNER_ACCESS, true);

    // Getting starred query.
    expectGetStarred(Lists.newArrayList(1L, 2L));

    // Checking edit access.
    EasyMock.expect(userService.hasEditAccess(projects.get(1))).andReturn(false);
    EasyMock.expect(userService.hasEditAccess(projects.get(2))).andReturn(true);
    EasyMock.expect(userService.hasEditAccess(projects.get(4))).andReturn(false);
    EasyMock.expect(userService.hasEditAccess(projects.get(5))).andReturn(true);

    EasyMock.expect(userService.getAccessLevel(projects.get(2))).andReturn(null);
    EasyMock.expect(userService.getAccessLevel(projects.get(5))).andReturn(null);

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryProjectsUserHasEditAccessTo();
    assertEquals(actual.size(), 2);
    assertEquals(actual.get(0).getProjectId().longValue(), 3L);
    assertEquals(actual.get(1).getProjectId().longValue(), 6L);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testQueryProjectsEdit_none() {
    List<Project> projects = Lists.newArrayList(
        newProject("no1 access", 1),
        newProject("view access", 2),
        newProject("no2 access", 3),
        newProject("explicit view access", 4),
        newProject("view2 access", 5));

    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);
    userService.isUserLoggedIn();
    EasyMock.expectLastCall().andReturn(true);

    // DB query.
    Query query = expectQuery(Project.class);
    expectOrdering(query, "projectId asc");
    expectExecute(query, projects);

    expectProjectCheck(projects.get(0), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(1), ProjectAccess.VIEW_ACCESS, true);
    expectProjectCheck(projects.get(2), ProjectAccess.NO_ACCESS, false);
    expectProjectCheck(projects.get(3), ProjectAccess.EXPLICIT_VIEW_ACCESS, true);
    expectProjectCheck(projects.get(4), ProjectAccess.VIEW_ACCESS, false);

    // Getting starred query.
    expectGetStarred(Lists.newArrayList(1L, 2L));

    // Checking edit access.
    EasyMock.expect(userService.hasEditAccess(projects.get(1))).andReturn(false);
    EasyMock.expect(userService.hasEditAccess(projects.get(3))).andReturn(false);

    // Actual call.
    EasyMock.replay(pmf, pm, userService, query);
    List<Project> actual = service.queryProjectsUserHasEditAccessTo();
    assertEquals(actual.size(), 0);
    EasyMock.verify(pmf, pm, userService, query);
  }

  public void testGetProjectById_withAccess() {
    long id = 123L;
    Project project = new Project();
    project.setProjectId(123);
    project.setName("Hello There 123");

    EasyMock.expect(pm.getObjectById(Project.class, id)).andReturn(project);
    EasyMock.expect(userService.hasViewAccess(project)).andReturn(true);
    pm.close();
    EasyMock.expectLastCall();
    EasyMock.expect(pm.detachCopy(project)).andReturn(project);
    EasyMock.expect(userService.getAccessLevel(project)).andReturn(null);
    EasyMock.replay(userService, pmf, pm);
    Project actual = service.getProjectById(id);
    EasyMock.verify(userService, pmf, pm);

    assertEquals(actual.getProjectId().longValue(), id);
    assertEquals(actual.getName(), "Hello There 123");
  }

  public void testGetProjectById_withoutAccess() {
    long id = 123L;
    Project project = new Project();
    project.setProjectId(123);
    project.setName("Hello There 123");

    EasyMock.expect(pm.getObjectById(Project.class, id)).andReturn(project);
    EasyMock.expect(userService.hasViewAccess(project)).andReturn(false);
    pm.close();
    EasyMock.expectLastCall();

    EasyMock.replay(userService, pmf, pm);
    Project actual = service.getProjectById(id);
    EasyMock.verify(userService, pmf, pm);

    assertEquals(actual, null);
  }

  public void testGetProjectById_nx() {
    EasyMock.expect(pm.getObjectById(Project.class, 123L)).andReturn(null);
    pm.close();
    EasyMock.expectLastCall();

    EasyMock.replay(userService, pmf, pm);
    Project actual = service.getProjectById(123L);
    EasyMock.verify(userService, pmf, pm);

    assertEquals(actual, null);
  }

  public void testGetProjectByName_access() {
    Project project = new Project();
    project.setProjectId(123);
    project.setName("Hello There 123");
    List<Project> projects = Lists.newArrayList(project);

    Query query = expectQuery(Project.class);
    query.declareParameters("String projectNameParam");
    EasyMock.expectLastCall();
    query.setFilter("name == projectNameParam");
    EasyMock.expectLastCall();
    expectExecute(query, projects, "Hello There 123");

    EasyMock.expect(userService.hasViewAccess(project)).andReturn(true);

    EasyMock.expect(pm.detachCopy(project)).andReturn(project);
    EasyMock.expect(userService.getAccessLevel(project)).andReturn(null);
    EasyMock.replay(userService, pmf, pm, query);
    Project actual = service.getProjectByName("Hello There 123");
    EasyMock.verify(userService, pmf, pm, query);

    assertEquals(actual.getName(), "Hello There 123");
    assertEquals(actual.getProjectId().longValue(), 123L);
  }

  public void testGetProjectByName_noAccess() {
    Project project = new Project();
    project.setProjectId(123);
    project.setName("Hello There 123");
    List<Project> projects = Lists.newArrayList(project);

    Query query = expectQuery(Project.class);
    query.declareParameters("String projectNameParam");
    EasyMock.expectLastCall();
    query.setFilter("name == projectNameParam");
    EasyMock.expectLastCall();
    expectExecute(query, projects, "Hello There 123");

    EasyMock.expect(userService.hasViewAccess(project)).andReturn(false);

    EasyMock.replay(userService, pmf, pm, query);
    Project actual = service.getProjectByName("Hello There 123");
    EasyMock.verify(userService, pmf, pm, query);

    assertEquals(actual, null);
  }

  public void testGetProjectByName_multipleOneWithAccess() {
    Project project = new Project();
    project.setProjectId(123);
    project.setName("Has a Dupe");

    Project project2 = new Project();
    project2.setProjectId(124);
    project2.setName("Has a Dupe");
    List<Project> projects = Lists.newArrayList(project, project2);

    Query query = expectQuery(Project.class);
    query.declareParameters("String projectNameParam");
    EasyMock.expectLastCall();
    query.setFilter("name == projectNameParam");
    EasyMock.expectLastCall();
    expectExecute(query, projects, "Has a Dupe");

    EasyMock.expect(userService.hasViewAccess(project)).andReturn(false);
    EasyMock.expect(userService.hasViewAccess(project2)).andReturn(true);

    EasyMock.expect(pm.detachCopy(project2)).andReturn(project2);
    EasyMock.expect(userService.getAccessLevel(project2)).andReturn(null);
    EasyMock.replay(userService, pmf, pm, query);
    Project actual = service.getProjectByName("Has a Dupe");
    EasyMock.verify(userService, pmf, pm, query);

    assertEquals(actual.getName(), "Has a Dupe");
    assertEquals(actual.getProjectId().longValue(), 124L);
  }

  public void testGetProjectByName_nx() {
    List<Project> projects = Lists.newArrayList();

    Query query = expectQuery(Project.class);
    query.declareParameters("String projectNameParam");
    EasyMock.expectLastCall();
    query.setFilter("name == projectNameParam");
    EasyMock.expectLastCall();
    expectExecute(query, projects, "Hello There 123");

    EasyMock.replay(userService, pmf, pm, query);
    Project actual = service.getProjectByName("Hello There 123");
    EasyMock.verify(userService, pmf, pm, query);

    assertEquals(actual, null);
  }

  private void expectGetStarred(List<Long> result) {
    userService.getStarredProjects();
    EasyMock.expectLastCall().andReturn(result);
  }

  private void expectExecute(Query query, Object result) {
    query.execute();
    EasyMock.expectLastCall().andReturn(result);
    pm.close();
    EasyMock.expectLastCall();
  }

  private void expectExecute(Query query, Object result, String param) {
    query.execute(param);
    EasyMock.expectLastCall().andReturn(result);
    pm.close();
    EasyMock.expectLastCall();
  }

  @SuppressWarnings("unchecked")
  private Query expectQuery(@SuppressWarnings("rawtypes") Class clazz) {
    Query query = EasyMock.createMock(Query.class);
    EasyMock.expect(pm.newQuery(clazz)).andReturn(query);
    return query;
  }

  private void expectOrdering(Query query, String order) {
    query.setOrdering(order);
    EasyMock.expectLastCall();
  }

  private Project newProject(String name, long id) {
    Project p = new Project();
    p.setName(name);
    p.setProjectId(id);
    return p;
  }
}
