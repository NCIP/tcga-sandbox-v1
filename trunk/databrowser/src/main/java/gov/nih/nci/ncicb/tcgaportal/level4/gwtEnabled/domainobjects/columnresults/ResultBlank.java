/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;

/**
 * Empty class indicates a blank to go in the results. Usually this means no patients were tested for a given anomaly.
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class ResultBlank extends ResultValue {

    public Object getSortableValue() {
        //sorting routines will make sure that nulls are sorted after non-nulls on the first click.
        return null;
    }

    public String toString() {
        return "";
    }
}
