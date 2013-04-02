<%@ page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%--
    Author: Jessica Chen
--%>

<head>
    <title>QcLive Monitor</title>
    <meta http-equiv="Refresh" content="30">
</head>
<body>

<h2>
    <form action="qcLiveJobs.htm" method="POST">
        <c:choose>
            <c:when test="${QcLiveIsRunning}">
                <input type="hidden" name="setRunning" value="false">
                QCLive is running normally.
                <br/><input type="submit" value="Pause QcLive">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="setRunning" value="true">
                QCLive is paused.  Once currently running jobs are done, no new jobs will be started.
                <br/><input type="submit" value="Resume QcLive">
            </c:otherwise>
        </c:choose>
    </form>
</h2>

<h2>QCLive Jobs Currently Running</h2>

<table border="1" rules="all">
    <tr>
        <th>Job Type</th>
        <th>Job Name</th>
        <th>Job Start Time</th>
    </tr>
    <c:forEach items="${QcLiveRunningJobs}" var="jobItem">

        <tr>
            <td><c:out value="${jobItem.jobType}"/></td>
            <td><c:out value="${jobItem.jobName}"/></td>
            <td><fmt:formatDate value="${jobItem.startTime}" type="date" pattern="dd-MMM-yyyy hh:mm:ss"/></td>
        </tr>
    </c:forEach>
</table border="1" rules="all">
<hr>
<h2>QCLive Jobs Waiting to Run</h2>

<table border="1" rules="all">
    <tr>
        <th>Job Type</th>
        <th>Job Name</th>
        <th>Scheduled Start Time</th>
    </tr>
    <c:forEach items="${QcLiveWaitingJobs}" var="jobItem">
        <tr>
            <td><c:out value="${jobItem.jobType}"/></td>
            <td><c:out value="${jobItem.jobName}"/></td>
            <td><fmt:formatDate value="${jobItem.startTime}" type="date" pattern="dd-MMM-yyyy hh:mm:ss"/></td>            
        </tr>
    </c:forEach>
</table>

<br/><br/>This page will automatically refresh every 30 seconds. <br/>
Last Updated: <i><%=java.util.Calendar.getInstance().getTime().toString()%></i>
</body>


