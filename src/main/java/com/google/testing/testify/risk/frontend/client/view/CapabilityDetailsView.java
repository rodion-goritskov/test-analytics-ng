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


package com.google.testing.testify.risk.frontend.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.presenter.TaPagePresenter;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.TestCase;

import java.util.Collection;
import java.util.List;

/**
 * View on top of the CapabilityDetails page.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public interface CapabilityDetailsView {

  /**
   * Presenter interface for this view.
   */
  public interface Presenter extends TaPagePresenter {
    public void assignBugToCapability(long capabilityId, long bugId);
    public void assignCheckinToCapability(long capabilityId, long checkinId);
    public void assignTestCaseToCapability(long capabilityId, long testId);
    public void updateCapability(Capability capability);
    public void setSignoff(long capabilityId, boolean isSignedOff);
  }
  
  public void setAttributes(List<Attribute> attributes);
  public void setBugs(List<Bug> bugs);
  public void setComponents(List<Component> components);
  public void setCapability(Capability capability);
  public void setTests(List<TestCase> tests);
  public void setCheckins(List<Checkin> checkins);
  public void setSignoff(boolean signoff);
  public void setProjectLabels(Collection<String> labels);

  public void setPresenter(Presenter presenter);
  
  /**
   * Reset clears all stored data.  Call before refresh, or setting new data,
   * to avoid staggering updates. */
  public void reset();
  public void makeEditable();
  public void refresh();
  public Widget asWidget();
}
