/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.annotations.security;

import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import org.springframework.ui.ModelMap;

/**
 * A controller to provide ACL related information to the model
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AclSecurityController {

    /**
     * Add a <code>hasPermission</code> attribute to the model, the value being determined as follow:
     *
     * If the <code>recipient</code> has the permission to access the given object instance
     * (caracterized by <code>objectClass</code> and <code>objectId</code>) according to the specified
     * <code>aclVoterName</code> then it will be <code>true</code>, otherwise it will be <code>false</code>.
     *
     * In the event that an exception was raised, the value for the <code>hasPermission</code> attribute is
     * <code>false</code>, and a <code>exception</code> attribute is added to the model with a value of
     * <code>true</code>.
     *
     * @param model
     * @param aclVoterName
     * @param objectClass
     * @param objectId
     * @param recipient
     * @return
     */
    public ModelMap handleRequest(final ModelMap model,
                                  final String aclVoterName,
                                  final String objectClass,
                                  final Long objectId,
                                  final String recipient);

    /**
     * Only for test :-/
     *
     * @param aclSecurityUtil
     */
    public void setAclSecurityUtil(AclSecurityUtil aclSecurityUtil);
}
