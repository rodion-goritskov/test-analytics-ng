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


package com.google.testing.testify.risk.frontend.client.view.impl;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.view.CapabilityDetailsView;
import com.google.testing.testify.risk.frontend.client.view.widgets.EditCapabilityWidget;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageSectionVerticalPanel;
import com.google.testing.testify.risk.frontend.model.Attribute;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Capability;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.Component;
import com.google.testing.testify.risk.frontend.model.UploadedDatum;
import com.google.testing.testify.risk.frontend.model.TestCase;

import java.util.Collection;
import java.util.List;

/**
 * View the details of a Capability, including attached data artifacts.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class CapabilityDetailsViewImpl extends Composite implements CapabilityDetailsView {

  private static final String HEADER_TEXT = "Details for Capability: ";

  /**
   * Used to wire parent class to associated UI Binder.
   */
  interface CapabilityDetailsViewImplUiBinder extends UiBinder<Widget, CapabilityDetailsViewImpl> {}

  private static final CapabilityDetailsViewImplUiBinder uiBinder =
      GWT.create(CapabilityDetailsViewImplUiBinder.class);

  @UiField
  public PageSectionVerticalPanel detailsSection;
  @UiField
  public CheckBox signoffBox;
  @UiField
  public Image testChart;
  @UiField
  public Grid testGrid;
  @UiField
  public HTML testNotRunCount;
  @UiField
  public HTML testPassedCount;
  @UiField
  public HTML testFailedCount;
  @UiField
  public Grid bugGrid;
  @UiField
  public Grid changeGrid;

  // Will be constructed and added to the above panel once we know what capability we're showing.
  public EditCapabilityWidget capabilityWidget;

  boolean isEditable = false;

  private CapabilityDetailsView.Presenter presenter;

  // TODO(jimr): Reconsider this data model.  Keeping each data item stored twice is not awesome.
  private List<Attribute> attributes;
  private List<Component> components;
  private List<Bug> bugs;
  private List<Bug> otherBugs = Lists.newArrayList();
  private List<Bug> capabilityBugs = Lists.newArrayList();
  private List<TestCase> tests;
  private List<TestCase> otherTests = Lists.newArrayList();
  private List<TestCase> capabilityTests = Lists.newArrayList();
  private List<Checkin> checkins;
  private List<Checkin> otherCheckins = Lists.newArrayList();
  private List<Checkin> capabilityCheckins = Lists.newArrayList();
  private Collection<String> projectLabels = Lists.newArrayList();

  private Anchor addBugAnchor;
  private Anchor addCheckinAnchor;
  private Anchor addTestAnchor;

  private Capability capability = null;

  public CapabilityDetailsViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    detailsSection.setHeaderText(HEADER_TEXT);
  }

  @UiHandler("signoffBox")
  public void addSignoffClickHandler(ClickEvent click) {
    presenter.setSignoff(capability.getCapabilityId(), signoffBox.getValue());
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
    refresh();
  }

  @Override
  public void setCapability(Capability capability) {
    this.capability = capability;
    refresh();
  }

  @Override
  public void setAttributes(List<Attribute> attributes) {
    this.attributes = attributes;
    refresh();
  }

  @Override
  public void setBugs(List<Bug> bugs) {
    this.bugs = bugs;
    refresh();
  }

  @Override
  public void setCheckins(List<Checkin> checkins) {
    this.checkins = checkins;
    refresh();
  }

  @Override
  public void setProjectLabels(Collection<String> labels) {
    projectLabels.clear();
    projectLabels.addAll(labels);
    if (capabilityWidget != null) {
      capabilityWidget.setLabelSuggestions(labels);
    }
  }

  private <T extends UploadedDatum> void splitData(List<T> inItems,
      List<T> otherItems, List<T> capabilityItems) {
    otherItems.clear();
    capabilityItems.clear();
    for (T item : inItems) {
      if (capability.getCapabilityId().equals(item.getTargetCapabilityId())) {
        capabilityItems.add(item);
      } else {
        otherItems.add(item);
      }
    }
  }

  @Override
  public void setTests(List<TestCase> tests) {
    this.tests = tests;
    refresh();
  }

  @Override
  public void setComponents(List<Component> components) {
    this.components = components;
    refresh();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void refresh() {
    // Don't re-draw until all data has successfully loaded.
    if (attributes != null && components != null && capability != null && bugs != null &&
        tests != null && checkins != null) {
      splitData(tests, otherTests, capabilityTests);
      splitData(bugs, otherBugs, capabilityBugs);
      splitData(checkins, otherCheckins, capabilityCheckins);
      capabilityWidget = new EditCapabilityWidget(capability);
      capabilityWidget.setLabelSuggestions(projectLabels);
      capabilityWidget.setAttributes(attributes);
      capabilityWidget.setComponents(components);
      capabilityWidget.disableDelete();
      if (isEditable) {
        capabilityWidget.makeEditable();
      }
      capabilityWidget.expand();
      capabilityWidget.addValueChangeHandler(new ValueChangeHandler<Capability>() {
          @Override
          public void onValueChange(ValueChangeEvent<Capability> event) {
            capability = event.getValue();
            presenter.updateCapability(capability);
            refresh();
            capabilityWidget.showSaved();
          }
        });
      detailsSection.clear();
      detailsSection.add(capabilityWidget);
      updateTestSection();
      updateBugSection();
      updateCheckinsSection();
      detailsSection.setHeaderText(HEADER_TEXT + capability.getName());
    }
  }

  @Override
  public void makeEditable() {
    isEditable = true;
    signoffBox.setEnabled(true);
    if (capabilityWidget != null) {
      capabilityWidget.makeEditable();
    }
    if (addTestAnchor != null) {
      addTestAnchor.setVisible(true);
    }
    if (addBugAnchor != null) {
      addBugAnchor.setVisible(true);
    }
    if (addCheckinAnchor != null) {
      addCheckinAnchor.setVisible(true);
    }
  }

  @Override
  public void reset() {
    attributes = null;
    components = null;
    capabilityWidget = null;
    capability = null;
    bugs = null;
    tests = null;
    checkins = null;
  }

  private TestCase getTestCaseById(long id) {
    for (TestCase test : tests) {
      if (test.getInternalId() == id) {
        return test;
      }
    }
    return null;
  }

  private Widget buildTestHeaderWidget(String header, String addText) {
    final ListBox options = new ListBox();
    for (TestCase test : otherTests) {
      options.addItem(test.getExternalId() + " " + test.getTitle(),
          String.valueOf(test.getInternalId()));
    }
    VerticalPanel addForm = new VerticalPanel();
    addForm.add(options);

    final DisclosurePanel disclosure = new DisclosurePanel();
    Button button = new Button(" Add ", new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          long id = Long.parseLong((options.getValue(options.getSelectedIndex())));
          presenter.assignTestCaseToCapability(capability.getCapabilityId(), id);
          disclosure.setOpen(false);
          TestCase test = getTestCaseById(id);
          test.setTargetCapabilityId(capability.getCapabilityId());
          refresh();
        }
      });
    addForm.add(button);
    disclosure.setAnimationEnabled(true);
    disclosure.setOpen(false);
    disclosure.setContent(addForm);

    HorizontalPanel title = new HorizontalPanel();
    title.add(new Label(header));
    addTestAnchor = new Anchor(addText);
    addTestAnchor.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          disclosure.setOpen(!disclosure.isOpen());
        }
      });
    addTestAnchor.setVisible(isEditable);
    title.add(addTestAnchor);

    VerticalPanel everything = new VerticalPanel();
    everything.add(title);
    everything.add(disclosure);
    return everything;
  }

  private void updateTestSection() {
    testGrid.setTitle("Recent Test Activity");
    testGrid.resize(capabilityTests.size() + 1, 1);
    testGrid.setWidget(0, 0, buildTestHeaderWidget("Recent Test Activity", "add test"));

    int passed = 0, failed = 0, notRun = 0;
    for (int i = 0; i < capabilityTests.size(); i++) {
      TestCase test = capabilityTests.get(i);
      HorizontalPanel panel = new HorizontalPanel();
      panel.add(getTestStateImage(test.getState()));
      Anchor anchor = new Anchor(test.getLinkText(), test.getLinkUrl());
      anchor.setTarget("_blank");
      panel.add(anchor);
      Label statusLabel = new Label();

      int state = getTestState(test.getState());
      if (state < 0) {
        statusLabel.setText(" - failed " + getDateText(test.getStateDate()));
        failed++;
      } else if (state > 0) {
        statusLabel.setText(" - passed " + getDateText(test.getStateDate()));
        passed++;
      } else {
        statusLabel.setText(" - no result");
        notRun++;
      }
      panel.add(statusLabel);
      testGrid.setWidget(i + 1, 0, panel);
    }
    testNotRunCount.setHTML("not run <b>" + notRun + "</b>");
    testPassedCount.setHTML("passed <b>" + passed + "</b>");
    testFailedCount.setHTML("failed <b>" + failed + "</b>");
    String imageUrl = getTestChartUrl(passed, failed, notRun);
    if (imageUrl == null || "".equals(imageUrl)) {
      testChart.setVisible(false);
    } else {
      testChart.setUrl(imageUrl);
      testChart.setVisible(true);
    }
  }

  private Bug getBugById(long id) {
    for (Bug bug : bugs) {
      if (bug.getInternalId() == id) {
        return bug;
      }
    }
    return null;
  }

  private Widget buildBugHeaderWidget(String header, String addText) {
    final ListBox options = new ListBox();
    for (Bug bug : otherBugs) {
      options.addItem(bug.getExternalId() + " " + bug.getTitle(),
          String.valueOf(bug.getInternalId()));
    }
    VerticalPanel addForm = new VerticalPanel();
    addForm.add(options);

    final DisclosurePanel disclosure = new DisclosurePanel();
    Button button = new Button(" Add ", new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          long id = Long.parseLong((options.getValue(options.getSelectedIndex())));
          presenter.assignBugToCapability(capability.getCapabilityId(), id);
          disclosure.setOpen(false);
          Bug bug = getBugById(id);
          bug.setTargetCapabilityId(capability.getCapabilityId());
          refresh();
        }
      });
    addForm.add(button);
    disclosure.setAnimationEnabled(true);
    disclosure.setOpen(false);
    disclosure.setContent(addForm);

    HorizontalPanel title = new HorizontalPanel();
    title.add(new Label(header));
    addBugAnchor = new Anchor(addText);
    addBugAnchor.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          disclosure.setOpen(!disclosure.isOpen());
        }
      });
    addBugAnchor.setVisible(isEditable);
    title.add(addBugAnchor);

    VerticalPanel everything = new VerticalPanel();
    everything.add(title);
    everything.add(disclosure);
    return everything;
  }

  private void updateBugSection() {
    bugGrid.resize(capabilityBugs.size() + 1, 1);
    bugGrid.setTitle("Bugs (" + capabilityBugs.size() + " total)");
    bugGrid.setWidget(0, 0, buildBugHeaderWidget("Bugs (" +  capabilityBugs.size() + " total)",
        "add bug"));

    for (int i = 0; i < capabilityBugs.size(); i++) {
      Bug bug = capabilityBugs.get(i);
      HorizontalPanel panel = new HorizontalPanel();
      panel.add(getBugStateImage(bug.getState()));
      Anchor anchor = new Anchor(bug.getLinkText(), bug.getLinkUrl());
      anchor.setTarget("_blank");
      panel.add(anchor);
      Label statusLabel = new Label();
      statusLabel.setText(" - filed " + getDateText(bug.getStateDate()));
      panel.add(statusLabel);
      bugGrid.setWidget(i + 1, 0, panel);
    }
  }

  private Checkin getCheckinById(long id) {
    for (Checkin checkin : checkins) {
      if (checkin.getInternalId() == id) {
        return checkin;
      }
    }
    return null;
  }

  private Widget buildCheckinHeaderWidget(String header, String addText) {
    final ListBox options = new ListBox();
    for (Checkin checkin : otherCheckins) {
      options.addItem(checkin.getExternalId() + " " + checkin.getSummary(),
          String.valueOf(checkin.getInternalId()));
    }
    VerticalPanel addForm = new VerticalPanel();
    addForm.add(options);

    final DisclosurePanel disclosure = new DisclosurePanel();
    Button button = new Button(" Add ", new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          long id = Long.parseLong((options.getValue(options.getSelectedIndex())));
          presenter.assignCheckinToCapability(capability.getCapabilityId(), id);
          disclosure.setOpen(false);
          Checkin checkin = getCheckinById(id);
          checkin.setTargetCapabilityId(capability.getCapabilityId());
          refresh();
        }
      });
    addForm.add(button);
    disclosure.setAnimationEnabled(true);
    disclosure.setOpen(false);
    disclosure.setContent(addForm);

    HorizontalPanel title = new HorizontalPanel();
    title.add(new Label(header));
    addCheckinAnchor = new Anchor(addText);
    addCheckinAnchor.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          disclosure.setOpen(!disclosure.isOpen());
        }
      });
    addCheckinAnchor.setVisible(isEditable);
    title.add(addCheckinAnchor);

    VerticalPanel everything = new VerticalPanel();
    everything.add(title);
    everything.add(disclosure);
    return everything;
  }

  private void updateCheckinsSection() {
    changeGrid.setTitle("Recent Code Changes (" + capabilityCheckins.size() + " total)");
    changeGrid.resize(capabilityCheckins.size() + 1, 1);
    changeGrid.setWidget(0, 0, buildCheckinHeaderWidget(
        "Recent Code Changes (" + capabilityCheckins.size() + " total)", "add code change"));

    for (int i = 0; i < capabilityCheckins.size(); i++) {
      Checkin checkin = capabilityCheckins.get(i);
      HorizontalPanel panel = new HorizontalPanel();
      panel.add(new Image("/images/teststate-passed.png"));
      Anchor anchor = new Anchor(checkin.getLinkText(), checkin.getLinkUrl());
      anchor.setTarget("_blank");
      panel.add(anchor);
      Label statusLabel = new Label();
      statusLabel.setText(" - submitted " + getDateText(checkin.getStateDate()));
      panel.add(statusLabel);
      changeGrid.setWidget(i + 1, 0, panel);
    }
  }

  private Image getBugStateImage(String state) {
    Image image = new Image();
    if (state != null && state.toLowerCase().equals("closed")) {
      image.setUrl("/images/bugstate-closed.png");
    } else {
      image.setUrl("/images/bugstate-active.png");
    }
    return image;
  }

  /**
   * Turns a number of days, eg: 3, into a string like "3 days ago" or "1 day ago" or "today".
   * @param date date reported/filed/etc.
   * @return string representation.
   */
  private String getDateText(Long date) {
    int days = -1;
    if (date != null && date > 0) {
      days = (int) ((double) System.currentTimeMillis() - date) / 86400000;
    }

    if (days == 0) {
      return "today";
    } else if (days == 1) {
      return "1 day ago";
    } else if (days > 1) {
      return days + " days ago";
    }
    return "";
  }

  /**
   * Determine from a text description what state a test is in.
   *
   * @param state the text state.
   * @return -1 for failing test, 0 for unsure/not run, 1 for passing.
   */
  private int getTestState(String state) {
    if (state == null) {
      return 0;
    }
    state = state.toLowerCase();
    if (state.startsWith("pass")) {
      return 1;
    } else if (state.startsWith("fail")) {
      return -1;
    } else {
      return 0;
    }
  }

  private Image getTestStateImage(String state) {
    Image image = new Image();
    int stateVal = getTestState(state);
    if (stateVal > 0) {
      image.setUrl("/images/teststate-passed.png");
    } else if (stateVal < 0) {
      image.setUrl("/images/teststate-failed.png");
    } else {
      image.setUrl("/images/teststate-notrun.png");
    }
    return image;
  }

  private String getTestChartUrl(int passed, int failed, int notRun) {
    int total = passed + failed + notRun;
    if (total < 1) {
      return null;
    }
    passed = passed * 100 / total;
    failed = failed * 100 / total;
    notRun = notRun * 100 / total;
    String pStr = String.valueOf(passed);
    String fStr = String.valueOf(failed);
    String nStr = String.valueOf(notRun);
    return "http://chart.apis.google.com/chart?chs=500x20&cht=bhs&chco=FFFFFF,008000,FF0000&chd=t:"
      + nStr + "|" + pStr + "|" + fStr;
  }

  @Override
  public void setSignoff(boolean signoff) {
    signoffBox.setValue(signoff);
  }
}
