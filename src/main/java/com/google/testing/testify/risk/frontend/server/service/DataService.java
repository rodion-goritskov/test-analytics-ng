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


package com.google.testing.testify.risk.frontend.server.service;

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
 * Server service for accessing data related to a project (ie; Bugs, Tests).
 *
 * @author jimr@google.com (Jim Reardon)
 */
public interface DataService {
  public boolean isSignedOff(AccElementType type, Long elementId);
  public void setSignedOff(long projectId, AccElementType type, long elementId,
      boolean isSignedOff);

  public List<Signoff> getSignoffsByType(long projectId, AccElementType type);

  public List<DataSource> getDataSources();

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
  public void addBug(Bug bug, String asEmail);
  public void updateBugAssociations(long bugId, long attributeId, long componentId,
      long capabilityId);

  public List<Checkin> getProjectCheckinsById(long projectId);
  public void addCheckin(Checkin checkin);
  public void addCheckin(Checkin checkin, String asEmail);
  public void updateCheckinAssociations(long bugId, long attributeId, long componentId,
      long capabilityId);

  public List<TestCase> getProjectTestCasesById(long projectId);
  public void addTestCase(TestCase testCase);
  public void addTestCase(TestCase testCase, String asEmail);
  public void updateTestAssociations(long testCaseId, long attributeId, long componentId,
      long capabilityId);
}
