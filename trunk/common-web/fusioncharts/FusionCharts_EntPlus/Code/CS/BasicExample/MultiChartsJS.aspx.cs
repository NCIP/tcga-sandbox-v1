using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using InfoSoftGlobal;

public partial class BasicExample_MultiChartsJS : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        FusionCharts.SetRenderer("javascript");
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../BasicExample/Data/Data.xml", "", "chart1", "600", "300", false, true, false);
        FusionCharts.SetRenderer("javascript");
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf", "../BasicExample/Data/Data.xml", "", "chart2", "600", "300", false, true, false);
        FusionCharts.SetRenderer("javascript");
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Area2D.swf", "../BasicExample/Data/Data.xml", "", "chart3", "600", "300", false, true);
    }
}
