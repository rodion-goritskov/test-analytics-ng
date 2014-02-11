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
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;
import com.google.testing.testify.risk.frontend.model.Filter;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.model.TestCase;

import java.util.List;

/**
 * Interface for requesting data aggregation from an external source.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@RemoteServiceRelativePath("service/data")
public interface DataRpc extends RemoteService {
  public List<DataSource> getDataSources();

  public void setSignedOff(Long projectId, AccElementType type, Long elementId,
      boolean isSignedOff);
  public List<Signoff> getSignoffsByType(Long projectId, AccElementType type);
  public Boolean isSignedOff(AccElementType type, Long elementId);
  public List<DataRequest> getProjectRequests(long projectId);
  public long addDataRequest(DataRequest request);
  public void updateDataRequest(DataRequest request);
  public void removeDataRequest(DataRequest request);

  public List<Filter> getFilters(long projectId);
  public long addFilter(Filter filter);
  public void updateFilter(Filter filter);
  public void removeFilter(Filter filter);

  public List<Bug> getProjectBugsById(long projectId);
  public void addBug(Bug bug);
  public void updateBugAssociations(long bugId, long attributeId, long componentId,
      long capabilityId);

  public List<Checkin> getProjectCheckinsById(long projectId);
  public void addCheckin(Checkin checkin);
  public void updateCheckinAssociations(long bugId, long attributeId, long componentId,
      long capabilityId);

  public List<TestCase> getProjectTestCasesById(long projectId);
  public void updateTestAssociations(long testCaseId, long attributeId, long componentId,
      long capabilityId);
  public void addTestCase(TestCase testCase);
}
