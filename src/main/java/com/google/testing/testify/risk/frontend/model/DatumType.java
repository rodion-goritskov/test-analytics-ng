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


package com.google.testing.testify.risk.frontend.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Supported data types.
 *
 * For each data type we keep track of how to refer to it in both a singular and plural sense.
 * We also track what parts of the datum support filtering.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public enum DatumType {
  /**
   * Filtering is currently executed inside the {@link Filter} class.  That, in turn, relies
   * upon each class that implements UploadedDatum to return any filterable field through the
   * <em>getField</em> method.
   */
  BUGS("Bugs", "Bug", Lists.newArrayList("Title", "Path", "Labels")),
  TESTS("Tests", "Test", Lists.newArrayList("Title", "Labels")),
  CHECKINS("Checkins", "Checkin", Lists.newArrayList("Summary", "Directories"));

  private final String plural;
  private final String singular;
  private final List<String> filterTypes;

  DatumType(String plural, String singular, List<String> filterTypes) {
    this.plural = plural;
    this.singular = singular;
    this.filterTypes = filterTypes;
  }

  public String getPlural() {
    return plural;
  }

  public String getSingular() {
    return singular;
  }

  public List<String> getFilterTypes() {
    return filterTypes;
  }
}
