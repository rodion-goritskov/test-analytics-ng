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


package com.google.testing.testify.risk.frontend.server.api.impl;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.util.DataRequestDocumentGenerator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for exposing project data requests to third party tools, so that those requests may be
 * fulfilled.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@Singleton
public class DataApiImpl extends HttpServlet {
  private static final Logger log = Logger.getLogger(DataApiImpl.class.getName());

  private final DataService dataService;
  private final ProjectService projectService;
  private final UserService userService;
  
  @Inject
  public DataApiImpl(DataService dataService, ProjectService projectService,
      UserService userService) {
    this.dataService = dataService;
    this.projectService = projectService;
    this.userService = userService;
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("DataRequestServlet::GET");

    if (!userService.isUserLoggedIn()) {
      resp.getWriter().print("You must be logged in to access this service.");
      resp.sendError(5000);
      return;
    }

    // Query all projects the current user (typically a role account) has EDIT access to.
    List<Project> projectsUserCanEdit = projectService.queryProjectsUserHasEditAccessTo();

    // Query all data requests for those projects.
    List<DataRequest> relevantDataRequests = Lists.newArrayList();
    for (Project project : projectsUserCanEdit) {
      relevantDataRequests.addAll(dataService.getProjectRequests(project.getProjectId()));
    }

    // Generate the XML document and serve.
    String xmlDocumentText = DataRequestDocumentGenerator.generateDocument(relevantDataRequests);
    resp.getWriter().write(xmlDocumentText);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("DataRequestServlet::POST");
    resp.getWriter().write("Please call this URL using GET.");
  }
}
