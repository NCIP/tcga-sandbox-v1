/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditorSupport;
import java.util.UUID;

/**
 * Spring Editor class to convert UUID from http requests
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UUIDEditor extends PropertyEditorSupport {

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public String getAsText() {
        UUID u = (UUID) getValue();
        logger.debug("Get Converted UUID: "+u);
        return u.toString();
    }

    @Override
    public void setAsText(String s) {
        logger.debug("Set converted UUID: "+s);
        setValue(UUID.fromString(s));
    }

}//End of Class
