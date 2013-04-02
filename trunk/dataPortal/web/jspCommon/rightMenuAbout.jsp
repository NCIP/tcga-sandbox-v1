			<div id="menuAbout" class="boxtop">
				In This Section
			</div>
			<div class="boxbody">
				<ul class="triangle">
					<li>
					<%
						if ("/tcgaAbout.jsp".equals(request.getServletPath())) {
							out.println("About the Data");
						}
						else {
							out.println("<a href=\"/tcga/tcgaAbout.jsp\">About the Data</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaDataType.jsp".equals(request.getServletPath())) {
							out.println("Data Levels and Data Types");
						}
						else {
							out.println("<a href=\"/tcga/tcgaDataType.jsp\">Data Levels and Data Types</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaPlatformDesign.jsp".equals(request.getServletPath())) {
							out.println("Platform Design");
						}
						else {
							out.println("<a href=\"/tcga/tcgaPlatformDesign.jsp\">Platform Design</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaAccessTiers.jsp".equals(request.getServletPath())) {
							out.println("Access Tiers");
						}
						else {
							out.println("<a href=\"/tcga/tcgaAccessTiers.jsp\">Access Tiers</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaDataTracking.jsp".equals(request.getServletPath())) {
							out.println("Reports");
						}
						else {
							out.println("<a href=\"/datareports/\">Reports</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaAnnouncements.jsp".equals(request.getServletPath())) {
							out.println("Announcements");
						}
						else {
							out.println("<a href=\"/tcga/tcgaAnnouncements.jsp\">Announcements</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaHelp.jsp".equals(request.getServletPath())) {
							out.println("User Guides and Help");
						}
						else {
							out.println("<a href=\"/tcga/tcgaHelp.jsp\">User Guides and Help</a>");
						}
					%>
					</li>
				</ul>
			</div>
