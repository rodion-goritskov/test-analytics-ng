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
 * Generalized representation of a testcase to mitigate risk in a piece of software.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@PersistenceCapable(detachable = "true")
public class TestCase implements Serializable, UploadedDatum {

  /** Unique identifier for this test case. */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long internalId;

  /** Project ID of the project the test case belongs to. */
  @Persistent
  private Long parentProjectId;

  /**
   * Test case ID provided by the data provider. Must be unique across all bugs associated with this
   * project.
   */
  @Persistent
  private Long externalId;

  /** Arbitrary title of the test case. */
  @Persistent
  private String title;

  /**
   * Test cases have tags, which will be used to map it to Attributes, Components, Capabilities,
   * and so on.
   */
  @Persistent
  private Set<String> tags = new HashSet<String>();

  /** URL to identify view more information about the test case. */
  @Persistent
  private String testCaseUrl;

  /** ID of the Attribute this testcase applies to, if any. */
  @Persistent
  private Long targetAttributeId;

  /** ID of the Component this testcase applies to, if any. */
  @Persistent
  private Long targetComponentId;

  /** ID of the Capability this testcase applies to, if any. */
  @Persistent
  private Long targetCapabilityId;

  /** The status -- passed, failed, etc. */
  @Persistent
  private String state;

  /** Result date. */
  @Persistent
  private Long stateDate;

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
  public Long  getParentProjectId() {
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

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public void removeTag(String tag) {
    tags.remove(tag);
  }

  public void addTag(String tag) {
    tags.add(tag);
  }

  /**
   * @return the tags associated with this test case as a comma separated list.
   */
  public String getTagsAsCommaSeparatedList() {
    return StringUtil.listToCsv(tags);
  }

  public void setTestCaseUrl(String testCaseUrl) {
    this.testCaseUrl = testCaseUrl;
  }

  public String getTestCaseUrl() {
    return testCaseUrl;
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
    return title;
  }

  @Override
  public String getLinkUrl() {
    return testCaseUrl;
  }

  @Override
  public String getToolTip() {
    StringBuilder text = new StringBuilder();
    text.append("Last Result: ");
    text.append(state == null ? "n/a" : state);
    text.append(" This testcase is labeled with the following tags: ");
    text.append(getTagsAsCommaSeparatedList());
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
    return DatumType.TESTS;
  }

  @Override
  public String getField(String field) {
    if ("Title".equals(field)) {
      return title;
    } else if ("Labels".equals(field)) {
      return getTagsAsCommaSeparatedList();
    }
    return null;
  }
}
