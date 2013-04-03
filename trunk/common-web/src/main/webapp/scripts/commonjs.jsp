<%@ page contentType="text/javascript; charset=UTF-8" %>
<%@ page session="false" %>
<% 
long now = System.currentTimeMillis();
response.setDateHeader("Expires", now + 1728000000); //20days 
%>
/* including thirdParty/ext/ext-base-debug.js*/
<jsp:include page="thirdParty/ext/ext-base-debug.js" />
/* including thirdParty/ext/ext-all-debug.js*/
<jsp:include page="thirdParty/ext/ext-all-debug.js" />
/* including utilities/util.js*/
<jsp:include page="utilities/util.js" />
/* including utilities/textDisplay.js*/
<jsp:include page="utilities/textDisplay.js" />
/* including utilities/hover.js*/
<jsp:include page="utilities/hover.js" />
/* including utilities/hoverSVG.js*/
<jsp:include page="utilities/hoverSVG.js" />
/* including utilities/colorUtil.js*/
<jsp:include page="utilities/colorUtil.js" />
/* including extensions/extOverrides.js*/
<jsp:include page="extensions/extOverrides.js" />
/* including extensions/buttonPlus.js*/
<jsp:include page="extensions/buttonPlus.js" />
/* including thirdParty/jquery/jquery-1.4.2.js*/
<jsp:include page="thirdParty/jquery/jquery-1.4.2.js" />
/* including thirdParty/jquery/jquery.hoverIntent.js*/
<jsp:include page="thirdParty/jquery/jquery.hoverIntent.js" />
/* including ux/PagingStore.js */
<jsp:include page="ux/PagingStore.js" />
/* including ux/Ext.ux.grid.RowActions.js */
<jsp:include page="ux/Ext.ux.grid.RowActions.js" />
/* including ux/FileUploadField.js */
<jsp:include page="ux/FileUploadField.js" />
/* including ux/miframe.js */
<jsp:include page="ux/miframe.js" />
/* including extensions/configurableLookTabPanel.js */
<jsp:include page="extensions/configurableLookTabPanel.js" />