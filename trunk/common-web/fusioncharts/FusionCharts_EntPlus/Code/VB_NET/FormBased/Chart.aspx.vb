Imports InfoSoftGlobal
Imports System.Text
Partial Class FormBased_Chart
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Dim strXML As StringBuilder = New StringBuilder()
        Dim intSoups As String = Request.Form("TextBoxSoups")
        Dim intSalads As String = Request("TextboxSalads")
        Dim intSandwiches As String = Request("TextboxSandwiches")
        Dim intBeverages As String = Request("TextboxBeverages")
        Dim intDesserts As String = Request("TextboxDesserts")

        strXML.Append("<chart caption='Sales by Product Category' subCaption='For this week' showPercentValues='1' pieSliceDepth='30' showBorder='1'>")
        strXML.Append("<set label='Soups' value='" & intSoups & "' />")
        strXML.Append("<set label='Salads' value='" & intSalads & "' />")
        strXML.Append("<set label='Sandwiches' value='" & intSandwiches & "' />")
        strXML.Append("<set label='Beverages' value='" & intBeverages & "' />")
        strXML.Append("<set label='Desserts' value='" & intDesserts & "' />")
        strXML.Append("</chart>")

        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", strXML.ToString(), "chart1", "600", "400", False, True, False)


    End Sub
End Class
