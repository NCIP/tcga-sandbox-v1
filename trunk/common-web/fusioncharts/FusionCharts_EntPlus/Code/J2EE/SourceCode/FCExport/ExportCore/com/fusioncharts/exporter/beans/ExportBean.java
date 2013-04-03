/**
 * 
 */
package com.fusioncharts.exporter.beans;

import java.util.HashMap;
import java.util.Iterator;

import com.fusioncharts.exporter.FusionChartsExportHelper;
import com.fusioncharts.exporter.error.LOGMESSAGE;

/**
 * Contains all the information required during the export process like chart
 * metadata, chart image data and export parameters
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class ExportBean {
	private ChartMetadata metadata;
	private String stream;
	private HashMap<String, Object> exportParameters = null;

	/**
	 * Initializes the default values for the export parameters
	 */
	public ExportBean() {
		exportParameters = new HashMap<String, Object>();
		// Default Values
		exportParameters.put(ExportParameterNames.EXPORTFILENAME.toString(),
				"FusionCharts");
		exportParameters.put(ExportParameterNames.EXPORTACTION.toString(),
				"download");
		exportParameters.put(
				ExportParameterNames.EXPORTTARGETWINDOW.toString(), "_self");
		exportParameters.put(ExportParameterNames.EXPORTFORMAT.toString(),
				"PDF");
	}

	public ExportBean(String stream, ChartMetadata metadata,
			HashMap<String, Object> exportParameters) {
		super();
		this.stream = stream;
		this.metadata = metadata;
		this.exportParameters = exportParameters;
	}

	/**
	 * Adds a parameter and value to the existing exportParameters map.
	 * 
	 * @param exportParameters
	 *            the exportParameters to set
	 */
	public void addExportParameter(String parameterName, Object value) {
		exportParameters.put(parameterName.toLowerCase(), value);
	}

	/**
	 * Adds all parameters and values from the given HashMap to the existing
	 * exportParameters map.
	 * 
	 * @param exportParameters
	 *            the exportParameters to set
	 */
	public void addExportParametersFromMap(
			HashMap<String, String> moreParameters) {
		exportParameters.putAll(moreParameters);
	}

	/**
	 * @return the exportParameters
	 */
	public HashMap<String, Object> getExportParameters() {

		return new HashMap<String, Object>(exportParameters);
	}

	/**
	 * @return the exportParameter Value
	 */
	public Object getExportParameterValue(String key) {
		return exportParameters.get(key);
	}

	/**
	 * @return the metadata
	 */
	public ChartMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Returns the metadata as a querystring
	 * 
	 * @param filePath
	 *            - path of the file on the server.
	 * @param isError
	 *            - whether error is present or not.
	 * @param isHTML
	 *            - whether to generate in html format or not.
	 * @return - String containing the metadata to be shown.
	 */
	public String getMetadataAsQueryString(String filePath, boolean isError,
			boolean isHTML) {
		String queryParams = "";
		if (isError) {
			queryParams += (isHTML ? "<BR>" : "&") + "width=0";
			queryParams += (isHTML ? "<BR>" : "&") + "height=0";
		} else {
			queryParams += (isHTML ? "<BR>" : "&") + "width="
					+ metadata.getWidth();
			queryParams += (isHTML ? "<BR>" : "&") + "height="
					+ metadata.getHeight();
		}
		// queryParams+="&bgColor="+metadata.getBgColor();
		queryParams += (isHTML ? "<BR>" : "&") + "DOMId=" + metadata.getDOMId();
		if (filePath != null) {
			queryParams += (isHTML ? "<BR>" : "&") + "fileName=" + filePath;
		}

		return queryParams;
	}

	/**
	 * Returns the metadata as a querystring
	 * 
	 * @return - String containing the metadata to be shown.
	 */
	public String getParametersAndMetadataAsQueryString() {
		String queryParams = "";
		queryParams += "?width=" + metadata.getWidth();
		queryParams += "&height=" + metadata.getHeight();
		queryParams += "&bgcolor=" + metadata.getBgColor();

		Iterator<String> iter = exportParameters.keySet().iterator();
		String key;
		String value;
		while (iter.hasNext()) {
			key = iter.next();
			value = (String) exportParameters.get(key);
			queryParams += "&" + key + "=" + value;
		}

		return queryParams;
	}

	/**
	 * @return the stream
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * Whether the response is going to be html or plain text
	 * 
	 * @return
	 */
	public boolean isHTMLResponse() {
		boolean isHTML = false;
		String exportAction = (String) getExportParameterValue(ExportParameterNames.EXPORTACTION
				.toString());
		if (exportAction != null && exportAction.equals("download"))
			isHTML = true;
		return isHTML;
	}

	/**
	 * @param exportParameters
	 *            the exportParameters to set
	 */
	public void setExportParameters(HashMap<String, Object> exportParameters) {
		this.exportParameters = exportParameters;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(ChartMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	public void setStream(String stream) {
		this.stream = stream;
	}

	/**
	 * Validates the ExportBean to check if all the required values are present.
	 * 
	 */
	public LogMessageSetVO validate() {
		LogMessageSetVO errorSetVO = new LogMessageSetVO();
		if (getMetadata().getWidth() == -1 || getMetadata().getHeight() == -1
				|| getMetadata().getWidth() == 0
				|| getMetadata().getHeight() == 0) {

			// If Width/Height parameter is not sent, the ChartMetadata will
			// have width/height as -1
			// Raise Error E101 - Width/Height not found
			errorSetVO.addError(LOGMESSAGE.E101);
		}

		if (getMetadata().getBgColor() == null) {

			// Background color not available
			errorSetVO.addWarning(LOGMESSAGE.W513);
		}

		if (getStream() == null) {

			// If image data not available
			// Raise Error E100
			errorSetVO.addError(LOGMESSAGE.E100);

		}
		if (exportParameters == null || exportParameters.isEmpty()) {
			// export data does not contain parameters
			errorSetVO.addWarning(LOGMESSAGE.W102);
		}
		// Export format should exist in the supported handlerAssociationsMap
		else {
			String exportFormat = (String) getExportParameterValue(ExportParameterNames.EXPORTFORMAT
					.toString());
			boolean exportFormatSupported = FusionChartsExportHelper
					.getHandlerAssociationsMap().containsKey(
							exportFormat.toUpperCase());
			if (!exportFormatSupported) {
				errorSetVO.addError(LOGMESSAGE.E517);
			}
		}

		return errorSetVO;
	}
}
