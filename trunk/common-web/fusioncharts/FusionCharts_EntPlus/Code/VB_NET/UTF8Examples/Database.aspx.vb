Imports InfoSoftGlobal

Partial Class UTF8Examples_Database
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        ' This page demonstrates the ease of generating charts using FusionCharts.
        ' For this chart, we've retrieved multilingual text from database
        ' through a XML relayer script file Data/getXMLFromDatabase.asp
        ' The file GetXMLFromDatabase.asp is already UTF-8 encoded having BOM
        ' Create the chart - Column 2D Chart
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column2D.swf", "GetXMLFromDatabase.aspx", "", "myFirst", "500", "400", False, True)
    End Sub
End Class
