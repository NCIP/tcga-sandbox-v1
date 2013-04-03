package gov.nih.nci.ncicb.tcgaportal.level4.dao.mock;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;
import gov.nih.nci.ncicb.tcgaportal.level4.util.CorrelationCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

/**
 * @author David Nassau
 *         Last updated by: $Author: nassaud $
 * @version $Rev: 5377 $
 */
public class AnomalySearchThread extends Thread {
    FilterSpecifier filter;
    Level4QueriesCallback callback;
    Exception exception;
    int chunkSize;
    CorrelationCalculator correlationCalculator;

    public AnomalySearchThread(FilterSpecifier filter, Level4QueriesCallback callback, int chunkSize, CorrelationCalculator cc) {
        this.filter = filter;
        this.callback = callback;
        this.chunkSize = chunkSize;
        this.correlationCalculator = cc;
    }

    public void run() {
        try {
            List<ResultRow> mockRows = makeMockRows();
            int totalRows = mockRows.size();
            if (totalRows == 0) {
                //send an empty result object
                Results empty = new Results(filter);
                empty.setTotalRowCount(0);
                empty.setFinalRowCount(true);
                //callback.sendNextChunk(empty, true);
                callback.sendFullResults(empty);
            }

            boolean end = false;

            Results fullResults = new Results(filter);
            for (int irow = 0; irow < totalRows; irow++) {
                ResultRow row = mockRows.get(irow);
                Map<String, Serializable> annot = null;
                fullResults.addRow(row);
            }
            fullResults.setTotalRowCount(totalRows);
            fullResults.setFinalRowCount(true);
            callback.sendFullResults(fullResults);

        } catch (QueriesException e) {
            e.printStackTrace();

            //send exception to caller to deal with it
            callback.sendException(e);
        } catch (Exception e) { //last chance for unexpected runtime exception
            e.printStackTrace();
            callback.sendException(new QueriesException(e));
        }
    }

    public List<ResultRow> makeMockRows() throws QueriesException {
        Map<String, ResultRow> rows = new HashMap<String, ResultRow>();

        List<ColumnType> pickedColumns = new ArrayList<ColumnType>();
        for (ColumnType ctype : filter.getColumnTypes()) {
            if (ctype.isPicked()) {
                pickedColumns.add(ctype);
            }
        }

//        Level4QueriesMock.Orientation orientation = (filter.isListByGene() ? Level4QueriesMock.Orientation.ByGene : Level4QueriesMock.Orientation.ByPatient);
        Level4QueriesMock.Orientation orientation;  //todo maybe get rid of this enum since it overlaps with FilterSpecifier.ListBy
        if (filter.getListBy() == FilterSpecifier.ListBy.Patients) {
            orientation = Level4QueriesMock.Orientation.ByPatient;
        } else {
            orientation = Level4QueriesMock.Orientation.ByGene;
        }
        String idList = (orientation == Level4QueriesMock.Orientation.ByGene ? filter.getGeneList() : filter.getPatientList());
        idList = normalizeList(idList);

        executeAnomalyFilter(rows, pickedColumns, orientation, idList);
        if (orientation == Level4QueriesMock.Orientation.ByGene) {
            executeCorrelations(rows, pickedColumns, idList);
        }

        //sort results by patient or gene id
        SortedSet<ResultRow> sortedRows = new TreeSet<ResultRow>(new Comparator<ResultRow>() {
            public int compare(ResultRow o1, ResultRow o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        sortedRows.addAll(rows.values());

        List<ResultRow> ret = new ArrayList<ResultRow>(sortedRows.size());
        ret.addAll(sortedRows);
        return ret;
    }

    private String normalizeList(String list) {
        if (list == null || list.trim().length() == 0) {
            return null;
        }
        StringBuffer ret = new StringBuffer();
        ret.append(',');
        StringTokenizer tokens = new StringTokenizer(list, ",; \t\n");
        while (tokens.hasMoreTokens()) {
            ret.append(tokens.nextToken().trim().toUpperCase()).append(',');
        }
        return ret.toString();
    }

    //for mock, we'll just read in a list of genes or patients and make up random ratios
    void executeAnomalyFilter(Map<String, ResultRow> rows, List<ColumnType> columnTypes, Level4QueriesMock.Orientation orientation, String idList) throws QueriesException {
        try {
            Random rnd = new Random(0);

            String idfile;
            int denominator;
            if (orientation == Level4QueriesMock.Orientation.ByGene) {
                idfile = Level4QueriesMock.FILE_GENES;
                denominator = Level4QueriesMock.TOTAL_PATIENTS;
            } else {
                idfile = Level4QueriesMock.FILE_PATIENTS;
                denominator = Level4QueriesMock.TOTAL_GENES;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(idfile)));

            int iline = 0;
            String id = reader.readLine();
            while (id != null) {
                boolean goahead = true;
                if (idList != null && idList.length() > 0 && !idList.contains("," + id + ",")) {
                    goahead = false;
                }

                if (goahead) {
                    ResultRow arow = rows.get(id);
                    if (arow == null) {
                        arow = new ResultRow();
//                        arow.id = id;
                        arow.setName(id);
//                        arow.anomalyResults = new ResultValue[columnTypes.size()];
                        arow.setColumnResults(new ResultValue[columnTypes.size()]);
                        rows.put(id, arow);

                        //row annotations
                        Map<String, Serializable> annot = new HashMap<String, Serializable>();
                        //every 4 make cnv
                        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_CNV, iline % 4 == 0);
                        //location
                        String chrom = (iline % 23 + 1) + "";
                        long start = iline * 10000;
                        long stop = iline * 10000 + 3000;
                        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM, chrom);
                        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START, start);
                        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP, stop);
                        arow.setRowAnnotations(annot);
                    }

                    for (int i = 0; i < columnTypes.size(); i++) {
                        ColumnType ctype = (ColumnType) columnTypes.get(i);
                        if (ctype instanceof AnomalyType) { //exclude the correlations
                            AnomalyType atype = (AnomalyType) columnTypes.get(i);
                            if (atype instanceof NonMutationAnomalyType) {
                                //for CN, there's the option of calculating gistic result
                                if (atype instanceof CopyNumberType) {
                                    CopyNumberType cntype = (CopyNumberType) atype;
                                    if (cntype.getCalculationType() == CopyNumberType.CalculationType.GISTIC) {
                                        ResultDouble rd = new ResultDouble();
                                        rd.setValue(rnd.nextDouble());
                                        arow.getColumnResults()[i] = rd;
                                    } else {
                                        AnomalyResultRatio rr = new AnomalyResultRatio();
                                        rr.setAffected(rnd.nextInt(denominator));
                                        rr.setTotal(denominator);
                                        //every 7 make unpaired
                                        Map<String, Serializable> annot = new HashMap<String, Serializable>();
                                        annot.put(AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, iline % 7 == 0);
                                        rr.setValueAnnotations(annot);
//                                        arow.anomalyResults[i] = rr;
                                        arow.getColumnResults()[i] = rr;
                                    }
                                } else {
                                    //expression
                                    AnomalyResultRatio rr = new AnomalyResultRatio();  //don't have a special subclass for expression - not yet anyway
                                    rr.setAffected(rnd.nextInt(denominator));
                                    rr.setTotal(denominator);
//                                    arow.anomalyResults[i] = rr;
                                    arow.getColumnResults()[i] = rr;
                                }
                            } else if (atype instanceof MutationType) {
                                AnomalyResultRatio rr = new AnomalyResultRatio();
                                rr.setAffected(rnd.nextInt(denominator));
                                rr.setTotal(denominator);
//                                arow.anomalyResults[i] = rr;
                                arow.getColumnResults()[i] = rr;
                            }
                        }
                    }
                }
                id = reader.readLine();
                iline++;
            }
        } catch (IOException e) {
            throw new QueriesException(e);
        }
    }

    void executeCorrelations(Map<String, ResultRow> rows, List<ColumnType> columnTypes, String idList) {

        //for each gene row
        for (String key : rows.keySet()) {
            //check if it's in the user-specified gene list, or if there's no list
            boolean goahead = true;
            if (idList != null && idList.length() > 0 && !idList.contains("," + key + ",")) {
                goahead = false;
            }
            if (goahead) {
                ResultRow row = rows.get(key);
                List<ResultDouble> correlations = new ArrayList<ResultDouble>();

                //for each correlation column
                for (int icolumn = 0; icolumn < columnTypes.size(); icolumn++) {
                    ColumnType ctype = columnTypes.get(icolumn);
                    if (ctype instanceof CorrelationType) {
                        row.getColumnResults()[icolumn] = new ResultDouble(calcRandomCorrelation());
                    }
                }
            }
        }
    }

    //generate random lists and do a real correlation on them (resulting correlation should be near 0)
    Random rnd = new Random(0);

    float calcRandomCorrelation() {
        float[] scores1 = new float[Level4QueriesMock.TOTAL_PATIENTS];
        float[] scores2 = new float[Level4QueriesMock.TOTAL_PATIENTS];
        for (int i = 0; i < Level4QueriesMock.TOTAL_PATIENTS; i++) {
            scores1[i] = rnd.nextFloat(); //represents the actual result for one patient for one gene
        }
        for (int i = 0; i < Level4QueriesMock.TOTAL_PATIENTS; i++) {
            scores2[i] = rnd.nextFloat(); //represents the actual result for one patient for one gene
        }

        return correlationCalculator.calculateCorrelation(scores1, scores2);
    }

}

