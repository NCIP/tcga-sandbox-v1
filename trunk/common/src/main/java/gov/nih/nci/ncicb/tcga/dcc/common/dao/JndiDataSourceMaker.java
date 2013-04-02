/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

/**
 * Makes a DataSource using a JNDI name.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class JndiDataSourceMaker implements DataSourceMaker {
    private JndiDataSourceLookup jndiDataSourceLookup;
    private String prefix;

    public DataSource makeDataSource(final String jndiName) {
        String nameToLookup = prefix == null ? jndiName : prefix + jndiName;
        return jndiDataSourceLookup.getDataSource(nameToLookup);
    }

    public void setJndiDataSourceLookup(final JndiDataSourceLookup jndiDataSourceLookup) {
        this.jndiDataSourceLookup = jndiDataSourceLookup;
    }

    /**
     * If set, this prefix will be prepended to the jndiName passed into makeDataSource before the lookup is done.
     * (For example, prefix can be "java:").
     *
     * @param prefix the prefix to prepend to the jndi name passed in
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
