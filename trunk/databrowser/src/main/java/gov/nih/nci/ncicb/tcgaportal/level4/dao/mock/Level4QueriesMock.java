package gov.nih.nci.ncicb.tcgaportal.level4.dao.mock;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Disease;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.CorrelationCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: class comments
 *
 * @author David Nassau
 *         Last updated by: $Author: bertondl $
 * @version $Rev: 7775 $
 */

//todo  retire this
public class Level4QueriesMock implements Level4Queries {
    public enum Orientation {
        ByPatient, ByGene
    }

    static final String FILE_PATIENTS = "patients.txt";
    static final String FILE_GENES = "genes.txt";
    static final int TOTAL_PATIENTS = 500;
    static final int TOTAL_GENES = 1500;

    static final String FILE_PATHWAYS = "pathways.txt";

    private CorrelationCalculator correlationCalculator;
    private List<ColumnType> baseColTypes;
    private int chunkSize = 999999; //one big chunk  //50;

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setCorrelationCalculator(CorrelationCalculator correlationCalculator) {
        this.correlationCalculator = correlationCalculator;
    }

    public List<Disease> getDiseases() throws QueriesException {
        List<Disease> ret = new ArrayList<Disease>();
        ret.add(new Disease("GBM", "Glioblastoma Multiforme"));
        ret.add(new Disease("OV", "Serous Cystadenocarcinoma"));
        return ret;
    }

    public List<ColumnType> getColumnTypes(String disease) throws QueriesException {
        if (baseColTypes != null) {
            return baseColTypes;
        }

        List<ColumnType> ret = new ArrayList<ColumnType>();

        //non-mutation anomalies: CN and expression

        //CN
        CopyNumberType at1 = new CopyNumberType();
        at1.setPlatformType(4); //cn
        at1.setDisplayPlatformType("Copy Number Results");
        at1.setDisplayCenter("HMS");
        at1.setDisplayPlatform("HG-CGH-244A");
        //defaults
        at1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        at1.setLowerLimit(1.5);  //less than 2 copies
        at1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        at1.setUpperLimit(2.5);  //more than 2
        ret.add(at1);

        CopyNumberType at11 = new CopyNumberType();
        at11.setPlatformType(4); //cn
        at11.setDisplayPlatformType("Copy Number Results");
        at11.setDisplayCenter("MSKCC");
        at11.setDisplayPlatform("HG-CGH-244A");
        //defaults
        at11.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        at11.setLowerLimit(1.5);  //less than 2 copies
        at11.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        at11.setUpperLimit(2.5);  //more than 2
        ret.add(at11);

        //expression
        ExpressionType at2 = new ExpressionType();
        at2.setPlatformType(3);
        at2.setDisplayPlatformType("Expression-Genes");
        at2.setDisplayCenter("UNC");
        at2.setDisplayPlatform("AgilentG4502A_07_1");
        //defaults
        at2.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        at2.setLowerLimit(.5);   //less than half expression
        at2.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        at2.setUpperLimit(2.);   //more than double
        ret.add(at2);

        //mutation anomalies
        //all the possible mutation types

        MutationType at4 = new MutationType();
        at4.setPlatformType(9);
        at4.setDisplayPlatformType("Somatic Mutation");
        at4.setDisplayCenter("HGSC");
        at4.setDisplayPlatform("ABI");
        at4.setCategory(MutationType.Category.Missense);
        ret.add(at4);

        MutationType at5 = new MutationType();
        at5.setPlatformType(9);
        at5.setDisplayPlatformType("Somatic Mutation");
        at5.setDisplayCenter("HGSC");
        at5.setDisplayPlatform("ABI");
        at5.setCategory(MutationType.Category.Nonsense);
        ret.add(at5);

        MutationType at6 = new MutationType();
        at6.setPlatformType(9);
        at6.setDisplayPlatformType("Somatic Mutation");
        at6.setDisplayCenter("HGSC");
        at6.setDisplayPlatform("ABI");
        at6.setCategory(MutationType.Category.Frameshift);
        ret.add(at6);

        MutationType at7 = new MutationType();
        at7.setPlatformType(9);
        at7.setDisplayPlatformType("Somatic Mutation");
        at7.setDisplayCenter("HGSC");
        at7.setDisplayPlatform("ABI");
        at7.setCategory(MutationType.Category.SpliceSite);
        ret.add(at7);

        MutationType at8 = new MutationType();
        at8.setPlatformType(9);
        at8.setDisplayPlatformType("Somatic Mutation");
        at8.setDisplayCenter("HGSC");
        at8.setDisplayPlatform("ABI");
        at8.setCategory(MutationType.Category.AnyNonSilent);
        ret.add(at8);

        MutationType at9 = new MutationType();
        at9.setPlatformType(9);
        at9.setDisplayPlatformType("Somatic Mutation");
        at9.setDisplayCenter("HGSC");
        at9.setDisplayPlatform("ABI");
        at9.setCategory(MutationType.Category.Silent);
        ret.add(at9);

        //correlations

        CorrelationType ct1 = new CorrelationType();
        ct1.setAnomalyType1(at1);
        ct1.setAnomalyType2(at2);
        ret.add(ct1);

        CorrelationType ct2 = new CorrelationType();
        ct2.setAnomalyType1(at1);
        ct2.setAnomalyType2(at8);
        ret.add(ct2);

        baseColTypes = ret;
        return ret;
    }

    public void getPathwayResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException {
        (new PathwaySearchThread(filter, callback, chunkSize)).start();
    }

    public void getAnomalyResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException {
        //the search thread will call methods of the callback to send results to middle tier
        (new AnomalySearchThread(filter, callback, chunkSize, correlationCalculator)).start();
    }

    @Override
    public Results getAnomalyResults(FilterSpecifier filter) throws QueriesException {
        return null;
    }

    public SinglePathwayResults getSinglePathway(SinglePathwaySpecifier sps) throws QueriesException {
        SinglePathwayResults ret = new SinglePathwayResults();
        String id = sps.getId();
        ret.setId(id);

        //find the pathway name.
        //In real life this will query db
        String name = null;
        String displayName = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Level4QueriesMock.FILE_PATHWAYS)));
            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith(id + ",")) {
                    int comma = line.indexOf(',');
                    int comma2 = line.indexOf(',', comma + 1);
                    displayName = line.substring(comma + 1, comma2);
                    name = line.substring(comma2 + 1);
                    break;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new QueriesException(e);
        }

        ret.setName(name);
        ret.setDisplayName(displayName);

        //todo add anomaly ratios

        return ret;
    }

    //no implementation, we're getting rid of this anyway
    public Results getPivotResults(FilterSpecifier.ListBy listby, String rowName, FilterSpecifier filter) throws QueriesException {
        return null;
    }
}
