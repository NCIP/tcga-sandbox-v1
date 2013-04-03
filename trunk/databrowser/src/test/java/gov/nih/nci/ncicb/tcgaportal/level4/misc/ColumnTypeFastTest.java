package gov.nih.nci.ncicb.tcgaportal.level4.misc;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;
import junit.framework.TestCase;

/**
 * Test class for ColumnType
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ColumnTypeFastTest extends TestCase {

    public void testLimits() {
        CopyNumberType cn = new CopyNumberType();
        cn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cn.setLowerLimit(-0.5);
        cn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cn.setUpperLimit(0.5);
        cn.setFrequency(0.2F);

        assertFalse(cn.passesCriteria(0));
        assertTrue(cn.passesCriteria(-1));
        assertTrue(cn.passesCriteria(1));
        
        ExpressionType e = new ExpressionType();
        e.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        e.setLowerLimit(-0.5);
        e.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        e.setUpperLimit(0.5);
        e.setFrequency(0.2F);
        
        assertFalse(e.passesCriteria(0));
        assertTrue(e.passesCriteria(-1));
        assertTrue(e.passesCriteria(1));
        
        CorrelationType c = new CorrelationType();
        c.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        c.setLowerLimit(-0.5);
        c.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        c.setUpperLimit(0.5);

        assertFalse(c.passesCriteria(0));
        assertTrue(c.passesCriteria(-1));
        assertTrue(c.passesCriteria(1));
        
    }

    public void testDisplayCriteria() {
        CopyNumberType cn = new CopyNumberType();
        cn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cn.setLowerLimit(-0.5);
        cn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cn.setUpperLimit(0.5);
        cn.setFrequency(0.2F);

        assertEquals("<= -0.5 or >= 0.5, Frequency >= 20%", cn.getDisplayCriteria("20%"));

        cn.setCalculationType(CopyNumberType.CalculationType.GISTIC);
        assertEquals("<= -0.5 or >= 0.5, Average Across Patients", cn.getDisplayCriteria(""));

        ExpressionType e = new ExpressionType();
        e.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        e.setLowerLimit(-0.5);
        e.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        e.setUpperLimit(0.5);
        e.setFrequency(0.2F);

        assertEquals("<= -0.5 or >= 0.5, Frequency >= 20%", e.getDisplayCriteria("20%"));

        MutationType m = new MutationType();
        m.setCategory(MutationType.Category.AnyNonSilent);
        m.setFrequency(0.1F);
        assertEquals("AnyNonSilent, Frequency >= 10%", m.getDisplayCriteria("10%"));
        m.setFrequency(0.00001F);
        assertEquals("AnyNonSilent, Frequency >= 1E-3%", m.getDisplayCriteria("1E-3%"));

        CorrelationType c = new CorrelationType();
        c.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        c.setLowerLimit(-0.5);
        c.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        c.setUpperLimit(0.5);
        c.setPvalueLimit(0.3);
        assertEquals("rvalue <= -0.5 or >= 0.5, pvalue <= 0.3", c.getDisplayCriteria("0.3"));
    }

}
