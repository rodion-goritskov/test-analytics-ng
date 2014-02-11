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


package com.google.testing.testify.risk.frontend.shared.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Common string functions.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class StringUtil {
  private static final Splitter UN_CSV = Splitter.on(",").trimResults().omitEmptyStrings();
  private static final Joiner TO_CSV = Joiner.on(", ").skipNulls();

  private StringUtil() {}  // COV_NF_LINE

  public static List<String> csvToList(String string) {
    if (string == null) {
      return Lists.newArrayList();
    }
    return Lists.newArrayList(UN_CSV.split(string));
  }

  public static String listToCsv(Collection<String> strings) {
    return TO_CSV.join(strings);
  }

  /**
   * Properly trims and formats a CSV string, removing extra space or blank entries.
   * EG: "wer, wer,,     wer" would turn into "wer, wer, wer".
   *
   * @param string the CSV string.
   * @return the re-formatted string.
   */
  public static String trimAndReformatCsv(String string) {
    return listToCsv(csvToList(string));
  }

  /**
   * Trims a string down to 500 characters. Ellipses will be added if truncated.
   *
   * @return text trimmed to 500 characters.
   */
  public static String trimString(String inputText) {
    if (inputText.length() <= 500) {
      return inputText;
    }

    return inputText.subSequence(0, 497) + "...";
  }

  /**
   * Returns items that are in a, but not in b.
   *
   * @param a the first list.
   * @param b the second list.
   * @return elements in a that are not in b.
   */
  public static List<String> subtractList(List<String> a, List<String> b) {
    List<String> result = Lists.newArrayList();
    for (String inA : a) {
      if (!b.contains(inA)) {
        result.add(inA);
      }
    }
    return result;
  }
}
