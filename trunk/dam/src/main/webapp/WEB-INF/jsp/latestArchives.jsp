<%@ include file="tagsInclude.jsp" %>
<%@ page contentType="text/plain;charset=UTF-8" language="java" %>
<%--
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<c:forEach var="archive" items="${archiveList}">
    <c:out value="${archive.realName}   http://tcga-data.nci.nih.gov${archive.deployLocation}"  /></c:forEach>
