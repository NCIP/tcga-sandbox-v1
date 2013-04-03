/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.webservice;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for the participant web service resource class
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ParticipantWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private ParticipantWS webservice;
    private Level4QueriesGetter l4QueriesGetter;
    private Level4Queries l4Queries;
    private CopyNumberType cnt = new CopyNumberType();
    private List<ColumnType> cList = new ArrayList<ColumnType>();
    private Results res = new Results();
    private ResultRow rr = new ResultRow();

    @Before
    public void setup() throws Exception {
        cnt.setDisplayCenter("broad.mit.edu");
        cnt.setDisplayPlatform("Genome_Wide_SNP_6");
        cList.add(cnt);
        res.addRow(rr);
        rr.setName("TCGA-01-2345");
        webservice = new ParticipantWS();
        l4QueriesGetter = context.mock(Level4QueriesGetter.class);
        l4Queries = context.mock(Level4Queries.class);
        Field l4QueriesGetterField = webservice.getClass().getDeclaredField("l4QueriesGetter");
        l4QueriesGetterField.setAccessible(true);
        l4QueriesGetterField.set(webservice, l4QueriesGetter);
        webservice.disease = "GBM";
        webservice.genes = "CDK4";
        context.checking(new Expectations() {{
           allowing(l4QueriesGetter).getLevel4Queries(webservice.disease);
           will(returnValue(l4Queries));
           allowing(l4Queries).getColumnTypes(webservice.disease);
           will(returnValue(cList));
           allowing(l4Queries).getAnomalyResults(with(any(FilterSpecifier.class)));
           will(returnValue(res));
        }});
    }
    
    @Test
    public void testParticipantList() throws Exception{
        webservice.frequency = 20f;
        webservice.lowerLimit = -0.5;
        webservice.upperLimit = 0.5;
        String res = webservice.getParticipantList();
        assertNotNull(res);
        assertEquals("Participant\nTCGA-01-2345",res);
    }

    @Test
    public void testParticipantListJSON() throws Exception{
        webservice.frequency = 20f;
        webservice.lowerLimit = -0.5;
        webservice.upperLimit = 0.5;
        String res = webservice.getParticipantListToJSON();
        assertNotNull(res);
        assertEquals("{\"participant\":[{\"barcode\":\"TCGA-01-2345\"}]}",res);
    }

    @Test(expected= WebApplicationException.class)
    public void testBadFrequency() throws Exception {
        webservice.frequency = 120f;
        webservice.lowerLimit = -0.5;
        webservice.upperLimit = 0.5;
        webservice.getParticipantList();
    }

    @Test(expected= WebApplicationException.class)
    public void testBadLowLimit() throws Exception {
        webservice.frequency = 20f;
        webservice.lowerLimit = null;
        webservice.upperLimit = 0.5;
        webservice.getParticipantList();
    }

    @Test(expected= WebApplicationException.class)
    public void testBadUpLimit() throws Exception {
        webservice.frequency = 20f;
        webservice.lowerLimit = -0.5;
        webservice.upperLimit = null;
        webservice.getParticipantList();
    }

    @Test(expected= WebApplicationException.class)
    public void testNullFrequency() throws Exception {
        webservice.frequency = null;
        webservice.lowerLimit = -.5;
        webservice.upperLimit = 0.5;
        webservice.getParticipantList();
    }

}//End of Class
