/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.export;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.NumberFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Number formatter to be used on server for export.
 *
 * @author David Nassau
 * @version $Rev$
 */
public class ExportNumberFormatter implements NumberFormatter {

    private NumberFormat numberFormat;

    public ExportNumberFormatter(String pattern) {
        numberFormat = new DecimalFormat(pattern);
    }

    public String format(double d) {
        return numberFormat.format(d);
    }
}
