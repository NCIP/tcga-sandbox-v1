package com.fusioncharts.exporter.error;

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