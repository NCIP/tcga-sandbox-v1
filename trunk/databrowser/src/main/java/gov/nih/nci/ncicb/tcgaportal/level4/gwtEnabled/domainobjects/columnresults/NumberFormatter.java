/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;

/**
 * Interface for number formatters so that different implementations can be used
 * on GWT client and server
 *
 * @author David Nassau
 * @version $Rev$
 */
public interface NumberFormatter {

    String format(double d);

}
