/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class GeneExpressionWidget extends NonMutationAnomalyWidget {


    public GeneExpressionWidget(ColumnType cType) {
        super(cType);
    }

    protected AnomalyWidget instanceForClone() {
        return new GeneExpressionWidget(cType);
    }


}
