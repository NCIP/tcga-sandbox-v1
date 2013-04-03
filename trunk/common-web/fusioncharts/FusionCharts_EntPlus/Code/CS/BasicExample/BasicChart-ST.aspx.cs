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

public partial class BasicExample_BasicChartST : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        //This page demonstrates the ease of generating charts using FusionCharts.
        //For this chart, we've used a pre-defined MSData.xml (contained in /Data/ folder)
        //Ideally, you would NOT use a physical data file. Instead you'll have 
        //your own ASP.NET scripts virtually relay the XML data document. Such examples are also present.
        //For a head-start, we've kept this example very simple.


        // Create the chart - Stacked Column 3D Chart with data from Data/MSData.xml
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/StackedColumn3D.swf", "Data/MSData.xml", "", "myFirst", "600", "300", false, true);
    }
}
