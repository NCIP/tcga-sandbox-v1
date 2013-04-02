/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web.editor;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

/**
 * Custom property editor definition
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class DAMBindingInitializer implements WebBindingInitializer {

    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(UUID.class, new UUIDEditor());
    }

}//End of Class
