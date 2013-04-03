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
public partial class UTF8Examples_DataURL : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "Data/Data.xml", "", "myFirst", "500", "400", false, false);
    }
}
