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


package com.google.testing.testify.risk.frontend.shared.util;

import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the StringUtil class.
 *
 * @author jimr@google.com (Jim Reardon)
 */
public class StringUtilTest extends TestCase {

  public void testBreakCommaSeperatedStringsWithNullEmpty() {
    List<String> nullString = StringUtil.csvToList(null);
    assertEquals(0, nullString.size());

    List<String> emptyString = StringUtil.csvToList("     ");
    assertEquals(0, emptyString.size());
  }

  public void testBreakCommaSeperatedStringsWithGoodInput() {
    List<String> test = StringUtil.csvToList(" A , B, ,,C, D");
    assertEquals(4, test.size());
    assertEquals("A", test.get(0));
    assertEquals("B", test.get(1));
    assertEquals("C", test.get(2));
    assertEquals("D", test.get(3));
  }

  public void testTrim_tooSmall() {
    String subString = "0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 49; i++) {
      sb.append(subString);
    }
    String string = sb.toString();

    assertEquals(string.toString(), StringUtil.trimString(string.toString()));
  }

  public void testTrim_tooBig() {
    String subString = "0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 51; i++) {
      sb.append(subString);
    }
    String string = sb.toString();
    sb = new StringBuilder();
    for (int i = 0; i < 49; i++) {
      sb.append(subString);
    }
    sb.append("0123456...");
    String expectedString = sb.toString();

    String result = StringUtil.trimString(string);
    assertEquals(expectedString, result);
    assertEquals(500, result.length());
  }

  public void testTrim_justRight() {
    String subString = "0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      sb.append(subString);
    }
    String string = sb.toString();

    assertEquals(string, StringUtil.trimString(string));
  }

  public void testTrimAndReformat() {
    String val = StringUtil.trimAndReformatCsv("  one ,two,    three,four   ,,,five");
    assertEquals("one, two, three, four, five", val);
  }

  public void testSubtractList() {
    List<String> a = Lists.newArrayList("one", "two", "three");
    List<String> b = Lists.newArrayList("two", "four");
    List<String> res = StringUtil.subtractList(a, b);
    assertTrue(res.contains("one"));
    assertTrue(res.contains("three"));
    assertEquals(res.size(), 2);
    res = StringUtil.subtractList(b,  a);
    assertEquals(res.size(), 1);
    assertTrue(res.contains("four"));
    res = StringUtil.subtractList(b,  new ArrayList<String>());
    assertEquals(res.size(), 2);
    assertTrue(res.contains("two"));
    assertTrue(res.contains("four"));
    res = StringUtil.subtractList(new ArrayList<String>(), b);
    assertEquals(res.size(), 0);
    b = Lists.newArrayList("four", "five");
    res = StringUtil.subtractList(a, b);
    assertEquals(res.size(), 3);
    assertTrue(res.contains("one"));
    assertTrue(res.contains("two"));
    assertTrue(res.contains("three"));
  }
}
