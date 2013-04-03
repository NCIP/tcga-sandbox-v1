package com.fusioncharts.exporter.error;

import java.io.File;
import java.util.HashMap;
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
			// Warning
			warn_buf.append(warning.toString());
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
	 * Builds the error response to be shown.
	 * 
	 * The key values in the HashMap will be something like this:(key,value)
	 * statusMessage, Success
	 * 
	 * statusCode, 1
	 * 
	 * fileName, FusionCharts.jpg
	 * 
	 * notice, Intelligent file naming turned off
	 * 
	 * @param logMessageSetVO
	 *            - LogMessageSetVO containing the errors and messages to parse
	 *            and convert to HashMap
	 * @return HashMap<String,String> - Error Response in a HashMap
	 */
	public static HashMap<String, String> buildResponseAsHashMap(
			LogMessageSetVO logMessageSetVO) {
		HashMap<String, String> keyValues = new HashMap<String, String>();
		// Assume that eCodes contains a list of comma separated error/warning
		// codes
		// Get the Error message for each code and terminate it with <BR> in
		// case of HTML, else terminate with &
		StringBuffer err_buf = new StringBuffer();
		StringBuffer warn_buf = new StringBuffer();

		Set<LOGMESSAGE> errorSet = logMessageSetVO.getErrorsSet();
		Set<LOGMESSAGE> warningSet = logMessageSetVO.getWarningSet();
		String otherMessages = logMessageSetVO.getOtherMessages();

		for (Enum<LOGMESSAGE> error : errorSet) {
			// Error
			err_buf.append(error.toString());
		}
		for (Enum<LOGMESSAGE> warning : warningSet) {
			// Warning
			warn_buf.append(warning.toString());
		}

		if (err_buf.length() > 0) {
			keyValues.put("statusMessage", err_buf.substring(0));
			keyValues.put("statusCode", "" + Status.FAILURE.getCode());
		} else {
			keyValues.put("statusMessage", Status.SUCCESS.toString());
			keyValues.put("statusCode", "" + Status.SUCCESS.getCode());
		}
		if (warn_buf.length() > 0) {
			keyValues.put("notice", warn_buf.substring(0));
		}
		StringTokenizer st = new StringTokenizer(otherMessages, "&");
		String token = "";
		StringTokenizer keyValueTokens = null;
		String key = "";
		String value = "";
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			keyValueTokens = new StringTokenizer(token, "=");
			if (keyValueTokens.countTokens() == 2) {
				key = keyValueTokens.nextToken();
				value = keyValueTokens.nextToken();
				keyValues.put(key, value);
			}
		}
		return keyValues;
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