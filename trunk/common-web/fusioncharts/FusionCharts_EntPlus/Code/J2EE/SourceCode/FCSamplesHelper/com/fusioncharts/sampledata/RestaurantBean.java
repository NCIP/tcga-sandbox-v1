package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class RestaurantBean {

	protected String xml;
	protected String chartId = "Sales";
	protected String url = "../Data/Data.xml";
	protected String width = "500";
	protected String height = "300";
	protected String filename = ChartType.PIE3D.getFileName();

	protected String soups = "";
	protected String salads = "";
	protected String sandwiches = "";
	protected String beverages = "";
	protected String desserts = "";

	public RestaurantBean() {

	}

	/**
	 * @return the beverages
	 */
	public String getBeverages() {
		return beverages;
	}

	public String getChartId() {
		return chartId;
	}

	/**
	 * @return the desserts
	 */
	public String getDesserts() {
		return desserts;
	}

	public String getFilename() {
		return filename;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the salads
	 */
	public String getSalads() {
		return salads;
	}

	/**
	 * @return the sandwiches
	 */
	public String getSandwiches() {
		return sandwiches;
	}

	/**
	 * @return the soups
	 */
	public String getSoups() {
		return soups;
	}

	public String getUrl() {
		return url;
	}

	public String getWidth() {
		return width;
	}

	public String getXml() {
		// Initialize <chart> element
		xml = "<chart caption='Sales by Product Category' subCaption='For this week' showPercentValues='0' pieSliceDepth='30' showBorder='1'>";
		// Add all data
		xml += "<set label='Soups' value='" + soups + "' />";
		xml += "<set label='Salads' value='" + salads + "' />";
		xml += "<set label='Sandwiches' value='" + sandwiches + "' />";
		xml += "<set label='Beverages' value='" + beverages + "' />";
		xml += "<set label='Desserts' value='" + desserts + "' />";
		// Close <chart> element
		xml += "</chart>";
		return xml;
	}

	/**
	 * @param beverages
	 *            the beverages to set
	 */
	public void setBeverages(String beverages) {
		this.beverages = beverages;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * @param desserts
	 *            the desserts to set
	 */
	public void setDesserts(String desserts) {
		this.desserts = desserts;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param salads
	 *            the salads to set
	 */
	public void setSalads(String salads) {
		this.salads = salads;
	}

	/**
	 * @param sandwiches
	 *            the sandwiches to set
	 */
	public void setSandwiches(String sandwiches) {
		this.sandwiches = sandwiches;
	}

	/**
	 * @param soups
	 *            the soups to set
	 */
	public void setSoups(String soups) {
		this.soups = soups;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}
