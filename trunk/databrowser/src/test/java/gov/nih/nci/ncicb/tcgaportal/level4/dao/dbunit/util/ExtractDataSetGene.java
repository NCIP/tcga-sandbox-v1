package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Description :  Class used to extract data set for test cases related to gene
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExtractDataSetGene extends ExtractDataSet {

    static String dbFileName = "TestDB_Gene.xml";
    
    static String patientList = "('TCGA-02-0015', 'TCGA-02-0016', 'TCGA-02-0021', 'TCGA-02-0023', 'TCGA-02-0024', " +
                        "'TCGA-02-0025', 'TCGA-02-0026', 'TCGA-02-0027', 'TCGA-02-0028', 'TCGA-02-0033', " +
                        "'TCGA-02-0099', 'TCGA-02-0114', 'TCGA-02-0289')";

    static final String SQL_L4_SAMPLE   = "SELECT * from L4_Sample sample_outer where sample_outer.sample_id in ( "+
            "Select s.sample_id FROM L4_sample s, L4_anomaly_value av "+
            "WHERE av.anomaly_data_set_id=10 "+
            "and av.sample_id=s.sample_id "+
            "and av.genetic_element_id in ( "+
            "select genetic_element_id from l4_genetic_element WHERE genetic_element_name in ('CRABP2','CREB3L1', 'EGFR')) "+
            "and s.patient_id in " +
                    "(select patient_id from l4_patient where  patient in " +  patientList + " ) "+
            ")";

    static final String SQL_L4_ANOMALY_VALUE= "SELECT av.anomaly_data_set_id, av.anomaly_value, av.anomaly_value_id, av.genetic_element_id, av.sample_id " +
            "FROM L4_sample s, L4_anomaly_value av " +
            "WHERE av.anomaly_data_set_id=10 " +
            "and av.sample_id=s.sample_id " +
            "and av.genetic_element_id in ( " +
            "select genetic_element_id from l4_genetic_element WHERE genetic_element_name in ('CRABP2','CREB3L1', 'EGFR') " +
            "and s.patient_id in " +
                    "(select patient_id from l4_patient where  patient in " +  patientList + " ) "+
            ")";

    

    public static void main(final String[] args) throws Exception {
        // database connection
        Class.forName("oracle.jdbc.driver.OracleDriver");
        final Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:oracle:thin:@cbiodb540.nci.nih.gov:1521:TCGADEV", "tcgaportaldev", "tcga029dev");
        //"jdbc:oracle:thin:@cbiodb530.nci.nih.gov:1521:TCGAQA", "tcgaunittest", "tcga123unit" );
        final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        // partial database export
        QueryDataSet ds = new QueryDataSet(connection);

        addCommonTables(ds);

        ds.addTable("L4_GENETIC_ELEMENT", SQL_L4_GENETIC_ELEMENT);
        ds.addTable("L4_DATA_SET_GENETIC_ELEMENT", SQL_L4_DATA_SET_GENETIC_ELEMENT);

        ds.addTable("L4_PATIENT ", SQL_L4_PATIENT );
        ds.addTable("L4_SAMPLE", SQL_L4_SAMPLE);        
        ds.addTable("L4_ANOMALY_VALUE", SQL_L4_ANOMALY_VALUE);



        System.out.println("writing to " + portalFolder + dbFileName);

        FlatXmlDataSet.write(ds, new FileOutputStream(portalFolder + dbFileName));
    }

}
