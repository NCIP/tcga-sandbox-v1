package com.fusioncharts.sampledata;

import java.sql.Connection;
import java.sql.ResultSet;
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
public class FactoriesBeanWithoutLink {

	protected String chartId = "FactorySum";

	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.PIE3D.getFileName();

	protected String xml = null;
	// We need to use single quotes in the fc:render tag because, we are using
	// dom to create the xml
	// and hence the values for attributes are in double quotes.
	protected String useSingleQuotes = "true";

	public FactoriesBeanWithoutLink() {
	}

	public String getChartId() {
		return chartId;
	}

	private String getFactoryXML() {
		// strXML will be used to store the entire XML document generated
		String strXML = "";
		try {
			DBConnection dbConn = new DBConnection();
			Connection oConn = dbConn.getConnection();
			// Database Objects - Initialization
			Statement st1 = null, st2 = null;
			ResultSet rs1 = null, rs2 = null;

			String strQuery = "";

			Map<String, String> chartAttributes = new HashMap<String, String>();
			;
			chartAttributes.put("caption", "Factory Output report");
			chartAttributes.put("subCaption", "By Quantity");
			chartAttributes.put("pieSliceDepth", "30");
			chartAttributes.put("showBorder", "1");
			chartAttributes.put("formatNumberScale", "0");
			chartAttributes.put("numberSuffix", " Units");

			DOMHelper domHelper = new DOMHelper();
			Document chartDoc = domHelper.getDocument();
			Element rootElement = chartDoc.createElement("chart");
			domHelper.addAttributesToElement(rootElement, chartAttributes);

			// Generate the chart element
			// strXML =
			// "<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' >";

			// Iterate through each factory
			strQuery = "select * from Factory_Master";
			st1 = oConn.createStatement();
			rs1 = st1.executeQuery(strQuery);

			String factoryId = null;
			String factoryName = null;
			String totalOutput = "";

			while (rs1.next()) {
				totalOutput = "";
				factoryId = rs1.getString("FactoryId");
				factoryName = rs1.getString("FactoryName");
				// Now create second recordset to get details for this factory
				strQuery = "select sum(Quantity) as TotOutput from Factory_Output where FactoryId="
						+ factoryId;
				st2 = oConn.createStatement();
				rs2 = st2.executeQuery(strQuery);
				if (rs2.next()) {
					totalOutput = rs2.getString("TotOutput");
				}
				// Generate <set label='..' value='..'/>
				// strXML += "<set label='" + factoryName + "' value='"
				// +totalOutput+ "' link='javaScript:updateChart("+factoryId +
				// ")'/>";
				Element setElem = chartDoc.createElement("set");
				setElem.setAttribute("label", factoryName);
				setElem.setAttribute("value", totalOutput);
				rootElement.appendChild(setElem);
				// close the resultset,statement
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
			}
			// Finally, close <chart> element
			// strXML += "</chart>";
			chartDoc.appendChild(rootElement);
			strXML = domHelper.getXMLString(chartDoc);
			// close the resultset,statement,connection
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
		xml = getFactoryXML();
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
