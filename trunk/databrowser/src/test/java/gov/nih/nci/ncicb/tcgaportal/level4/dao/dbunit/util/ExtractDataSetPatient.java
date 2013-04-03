package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Description :  Utility class used to extract data from a database (eg Dev Db) in an XML file
 * so that it can be used to load test data in test database used by DBUnit. This
 * particular class takes care of loading data related to patient test cases
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExtractDataSetPatient extends ExtractDataSet {

    static String dbFileName = "TestDB_Patient.xml";

    static String data_set_id = "(10, 14)";
    static String geneList = "('CRABP2','CREB3L1', 'EGFR')";


    static String patientList = "('TCGA-02-0015', 'TCGA-02-0016', 'TCGA-02-0021', 'TCGA-02-0023', 'TCGA-02-0024', " +
                        "'TCGA-02-0025', 'TCGA-02-0026', 'TCGA-02-0027', 'TCGA-02-0028', 'TCGA-02-0033', " +
                        "'TCGA-02-0099', 'TCGA-02-0114', 'TCGA-02-0289')";

    static String SQL_L4_SAMPLES = "Select * from l4_sample where sample_id in ( " +
                "Select l4_sample.sample_id " +
                "FROM L4_sample l4_sample, L4_anomaly_value av, L4_data_set_sample dss, L4_genetic_element ge " +
                "WHERE av.anomaly_data_set_id in " + data_set_id + " " +
                "and av.sample_id=l4_sample.sample_id " +
                "and dss.sample_id=l4_sample.sample_id " +
                "and dss.anomaly_data_set_id=av.anomaly_data_set_id " +
                "and ge.genetic_element_id=av.genetic_element_id " +
                "and ge.genetic_element_id in " +
                    "(select genetic_element_id from l4_genetic_element where genetic_element_name in "+ geneList +") " +
                "and l4_sample.patient_id in " +
                    "(select patient_id from l4_patient where  patient in " +
                    patientList +"))";

/*    static final String SQL_L4_ANOMALY_VALUE = "Select * from l4_anomaly_value where genetic_element_id  " +
            "in (Select genetic_element_id from L4_Genetic_Element where Genetic_element_name in ('EGFR'))" +
            "and anomaly_data_set_id = 11";    */


    static String SQL_L4_ANOMALY_VALUE = "Select av.anomaly_value_id, av.anomaly_value, av.anomaly_data_set_id, " +
                "av.genetic_element_id, av.sample_id " +
                "FROM L4_sample l4_sample, L4_anomaly_value av, L4_data_set_sample dss, L4_genetic_element ge " +
                "WHERE av.anomaly_data_set_id in " + data_set_id + " " +
                "and av.sample_id=l4_sample.sample_id " +
                "and dss.sample_id=l4_sample.sample_id " +
                "and dss.anomaly_data_set_id=av.anomaly_data_set_id " +
                "and ge.genetic_element_id=av.genetic_element_id " +
                "and ge.genetic_element_id in " +
                    "(select genetic_element_id from l4_genetic_element where genetic_element_name in "+ geneList +") " +
                "and l4_sample.patient_id in " +
                    "(select patient_id from l4_patient where  patient in " + patientList + ") ";


    /*static final String SQL_L4_DATA_SET_SAMPLE = "Select * from L4_data_set_sample where anomaly_data_set_id = 11 "+
                "and sample_id in (Select sample_id from L4_Sample where patient in ('TCGA-02-0001', 'TCGA-02-0002', 'TCGA-02-0003'," +
                "'TCGA-02-0004', 'TCGA-02-0006', 'TCGA-02-0007', 'TCGA-02-0009', 'TCGA-02-0010', 'TCGA-02-0011', 'TCGA-02-0014'," +
                "'TCGA-02-0015', 'TCGA-02-0016', 'TCGA-02-0021', 'TCGA-02-0023', 'TCGA-02-0024', 'TCGA-02-0025', 'TCGA-02-0026',"+
                "'TCGA-02-0027', 'TCGA-02-0028', 'TCGA-02-0033', 'TCGA-02-0099', 'TCGA-02-0114', 'TCGA-02-0289'))";*/

    static String SQL_L4_DATA_SET_SAMPLE = "Select distinct(dss.L4_DATA_SET_SAMPLE_ID), dss.ANOMALY_DATA_SET_ID, " +
                "dss.SAMPLE_ID, dss.IS_PAIRED " +
                "FROM L4_sample l4_sample, L4_anomaly_value av, L4_data_set_sample dss, L4_genetic_element ge " +
                "WHERE av.anomaly_data_set_id in " + data_set_id + " " +
                "and av.sample_id=l4_sample.sample_id " +
                "and dss.sample_id=l4_sample.sample_id " +
                "and dss.anomaly_data_set_id=av.anomaly_data_set_id " +
                "and ge.genetic_element_id=av.genetic_element_id " +
                "and ge.genetic_element_id in " +
                    "(select genetic_element_id from l4_genetic_element where genetic_element_name in "+ geneList +") " +
                "and l4_sample.patient_id in " +
                    "(select patient_id from l4_patient where  patient in " + patientList + ") ";


    public static void main( final String[] args ) throws Exception {
        // database connection
        Class.forName( "oracle.jdbc.driver.OracleDriver" );
        final Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:oracle:thin:@cbiodb540.nci.nih.gov:1521:TCGADEV", "tcgagbm", "tcga2874gbm" );
                //"jdbc:oracle:thin:@cbiodb530.nci.nih.gov:1521:TCGAQA", "tcgaunittest", "tcga123unit" );
        final IDatabaseConnection connection = new DatabaseConnection( jdbcConnection );

        QueryDataSet ds = new QueryDataSet( connection );

        addCommonTables(ds);

        ds.addTable( "L4_GENETIC_ELEMENT", SQL_L4_GENETIC_ELEMENT);
        ds.addTable( "L4_DATA_SET_GENETIC_ELEMENT", SQL_L4_DATA_SET_GENETIC_ELEMENT);

        ds.addTable("L4_PATIENT ", SQL_L4_PATIENT );

        ds.addTable( "L4_SAMPLE", SQL_L4_SAMPLES);
        ds.addTable( "L4_ANOMALY_VALUE", SQL_L4_ANOMALY_VALUE);
        ds.addTable( "L4_DATA_SET_SAMPLE", SQL_L4_DATA_SET_SAMPLE);

        System.out.println( "writing to " + portalFolder + dbFileName );

        FlatXmlDataSet.write( ds, new FileOutputStream( portalFolder + dbFileName ) );
    }

}
