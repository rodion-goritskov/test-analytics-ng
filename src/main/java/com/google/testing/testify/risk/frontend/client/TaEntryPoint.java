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


package com.google.testing.testify.risk.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpc;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

/**
 * Entry point for Test Analytics application.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class TaEntryPoint implements EntryPoint {

  private Panel contentPanel;
  private ProjectRpcAsync projectService;
  private UserRpcAsync userService;
  private DataRpcAsync dataService;

  private static final String HOMEPAGE_HISTORY_TOKEN = "/homepage";

  // URL related error messages.
  private static final String INVALID_URL = "Invalid URL";
  private static final String INVALID_URL_TEXT = "The page could not be found.";
  private static final String INVALID_PROJECT_ID_TEXT = "No project with that ID was found.";

  // Project-load related error messages.
  private static final String ERROR_LOADING_PROJECT = "Error loading project";
  private static final String ERROR_LOADING_PROJECT_TEXT =
      "There was an error loading the requested project";
  private static final String PROJECT_NOT_FOUND = "Project not found";
  private static final String PROJECT_ID_NOT_FOUND_TEXT = "No project with that ID was found.";

  /** UI for the currently opened project. */
  private TaApplication currentApplicationInstance = null;

  /**
   * Entry point for the application.
   */
  @Override
  public void onModuleLoad() {
    projectService = GWT.create(ProjectRpc.class);
    userService = GWT.create(UserRpc.class);
    dataService = GWT.create(DataRpc.class);

    contentPanel = new LayoutPanel();
    RootLayoutPanel.get().add(contentPanel);

    // Handle history changes. (Such as clicking a navigation hyperlink.)
    History.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        handleUrl(historyToken);
      }
    });

    handleUrl(History.getToken());
  }

  /**
   * Handles the URL, updating any UI elements for the current Testify application if necessary.
   */
  private void handleUrl(String url) {
    // Intercept well-known pages.
    if ((url.length() == 0) || ("/".equals(url)) || (HOMEPAGE_HISTORY_TOKEN.equals(url))) {
      displayHomePage();
      return;
    }

    // Expected format: "/project-id/target-page"
    // or: "/project-id/target-page/page-data" (project data is passed into the project page).
    // Thus part 0 will be an empty string, part 1 will be the project ID, part 2 the page name,
    // and part 3 the page data.
    String[] parts = url.split("/");
    if ((parts.length < 2) || (parts[0].length() != 0)) {
      displayErrorPage(INVALID_URL, INVALID_URL_TEXT);
      return;
    }

    // The first part, the project ID.
    Long projectId = tryParseLong(parts[1]);
    if (projectId == null) {
      displayErrorPage(INVALID_URL, INVALID_PROJECT_ID_TEXT);
      return;
    }

    // Page name.
    String pageName = (parts.length > 2 ? parts[2] : null);
    // Page has data that should be sent to the target page.
    String pageData = (parts.length > 3 ? parts[3] : null);

    // Attempt to switch the page view.
    if (currentApplicationInstance != null &&
        (currentApplicationInstance.getProject().getProjectId().equals(projectId))) {
      currentApplicationInstance.switchToPage(pageName, pageData);
    } else {
      // Otherwise, attempt to load the project and switch the view.
      loadProjectAndSwitchToUrl(projectId, url);
    }
  }

  /**
   * Performs an asynchronous load of a project with the given ID name. Afterwards, navigates to the
   * target URL.
   */
  private void loadProjectAndSwitchToUrl(final long projectId, final String targetUrl) {
    projectService.getProjectById(projectId,
      new AsyncCallback<Project>() {
        @Override
        public void onFailure(Throwable caught) {
          displayErrorPage(ERROR_LOADING_PROJECT, ERROR_LOADING_PROJECT_TEXT);
        }

        @Override
        public void onSuccess(Project result) {
          if (result == null) {
            displayErrorPage(PROJECT_NOT_FOUND, PROJECT_ID_NOT_FOUND_TEXT);
          } else {
            displayProjectView(result);
            handleUrl(targetUrl);
          }
        }
      });
  }

  /**
   * Switches the application's view to displays an error message.
   */
  private void displayErrorPage(String errorType, String errorMessage) {
    GWT.log("General error: " + errorType);

    GeneralErrorPage errorPage = new GeneralErrorPage();
    errorPage.setErrorType(errorType);
    errorPage.setErrorText(errorMessage);

    setPageContent(errorPage);
  }

  /**
   * Switches the application's view to the home page.
   */
  private void displayHomePage() {
    final HomePage homePage = new HomePage(projectService, userService);
    setPageContent(homePage);
  }

  /**
   * Switches the application's view to a specific Project.
   */
  public void displayProjectView(Project project) {
    // All projects must be serialized (have a Project ID) first.
    if (project.getProjectId() == null) {
      return;
    }

    GWT.log("Switching to view project " + project.getProjectId().toString());
    currentApplicationInstance = new TaApplication(project, projectService, userService,
        dataService);

    setPageContent(currentApplicationInstance);
  }

  /**
   * Sets the GWT page to display the provided content.
   */
  private void setPageContent(Widget content) {
    contentPanel.clear();
    contentPanel.add(content);
  }

  /**
   * Attempts to parse the given string as integer, if not returns null.
   */
  private Long tryParseLong(String text) {
    Long result = null;
    try {
      return Long.parseLong(text);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
}
