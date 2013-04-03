/**
 * 
 */
package com.fusioncharts.helper;

/**
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public enum FCParams {
	/**
	 * Enum for the parameters to the FusionCharts JS constructor
	 * 
	 * @author InfoSoft Global (P) Ltd.
	 * 
	 */

	SWFURL("swfUrl"), WIDTH("width"), HEIGHT("height"), RENDERAT("renderAt"), RENDERER(
			"renderer"), DATASOURCE("dataSource"), DATAFORMAT("dataFormat"), ID(
			"id"), LANG("lang"), DEBUGMODE("debugMode"), REGISTERWITHJS(
			"registerWithJS"), DETECTFLASHVERSION("detectFlashVersion"), AUTOINSTALLREDIRECT(
			"autoInstallRedirect"), WMODE("wMode"), SCALEMODE("scaleMode"), MENU(
			"menu"), BGCOLOR("bgColor"), QUALITY("quality");

	String paramName = "";

	private FCParams(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}

}
