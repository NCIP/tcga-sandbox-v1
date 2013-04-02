/*
 *
 *  * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  * Copyright Notice.  The software subject to this notice and license includes both human
 *  * readable source code form and machine readable, binary, object code form (the "caBIG
 *  * Software").
 *  *
 *  * Please refer to the complete License text for full details at the root of the project.
 *
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataTypeCountQueries;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Queries for calculation and saving of the Cancer Details counts, and for querying the numbers back out.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataTypeCountQueriesImpl extends SimpleJdbcDaoSupport implements DataTypeCountQueries {

    private static final String DISEASE_ABBREVIATION = "disease_abbreviation";
    private static final String HEADER_NAME = "header_name";
    private static final String CASE_COUNT = "case_count";
    private static final String HEALTHY_CONTROL_COUNT = "healthy_control_count";

    private static final String TUMOR_DETAILS_QUERY = "SELECT " +
            DISEASE_ABBREVIATION + ", " +
            HEADER_NAME + ", " +
            CASE_COUNT + ", " +
            HEALTHY_CONTROL_COUNT +
            " FROM home_page_drilldown " +
            "WHERE " + DISEASE_ABBREVIATION + " = ? ";

    private static final String HEADER_TOTAL = "Total";
    private static final String HEADER_EXOME = "Exome";
    private static final String HEADER_SNP = "SNP";
    private static final String HEADER_MRNA = "mRNA";
    private static final String HEADER_MIRNA = "miRNA";
    private static final String HEADER_METHYLATION = "Methylation";
    private static final String HEADER_CLINICAL = "Clinical";
    private static final String SQL_CASE_TOTAL = "SELECT disease_abbreviation, count(distinct case) as cases\n" +
            "FROM case_data_received \n" +
            "where is_tumor=1 and data_type != 'Biospecimen'\n" +
            "group by disease_abbreviation\n" +
            "order by disease_abbreviation";

    private static final String SQL_HEALTHY_CASE_TOTAL = "SELECT cdr1.disease_abbreviation, count(distinct cdr1.case) as cases\n" +
            "FROM   case_data_received cdr1\n" +
            "WHERE  cdr1.sample_Type_Code IN (10,11) and data_type != 'Biospecimen'\n" +
            "AND    NOT EXISTS (SELECT * FROM case_data_received cdr2\n" +
            "                   WHERE cdr1.case = cdr2.case\n" +
            "                   AND   cdr1.disease_abbreviation = cdr2.disease_abbreviation\n" +
            "                   AND   cdr2.is_tumor = 1)\n" +
            "group by cdr1.disease_abbreviation\n" +
            "order by disease_abbreviation";

    private static final String SQL_CASE_CLINICAL = "SELECT disease_abbreviation, count(distinct case) as cases, data_type\n" +
            " FROM   case_data_received \n" +
            " WHERE  data_type = 'Clinical'\n" +
            " AND    is_tumor = 1\n" +
            " group by disease_abbreviation, data_Type\n" +
            " order by disease_abbreviation";

    private static final String SQL_HEALTHY_CLINICAL = "WITH tumor AS\n" +
            " (SELECT disease_abbreviation, case, data_type\n" +
            " FROM   case_data_received \n" +
            " WHERE  data_type = 'Clinical'\n" +
            " AND    is_tumor = 1),\n" +
            " healthy AS\n" +
            " (SELECT disease_abbreviation, case, data_type\n" +
            " FROM   case_data_received \n" +
            " WHERE  data_type = 'Clinical'\n" +
            " AND    sample_type_code in (10,11))\n" +
            " SELECT disease_abbreviation, COUNT(case) as cases,  data_Type \n" +
            " FROM healthy \n" +
            " WHERE NOT EXISTS (SELECT case  \n" +
            "                   FROM tumor \n" +
            "                   WHERE tumor.case = healthy.case) \n" +
            "GROUP BY disease_abbreviation, data_type";

    private static final String SQL_CASE_DATA_TYPES = "WITH tumor as\n" +
            "(SELECT disease_abbreviation, case, data_type\n" +
            "FROM   case_data_received \n" +
            "WHERE data_type != 'Clinical' and data_type != 'Biospecimen'\n" +
            "AND   is_tumor = 1),\n" +
            "norm as\n" +
            "(SELECT disease_abbreviation, case, data_type\n" +
            "FROM   case_data_received \n" +
            "WHERE data_type = 'Exome'\n" +
            "AND   is_tumor = 0)\n" +
            "SELECT tumor.disease_abbreviation, count(DISTINCT tumor.case) cases, tumor.data_type\n" +
            "FROM   tumor\n" +
            "WHERE  data_type != 'Exome' and data_type != 'Biospecimen'\n" +
            "GROUP BY tumor.disease_abbreviation, tumor.data_type\n" +
            "UNION\n" +
            "SELECT tumor.disease_abbreviation, count(DISTINCT tumor.case) cases, tumor.data_type\n" +
            "FROM   tumor, norm\n" +
            "WHERE  tumor.data_type = norm.data_type\n" +
            "AND    tumor.case = norm.case\n" +
            "AND    tumor.data_type = 'Exome'\n" +
            "GROUP BY tumor.disease_abbreviation, tumor.data_Type\n" +
            "order by disease_abbreviation, data_type";

    private static final String SQL_HEALTHY_CONTROL_DATA_TYPES = "SELECT cdr1.disease_abbreviation, " +
            " count(distinct cdr1.case) as cases, cdr1.data_type\n" +
            " FROM   case_data_received cdr1\n" +
            " WHERE  cdr1.sample_Type_Code IN (10,11)\n" +
            " AND data_type != 'Clinical' and data_type != 'Biospecimen'\n" +
            " AND    NOT EXISTS (SELECT * FROM case_data_received cdr2\n" +
            "                    WHERE cdr1.case = cdr2.case\n" +
            "                    AND   cdr1.disease_abbreviation = cdr2.disease_abbreviation\n" +
            "                    AND   cdr2.is_tumor = 1)\n" +
            " group by cdr1.disease_abbreviation, cdr1.data_type\n" +
            " order by disease_abbreviation, data_type";

    @Override
    public void calculateAndSaveCounts() {
        // first refresh the materialized view
        getSimpleJdbcTemplate().update("call dbms_mview.refresh(?)", "case_data_received");

        final Map<String, DataTypeCount> caseCountsPerDisease = new HashMap<String, DataTypeCount>();
        final Map<String, DataTypeCount> healthyCountsPerDisease = new HashMap<String, DataTypeCount>();
        calculateTotals(caseCountsPerDisease, healthyCountsPerDisease);
        calculateDataTypes(caseCountsPerDisease, healthyCountsPerDisease);
        calculateClinical(caseCountsPerDisease, healthyCountsPerDisease);

        // at end save all at once
        final Set<String> diseases = new HashSet<String>();
        diseases.addAll(caseCountsPerDisease.keySet());
        diseases.addAll(healthyCountsPerDisease.keySet());

        for (final String disease : diseases) {
            save(disease, caseCountsPerDisease.get(disease), healthyCountsPerDisease.get(disease));
        }
    }

       private static final String SQL_HOME_PAGE_DRILLDOWN_MERGE = "MERGE INTO home_page_drilldown h " +
            "USING " +
            "(SELECT ? disease_abbreviation,? header_name, ? case_count, ? healthy_control_count " +
            "FROM DUAL) a " +
            "ON (h.disease_abbreviation = a.disease_abbreviation " +
            "AND h.header_name = a.header_name) " +
            "WHEN MATCHED THEN " +
            "UPDATE SET h.case_count = a.case_count, " +
            "h.healthy_control_count = a.healthy_control_count " +
            "WHEN NOT MATCHED THEN " +
            "INSERT (disease_abbreviation,header_name,case_count,healthy_control_count) " +
            "VALUES (a.disease_abbreviation,a.header_name,a.case_count,a.healthy_control_count)";

    private void save(final String tumor, DataTypeCount caseCount, DataTypeCount healthyCount) {
        // if either is null make new one, which will have 0 for all values
        if (caseCount == null) {
            caseCount = new DataTypeCount(tumor, DataTypeCount.CountType.Case);
        }

        if (healthyCount == null) {
            healthyCount = new DataTypeCount(tumor, DataTypeCount.CountType.HealthyControl);
        }

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_TOTAL,
                caseCount.getTotal(),
                healthyCount.getTotal());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_SNP,
                caseCount.getSnp(),
                healthyCount.getSnp());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_METHYLATION,
                caseCount.getMethylation(),
                healthyCount.getMethylation());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_MRNA,
                caseCount.getmRna(),
                healthyCount.getmRna());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_MIRNA,
                caseCount.getMiRna(),
                healthyCount.getMiRna());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_EXOME,
                caseCount.getExome(),
                healthyCount.getExome());

        getSimpleJdbcTemplate().update(SQL_HOME_PAGE_DRILLDOWN_MERGE, tumor, HEADER_CLINICAL,
                caseCount.getClinical(),
                healthyCount.getClinical());
    }


    private void calculateClinical(final Map<String, DataTypeCount> dataTypeCountsPerDisease,
                                   final Map<String, DataTypeCount> healthyCountsPerDisease) {

        calculate(DataTypeCount.CountType.Case, SQL_CASE_CLINICAL, clinicalCountSetter, dataTypeCountsPerDisease,
                false);
        calculate(DataTypeCount.CountType.HealthyControl, SQL_HEALTHY_CLINICAL, clinicalCountSetter,
                healthyCountsPerDisease, false);
    }



    private void calculateDataTypes(final Map<String, DataTypeCount> dataTypeCountsPerDisease, final Map<String, DataTypeCount> healthyCountsPerDisease) {
        calculate(DataTypeCount.CountType.Case, SQL_CASE_DATA_TYPES, dataTypeCountSetter, dataTypeCountsPerDisease, true);
        calculate(DataTypeCount.CountType.HealthyControl, SQL_HEALTHY_CONTROL_DATA_TYPES, dataTypeCountSetter, healthyCountsPerDisease, true);
    }



    private void calculateTotals(final Map<String, DataTypeCount> dataTypeCountsPerDisease, final Map<String, DataTypeCount> healthyCountsPerDisease) {

        calculate(DataTypeCount.CountType.Case, SQL_CASE_TOTAL, totalCountSetter, dataTypeCountsPerDisease, false);
        calculate(DataTypeCount.CountType.HealthyControl, SQL_HEALTHY_CASE_TOTAL, totalCountSetter, healthyCountsPerDisease, false);
    }

    private void calculate(final DataTypeCount.CountType countType, final String sql, final DataTypeCountSetter dataTypeCountSetter,
                           final Map<String, DataTypeCount> dataTypeCountsPerDisease, final boolean expectDataTypeColumn) {

        // get Case totals
        getJdbcTemplate().query(sql, new RowCallbackHandler() {
            public void processRow(final ResultSet rs) throws SQLException {
                final String disease = rs.getString(DISEASE_ABBREVIATION);

                final int caseCount = rs.getInt("cases");
                String dataType = null;
                if (expectDataTypeColumn) {
                    dataType= rs.getString("data_type");
                }

                DataTypeCount dataTypeCountForDisease = dataTypeCountsPerDisease.get(disease);
                if (dataTypeCountForDisease == null) {
                    dataTypeCountForDisease = new DataTypeCount(disease, countType);
                    dataTypeCountsPerDisease.put(disease, dataTypeCountForDisease);
                }
                dataTypeCountSetter.setCount(dataTypeCountForDisease, caseCount, dataType);
            }
        });

    }


    interface DataTypeCountSetter {
        void setCount(DataTypeCount dataTypeCount, int count, String dataType);
    }

    private final DataTypeCountSetter clinicalCountSetter = new DataTypeCountSetter() {
        @Override
        public void setCount(final DataTypeCount dataTypeCount, final int count, final String dataType) {
            dataTypeCount.setClinical(count);
        }
    };

    private final DataTypeCountSetter totalCountSetter = new DataTypeCountSetter() {
        @Override
        public void setCount(final DataTypeCount dataTypeCount, final int count, final String dataType) {
            dataTypeCount.setTotal(count);
        }
    };

    private final DataTypeCountSetter dataTypeCountSetter = new DataTypeCountSetter() {
        @Override
        public void setCount(final DataTypeCount dataTypeCount, final int count, final String dataType) {
            if (dataType.equals("SNP")) {
                dataTypeCount.setSnp(count);
            } else if (dataType.equals("Exome")) {
                dataTypeCount.setExome(count);
            } else if (dataType.equals("Methylation")) {
                dataTypeCount.setMethylation(count);
            } else if (dataType.equals("mRNA")) {
                dataTypeCount.setmRna(count);
            } else if (dataType.equals("miRNA")) {
                dataTypeCount.setMiRna(count);
            } else if (dataType.equals("Biospecimen")) {
                // skip
            } else {
                throw new IllegalArgumentException("Unknown data type: " + dataType);
            }
        }
    };

    @Override
    public DataTypeCount[] getDataTypeCountArray(final String diseaseAbbreviation) {
        final DataTypeCount[] dataTypeCounts = new DataTypeCount[2];
        dataTypeCounts[0] = new DataTypeCount(diseaseAbbreviation, DataTypeCount.CountType.Case);
        dataTypeCounts[1] = new DataTypeCount(diseaseAbbreviation, DataTypeCount.CountType.HealthyControl);

        getJdbcTemplate().query(TUMOR_DETAILS_QUERY, new RowCallbackHandler() {
            public void processRow(final ResultSet rs) throws SQLException {
                final String header = rs.getString(HEADER_NAME);
                final int caseCount = rs.getInt(CASE_COUNT);
                final int healthyCount = rs.getInt(HEALTHY_CONTROL_COUNT);

                if (header.equals(HEADER_TOTAL)) {
                    dataTypeCounts[0].setTotal(caseCount);
                    dataTypeCounts[1].setTotal(healthyCount);
                } else if (header.equals(HEADER_EXOME)) {
                    dataTypeCounts[0].setExome(caseCount);
                    dataTypeCounts[1].setExome(healthyCount);
                } else if (header.equals(HEADER_SNP)) {
                    dataTypeCounts[0].setSnp(caseCount);
                    dataTypeCounts[1].setSnp(healthyCount);
                } else if (header.equals(HEADER_METHYLATION)) {
                    dataTypeCounts[0].setMethylation(caseCount);
                    dataTypeCounts[1].setMethylation(healthyCount);
                } else if (header.equals(HEADER_MRNA)) {
                    dataTypeCounts[0].setmRna(caseCount);
                    dataTypeCounts[1].setmRna(healthyCount);
                } else if (header.equals(HEADER_MIRNA)) {
                    dataTypeCounts[0].setMiRna(caseCount);
                    dataTypeCounts[1].setMiRna(healthyCount);
                } else if (header.equals(HEADER_CLINICAL)) {
                    dataTypeCounts[0].setClinical(caseCount);
                    dataTypeCounts[1].setClinical(healthyCount);
                }
            }
        }, diseaseAbbreviation);
        return dataTypeCounts;
    }
}
