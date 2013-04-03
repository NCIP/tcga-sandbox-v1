package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class MultiLingualData {

	protected String japanesexml;
	protected String frenchXml;
	protected String mixedXml;

	protected String chartId = "UTFChart";
	protected String url = "Data/JapaneseData.xml";
	protected String dynamicurl = "PieDataJapanese.jsp";
	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.COLUMN2D.getFileName();

	public MultiLingualData() {
		constructJapaneseXML();
		constructFrenchXML();
		constructMixedXML();

	}

	/**
	 * Here, we have hard-coded the array, in real-world applications you would
	 * get the data from database or elsewhere as an array.
	 */
	private void constructFrenchXML() {

	}

	/**
	 * Here, we have hard-coded the array, in real-world applications you would
	 * get the data from database or elsewhere as an array.
	 */
	private void constructJapaneseXML() {

	}

	/**
	 * 
	 */
	private void constructMixedXML() {
		mixedXml = "<chart caption='Monthly Sales Summary' subcaption='For the year 2008' ";
		mixedXml += " xAxisName='Month' yAxisName='Sales' numberPrefix='$' showNames='1'";
		mixedXml += " showValues='0' showColumnShadow='1' animation='1'";
		mixedXml += " baseFontColor='666666' lineColor='FF5904' lineAlpha='85'";
		mixedXml += " valuePadding='10' labelDisplay='rotate' useRoundEdges='1'>";
		mixedXml += "<set label='januári' value='17400' />";
		mixedXml += "<set label='Fevruários' value='19800' />";
		mixedXml += "<set label='مارس' value='21800' />";
		mixedXml += "<set label='أبريل' value='23800' />";
		mixedXml += "<set label='五月' value='29600' />";
		mixedXml += "<set label='六月' value='27600' />";
		mixedXml += "<set label='תִּשׁרִי' value='31800' />";
		mixedXml += "<set label='Marešwān' value='39700' />";
		mixedXml += "<set label='settèmbre' value='37800' />";
		mixedXml += "<set label='ottàgono' value='21900' />";
		mixedXml += "<set label='novèmbre' value='32900' />";
		mixedXml += "<set label='décembre' value='39800' />";
		mixedXml += "<styles><definition><style name='myCaptionFont' type='font' size='12'/></definition>";
		mixedXml += "<application><apply toObject='datalabels' styles='myCaptionFont' /></application></styles>";
		mixedXml += "</chart>";

	}

	public String getChartId() {
		return chartId;
	}

	/**
	 * @return the dynamicurl
	 */
	public String getDynamicurl() {
		return dynamicurl;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the frenchXml
	 */
	public String getFrenchXml() {
		return frenchXml;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the japanesexml
	 */
	public String getJapanesexml() {
		return japanesexml;
	}

	/**
	 * @return the mixedXml
	 */
	public String getMixedXml() {
		return mixedXml;
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
	 * @param dynamicurl
	 *            the dynamicurl to set
	 */
	public void setDynamicurl(String dynamicurl) {
		this.dynamicurl = dynamicurl;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param frenchXml
	 *            the frenchXml to set
	 */
	public void setFrenchXml(String frenchXml) {
		this.frenchXml = frenchXml;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param japanesexml
	 *            the japanesexml to set
	 */
	public void setJapanesexml(String japanesexml) {
		this.japanesexml = japanesexml;
	}

	/**
	 * @param mixedXml
	 *            the mixedXml to set
	 */
	public void setMixedXml(String mixedXml) {
		this.mixedXml = mixedXml;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
