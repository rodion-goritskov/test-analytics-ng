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

import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Generalized representation of a code checkin.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@PersistenceCapable(detachable = "true")
public class Checkin implements Serializable, UploadedDatum {

  /** Unique identifier to store the checkin in App Engine. */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long internalId;

  /** Project ID of the project the checkin belongs to. */
  @Persistent
  private Long parentProjectId;

  /**
   * Checkin ID provided by the data provider. Must be unique across all checknis associated with
   * this project.
   */
  @Persistent
  private Long externalId;

  @Persistent
  private String summary;

  /**
   * Directories where files were touched by this checkin. E.g., if files:
   * ADD    alpha\beta\gamma\file1.txt
   * DELETE alpha\beta\gamma\file2.txt
   * EDIT   alpha\beta\delta\file3.txt
   *
   * The set of directories would be { "alpha\beta\gamma", "alpha\beta\delta" }.
   */
  @Persistent
  private Set<String> directoriesTouched = new HashSet<String>();

  /** URL to identify view more information about the checkin. */
  @Persistent
  private String changeUrl;

  /** Submitted, pending, etc. */
  @Persistent
  private String state;

  /** Date it entered current state. */
  @Persistent
  private Long stateDate;

  /** ID of the Attribute this checkin applies to, if any. */
  @Persistent
  private Long targetAttributeId;

  /** ID of the Component this checkin applies to, if any. */
  @Persistent
  private Long targetComponentId;

  /** ID of the Capability this checkin applies to, if any. */
  @Persistent
  private Long targetCapabilityId;

  @Override
  public void setInternalId(Long internalId) {
    this.internalId = internalId;
  }

  @Override
  public Long getInternalId() {
    return internalId;
  }

  @Override
  public void setParentProjectId(Long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  @Override
  public Long getParentProjectId() {
    return parentProjectId;
  }

  @Override
  public void setExternalId(Long externalId) {
    this.externalId = externalId;
  }

  @Override
  public Long getExternalId() {
    return externalId;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getSummary() {
    return summary;
  }

  public void setDirectoriesTouched(Set<String> directoriesTouched) {
    this.directoriesTouched = directoriesTouched;
  }

  public Set<String> getDirectoriesTouched() {
    return directoriesTouched;
  }

  /**
   * @return the directories the checkin has touched as a comma separated list.
   */
  public String getDirectoriesTouchedAsCommaSeparatedList() {
    return StringUtil.listToCsv(directoriesTouched);
  }

  public void addDirectoryTouched(String directory) {
    directoriesTouched.add(directory);
  }

  public void removeDirectoryTouched(String directory) {
    directoriesTouched.remove(directory);
  }

  public void setChangeUrl(String changeUrl) {
    this.changeUrl = changeUrl;
  }

  public String getChangeUrl() {
    return changeUrl;
  }

  @Override
  public void setTargetAttributeId(Long targetAttributeId) {
    this.targetAttributeId = targetAttributeId;
  }

  @Override
  public Long getTargetAttributeId() {
    return targetAttributeId;
  }

  @Override
  public void setTargetComponentId(Long targetComponentId) {
    this.targetComponentId = targetComponentId;
  }

  @Override
  public Long getTargetComponentId() {
    return targetComponentId;
  }

  @Override
  public void setTargetCapabilityId(Long targetCapabilityId) {
    this.targetCapabilityId = targetCapabilityId;
  }

  @Override
  public Long getTargetCapabilityId() {
    return targetCapabilityId;
  }

  @Override
  public String getLinkText() {
    if (externalId == null) {
      return "Checkin (no id)";
    }
    return ("Checkin #" + Long.toString(externalId));
  }

  @Override
  public String getLinkUrl() {
    return changeUrl;
  }

  @Override
  public String getToolTip() {
    StringBuilder text = new StringBuilder();
    text.append(summary);
    text.append("The following directories were touched: ");
    text.append(getDirectoriesTouchedAsCommaSeparatedList());
    return text.toString();
  }

  @Override
  public boolean isAttachedToAttribute() {
    return getTargetAttributeId() != null;
  }

  @Override
  public boolean isAttachedToCapability() {
    return getTargetCapabilityId() != null;
  }

  @Override
  public boolean isAttachedToComponent() {
    return getTargetComponentId() != null;
  }

  @Override
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public Long getStateDate() {
    return stateDate;
  }

  public void setStateDate(Long stateDate) {
    this.stateDate = stateDate;
  }

  @Override
  public DatumType getDatumType() {
    return DatumType.CHECKINS;
  }

  @Override
  public String getField(String field) {
    if ("Summary".equals(field)) {
      return summary;
    } else if ("Directories".equals(field)) {
      return getDirectoriesTouchedAsCommaSeparatedList();
    }
    return null;
  }
}
