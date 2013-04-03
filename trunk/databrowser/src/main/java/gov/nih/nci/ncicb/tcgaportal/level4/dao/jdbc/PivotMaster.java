package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DAO class for handling pivot operations.  This does not extend the Level4Queries interface,
 * but is called as a utility class from the main DAO class. The pivoted result set contains
 * the same columns as the source result set, with the exception that correlation columns are not included.
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

//PivotMaster sounds like an exercise machine, that' s one reason I like this name..
public class PivotMaster extends SimpleJdbcDaoSupport {

    //friend access to be set directly by DAO class
    Level4QueriesJDBCImpl dao;

    /**
     * Returns a result set for a pivot operation
     *
     * @param sourceListby  the ListBy of the source result set. For example, if pivoting from genes to patients, is set to genes.
     * @param sourceRowName gene symbol or patient Id
     * @param filter        FilterSpecifier for the source result set, or it might be the source result set itself since Results extends FilterSpecifier
     * @return new Results
     * @throws QueriesException  exception which is thrown if anything goes wrong, unless it's a runtime exception
     */
    public Results getPivotResults(FilterSpecifier.ListBy sourceListby, String sourceRowName, FilterSpecifier filter) throws QueriesException {
        checkInputs(sourceListby, sourceRowName, filter);

        //get all column values into memory
        List<Map<String, ResultValue>> resultValueMaps = searchColumnValues(sourceListby, sourceRowName, filter);

        //derive a single list of genes/patients and sort
        boolean pivotListByGenes = sourceListby == FilterSpecifier.ListBy.Patients;
        List<Level4QueriesJDBCImpl.ResultElement> elements = dao.combineElements(pivotListByGenes, resultValueMaps);
        Collections.sort(elements);

        //make pivoted version of the filter specifier
        FilterSpecifier pivotedFilter = makePivotedFilter(filter);
        boolean isMirnaSearch = isMirnaSearch(filter, pivotedFilter.getListBy());
        boolean isMethSearch = isMethSearch(filter, pivotedFilter.getListBy());

        //build up the results object which will go to the client
        int icols = resultValueMaps.size();
        Results result = new Results(pivotedFilter);
        for (Level4QueriesJDBCImpl.ResultElement element : elements) {
            String rowId = element.getIdString();
            String rowName = element.toString();
            ResultValue[] colVals = new ResultValue[icols];
            for (int i = 0; i < icols; i++) {
                ResultValue rv = resultValueMaps.get(i).get(rowId);
                if (rv == null) {
                    rv = new ResultBlank();
                }
                colVals[i] = rv;
            }
            Map<String, Serializable> annotations = buildRowAnnotations(sourceListby, filter, isMirnaSearch, isMethSearch, element);
            result.addRow(rowName, colVals, annotations);
        }

        result.setRowsPerPage(25);
        int rows = result.getActualRowCount();
        result.setGatheredRows(rows);
        result.setTotalRowCount(rows);

        int pages = (int)Math.ceil( rows / (double) result.getRowsPerPage() );
        result.setTotalPages(pages);
        result.setGatheredPages(pages);
        result.setFinalRowCount(true);

        return result;
    }

    private FilterSpecifier makePivotedFilter(FilterSpecifier filter) {
        FilterSpecifier pivotedFilter = new FilterSpecifier(filter);
        FilterSpecifier.ListBy pivotedListBy = (filter.getListBy() == FilterSpecifier.ListBy.Genes ? FilterSpecifier.ListBy.Patients : FilterSpecifier.ListBy.Genes);
        pivotedFilter.setListBy(pivotedListBy);
        //remove any correlation column
        for (int i = pivotedFilter.getColumnTypes().size() - 1; i >= 0; i--) {
            if (pivotedFilter.getColumnTypes().get(i) instanceof CorrelationType) {
                pivotedFilter.getColumnTypes().remove(i);
            }
        }
        return pivotedFilter;
    }

    private void checkInputs(FilterSpecifier.ListBy sourceListby, String sourceRowName, FilterSpecifier filter) throws QueriesException {
        if (filter == null) {
            throw new QueriesException("Filter cannot be null");
        }
        if (filter.getPickedColumns().size() == 0) {
            throw new QueriesException("No columns were picked.");
        }
        if (filter.getPickedColumns().size() > 1) {
            throw new QueriesException("Only one column can be pivoted");
        }
        if (!(filter.getPickedColumns().get(0) instanceof AnomalyType)) {
            throw new QueriesException("Can only pivot anomaly columns (not correlation)");
        }
        if (sourceListby != FilterSpecifier.ListBy.Genes && sourceListby != FilterSpecifier.ListBy.Patients) {
            throw new QueriesException("sourceListBy must be Genes or Patients");
        }
        if (sourceRowName == null || sourceRowName.length() == 0) {
            throw new QueriesException("No sourceRowName was specified");
        }
    }

    private List<Map<String, ResultValue>> searchColumnValues(FilterSpecifier.ListBy sourceListby, String sourceRowName, FilterSpecifier filter) throws QueriesException {
        List<Integer[]> elementLists = dao.parseLists(filter);
        Integer[] geneIds = elementLists.get(0);
        Integer[] patientIds = elementLists.get(1);

        //note: even though we are only pivoting one column, the call to dao.combineElements expects a list of maps, so we return it in that form
        List<Map<String, ResultValue>> resultValueMaps = new ArrayList<Map<String, ResultValue>>();
        AnomalyType atype = (AnomalyType)filter.getPickedColumns().get(0);
        resultValueMaps.add(getPivotResultColumn(sourceListby, sourceRowName, atype, geneIds, patientIds));
        return resultValueMaps;
    }

    private Map<String, Serializable> buildRowAnnotations(FilterSpecifier.ListBy sourceListby, FilterSpecifier filter, boolean mirnaSearch, boolean methSearch, Level4QueriesJDBCImpl.ResultElement element) {
        Level4QueriesJDBCImpl.SearchType searchType = (sourceListby == FilterSpecifier.ListBy.Genes ? Level4QueriesJDBCImpl.SearchType.ANOMALY_BYPATIENT : Level4QueriesJDBCImpl.SearchType.ANOMALY_BYGENE);
        Map<String, Serializable> annotationsCopy = dao.copyAnnotations(element, filter, searchType);
        if (mirnaSearch) {
            Level4QueriesJDBCImpl.ResultElement mirnaElement = dao.getGeneticElement(element.miRNAId);
            if (mirnaElement != null) {
                annotationsCopy.put(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA, mirnaElement.name);
            }
        }
        if (methSearch) {
            Level4QueriesJDBCImpl.ResultElement methElement = dao.getGeneticElement(element.methylationTargetId);
            if (methElement != null) {
                annotationsCopy.put(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE, methElement.name);
            }
        }
        if (annotationsCopy.isEmpty()) {
            annotationsCopy = null;
        }
        return annotationsCopy;
    }

    private boolean isMirnaSearch(FilterSpecifier filter, FilterSpecifier.ListBy pivotedListBy) {
        boolean mirna = false;
        if (pivotedListBy == FilterSpecifier.ListBy.Genes) {
            for (ColumnType col : filter.getColumnTypes()) {
                if (col.isPicked() && col instanceof AnomalyType) {
                    if (((AnomalyType) col).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                        mirna = true;
                        break;
                    }
                }
            }
        }
        return mirna;
    }

    private boolean isMethSearch(FilterSpecifier filter, FilterSpecifier.ListBy pivotedListBy) {
        boolean meth = false;
        if (pivotedListBy == FilterSpecifier.ListBy.Genes) {
            for (ColumnType col : filter.getColumnTypes()) {
                if (col.isPicked() && col instanceof MethylationType) {
                    meth = true;
                    break;
                }
            }
        }
        return meth;
    }

    private Map<String, ResultValue> getPivotResultColumn(FilterSpecifier.ListBy sourceListby, String sourceRowName, AnomalyType colType, Integer[] geneIds, Integer[] patientIds) throws QueriesException {
        Map<String, ResultValue> resultMap;
        if (sourceListby == FilterSpecifier.ListBy.Genes) {
            if (colType.getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                resultMap = getPivotResultColumnForGene_GeneColumn(sourceRowName, colType, patientIds);
            } else { //miRNA or meth
                resultMap = getPivotResultColumnForGene_miRNAOrMethColumn(sourceRowName, colType, patientIds);
            }
        } else {
            if (colType.getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                resultMap = getPivotResultColumnForPatient_GeneColumn(sourceRowName, colType, geneIds);
            } else { //miRNA or meth
                resultMap = getPivotResultColumnForPatient_miRNAOrMethColumn(sourceRowName, colType, geneIds);
            }
        }
        return resultMap;
    }

    private String buildGeneInClause(Integer[] geneIds, List<Object> bindVars) {
        StringBuilder clause = new StringBuilder();
        if (geneIds.length > 0) {
            clause.append("and ge.genetic_element_id in (");
            for (int i = 0; i < geneIds.length; i++) {
                if (i > 0) {
                    clause.append(", ");
                }
                clause.append("?");
                bindVars.add(geneIds[i]);
            }
            clause.append(") ");
        }
        return clause.toString();
    }

    private String buildPatientInClause(Integer[] patientIds, List<Object> bindVars) {
        StringBuilder clause = new StringBuilder();
        if (patientIds.length > 0) {
            clause.append("and s.patient_id in (");
            for (int i = 0; i < patientIds.length; i++) {
                if (i > 0) {
                    clause.append(", ");
                }
                clause.append("?");
                bindVars.add(patientIds[i]);
            }
            clause.append(") ");
        }
        return clause.toString();
    }

    private Map<String, ResultValue> getPivotResultColumnForPatient_miRNAOrMethColumn(String patient, AnomalyType colType, Integer[] geneIds) throws QueriesException {
        List<Object> bindVars = new ArrayList<Object>();
        StringBuilder query = new StringBuilder();
        query.append("select t.target_genetic_element_id as element_id, av.genetic_element_id as other_id, av.anomaly_value ")
                .append("FROM L4_sample s, L4_anomaly_value av , L4_target t ");
        if (geneIds.length > 0) {
            query.append(", L4_genetic_element ge ");
        }
        query.append("WHERE ").append(dao.buildDatasetClause(colType, bindVars, "av"))
            .append(" and av.sample_id=s.sample_id ")
            .append("and av.genetic_element_id=t.source_genetic_element_id and s.patient = ? ")
            .append(buildValueLimitClause(colType));
        bindVars.add(patient);
        if (geneIds.length > 0) {
            query.append("and ge.genetic_element_id=t.target_genetic_element_id ");
            query.append(buildGeneInClause(geneIds, bindVars));
        }
        ProcessLogger logger = dao.logger;
        logger.logDebug("query==" + query.toString());
        for (int i=0; i<bindVars.size(); i++) {
            logger.logDebug(bindVars.get(i).toString());
        }
        final Map<String, ResultValue> resultMap = new HashMap<String, ResultValue>();
        final boolean isMIRNA = (colType.getGeneticElementType() == AnomalyType.GeneticElementType.miRNA);
        getJdbcTemplate().query(query.toString(), bindVars.toArray(), new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                ResultValue rv = new ResultDouble(resultSet.getDouble("anomaly_value"));
                String idStr;
                if (isMIRNA) {
                    idStr = dao.makeIdString(resultSet.getInt("element_id"), 0, resultSet.getInt("other_id"));
                } else { //meth
                    idStr = dao.makeIdString(resultSet.getInt("element_id"), resultSet.getInt("other_id"), 0);
                }
                resultMap.put(idStr, rv);
            }
        });
        return resultMap;
    }

    private Map<String, ResultValue> getPivotResultColumnForPatient_GeneColumn(String patient, AnomalyType colType, Integer[] geneIds) throws QueriesException {
        List<Object> bindVars = new ArrayList<Object>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT av.genetic_element_id, av.anomaly_value ")
                .append("FROM L4_sample s, L4_anomaly_value av, L4_genetic_element ge ")
                .append("WHERE ").append(dao.buildDatasetClause(colType, bindVars, "av")).append(" ")
                .append("and av.sample_id=s.sample_id and ge.genetic_element_id=av.genetic_element_id ")
                .append("AND s.patient = ? ")
                .append(buildValueLimitClause(colType));
        bindVars.add(patient);
        if (geneIds.length > 0) {
            query.append(buildGeneInClause(geneIds, bindVars));
        }
        ProcessLogger logger = dao.logger;
        logger.logDebug("query==" + query.toString());
        for (int i=0; i<bindVars.size(); i++) {
            logger.logDebug(bindVars.get(i).toString());
        }

        final Map<String, ResultValue> resultMap = new HashMap<String, ResultValue>();
        getJdbcTemplate().query(query.toString(), bindVars.toArray(), new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                ResultValue rv = new ResultDouble(resultSet.getDouble("anomaly_value"));
                String idStr = dao.makeIdString(resultSet.getInt("genetic_element_id"));
                resultMap.put(idStr, rv);
            }
        });
        return resultMap;
    }

    private Map<String, ResultValue> getPivotResultColumnForGene_miRNAOrMethColumn(String geneticElement, AnomalyType colType, Integer[] patientIds) throws QueriesException {
        List<Object> bindVars = new ArrayList<Object>();
        StringBuilder query = new StringBuilder();
        query.append("select distinct s.patient_id, av.anomaly_value, dss.is_paired ")
            .append("from L4_sample s, L4_anomaly_value av, L4_genetic_element ge, L4_data_set_sample dss ")
            .append("where ").append(dao.buildDatasetClause(colType, bindVars, "av"))
            .append("and av.sample_id=s.sample_id ")
            .append("and dss.sample_id=s.sample_id ")
            .append("and dss.anomaly_data_set_id=av.anomaly_data_set_id ")
            .append("and av.genetic_element_id=ge.genetic_element_id ")
            .append("and ge.genetic_element_name=? ")
            .append(buildValueLimitClause(colType));
        bindVars.add(geneticElement);
        if (patientIds.length > 0) {
            query.append(buildPatientInClause(patientIds, bindVars));
        }

        ProcessLogger logger = dao.logger;
        logger.logDebug("query==" + query.toString());
        for (int i=0; i<bindVars.size(); i++) {
            logger.logDebug(bindVars.get(i).toString());
        }

        final Map<String, ResultValue> resultMap = new HashMap<String, ResultValue>();
        getJdbcTemplate().query(query.toString(), bindVars.toArray(), new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                ResultValue rv = new ResultDouble(resultSet.getDouble("anomaly_value"));
                int paired = resultSet.getInt("is_paired");
                if (paired != 0) {
                    Map<String, Serializable> annot = new HashMap<String, Serializable>();
                    annot.put(AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, true);
                    rv.setValueAnnotations(annot);
                }
                String idStr = dao.makeIdString(resultSet.getInt("patient_id"));
                resultMap.put(idStr, rv);
            }
        });
        return resultMap;
    }

    private Map<String, ResultValue> getPivotResultColumnForGene_GeneColumn(String gene, AnomalyType colType, Integer[] patientIds) throws QueriesException {
        List<Object> bindVars = new ArrayList<Object>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT s.patient_id, dss.is_paired, av.anomaly_value ")
                .append("FROM L4_sample s, L4_anomaly_value av, L4_data_set_sample dss, L4_genetic_element ge ")
                .append("WHERE ").append(dao.buildDatasetClause(colType, bindVars, "av")).append(" ")
                .append("AND av.sample_id=s.sample_id AND dss.sample_id=s.sample_id ")
                .append("AND dss.anomaly_data_set_id=av.anomaly_data_set_id AND ge.genetic_element_id=av.genetic_element_id ")
                .append("AND ge.genetic_element_name = ? ")
                .append(buildValueLimitClause(colType));
        bindVars.add(gene);
        if (patientIds.length > 0) {
            query.append(buildPatientInClause(patientIds, bindVars));
        }

        ProcessLogger logger = dao.logger;
        logger.logDebug("query==" + query.toString());
        for (int i=0; i<bindVars.size(); i++) {
            logger.logDebug(bindVars.get(i).toString());
        }

        final Map<String, ResultValue> resultMap = new HashMap<String, ResultValue>();
        getJdbcTemplate().query(query.toString(), bindVars.toArray(), new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                ResultValue rv = new ResultDouble(resultSet.getDouble("anomaly_value"));
                int paired = resultSet.getInt("is_paired");
                if (paired != 0) {
                    Map<String, Serializable> annot = new HashMap<String, Serializable>();
                    annot.put(AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, true);
                    rv.setValueAnnotations(annot);
                }
                String idStr = dao.makeIdString(resultSet.getInt("patient_id"));
                resultMap.put(idStr, rv);
            }
        });
        return resultMap;
    }

    private String buildValueLimitClause(AnomalyType colType) throws QueriesException {
        StringBuilder clause = new StringBuilder();
        if (colType instanceof NonMutationAnomalyType) {
            NonMutationAnomalyType nmat = (NonMutationAnomalyType) colType;
            UpperAndLowerLimits.Operator lowerOp = nmat.getLowerOperator();
            UpperAndLowerLimits.Operator upperOp = nmat.getUpperOperator();
            if (lowerOp != UpperAndLowerLimits.Operator.None || upperOp != UpperAndLowerLimits.Operator.None) {
                clause.append("and (");
                if (lowerOp != UpperAndLowerLimits.Operator.None) {
                    clause.append("av.anomaly_value ")
                            .append(nmat.getLowerOperator().toString()).append(" ")
                            .append(nmat.getLowerLimit()).append(" ");
                    if (upperOp != UpperAndLowerLimits.Operator.None) {
                        clause.append(" or ");
                    }
                }

                if (upperOp != UpperAndLowerLimits.Operator.None) {
                    clause.append("av.anomaly_value ")
                            .append(nmat.getUpperOperator().toString()).append(" ")
                            .append(nmat.getUpperLimit()).append(" ");
                }
                clause.append(") ");
            }
        } else { //mutation
            clause.append("and av.anomaly_value > 0 ");
        }
        return clause.toString();
    }

}
