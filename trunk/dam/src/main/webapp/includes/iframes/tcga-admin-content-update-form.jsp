
<%@include file="/includes/page-variables.jspf"%>
<% rootDir = "tcga"; %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head><title>Content Update Form</title>
    </head>
    <body>
		<%@ page import="org.springframework.jdbc.object.SqlUpdate" %>
		<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="/<%=rootDir%>/scripts/ckeditor.js"></script>
		<script src="/<%=rootDir%>/scripts/editor_sample.js" type="text/javascript"></script>
		<link href="/<%=rootDir%>/styles/editor_form.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="/<%=rootDir%>/scripts/editorwyz.js"></script>			
		<%
		    DriverManagerDataSource dataSource = new DriverManagerDataSource();
		    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		    dataSource.setUrl(commondb);
		    dataSource.setUsername(commondbusr);
		    dataSource.setPassword(commondbpwd);
		 
		    Clob cv; 
			int rowCount = 0;
			String art_id = "";
			String art_title = "";
			String art_title_h1 = "";
			String art_desc = "";
			String art_keywords = "";
			String art_body = "";
			String art_url = "";
			String updateSqlStr = "";
			
		    JdbcTemplate  jdbcUpdateTemplate = new JdbcTemplate(dataSource);
			SqlUpdate sqlUpdate = new SqlUpdate(); 
			sqlUpdate.setJdbcTemplate(jdbcUpdateTemplate); 
		    
		    if (request.getParameter("post_id") != null) {
		    
		        if ( request.getParameter("body")!= null ){
					art_body = request.getParameter("body");
				}		
		        if ( request.getParameter("post_id")!= null ){
					art_id = request.getParameter("post_id");
				}
		        if ( request.getParameter("title") != null ){
					art_title = request.getParameter("title");
				}
		        if ( request.getParameter("title_h1") != null ){
					art_title_h1 = request.getParameter("title_h1");
				}
		        if ( request.getParameter("description") != null ){
					art_desc = request.getParameter("description");
				}
		        if ( request.getParameter("keywords") != null ){
					art_keywords = request.getParameter("keywords");
				}	
		        if ( request.getParameter("post_url") != null ){
					art_url = request.getParameter("post_url");
				}
				
				updateSqlStr = "UPDATE content_post SET title='" + art_title + "', title_h1='" + art_title_h1 + "', description='" + art_desc + "', keywords='" + art_keywords + "', body='" + art_body + "' WHERE post_id='" + art_id + "'";
		    
				sqlUpdate.setSql(updateSqlStr); 
				sqlUpdate.compile(); 
				sqlUpdate.update();
		    
		    }
		    if ( request.getParameter("post_url") != null ){
				art_url = request.getParameter("post_url");
			}
				
		    JdbcTemplate  jdbcTemplate = new JdbcTemplate(dataSource);
		    SqlRowSet contentRow = jdbcTemplate.queryForRowSet("select * from content_post where post_url = '" + art_url + "'");
		
		    if (contentRow.next()) {
		        cv = (SerialClob)contentRow.getObject("BODY");
		        art_body = cv.getSubString(1L, (int)cv.length());	
		        
		        if ( contentRow.getString("POST_ID")!= null ){
					art_id = contentRow.getString("POST_ID");
				}
		        if ( contentRow.getString("TITLE") != null ){
					art_title = contentRow.getString("TITLE");
				}
		        if ( contentRow.getString("TITLE_H1") != null ){
					art_title_h1 = contentRow.getString("TITLE_H1");
				}
		        if ( contentRow.getString("DESCRIPTION") != null ){
					art_desc = contentRow.getString("DESCRIPTION");
				}
		        if ( contentRow.getString("KEYWORDS") != null ){
					art_keywords = contentRow.getString("KEYWORDS");
				}	
		        if ( contentRow.getString("POST_URL") != null ){
					art_url = contentRow.getString("POST_URL");
				}
		%>
				<form method="post" name="editform" id="editform" action="">
					<input type="hidden" name="row-number" id="row<%=rowCount%>" />
					<input type="hidden" name="post_id" value="<%=art_id%>" />					
					<fieldset>
						<label>Title</label><br />
						<input type="text" name="title" size="110" maxlength="100" value="<%=art_title%>" />
					</fieldset>
					<fieldset>
						<label>H1 Override</label><br />
						<input type="text" name="title_h1" id="linktext<%=rowCount%>" size="110" maxlength="100" value="<%=art_title_h1%>" />
					</fieldset>
					<fieldset>
						<label>Key Words</label><br />
						<textarea cols="70" name="keywords" id="keywords<%=rowCount%>" rows="2"><%=art_keywords%></textarea>
					</fieldset>
					<fieldset>
						<label>Description</label><br />
						<textarea cols="70" name="description" id="desc" rows="2"><%=art_desc%></textarea>
					</fieldset>
					<fieldset>
						<label>Article</label><br />
						<textarea cols="70" name="body"  id="body" style="height:480px;"><%=art_body%></textarea>
					</fieldset>
					<fieldset>
						<input type="image" src="/tcga/images/send.gif" name="Submit" value="submit">
					</fieldset>
				</form>
				<script type="text/javascript">applyWyz( 'body' );</script>
		<% } %>
	</body>
</html>