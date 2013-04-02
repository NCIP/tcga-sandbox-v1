<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%
response.setStatus(301);
response.setHeader( "Location", "https://wiki.nci.nih.gov/display/TCGA/TCGA+Home" );
response.setHeader( "Connection", "close" );
%> 