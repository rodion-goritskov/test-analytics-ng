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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.view.RiskView;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import java.util.List;

/**
 * Base class for Presenters surfacing views on top of Risk and/or Risk Mitigations.
 *
 * @author chrsmith@google.com (Chris)
 */
public abstract class RiskPresenter extends BasePagePresenter {

  protected final ProjectRpcAsync projectService;
  protected final RiskView view;
  protected final Project project;

  public RiskPresenter(Project project, ProjectRpcAsync projectService, RiskView view) {
    this.project = project;
    this.projectService = projectService;
    this.view = view;
  }

  /**
   * Refreshes the view based on data obtained from the Project Service.
   */
  public void refreshBaseView() {
    final long projectId = project.getProjectId();

    projectService.getProjectAttributes(projectId,
        new TaCallback<List<Attribute>>("Querying Attributes") {
          @Override
          public void onSuccess(List<Attribute> result) {
            view.setAttributes(result);
          }
        });

    projectService.getProjectComponents(projectId,
        new TaCallback<List<Component>>("Querying Components") {
          @Override
          public void onSuccess(List<Component> result) {
            view.setComponents(result);
          }
        });

    projectService.getProjectCapabilities(projectId,
        new TaCallback<List<Capability>>("Querying Capabilities") {
          @Override
          public void onSuccess(List<Capability> result) {
            view.setCapabilities(result);
          }
        });
  }
}
