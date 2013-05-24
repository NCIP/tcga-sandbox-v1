/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import javax.sql.DataSource;

import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;

/**
 * This is just like the Spring JdbcMutableAclService except when you call updateAcl, the object identity is not updated.
 * That means changes to the object owner or parent are ignored.  This is because we are encountering a weird error
 * when trying to update object identity, and we don't actually use that functionality anyway.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SimpleJdbcMutableAclService extends JdbcMutableAclService {
    public SimpleJdbcMutableAclService(final DataSource dataSource, final LookupStrategy lookupStrategy, final AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    @Override
    protected void updateObjectIdentity(final MutableAcl acl) {
        // do nothing, we don't allow updates to object's owner, etc (is causing an error and we don't update these
        // things anyway)
    }
}
