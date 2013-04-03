<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%--

This file is needed so that extjs file upload form can get the upload status value.
The body of this document is used by etxjs as response

Excerpt from extjs documentation :
File uploads are not performed using Ajax submission, that is they are not performed
using XMLHttpRequests. Instead the form is submitted in the standard manner with the DOM
<form> element temporarily modified to have its target  set to refer to a dynamically generated,
hidden <iframe> which is inserted into the document but removed after the return data has
been gathered. The server response is parsed by the browser to create the document for the IFRAME.

--%>

<html>
<body>
<c:out value='${response}'/>
</body>
</html>