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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.AccElementType;
import com.google.testing.testify.risk.frontend.model.AccLabel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.HasLabels;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.util.ServletUtils;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;
import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * This acts as a broker for the client when accessing project information, such as Attributes and
 * Components. Any operation will be checked using the user's current credentials, and fail if the
 * user doesn't have access to view or edit any project information.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@Singleton
public class ProjectServiceImpl implements ProjectService {
  private static final Logger log = Logger.getLogger(ProjectServiceImpl.class.getName());
  private final PersistenceManagerFactory pmf;
  private final UserService userService;

  /**
   * Creates a new ProjectServiceImpl instance. Internally all methods will use the
   * PersistanceManager associated with JDOHelper.getPersistenceManagerFactory (meaning
   * jdoconfig.xml).
   */
  @Inject
  public ProjectServiceImpl(PersistenceManagerFactory pmf, UserService userService) {
    this.pmf = pmf;
    this.userService = userService;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Project> query(String query) {
    log.info("Querying: " + query);
    PersistenceManager pm = pmf.getPersistenceManager();

    // TODO(jimr): this currently does not do a query, it just returns all public projects.
    Query jdoQuery = pm.newQuery(Project.class);
    jdoQuery.setOrdering("projectId asc");

    List<Project> results = null;
    try {
      List<Project> returnedProjects = (List<Project>) jdoQuery.execute();
      List<Project> safeToDisplayProjs = Lists.newArrayList();

      for (Project returnedProject : returnedProjects) {
        if (userService.hasViewAccess(returnedProject)) {
          safeToDisplayProjs.add(returnedProject);
        }
      }

      results = ServletUtils.makeGwtSafe(safeToDisplayProjs, pm);
      populateCachedAccess(results);
    } finally {
      pm.close();
    }

    return results;
  }

  private void populateCachedAccess(List<Project> projects) {
    for (Project p : projects) {
      populateCachedAccess(p);
    }
  }

  private void populateCachedAccess(Project p) {
    p.setCachedAccessLevel(userService.getAccessLevel(p));
  }

  /**
   * Retrieves the list of projects relevant to the currently logged in user.
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Project> queryUserProjects() {
    if (!userService.isUserLoggedIn()) {
      log.info("Querying user projects; user is not logged in.");
      return Lists.newArrayList();
    }

    log.info("Querying user projects.");
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Project.class);
    jdoQuery.setOrdering("projectId asc");

    List<Project> results = null;
    try {
      List<Long> starredProjects = userService.getStarredProjects();

      List<Project> returnedProjects = (List<Project>) jdoQuery.execute();
      List<Project> projectsToReturn = Lists.newArrayList();

      for (Project returnedProject : returnedProjects) {
        // Only returns the projects that are either:
        // * Starred by the user and have access (implicit or explicit).
        // * Granted VIEW or EDIT access explicitly. (Ignore public projects.)
        ProjectAccess access = userService.getAccessLevel(returnedProject);

        // If explicit, add it. Otherwise, check starred access (and implicit access).
        if (access.hasAccess(ProjectAccess.EXPLICIT_VIEW_ACCESS)) {
          projectsToReturn.add(returnedProject);
        } else if (access.hasAccess(ProjectAccess.VIEW_ACCESS)
            && starredProjects.contains(returnedProject.getProjectId())) {
          projectsToReturn.add(returnedProject);
        }
      }

      results = ServletUtils.makeGwtSafe(projectsToReturn, pm);
      populateCachedAccess(results);
    } finally {
      pm.close();
    }

    return results;
  }

  /**
   * Returns the list of projects the currently logged in user has EDIT access to.
   */
  @Override
  public List<Project> queryProjectsUserHasEditAccessTo() {
    ServletUtils.requireAccess(userService.isUserLoggedIn());

    // Start with all starred projects and those granting explicit access.
    List<Project> projects = queryUserProjects();
    List<Project> projectsToReturn = Lists.newArrayList();

    for (Project project : projects) {
      if (userService.hasEditAccess(project)) {
        projectsToReturn.add(project);
      }
    }

    populateCachedAccess(projectsToReturn);
    return projectsToReturn;
  }

  @Override
  public Project getProjectById(long id) {
    log.info("Getting project: " + Long.toString(id));

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Project retrievedProject = pm.getObjectById(Project.class, id);
      // Don't disclose that the project even exists if they don't have view access.
      if (retrievedProject != null && userService.hasViewAccess(retrievedProject)) {
        retrievedProject = ServletUtils.makeGwtSafe(retrievedProject, pm);
        populateCachedAccess(retrievedProject);
        return retrievedProject;
      }
    } finally {
      pm.close();
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Project getProjectByName(String name) {
    log.info("Getting project with name: " + name);

    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Project.class);
    jdoQuery.declareParameters("String projectNameParam");
    jdoQuery.setFilter("name == projectNameParam");

    try {
      List<Project> returnedProjects = (List<Project>) jdoQuery.execute(name);
      log.info(String.format("Found %s projects with name %s", returnedProjects.size(), name));

      for (Project target : returnedProjects) {
        if (userService.hasViewAccess(target)) {
          target = ServletUtils.makeGwtSafe(target, pm);
          populateCachedAccess(target);
          return target;
        }
      }
    } finally {
      pm.close();
    }
    return null;
  }

  @Override
  public Long createProject(Project projInfo) {
    // The user must be logged in to create a project.
    ServletUtils.requireAccess(userService.isUserLoggedIn());
    log.info("Creating new Project with name: " + projInfo.getName());

    if (projInfo.getProjectId() != null) {
      throw new IllegalArgumentException(
          "You can only create a project with a null ID.");
    }

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      // Automatically add the current user as an OWNER for the project.
      projInfo.addProjectOwner(userService.getEmail());
      pm.makePersistent(projInfo);
    } finally {
      pm.close();
    }

    return projInfo.getProjectId();
  }

  @Override
  public void updateProject(Project projInfo) {
    if (projInfo.getProjectId() == null) {
      throw new IllegalArgumentException(
          "projInfo has not been saved. Please call createProject first.");
    }
    ServletUtils.requireAccess(userService.hasEditAccess(projInfo.getProjectId()));

    log.info("Updating Project: " + projInfo.getProjectId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    Project oldProject;
    try {
      oldProject = pm.getObjectById(Project.class, projInfo.getProjectId());
      List<String> oldOwners = oldProject.getProjectOwners();
      List<String> oldEditors = oldProject.getProjectEditors();
      List<String> oldViewers = oldProject.getProjectViewers();

      // If they're not an owner, keep the old list of owners around.
      if (!userService.hasOwnerAccess(projInfo.getProjectId())) {
        projInfo.setIsPubliclyVisible(oldProject.getIsPubliclyVisible());
        projInfo.setProjectOwners(oldProject.getProjectOwners());
      }

      pm.makePersistent(projInfo);

      log.info("Notifying users of any changes to access level");
      String from = userService.getEmail();
      List<String> added = StringUtil.subtractList(projInfo.getProjectOwners(),
          oldOwners);
      if (added.size() > 0) {
        ServletUtils.notifyAddedAccess(from, added, "owner", projInfo.getName(),
            projInfo.getProjectId().toString());
      }
      List<String> removed = StringUtil.subtractList(oldOwners, projInfo.getProjectOwners());
      if (removed.size() > 0) {
          ServletUtils.notifyRemovedAccess(from, removed, "owner", projInfo.getName(),
            projInfo.getProjectId().toString());
      }

      added = StringUtil.subtractList(projInfo.getProjectEditors(), oldEditors);
      if (added.size() > 0) {
        ServletUtils.notifyAddedAccess(from, added, "editor", projInfo.getName(),
            projInfo.getProjectId().toString());
      }
      removed = StringUtil.subtractList(oldEditors, projInfo.getProjectEditors());
      if (removed.size() > 0) {
        ServletUtils.notifyRemovedAccess(from, removed, "editor", projInfo.getName(),
            projInfo.getProjectId().toString());
      }

      added = StringUtil.subtractList(projInfo.getProjectViewers(), oldViewers);
      if (added.size() > 0) {
        ServletUtils.notifyAddedAccess(from, added, "viewer", projInfo.getName(),
            projInfo.getProjectId().toString());
      }
      removed = StringUtil.subtractList(oldViewers, projInfo.getProjectViewers());
      if (removed.size() > 0) {
        ServletUtils.notifyRemovedAccess(from, removed, "viewer", projInfo.getName(),
            projInfo.getProjectId().toString());
      }
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeProject(Project projInfo) {
    if (projInfo.getProjectId() == null) {
      log.info("Attempting to delete unsaved project. Ignoring.");
      return;
    }
    ServletUtils.requireAccess(userService.hasOwnerAccess(projInfo.getProjectId()));

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Project projToDelete = pm.getObjectById(Project.class, projInfo.getProjectId());

      // TODO(jimr): This delete could succeed but successive removes might fail, leaving dangling
      // data.  We should retry or do something to assure complete destruction.

      // TODO(jimr): Undo?
      pm.deletePersistent(projToDelete);

      // Delete any child attributes, components, or capabilities.
      removeObjectsWithFieldValue(pm, Attribute.class, "parentProjectId", projInfo.getProjectId());
      removeObjectsWithFieldValue(pm, Component.class, "parentProjectId", projInfo.getProjectId());
      removeObjectsWithFieldValue(pm, Capability.class, "parentProjectId", projInfo.getProjectId());

      // TODO(chrsmith): What about enabled data providers? For example, bugs or checkin data
      // associated with this project? We need to clear those out as well.
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<AccLabel> getLabels(long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting labels for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(AccLabel.class);
    jdoQuery.setFilter("projectId == projectIdParam");
    jdoQuery.declareParameters("Long projectIdParam");

    try {
      List<AccLabel> labels = (List<AccLabel>) jdoQuery.execute(projectId);
      return ServletUtils.makeGwtSafe(labels, pm);
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Attribute> getProjectAttributes(long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting Attributes for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Attribute.class);
    jdoQuery.setFilter("parentProjectId == parentProjectParam");
    jdoQuery.setOrdering("displayOrder asc");
    jdoQuery.declareParameters("Long parentProjectParam");

    try {
      List<Attribute> returnedAttributes = (List<Attribute>) jdoQuery.execute(projectId);
      returnedAttributes = ServletUtils.makeGwtSafe(returnedAttributes, pm);
      populateLabels(returnedAttributes, pm);
      return returnedAttributes;
    } finally {
      pm.close();
    }
  }

  /**
   * Populates labels for a HasLabels item.
   *
   * @param <T> An object that HasLabels.
   * @param item The item to get labels for.
   * @param pm A persistence manager object.
   */
  private <T extends HasLabels> void populateLabels(T item, PersistenceManager pm) {
    item.setAccLabels(getLabelsForItem(item, pm));
  }

  /**
   * Populates labels for a list of HasLabels items.  All items MUST be for the same project
   * and of the same type (ie, all Attributes).
   *
   * IMPORTANT: you probably want to detach your object before calling this.
   *
   * @param <T> An object that HasLabels.
   * @param items The items to get labels for.
   * @param pm A persistence manager object.
   */
  @SuppressWarnings("unchecked")
  private <T extends HasLabels> void populateLabels(List<T> items, PersistenceManager pm) {
    if (items.size() > 0) {
      Map<Long, T> idToItem = Maps.newHashMap();
      for (T item : items) {
        idToItem.put(item.getId(), item);
      }
      T item = items.get(0);
      AccElementType type = item.getElementType();
      Long parentProjectId = item.getParentProjectId();
      Query query = pm.newQuery(AccLabel.class);
      query.declareParameters("AccElementType elementTypeParam, Long projectIdParam");
      query.setFilter("elementType == elementTypeParam && projectId == projectIdParam");
      List<AccLabel> labels = (List<AccLabel>) query.execute(type, parentProjectId);
      log.info("Found labels: " + labels.size());

      for (AccLabel label : labels) {
        item = idToItem.get(label.getElementId());
        if (item != null) {
          log.info("Putting label where it belongs: " + label.getLabelText());
          item.addLabel(label);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends HasLabels> List<AccLabel> getLabelsForItem(T item, PersistenceManager pm) {
    Query query = pm.newQuery(AccLabel.class);
    query.declareParameters(
        "AccElementType elementTypeParam, Long elementIdParam, Long projectIdParam");
    query.setFilter("elementType == elementTypeParam && elementId == elementIdParam && "
        + "projectId == projectIdParam");
    List<AccLabel> labels = (List<AccLabel>) query.execute(item.getElementType(), item.getId(),
        item.getParentProjectId());
    // This constructs a new, real list -- the list returned by the above execution is a lazy-loaded
    // streaming query.  That doesn't serialize.
    return Lists.newArrayList(labels);
  }

  /**
   * Saves labels for an item.
   *
   * This is not extremely necessary, but for simplicity ('always call persistLabels') it's here.
   *
   * @param <T> An object that HasLabels.
   * @param item The item to save the labels for.
   * @param pm A persistence mananger.
   */
  private <T extends HasLabels> void persistLabels(T item, PersistenceManager pm) {
    List<AccLabel> toDelete = Lists.newArrayList();
    Set<String> newLabelKeys = Sets.newHashSet();
    for (AccLabel label : item.getAccLabels()) {
      if (label.getId() != null) {
        newLabelKeys.add(label.getId());
      }
    }

    List<AccLabel> currentLabels = getLabelsForItem(item, pm);
    for (AccLabel label : currentLabels) {
      if (!newLabelKeys.contains(label.getId())) {
        toDelete.add(label);
      }
    }
    pm.deletePersistentAll(toDelete);
    // And add the new ones.
    pm.makePersistentAll(item.getAccLabels());
  }


  private <T extends HasLabels> void updateLabelElementIds(T item, Long id) {
    for (AccLabel label : item.getAccLabels()) {
      label.setElementId(id);
    }
  }

  private <T extends HasLabels> void deleteLabels(T item, PersistenceManager pm) {
    pm.deletePersistentAll(item.getAccLabels());
  }

  @Override
  public Long createAttribute(Attribute attribute) {
    ServletUtils.requireAccess(userService.hasEditAccess(attribute.getParentProjectId()));

    if (attribute.getAttributeId() != null) {
      throw new IllegalArgumentException(
          "You can only create an attribute with a null ID.");
    }

    log.info("Creating new Attribute with name: " + attribute.getName());
    attribute.setName(attribute.getName().trim());

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(attribute);

      // If we have labels, update their element ID now that we have an ID.
      if (attribute.getAccLabels().size() > 0) {
        pm.close();
        updateLabelElementIds(attribute, attribute.getAttributeId());
        pm = pmf.getPersistenceManager();
        persistLabels(attribute, pm);
      }
    } finally {
      pm.close();
    }

    return attribute.getAttributeId();
  }

  @Override
  public Attribute updateAttribute(Attribute attribute) {
    if (attribute.getAttributeId() == null) {
      throw new IllegalArgumentException(
          "attribute has not been saved. Please call createAttribute first.");
    }

    // Make sure they can edit the project into which they are trying to save.
    ServletUtils.requireAccess(userService.hasEditAccess(attribute.getParentProjectId()));

    log.info("Updating Attribute: " + attribute.getAttributeId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Attribute oldAttribute = pm.getObjectById(Attribute.class, attribute.getAttributeId());
      if (oldAttribute.getParentProjectId() != attribute.getParentProjectId()) {
        log.severe("Possible attack -- attribute sent in and attribute being overwritten had"
            + " different project IDs.");
        ServletUtils.requireAccess(false);
      }

      persistLabels(attribute, pm);
      pm.makePersistent(attribute);
      attribute = ServletUtils.makeGwtSafe(attribute, pm);
      populateLabels(attribute, pm);
      attribute.setAccLabels(ServletUtils.makeGwtSafe(attribute.getAccLabels(), pm));
      return attribute;
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeAttribute(Attribute attribute) {
    if (attribute.getAttributeId() == null) {
      log.info("Attempting to delete unsaved Attribute. Ignoring.");
      return;
    }

    log.info("Removing Attribute: " + attribute.getAttributeId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Attribute attributeToDelete = pm.getObjectById(Attribute.class, attribute.getAttributeId());
      ServletUtils.requireAccess(userService.hasEditAccess(attributeToDelete.getParentProjectId()));

      pm.deletePersistent(attributeToDelete);
      deleteLabels(attribute, pm);

      // Delete any child capabilities.
      removeObjectsWithFieldValue(pm, Capability.class, "attributeId", attribute.getAttributeId());
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void reorderAttributes(long projectId, List<Long> newOrdering) {
    log.info("Reordering Attributes for project: " + Long.toString(projectId));
    Function<Attribute, Long> getId = new Function<Attribute, Long>() {
      @Override
      public Long apply(Attribute input) {
        return input.getAttributeId();
      }
    };
    Setter<Attribute> setId = new Setter<Attribute>() {
      @Override
      public boolean apply(Attribute input, long value) {
        if (input.getDisplayOrder() != value) {
          input.setDisplayOrder(value);
          return true;
        }
        return false;
      }
    };
    setOrder(projectId, Attribute.class, newOrdering, getId, setId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Component> getProjectComponents(long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting Components for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Component.class);
    jdoQuery.setFilter("parentProjectId == parentProjectParam");
    jdoQuery.setOrdering("displayOrder asc");
    jdoQuery.declareParameters("Long parentProjectParam");

    try {
      List<Component> returnedComponents = (List<Component>) jdoQuery.execute(projectId);
      returnedComponents = ServletUtils.makeGwtSafe(returnedComponents, pm);
      populateLabels(returnedComponents, pm);
      return returnedComponents;
    } finally {
      pm.close();
    }
  }

  @Override
  public Long createComponent(Component component) {
    ServletUtils.requireAccess(userService.hasEditAccess(component.getParentProjectId()));

    if (component.getComponentId() != null) {
      throw new IllegalArgumentException(
          "You can only create a component with a null ID.");
    }

    log.info("Creating new Component with name: " + component.getName());
    component.setName(component.getName().trim());

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(component);

      // If we have labels, update their element ID now that we have an ID.
      if (component.getAccLabels().size() > 0) {
        pm.close();
        updateLabelElementIds(component, component.getId());
        pm = pmf.getPersistenceManager();
        persistLabels(component, pm);
      }
    } finally {
      pm.close();
    }

    return component.getComponentId();
  }

  @Override
  public Component updateComponent(Component component) {
    if (component.getComponentId() == null) {
      throw new IllegalArgumentException(
          "component has not been saved. Please call createComponent first.");
    }

    // Make sure they can edit the project into which they are trying to save.
    ServletUtils.requireAccess(userService.hasEditAccess(component.getParentProjectId()));

    log.info("Updating Component: " + component.getComponentId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Component oldComponent = pm.getObjectById(Component.class, component.getComponentId());
      if (oldComponent.getParentProjectId() != component.getParentProjectId()) {
        log.severe("Possible attack -- component sent in and component being overwritten had"
            + " different project IDs.");
        ServletUtils.requireAccess(false);
      }

      persistLabels(component, pm);
      pm.makePersistent(component);
      component = ServletUtils.makeGwtSafe(component, pm);
      populateLabels(component, pm);
      component.setAccLabels(ServletUtils.makeGwtSafe(component.getAccLabels(), pm));
      return component;
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeComponent(Component component) {
    if (component.getComponentId() == null) {
      log.info("Attempting to delete unsaved Component. Ignoring.");
      return;
    }

    log.info("Removing Component: " + component.getComponentId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Component componentToDelete = pm.getObjectById(Component.class, component.getComponentId());
      ServletUtils.requireAccess(userService.hasEditAccess(componentToDelete.getParentProjectId()));

      pm.deletePersistent(componentToDelete);
      deleteLabels(componentToDelete, pm);

      // Delete any child capabilities.
      removeObjectsWithFieldValue(pm, Capability.class, "componentId", component.getComponentId());
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> void setOrder(long projectId, Class<T> clazz, List<Long> order,
      Function<T, Long> getId, Setter<T> applyOrder) {
    log.info("setOrder executing with " + order.size() + " items passed in.");
    ServletUtils.requireAccess(userService.hasEditAccess(projectId));

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Query jdoQuery = pm.newQuery(clazz);
      jdoQuery.setFilter("parentProjectId == parentProjectParam");
      jdoQuery.declareParameters("Long parentProjectParam");
      List<T> items = (List<T>) jdoQuery.execute(projectId);

      Map<Long, Integer> lookup = Maps.newHashMap();
      for (int i = 0; i < order.size(); i++) {
        Long id = order.get(i);
        lookup.put(id, i);
      }
      for (T item : items) {
        Long id = getId.apply(item);
        Integer newIndex = lookup.get(id);
        if (newIndex != null) {
          if (applyOrder.apply(item, newIndex)) {
            pm.makePersistent(item);
          }
        } else {
          log.warning("Project contains item not covered in new ordering - ID: " + id);
        }
      }
    } finally {
      pm.close();
    }
    log.info("setOrder complete");
  }

  @Override
  public void reorderCapabilities(long projectId, List<Long> newOrdering) {
    log.info("Reordering capabilities for project: " + Long.toString(projectId));
    Function<Capability, Long> getId = new Function<Capability, Long>() {
      @Override
      public Long apply(Capability input) {
        return input.getCapabilityId();
      }
    };
    Setter<Capability> setId = new Setter<Capability>() {
      @Override
      public boolean apply(Capability input, long value) {
        if (input.getDisplayOrder() != value) {
          input.setDisplayOrder(value);
          return true;
        }
        return false;
      }
    };
    setOrder(projectId, Capability.class, newOrdering, getId, setId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void reorderComponents(long projectId, List<Long> newOrdering) {
    log.info("Reordering Components for project: " + Long.toString(projectId));
    Function<Component, Long> getId = new Function<Component, Long>() {
      @Override
      public Long apply(Component input) {
        return input.getComponentId();
      }
    };
    Setter<Component> setId = new Setter<Component>() {
      @Override
      public boolean apply(Component input, long value) {
        if (input.getDisplayOrder() != value) {
          input.setDisplayOrder(value);
          return true;
        }
        return false;
      }
    };
    setOrder(projectId, Component.class, newOrdering, getId, setId);
  }

  private interface Setter<F> {
    boolean apply(F input, long value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Capability getCapabilityById(long projectId, long capabilityId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));
    log.info("Getting capability for project by id: " + Long.toString(projectId) + ", "
        + Long.toString(capabilityId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Capability.class);
    jdoQuery.setFilter(
        "parentProjectId == parentProjectParam && capabilityId == capabilityIdParam");
    jdoQuery.declareParameters("Long parentProjectParam, Long capabilityIdParam");
    try {
      List<Capability> results = (List<Capability>) jdoQuery.execute(
          projectId, capabilityId);
      if (results.size() > 0) {
        Capability c = results.get(0);
        c = ServletUtils.makeGwtSafe(c, pm);
        populateLabels(c, pm);
        return c;
      } else {
        return null;
      }
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Capability> getProjectCapabilities(long projectId) {
    ServletUtils.requireAccess(userService.hasViewAccess(projectId));

    log.info("Getting Capabilities for project: " + Long.toString(projectId));
    PersistenceManager pm = pmf.getPersistenceManager();

    Query jdoQuery = pm.newQuery(Capability.class);
    jdoQuery.setFilter("parentProjectId == parentProjectParam");
    jdoQuery.setOrdering("displayOrder asc");
    jdoQuery.declareParameters("Long parentProjectParam");

    try {
      List<Capability> returnedCapabilities = (List<Capability>) jdoQuery.execute(projectId);
      returnedCapabilities = ServletUtils.makeGwtSafe(returnedCapabilities, pm);
      populateLabels(returnedCapabilities, pm);
      return returnedCapabilities;
    } finally {
      pm.close();
    }
  }

  @Override
  public Capability createCapability(Capability capability) {
    ServletUtils.requireAccess(userService.hasEditAccess(capability.getParentProjectId()));

    if (capability.getCapabilityId() != null) {
      throw new IllegalArgumentException(
          "You can only create a capability with a null ID.");
    }

    log.info("Creating new Capability with name: " + capability.getName());
    capability.setName(capability.getName().trim());

    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      pm.makePersistent(capability);
      capability = ServletUtils.makeGwtSafe(capability, pm);

      // If we have labels, update their element ID now that we have an ID.
      if (capability.getAccLabels().size() > 0) {
        pm.close();
        updateLabelElementIds(capability, capability.getId());
        pm = pmf.getPersistenceManager();
        persistLabels(capability, pm);
      }
      return capability;
    } finally {
      pm.close();
    }
  }

  @Override
  public void updateCapability(Capability capability) {
    if (capability.getCapabilityId() == null) {
      throw new IllegalArgumentException(
          "Capability has not been saved. Please call createCapability first.");
    }

    // Make sure they can edit the project into which they are trying to save.
    ServletUtils.requireAccess(userService.hasEditAccess(capability.getParentProjectId()));

    log.info("Updating capability: " + capability.getCapabilityId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Capability oldCapability = pm.getObjectById(Capability.class, capability.getCapabilityId());
      if (oldCapability.getParentProjectId() != capability.getParentProjectId()) {
        log.severe("Possible attack -- capability sent in and capability being overwritten had"
            + " different project IDs.");
        ServletUtils.requireAccess(false);
      }

      pm.makePersistent(capability);
      persistLabels(capability, pm);
    } finally {
      pm.close();
    }
  }

  @Override
  public void removeCapability(Capability capability) {
    if (capability.getCapabilityId() == null) {
      log.info("Attempting to delete unsaved Component. Ignoring.");
      return;
    }

    log.info("Removing Capability: " + capability.getCapabilityId().toString());
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Capability capabilityToDelete = pm.getObjectById(
                                          Capability.class, capability.getCapabilityId());
      ServletUtils.requireAccess(userService.hasEditAccess(
          capabilityToDelete.getParentProjectId()));
      pm.deletePersistent(capabilityToDelete);
      deleteLabels(capabilityToDelete, pm);
    } finally {
      pm.close();
    }
  }

  /**
   * Deletes all stored objects of the given type with the field matching the specified ID.
   * Primarily this is used for deleting dependent, child objects when the parent is removed.
   *
   * @param pm the PersistenceManager used to perform the deletion operation.
   * @param type the type of the object to be removed.
   * @param fieldName the name on the object to check.
   * @param fieldValue the value which must match in order for the object to get deleted.
   */
  private <T> void removeObjectsWithFieldValue(
      PersistenceManager pm, Class<T> type, String fieldName, long fieldValue)
      throws IllegalArgumentException {

    // Do a brief sanity check on the field name.
    fieldName = fieldName.trim();
    if ((fieldName.contains(" ")) || (fieldName.contains(";"))) {
      throw new IllegalArgumentException();
    }

    StringBuilder message = new StringBuilder();
    message.append("Deleting all ");
    message.append(type.getCanonicalName());
    message.append(" with '");
    message.append(fieldName);
    message.append("' equal to ");
    message.append(Long.toString(fieldValue));

    log.info(message.toString());

    Query objectsToDeleteQuery = pm.newQuery(type);
    objectsToDeleteQuery.setFilter(fieldName + " == fieldValueParam");
    objectsToDeleteQuery.declareParameters("int fieldValueParam");
    objectsToDeleteQuery.deletePersistentAll(fieldValue);
  }
}
