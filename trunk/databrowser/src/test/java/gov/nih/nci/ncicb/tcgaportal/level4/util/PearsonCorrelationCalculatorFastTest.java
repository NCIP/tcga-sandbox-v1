package gov.nih.nci.ncicb.tcgaportal.level4.util;

import junit.framework.TestCase;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jan 21, 2009
 * Time: 11:15:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class PearsonCorrelationCalculatorFastTest extends TestCase {

    static int TOTAL_PATIENTS = 500;
    static int TOTAL_GENES = 11000;

    CorrelationCalculator cc;

    public void setUp() {
        cc = new PearsonCorrelationCalculator();
    }

    public void testCorrelations() {
        Random rnd = new Random();

        long before = System.currentTimeMillis();
        for (int igene=0; igene<TOTAL_GENES; igene++) {
            float[] scores1 = new float[TOTAL_PATIENTS];
            float[] scores2 = new float[TOTAL_PATIENTS];
            for (int i=0; i<TOTAL_PATIENTS; i++) {
                scores1[i] = rnd.nextFloat(); //represents the actual result for one patient for one gene
            }
            for (int i=0; i<TOTAL_PATIENTS; i++) {
                scores2[i] = rnd.nextFloat(); //represents the actual result for one patient for one gene
            }

            float correlation = cc.calculateCorrelation(scores1, scores2);
            System.out.println(correlation);
        }
        long after = System.currentTimeMillis();

        System.out.println("Total time=" + (after-before));
    }

}
