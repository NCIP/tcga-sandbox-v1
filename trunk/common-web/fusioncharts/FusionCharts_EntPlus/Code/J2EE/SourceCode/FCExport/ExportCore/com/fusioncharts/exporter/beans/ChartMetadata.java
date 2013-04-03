/**
 * 
 */
package com.fusioncharts.exporter.beans;

/**
 * Contains the metadata of the chart like width,height, bgColor
 * 
 * @author Infosoft Global (P) ltd.
 * 
 */
public class ChartMetadata {

	private double width = -1;
	private double height = -1;
	private String bgColor;
	private String DOMId = "";

	/**
	 * Returns the value in the field bgColor
	 * 
	 * @return the bgColor
	 */
	public String getBgColor() {
		return bgColor;
	}

	/**
	 * Returns the value in the field dOMId
	 * 
	 * @return the dOMId
	 */
	public String getDOMId() {
		return DOMId;
	}

	/**
	 * Returns the value in the field height
	 * 
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns the value in the field width
	 * 
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the value for bgColor
	 * 
	 * @param bgColor
	 *            the bgColor to set
	 */
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Sets the value for dOMId
	 * 
	 * @param id
	 *            the dOMId to set
	 */
	public void setDOMId(String id) {
		DOMId = id;
	}

	/**
	 * Sets the value for height
	 * 
	 * @param height
	 *            the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Sets the value for width
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

}
