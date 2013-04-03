package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSP examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class BasicRenderData {

	protected String xml;
	protected String chartId = "basicChart";
	protected String url = "Data/Data.xml";
	protected String jsonUrl = "Data/Data.json";
	protected String width = "600";
	protected String height = "300";
	protected String swfFilename = ChartType.COLUMN3D.getFileName();
	protected String uniqueId = "";

	/**
	 * Constructor - constructs the xml string
	 */
	public BasicRenderData() {
		xml = "<chart caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units' showValues='0' formatNumberScale='0' showBorder='1'>";
		xml += "<set label='Jan' value='462' />";
		xml += "<set label='Feb' value='857' />";
		xml += "<set label='Mar' value='671' />";
		xml += "<set label='Apr' value='494' />";
		xml += "<set label='May' value='761' />";
		xml += "<set label='Jun' value='960' />";
		xml += "<set label='Jul' value='629' />";
		xml += "<set label='Aug' value='622' />";
		xml += "<set label='Sep' value='376' />";
		xml += "<set label='Oct' value='494' />";
		xml += "<set label='Nov' value='761' />";
		xml += "<set label='Dec' value='960' />";
		xml += "</chart>";
	}

	/**
	 * Returns the value in the field chartId
	 * 
	 * @return the chartId
	 */
	public String getChartId() {
		return chartId;
	}

	/**
	 * Returns the value in the field height
	 * 
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Returns the value in the field jsonUrl
	 * 
	 * @return the jsonUrl
	 */
	public String getJsonUrl() {
		return jsonUrl;
	}

	/**
	 * Returns the value in the field swfFilename
	 * 
	 * @return the swfFilename
	 */
	public String getSwfFilename() {
		return swfFilename;
	}

	/**
	 * Returns a UniqueId
	 * 
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		int randomNum = (int) Math.floor(Math.random() * 100);
		uniqueId = "Chart" + "_" + randomNum;
		return uniqueId;
	}

	/**
	 * Returns the value in the field url
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Returns the value in the field width
	 * 
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Returns the value in the field xml
	 * 
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * Sets the value for chartId
	 * 
	 * @param chartId
	 *            the chartId to set
	 */
	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * Sets the value for height
	 * 
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Sets the value for jsonUrl
	 * 
	 * @param jsonUrl
	 *            the jsonUrl to set
	 */
	public void setJsonUrl(String jsonUrl) {
		this.jsonUrl = jsonUrl;
	}

	/**
	 * Sets the value for swfFilename
	 * 
	 * @param swfFilename
	 *            the swfFilename to set
	 */
	public void setSwfFilename(String swfFilename) {
		this.swfFilename = swfFilename;
	}

	/**
	 * Sets the value for uniqueId
	 * 
	 * @param uniqueId
	 *            the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * Sets the value for url
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the value for width
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Sets the value for xml
	 * 
	 * @param xml
	 *            the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

}
