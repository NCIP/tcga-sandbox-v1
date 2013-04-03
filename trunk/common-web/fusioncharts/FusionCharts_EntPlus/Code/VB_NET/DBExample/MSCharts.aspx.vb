Imports InfoSoftGlobal
Imports DataConnection
Imports System.Data.Odbc
Imports System.Data.OleDb
Imports System.Text

Partial Class DBExample_MSCharts
    Inherits System.Web.UI.Page
    Public Function mschartsconnection() As String
        'In this example, we show how to connect multi-series FusionCharts to a database.
        'For the sake of ease, we've used an Access database which is present in
        '../App_Data/FactoryDB.mdb. It just contains two tables, which are linked to each
        'other. 
        'xmlData will be used to store the entire XML document generated

        Dim xmlData As New StringBuilder()
        'Generate the chart element
        xmlData.Append("<chart caption='Factory Output report' subCaption='By Quantity' showBorder='1' formatNumberScale='0' rotatelabels='1' showvalues='0'>")
        xmlData.Append("<categories>")
        'create recordset to get details for the factory id
        

        
        'create recordset to get details for the datepro
        Dim factoryQuery As String = "select distinct format(datepro,'dd/mm/yyyy') as dd from factory_output"
        Dim oRs As New DbConn(factoryQuery)

        'Iterate through each record
        While oRs.ReadData.Read

            'Generate the category labels
            xmlData.Append("<category label='" & oRs.ReadData("dd").ToString() & "'/>")

        End While
        oRs.ReadData.Close()


        'Close categories element
        xmlData.Append("</categories>")
        'oRs4.ReadData.Close()

        'Create recordset to create details for factory names from the master table
        Dim factoryQuery2 As String = "select * from factory_master"
        Dim oRs1 As New DbConn(factoryQuery2)

        'Iterate through each record
        While oRs1.ReadData.Read()

            'Generate the <dataset seriesname='..'>
            xmlData.Append("<dataset seriesName='" & oRs1.ReadData("factoryname").ToString() & "'>")
            'Create recordset to get the details of the quantity from the factory_output table
            Dim factoryQuery3 As String = "select quantity from factory_output where factoryid=" + oRs1.ReadData("factoryid").ToString()
            Dim oRs2 As New DbConn(factoryQuery3)

            'Iterate through each record
            While oRs2.ReadData.Read()
                'Generate <set value='..' />
                xmlData.Append("<set value='" & oRs2.ReadData("quantity").ToString() & "'/>")
            End While

            oRs2.ReadData.Close()
            'Close dataset element
            xmlData.Append("</dataset>")

        End While
        oRs1.ReadData.Close()
        'Close chart element
        xmlData.Append("</chart>")

        'Create the chart - Multi-Series Line Chart with data from xmlData
        Return FusionCharts.RenderChart("../FusionCharts/MSLine.swf", "", xmlData.ToString(), "chartid", "600", "400", False, True)

    End Function

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Literal1.Text = mschartsconnection()

    End Sub
End Class
