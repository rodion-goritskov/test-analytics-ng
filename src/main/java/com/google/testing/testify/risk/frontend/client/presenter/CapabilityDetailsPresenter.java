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


package com.google.testing.testify.risk.frontend.client.presenter;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.view.CapabilityDetailsView;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.Collection;
import java.util.List;

/**
 * Presenter for the details of a single capability.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilityDetailsPresenter extends BasePagePresenter
    implements CapabilityDetailsView.Presenter {

  protected final Project project;
  protected final ProjectRpcAsync projectService;
  protected final UserRpcAsync userService;
  protected final DataRpcAsync dataService;
  protected final CapabilityDetailsView view;

  protected String pageData;

  private Long capabilityId;

  public CapabilityDetailsPresenter(Project project, ProjectRpcAsync projectService,
      DataRpcAsync dataService, UserRpcAsync userService,
      CapabilityDetailsView view) {
    this.project = project;
    this.projectService = projectService;
    this.dataService = dataService;
    this.userService = userService;
    this.view = view;

    view.setPresenter(this);
  }

  @Override
  public void refreshView() {
    final long projectId = project.getProjectId();

    // Tell view to expect an entire reload of data.
    view.reset();
    userService.hasEditAccess(projectId,
        new TaCallback<Boolean>("Querying user permissions") {
          @Override
          public void onSuccess(Boolean result) {
            if (result == true) {
              view.makeEditable();
            }
          }
        });
    projectService.getProjectAttributes(projectId,
        new TaCallback<List<Attribute>>("Querying Attributes") {
          @Override
          public void onSuccess(List<Attribute> result) {
            view.setAttributes(result);
          }
        });

    projectService.getLabels(projectId,
        new TaCallback<List<AccLabel>>("Querying Components") {
          @Override
          public void onSuccess(List<AccLabel> result) {
            Collection<String> labels = Lists.newArrayList();
            for (AccLabel l : result) {
              labels.add(l.getLabelText());
            }
            view.setProjectLabels(labels);
          }
        });

    projectService.getProjectComponents(projectId,
        new TaCallback<List<Component>>("Querying Components") {
          @Override
          public void onSuccess(List<Component> result) {
            view.setComponents(result);
          }
        });

    projectService.getCapabilityById(projectId, capabilityId,
        new TaCallback<Capability>("Querying capability") {
          @Override
          public void onSuccess(Capability result) {
            view.setCapability(result);
          }
        });

    dataService.isSignedOff(AccElementType.CAPABILITY, capabilityId,
        new TaCallback<Boolean>("Getting signoff status") {
          @Override
          public void onSuccess(Boolean result) {
            view.setSignoff(result == null ? false : result);
          }
        });

    dataService.getProjectBugsById(projectId,
        new TaCallback<List<Bug>>("Querying Bugs") {
          @Override
          public void onSuccess(List<Bug> result) {
            view.setBugs(result);
          }
        });

    dataService.getProjectTestCasesById(projectId,
        new TaCallback<List<TestCase>>("Querying Tests") {
          @Override
          public void onSuccess(List<TestCase> result) {
            view.setTests(result);
          }
        });

    dataService.getProjectCheckinsById(projectId,
        new TaCallback<List<Checkin>>("Querying Checkins") {
        @Override
        public void onSuccess(List<Checkin> result) {
          view.setCheckins(result);
        }
      });
  }

  @Override
  public void assignBugToCapability(long capabilityId, long bugId) {
    dataService.updateBugAssociations(bugId, -1, -1, capabilityId,
        new TaCallback<Void>("assigning bug to capability"));
  }

  @Override
  public void assignCheckinToCapability(long capabilityId, long checkinId) {
    dataService.updateCheckinAssociations(checkinId, -1, -1, capabilityId,
        new TaCallback<Void>("assigning checkin to capability"));
  }

  @Override
  public void assignTestCaseToCapability(long capabilityId, long testId) {
    dataService.updateTestAssociations(testId, -1, -1, capabilityId,
        new TaCallback<Void>("assigning test to capability"));
  }

  @Override
  public void refreshView(String pageData) {
    try {
      capabilityId = Long.parseLong(pageData);
    } catch (NumberFormatException e) {
      Window.alert("Cannot refresh capability details page, invalid capbility ID.");
    }
    refreshView();
  }

  @Override
  public Widget getView() {
    return view.asWidget();
  }

  @Override
  public void updateCapability(Capability capability) {
    projectService.updateCapability(capability, new TaCallback<Void>("updating capability"));
  }

  @Override
  public void setSignoff(long capabilityId, boolean isSignedOff) {
    dataService.setSignedOff(project.getProjectId(), AccElementType.CAPABILITY, capabilityId,
        isSignedOff, new TaCallback<Void>("setting signoff status"));
  }
}
