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
public class PearsonCorrelationCalculator implements CorrelationCalculator {

    /**
     * Returns a single correlation result (number between -1 and 1) given two lists of patient results.
     * For example, scores1 can be all the patient results for a copy number platform for a specific gene,
     * and scores2 can be all the gene expression results for the same gene.  It returns the correlation
     * CN/Exp for that gene.
     */
    public float calculateCorrelation(float[] scores1, float[] scores2) {
        if (scores1 == null || scores2 == null || scores1.length == 0 || scores2.length == 0) {
            throw new IllegalArgumentException("Cannot correlate 0-length lists");
        }
        if (scores1.length != scores2.length) {
            throw new IllegalArgumentException("Cannot correlate lists of different length");
        }

        float result = 0;
        float sum_sq_x = 0;
        float sum_sq_y = 0;
        float sum_coproduct = 0;
        float mean_x = scores1[0];
        float mean_y = scores2[0];
        for (int i = 2; i < scores1.length + 1; i += 1) {
            float sweep = Float.valueOf(i - 1) / i;
            float delta_x = scores1[i - 1] - mean_x;
            float delta_y = scores2[i - 1] - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        float pop_sd_x = (float) Math.sqrt(sum_sq_x / scores1.length);
        float pop_sd_y = (float) Math.sqrt(sum_sq_y / scores1.length);
        float cov_x_y = sum_coproduct / scores1.length;
        result = cov_x_y / (pop_sd_x * pop_sd_y);
        //always round to two decimals, since those are the only meaningful places anyway
        result = Math.round(result * 100) / 100f;
        return result;
    }

}
