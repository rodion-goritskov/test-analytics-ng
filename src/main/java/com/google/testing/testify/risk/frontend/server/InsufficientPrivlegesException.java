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

/**
 * Exception thrown when trying to access project information for which the current user does not
 * have sufficient permission.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class InsufficientPrivlegesException extends RuntimeException {

  private final String message;

  public InsufficientPrivlegesException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return "Error: You do not have sufficient privleges. " + message;
  }
}
