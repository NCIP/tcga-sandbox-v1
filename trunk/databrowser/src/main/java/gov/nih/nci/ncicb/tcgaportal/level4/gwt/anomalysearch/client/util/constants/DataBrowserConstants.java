/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.constants;

import com.google.gwt.i18n.client.Constants;

import java.io.Serializable;

/**
 * Constants looked up from properties file and used in the GWT user interface.
 * The DefaultStringValue annotations are set to empty string, which will force it to look
 * up the values from DataBrowserConstants.properties instead.  The annotations have to be there though.
 * This mechanism can allow us to localize the UI if we so choose.
 *
 * @author David Nassau
 * @version $Rev$
 */
public interface DataBrowserConstants extends Constants, Serializable {

    @DefaultStringValue("")
    String genes();

    @DefaultStringValue("")
    String patients();

    @DefaultStringValue("")
    String pathways();

    @DefaultStringValue("")
    String diseaseType();

    @DefaultStringValue("")
    String keepInSynchRB();

    @DefaultStringValue("")
    String allGenesRB();

    @DefaultStringValue("")
    String geneListRB();

    @DefaultStringValue("")
    String chromosomeRegionRB();

    @DefaultStringValue("")
    String allPatientsRB();

    @DefaultStringValue("")
    String patientListRB();
}
