<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>

<%--
    Author: David Nassau
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<tr>
    <%
	    String currentSelectedCells = "," + request.getParameterValues("currentSelectedCells")[0] + ",";
	    String currentSelectedHeaders = "," + request.getParameterValues("currentSelectedHeaders")[0] + ",";

	    List<String> levelIds = (List<String>) getServletConfig().getServletContext().getAttribute("levelHeaderIds");
        Pattern patternForLevelIds = Pattern.compile("((header_p\\d+_)c\\d+_)l\\d+_column\\d+_");

	    
        DAMFacadeI facadeI = (DAMFacadeI)request.getAttribute(DAMFacade.FACADE_KEY_NAME);
        
        //if user is setting to new color scheme
        String currentCSName = request.getParameter("currentCSName");
        facadeI.setColorSchemeName(currentCSName);                        
        
        String sbatch = request.getParameterValues("batchno")[0];
        int batchNo = Integer.parseInt(sbatch);
        String batchId = facadeI.getBatchHeaderId(batchNo);
        int max = facadeI.getChildHeaderCount(batchId);
        int selectedRowCount = 0;
        String header1CurrentID;
        String header2CurrentID = "";
        String cellCurrentID;
        String levelID = "";
        String tempCellJsString = "";   
        String tempSampleJsString = "";        
        String finalJsString = "";
        

        header1CurrentID = "header_batch" + sbatch + "_";
        int numberOfColumns = facadeI.getTotalColumns();
        
        String[] levelHeaders = new String[numberOfColumns];
        
        //Balancing pageload speed and interaction speed:
        //On pageload, dom location of these cells in these columns will will be recorded
        //otherwise location is recorded at first interaction. Negative if unused.
        int ieComfortUB = 24;
        int ieComfortDB = 18;
    %>
    <th id="header_batch<%=sbatch%>_"
        onclick="toggleHeader(event, this, null)"
        rowspan="<%=facadeI.getBatchHeaderRowSpan(batchId)%>"
        class="headerCell left_header1"><%=facadeI.getHeaderName( batchId )%>
    </th>
    <%
        for (int iSample=0; iSample<max; iSample++) {
            String sampleId = facadeI.getChildHeaderId(batchId, iSample);
        	header2CurrentID = header1CurrentID + "sample" + iSample + "_";
        	selectedRowCount = 0;
            if (iSample > 0) {
    %>
<tr>
    <%
        }
    %>
    <th id="<%=header2CurrentID%>" onclick="toggleHeader(event,this, null)"
		class= "headerCell left_header2"><%=facadeI.getHeaderName( sampleId )%>
    </th>
    <%
        tempCellJsString = "";

        for(int iCell = 0, max2 = facadeI.getTotalColumns(); iCell < max2; iCell++) {
            String cellId = facadeI.getChildCellId( sampleId, iCell );
            String availability = facadeI.getCellAvailability( cellId );
            String cellclass = "cell";
                cellCurrentID = header2CurrentID + "column" + iCell + "_cellID_" + cellId;

            String levelHeaderId = levelIds.get(iCell);
            Matcher matcher = patternForLevelIds.matcher(levelHeaderId);
            String platformHeaderId = "";
            String centerHeaderId = "";
            if (matcher.matches()) {
                platformHeaderId = matcher.group(1);
                centerHeaderId = matcher.group(2);
            }

            if(currentSelectedCells.indexOf(","+cellId+",", 0) > -1) {
                cellclass = "cellselected";
                selectedRowCount++;
            } else if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
                cellclass = "cell selectable";
            }
            if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE )) {
                availability = "&nbsp;";
            }
            String[] colorcode = facadeI.getCellColorAndLetter( cellId );

    %>
    <td id="<%=cellCurrentID%>" headers="<%=header1CurrentID%> <%=header2CurrentID%> <%=platformHeaderId%> <%=centerHeaderId%> <%=levelHeaderId%>"
    <%
        if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
                %> onclick="toggleCell(this)"<%
                
                if( iCell == 0 || (iCell > ieComfortDB && iCell < ieComfortUB) ){
	                if( batchNo == 0 && iSample == 0 ){
	                	levelHeaders[iCell] = "document.getElementById(\"" + cellCurrentID + "\")";
	                } else {
	                	levelHeaders[iCell] += "document.getElementById(\"" + cellCurrentID + "\")";
	                }
	            } else {
                if( batchNo == 0 && iSample == 0 ){
	                levelHeaders[iCell] = cellCurrentID;
	            } else {
	                levelHeaders[iCell] += cellCurrentID;
	            }
	            }
	            
                tempCellJsString +=  cellCurrentID;
                
                if (iCell + 1 < max2) {
                    tempCellJsString += ",";
                }
        }
    %>
    class="<%=cellclass%>"><div class="<%=colorcode[0].replace("#","cellBGClass")%>"><%=colorcode[1] != null ? colorcode[1] : "" %></div></td>

    <%
        }
    %>
</tr>
<%
        tempSampleJsString +=  header2CurrentID;
        if (iSample + 1 < max) {
        	tempSampleJsString += ",";
        }
        finalJsString += "cellHeaders['" + header2CurrentID + "']='" + tempCellJsString.replaceAll(",$", "") + "';selectedCellInRows['" + header2CurrentID + "'] = " + selectedRowCount + ";";
    }
    finalJsString += "cellHeaders['" + header1CurrentID + "']='" + tempSampleJsString.replaceAll(",$", "") + "';";
%>
<script type="text/javascript">
        <%=finalJsString%>
<%
                        //append to headers javascript array after adding in commas and removing nulls levelHeader array had not set values
            for(int columnNum=0;columnNum<numberOfColumns;columnNum++){
                if( batchNo == 0 ){ %>
                        levelHeaders[<%=columnNum%>] = new Array();
                
<%				}
                if(levelHeaders[columnNum] != null && levelHeaders[columnNum].length() > 0 &&  (columnNum == 0 || (columnNum > ieComfortDB && columnNum < ieComfortUB)) ){
                %>
        levelHeaders[<%=columnNum%>].push(<%=(levelHeaders[columnNum] + "").replace("document",",document").replace("null","").substring(1)%>);

        <%      } else if(levelHeaders[columnNum] != null && levelHeaders[columnNum].length() > 0){ %>
        cellHeaders["<%=columnNum%>"] += '<%=(levelHeaders[columnNum] + "").replace("header",",header").replace("null","").substring(1) + ","%>';
        <%      } 
            }
        %>
                batchArray.push(['header_batch<%=sbatch%>_',<%=max%>]);
</script>