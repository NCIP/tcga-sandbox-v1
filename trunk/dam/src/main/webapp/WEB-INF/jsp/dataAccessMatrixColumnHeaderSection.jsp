<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.Header" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade" %>
<%@ page import="gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.Integer" %>
<%
    DAMFacade facade;
    String scat;
    String labelType;
    Header.HeaderCategory category;
    String colId;
    String headerText;
    String imageName;
    String cssclass;
    String headerTextToDisplay;
    String baseName;
    boolean isLevelHeader;
	String levelID = "";	
    String headerImgFolder = "/tcga/images/matrix/headers/2012-02";
	String currentSelectedHeaders = "," + request.getParameterValues("currentSelectedHeaders")[0] + ",";
	
	//trackers bellow keep track of the "table-design" tree relationship of the headers and cells
	//so that the javascript and css knows how to behave given a user interaction with a given cell.
	
	//keeps track of children per parent... example platformChildren(0)  might give you 3 children i.e 3 centers
	List platformChildren = new ArrayList();
	List centerChildren = new ArrayList();

	int platformx; //counter: keeps track of platform p of a column header as it relates to grid
	int centerx; //counter: keeps track of center of a column header as it relates to grid
	int levelx; //counter: keeps track of level of a column header as it relates to grid
	
	int centerParentControl = 0; //counter: keeps track of children per center parent (platform)
    int levelParentControl = 0; //counter: keeps track of children per level parent (center)
    int centerParentsReg = 0; //counter: keeps track of center parents to be able to query number of children they have
    int levelParentsReg = 0; //counter: keeps track of level parents to be able to query number of children they have

    int platformFirstColumn = 0; //keeps track of the first level of a platform   
    int centerFirstColumn = 0; //keeps track of the first level of a center
    String levelJsArray = ""; //makes Js array of level names
    
	String tempStr;

    facade = (DAMFacade) request.getAttribute(DAMFacade.FACADE_KEY_NAME);
    facade.setColorSchemeName(request.getParameter("colorSchemeName"));

    scat = "platform";
    labelType = scat;
    category = Header.HeaderCategory.PlatformType;
    imageName = null;
    cssclass = null;
    tempStr = null;
%>
        					<%@include file="/includes/dataAccessMatrixColumnHeaders.jspf" %>
                        </tr>
                        <tr>
<%   
	//resets 
    scat = "center";
    labelType = scat;
    category = Header.HeaderCategory.Center;
    imageName = null;
    cssclass = null;
    centerParentControl = 0;
    levelParentControl = 0;
	centerParentsReg = 0;
    levelParentsReg = 0;
    tempStr = null;
%>
                            <%@include file="/includes/dataAccessMatrixColumnHeaders.jspf" %>
                        </tr>
                        <tr>
                            <td colspan="2" class="headerLabel">
                            	<div id="batchLabel"><spring:message code="label.batchSample"/></div>
                            	<div id="levelLabel"><spring:message code="label.level"/></div>
                            	<div class="clear"></div>
                            </td>
<%    
	//resets
    scat = "level";
    labelType = scat;
    category = Header.HeaderCategory.Level;
    imageName = null;
    cssclass = null;
    centerParentControl = 0;
    levelParentControl = 0;
	centerParentsReg = 0;
    levelParentsReg = 0;
    tempStr = null;
%>
                            <%@include file="/includes/dataAccessMatrixColumnHeaders.jspf" %>
<script type="text/javascript">
      var levelNamesArray = [<%=levelJsArray.replace("''","','")%>];
</script>