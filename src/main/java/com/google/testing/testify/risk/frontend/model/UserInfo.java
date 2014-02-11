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

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Object for tracking user information. For example, starred/favorite projects.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@PersistenceCapable(detachable = "true")
public class UserInfo implements Serializable {

  @SuppressWarnings("unused")
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  /** The User ID is the same ID returned by the App Engine UserService. */
  @Persistent
  private String userId;

  /**
   * User's current email.  This should not be keyed off of as the user's
   * email might change but still remain the same User ID.
   * 
   * This can also be used to populate user id -- if user ID is null or missing, but email
   * is present, upon login the user ID will be populated.
   */
  @Persistent
  private String currentEmail;

  /** 
   * If a user is whitelisted, they will always have access to the application.  Some users
   * will not require to be whitelisted, if they are permitted by default (ie, @google.com).
   */
  @Persistent
  private Boolean isWhitelisted;

  /** List of IDs of starred projects. */
  @Persistent
  private List<Long> starredProjects = Lists.newArrayList();

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void starProject(long projectId) {
    starredProjects.add(projectId);
  }

  public void unstarProject(long projectId) {
    starredProjects.remove(projectId);
  }

  public void setStarredProjects(List<Long> starredProjects) {
    this.starredProjects = starredProjects;
  }

  public List<Long> getStarredProjects() {
    return starredProjects;
  }

  public void setIsWhitelisted(boolean isWhitelisted) {
    this.isWhitelisted = isWhitelisted;
  }

  public Boolean getIsWhitelisted() {
    return isWhitelisted == null ? false : isWhitelisted;
  }

  public void setCurrentEmail(String currentEmail) {
    this.currentEmail = currentEmail;
  }

  public String getCurrentEmail() {
    return currentEmail;
  }
}
