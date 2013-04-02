/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TissueSourceSiteQueries;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for BarcodeTumorValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class BarcodeTumorValidatorFastTest {
    private Mockery context = new JUnit4Mockery();
    private BarcodeTumorValidatorImpl barcodeTumorValidator;
    private BCRIDProcessor mockBCRIDProcessor = context.mock(BCRIDProcessor.class);
    private TissueSourceSiteQueries mockTissueSourceSiteQueries = context.mock(TissueSourceSiteQueries.class);

    @Before
    public void setup() {
        barcodeTumorValidator = new BarcodeTumorValidatorImpl();
        barcodeTumorValidator.setBcrIdProcessor(mockBCRIDProcessor);
        barcodeTumorValidator.setTissueSourceSiteQueries(mockTissueSourceSiteQueries);
    }

    @Test
    public void testBarcodeIsValidForTumor() throws Processor.ProcessorException, ParseException {
        final BCRID barcode1 = new BCRID();
        barcode1.setSiteID("00");
        final BCRID barcode2 = new BCRID();
        barcode2.setSiteID("11");
        final List<String> diseaseList1 = new ArrayList<String>();
        diseaseList1.add("tumor");
        final List<String> diseaseList2 = new ArrayList<String>();
        diseaseList2.add("bad");

        context.checking(new Expectations() {{
            one(mockBCRIDProcessor).parseAliquotBarcode("barcode1");
            will(returnValue(barcode1));
            one(mockTissueSourceSiteQueries).getDiseasesForTissueSourceSiteCode("00");
            will(returnValue(diseaseList1));

            one(mockBCRIDProcessor).parseAliquotBarcode("barcode2");
            will(returnValue(barcode2));
            one(mockTissueSourceSiteQueries).getDiseasesForTissueSourceSiteCode("11");
            will(returnValue(diseaseList2));

        }});

        assertTrue(barcodeTumorValidator.barcodeIsValidForTumor("barcode1", "tumor"));
        assertFalse(barcodeTumorValidator.barcodeIsValidForTumor("barcode2", "tumor"));
    }
}
