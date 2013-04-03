/**
 * @author Infosoft Global (P) Ltd.
 * @version 2.0 Added case insensitivity to export format - so JPG or jpg is now supported. Also, handled the case when width and height are not numbers.
 */
package com.fusioncharts.exporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.UUID;

import com.fusioncharts.exporter.beans.ChartMetadata;
import com.fusioncharts.exporter.beans.ExportBean;
import com.fusioncharts.exporter.beans.ExportConfiguration;
import com.fusioncharts.exporter.beans.FusionChartsExportData;

/**
 * Helps in the export process Contains File format to Mime type HashMap, Format
 * -> Extension HashMap, Handler Associations (for which format, which handler
 * should be used) Loads properties from properties file
 * fusioncharts_export.properties (if present in the classpath) Otherwise uses
 * default values. Contains functions to get the unique filename, parse the
 * parameters and create the ExportBean and much more.
 * 
 */
public class FusionChartsExportHelper {

	/*
	 * MIME TYPES
	 */
	private static HashMap<String, String> mimeTypes = new HashMap<String, String>();

	private static HashMap<String, String> extensions = new HashMap<String, String>();
	// private static final String HANDLERASSOCIATIONS=
	// "PDF=PDF;JPEG=IMG;JPG=IMG;PNG=IMG";
	private static HashMap<String, String> handlerAssociationsMap = new HashMap<String, String>();

	static {

		handlerAssociationsMap.put("PDF", "PDF");
		handlerAssociationsMap.put("JPEG", "IMG");
		handlerAssociationsMap.put("JPG", "IMG");
		handlerAssociationsMap.put("PNG", "IMG");
		handlerAssociationsMap.put("GIF", "IMG");

		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("pdf", "application/pdf");

		extensions.put("jpeg", "jpg");
		extensions.put("jpg", "jpg");
		extensions.put("png", "png");
		extensions.put("gif", "gif");
		extensions.put("pdf", "pdf");

	}

	/**
	 * Parses the parameters which are separated by | and returns a Hashmap
	 * containing the values.
	 * 
	 * @param strParams
	 *            Request parameter "parameters" which consists of all the
	 *            export parameters separated by |
	 * @return HashMap containing parameters as key value pair.
	 */
	public static HashMap<String, String> bang(String strParams) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (strParams != null) {
			StringTokenizer stPipe = new StringTokenizer(strParams, "|");
			String keyValue;
			String keyValueArr[];
			while (stPipe.hasMoreTokens()) {
				keyValue = stPipe.nextToken();
				keyValueArr = keyValue.split("=");
				if (keyValueArr.length > 1) {
					params.put(keyValueArr[0].toLowerCase(), keyValueArr[1]);
				}
			}
		}
		return params;
	}

	/**
	 * Constructs the path to the exporter resource.
	 * 
	 * @param strFormat
	 *            Format of the export to be handled
	 * @return String containing the path to the Handler Resource for this
	 *         format (Eg. com.fusioncharts.exporter.resources.FCExporter_IMG)
	 */
	public static String getExportHandlerClassName(String strFormat) {
		String exporterSuffix = handlerAssociationsMap.get(strFormat
				.toUpperCase());
		exporterSuffix = exporterSuffix != null ? exporterSuffix : strFormat
				.toUpperCase();
		String path = ExportConfiguration.RESOURCEPACKAGE + "."
				+ ExportConfiguration.EXPORTHANDLER
				+ exporterSuffix.toUpperCase();
		return path;
	}

	/**
	 * returns the extension for a particular format
	 * 
	 * @param format
	 * @return
	 */
	public static String getExtensionFor(String format) {
		return extensions.get(format);
	}

	/**
	 * @return the handlerAssociationsMap
	 */
	public static HashMap<String, String> getHandlerAssociationsMap() {
		return handlerAssociationsMap;
	}

	/**
	 * Returns the mime type for a particular format
	 * 
	 * @param format
	 * @return
	 */
	public static String getMimeTypeFor(String format) {
		return mimeTypes.get(format);
	}

	/**
	 * @return the mimeTypes
	 */
	public static HashMap<String, String> getMimeTypes() {
		return mimeTypes;
	}

	/**
	 * Gets the unique filename to save on the server
	 * 
	 * @param filePath
	 *            Complete path to the file without the extension in the
	 *            filename
	 * @param extension
	 *            extension of the file to be saved
	 * @return String complete path to the unique file that needs to be saved
	 *         along with extension
	 */
	public static String getUniqueFileName(String filePath, String extension) {
		UUID uuid = UUID.randomUUID();
		String uid = uuid.toString();
		String uniqueFileName = filePath + "." + extension;
		do {
			uniqueFileName = filePath;
			if (!ExportConfiguration.FILESUFFIXFORMAT
					.equalsIgnoreCase("TIMESTAMP")) {
				// put random number suffix
				uniqueFileName += uid + "_" + Math.random();
			} else {
				// put timestamp suffix
				SimpleDateFormat sdf = new SimpleDateFormat("dMyHms");
				String date = sdf.format(Calendar.getInstance().getTime());
				uniqueFileName += uid + "_" + date + "_"
						+ Calendar.getInstance().getTimeInMillis();
			}
			uniqueFileName += "." + extension;
		} while (new File(uniqueFileName).exists());
		return uniqueFileName;
	}

	/**
	 * Parses the request parameters and creates an ExportBean Note that, if
	 * some of the required parameters are not provided, the default values will
	 * be put in parameters
	 * 
	 * @param exportRequestStream
	 *            HttpServletRequest - used to get the parameters
	 * @return ExportBean the bean containing all the export related information
	 */
	public static ExportBean parseExportRequestStream(
			FusionChartsExportData exportData) {
		ExportBean exportBean = new ExportBean();
		// String of compressed data
		String stream = exportData.getStream();

		// Get all export parameters
		String parameters = exportData.getParameters();

		ChartMetadata metadata = new ChartMetadata();
		// get width and height of the chart
		String strWidth = exportData.getMeta_width();

		String strHeight = exportData.getMeta_height();
		try {
			metadata.setWidth(Double.parseDouble(strWidth));
			metadata.setHeight(Double.parseDouble(strHeight));
		} catch (NumberFormatException nfe) {
			// width or height provided is null or not a number
			metadata.setWidth(-1);
			metadata.setHeight(-1);
		}

		String bgColor = exportData.getMeta_bgColor();

		String DOMId = exportData.getMeta_DOMId();
		// DOM id of the chart
		metadata.setDOMId(DOMId);
		// Background color of chart
		metadata.setBgColor(bgColor);

		exportBean.setMetadata(metadata);
		exportBean.setStream(stream);

		HashMap<String, String> exportParamsFromRequest = bang(parameters);
		// ToDO Validate the params
		exportBean.addExportParametersFromMap(exportParamsFromRequest);

		return exportBean;
	}

}
