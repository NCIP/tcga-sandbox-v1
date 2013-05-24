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

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

/**
 * This class is necessary to overwrite Spring Security default getId() call
 * on the bean for which it is trying to build an ObjectIdentity.
 *
 * @author Julien Baboud
 */
public class DccAnnotationNoteRetrievalStrategy implements ObjectIdentityRetrievalStrategy {

    /**
     * @param domainObject the object out of which an ObjectIdentity is built for Spring Security
     * @return the ObjectIdentity
     */
    public ObjectIdentity getObjectIdentity(final Object domainObject) {
        DccAnnotationNote dccAnnotationNote = (DccAnnotationNote)domainObject;
        return new ObjectIdentityImpl(DccAnnotationNote.class, dccAnnotationNote.getNoteId());
    }
}
