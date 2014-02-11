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
public interface DataRpcAsync {
  public void getDataSources(AsyncCallback<List<DataSource>> callback);

  public void setSignedOff(Long projectId, AccElementType type, Long elementId, boolean isSignedOff,
      AsyncCallback<Void> callback);
  public void isSignedOff(AccElementType type, Long elementId, AsyncCallback<Boolean> callback);
  public void getSignoffsByType(Long projectId, AccElementType type,
      AsyncCallback<List<Signoff>> callback);

  public void getProjectRequests(long projectId, AsyncCallback<List<DataRequest>> callback);
  public void addDataRequest(DataRequest request, AsyncCallback<Long> callback);
  public void updateDataRequest(DataRequest request, AsyncCallback<Void> callback);
  public void removeDataRequest(DataRequest request, AsyncCallback<Void> callback);

  public void getFilters(long projectId, AsyncCallback<List<Filter>> callback);
  public void addFilter(Filter filter, AsyncCallback<Long> callback);
  public void updateFilter(Filter filter, AsyncCallback<Void> callback);
  public void removeFilter(Filter filter, AsyncCallback<Void> callback);

  public void getProjectBugsById(long projectId, AsyncCallback<List<Bug>> callback);
  public void updateBugAssociations(long bugId, long attributeId, long componentId,
      long capabilityId, AsyncCallback<Void> callback);
  public void addBug(Bug bug, AsyncCallback<Void> callback);

  public void getProjectCheckinsById(long projectId, AsyncCallback<List<Checkin>> callback);
  public void updateCheckinAssociations(long checkinId, long attributeId, long componentId,
      long capabilityId, AsyncCallback<Void> callback);
  public void addCheckin(Checkin checkin, AsyncCallback<Void> callback);

  public void getProjectTestCasesById(long projectId, AsyncCallback<List<TestCase>> callback);
  public void updateTestAssociations(long testCaseId, long attributeId, long componentId,
      long capabilityId, AsyncCallback<Void> callback);
  public void addTestCase(TestCase testCase, AsyncCallback<Void> callback);

}