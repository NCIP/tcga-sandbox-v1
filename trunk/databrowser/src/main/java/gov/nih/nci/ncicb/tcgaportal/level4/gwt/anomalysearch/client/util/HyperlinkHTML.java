/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.ui.HTML;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * Trivial class to extend HTML and add a target token, so it can be used as a hyperlink.
 * We need this so we can also use addMouseListener for tooltips (which the native Hyperlink doesn't support)
 *
 * @author David Nassau
 * @version $Rev$
 */
public class HyperlinkHTML extends HTML {

    private String targetHistoryToken;
    
    private ColumnType column;

    public HyperlinkHTML(String text, String targetHistoryToken, ColumnType pivotColumn) {
        super(text);
        this.targetHistoryToken = targetHistoryToken;
        this.column = pivotColumn;
        addStyleName("action");
    }

    public String getTargetHistoryToken() {
        return targetHistoryToken;
    }

    public ColumnType getColumn() {
        return column;
    }

}
