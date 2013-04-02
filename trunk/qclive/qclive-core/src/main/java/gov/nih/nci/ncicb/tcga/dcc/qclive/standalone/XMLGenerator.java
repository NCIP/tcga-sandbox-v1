package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import com.thoughtworks.xstream.XStream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates XML file which contains data to schedule level 2 jobs
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class XMLGenerator {
    private DataSource dataSource;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private static String REPLACE_DISEASE = "REPLACE_DISEASE";
    private static final String SELECT_DISEASE = " select disease_abbreviation from " +
            REPLACE_DISEASE +
            "disease";

    private static final String SELECT_EXPERIMENTS = " select distinct platform.platform_name," +
            " center.domain_name," +
            " data_set.experiment_id " +
            " FROM " +
            REPLACE_DISEASE +
            "data_set, " +
            REPLACE_DISEASE +
            "center," +
            REPLACE_DISEASE +
            "platform, " +
            REPLACE_DISEASE +
            "archive_info" +
            " WHERE " +
            " data_set.archive_id = archive_info.archive_id AND" +
            " archive_info.is_latest = 1 AND" +
            " data_set.center_id = center.center_id AND" +
            " data_set.platform_id = platform.platform_id ";


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    public String getReplacedQuery(final String query, String diseaseDBName) {

        diseaseDBName = (diseaseDBName.length() > 0) ? diseaseDBName + "." : diseaseDBName;
        final String replacedQuery = query.replaceAll(REPLACE_DISEASE, diseaseDBName);
        System.out.println("REPLACED QUERY " + replacedQuery);
        return replacedQuery;
    }


    public void generateDataXML(final String diseaseDBName) throws IOException {
        // get the disease
        final String diseaseAbbreviation = simpleJdbcTemplate.queryForObject(getReplacedQuery(SELECT_DISEASE, diseaseDBName), String.class);

        // get the List of experiments
        final Map<String, Level2DataFilterBean> dataMap = new HashMap<String, Level2DataFilterBean>();
        ((JdbcTemplate) simpleJdbcTemplate.getJdbcOperations()).query(getReplacedQuery(SELECT_EXPERIMENTS, diseaseDBName),
                new RowCallbackHandler() {
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        final String domainName = resultSet.getString("domain_name");
                        final String platformName = resultSet.getString("platform_name");
                        final Long experimentId = resultSet.getLong("experiment_id");
                        final String key = domainName + "_" + platformName;
                        Level2DataFilterBean level2DataFilterBean = dataMap.get(key);
                        if (level2DataFilterBean == null) {
                            level2DataFilterBean = new Level2DataFilterBean();
                            level2DataFilterBean.setCenterDomainName(domainName);
                            level2DataFilterBean.setPlatformName(platformName);
                            level2DataFilterBean.setDiseaseAbbreviation(diseaseAbbreviation);
                            dataMap.put(key, level2DataFilterBean);
                        }
                        level2DataFilterBean.addExperimentId(experimentId);
                    }
                }
        );


        List<Level2DataFilterBean> data = new ArrayList<Level2DataFilterBean>(dataMap.values());
        if (data == null || data.size() == 0) {
            System.out.println(" ERROR: NO DATA FOUND");
            return;
        }
        //convert to xml
        XStream xstream = new XStream();
        xstream.alias("Level2DataFilterBean", Level2DataFilterBean.class);
        String xml = xstream.toXML(data);
        // write this into file
        File dataFile = new File(CacheGeneratorApp.DATA_FILE);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(dataFile);
            fileWriter.write(xml);
        } catch (IOException exp) {
            System.out.println(" Could not generate xml file " + exp.toString());
            throw exp;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println(" File close error " + e.toString());
                }
            }
        }


    }

}
