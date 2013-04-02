/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * This bean subclasses Spring's SchedulerFactoryBean, but has an extra parameter
 * for whether or not to use the supplied data source.  Helpful for when local deployments
 * shouldn't use database persistence but others should.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class SchedulerFactoryBeanWithDataSource extends SchedulerFactoryBean {
    private final boolean useDataSource;

    public SchedulerFactoryBeanWithDataSource(boolean useDataSource) {
        this.useDataSource = useDataSource;
    }

    @Override
    public void setDataSource(final DataSource dataSource) {
        if (useDataSource) {
            super.setDataSource(dataSource);
        }
    }    
}
