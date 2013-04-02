package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetSorterAndGapFiller;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test class for DAMStaticModel
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMStaticModelFastTest {

    private DAMStaticModel staticModel;

    @Before
    public void setUp() {
        staticModel = new DAMStaticModel("TEST");
        staticModel.setDataSetSorterAndGapFiller(new DataSetSorterAndGapFiller());
    }

    @Test
    public void testAddDataSets() {
        final List<DataSet> dataSets = new ArrayList<DataSet>();

        /*
           Expected final model is:

                 Exp-Gene               SNP        miRNASeq     Mutations              Clinical
                 c1.gene1  c2.gene1     c3.snp    c6.miRNA      c5.abi   c6.DNASeq     bcr
                 1  2   3  1  2  3      1  2  3    3             1         2   3         C
                 0  1   2  3  4  5      6  7  8    9             10       11  12        13
                 ------------------------------------------------------------------------------
Batch 1 TCGA-1   A  A   A  A  A  A      A          A             A                       A
        TCGA-2   A  A   A               A          A             A         A   A         A

Batch 2 TCGA-3   A         A  A  A      A  A  A    A                       A   A


         */

        // build a matrix of the data sets where they are expected to be in the model
        final DataSet[][] expectedMatrix = new DataSet[14][3];

        expectedMatrix[0][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c1", "gene1", "1", 1);
        final DataSet ds1x1b = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c1", "gene1", "1", 1); // 2nd data set for cell 1,1
        expectedMatrix[0][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Exp-Gene", "c1", "gene1", "1", 1);
        expectedMatrix[0][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Exp-Gene", "c1", "gene1", "1", 1);

        expectedMatrix[1][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c1", "gene1", "2", 1);
        expectedMatrix[1][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Exp-Gene", "c1", "gene1", "2", 1);
        expectedMatrix[1][2] = null;

        expectedMatrix[2][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c1", "gene1", "3", 1);
        expectedMatrix[2][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Exp-Gene", "c1", "gene1", "3", 1);
        expectedMatrix[2][2] = null;

        expectedMatrix[3][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c2", "gene1", "1", 1);
        expectedMatrix[3][1] = null;
        expectedMatrix[3][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Exp-Gene", "c2", "gene1", "1", 1);

        expectedMatrix[4][0] = makeDataSet(dataSets,"Batch1", "TCGA-1", "Exp-Gene", "c2", "gene1", "2", 1);
        expectedMatrix[4][1] = null;
        expectedMatrix[4][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Exp-Gene", "c2", "gene1", "2", 1);

        expectedMatrix[5][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Exp-Gene", "c2", "gene1", "3", 1);
        expectedMatrix[5][1] = null;
        expectedMatrix[5][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Exp-Gene", "c2", "gene1", "3", 1);

        expectedMatrix[6][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "SNP", "c3", "snp", "1", 2);
        expectedMatrix[6][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "SNP", "c3", "snp", "1", 2);
        expectedMatrix[6][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "SNP", "c3", "snp", "1", 2);

        expectedMatrix[7][0] = null;
        expectedMatrix[7][1] = null;
        expectedMatrix[7][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "SNP", "c3", "snp", "2", 2);

        expectedMatrix[8][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "SNP", "c3", "snp", "3", 2);

        expectedMatrix[9][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "miRNASeq", "c6", "miRNA", "3", 3);
        expectedMatrix[9][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "miRNASeq", "c6", "miRNA", "3", 3);
        expectedMatrix[9][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "miRNASeq", "c6", "miRNA", "3", 3);

        expectedMatrix[10][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Mutations", "c5", "abi", "1", 4);
        expectedMatrix[10][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Mutations", "c5", "abi", "1", 4);
        expectedMatrix[10][2] = null;

        expectedMatrix[11][0] = null;
        expectedMatrix[11][1] = makeDataSet(dataSets,"Batch1", "TCGA-2", "Mutations", "c6", "DNASeq", "2", 4);
        expectedMatrix[11][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Mutations", "c6", "DNASeq", "2",4 );

        expectedMatrix[12][0] = null;
        expectedMatrix[12][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Mutations", "c6", "DNASeq", "3", 4);
        expectedMatrix[12][2] = makeDataSet(dataSets, "Batch2", "TCGA-3", "Mutations", "c6", "DNASeq", "3", 4);

        expectedMatrix[13][0] = makeDataSet(dataSets, "Batch1", "TCGA-1", "Clinical", "bcr", null, "C", 100);
        expectedMatrix[13][1] = makeDataSet(dataSets, "Batch1", "TCGA-2", "Clinical", "bcr", null, "C", 100);

        // now add data sets to the model
        staticModel.addDataSets(dataSets);

        // verify headers
        checkHeaderOrder(staticModel.getHeadersForCategory(Header.HeaderCategory.PlatformType),
                "Exp-Gene", "SNP", "miRNASeq", "Mutations", "Clinical");
        checkHeaderOrder(staticModel.getHeadersForCategory(Header.HeaderCategory.Center),
                "c1.gene1", "c2.gene1", "c3.snp", "c6.miRNA", "c5.abi", "c6.DNASeq", "bcr");
        checkHeaderParentName(staticModel.getHeadersForCategory(Header.HeaderCategory.Center),
                "Exp-Gene", "Exp-Gene", "SNP", "miRNASeq", "Mutations", "Mutations", "Clinical");

        checkHeaderOrder(staticModel.getHeadersForCategory(Header.HeaderCategory.Level),
                "1", "2", "3",
                "1", "2", "3",
                "1", "2", "3",
                "3",
                "1",
                "2", "3",
                "C");
        final List<Header> levelHeaders = staticModel.getHeadersForCategory(Header.HeaderCategory.Level);

        checkHeaderParentName(levelHeaders,
                "c1.gene1", "c1.gene1", "c1.gene1",
                "c2.gene1", "c2.gene1", "c2.gene1",
                "c3.snp", "c3.snp", "c3.snp",
                "c6.miRNA",
                "c5.abi",
                "c6.DNASeq", "c6.DNASeq",
                "bcr"
        );

        // verify cells contain expected data sets
        for (int i=0; i<levelHeaders.size(); i++) {
            List<Cell> columnCells = staticModel.getCellsForHeader(levelHeaders.get(i));
            for (int j=0; j<columnCells.size(); j++) {
                if (expectedMatrix[i][j] != null && columnCells.get(j) != null) {
                    assertEquals("cell at " + i + ", " + j + " doesn't contain expected data set",
                            expectedMatrix[i][j], columnCells.get(j).getDatasets().get(0));

                    // special case: cell 1x1 should have 2 data sets
                    if (i==0 && j==0) {
                        assertEquals(ds1x1b, columnCells.get(j).getDatasets().get(1));
                    }
                } else {
                    assertNull("expectedMatrix at " + i + ", " + j + " is not null", expectedMatrix[i][j]);
                    assertNull("cell at column " + i + " row " + j + " is not null", columnCells.get(j));
                }
            }
        }


    }

    private void checkHeaderParentName(final List<Header> headers, final String... expectedParentNames) {
        for (int i=0; i<expectedParentNames.length; i++) {
            assertEquals(expectedParentNames[i], headers.get(i).getParentHeader().getName());
        }
    }

    private void checkHeaderOrder(final List<Header> headers, final String... expectedHeaderNames) {
        for (int i=0; i<expectedHeaderNames.length; i++) {
            assertEquals("incorrect header at position " + i, expectedHeaderNames[i], headers.get(i).getName());
        }
    }

    private DataSet makeDataSet(final List<DataSet> dataSets,
                                final String batch, final String sample, final String platformTypeId,
                                final String centerId, final String platformAlias, final String level,
                                final int platformSortOrder) {
        final DataSet dataSet = new DataSet();
        dataSet.setPlatformTypeSortOrder(platformSortOrder);
        dataSet.setBatch(batch);
        dataSet.setSample(sample);
        dataSet.setPlatformTypeId(platformTypeId);
        dataSet.setCenterId(centerId);
        dataSet.setPlatformAlias(platformAlias);
        dataSet.setLevel(level);
        dataSet.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataSets.add(dataSet);
        return dataSet;
    }
}
