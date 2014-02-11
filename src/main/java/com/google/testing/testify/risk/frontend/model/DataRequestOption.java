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

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * An individual configuration option for a data request.  For example, this is an example of
 * a data request option for a Bug
 * 
 * name = "ComponentPath"
 * value = "\Testing\Tools\Test Analytics"
 * 
 * This would import any bug under that component path.
 * 
 * or
 * 
 * name = "Hotlist"
 * value = "123123"
 * 
 * This would import the bugs in hotlist 123123.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
@PersistenceCapable(detachable = "true")
public class DataRequestOption implements Serializable {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
  private String id;
  
  @Persistent
  private String name;

  @Persistent
  private String value;
  
  @Persistent
  private DataRequest dataRequest;

  public DataRequestOption() {
  }

  public DataRequestOption(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DataRequest getDataRequest() {
    return dataRequest;
  }

  public void setDataRequest(DataRequest dataRequest) {
    this.dataRequest = dataRequest;
  }
}
