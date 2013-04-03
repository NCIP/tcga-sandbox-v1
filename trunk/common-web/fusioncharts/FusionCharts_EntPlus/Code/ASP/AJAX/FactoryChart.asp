<%
'We've included ../Includes/FusionCharts.asp, which contains functions
'to help us easily embed the charts.
%>
	<!-- #INCLUDE FILE="../Includes/FusionCharts.asp" -->
	<!-- #INCLUDE FILE="../Includes/DBConn.asp" -->
<%
    'This page is invoked from Default.php. When the user clicks on a pie
    'slice in Default.php, the factory Id is passed to this page. We need
    'to get that factory id, get information from database and then show
    'a detailed chart.
	Dim FactoryId
    'Request the factory Id from Querystring
    FactoryId = Request("FactoryId") 
	If FactoryId = "" Then
		FactoryId = 1
	End If

    'Generate the chart element string
    strXML = "<chart palette='2' caption='Factory " & FactoryId & " Output ' subcaption='(In Units)' xAxisName='Date' showValues='1' >"

	'Create the recordset to retrieve data
	Set oRs = Server.CreateObject("ADODB.Recordset")

    'Now, we get the data for that factory
    strQuery = "select * from Factory_Output where FactoryId=" & FactoryId
    Set oRs = oConn.Execute(strQuery)
    
    'Iterate through each factory
    If Not oRs Is Nothing Then
        While Not oRs.EoF
            'Here, we convert date into a more readable form for set label.
            strXML = strXML & "<set label='" & datePart("d",ors("DatePro")) & "/" & datePart("m",ors("DatePro")) & "' value='" & ors("Quantity") & "'/>"
			oRs.MoveNext
		Wend
    End If
    
    'Close <chart> element
    strXML = strXML  & "</chart>"
	
    'Create the chart - Column 2D Chart with data from strXML
    Call renderChartHTML("../../FusionCharts/Column2D.swf", "", strXML, "FactoryDetailed", 600, 300, false)
%>
