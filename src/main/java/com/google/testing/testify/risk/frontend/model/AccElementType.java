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

/**
 * Enum that lists all the possible element types in an ACC model.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public enum AccElementType {
  ATTRIBUTE("Attribute"),
  COMPONENT("Component"),
  CAPABILITY("Capability");
  
  private String friendlyName;
  
  private AccElementType(String friendlyName) {
    this.friendlyName = friendlyName;
  }

  public String getFriendlyName() {
    return friendlyName;
  }
}
