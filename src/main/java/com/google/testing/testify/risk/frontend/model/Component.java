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
 * JDO object for Component.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class Component implements Serializable, HasLabels {

  private static final AccElementType ELEMENT_TYPE = AccElementType.COMPONENT;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long componentId;

  @Persistent
  private long parentProjectId;

  @Persistent
  private String name;

  @Persistent
  private String description;

  /**
   * Ordering hint for views displaying Attributes. Lower values should be put higher in the list.
   * Note that value is NOT guaranteed to be unique or greater than zero.
   */
  @Persistent
  private Long displayOrder = 0 - System.currentTimeMillis();

  @NotPersistent
  private List<AccLabel> accLabels = new ArrayList<AccLabel>();

  public Component() {
  }

  public Component(long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public Long getComponentId() {
    return componentId;
  }

  @Override
  public Long getId() {
    return getComponentId();
  }

  @Override
  public AccElementType getElementType() {
    return ELEMENT_TYPE;
  }

  public void setComponentId(long componentId) {
    this.componentId = componentId;
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

  public void setDisplayOrder(long displayOrder) {
    this.displayOrder = displayOrder;
  }

  public long getDisplayOrder() {
    return displayOrder;
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

  public void addLabel(String labelText) {
    AccLabel label = new AccLabel();
    label.setProjectId(parentProjectId);
    label.setElementId(componentId);
    label.setElementType(ELEMENT_TYPE);
    label.setLabelText(labelText);

    accLabels.add(label);
  }

  public void addLabel(String name, String value) {
    AccLabel label = new AccLabel();
    label.setProjectId(parentProjectId);
    label.setElementId(componentId);
    label.setElementType(ELEMENT_TYPE);
    label.setName(name);
    label.setValue(value);
    accLabels.add(label);
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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description == null ? "" : description;
  }
}
