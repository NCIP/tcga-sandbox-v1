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

public partial class BasicExample_MultiCharts : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

        //This page demonstrates how you can show multiple charts on the same page.
        //For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
        //However, you can very easily change the data source for any chart. 

        //IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
        //If you do not provide a unique Id, only the last chart might be visible.
        //Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.

        //Create the chart - Column 3D Chart with data from Data/Data.xml
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../BasicExample/Data/Data.xml", "", "chart1", "600", "300", false, false);



        //This page demonstrates how you can show multiple charts on the same page.
        //For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
        //However, you can very easily change the data source for any chart. 

        //IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
        //If you do not provide a unique Id, only the last chart might be visible.
        //Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.

        //Now, create a Column 2D Chart
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../BasicExample/Data/Data.xml", "", "chart2", "600", "300", false, true);


        //This page demonstrates how you can show multiple charts on the same page.
        //For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
        //However, you can very easily change the data source for any chart. 

        //IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
        //If you do not provide a unique Id, only the last chart might be visible.
        //Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.

        //Now, create a Line 2D Chart
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf", "../BasicExample/Data/Data.xml", "", "chart3", "600", "300", false, true);


        //This page demonstrates how you can show multiple charts on the same page.
        //For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
        //However, you can very easily change the data source for any chart. 

        //IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
        //If you do not provide a unique Id, only the last chart might be visible.
        //Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.

        //Now, create a Grid Chart
        Literal4.Text = FusionCharts.RenderChart("../FusionCharts/SSGrid.swf", "../BasicExample/Data/Data.xml", "", "chart4", "600", "300", false, true);


    }
}
