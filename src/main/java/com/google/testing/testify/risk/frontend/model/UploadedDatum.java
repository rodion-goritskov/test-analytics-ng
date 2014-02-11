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

/**
 * Interface for surfacing inform from uploaded data to enable display.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public interface UploadedDatum {

  public DatumType getDatumType();

  /** Returns the text for any links to this datum. */
  public String getLinkText();

  /** Returns an external URL to refer to the uploaded datum. */
  public String getLinkUrl();

  /** Returns a Tool Tip to display when hovering over the datum. */
  public String getToolTip();

  /** State, such as Open, Passed, Failed, etc. */
  public String getState();

  /** Date the item entered the state, eg: day it passed, date it was submitted. */
  public Long getStateDate();

  /** Methods for checking if the datum is attached to an Attribute, Component, or Capability. */
  public boolean isAttachedToAttribute();
  public boolean isAttachedToComponent();
  public boolean isAttachedToCapability();

  public void setTargetCapabilityId(Long targetCapabilityId);
  public Long getTargetCapabilityId();
  public void setTargetAttributeId(Long targetAttributeId);
  public Long getTargetAttributeId();
  public void setTargetComponentId(Long targetComponentId);
  public Long getTargetComponentId();

  public Long getParentProjectId();
  public void setParentProjectId(Long parentProjectId);

  public Long getExternalId();
  public void setExternalId(Long externalId);

  public Long getInternalId();
  public void setInternalId(Long internalId);

  /** Allows generic access for filtering. */
  public String getField(String field);
}
