<%@ include file="../../header.jsp" %>
<form method="POST" action="emailAdmin.htm">
<div id="middle" align="center">
    <div id="content">
        <table style="border: solid 0px #999999;" cellpadding="5" cellspacing="0" align="center" bgcolor="#F3F3F3" width="80%">
        <tr>
            <td valign="top"><strong>DCC Data Summary Report to PI</strong></td>
            <td bgcolor="white">
               <input type="radio" name="datareport" value="active" <c:if test="${runner.active}"> checked </c:if>> Active
            </td>
            <td bgcolor="white">
                <input type="radio" name="datareport" value="inactive" <c:if test="${!runner.active}"> checked </c:if>> Inactive
            </td>
        </tr>
        <tr>
            <td valign="top"><strong>Other Email Service</strong></td>
            <td bgcolor="white">
                <input type="radio" name="otherservice" value="active" > Active
            </td>
            <td bgcolor="white">
                <input type="radio" name="otherservice" value="inactive" checked> Inactive
            </td>
        </tr>
        <tr>
            <td valign="top"><strong></strong></td>
            <td bgcolor="white">
                <input type="submit" value="Submit"/>
            </td>
            <td bgcolor="white">
                <input type="reset" value="Cancel"/>
            </td>
        </tr>
        </table>
    </div>
</div>
</form>
<div id="blue_line" align="center">
    <img src="<c:url value="/images/layout/blue_line.gif"/>"/>
</div>
<%@ include file="../../footer.jsp" %>