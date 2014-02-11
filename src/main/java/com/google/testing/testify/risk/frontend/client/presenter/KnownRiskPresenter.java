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

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.view.RiskView;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

/**
 * Presenter for viewing a project's known risk, that is outstanding risk minus mitigations.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class KnownRiskPresenter extends RiskPresenter implements TaPagePresenter {

  public KnownRiskPresenter(
        Project project, ProjectRpcAsync projectService, RiskView view) {
    super(project, projectService, view);
    refreshView();
  }

  @Override
  public void refreshView() {
    refreshBaseView();
  }

  @Override
  public Widget getView() {
    return view.asWidget();
  }
}
