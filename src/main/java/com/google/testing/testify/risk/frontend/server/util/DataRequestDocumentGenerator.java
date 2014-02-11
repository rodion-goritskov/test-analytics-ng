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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.testing.testify.risk.frontend.model.DataRequest;
import com.google.testing.testify.risk.frontend.model.DataRequestOption;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Generates an XML document for external data collectors.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class DataRequestDocumentGenerator {
  private DataRequestDocumentGenerator() {}  // COV_NF_LINE

  /**
   * Returns an XML document describing the data requests.
   */
  public static String generateDocument(List<DataRequest> allDataRequests) {
    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document document = docBuilder.newDocument();

      Element documentRoot = document.createElement("TestAnalytics");
      document.appendChild(documentRoot);

      // Group all requests by their parent project.
      Multimap<Long, DataRequest> requestsByProject = getRequestsByProject(allDataRequests);
      for (Long projectId : requestsByProject.keySet()) {
        Element projectElement = document.createElement("DataRequests");
        projectElement.setAttribute("ProjectID", Long.toString(projectId));
        documentRoot.appendChild(projectElement);

        // Group project requests by data source.
        Collection<DataRequest> projectRequests = requestsByProject.get(projectId);
        Multimap<String, DataRequest> requestsBySource = getRequestsByDataSource(projectRequests);
        for (String sourceName : requestsBySource.keySet()) {
          Element dataSourceElement = document.createElement("DataRequest");
          dataSourceElement.setAttribute("Type", sourceName);
          projectElement.appendChild(dataSourceElement);

          // Write out the configuration parameter strings for the data source.
          for (DataRequest request : requestsBySource.get(sourceName)) {
            for (DataRequestOption option : request.getDataRequestOptions()) {
              Element dataSourceParameter = document.createElement("Parameter");
              dataSourceParameter.setAttribute("Name", option.getName());
              dataSourceParameter.appendChild(document.createTextNode(option.getValue()));
              dataSourceElement.appendChild(dataSourceParameter);
            }
          }
        }
      }

      // Now dump the document in memory to a string.
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      DOMSource source = new DOMSource(document);
      StreamResult result = new javax.xml.transform.stream.StreamResult(new StringWriter());

      transformer.transform(source, result);
      return result.getWriter().toString();
      // COV_NF_START
    } catch (TransformerConfigurationException tce) {
      return "Error in transformer configuration.";
    } catch (TransformerException te) {
      return "Error transforming document.";
    } catch (ParserConfigurationException pce) {
      return "Error in parser configuration.";
    }
    // COV_NF_END
  }

  private static Multimap<Long, DataRequest> getRequestsByProject(
      Collection<DataRequest> requests) {
    Multimap<Long, DataRequest> requestsByProject = HashMultimap.create();
    for (DataRequest request : requests) {
      requestsByProject.put(request.getParentProjectId(), request);
    }
    return requestsByProject;
  }

  private static Multimap<String, DataRequest> getRequestsByDataSource(
      Collection<DataRequest> requests) {
    Multimap<String, DataRequest> requestsBySource = HashMultimap.create();
    for (DataRequest request : requests) {
      requestsBySource.put(request.getDataSourceName(), request);
    }

    return requestsBySource;
  }
}
