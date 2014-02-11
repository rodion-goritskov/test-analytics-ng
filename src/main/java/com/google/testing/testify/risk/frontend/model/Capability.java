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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO object for Capability.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class Capability implements Serializable, HasLabels {

  private static final AccElementType ELEMENT_TYPE = AccElementType.CAPABILITY;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long capabilityId;

  @Persistent
  private long parentProjectId;

  @Persistent
  private String name;

  @Persistent
  private String description;

  @NotPersistent
  private List<AccLabel> accLabels = new ArrayList<AccLabel>();

  /** Parent Component. */
  @Persistent
  private long componentId;

  /** Parent Attribute. */
  @Persistent
  private long attributeId;

  @Persistent
  private FailureRate failureRate = FailureRate.NA;

  @Persistent
  private UserImpact userImpact = UserImpact.NA;

  @Persistent
  private Long displayOrder = 0 - System.currentTimeMillis();

  public Capability() {
  }

  /**
   * Constructs a new Capability.
   *
   * @param parentProjectId ID of the owning Project.
   * @param parentAttributeId Attribute ID of the parent Attribute.
   * @param parentComponentId Component ID of the parent Component.
   */
  public Capability(long parentProjectId, long parentAttributeId, long parentComponentId) {
    this.parentProjectId = parentProjectId;
    this.componentId = parentComponentId;
    this.attributeId = parentAttributeId;
  }

  public Long getCapabilityId() {
    return capabilityId;
  }

  @Override
  public Long getId() {
    return getCapabilityId();
  }

  @Override
  public AccElementType getElementType() {
    return ELEMENT_TYPE;
  }

  public void setCapabilityId(long capabilityId) {
    this.capabilityId = capabilityId;
  }

  public void setParentProjectId(long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  @Override
  public long getParentProjectId() {
    return parentProjectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getComponentId() {
    return componentId;
  }

  public void setComponentId(long componentId) {
    this.componentId = componentId;
  }

  public long getAttributeId() {
    return attributeId;
  }

  public void setAttributeId(long attributeId) {
    this.attributeId = attributeId;
  }

  public UserImpact getUserImpact() {
    return userImpact;
  }

  public void setUserImpact(UserImpact userImpact) {
    this.userImpact = userImpact;
  }

  public FailureRate getFailureRate() {
    return failureRate;
  }

  public void setFailureRate(FailureRate failureRate) {
    this.failureRate = failureRate;
  }

  /**
   * @return the stable intersection key. {@See getCapabilityIntersectionKey}
   */
  public int getCapabilityIntersectionKey() {
    return Capability.getCapabilityIntersectionKey(componentId, attributeId);
  }

  /**
   * Given a parent Component and Attribute return a random, but stable integer index. This
   * allows efficient lookup for Capabilities based on parent Components and Attribute pairs.
   *
   * @param component the parent component.
   * @param attribute the parent attribute.
   * @return a stable integer corresponding to the unique component / attribute pairing sutable
   * for placing Capability lists into a map.
   */
  public static int getCapabilityIntersectionKey(Component component, Attribute attribute) {
    return Capability.getCapabilityIntersectionKey(
               component.getComponentId(), attribute.getAttributeId());
  }

  /**
   * Computes a capability intersection key given the raw component and attribute IDs.
   */
  private static int getCapabilityIntersectionKey(long componentId, long attributeId) {
    return Integer.valueOf((int) ((componentId << 16) ^ attributeId));
  }

  public long getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(long displayOrder) {
    this.displayOrder = displayOrder;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description == null ? "" : description;
  }

  @Override
  public List<AccLabel> getAccLabels() {
    return accLabels;
  }

  public AccLabel getAccLabel(String accLabelId) {
    for (AccLabel l : accLabels) {
      if (accLabelId.equals(l.getId())) {
        return l;
      }
    }
    return null;
  }

  @Override
  public void setAccLabels(List<AccLabel> labels) {
    this.accLabels = labels;
  }

  public AccLabel addLabel(String labelText) {
    AccLabel label = new AccLabel();
    label.setProjectId(parentProjectId);
    label.setElementId(capabilityId);
    label.setElementType(ELEMENT_TYPE);
    label.setLabelText(labelText);

    accLabels.add(label);
    return label;
  }

  public AccLabel addLabel(String name, String value) {
    AccLabel label = new AccLabel();
    label.setProjectId(parentProjectId);
    label.setElementId(capabilityId);
    label.setElementType(ELEMENT_TYPE);
    label.setName(name);
    label.setValue(value);
    accLabels.add(label);
    return label;
  }

  @Override
  public void addLabel(AccLabel label) {
    accLabels.add(label);
  }

  public void removeLabel(AccLabel label) {
    Iterator<AccLabel> i = accLabels.iterator();
    AccLabel l;
    while (i.hasNext()) {
      l = i.next();
      if (label.getId() != null) {
        if (label.getId().equals(l.getId())) {
          i.remove();
        }
      } else {
        if (label.getLabelText().equals(l.getLabelText())) {
          i.remove();
        }
      }
    }
  }

  public void removeLabel(String labelText) {
    Iterator<AccLabel> i = accLabels.iterator();
    AccLabel l;
    while (i.hasNext()) {
      l = i.next();
      if (labelText.equals(l.getLabelText())) {
        i.remove();
      }
    }
  }

  public void removeLabel(String name, String value) {
    Iterator<AccLabel> i = accLabels.iterator();
    AccLabel l;
    while (i.hasNext()) {
      l = i.next();
      if (name.equals(l.getName()) && value.equals(l.getValue())) {
        i.remove();
      }
    }
  }

  public void updateLabel(String oldLabelText, String newLabelText) {
    for (AccLabel l : accLabels) {
      if (oldLabelText.equals(l.getLabelText())) {
        l.setLabelText(newLabelText);
      }
    }
  }
}
