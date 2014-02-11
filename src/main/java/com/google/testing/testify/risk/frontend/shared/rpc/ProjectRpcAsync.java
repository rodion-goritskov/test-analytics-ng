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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;

import java.util.List;

/**
 * Async interface for updating project information between client and server.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface ProjectRpcAsync {
  public void query(String query, AsyncCallback<List<Project>> callback);
  public void queryUserProjects(AsyncCallback<List<Project>> callback);

  public void getProjectById(long projectId, AsyncCallback<Project> callback);
  public void createProject(Project projInfo, AsyncCallback<Long> callback);
  public void updateProject(Project projInfo, AsyncCallback<Void> callback);
  public void removeProject(Project projInfo, AsyncCallback<Void> callback);

  public void getLabels(long projectId, AsyncCallback<List<AccLabel>> callback);

  public void getProjectAttributes(long projectId, AsyncCallback<List<Attribute>> callback);
  public void createAttribute(Attribute attribute, AsyncCallback<Long> callback);
  public void updateAttribute(Attribute attribute, AsyncCallback<Attribute> callback);
  public void removeAttribute(Attribute attribute, AsyncCallback<Void> callback);
  public void reorderAttributes(long projectId, List<Long> newOrder, AsyncCallback<Void> callback);

  public void getProjectComponents(long projectId, AsyncCallback<List<Component>> callback);
  public void createComponent(Component component, AsyncCallback<Long> callback);
  public void updateComponent(Component component, AsyncCallback<Component> callback);
  public void removeComponent(Component component, AsyncCallback<Void> callback);
  public void reorderComponents(long projectId, List<Long> newOrder, AsyncCallback<Void> callback);

  public void getCapabilityById(long projectId, long capabilityId,
      AsyncCallback<Capability> callback);
  public void getProjectCapabilities(long projectId, AsyncCallback<List<Capability>> callback);
  public void createCapability(Capability capability, AsyncCallback<Capability> callback);
  public void updateCapability(Capability capability, AsyncCallback<Void> callback);
  public void removeCapability(Capability capability, AsyncCallback<Void> callback);
  public void reorderCapabilities(long projectId, List<Long> newOrder,
      AsyncCallback<Void> callback);
}
