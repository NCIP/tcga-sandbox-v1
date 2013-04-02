<%
	String[] jsFiles;
	String param = request.getParameter("js");

	if(param != null && param.length() > 0){
		jsFiles = param.split(",");
		for (int i = 0; i < jsFiles.length; i++) { 
%>
/* <%=jsFiles[i]%> */
			<jsp:include page="<%=jsFiles[i]%>" />
<% 
 		}
	}
%>