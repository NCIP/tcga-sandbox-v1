/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.annotations.TCGAValue;
import org.springframework.stereotype.Component;

/**
 * Constants class for BAM loader
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class BAMLoaderConstants {

    public static final String DISEASE_QUERY = "select DISEASE_ID, DISEASE_ABBREVIATION," +
            " DISEASE_NAME from DISEASE where ACTIVE=1 order by DISEASE_NAME";

    public static final String CENTER_QUERY = "select CENTER_ID, SHORT_NAME," +
            " CENTER_TYPE_CODE from CENTER order by SHORT_NAME";

    public static final String DATATYPE_BAM_QUERY = "select BAM_DATATYPE_ID, BAM_DATATYPE, " +
            " GENERAL_DATATYPE  from BAM_FILE_DATATYPE order by BAM_DATATYPE";

    public static final String ALIQUOT_QUERY = "select SHIPPED_BIOSPECIMEN_ID, BUILT_BARCODE from " +
            "SHIPPED_BIOSPECIMEN order by BUILT_BARCODE";

    public static final String BAM_INSERT = "insert into BAM_FILE " +
            "(BAM_FILE_ID, BAM_FILE_NAME, DISEASE_ID, CENTER_ID, BAM_FILE_SIZE, DATE_RECEIVED, BAM_DATATYPE_ID) " +
            "values (?, ?, ?, ?, ?, ?, ?)";

    public static final String EXTENDED_BAM_INSERT = "insert into BAM_FILE " +
            "(ANALYSIS_ID, BAM_FILE_ID, BAM_FILE_NAME, DISEASE_ID, CENTER_ID, BAM_FILE_SIZE, DATE_RECEIVED, BAM_DATATYPE_ID, ANALYTE_CODE,DCC_RECEIVED_DATE) " +
            "values (?,?, ?, ?, ?, ?, ?, ?,?,?)";

    public static final String BAM_TO_ALIQUOT_INSERT = "insert into SHIPPED_BIOSPECIMEN_BAMFILE " +
            "(SHIPPED_BIOSPECIMEN_ID, BAM_FILE_ID) " +
            "values (?, ?)";

    public static final String BAM_TO_ALIQUOT_DELETE = "delete from SHIPPED_BIOSPECIMEN_BAMFILE";

    public static final String BAM_DELETE = "delete from BAM_FILE";

    public static final int BATCH_SIZE = 1000;

    @TCGAValue(key = "BAMFilePath")
    public static String BAMFilePath;

    @TCGAValue(key = "extendedBAMFilePath")
    public static String extendedBAMFilePath;

    @TCGAValue(key = "extendedBAMFile")
    public static Boolean extendedBAMFile;

    public void setBAMFilePath(String BAMFilePath) {
        BAMLoaderConstants.BAMFilePath = BAMFilePath;
    }

    public static String getExtendedBAMFilePath() {
        return extendedBAMFilePath;
    }

    public void setExtendedBAMFilePath(String extendedBAMFilePath) {
        BAMLoaderConstants.extendedBAMFilePath = extendedBAMFilePath;
    }

    public static Boolean isExtendedBAMFile() {
        return extendedBAMFile;
    }

    public static Boolean getExtendedBAMFile() {
        return extendedBAMFile;
    }

    public void setExtendedBAMFile(Boolean extendedBAMFile) {
        BAMLoaderConstants.extendedBAMFile = extendedBAMFile;
    }
}//End of Class
