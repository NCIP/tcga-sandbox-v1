package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Fast test for ArchiveListerQueriesJDBCImpl
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListerQueriesJDBCImplFastTest {
    private ArchiveListerQueriesJDBCImpl archiveListerQueries;
    private List<Object> bindParams;

    @Before
    public void setUp() throws Exception {
        archiveListerQueries = new ArchiveListerQueriesJDBCImpl();
        bindParams = new ArrayList<Object>();
    }

    @Test
    public void testBuildGetCollectionsQueryWithAccess() {
        assertEquals("select collection_name, max(f2c.file_date) as latest_date from file_collection fc, file_to_collection f2c, visibility v " +
                "where fc.file_collection_id=f2c.file_collection_id and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and fc.disease_id is null " +
                "and fc.center_type_code is null " +
                "and fc.center_id is null " +
                "and fc.platform_id is null " +
                "group by collection_name order by collection_name",
                archiveListerQueries.buildCollectionLinksQuery(0, null, null, null, null, bindParams));
        assertEquals(1, bindParams.size());
        assertEquals(0, bindParams.get(0));
    }

    @Test
    public void testBuildCollectionsQueryWithDisease() {
        assertEquals("select collection_name, max(f2c.file_date) as latest_date from file_collection fc, file_to_collection f2c, visibility v, disease d " +
                "where fc.file_collection_id=f2c.file_collection_id and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? " +
                "and fc.center_type_code is null " +
                "and fc.center_id is null " +
                "and fc.platform_id is null " +
                "group by collection_name order by collection_name",
                archiveListerQueries.buildCollectionLinksQuery(1, "gbm", null, null, null, bindParams));

        assertEquals(2, bindParams.size());
        assertEquals(1, bindParams.get(0));
        assertEquals("gbm", bindParams.get(1));
    }

    @Test
    public void testBuildCollectionsQueryWithCenterType() {
        assertEquals("select collection_name, max(f2c.file_date) as latest_date from file_collection fc, file_to_collection f2c, visibility v, disease d " +
                "where fc.file_collection_id=f2c.file_collection_id and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? " +
                "and fc.center_type_code=? " +
                "and fc.center_id is null " +
                "and fc.platform_id is null " +
                "group by collection_name order by collection_name",
                archiveListerQueries.buildCollectionLinksQuery(0, "ucec", "bcr", null, null, bindParams));
        assertEquals(3, bindParams.size());
        assertEquals(Arrays.asList((Object) 0, "ucec", "bcr"), bindParams);
    }

    @Test
    public void testBuildCollectionsQueryWithCenter() {
        assertEquals("select collection_name, max(f2c.file_date) as latest_date from file_collection fc, file_to_collection f2c, visibility v, disease d, center c " +
                "where fc.file_collection_id=f2c.file_collection_id and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? " +
                "and fc.center_type_code=? and c.center_id=fc.center_id and c.domain_name=? " +
                "and fc.platform_id is null " +
                "group by collection_name order by collection_name",
                archiveListerQueries.buildCollectionLinksQuery(1, "laml", "cgcc", "broad.mit.edu", null, bindParams));
        assertEquals(4, bindParams.size());
        assertEquals(Arrays.asList((Object)1, "laml", "cgcc", "broad.mit.edu"), bindParams);
    }

    @Test
    public void testBuildCollectionsQueryWithPlatform() {
        assertEquals("select collection_name, max(f2c.file_date) as latest_date from file_collection fc, file_to_collection f2c, visibility v, disease d, center c, platform p " +
                "where fc.file_collection_id=f2c.file_collection_id and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? " +
                "and fc.center_type_code=? " +
                "and c.center_id=fc.center_id and c.domain_name=? " +
                "and p.platform_id=fc.platform_id and p.platform_name=? " +
                "group by collection_name order by collection_name",
                archiveListerQueries.buildCollectionLinksQuery(0, "kirc", "bcr", "nationwide", "bio", bindParams));
        assertEquals(5, bindParams.size());
        assertEquals(Arrays.asList((Object)0, "kirc", "bcr", "nationwide", "bio"), bindParams);
    }

    @Test
    public void testBuildCollectionFileQueryWithAccess() throws ArchiveListerQueries.ArchiveListerException {
        assertEquals("select file_name, file_location_url, file_size, file_date " +
                "from file_collection fc, file_to_collection f2c, file_info f, visibility v " +
                "where fc.collection_name=? and fc.file_collection_id=f2c.file_collection_id and f.file_id=f2c.file_id " +
                "and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and fc.disease_id is null and fc.center_type_code is null and fc.center_id is null and fc.platform_id is null order by file_name",
                archiveListerQueries.buildCollectionFileLinksQuery("aCollection", 1, null, null, null, null, bindParams));
        assertEquals(2, bindParams.size());
        assertEquals(Arrays.asList((Object)"aCollection", 1), bindParams);
    }

    @Test
    public void testBuildFileCollectionQueryWithDisease() throws ArchiveListerQueries.ArchiveListerException {
        assertEquals("select file_name, file_location_url, file_size, file_date " +
                "from file_collection fc, file_to_collection f2c, file_info f, visibility v, disease d " +
                "where fc.collection_name=? and fc.file_collection_id=f2c.file_collection_id and f.file_id=f2c.file_id " +
                "and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? and fc.center_type_code is null and fc.center_id is null and fc.platform_id is null order by file_name",
                archiveListerQueries.buildCollectionFileLinksQuery("aCollection", 1, "aDisease", null, null, null, bindParams));
        assertEquals(3, bindParams.size());
        assertEquals(Arrays.asList((Object)"aCollection", 1, "aDisease"), bindParams);
    }

    @Test
    public void testBuildFileCollectionQueryWithCenterType() throws ArchiveListerQueries.ArchiveListerException {
        assertEquals("select file_name, file_location_url, file_size, file_date " +
                "from file_collection fc, file_to_collection f2c, file_info f, visibility v, disease d " +
                "where fc.collection_name=? and fc.file_collection_id=f2c.file_collection_id and f.file_id=f2c.file_id " +
                "and v.visibility_id=fc.visibility_id and v.identifiable=? " +
                "and d.disease_id=fc.disease_id and d.disease_abbreviation=? " +
                "and fc.center_type_code=? and fc.center_id is null and fc.platform_id is null " +
                "order by file_name",
                archiveListerQueries.buildCollectionFileLinksQuery("stuff", 0, "gbm", "tenacious", null, null, bindParams ));
        assertEquals(4, bindParams.size());
        assertEquals(Arrays.asList((Object)"stuff", 0, "gbm", "tenacious"), bindParams);
    }

}
