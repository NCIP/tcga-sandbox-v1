using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Text;
using DataConnection;
using InfoSoftGlobal;

public partial class DBExample_Detailed : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        int Id;
        string strQuery2 = "Select FactoryId from Factory_Master";
        DbConn oRs3 = new DbConn(strQuery2);

        StringBuilder strXML = new StringBuilder();

        //Generate the chart element string
        strXML.Append("<chart palette='2' caption='Factory  Output ' subcaption='(In Units)' xAxisName='Date' showValues='1' labelStep='2' >");

        // Connet to the DB
        while (oRs3.ReadData.Read())
        {
            //int Id = Convert.ToInt32(oRs3.ReadData.Read());
            //Now, we get the data for that factory
            Id = Convert.ToInt32(oRs3.ReadData.Read());
            string strQuery = "select Format(DatePro,'dd/MM') as dDate, Quantity from Factory_Output where FactoryId=" + Id.ToString();
            DbConn oRs2 = new DbConn(strQuery);

            //Iterate through each factory
            while (oRs2.ReadData.Read())
            {
                //Here, we convert date into a more readable form for set label.
                strXML.AppendFormat("<set label='{0}' value='{1}' />", oRs2.ReadData["dDate"].ToString(), oRs2.ReadData["Quantity"].ToString());
            }

        }

        //Close <chart> element
        strXML.Append("</chart>");

        //Create the chart - Column 2D Chart with data from strXML
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "", strXML.ToString(), "FactoryDetailed", "600", "300", false, true, false);

    }
}
