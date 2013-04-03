package com.fusioncharts.exporter.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fusioncharts.exporter.FusionChartsExportHelper;
import com.fusioncharts.exporter.beans.ExportBean;
import com.fusioncharts.exporter.beans.ExportConfiguration;
import com.fusioncharts.exporter.beans.ExportParameterNames;
import com.fusioncharts.exporter.beans.FusionChartsExportData;
import com.fusioncharts.exporter.beans.LogMessageSetVO;
import com.fusioncharts.exporter.error.ErrorHandler;
import com.fusioncharts.exporter.error.LOGMESSAGE;
import com.fusioncharts.exporter.resources.FCExporter_Format;

/**
 * Servlet implementation class FCExporter Uses the core FCExporter classes to
 * get the job done.
 */
public class FCExporter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private boolean SAVEFOLDEREXISTS = true;

	private static Logger logger = null;
	static {
		logger = Logger.getLogger(FCExporter.class.getName());
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FCExporter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Checks whether the export resources are present or not.
	 * 
	 * @return boolean - Whether the export resources are present or not.
	 */
	private boolean checkExportResources() {
		boolean allExportResourcesFound = true;
		for (String exportFormat : FusionChartsExportHelper
				.getHandlerAssociationsMap().values()) {
			String exporterClassName = FusionChartsExportHelper
					.getExportHandlerClassName(exportFormat);
			try {
				Class exporterClass = Class.forName(exporterClassName);
				FCExporter_Format fcExporter = (FCExporter_Format) exporterClass
						.newInstance();

			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, LOGMESSAGE.E404.toString() + ":"
						+ exporterClassName);
				allExportResourcesFound = false;

			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, LOGMESSAGE.E404.toString() + ":"
						+ exporterClassName);
				allExportResourcesFound = false;

			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, LOGMESSAGE.E404.toString() + ":"
						+ exporterClassName);
				allExportResourcesFound = false;

			}
		}
		return allExportResourcesFound;

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Create the bean, set all the properties
		FusionChartsExportData exportData = new FusionChartsExportData(request
				.getParameter("stream"), request.getParameter("parameters"),
				request.getParameter("meta_width"), request
						.getParameter("meta_height"), request
						.getParameter("meta_DOMId"), request
						.getParameter("meta_bgColor"));
		ExportBean exportBean = FusionChartsExportHelper
				.parseExportRequestStream(exportData);

		String exportTargetWindow = (String) exportBean
				.getExportParameterValue(ExportParameterNames.EXPORTTARGETWINDOW
						.toString());

		String exportFormat = (String) exportBean
				.getExportParameterValue(ExportParameterNames.EXPORTFORMAT
						.toString());

		String exportAction = (String) exportBean
				.getExportParameterValue(ExportParameterNames.EXPORTACTION
						.toString());

		String action = request.getParameter("fcserveraction");

		/*
		 * action parameter is handled. If fcserveraction parameter is passed,
		 * the servlet can use it instead of relying on the exportAction Even if
		 * exportAction is download, it can be bypassed by setting
		 * fcserveraction as save.
		 */

		logger.info("action" + action);
		if (action != null && action.equals("save")) {
			exportAction = "save";
			exportBean.addExportParameter(ExportParameterNames.EXPORTACTION
					.toString(), exportAction);

		}

		// Validate the request parameters in the bean
		LogMessageSetVO logMessageSetVO = exportBean.validate();

		// check if there are any errors in validation
		// if validation is not successful, then output the error
		Set<LOGMESSAGE> errorsSet = logMessageSetVO.getErrorsSet();
		boolean isHTML = exportBean.isHTMLResponse();

		if (errorsSet != null && !errorsSet.isEmpty()) {
			// There are errors - forward to error page
			String meta_values = exportBean.getMetadataAsQueryString(null,
					true, isHTML);
			logMessageSetVO.setOtherMessages(meta_values);
			writeError(response, isHTML, logMessageSetVO, exportTargetWindow);
			return;
		}
		// Else If validation is success, then conduct some more validations and
		// then find the delegate for this exportFormat and delegate the export
		// work to it
		else {

			if (!exportAction.equals("download")) {
				if (!SAVEFOLDEREXISTS) {
					logMessageSetVO.addError(LOGMESSAGE.E508);
					// if server save folder does not exist, then quit writing
					// the error
					String meta_values = exportBean.getMetadataAsQueryString(
							null, true, isHTML);
					logMessageSetVO.setOtherMessages(meta_values);
					writeError(response, isHTML, logMessageSetVO,
							exportTargetWindow);
					return;
				}

				String fileNameWithoutExt = (String) exportBean
						.getExportParameterValue(ExportParameterNames.EXPORTFILENAME
								.toString());
				String extension = FusionChartsExportHelper
						.getExtensionFor(exportFormat.toLowerCase());
				;
				String fileName = fileNameWithoutExt + "." + extension;
				logMessageSetVO = ErrorHandler.checkServerSaveStatus(fileName);
				errorsSet = logMessageSetVO.getErrorsSet();
			}
			if (errorsSet != null && !errorsSet.isEmpty()) {
				// There are errors - forward to error page
				String meta_values = exportBean.getMetadataAsQueryString(null,
						true, isHTML);
				logMessageSetVO.setOtherMessages(meta_values);
				writeError(response, isHTML, logMessageSetVO,
						exportTargetWindow);
				return;
			} else {

				// no errors - find the delegate for this exportFormat and
				// delegate the export work to it
				// The delegate will process the request, to create the chart
				// image or pdf and write to the stream directly.
				// get the exporter for this format
				// gives without any extension
				String exporterClassName = FusionChartsExportHelper
						.getExportHandlerClassName(exportFormat);
				try {
					Class exporterClass = Class.forName(exporterClassName);

					FCExporter_Format fcExporter = (FCExporter_Format) exporterClass
							.newInstance();
					Object exportObject = fcExporter
							.exportProcessor(exportBean);
					String status = fcExporter.exportOutput(exportObject,
							response);

				} catch (ClassNotFoundException e) {
					logMessageSetVO.addError(LOGMESSAGE.E404);
					// There are errors - forward to error page
					String meta_values = exportBean.getMetadataAsQueryString(
							null, true, isHTML);
					logMessageSetVO.setOtherMessages(meta_values);
					writeError(response, isHTML, logMessageSetVO,
							exportTargetWindow);
				} catch (InstantiationException e) {
					logMessageSetVO.addError(LOGMESSAGE.E404);
					// There are errors - forward to error page
					String meta_values = exportBean.getMetadataAsQueryString(
							null, true, isHTML);
					logMessageSetVO.setOtherMessages(meta_values);
					writeError(response, isHTML, logMessageSetVO,
							exportTargetWindow);
				} catch (IllegalAccessException e) {
					logMessageSetVO.addError(LOGMESSAGE.E404);
					// There are errors - forward to error page
					String meta_values = exportBean.getMetadataAsQueryString(
							null, true, isHTML);
					logMessageSetVO.setOtherMessages(meta_values);
					writeError(response, isHTML, logMessageSetVO,
							exportTargetWindow);
				}
			}
		}

	}

	/**
	 * The init of the servlet. Performs all the basic checks and
	 * initializations. Loads the export properties into the bean
	 * ExportConfiguration. Sets the SAVEABSOLUTEPATH equal SAVEPATH to if the
	 * user has given absolute path, else, pre-pends the realpath to SAVEPATH
	 * and sets that. Checks whether save folder exists on the server or not.
	 * Checks whether export resources are present or not.
	 * 
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("FCExporter Servlet Init called");
		// load properties file
		ExportConfiguration.loadProperties();
		File f = new File(ExportConfiguration.SAVEPATH);
		boolean savePathAbsolute = f.isAbsolute();
		logger.info("Is SAVEPATH on server absolute?" + savePathAbsolute);
		String realPath = config.getServletContext().getRealPath(
				ExportConfiguration.SAVEPATH);
		// in some operating systems, from war deployment, getRealPath returns
		// null.
		if (realPath == null) {
			logger.log(Level.SEVERE,
					"For this environment, SAVEPATH should be absolute");
			realPath = "";
		}
		ExportConfiguration.SAVEABSOLUTEPATH = savePathAbsolute ? ExportConfiguration.SAVEPATH
				: realPath;
		// now the properties are loaded into ExportConfiguration
		// check if server folder exists, otherwise log a message
		// The user needs to provide complete path to the save folder
		SAVEFOLDEREXISTS = ErrorHandler.doesServerSaveFolderExist();
		if (!SAVEFOLDEREXISTS)
			logger.warning(LOGMESSAGE.E508.toString() + "Path used: "
					+ ExportConfiguration.SAVEABSOLUTEPATH);
		// check whether resource exists for each supported format
		checkExportResources();
	}

	/**
	 * Writes the error.
	 * 
	 * @param response
	 *            HttpServletResponse - to which error has to be written.
	 * @param isHTML
	 *            - whether the response has to be in html format or not.
	 * @param logMessageSetVO
	 *            - LogMessageSetVO - set of errors.
	 * @param exportTargetWindow
	 *            - export parameter specifying the target window.
	 */
	private void writeError(HttpServletResponse response, boolean isHTML,
			LogMessageSetVO logMessageSetVO, String exportTargetWindow) {

		response.setContentType("text/html");
		if (exportTargetWindow != null
				&& exportTargetWindow.equalsIgnoreCase("_self")) {
			response.addHeader("Content-Disposition", "attachment;");
		} else {
			response.addHeader("Content-Disposition", "inline;");
		}
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(ErrorHandler.buildResponse(logMessageSetVO, isHTML));
		} catch (IOException e) {

		}
	}
}
