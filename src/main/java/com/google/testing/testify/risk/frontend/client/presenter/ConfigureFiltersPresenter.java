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

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.view.ConfigureFiltersView;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.Filter;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;

import java.util.HashMap;
import java.util.List;

/**
 * Presenter for Filter list and creating filters.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class ConfigureFiltersPresenter extends BasePagePresenter implements TaPagePresenter,
    ConfigureFiltersView.Presenter {

  private final Project project;
  private final DataRpcAsync dataService;
  private final ProjectRpcAsync projectService;
  private final ConfigureFiltersView view;

  public ConfigureFiltersPresenter(Project project, DataRpcAsync dataService,
      ProjectRpcAsync projectService, ConfigureFiltersView view) {

    this.project = project;
    this.dataService = dataService;
    this.projectService = projectService;
    this.view = view;

    refreshView();
  }

  @Override
  public Widget getView() {
    return view.asWidget();
  }

  @Override
  public void refreshView() {
    view.setPresenter(this);

    dataService.getFilters(project.getProjectId(),
        new TaCallback<List<Filter>>("Getting filters") {
          @Override
          public void onSuccess(List<Filter> result) {
            view.setFilters(result);
          }
        });

    projectService.getProjectAttributes(project.getProjectId(),
        new TaCallback<List<Attribute>>("Getting attributes") {
          @Override
          public void onSuccess(List<Attribute> result) {
            HashMap<String, Long> map = Maps.newHashMap();
            for (Attribute attribute : result) {
              map.put(attribute.getName(), attribute.getAttributeId());
            }
            view.setAttributes(map);
          }
        });

    projectService.getProjectComponents(project.getProjectId(),
        new TaCallback<List<Component>>("Getting components") {
          @Override
          public void onSuccess(List<Component> result) {
            HashMap<String, Long> map = Maps.newHashMap();
            for (Component component : result) {
              map.put(component.getName(), component.getComponentId());
            }
            view.setComponents(map);
          }
        });

    projectService.getProjectCapabilities(project.getProjectId(),
        new TaCallback<List<Capability>>("Getting capabilities") {
          @Override
          public void onSuccess(List<Capability> result) {
            HashMap<String, Long> map = Maps.newHashMap();
            for (Capability capability : result) {
              map.put(capability.getName(), capability.getCapabilityId());
            }
            view.setCapabilities(map);
          }
        });
  }

  @Override
  public void addFilter(final Filter newFilter) {
    newFilter.setParentProjectId(project.getProjectId());
    dataService.addFilter(newFilter,
        new TaCallback<Long>("Adding new filter") {
          @Override
          public void onSuccess(Long result) {
            newFilter.setId(result);
            refreshView();
          }
      });
  }

  @Override
  public void deleteFilter(Filter filterToDelete) {
    dataService.removeFilter(filterToDelete,
        new TaCallback<Void>("Deleting filter") {
          @Override
          public void onSuccess(Void result) {
            refreshView();
          }
      });
  }

  @Override
  public void updateFilter(Filter filterToUpdate) {
    dataService.updateFilter(filterToUpdate, TaCallback.getNoopCallback());
  }
}
