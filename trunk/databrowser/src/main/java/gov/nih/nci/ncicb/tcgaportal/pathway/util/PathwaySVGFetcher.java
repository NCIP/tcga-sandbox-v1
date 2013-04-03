package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Oct 31, 2008
 * Time: 11:46:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class PathwaySVGFetcher implements IPathwaySVGFetcher {

    private String svgSite;
    private String cssURL;
    private IPathwayQueries prototypeQueries;
    private String cachedStyleSheet;

    public void setSvgSite(String svgSite) {
        this.svgSite = svgSite;
    }

    public void setCssURL(String cssFilePath) {
        this.cssURL = cssFilePath;
    }

    public void setPrototypeQueries(IPathwayQueries prototypeQueries) {
        this.prototypeQueries = prototypeQueries;
    }

    public String fetchPathwaySVG(String pathwayName, String bcgene, String anomaly) throws IOException, IPathwayQueries.PathwayQueriesException {
        return readSvgIntoString(pathwayName, bcgene, anomaly);
    }

    private String readSvgIntoString(String pathwayName, String bcgene, String anomaly) throws IOException, IPathwayQueries.PathwayQueriesException {
        StringBuffer svg = new StringBuffer();
        BufferedReader reader = null;
        try {
            String svgurl = makeUrlToPathwayServer(pathwayName, bcgene, anomaly);

            URL pathwayUrl = new URL(svgurl);
            URLConnection pathwayConnection = pathwayUrl.openConnection();
            pathwayConnection.connect();
            reader = new BufferedReader(new InputStreamReader(pathwayConnection.getInputStream()));

            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("<?xml-stylesheet")) {
                    svg.append(line).append('\n');
                    if (line.startsWith("<svg ")) {
                        insertInlineStyleSheet(svg);
                    }
                }
                line = reader.readLine();
            }
        } finally {
            if (reader != null) reader.close();
        }
        return svg.toString();
    }


    private void insertInlineStyleSheet(StringBuffer svg) throws IOException {
        svg.append("<style type=\"text/css\"><![CDATA[").append('\n');

        if (cachedStyleSheet == null) {
            StringBuffer cssBuf = new StringBuffer();
            BufferedReader styleReader = null;
            try {
                URL url = new URL(cssURL);
                URLConnection conn = url.openConnection();
                styleReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = styleReader.readLine();
                if (line == null) {
                    throw new IOException("No style sheet found at " + cssURL);
                }
                while (line != null) {
                    cssBuf.append(line).append('\n');
                    line = styleReader.readLine();
                }
            } finally {
                if (styleReader != null) styleReader.close();
            }

            cachedStyleSheet = cssBuf.toString();
        }

        svg.append(cachedStyleSheet).append('\n');
        svg.append("]]></style>").append('\n');
    }

    private String makeUrlToPathwayServer(String pathwayName, String bcgene, String anomaly) throws IPathwayQueries.PathwayQueriesException {
        String pathwayFileName = prototypeQueries.lookupPathwayFileName(pathwayName);
        String urlstr = svgSite + "?pathway=" + pathwayFileName;

        if (bcgene != null) urlstr += "&bcgene=" + bcgene;
        if (anomaly == null || anomaly.length() == 0) {
            urlstr += "&anomaly=none";
        } else {
            //"agent" is not really an anomaly, so we can't pass it directly in the URL
            if (anomaly.equals(Gene.ANOMALYTYPE_AGENT)) {
                anomaly = Gene.ANOMALYTYPE_ANY;
            }
            urlstr += "&anomaly=" + anomaly;
        }
        return urlstr;
    }
}
