/**
 * 
 */
package com.fusioncharts.exporter.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fusioncharts.exporter.FusionChartsExportHelper;
import com.fusioncharts.exporter.beans.ChartMetadata;
import com.fusioncharts.exporter.beans.ExportBean;
import com.fusioncharts.exporter.beans.ExportConfiguration;
import com.fusioncharts.exporter.beans.LogMessageSetVO;
import com.fusioncharts.exporter.error.LOGMESSAGE;
import com.fusioncharts.exporter.error.Status;
import com.fusioncharts.exporter.generators.PDFGenerator;

/**
 * @author InfosoftGlobal (P) Ltd.
 * 
 */
public class FCExporter_PDF extends FCExporter_Format {
	private ExportBean exportBean = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fusioncharts.exporter.resources.FcExporter_Format#exportOutput(java
	 * .lang.Object, javax.servlet.http.HttpServletResponse, java.lang.String,
	 * int)
	 */
	/**
	 * 
	 * @param exportObj
	 *            - expects byte[]
	 * @param response
	 *            - HttpServletResponse - the response to which to write the
	 *            image
	 * @return String - for future purpose. Currently, empty string is returned.
	 */
	@Override
	public String exportOutput(Object exportObj, HttpServletResponse response) {
		byte[] pdfBytes = (byte[]) exportObj;
		String action = (String) exportBean
				.getExportParameterValue("exportaction");
		String exportFormat = (String) exportBean
				.getExportParameterValue("exportformat");
		String exportTargetWindow = (String) exportBean
				.getExportParameterValue("exporttargetwindow");

		String fileNameWithoutExt = (String) exportBean
				.getExportParameterValue("exportfilename");
		String extension = FusionChartsExportHelper
				.getExtensionFor(exportFormat.toLowerCase());
		;
		String fileName = fileNameWithoutExt + "." + extension;

		String stream = exportBean.getStream();
		ChartMetadata metadata = exportBean.getMetadata();

		boolean isHTML = false;
		if (action.equals("download"))
			isHTML = true;

		LogMessageSetVO logMessageSetVO = new LogMessageSetVO();

		String noticeMessage = "";
		String meta_values = exportBean.getMetadataAsQueryString(null, false,
				isHTML);

		if (!action.equalsIgnoreCase("download")) {
			noticeMessage = "&notice=";
			String pathToWebAppRoot = (String) exportBean
					.getExportParameterValue("webapproot");

			String pathToSaveFolder = ExportConfiguration.SAVEABSOLUTEPATH;
			File saveFolder = new File(pathToSaveFolder);

			String completeFilePath = pathToSaveFolder + File.separator
					+ fileName;
			String completeFilePathWithoutExt = pathToSaveFolder
					+ File.separator + fileNameWithoutExt;
			File saveFile = new File(completeFilePath);
			if (saveFile.exists()) {
				noticeMessage += LOGMESSAGE.W509;
				if (!ExportConfiguration.OVERWRITEFILE) {
					if (ExportConfiguration.INTELLIGENTFILENAMING) {
						noticeMessage += LOGMESSAGE.W514;
						completeFilePath = FusionChartsExportHelper
								.getUniqueFileName(completeFilePathWithoutExt,
										extension);
						File tempFile = new File(completeFilePath);
						fileName = tempFile.getName();
						noticeMessage += LOGMESSAGE.W515 + fileName;
						logMessageSetVO.addWarning(LOGMESSAGE.W515);
					}
				}
			}

			try {
				FileOutputStream fos = new FileOutputStream(completeFilePath);

				for (int i = 0; i < pdfBytes.length; i++)
					fos.write(pdfBytes[i]);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				logMessageSetVO.addError(LOGMESSAGE.E600);

			}
			// In Save mode, send back Successful response back to chart

			String pathToDisplay = ExportConfiguration.HTTP_URI + "/"
					+ fileName;
			if (ExportConfiguration.HTTP_URI.endsWith("/")) {
				pathToDisplay = ExportConfiguration.HTTP_URI + fileName;
			}
			// In save mode, isHTML is false
			meta_values = exportBean.getMetadataAsQueryString(pathToDisplay,
					false, isHTML);
			/*
			 * noticeMessage+="&fileName="+ pathToDisplay;
			 * noticeMessage+="&width="+ metadata.getWidth();
			 * noticeMessage+="&height="+ metadata.getHeight();
			 */
			if (logMessageSetVO.getErrorsSet() == null
					|| logMessageSetVO.getErrorsSet().isEmpty()) {
				// if there are no errors
				PrintWriter out;
				try {
					out = response.getWriter();
					out.print(meta_values + noticeMessage + "&statusCode="
							+ Status.SUCCESS.getCode() + "&statusMessage="
							+ Status.SUCCESS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else {
			// PDF Streaming
			response.setContentType(FusionChartsExportHelper
					.getMimeTypeFor(exportFormat.toLowerCase()));

			if (exportTargetWindow.equalsIgnoreCase("_self")) {
				response.addHeader("Content-Disposition",
						"attachment; filename=\"" + fileName + "\"");
				// response.addHeader("Content-length",""+ios.length());
			} else {
				response.addHeader("Content-Disposition", "inline; filename=\""
						+ fileName + "\"");
			}
			OutputStream os;
			try {
				os = response.getOutputStream();

				for (int i = 0; i < pdfBytes.length; i++)
					os.write(pdfBytes[i]);
				os.flush();
				// os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (logMessageSetVO.getErrorsSet() == null
				|| logMessageSetVO.getErrorsSet().isEmpty()) {
			meta_values = exportBean.getMetadataAsQueryString(null, true,
					isHTML);
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print(meta_values + noticeMessage + "&statusCode="
						+ Status.SUCCESS.getCode() + "&statusMessage="
						+ Status.SUCCESS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Processes the export and generates the pdf byte[] from chart.
	 * 
	 * @param exportBean
	 *            ExportBean containing all the properties required for export.
	 * @return Object - byte[] containing the chart
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fusioncharts.exporter.resources.FcExporter_Format#exportProcessor
	 * (com.fusioncharts.exporter.beans.ExportBean)
	 */
	@Override
	public Object exportProcessor(ExportBean pExportBean) {
		exportBean = pExportBean;
		String stream = exportBean.getStream();
		ChartMetadata metadata = exportBean.getMetadata();
		PDFGenerator pdf = new PDFGenerator(stream, metadata);
		byte[] pdfBytes = pdf.getPDFObjects(true);
		return pdfBytes;
	}

}
