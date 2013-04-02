/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.sid.Sid;

/**
 * Simple wrapper around ACL-based security operations, to be used by services that create a DccAnnotationNote object
 * so as to grant the appropriate Permission on the new DccAnnotationNote, according to the application needs.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AclSecurityUtil {

    /**
     * Add the permission level to be granted to the current authenticated user when accessing the given DccAnnotationNote
     *
     * @param dccAnnotationNote the DccAnnotationNote object on which to grant the permission
     * @param permission the permission to be granted on the DccAnnotationNote object
     */
    public void addPermission(DccAnnotationNote dccAnnotationNote, Permission permission);

    /**
     * Add the permission level to be granted to the recipient when accessing the given DccAnnotationNote
     *
     * @param dccAnnotationNote the DccAnnotationNote object on which to grant the permission
     * @param recipient the user for which the permission is granted
     * @param permission the permission to be granted on the DccAnnotationNote object
     */
    public void addPermission(DccAnnotationNote dccAnnotationNote, Sid recipient, Permission permission);

    /**
     * Return <code>true</code> if the recipient has the given permission on the DccAnnotationNote,
     * <code>false</code> otherwise.
     *
     * @param dccAnnotationNote the DccAnnotationNote object on which the permission is verified
     * @param recipient the user for which the permission is granted
     * @param permission the permission granted on the DccAnnotationNote object
     * @return <code>true</code> if the recipient has the given permission on the DccAnnotationNote, <code>false</code> otherwise
     */
    public boolean hasPermission(final DccAnnotationNote dccAnnotationNote,
                                 final Sid recipient,
                                 final Permission permission);

    /**
     * Return <code>true</code> if the recipient has the given permission on the DccAnnotationNote with Id <code>dccAnnotationNoteId</code>,
     * <code>false</code> otherwise.
     *
     * @param dccAnnotationNoteId the id of the DccAnnotationNote object on which the permission is verified
     * @param recipient the user for which the permission is granted
     * @param permission the permission granted on the DccAnnotationNote object
     * @return <code>true</code> if the recipient has the given permission on the DccAnnotationNote with Id <code>dccAnnotationNoteId</code>, <code>false</code> otherwise
     * @throws AnnotationQueries.AnnotationQueriesException
     */
    public boolean hasPermission(final Long dccAnnotationNoteId,
                                 final String recipient,
                                 final Permission permission) throws AnnotationQueries.AnnotationQueriesException;

    /**
     * Return <code>true</code> if the recipient has 'BasePermission.WRITE' permission on the DccAnnotationNote with Id <code>dccAnnotationNoteId</code>,
     * <code>false</code> otherwise.
     *
     * @param dccAnnotationNoteId the id of the DccAnnotationNote object on which the permission is verified
     * @param recipient the user for which the permission is granted
     * @return <code>true</code> if the recipient has 'BasePermission.WRITE' permission on the DccAnnotationNote with Id <code>dccAnnotationNoteId</code>, <code>false</code> otherwise
     * @throws AnnotationQueries.AnnotationQueriesException
     */
    public boolean hasWritePermission(final Long dccAnnotationNoteId,
                                      final String recipient) throws AnnotationQueries.AnnotationQueriesException;

    /**
     * Setter for the unit tests only
     *
     * @param mutableAclService
     */
    public void setMutableAclService(final MutableAclService mutableAclService);

    /**
     * Setter for the unit tests only
     * 
     * @param annotationQueries
     */
    public void setAnnotationQueries(final AnnotationQueries annotationQueries);

}
