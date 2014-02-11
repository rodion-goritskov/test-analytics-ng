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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedEvent.DialogResult;
import com.google.testing.testify.risk.frontend.client.event.DialogClosedHandler;
import com.google.testing.testify.risk.frontend.client.view.ProjectSettingsView;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageSectionVerticalPanel;
import com.google.testing.testify.risk.frontend.client.view.widgets.StandardDialogBox;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc.ProjectAccess;
import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import java.util.List;

/**
 * A widget for controlling the settings of a project.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class ProjectSettingsViewImpl extends Composite implements ProjectSettingsView {

  interface ProjectSettingsViewImplUiBinder extends UiBinder<Widget, ProjectSettingsViewImpl> {}
  private static final ProjectSettingsViewImplUiBinder uiBinder =
          GWT.create(ProjectSettingsViewImplUiBinder.class);

  @UiField
  public TextBox projectName;

  @UiField
  public TextArea projectDescription;

  @UiField
  public CheckBox projectIsPublicCheckBox;

  @UiField
  public TextBox projectOwnersTextBox;

  @UiField
  public TextArea projectEditorsTextArea;

  @UiField
  public TextArea projectViewersTextArea;

  @UiField
  public Button updateProjectInfoButton;

  @UiField
  public HorizontalPanel savedPanel;

  @UiField
  public VerticalPanel publicPanel;

  @UiField
  public CheckBox deleteProjectCheckBox;

  @UiField
  PageSectionVerticalPanel deleteApplicationSection;

  // Handle to the underlying Presenter corresponding to this View.
  private Presenter presenter;

  private List<String> currentOwners;
  private List<String> currentEditors;
  private List<String> currentViewers;

  /**
   * Constructs a ProjectSettings object.
   */
  public ProjectSettingsViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("projectOwnersTextBox")
  protected void onOwnersChange(ChangeEvent event) {
    projectOwnersTextBox.setText(StringUtil.trimAndReformatCsv(projectOwnersTextBox.getText()));
  }

  @UiHandler("projectEditorsTextArea")
  protected void onEditorsChange(ChangeEvent event) {
    projectEditorsTextArea.setText(StringUtil.trimAndReformatCsv(projectEditorsTextArea.getText()));
  }

  @UiHandler("projectViewersTextArea")
  protected void onViewersChange(ChangeEvent event) {
    projectViewersTextArea.setText(StringUtil.trimAndReformatCsv(projectViewersTextArea.getText()));
  }

  /**
   * Handler for the updateProjectInfoButton's click event.
   */
  @UiHandler("updateProjectInfoButton")
  protected void onUpdateProjectInfoButtonClicked(ClickEvent event) {
    savedPanel.setVisible(false);
    updateProjectInfoButton.setEnabled(false);
    if (presenter != null) {
      if (deleteProjectCheckBox.getValue()) {
        presenter.removeProject();
        reloadPage();
      } else {
        final List<String> newOwners = StringUtil.csvToList(projectOwnersTextBox.getText());
        final List<String> newEditors = StringUtil.csvToList(projectEditorsTextArea.getText());
        final List<String> newViewers = StringUtil.csvToList(projectViewersTextArea.getText());

        if (newOwners.size() < 1) {
          Window.alert("Error: The project must have at least one owner.");
          return;
        }

        StringBuilder warning = new StringBuilder();
        List<String> difference = StringUtil.subtractList(newOwners, currentOwners);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Added owners:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }
        difference = StringUtil.subtractList(currentOwners, newOwners);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Removed owners:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }
        difference = StringUtil.subtractList(newEditors, currentEditors);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Added editors:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }
        difference = StringUtil.subtractList(currentEditors, newEditors);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Removed editors:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }
        difference = StringUtil.subtractList(newViewers, currentViewers);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Added viewers:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }
        difference = StringUtil.subtractList(currentViewers, newViewers);
        if (difference.size() > 0) {
          warning.append("<br><br><b>Removed viewers:</b> ");
          warning.append(SafeHtmlUtils.htmlEscape(StringUtil.listToCsv(difference)));
        }

        // If there's a warning to save, then display it and require confirmation before saving.
        // Otherwise, just save.
        if (warning.length() > 0) {
          StandardDialogBox box = new StandardDialogBox();
          box.setTitle("Permission Changes");
          box.add(new HTML("You are changing some of the permissions for this project."
              + warning.toString() + "<br><br>"));
          box.addDialogClosedHandler(new DialogClosedHandler() {
              @Override
              public void onDialogClosed(DialogClosedEvent event) {
                if (event.getResult().equals(DialogResult.OK)) {
                  presenter.onUpdateProjectInfoClicked(projectName.getText(),
                      projectDescription.getText(), newOwners, newEditors, newViewers,
                      projectIsPublicCheckBox.getValue());
                  currentOwners = newOwners;
                  currentEditors = newEditors;
                  currentViewers = newViewers;
                } else {
                  updateProjectInfoButton.setEnabled(true);
                }
              }
            });
          StandardDialogBox.showAsDialog(box);
        } else {
        presenter.onUpdateProjectInfoClicked(projectName.getText(), projectDescription.getText(),
            newOwners, newEditors, newViewers, projectIsPublicCheckBox.getValue());
        }
      }
    }
  }

  @Override
  public void showSaved() {
    updateProjectInfoButton.setEnabled(true);
    savedPanel.setVisible(true);
    Timer timer = new Timer() {
      @Override
      public void run() {
        savedPanel.setVisible(false);
      }
    };
    // Make the saved item disappear after 10 seconds.
    timer.schedule(10000);
  }

  /**
   * Handler for the deleteProjectButton's click event. Deletes the project.
   */
  @UiHandler("deleteProjectCheckBox")
  void onDeleteProjectCheckBoxChecked(ClickEvent event) {
    String warningMessage = "This will permanently delete your project when you click save."
      + " Are you sure?";
    if (!Window.confirm(warningMessage)) {
      deleteProjectCheckBox.setValue(false);
    }
  }

  /**
   * Reloads the current web page.
   */
  public void reloadPage() {
    History.newItem("/homepage");
  }

  /**
   * Binds this View to the given Presenter.
   */
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  /** Updates the view to enable editing project data given the access level. */
  @Override
  public void enableProjectEditing(ProjectAccess userAccessLevel) {
    // Enable certain controls only if they have OWNER access.
    if (userAccessLevel.hasAccess(ProjectAccess.OWNER_ACCESS)) {
      projectOwnersTextBox.setEnabled(true);
      deleteApplicationSection.setVisible(true);
      publicPanel.setVisible(true);
    }

    // Enable other widgets only if they have EDIT access.
    if (userAccessLevel.hasAccess(ProjectAccess.EDIT_ACCESS)) {
      projectName.setEnabled(true);
      projectDescription.setEnabled(true);
      projectEditorsTextArea.setEnabled(true);
      projectViewersTextArea.setEnabled(true);
      updateProjectInfoButton.setEnabled(true);
    }
  }

  /**
   * Updates the UI with the given project information.
   */
  @Override
  public void setProjectSettings(Project projectInformation) {
    currentOwners = projectInformation.getProjectOwners();
    currentEditors = projectInformation.getProjectEditors();
    currentViewers = projectInformation.getProjectViewers();

    projectName.setText(projectInformation.getName());
    projectDescription.setText(projectInformation.getDescription());

    projectOwnersTextBox.setText(StringUtil.listToCsv(currentOwners));
    projectEditorsTextArea.setText(StringUtil.listToCsv(currentEditors));
    projectViewersTextArea.setText(StringUtil.listToCsv(currentViewers));

    projectIsPublicCheckBox.setValue(projectInformation.getIsPubliclyVisible());
  }
}
