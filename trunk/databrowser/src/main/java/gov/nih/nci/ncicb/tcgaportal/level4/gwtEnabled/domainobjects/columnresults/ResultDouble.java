package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;


/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jan 31, 2009
 * Time: 1:00:52 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * For any double-typed result
 */
public class ResultDouble extends ResultValue {

    private double dval;

    public ResultDouble() {
    }

    public ResultDouble(double dval) {
        this.dval = dval;
    }

    public double getValue() {
        return dval;
    }

    public void setValue(double correlation) {
        this.dval = correlation;
    }

    public String toString() {
        return Double.toString(dval);
    }

    public String toString(NumberFormatter formatter) {
        return formatter.format(dval);
    }

    public Object getSortableValue() {
        return dval;
    }
}
