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


package com.google.testing.testify.risk.frontend.client.util;

import com.google.common.collect.ImmutableList;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.List;

/**
 * Helper functions for links.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class LinkUtil {

  private static final List<String> PROTOCOL_WHITELIST = ImmutableList.of("http", "https");

  /**
   * Returns the domain of a link, for displaying next to link.  Examples:
   *  http://ourbugsoftware/1239123 -> [ourbugsoftware/]
   *  http://testcases.example/mytest/1234 -> [testcases.example/]
   *
   * If the protocol isn't whitelisted (see PROTOCOL_WHITELIST) or the URL can't be parsed,
   * this will return null.
   *
   * @param link the full URL
   * @return the host of the URL.  Null if protocol isn't in PROTOCOL_WHITELIST or URL can't be
   * parsed.
   */
  public static String getLinkHost(String link) {
    if (link != null) {
      // It doesn't seem as if java.net.URL is GWT-friendly.  Thus...  GWT regular expressions!
      RegExp regExp = RegExp.compile("(\\w+?)://([\\-\\.\\w]+?)/.*");
      // toLowerCase is okay because nothing we're interested in is case sensitive.
      MatchResult result = regExp.exec(link.toLowerCase());

      if (result != null) {
        String protocol = result.getGroup(1);
        String host = result.getGroup(2);
        if (PROTOCOL_WHITELIST.contains(protocol)) {
          return "[" + host + "/]";
        }
      }
    }
    return null;
  }
}
