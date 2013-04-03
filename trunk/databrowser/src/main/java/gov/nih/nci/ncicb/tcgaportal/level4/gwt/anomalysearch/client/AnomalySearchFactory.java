/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client;

/**
    One-off interface implemented by AnomalySearch.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
//todo  is needed?  Why not reference AnomalySearch directly?
public interface AnomalySearchFactory {

    void createApplication();

    void filterPanelCallback();

    boolean areTooltipsEnabled();
}
