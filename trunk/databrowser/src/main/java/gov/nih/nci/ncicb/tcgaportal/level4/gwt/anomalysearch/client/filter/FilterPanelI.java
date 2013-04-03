/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchServiceAsync;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;


/**
 * One-off interface for the FilterPanel.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
//todo  does this interface really need to exist? Why not just use the FilterPanel type itself?
public interface FilterPanelI {

    //used by main factory
    void init();

    void setAnomalySearchService(AnomalySearchServiceAsync searchService, String selectedDiease);

    void showError(String message, boolean errorPrefix);

    void clearError();

    void setSearchButtonText(String buttonText);

    void startSearchSpinner();

    void stopSearchSpinner();

    FilterSpecifier makeFilterSpecifier() throws FilterPanel.FilterPanelException;

    void addToGeneList(String genes);

    void addToPatientList(String patients);

    void copyFrom(FilterPanelI filterPanelI);

    boolean keepInSync();

    public void enableTooltips(boolean enable);

    void setKeepInSync(boolean b);

    public void searchStarted(Widget collapsedRepresentation);

}
