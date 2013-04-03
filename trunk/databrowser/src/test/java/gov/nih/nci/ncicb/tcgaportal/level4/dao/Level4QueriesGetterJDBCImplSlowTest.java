/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 * Last updated by: $Author$
 * @version $Rev$
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataSourceMaker;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for Level4Queries getter
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesGetterJDBCImplSlowTest {
    private Mockery context = new JUnit4Mockery();

    @Test
    public void test() {
        final DataSourceMaker mockDataSourceMaker = context.mock(DataSourceMaker.class);
        context.checking(new Expectations() {{
            one(mockDataSourceMaker).makeDataSource("hi");
            will(returnValue(null));
            one(mockDataSourceMaker).makeDataSource("bye");
            will(returnValue(null));
        }});
        final Level4QueriesGetter level4QueriesGetter =
                new Level4QueriesGetterJDBCImpl(100, null, null, new ProcessLogger(), mockDataSourceMaker, "DIS1:hi,DIS2:bye");
        assertEquals(2, level4QueriesGetter.getDiseaseNames().size());
        assertTrue(level4QueriesGetter.getDiseaseNames().contains("DIS1"));
        assertTrue(level4QueriesGetter.getDiseaseNames().contains("DIS2"));
    }
}
