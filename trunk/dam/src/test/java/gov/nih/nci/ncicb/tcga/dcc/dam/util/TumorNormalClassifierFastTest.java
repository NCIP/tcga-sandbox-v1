/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for TumorNormalClassifier
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TumorNormalClassifierFastTest {
    private Mockery context; // IDEA says this can be converted to local variable, but it lies!
    private TumorNormalClassifier classifier;
    private SampleTypeQueries mockSampleTypeQueries;
    private TissueSourceSiteQueries mockTissueSourceSiteQueries;

    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockSampleTypeQueries = context.mock(SampleTypeQueries.class);
        classifier = new TumorNormalClassifier();
        classifier.setSampleTypeQueries(mockSampleTypeQueries);

        mockTissueSourceSiteQueries = context.mock(TissueSourceSiteQueries.class);
        classifier.setTissueSourceSiteQueries(mockTissueSourceSiteQueries);

        final List<SampleType> sampleTypes = new ArrayList<SampleType>();
        addSampleType(sampleTypes, "01", true);
        addSampleType(sampleTypes, "11", false);
        addSampleType(sampleTypes, "10", false);
        addSampleType(sampleTypes, "20", false);

        final List<String> controlTssCodes = new ArrayList<String>();
        controlTssCodes.add("AV");

        context.checking(new Expectations() {{
            allowing(mockSampleTypeQueries).getAllSampleTypes();
            will(returnValue(sampleTypes));

            allowing(mockTissueSourceSiteQueries).getControlTssCodes();
            will(returnValue(controlTssCodes));
        }});
    }

    private void addSampleType(final List<SampleType> sampleTypes, final String type, final boolean isTumor) {
        SampleType sampleType = new SampleType();
        sampleType.setSampleTypeCode(type);
        sampleType.setDefinition(type);
        sampleType.setIsTumor(isTumor);
        sampleTypes.add(sampleType);
    }

    @Test
    public void testMatchedTumor() {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        // tumor with match
        DataSet tumorNormal = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-01-0001-01");
        dataSets.add( tumorNormal );
        // normal that matches above tumor
        DataSet normalTumor = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-01-0001-11" );
        dataSets.add( normalTumor );
        // tumor with no match
        DataSet tumor = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-01-0002-01");
        dataSets.add( tumor );
        // normal with no match
        DataSet normal = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-01-0003-11" );
        dataSets.add( normal );
        // tumor with normal that isn't available
        DataSet tumorPendingNormal = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-01-0004-01" );
        dataSets.add( tumorPendingNormal );
        DataSet pendingNormal = makeDataSet( DataAccessMatrixQueries.AVAILABILITY_PENDING,
                "TCGA-01-0004-10");
        dataSets.add( pendingNormal );
        DataSet unmatchedControl = makeDataSet(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, "TCGA-AV-2222-20");
        dataSets.add(unmatchedControl);

        // normal with tumor that isn't available
        DataSet normalWithUnavailTumor = makeDataSet(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                "TCGA-AA-1234-11");
        dataSets.add(normalWithUnavailTumor);
        DataSet unavailTumor = makeDataSet(DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE,
                "TCGA-AA-1234-01");
        dataSets.add(unavailTumor);

        classifier.classifyTumorNormal(dataSets);
        assertEquals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, tumorNormal.getTumorNormal() );
        assertEquals( DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, normalTumor.getTumorNormal() );
        assertEquals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL, tumor.getTumorNormal() );
        assertEquals( DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL, normal.getTumorNormal() );
        assertEquals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, tumorPendingNormal.getTumorNormal() );
        assertEquals(DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL, unmatchedControl.getTumorNormal());
        assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, normalWithUnavailTumor.getTumorNormal());

        assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, pendingNormal.getTumorNormal());
        assertEquals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, unavailTumor.getTumorNormal());
    }

    @Test
    public void testUnknownSampleType() {
        DataSet unknownTypeDS = makeDataSet(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, "TCGA-11-2222-99");
        try {
            classifier.classifyTumorNormal(Arrays.asList(unknownTypeDS));
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Sample type 99 is unknown", e.getMessage());
        }        
    }

    private DataSet makeDataSet( final String availability, final String sample ) {
        DataSet ds = new DataSet();
        ds.setAvailability( availability );
        ds.setSample( sample );
        
        return ds;
    }
}
