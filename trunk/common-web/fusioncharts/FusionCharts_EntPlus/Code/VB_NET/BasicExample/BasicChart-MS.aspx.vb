Imports InfoSoftGlobal


Partial Class BasicChartMS
    Inherits System.Web.UI.Page


    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/MSColumn3D.swf", "Data/MSData.xml", "", "myFirst", "600", "300", False, True)
    End Sub

End Class
