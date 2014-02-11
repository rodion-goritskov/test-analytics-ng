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


package com.google.testing.testify.risk.frontend.server;

import com.google.inject.servlet.ServletModule;
import com.google.testing.testify.risk.frontend.server.api.impl.DataApiImpl;
import com.google.testing.testify.risk.frontend.server.api.impl.UploadApiImpl;
import com.google.testing.testify.risk.frontend.server.filter.WhitelistFilter;
import com.google.testing.testify.risk.frontend.server.rpc.impl.DataRpcImpl;
import com.google.testing.testify.risk.frontend.server.rpc.impl.ProjectRpcImpl;
import com.google.testing.testify.risk.frontend.server.rpc.impl.TestProjectCreatorRpcImpl;
import com.google.testing.testify.risk.frontend.server.rpc.impl.UserRpcImpl;
import com.google.testing.testify.risk.frontend.server.task.UploadDataTask;

/**
 * Guice module to inject servlets.  This maps URLs (the first part of the map) to classes (the
 * second).
 *
 * Note guice servlets must be singletons.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    // GWT services.
    serve("/service/project").with(ProjectRpcImpl.class);
    serve("/service/user").with(UserRpcImpl.class);
    serve("/service/data").with(DataRpcImpl.class);
    serve("/service/testprojectcreator").with(TestProjectCreatorRpcImpl.class);
    // The external API.
    serve("/api/data").with(DataApiImpl.class);
    serve("/api/upload").with(UploadApiImpl.class);
    // Administrative tasks, migration tasks, and crons.  These must be locked down to admin
    // only rights, because they may allow user impersonation.
    serve("/_tasks/upload").with(UploadDataTask.class);

    // Do not filter special pages, like /_tasks/ or /_cron/, which are already protected as
    // admin-only through web.xml.  This allows crons/tasks to run.
    filterRegex("/[^_].*", "/$").through(WhitelistFilter.class);
  }
}
