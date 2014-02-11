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


package com.google.testing.testify.risk.frontend.model;

import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.model.DatumType;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * A representation of a filter to automatically assign data to ACC parts.
 *
 * A Filter will automatically assign data uploaded to specific ACC pieces.  For example,
 * a Filter may say "assign any test labeled with 'Security' to the Security Attribute.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class Filter implements Serializable {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private long parentProjectId;

  @Persistent
  private DatumType filterType;

  @Persistent
  private String filterConjunction;

  @Persistent(mappedBy = "filter", defaultFetchGroup = "true")
  private List<FilterOption> filterOptions = Lists.newArrayList();

  @Persistent
  private Long targetAttributeId;

  @Persistent
  private Long targetComponentId;

  @Persistent
  private Long targetCapabilityId;

  /**
   * Creates a friendly title for this filter.
   * @return a title.
   */
  public String getTitle() {
    return filterType.getSingular() + " Filter";
  }

  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getParentProjectId() {
    return parentProjectId;
  }

  public void setParentProjectId(long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public Long getTargetAttributeId() {
    return targetAttributeId;
  }

  public void setTargetAttributeId(Long targetAttributeId) {
    this.targetAttributeId = targetAttributeId;
  }

  public Long getTargetComponentId() {
    return targetComponentId;
  }

  public void setTargetComponentId(Long targetComponentId) {
    this.targetComponentId = targetComponentId;
  }

  public Long getTargetCapabilityId() {
    return targetCapabilityId;
  }

  public void setTargetCapabilityId(Long targetCapabilityId) {
    this.targetCapabilityId = targetCapabilityId;
  }

  public DatumType getFilterType() {
    return filterType;
  }

  public void setFilterType(DatumType filterType) {
    this.filterType = filterType;
  }

  public List<FilterOption> getFilterOptions() {
    return filterOptions;
  }

  public void setFilterOptions(List<FilterOption> filterOptions) {
    this.filterOptions = filterOptions;
  }

  public void addFilterOption(String field, String value) {
    filterOptions.add(new FilterOption(field, value));
  }

  public String getFilterConjunction() {
    return filterConjunction;
  }

  public void setFilterConjunction(String filterConjunction) {
    this.filterConjunction = filterConjunction;
  }

  public void apply(UploadedDatum item) {
    if (item.getDatumType() != filterType) {
      throw new IllegalArgumentException("Data types do not match; I filter "
          + filterType.getPlural() + " but received a " + item.getDatumType().getSingular());
    }
    if (filterOptions.size() < 1 ||
        (targetAttributeId == null && targetCapabilityId == null && targetComponentId == null)) {
      return;
    }

    boolean matchesAny = false;
    boolean matchesAll = true;
    for (FilterOption option : filterOptions) {
      String value = item.getField(option.getType());
      if (value != null) {
        if (value.contains(option.getValue())) {
          matchesAny = true;
        } else {
          matchesAll = false;
        }
      }
    }

    if (matchesAll || ("any".equals(filterConjunction) && matchesAny)) {
      if (targetAttributeId != null) {
        item.setTargetAttributeId(targetAttributeId);
      }
      if (targetComponentId != null) {
        item.setTargetComponentId(targetComponentId);
      }
      if (targetCapabilityId != null) {
        item.setTargetCapabilityId(targetCapabilityId);
      }
    }
  }
}
