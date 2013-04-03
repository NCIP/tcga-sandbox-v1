package com.fusioncharts.sampledata;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
public class ArrayRenderData {

	protected String xml;
	protected String multiSeriesXml;
	protected String stackedXml;
	protected String combinationXml;
	protected String chartId = "productSales";
	protected String url = null;
	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.COLUMN3D.getFileName();
	protected String multiSeriesFilename = ChartType.MSCOLUMN3D.getFileName();
	protected String stackedFilename = ChartType.STACKEDCOLUMN3D.getFileName();
	protected String combinationFilename = ChartType.MSCOLUMN3DLINEDY
			.getFileName();

	public ArrayRenderData() {
		constructSingleSeriesXML();
		constructMultiSeriesXML();
		constructStackedXML();
		constructCombinationXML();
	}

	private void constructCombinationXML() {
		/*
		 * The array will have three columns - first one for Quarter Name second
		 * one for sales figure and third one for quantity.
		 */
		String[][] arrData = new String[4][3];
		// Store Quarter Name
		arrData[0][0] = "Quarter 1";
		arrData[1][0] = "Quarter 2";
		arrData[2][0] = "Quarter 3";
		arrData[3][0] = "Quarter 4";

		// Store revenue data
		arrData[0][1] = "576000";
		arrData[1][1] = "448000";
		arrData[2][1] = "956000";
		arrData[3][1] = "734000";

		// Store Quantity
		arrData[0][2] = "576";
		arrData[1][2] = "448";
		arrData[2][2] = "956";
		arrData[3][2] = "734";

		/*
		 * Now, we need to convert this data into combination XML. We convert
		 * using string concatenation. strXML - Stores the entire XML
		 * strCategories - Stores XML for the <categories> and child <category>
		 * elements strDataRev - Stores XML for current year's sales strDataQty
		 * - Stores XML for previous year's sales
		 */

		// Initialize <chart> element
		combinationXml = "<chart palette='4' caption='Product A - Sales Details' PYAxisName='Revenue' SYAxisName='Quantity (in Units)' numberPrefix='$' formatNumberScale='0' showValues='0' decimals='0'>";

		// Initialize <categories> element - necessary to generate a
		// multi-series chart
		String strCategories = "<categories>";

		// Initiate <dataset> elements
		String strDataRev = "<dataset seriesName='Revenue'>";
		String strDataQty = "<dataset seriesName='Quantity' parentYAxis='S'>";

		// Iterate through the data
		for (int i = 0; i < arrData.length; i++) {
			// Append <category name='...' /> to strCategories
			strCategories += "<category name='" + arrData[i][0] + "' />";
			// Add <set value='...' /> to both the datasets
			strDataRev += "<set value='" + arrData[i][1] + "' />";
			strDataQty = strDataQty + "<set value='" + arrData[i][2] + "' />";
		}

		// Close <categories> element
		strCategories += "</categories>";

		// Close <dataset> elements
		strDataRev += "</dataset>";
		strDataQty += "</dataset>";

		// Assemble the entire XML now
		combinationXml += strCategories + strDataRev + strDataQty + "</chart>";
	}

	/**
	 * Here, we have hard-coded the array, in real-world applications you would
	 * get the data from database or elsewhere as an array.
	 */
	private void constructMultiSeriesXML() {
		/*
		 * Let's store the sales data for 6 products in our array. We also store
		 * the name of products.
		 */
		String[][] arrData = new String[6][3];
		// Store Name of Products
		arrData[0][0] = "Product A";
		arrData[1][0] = "Product B";
		arrData[2][0] = "Product C";
		arrData[3][0] = "Product D";
		arrData[4][0] = "Product E";
		arrData[5][0] = "Product F";

		// Store sales data
		arrData[0][1] = "567500";
		arrData[1][1] = "815300";
		arrData[2][1] = "556800";
		arrData[3][1] = "734500";
		arrData[4][1] = "676800";
		arrData[5][1] = "648500";

		// Store sales data for previous year
		arrData[0][2] = "547300";
		arrData[1][2] = "584500";
		arrData[2][2] = "754000";
		arrData[3][2] = "456300";
		arrData[4][2] = "754500";
		arrData[5][2] = "437600";

		multiSeriesXml = "<chart caption='Sales by Product' numberPrefix='$' formatNumberScale='1' rotateValues='1' placeValuesInside='1' decimals='0' >";

		// Initialize <categories> element - necessary to generate a
		// multi-series chart
		String strCategories = "<categories>";

		// Initiate <dataset> elements
		String strDataCurr = "<dataset seriesName='Current Year'>";
		String strDataPrev = "<dataset seriesName='Previous Year'>";

		// Iterate through the data
		for (int i = 0; i < arrData.length; i++) {
			// Append <category name='...' /> to strCategories
			strCategories = strCategories + "<category name='" + arrData[i][0]
					+ "' />";
			// Add <set value='...' /> to both the datasets
			strDataCurr = strDataCurr + "<set value='" + arrData[i][1] + "' />";
			strDataPrev = strDataPrev + "<set value='" + arrData[i][2] + "' />";
		}

		// Close <categories> element
		strCategories = strCategories + "</categories>";

		// Close <dataset> elements
		strDataCurr = strDataCurr + "</dataset>";
		strDataPrev = strDataPrev + "</dataset>";

		// Assemble the entire XML now
		multiSeriesXml += strCategories + strDataCurr + strDataPrev
				+ "</chart>";
	}

	/**
	 * Here, we have hard-coded the array, in real-world applications you would
	 * get the data from database or elsewhere as an array.
	 */
	private void constructSingleSeriesXML() {
		/*
		 * Let's store the sales data for 6 products in our array. We also store
		 * the name of products.
		 */
		String[][] arrData = new String[6][2];
		// Store Name of Products
		arrData[0][0] = "Product A";
		arrData[1][0] = "Product B";
		arrData[2][0] = "Product C";
		arrData[3][0] = "Product D";
		arrData[4][0] = "Product E";
		arrData[5][0] = "Product F";

		// Store sales data
		arrData[0][1] = "567500";
		arrData[1][1] = "815300";
		arrData[2][1] = "556800";
		arrData[3][1] = "734500";
		arrData[4][1] = "676800";
		arrData[5][1] = "648500";

		int i = 0;
		// Initialize <chart> element
		xml = "<chart caption='Sales by Product' numberPrefix='$' formatNumberScale='0'>";
		// Convert data to XML and append
		for (i = 0; i < arrData.length; i++) {
			xml += "<set label='" + arrData[i][0] + "' value='" + arrData[i][1]
					+ "' />";
		}
		// Close <chart> element
		xml += "</chart>";
	}

	/**
	 * 
	 */
	private void constructStackedXML() {
		/*
		 * The array will have three columns - first one for Quarter Name and
		 * the next two for data values. The first data value column would store
		 * sales information for Product A and the second one for Product B.
		 */
		String[][] arrData = new String[4][3];
		// Store Quarter Name
		arrData[0][0] = "Quarter 1";
		arrData[1][0] = "Quarter 2";
		arrData[2][0] = "Quarter 3";
		arrData[3][0] = "Quarter 4";
		// Sales data for Product A
		arrData[0][1] = "567500";
		arrData[1][1] = "815300";
		arrData[2][1] = "556800";
		arrData[3][1] = "734500";

		// Sales data for Product B
		arrData[0][2] = "547300";
		arrData[1][2] = "594500";
		arrData[2][2] = "754000";
		arrData[3][2] = "456300";

		/*
		 * Now, we need to convert this data into multi-series XML. We convert
		 * using string concatenation. strXML - Stores the entire XML
		 * strCategories - Stores XML for the <categories> and child <category>
		 * elements strDataProdA - Stores XML for current year's sales
		 * strDataProdB - Stores XML for previous year's sales
		 */

		// Initialize <chart> element
		stackedXml = "<chart caption='Sales' numberPrefix='$' formatNumberScale='0' chartLeftMargin='50' chartRightMargin='50'>";

		// Initialize <categories> element - necessary to generate a stacked
		// chart
		String strCategories = "<categories>";

		// Initiate <dataset> elements
		String strDataProdA = "<dataset seriesName='Product A'>";
		String strDataProdB = "<dataset seriesName='Product B'>";

		// Iterate through the data
		for (int i = 0; i < arrData.length; i++) {
			// Append <category name='...' /> to strCategories
			strCategories += "<category name='" + arrData[i][0] + "' />";
			// Add <set value='...' /> to both the datasets
			strDataProdA += "<set value='" + arrData[i][1] + "' />";
			strDataProdB += "<set value='" + arrData[i][2] + "' />";
		}

		// Close <categories> element
		strCategories += "</categories>";

		// Close <dataset> elements
		strDataProdA += "</dataset>";
		strDataProdB += "</dataset>";

		// Assemble the entire XML now
		stackedXml += strCategories + strDataProdA + strDataProdB + "</chart>";
	}

	public String getChartId() {
		return chartId;
	}

	/**
	 * @return the combinationFilename
	 */
	public String getCombinationFilename() {
		return combinationFilename;
	}

	/**
	 * @return the combinationXml
	 */
	public String getCombinationXml() {
		return combinationXml;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the multiSeriesFilename
	 */
	public String getMultiSeriesFilename() {
		return multiSeriesFilename;
	}

	/**
	 * @return the downloadUrl
	 */
	public String getMultiSeriesXml() {
		return multiSeriesXml;
	}

	/**
	 * @return the stackedFilename
	 */
	public String getStackedFilename() {
		return stackedFilename;
	}

	/**
	 * @return the saveUrl
	 */
	public String getStackedXml() {
		return stackedXml;
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

	/**
	 * @param combinationFilename
	 *            the combinationFilename to set
	 */
	public void setCombinationFilename(String combinationFilename) {
		this.combinationFilename = combinationFilename;
	}

	/**
	 * @param combinationXml
	 *            the combinationXml to set
	 */
	public void setCombinationXml(String combinationXml) {
		this.combinationXml = combinationXml;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param multiSeriesFilename
	 *            the multiSeriesFilename to set
	 */
	public void setMultiSeriesFilename(String multiSeriesFilename) {
		this.multiSeriesFilename = multiSeriesFilename;
	}

	/**
	 * @param downloadUrl
	 *            the downloadUrl to set
	 */
	public void setMultiSeriesXml(String multiSeriesXml) {
		this.multiSeriesXml = multiSeriesXml;
	}

	/**
	 * @param stackedFilename
	 *            the stackedFilename to set
	 */
	public void setStackedFilename(String stackedFilename) {
		this.stackedFilename = stackedFilename;
	}

	/**
	 * @param saveUrl
	 *            the saveUrl to set
	 */
	public void setStackedXml(String stackedXml) {
		this.stackedXml = stackedXml;
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
