/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BAMFileQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Loads BAM data.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMLoader {
    protected final Log logger = LogFactory.getLog(getClass());
    private String bamXmlFilePath;
    private String cgHubUrl;
    private Integer cgHubXMLParsingDelayInterval;

    @Autowired
    private BAMParser bamParser;
    @Autowired
    private BAMValidator bamValidator;
    @Autowired
    private BAMFileQueries bamFileQueries;



    public void loadBAMData(final BamContext bamContext) throws IOException, SAXException {

        String bamFile = null;
        try{

            bamFile = downloadBamXmlFile(getCGHubWebServiceParameter());
            logger.info(" Downloaded bam File "+ bamFile);
            delayParsingBamFile();
            final BamXmlResultSet bamXmlResultSet = bamParser.parse(bamFile);
            bamValidator.validate(bamXmlResultSet, bamContext);
            logger.info(" Validated bam file.");
            bamFileQueries.store(bamXmlResultSet);
            logger.info(" Persisted "+ bamXmlResultSet.getBamXmlResultList().size()+ " bam records.");

        }finally {
            if(bamFile != null) {
                final File file = new File(bamFile);
                file.delete();
            }

        }
    }

    private void delayParsingBamFile(){
        // This delay is introduced for QA to update the BAMFile before it get parsed.
        try{
            Thread.sleep(getCgHubXMLParsingDelayInterval()*1000l);
        }catch(Exception e){

        }
    }
    protected String downloadBamXmlFile(String urlParam) throws IOException {
        urlParam = URLEncoder.encode(urlParam, "UTF-8");
        final URL url = new URL(cgHubUrl + urlParam);
        final String fileName = "BAM_" + UUID.randomUUID() + FileName.XML_EXTENSION.getValue();
        final File file = new File(bamXmlFilePath + fileName);
        org.apache.commons.io.FileUtils.copyURLToFile(url, file);
        return file.getPath();
    }

    private String getCGHubWebServiceParameter() {
        final Date lastUploadedDate = bamFileQueries.getLatestUploadedDate();
        final StringBuffer sb = new StringBuffer();
        // Eg. [2011-07-15T23:59:59.99Z TO 2011-07-25T23:59:59.99Z]
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        sb.append("[")
                .append(simpleDateFormat.format(lastUploadedDate))
                .append("T00:00:00.99Z TO ")
                .append(simpleDateFormat.format(new Date()))
                .append("T23:59:59.99Z]");

        return sb.toString();
    }

    public void setBamParser(BAMParser bamParser) {
        this.bamParser = bamParser;
    }

    public void setBamFileQueries(BAMFileQueries bamFileQueries) {
        this.bamFileQueries = bamFileQueries;
    }

    public void setBamXmlFilePath(String bamXmlFilePath) {
        this.bamXmlFilePath = bamXmlFilePath;
    }

    public void setCgHubUrl(String cgHubUrl) {
        this.cgHubUrl = cgHubUrl;
    }

    public void setBamValidator(BAMValidator bamValidator) {
        this.bamValidator = bamValidator;
    }

    public Integer getCgHubXMLParsingDelayInterval() {
        return cgHubXMLParsingDelayInterval;
    }

    public void setCgHubXMLParsingDelayInterval(Integer cgHubXMLParsingDelayInterval) {
        this.cgHubXMLParsingDelayInterval = cgHubXMLParsingDelayInterval;
    }
}//End of class
