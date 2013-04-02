/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast test for DAMUtils.
 *
 * @author Jessica Chen
 *    Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DAMUtilsFastTest {

    private Mockery context = new JUnit4Mockery();
    private DAMUtils damUtils;
    private DAMDiseaseQueries mockDamDiseaseQueries;
    private PlatformQueries platformQueries;

    @Before
    public void setup() {

        mockDamDiseaseQueries = context.mock(DAMDiseaseQueries.class);
        platformQueries = context.mock(PlatformQueries.class);

        damUtils = DAMUtils.getInstance();
        damUtils.setDamDiseaseQueries(mockDamDiseaseQueries);
        damUtils.setPlatformQueries(platformQueries);
    }

    @Test
    public void testGetDiseases() {
        final List<Disease> diseases = new ArrayList<Disease>();
        context.checking(new Expectations() {{
            one(mockDamDiseaseQueries).getDiseases();
            will(returnValue(diseases));
        }});
        List<Disease> returnedDiseases = damUtils.getDiseases();
        assertEquals(diseases, returnedDiseases);
    }

    @Test
    public void testGetDisease() {
        final Disease dis = new Disease("dis", "Disease", true);
        context.checking(new Expectations() {{
            one(mockDamDiseaseQueries).getDisease("dis");
            will(returnValue(dis));
        }});
        Disease returnedDisease = damUtils.getDisease("dis");
        assertEquals(dis, returnedDisease);
    }

    @Test
    public void getPlatformWithAlias() {

        final String platformAlias = "platformAlias";
        final Platform expectedPlatform = new Platform();
        expectedPlatform.setPlatformAlias(platformAlias); //optional but let's pretend

        context.checking(new Expectations() {{
            one(platformQueries).getPlatformWithAlias("platformAlias");
            will(returnValue(expectedPlatform));
        }});

        final Platform actualPlatform = damUtils.getPlatformWithAlias(platformAlias);
        assertEquals(expectedPlatform, actualPlatform);
    }

    @Test
    public void testGetLevel3AllowedDataTypes() {
        // assuming post-constructor queries worked...
        DAMUtils.getInstance().setDataTypeId(DAMUtils.MIRNASEQ_QUANTIFICATION,"123");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.MIRNASEQ_ISOFORM,"456");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.MIRNASEQ, "789");

        final List<String> miRNADataTypes = damUtils.getLevel3AllowedDataTypes(DAMUtils.MIRNA_SEQ_TYPE);
        assertEquals(3, miRNADataTypes.size());
        assertEquals("123", DAMUtils.getInstance().getDataTypeId(DAMUtils.MIRNASEQ_QUANTIFICATION));
        assertEquals("456", DAMUtils.getInstance().getDataTypeId(DAMUtils.MIRNASEQ_ISOFORM));
        assertEquals("789", DAMUtils.getInstance().getDataTypeId(DAMUtils.MIRNASEQ));
    }

    @Test
    public void testGetLevel3RnaSeqDataTypes() {
        DAMUtils.getInstance().setDataTypeId(DAMUtils.RNA_SEQ_EXON,"1");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.RNA_SEQ_GENE,"2");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.RNA_SEQ_JUNCTION,"3");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.RNA_SEQ,"4");
        DAMUtils.getInstance().setDataTypeId(DAMUtils.RNA_SEQ_V2,"5");

        final List<String> RNADataTypes = damUtils.getLevel3AllowedDataTypes(DAMUtils.RNA_SEQ_TYPE);
        assertEquals(5, RNADataTypes.size());
        final List<String> expectedDataTypes = Arrays.asList("1","2","3","4","5");
        assertTrue(expectedDataTypes.containsAll(RNADataTypes));
    }

    @Test
    public void testGetLevel3ProteinDataType() {
        DAMUtils.getInstance().setDataTypeId(DAMUtils.PROTEIN_EXP, "15");

        final List<String> proteinDataTypes = damUtils.getLevel3AllowedDataTypes(DAMUtils.PROTEIN_EXP);
        assertEquals(1, proteinDataTypes.size());
        assertEquals("15", proteinDataTypes.get(0));
    }

    @Test
    public void testGroupDataSetsByDiseaseOnlyOneDisease() {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        DataSet ds1 = new DataSet(); ds1.setDiseaseType("foo");
        DataSet ds2 = new DataSet(); ds2.setDiseaseType("foo");
        dataSets.add(ds1); dataSets.add(ds2);
        Map<String, List<DataSet>> dataSetsGrouped = DAMUtils.getInstance().groupDataSetsByDisease(dataSets);
        assertNotNull(dataSetsGrouped);
        assertEquals(1, dataSetsGrouped.size());
        assertEquals("foo", dataSetsGrouped.keySet().toArray()[0]);
        assertEquals(2, dataSetsGrouped.get("foo").size());
    }

    @Test
    public void testGroupDataSetsByDiseaseTwoDiseases() {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        DataSet ds1 = new DataSet(); ds1.setDiseaseType("foo");
        DataSet ds2 = new DataSet(); ds2.setDiseaseType("moo");
        dataSets.add(ds1); dataSets.add(ds2);
        Map<String, List<DataSet>> dataSetsGrouped = DAMUtils.getInstance().groupDataSetsByDisease(dataSets);
        assertNotNull(dataSetsGrouped);
        assertEquals(2, dataSetsGrouped.size());
        assertEquals(1, dataSetsGrouped.get("foo").size());
        assertEquals(1, dataSetsGrouped.get("moo").size());
    }

    @Test
    public void testGroupDataSetsByDiseaseNoDisease() {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        Map<String, List<DataSet>> dataSetsGrouped = DAMUtils.getInstance().groupDataSetsByDisease(dataSets);
        assertNotNull(dataSetsGrouped);
        assertEquals(0, dataSetsGrouped.size());
    }

    @Test
    public void testGroupDataSetsByDiseaseNullDataSet() {
        Map<String, List<DataSet>> dataSetsGrouped = DAMUtils.getInstance().groupDataSetsByDisease(null);
        assertNotNull(dataSetsGrouped);
        assertEquals(0, dataSetsGrouped.size());
    }
}
