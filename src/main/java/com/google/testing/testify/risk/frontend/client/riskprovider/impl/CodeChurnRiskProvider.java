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


package com.google.testing.testify.risk.frontend.client.riskprovider.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.TaCallback;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Risk provider showing risk due to code churn. It works by analyzing individual changes which
 * each contain a list of 'directories they care about' and then scanning all checkins to see
 * if they touched any directories of interest.
 *
 * Note that if a checkin is in "foo\bar\baz", Components which care about "foo" and "foo\bar" will
 * both have the risk associated with the checkin. To facilitate risk cascading down a directory
 * hierarchy, a tree will be created where each node is a directory name.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CodeChurnRiskProvider implements RiskProvider {

  // TODO(chrsmith): While time-efficient, creating a checkin lookup tree is not the simplest
  // solution. Perhaps we can use a Multimap<String, Checkin> instead. Note however that we
  // would need to walk every Key and see if the Key starts with the target directory in the case
  // that the Component cares about "alpha\beta" and a checkin touches "alpha\beta\gamma".

  /** Tree used to quickly lookup checkins based on a code directory. */
  CheckinDirectoryTreeNode treeRoot = new CheckinDirectoryTreeNode();

  @Override
  public double calculateRisk(CapabilityIntersectionData targetCell) {
    Set<Checkin> relevantCheckins = getCheckinsRealtedToRiskCell(targetCell);
    return relevantCheckins.size() * 0.20;
  }

  @Override
  public String getName() {
    return "Code churn";
  }

  @Override
  public void initialize(List<CapabilityIntersectionData> projectData) {
    DataRpcAsync dataService = GWT.create(DataRpc.class);

    long projectId = projectData.get(0).getParentComponent().getParentProjectId();
    dataService.getProjectCheckinsById(projectId,
        new TaCallback<List<Checkin>>("Querying Checkins") {
          @Override
          public void onSuccess(List<Checkin> result) {
            initializeCheckinLookup(result);
          }
        });
  }

  @Override
  public Widget onClick(CapabilityIntersectionData targetCell) {
    VerticalPanel checkinsPanel = new VerticalPanel();

    Set<Checkin> relevantCheckins = getCheckinsRealtedToRiskCell(targetCell);
    if (relevantCheckins.size() == 0) {
      checkinsPanel.add(new Label("No checkins are associated with this cell."));
    } else {
      for (Checkin checkin : relevantCheckins) {
        String linkText = Long.toString(checkin.getExternalId()) + ": " + checkin.getSummary();

        // Display only 100 characters of a checkin summary.
        if (linkText.length() > 100) {
          linkText = linkText.substring(0, 97) + "...";
        }

        checkinsPanel.add(new Anchor(linkText, checkin.getChangeUrl()));
      }
    }

    return checkinsPanel;
  }

  /**
   * Initialize the checkin directory tree data structure based on the directories touched by all
   * known checkins.
   */
  private void initializeCheckinLookup(List<Checkin> checkins) {
    treeRoot = new CheckinDirectoryTreeNode();
    for (Checkin checkin : checkins) {
      for (String directoryTouched : checkin.getDirectoriesTouched()) {
        // NOTE: This means that if a checkin touches directory A and directory B, then
        // the checkin will be double-counted if the component looks for checkins in both A and B.
        treeRoot.addCheckin(directoryTouched, checkin);
      }
    }
  }

  /**
   * @return the checkins relevant to the risk provider cell. (Based on the current checkin
   * directory tree in memory.)
   */
  private Set<Checkin> getCheckinsRealtedToRiskCell(CapabilityIntersectionData cell) {
    // TODO(chrsmith): Implement this by relying on ComponentSuperLabels.
    return new HashSet<Checkin>();
  }
}
