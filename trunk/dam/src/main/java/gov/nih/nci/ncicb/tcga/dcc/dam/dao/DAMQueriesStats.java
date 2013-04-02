/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.util.*;

/**
 * DBUnit test for DAMQueriesStats
 *
 * @author nanans
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesStats implements DAMQueriesStatsI {
    
    private static final String STATS_QUERY =
            "select d.disease_abbreviation, dt.name as data_type_name, f.level_number, count(distinct(sample)) as num_samples " +
                    "from shipped_biospecimen_aliquot bb, shipped_biospecimen_file b2f, file_info f, file_to_archive f2a, archive_info a, disease d, platform p, data_type dt " +
                    "where " +
                    "bb.shipped_biospecimen_id=b2f.shipped_biospecimen_id and " +
                    "b2f.file_id=f.file_id and " +
                    "f2a.file_id=f.file_id and " +
                    "f2a.archive_id=a.archive_id and " +
                    "a.disease_id=d.disease_id and " +
                    "a.platform_id=p.platform_id and " +
                    "p.base_data_type_id=dt.data_type_id and " +
                    "a.is_latest = 1 and " +
                    "f.level_number is not null " +
                    "group by d.disease_abbreviation, dt.name, f.level_number";

    // note: removed separate query for mutation stats, because we are now adding biospecimen_to_file records for
    // maf files also.  but, the level will just be level 2 until we add a qclive feature to automatically create
    // level 3 maf archives from a level 2 archive.  o

    private static final String NUM_SAMPLES = "num_samples";
    private static final String DATA_TYPE_NAME = "data_type_name";
    private static final String LEVEL_NUMBER = "level_number";
    private static final String DISEASE_ABBREVIATION = "disease_abbreviation";

    private SimpleJdbcDaoSupport daoSupport;

    public DAMQueriesStats() {
        daoSupport = new SimpleJdbcDaoSupport();
    }

    public void setDataSource(final DataSource dataSource) {
        daoSupport.setDataSource(dataSource);
    }

    //todo  fix queries so they don't double-count samples <-- I don't know what this is referring to.  :(
    public String[][] getStats() {
        List<Map<String, Object>> records = getStatRecords();
        HashMap<String, Object> diseases = new HashMap<String, Object>();
        HashMap<String, Integer> headers = new HashMap<String, Integer>();
        findUniqueDiseasesAndHeaders(records, diseases, headers);
        int colcount = headers.size();
        int rowcount = diseases.size();
        String[][] ret = new String[colcount + 1][rowcount + 2];
        int col = 1; //skip first column
        int row = 0;
        //first header row - platform types
        for (int i = 0; i < colcount; i++) {
            Map<String, Object> record = records.get(i);
            ret[col++][row] = (String) record.get(DATA_TYPE_NAME);
        }
        //second header row - levels
        col = 1;
        row++;
        for (int i = 0; i < colcount; i++) {
            Map<String, Object> record = records.get(i);
            ret[col++][row] = "L" + record.get(LEVEL_NUMBER);
        }
        //rows for the numbers, with tumor abbreviation on left
        String prevdisease = "";
        for (final Map<String, Object> record : records) {
            String disease = (String) record.get(DISEASE_ABBREVIATION);
            if (!disease.equals(prevdisease)) {
                row++;
                ret[0][row] = disease;
                prevdisease = disease;
            }
            //look up the column from the headers map. This is how we can skip the gaps
            col = headers.get(record.get(DATA_TYPE_NAME) + "." + record.get(LEVEL_NUMBER));
            ret[col][row] = ( record.get(NUM_SAMPLES)).toString();
        }
        return ret;
    }

    private void findUniqueDiseasesAndHeaders(final List<Map<String, Object>> records,
                                              final HashMap<String, Object> diseases,
                                              final HashMap<String, Integer> headers) {
        //count the disease types
        for (final Map<String, Object> record : records) {
            String key = (String) record.get(DISEASE_ABBREVIATION);
            diseases.put(key, key);
        }

        // find all headers
        int iheader = 1;
        for (final Map<String, Object> record : records) {
            String key = record.get(DATA_TYPE_NAME) + "." + record.get(LEVEL_NUMBER);
            headers.put(key, iheader++);
        }
    }

    private List<Map<String, Object>> getStatRecords() {
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        SimpleJdbcTemplate jdbc = daoSupport.getSimpleJdbcTemplate();
        records.addAll(jdbc.queryForList(STATS_QUERY));
        Collections.sort(records, new Comparator<Map<String, Object>>() {
            public int compare(final Map<String, Object> record1, final Map<String, Object> record2) {
                String tumor1 = (String) record1.get(DISEASE_ABBREVIATION);
                String tumor2 = (String) record2.get(DISEASE_ABBREVIATION);
                String datatype1 = (String) record1.get(DATA_TYPE_NAME);
                String datatype2 = (String) record2.get(DATA_TYPE_NAME);
                Integer level1 = ((Number) record1.get(LEVEL_NUMBER)).intValue();
                Integer level2 = ((Number) record2.get(LEVEL_NUMBER)).intValue();
                int compareResult = tumor1.compareTo(tumor2);
                if (compareResult == 0) {
                    compareResult = datatype1.compareTo(datatype2);
                    if (compareResult == 0) {
                        compareResult = level1.compareTo(level2);
                    }
                }
                return compareResult;
            }
        });
        return records;
    }
}
    