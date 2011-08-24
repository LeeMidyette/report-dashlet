Report dashlet for Alfresco Share
=======================================================

Description
-----------
This jar provides Report Dashlet. This provides
information (chart and table) from repository contents stacked by some property value.


Author
------
Pedro Salido López (psalido@dashlet.info - http://www.dashlet.info)


Installation
------------

The component has been developed to install on top of an existing Alfresco 3.4 installation.

An Ant build script is provided to build a JAR file containing the 
custom files, which can then be installed into the 'tomcat/shared/lib' folder 
of your Alfresco installation.

To build the JAR file, run the following command from the base project 
directory.

    ant clean dist-jar

The command should build a JAR file named report-dashlet.jar
in the 'dist' directory within your project.

To install the component, drop the report-dashlet.jar file into the following two 
directories within your Alfresco installation, and restart the application server.

	tomcat/shared/lib
	OR
    tomcat/webapps/share/WEB-INF/lib
    
Once you have run this you will need to restart Tomcat so that the classpath 
resources in the JAR file are picked up.

Using the component
-------------------

Log in to Alfresco Share as an admin user and customize dashboard adding new Report Dashlet