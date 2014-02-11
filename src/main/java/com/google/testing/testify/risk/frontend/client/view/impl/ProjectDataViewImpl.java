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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.util.LinkUtil;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageSectionVerticalPanel;
import com.google.testing.testify.risk.frontend.model.UploadedDatum;

import java.util.List;

/**
 * Page for displaying data uploaded into a project.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectDataViewImpl extends Composite {

 interface ProjectDataViewImplUiBinder extends UiBinder<Widget, ProjectDataViewImpl> {}
 private static final ProjectDataViewImplUiBinder uiBinder =
     GWT.create(ProjectDataViewImplUiBinder.class);

 @UiField
 public PageSectionVerticalPanel pageSection;

 @UiField
 public InlineLabel introText;

 @UiField
 public Grid dataGrid;

 @UiField
 public Label dataSummary;

 private static final String GRID_CELL_CSS_STYLE = "tty-DataGridCell";
 private static final String GRID_IMAGE_CELL_CSS_STYLE = "tty-DataGridImageCell";
 private static final String GRID_HEADER_CSS_STYLE = "tty-DataGridHeaderCell";

 public ProjectDataViewImpl() {
   initWidget(uiBinder.createAndBindUi(this));
   dataSummary.setText("No data provided yet.");
 }

 /** Sets the page header and intro text. */
 public void setPageText(String headerText, String introText) {
   pageSection.setHeaderText(headerText);
   this.introText.setText(introText);
 }

 public void displayData(List<UploadedDatum> data) {
   if (data.size() == 0) {
     dataSummary.setText("No items have been uploaded.");
     return;
   }

   UploadedDatum firstItem = data.get(0);
   dataSummary.setText(
     "Showing " + Integer.toString(data.size()) + " " + firstItem.getDatumType().getPlural());

   dataGrid.clear();
   // Header row + one for each bug x datum, Attribute, Component, Capability
   dataGrid.resize(data.size() + 1, 4);

   // Set grid headers.
   dataGrid.setWidget(0, 0, new Label(firstItem.getDatumType().getPlural()));
   dataGrid.setWidget(0, 1, new Label("Attribute"));
   dataGrid.setWidget(0, 2, new Label("Component"));
   dataGrid.setWidget(0, 3, new Label("Capability"));

   dataGrid.getWidget(0, 0).addStyleName(GRID_HEADER_CSS_STYLE);
   dataGrid.getWidget(0, 1).addStyleName(GRID_HEADER_CSS_STYLE);
   dataGrid.getWidget(0, 2).addStyleName(GRID_HEADER_CSS_STYLE);
   dataGrid.getWidget(0, 3).addStyleName(GRID_HEADER_CSS_STYLE);

   // Fill with data.
   for (int i = 0; i < data.size(); i++) {
     UploadedDatum datum = data.get(i);

     String host = LinkUtil.getLinkHost(datum.getLinkUrl());
     Widget description;
     if (host != null) {
       HorizontalPanel panel = new HorizontalPanel();
       Anchor anchor = new Anchor(datum.getLinkText(), datum.getLinkUrl());
       anchor.setTarget("_blank");

       Label hostLabel = new Label(host);
       panel.add(anchor);
       panel.add(hostLabel);

       description = panel;
     } else {
       description = new Label(datum.getLinkText() + " [" + datum.getLinkUrl() + "]");
     }
     description.addStyleName(GRID_CELL_CSS_STYLE);
     description.setTitle(datum.getToolTip());
     dataGrid.setWidget(i + 1, 0, description);

     // Display images indicating whether or not the datum is associated with project artifacts.
     // For example, a Bug may be associated with a Component or a Testcase might validate scenarios
     // for a given Attribute. The user can associate data with project artifacts using SuperLabels.
     dataGrid.setWidget(i + 1, 1, (datum.isAttachedToAttribute()) ? getX() : getCheckmark());
     dataGrid.setWidget(i + 1, 2, (datum.isAttachedToComponent()) ? getX() : getCheckmark());
     dataGrid.setWidget(i + 1, 3, (datum.isAttachedToCapability()) ? getX() : getCheckmark());
   }
 }

  /** Returns an X image for the dataGrid. */
  private Image getX() {
    Image redXImage = new Image("/images/redx_12.png");
    redXImage.addStyleName(GRID_IMAGE_CELL_CSS_STYLE);
    return redXImage;
  }

  /** Returns a checkmark image for the dataGrid. */
  private Image getCheckmark() {
    Image checkImage = new Image("/images/checkmark_12.png");
    checkImage.addStyleName(GRID_IMAGE_CELL_CSS_STYLE);
    return checkImage;
  }
}
