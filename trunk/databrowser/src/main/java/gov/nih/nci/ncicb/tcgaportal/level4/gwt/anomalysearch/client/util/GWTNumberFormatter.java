/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.i18n.client.NumberFormat;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.NumberFormatter;

/**
 * Number formatter to be used in GWT client
 *
 * @author David Nassau
 * @version $Rev$
 */
public class GWTNumberFormatter implements NumberFormatter {

    private NumberFormat numberFormat;

    public GWTNumberFormatter(String pattern) {
        numberFormat = NumberFormat.getFormat(pattern);
    }

    public String format(double d) {
        return numberFormat.format(d);
    }
}
