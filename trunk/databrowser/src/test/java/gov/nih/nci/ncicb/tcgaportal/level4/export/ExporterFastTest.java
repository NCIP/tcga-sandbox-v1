package gov.nih.nci.ncicb.tcgaportal.level4.export;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExporterFastTest extends TestCase {

    public void testExport() throws IOException {
        
        ExportData exportData = ExportData.getInstance(FilterSpecifier.ListBy.Genes);

        String compareData = getActualResult();
        Results results = constructResults();

        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);

        exportData.export(results, printWriter);

        assertNotNull(sw.toString());
        assertEquals(compareData, sw.toString());

    }

    private String getActualResult() {
        String result;
        result = "Gene\tChromosome\tStart\tStop" + ExportData.LINE_DELIMITER;
        result += "EGFR\t3\t57517043\t57522721" + ExportData.LINE_DELIMITER;
        return result;  //To change body of created methods use File | Settings | File Templates.
    }

    private Results constructResults() {

        // Create the Filter
        
    /*    CopyNumberType cn = new CopyNumberType();
        cn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cn.setLowerLimit(-0.5);
        cn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cn.setUpperLimit(0.5);
        cn.setFrequency(0.2F);
        cn.setPicked(true);

        List<ColumnType> listColumnTypes = new ArrayList<ColumnType>();
        listColumnTypes.add(cn);
*/

        FilterSpecifier filter = new FilterSpecifier();
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        //filter.setColumnTypes(listColumnTypes);

        filter.setDisease("GBM");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);

        // add Search criteria to the Results
        Results results = new Results(filter);


        // Create the result values
        ResultValue resultValue = new ResultDouble(.01);
        ResultValue[] resultValues = new ResultValue[1];
        resultValues[0] = resultValue;

        ResultRow resultRow = new ResultRow();
        resultRow.setName("EGFR");
        resultRow.setRowIndex(0);
        resultRow.setColumnResults(resultValues);

        Map<String, Serializable> annot = new HashMap<String,Serializable>();
        //every 4 make cnv
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_CNV, true);
        //location
        String chrom = "3";
        long start = 57517043;
        long stop = 57522721;
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM, chrom);
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START, start);
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP, stop);
        resultRow.setRowAnnotations(annot);
        
        results.addRow(resultRow);

        return results;
    }

}
