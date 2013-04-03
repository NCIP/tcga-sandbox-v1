Imports InfoSoftGlobal
Imports System.Text
Partial Class BasicExample_SimpleChartJS
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        FusionCharts.SetDataFormat("json")
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../BasicExample/Data/Data.json", "", "chart1", "600", "300", False, True)


    End Sub
End Class
