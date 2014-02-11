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

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Configuration object for gathering data from an external source.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
@PersistenceCapable(detachable = "true")
public class DataRequest implements Serializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long requestId;

  @Persistent
  private long parentProjectId;

  /** Name of the external data source. For example, "Google Code Bug Database". */
  @Persistent
  private String dataSourceName;

  /** A custom name if this is, for example, the "Other..." box. */
  @Persistent
  private String customName;

  /**
   * List of options for this data request.  For example, an option might be:
   *   Component Path -> { Project/Component/Path }
   */
  @Persistent(mappedBy = "dataRequest", defaultFetchGroup = "true")
  private List<DataRequestOption> dataRequestOptions = Lists.newArrayList();

  public void setRequestId(Long requestId) {
    this.requestId = requestId;
  }

  public Long getRequestId() {
    return requestId;
  }

  public void setParentProjectId(long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public long getParentProjectId() {
    return parentProjectId;
  }

  public void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public String getCustomName() {
    return customName;
  }

  public List<DataRequestOption> getDataRequestOptions() {
    return dataRequestOptions;
  }

  public void setDataRequestOptions(List<DataRequestOption> dataRequestOptions) {
    this.dataRequestOptions = dataRequestOptions;
  }
}
