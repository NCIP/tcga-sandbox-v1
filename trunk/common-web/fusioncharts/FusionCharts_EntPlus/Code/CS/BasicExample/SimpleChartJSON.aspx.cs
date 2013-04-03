using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using InfoSoftGlobal;

public partial class BasicExample_SimpleChartJSON : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        FusionCharts.SetDataFormat("json");
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../BasicExample/Data/Data.json", "", "chart1", "600", "300", false, true, false);
    }
}
