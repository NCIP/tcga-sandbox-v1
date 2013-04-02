/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human 
 * readable source code form and machine readable, binary, object code form (the "caBIG 
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.annotations.security.AclSecurityController;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller to provide ACL related information to the model.
 * This implementation focuses on ojbects that have to be secured in the Annotation application.
 * 
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class AclSecurityControllerImpl implements AclSecurityController {

    @Autowired
    private AclSecurityUtil aclSecurityUtil;

    /**
     * Add a <code>hasPermission</code> attribute to the model, the value being determined as follow:
     *
     * If the <code>recipient</code> has the permission to access the given object instance
     * (characterized by <code>objectClass</code> and <code>objectId</code>) according to the specified
     * <code>aclVoterName</code> then it will be <code>true</code>, otherwise it will be <code>false</code>.
     *
     * In the event that an exception was raised, the value for the <code>hasPermission</code> attribute is
     * <code>false</code>, and a <code>exception</code> attribute is added to the model with a value of
     * <code>true</code>.
     *
     * NOTE: this implementation is limited to the case where:
     *  - the object class is <code>DccAnnotationNote</code>
     *  - the <code>aclVoterName</code> is <code>ACL_ANNOTATION_NOTE_EDITOR</code>
     *
     * The implementation will need to be changed to accommodate general cases.
     *
     * @param model
     * @param aclVoterName
     * @param objectClass
     * @param objectId
     * @param recipient
     * @return the model with attributes as described above
     */
    @Override
    @RequestMapping(value = "/acl.securityjson", method = RequestMethod.GET)
    public ModelMap handleRequest(
            final ModelMap model,
            @RequestParam(value="aclVoterName") final String aclVoterName,
            @RequestParam(value="objectClass") final String objectClass,
            @RequestParam(value="objectId") final Long objectId,
            @RequestParam(value="recipient") final String recipient
    ) {
        try {
            boolean hasPermission = false;

            if("ACL_ANNOTATION_NOTE_EDITOR".equals(aclVoterName) && "DccAnnotationNote".equals(objectClass)) {
                hasPermission = aclSecurityUtil.hasWritePermission(objectId, recipient);
            }

            model.addAttribute("hasPermission", hasPermission);

        } catch(AnnotationQueries.AnnotationQueriesException x) {

            model.addAttribute("hasPermission", false);
            model.addAttribute("exception", true);
        }

        return model;
    }

    /**
     * Only for test :-/
     *
     * @param aclSecurityUtil
     */
    public void setAclSecurityUtil(final AclSecurityUtil aclSecurityUtil) {
        this.aclSecurityUtil = aclSecurityUtil;
    }
}
