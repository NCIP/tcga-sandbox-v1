package com.fusioncharts.helper;

/**
 * Enum to hold different data formats supported by FusionCharts v3.2
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public enum ChartDataFormat {
	XML("xml"), JSON("json"), XMLURL("xmlurl"), JSONURL("jsonurl");

	public static boolean exists(String dataFormat) {
		boolean formatExists = false;
		for (ChartDataFormat format : ChartDataFormat.values()) {
			if (dataFormat.equalsIgnoreCase(format.toString())) {
				formatExists = true;
				break;
			}
		}
		return formatExists;
	}

	private String dataFormat;

	private ChartDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	@Override
	public String toString() {
		return this.dataFormat;
	}
}
