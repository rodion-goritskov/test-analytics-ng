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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.server.service.ProjectService;
import com.google.testing.testify.risk.frontend.server.service.UserService;
import com.google.testing.testify.risk.frontend.server.service.impl.DataServiceImpl;
import com.google.testing.testify.risk.frontend.server.service.impl.ProjectServiceImpl;
import com.google.testing.testify.risk.frontend.server.service.impl.UserServiceImpl;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Sets up injectable classes for Guice. Items listed here can be injected into the constructors
 * of servlets.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class GuiceProviderModule extends AbstractModule {
  @Provides @RequestScoped @Inject
  PersistenceManager getPersistenceManager(PersistenceManagerFactory pmf) {
    return pmf.getPersistenceManager();
  }

  @Provides @Singleton
  PersistenceManagerFactory getPersistenceManagerFactory() {
    return JDOHelper.getPersistenceManagerFactory("transactions-optional");
  }

  @Override
  protected void configure() {
    bind(DataService.class).to(DataServiceImpl.class);
    bind(ProjectService.class).to(ProjectServiceImpl.class);
    bind(UserService.class).to(UserServiceImpl.class);
  }
}
