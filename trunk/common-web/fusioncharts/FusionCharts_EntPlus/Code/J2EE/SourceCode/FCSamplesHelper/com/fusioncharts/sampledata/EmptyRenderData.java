package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class EmptyRenderData {

	protected String xml = "<chart></chart>";
	protected String chartId = "FactoryDetailed";
	protected String url = "";
	protected String width = "600";
	protected String height = "250";
	protected String filename = ChartType.COLUMN2D.getFileName();
	protected String noDataParameter = "?ChartNoDataText=Please select a factory from pie chart above to view detailed data.";

	protected String uniqueId = "";

	public EmptyRenderData() {
	}

	public String getChartId() {
		return chartId;
	}

	public String getFilename() {
		return filename;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the noDataParameter
	 */
	public String getNoDataParameter() {
		return noDataParameter;
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

	public String getXml() {
		return xml;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param noDataParameter
	 *            the noDataParameter to set
	 */
	public void setNoDataParameter(String noDataParameter) {
		this.noDataParameter = noDataParameter;
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

	public void setXml(String xml) {
		this.xml = xml;
	}
}
