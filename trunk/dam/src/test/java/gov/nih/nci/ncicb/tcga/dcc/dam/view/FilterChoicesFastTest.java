/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FilterChoices
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilterChoicesFastTest {
    private FilterChoices filterChoices;
    private Header platformHeader1, platformHeader2;
    private Header centerHeader1, centerHeader2, centerHeader3;
    private Header levelHeader1, levelHeader2, levelHeader3;
    private Header sampleHeader1, sampleHeader2, sampleHeader3, sampleHeader4;
    private static final String TEST_DISEASE = "TST";

    @Before
    public void setUp() {
        // These headers are all used by the fake DAM Model as return values

        platformHeader1 = new Header();
        platformHeader1.setCategory(Header.HeaderCategory.PlatformType);
        platformHeader1.setName("p1");
        platformHeader2 = new Header();
        platformHeader2.setCategory(Header.HeaderCategory.PlatformType);
        platformHeader2.setName("p2");

        centerHeader1 = new Header();
        centerHeader1.setCategory(Header.HeaderCategory.Center);
        centerHeader1.setName("c1");
        centerHeader2 = new Header();
        centerHeader2.setCategory(Header.HeaderCategory.Center);
        centerHeader2.setName("c2");
        centerHeader3 = new Header();
        centerHeader3.setCategory(Header.HeaderCategory.Center);
        centerHeader3.setName("c3");

        final Header clinicalBiotabHeader = new Header();
        clinicalBiotabHeader.setCategory(Header.HeaderCategory.Center);
        clinicalBiotabHeader.setName(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);

        final Header clinicalXmlHeader = new Header();
        clinicalXmlHeader.setCategory(Header.HeaderCategory.Center);
        clinicalXmlHeader.setName(DataAccessMatrixQueries.CLINICAL_XML_CENTER);

        levelHeader1 = new Header();
        levelHeader1.setCategory(Header.HeaderCategory.Level);
        levelHeader1.setName("l1");
        levelHeader2 = new Header();
        levelHeader2.setCategory(Header.HeaderCategory.Level);
        levelHeader2.setName("l2");
        levelHeader3 = new Header();
        levelHeader3.setCategory(Header.HeaderCategory.Level);
        levelHeader3.setName("l3");

        sampleHeader1 = new Header();
        sampleHeader1.setCategory(Header.HeaderCategory.Sample);
        sampleHeader1.setName("TCGA-01-1111-01");
        sampleHeader2 = new Header();
        sampleHeader2.setCategory(Header.HeaderCategory.Sample);
        sampleHeader2.setName("TCGA-01-1111-10");
        sampleHeader3 = new Header();
        sampleHeader3.setCategory(Header.HeaderCategory.Sample);
        sampleHeader3.setName("TCGA-02-2222-01");
        sampleHeader4 = new Header();
        sampleHeader4.setCategory(Header.HeaderCategory.Sample);
        sampleHeader4.setName("TCGA-02-2222-11");

        DAMModel fakeModel = new DAMModel() {

            @Override
            public List<Header> getHeadersForCategory(final Header.HeaderCategory category) {
                if (category == Header.HeaderCategory.PlatformType) {
                    return Arrays.asList(platformHeader1, platformHeader2);
                } else if (category == Header.HeaderCategory.Center) {
                    // note! centerHeader3 added twice on purpose
                    return Arrays.asList(centerHeader3, centerHeader1, centerHeader2, centerHeader3, clinicalBiotabHeader, clinicalXmlHeader);
                } else if (category == Header.HeaderCategory.Level) {
                    // each level added multiple times on purpose, since on the real DAM the levels are repeated
                    return Arrays.asList(levelHeader1, levelHeader3, levelHeader2, levelHeader1, levelHeader2, levelHeader3);
                } else if (category == Header.HeaderCategory.Sample) {
                    return Arrays.asList(sampleHeader1, sampleHeader3, sampleHeader2, sampleHeader4);
                } else {
                    return null;
                }
            }

            @Override
            public String getDiseaseType() {
                return TEST_DISEASE;
            }

            @Override
            public Header getBatchHeader(final int index) {
                Header header = new Header();
                header.setName("Batch " + (index + 1));

                return header;
            }

            // methods below not used in FilterChoices so not implemented in fake

            @Override
            public Cell getCell(final String id) {
                return null;
            }

            @Override
            public Header getHeader(final Header.HeaderCategory category, final String name) {
                return null;
            }

            @Override
            public List<Cell> getCellsForHeader(final Header header) {
                return null;
            }

            @Override
            public List<Header> getHeadersForHeader(final Header header) {
                return null;
            }

            @Override
            public int getTotalBatches() {
                return 10;
            }

            @Override
            public Header getHeaderById(final String id) {
                return null;
            }

            @Override
            public Collection<Cell> getAllCells() {
                return null;
            }

            @Override
            public int getTotalColumns() {
                return 0;
            }

            @Override
            public int getHeaderColSpan(final String headerId) {
                return 0;
            }

            @Override
            public int getHeaderRowSpan(final String headerId) {
                return 0;
            }

            @Override
            public DAMModel getWrappedModel() {
                return null;
            }
        };

        filterChoices = FilterChoices.getInstance(fakeModel);
    }

    @Test
    public void testGetAllBatches() {
        List<String> batches = filterChoices.getAllBatches();
        assertEquals(10, batches.size());
        for (int i = 0; i<10; i++) {
            assertEquals("Batch " + (i+1), batches.get(i));
        }        
    }

    @Test
    public void testGetAllPlatformTypes() {
        List<String> platformTypes = filterChoices.getAllPlatformTypes();
        assertEquals(2, platformTypes.size());
        assertEquals("p1", platformTypes.get(0));
        assertEquals("p2", platformTypes.get(1));        
    }

    @Test
    public void testGetAllCenters() {
        List<String> centers = filterChoices.getAllCenters();
        // 6 headers were returned by the fake DAMModel, but 2 were clinical and 2 of the others had the same name, so only 3 should be in final list
        assertEquals(3, centers.size());
        assertEquals("c1", centers.get(0));
        assertEquals("c2", centers.get(1));
        assertEquals("c3", centers.get(2));
    }

    @Test
    public void testGetAllLevels() {
        List<String> levels = filterChoices.getAllLevels();
        assertEquals(3, levels.size());
        assertEquals("l1", levels.get(0));
        assertEquals("l2", levels.get(1));
        assertEquals("l3", levels.get(2));
    }

    @Test
    public void testGetSampleCollectionCenterOptions() {
        List<String> collectionCenters = filterChoices.getSampleCollectionCenterOptions();
        assertEquals(2, collectionCenters.size());
        assertEquals("01", collectionCenters.get(0));
        assertEquals("02", collectionCenters.get(1));
    }

    @Test
    public void testGetSampleTypeOptions() throws Exception {
        List<String> sampleTypes = filterChoices.getSampleTypeOptions();
        assertEquals(3, sampleTypes.size());
        assertEquals("01", sampleTypes.get(0));
        assertEquals("10", sampleTypes.get(1));
        assertEquals("11", sampleTypes.get(2));
    }

    @Test
    public void testClearInstances() {
        // there should be a model there from setup
        assertTrue(FilterChoices.hasInstanceFor(TEST_DISEASE));
        FilterChoices.clearInstances();
        assertFalse(FilterChoices.hasInstanceFor(TEST_DISEASE));
    }
}
