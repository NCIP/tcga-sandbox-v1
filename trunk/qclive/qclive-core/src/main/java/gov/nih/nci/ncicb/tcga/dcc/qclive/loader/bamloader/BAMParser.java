/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.javabean.binding.xml.XMLBinding;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Parser for CGHub provided Bam xml files.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class BAMParser {
    protected final Log logger = LogFactory.getLog(getClass());

    public BamXmlResultSet parse(final String fileName) throws IOException, SAXException {
        logger.info("Started parsing bam file "+fileName);
        final XMLBinding xmlBinding = new XMLBinding().add("bam-smooks-config.xml");
        xmlBinding.intiailize();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(fileName));
            Source xmlSource = new StreamSource(reader);
            BamXmlResultSet bamXmlResultSet = xmlBinding.fromXML(xmlSource, BamXmlResultSet.class);
            logger.info("Completed parsing bam file "+fileName);
            return bamXmlResultSet;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}//End of class
