Imports InfoSoftGlobal


Partial Class BasicChartST
    Inherits System.Web.UI.Page


    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/StackedSColumn3D.swf", "Data/MSData.xml", "", "myFirst", "600", "300", False, True)
    End Sub

End Class
