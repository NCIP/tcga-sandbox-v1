package com.fusioncharts.exporter;

import java.io.File;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.fusioncharts.exporter.beans.ExportConfiguration;
import com.fusioncharts.exporter.beans.LogMessageSetVO;

/**
 * Contains error messages and error handling functions used by FusionCharts
 * Export jsps to handle errors.
 * 
 * @author Infosoft Global (P) Ltd.
 */
public class ErrorHandler {

	/**
	 * Enum for the type of error - "error" and "warning".
	 * 
	 */
	public enum ErrorType {
		ERROR, WARNING;
		@Override
		public String toString() {
			return super.toString();
		}
	}

	/**
	 * 
	 * Enum for log messages - errors and warnings.
	 * 
	 */
	public enum LOGMESSAGE {
		E100(" Insufficient data.", ErrorType.ERROR), E101(
				" Width/height not provided.", ErrorType.ERROR), W102(
				" Insufficient export parameters.", ErrorType.WARNING),

		E400(" Bad request.", ErrorType.ERROR), E401(" Unauthorized access.",
				ErrorType.ERROR), E403(" Directory write access forbidden.",
				ErrorType.ERROR), E404(" Export Resource not found.",
				ErrorType.ERROR), E507(" Insufficient Storage.",
				ErrorType.ERROR), E508(" Server Directory does not exist.",
				ErrorType.ERROR), W509(" File already exists.",
				ErrorType.WARNING), W510(
				" Export handler's Overwrite setting is on. Trying to overwrite.",
				ErrorType.WARNING), E511(
				" Overwrite forbidden. File cannot be overwritten",
				ErrorType.ERROR),

		E512("Intelligent File Naming is Turned off.", ErrorType.ERROR), W513(
				"Background Color not specified. Taking White (FFFFFF) as default background color.",
				ErrorType.WARNING),

		W514(
				"Using intelligent naming of file by adding unique suffix to the exising name.",
				ErrorType.WARNING), W515("The filename has changed - ",
				ErrorType.WARNING),

		E516(" Unable to encode buffered image.", ErrorType.ERROR), E600(
				"Internal Server Error", ErrorType.ERROR), E517(
				" Invalid Export format.", ErrorType.ERROR);

		private String errorMessage = null;
		private String errorType = null;

		private LOGMESSAGE(String message, ErrorType type) {
			this.errorMessage = message;
			this.errorType = type.toString();
		}

		@Override
		public String toString() {
			return errorMessage;
		}

		public String type() {
			return errorType;
		}
	}

	/**
	 * 
	 * Enum for the different statuses - "success" and "failure".
	 * 
	 */
	public enum Status {
		SUCCESS(1, "Success"), FAILURE(0, "Failure");
		private final int statusCode;
		private final String statusMessage;

		private Status(int statusCode, String statusMessage) {
			this.statusCode = statusCode;
			this.statusMessage = statusMessage;
		}

		public int getCode() {
			return statusCode;
		}

		@Override
		public String toString() {
			return statusMessage;
		}
	}

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(ErrorHandler.class.getName());
	}

	/**
	 * Builds the response to be shown.
	 * 
	 * @param logMessageSetVO
	 *            - Object of LogMessageSetVO containing errors/warnings to be
	 *            shown.
	 * @param isHTML
	 *            - Whether response is in html format or not.
	 * @return - The response string.
	 */
	public static String buildResponse(LogMessageSetVO logMessageSetVO,
			boolean isHTML) {

		// Assume that eCodes contains a list of comma separated error/warning
		// codes
		// Get the Error message for each code and terminate it with <BR> in
		// case of HTML, else terminate with &

		StringBuffer err_buf = new StringBuffer();
		StringBuffer warn_buf = new StringBuffer();
		String errors = "";
		String notices = "";
		Set<LOGMESSAGE> errorSet = logMessageSetVO.getErrorsSet();
		Set<LOGMESSAGE> warningSet = logMessageSetVO.getWarningSet();

		for (Enum<LOGMESSAGE> error : errorSet) {
			// Error
			err_buf.append(error.toString());
		}
		for (Enum<LOGMESSAGE> warning : warningSet) {
			// Error
			err_buf.append(warning.toString());
		}

		if (err_buf.length() > 0)
			errors = (isHTML ? "<BR>" : "&") + "statusMessage="
					+ err_buf.substring(0) + (isHTML ? "<BR>" : "&")
					+ "statusCode=" + Status.FAILURE.getCode();
		else
			errors = "statusMessage=" + Status.SUCCESS + "&statusCode="
					+ Status.SUCCESS.getCode();
		if (warn_buf.length() > 0)
			notices = (isHTML ? "<BR>" : "&") + "notice="
					+ warn_buf.substring(0);

		String otherMessages = logMessageSetVO.getOtherMessages();
		otherMessages = otherMessages == null ? "" : otherMessages;

		logger.info("Errors=" + errors);
		logger.info("Notices=" + notices);
		logger.info("Miscellaneous Messages=" + otherMessages);
		return errors + notices + otherMessages;
	}

	/**
	 * Builds the error response to be shown either in the html page or in the
	 * swf.
	 * 
	 * @param eCodes
	 *            Comma separated error codes
	 * @param isHTML
	 *            Whether output should be in html format or not (Use <br>
	 *            tags or not)
	 * @return String - Error Response string
	 */
	public static String buildResponse(String eCodes, boolean isHTML) {

		// Assume that eCodes contains a list of comma separated error/warning
		// codes
		// Get the Error message for each code and terminate it with <BR> in
		// case of HTML, else terminate with &
		StringTokenizer tokenizer = new StringTokenizer(eCodes, ",");
		// if(err_warn_Codes.indexOf("E") != -1) {
		StringBuffer err_buf = new StringBuffer();
		StringBuffer warn_buf = new StringBuffer();
		String errors = "";
		String notices = "";
		String errCode = null;

		while (tokenizer.hasMoreTokens()) {
			errCode = tokenizer.nextToken();
			if (errCode.length() > 0) {
				if (errCode.indexOf("E") != -1) {
					// Error
					err_buf.append(LOGMESSAGE.valueOf(errCode));
				} else {
					// Notice
					warn_buf.append(LOGMESSAGE.valueOf(errCode));
				}
			}

		}
		if (err_buf.length() > 0)
			errors = (isHTML ? "<BR>" : "&") + "statusMessage="
					+ err_buf.substring(0) + (isHTML ? "<BR>" : "&")
					+ "statusCode=" + Status.FAILURE.getCode();
		else
			errors = "statusMessage=" + Status.SUCCESS + "&statusCode="
					+ Status.SUCCESS.getCode();
		if (warn_buf.length() > 0)
			notices = (isHTML ? "<BR>" : "&") + "notice="
					+ warn_buf.substring(0);
		logger.info("Errors=" + errors);
		logger.info("Notices=" + notices);
		return errors + notices;
	}

	/**
	 * Checks the status of the server. Whether it is ready for saving the file
	 * or not. ExportConfiguration.SAVEABSOLUTEPATH is pre-pended to the
	 * filename to test for folder and file existence.
	 * 
	 * @param fileName
	 *            - name of the file in which data is going to be saved.
	 * @return LogMessageSetVO - object of LogMessageSetVO containing
	 *         errors/warnings.
	 */
	public static LogMessageSetVO checkServerSaveStatus(String fileName) {
		LogMessageSetVO errorSetVO = new LogMessageSetVO();

		String pathToSaveFolder = ExportConfiguration.SAVEABSOLUTEPATH;

		// check whether directory exists
		// raise error and return
		File saveFolder = new File(pathToSaveFolder);
		if (!saveFolder.exists()) {
			errorSetVO.addError(LOGMESSAGE.E508);
		} else {
			// check if directory is writable or not
			if (!saveFolder.canWrite()) {
				errorSetVO.addError(LOGMESSAGE.E403);
			} else {
				// build filepath
				String completeFilePath = pathToSaveFolder + File.separator
						+ fileName;
				File saveFile = new File(completeFilePath);

				// check whether file already exists
				if (saveFile.exists()) {
					errorSetVO.addWarning(LOGMESSAGE.W509);

					if (ExportConfiguration.OVERWRITEFILE) {
						errorSetVO.addWarning(LOGMESSAGE.W510);

						if (!saveFile.canWrite()) {
							errorSetVO.addError(LOGMESSAGE.E511);
						}
					} else {

						if (!ExportConfiguration.INTELLIGENTFILENAMING) {
							errorSetVO.addError(LOGMESSAGE.E512);
						}

					}
				}

			}
		}

		return errorSetVO;

	}

	/**
	 * Whether the folder at ExportConfiguration.SAVEABSOLUTEPATH exists or not.
	 * 
	 * @return boolean - Whether the folder at
	 *         ExportConfiguration.SAVEABSOLUTEPATH exists or not.
	 */
	public static boolean doesServerSaveFolderExist() {
		boolean saveFolderExists = true;
		String pathToSaveFolder = ExportConfiguration.SAVEABSOLUTEPATH;

		// check whether directory exists
		// raise error and return
		File saveFolder = new File(pathToSaveFolder);
		if (!saveFolder.exists()) {
			saveFolderExists = false;
		}
		return saveFolderExists;

	}
}