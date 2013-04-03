Imports InfoSoftGlobal
Partial Class BasicExample_TransparentChart
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        ''This page demonstrates the ease of generating charts using FusionCharts.
        ''For this chart, we've used a pre-defined Data.xml (contained in /Data/ folder)
        ''Ideally, you would NOT use a physical data file. Instead you'll have 
        ''your own ASP.NET scripts virtually relay the XML data document. Such examples are also present.
        ''For a head-start, we've kept this example very simple.

        'Create an XML data document in a string variable
        Dim xmlData As StringBuilder = New StringBuilder()
        xmlData.Append("<chart bgAlpha='0' canvasBgAlpha='0' canvasBorderAlpha='100' showBorder='1' caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units'>")
        xmlData.Append("<set label='Jan' value='462' />")
        xmlData.Append("<set label='Feb' value='857' />")
        xmlData.Append("<set label='Mar' value='671' />")
        xmlData.Append("<set label='Apr' value='494' />")
        xmlData.Append("<set label='May' value='761' />")
        xmlData.Append("<set label='Jun' value='960' />")
        xmlData.Append("<set label='Jul' value='629' />")
        xmlData.Append("<set label='Aug' value='622' />")
        xmlData.Append("<set label='Sep' value='376' />")
        xmlData.Append("<set label='Oct' value='494' />")
        xmlData.Append("<set label='Nov' value='761' />")
        xmlData.Append("<set label='Dec' value='960' />")
        xmlData.Append("</chart>")

        ''Create the chart - Transparent Column 3D Chart
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "", xmlData.ToString(), "myFirst", "600", "300", False, True, True)


    End Sub
End Class
