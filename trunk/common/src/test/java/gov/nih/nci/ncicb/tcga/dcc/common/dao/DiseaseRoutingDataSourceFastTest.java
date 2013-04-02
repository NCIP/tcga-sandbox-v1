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
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Test the DiseaseRoutingDataSource. We want to make sure that it gives a connection specific to a
 * disease depending on a thread-local value set in DiseaseContextHolder
 *
 * @author David Nassau
 * @version $Rev$
 */
@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
@RunWith(JMock.class)
public class DiseaseRoutingDataSourceFastTest {

    private DiseaseRoutingDataSource routingDS;
    private Mockery mocker = new JUnit4Mockery();  //neither mod nor rocker
    private DataSource mockDS_gbm = mocker.mock(DataSource.class, "ds1");
    private DataSource mockDS_ov = mocker.mock(DataSource.class, "ds2");
    private Connection mockConnection_gbm = mocker.mock(Connection.class, "conn1");
    private Connection mockConnection_ov = mocker.mock(Connection.class, "conn2");

    @Before
    public void setup() {
        mocker.checking(new Expectations() {{
            try {
                allowing(mockDS_gbm).getConnection();
                will(returnValue(mockConnection_gbm));
                allowing(mockDS_ov).getConnection();
                will(returnValue(mockConnection_ov));
            } catch (SQLException e) {
                fail();
            }
        }});

        DataSourceMaker dataSourceMaker = new DataSourceMaker() {
            public DataSource makeDataSource(final String propertyString) {
                if (propertyString.equals("ds1")) {
                    return mockDS_gbm;
                } else if (propertyString.equals("ds2")) {
                    return mockDS_ov;
                } else {
                    return null;
                }
            }
        };

        routingDS = new DiseaseRoutingDataSource(dataSourceMaker, "GBM:ds1,OV:ds2");
        routingDS.afterPropertiesSet();
    }

    @Test
    public void testResolveDS() throws SQLException {
        DiseaseContextHolder.setDisease("GBM"); //thread-bound data
        Connection conn = routingDS.getConnection();
        assertSame(mockConnection_gbm, conn);

        DiseaseContextHolder.setDisease("OV"); //thread-bound data
        conn = routingDS.getConnection();
        assertSame(mockConnection_ov, conn);
    }
}
