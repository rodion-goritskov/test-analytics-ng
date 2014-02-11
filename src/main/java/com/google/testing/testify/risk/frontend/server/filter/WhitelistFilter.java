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


package com.google.testing.testify.risk.frontend.server.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.server.service.UserService;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filters access to the application based off the whitelist field in a UserInfo object.
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class WhitelistFilter implements Filter {

  private static final Logger log = Logger.getLogger(WhitelistFilter.class.getName());
  private final UserService userService;

  @Inject
  public WhitelistFilter(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    if (userService.isWhitelistingEnabled() && !userService.isWhitelisted()) {
      String email = userService.getEmail();
      if (email == null) {
        email = "(null)";
      }
      log.warning("User not whitelisted tried to access server: " + email);
      httpResponse.getWriter().write("404 Not Found");
      httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig arg0) {
  }
}
