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


package com.google.testing.testify.risk.frontend.server.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataSource;
import com.google.testing.testify.risk.frontend.model.DatumType;
import com.google.testing.testify.risk.frontend.model.Filter;
import com.google.testing.testify.risk.frontend.model.Signoff;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.model.UploadedDatum;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.util.ServletUtils;
import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Implementation of the DataRequestService, for tracking project requests for external
 * data. This data is also exposed via the DataRequest servlet.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class DataServiceImpl implements DataService {
  private static final Logger log = Logger.getLogger(DataServiceImpl.class.getName());
  private final PersistenceManagerFactory pmf;
  private final UserService userService;

  /**
   * Creates a new DataServiceImpl instance.
   */
  @Inject
  public DataServiceImpl(PersistenceManagerFactory pmf, UserService userService) {
    this.pmf = pmf;
    this.userService = userService;
  }

  @Override
  public boolean isSignedOff(AccElementType type, Long elementId) {
    Signoff signoff = getSignoff(type, elementId);
    return signoff == null ? false : signoff.getSignedOff();
  }

  @Override
  public void setSignedOff(long projectId, AccElementType type, long elementId,
      boolean isSignedOff) {
    ServletUtils.requireAccess(userService.hasEditAccess(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Signoff signoff = getSignoff(type, elementId);
      if (signoff == null) {
        signoff = new Signoff();
        signoff.setParentProjectId(projectId);
        signoff.setElementType(type);
        signoff.setElementId(elementId);
      } else {
        if (signoff.getParentProjectId() != projectId) {
          ServletUtils.requireAccess(false);
        }
      }
      signoff.setSignedOff(isSignedOff);
      pm.makePersistent(signoff);
    } finally {
      pm.close();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Signoff> getSignoffsByType(long projectId, AccElementType type) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Query query = pm.newQuery(Signoff.class);
      query.declareParameters("AccElementType elementTypeParam, Long projectIdParam");
      query.setFilter("elementType == elementTypeParam && parentProjectId == projectIdParam");
      List<Signoff> results = (List<Signoff>) query.execute(type, projectId);
      return ServletUtils.makeGwtSafe(results, pm);
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  private Signoff getSignoff(AccElementType type, Long elementId) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Query query = pm.newQuery(Signoff.class);
      query.declareParameters("AccElementType elementTypeParam, Long elementIdParam");
      query.setFilter("elementType == elementTypeParam && elementId == elementIdParam");
      List<Signoff> results = (List<Signoff>) query.execute(type, elementId);
      if (results.size() > 0) {
        Signoff signoff = results.get(0);
        ServletUtils.requireAccess(userService.hasViewAccess(signoff.getParentProjectId()));
        return ServletUtils.makeGwtSafe(signoff, pm);
      } else {
        return null;
      }
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DataSource> getDataSources() {
    boolean isInternal = userService.isInternalUser();
    PersistenceManager pm = pmf.getPersistenceManager();
    List<DataSource> results = null;
    log.info("Retrieving data sources.");
    try {
      Query query = pm.newQuery(DataSource.class);
      if (isInternal == false) {
        query.setFilter("internalOnly == false");
        log.info("Only retrieving external friendly sources, not an internal user.");
      }
      results = (List<DataSource>) query.execute();
      results = ServletUtils.makeGwtSafe(results, pm);
    } finally {
      pm.close();
    }
    log.info("Returning results: " + results.size());
    return results;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DataRequest> getProjectRequests(long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting Data Requests for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(DataRequest.class);
    jdoQuery.declareParameters("Long parentProjectParam");
    jdoQuery.setFilter("parentProjectId == parentProjectParam");

    List<DataRequest> results = null;
    try {
      List<DataRequest> returnedRequests = (List<DataRequest>) jdoQuery.execute(projectId);
      results = ServletUtils.makeGwtSafe(returnedRequests, pm);
    } finally {
      pm.close();
    }

    return results;
  }

  @Override
  public long addDataRequest(DataRequest request) {
    ServletUtils.requireAccess(userService.hasEditAccess(request.getParentProjectId()));

    log.info("Creating new Data Request for source: " + request.getDataSourceName());
    request.setDataSourceName(request.getDataSourceName().trim());

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(request);
    } finally {
      pm.close();
    }

    return request.getRequestId();
  }

  @Override
  public void updateDataRequest(DataRequest request) {
    if (request.getRequestId() == null) {
        throw new IllegalArgumentException(
            "Request has not been saved. Please call addDataRequest first.");
    }
    ServletUtils.requireAccess(userService.hasEditAccess(request.getParentProjectId()));

    log.info("Updating DataRequest: " + request.getRequestId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(request);
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeDataRequest(DataRequest request) {
    if (request.getRequestId() == null) {
      log.info("Attempting to delete unsaved DataRequest. Ignoring.");
      return;
    }

    ServletUtils.requireAccess(userService.hasEditAccess(request.getParentProjectId()));

    log.info("Removing DataRequest: " + request.getRequestId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      DataRequest requestToDelete = pm.getObjectById(DataRequest.class, request.getRequestId());
      pm.deletePersistent(requestToDelete);
    } finally {
      pm.close();
    }
  }

  @Override
  public List<Filter> getFilters(long projectId) {
    return getFiltersByType(projectId, null);
  }

  @SuppressWarnings("unchecked")
  private List<Filter> getFiltersByType(long projectId, DatumType filterType) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting Filters for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Filter.class);
    List<Filter> jdoResults;
    try {
      if (filterType == null) {
        jdoQuery.declareParameters("Long parentProjectParam");
        jdoQuery.setFilter("parentProjectId == parentProjectParam");
        jdoResults = (List<Filter>) jdoQuery.execute(projectId);
      } else {
        jdoQuery.declareParameters("Long parentProjectParam, DatumType filterTypeParam");
        jdoQuery.setFilter(
            "parentProjectId == parentProjectParam && filterType == filterTypeParam");
        jdoResults = (List<Filter>) jdoQuery.execute(projectId, filterType);
      }
      return ServletUtils.makeGwtSafe(jdoResults, pm);
    } finally {
      pm.close();
    }
  }

  @Override
  public long addFilter(Filter filter) {
    ServletUtils.requireAccess(userService.hasEditAccess(filter.getParentProjectId()));

    log.info("Adding filter for project: " + filter.getParentProjectId());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(filter);
    } finally {
      pm.close();
    }

    return filter.getId();
  }

  @Override
  public void updateFilter(Filter filter) {
    if (filter.getId() == null) {
        throw new IllegalArgumentException(
            "Filter has not been saved. Please call addFilter first.");
    }
    ServletUtils.requireAccess(userService.hasEditAccess(filter.getParentProjectId()));

    log.info("Updating filter: " + filter.getId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(filter);
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeFilter(Filter filter) {
    if (filter.getId() == null) {
      log.info("Attempting to delete unsaved Filter. Ignoring.");
      return;
    }

    ServletUtils.requireAccess(userService.hasEditAccess(filter.getParentProjectId()));

    log.info("Deleting filter: " + filter.getId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Filter filterToDelete = pm.getObjectById(Filter.class, filter.getId());
      pm.deletePersistent(filterToDelete);
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Bug> getProjectBugsById(long projectId) {
    return getProjectData(Bug.class, projectId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updateBugAssociations(long bugId, long attributeId, long componentId,
      long capabilityId) {
    updateAssociations(Bug.class, bugId, attributeId, componentId, capabilityId);
  }

  /**
   * Try to upload a new bug into the GAE datastore.  If a bug with the same Bug ID already exists,
   * it will be updated.  This is a friendly function; it will not throw if there's an error
   * adding the bug.
   */
  @Override
  public void addBug(Bug bug) {
    addBug(bug, userService.getEmail());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addBug(Bug bug, String asEmail) {
    log.info("Trying to add Bug: " + bug.getTitle() + " for project " + bug.getParentProjectId());
    ServletUtils.requireAccess(userService.hasEditAccess(bug.getParentProjectId(), asEmail));

    // Trim long fields.
    bug.setTitle(StringUtil.trimString(bug.getTitle()));
    saveOrUpdateDatum(bug);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Checkin> getProjectCheckinsById(long projectId) {
    return getProjectData(Checkin.class, projectId);
  }

  @Override
  public void addCheckin(Checkin checkin) {
    addCheckin(checkin, userService.getEmail());
  }

  /**
   * Try to upload a new Checkin into the GAE datastore.  If a Checkin with the same Checkin ID
   * already exists, it will be updated.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void addCheckin(Checkin checkin, String asEmail) {
    log.info("Trying to add Checkin: " + checkin.getSummary());
    ServletUtils.requireAccess(userService.hasEditAccess(checkin.getParentProjectId(), asEmail));

    checkin.setSummary(StringUtil.trimString(checkin.getSummary()));
    saveOrUpdateDatum(checkin);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updateCheckinAssociations(long checkinId, long attributeId, long componentId,
      long capabilityId) {
    updateAssociations(Checkin.class, checkinId, attributeId, componentId, capabilityId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<TestCase> getProjectTestCasesById(long projectId) {
    return getProjectData(TestCase.class, projectId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updateTestAssociations(long testCaseId, long attributeId, long componentId,
      long capabilityId) {
    updateAssociations(TestCase.class, testCaseId, attributeId, componentId, capabilityId);
  }

  @Override
  public void addTestCase(TestCase test) {
    addTestCase(test, userService.getEmail());
  }

  /**
   * Try to upload a new bug into the GAE datastore.  If a test case with the same ID already
   * exists, it will be updated.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void addTestCase(TestCase test, String asEmail) {
    log.info("Trying to add Test: " + test.getTitle());
    ServletUtils.requireAccess(userService.hasEditAccess(test.getParentProjectId(), asEmail));

    // Trim long fields.
    test.setTitle(StringUtil.trimString(test.getTitle()));
    saveOrUpdateDatum(test);
  }

  @SuppressWarnings("unchecked")
  private <T extends UploadedDatum> List<T> getProjectData(Class<T> clazz, long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));
    log.info("Getting data for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(clazz);
    jdoQuery.declareParameters("Long parentProjectParam");
    jdoQuery.setFilter("parentProjectId == parentProjectParam");
    jdoQuery.setOrdering("externalId asc");

    List<T> projectData = null;
    try {
      List<T> results = (List<T>) jdoQuery.execute(projectId);
      projectData = ServletUtils.makeGwtSafe(results, pm);
    } finally {
      pm.close();
    }
    return projectData;
  }

  /**
   * Saves a new datum, or updates an existing datum in the database if it already exists.
   *
   * @param datum
   */
  @SuppressWarnings("unchecked")
  private <T extends UploadedDatum> void saveOrUpdateDatum(T datum) {
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(datum.getClass());
    jdoQuery.declareParameters("Long parentProjectParam, Long externalIdParam");
    jdoQuery.setFilter("parentProjectId == parentProjectParam && externalId == externalIdParam");

    try {
      // There are two ways an existing item may exist: the same primary key, or the same
      // parent project ID and bug ID.
      T oldDatum = null;
      if (datum.getInternalId() != null) {
        // If it's the same primary key, make sure it makes sense to overwrite it.  The existing
        // bug should be non-null (we shouldn't have a pkey if it's unsaved already) and match
        // projects.
        oldDatum = (T) pm.getObjectById(datum.getClass(), datum.getInternalId());
        ServletUtils.requireAccess(oldDatum != null);
        ServletUtils.requireAccess(oldDatum.getParentProjectId() == datum.getParentProjectId());
      } else {
        // Try to load by a combination of project and external IDs.
        List<T> results = (List<T>)
            jdoQuery.execute(datum.getParentProjectId(), datum.getExternalId());
        if (results.size() > 0) {
          oldDatum = results.get(0);
        }
      }
      if (oldDatum != null) {
        datum.setInternalId(oldDatum.getInternalId());
        transferAssignments(oldDatum, datum);
      } else {
        applyFilters(datum);
      }
      pm.makePersistent(datum);
    } finally {
      pm.close();
    }
  }

  private <T extends UploadedDatum> void updateAssociations(Class<T> clazz, long internalId,
      long attributeId, long componentId,
      long capabilityId) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      T datum = pm.getObjectById(clazz, internalId);
      if (datum == null) {
        log.info("No results when querying for datum ID: " + internalId);
        return;
      }
      ServletUtils.requireAccess(userService.hasEditAccess(datum.getParentProjectId()));

      boolean updated = false;
      if (attributeId >= 0) {
        datum.setTargetAttributeId(attributeId == 0 ? null : attributeId);
        updated = true;
      }
      if (componentId >= 0) {
        datum.setTargetComponentId(componentId == 0 ? null : componentId);
        updated = true;
      }
      if (capabilityId >= 0) {
        datum.setTargetCapabilityId(capabilityId == 0 ? null : capabilityId);
        updated = true;
      }

      if (updated) {
        pm.makePersistent(datum);
      }
    } finally {
      pm.close();
    }
  }

  /**
   * Iff the old datum (from the database) has an assigned attribute, component, or capability
   * AND the new datum doesn't have an assignment, we copy the old assignment over.
   *
   * @param oldDatum the old item.
   * @param newDatum the new item; it will have the a/c/c set from the old item.
   */
  private void transferAssignments(UploadedDatum oldDatum, UploadedDatum newDatum) {
    if (positive(oldDatum.getTargetAttributeId()) && !positive(newDatum.getTargetAttributeId())) {
      newDatum.setTargetAttributeId(oldDatum.getTargetAttributeId());
    }

    if (positive(oldDatum.getTargetComponentId()) && !positive(newDatum.getTargetComponentId())) {
      newDatum.setTargetComponentId(oldDatum.getTargetComponentId());
    }

    if (positive(oldDatum.getTargetCapabilityId()) && !positive(newDatum.getTargetCapabilityId())) {
      newDatum.setTargetCapabilityId(oldDatum.getTargetCapabilityId());
    }
  }

  private void applyFilters(UploadedDatum item) {
    // TODO(jimr): This is a poor way to filter items... it's a stop-gap solution until the recently
    // announced Full Text Search is available for the AppEngine datastore.
    List<Filter> filters = getFiltersByType(item.getParentProjectId(), item.getDatumType());
    for (Filter filter : filters) {
      filter.apply(item);
    }
  }

  private boolean positive(Long value) {
    return value != null && value > 0;
  }
}
