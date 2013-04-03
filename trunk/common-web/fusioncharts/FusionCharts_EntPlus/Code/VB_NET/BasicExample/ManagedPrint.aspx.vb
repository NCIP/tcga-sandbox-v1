Imports System.Text
Imports InfoSoftGlobal
Partial Class BasicExample_ManagedPrint
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Literal4.Text = FusionCharts.EnablePrintManager()
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../BasicExample/Data/Data.xml", "", "chart1", "600", "300", False, True)
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../BasicExample/Data/Data.xml", "", "chart2", "600", "300", False, True)
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf", "../BasicExample/Data/Data.xml", "", "chart3", "600", "300", False, True)

    End Sub
End Class
