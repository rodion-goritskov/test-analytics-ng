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
 * Generalized representation of a bug or defect in a piece of software.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@PersistenceCapable(detachable = "true")
public class Bug implements Serializable, UploadedDatum {

  /** Unique identifier to store the bug in App Engine. */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long internalId;

  /** Project ID of the project the bug belongs to. */
  @Persistent
  private Long parentProjectId;

  /**
   * Bug ID provided by the data provider. Must be unique across all bugs associated with this
   * project.
   */
  @Persistent
  private Long externalId;

  /** Bug severity. Sev 1 - severe, Sev 4 - ignorable.*/
  @Persistent
  private Long severity;

  /** Bug priority. Pri 1 - fix now, Pri 4 - puntable. */
  @Persistent
  private Long priority;

  /** Arbitrary title of the bug. */
  @Persistent
  private String title;

  /** Component path, or similar, of the bug. */
  @Persistent
  private String path;

  /** State, eg: Closed, Open, Active, etc **/
  @Persistent
  private String state;

  /** Date this bug was originally reported **/
  @Persistent
  private Long stateDate;

  /** Attribute this bug should be associated with (if any). */
  @Persistent
  private Long targetAttributeId;

  /** Component this bug should be associated with (if any). */
  @Persistent
  private Long targetComponentId;

  /** Capability this bug should be associated with (if any). */
  @Persistent
  private Long targetCapabilityId;

  /**
   * Bug group is a meta string to identify groups of bugs, typically for linking them to specific
   * components. For example, if the bug database is organized by component then the bug
   * group should be its path in the database.
   */
  @Persistent
  private Set<String> groups = new HashSet<String>();

  /** URL to identify view more information about the bug. */
  @Persistent
  private String bugUrl;

  @Override
  public void setParentProjectId(Long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  @Override
  public Long getParentProjectId() {
    return parentProjectId;
  }

  @Override
  public void setInternalId(Long internalId) {
    this.internalId = internalId;
  }

  /**
   * @return the bug's internal ID, which is unique across all projects.
   */
  @Override
  public Long getInternalId() {
    return internalId;
  }

  @Override
  public void setExternalId(Long externalId) {
    this.externalId = externalId;
  }

  /**
   * @return an arbitrary ID associated with the bug. (The bug ID in an external bug database.)
   */
  @Override
  public Long getExternalId() {
    return externalId;
  }

  public void setSeverity(Long severity) {
    this.severity = severity;
  }

  public Long getSeverity() {
    return severity;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  public Long getPriority() {
    return priority;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void addBugGroup(String groupName) {
    groups.add(groupName);
  }

  public void removeBugGroup(String groupName) {
    groups.remove(groupName);
  }

  public void setBugGroups(Set<String> bugGroups) {
    this.groups = bugGroups;
  }

  public Set<String> getBugGroups() {
    return groups;
  }

  /**
   * @return the group names associated with this bug as a comma separated list.
   */
  public String getGroupsAsCommaSeparatedList() {
    return StringUtil.listToCsv(groups);
  }

  public void setBugUrl(String bugUrl) {
    this.bugUrl = bugUrl;
  }

  public String getBugUrl() {
    return bugUrl;
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
    return title;
  }

  @Override
  public String getLinkUrl() {
    return bugUrl;
  }

  @Override
  public String getToolTip() {
    StringBuilder text = new StringBuilder();
    text.append("This bug is attached to the following groups: ");
    text.append(this.getGroupsAsCommaSeparatedList());
    return text.toString();
  }

  @Override
  public boolean isAttachedToAttribute() {
    return targetAttributeId != null;
  }

  @Override
  public boolean isAttachedToComponent() {
    return targetComponentId != null;
  }

  @Override
  public boolean isAttachedToCapability() {
    return targetCapabilityId != null;
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
  public Long getStateDate() {
    return stateDate;
  }

  public void setStateDate(Long stateDate) {
    this.stateDate = stateDate;
  }

  @Override
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public DatumType getDatumType() {
    return DatumType.BUGS;
  }

  @Override
  public String getField(String field) {
    if ("Title".equals(field)) {
      return title;
    } else if ("Path".equals(field)) {
      return path;
    } else if ("Labels".equals(field)) {
      return getGroupsAsCommaSeparatedList();
    }
    return null;
  }
}
