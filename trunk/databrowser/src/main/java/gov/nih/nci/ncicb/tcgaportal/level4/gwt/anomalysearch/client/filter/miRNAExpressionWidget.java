package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jul 27, 2009
 * Time: 4:31:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class miRNAExpressionWidget extends NonMutationAnomalyWidget {

    public miRNAExpressionWidget(ColumnType cType) {
        super(cType);
    }

    protected AnomalyWidget instanceForClone() {
        return new miRNAExpressionWidget(cType);
    }

}
