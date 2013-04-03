package com.fusioncharts.sampledata;


/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class DynamicRenderData {

	protected String chartId = "FactorySum";

	protected String width = "600";
	protected String height = "400";
	protected String filename = ChartType.PIE3D.getFileName();

	protected String url = "PieData.jsp";

	public DynamicRenderData() {
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

	public String getUrl() {
		return url;
	}

	public String getWidth() {
		return width;
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

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
