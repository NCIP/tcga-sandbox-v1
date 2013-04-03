/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * wrapper bean to be used for uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataViewWS {

    UUIDBrowserWS tcgaElement;

    public MetadataViewWS() {
    }

    public MetadataViewWS(UUIDBrowserWS tcgaElement) {
        this.tcgaElement = tcgaElement;
    }

    public UUIDBrowserWS getTcgaElement() {
        return tcgaElement;
    }

    public void setTcgaElement(UUIDBrowserWS tcgaElement) {
        this.tcgaElement = tcgaElement;
    }
}//End of class
