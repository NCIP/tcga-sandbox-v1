<%@ page contentType="text/javascript; charset=UTF-8" %>
<%@ page session="false" %>
<% 
long now = System.currentTimeMillis();
response.setDateHeader("Expires", now + 1728000000); //20days 
%>
<%
	String[] jsFiles;
	String param = request.getParameter("js");

	if(param != null && param.length() > 0){
		jsFiles = param.split(" ");
		for (int i = 0; i < jsFiles.length; i++) { 
%>
/* including <%=jsFiles[i]%> */
			<jsp:include page="<%=jsFiles[i]%>" />
<% 
 		}
	}
%>