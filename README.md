BRAP - Binary Remoting and Authentication Protocol
==================================================

This is the readme file for BRAP.

This is not the original version of BRAP hosted at http://brap.tornado.no/

This is a fork of BRAP that uses Apache HttpClient instead of 
HttpURLConnection. The site for this fork of BRAP can be found here:
http://polopoly.github.com/brap/

BRAP is a Java remoting protocol that uses native Java object serialization,
encapsulated in HTTP.

It aims to be an alternative to Spring HttpInvoker and Spring Security
especially when you don't need or want the dependencies of Spring in your
client, for example when building a rich client application where size might
be an issue.

The authentication mechanism lets you use your own domain objects as
credentials.

BRAP gives you "pass by reference" even though the object arguments are
serialized and passed to the remote service - changes that happen on the
remote side can be applied to the client side automatically.

Server configuration can be expressed solely in web.xml or by subclassing the
ProxyServlet but there is also an optional SpringProxyServlet available for
seamless Spring Integration.

BRAP focuses on being easy to use, small in size, yet powerful and extensible.

Contents
--------
 - License
 - Java/Application server requirements
 - What is in this package
 - Dependencies
 - Building BRAP from source
 - Getting help

License
-------

BRAP is distributed under the terms of the Apache Software Foundation
license, version 2.0. The text is included in the file LICENSE in the root
of the project.

Java/Application server requirements
------------------------------------

BRAP requires at least Java 1.5. The application server for running your web
application where you expose your service should adhere to the servlet
specification version 2.3 or newer.

What is in this package
-----------------------

The archive you just downloaded and unpacked contains the source code and the
jars of the core projects of BRAP, including examples.

For creating a client, you need to include brap-client.jar and brap-common.jar.

For creating a server, you need to include brap-server.har and brap-common.jar.

For the optional Spring support on the server, you also need to include
brap-spring.jar.

For the optional modification support on the server, you also need to include
brap-modification.jar.

Dependencies
------------

The server needs a servlet container to run in. The only other dependency is the
Appache HttpClient 4.2 (http://hc.apache.org/)

The easiest way of getting the dependencies of your BRAP based projects right
is to use Apache Maven (http://maven.apache.org) with your projects and include
the BRAP dependencies you want.

Maven will then take care of including the appropriate dependencies.

If you do not want to use maven, just make sure you include the above mentioned
jars on your classpath.

Building BRAP from source
-------------------------

You can check out the source from subversion:

git clone git://github.com/polopoly/brap.git brap

Building using maven 2:

mvn install

Getting help
------------

 - The documentation can be found here: http://polopoly.github.com/brap/ 
 - Bugs and feature requests goes here: https://github.com/polopoly/brap/issues
