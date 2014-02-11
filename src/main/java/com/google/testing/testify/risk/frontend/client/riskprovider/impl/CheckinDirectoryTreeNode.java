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


package com.google.testing.testify.risk.frontend.client.riskprovider.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.testing.testify.risk.frontend.model.Checkin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Node in a tree representing a directory hierarchy as it applies to code checkins.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class CheckinDirectoryTreeNode {

  /** The name of this directory, e.g. "alpha" */
  private final String directoryName;

  /** Child directories. */
  private final HashMap<String, CheckinDirectoryTreeNode> childDirectories =
      new HashMap<String, CheckinDirectoryTreeNode>();

  /**
   * All checkins which have touched this directory or one of its children.
   *
   * NOTE: As you descend down the tree this will be strictly additive. For deep trees with many
   * checkins it might be more efficent to store a parent node and walk up the tree rebuilding the
   * set of checkins on each node.
   */
  private final Set<Checkin> checkins = new HashSet<Checkin>();

  /**
   * Creates a new root directory tree node.
   */
  public CheckinDirectoryTreeNode() {
    directoryName = "";
  }

  /**
   * Creates a new directory tree node with the given parent and directory name.
   */
  public CheckinDirectoryTreeNode(CheckinDirectoryTreeNode parent, String directoryName) {
    this.directoryName = directoryName;
  }

  public String getDirectoryName() {
    return directoryName;
  }

  public ImmutableMap<String, CheckinDirectoryTreeNode> getChildNodes() {
    ImmutableMap<String, CheckinDirectoryTreeNode> immutableMap =
        ImmutableMap.copyOf(childDirectories);
    return immutableMap;
  }

  public ImmutableSet<Checkin> getCheckins() {
    ImmutableSet<Checkin> immutableSet = ImmutableSet.copyOf(checkins);
    return immutableSet;
  }

  /**
   * Returns the list of all checkins that happen in the directory under this node. For example,
   * if the checkinDirectory was "alpha/beta" then it would return all bugs under:
   * this.getChildNodes().get("alpha").getChildNodes().get("beta").getCheckins()
   */
  public ImmutableSet<Checkin> getCheckinsUnder(String checkinDirectory) {
    if (checkinDirectory.trim().isEmpty()) {
      return getCheckins();
    }

    LinkedList<String> directoryNames = getDirectoryAsLinkedList(checkinDirectory);

    CheckinDirectoryTreeNode node = this;
    while (!directoryNames.isEmpty()) {
      String directoryName = directoryNames.remove();
      if (node.childDirectories.containsKey(directoryName)) {
        node = node.childDirectories.get(directoryName);
      } else {
        return ImmutableSet.of();
      }
    }

    return node.getCheckins();
  }

  /**
   * @return the input string represented as a linked list, split by '/' or '\' characters.
   */
  private LinkedList<String> getDirectoryAsLinkedList(String checkinDirectory) {
    // Normalize folder path separators.
    if (checkinDirectory.indexOf('|') != -1) {
      throw new IllegalArgumentException("The checkin directory contains an invalid character '|'");
    }
    checkinDirectory = checkinDirectory.replace('/', '|');
    checkinDirectory = checkinDirectory.replace('\\', '|');

    // Convert an array of directory names into a linked list for more efficent processing.
    String[] directories = checkinDirectory.split("|");
    LinkedList<String> list = Lists.newLinkedList();
    for (String s : directories) {
      list.add(s);
    }
    return list;
  }

  /**
   * Adds the given checkin to the directory tree, building child nodes as necessary.
   */
  public void addCheckin(String checkinDirectory, Checkin checkin) {
    LinkedList<String> directoryNames = getDirectoryAsLinkedList(checkinDirectory);

    addCheckin(directoryNames, checkin);
  }

  /**
   * Adds a checkin to the tree node under the given path of directory names.
   *
   * @param pathToCheckin List of directories left in the path. E.g. directory "foo\bar\ram" will
   * become "foo" -> "bar -> "ram"
   * @param checkin the checkin associated with the tree. Note that it will be attached to each
   * node up to the root. (Since the checkin is 'under' the root.)
   */
  private void addCheckin(LinkedList<String> pathToCheckin, Checkin checkin) {
    checkins.add(checkin);

    if (!pathToCheckin.isEmpty()) {
      // Get the head element and chop it from the list.
      String nextDirectory = pathToCheckin.remove();

      if (!childDirectories.containsKey(nextDirectory)) {
        CheckinDirectoryTreeNode newChild = new CheckinDirectoryTreeNode(this, nextDirectory);
        childDirectories.put(nextDirectory, newChild);
      }

      CheckinDirectoryTreeNode child = childDirectories.get(nextDirectory);
      child.addCheckin(pathToCheckin, checkin);
    }
  }
}
