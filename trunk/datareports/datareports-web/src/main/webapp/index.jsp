<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
Diagnostic information. Uncomment to test your environment.

Working with server: <%= application.getServerInfo() %><br>
Servlet Specification: <%= application.getMajorVersion() %>.<%= application.getMinorVersion() %> <br>
JSP version: <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %><br>
Java Version: <%= System.getProperty("java.version") %><br>

ContextPath: <%= request.getContextPath() %><br>
ServletPath: <%= request.getServletPath() %><br>
Actual path: <%= application.getRealPath("/") %><br>
<hr>
El is working ?  ${1 == 1}<br>
1 + 1 = ${1 + 1}<br>
<br>

--%>

<jsp:forward page="dataReportsHome.htm"/>