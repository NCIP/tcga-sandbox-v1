/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for DataSourceMakerUtil
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DataSourceMakerUtilFastTest {
    private Mockery context = new JUnit4Mockery();
    private DataSourceMaker mockDataSourceMaker;

    @Before
    public void setup() {
        mockDataSourceMaker = context.mock(DataSourceMaker.class);
    }

    @Test
    public void testMakeDataSources() {
        context.checking(new Expectations() {{
            one(mockDataSourceMaker).makeDataSource("prop1");
            one(mockDataSourceMaker).makeDataSource("prop2");
            one(mockDataSourceMaker).makeDataSource("prop3");            
            one(mockDataSourceMaker).makeDataSource("propKey:propVal");
        }});
        Map<Object, Object> dataSources = DataSourceMakerUtil.makeDataSources(mockDataSourceMaker,
                "dis1:prop1,dis2:prop2,dis3:prop3,dis4:propKey:propVal");
        assertEquals(4, dataSources.size());
        assertTrue(dataSources.keySet().contains("dis1"));
        assertTrue(dataSources.keySet().contains("dis2"));
        assertTrue(dataSources.keySet().contains("dis3"));
        assertTrue(dataSources.keySet().contains("dis4"));
    }
}
