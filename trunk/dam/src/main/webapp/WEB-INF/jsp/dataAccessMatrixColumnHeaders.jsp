<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.Header" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade" %><%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle" %>
<%--
Author: David Nassau
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    DAMFacade facade = (DAMFacade) request.getAttribute(DAMFacade.FACADE_KEY_NAME);

    String scat = request.getParameterValues("category")[0];
    Header.HeaderCategory category = null;
    String labelType = "label";
    if (scat.equals("platform")) {
        category = Header.HeaderCategory.PlatformType;
        labelType = "platform";
    } else if (scat.equals("center")) {
        category = Header.HeaderCategory.Center;
        labelType = "center";
    } else if (scat.equals("level")) {
        category = Header.HeaderCategory.Level;
        labelType = "level";
    }
    boolean isLevelHeader = (category == Header.HeaderCategory.Level);

    for (int iCol = 0, max = facade.getColumnCount(category); iCol < max; iCol++) {
        String colId = facade.getColumnHeaderId(category, iCol);
        String headerText = DataAccessMatrixJSPUtil.lookupHeaderText(category, facade.getHeaderName(colId), getServletConfig().getServletContext());
        String imageName = null, cssclass;
        String headerTextToDisplay;
        String baseName;

        if (!isLevelHeader) { //uses image for vertical text
            baseName = DataAccessMatrixJSPUtil.removePunctuation(headerText.toLowerCase());
            imageName = baseName + (facade.isHeaderSelected(colId) ? "_s" : "") + ".gif";
            headerTextToDisplay = DAMResourceBundle.getMessage(labelType + "." + baseName, headerText);
            cssclass = (facade.isHeaderSelected(colId) ? "headerCell selected" : "headerCell vertical");
        } else { //level: just displays number as text
            if (headerText == null || headerText.equals(DataAccessMatrixQueries.LEVEL_CLINICAL )) {
                headerTextToDisplay =  "&nbsp;";
            }else {
                headerTextToDisplay = DAMResourceBundle.getMessage(labelType + "." + headerText, headerText);                  
            }

            cssclass = "headerCell numbers";
            if (facade.isHeaderProtected(colId)) {
                cssclass += "_protected";
                //? "headerCell numbers_selected" : "headerCell numbers");
            }
            if (facade.isHeaderSelected(colId)) {
                cssclass += "_selected";
            }
        }
%>
<td id="<%=colId%>"
    colspan="<%=facade.getColumnHeaderColSpan(colId)%>"
    valign="top"
    class="<%=cssclass%>"
    onclick="toggleHeader(event, this)">
    <%
        if ( !isLevelHeader) {  //display vertical-text image
    %>
    <img src="http://tcga-data.nci.nih.gov/web/images/matrix/headers/<%=imageName%>" title="<%=headerTextToDisplay%>" alt="<%=headerTextToDisplay%>" />
    <%
        } else {  //levels: just a number

    %>
    <div><%=headerTextToDisplay%></div>
    <%
        }
    %>
</td>
<%
    }
%>

