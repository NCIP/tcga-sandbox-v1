<%@ taglib prefix="spring" uri="http://java.sun.com/jstl/fmt" %>
<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
~ Copyright Notice.  The software subject to this notice and license includes both human
~ readable source code form and machine readable, binary, object code form (the "caBIG
~ Software").
~
~ Please refer to the complete License text for full details at the root of the project.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>



<center><font size="+1" face="Verdana">An Error Occurred</font></center>
<br>
<center>
    <br><br>
    <b>Stack trace: </b><c:out value="${ErrorInfo.stackTrace}"/>
    <br><br>
</center>


