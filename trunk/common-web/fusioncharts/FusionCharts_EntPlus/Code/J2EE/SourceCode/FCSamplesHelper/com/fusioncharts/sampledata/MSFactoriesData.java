package com.fusioncharts.sampledata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fusioncharts.database.DBConnection;

/**
 * Bean containing sample chart data. Default values for all the fields have
 * been set. These can be changed using the set methods. This class has been
 * used as a backing bean in JSF examples.
 * 
 * @author Infosoft Global (P) Ltd.
 * 
 */
/**
 * @author InfoSoft Global (P) Ltd.
 *
 */
/**
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class MSFactoriesData {

	protected String chartId = "FactorySum";
	protected String width = "700";
	protected String height = "400";

	protected String filename = ChartType.MSLINE.getFileName();
	protected String xml = null;

	// We need to use single quotes in the fc:render tag because, we are using
	// dom to create the xml
	// and hence the values for attributes are in double quotes.
	protected String useSingleQuotes = "true";

	/**
	 * Build the xml for the categories
	 * 
	 * @param chartDoc
	 * @param rootElement
	 * @param categoriesResultSet
	 * @throws SQLException
	 */
	private void buildCategoriesXML(Document chartDoc, Element rootElement,
			ResultSet categoriesResultSet) throws SQLException {
		Element categoriesElem = chartDoc.createElement("categories");
		if (categoriesResultSet.next()) {
			while (categoriesResultSet.next()) {
				String datePro = categoriesResultSet.getString("DatePro");
				Element categoryElem = chartDoc.createElement("category");
				categoryElem.setAttribute("label", datePro);
				categoriesElem.appendChild(categoryElem);
			}
			rootElement.appendChild(categoriesElem);
		}
	}

	/**
	 * Build the xml for datasets
	 * 
	 * @param chartDoc
	 * @param rootElement
	 * @param datasetResultSet
	 * @throws SQLException
	 */
	private void buildDatasetXML(Document chartDoc, Element rootElement,
			ResultSet datasetResultSet) throws SQLException {
		String factoryName = null;
		String previousFactoryName = null;
		Element datasetElem = null;
		if (datasetResultSet.next()) {

			while (datasetResultSet.next()) {
				factoryName = datasetResultSet.getString("FactoryName");
				if (!factoryName.equals(previousFactoryName)) {
					datasetElem = chartDoc.createElement("dataset");
					datasetElem.setAttribute("seriesName", factoryName);
				}
				String quantity = datasetResultSet.getString("Quantity");
				Element setElem = chartDoc.createElement("set");
				setElem.setAttribute("value", quantity);
				datasetElem.appendChild(setElem);
				if (!factoryName.equals(previousFactoryName)) {
					rootElement.appendChild(datasetElem);
				}
				previousFactoryName = factoryName;
			}

		}
	}

	/**
	 * @return
	 */
	public String getChartId() {
		return chartId;
	}

	/**
	 * The xml containing the details of output for each factory
	 * 
	 * @return String - the xml for this chart
	 */
	private String getFactoryXML() {
		// strXML will be used to store the entire XML document generated
		String strXML = "";
		try {
			DBConnection dbConn = new DBConnection();
			Connection oConn = dbConn.getConnection();
			// Database Objects - Initialization
			Statement st1 = null, st2 = null;
			ResultSet rs1 = null, rs2 = null;

			String strCategoryQuery = "";
			String strQuery = "";

			Map<String, String> chartAttributes = new HashMap<String, String>();
			;
			chartAttributes.put("caption", "Factory Output report");
			chartAttributes.put("subCaption", "By Quantity");
			chartAttributes.put("xAxisName", "Factory");
			chartAttributes.put("yAxisName", "Units");
			chartAttributes.put("showValues", "0");
			chartAttributes.put("animation", "1");
			chartAttributes.put("formatNumberScale", "0");
			chartAttributes.put("rotateValues", "1");

			DOMHelper domHelper = new DOMHelper();
			Document chartDoc = domHelper.getDocument();
			Element rootElement = chartDoc.createElement("chart");
			domHelper.addAttributesToElement(rootElement, chartAttributes);

			// Generate the chart element

			// Iterate through each factory
			strCategoryQuery = "select distinct DATE_FORMAT(factory_output.DatePro,'%c-%d-%Y') as DatePro from factory_output order by DatePro";
			strQuery = "select factory_master.FactoryName, DATE_FORMAT(factory_output.DatePro,'%c-%d-%Y') as DatePro, factory_output.Quantity from factory_master factory_master, factory_output factory_output where factory_output.FactoryID = factory_master.FactoryId order by factory_output.FactoryID, factory_output.DatePro";

			st1 = oConn.createStatement();
			rs1 = st1.executeQuery(strCategoryQuery);
			buildCategoriesXML(chartDoc, rootElement, rs1);

			st1 = oConn.createStatement();
			rs1 = st1.executeQuery(strCategoryQuery);
			// close the resultset,statement
			// enclose them in try catch block
			try {
				if (null != rs1) {
					rs1.close();
					rs1 = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the resultset");
			}
			try {
				if (null != st1) {
					st1.close();
					st1 = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the statement");
			}

			st2 = oConn.createStatement();
			rs2 = st2.executeQuery(strQuery);
			buildDatasetXML(chartDoc, rootElement, rs2);
			// close the resultset,statement
			// enclose them in try catch block

			// Finally, close <chart> element
			// strXML += "</chart>";
			chartDoc.appendChild(rootElement);
			strXML = domHelper.getXMLString(chartDoc);
			// close the resultset,statement,connection
			// enclose them in try catch block

			try {
				if (null != rs2) {
					rs2.close();
					rs2 = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the resultset");
			}
			try {
				if (null != st2) {
					st2.close();
					st2 = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the statement");
			}
			try {
				if (null != oConn) {
					oConn.close();
					oConn = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the connection");
			}

		} catch (java.sql.SQLException e) {
			System.out.println("Could not close the statement");
		}
		// not required since we will useSingleQuotes in fc:renderHTML tag
		// strXML = strXML.replaceAll("\"", "\\\\\"");
		return strXML;

	}

	/**
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @return
	 */
	public String getUseSingleQuotes() {
		return useSingleQuotes;
	}

	/**
	 * @return
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @return
	 */
	public String getXml() {
		xml = getFactoryXML();
		return xml;
	}

	/**
	 * @param chartId
	 */
	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param height
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param useSingleQuotes
	 */
	public void setUseSingleQuotes(String useSingleQuotes) {
		this.useSingleQuotes = useSingleQuotes;
	}

	/**
	 * @param width
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @param xml
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

}
