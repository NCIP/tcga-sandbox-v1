Imports InfoSoftGlobal
Imports System.Text
Partial Class BasicExample_MultiChartsJS
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        FusionCharts.SetRenderer("javascript")
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../BasicExample/Data/Data.xml", "", "chart1", "600", "300", False, True, False)
        FusionCharts.SetRenderer("javascript")
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf", "../BasicExample/Data/Data.xml", "", "chart2", "600", "300", False, True, False)
        FusionCharts.SetRenderer("javascript")
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Area2D.swf", "../BasicExample/Data/Data.xml", "", "chart3", "600", "300", False, True, False)


    End Sub
End Class
