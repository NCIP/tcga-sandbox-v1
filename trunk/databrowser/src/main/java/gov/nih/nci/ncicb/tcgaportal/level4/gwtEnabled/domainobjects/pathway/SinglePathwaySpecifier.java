/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;

/**
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class SinglePathwaySpecifier implements IsSerializable {
    private String id;
    private FilterSpecifier filterSpecifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FilterSpecifier getFilterSpecifier() {
        return filterSpecifier;
    }

    public void setFilterSpecifier(FilterSpecifier filterSpecifier) {
        this.filterSpecifier = filterSpecifier;
    }
}
