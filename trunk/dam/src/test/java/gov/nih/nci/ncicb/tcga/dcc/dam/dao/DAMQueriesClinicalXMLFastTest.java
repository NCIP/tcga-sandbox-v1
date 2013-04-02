package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Fast test for DAMQueriesClinicalXML
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesClinicalXMLFastTest {
    private DAMQueriesClinicalXML damQueriesClinicalXML;

    @Before
    public void setUp() {
        damQueriesClinicalXML = new DAMQueriesClinicalXML();
    }

    @Test
    public void testGetFileInfoForSelectedDataSets() throws Exception {
        final List<DataSet> selectedDataSets = new ArrayList<DataSet>();

        final DataSet ds1 = new DataSetClinical();
        ds1.setCenterId(DataAccessMatrixQueries.CLINICAL_XML_CENTER);
        final DataFile df1 = new DataFileClinical();
        df1.setFileId("1");
        ds1.setDataFiles(Arrays.asList(df1));

        final DataSet ds2 = new DataSetClinical();
        ds2.setCenterId(DataAccessMatrixQueries.CLINICAL_XML_CENTER);
        // should not be added because same ID as files in first dataset
        final DataFile df1Dupe = new DataFileClinical();
        df1Dupe.setFileId("1");
        final DataFile df2 = new DataFileClinical();
        df2.setFileId("2");
        final DataFile df3 = new DataFileClinical();
        df3.setFileId("3");
        ds2.setDataFiles(Arrays.asList(df1Dupe, df2, df3));

        // should not be added to the data file list because wrong center type
        final DataSet otherDataSet = new DataSetClinical();
        otherDataSet.setCenterId(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);
        final DataFile otherDataFile = new DataFileClinical();
        otherDataSet.setDataFiles(Arrays.asList(otherDataFile));

        selectedDataSets.add(ds1);
        selectedDataSets.add(ds2);

        List<DataFile> dataFiles = damQueriesClinicalXML.getFileInfoForSelectedDataSets(selectedDataSets, false);
        assertEquals(3, dataFiles.size());
        assertTrue(dataFiles.contains(df1));
        assertTrue(dataFiles.contains(df2));
        assertTrue(dataFiles.contains(df3));
        assertFalse(dataFiles.contains(df1Dupe));
    }
}
