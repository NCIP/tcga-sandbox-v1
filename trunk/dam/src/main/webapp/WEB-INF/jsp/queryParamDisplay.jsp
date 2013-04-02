<div id="quereyResults">
    <spring:message code="label.searchParameters"/><br>
    <%
        final String[] platVals = (String[]) request.getSession().getAttribute("platVals");
        final String[] centerVals = (String[]) request.getSession().getAttribute("centerVals");
        final String[] dataTypeVals = (String[]) request.getSession().getAttribute("dataTypeVals");
        final String[] tumorVals = (String[]) request.getSession().getAttribute("tumorVals");
        final String fileName = (String) request.getSession().getAttribute("fileName");
    %>
    <table style="border: solid 0px #999999;" cellpadding="5" cellspacing="0" align="center" bgcolor="#F3F3F3"
           width="50%">
        <tr>
            <td valign="top"><strong><spring:message code="label.dateRange"/></strong></td>
            <td bgcolor="white">
                <spring:message code="label.startOn"/> <%=request.getSession().getAttribute( "dateStart" )%> <spring:message code="label.endOn"/><%=request.getSession().getAttribute( "dateEnd" )%>
            </td>
        </tr>
        <c:if test="${tumorVals[0] != 0}">
            <tr>

                <td valign="top"><strong><spring:message code="label.tumors"/></strong></td>
                <td bgcolor="white">
                    <c:forEach items="${tumorVals}" var="tumorParam">
                        <c:forEach items="${tumors}" var="tumor">
                            <c:if test="${tumor.disease_id == tumorParam}">
                                * <c:out value="${tumor.disease_name}"/><br>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </td>
            </tr>
        </c:if>
        <c:if test="${centerVals[0] != 0}">
            <tr>
                <td valign="top"><strong><spring:message code="label.centers"/></strong></td>
                <td bgcolor="white">
                    <c:forEach items="${centerVals}" var="centerParam">
                        <c:forEach items="${centers}" var="center">
                            <c:if test="${center.center_id == centerParam}">
                                * <c:out value="${center.display_name}"/><br>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </td>
            </tr>
        </c:if>
        <c:if test="${platVals[0] != 0}">
            <tr>
                <td valign="top"><strong><spring:message code="label.platforms"/></strong></td>
                <td bgcolor="white">
                    <c:forEach items="${platVals}" var="platParam">
                        <c:forEach items="${platforms}" var="platform">
                            <c:if test="${platform.platform_id == platParam}">
                                * <c:out value="${platform.platform_display_name}"/><br>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </td>
            </tr>
        </c:if>
        <c:if test="${dataTypeVals[0] != 0}">
            <tr>
                <td valign="top"><strong><spring:message code="label.dataTypes"/></strong></td>
                <td bgcolor="white">
                    <c:forEach items="${dataTypeVals}" var="dataTypeParam">
                        <c:forEach items="${datatypes}" var="dataType">
                            <c:if test="${dataType.data_type_id == dataTypeParam}">
                                * <c:out value="${dataType.name}"/><br>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </td>
            </tr>
        </c:if>
        <c:if test="${fileName != null}">
            <tr>
                <td valign="top"><strong><spring:message code="label.fileName"/></strong></td>
                <td bgcolor="white">
                    <%= fileName %>
                </td>
            </tr>
        </c:if>
    </table>
</div>