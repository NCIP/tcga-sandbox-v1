Imports Microsoft.VisualBasic
Imports System
Imports System.Text
Imports System.Collections
Imports System.Web.UI.WebControls
Imports System.Web
Imports System.Collections.Generic
Imports System.Globalization

Namespace InfosoftGlobal

    ''' <summary>
    ''' @version: v3.2.2.1 
    ''' @date: 23 November 2011
    ''' </summary>
    ''' <remarks></remarks>
    Public Class FusionCharts
        Private Shared __CONFIG__ As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
        Private Shared __CONFIG__Initialized As Boolean = False

#Region "RenderALL methods"
        ''' <summary>
        ''' Generate html code for rendering chart
        ''' This function assumes that you've already included the FusionCharts JavaScript class in your page
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <param name="bgColor">Back Ground Color</param>
        ''' <param name="scaleMode">Set Scale Mode</param>
        ''' <param name="language">Set SWF file Language</param>
        ''' <returns>JavaScript + HTML code required to embed a chart</returns>
        Private Shared Function RenderChartALL(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean, ByVal bgColor As String, ByVal scaleMode As String, ByVal language As String) As String

            __INIT()

            ' Creating a local copy of global Configuration.
            Dim __CONFIGCLONE__ As Hashtable = __CONFIG__.Clone()

            ' String dataprovider_js_code
            SetConfiguration(__CONFIGCLONE__, "debugMode", boolToNum(debugMode))
            SetConfiguration(__CONFIGCLONE__, "registerWithJS", boolToNum(True))
            ' Setup debug mode js parameter
            Dim debugMode_js_param As Integer = boolToNum(debugMode)
            ' Setup register with js js parameter
            Dim registerWithJS_js_param As Integer = boolToNum(True)
            Dim dataFormat As String = GetConfiguration(__CONFIGCLONE__, "dataFormat")
            dataFormat = IIf(dataFormat = "", "xml" & IIf(dataStr = "", "url", ""), dataFormat & IIf(dataStr = "", "url", ""))

            If (GetConfiguration(__CONFIGCLONE__, "renderAt") = "") Then SetConfiguration(__CONFIGCLONE__, "renderAt", chartId + "Div")

            Dim wmode As String = GetConfiguration(__CONFIGCLONE__, "wMode")
            'If (wmode.Trim() = "") Or (wmode = DBNull.Value) Then
            If (wmode.Trim() = "") Then
                wmode = IIf(allowTransparent = True, "transparent", "opaque")
            End If

            SetConfiguration(__CONFIGCLONE__, "swfUrl", chartSWF)
            SetConfiguration(__CONFIGCLONE__, "dataFormat", dataFormat)
            SetConfiguration(__CONFIGCLONE__, "id", chartId)
            SetConfiguration(__CONFIGCLONE__, "width", chartWidth)
            SetConfiguration(__CONFIGCLONE__, "height", chartHeight)
            SetConfiguration(__CONFIGCLONE__, "wMode", wmode)
            SetConfiguration(__CONFIGCLONE__, "bgColor", bgColor)
            SetConfiguration(__CONFIGCLONE__, "scaleMode", scaleMode)
            SetConfiguration(__CONFIGCLONE__, "lang", language)

            Dim dataSource As String = IIf(dataStr = "", dataUrl, dataStr.Replace(vbCrLf, ""))
            Dim dataSourceJSON As String = """dataSource"" : " & IIf(dataFormat = "json", dataSource, """" + dataSource + """")
            Dim chartConfigJSON As String = "{" + FC_EncodeJSON(GetConfigurationGroup(__CONFIGCLONE__, "params"), False) + "," + dataSourceJSON + "}"

            Dim builder As StringBuilder = New StringBuilder()
            builder.Append("<!-- Using ASP.NET VB FusionCharts v3.2.2.1 Wrapper and JavaScript rendering --><!-- START Script Block for Chart " & chartId & " -->" & Environment.NewLine)
            builder.Append("<div id='" & chartId & "Div' >" & Environment.NewLine)
            builder.Append("Chart." + Environment.NewLine)
            builder.Append("</div>" + Environment.NewLine)
            builder.Append("<script type=""text/javascript"">" + Environment.NewLine)
            builder.Append("if (FusionCharts && FusionCharts('" & chartId & "') ) FusionCharts('" & chartId & "').dispose();" + Environment.NewLine)
            builder.Append("var chart_" & chartId & " = new FusionCharts(" & chartConfigJSON & ").render()")
            builder.Append("</script>" + Environment.NewLine)
            builder.Append("<!-- END Script Block for Chart " & chartId & " -->" + Environment.NewLine)

            ' Re-Initializing...
            __fc__initialize__()

            __CONFIGCLONE__ = Nothing

            Return builder.ToString()
        End Function
        ''' <summary>
        ''' Renders the HTML code for the chart. This
        ''' method does NOT embed the chart using JavaScript class. Instead, it uses
        ''' direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
        ''' see the "Click to activate..." message on the chart.
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <param name="bgColor">Back Ground Color</param>
        ''' <param name="scaleMode">Set Scale Mode</param>
        ''' <param name="language">Set SWF file Language</param>
        ''' <returns></returns>
        Private Shared Function RenderChartHTMLALL(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean, ByVal bgColor As String, ByVal scaleMode As String, ByVal language As String) As String
            __INIT()
            ' Creating a local copy of global Configuration.
            Dim __CONFIGCLONE__ As Hashtable = __CONFIG__.Clone()

            Dim wmode As String = GetConfiguration(__CONFIGCLONE__, "wMode")
            'If (wmode.Trim() = "") Or (wmode = null) Then
            If (wmode.Trim() = "") Then
                wmode = IIf(allowTransparent = True, "transparent", "opaque")
            End If

            SetConfiguration(__CONFIGCLONE__, "data", chartSWF)
            SetConfiguration(__CONFIGCLONE__, "movie", chartSWF)
            SetConfiguration(__CONFIGCLONE__, "dataXML", dataStr)
            SetConfiguration(__CONFIGCLONE__, "dataURL", dataUrl)
            SetConfiguration(__CONFIGCLONE__, "width", chartWidth)
            SetConfiguration(__CONFIGCLONE__, "height", chartHeight)
            SetConfiguration(__CONFIGCLONE__, "chartWidth", chartWidth)
            SetConfiguration(__CONFIGCLONE__, "chartHeight", chartHeight)
            SetConfiguration(__CONFIGCLONE__, "DOMId", chartId)
            SetConfiguration(__CONFIGCLONE__, "id", chartId)
            SetConfiguration(__CONFIGCLONE__, "debugMode", boolToNum(debugMode))
            SetConfiguration(__CONFIGCLONE__, "wMode", wmode)
            SetConfiguration(__CONFIGCLONE__, "bgColor", bgColor)
            SetConfiguration(__CONFIGCLONE__, "scaleMode", scaleMode)
            SetConfiguration(__CONFIGCLONE__, "lang", language)

            Dim strFlashVars As String = FC_Transform(GetConfigurationGroup(__CONFIGCLONE__, "fvars"), "&{key}={value}", True)
            SetConfiguration(__CONFIGCLONE__, "flashvars", strFlashVars)

            Dim strObjectNode As String = "<object " + FC_Transform(GetConfigurationGroup(__CONFIGCLONE__, "object"), " {key}=""{value}""", True) + " >" + Environment.NewLine
            Dim strObjectParamsNode As String = FC_Transform(GetConfigurationGroup(__CONFIGCLONE__, "objparams"), vbTab + "<param name=""{key}"" value=""{value}"">" + Environment.NewLine, True)


            Dim htmlcodes As StringBuilder = New StringBuilder()
            htmlcodes.Append("<!-- Using ASP.NET VB FusionCharts v3.2.2.1 Wrapper --><!-- START Code Block for Chart " + chartId + " -->" + Environment.NewLine)
            htmlcodes.Append(strObjectNode + Environment.NewLine)
            htmlcodes.Append(strObjectParamsNode + Environment.NewLine)
            htmlcodes.Append("</object>" + Environment.NewLine + "<!-- END Code Block for Chart " + chartId + " -->" + Environment.NewLine)

            ' Re-Initializing...
            __fc__initialize__()

            __CONFIGCLONE__ = Nothing

            Return htmlcodes.ToString()
        End Function
#End Region

        ''' <summary>
        ''' Generate html code for rendering chart
        ''' This function assumes that you've already included the FusionCharts JavaScript class in your page
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <returns>JavaScript + HTML code required to embed a chart</returns>
        Public Shared Function RenderChart(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean) As String
            Return RenderChartALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, False, "", "noScale", "EN")
        End Function

        ''' <summary>
        ''' Generate html code for rendering chart
        ''' This function assumes that you've already included the FusionCharts JavaScript class in your page
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <returns>JavaScript + HTML code required to embed a chart</returns>
        Public Shared Function RenderChart(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean) As String
            Return RenderChartALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, allowTransparent, "", "noScale", "EN")
        End Function
        ''' <summary>
        ''' Generate html code for rendering chart
        ''' This function assumes that you've already included the FusionCharts JavaScript class in your page
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <param name="bgColor">Back Ground Color</param>
        ''' <param name="scaleMode">Set Scale Mode</param>
        ''' <param name="language">Set SWF file Language</param>
        ''' <returns>JavaScript + HTML code required to embed a chart</returns>
        Public Shared Function RenderChart(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean, ByVal bgColor As String, ByVal scaleMode As String, ByVal language As String) As String
            Return RenderChartALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, allowTransparent, bgColor, scaleMode, language)
        End Function
        ''' <summary>
        ''' Renders the HTML code for the chart. This
        ''' method does NOT embed the chart using JavaScript class. Instead, it uses
        ''' direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
        ''' see the "Click to activate..." message on the chart.
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <returns></returns>
        Public Shared Function RenderChartHTML(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean) As String
            Return RenderChartHTMLALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, True, False, "", "noScale", "EN")
        End Function
        ''' <summary>
        ''' Renders the HTML code for the chart. This
        ''' method does NOT embed the chart using JavaScript class. Instead, it uses
        ''' direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
        ''' see the "Click to activate..." message on the chart.
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <returns></returns>
        Public Shared Function RenderChartHTML(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean) As String
            Return RenderChartHTMLALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, False, "", "noScale", "EN")
        End Function
        ''' <summary>
        ''' Renders the HTML code for the chart. This
        ''' method does NOT embed the chart using JavaScript class. Instead, it uses
        ''' direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
        ''' see the "Click to activate..." message on the chart.
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <returns></returns>
        Public Shared Function RenderChartHTML(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean) As String
            Return RenderChartHTMLALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, allowTransparent, "", "noScale", "EN")
        End Function
        ''' <summary>
        ''' Renders the HTML code for the chart. This
        ''' method does NOT embed the chart using JavaScript class. Instead, it uses
        ''' direct HTML embedding. So, if you see the charts on IE 6 (or above), you'll
        ''' see the "Click to activate..." message on the chart.
        ''' </summary>
        ''' <param name="chartSWF">SWF File Name (and Path) of the chart which you intend to plot</param>
        ''' <param name="dataUrl">If you intend to use dataURL method for this chart, pass the URL as this parameter. Else, set it to "" (in case of dataXML method)</param>
        ''' <param name="dataStr">If you intend to use dataXML method for this chart, pass the XML data as this parameter. Else, set it to "" (in case of dataURL method)</param>
        ''' <param name="chartId">Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.</param>
        ''' <param name="chartWidth">Intended width for the chart (in pixels)</param>
        ''' <param name="chartHeight">Intended height for the chart (in pixels)</param>
        ''' <param name="debugMode">Whether to start the chart in debug mode</param>
        ''' <param name="registerWithJS">Whether to ask chart to register itself with JavaScript</param>
        ''' <param name="allowTransparent">Whether allowTransparent chart (true / false)</param>
        ''' <param name="bgColor">Back Ground Color</param>
        ''' <param name="scaleMode">Set Scale Mode</param>
        ''' <param name="language">Set SWF file Language</param>
        ''' <returns></returns>
        Public Shared Function RenderChartHTML(ByVal chartSWF As String, ByVal dataUrl As String, ByVal dataStr As String, ByVal chartId As String, ByVal chartWidth As String, ByVal chartHeight As String, ByVal debugMode As Boolean, ByVal registerWithJS As Boolean, ByVal allowTransparent As Boolean, ByVal bgColor As String, ByVal scaleMode As String, ByVal language As String) As String
            Return RenderChartHTMLALL(chartSWF, dataUrl, dataStr, chartId, chartWidth, chartHeight, debugMode, registerWithJS, allowTransparent, bgColor, scaleMode, language)
        End Function

        ''' <summary>
        ''' Encodes the dataURL before it's served to FusionCharts
        ''' If you have parameters in your dataURL, you'll necessarily need to encode it
        ''' </summary>
        ''' <param name="dataURL">dataURL to be fed to chart</param>
        ''' <param name="noCacheStr">Whether to add aditional string to URL to disable caching of data</param>
        ''' <returns>Encoded dataURL, ready to be consumed by FusionCharts</returns>
        Public Shared Function EncodeDataURL(ByVal dataUrl As String, ByVal noCacheStr As Boolean) As String
            Dim result As String = dataUrl
            If (noCacheStr) Then
                result += IIf((dataUrl.IndexOf("?") <> -1), "&", "?")
                ' Replace : in time with _, as FusionCharts cannot handle : in URLs
                result += "FCCurrTime=" + DateTime.Now.ToString().Replace(":", "_")
            End If
            Return System.Web.HttpUtility.UrlEncode(result)
        End Function

        ''' <summary>
        ''' Enables Print Manager for Mozilla browsers
        ''' This function returns a small JavaScript snippet which can be added to ClientScript's RegisterClientScriptBlock method
        ''' </summary>
        ''' <example>ClientScript.RegisterClientScriptBlock(Page.GetType(), "", FusionCharts.enableFCPrintManager());</example>
        ''' <returns>String with the JavaScript code</returns>
        Public Shared Function EnablePrintManager() As String
            Dim strHTML As String = "<script type=""text/javascript""><!--" + Environment.NewLine + " if(FusionCharts && FusionCharts.printManager) FusionCharts.printManager.enabled(true);" + Environment.NewLine + "// --></script>"
            Return strHTML
        End Function

        ''' <summary>
        ''' Enables Print Manager for Mozilla browsers
        ''' </summary>
        ''' <param name="CurrentPage">Current page reference</param>
        Public Shared Sub EnablePrintManager(ByVal CurrentPage As Object)
            Dim HostPage As System.Web.UI.Page
            HostPage = DirectCast(CurrentPage, System.Web.UI.Page)
            Dim strHTML As String = "<script type=""text/javascript""><!--" + Environment.NewLine + " if(FusionCharts && FusionCharts.printManager) FusionCharts.printManager.enabled(true);" + Environment.NewLine + "// --></script>"
            HostPage.ClientScript.RegisterClientScriptBlock(HostPage.GetType(), "", strHTML)
        End Sub

        Private Shared Sub __INIT()
            If (__CONFIG__Initialized = False) Then

                __fc__initialize__()
                __fc__initstatic__()
                __CONFIG__Initialized = True
            End If
        End Sub
        ''' <summary>
        ''' Sets the dataformat to be provided to charts (json/xml)
        ''' </summary>
        ''' <param name="format">Data format. Default is 'xml'. Other format is 'json'</param>
        Public Shared Sub SetDataFormat(ByVal format As String)
            __INIT()

            If (format.Trim().Length = 0) Then

                format = "xml"
            End If
            ' Stores the dataformat in global configuration store
            SetConfiguration("dataFormat", format)
        End Sub

        ''' <summary>
        ''' Sets renderer type (flash/javascript)
        ''' </summary>
        ''' <param name="renderer"> Name of the renderer. Default is 'flash'. Other possibility is 'javascript'</param>
        Public Shared Sub SetRenderer(ByVal renderer As String)
            __INIT()

            If (renderer.Trim().Length = 0) Then
                renderer = "flash"
            End If
            ' stores the renderer name in global configuration store
            SetConfiguration("renderer", renderer)
        End Sub

        ''' <summary>
        ''' Explicitely sets window mode (window[detault]/transpatent/opaque)
        ''' </summary>
        ''' <param name="mode">Name of the mode. Default is 'window'. Other possibilities are 'transparent'/'opaque'</param>
        Public Shared Sub SetWindowMode(ByVal mode As String)
            __INIT()
            SetConfiguration("wMode", mode)
        End Sub

        ''' <summary>
        ''' SetConfiguration sets various configurations of FusionCharts
        ''' It takes configuration names as first parameter and its value a second parameter
        ''' There are config groups which can contain common configuration names. All config names in all groups gets set with this value
        ''' unless group is specified explicitly
        ''' </summary>
        ''' <param name="setting">Name of configuration</param>
        ''' <param name="value">Value of configuration</param>
        Public Shared Sub SetConfiguration(ByVal setting As String, ByVal value As Object)
            Dim de As DictionaryEntry
            For Each de In __CONFIG__
                If (DirectCast(__CONFIG__(de.Key), Hashtable).ContainsKey(setting)) Then
                    DirectCast(__CONFIG__(de.Key), Hashtable)(setting) = value
                End If
            Next
        End Sub

        Private Shared Sub SetConfiguration(ByRef __CONFIGCLONE__ As Hashtable, ByVal setting As String, ByVal value As Object)
            Dim de As DictionaryEntry
            For Each de In __CONFIGCLONE__
                If (DirectCast(__CONFIGCLONE__(de.Key), Hashtable).ContainsKey(setting)) Then
                    DirectCast(__CONFIGCLONE__(de.Key), Hashtable)(setting) = value
                End If
            Next
        End Sub

        Private Shared Function GetHTTP() As String

            'Checks for protocol type.
            Dim isHTTPS As String = HttpContext.Current.Request.ServerVariables("HTTPS")
            'Checks browser type.
            Dim isMSIE As Boolean = HttpContext.Current.Request.ServerVariables("HTTP_USER_AGENT").Contains("MSIE")
            'Protocol initially sets to http.
            Dim sHTTP As String = "http"
            If (isHTTPS.ToLower() = "on") Then
                sHTTP = "https"
            End If
            Return sHTTP

        End Function


        ''' <summary>
        ''' Transform the meaning of boolean value in integer value
        ''' </summary>
        ''' <param name="value">true/false value to be transformed</param>
        ''' <returns>1 if the value is true, 0 if the value is false</returns>
        Private Shared Function boolToNum(ByVal value As Boolean) As Integer
            Return IIf(value = True, 1, 0)
        End Function


        Private Shared Sub SetCONSTANTConfiguration(ByVal setting As String, ByVal value As Object)
            DirectCast(__CONFIG__("constants"), Hashtable)(setting) = value
        End Sub

        Private Shared Function GetConfiguration(ByVal setting As String) As String
            Dim de As System.Collections.DictionaryEntry
            For Each de In __CONFIG__
                If DirectCast(__CONFIG__(de.Key), Hashtable).ContainsKey(setting) Then
                    Return (DirectCast(__CONFIG__(de.Key), Hashtable)(setting).ToString())
                End If
            Next
            Return Nothing
        End Function

        Private Shared Function GetConfiguration(ByRef __CONFIGCLONE__ As Hashtable, ByVal setting As String) As String
            Dim de As System.Collections.DictionaryEntry
            For Each de In __CONFIGCLONE__
                If DirectCast(__CONFIGCLONE__(de.Key), Hashtable).ContainsKey(setting) Then
                    Return (DirectCast(__CONFIGCLONE__(de.Key), Hashtable)(setting).ToString())
                End If
            Next
            Return Nothing
        End Function

        Private Shared Function GetConfigurationGroup(ByVal setting As String) As Hashtable
            If (__CONFIG__.ContainsKey(setting)) Then
                Return DirectCast(__CONFIG__(setting), Hashtable)
            End If
            Return Nothing
        End Function

        Private Shared Function GetConfigurationGroup(ByRef __CONFIGCLONE__ As Hashtable, ByVal setting As String) As Hashtable
            If (__CONFIGCLONE__.ContainsKey(setting)) Then
                Return DirectCast(__CONFIGCLONE__(setting), Hashtable)
            End If
            Return Nothing
        End Function

        Private Shared Function FC_Transform(ByVal arr As System.Collections.Hashtable, ByVal tFormat As String, ByVal ignoreBlankValues As Boolean) As String
            Dim converted As String = ""
            Dim Key As String = ""
            Dim Value As String = ""
            Dim ds As System.Collections.DictionaryEntry

            For Each ds In arr
                If (ignoreBlankValues = True And ds.Value.ToString().Trim() = "") Then Continue For
                Key = ds.Key.ToString()
                Value = ds.Value.ToString()
                If (Key.ToLower().Equals("codebase")) Then
                    Value = Value.Replace("http", GetHTTP())
                End If
                Dim TFApplied As String = tFormat.Replace("{key}", Key)
                TFApplied = TFApplied.Replace("{value}", Value)
                converted = converted & TFApplied
            Next

            Return converted

        End Function

        Private Shared Function FC_EncodeJSON(ByVal json As Hashtable, ByVal enclosed As Boolean) As String
            Dim strjson As String = ""
            If (enclosed = True) Then strjson = "{"
            strjson = strjson & FC_Transform(json, """{key}"" : ""{value}"", ", True)
            strjson = strjson.Trim()

            If (strjson.EndsWith(",")) Then strjson = strjson.Remove(strjson.Length - 1)

            Return strjson
        End Function

        Private Shared Sub __fc__initstatic__()
            Dim constant As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
            constant("scriptbaseUri") = ""
            __CONFIG__("constants") = constant
            constant = Nothing
        End Sub

        Private Shared Sub __fc__initialize__()

            __CONFIG__ = Nothing
            __CONFIG__ = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())

            Dim param As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())

            param("swfUrl") = ""
            param("width") = ""
            param("height") = ""
            param("renderAt") = ""
            param("renderer") = ""
            param("dataSource") = ""
            param("dataFormat") = ""
            param("id") = ""
            param("lang") = ""
            param("debugMode") = ""
            param("registerWithJS") = ""
            param("detectFlashVersion") = ""
            param("autoInstallRedirect") = ""
            param("wMode") = ""
            param("scaleMode") = ""
            param("menu") = ""
            param("bgColor") = ""
            param("quality") = ""

            __CONFIG__("params") = param

            Dim fvar As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
            fvar("dataURL") = ""
            fvar("dataXML") = ""
            fvar("chartWidth") = ""
            fvar("chartHeight") = ""
            fvar("DOMId") = ""
            fvar("registerWithJS") = "1"
            fvar("debugMode") = "0"
            fvar("scaleMode") = "noScale"
            fvar("lang") = "EN"
            fvar("animation") = "undefined"
            __CONFIG__("fvars") = fvar

            Dim obj As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
            obj("height") = ""
            obj("width") = ""
            obj("id") = ""
            obj("lang") = "EN"
            obj("type") = "application/x-shockwave-flash"
            obj("class") = "FusionCharts"
            obj("data") = ""
            __CONFIG__("object") = obj

            Dim objparam As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
            objparam("movie") = ""
            objparam("scaleMode") = "noScale"
            objparam("scale") = ""
            objparam("wMode") = ""
            objparam("allowScriptAccess") = "always"
            objparam("quality") = "best"
            objparam("FlashVars") = ""
            objparam("bgColor") = ""
            objparam("swLiveConnect") = ""
            objparam("base") = ""
            objparam("align") = ""
            objparam("salign") = ""
            objparam("menu") = ""
            __CONFIG__("objparams") = objparam

            Dim embeds As Hashtable = New Hashtable(New CaseInsensitiveHashCodeProvider(), New CaseInsensitiveComparer())
            embeds("height") = ""
            embeds("width") = ""
            embeds("id") = ""
            embeds("src") = ""
            embeds("flashvars") = ""
            embeds("name") = ""
            embeds("scaleMode") = "noScale"
            embeds("wMode") = ""
            embeds("bgColor") = ""
            embeds("quality") = "best"
            embeds("allowScriptAccess") = "always"
            embeds("type") = "application/x-shockwave-flash"
            embeds("pluginspage") = "http://www.macromedia.com/go/getflashplayer"
            embeds("swLiveConnect") = ""
            embeds("base") = ""
            embeds("align") = ""
            embeds("salign") = ""
            embeds("scale") = ""
            embeds("menu") = ""
            __CONFIG__("embed") = embeds


            param = Nothing
            fvar = Nothing
            obj = Nothing
            objparam = Nothing
            embeds = Nothing

        End Sub
    End Class
End Namespace


