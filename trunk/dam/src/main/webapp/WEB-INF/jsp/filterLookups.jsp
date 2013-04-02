<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page contentType="text/plain;charset=UTF-8" language="java" %>
<%
Map lookups = (Map)request.getAttribute("lookups");
Iterator keyIter = lookups.keySet().iterator();
while (keyIter.hasNext()) {
    String category = (String)keyIter.next();
    List pairs = (List)lookups.get(category);
    Iterator pairIter = pairs.iterator();
    while (pairIter.hasNext()) {
        String[] pair = (String[])pairIter.next();
%>
<%=category + "\t" + pair[0] + "\t" +  pair[1]%>
<%
    }
}
%>