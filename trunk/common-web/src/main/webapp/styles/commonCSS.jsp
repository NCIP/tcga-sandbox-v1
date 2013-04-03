<%@ page contentType="text/css; charset=UTF-8" %>
<%@ page session="false" %>
<% 
long now = System.currentTimeMillis();
response.setDateHeader("Expires", now + 1728000000); //20days 
%>
/* including ext-all.css*/
<jsp:include page="ext-all.css" />
/* including ext-overrides.css*/
<jsp:include page="ext-overrides.css" />
/* including xtheme-gray.css*/
<jsp:include page="xtheme-gray.css" />
/* including ux/Ext.ux.grid.RowActions.css*/
<jsp:include page="ux/Ext.ux.grid.RowActions.css" />
/* including tcga_main.css*/
<jsp:include page="tcga_main.css" />