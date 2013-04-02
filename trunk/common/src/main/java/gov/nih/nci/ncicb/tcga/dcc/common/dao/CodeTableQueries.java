/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

/**
 * Class to validate if the different codes (project, Tissue source site etc) exist in database 
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface CodeTableQueries {

    /**
     * Checks if the given project name exists in database
     * @param projectName project name 
     * @return true if exists otherwise false
     */
    public boolean projectNameExists(String projectName);

    /**
     * Checks if the given tissue source site code exists in database
     * @param tssCode tissue source site code
     * @return true if exists otherwise false
     */
    public boolean tssCodeExists(final String tssCode);

    /**
     * Checks if a given sample type exists in database
     * @param sampleType sample type
     * @return true if exists otherwise false
     */
    public boolean sampleTypeExists(final String sampleType);

    /**
     * Checks if the given portion analyte exists in database 
     * @param portionAnalyte portion analyte
     * @return true if exists otherwise false
     */
    public boolean portionAnalyteExists(final String portionAnalyte);

    /**
     * Checks if the given bcr center id exists in database
     * @param bcrCenterId bcr center id
     * @return true if exists otherwise false
     */
    public boolean bcrCenterIdExists(final String bcrCenterId);
    
}
