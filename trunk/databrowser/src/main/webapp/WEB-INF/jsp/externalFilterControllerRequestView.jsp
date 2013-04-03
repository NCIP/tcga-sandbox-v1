<%--
    Description : JSP that gets the URL parameters from request URL and embeds them here.
                    These can then be used in the corresponding GWT module. 
    @author Namrata Rane
    Last updated by: $Author$
    @version $Rev$
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="tagsInclude.jsp" %>

<html>
  <head>
      <title>External Request Page</title>
      <meta name='gwt:module' content='anomalysearch.AnomalySearch/anomalysearch.AnomalySearch'>
  </head>
  <body>
      <script type="text/javascript" language="javascript" src="AnomalySearch/AnomalySearch.nocache.js"></script>
      <div id="anomalySearch">
          <jsp:useBean id="externalRequest" scope="request" type="gov.nih.nci.ncicb.tcgaportal.level4.web.request.ExternalRequest"/>
          <input type="hidden" id="mode" name="mode" value="<c:out value='${externalRequest.mode}'/>"/>
          <input type="hidden" id="disease" name="disease" value="<c:out value='${externalRequest.disease}'/>"/>
      </div>
  </body>
</html>