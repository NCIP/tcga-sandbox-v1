package com.fusioncharts.exporter.error;

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
			ErrorType.ERROR), E507(" Insufficient Storage.", ErrorType.ERROR), E508(
			" Server Directory does not exist.", ErrorType.ERROR), W509(
			" File already exists.", ErrorType.WARNING), W510(
			" Export handler's Overwrite setting is on. Trying to overwrite.",
			ErrorType.WARNING), E511(
			" Overwrite forbidden. File cannot be overwritten", ErrorType.ERROR),

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