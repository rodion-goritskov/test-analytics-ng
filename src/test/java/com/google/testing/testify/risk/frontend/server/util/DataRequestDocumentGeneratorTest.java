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


package com.google.testing.testify.risk.frontend.server.util;

import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataRequestOption;

import junit.framework.TestCase;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit tests for the DataRequestDocumentGeneratorTest class.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class DataRequestDocumentGeneratorTest extends TestCase {

  private static final String XML_HEADER =
    "<\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"\\?>";

  private static final String TA_OPEN = "<TestAnalytics>";
  private static final String TA_CLOSE = "</TestAnalytics>";
  private static final String TA_EMPTY = "<TestAnalytics/>";

  public void testZeroDataRequests() {
    List<DataRequest> requests = Lists.newArrayList();
    String result = DataRequestDocumentGenerator.generateDocument(requests);
    Pattern pattern = Pattern.compile(XML_HEADER + TA_EMPTY);
    Matcher matcher = pattern.matcher(result);
    assertTrue(matcher.matches());
  }

  public void testMultipleProjectsSameDataSource() {
    DataRequest project1Request = new DataRequest();
    project1Request.setParentProjectId(1L);
    project1Request.setDataSourceName("GoogleCodeBugs");

    DataRequest project2Request = new DataRequest();
    project2Request.setParentProjectId(2L);
    project2Request.setDataSourceName("GoogleCodeBugs");

    List<DataRequest> requests = Lists.newArrayList();
    requests.add(project1Request);
    requests.add(project2Request);

    String result = DataRequestDocumentGenerator.generateDocument(requests);
    List<String> expected = Lists.newArrayList(
        "<DataRequests ProjectID=\"1\"><DataRequest Type=\"GoogleCodeBugs\"/>",
        "<DataRequests ProjectID=\"2\"><DataRequest Type=\"GoogleCodeBugs\"/>");

    Pattern pattern = Pattern.compile(XML_HEADER + TA_OPEN + "(.*)" + TA_CLOSE);
    Matcher matcher = pattern.matcher(result);
    assertTrue(matcher.matches());
    List<String> actual = Lists.newArrayList((matcher.group(1)).split("</.*?>"));
    assertContentsAnyOrder("Contents don't match", actual, expected);
  }

  private <A> void assertContentsAnyOrder(String msg, List<A> actual, List<A> expected) {
    for (A a : actual) {
      assertTrue(msg, expected.contains(a));
    }
    for (A a : expected) {
      assertTrue(msg, actual.contains(a));
    }
  }

  public void testMultipleRequestsSameProject() {
    // Add two data requests to the same project. Both requesting from the same data source.
    DataRequest project1Request = new DataRequest();
    project1Request.setParentProjectId(1L);
    project1Request.setDataSourceName("GoogleCodeBugs");
    project1Request.getDataRequestOptions().add(new DataRequestOption("Alpha", "1111"));
    project1Request.getDataRequestOptions().add(new DataRequestOption("Beta", "3333"));

    DataRequest project2Request = new DataRequest();
    project2Request.setParentProjectId(1L);
    project2Request.setDataSourceName("GoogleCodeBugs");
    project2Request.getDataRequestOptions().add(new DataRequestOption("Alpha", "2222"));
    project2Request.getDataRequestOptions().add(new DataRequestOption("Beta", "4444"));

    List<DataRequest> requests = Lists.newArrayList();
    requests.add(project1Request);
    requests.add(project2Request);

    String result = DataRequestDocumentGenerator.generateDocument(requests);
    List<String> expected = Lists.newArrayList(
        "<Parameter Name=\"Beta\">3333",
        "<Parameter Name=\"Alpha\">1111",
        "<Parameter Name=\"Beta\">4444",
        "<Parameter Name=\"Alpha\">2222");

    Pattern pattern = Pattern.compile(XML_HEADER + TA_OPEN
        + "<DataRequests ProjectID=\"1\"><DataRequest Type=\"GoogleCodeBugs\">(.*)</DataRequest>"
        + "</DataRequests>" + TA_CLOSE);
    Matcher matcher = pattern.matcher(result);
    assertTrue(matcher.matches());
    List<String> actual = Lists.newArrayList((matcher.group(1)).split("</.*?>"));
    assertContentsAnyOrder("Contents don't match", actual, expected);
  }

  public void testDataRequestFields() {
    DataRequest request = new DataRequest();
    assertEquals(null, request.getRequestId());
    request.setRequestId(123L);
    assertEquals(123, request.getRequestId().longValue());
    assertEquals(null, request.getCustomName());
    request.setCustomName("test name");
    assertEquals("test name", request.getCustomName());
    assertEquals(0, request.getDataRequestOptions().size());
    DataRequestOption option = new DataRequestOption();
    option.setName("name2");
    option.setValue("value2");
    option.setId("drq1");
    option.setDataRequest(request);
    request.setDataRequestOptions(Lists.newArrayList(option,
        new DataRequestOption("name1", "value1")));
    assertEquals(2, request.getDataRequestOptions().size());
    assertTrue(request.getDataRequestOptions().contains(option));
    assertEquals("drq1", option.getId());
    assertEquals("name2", option.getName());
    assertEquals("value2", option.getValue());
    assertEquals(request, option.getDataRequest());
  }
}
