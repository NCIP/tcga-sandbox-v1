/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

/**
 * Interface for any widget which serves as a tab in the ResultsTabPanel
 */
public interface ResultsPanel {

    /**
     * @return Text to be displayed in the tab
     */
    String getTabText();

    /**
     * @return Unique ID to be used for Tab
     */
    String getTabId();
    
}
