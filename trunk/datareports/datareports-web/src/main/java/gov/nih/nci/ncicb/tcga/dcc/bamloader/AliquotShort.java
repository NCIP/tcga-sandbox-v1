/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

/**
 * bean class of a very short representation of an aliquot in the database
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotShort {

    private Long aliquotId;
    private String barcode;

    public AliquotShort() {
    }

    public AliquotShort(Long aliquotId, String barcode) {
        this.aliquotId = aliquotId;
        this.barcode = barcode;
    }

    public Long getAliquotId() {
        return aliquotId;
    }

    public void setAliquotId(Long aliquotId) {
        this.aliquotId = aliquotId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}//End of Class
