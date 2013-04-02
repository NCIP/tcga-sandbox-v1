/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Class that defines an object containing a view and an extension for exports.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class ViewAndExtensionForExport {

    private String view;
    private String extension;

    public String getView() {
        return view;
    }

    public void setView(final String view) {
        this.view = view;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }
    
}//End of Class
