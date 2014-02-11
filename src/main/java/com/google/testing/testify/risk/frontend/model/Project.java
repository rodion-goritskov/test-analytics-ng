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


package com.google.testing.testify.risk.frontend.model;

import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO object for storing Project data.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class Project implements Serializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long projectId;

  @Persistent
  private String name;

  /**
   * Full paragraph or page-length description of the project.
   */
  @Persistent
  private String description;

  /**
   * This field controls whether or not the project is listed in public queries.
   */
  @Persistent
  private Boolean isPubliclyVisible = false;

  /**
   * Email addresses of users with owner access.  They can do anything editors can do as well as
   * delete the project..
   */
  @Persistent
  private List<String> projectOwners = new ArrayList<String>();

  /**
   * Email addresses of users with editor access.  They can add, edit, or update project data.
   */
  @Persistent
  private List<String> projectEditors = new ArrayList<String>();

  /**
   * Email addresses of users with view access.  They can view but not edit project data.
   */
  @Persistent
  private List<String> projectViewers = new ArrayList<String>();

  /**
   * Allows the server-side to set the permissions so UI code has easy access without executing
   * an RPC.  Server-side code should never rely on this value.
   */
  @NotPersistent
  private ProjectAccess cachedAccessLevel;

  /**
   * Returns the Project's ID. Note that this will return null in the case the project hasn't been
   * persisted in the backing store (and doesn't have an ID yet).
   */
  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIsPubliclyVisible(Boolean isPubliclyVisible) {
    this.isPubliclyVisible = isPubliclyVisible;
  }

  public boolean getIsPubliclyVisible() {
    return isPubliclyVisible;
  }

  public void addProjectOwner(String ownerEmailAddress) {
    if (!projectOwners.contains(ownerEmailAddress)) {
      projectOwners.add(ownerEmailAddress);
    }
  }

  public void removeProjectOwner(String ownerEmailAddress) {
    projectOwners.remove(ownerEmailAddress);
  }

  public void setProjectOwners(List<String> projectOwners) {
    this.projectOwners = projectOwners;
  }

  public List<String> getProjectOwners() {
    return projectOwners;
  }

  public void addProjectEditor(String editorEmailAddress) {
    if (!projectEditors.contains(editorEmailAddress)) {
      projectEditors.add(editorEmailAddress);
    }
  }

  public void removeProjectEditor(String editorEmailAddress) {
    projectEditors.remove(editorEmailAddress);
  }

  public void setProjectEditors(List<String> projectEditors) {
    this.projectEditors = projectEditors;
  }

  public List<String> getProjectEditors() {
    return projectEditors;
  }

  public void addProjectView(String viewEmailAddress) {
    if (!projectViewers.contains(viewEmailAddress)) {
      projectViewers.add(viewEmailAddress);
    }
  }

  public void removeProjectViewer(String viewerEmailAddress) {
    projectViewers.remove(viewerEmailAddress);
  }

  public void setProjectViewers(List<String> projectViewers) {
    this.projectViewers = projectViewers;
  }

  public List<String> getProjectViewers() {
    return projectViewers;
  }

  public void setCachedAccessLevel(ProjectAccess cachedAccessLevel) {
    this.cachedAccessLevel = cachedAccessLevel;
  }

  public ProjectAccess getCachedAccessLevel() {
    return cachedAccessLevel;
  }
}
