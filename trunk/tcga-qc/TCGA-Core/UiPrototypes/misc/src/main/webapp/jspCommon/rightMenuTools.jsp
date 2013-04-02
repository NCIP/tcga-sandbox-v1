			<div class="boxtop">
				In This Section
			</div>
			<div class="boxbody">
				<ul class="triangle">
					<li>
					<%
						if ("/tcgaTools.jsp".equals(request.getServletPath())) {
							out.println("Tools");
						}
						else {
							out.println("<a href=\"/tcga/tcgaTools.jsp\">Tools</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaAnalyticalTools.jsp".equals(request.getServletPath())) {
							out.println("Analytical Tools");
						}
						else {
							out.println("<a href=\"/tcga/tcgaAnalyticalTools.jsp\">Analytical Tools</a>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaAnnotations.jsp".equals(request.getServletPath())) {
							out.println("Annotations Manager");
						}
						else {
							out.println("<span class=\"annotationsLink\"><span class=\"greyLink\">Annotations Manager</span></span>");
						}
					%>
					</li>
					<li>
					<%
						if ("/tcgaUuidManager.jsp".equals(request.getServletPath())) {
							out.println("UUID Manager");
						}
						else {
							out.println("<span class=\"uuidLink\"><span class=\"greyLink\">UUID Manager</span></span>");
						}
					%>
					</li>
				</ul>
			</div>
