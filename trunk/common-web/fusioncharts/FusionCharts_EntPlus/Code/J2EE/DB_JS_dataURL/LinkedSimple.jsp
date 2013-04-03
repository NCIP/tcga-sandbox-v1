<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.LinkedData" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts - Database and Linked Chart Example"
	scope="request" />
<c:set var="header1"
	value="JSP Linked Charts Example" scope="request" />
<c:set var="header2"
	value="Click on any pie slice to open linked chart" scope="request" />
<c:set var="jsPath" value="${folderPath}" scope="request" />
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<tags:template2>

	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoriesBean.chartId}"
		swfFilename="${folderPath}${factoriesBean.filename}"
		width="${factoriesBean.width}" height="${factoriesBean.height}"
		debugMode="false" xmlData="${factoriesBean.xml}" registerWithJS="true"/>
 <br>
                        <center>
                            <!-- linked chart container -->
                            <div id="linked-chart" style="width:600px; height:250px; border:1px solid #999;">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" height="250" >
                                    <tr>
                                        <td align="center" valign="middle">Click on a pie slice above to see the linked chart appear here</td>
                                    </tr>
                                </table>
                            </div>
                        </center>

                        <script type="text/javascript">

                            // Configure linked chart
                            // set chart type to column2D, render the linked chart to another container
                            // configute the overlay button
                            FusionCharts("FactorySum").configureLink (
                            {
                                swfUrl	: "../../FusionCharts/Column2D.swf",
                                renderAt : "linked-chart",
                                width 	: "100%" ,
                                height	: "100%" ,
                                debugMode	: "0" ,
                                overlayButton: {
                                    show : true,
                                    message: ' x ',
                                    bgColor:'E4E7D9',
                                    borderColor: 'B7BBA9'
                                }
                            }, 0);

                            // store the content of the target div
                            var store =document.getElementById("linked-chart").innerHTML;

                            // When linked chart is closed revert back to the target div's original content
                            // This is achieve by setting event-listener to LinkedItemClosed event
                            FusionCharts("FactorySum").addEventListener(FusionChartsEvents.LinkedItemClosed, function() { document.getElementById("linked-chart").innerHTML = store;  } );

                        </script>

</tags:template2>