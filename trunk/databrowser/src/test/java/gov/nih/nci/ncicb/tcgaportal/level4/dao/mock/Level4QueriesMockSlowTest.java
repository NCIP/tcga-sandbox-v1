package gov.nih.nci.ncicb.tcgaportal.level4.dao.mock;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.PathwayListResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;
import junit.framework.TestCase;

import java.util.List;

/**
 * Test class for Level4QueriesMock
 *
 * @author David Nassau
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 561 $
 */
public class Level4QueriesMockSlowTest extends TestCase {

    Level4Queries dao;
    final Object mutex = new Object();

    public void setUp() {
        Level4QueriesMock daoMock = new Level4QueriesMock();
        daoMock.setCorrelationCalculator(new PearsonCorrelationCalculator());
        dao = daoMock;
    }

    public void testGetColumnTypes() throws QueriesException {
        List<ColumnType> coltypes = dao.getColumnTypes("GBM");

        for (ColumnType coltype : coltypes) {
            if (coltype instanceof AnomalyType) {
                if (coltype instanceof NonMutationAnomalyType) {
                    NonMutationAnomalyType atype = (NonMutationAnomalyType) coltype;
                    if (atype instanceof CopyNumberType) {
                        System.out.println("CopyNumberType");
                    } else {
                        System.out.println("ExpressionType");
                    }
                    System.out.println(atype.getId());
                    System.out.println(atype.getPlatformType());
                    System.out.println(atype.getLowerOperator());
                    System.out.println(atype.getLowerLimit());
                    System.out.println(atype.getUpperOperator());
                    System.out.println(atype.getUpperLimit());
                } else if (coltype instanceof MutationType) {
                    MutationType mtype = (MutationType) coltype;
                    System.out.println("MutationType");
                    System.out.println(mtype.getId());
                    System.out.println(mtype.getPlatformType());
                    System.out.println(mtype.getCategory());
                }
            } else {
                CorrelationType ctype = (CorrelationType) coltype;
                System.out.println("CorrelationType");
                System.out.println(ctype.getId());
                System.out.println(ctype.getAnomalyType1());
                System.out.println(ctype.getAnomalyType2());
            }
        }
    }

    public void testGetAnomalyResultsByGene() throws QueriesException {
        getAnomalyResults(Level4QueriesMock.Orientation.ByGene, false);
    }

    public void testGetAnomalyResultsByGeneGistic() throws QueriesException {
        getAnomalyResults(Level4QueriesMock.Orientation.ByGene, true);
    }

    public void testGetAnomalyResultsByPatient() throws QueriesException {
        getAnomalyResults(Level4QueriesMock.Orientation.ByPatient, false);
    }

    void getAnomalyResults(final Level4QueriesMock.Orientation orientation, final boolean doGistic) throws QueriesException {
        List<ColumnType> coltypes = dao.getColumnTypes("GBM");
        //take first 2 anomaly types and first correlation type
        //AnomalyType at1=null, at2=null;
        CopyNumberType cntype = null;
        MutationType muttype = null;
        CorrelationType corrtype = null;
        for (ColumnType c : coltypes) {
            if (cntype == null && c instanceof CopyNumberType) {
                cntype = (CopyNumberType) c;
            } else if (muttype == null && c instanceof MutationType) {
                muttype = (MutationType) c;
            } else if (corrtype == null && c instanceof CorrelationType) {
                corrtype = (CorrelationType) c;
            }
        }

        assertNotNull(cntype);
        assertNotNull(muttype);
        assertNotNull(corrtype);

        cntype.setPicked(true);
        muttype.setPicked(true);
        if (orientation == Level4QueriesMock.Orientation.ByGene) {
            corrtype.setPicked(true);
        }

        //declare final just so we can use in callback
        final CopyNumberType _cntype = cntype;
        final MutationType _muttype = muttype;
        final CorrelationType _corrtype = corrtype;

        if (orientation == Level4QueriesMock.Orientation.ByGene && doGistic) {
            cntype.setCalculationType(CopyNumberType.CalculationType.GISTIC);
        }

        FilterSpecifier filter = new FilterSpecifier();
//        filter.setListByGene(orientation == Level4QueriesMock.Orientation.ByGene);
        filter.setListBy(orientation == Level4QueriesMock.Orientation.ByGene ? FilterSpecifier.ListBy.Genes : FilterSpecifier.ListBy.Patients);
        filter.setColumnTypes(coltypes);

        dao.getAnomalyResults(filter, new Level4QueriesCallback() {

            public void sendFullResults(Results rs) {
                if (orientation == Level4QueriesMock.Orientation.ByGene) {
                    assertEquals(rs.getListBy(), FilterSpecifier.ListBy.Genes);
                } else {
                    assertEquals(rs.getListBy(), FilterSpecifier.ListBy.Patients);
                }
                assertEquals(_cntype, rs.getColumnTypes().get(0));
                assertEquals(_muttype, rs.getColumnTypes().get(1));
                if (orientation == Level4QueriesMock.Orientation.ByGene) {
                    assertEquals(_corrtype, rs.getColumnTypes().get(2));
                }

                System.out.println("columns:");
                for (ColumnType ct : rs.getColumnTypes()) {
                    System.out.println(ct.getDisplayName());
                }
                System.out.println("results:");
                int rows = rs.getActualRowCount();
                for (int i = 0; i < rows; i++) {
                    ResultRow row = rs.getRow(i);
                    if (doGistic) {
                        assertTrue(row.getColumnResults()[0] instanceof ResultDouble);
                    } else {
                        assertTrue(row.getColumnResults()[0] instanceof AnomalyResultRatio);
                    }
                    assertTrue(row.getColumnResults()[1] instanceof AnomalyResultRatio);
                    if (orientation == Level4QueriesMock.Orientation.ByGene) {
                        assertTrue(row.getColumnResults()[2] instanceof ResultDouble);
                    }
                    System.out.println(row.toString());
                }
                synchronized(mutex) {
                    mutex.notifyAll();
                }
            }

            public Results getPageMeta() {
                return null;
            }

            public void sendException(QueriesException ex) {
                fail(ex.getMessage());
            }

            public void dieYoung() {
                // n/a
            }

            public Results getPage(int pageno) throws QueriesException {
                return null; // not used in this test
            }

            public void sortResults(SortSpecifier sortspec) {
                // n/a
            }

            public void setRowsPerPage(int rowsPerPage) {
                // n/a
            }

            public Results getResultSet() {
                return null;
            }
        });

        //this prevents the method from completing until the callback has received all the results
        synchronized (mutex) {
            try {
                mutex.wait();
            } catch (InterruptedException e) {
                // empty
            }
        }
    }

    public void testGetDiseases() throws QueriesException {
        List<Disease> diseases = dao.getDiseases();
        assertEquals("GBM", diseases.get(0).getId());
        assertEquals("OV", diseases.get(1).getId());
    }

    public void testGetPathways() throws QueriesException {
        List<ColumnType> coltypes = dao.getColumnTypes("GBM");
        //take first 2 anomaly types and first correlation type
        //AnomalyType at1=null, at2=null;
        CopyNumberType cntype = null;
        MutationType muttype = null;
        for (ColumnType c : coltypes) {
            if (cntype == null && c instanceof CopyNumberType) {
                cntype = (CopyNumberType) c;
            } else if (muttype == null && c instanceof MutationType) {
                muttype = (MutationType) c;
            }
        }

        assertNotNull(cntype);
        assertNotNull(muttype);

        cntype.setPicked(true);
        muttype.setPicked(true);

        //declare final just so we can use in callback
        final CopyNumberType _cntype = cntype;
        final MutationType _muttype = muttype;

        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(coltypes);

        dao.getPathwayResults(filter, new Level4QueriesCallback() {

            public void sendFullResults(Results rs) {
                assertEquals(_cntype, rs.getColumnTypes().get(0));
                assertEquals(_muttype, rs.getColumnTypes().get(1));

                System.out.println("columns:");
                for (ColumnType ct : rs.getColumnTypes()) {
                    System.out.println(ct.getDisplayName());
                }
                System.out.println("results:");
                int rows = rs.getActualRowCount();
                for (int i = 0; i < rows; i++) {
                    ResultRow row = rs.getRow(i);
                    assertTrue(row.getColumnResults()[0] instanceof PathwayListResultValue);
                    assertTrue(row.getColumnResults()[1] instanceof PathwayListResultValue);
                    System.out.println(row.toString());
                }

                synchronized (mutex) {
                    mutex.notifyAll();
                }
            }

            public Results getPageMeta() {
                return null;
            }

            public void sendException(QueriesException ex) {
                fail(ex.getMessage());
            }

            public void dieYoung() {
                // n/a
            }

            public Results getPage(int pageno) throws QueriesException {
                return null;  // not used in this test
            }

            public void sortResults(SortSpecifier sortspec) {
                // n/a
            }

            public void setRowsPerPage(int rowsPerPage) {
                // n/a
            }

            public Results getResultSet() {
                return null; //not used
            }

        });

        //this prevents the method from completing until the callback has received all the results
        synchronized (mutex) {
            try {
                mutex.wait();
            } catch (InterruptedException e) {
            }
        }
    }

}
