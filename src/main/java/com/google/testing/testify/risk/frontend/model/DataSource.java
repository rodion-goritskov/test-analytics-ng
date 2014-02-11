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

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents an option for importing data and its options.  For example, "Issue Tracker" would
 * be a DataSource and its parameters are options for search, such as "Owner".
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class DataSource implements Serializable {

  @SuppressWarnings("unused")
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String name;

  @Persistent
  private List<String> parameters;

  @Persistent
  private Boolean internalOnly;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setInternalOnly(Boolean internalOnly) {
    this.internalOnly = internalOnly;
  }

  public Boolean isInternalOnly() {
    return internalOnly;
  }

  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  public List<String> getParameters() {
    return parameters;
  }
}
