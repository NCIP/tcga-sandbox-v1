/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.web.AbstractSpringAwareGrizzlyJerseyTest;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for the {@link ShippedBiospecimenWSQueriesImpl} class.
 *
 * @author nichollsmc
 */
public class ShippedBiospecimenWSQueriesImplSlowTest extends AbstractSpringAwareGrizzlyJerseyTest {

    @Autowired
    private ShippedBiospecimenQueries shippedBiospecimenQueries;

    private ShippedBiospecimenWSQueriesImpl shippedBiospcimenWSQueries = null;
    private QcContext qcContext = null;

    public ShippedBiospecimenWSQueriesImplSlowTest() throws Exception {
        super(new WebAppDescriptor.Builder("gov.nih.nci.ncicb.tcga.dcc.uuid")
                .initParam(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString())
                .contextParam("contextConfigLocation", "classpath*:conf/applicationContext-ws-test.xml")
                .servletClass(SpringServlet.class)
                .contextListenerClass(ContextLoaderListener.class).build());
    }

    @Before
    public void before() {
        shippedBiospcimenWSQueries = new ShippedBiospecimenWSQueriesImpl();
        shippedBiospcimenWSQueries.setBaseUUIDMetaDataURI("http://localhost:9998/metadata");
        shippedBiospcimenWSQueries.setUseRemoteService(true);
        qcContext = new QcContext();
        shippedBiospcimenWSQueries.setQcContext(qcContext);
    }

    @Test
    public void shouldReturnNull() {
        final MetaDataBean metaDataBean = shippedBiospcimenWSQueries.retrieveUUIDMetadata("this-is-an-invalid-uuid");

        assertTrue(qcContext.getErrorCount() == 0);
        assertNull(metaDataBean);
    }

    @Test
    public void shouldNotUseRemoteService() {
        shippedBiospcimenWSQueries.setUseRemoteService(false);
        final MetaDataBean metaDataBean = shippedBiospcimenWSQueries.retrieveUUIDMetadata("12345678-1234-1234-1234-abcdefabcdef");

        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
        assertNotNull(metaDataBean);
    }

    @Test
    public void shouldNotUseRemoteServiceInvalidUUID() {
        shippedBiospcimenWSQueries.setUseRemoteService(false);
        final MetaDataBean metaDataBean = shippedBiospcimenWSQueries.retrieveUUIDMetadata("this is invalid for a UUID");

        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
        assertNull(metaDataBean);
    }

    @Test
    public void shouldReturnMetaDataForUUID() {
        final String uuid = "5225092a-8037-485a-bcdb-e0605272acdc";
        final MetaDataBean uuidMetaDataBean = new MetaDataBean() {{
            setUUID(uuid);
            setParticipantCode("0001");
            setTssCode("02");
            setSampleCode("01");
        }};

        Mockito.when(
                shippedBiospecimenQueries.retrieveUUIDMetadata(uuid))
                .thenReturn(uuidMetaDataBean);

        final MetaDataBean result = shippedBiospcimenWSQueries.retrieveUUIDMetadata("5225092a-8037-485a-bcdb-e0605272acdc");

        assertNotNull(result);
        assertEquals(uuid, result.getUUID());
    }

    @Test
    public void testGetUUIDLevel() throws Exception {
        final String uuid = "5225092a-8037-485a-bcdb-e0605272acdc";
        Mockito.when(shippedBiospecimenQueries.getUUIDLevel(uuid))
                .thenReturn("Aliquot");
        final String res = shippedBiospcimenWSQueries.getUUIDLevel(uuid);
        assertNotNull(res);
        assertEquals("Aliquot", res);
    }

    @Test
    public void testGetUUIDLevelBad() throws Exception {
        final String uuid = "bad";
        final String res = shippedBiospcimenWSQueries.getUUIDLevel(uuid);
        assertNull(res);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testGetUUIDDisease() throws Exception {
        final String uuid = "5225092a-8037-485a-bcdb-e0605272acdc";
        Mockito.when(shippedBiospecimenQueries.getDiseaseForUUID(uuid))
                .thenReturn("GBM");
        final String res = shippedBiospcimenWSQueries.getDiseaseForUUID(uuid);
        assertNotNull(res);
        assertEquals("GBM", res);
    }

    @Test
    public void testGetUppercaseUUIDDisease() {
        Mockito.when(shippedBiospecimenQueries.getDiseaseForUUID("5225092a-8037-485a-bcdb-e0605272acdc")).thenReturn("HI");
        final String disease = shippedBiospcimenWSQueries.getDiseaseForUUID("5225092A-8037-485A-BCDB-E0605272ACDC");
        Assert.assertEquals("HI", disease);
    }

    @Test
    public void testGetUUIDDiseaseBad() throws Exception {
        final String uuid = "bad";
        final String res = shippedBiospcimenWSQueries.getDiseaseForUUID(uuid);
        assertNull(res);
        assertEquals(0, qcContext.getErrorCount());
    }
}
