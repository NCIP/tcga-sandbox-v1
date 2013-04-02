/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

/**
 * Make a data source object based on a property string. Spring data source maker
 * looks up a connection string in Spring configuration. This class is to be used in standalone
 * applications where jndi is not available.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SpringDataSourceMaker implements DataSourceMaker, ApplicationContextAware {

    private static ApplicationContext context = null;

    /**
     * Returns a data source for a given disease
     *
     * @param dataSourceToLookup
     * @return disease specific data source
     */
    @Override
    public DataSource makeDataSource(String dataSourceToLookup) {
        DataSource returnDataSource = null;

        Object bean = context.getBean(dataSourceToLookup);
        if (bean instanceof DataSource) {
            returnDataSource = (DataSource) bean;
        }
        return returnDataSource;
    }

    /**
     * Used by Spring to set Application Context reference on the datasource maker
     *
     * @param ctx ApplicationContext
     * @throws BeansException
     */
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

}
