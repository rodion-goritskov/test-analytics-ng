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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpc;

import java.util.List;

/**
 * RPC methods that allow interaction with project data.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class ProjectRpcImpl extends RemoteServiceServlet implements ProjectRpc {

  private final ProjectService projectService;

  @Inject
  public ProjectRpcImpl(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  public Long createAttribute(Attribute attribute) {
    return projectService.createAttribute(attribute);
  }

  @Override
  public Capability createCapability(Capability capability) {
    return projectService.createCapability(capability);
  }

  @Override
  public Long createComponent(Component component) {
    return projectService.createComponent(component);
  }

  @Override
  public Long createProject(Project project) {
    return projectService.createProject(project);
  }

  @Override
  public Capability getCapabilityById(long projectId, long capabilityId) {
    return projectService.getCapabilityById(projectId, capabilityId);
  }

  @Override
  public List<AccLabel> getLabels(long projectId) {
    return projectService.getLabels(projectId);
  }

  @Override
  public List<Attribute> getProjectAttributes(long projectId) {
    return projectService.getProjectAttributes(projectId);
  }

  @Override
  public Project getProjectById(long projectId) {
    return projectService.getProjectById(projectId);
  }

  @Override
  public List<Capability> getProjectCapabilities(long projectId) {
    return projectService.getProjectCapabilities(projectId);
  }

  @Override
  public List<Component> getProjectComponents(long projectId) {
    return projectService.getProjectComponents(projectId);
  }

  @Override
  public List<Project> query(String query) {
    return projectService.query(query);
  }

  @Override
  public List<Project> queryUserProjects() {
    return projectService.queryUserProjects();
  }

  @Override
  public void removeAttribute(Attribute attribute) {
    projectService.removeAttribute(attribute);
  }

  @Override
  public void removeCapability(Capability capability) {
    projectService.removeCapability(capability);
  }

  @Override
  public void removeComponent(Component component) {
    projectService.removeComponent(component);
  }

  @Override
  public void removeProject(Project project) {
    projectService.removeProject(project);
  }

  @Override
  public void reorderAttributes(long projectId, List<Long> newOrder) {
    projectService.reorderAttributes(projectId, newOrder);
  }

  @Override
  public void reorderCapabilities(long projectId, List<Long> newOrder) {
    projectService.reorderCapabilities(projectId, newOrder);
  }

  @Override
  public void reorderComponents(long projectId, List<Long> newOrder) {
    projectService.reorderComponents(projectId, newOrder);
  }

  @Override
  public Attribute updateAttribute(Attribute attribute) {
    return projectService.updateAttribute(attribute);
  }

  @Override
  public void updateCapability(Capability capability) {
    projectService.updateCapability(capability);
  }

  @Override
  public Component updateComponent(Component component) {
    return projectService.updateComponent(component);
  }

  @Override
  public void updateProject(Project project) {
    projectService.updateProject(project);
  }
}
