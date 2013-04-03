using System;
using System.Collections;
using System.Configuration;
using System.Data;

using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;

using System.Text;
using InfoSoftGlobal;
using DataConnection;

public partial class DB_DrillDown_Default : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        //In this example, we show how to connect FusionCharts to a database.
        //For the sake of ease, we//ve used an Access database which is present in
        //../App_Data/FactoryDB.mdb. It just contains two tables, which are linked to each
        //other. 
        //xmlData will be used to store the entire XML document generated
        StringBuilder xmlData = new StringBuilder();
        //We also keep a flag to specify whether we've to animate the chart or not.
        //If the user is viewing the detailed chart and comes back to this page, he shouldn't
        //see the animation again.

        //Generate the chart element
        xmlData.Append("<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units'>");

        //create recordset to get details for the factories
        string factoryQuery = "select a.FactoryId, a.FactoryName, sum(b.Quantity) as TotQ from .Factory_Master a, Factory_Output b where a.FactoryId=b.FactoryID group by a.FactoryId, a.FactoryName";
        DbConn oRs = new DbConn(factoryQuery);

        //Iterate through each record
        while (oRs.ReadData.Read())
        {
            //Generate <set name='...' value='...' link='...'/>	
            //The link causes drill-down by loading a another page
            //The page is passed the factoryId
            //Accordingly the page creates a detailed chart against that FactoryId
            xmlData.Append("<set label='" + oRs.ReadData["FactoryName"].ToString() + "' value='" + oRs.ReadData["TotQ"].ToString() + "' link='" + ("Detailed.aspx?FactoryId=" + oRs.ReadData["FactoryId"].ToString()) + "'/>");
        }

        oRs.ReadData.Close();
        //Finally, close <chart> element
        xmlData.Append("</chart>");

        //Create the chart - Pie 3D Chart with data from xmlData
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", xmlData.ToString(), "FactorySum", "600", "300", false, true);
    }
}
