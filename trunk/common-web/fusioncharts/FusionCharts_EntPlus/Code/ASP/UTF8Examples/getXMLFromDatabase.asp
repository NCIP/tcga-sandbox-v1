<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- #INCLUDE FILE="../Includes/DBConn.asp" -->
<%
	
	'This page builds XML from database. The database contains UTF-8 encoded multilingual text.
	'We have pre encoded this file as UTF-8 encoded with BOM
	'Hence, we would just retrieve the text and relay it to the chart
	
	'We've included  ../Includes/DBConn.asp, which contains functions
    'to help us easily connect to a database.

    'For the sake of ease, we've used an MSAccess(MDB) databases - sales and all data in a table - 'monthly_utf8'

    'strXML will be used to store the entire XML document generated
    'Generate the chart element
	strXML  = "<chart caption='Monthly Sales Summary' subcaption='For the year 2008' "
	strXML = strXML &  " xAxisName='Month' yAxisName='Sales' numberPrefix='$' showNames='1'"
	strXML = strXML &  " showValues='0' showColumnShadow='1' animation='1'"	
	strXML = strXML &  " baseFontColor='666666' lineColor='FF5904' lineAlpha='85'"
	strXML = strXML &  " valuePadding='10' labelDisplay='Rotate' useRoundEdges='1' >"	

	'Create the recordset to retrieve data
	Set oRs = Server.CreateObject("ADODB.Recordset")
	
    ' Fetch all factory records
    strQuery = "select * from monthly_utf8"
    Set oRs = oConn.Execute(strQuery)
    
    'Iterate through each month
    If Not oRs Is Nothing Then
        While Not oRs.Eof
            'Generate <set label='..' value='..'/>     
            strXML = strXML &  "<set label='" & oRs("month_name") & "' value='" & oRs("amount") & "' />"
			oRs.MoveNext
		Wend
    End If
	
	' add style
	strXML = strXML &  "<styles><definition><style name='myCaptionFont' type='font' size='12'/></definition>"
	strXML = strXML &  "<application><apply toObject='datalabels' styles='myCaptionFont' /></application></styles>"
    
	'Finally, close <chart> element
    strXML = strXML &  "</chart>"


    'Set Proper output content-type
    Call Response.AddHeader("Content-type", "text/xml")

	' Write BOM
	Response.CodePage = 65001
	Response.BinaryWrite(chrb(239))
	Response.BinaryWrite(chrb(187))
	Response.BinaryWrite(chrb(191)) 
	
    'Just write out the XML data
    Response.Write strXML
	
%>