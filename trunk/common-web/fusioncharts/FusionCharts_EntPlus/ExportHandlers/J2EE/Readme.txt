FusionCharts Server-side Export Handler - J2EE
==============================================

For exporting the chart as image/pdf at server side using JSP, the following library files are required on your server:

1. fcexporter.jar (contains all the dependency classes)
2. fcexporthandler.jar (contains the export handler servlet and resources)
3. /classes/fusioncharts_export.properties (configuration file)

Setup
-----

Step1: Copy the neccessary files
Please place the export jars fcexporter.jar and fcexporthandler.jar in WEB-INF/lib and fusioncharts_export.properties in WEB-INF/classes folder.

FusionCharts Exporter has been tested with JDK 1.5.
Note that the FusionCharts Exporter jars for jdk1.4.2 is also available in ExportHandlers/JDK1.4 folder

Step2: Configure web.xml
Add the following servlet mapping in your application's web.xml

  <servlet>
    <display-name>FCExporter</display-name>
    <servlet-name>FCExporter</servlet-name>
    <servlet-class>com.fusioncharts.exporter.servlet.FCExporter</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>FCExporter</servlet-name>
    <url-pattern>/JSP/ExportExample/FCExporter</url-pattern>
  </servlet-mapping>
Please modify the url-pattern as per your application needs.
Step3: 
Specify the xml attribute exportHandler='FCExporter' assuming that the jsp rendering the chart is present in /JSP/ExportExample folder. 

Step4: 
Configuration of save folder for server-side save
--------------------------------------------------
This is to be done in fusioncharts_export.properties file. Make sure that the folder path that you specify
has write permissions to it. 

