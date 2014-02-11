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


package com.google.testing.testify.risk.frontend.model;

import java.io.Serializable;

/**
 * Contains the status of the current user (logged in, logged out, email, et cetera).
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class LoginStatus implements Serializable {

  private boolean isLoggedIn;
  private String url;
  private String email;

  public LoginStatus() { }

  /** Disable construction, see {@See LoggedInStatus} and {@See LoggedOutStatus}. */
  public LoginStatus(boolean isLoggedIn, String url, String email) {
    this.isLoggedIn = isLoggedIn;
    this.url = url;
    this.email = email;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setIsLoggedIn(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }

  public boolean getIsLoggedIn() {
    return isLoggedIn;
  }
}
