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

public partial class UTF8Examples_DataXML : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        string strXML;
        //Create an XML data document in a string variable
        strXML = "<chart caption='Monthly Sales Summary' subcaption='For the year 2008' ";
        strXML = strXML + " xAxisName='Month' yAxisName='Sales' numberPrefix='$' showNames='1'";
        strXML = strXML + " showValues='0' showColumnShadow='1' animation='1'";
        strXML = strXML + " baseFontColor='666666' lineColor='FF5904' lineAlpha='85'";
        strXML = strXML + " valuePadding='10' labelDisplay='rotate' useRoundEdges='1'>";
        strXML = strXML + "<set label='januári' value='17400' />";
        strXML = strXML + "<set label='Fevruários' value='19800' />";
        strXML = strXML + "<set label='مارس' value='21800' />";
        strXML = strXML + "<set label='أبريل' value='23800' />";
        strXML = strXML + "<set label='五月' value='29600' />";
        strXML = strXML + "<set label='六月' value='27600' />";
        strXML = strXML + "<set label='תִּשׁרִי' value='31800' />";
        strXML = strXML + "<set label='Marešwān' value='39700' />";
        strXML = strXML + "<set label='settèmbre' value='37800' />";
        strXML = strXML + "<set label='ottàgono' value='21900' />";
        strXML = strXML + "<set label='novèmbre' value='32900' />";
        strXML = strXML + "<set label='décembre' value='39800' />";
        strXML = strXML + "<styles><definition><style name='myCaptionFont' type='font' size='12'/></definition>";
        strXML = strXML + "<application><apply toObject='datalabels' styles='myCaptionFont' /></application></styles>";
        strXML = strXML + "</chart>"; 

        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "", strXML, "myFirst", "500", "400", false, false);
    }
}
