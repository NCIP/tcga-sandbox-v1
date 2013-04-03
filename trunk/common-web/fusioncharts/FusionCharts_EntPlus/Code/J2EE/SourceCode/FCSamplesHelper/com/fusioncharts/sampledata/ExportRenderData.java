package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class ExportRenderData {

	protected String xml;
	protected String downloadUrl = "Data/DownloadData.xml";
	protected String saveUrl = "Data/SaveData.xml";
	protected String chartId = "exportSample";
	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.COLUMN3D.getFileName();
	protected String filename2 = ChartType.PIE3D.getFileName();
	protected String filename3 = ChartType.COLUMN2D.getFileName();
	protected String uniqueId = "";

	public ExportRenderData() {

	}

	public String getChartId() {
		return chartId;
	}

	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return downloadUrl;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the filename2
	 */
	public String getFilename2() {
		return filename2;
	}

	/**
	 * @return the filename3
	 */
	public String getFilename3() {
		return filename3;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the saveUrl
	 */
	public String getSaveUrl() {
		return saveUrl;
	}

	/**
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		int randomNum = (int) Math.floor(Math.random() * 100);
		uniqueId = "Chart" + "_" + randomNum;
		return uniqueId;
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

	/**
	 * @param downloadUrl
	 *            the downloadUrl to set
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param filename2
	 *            the filename2 to set
	 */
	public void setFilename2(String filename2) {
		this.filename2 = filename2;
	}

	/**
	 * @param filename3
	 *            the filename3 to set
	 */
	public void setFilename3(String filename3) {
		this.filename3 = filename3;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param saveUrl
	 *            the saveUrl to set
	 */
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}

	/**
	 * @param uniqueId
	 *            the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
}
