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
 * Contains a thread-local value indicating the disease for the current
 * request. Used by the DiseaseRoutingDataSource to dispatch to the appropriate
 * schema for that disease.
 *
 * @author David Nassau
 * @version $Rev$
 */
public class DiseaseContextHolder {

    private static final ThreadLocal<String> diseaseHolder = new ThreadLocal<String>();

    public static void setDisease(String disease) {
        diseaseHolder.set(disease);
    }

    public static String getDisease() {
        return (String)diseaseHolder.get();
    }

    public static void clearDisease() {
        diseaseHolder.remove();
    }

}
