/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Cell;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests CachedOutputManagaer
 *
 * @author David Nassau
 * @version $Rev$
 */
@RunWith(JMock.class)
public class CachedOutputManagerFastTest {

    private Mockery mockEngine = new JUnit4Mockery();

    @Before
    public void setUp() {
        final DAMModel mockModel = mockEngine.mock(DAMModel.class);

        final List<Header> ptHeaders = new ArrayList<Header>();
        final Header ptHeader = new Header(Header.HeaderCategory.PlatformType, "Exp-Gene", null, Header.HeaderType.COL_HEADER);
        ptHeaders.add(ptHeader);
        final Header cpHeader = new Header(Header.HeaderCategory.Center, "BI.HT_HG-U133A", ptHeader, Header.HeaderType.COL_HEADER);
        ptHeader.getChildHeaders().add(cpHeader);
        final Header lev2Header = new Header(Header.HeaderCategory.Level, "2", cpHeader, Header.HeaderType.COL_HEADER);
        cpHeader.getChildHeaders().add(lev2Header);

        final Header lev3Header = new Header(Header.HeaderCategory.Level, "3", cpHeader, Header.HeaderType.COL_HEADER);
        cpHeader.getChildHeaders().add(lev3Header);

        final Header clinPtHeader = new Header(Header.HeaderCategory.PlatformType, DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, null, Header.HeaderType.COL_HEADER);
        ptHeaders.add(clinPtHeader);
        final Header clinCpHeader = new Header(Header.HeaderCategory.Center, DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER, clinPtHeader, Header.HeaderType.COL_HEADER);
        clinPtHeader.getChildHeaders().add(clinCpHeader);
        final Header clinLevHeader = new Header(Header.HeaderCategory.Level, "", clinCpHeader, Header.HeaderType.COL_HEADER);
        clinCpHeader.getChildHeaders().add(clinLevHeader);

        //use the same set of cells for level 2 level 3 and clinical columns - not realistic but works ok for test
        final List<Cell> columnCells = new ArrayList<Cell>();
        for (int i = 0; i < 20; i++) {
            DataSet ds = new DataSet();
            if (i % 2 == 0) {
                ds.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
            } else {
                ds.setAvailability(DataAccessMatrixQueries.AVAILABILITY_PENDING);
            }
            ds.setPlatformTypeId("1");
            ds.setCenterId("1");
            ds.setPlatformId("1");
            Cell cell = new Cell();
            cell.addDataset(ds);
            columnCells.add(cell);
        }

        mockEngine.checking(new Expectations() {{
            one(mockModel).getHeadersForCategory(Header.HeaderCategory.PlatformType);
            will(returnValue(ptHeaders));
            allowing(mockModel).getDiseaseType();
            will(returnValue("GBM"));
            allowing(mockModel).getCellsForHeader(lev2Header);
            will(returnValue(columnCells));
            allowing(mockModel).getCellsForHeader(lev3Header);
            will(returnValue(columnCells));
            allowing(mockModel).getCellsForHeader(clinLevHeader);
            will(returnValue(columnCells));
        }});

        CachedOutputManager.clear();
        (new CachedOutputManager()).setCachefileDirectory("/test/");
        CachedOutputManager.registerColumnSizes(mockModel);
    }

    @Test
    public void testLevel2Column() {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileLevelTwo df = new DataFileLevelTwo();
        df.setFileName("testlev2.txt");
        df.setPlatformName("platform");
        df.setCenterName("center");
        df.setSourceFileType("sourcetype");
        df.setPlatformTypeId("1");
        df.setCenterId("1");
        df.setPlatformId("1");
        Collection<Integer> dp = new ArrayList<Integer>();
        dp.add(1);
        df.setDataSetsDP(dp);
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        String expectedPath = "/test/GBM_level2_center_platform_sourcetype.txt";
        assertTrue(df.isPermanentFile());
        assertEquals(expectedPath, df.getPath());

        //this part is done at time of generation
        assertTrue(df.decideWhetherToGenerateCacheFile());
        assertFalse(df.isPermanentFile());
        assertNull(df.getPath());
        assertEquals(expectedPath, df.getCacheFileToGenerate());
    }

    @Test
    public void testLevel2IncompleteColumnSelection() {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileLevelTwo df = new DataFileLevelTwo();
        df.setFileName("testlev2.txt");
        df.setPlatformTypeId("1");
        df.setCenterId("1");
        df.setPlatformId("1");
        Collection<Integer> dp = new ArrayList<Integer>();
        dp.add(1);
        df.setDataSetsDP(dp);
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {             //means not complete column
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        assertFalse(df.isPermanentFile());

        //this part is done at time of generation
        assertFalse(df.decideWhetherToGenerateCacheFile());
        assertNull(df.getCacheFileToGenerate());
    }

    @Test
    public void testLevel3Column() {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileLevelThree df = new DataFileLevelThree();
        df.setFileName("testlev3.txt");
        df.setPlatformTypeId("1");
        df.setCenterId("1");
        df.setPlatformId("1");
        Collection<Integer> dp = new ArrayList<Integer>();
        dp.add(1);
        df.setDataSetsDP(dp);
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        String expectedPath = "/test/GBM.level3.1.testlev3.txt";
        assertTrue(df.isPermanentFile());
        assertEquals(expectedPath, df.getPath());

        //this part is done at time of generation
        assertTrue(df.decideWhetherToGenerateCacheFile());
        assertFalse(df.isPermanentFile());
        assertNull(df.getPath());
        assertEquals(expectedPath, df.getCacheFileToGenerate());
    }

    @Test
    public void testLevel3IncompleteColumnSelection() {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileLevelThree df = new DataFileLevelThree();
        df.setFileName("testlev3.txt");
        df.setPlatformTypeId("1");
        df.setCenterId("1");
        df.setPlatformId("1");
        Collection<Integer> dp = new ArrayList<Integer>();
        dp.add(1);
        df.setDataSetsDP(dp);
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {             //means not complete column
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        assertFalse(df.isPermanentFile());

        //this part is done at time of generation
        assertFalse(df.decideWhetherToGenerateCacheFile());
        assertNull(df.getCacheFileToGenerate());
    }

    @Test
    public void testClinicalColumn() throws ParseException {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileClinical df = new DataFileClinical();
        df.setFileName("testclinical.txt");
        df.setPath("/path/to/file");

        df.setCenterId(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);
        df.setDateAdded(makeDate(2009, 1, 1));
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        assertFalse(df.isPermanentFile());
        assertEquals("/path/to/file", df.getPath());


        //this part is done at time of generation
        assertFalse(df.decideWhetherToGenerateCacheFile());
        assertFalse(df.isPermanentFile());
        assertEquals("/path/to/file", df.getPath());

    }

    private Date makeDate(final int year, final int month, final int day) throws ParseException {
        String dateString = month + "-" + day + "-" + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.parse(dateString);
    }

    @Test
    public void testClinicalIncompleteColumnSelection() throws ParseException {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileClinical df = new DataFileClinical();
        df.setFileName("testclinical.txt");
        df.setCenterId(DataAccessMatrixQueries.CLINICAL_XML_CENTER);
        df.setDateAdded(makeDate(2009, 1, 1));
        //df.setPlatformId();
        Collection<String> samples = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {             //means not complete column
            samples.add("sample" + i);
        }
        df.setSamples(samples);
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        assertFalse(df.isPermanentFile());

        //this part is done at time of generation
        assertFalse(df.decideWhetherToGenerateCacheFile());
        assertNull(df.getCacheFileToGenerate());
    }

    @Test
    public void testMakeDateString() throws ParseException {
        String s = CachedOutputManager.makeDateString(makeDate(2009, 1, 1));
        assertEquals("20090101", s);
        s = CachedOutputManager.makeDateString(makeDate(2009, 1, 11));
        assertEquals("20090111", s);
        s = CachedOutputManager.makeDateString(makeDate(2009, 11, 11));
        assertEquals("20091111", s);
    }

    @Test
    public void testLevel2ColumnNotConsolidated() {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFileLevelTwo df = new DataFileLevelTwo();
        df.setConsolidated(false);
        df.setFileName("oneBarcode.txt");
        df.setPlatformTypeId("1");
        df.setCenterId("1");
        df.setPlatformId("1");
        df.setDataSetsDP(Arrays.asList(1));
        df.setSamples(Arrays.asList("sample1"));
        files.add(df);
        CachedOutputManager.addCachedFileNames("GBM", files);
        assertFalse("File isn't consolidated (i.e. is one per barcode) so should never be cached", df.isPermanentFile());

        assertFalse(df.decideWhetherToGenerateCacheFile());
        assertNull(df.getCacheFileToGenerate());

    }
}
