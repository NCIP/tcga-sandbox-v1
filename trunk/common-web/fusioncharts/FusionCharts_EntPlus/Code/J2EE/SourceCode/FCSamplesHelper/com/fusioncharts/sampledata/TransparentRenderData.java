package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class TransparentRenderData {

	protected String chartId = "myFirstChart_Transparent";
	protected String chartId2 = "myFirstChart_Opaque";
	protected String url = "Data/Data_Transparent.xml";
	protected String width = "600";
	protected String height = "300";
	protected String swfFilename = ChartType.COLUMN3D.getFileName();
	protected String uniqueId = "";

	public TransparentRenderData() {

	}

	public String getChartId() {
		return chartId;
	}

	/**
	 * Returns the value in the field chartId2
	 * 
	 * @return the chartId2
	 */
	public String getChartId2() {
		return chartId2;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the swfFilename
	 */
	public String getSwfFilename() {
		return swfFilename;
	}

	/**
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		int randomNum = (int) Math.floor(Math.random() * 100);
		uniqueId = "Chart" + "_" + randomNum;
		return uniqueId;
	}

	public String getUrl() {
		return url;
	}

	public String getWidth() {
		return width;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * Sets the value for chartId2
	 * 
	 * @param chartId2
	 *            the chartId2 to set
	 */
	public void setChartId2(String chartId2) {
		this.chartId2 = chartId2;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param swfFilename
	 *            the swfFilename to set
	 */
	public void setSwfFilename(String swfFilename) {
		this.swfFilename = swfFilename;
	}

	/**
	 * @param uniqueId
	 *            the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
