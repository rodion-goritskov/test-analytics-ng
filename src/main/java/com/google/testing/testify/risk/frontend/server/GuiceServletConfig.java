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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This creates a Guice injector which will be used to inject servlets and classes.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public class GuiceServletConfig extends GuiceServletContextListener {
  /**
   * Create and return a Guice injector.
   * 
   * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
   */
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new GuiceProviderModule(),
        new GuiceServletModule());
  }
}
