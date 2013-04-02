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
 * Bean class representing the BAM datatype table
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMDatatype {

    private Integer datatypeBAMId;
    private String datatypeBAM;
    private String molecule;
    private String generalDatatype;

    public BAMDatatype() {
    }

    public BAMDatatype(Integer datatypeBAMId, String datatypeBAM, String molecule, String generalDatatype) {
        this.datatypeBAMId = datatypeBAMId;
        this.datatypeBAM = datatypeBAM;
        this.molecule = molecule;
        this.generalDatatype = generalDatatype;
    }

    public Integer getDatatypeBAMId() {
        return datatypeBAMId;
    }

    public void setDatatypeBAMId(Integer datatypeBAMId) {
        this.datatypeBAMId = datatypeBAMId;
    }

    public String getDatatypeBAM() {
        return datatypeBAM;
    }

    public void setDatatypeBAM(String datatypeBAM) {
        this.datatypeBAM = datatypeBAM;
    }

    public String getMolecule() {
        return molecule;
    }

    public void setMolecule(String molecule) {
        this.molecule = molecule;
    }

    public String getGeneralDatatype() {
        return generalDatatype;
    }

    public void setGeneralDatatype(String generalDatatype) {
        this.generalDatatype = generalDatatype;
    }
}//End of Class
