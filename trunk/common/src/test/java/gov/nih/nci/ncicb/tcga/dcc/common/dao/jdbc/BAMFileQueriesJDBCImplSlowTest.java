package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlFileRef;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResult;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class for BAMFileQueries
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMFileQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() +
                    File.separator;
    private static final String TEST_DATA_FILE = "dao/BAMFileData.xml";
    private final String selectBam = "select bam_file_name, disease_abbreviation, domain_name, bam_file_size, " +
            "date_received, general_datatype, analyte_code, uuid, bam_datatype from bam_file bf, " +
            "shipped_biospecimen sb, bam_file_datatype bfd, shipped_biospecimen_bamfile sbb, center c, disease d " +
            "where bf.bam_datatype_id = bfd.bam_datatype_id " +
            "and bf.disease_id = d.disease_id " +
            "and bf.center_id = c.center_id " +
            "and bf.bam_file_id = sbb.bam_file_id " +
            "and sbb.shipped_biospecimen_id = sb.shipped_biospecimen_id " +
            "order by bf.bam_file_id";
    private final ParameterizedRowMapper<BamTelemetry> bamTelemetryRowMapper = new ParameterizedRowMapper<BamTelemetry>() {
        public BamTelemetry mapRow(ResultSet resultSet, int i) throws SQLException {
            final BamTelemetry bam = new BamTelemetry();
            bam.setBamFile(resultSet.getString(1));
            bam.setDisease(resultSet.getString(2));
            bam.setCenter(resultSet.getString(3));
            bam.setFileSize(resultSet.getLong(4));
            bam.setDateReceived(resultSet.getDate(5));
            bam.setDataType(resultSet.getString(6));
            bam.setAnalyteCode(resultSet.getString(7));
            bam.setAliquotUUID(resultSet.getString(8));
            bam.setLibraryStrategy(resultSet.getString(9));
            return bam;
        }
    };
    private BAMFileQueriesJDBCImpl queries;
    private JdbcTemplate jdbcTemplate;

    public BAMFileQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new BAMFileQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
        queries.initBamLookupQueries();
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    public void testGetLatestUploadedDate() throws Exception {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        assertEquals(simpleDateFormat.parse("2013-04-03 16:44:50").getTime(), queries.getLatestUploadedDate().getTime());
    }

    public void testStore() throws Exception {
        queries.store(makeBamXmlResultSet());
        final List<BamTelemetry> resList = jdbcTemplate.query(selectBam, bamTelemetryRowMapper);
        assertNotNull(resList);
        assertEquals(5, resList.size());
        assertEquals("UNCID_1109493.c11e3246-c33d-47ef-b4b4-be287b92d91e.sorted_genome_alignments.bam",
                resList.get(0).getBamFile());
        assertEquals(new Long(4058557710L), resList.get(0).getFileSize());
        assertEquals("myFunnyBam", resList.get(2).getBamFile());
        assertEquals("uuid2", resList.get(2).getAliquotUUID());
        assertEquals("OV", resList.get(4).getDisease());
        assertEquals("WGS", resList.get(4).getLibraryStrategy());
        assertEquals("uuid1", resList.get(3).getAliquotUUID());
        assertEquals("R", resList.get(2).getAnalyteCode());
        assertEquals("Exome", resList.get(1).getDataType());
    }

    public void testStoreWithNonLive() throws Exception {
        queries.store(makeBamXmlResultSetWithNonLive());
        final List<BamTelemetry> resList = jdbcTemplate.query(selectBam, bamTelemetryRowMapper);
        assertNotNull(resList);
        assertEquals(4, resList.size());
        assertEquals("UNCID_1109493.c11e3246-c33d-47ef-b4b4-be287b92d91e.sorted_genome_alignments.bam",
                resList.get(0).getBamFile());
        assertEquals(new Long(4058557710L), resList.get(0).getFileSize());
        assertEquals("OV", resList.get(3).getDisease());
        assertEquals("WGS", resList.get(3).getLibraryStrategy());
        assertEquals("uuid1", resList.get(2).getAliquotUUID());
        assertEquals("D", resList.get(1).getAnalyteCode());
        assertEquals("Unknown", resList.get(0).getDataType());
    }

    public void testGetCGHubCenter() throws Exception {
        assertEquals(new Long(5), queries.getDCCCenterId("IGC"));
        assertEquals(new Long(0), queries.getDCCCenterId("nocenter"));
    }

    public void testGetAliquotId() throws Exception {
        assertEquals(new Long(4), queries.getAliquotId("uuid1"));
        assertEquals(new Long(0), queries.getAliquotId("noaliquot"));
    }

    public void testGetBamdatatypeId() throws Exception {
        assertEquals(new Long(2), queries.getDatatypeBAMId("WGS"));
        assertEquals(new Long(4), queries.getDatatypeBAMId("notype"));
    }

    private BamXmlResultSet makeBamXmlResultSet() {
        BamXmlResultSet bamXmlResultSet = new BamXmlResultSet();
        bamXmlResultSet.setFetchDate(new Date());
        List<BamXmlResult> bamXmlResultList = new LinkedList<BamXmlResult>();
        bamXmlResultList.add(makeBamXmlResult("4", "1", "D", "uuid1", "1"));
        bamXmlResultList.add(makeBamXmlResult("5", "2", "R", "uuid2", "1"));
        bamXmlResultList.add(makeBamXmlResult("6", "2", "R", "uuid3", "1"));
        bamXmlResultSet.setBamXmlResultList(bamXmlResultList);
        return bamXmlResultSet;
    }

    private BamXmlResultSet makeBamXmlResultSetWithNonLive() {
        BamXmlResultSet bamXmlResultSet = new BamXmlResultSet();
        bamXmlResultSet.setFetchDate(new Date());
        List<BamXmlResult> bamXmlResultList = new LinkedList<BamXmlResult>();
        bamXmlResultList.add(makeBamXmlResult("4", "1", "D", "uuid1", "1"));
        final BamXmlResult nonLive = makeBamXmlResult("5", "2", "R", "uuid2", "1");
        nonLive.setState("field");
        bamXmlResultList.add(nonLive);
        bamXmlResultList.add(makeBamXmlResult("6", "2", "R", "uuid3", "1"));
        bamXmlResultSet.setBamXmlResultList(bamXmlResultList);
        return bamXmlResultSet;
    }

    private BamXmlResult makeBamXmlResult(String id, String disease, String analyte, String uuid, String center) {
        BamXmlResult bam = new BamXmlResult();
        bam.setState("live");
        bam.setAnalysisId(id);
        bam.setDisease(disease);
        bam.setAnalyteCode(analyte);
        bam.setAliquotUUID(uuid);
        bam.setCenter(center);
        bam.setLibraryStrategy("WGS");
        bam.setDateReceived(new Date());
        bam.setBamXmlFileRefList(new LinkedList<BamXmlFileRef>() {{
            add(new BamXmlFileRef() {{
                setFileName("myFunnyBam");
                setFileSize("123456789");
            }});
        }});
        return bam;
    }

}//End of Class
