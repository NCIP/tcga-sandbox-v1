Imports DataConnection

Partial Class UTF8Examples_GetXMLFromDatabase
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load
        'This page builds XML from database. The database contains UTF-8 encoded multilingual text.
        'We have pre encoded this file as UTF-8 encoded with BOM
        'Hence, we would just retrieve the text and relay it to the chart
        'For the sake of ease, we've used an MSAccess(MDB) databases - sales and all data in a table - 'monthly_utf8'
        'strXML will be used to store the entire XML document generated

        Dim strXML, strQuery As String

        'Generate the chart element
        strXML = "<chart caption='Monthly Sales Summary' subcaption='For the year 2008' "
        strXML = strXML + " xAxisName='Month' yAxisName='Sales' numberPrefix='$' showNames='1'"
        strXML = strXML + " showValues='0' showColumnShadow='1' animation='1'"
        strXML = strXML + " baseFontColor='666666' lineColor='FF5904' lineAlpha='85'"
        strXML = strXML + " valuePadding='10' labelDisplay='Rotate' useRoundEdges='1' >"

        ' Fetch all factory records
        strQuery = "select * from monthly_utf8"

        Dim oRs As DbConn = New DbConn(strQuery)

        'Iterate through each month
        While (oRs.ReadData.Read())
            'Generate <set label='..' value='..'/>
            strXML = strXML & "<set label='" & oRs.ReadData("month_name") & "' value='" & oRs.ReadData("amount") & "' />"
        End While

        oRs.ReadData.Close()
        ' Adding style
        strXML = strXML + "<styles><definition><style name='myCaptionFont' type='font' size='12'/></definition>"
        strXML = strXML + "<application><apply toObject='datalabels' styles='myCaptionFont' /></application></styles>"

        ' Finally, close <chart> element
        strXML = strXML + "</chart>"

        ' Set Proper output content-type with BOM
        Response.ContentType = "text/xml; characterset=utf-8"
        Dim UTFHeader() As Byte = {&HEF, &HBB, &HBF}
        Response.BinaryWrite(UTFHeader)

        ' Just write out the XML data
        Response.Write(strXML)
    End Sub
End Class
