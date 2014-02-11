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
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;
import com.google.testing.testify.risk.frontend.model.Filter;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpc;

import java.util.List;

/**
 * RPC by which data details are retrieved or updated.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class DataRpcImpl extends RemoteServiceServlet implements DataRpc {

  private final DataService dataService;

  @Inject
  public DataRpcImpl(DataService dataService) {
    this.dataService = dataService;
  }

  @Override
  public void addBug(Bug bug) {
    dataService.addBug(bug);
  }

  @Override
  public void addCheckin(Checkin checkin) {
    dataService.addCheckin(checkin);
  }

  @Override
  public long addDataRequest(DataRequest request) {
    return dataService.addDataRequest(request);
  }

  @Override
  public long addFilter(Filter filter) {
    return dataService.addFilter(filter);
  }

  @Override
  public void addTestCase(TestCase testCase) {
    dataService.addTestCase(testCase);
  }

  @Override
  public List<DataSource> getDataSources() {
    return dataService.getDataSources();
  }

  @Override
  public List<Filter> getFilters(long projectId) {
    return dataService.getFilters(projectId);
  }

  @Override
  public List<Bug> getProjectBugsById(long projectId) {
    return dataService.getProjectBugsById(projectId);
  }

  @Override
  public List<Checkin> getProjectCheckinsById(long projectId) {
    return dataService.getProjectCheckinsById(projectId);
  }

  @Override
  public List<DataRequest> getProjectRequests(long projectId) {
    return dataService.getProjectRequests(projectId);
  }

  @Override
  public List<TestCase> getProjectTestCasesById(long projectId) {
    return dataService.getProjectTestCasesById(projectId);
  }

  @Override
  public List<Signoff> getSignoffsByType(Long projectId, AccElementType type) {
    return dataService.getSignoffsByType(projectId, type);
  }

  @Override
  public Boolean isSignedOff(AccElementType type, Long elementId) {
    return dataService.isSignedOff(type, elementId);
  }

  @Override
  public void removeDataRequest(DataRequest request) {
    dataService.removeDataRequest(request);
  }

  @Override
  public void removeFilter(Filter filter) {
    dataService.removeFilter(filter);
  }

  @Override
  public void setSignedOff(
      Long projectId, AccElementType type, Long elementId, boolean isSignedOff) {
    dataService.setSignedOff(projectId, type, elementId, isSignedOff);
  }

  @Override
  public void updateBugAssociations(
      long bugId, long attributeId, long componentId, long capabilityId) {
    dataService.updateBugAssociations(bugId, attributeId, componentId, capabilityId);
  }

  @Override
  public void updateCheckinAssociations(
      long bugId, long attributeId, long componentId, long capabilityId) {
    dataService.updateCheckinAssociations(bugId, attributeId, componentId, capabilityId);
  }

  @Override
  public void updateDataRequest(DataRequest request) {
    dataService.updateDataRequest(request);
  }

  @Override
  public void updateFilter(Filter filter) {
    dataService.updateFilter(filter);
  }

  @Override
  public void updateTestAssociations(
      long testCaseId, long attributeId, long componentId, long capabilityId) {
    dataService.updateTestAssociations(testCaseId, attributeId, componentId, capabilityId);
  }
}
