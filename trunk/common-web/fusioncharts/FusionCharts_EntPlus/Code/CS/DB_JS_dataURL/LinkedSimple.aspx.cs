using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Text;
using InfoSoftGlobal;
using DataConnection;

public partial class DB_JS_dataURL_LinkedSimple : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        //In this example, we show how to connect FusionCharts to a database.
        //For the sake of ease, we've used an MSACCESS databases containing two
        //tables.

        //strXML will be used to store the entire XML document generated
        //Generate the chart element
        StringBuilder xmlData = new StringBuilder();

        xmlData.Append("<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' >");

        string strQuery = "select a.FactoryId,a.FactoryName,sum(b.Quantity) as TotQ from Factory_Master a,Factory_Output b where a.FactoryId=b.FactoryID group by a.FactoryId,a.FactoryName";

        // Connect to the DB
        DbConn oRs = new DbConn(strQuery);

        //Iterate through each factory
        while (oRs.ReadData.Read())
        {
            //Generate <set label='..' value='..' link='..' />
            //Note that we're setting link as newchart-xmlurl-url
            //This link denotes that linked chart would open 
            //The source data for each each is defined in the URL which will get data dynamically from the database as per the fctory id
            xmlData.AppendFormat("<set label='{0}' value='{1}' link='{2}'/>", oRs.ReadData["FactoryName"].ToString(), oRs.ReadData["TotQ"].ToString(), Server.UrlEncode("newchart-xmlurl-FactoryData.aspx?FactoryId=" + oRs.ReadData["FactoryId"].ToString()));
        }

        oRs.ReadData.Close();
        xmlData.Append("</chart>");

        //Create the chart - Pie 3D Chart with data from strXML
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", xmlData.ToString(), "FactorySum", "500", "250", false, true, false);
    }
}
