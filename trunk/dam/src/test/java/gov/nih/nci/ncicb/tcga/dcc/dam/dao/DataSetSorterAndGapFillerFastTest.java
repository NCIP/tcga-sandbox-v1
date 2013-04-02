package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetSorterAndGapFiller;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Fast test for DataSetSorterAndGapFiller
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataSetSorterAndGapFillerFastTest {


    @Test
    public void testSort() throws Exception {
        // make data sets in order we expect them to sort into
        final List<DataSet> dataSetsInExpectedOrder = new ArrayList<DataSet>();
        dataSetsInExpectedOrder.add(makeDataSet(0, "Exp-Gene", "BI", "Illumina", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(0, "Exp-Gene", "BI", "Illumina", "2", "Batch 1", "sample 2"));
        dataSetsInExpectedOrder.add(makeDataSet(0, "Exp-Gene", "BI", "Illumina", "2", "Batch 2", "sample 5"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BCM", "ABI", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "ABI", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "ABI", "3", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "GA_DNASeq", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "GA_DNASeq", "3", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "SOLiD_DNASeq", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "BI", "SOLiD_DNASeq", "3", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "WUSM", "GA_DNASeq", "2", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(1, "mutation", "WUSM", "GA_DNASeq", "3", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(2, "miRNA", "BCGSC", "HiSeq_miRNASeq", "3", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(3, "Exp-Protein", "MDA", "MDA_RPPA", "1", "Batch 1", "sample 1"));
        dataSetsInExpectedOrder.add(makeDataSet(3, "Exp-Protein", "MDA", "MDA_RPPA", "2", "Batch 1", "sample 1"));

        // then copy the list and shuffle it
        List<DataSet> dataSetsToSort = new ArrayList<DataSet>(dataSetsInExpectedOrder);
        Collections.shuffle(dataSetsToSort);

        // sort shuffled list
        DataSetSorterAndGapFiller sorterAndGapFiller = new DataSetSorterAndGapFiller();
        sorterAndGapFiller.sort(dataSetsToSort);

        // make sure newly sorted list is in expected order
        assertEquals(dataSetsInExpectedOrder, dataSetsToSort);

    }

    private DataSet makeDataSet(final Integer platformSortOrder, final String platformTypeId, final String center,
                                final String platform, final String level, final String batch, final String sample) {

        final DataSet dataSet = new DataSet();
        dataSet.setPlatformTypeSortOrder(platformSortOrder);
        dataSet.setPlatformTypeId(platformTypeId);
        dataSet.setPlatformId(platform);
        dataSet.setCenterId(center);
        dataSet.setLevel(level);
        dataSet.setBatch(batch);
        dataSet.setSample(sample);

        return dataSet;
    }
}
