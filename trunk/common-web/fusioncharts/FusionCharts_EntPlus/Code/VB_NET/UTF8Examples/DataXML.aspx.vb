Imports InfoSoftGlobal

Partial Class UTF8Examples_DataXML
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        Dim strXML As String

        ' Create an XML data document in a string variable
        strXML = "<chart caption='Monthly Sales Summary' subcaption='For the year 2008' "
        strXML = strXML + " xAxisName='Month' yAxisName='Sales' numberPrefix='$' showNames='1'"
        strXML = strXML + " showValues='0' showColumnShadow='1' animation='1'"
        strXML = strXML + " baseFontColor='666666' lineColor='FF5904' lineAlpha='85'"
        strXML = strXML + " valuePadding='10' labelDisplay='rotate' useRoundEdges='1'>"
        strXML = strXML + "<set label='januári' value='17400' />"
        strXML = strXML + "<set label='Fevruários' value='19800' />"
        strXML = strXML + "<set label='مارس' value='21800' />"
        strXML = strXML + "<set label='أبريل' value='23800' />"
        strXML = strXML + "<set label='五月' value='29600' />"
        strXML = strXML + "<set label='六月' value='27600' />"
        strXML = strXML + "<set label='תִּשׁרִי' value='31800' />"
        strXML = strXML + "<set label='Marešwān' value='39700' />"
        strXML = strXML + "<set label='settèmbre' value='37800' />"
        strXML = strXML + "<set label='ottàgono' value='21900' />"
        strXML = strXML + "<set label='novèmbre' value='32900' />"
        strXML = strXML + "<set label='décembre' value='39800' />"
        strXML = strXML + "<styles><definition><style name='myCaptionFont' type='font' size='12'/></definition>"
        strXML = strXML + "<application><apply toObject='datalabels' styles='myCaptionFont' /></application></styles>"
        strXML = strXML + "</chart>"

        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "", strXML, "myFirst", "500", "400", False, False)
    End Sub
End Class
