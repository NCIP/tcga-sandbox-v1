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

public partial class BasicExample_BasicChartCSY : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        //This page demonstrates the ease of generating charts using FusionCharts.
        //For this chart, we've used a pre-defined CSYSData.xml (contained in /Data/ folder)
        //Ideally, you would NOT use a physical data file. Instead you'll have 
        //your own ASP.NET scripts virtually relay the XML data document. Such examples are also present.
        //For a head-start, we've kept this example very simple.


        // Create the chart - Combination Chart with data from Data/CSYData.xml
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/MSCombi2D.swf", "Data/CSYData.xml", "", "myFirst", "600", "300", false, true);
    }
}
