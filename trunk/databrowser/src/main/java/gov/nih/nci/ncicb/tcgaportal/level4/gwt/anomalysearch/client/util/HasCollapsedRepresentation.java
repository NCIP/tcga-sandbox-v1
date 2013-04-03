/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.ui.Widget;

/**
 * A widget such as the Filter Panel, which has another widget as its collapsed
 * representation.  This interface allows the CloseablePanel to obtain that representation
 * without knowing specifically about the Filter Panel.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface HasCollapsedRepresentation {

    Widget getCollapsedRepresentation();

    String getExpansionLinkText();

    void expand();

    void collapse();

}
