/**
 * 
 */
package com.fusioncharts.exporter.resources;

import javax.servlet.http.HttpServletResponse;

import com.fusioncharts.exporter.beans.ExportBean;

/**
 * Abstract class for the export resources. Export resource classes need to
 * extend this class.
 * 
 * @author InfosoftGlobal (P) Ltd.
 * @see FCExporter_IMG
 * @see FCExporter_PDF
 */
public abstract class FCExporter_Format {

	/**
	 * Generates the output of export
	 * 
	 * @param exportObj
	 * @param response
	 * @return String
	 */
	abstract public String exportOutput(Object exportObj,
			HttpServletResponse response);

	/**
	 * Processes the export.
	 * 
	 * @param exportBean
	 *            ExportBean containing all the properties required for export.
	 * @return Object
	 */
	abstract public Object exportProcessor(ExportBean exportBean);
}
