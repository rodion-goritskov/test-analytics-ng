test-analytics-ng
=================

Fork of the Google Test Analytics project

How to build?
----------------
You need Maven (http://maven.apache.org/) to build the test-analytics-ng project.

1. Clone this repo

2. If you have Maven version 3.0.X change the appengine-maven-plugin version in pom.xml to 1.8.3-maven3.0

3. Run 
>mvn compile

4. Run 
>mvn package

How to run development server to see how it works?
-----------------

You can take a brief tour through test-analytics-ng by making steps 1 and 2 of the previous section and then running:

>mvn appengine:devserver

Deploying test-analytics-ng in AppScale
-------

AppScale (http://www.appscale.com/) is an open-source GAE (Google App Engine) implementation. You can deploy and use test-analytics-ng in it.

How-to coming soon. 