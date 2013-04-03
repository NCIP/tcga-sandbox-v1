/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Disease;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterChromRegion;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AggregateMutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MethylationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.NonMutationAnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.CorrelationCalculator;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExact;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JDBC implementation of Level4Queries interface.  Is responsible for fetching all needed information from
 * the database
 *
 * @author Jessica Chen
 *         Last updated by: $Author: alonsos $
 * @version $Rev: 10317 $
 */

//todo  class is big, break into multiple classes
public class Level4QueriesJDBCImpl extends SimpleJdbcDaoSupport implements Level4Queries {
    static final boolean DEBUG_TIMING = false; //switch to true to print out timing information

    public enum SearchType {
        ANOMALY_BYGENE,
        ANOMALY_BYPATIENT,
        ANOMALY_BYPATHWAY,
        SINGLE_PATHWAY
    }

    public Level4QueriesJDBCImpl(
            final int fetchSize, final CorrelationCalculator correlationCalculator,
                                 final FishersExact fishersExact, final DataSource dataSource) {
        setCorrelationCalculator(correlationCalculator);
        setFishersExact(fishersExact);
        setFetchSize(fetchSize);
        if (dataSource != null) {
            setDataSource(dataSource);
            init();
        }
    }

    public Level4QueriesJDBCImpl() {
    }

    private boolean isInitialized = false;
    private int fetchSize = 1000;
    private CorrelationCalculator correlationCalculator;
    private FishersExact fishersExact;
    ProcessLogger logger;

    static final String ID_SEPARATOR = ":";
    //for debugging performance against db
    private long startGetRows = 0, endGetRows = 0, elapsedGetRows = 0;
    long startRunCalc = 0, endRunCalc = 0, elapsedRunCalc = 0;
    long startRunQuery = 0, endRunQuery = 0, elapsedRunQuery = 0;

    public void setLogger(final ProcessLogger logger) {
        this.logger = logger;
    }

    public final void setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
    }

    // key is disease name, value is list of column types
    private Map<String, List<ColumnType>> columnTypesForDisease = new HashMap<String, List<ColumnType>>();
    private List<Disease> diseases;
    private Map<String, Integer> workbenchTrackForDisease = new HashMap<String, Integer>();
    // key is id
    private Map<Integer, ResultElement> geneticElements;
    private List<ResultElement> genes;
    private Map<String, List<Integer>> geneticElementIdsForName;
    private Map<Integer, ResultElement> patients;
    private Map<String, Integer> patientIdForName;
    private Map<String, List<Integer>> patientIdsByDisease = new HashMap<String, List<Integer>>(); // key is disease abbreviation, value is list of patient ids
    private Map<Integer, ResultElement> pathways;
    private int totalPathwayGenes;
    private PivotMaster pivotMaster;

    private void cacheGeneticElements() {
        geneticElements = new HashMap<Integer, ResultElement>();
        geneticElementIdsForName = new HashMap<String, List<Integer>>();
        genes = new ArrayList<ResultElement>();
        String query = "SELECT ge.genetic_element_id, ge.genetic_element_name, gt.genetic_element_type, start_pos, stop_pos, chromosome, in_cnv_region, info_url " +
                "FROM L4_genetic_element ge, L4_genetic_element_type gt WHERE ge.genetic_element_type_id=gt.genetic_element_type_id";
        getJdbcTemplate().query(query, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                ResultElement geneticElement = new ResultElement();
                geneticElement.id = resultSet.getInt("genetic_element_id");
                geneticElement.name = resultSet.getString("genetic_element_name");
                if (!resultSet.getString("chromosome").equals("?")) {
                    geneticElement.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM, resultSet.getString("chromosome"));
                    geneticElement.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START, resultSet.getLong("start_pos"));
                    geneticElement.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP, resultSet.getLong("stop_pos"));
                    geneticElement.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_URL, getInfoUrl(resultSet));
                }
                String type = resultSet.getString("genetic_element_type");
                if (type.equals(AnomalyType.GeneticElementType.Gene.toString())) {
                    genes.add(geneticElement);
                }
                geneticElement.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_CNV, resultSet.getInt("in_cnv_region") == 1);

                geneticElements.put(resultSet.getInt("genetic_element_id"), geneticElement);
                List<Integer> geIds = geneticElementIdsForName.get(geneticElement.name);
                if (geIds == null) {
                    geIds = new ArrayList<Integer>();
                    geneticElementIdsForName.put(geneticElement.name, geIds);
                }
                geIds.add(resultSet.getInt("genetic_element_id"));
            }
        });
    }

    ResultElement getGeneticElement(final int id) {
        return geneticElements.get(id);
    }

    protected ResultElement getPatient(final int id) {
        return patients.get(id);
    }

    protected ResultElement getPathway(final int id) {
        return pathways.get(id);
    }

    private void cachePatients() {
        patients = new HashMap<Integer, ResultElement>();
        patientIdForName = new HashMap<String, Integer>();
        String query = "SELECT distinct p.patient_id, d.disease_abbreviation, p.patient FROM disease d, L4_patient p, L4_sample s WHERE p.patient_id=s.patient_id and s.disease_id=d.disease_id";
        getJdbcTemplate().query(query, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                ResultElement patient = new ResultElement();
                patient.id = resultSet.getInt("patient_id");
                patient.name = resultSet.getString("patient");
                patients.put(resultSet.getInt("patient_id"), patient);
                patientIdForName.put(patient.name, resultSet.getInt("patient_id"));
                List<Integer> diseasePatients = patientIdsByDisease.get(resultSet.getString("disease_abbreviation"));
                if (diseasePatients == null) {
                    diseasePatients = new ArrayList<Integer>();
                    patientIdsByDisease.put(resultSet.getString("disease_abbreviation"), diseasePatients);
                }
                diseasePatients.add(resultSet.getInt("patient_id"));
            }
        });
    }

    private void cachePathways() {
        // need genes done first
        if (geneticElements == null) {
            cacheGeneticElements();
        }
        int totalGenes = 0;
        pathways = new HashMap<Integer, ResultElement>();
        String query = "SELECT p.pathway_id, p.svg_identifier, p.display_name, ge.genetic_element_id, biocarta_symbol " +
                "from pathway p, biocarta_gene_pathway bgp, biocarta_gene bg, gene g, L4_genetic_element ge " +
                "where g.gene_id=bg.gene_id and bg.biocarta_gene_id=bgp.biocarta_gene_id and " +
                "bgp.pathway_id=p.pathway_id and g.entrez_symbol=ge.genetic_element_name order by p.pathway_id";
        getJdbcTemplate().query(query, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                int pathwayId = resultSet.getInt("pathway_id");
                ResultElement pathway = pathways.get(pathwayId);
                if (pathway == null) {
                    pathway = new ResultElement();
                    pathway.id = pathwayId;
                    pathway.name = resultSet.getString("display_name");
                    pathway.annotations.put("svg_identifier", resultSet.getString("svg_identifier"));
                    pathways.put(pathwayId, pathway);
                }
                // add gene to pathway
                int geId = resultSet.getInt("genetic_element_id");
                ResultElement gene = getGeneticElement(geId);
                if (gene != null) {
                    gene.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE, resultSet.getString("biocarta_symbol"));
                    pathway.children.put(geId, gene);
                } else {
                    // why does this happen???
                    System.out.println("!!! Gene for pathway not found in genetic element cache! Id = " + resultSet.getInt("genetic_element_id"));
                }
            }
        });

        // now count the total genes in all pathways, and save this number for use in pathway search
        for (ResultElement pathway : pathways.values()) {
            totalGenes += pathway.children.size();
        }
        totalPathwayGenes = totalGenes;
    }

    private String buildAnomalyQuery(FilterSpecifier filter, AnomalyType column, Integer[] genes, Integer[] patients, List<Object> bindVariables) {
        StringBuilder query = new StringBuilder();

        final boolean joinToTargetTable = column.getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe ||
                    column.getGeneticElementType() == AnomalyType.GeneticElementType.miRNA;

        // Case 1: patient query
        if (filter.getListBy() == FilterSpecifier.ListBy.Patients) {
            // Note: query is different for copynumber columns vs all other types.  For copynumber, we want to count distinct gene ids,
            // even if they have the same name, because each one is in a different place.  For non-copynumber, we want to count
            // distinct gene names, because genes with different ids and the same names actually represent the same data.
            // Hence the conditions in the following code -- count genetic_element_id vs genetic_element_name.

            // the column to use to get the gene counts (note: genes, not miRNAs or other things)
            String geneColumn = "genetic_element_name";
            if (column instanceof CopyNumberType) {
                if (joinToTargetTable) {
                    geneColumn = "t.target_genetic_element_id";
                } else {
                    geneColumn = "av.genetic_element_id";
                }
            }
            query.append("SELECT patient_id as element_id, is_paired, count(distinct ").append(geneColumn).append(") as total_count, ");
            String valueConstraint = buildValueConstraint(column, bindVariables);
            if (valueConstraint.length() > 0) {
                query.append("count(distinct case when ").append(valueConstraint).append(" then ").append(geneColumn).append(" end) as affected_count ");
            } else {
                // if no value constraints, just get total again for "affected" -- will result in all 100% results
                query.append("count(distinct ").append(geneColumn).append(") as affected_count ");
            }
            query.append("FROM L4_sample s, L4_anomaly_value av, L4_data_set_sample dss, L4_genetic_element ge");
            if (joinToTargetTable) {
                query.append(", L4_target t");
            }
            query.append(" WHERE ").append(buildDatasetClause(column, bindVariables, "av")).
                    append(" and av.sample_id=s.sample_id and dss.sample_id=s.sample_id and ").
                    append("dss.anomaly_data_set_id=av.anomaly_data_set_id ");
            if (joinToTargetTable) {
                query.append("and ge.genetic_element_id=t.target_genetic_element_id and t.source_genetic_element_id=av.genetic_element_id ");
            } else {
                query.append("and ge.genetic_element_id=av.genetic_element_id ");
            }

            query.append(getGeneInClause(genes, bindVariables));
            query.append(getPatientInClause(patients, column, bindVariables));
            query.append(getRegionClause(filter, bindVariables));

            query.append("GROUP BY patient_id, is_paired ");

        } else {
            // Case 2: gene query
            final boolean doGistic = column instanceof CopyNumberType &&
                    ((CopyNumberType) column).getCalculationType() == CopyNumberType.CalculationType.GISTIC;
            final boolean needsGeneticElementTable = filter.getGeneListOptions() == FilterSpecifier.GeneListOptions.Region || genes.length > 0;

            if (doGistic) {
                query.append("SELECT ");
                if (joinToTargetTable) {
                    query.append("t.target_genetic_element_id as element_id, av.genetic_element_id as other_id, ");
                } else {
                    query.append("av.genetic_element_id as element_id, ");
                }
                query.append("sum(anomaly_value)/count(distinct patient_id) as gistic_value ");
                query.append("FROM L4_sample s, L4_anomaly_value av ");
                if (joinToTargetTable) {
                    query.append(", L4_target t ");
                }
                if (needsGeneticElementTable) {
                    query.append(", L4_genetic_element ge ");
                }
                query.append("WHERE ").
                        append(buildDatasetClause(column, bindVariables, "av")).
                        append(" and av.sample_id=s.sample_id ");
                if (joinToTargetTable) {
                    if (needsGeneticElementTable) {
                        query.append("and ge.genetic_element_id=t.target_genetic_element_id ");
                    }
                    query.append("and av.genetic_element_id=t.source_genetic_element_id ");
                } else if (needsGeneticElementTable) {
                    query.append("and ge.genetic_element_id=av.genetic_element_id ");
                }

                query.append(getGeneInClause(genes, bindVariables));
                query.append(getPatientInClause(patients, column, bindVariables));
                query.append(getRegionClause(filter, bindVariables));
                query.append("GROUP BY av.genetic_element_id");
                if (joinToTargetTable) {
                    query.append(", t.target_genetic_element_id");
                }

            } else {
                String valueConstraint = buildValueConstraint(column, bindVariables);
                query.append("SELECT ");
                if (joinToTargetTable) {
                    query.append("t.target_genetic_element_id as element_id, av.genetic_element_id as other_id, ");
                } else {
                    query.append("av.genetic_element_id as element_id, ");
                }
                query.append("count(distinct patient_id) as total_count, ");
                if (valueConstraint.length() > 0) {
                    query.append("count(distinct case when ").append(valueConstraint).append(" then patient_id end) as affected_count ");
                } else {
                    query.append("count(distinct patient_id) as affected_count ");
                }
                query.append("FROM L4_sample s, L4_anomaly_value av ");
                if (needsGeneticElementTable) {
                    query.append(", L4_genetic_element ge ");
                }
                if (joinToTargetTable) {
                    query.append(", L4_target t ");
                }
                query.append("WHERE ").append(buildDatasetClause(column, bindVariables, "av")).
                        append(" and av.sample_id=s.sample_id ");
                if (joinToTargetTable) {
                    if (needsGeneticElementTable) {
                        query.append("and ge.genetic_element_id=t.target_genetic_element_id ");
                    }
                    query.append("and av.genetic_element_id=t.source_genetic_element_id ");
                } else if (needsGeneticElementTable) {
                    query.append("and ge.genetic_element_id=av.genetic_element_id ");
                }
                query.append(getGeneInClause(genes, bindVariables));
                query.append(getPatientInClause(patients, column, bindVariables));
                query.append(getRegionClause(filter, bindVariables));
                query.append("GROUP BY av.genetic_element_id ");
                if (joinToTargetTable) {
                    query.append(", t.target_genetic_element_id");
                }
            }
        }

        return query.toString();
    }

    private String buildCorrelationQuery(FilterSpecifier filter, CorrelationType column, Integer[] genes, Integer[] patients, List<Object> bindVariables) {
        // note: correlations only supported for gene search
        StringBuilder query = new StringBuilder();
        boolean hasRegionClause = filter.getGeneListOptions() == FilterSpecifier.GeneListOptions.Region && filter.getChromRegions().size() > 0;
        boolean hasOtherId = false;
        boolean bothOtherIds = false;
        boolean bothSame = false;
        String elementIdTable = "";
        String otherIdTable = "";

        if (column.getAnomalyType1().getGeneticElementType() == column.getAnomalyType2().getGeneticElementType()) {
            bothSame = true;
        }
        if (column.getAnomalyType1().getGeneticElementType() != AnomalyType.GeneticElementType.Gene &&
                column.getAnomalyType2().getGeneticElementType() != AnomalyType.GeneticElementType.Gene) {
            bothOtherIds = true;
        } else if (column.getAnomalyType1().getGeneticElementType() != AnomalyType.GeneticElementType.Gene) {
            hasOtherId = true;
            otherIdTable = "av1";
            elementIdTable = "av2";
        } else if (column.getAnomalyType2().getGeneticElementType() != AnomalyType.GeneticElementType.Gene) {
            hasOtherId = true;
            otherIdTable = "av2";
            elementIdTable = "av1";
        }

        query.append("SELECT av1.anomaly_value as value1, av2.anomaly_value as value2, ");
        if (bothSame && !bothOtherIds) {
            // gene vs gene
            query.append("av1.genetic_element_id as element_id, 0 as other_id ");
        } else if (bothSame && bothOtherIds) {
            // mirna vs mirna or methyl vs methyl
            query.append("av1.genetic_element_id as other_id, t.target_genetic_element_id as element_id ");
        } else if (bothOtherIds) {
            // mirna vs methyl or methyl vs mirna
            query.append("av1.genetic_element_id as other_id1, av2. genetic_element_id as other_id2, t1.target_genetic_element_id as element_id ");
        } else if (hasOtherId) {
            // gene vs non-gene or non-gene vs gene
            query.append(elementIdTable).append(".genetic_element_id as element_id, ").append(otherIdTable).append(".genetic_element_id as other_id ");
        }

        query.append("FROM ");
        if (hasRegionClause || genes.length > 0) {
            query.append("L4_genetic_element ge, ");
        }
        query.append("L4_sample s1, L4_sample s2, L4_anomaly_value av1, L4_anomaly_value av2 ");
        if (bothOtherIds && !bothSame) {
            query.append(", L4_target t1, L4_target t2 ");
        } else if (bothOtherIds || hasOtherId) {
            query.append(", L4_target t ");
        }
        query.append("WHERE ");
        query.append(buildDatasetClause(column.getAnomalyType1(), bindVariables, "av1"));
        query.append(" AND ");
        query.append(buildDatasetClause(column.getAnomalyType2(), bindVariables, "av2"));
        query.append(" AND s1.patient_id=s2.patient_id AND av1.sample_id=s1.sample_id ").
                append("AND av2.sample_id=s2.sample_id ");
        if (bothSame && !bothOtherIds) {
            // gene vs gene
            query.append("and av1.genetic_element_id=av2.genetic_element_id ");
        } else if (bothSame && bothOtherIds) {
            // mirna vs mirna or methyl vs methyl
            query.append("AND av1.genetic_element_id=av2.genetic_element_id AND av1.genetic_element_id=t.source_genetic_element_id ");
        } else if (bothOtherIds) {
            // mirna vs methyl or methyl vs mirna
            query.append("AND t1.target_genetic_element_id=t2.target_genetic_element_id ").
                    append("AND t1.source_genetic_element_id=av1.genetic_element_id ").
                    append("AND t2.source_genetic_element_id=av2.genetic_element_id ");
        } else if (hasOtherId) {
            // gene vs non-gene or non-gene vs gene
            query.append("AND t.target_genetic_element_id=").append(elementIdTable).append(".genetic_element_id and ").
                    append("t.source_genetic_element_id=").append(otherIdTable).append(".genetic_element_id ");
        }
        if (hasRegionClause || genes.length > 0) {
            if (bothSame && bothOtherIds) {
                query.append("AND t.target_genetic_element_id=ge.genetic_element_id ");
            } else if (!bothSame && bothOtherIds) {
                query.append("AND t1.target_genetic_element_id=ge.genetic_element_id AND t2.target_genetic_element_id=ge.genetic_element_id ");
            } else if (hasOtherId) {
                query.append("AND ").append(elementIdTable).append(".genetic_element_id=ge.genetic_element_id ");
            } else {
                query.append("AND av1.genetic_element_id=ge.genetic_element_id AND av2.genetic_element_id=ge.genetic_element_id ");
            }
        }
        query.append(getGeneInClause(genes, bindVariables));
        query.append(getPatientInClause(patients, column, bindVariables));
        query.append(getRegionClause(filter, bindVariables));
        if (bothSame && !bothOtherIds || !bothSame && hasOtherId) {
            query.append(" ORDER BY av1.genetic_element_id, av2.genetic_element_id");
        } else if (bothSame && bothOtherIds) {
            query.append("ORDER BY t.target_genetic_element_id, av1.genetic_element_id");
        } else if (bothOtherIds) {
            query.append("ORDER BY t1.target_genetic_element_id, av1.genetic_element_id, av2.genetic_element_id");
        }

        return query.toString();
    }

    String buildDatasetClause(AnomalyType column, List<Object> bindVariables, String tableName) {
        StringBuilder clause = new StringBuilder();

        if (column instanceof AggregateMutationType) {
            clause.append("(");
            Collection<MutationType> mutationTypes = ((AggregateMutationType) column).getMutationTypes();
            Iterator<MutationType> it = mutationTypes.iterator();
            while (it.hasNext()) {
                clause.append(tableName).append(".anomaly_data_set_id=?");
                bindVariables.add(it.next().getDataSetId());
                if (it.hasNext()) {
                    clause.append(" OR ");
                }
            }
            clause.append(") ");
        } else {
            clause.append(tableName).append(".anomaly_data_set_id=? ");
            bindVariables.add(column.getDataSetId());
        }

        return clause.toString();
    }

    private String getGeneInClause(Integer[] geneIds, List<Object> bindParameters) {
        StringBuilder clause = new StringBuilder();
        if (geneIds.length > 0) {
            clause.append("and ge.genetic_element_id in (");
            for (int i = 0; i < geneIds.length; i++) {
                if (i > 0) {
                    clause.append(", ");
                }
                clause.append("?");
                bindParameters.add(geneIds[i]);
            }
            clause.append(") ");
        }
        return clause.toString();
    }

    private String getPatientInClause(Integer[] patientIds, ColumnType column, List<Object> bindParameters) {
        StringBuilder clause = new StringBuilder();
        if (patientIds.length > 0) {
            if (column instanceof CorrelationType) {
                clause.append("and s1.patient_id in (");
            } else {
                clause.append("and s.patient_id in (");
            }

            for (int i = 0; i < patientIds.length; i++) {
                if (i > 0) {
                    clause.append(", ");
                }
                clause.append("?");
                bindParameters.add(patientIds[i]);
            }
            clause.append(") ");
        }
        return clause.toString();
    }

    public Results getPivotResults(FilterSpecifier.ListBy sourceListby, String rowName, FilterSpecifier filter) throws QueriesException {
        if (!isInitialized) {
            init();
        }
        return pivotMaster.getPivotResults(sourceListby, rowName, filter);
    }

    public SinglePathwayResults getSinglePathway(SinglePathwaySpecifier sps) throws QueriesException {
        if (!isInitialized) {
            init();
        }

        ResultElement pathway = pathways.get(Integer.valueOf(sps.getId()));
        List<String> searchedGenes = new ArrayList<String>();
        searchedGenes.addAll(Arrays.asList(parseList(sps.getFilterSpecifier().getGeneList())));

        List<Integer[]> elementLists = parseLists(sps.getFilterSpecifier());
        Integer[] patientIds = elementLists.get(1);

        // actual gene list to pass to the search is just the pathway's genes
        Integer[] pathwayGeneIds = new Integer[pathway.children.size()];
        int index = 0;
        for (ResultElement pathwayGene : pathway.children.values()) {
            pathwayGeneIds[index++] = pathwayGene.id;
        }

        FilterSpecifier.ListBy originalListBy = sps.getFilterSpecifier().getListBy();
        sps.getFilterSpecifier().setListBy(FilterSpecifier.ListBy.Genes);
        Results pathwayGeneResults = getAnomalyResults(sps.getFilterSpecifier(),
                pathwayGeneIds, patientIds, SearchType.SINGLE_PATHWAY);
        sps.getFilterSpecifier().setListBy(originalListBy);
        SinglePathwayResults results = new SinglePathwayResults();
        results.initialize(pathwayGeneResults);
        for (int i = 0; i < pathwayGeneResults.getActualRowCount(); i++) {
            ResultRow row = pathwayGeneResults.getRow(i);
            if (sps.getFilterSpecifier().getGeneListOptions() == FilterSpecifier.GeneListOptions.List && !searchedGenes.contains(row.getName())) {
                // if did not search for gene, set to not matched no matter what (assuming did a gene name search)
                row.addRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, false);
            }
            if (sps.getFilterSpecifier().getGeneListOptions() == FilterSpecifier.GeneListOptions.Region) {
                String chrom = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM);
                Long start = (Long) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START);
                Long end = (Long) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP);
                // check that this row's location matches one of the specified regions
                boolean matched = false;
                if (chrom != null && !chrom.equals("?")) {
                    for (FilterChromRegion region : sps.getFilterSpecifier().getChromRegions()) {
                        if (region.overlapsWith(chrom, start, end)) {
                            matched = true;
                        }
                    }
                }
                row.addRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, matched);
            }
            results.addRow(row);
        }
        results.setDisplayName(pathway.name);
        results.setTotalRowCount(pathwayGeneResults.getActualRowCount());
        results.setId(String.valueOf(pathway.id));
        results.setName(pathway.annotations.get("svg_identifier").toString());

        return results;
    }


    public void getAnomalyResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException {
        if (!isInitialized) {
            init();
        }

        if (filter.getListBy() == FilterSpecifier.ListBy.Pathways) {
            getPathwayResults(filter, callback);
        } else {
            List<Integer[]> elementLists = parseLists(filter);
            Integer[] geneIds = elementLists.get(0);
            Integer[] patientIds = elementLists.get(1);
            Results results = getAnomalyResults(filter, geneIds, patientIds,
                    filter.getListBy() == FilterSpecifier.ListBy.Genes ? SearchType.ANOMALY_BYGENE : SearchType.ANOMALY_BYPATIENT);
            callback.sendFullResults(results);
        }
    }

    public Results getAnomalyResults(FilterSpecifier filter) throws QueriesException {
        if (!isInitialized) {
            init();
        }
        List<Integer[]> elementLists = parseLists(filter);
        Integer[] geneIds = elementLists.get(0);
        Integer[] patientIds = elementLists.get(1);
        return getAnomalyResults(filter, geneIds, patientIds,
                filter.getListBy() == FilterSpecifier.ListBy.Genes ? SearchType.ANOMALY_BYGENE : SearchType.ANOMALY_BYPATIENT);
    }

    List<Integer[]> parseLists(FilterSpecifier filter) throws QueriesException {
        List<Integer[]> geneAndPatientLists = new ArrayList<Integer[]>();
        if (filter.getGeneListOptions() == FilterSpecifier.GeneListOptions.List) {
            Integer[] geneIds = getGeneIds(filter.getGeneList());
            if (geneIds.length == 0) {
                throw new QueriesException("None of the specified genes were found in the database: " + filter.getGeneList());
            }
            geneAndPatientLists.add(geneIds);
        } else {
            geneAndPatientLists.add(new Integer[0]);
        }
        if (filter.getPatientListOptions() == FilterSpecifier.PatientListOptions.List) {
            Integer[] patientIds = getPatientIds(filter.getPatientList());
            if (patientIds.length == 0) {
                throw new QueriesException("None specified patients were found in the database: " + filter.getPatientList());
            }
            geneAndPatientLists.add(patientIds);
        } else {
            geneAndPatientLists.add(new Integer[0]);
        }
        return geneAndPatientLists;
    }

    public final synchronized void init() {
        if (!isInitialized) {
            getJdbcTemplate().setFetchSize(fetchSize);
            getDiseases();
            if (geneticElements == null) {
                cacheGeneticElements();
            }
            if (patients == null) {
                cachePatients();
            }
            if (pathways == null) {
                cachePathways();
            }
            if (pivotMaster == null) {
                pivotMaster = new PivotMaster();
                pivotMaster.setDataSource(getDataSource());
                pivotMaster.dao = this;
            }
            isInitialized = true;
        }
    }

    public void getPathwayResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException {
        if (!isInitialized) {
            init();
        }

        if (filter.getListBy() != FilterSpecifier.ListBy.Pathways) {
            getAnomalyResults(filter, callback);
        } else {
            List<Integer[]> elementLists = parseLists(filter);
            Integer[] geneIds = elementLists.get(0);
            Integer[] patientIds = elementLists.get(1);
            FilterSpecifier.ListBy origListBy = filter.getListBy();
            // temporarily set listby to genes so queries will build correctly
            filter.setListBy(FilterSpecifier.ListBy.Genes);
            Results geneResults = getAnomalyResults(filter, geneIds, patientIds, SearchType.ANOMALY_BYPATHWAY);
            filter.setListBy(origListBy);

            boolean criteriaSelected = (filter.getPickedColumns().size() > 0 || filter.getGeneList() != null);

            // now for ALL pathways, go through each one and count gene matches

            // first make a map of gene ids in the gene results
            Map<Integer, Integer> matchedGeneIds = new HashMap<Integer, Integer>();
            for (int i = 0; i < geneResults.getActualRowCount(); i++) {
                Integer geneId = Integer.valueOf(geneResults.getRow(i).getName());
                matchedGeneIds.put(geneId, geneId);
            }

            // save matching pathways for sorting before putting into Results object
            List<ResultElement> matchingPathways = new ArrayList<ResultElement>();
            for (ResultElement pathway : pathways.values()) {
                // for each pathway, count the number of pathway genes that are in the matchedGeneId list
                // if at least 1, include this pathway in the results.
                int pathwayGenesMatched = 0;
                for (Integer geneId : pathway.children.keySet()) {
                    if (matchedGeneIds.containsKey(geneId)) {
                        pathwayGenesMatched++;
                    }
                }
                if (pathwayGenesMatched > 0) {
                    // if there were no column types selected, don't want to calculate fischers on just the complete gene list -- meaningless
                    if (criteriaSelected) {
                        // calculate Fisher's Exact for this pathway
                        double fishers = fishersExact.calculateFisherRightTail(matchedGeneIds.size(), pathwayGenesMatched,
                                totalPathwayGenes, pathway.children.size());
                        // add a row to the results for this
                        pathway.annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER, fishers);
                    }
                    matchingPathways.add(pathway);
                }
            }
            Collections.sort(matchingPathways, new Comparator<ResultElement>() {
                public int compare(ResultElement pathway1, ResultElement pathway2) {
                    Double fisher1 = (Double) pathway1.annotations.get(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER);
                    Double fisher2 = (Double) pathway2.annotations.get(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER);
                    if (fisher1 == null || fisher2 == null) {
                        return pathway1.name.toLowerCase().compareTo(pathway2.name.toLowerCase());
                    } else {
                        return fisher1.compareTo(fisher2);
                    }
                }
            });
            Results results = new Results(filter);
            for (ResultElement pathway : matchingPathways) {
                Map<String, Serializable> annotations = new HashMap<String, Serializable>();
                annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID, pathway.id);
                annotations.put(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER, pathway.annotations.get(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER));
                results.addRow(pathway.name, new ResultBlank[0], annotations);
            }
            callback.sendFullResults(results);
        }
    }

    public List<Disease> getDiseases() {
        // only fetch the list if we haven't already
        if (diseases == null) {
            diseases = new ArrayList<Disease>();
            StringBuilder query = new StringBuilder().append("SELECT distinct d.disease_abbreviation, d.disease_name, d.workbench_track ").
                    append("FROM L4_anomaly_type at,  L4_anomaly_data_set ads, L4_anomaly_data_set_version adsv, L4_anomaly_data_version adv,disease d ").
                    append("WHERE at.anomaly_type_id=ads.anomaly_type_id ").
                    append("and ads.anomaly_data_set_id=adsv.anomaly_data_set_id ").
                    append("and adsv.anomaly_data_version_id=adv.anomaly_data_version_id ").
                    append("and adv.is_active=1 ").
                    append("and adv.disease_id=d.disease_id ").
                    append("and d.active=1");
            getJdbcTemplate().query(query.toString(), new RowCallbackHandler() {
                public void processRow(ResultSet row) throws SQLException {
                    Disease disease = new Disease();
                    disease.setId(row.getString("disease_abbreviation"));
                    disease.setName(row.getString("disease_name"));
                    diseases.add(disease);
                    workbenchTrackForDisease.put(disease.getName(), row.getInt("workbench_track"));
                }
            });
        }
        return diseases;
    }

    /**
     * This creates a List of ColumnType objects that reflect what data is currently available for the given disease.
     * For now this assumes we are using the latest active version of the data.
     *
     * @param disease the disease name
     * @return a list of all columns types with data for the given disease type
     * @throws gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException
     *
     */
    public List<ColumnType> getColumnTypes(String disease) throws QueriesException {        
        if (!isInitialized) {
            init();
        }

        if (columnTypesForDisease.get(disease) != null) {
            return columnTypesForDisease.get(disease);
        } else {
            // 1. get the basic anomaly types as columns
            List<ColumnType> columnTypes = getAnomalyTypes(disease);

            // 2. create correlations
            createCorrelations(columnTypes);
            columnTypesForDisease.put(disease, columnTypes);
            return columnTypes;
        }
    }

    /*
     * Creates correlation types based on the available anomaly types
     */
    private void createCorrelations(final List<ColumnType> columnTypes) {
        // first get all valid correlation types
        String query = "select correlation_name, anomaly_type_id_1, anomaly_type_id_2 from L4_correlation_type order by correlation_name";

        getJdbcTemplate().query(query, new RowCallbackHandler() {

            public void processRow(ResultSet row) throws SQLException {
                int anomalyType1 = row.getInt("anomaly_type_id_1");
                int anomalyType2 = row.getInt("anomaly_type_id_2");

                // look for column types of type 1 and all of type 2
                // if have a type 1 column and a type 2 column, make a correlation from them
                // note: if a data version has multiple data sets for the same anomaly type, this will only get one of them!
                // also, if we ever decide to correlation mutation data, this will not work because the column types for mutations
                // are aggregate mutation types.
                AnomalyType col1 = null;
                AnomalyType col2 = null;
                for (ColumnType column : columnTypes) {
                    if (column instanceof AnomalyType && ((AnomalyType) column).getAnomalyTypeId() == anomalyType1) {
                        col1 = (AnomalyType) column;
                    }
                    if (column instanceof AnomalyType && ((AnomalyType) column).getAnomalyTypeId() == anomalyType2) {
                        col2 = (AnomalyType) column;
                    }
                }
                if (col1 != null && col2 != null) {
                    CorrelationType correlation = new CorrelationType();
                    correlation.setDisplayName(row.getString("correlation_name"));
                    correlation.setAnomalyType1(col1);
                    correlation.setAnomalyType2(col2);
                    columnTypes.add(correlation);
                }
            }
        });
    }

    private List<ColumnType> getAnomalyTypes(String disease) throws QueriesException {

        // this query gets all anomaly types that have at least one anomaly data set associated with them
        StringBuilder query = new StringBuilder().append("SELECT L4_anomaly_type.anomaly_name, L4_anomaly_data_set.anomaly_data_set_id, ").
                append("platform.platform_name as platform_name, platform.platform_id, L4_anomaly_data_version.version, disease.disease_abbreviation, ").
                append("center.domain_name as center_name, center.center_id, data_type.name as data_type, data_type.data_type_id, L4_anomaly_type.anomaly_type_id, ").
                append("L4_genetic_element_type.genetic_element_type ").
                append("FROM L4_anomaly_type, platform, center, data_type, L4_anomaly_data_set, L4_anomaly_data_set_version, disease, L4_anomaly_data_version, L4_genetic_element_type ").
                append("WHERE L4_anomaly_type.anomaly_type_id=L4_anomaly_data_set.anomaly_type_id ").
                append("and L4_anomaly_type.platform_id=platform.platform_id and L4_anomaly_type.center_id=center.center_id ").
                append("and L4_anomaly_type.data_type_id=data_type.data_type_id ").
                append("and L4_anomaly_data_set_version.anomaly_data_set_id=L4_anomaly_data_set.anomaly_data_set_id ").
                append("and L4_anomaly_data_set_version.anomaly_data_version_id=L4_anomaly_data_version.anomaly_data_version_id ").
                append("and L4_anomaly_data_version.disease_id=disease.disease_id ").
                append("and L4_anomaly_type.genetic_element_type_id=L4_genetic_element_type.genetic_element_type_id ");

        // narrow by disease
        if (disease == null) { // default
            query.append("and L4_anomaly_data_version.disease_id in (select disease_id from disease where dam_default=1) ");
        } else { // specific
            query.append("and disease.disease_abbreviation='").append(disease).append("' ");
        }

        // for now, just using is_active version, assuming just one will be active at a time.
        // in future, can instead select for a specific version rather than the active version
        query.append("and L4_anomaly_data_version.is_active=1");
        //todo  move mutations into submethod
        // need to aggregate all mutation types of same type...
        final Map<MutationType.Category, List<MutationType>> mutationTypes = new HashMap<MutationType.Category, List<MutationType>>();
        final List<ColumnType> columns = new ArrayList<ColumnType>();
        getJdbcTemplate().query(query.toString(),
                new RowCallbackHandler() {
                    // map each row to an AnomalyType
                    public void processRow(ResultSet resultSet) throws SQLException {
                        String dataType = resultSet.getString("data_type");
                        AnomalyType.GeneticElementType geneticElementType = AnomalyType.GeneticElementType.Gene;
                        if (resultSet.getString("genetic_element_type").equals("miRNA")) {
                            geneticElementType = AnomalyType.GeneticElementType.miRNA;
                        } else if (resultSet.getString("genetic_element_type").contains("methylation")) {
                            geneticElementType = AnomalyType.GeneticElementType.MethylationProbe;
                        }
                        AnomalyType anomalyType = null;

                        // figure out if this is mutation type or not
                        if (dataType.equals("Somatic Mutations")) {
                            anomalyType = new MutationType();
                            MutationType.Category category = MutationType.getCategoryForName(resultSet.getString("anomaly_name"));
                            List<MutationType> categoryMutations = mutationTypes.get(category);
                            if (categoryMutations == null) {
                                categoryMutations = new ArrayList<MutationType>();
                                mutationTypes.put(category, categoryMutations);
                            }
                            categoryMutations.add((MutationType) anomalyType);
                        } else {

                            if (dataType.equals("Copy Number Results")) {
                                anomalyType = new CopyNumberType(geneticElementType);
                            } else if (dataType.equals("Expression-Genes")) {
                                anomalyType = new ExpressionType(AnomalyType.GeneticElementType.Gene);
                            } else if (dataType.contains("Methylation")) {
                                anomalyType = new MethylationType();
                            } else if (dataType.equals("Expression-miRNA")) {
                                anomalyType = new ExpressionType(AnomalyType.GeneticElementType.miRNA);
                            } else {
                                // so what? don't understand this data type yet, so skip it
                            }
                            // only add non-mutation types to main list for now, need to aggregate mutation types later
                            if (anomalyType != null) {
                                columns.add(anomalyType);
                            }
                        }
                        if (anomalyType != null) {
                            anomalyType.setDisplayCenter(resultSet.getString("center_name"));
                            anomalyType.setPlatformType(resultSet.getInt("data_type_id"));
                            anomalyType.setDataSetId(resultSet.getInt("anomaly_data_set_id"));
                            anomalyType.setAnomalyTypeId(resultSet.getInt("anomaly_type_id"));
                            anomalyType.setDisplayName(resultSet.getString("anomaly_name"));                            

                            if (dataType.equals("Somatic Mutations")) {
                                anomalyType.setDisplayPlatformType("Mutations");
                                anomalyType.setDisplayPlatform("ABI");
                            } else {
                                anomalyType.setDisplayPlatform(resultSet.getString("platform_name"));
                                anomalyType.setDisplayPlatformType(resultSet.getString("data_type"));
                            }
                        }

                    }
                });

        List<MutationType> nonSilentTypes = new ArrayList<MutationType>();
        // create aggregate mutation types for each type found... also add all non-silent types to the non-silent aggregate
        for (MutationType.Category category : mutationTypes.keySet()) {
            AggregateMutationType mutationType = new AggregateMutationType();
            mutationType.setCategory(category);
            List<MutationType> types = mutationTypes.get(category);
            mutationType.setMutationTypes(types);
            // platform type is the same for all mutation types.  cannot set center though.  platform?
            mutationType.setPlatformType(types.get(0).getPlatformType());
            columns.add(mutationType);
            if (category != MutationType.Category.Silent) {
                nonSilentTypes.addAll(types);
            }
        }
        if (nonSilentTypes.size() > 0) {
            AggregateMutationType nonSilent = new AggregateMutationType();
            nonSilent.setCategory(MutationType.Category.AnyNonSilent);
            nonSilent.setMutationTypes(nonSilentTypes);
            columns.add(nonSilent);
        }

        Collections.sort(columns, new Comparator<ColumnType>() {
            public int compare(ColumnType o1, ColumnType o2) {
                String name1 = o1.getDisplayName();
                String name2 = o2.getDisplayName();
                // always put AnyNonSilent first
                if (o1 instanceof AggregateMutationType && ((AggregateMutationType) o1).getCategory() == MutationType.Category.AnyNonSilent) {
                    return -1;
                }
                if (o2 instanceof AggregateMutationType && ((AggregateMutationType) o2).getCategory() == MutationType.Category.AnyNonSilent) {
                    return 1;
                }
                return name1.compareTo(name2);
            }
        });

        return columns;
    }

    protected Integer[] getGeneIds(String geneListStr) {
        //return getElementIds(geneListStr, geneticElementIdForName);
        String[] geneNames = parseList(geneListStr);
        Arrays.sort(geneNames);
        final List<Integer> ids = new ArrayList<Integer>();
        for (String geneName : geneNames) {
            if (geneticElementIdsForName.containsKey(geneName)) {
                // some genes have multiple IDs
                for (int id : geneticElementIdsForName.get(geneName)) {
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                }
            }
        }
        return ids.toArray(new Integer[ids.size()]);
    }

    protected Integer[] getPatientIds(String patientListStr) {
        return getElementIds(patientListStr, patientIdForName);
    }

    protected Integer[] getElementIds(String elementListStr, Map<String, Integer> idForNameMap) {
        String[] elementNames = parseList(elementListStr); // this uppercases everything
        Arrays.sort(elementNames);
        final List<Integer> elements = new ArrayList<Integer>();
        for (String elementName : elementNames) {
            Integer id = idForNameMap.get(elementName);
            if (id != null && !elements.contains(id)) {
                elements.add(idForNameMap.get(elementName));
            }
        }
        return elements.toArray(new Integer[elements.size()]);
    }

    protected String[] parseList(String listStr) {
        if (listStr == null || listStr.trim().length() == 0) {
            return new String[0];
        }

        String[] list = listStr.trim().split("[\\s,;]");
        List<String> temp = new ArrayList<String>();
        for (String str : list) {
            if (str.trim().length() > 0) {
                temp.add(str.toUpperCase().trim());
            }
        }
        return temp.toArray(new String[temp.size()]);
    }

    private String getInfoUrl(ResultSet rs) throws SQLException {
        String infoUrl = null;
        String url = rs.getString("info_url");
        if (url != null) {
            infoUrl = url;
            Integer start = rs.getInt("start_pos");
            Integer stop = rs.getInt("stop_pos");
            String chrom = rs.getString("chromosome");
            String name = rs.getString("genetic_element_name");

            if (infoUrl.contains("START_POS")) {
                if (start != null && start != -1) {
                    infoUrl = infoUrl.replaceAll("START_POS", String.valueOf(start));
                } else {
                    // url requires start but don't know it for this, so no URL
                    return null;
                }
            }
            if (infoUrl.contains("STOP_POS")) {
                if (stop != null && stop != -1) {
                    infoUrl = infoUrl.replaceAll("STOP_POS", String.valueOf(stop));
                } else {
                    // url requires stop but don't know it for this
                    return null;
                }
            }
            if (infoUrl.contains("CHROMOSOME")) {
                if (chrom != null && !chrom.equals("?")) {
                    infoUrl = infoUrl.replaceAll("CHROMOSOME", chrom);
                } else {
                    // url requires chromosome but don't know it for this
                    return null;
                }
            }
            // NOTE: WORKBENCH_TRACK REPLACED LATER

            infoUrl = infoUrl.replaceAll("GENETIC_ELEMENT_NAME", name);
        }

        return infoUrl;
    }

    /*
     * Gets list of elements (genes or patients) to return for a search that has no columns included, just (optionally)
     * gene list, patient list, or chromosome location
     */
    private List<ResultElement> getElementsForNoColumnSearch(FilterSpecifier filter, Integer[] geneIds, Integer[] patientIds) {
        final List<ResultElement> elements = new ArrayList<ResultElement>();

        if (filter.getChromRegions().size() > 0 && filter.getListBy() == FilterSpecifier.ListBy.Genes) {
            StringBuilder query = new StringBuilder("SELECT distinct genetic_element_id FROM L4_genetic_element ge WHERE 1=1 ");
            List<Object> bindVariables = new ArrayList<Object>();
            query.append(getRegionClause(filter, bindVariables));
            query.append(getGeneInClause(geneIds, bindVariables));
            getJdbcTemplate().query(query.toString(), bindVariables.toArray(), new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    // get genetic element from cache for this id
                    int id = resultSet.getInt("genetic_element_id");
                    ResultElement gene = getGeneticElement(id);
                    if (gene != null) {
                        elements.add(gene);
                    }
                }
            });

        } else {
            // no regions, don't need to do query
            // get genes or patients from cache
            if (filter.getListBy() == FilterSpecifier.ListBy.Genes) {
                // no columns and no gene list, so return all genes
                if (geneIds.length == 0) {
                    elements.addAll(genes);
                } else {
                    // no columns and gene list, so return all genes from list that matched
                    for (Integer geneId : geneIds) {
                        elements.add(getGeneticElement(geneId));
                    }
                }
            } else {
                List<Integer> diseasePatients = getPatientsForDisease(filter.getDisease());
                if (diseasePatients != null) {
                    if (patientIds.length == 0) {
                        // no columns, no patient list, so return all patients for the disease
                        for (Integer patientId : diseasePatients) {
                            elements.add(patients.get(patientId));
                        }
                    } else {
                        // no columns but patient list, so return all patients from list that are for this disease
                        for (Integer patientId : patientIds) {
                            if (diseasePatients.contains(patientId)) {
                                elements.add(patients.get(patientId));
                            }
                        }
                    }
                }
            }

        }

        return elements;
    }

    protected List<Integer> getPatientsForDisease(String disease) {
        return patientIdsByDisease.get(disease);
    }

    private Results getAnomalyResults(FilterSpecifier filter, Integer[] geneIds, Integer[] patientIds,
                                      SearchType searchType) throws QueriesException {
        if (geneIds == null) geneIds = new Integer[0];
        if (patientIds == null) patientIds = new Integer[0];

        boolean isListByGene = filter.getListBy() == FilterSpecifier.ListBy.Genes;

        // will hold final results, to be returned at end
        Results results = new Results(filter);

        // list of result values for each column, each element a map
        List<Map<String, ResultValue>> resultValues = new ArrayList<Map<String, ResultValue>>();
        List<ColumnType> pickedColumns = filter.getPickedColumns();

        for (ColumnType column : pickedColumns) {
            Map<String, ResultValue> columnResults = null;
            if (column instanceof CorrelationType) {
                columnResults = getCorrelationColumnResult((CorrelationType) column, filter, geneIds, patientIds);
            } else if (column instanceof AnomalyType) {
                // map has gene/patient name as key and ResultRatio as value
                columnResults = getAnomalyColumnResult((AnomalyType) column, filter, geneIds, patientIds);
            }

            if (columnResults != null) {
                resultValues.add(columnResults);
            }
        }

        List<ResultElement> elements;

        if (resultValues.size() > 0) {
            elements = combineElements(isListByGene, resultValues);
        } else {
            elements = getElementsForNoColumnSearch(filter, geneIds, patientIds);
        }

        // for each gene/patient OR all the values with same Anomaly Type
        // AND all the grps to get the final result

        Map<String, Boolean> distinctColumnTypes;
        Collections.sort(elements);

        for (ResultElement element : elements) {
            // fetch any permanent annotations for this gene/patient
            Map<String, Serializable> annotationsCopy = copyAnnotations(element, filter, searchType);

            // tracks if found a non-zero value for this element
            ResultValue[] elementVals = new ResultValue[resultValues.size()];
            distinctColumnTypes = getDistinctColumnTypes(filter.getPickedColumns());

            for (String columnTypeName : distinctColumnTypes.keySet()) {
                for (int i = 0; i < resultValues.size(); i++) {
                    ColumnType column = pickedColumns.get(i);
                    if ((getColumnTypeName(column).equals(columnTypeName))) {
                        Map<String, ResultValue> columnVals = resultValues.get(i);
                        String elementKey = getElementKeyForColumn(element, column, filter.getListBy() == FilterSpecifier.ListBy.Genes);
                        ResultValue value = columnVals.get(elementKey);
                        boolean valuePasses;
                        if (value == null) {
                            value = new ResultBlank();
                            valuePasses = false;
                        } else {
                            valuePasses = valuePassesFilter(value, pickedColumns.get(i));
                        }
                        boolean columnPasses = distinctColumnTypes.get(columnTypeName) || valuePasses;
                        distinctColumnTypes.put(columnTypeName, columnPasses);
                        elementVals[i] = value;
                        if (element.methylationTargetId != 0) {
                            // look up the name of the region and add it to row annotations
                            annotationsCopy.put(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE,
                                    getGeneticElement(element.methylationTargetId).name);
                        }
                        if (element.miRNAId != 0) {
                            annotationsCopy.put(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA,
                                    getGeneticElement(element.miRNAId).name);
                        }
                    }
                }
            }

            boolean rowMatches = true;
            for (String distinctColTypeName : distinctColumnTypes.keySet()) {
                rowMatches = distinctColumnTypes.get(distinctColTypeName) && rowMatches;
            }

            rowMatches = rowMatches || pickedColumns.size() == 0;

            if (rowMatches || searchType == SearchType.SINGLE_PATHWAY) {
                if (searchType == SearchType.ANOMALY_BYPATHWAY) {
                    // only need the id of the matching element
                    results.addRow(String.valueOf(element.id), null, null);
                } else {
                    if (searchType == SearchType.SINGLE_PATHWAY) {
                        boolean highlightGene = rowMatches && (pickedColumns.size() > 0 || filter.getGeneList() != null);
                        annotationsCopy.put(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, highlightGene); //if searching all pathways, don't highlight
                    }
                    // if no annotations, pass in null
                    if (annotationsCopy.size() == 0) {
                        annotationsCopy = null;
                    }
                    // now add a row for the element with all values and annotations
                    results.addRow(element.name, elementVals, annotationsCopy);
                }
            }
        }

        return results;
    }

    List<ResultElement> combineElements(boolean listByGene, List<Map<String, ResultValue>> resultValues) throws QueriesException {
        List<ResultElement> elements = new ArrayList<ResultElement>();

        Map<Integer, Map<Integer, Integer>> methylIdsForGeneId = new HashMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<Integer, Integer>> mirnaIdsForGeneId = new HashMap<Integer, Map<Integer, Integer>>();
        for (Map<String, ResultValue> columnResult : resultValues) {
            // for each result in this column...
            for (String idString : columnResult.keySet()) {
                Integer[] ids;
                try {
                    ids = parseIdString(idString); // 0 is gene/patient id, 1 is methylation, 2, is miRNA... 0 if none
                } catch (NumberFormatException nfe) {
                    throw new QueriesException("ID could not be parsed as a number");
                }
                Map<Integer, Integer> geneMethylMap = methylIdsForGeneId.get(ids[0]);
                if (geneMethylMap == null) {
                    geneMethylMap = new HashMap<Integer, Integer>();
                    methylIdsForGeneId.put(ids[0], geneMethylMap);
                }
                if (ids[1] != 0) {
                    geneMethylMap.put(ids[1], ids[1]);
                }
                Map<Integer, Integer> geneMirnaMap = mirnaIdsForGeneId.get(ids[0]);
                if (geneMirnaMap == null) {
                    geneMirnaMap = new HashMap<Integer, Integer>();
                    mirnaIdsForGeneId.put(ids[0], geneMirnaMap);
                }
                if (ids[2] != 0) {
                    geneMirnaMap.put(ids[2], ids[2]);
                }
            }
        }

        Set<Integer> geneIds = new HashSet<Integer>();
        geneIds.addAll(methylIdsForGeneId.keySet());
        geneIds.addAll(mirnaIdsForGeneId.keySet());

        for (Integer geneId : geneIds) {
            ResultElement baseElement = (listByGene ? getGeneticElement(geneId) : getPatient(geneId));
            if (methylIdsForGeneId.get(geneId).size() == 0) {
                methylIdsForGeneId.get(geneId).put(0, 0);
            }
            if (mirnaIdsForGeneId.get(geneId).size() == 0) {
                mirnaIdsForGeneId.get(geneId).put(0, 0);
            }

            for (Integer methylId : methylIdsForGeneId.get(geneId).keySet()) {
                for (Integer mirnaId : mirnaIdsForGeneId.get(geneId).keySet()) {
                    ResultElement element = baseElement.cloneElement();
                    element.methylationTargetId = methylId;
                    element.miRNAId = mirnaId;
                    elements.add(element);
                }
            }
        }
        return elements;
    }

    String makeIdString(int id, int methylationId, int mirnaId) {
        return id + ID_SEPARATOR + methylationId + ID_SEPARATOR + mirnaId;
    }

    String makeIdString(int id) {
        return makeIdString(id, 0, 0);
    }

    private String getElementKeyForColumn(ResultElement element, ColumnType column, boolean listByGene) {
        if (listByGene) {
            if (column instanceof AnomalyType && ((AnomalyType) column).getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe) {
                return makeIdString(element.id, element.methylationTargetId, 0);
            } else if (column instanceof CorrelationType) {
                // depends on type of correlation...
                AnomalyType.GeneticElementType type1 = ((CorrelationType) column).getAnomalyType1().getGeneticElementType();
                AnomalyType.GeneticElementType type2 = ((CorrelationType) column).getAnomalyType2().getGeneticElementType();
                boolean hasMirna = false;
                boolean hasMethyl = false;
                if (type1 == AnomalyType.GeneticElementType.miRNA || type2 == AnomalyType.GeneticElementType.miRNA) {
                    hasMirna = true;
                }
                if (type1 == AnomalyType.GeneticElementType.MethylationProbe || type2 == AnomalyType.GeneticElementType.MethylationProbe) {
                    hasMethyl = true;
                }

                return makeIdString(element.id, hasMethyl ? element.methylationTargetId : 0, hasMirna ? element.miRNAId : 0);

            } else if (column instanceof AnomalyType && ((AnomalyType) column).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                // miRNA type or correlation with miRNA in the 2nd position
                return makeIdString(element.id, 0, element.miRNAId);
            } else {
                // everything else
                return makeIdString(element.id, 0, 0);
            }
        } else {
            // for patient search, we don't have other ids
            return makeIdString(element.id, 0, 0);
        }
    }

    private Integer[] parseIdString(String idString) throws NumberFormatException {
        String[] idStrings = idString.split(ID_SEPARATOR);
        Integer[] ids = new Integer[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            int id = Integer.valueOf(idStrings[i]);
            ids[i] = id;
        }
        return ids;
    }

    Map<String, Serializable> copyAnnotations(ResultElement element, FilterSpecifier filter, SearchType searchType) {
        Map<String, Serializable> annotationsCopy = new HashMap<String, Serializable>();
        for (String key : element.annotations.keySet()) {
            if (key.equals(AnomalySearchConstants.ROWANNOTATIONKEY_URL) && element.annotations.get(key) != null) {
                // check if element has a URL that needs to be completed based on the disease
                String url = element.annotations.get(key).toString();
                if (url.contains("WORKBENCH_TRACK")) {
                    url = url.replace("WORKBENCH_TRACK", String.valueOf(workbenchTrackForDisease.get(filter.getDisease())));
                    annotationsCopy.put(key, url);
                }
            } else if (key.equals(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE)) {
                // only copy biocarta gene symbol for single pathway searches
                if (searchType == SearchType.SINGLE_PATHWAY) {
                    annotationsCopy.put(key, element.annotations.get(key));
                }
            } else {
                annotationsCopy.put(key, element.annotations.get(key));
            }
        }
        return annotationsCopy;
    }

    private String getColumnTypeName(ColumnType column) {
        String name = column.getClass().getName();
        if (column instanceof AnomalyType) {
            name += ID_SEPARATOR + ((AnomalyType) column).getGeneticElementType().toString();
        }
        return name;
    }

    private Map<String, Boolean> getDistinctColumnTypes(List<ColumnType> columnTypes) {
        Map<String, Boolean> distinctColumnTypes = new HashMap<String, Boolean>();
        for (ColumnType column : columnTypes) {
            String name = getColumnTypeName(column);
            if (distinctColumnTypes.get(name) == null) {
                distinctColumnTypes.put(name, false);
            }
        }
        return distinctColumnTypes;
    }

    private boolean valuePassesFilter(ResultValue value, ColumnType columnType) {
        boolean passes = true;
        if (value instanceof ResultBlank) {
            passes = false;
        } else if (columnType instanceof AnomalyType && value instanceof AnomalyResultRatio) {
            // if value is a ratio, check if it is above the threshold for this column
            // must be greater than or equal to the threshold
            passes = ((AnomalyResultRatio) value).getRatio() >= ((AnomalyType) columnType).getFrequency();

        } else if (columnType instanceof CorrelationType) {
            if (value.getValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE) == null) {
                // no p-value means not able to calculate it... so no valid result
                return false;
            }
            Double pval = (Double) value.getValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE);
            // if limit is -1, means no limit so all pass
            passes = (((CorrelationType) columnType).getPvalueLimit() == -1) || (pval <= ((CorrelationType) columnType).getPvalueLimit());
            // also check limit on correlation value
            passes = passes && ((CorrelationType) columnType).passesCriteria(((ResultDouble) value).getValue());
        } else if (columnType instanceof NonMutationAnomalyType) {
            if (value instanceof ResultDouble) {
                passes = ((NonMutationAnomalyType) columnType).passesCriteria(((ResultDouble) value).getValue());
            }
        }
        return passes;
    }


    private float roundFloat(Float aFloat, int decimals) {
        int multiplier = 1;
        for (int i = 0; i < decimals; i++) {
            multiplier *= 10;
        }
        float fl = aFloat * (float) multiplier;
        int rounded = Math.round(fl);
        fl = (float) rounded / multiplier;
        return fl;
    }

    private Map<String, ResultValue> getCorrelationColumnResult(final CorrelationType correlationType, final FilterSpecifier filter,
                                                                Integer[] geneIds, Integer[] patientIds) {

        // query will be sorted by element (gene or patient) name
        List<Object> bindVariables = new ArrayList<Object>();
        String query = buildCorrelationQuery(filter, correlationType, geneIds, patientIds, bindVariables);
        Object[] params = bindVariables.toArray();

        final Map<String, ResultValue> correlations = new HashMap<String, ResultValue>();

        final String[] currentElement = new String[1];
        currentElement[0] = null;
        final List<Float> elementValues1 = new ArrayList<Float>();
        final List<Float> elementValues2 = new ArrayList<Float>();

        // do actual query
        // once get to new element, calculate correlation and then don't save raw values!
        // otherwise for all genes & patients, uses up way too much memory to pull 3.6 million values in
        if (DEBUG_TIMING) {
            StringBuilder msg = (new StringBuilder()).append("Correlation timing - query: ")
                    .append(query).append('\n').append("params: ");
            for (Object param : params) {
                msg.append(param.toString()).append(" ");
            }
            logger.logDebug(msg.toString());
            startGetRows = 0;
            elapsedGetRows = 0;
            startRunCalc = 0;
            elapsedRunCalc = 0;
            startRunQuery = System.currentTimeMillis();
            elapsedRunQuery = 0;
        }
        getJdbcTemplate().query(query, params,
                new RowCallbackHandler() {
                    public void processRow(ResultSet resultSet) throws SQLException {
                        if (DEBUG_TIMING) {
                            if (elapsedRunQuery == 0) {
                                endRunQuery = System.currentTimeMillis();
                                elapsedRunQuery = endRunQuery - startRunQuery;
                                logger.logDebug("run-query secs: " + (((double) elapsedRunQuery) / 1000.));
                            }
                            if (startGetRows == 0) startGetRows = System.currentTimeMillis();
                        }

                        int elementId = resultSet.getInt("element_id");
                        int methylationId = 0;
                        int mirnaId = 0;

                        // special case: neither type gene, but not the same
                        if (correlationType.getAnomalyType1().getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe &&
                                correlationType.getAnomalyType2().getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                            methylationId = resultSet.getInt("other_id1");
                            mirnaId = resultSet.getInt("other_id2");
                        } else if (correlationType.getAnomalyType1().getGeneticElementType() == AnomalyType.GeneticElementType.miRNA &&
                                correlationType.getAnomalyType2().getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe) {
                            methylationId = resultSet.getInt("other_id2");
                            mirnaId = resultSet.getInt("other_id1");
                        } else if (correlationType.getAnomalyType1().getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe ||
                                correlationType.getAnomalyType2().getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe) {
                            methylationId = resultSet.getInt("other_id");
                        } else if (correlationType.getAnomalyType1().getGeneticElementType() == AnomalyType.GeneticElementType.miRNA ||
                                correlationType.getAnomalyType2().getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                            mirnaId = resultSet.getInt("other_id");
                        }
                        String idString = makeIdString(elementId, methylationId, mirnaId);

                        // get values, add to each list for the element
                        float value1 = resultSet.getFloat("value1");
                        float value2 = resultSet.getFloat("value2");

                        if (currentElement[0] != null && !idString.equals(currentElement[0])) {
                            if (DEBUG_TIMING) {
                                endGetRows = System.currentTimeMillis();
                                elapsedGetRows += (endGetRows - startGetRows);
                                startRunCalc = endGetRows;
                            }

                            // need to process the previous element since we have moved on to a new one
                            runCorrelation(elementValues1, elementValues2, correlations, currentElement);

                            if (DEBUG_TIMING) {
                                endRunCalc = System.currentTimeMillis();
                                elapsedRunCalc += (endRunCalc - startRunCalc);
                                startGetRows = endRunCalc;
                            }
                        }

                        currentElement[0] = idString;
                        elementValues1.add(value1);
                        elementValues2.add(value2);
                    }
                }
        );

        // process last element!
        if (elementValues1.size() > 0) {
            if (DEBUG_TIMING) {
                endGetRows = System.currentTimeMillis();
                elapsedGetRows += (endGetRows - startGetRows);
                startRunCalc = endGetRows;
            }
            runCorrelation(elementValues1, elementValues2, correlations, currentElement);
            if (DEBUG_TIMING) {
                endRunCalc = System.currentTimeMillis();
                elapsedRunCalc += (endRunCalc - startRunCalc);
            }
        }

        if (DEBUG_TIMING) {
            logger.logDebug("get-rows secs: " + ((double) elapsedGetRows) / 1000.);
            logger.logDebug("run-calc secs: " + ((double) elapsedRunCalc) / 1000.);
        }

        return correlations;
    }

    private void runCorrelation(List<Float> elementValues1, List<Float> elementValues2, Map<String, ResultValue> correlations, String[] currentElement) {
        double[] scores = calculateCorrelation(elementValues1, elementValues2);

        if (scores[1] != -1) {
            ResultDouble correlation = new ResultDouble();
            correlation.setValue((float) scores[0]);
            //store p-value as value annotation
            // note if p-value is -1, that means could not calculate, so set to null
            correlation.addValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE, scores[1] == -1 ? null : scores[1]);
            correlations.put(currentElement[0], correlation);
        } else {
            correlations.put(currentElement[0], new ResultBlank());
        }
        elementValues1.clear();
        elementValues2.clear();
    }

    private double[] calculateCorrelation(final List<Float> elementValues1, final List<Float> elementValues2) {
        double[] ret = new double[2];
        float[] scores1 = new float[elementValues1.size()];
        float[] scores2 = new float[elementValues2.size()];
        for (int i = 0; i < elementValues1.size(); i++) {
            scores1[i] = elementValues1.get(i);
            scores2[i] = elementValues2.get(i);
        }
        float correlationVal = correlationCalculator.calculateCorrelation(scores1, scores2);

        ret[0] = roundFloat(correlationVal, 2);
        // calculate the t-value, which then is used to find the p-value
        // t = (r * sqrt(n-2)) / sqrt(1-r^2)
        // p = 2 * (1-P), where P is calculated from the stats package using df = n-2
        double t = correlationVal * Math.sqrt((elementValues1.size() - 2)) / Math.sqrt(1 - (correlationVal * correlationVal));
        if (elementValues1.size() > 2) {
            double pval = (double) 2 * (1 - DistLib.t.cumulative(t, elementValues1.size() - 2));
            ret[1] = pval;
        } else {
            ret[1] = -1;
        }
        return ret;
    }

    private String buildValueConstraint(ColumnType column, List<Object> bindVariables) {
        // if non-mutation, get limits for value
        StringBuilder query = new StringBuilder();
        if (column instanceof NonMutationAnomalyType) {

            boolean hasLower = ((NonMutationAnomalyType) column).getLowerOperator() != null && ((NonMutationAnomalyType) column).getLowerOperator() != UpperAndLowerLimits.Operator.None;
            boolean hasUpper = ((NonMutationAnomalyType) column).getUpperOperator() != null && ((NonMutationAnomalyType) column).getUpperOperator() != UpperAndLowerLimits.Operator.None;
            // if either lower or upper bound set, add clause
            if (hasLower || hasUpper) {
                // if lower bound set, add clause
                if (hasLower) {
                    query.append("av.anomaly_value").
                            append(((NonMutationAnomalyType) column).getLowerOperator().toString()).
                            append("?");
                    bindVariables.add(((NonMutationAnomalyType) column).getLowerLimit());
                }
                // if both set, add 'or' for between them
                if (hasLower && hasUpper) {
                    query.append(" or ");
                }
                // if upper bound set, add clause
                if (hasUpper) {
                    query.append("av.anomaly_value").
                            append(((NonMutationAnomalyType) column).getUpperOperator().toString()).
                            append("?");
                    bindVariables.add(((NonMutationAnomalyType) column).getUpperLimit());
                }
            }
        } else if (column instanceof MutationType) {
            query.append("av.anomaly_value>? ");
            bindVariables.add(0);
        }
        return query.toString();
    }

    private String getRegionClause(FilterSpecifier filter, List<Object> bindVariables) {
        StringBuilder clause = new StringBuilder();
        if (filter.getGeneListOptions() == FilterSpecifier.GeneListOptions.Region && filter.getChromRegions().size() > 0) {
            clause.append("AND (");

            // gene has to overlap only one region, so join with ORs
            for (int i = 0; i < filter.getChromRegions().size(); i++) {
                FilterChromRegion chromRegion = filter.getChromRegions().get(i);
                if (i > 0) {
                    clause.append("OR ");
                }
                if (chromRegion.getChromosome() == null || chromRegion.getChromosome().trim().equals("")) {
                    throw new IllegalArgumentException("Region does not specify a chromosome");
                }
                if (chromRegion.getStop() > 0 && chromRegion.getStart() > 0 && chromRegion.getStop() < chromRegion.getStart()) {
                    throw new IllegalArgumentException("Region range is invalid: " + chromRegion.getStart() + "-" + chromRegion.getStop());
                }
                // ge.chrom='n' and (ge.start in region or ge.stop in region or ge covers region)
                clause.append("(ge.chromosome=? ");
                bindVariables.add(chromRegion.getChromosome());

                // if stop is <1 that means not specified
                if (chromRegion.getStop() < 1 && chromRegion.getStart() > 0) {
                    // just start of region is defined, so find all genes that stop after that
                    clause.append(" AND ge.stop_pos>? ");
                    bindVariables.add(chromRegion.getStart());

                } else if (chromRegion.getStart() < 1 && chromRegion.getStop() > 0) {
                    // just end of region is defined, so find all genes that start before that
                    clause.append(" AND ge.start_pos<? ");
                    bindVariables.add(chromRegion.getStop());

                } else if (chromRegion.getStart() > 0 && chromRegion.getStop() > 0) {
                    // both start and end defined, find all genes that overlap in any way with the region
                    clause.append(" AND ((ge.start_pos>=? AND ge.start_pos<=? ").
                            append(") OR (ge.stop_pos>=? AND ge.stop_pos<=?").
                            append(") OR (ge.start_pos<? AND ge.stop_pos>?))");
                    bindVariables.add(chromRegion.getStart());
                    bindVariables.add(chromRegion.getStop());
                    bindVariables.add(chromRegion.getStart());
                    bindVariables.add(chromRegion.getStop());
                    bindVariables.add(chromRegion.getStart());
                    bindVariables.add(chromRegion.getStop());
                }
                clause.append(") ");
            }
            clause.append(") ");
        }
        return clause.toString();
    }

    /*
     * gets results for a single column
     * returns map between gene or patient element and result ratio
     * key to map is string in form "id:otherId" where otherId is 0 if none.
     */
    private Map<String, ResultValue> getAnomalyColumnResult(final AnomalyType column, final FilterSpecifier filter,
                                                            final Integer[] geneIds, final Integer[] patientIds) {
        final Map<String, ResultValue> resultVals = new HashMap<String, ResultValue>();
        final boolean listByGene = (filter.getListBy() == FilterSpecifier.ListBy.Genes);
        final boolean doGistic = listByGene && column instanceof CopyNumberType
                && ((CopyNumberType) column).getCalculationType() == CopyNumberType.CalculationType.GISTIC;

        List<Object> bindVariables = new ArrayList<Object>();
        String query = buildAnomalyQuery(filter, column, geneIds, patientIds, bindVariables);
        Object[] params = bindVariables.toArray();

        if (DEBUG_TIMING) {
            StringBuilder msg = (new StringBuilder()).append("Anomaly timing - query: ")
                    .append(query).append('\n').append("params: ");
            for (Object param : params) {
                msg.append(param.toString()).append(" ");
            }
            logger.logDebug(msg.toString());
            startGetRows = 0;
            elapsedGetRows = 0;
            startRunQuery = System.currentTimeMillis();
            elapsedRunQuery = 0;
        }

        getJdbcTemplate().query(query, params,
                new RowCallbackHandler() {
                    public void processRow(ResultSet row) throws SQLException {
                        if (DEBUG_TIMING) {
                            if (elapsedRunQuery == 0) {
                                endRunQuery = System.currentTimeMillis();
                                elapsedRunQuery = endRunQuery - startRunQuery;
                                logger.logDebug("run-query secs: " + (((double) elapsedRunQuery) / 1000.));
                            }
                            if (startGetRows == 0) startGetRows = System.currentTimeMillis();
                        }

                        Integer id = row.getInt("element_id");
                        String idString;
                        if (listByGene && column.getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                            idString = makeIdString(id, 0, row.getInt("other_id"));
                        } else if (listByGene && column.getGeneticElementType() == AnomalyType.GeneticElementType.MethylationProbe) {
                            idString = makeIdString(id, row.getInt("other_id"), 0);
                        } else {
                            idString = makeIdString(id, 0, 0);
                        }

                        if (doGistic) {
                            ResultDouble gistic = new ResultDouble();
                            gistic.setValue(row.getDouble("gistic_value"));

                            resultVals.put(idString, gistic);
                        } else {
                            AnomalyResultRatio ratio = (AnomalyResultRatio) resultVals.get(idString);
                            if (ratio == null) {
                                ratio = new AnomalyResultRatio();
                            }
                            ratio.setTotal(ratio.getTotal() + row.getInt("total_count"));
                            ratio.setAffected(row.getInt("affected_count"));
                            // only add annotation if paired... so if no annotation, means not paired
                            if (!listByGene && row.getInt("is_paired") == 1) {
                                ratio.addValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, true);
                            }

                            resultVals.put(idString, ratio);
                        }
                    }
                }
        );

        if (DEBUG_TIMING) {
            endGetRows = System.currentTimeMillis();
            logger.logDebug("get-rows secs: " + ((double) elapsedGetRows) / 1000.);
        }

        return resultVals;
    }

    public final void setCorrelationCalculator(CorrelationCalculator correlationCalculator) {
        this.correlationCalculator = correlationCalculator;
    }

    public final void setFishersExact(FishersExact fishersExact) {
        this.fishersExact = fishersExact;
    }

    // inner class used by this class for storing results while searching/processing
    // each element is either a gene or a patient or a pathway.
    // annotations are row-level annotations, not column/cell annotations
    public class ResultElement implements Comparable {
        String name;
        int id;
        int methylationTargetId = 0;
        int miRNAId = 0;
        Map<Integer, ResultElement> children = new HashMap<Integer, ResultElement>();
        Map<String, Serializable> annotations = new HashMap<String, Serializable>(5);

        public boolean equals(Object o) {
            // two resultelements are equal if their ids are the same            
            return o instanceof ResultElement && this.id == ((ResultElement) o).id &&
                    this.methylationTargetId == ((ResultElement) o).methylationTargetId &&
                    this.miRNAId == ((ResultElement) o).miRNAId;
        }

        public String toString() {
            return name;
        }

        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }

        public String getIdString() {
            return makeIdString(id, methylationTargetId, miRNAId);
        }

        public int hashCode() {
            return getIdString().hashCode();
        }

        public ResultElement cloneElement() {
            ResultElement clone = new ResultElement();
            clone.name = this.name;
            clone.id = this.id;
            clone.methylationTargetId = this.methylationTargetId;
            clone.miRNAId = this.miRNAId;
            for (ResultElement child : this.children.values()) {
                ResultElement childClone = child.cloneElement();
                clone.children.put(childClone.id, childClone);
            }
            for (String annotationKey : this.annotations.keySet()) {
                clone.annotations.put(annotationKey, this.annotations.get(annotationKey));
            }
            return clone;
        }
    }
}
