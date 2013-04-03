/**
 * 
 */
package com.fusioncharts.exporter.beans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Class to hold the configuration of export. The properties have to be loaded
 * using the loadProperties static method. For example, the servlet does the job
 * of loading the properties once in its init method.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class ExportConfiguration {

	private static Logger logger = null;

	// This constant defines the name of the export handler file
	// The name is appended with a suffix (from the map handlerAssociationsMap)
	public static String EXPORTHANDLER = "FCExporter_";

	/*
	 * ------------------------- EXPORT RESOURCES
	 * --------------------------------
	 */

	// Package name of the resource handlers
	// By default the path is "com.fusioncharts.exporter.resources"
	public static String RESOURCEPACKAGE = "com.fusioncharts.exporter.resources";
	/**
	 * IMPORTANT: You need to change the location of folder where the exported
	 * chart images/PDFs will be saved on your server. Please specify the path
	 * to a folder with write permissions in the constant SAVE_PATH below. This
	 * path is relative to the web application root
	 */

	public static String SAVEPATH = "./";
	/**
	 * The absolute path to the save folder, SAVEPATH and SAVEABSOLUTEPATH will
	 * have same value if user provides absolute path
	 */
	public static String SAVEABSOLUTEPATH = "./";

	/**
	 * IMPORTANT: This constant HTTP_URI stores the HTTP reference to the folder
	 * where exported charts will be saved. Please enter the HTTP representation
	 * of that folder in this constant e.g., http://www.yourdomain.com/
	 */
	public static String HTTP_URI = "http://yourdomain.com/";

	/**
	 * Not being used
	 */
	public static String TMPSAVEPATH = "";

	/*
	 * ---------- Export Settings -------------------------------- OVERWRITEFILE
	 * sets whether the export handler would overwrite an existing file the
	 * newly created exported file. If it is set to false the export handler
	 * would not overwrite. In this case if INTELLIGENTFILENAMING is set to true
	 * the handler would add a suffix to the new file name. The suffix is a
	 * randomly generated UUID. Additionally, you can add a timestamp or random
	 * number as additional prefix.
	 */
	public static boolean OVERWRITEFILE = false;

	public static boolean INTELLIGENTFILENAMING = true;

	public static String FILESUFFIXFORMAT = "TIMESTAMP"; // can be TIMESTAMP or
	// RANDOM
	static {
		logger = Logger.getLogger(ExportConfiguration.class.getName());
	}

	public static void loadProperties() {

		// create an instance of properties class

		Properties props = new Properties();

		// try retrieve data from file
		try {

			props.load(ExportConfiguration.class
					.getResourceAsStream("/fusioncharts_export.properties"));

			EXPORTHANDLER = props.getProperty("EXPORTHANDLER", EXPORTHANDLER);
			RESOURCEPACKAGE = props.getProperty("RESOURCEPACKAGE",
					RESOURCEPACKAGE);
			SAVEPATH = props.getProperty("SAVEPATH", SAVEPATH);

			HTTP_URI = props.getProperty("HTTP_URI", HTTP_URI);
			TMPSAVEPATH = props.getProperty("TMPSAVEPATH", TMPSAVEPATH);

			/*
			 * ---------- Export Settings --------------------------------
			 * OVERWRITEFILE sets whether the export handler would overwrite an
			 * existing file the newly created exported file. If it is set to
			 * false the export handler would not overwrite. In this case if
			 * INTELLIGENTFILENAMING is set to true the handler would add a
			 * suffix to the new file name. The suffix is a randomly generated
			 * UUID. Additionally, you can add a timestamp or random number as
			 * additional prefix.
			 */

			String OVERWRITEFILESTR = props.getProperty("OVERWRITEFILE",
					"false");
			OVERWRITEFILE = new Boolean(OVERWRITEFILESTR).booleanValue();

			String INTELLIGENTFILENAMINGSTR = props.getProperty(
					"INTELLIGENTFILENAMING", "true");
			INTELLIGENTFILENAMING = new Boolean(INTELLIGENTFILENAMINGSTR)
					.booleanValue();

			FILESUFFIXFORMAT = props.getProperty("FILESUFFIXFORMAT",
					FILESUFFIXFORMAT);

		} catch (NullPointerException e) {
			logger.info("NullPointer: Properties file not FOUND");
			// e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.info("Properties file not FOUND");

			// e.printStackTrace();
		}
		// TODO catch exception in case properties file does not exist
		catch (IOException e) {
			logger.info("IOException: Properties file not FOUND");
			// e.printStackTrace();
		}

	}

}
