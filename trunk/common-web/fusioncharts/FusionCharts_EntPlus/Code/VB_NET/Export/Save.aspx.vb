Imports InfoSoftGlobal

Partial Class Export_SaveCharts
    Inherits System.Web.UI.Page

    Function saveCharts() As String

        'Create the chart - Column3D Chart with data of SaveData.xml file using dataURL method
        Return FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../Export/Data/SaveData.xml", "", "chartid", "600", "400", False, True)

    End Function

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Literal1.Text = saveCharts()

    End Sub
End Class
