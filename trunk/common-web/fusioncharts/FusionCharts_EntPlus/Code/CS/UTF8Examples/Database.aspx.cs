using System;
using System.Collections;
using System.Configuration;
using System.Data;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using InfoSoftGlobal;

public partial class UTF8Examples_Default : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        /*
            This page demonstrates the ease of generating charts using FusionCharts.
            For this chart, we've retrieved multilingual text from database
            through a XML relayer script file Data/getXMLFromDatabase.asp
            The file GetXMLFromDatabase.asp is already UTF-8 encoded having BOM
        */
        //Create the chart - Column 2D Chart
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "GetXMLFromDatabase.aspx", "", "myFirst", "500", "400", false, true);
    }
}
