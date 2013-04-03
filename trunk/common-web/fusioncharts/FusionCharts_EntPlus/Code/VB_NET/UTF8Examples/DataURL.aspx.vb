Imports InfoSoftGlobal

Partial Class UTF8Examples_DataURL
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "Data/Data.xml", "", "myFirst", "500", "400", False, False)
    End Sub
End Class
