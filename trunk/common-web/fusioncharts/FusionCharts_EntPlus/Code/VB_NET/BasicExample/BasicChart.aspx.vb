Imports InfoSoftGlobal


Partial Class BasicChart
    Inherits System.Web.UI.Page


    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        Literal1.Text = FusionCharts.RenderChartHTML("../FusionCharts/Column3D.swf", "Data/Data.xml", "", "myFirst", "600", "300", False)
    End Sub

End Class
