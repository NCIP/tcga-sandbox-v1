package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Version of DAO class that exposes add/getGeneticElement for testing purposes.
 * Renamed "Tstable.." from "Testable.." to prevent ant task for trying to run it as a unit test.
 */
public class MockLevel4QueriesJDBCImpl extends Level4QueriesJDBCImpl {
    Map<Integer, ResultElement> geneticElements = new HashMap<Integer, ResultElement>();

    public MockLevel4QueriesJDBCImpl(DataSource datasource) {
        super(1000, new PearsonCorrelationCalculator(), new FishersExactImpl(), datasource);
    }

    public void addGeneticElement(int id, int methylId, int mirnaId) {
        ResultElement geneticElement = new ResultElement();
        geneticElement.id = id;
        geneticElement.methylationTargetId = methylId;
        geneticElement.miRNAId = mirnaId;
        geneticElements.put(id, geneticElement);
    }

    protected ResultElement getGeneticElement(int id) {
        if (geneticElements == null || geneticElements.size() == 0) {
            return super.getGeneticElement(id);
        } else {
            return geneticElements.get(id);
        }
    }
}