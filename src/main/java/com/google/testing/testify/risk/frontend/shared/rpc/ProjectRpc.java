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


package com.google.testing.testify.risk.frontend.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;

import java.util.List;

/**
 * Interface for updating project information between client and server.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@RemoteServiceRelativePath("service/project")
public interface ProjectRpc extends RemoteService {
  public List<Project> query(String query);
  public List<Project> queryUserProjects();

  public Project getProjectById(long projectId);
  public Long createProject(Project project);
  public void updateProject(Project project);
  public void removeProject(Project project);

  public List<AccLabel> getLabels(long projectId);

  public List<Attribute> getProjectAttributes(long projectId);
  public Long createAttribute(Attribute attribute);
  public Attribute updateAttribute(Attribute attribute);
  public void removeAttribute(Attribute attribute);
  public void reorderAttributes(long projectId, List<Long> newOrder);

  public List<Component> getProjectComponents(long projectId);
  public Long createComponent(Component component);
  public Component updateComponent(Component component);
  public void removeComponent(Component component);
  public void reorderComponents(long projectId, List<Long> newOrder);

  public Capability getCapabilityById(long projectId, long capabilityId);
  public List<Capability> getProjectCapabilities(long projectId);
  public Capability createCapability(Capability capability);
  public void updateCapability(Capability capability);
  public void removeCapability(Capability capability);
  public void reorderCapabilities(long projectId, List<Long> newOrder);
}
