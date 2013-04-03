/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.ClickListener;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;

/**
 * ResultsPanel which can be used to copy gene/patient symbols to the current filter.
 *
 * @author David Nassau
 * @version $Rev$
 */
public interface ResultsPanelCanCopyToFilter extends ResultsPanel {

    /**
     * Adds listener used to copy genes/patients to filter pane.  May not be applicable in some implemeting classes.
     *
     * @param listener
     */
    void addCopyToFilterClickListener(ClickListener listener);

    /**
     * @return The current table which is rendered in the panel.
     */
    ResultsTable getResultsTable();

    /**
     * @return genes, patients, pathways or null
     */
    FilterSpecifier.ListBy getListBy();

}
