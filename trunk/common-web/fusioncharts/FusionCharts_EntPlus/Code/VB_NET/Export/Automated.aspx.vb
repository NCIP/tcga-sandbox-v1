Imports System
Imports InfoSoftGlobal


Partial Class Export_Automated
    Inherits System.Web.UI.Page

    Public Function AutomatedExport() As String

        'Create the chart - Column3D Chart with data of DownloadData.xml file using dataURL method
        Return FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "../Export/Data/DownloadData.xml", "", "chartId", "600", "300", False, True)

    End Function


    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Literal1.Text = AutomatedExport()

    End Sub
End Class
