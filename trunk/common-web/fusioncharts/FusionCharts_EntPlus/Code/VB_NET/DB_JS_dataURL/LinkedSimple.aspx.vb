Imports InfoSoftGlobal
Imports System.Text
Imports DataConnection
Partial Class DB_JS_dataURL_LinkedSimple
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        'In this example, we show how to connect FusionCharts to a database.
        'For the sake of ease, we've used an MySQL databases containing two
        'tables.

        ' Connect to the DB

        'strXML will be used to store the entire XML document generated
        'Generate the chart element
        Dim xmlData As StringBuilder = New StringBuilder()
        'string strXML = ""
        xmlData.Append("<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' >")


        Dim strQuery As String = "select a.FactoryId,a.FactoryName,sum(b.Quantity) as TotQ from Factory_Master a,Factory_Output b where a.FactoryId=b.FactoryID group by a.FactoryId,a.FactoryName"
        '  result = strQuery 
        Dim oRs As DbConn = New DbConn(strQuery)
        'Iterate through each factory
        ' if (result == strQuery) {
        While oRs.ReadData.Read()

            'Generate <set label='..' value='..' link='..' />
            'Note that we're setting link as newchart-xmlurl-url
            'This link denotes that linked chart would open 
            'The source data for each each is defined in the URL which will get data dynamically from the database as per the fctory id
            xmlData.AppendFormat("<set label='{0}' value='{1}' link='{2}'/>", oRs.ReadData("FactoryName").ToString(), oRs.ReadData("TotQ").ToString(), Server.UrlEncode("newchart-xmlurl-FactoryData.aspx?FactoryId=" + oRs.ReadData("FactoryId").ToString()))
            'free the resultset
        End While

        oRs.ReadData.Close()
        xmlData.Append("</chart>")

        'Create the chart - Pie 3D Chart with data from strXML
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Pie3D.swf", "", xmlData.ToString(), "FactorySum", "500", "250", False, True, False)


    End Sub
End Class
