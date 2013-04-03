<%
'Page: FusionCharts.asp
'Author: InfoSoft Global (P) Ltd.

'This page contains functions that can be used to render FusionCharts.
%>
<%
' Associative Array Class.
	Class AssociativeArray
		Private oDIC

		Private Sub Class_Initialize()
			Set oDIC=Server.CreateObject("Scripting.Dictionary")
		End Sub

		Private Sub Class_Terminate()
			Set oDIC=Nothing
		End Sub

		Public Default Property Get Item(sKey)
			If Not oDIC.Exists(sKey) Then
				oDIC.Add sKey,New AssociativeArray
			End If

			If IsObject(oDIC.Item(sKey)) Then
				Set Item=oDIC.Item(sKey)
			Else
				Item=oDIC.Item(sKey)
			End If
		End Property

		Public Property Let Item(sKey,sValue)
			If oDIC.Exists(sKey) Then
				If IsObject(sValue) Then
					Set oDIC.Item(sKey)=sValue
				Else
					oDIC.Item(sKey)=sValue
				End If
			Else
				oDIC.Add sKey,sValue
			End If
		End Property
		
		Public Property Get Exists(sKey)
			Exists = oDIC.Exists(sKey)
		End Property

		Public Property Get Keys()
			Keys = oDIC.Keys
		End Property
		
	End Class
%>

<%

Dim FC__SETTINGS
Set FC__SETTINGS = New AssociativeArray
Call FC_INITIALIZE()
Call FC_INITSTATIC()

' * Initializes FusionCharts Static configurations
' *
' * Prepares the wrapper to load default chart configurations
' *
Function FC_INITSTATIC()
	FC__SETTINGS("constants")( "scriptBaseUri" ) = "" 
	FC__SETTINGS("params")( "wmode" ) = "opaque"
	FC__SETTINGS("embed")( "wmode" ) = "opaque" 
	FC__SETTINGS("objparams")( "wmode" ) = "opaque" 
	
End Function

Function FC_INITIALIZE	()
	'Set FC__SETTINGS = Nothing
	FC__SETTINGS("params")( "swfUrl" ) = "" 
	FC__SETTINGS("params")( "width" ) = "" 
	FC__SETTINGS("params")( "height" ) =  "" 
	FC__SETTINGS("params")( "renderAt" ) = "" 
	FC__SETTINGS("params")( "renderer" ) = "" 
	FC__SETTINGS("params")( "dataSource" ) = "" 
	FC__SETTINGS("params")( "dataFormat" ) = "" 
	FC__SETTINGS("params")( "id" ) = "" 
	FC__SETTINGS("params")( "lang" ) = "" 
	FC__SETTINGS("params")( "debugMode" ) = ""  
	FC__SETTINGS("params")( "registerWithJS" ) = ""  
	FC__SETTINGS("params")( "detectFlashVersion" ) = "" 
	FC__SETTINGS("params")( "autoInstallRedirect" ) = "" 
	FC__SETTINGS("params")( "scaleMode" ) = ""  
	FC__SETTINGS("params")( "menu" ) = "" 
	FC__SETTINGS("params")( "bgColor" ) = "" 
	FC__SETTINGS("params")( "quality" ) = "" 


	FC__SETTINGS("fvars")( "dataURL" ) = "" 
	FC__SETTINGS("fvars")( "dataXML" ) = "" 
	FC__SETTINGS("fvars")( "chartWidth" ) = "" 
	FC__SETTINGS("fvars")( "chartHeight" ) = "" 
	FC__SETTINGS("fvars")( "DOMId" ) = "" 
	FC__SETTINGS("fvars")( "registerWithJS" ) = "1" 
	FC__SETTINGS("fvars")( "debugMode" ) = "0" 
	FC__SETTINGS("fvars")( "scaleMode" ) = "noScale" 
	FC__SETTINGS("fvars")( "lang" ) = "EN" 
	
	FC__SETTINGS("object")( "height" ) = "" 
	FC__SETTINGS("object")( "width" ) = "" 
	FC__SETTINGS("object")( "id" ) = "" 
	FC__SETTINGS("object")( "classid" ) = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" 
	FC__SETTINGS("object")( "codebase" ) = "http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" 
	
	FC__SETTINGS("objparams")( "movie" ) = "" 
	FC__SETTINGS("objparams")( "FlashVars" ) = "" 
	FC__SETTINGS("objparams")( "scaleMode" ) = "noScale" 
	FC__SETTINGS("objparams")( "bgColor" ) = "" 
	FC__SETTINGS("objparams")( "quality" ) = "best" 
	FC__SETTINGS("objparams")("allowScriptAccess") = "always" 
	FC__SETTINGS("objparams")( "swLiveConnect" ) = "" 
	FC__SETTINGS("objparams")( "base" ) = "" 
	FC__SETTINGS("objparams")( "align" ) = "" 
	FC__SETTINGS("objparams")( "salign" ) = "" 
	FC__SETTINGS("objparams")( "scale" ) = "" 
	FC__SETTINGS("objparams")( "menu" ) = "" 


	FC__SETTINGS("embed")( "height" ) = "" 
	FC__SETTINGS("embed")( "width" ) = "" 
	FC__SETTINGS("embed")( "id" ) = "" 
	FC__SETTINGS("embed")( "src" ) = "" 
	FC__SETTINGS("embed")( "flashvars" ) = "" 
	FC__SETTINGS("embed")( "name" ) = "" 
	FC__SETTINGS("embed")( "scaleMode" ) = "noScale" 
	FC__SETTINGS("embed")( "bgColor" ) = "" 
	FC__SETTINGS("embed")( "quality" ) = "best" 
	FC__SETTINGS("embed")("allowScriptAccess") = "always" 
	FC__SETTINGS("embed")("type") = "application/x-shockwave-flash"
	FC__SETTINGS("embed")("pluginspage")= "http://www.macromedia.com/go/getflashplayer" 
	FC__SETTINGS("embed")( "swLiveConnect" ) = "" 
	FC__SETTINGS("embed")( "base" ) = "" 
	FC__SETTINGS("embed")( "align" ) = "" 
	FC__SETTINGS("embed")( "salign" ) = "" 
	FC__SETTINGS("embed")( "scale" ) = "" 
	FC__SETTINGS("embed")( "menu" ) = "" 
End Function

Function FC_SetConfiguration (name, value, group, addNew)
	
	dim isSet
	dim oObject
	dim oObject1
	
	isSet=False
	' Triming keys and converting to lower case.
	group = LCase(Trim(group))
	If (group <> ""  Or (FC__SETTINGS.Exists(group))) Then
		' Set in global configuration store
		For Each oObject In FC__SETTINGS(group).Keys
			If LCase(oObject)=LCase(name) then
				FC__SETTINGS(group)(oObject)=value
				isSet=True
			End If
		Next
	Else
		For Each oObject In FC__SETTINGS.Keys
			For Each oObject1 In FC__SETTINGS(oObject).Keys
				If LCase(oObject1)=LCase(name) then
					FC__SETTINGS(oObject)(oObject1)=value
					isSet=True
				End If
			Next
		Next
	End If

	If (isSet = False And group <> "" And addNew = True) Then
		FC__SETTINGS(group)(name) = value
		isSet = True
	End If
	
	FC_SetConfiguration = isSet
	
	Set oObject1 = Nothing
	Set oObject = Nothing
	
End Function

Function FC_GetConfiguration(name, group)
	Dim oObject
	Dim oObject1
	
	Dim retVal
	group = LCase(Trim(group))
	name = LCase(Trim(name))

	If ( group = "" ) Then
		' If the configuration is in store
		For Each oObject In FC__SETTINGS.Keys
			If (LCase(name) = LCase(oObject)) Then
				Set FC_GetConfiguration = FC__SETTINGS(oObject)
				Exit Function
			Else
				For Each oObject1 In FC__SETTINGS(oObject).Keys 
					If (LCase(oObject1) = LCase(name)) Then
						FC_GetConfiguration = FC__SETTINGS(oObject)(oObject1)
						Exit Function
					End If
				Next
			End If
		Next 
	Else
		If FC__SETTINGS.Exists(group) = True Then
			For Each oObject In  FC__SETTINGS(group).Keys
				If ( oObject = name ) Then
					FC_GetConfiguration = FC__SETTINGS(group)(oObject) 
					Exit Function
				End If
			Next
		End If
		
	End If
	
	FC_GetConfiguration = ""

	Set oObject = Nothing
	Set oObject1 = Nothing
	
End Function

'**
' * Sets a collection of configurations
' * 
' * @param	objConfig	Array  - An Array of configurations with key as configuration name and values as  configuration value
' */
Function FC_SetConfigurations( objConfig )
	
	Dim oObject
	
	' Iterate through array
	For Each oObject In objConfig
		' Set config
		Call FC_SetConfiguration (oObject, objConfig(oObject), "", True)
	Next
End Function

' * sets the dataformat to be provided to charts (json/xml)
' *
' * @param	format String  - data format. Default is 'xml'. Other format is 'json'
' *
Function FC_SetDataFormat(format)
	If format="" Then format = "xml"
	' Stores the dataformat in global configuration store
	Call FC_SetConfiguration ("dataFormat", format, "", True)
End Function

' * sets renderer type (flash/javascript)
' *
' * @param	renderer String  - Name of the renderer. Default is 'flash'. Other possibility is 'javascript'
' *
Function FC_SetRenderer( renderer )
	If Trim(renderer)="" Then renderer = "flash"
	' Stores the renderer name in global configuration store
	Call FC_SetConfiguration ("renderer", LCase(renderer),"",True)
End Function

' * explicitely sets window mode (window(detault)/transpatent/opaque)
' *
' * @param	mode String  - Name of the mode. Default is 'windoe'. Other possibilities are 'transparent'/'opaque'
' *
Function FC_SetWindowMode( mode)
	If Trim(mode)="" Then mode = "window"
	' Stores the window mode to configuration store
	Call FC_SetConfiguration ( "wmode", mode, "", True )
End Function

' Backward compatibility
Function SetTransparentChart(Transparent)
	If Transparent Then
		Call FC_SetConfiguration("wmode","transparent","",True)
	Else
		Call FC_SetConfiguration("wmode","opaque","",True)
	End If
End Function

' * Enables Print Manager for Mozilla browsers
' * 
' * This function adds a small JavaScript snippet to the page which enables the Managed Print option for Mozilla basec browsers
' * 
' * The parameter directWriteToPage which if set to true would write the code directly to page. Otherwise the 
' * code snippet is returned as string 
' * 
' * @param	directWriteToPage	Boolean  - Whether to write the JavaScript code directly to page or return as string
' *
' * @return	A blank string when the code is directly written to page, otherwize, the JavaScript as string.
Function FC_EnablePrintManager(directWriteToPage)
	Dim strHTML
	strHTML = "<script type=""text/javascript""><!--" & vbCrLf & " if(FusionCharts && FusionCharts.printManager) FusionCharts.printManager.enabled(true);" &  vbCrLf & "// -->" & vbCrLf & "</script>"
	If (directWriteToPage=True) Then
		%><%=strHTML%><%
	Else
		FC_EnablePrintManager = strHTML
	End If
End Function


' * Converts associative array to To JSON String 
' *
' * @param	mask			String - what part of the date to return "m' for month,"d" for day, and "y" for year
' * @param	dateTimeStr String - MySQL date/time format (yyyy-mm-dd HH:ii:ss)
' *
' * @return	converted date
' *
Function fc_encode_json(json, enclosed )

	Dim strjson 
	strjson = ""
	
	If(enclosed) Then strjson = strjson & "{"
	
	strjson = strjson & FC_Transform (json, """{key}"" : ""{value}"", ", True)
	
	' Removes last two (, ) from the string
    If Right(strjson, 2) = ", " Then
        strjson = Left(strjson, Len(strjson) - 2)
    End If	
	
	If(enclosed) Then strjson  = strjson & "}"
	
	fc_encode_json = strjson
	
End Function

' *  Transforms an associaitive array to string
' * 
' *
' * @param	arr			Array - Associative array
' * @param	tFormat 		String - String builder format. The format is a string with placeholder for key and value.
' * 									The function iterated through the array 
' * 									replaces all "{key}" (placeholder for key) in the String with the key name of the array element
' * 									replaces all "{value}"  (placeholder for value) in the String with the value associated with the above key
' *
' * @param	ignoreBlankValues		Boolean - If true it igonores all elements with blank values (Default: True)
' *
' * @return	converted date
' *
Function FC_Transform(arr, tFormat, ignoreBlankValues) 
	Dim converted
	Dim oObject
	Dim TFApplied
	
	converted = "" 
	
	For Each oObject In arr.Keys 
		If(ignoreBlankValues = True And arr(oObject) <> "" ) Then
			TFApplied = Replace(tFormat,"{key}", oObject)
			TFApplied = Replace(TFApplied, "{value}", arr(oObject))
			converted = converted & TFApplied
		End If
	Next
	
	FC_Transform =  converted
	Set oObject = Nothing
	
End Function

' *  encodeDataURL function encodes the dataURL before it's served to FusionCharts.
' *  If you've parameters in your dataURL, you necessarily need to encode it.
' *  Param: strDataURL - dataURL to be fed to chart
' *  Param: addNoCacheStr - Whether to add aditional string to URL to disable caching of data
Function encodeDataURL(strDataURL, addNoCacheStr)
	'Add the no-cache string if required
	if addNoCacheStr=true then
		'We add ?FCCurrTime=xxyyzz
		'If the dataURL already contains a ?, we add &FCCurrTime=xxyyzz
		'We replace : with _, as FusionCharts cannot handle : in URLs
		if Instr(strDataURL,"?")<>0 then
			strDataURL = strDataURL & "&FCCurrTime=" & Replace(Now(),":","_")
		else
			strDataURL = strDataURL & "?FCCurrTime=" & Replace(Now(),":","_")
		end if
	end if	
	'URL Encode it
	encodeDataURL = Server.URLEncode(strDataURL)
End Function

' *  renderChart renders the JavaScript + HTML code required to embed a chart.
' *  This function assumes that you've already included the FusionCharts JavaScript class
' *  in your page.

' *   chartSWF - SWF File Name (and Path) of the chart which you intend to plot
' *   strURL - If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)
' *   strXML - If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)
' *   chartId - Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.
' *   chartWidth - Intended width for the chart (in pixels)
' *   chartHeight - Intended height for the chart (in pixels)
' *   debugMode - Whether to start the chart in debug mode
' *   registerWithJS - Whether to ask chart to register itself with JavaScript
Function renderChart(chartSWF, strURL, strXML, chartId, chartWidth, chartHeight, debugMode, registerWithJS)
	'First we create a new DIV for each chart. We specify the name of DIV as "chartId"Div.			
	'DIV names are case-sensitive.
	Dim dataFormat
	Dim wmode
	
	dataFormat = FC_GetConfiguration ("dataFormat","")
	
	If Trim(dataFormat) = "" Then dataFormat="xml"
	
	If Trim(strXML) = "" Then dataFormat = dataFormat & "url"
	
	If (FC_GetConfiguration ("renderAt", "")="") Then
		Call FC_SetConfiguration("renderAt", chartId & "Div","",True )
	End If

	Call FC_SetConfiguration("swfUrl" , chartSWF, "", True)
	Call FC_SetConfiguration("dataFormat" , dataFormat, "", True)
	Call FC_SetConfiguration("id", chartId, "", True) 
	Call FC_SetConfiguration("width", chartWidth, "", True) 
	Call FC_SetConfiguration("height", chartHeight, "", True)
	
	If (debugMode) Then Call FC_SetConfiguration("debugMode", boolToNum(debugMode), "", True )
	
	wmode = FC_GetConfiguration("wmode", "") 
	
	If ( wmode = "" ) Then 
		wmode = "opaque"
	End If
	
	Dim dataSource
	Dim datasource_json
	Dim chart_config_json
	
	If Trim(strXML) = "" Then
		dataSource = strURL
	Else
		dataSource = strXML
	End If
	dataSource = Replace(dataSource, vbCrLF , "")
	
	If LCase(dataFormat) = "json" Then
		datasource_json = """dataSource"" : " & dataSource
	Else
		datasource_json = """dataSource"" : " & """" & dataSource & """" 
	End If
	
	chart_config_json = "{ " & fc_encode_json( FC_GetConfiguration("params","" ), False ) & ", " & datasource_json & " }"
	
%>
	<!-- START Script Block for Chart <%=chartId%> -->
	<div id='<%=chartId%>Div' align='center' style='width:<%=FC_GetConfiguration("width", "params")%>;height:<%=FC_GetConfiguration("height", "params")%>;'>
		Chart.
		<%
		'The above text "Chart" is shown to users before the chart has started loading
		'(if there is a lag in relaying SWF from server). This text is also shown to users
		'who do not have Flash Player installed. You can configure it as per your needs.
		%>
	</div>
		<%
		'Now, we render the chart using FusionCharts Class. Each chart's instance (JavaScript) Id 
		'is named as chart_"chartId".		
		%>
	<script type="text/javascript"><!--	
		//Instantiate the Chart	
		if ( FusionCharts("<%=chartId%>") && FusionCharts("<%=chartId%>").dispose ) FusionCharts("<%=chartId%>").dispose();
		var chart_<%=chartId%> = new FusionCharts( <%=chart_config_json%> ).render();
		// --></script>	
	<!-- END Script Block for Chart <%=chartId%> -->
	<%
	Call FC_INITIALIZE()
End Function

' *  renderChartHTML function renders the HTML code for the JavaScript. This
' *  method does NOT embed the chart using JavaScript class. Instead, it uses
' *  direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
' *  see the "Click to activate..." message on the chart.
' *   chartSWF - SWF File Name (and Path) of the chart which you intend to plot
' *   strURL - If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)
' *   strXML - If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)
' *   chartId - Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.
' *   chartWidth - Intended width for the chart (in pixels)
' *   chartHeight - Intended height for the chart (in pixels)
' *   debugMode - Whether to start the chart in debug mode
Function renderChartHTML(chartSWF, strURL, strXML, chartId, chartWidth, chartHeight, debugMode)
	'Generate the FlashVars string based on whether dataURL has been provided
	'or dataXML.
	Dim wmode
	Dim strFlashVars, strObjectNode, strObjectParamsNode, strEmbedNode
	
	wmode = FC_GetConfiguration("wmode" , "")
	If ( wmode = "" ) Then 
		wmode = "opaque"
	End If
	
	Call FC_SetConfiguration("movie",chartSWF, "", True)
	Call FC_SetConfiguration("src",chartSWF, "", True)
	Call FC_SetConfiguration("dataXML",strXML, "", True)
	Call FC_SetConfiguration("dataURL",strURL, "", True)
	Call FC_SetConfiguration("width",chartWidth, "", True)
	Call FC_SetConfiguration("height",chartHeight, "", True)
	Call FC_SetConfiguration("chartWidth",chartWidth, "", True)
	Call FC_SetConfiguration("chartHeight",chartHeight, "", True)
	Call FC_SetConfiguration("DOMId",chartId, "", True)
	Call FC_SetConfiguration("id",chartId, "", True)
	Call FC_SetConfiguration("debugMode",boolToNum(debugMode), "", True)
	Call FC_SetConfiguration("registerWithJS",boolToNum(True), "", True)
	
	' Generate the FlashVars string based on whether dataUrl has been provided
	' or dataXML.
	strFlashVars = FC_Transform(FC_GetConfiguration("fvars", ""), "&{key}={value}", True)
	Call FC_SetConfiguration ("flashvars", strFlashVars, "", True )
	
	strObjectNode = "<object " & FC_Transform(FC_GetConfiguration("object", ""), " {key}=""{value}""", True) & " >" & vbCrLf
	strObjectParamsNode = FC_Transform(FC_GetConfiguration("objparams", ""), vbTab & "<param name=""{key}"" value=""{value}"">" & vbCrLf, True)
	strEmbedNode = vbTab & "<embed " & FC_Transform(FC_GetConfiguration("embed", ""), " {key}=""{value}""", True) & " />" 
	%>
<!-- START Code Block for Chart <%=chartId%> -->
<%=strObjectNode%>
	<%=strObjectParamsNode%>
	<%=strEmbedNode%>
</object>
<!-- END Code Block for Chart <%=chartId%> -->
	<%
	Call FC_INITIALIZE()
End Function

' *  boolToNum function converts boolean values to numeric (1/0)
Function boolToNum(bVal)
	Dim intNum
	if bVal=true then
		intNum = 1
	else
		intNum = 0
	end if
	boolToNum = intNum
End Function
%>