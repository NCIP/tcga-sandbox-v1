/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Interface for shipped portion id processing
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public interface ShippedPortionIdProcessor {
    public Collection<String> getTextElements(final File file, final String parentXPath, final String elementName)  throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException;
    public Boolean shippedPortionExists(final File file)  throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException;
}
