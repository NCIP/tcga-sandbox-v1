/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

/**
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public interface CorrelationCalculator {

    /**
     * Returns a single correlation result (number between -1 and 1) given two lists of patient results.
     * For example, scores1 can be all the patient results for a copy number platform for a specific gene,
     * and scores2 can be all the gene expression results for the same gene.  It returns the correlation
     * CN/Exp for that gene.
     */
    float calculateCorrelation(float[] scores1, float[] scores2);

}
