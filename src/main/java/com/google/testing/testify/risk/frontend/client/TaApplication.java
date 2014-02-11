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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.testing.testify.risk.frontend.client.event.ProjectChangedEvent;
import com.google.testing.testify.risk.frontend.client.event.ProjectChangedHandler;
import com.google.testing.testify.risk.frontend.client.event.ProjectElementAddedEvent;
import com.google.testing.testify.risk.frontend.client.event.ProjectElementAddedHandler;
import com.google.testing.testify.risk.frontend.client.event.ProjectHasNoElementsEvent;
import com.google.testing.testify.risk.frontend.client.event.ProjectHasNoElementsHandler;
import com.google.testing.testify.risk.frontend.client.presenter.AttributesPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.BasePagePresenter;
import com.google.testing.testify.risk.frontend.client.presenter.CapabilitiesPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.CapabilityDetailsPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.ComponentsPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.ConfigureDataPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.ConfigureFiltersPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.KnownRiskPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.ProjectSettingsPresenter;
import com.google.testing.testify.risk.frontend.client.presenter.TaPagePresenter;
import com.google.testing.testify.risk.frontend.client.util.NotificationUtil;
import com.google.testing.testify.risk.frontend.client.view.impl.AttributesViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.CapabilitiesViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.CapabilityDetailsViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.ComponentsViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.ConfigureDataViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.ConfigureFiltersViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.KnownRiskViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.ProjectDataViewImpl;
import com.google.testing.testify.risk.frontend.client.view.impl.ProjectSettingsViewImpl;
import com.google.testing.testify.risk.frontend.client.view.widgets.NavigationLink;
import com.google.testing.testify.risk.frontend.client.view.widgets.ProjectFavoriteStar;
import com.google.testing.testify.risk.frontend.client.view.widgets.PageHeaderWidget;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.UploadedDatum;
import com.google.testing.testify.risk.frontend.model.Project;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.shared.rpc.DataRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.ProjectRpcAsync;
import com.google.testing.testify.risk.frontend.shared.rpc.UserRpcAsync;

import java.util.List;

/**
 * The main application for Test Analytics.  Contains the general view area which is populated
 * with pages that show data.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class TaApplication extends Composite {
  protected interface TestifyApplicationUiBinder extends UiBinder<Widget, TaApplication> {}

  private static final TestifyApplicationUiBinder uiBinder =
      GWT.create(TestifyApplicationUiBinder.class);

  @UiField
  protected SimplePanel contentPanel;

  @UiField
  protected ListBox userProjectsListBox;

  @UiField
  protected ProjectFavoriteStar projectFavoriteStar;

  @UiField
  protected Label projectNameLabel;

  @UiField
  protected NavigationLink projectDetailsLink;
  @UiField
  protected NavigationLink attributesLink;
  @UiField
  protected NavigationLink componentsLink;
  @UiField
  protected NavigationLink capabilitiesLink;
  @UiField
  protected NavigationLink configureDataLink;
  @UiField
  protected NavigationLink configureFiltersLink;
  @UiField
  protected NavigationLink projectBugsLink;
  @UiField
  protected NavigationLink projectCheckinsLink;
  @UiField
  protected NavigationLink projectTestcasesLink;
  @UiField
  protected NavigationLink knownRisksLink;
  // This link is treated differently because it does not show up in the sidebar.
  private NavigationLink capabilityDetailsLink = new NavigationLink("", -1, "capability-details",
      null);
  public static final String PAGE_HISTORY_TOKEN_CAPABILITY_DETAILS = "capability-details";

  private final List<NavigationLink> allLinks;

  private Project project;
  private final ProjectRpcAsync projectService;
  private final UserRpcAsync userService;
  private final DataRpcAsync dataService;

  /** EventBus for firing and subscribing to application-level events. */
  private final EventBus eventBus = new SimpleEventBus();

  /** Current page the application is on. */
  private NavigationLink currentLink;

  public TaApplication(Project project, ProjectRpcAsync projectService,
      UserRpcAsync userService, DataRpcAsync dataService) {
    this.projectService = projectService;
    this.userService = userService;
    this.dataService = dataService;
    initWidget(uiBinder.createAndBindUi(this));
    allLinks = Lists.newArrayList(
        projectDetailsLink, attributesLink, componentsLink, capabilitiesLink, configureDataLink,
        configureFiltersLink, projectBugsLink, projectCheckinsLink, projectTestcasesLink,
        knownRisksLink, capabilityDetailsLink);
    setProject(project);
    initializeToolbar();
    initializeMenuItems();
    hookupEventListeners();

    // Default page.
    switchToPage(projectDetailsLink, "");
  }

  @UiFactory
  public PageHeaderWidget createTestifyPageHeaderWidget() {
    return new PageHeaderWidget(userService);
  }

  @UiHandler("userProjectsListBox")
  protected void onSelectNewProject(ChangeEvent event) {
    int selectedIndex = userProjectsListBox.getSelectedIndex();
    History.newItem(
        "/" + userProjectsListBox.getValue(selectedIndex) +
        "/" + currentLink.getHistoryTokenName());
  }

  /**
   * Initialize toolbar widgets; the project list box (which contains projects the user has
   * explicit access to or are starred) and the star for the current project.
   */
  private void initializeToolbar() {
    final long currentProjectId = project.getProjectId();

    // Add the current project as a place holder while we load other projects the user cares about.
    userProjectsListBox.addItem(project.getName());
    projectService.queryUserProjects(
        new TaCallback<List<Project>>("querying projects") {
          @Override
          public void onSuccess(List<Project> result) {
            userProjectsListBox.clear();

            // The first item in the list box is always the 'current' project.
            userProjectsListBox.addItem(project.getName(), project.getProjectId().toString());
            for (Project p : result) {
              if (!p.getProjectId().equals(currentProjectId)) {
                userProjectsListBox.addItem(p.getName(), p.getProjectId().toString());
              }
            }
            userProjectsListBox.setSelectedIndex(0);
          }
        });

    // Initialize the project favorite star.
    projectFavoriteStar.attachToProject(project.getProjectId());

    userService.getStarredProjects(
        new TaCallback<List<Long>>("retrieving starred projects") {
          @Override
          public void onSuccess(List<Long> result) {
            if (result.contains(project.getProjectId())) {
              projectFavoriteStar.setStarredStatus(true);
            }
          }
        });

    // Initialize the project name.
    projectNameLabel.setText(project.getName());
  }

  private void setProject(Project project) {
    this.project = project;
  }

  public Project getProject() {
    return project;
  }

  public void switchToPage(String historyToken, String pageData) {
    // If there's no page specified, switch to our default project details page.
    if (historyToken == null || "".equals(historyToken)) {
      switchToPage(projectDetailsLink, "");
      return;
    }
    for (NavigationLink link : allLinks) {
      if (link.getHistoryTokenName().equals(historyToken)) {
        switchToPage(link, pageData);
        return;
      }
    }
    NotificationUtil.displayErrorMessage("The requested page is currently unavailable.");
  }

  /**
   * Switches the current view to the provided Testify application page.
   */
  public void switchToPage(NavigationLink link, String pageData) {
    NavigationLink oldLink = currentLink;
    this.currentLink = link;

    if (oldLink != null) {
      oldLink.unSelect();
    }
    link.select();

    TaPagePresenter presenter = link.getPresenter();
    if (presenter != null) {
      presenter.refreshView(pageData);
      setAsMainContent(presenter.getView());
    } else {
      // No presenter means something went awry.
      NotificationUtil.displayErrorMessage("The requested page is currently unavailable.");
    }
  }

  /** Initializes machinery related to navigation via menu items. */
  private void initializeMenuItems() {
    projectDetailsLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createProjectSettingsPage();
      }
    });

    attributesLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createAttributesPage();
      }
    });

    componentsLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createComponentsPage();
      }
    });

    capabilitiesLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createEditCapabilitiesPage();
      }
    });

    configureDataLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createConfigureDataPage();
      }
    });

    configureFiltersLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createFiltersPage();
      }
    });

    projectBugsLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createBugsPage();
      }
    });

    projectCheckinsLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createCheckinsPage();
      }
    });

    projectTestcasesLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createTestcasesPage();
      }
    });

    knownRisksLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createKnownRiskPage();
      }
    });

    capabilityDetailsLink.setCreatePresenterFunction(new Function<Void, TaPagePresenter>() {
      @Override
      public TaPagePresenter apply(Void input) {
        return createCapabilityDetailsPage();
      }
    });

    // Update menu item hyper links to include the project number.
    for (NavigationLink link : allLinks) {
      link.setProjectId(project.getProjectId());
    }
  }

  /**
   * Subscribes top-level UI elements to application-level events.
   */
  private void hookupEventListeners() {
    /**
     * Associate all navigation links with project stage. We can then enable or disable links based
     * on changes the user makes relative to their current project stage.
     */
    final List<NavigationLink> needFullAccModel = Lists.newArrayList(
        configureDataLink, configureFiltersLink, projectTestcasesLink, projectBugsLink,
        projectCheckinsLink, knownRisksLink);

    // If the project has no elements associated with stage n, disable all links to stages after n.
    eventBus.addHandler(ProjectHasNoElementsEvent.getType(),
      new ProjectHasNoElementsHandler() {
        @Override
        public void onProjectHasNoElements(ProjectHasNoElementsEvent event) {
          // Disable capabilities link if we don't have both attributes and components.
          if (event.projectHasNoAttributes() || event.projectHasNoComponents()) {
            capabilitiesLink.disable();
          }
          // If we're missing any ACC components, disable anything that requires a full model.
          if (event.projectHasNoAttributes() || event.projectHasNoCapabilities()
              || event.projectHasNoComponents()) {
            for (NavigationLink link : needFullAccModel) {
              link.disable();
            }
          }
        }
      });

    // If the project adds a new element, enable links to the next stage.
    eventBus.addHandler(ProjectElementAddedEvent.getType(),
        new ProjectElementAddedHandler() {
          @Override
          public void onProjectElementAdded(ProjectElementAddedEvent event) {
            if (event.isAttributeAddedEvent() || event.isComponentAddedEvent()) {
              capabilitiesLink.enable();
            } else if (event.isCapabilityAddedEvent()) {
              for (NavigationLink link : needFullAccModel) {
                link.enable();
              }
            }
          }
        });
  }

  /**
   * Displays the widget on the main content panel.
   */
  private void setAsMainContent(Widget widget) {
    if (widget != null) {
      contentPanel.clear();
      contentPanel.setWidget(widget);
    }
  }

  /**
   * Initializes the Presenter and View for Project Settings.
   */
  private TaPagePresenter createProjectSettingsPage() {
    ProjectSettingsViewImpl projectSettingsView = new ProjectSettingsViewImpl();
    ProjectSettingsPresenter projectSettingsPresenter = new ProjectSettingsPresenter(project,
        projectService, userService, projectSettingsView, eventBus);

    // Hook into the Project Settings Presenter's ProjectChanged event.
    eventBus.addHandler(ProjectChangedEvent.getType(),
        new ProjectChangedHandler() {
          @Override
          public void onProjectChanged(ProjectChangedEvent event) {
            setProject(event.getProject());
            initializeToolbar();
          }
        });
    return projectSettingsPresenter;
  }

  /**
   * Initializes the Presenter and View for Attributes page.
   */
  private AttributesPresenter createAttributesPage() {
    AttributesViewImpl attributesView = new AttributesViewImpl();
    AttributesPresenter attributesPresenter = new AttributesPresenter(
        project, projectService, userService, dataService, attributesView, eventBus);
    return attributesPresenter;
  }

  /**
   * Initializes the Presenter and View for the Components page.
   */
  private ComponentsPresenter createComponentsPage() {
    ComponentsViewImpl componentsView = new ComponentsViewImpl();
    ComponentsPresenter componentsPresenter = new ComponentsPresenter(
        project, projectService, userService, dataService, componentsView,
        eventBus);
    return componentsPresenter;
  }

  /**
   * Creates the tab which controls a Projects' Capabilities.
   */
  private CapabilitiesPresenter createEditCapabilitiesPage() {
    CapabilitiesViewImpl capabilitiesView = new CapabilitiesViewImpl();
    CapabilitiesPresenter capabilitiesPresenter = new CapabilitiesPresenter(
        project, projectService, userService, capabilitiesView, eventBus);
    return capabilitiesPresenter;
  }

  private CapabilityDetailsPresenter createCapabilityDetailsPage() {
    CapabilityDetailsViewImpl detailsView = new CapabilityDetailsViewImpl();
    CapabilityDetailsPresenter capabilityDetailsPresenter = new CapabilityDetailsPresenter(project,
        projectService, dataService, userService, detailsView);
    return capabilityDetailsPresenter;
  }

  /**
   * Initializes the Presenter and View for the Configure Data page.
   */
  private ConfigureDataPresenter createConfigureDataPage() {
    ConfigureDataViewImpl view = new ConfigureDataViewImpl();
    ConfigureDataPresenter configureDataPresenter = new ConfigureDataPresenter(project,
        dataService, view);
    return configureDataPresenter;
  }

  private ConfigureFiltersPresenter createFiltersPage() {
    ConfigureFiltersViewImpl view = new ConfigureFiltersViewImpl();
    ConfigureFiltersPresenter filtersPresenter = new ConfigureFiltersPresenter(project, dataService,
        projectService, view);
    return filtersPresenter;
  }

  /**
   * Returns a generic page presenter displaying the given view and performing the given
   * action when refreshView is called.
   */
  private TaPagePresenter createPagePresenter(
        final Widget mainView, final Function<Void, Void> onRefreshView) {
    final Label emptyLabel = new Label("");
    return new BasePagePresenter() {
        @Override
        public Widget getView() {
          return mainView;
        }

        @Override
        public void refreshView() {
          onRefreshView.apply(null);
        }
    };
  }

  /** Initializes the Presenter and View for the Bugs page. */
  private TaPagePresenter createBugsPage() {
    final Label emptyLabel = new Label();
    final ProjectDataViewImpl dataView = new ProjectDataViewImpl();
    dataView.setPageText(
      "Project Bugs",
      "The following bugs have been uploaded to your Test Analytics project.");

    Function<Void, Void> onRefreshPage =
        new Function<Void, Void>() {
          @Override
          public Void apply(Void input) {
            dataService.getProjectBugsById(getProject().getProjectId(),
                new TaCallback<List<Bug>>("querying project bugs") {
                  @Override
                  public void onSuccess(List<Bug> results) {
                    List<UploadedDatum> converted = Lists.newArrayList();
                    for (Bug item : results) {
                      converted.add(item);
                    }
                    dataView.displayData(converted);
                  }
                });
            return null;
          }
        };

    // Start an initial request for project bugs.
    onRefreshPage.apply(null);

    TaPagePresenter projectBugsPresenter = createPagePresenter(dataView, onRefreshPage);
    return projectBugsPresenter;
  }

  /** Initializes the Presenter and View for the Checkins page. */
  private TaPagePresenter createCheckinsPage() {
    final Label emptyLabel = new Label();
    final ProjectDataViewImpl dataView = new ProjectDataViewImpl();
    dataView.setPageText(
      "Project Checkins",
      "The following checkins have been uploaded to your Test Analytics project.");

    Function<Void, Void> onRefreshPage =
        new Function<Void, Void>() {
          @Override
          public Void apply(Void input) {
            dataService.getProjectCheckinsById(getProject().getProjectId(),
                new TaCallback<List<Checkin>>("querying project checkins") {
                  @Override
                  public void onSuccess(List<Checkin> results) {
                    List<UploadedDatum> converted = Lists.newArrayList();
                    for (Checkin item : results) {
                      converted.add(item);
                    }
                    dataView.displayData(converted);
                  }
                });
            return null;
          }
      };

    // Start an initial request for project checkins.
    onRefreshPage.apply(null);

    TaPagePresenter projectCheckinsPresenter = createPagePresenter(dataView, onRefreshPage);
    return projectCheckinsPresenter;
  }

  /** Initializes the Presenter and View for the testcases page. */
  private TaPagePresenter createTestcasesPage() {
    final Label emptyLabel = new Label();
    final ProjectDataViewImpl dataView = new ProjectDataViewImpl();
    dataView.setPageText(
        "Project Testcases",
        "The following testcases have been uploaded to your Test Analytics project.");

    Function<Void, Void> onRefreshPage =
        new Function<Void, Void>() {
          @Override
          public Void apply(Void input) {
            dataService.getProjectTestCasesById(getProject().getProjectId(),
                new TaCallback<List<TestCase>>("querying project testcases") {
                  @Override
                  public void onSuccess(List<TestCase> results) {
                    List<UploadedDatum> converted = Lists.newArrayList();
                    for (TestCase item : results) {
                      converted.add(item);
                    }
                    dataView.displayData(converted);
                  }
                });
            return null;
          }
        };

      // Start an initial request for project testcases.
      onRefreshPage.apply(null);

      TaPagePresenter projectTestcasesPresenter = createPagePresenter(dataView, onRefreshPage);
      return projectTestcasesPresenter;
    }

  /**
   * Initialize the Presenter and View for the Known Risks page.
   */
  private KnownRiskPresenter createKnownRiskPage() {
    KnownRiskViewImpl riskView = new KnownRiskViewImpl();
    KnownRiskPresenter knownRiskPresenter = new KnownRiskPresenter(project, projectService,
        riskView);
    return knownRiskPresenter;
  }
}
