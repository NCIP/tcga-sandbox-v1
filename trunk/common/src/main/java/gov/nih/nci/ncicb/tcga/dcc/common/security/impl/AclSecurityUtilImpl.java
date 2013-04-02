/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.DccAnnotationNoteRetrievalStrategy;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple wrapper around ACL-based security operations, to be used by services that create a DccAnnotationNote object
 * so as to grant the appropriate Permission on the new DccAnnotationNote, according to the application needs.
 * <p/>
 * This implementation of <code>AclSecurityUtil</code> uses <code>MutableAclService</code> and <code>SecurityUtil</code>
 * from the application context.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AclSecurityUtilImpl implements AclSecurityUtil {

    private static Log logger = LogFactory.getLog(AclSecurityUtilImpl.class);

    @Autowired
    private MutableAclService mutableAclService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private AnnotationQueries annotationQueries;

    @Override
    public void setMutableAclService(final MutableAclService mutableAclService) {
        this.mutableAclService = mutableAclService;
    }

    @Override
    public void setAnnotationQueries(final AnnotationQueries annotationQueries) {
        this.annotationQueries = annotationQueries;
    }

    @Override
    @Transactional
    public void addPermission(final DccAnnotationNote dccAnnotationNote, final Permission permission) {

        final Sid recipient = new PrincipalSid(securityUtil.getAuthenticatedPrincipalLoginName());
        addPermission(dccAnnotationNote, recipient, permission);
    }

    @Override
    @Transactional
    public void addPermission(final DccAnnotationNote dccAnnotationNote,
                              final Sid recipient,
                              final Permission permission) {

        // Prepare the information to be put in the access control entry (ACE)
        final ObjectIdentity objectIdentity = new DccAnnotationNoteRetrievalStrategy()
                .getObjectIdentity(dccAnnotationNote);

        // Create or update the relevant ACL
        MutableAcl acl;
        try {
            acl = (MutableAcl) mutableAclService.readAclById(objectIdentity);

        } catch (NotFoundException nfe) {
            acl = mutableAclService.createAcl(objectIdentity);
        }

        final boolean granting = true;
        try {
            acl.insertAce(acl.getEntries().length, permission, recipient, granting);
        } catch (NotFoundException nfe) {
            logger.debug("Could not insert ACE [recipient:" + recipient + ", with permission:" + permission + ", granting:" + granting + "] (NotFoundException)");
        }
        mutableAclService.updateAcl(acl);
    }

    @Override
    @Transactional
    public boolean hasPermission(final DccAnnotationNote dccAnnotationNote,
                                 final Sid recipient,
                                 final Permission permission) {

        // Retrieve the Object Identity
        final ObjectIdentity objectIdentity = new DccAnnotationNoteRetrievalStrategy()
                .getObjectIdentity(dccAnnotationNote);

        // Retrieve the relevant ACL
        MutableAcl acl;
        try {
            Sid[] sidArray = {recipient};
            acl = (MutableAcl) mutableAclService.readAclById(objectIdentity, sidArray);

            AccessControlEntry[] accessControlEntries = acl.getEntries();
            for (final AccessControlEntry accessControlEntry : accessControlEntries) {

                if (accessControlEntry.getPermission().getMask() == permission.getMask()) {

                    //The recipient has the permission
                    return true;
                }
            }

        } catch (NotFoundException nfe) {
            logger.debug("Could not find ACL for DccAnnotationNote with Id " + dccAnnotationNote.getNoteId() + " (NotFoundException)");
        }

        return false;
    }

    @Override
    @Transactional
    public boolean hasPermission(final Long dccAnnotationNoteId,
                                 final String recipient,
                                 final Permission permission) throws AnnotationQueries.AnnotationQueriesException {

        boolean result = false;

        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
        if (dccAnnotationNote != null) {
            //Update result
            result = hasPermission(dccAnnotationNote, new PrincipalSid(recipient), permission);
        } else {
            logger.debug("Could not find DccAnnotationNote with Id " + dccAnnotationNoteId);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean hasWritePermission(final Long dccAnnotationNoteId,
                                      final String recipient) throws AnnotationQueries.AnnotationQueriesException {
        return hasPermission(dccAnnotationNoteId, recipient, BasePermission.WRITE);
    }
}
