			<div id="menuDownloadData" class="boxtop">
				In This Section
			</div>
			<div class="boxbody">
				<ul class="triangle">
					<li>
					<%
						if ("/tcgaDownload.jsp".equals(request.getServletPath())) {
							out.println("Download Data");
						}
						else {
							out.println("<a href=\"/tcga/tcgaDownload.jsp\">Download Data</a>");
						}
					%>
					</li>
					<li><a href="/tcga/dataAccessMatrix.htm">Data Matrix</a></li>
					<li>
					<%
						if ("/findArchives.htm".equals(request.getServletPath())) {
							out.println("Bulk Download");
						}
						else {
							out.println("<a href=\"/tcga/findArchives.htm\">Bulk Download</a>");
						}
					%>
					</li>
					<li><a href="https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/"target="newWin">Open-Access HTTP Directory</a> <div class="newWinIcon">&nbsp;</div></li>
					<li><a href="https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/"target="newWin">Controlled-Access HTTP Directory</a> <div class="newWinIcon">&nbsp;</div></li>
				</ul>
			</div>
