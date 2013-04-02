/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * Author: David Nassau
 */
//unit test for DataAccessMatrixModel class
public class DataAccessMatrixModelFastTest extends TestCase {

	static final String SAMPLES_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    static final String TEST_DATAFILE = "mock_sampleinfo.txt";
    static final String DISEASE_TYPE = "GBM";
    static List<DataSet> dpts;

    public void setUp() throws Exception {
        try {
            loadDataPointList();
        }
        catch(IOException e) {
            throw new Exception( e.getMessage(), e );
        }
    }

    //copied from DataAccessMatrixQueriesMockImpl
    //creates a list of datapoints from a tab-delimited file, just as the mock DAO does
    //But we use a different copy of the data file, so it won't be affected by any change in the mock data
    //dpts array is stored statically for use by multiple junit tests.
    public void loadDataPointList() throws IOException {
        if(dpts == null) {
            dpts = new ArrayList<DataSet>();
            BufferedReader reader = null;
            try {
                final String fname = SAMPLES_DIR + "damModel/" + TEST_DATAFILE;
                reader = new BufferedReader( new FileReader( fname ) );
                String record = reader.readLine();
                while(record != null) {
                    final StringTokenizer st = new StringTokenizer( record, "\t" );
                    final String platformType = readNext( st );
                    final String center = readNext( st );
                    final String level = readNext( st );
                    final String batch = readNext( st );
                    final String sample = readNext( st );
                    final String availability = readNext( st );
                    final String sProtected = readNext( st );
                    final String barcode = readNext( st );
                    final String platform = readNext( st );
                    final DataSet dataSet = new DataSet();
                    dataSet.setPlatformTypeId( platformType );
                    // need to set sort order so the datasets sort correctly in the model
                    int platformTypeId = Integer.valueOf( platformType );
                    if(platformTypeId < 1) {
                        // if negative, make positive
                        platformTypeId *= -1;
                    }
                    dataSet.setPlatformTypeSortOrder( platformTypeId );
                    dataSet.setCenterId( center );
                    dataSet.setLevel( level );
                    dataSet.setBatch( batch );
                    dataSet.setSample( sample );
                    dataSet.setAvailability( availability );
                    dataSet.setProtected( "Y".equals( sProtected ) );
                    final ArrayList<String> barcodes = new ArrayList<String>();
                    barcodes.add( barcode );
                    dataSet.setBarcodes( barcodes );
                    dataSet.setPlatformId( platform );
                    dpts.add( dataSet );
                    record = reader.readLine();
                }
            }
            catch(IOException e) {
                dpts = null;
                throw e;
            }
            finally {
                try {
                    if(reader != null) {
                        reader.close();
                    }
                }
                catch(IOException ex) {
                }
            }
        }
    }

    //compensates for a quirk of the string tokenizer - skips null tokens so we have to insert . in the test data
    private String readNext( final StringTokenizer st ) {
        String ret = st.nextToken();
        if(".".equals( ret )) {
            ret = null; //"";
        }
        return ret;
    }

    //checks integrity of the model against original data set
    public void testMatrixIntegrity() {
        //todo: make it more realistic by supplying a map of submitted samples
        final DAMStaticModel model = DAMStaticModel.createInstance( DISEASE_TYPE, dpts, null );
        assertEquals( DISEASE_TYPE, model.getDiseaseType() );
        checkColumnHeaders( model );
        checkBatchCount( model );
        checkSampleCount( model );
        checkCells( model );
    }
    //do a tree-navigation of all the column headers.  Check against the original
    //data.  The order and hierarchy should be the same.
    //Assumes original data is sorted by platform type, center, level
    //assume for now there are not cases where everythings the same except platform - we don't know if we need to deal with that

    private void checkColumnHeaders( final DAMStaticModel model ) {
        Header platformHeader, centerHeader, levelHeader;
        List<Header> platformHeaders = null, centerHeaders = null, levelHeaders = null;
        String platformType = null, center = null, platform = null, level = null;
        boolean newPlatformType = false, newCenterOrPlatform = false, newLevel = false;
        platformHeaders = model.getHeadersForCategory( Header.HeaderCategory.PlatformType );
        int platformTypeIdx = 0, centerIdx = 0, levelIdx = 0;
        for(final DataSet dpt : dpts) {
            newPlatformType = false;
            newCenterOrPlatform = false;
            newLevel = false;
            if(!compareStr( platformType, dpt.getPlatformTypeId() )) {
                platformType = dpt.getPlatformTypeId();
                center = dpt.getCenterId();
                platform = dpt.getPlatformId();
                level = dpt.getLevel();
                newPlatformType = true;
                newCenterOrPlatform = true;
                newLevel = true;
            } else if(!compareStr( center, dpt.getCenterId() ) || !compareStr( platform, dpt.getPlatformId() )) {
                center = dpt.getCenterId();
                platform = dpt.getPlatformId();
                level = dpt.getLevel();
                newCenterOrPlatform = true;
                newLevel = true;
            } else if(!compareStr( level, dpt.getLevel() )) {
                level = dpt.getLevel();
                newLevel = true;
            }
            if(newPlatformType) {
                platformHeader = platformHeaders.get( platformTypeIdx++ );
                assertEquals( platformType, platformHeader.getName() );
                centerHeaders = platformHeader.getChildHeaders();
                centerIdx = 0;
            }
            if(newCenterOrPlatform) {
                centerHeader = centerHeaders.get( centerIdx++ );
                final String compareval = ( platform != null ? center + "." + platform : center );
                assertEquals( compareval, centerHeader.getName() );
                levelHeaders = centerHeader.getChildHeaders();
                levelIdx = 0;
            }
            if(newLevel) {
                levelHeader = levelHeaders.get( levelIdx++ );
                assertEquals( level, levelHeader.getName() );
            }
        }
    }

    private boolean compareStr( final String s1, final String s2 ) {
        boolean ret = false;
        if(s1 == null && s2 != null) {
            ret = false;
        } else if(s2 == null && s1 != null) {
            ret = false;
        } else if(s1 == null && s2 == null) {
            ret = true;
        } else {
            ret = s1.equals( s2 );
        }
        return ret;
    }

    //Check the number of batches in the model against the original data.
    //assumes each batch has a unique name
    private void checkBatchCount( final DAMStaticModel model ) {
        final int batchCountFromModel = model.getTotalBatches();
        //count the unique batch names in the input
        int batchCountFromInput = 0;
        final Hashtable<String, String> batchNames = new Hashtable<String, String>();
        for(final DataSet dpt : dpts) {
            final String batchName = dpt.getBatch();
            if(batchNames.get( batchName ) == null) {
                batchCountFromInput++;
                batchNames.put( batchName, batchName );
            }
        }
        assertEquals( batchCountFromModel, batchCountFromInput );
    }

    //Check the number of sample in the model against the original data.
    //assumes each sample has a unique name
    public void checkSampleCount( final DAMStaticModel model ) {
        int sampleCountFromModel = 0;
        for(int i = 0; i < model.getTotalBatches(); i++) {
            final Header batchHeader = model.getBatchHeader( i );
            sampleCountFromModel += batchHeader.getChildHeaders().size();
        }
        //count the unique sample names in the input
        int sampleCountFromInput = 0;
        final Hashtable<String, String> batchNames = new Hashtable<String, String>();
        for(final DataSet dpt : dpts) {
            final String sampleName = dpt.getSample();
            if(batchNames.get( sampleName ) == null) {
                sampleCountFromInput++;
                batchNames.put( sampleName, sampleName );
            }
        }
        assertEquals( sampleCountFromModel, sampleCountFromInput );
    }

    //traverse all cells by column then row, compare against original data
    //Assumes data file is sorted platform, center, level, then batch and sample.
    //Also make sure the assigned ID is unique across all cells
    private void checkCells( final DAMStaticModel model ) {
        int dptIndex = 0;
        final List<String> cellIds = new ArrayList<String>();
        for(final Header levelHeader : getLevelHeadersInOrder( model )) {
            for(final Cell cell : model.getCellsForHeader( levelHeader )) {
                final DataSet dpt = dpts.get( dptIndex++ );
                for(DataSet ds : cell.getDatasets()) {
                    assertEquals( ds.getPlatformTypeId(), dpt.getPlatformTypeId() );
                    assertEquals( ds.getLevel(), dpt.getLevel() );
                    assertEquals( ds.getBatch(), dpt.getBatch() );
                    assertEquals( ds.getSample(), dpt.getSample() );
                    assertEquals( cell.getAvailability(), dpt.getAvailability() );
                    assertEquals( ds.getBarcodes().get( 0 ), dpt.getBarcodes().get( 0 ) );
//                String centerAndPlatform = dpt.getCenterId();
//                if (dpt.getPlatformId() != null) centerAndPlatform += ("." + dpt.getPlatformId());
//                assertEquals(cell.getCenter(), centerAndPlatform);
                    assertEquals( ds.getCenterId(), dpt.getCenterId() );
                }
                //check uniqueness of ID
                assertFalse( cellIds.contains( cell.getId() ) );
                cellIds.add( cell.getId() );
            }
        }
        //make sure no dpt left out
    }

    private List<Header> getLevelHeadersInOrder( final DAMStaticModel model ) {
        final List<Header> ret = new ArrayList<Header>();
        for(final Header platformHeader : model.getHeadersForCategory( Header.HeaderCategory.PlatformType )) {
            for(final Header centerHeader : platformHeader.getChildHeaders()) {
                for(final Header levelHeader : centerHeader.getChildHeaders()) {
                    ret.add( levelHeader );
                }
            }
        }
        return ret;
    }
/* selection is no longer part of DAMModel functionality
    public void testModelSelection() {
        DataAccessMatrixModel damUserModel = createUserStateModel();

        checkSelectCells(damUserModel );
        checkSelectAll(damUserModel );
        checkSelectHeader(damUserModel );
        checkSelectIntersect(damUserModel );
    }

    //select one level header and one sample header, the result should be a single selected cell
    private void checkSelectIntersect(DataAccessMatrixModel damUserModel) {
        damUserModel.unselectAll();
        Header levelHeader = damUserModel.getColumnHeaders( Header.HeaderCategory.Level).get(0);
        Header sampleHeader = damUserModel.getBatchHeader(0).getChildHeaders().get(0);
        damUserModel.toggleHeader(levelHeader.getId(), false);
        damUserModel.toggleHeader(sampleHeader.getId(), true);
        assertEquals(1, damUserModel.getSelectedDataSets().size());
    }

    //select the first platform column header, see if all the downstream cells and headers are selected
    private void checkSelectHeader(DataAccessMatrixModel damUserModel) {
        damUserModel.unselectAll();

        int selectedCellCount = 0;
        Header platformHeader = damUserModel.getColumnHeaders(Header.HeaderCategory.PlatformType).get(0);
        assertFalse(damUserModel.isSelected(platformHeader));
        damUserModel.toggleHeader(platformHeader.getId(), false);
        assertTrue(damUserModel.isSelected(platformHeader));
        for (Header centerHeader : platformHeader.getChildHeaders()) {
            assertTrue(damUserModel.isSelected(centerHeader));
            for (Header levelHeader : centerHeader.getChildHeaders()) {
                assertTrue(damUserModel.isSelected(levelHeader));
                for (Cell cell : damUserModel.getCellsForHeader(levelHeader)) {
                    //should only be selected if data is available
                    if (cell.getAvailability().equals("A")) {
                        assertTrue(damUserModel.isSelected(cell));
                        selectedCellCount++;
                    } else {
                        assertFalse(damUserModel.isSelected(cell));
                    }
                }
            }
        }

        //also check the array of selected cells to make there are no additional cells selected
        assertEquals(selectedCellCount, damUserModel.getSelectedDataSets().size());
    }

    //select all cells, compare the count of selected cells against the total count
    private void checkSelectAll(DataAccessMatrixModel damUserModel) {
        damUserModel.unselectAll();

        int cellCount = 0;
        //count all the cells
        for (int i=0; i<damUserModel.getTotalBatches(); i++) {
            Header batch = damUserModel.getBatchHeader(i);
            for (Header sample : batch.getChildHeaders()) {
                for (Cell cell : damUserModel.getCellsForHeader(sample)) {
                    if (DataAccessMatrixQueries.AVAILABILITY_AVAILABLE.equals(cell.getAvailability())) {
                        cellCount++;
                    }
                }
            }
        }

        damUserModel.selectAll();

        assertEquals(cellCount, damUserModel.getSelectedDataSets().size());
    }

    //select a few cells, make sure we get the same IDs back
    private void checkSelectCells(DataAccessMatrixModel damUserModel) {
        damUserModel.unselectAll();

        Header batch1 = damUserModel.getBatchHeader(0);
        Header sample1 = batch1.getChildHeaders().get(0);
        List<Cell> cells = damUserModel.getCellsForHeader(sample1);

        //select the first three
        ArrayList idsToSelect = new ArrayList();
        for (int i=0; i<3; i++) {
            idsToSelect.add(cells.get(i).getId());
        }

        String idsToSelectStr = (String)idsToSelect.get(0) + "," +
                                (String)idsToSelect.get(1) + "," +
                                (String)idsToSelect.get(2);

        damUserModel.updateSelectedCells(idsToSelectStr);
        List modelSelection = damUserModel.getSelectedCellIds();
        assertEquals(3, modelSelection.size());

        for (int i=0; i<modelSelection.size(); i++) {
            assertTrue(idsToSelect.contains((String)modelSelection.get(i)));
        }

        for (int i=0; i<3; i++) {
            assertTrue(modelSelection.contains((String)idsToSelect.get(i)));
        }

    }
*/
}
