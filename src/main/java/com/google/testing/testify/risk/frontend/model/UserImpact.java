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

/**
 * Enumeration for storing potential failure rates.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public enum UserImpact {
  NA (-2, "n/a"),
  MINIMAL (0, "Minimal"),
  SOME (1, "Some"),
  CONSIDERABLE (2, "Considerable"),
  MAXIMAL (3, "Maximal");

  final int ordinal;
  final String description;

  private UserImpact(int ordinal, String description) {
    this.ordinal = ordinal;
    this.description = description;
  }

  public int getOrdinal() {
    return ordinal;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Convert UserImpact.getDescription() into the original UserImpact instance. Enum.getValue
   * cannot be used because that requires the "ALL_CAPS" form of the value, rather than
   * the "User Friendly" getName version.
   */
  public static UserImpact fromDescription(String name) {
    for (UserImpact impact : UserImpact.values()) {
      if (impact.getDescription().equals(name)) {
        return impact;
      }
    }
    return null;
  }
}
