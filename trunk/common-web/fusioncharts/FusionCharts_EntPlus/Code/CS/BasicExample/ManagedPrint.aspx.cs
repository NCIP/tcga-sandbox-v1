using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using InfoSoftGlobal;

public partial class BasicExample_ManagedPrint : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        
        // FusionCharts.EnablePrintManager(this);
        // OR
        Literal4.Text = FusionCharts.EnablePrintManager();
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../BasicExample/Data/Data.xml", "", "chart1", "600", "300", false, true, false);
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../BasicExample/Data/Data.xml", "", "chart2", "600", "300", false, true, false);
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf",     "../BasicExample/Data/Data.xml", "", "chart3", "600", "300", false, true, false);
    }
}
