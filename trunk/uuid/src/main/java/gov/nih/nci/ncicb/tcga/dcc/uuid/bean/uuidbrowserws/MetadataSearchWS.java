/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * wrapper bean to be used for uuid browser search web service with hack because jersey'jaxb do not handle
 * namespace prefix well
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name="metadata")
@XmlAccessorType(XmlAccessType.NONE)
public class MetadataSearchWS {

    @XmlElement
    List<BiospecimenMetaData> tcgaElement;

    public MetadataSearchWS() {
    }

    public MetadataSearchWS(List<BiospecimenMetaData> tcgaElement) {
        this.tcgaElement = tcgaElement;
    }

    public List<BiospecimenMetaData> getTcgaElement() {
        return tcgaElement;
    }

    public void setTcgaElement(List<BiospecimenMetaData> tcgaElement) {
        this.tcgaElement = tcgaElement;
    }
    
}//End of class
