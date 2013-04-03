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
public class FactoriesWithJSLinkBean {

	protected String chartId = "FactorySum";

	protected String width = "600";
	protected String height = "300";
	protected String filename = ChartType.PIE3D.getFileName();

	protected String xml = null;
	// We need to use single quotes in the fc:render tag because, we are using
	// dom to create the xml
	// and hence the values for attributes are in double quotes.
	protected String useSingleQuotes = "true";

	protected String factoryDetailsJsArrAsStr = null;

	public FactoriesWithJSLinkBean() {
	}

	public String getChartId() {
		return chartId;
	}

	public String getFactoryDetailsJsArrAsStr() {
		String jsVarString = "";
		try {
			DBConnection dbConn = new DBConnection();
			Connection oConn = dbConn.getConnection();
			// Database Objects - Initialization
			Statement st1 = null, st2 = null;
			ResultSet rs1 = null, rs2 = null;

			String strQuery = "";

			// Iterate through each factory
			strQuery = "select * from Factory_Master";
			st1 = oConn.createStatement();
			rs1 = st1.executeQuery(strQuery);

			String factoryId = null;
			String quantity = "";
			java.sql.Date date = null;
			java.util.Date uDate = null;
			String uDateStr = "";
			int indexCount = 0;
			while (rs1.next()) {
				indexCount += 1;
				factoryId = rs1.getString("FactoryId");

				// Create JavaScript code to add sub-array to data array
				// data is an array defined in JavaScript (see below)
				// We've added \t & \n to data so that if you View Source of the
				// output HTML, it will appear properly. It helps during
				// debugging
				jsVarString += "\t\t" + "data[" + indexCount
						+ "] = new Array();\n";

				// Now create second recordset to get details for this factory
				strQuery = "select * from Factory_Output where FactoryId="
						+ factoryId + " order by DatePro Asc ";
				st2 = oConn.createStatement();
				rs2 = st2.executeQuery(strQuery);
				while (rs2.next()) {
					date = rs2.getDate("DatePro");
					quantity = rs2.getString("Quantity");
					if (date != null) {
						uDate = new java.util.Date(date.getTime());
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
						uDateStr = sdf.format(uDate);
					}
					// Put this data into JavaScript as another nested array.
					// Finally the array would look like
					// data[factoryIndex][i][dataLabel,dataValue]
					jsVarString += "\t\t" + "data[" + indexCount
							+ "].push(new Array('" + uDateStr + "'," + quantity
							+ "));" + "\n\r";
				}
			}
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
		return jsVarString;

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
			int indexCount = 0;
			while (rs1.next()) {
				indexCount += 1;
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
				setElem.setAttribute("link", "javaScript:updateChart("
						+ indexCount + ",\"" + factoryName + "\")");
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
