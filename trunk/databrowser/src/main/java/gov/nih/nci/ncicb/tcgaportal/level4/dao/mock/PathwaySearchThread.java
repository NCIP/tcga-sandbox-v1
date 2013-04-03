package gov.nih.nci.ncicb.tcgaportal.level4.dao.mock;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.PathwayListResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Feb 27, 2009
 * Time: 4:02:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PathwaySearchThread extends Thread {
    FilterSpecifier filter;
    Level4QueriesCallback callback;
    Exception exception;
    int chunkSize;

    public PathwaySearchThread(FilterSpecifier filter, Level4QueriesCallback callback, int chunkSize) {
        this.filter = filter;
        this.callback = callback;
        this.chunkSize = chunkSize;
    }

    public void run() {
        try {
            List<ResultRow> mockRows = makeMockRows();
            int totalRows = mockRows.size();
            boolean end = false;

            Results fullResults = new Results(filter);
            for (int irow = 0; irow < totalRows; irow++) {
                ResultRow row = mockRows.get(irow);
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

    //much of this copied from AnomalySearchThread. Do we care about the duplication? maybe not
    public List<ResultRow> makeMockRows() throws QueriesException {
        Map<String, ResultRow> rows = new HashMap<String, ResultRow>();

        List<ColumnType> pickedColumns = new ArrayList<ColumnType>();
        for (ColumnType ctype : filter.getColumnTypes()) {
            if (ctype.isPicked()) {
                pickedColumns.add(ctype);
            }
        }

        executePathwayFilter(rows, pickedColumns);

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

    //for mock, we'll just read in a list of genes or patients and make up random ratios
    //in reality, if there's a gene list needs to find all pathways for the genes in that list.
    //If there's a patient list, needs to generate ratios based on those patients.
    void executePathwayFilter(Map<String, ResultRow> rows, List<ColumnType> columnTypes) throws QueriesException {
        try {
            Random rnd = new Random();

            String idfile = Level4QueriesMock.FILE_PATHWAYS;

            int denominator = Level4QueriesMock.TOTAL_PATIENTS;

            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(idfile)));

            String line = reader.readLine();
            while (line != null) {
                int comma = line.indexOf(',');
                int comma2 = line.indexOf(',', comma + 1);
                String pathId = line.substring(0, comma);
                String displayName = line.substring(comma + 1, comma2);
                ResultRow arow = rows.get(pathId);
                if (arow == null) {
                    arow = new ResultRow();
                    //use the display name as the row id, and set the pathway id in an annotation
                    //this will sort the results by display name automatically
                    arow.setName(displayName);
                    arow.addRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID, pathId);

                    arow.setColumnResults(new ResultValue[columnTypes.size()]);
                    rows.put(line, arow);

                }

                //for each column make an avg and a max result value
                for (int i = 0; i < columnTypes.size(); i++) {
                    ColumnType ctype = (ColumnType) columnTypes.get(i);
                    if (ctype instanceof AnomalyType) {
                        AnomalyType atype = (AnomalyType) columnTypes.get(i);
                        if (atype instanceof NonMutationAnomalyType) {
                            //for CN, there's the option of calculating gistic result
                            if (atype instanceof CopyNumberType) {
                                CopyNumberType cntype = (CopyNumberType) atype;
                                if (cntype.getCalculationType() == CopyNumberType.CalculationType.GISTIC) {
                                    //make gistic results for both average and max
                                    PathwayListResultValue plrv = new PathwayListResultValue();
                                    plrv.setMaximum(makeRandomGistic(rnd));
                                    plrv.setAverage(makeRandomGistic(rnd));
                                    arow.getColumnResults()[i] = plrv;
                                } else {
                                    //make ratio results for both average and max
                                    PathwayListResultValue plrv = new PathwayListResultValue();
                                    plrv.setMaximum(makeRandomRatio(rnd, denominator));
                                    plrv.setAverage(makeRandomRatio(rnd, denominator));
                                    arow.getColumnResults()[i] = plrv;
                                }
                            } else {
                                //expression - same thing
                                PathwayListResultValue plrv = new PathwayListResultValue();
                                plrv.setMaximum(makeRandomRatio(rnd, denominator));
                                plrv.setAverage(makeRandomRatio(rnd, denominator));
                                arow.getColumnResults()[i] = plrv;
                            }
                        } else if (atype instanceof MutationType) {
                            //mutation - same deal
                            PathwayListResultValue plrv = new PathwayListResultValue();
                            plrv.setMaximum(makeRandomRatio(rnd, denominator));
                            plrv.setAverage(makeRandomRatio(rnd, denominator));
                            arow.getColumnResults()[i] = plrv;
                        }
                    } else {
                        //correlation - make max and average
                        PathwayListResultValue plrv = new PathwayListResultValue();
                        plrv.setMaximum(makeRandomCorrelation(rnd));
                        plrv.setAverage(makeRandomCorrelation(rnd));
                        arow.getColumnResults()[i] = plrv;
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new QueriesException(e);
        }
    }

    AnomalyResultRatio makeRandomRatio(Random rnd, int denominator) {
        AnomalyResultRatio ret = new AnomalyResultRatio();
        ret.setAffected(rnd.nextInt(denominator));
        ret.setTotal(denominator);
        return ret;
    }

    ResultValue makeRandomGistic(Random rnd) {
        ResultDouble ret = new ResultDouble();
        ret.setValue(rnd.nextDouble());
        return ret;
    }

    ResultDouble makeRandomCorrelation(Random rnd) {
        ResultDouble ret = new ResultDouble();
        ret.setValue(rnd.nextFloat());
        ret.addValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE, rnd.nextDouble());
        return ret;
    }

}
