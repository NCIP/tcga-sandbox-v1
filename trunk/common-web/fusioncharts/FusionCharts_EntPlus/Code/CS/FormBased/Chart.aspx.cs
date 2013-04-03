using System;
using System.Collections.Generic;

using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Text;
using InfoSoftGlobal;

public partial class FormBased_Chart : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        StringBuilder strXML = new StringBuilder();
        string intSoups = Request.Form["TextBoxSoups"];
        string intSalads = Request["TextboxSalads"];
        string intSandwiches = Request["TextboxSandwiches"];
        string intBeverages = Request["TextboxBeverages"];
        string intDesserts = Request["TextboxDesserts"];

        strXML.Append("<chart caption='Sales by Product Category' subCaption='For this week' showPercentValues='1' pieSliceDepth='30' showBorder='1'>");
        strXML.AppendFormat("<set label='Soups' value='{0}' />" , intSoups);
	    strXML.AppendFormat("<set label='Salads' value='{0}' />" , intSalads);
        strXML.AppendFormat("<set label='Sandwiches' value='{0}' />" , intSandwiches);
        strXML.AppendFormat("<set label='Beverages' value='{0}' />" , intBeverages);
        strXML.AppendFormat("<set label='Desserts' value='{0}' />" , intDesserts);
	    strXML.Append("</chart>");

        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", strXML.ToString(), "chart1", "600", "400", false, true, false);

    }
}
