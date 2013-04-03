package com.fusioncharts.sampledata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
public class FactoryDetailsBean {

	protected String chartId = "FactoryDetails";

	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.COLUMN2D.getFileName();

	protected String xml = null;
	// We need to use single quotes in the fc:render tag because, we are using
	// dom to create the xml
	// and hence the values for attributes are in double quotes.
	protected String useSingleQuotes = "true";

	protected String factoryId = "1"; // Default 1

	public FactoryDetailsBean() {
	}

	public String getChartId() {
		return chartId;
	}

	private String getFactoryDetailsXML(String factoryId) {
		// strXML will be used to store the entire XML document generated
		String strXML = "";
		try {
			DBConnection dbConn = new DBConnection();
			Connection oConn = dbConn.getConnection();
			// Database Objects - Initialization
			Statement st = null;
			ResultSet rs = null;
			java.sql.Date date = null;
			java.util.Date uDate = null;
			String uDateStr = "";
			String quantity = "";

			String strQuery = "";

			Map<String, String> chartAttributes = new HashMap<String, String>();
			chartAttributes.put("caption", "Factory " + factoryId + " Output");
			chartAttributes.put("subCaption", "(In Units)");
			chartAttributes.put("xAxisName", "Date");
			chartAttributes.put("showValues", "1");
			chartAttributes.put("labelStep", "2");
			chartAttributes.put("palette", "2");

			DOMHelper domHelper = new DOMHelper();
			Document chartDoc = domHelper.getDocument();
			Element rootElement = chartDoc.createElement("chart");
			domHelper.addAttributesToElement(rootElement, chartAttributes);

			// Now, we get the data for that factory
			strQuery = "select * from Factory_Output where FactoryId="
					+ factoryId + " order by DatePro Asc ";

			st = oConn.createStatement();
			rs = st.executeQuery(strQuery);

			while (rs.next()) {
				date = rs.getDate("DatePro");
				quantity = rs.getString("Quantity");
				if (date != null) {
					uDate = new java.util.Date(date.getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
					uDateStr = sdf.format(uDate);
				}
				// Generate <set label='..' value='..'/>
				Element setElem = chartDoc.createElement("set");
				setElem.setAttribute("label", uDateStr);
				setElem.setAttribute("value", quantity);
				rootElement.appendChild(setElem);
			}
			// Finally, close <chart> element
			chartDoc.appendChild(rootElement);
			strXML = domHelper.getXMLString(chartDoc);
			// close the resultset,statement,connection
			// enclose them in try catch block
			try {
				if (null != rs) {
					rs.close();
					rs = null;
				}
			} catch (java.sql.SQLException e) {
				System.out.println("Could not close the resultset");
			}
			try {
				if (null != st) {
					st.close();
					st = null;
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
		// not required since we will useSingleQuotes in fc:renderHTML or
		// fc:render tag
		// strXML = strXML.replaceAll("\"", "\\\\\"");
		return strXML;

	}

	/**
	 * @return the factoryId
	 */
	public String getFactoryId() {
		return factoryId;
	}

	public String getFilename() {
		return filename;
	}

	public String getHeight() {
		return height;
	}

	/**
	 * @return the useSingleQuotes
	 */
	public String getUseSingleQuotes() {
		return useSingleQuotes;
	}

	public String getWidth() {
		return width;
	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * @param factoryId
	 *            the factoryId to set
	 */
	public void setFactoryId(String factoryId) {
		this.factoryId = factoryId;
		// set the xml for this factory
		xml = getFactoryDetailsXML(factoryId);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param useSingleQuotes
	 *            the useSingleQuotes to set
	 */
	public void setUseSingleQuotes(String useSingleQuotes) {
		this.useSingleQuotes = useSingleQuotes;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @param xml
	 *            the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

}
