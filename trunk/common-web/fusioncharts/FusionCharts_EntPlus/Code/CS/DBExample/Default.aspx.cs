using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Text;
using DataConnection;
using InfoSoftGlobal;

public partial class DBExample_Default : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        StringBuilder strXML = new StringBuilder();

        //$strXML will be used to store the entire XML document generated
        //Generate the chart element
        strXML.Append("<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units'>");

        // Fetch all factory records
        string strQuery = "select * from Factory_Master ";
        DbConn oRs1 = new DbConn(strQuery);
        while (oRs1.ReadData.Read())
        {
            string strQuery1 = "select sum(Quantity) as TotQ from Factory_Output where FactoryId=" + oRs1.ReadData["FactoryId"];
            DbConn oRs = new DbConn(strQuery1);
            //Iterate through each factory
            while (oRs.ReadData.Read())
            {
                //Now create a second query to get details for this factory
                //Note that we're setting link as Detailed.php?FactoryId=<<FactoryId>>
                strXML.AppendFormat("<set label='{0}'  value='{1}'  link='{2}' />", oRs1.ReadData["FactoryName"].ToString(), oRs.ReadData["TotQ"].ToString(), ("Detailed.aspx?Id=" + oRs1.ReadData["FactoryId"]));
            }
            //free the resultset
            oRs.ReadData.Close();
        }
        oRs1.ReadData.Close();

        //Finally, close <chart> element
        strXML.Append("</chart>");

        //Create the chart - Pie 3D Chart with data from strXML
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", strXML.ToString(), "FactorySum", "600", "300", false, true, false);

    }
}
