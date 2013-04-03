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
using InfoSoftGlobal;

public partial class Export_MultipleCharts : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "./Data/SaveData.xml", "", "myFirst1", "600", "300", false, true);
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "./Data/SaveData.xml", "", "myFirst2", "600", "300", false, true);
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf",     "./Data/SaveData.xml", "", "myFirst3", "600", "300", false, true);
    }
}
