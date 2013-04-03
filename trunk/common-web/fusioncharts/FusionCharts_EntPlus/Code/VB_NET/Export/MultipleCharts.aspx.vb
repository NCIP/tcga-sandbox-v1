Imports InfoSoftGlobal
Partial Class Export_MultipleCharts
    Inherits System.Web.UI.Page

    Private Sub multiplecharts()

        'Create the chart - Column3D Chart with data of SaveData.xml file using dataURL method
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../Export/Data/SaveData.xml", "", "chartId1", "600", "300", False, True)
        'Create the chart - Column2D Chart with data of SaveData.xml file using dataURL method
        Literal2.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "../Export/Data/SaveData.xml", "", "chartId2", "600", "300", False, True)
        'Create the chart - Line Chart with data of SaveData.xml file using dataURL method
        Literal3.Text = FusionCharts.RenderChart("../FusionCharts/Line.swf", "../Export/Data/SaveData.xml", "", "chartId3", "600", "300", False, True)


    End Sub
    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Call multiplecharts()

    End Sub
End Class
