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

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Stores a boolean that determines if a ACC member has been signed off upon.  This could be a
 * boolean on the ACC directly, but likely we will start to store much more data here -- an
 * audit trail, etc, so this will start out as a very small class in expectation to grow.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class Signoff implements Serializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private long parentProjectId;

  @Persistent
  private AccElementType elementType;
  
  @Persistent
  private Long elementId;
  
  @Persistent
  private Boolean signedOff;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getParentProjectId() {
    return parentProjectId;
  }

  public void setParentProjectId(long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public AccElementType getElementType() {
    return elementType;
  }

  public void setElementType(AccElementType elementType) {
    this.elementType = elementType;
  }

  public Long getElementId() {
    return elementId;
  }

  public void setElementId(Long elementId) {
    this.elementId = elementId;
  }

  public Boolean getSignedOff() {
    return signedOff;
  }

  public void setSignedOff(Boolean signedOff) {
    this.signedOff = signedOff;
  }
}
