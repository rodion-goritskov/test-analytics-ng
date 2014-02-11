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


package com.google.testing.testify.risk.frontend.client.view.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.riskprovider.RiskProvider;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.BugRiskProvider;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.CodeChurnRiskProvider;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.StaticRiskProvider;
import com.google.testing.testify.risk.frontend.client.riskprovider.impl.TestCoverageRiskProvider;
import com.google.testing.testify.risk.frontend.client.view.widgets.LinkCapabilityWidget;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.CapabilityIntersectionData;
import com.google.testing.testify.risk.frontend.model.Pair;

import java.util.List;

/**
 * View of mitigated risk for a project.
 *
 * @author chrsmith@google.com (Chris Smith)
 * @author jimr@google.com (Jim Reardon)
 */
public class KnownRiskViewImpl extends RiskViewImpl {

  /**
   * Stores details on a risk provider along with a checkbox indicating its state.
   */
  private class SourceItem {
    private final RiskProvider provider;
    private final CheckBox checkBox;

    public SourceItem(RiskProvider provider, CheckBox checkBox) {
      this.provider = provider;
      this.checkBox = checkBox;
    }

    public RiskProvider getProvider() { return provider; }
    public CheckBox getCheckBox() { return checkBox; }
  }

  /** Panel to hold all of the check boxes associated with Risk sources. */
  private final HorizontalPanel sourcesPanel = new HorizontalPanel();

  /** Panel to hold the risk page's content. */
  private final HorizontalPanel contentPanel = new HorizontalPanel();

  private final List<SourceItem> sources = Lists.newArrayList();

  public KnownRiskViewImpl() {
    String introText =
      "This shows the Total Risk to your application, taking into account any Risk Sources "
      + "as well as Mitigation Sources that are checked below.";
    setPageText("Known Risk", introText);
    sourcesPanel.addStyleName("tty-RiskSourcesPanel");
    contentPanel.add(sourcesPanel);
    this.content.add(contentPanel);

    addValueChangeHandler(
        new ValueChangeHandler<Pair<Integer, Integer>>() {
          @Override
          public void onValueChange(ValueChangeEvent<Pair<Integer, Integer>> event) {
            CapabilityIntersectionData cellData = getDataForCell(
                event.getValue().first, event.getValue().second);
            bottomContent.clear();
            bottomContent.setWidget(createBottomWidget(cellData));
          }
        });
  }

  private Widget createBottomWidget(CapabilityIntersectionData data) {
    VerticalPanel panel = new VerticalPanel();
    panel.setStyleName("tty-ItemContainer");
    String aName = data.getParentAttribute().getName();
    String cName = data.getParentComponent().getName();
    Label name = new Label(cName + " is " + aName);
    name.setStyleName("tty-ItemName");
    panel.add(name);

    for (Capability capability : data.getCapabilities()) {
      LinkCapabilityWidget widget = new LinkCapabilityWidget(capability);
      panel.add(widget);
    }
    return panel;
  }

  /**
   * Returns a CheckBox to control the RiskProvider (changing the check state regenerates the risk
   * grid's colors.)
   */
  private CheckBox getRiskProviderCheckBox(RiskProvider provider) {
    CheckBox providerCheckBox = new CheckBox(provider.getName());
    providerCheckBox.setValue(true);
    providerCheckBox.addValueChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            refreshRiskCalculation();
          }
        });
    return providerCheckBox;
  }

  @Override
  protected void onInitialized() {
    List<RiskProvider> providers = Lists.newArrayList(
        new StaticRiskProvider(),
        new BugRiskProvider(),
        new CodeChurnRiskProvider(),
        new TestCoverageRiskProvider());

    // Initialize risk sources.
    sources.clear();
    sourcesPanel.clear();
    for (RiskProvider provider : providers) {
      CheckBox providerCheckBox = getRiskProviderCheckBox(provider);
      sourcesPanel.add(providerCheckBox);
      SourceItem sourceItem = new SourceItem(provider, providerCheckBox);
      sources.add(sourceItem);
    }

    refreshRiskCalculation();
  }

  /**
   * Initialize every cell in the table. This includes calculating the risk of every risk source
   * and mitigation and then viewing the delta.
   */
  private void refreshRiskCalculation() {
    Predicate<SourceItem> getChecked = new Predicate<SourceItem>() {
      @Override
      public boolean apply(SourceItem input) {
        return input.getCheckBox().getValue();
      }};

    Function<SourceItem, RiskProvider> getProvider = new Function<SourceItem, RiskProvider>() {
      @Override
      public RiskProvider apply(SourceItem arg0) {
        return arg0.getProvider();
      }
    };

    List<RiskProvider> enabled =
      Lists.newArrayList(
        Iterables.transform(
            Iterables.filter(sources, getChecked),
            getProvider));

    refreshRiskCalculation(enabled);
  }
}
