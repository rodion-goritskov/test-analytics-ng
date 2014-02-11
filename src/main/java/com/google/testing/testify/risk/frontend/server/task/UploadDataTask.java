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


package com.google.testing.testify.risk.frontend.server.task;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.testing.testify.risk.frontend.model.Bug;
import com.google.testing.testify.risk.frontend.model.Checkin;
import com.google.testing.testify.risk.frontend.model.TestCase;
import com.google.testing.testify.risk.frontend.server.service.DataService;
import com.google.testing.testify.risk.frontend.shared.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This task processes a individual item to be uploaded (ie, one bug or one test) and inserts
 * it into the data store.
 *
 * The expected data are:
 *   - json (the json of ONE data item: bug, test, checkin)
 *   - user (the user to insert on behalf of)
 *
 * @author jimr@google.com (Jim Reardon)
 */
@Singleton
public class UploadDataTask extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(UploadDataTask.class.getName());

  public static final String QUEUE = "dataupload";
  public static final String URL = "/_tasks/upload";

  private final DataService dataService;

  @Inject
  public UploadDataTask(DataService dataService) {
    this.dataService = dataService;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    LOG.info("TestCaseUploadTask::GET called, returning unsupported exception.");
    error(resp, "<h1>GET is unsupported</h1>\nTo upload test cases, use POST.");
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String user = req.getParameter("user");
    String jsonString = req.getParameter("json");
    try {
      // TODO(jimr): add impersonation of user in string user.
      JSONObject json = new JSONObject(jsonString);
      JSONObject item;
      if (json.has("bug")) {
        item = json.getJSONObject("bug");
        Bug bug = new Bug();
        bug.setParentProjectId(item.getLong("projectId"));
        bug.setExternalId(item.getLong("bugId"));
        bug.setTitle(item.getString("title"));
        bug.setPath(item.getString("path"));
        bug.setSeverity(item.getLong("severity"));
        bug.setPriority(item.getLong("priority"));
        bug.setBugGroups(Sets.newHashSet(StringUtil.csvToList(item.getString("groups"))));
        bug.setBugUrl(item.getString("url"));
        bug.setState(item.getString("status"));
        bug.setStateDate(item.getLong("statusDate"));
        dataService.addBug(bug);
      } else if (json.has("test")) {
        item = json.getJSONObject("test");
        TestCase test = new TestCase();
        test.setParentProjectId(item.getLong("projectId"));
        test.setExternalId(item.getLong("testId"));
        test.setTitle(item.getString("title"));
        test.setTags(Sets.newHashSet(StringUtil.csvToList(item.getString("tags"))));
        test.setTestCaseUrl(item.getString("url"));
        test.setState(item.getString("result"));
        test.setStateDate(item.getLong("resultDate"));
        dataService.addTestCase(test);
      } else if (json.has("checkin")) {
        item = json.getJSONObject("checkin");
        Checkin checkin = new Checkin();
        checkin.setParentProjectId(item.getLong("projectId"));
        checkin.setExternalId(item.getLong("checkinId"));
        checkin.setSummary(item.getString("summary"));
        checkin.setDirectoriesTouched(
            Sets.newHashSet(StringUtil.csvToList(item.getString("directories"))));
        checkin.setChangeUrl(item.getString("url"));
        checkin.setState(item.getString("state"));
        checkin.setStateDate(item.getLong("stateDate"));
        dataService.addCheckin(checkin);
      } else {
        LOG.severe("No applicable data found for json: " + json.toString());
      }
    } catch (JSONException e) {
      // We don't issue a 500 or similar response code here to prevent retries, which would have
      // no different a result.
      LOG.severe("Couldn't parse input JSON: " + jsonString);
      return;
    }
  }

  private void error(HttpServletResponse resp, String errorText) throws IOException {
    resp.getOutputStream().print(errorText);
    resp.sendError(500);
    return;
  }
}
