/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.HTML;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SeleniumTags;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class MutationDisplay extends AnomalyDisplay {

    MutationDisplay(FilterPanel filterPanel) {
        super(filterPanel);
        WidgetHelper.setDomId(addAnomalyButton, SeleniumTags.ADDMUT_BUTTON);
        headingHtml = new HTML("Validated Somatic Mutations");
    }

    protected boolean shouldInclude(ColumnType ctype) {
        return ctype instanceof MutationType;
    }

    protected AnomalyWidget makeNewWidget(ColumnType selectedColumnType, boolean isListByGene) {
        return new MutationWidget((MutationType) selectedColumnType);
    }

}
