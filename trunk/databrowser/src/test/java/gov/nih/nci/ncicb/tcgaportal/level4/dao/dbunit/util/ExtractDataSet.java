package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.QueryDataSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description :  Utility class used to extract data from a database (eg Dev Db) in an XML file
 * so that it can be used to load test data in test database used by DBUnit. This
 * particualr class takes care of loading common tables. We have different classes for loading
 * specific data for Genes, Patiens, Pathways etc
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExtractDataSet {

    static final String portalFolder = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    static final String SQL_CENTER = "Select center_id, domain_name from Center";
    static final String SQL_PLATFORM = "Select platform_id, name from Platform order by platform_id";
    static final String SQL_PLATFORM_TYPE = "Select platform_id, data_type_id, platform_type_id from Platform_Type order by platform_id";
    static final String SQL_DISEASE = "Select disease_id, disease_abbreviation, name, active, dam_default, workbench_track from Disease";
    static final String SQL_EXPERIMENT = "select * from experiment";
    static final String SQL_DATA_SET = "Select * from Data_Set";
    static final String SQL_DATA_TYPE = "Select data_type_id, name from Data_Type";

    static final String SQL_L4_ANOMALY_DATA_SET = "Select * from L4_Anomaly_Data_Set";
    static final String SQL_L4_DATA_SET_GENETIC_ELEMENT = "select * from L4_data_set_genetic_element " +
            "where anomaly_data_set_id in (10, 14) and " +
            "genetic_element_id in (" +
            "select genetic_element_id from l4_genetic_element " +
            "where genetic_element_name in ('CRABP2','CREB3L1','EGFR'))";

    static final String SQL_L4_ANOMALY_DATA_VERSION = "Select * from L4_Anomaly_Data_Version";
    static final String SQL_L4_ANOMALY_DATA_SET_VERSION = "Select * from L4_Anomaly_Data_Set_Version";

    static final String SQL_L4_CORRELATION_TYPE = "Select * from L4_Correlation_Type";
    static final String SQL_L4_ANOMALY_TYPE = "Select * from L4_Anomaly_Type";

    static final String SQL_L4_GENETIC_ELEMENT_TYPE = "Select * from l4_Genetic_element_Type ";

    static final String SQL_L4_GENETIC_ELEMENT = "Select * from l4_Genetic_element " +
            "where genetic_element_name in ('CRABP2','CREB3L1','EGFR') ";

    static final String SQL_L4_PATIENT = "Select * from L4_patient where patient in ('TCGA-02-0015', 'TCGA-02-0016'," +
            "'TCGA-02-0021', 'TCGA-02-0023', 'TCGA-02-0024', 'TCGA-02-0025', 'TCGA-02-0026'," +
            "'TCGA-02-0027', 'TCGA-02-0028', 'TCGA-02-0033', 'TCGA-02-0099', 'TCGA-02-0114', 'TCGA-02-0289')";


    public static void addCommonTables(QueryDataSet ds) throws AmbiguousTableNameException {

        ds.addTable("CENTER", SQL_CENTER);
        ds.addTable("DATA_TYPE", SQL_DATA_TYPE);

        ds.addTable("PLATFORM", SQL_PLATFORM);

        ds.addTable("DISEASE", SQL_DISEASE);

        ds.addTable("EXPERIMENT", SQL_EXPERIMENT);
        ds.addTable("DATA_SET", SQL_DATA_SET);

        ds.addTable("L4_GENETIC_ELEMENT_TYPE", SQL_L4_GENETIC_ELEMENT_TYPE);
        ds.addTable("L4_ANOMALY_TYPE", SQL_L4_ANOMALY_TYPE);
        ds.addTable("L4_ANOMALY_DATA_SET", SQL_L4_ANOMALY_DATA_SET);
        ds.addTable("L4_ANOMALY_DATA_VERSION", SQL_L4_ANOMALY_DATA_VERSION);
        ds.addTable("L4_ANOMALY_DATA_SET_VERSION", SQL_L4_ANOMALY_DATA_SET_VERSION);

        ds.addTable("L4_CORRELATION_TYPE", SQL_L4_CORRELATION_TYPE);
    }

    public static List<String> getCommonTables() {
        List<String> list = new ArrayList<String>();
        list.add("L4_CORRELATION_TYPE");
        list.add("L4_ANOMALY_DATA_SET_VERSION");
        list.add("L4_ANOMALY_DATA_VERSION");
        list.add("L4_ANOMALY_DATA_SET");
        list.add("L4_ANOMALY_TYPE");
        list.add("DATA_SET");
        list.add("EXPERIMENT");
        list.add("CENTER_DISEASE");
        list.add("DISEASE");
        list.add("CENTER_PLATFORM");
        list.add("PLATFORM");
        list.add("DATA_TYPE");
        list.add("CENTER");

        return list;
    }

}
