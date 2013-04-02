/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBUnit test for ClinicalLoaderQueries.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalLoaderQueriesDBUnitSlowTest extends DBUnitTestCase {
	private ClinicalObject patient;
	private ClinicalLoaderQueriesJDBCImpl clinicalLoaderQueries;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private List<String> newElements = new ArrayList<String>();
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	public ClinicalLoaderQueriesDBUnitSlowTest() {
		super(SAMPLES_DIR, "qclive/dao/ClinicalLoaderQueries_testData.xml",
				"oracle.unittest.properties");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		clinicalLoaderQueries = new ClinicalLoaderQueriesJDBCImpl();
		clinicalLoaderQueries.setDataSource(getDataSource());
		patient = new ClinicalObject();
		final ClinicalTable clinicalTable = new ClinicalTable();
		clinicalTable.setTableName("PATIENT");
		clinicalTable.setIdColumnName("PATIENT_ID");
		clinicalTable.setBarcodeColumName("PATIENT_BARCODE");
		clinicalTable.setArchiveLinkTableName("PATIENT_ARCHIVE");
		patient.setClinicalTable(clinicalTable);
		patient.setBarcode("TCGA-00-0001");
		patient.setUuid("testing-testing-123");

		simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
	}

	@Override
	public void tearDown() throws Exception {
		simpleJdbcTemplate.update("delete from sample_archive");
		simpleJdbcTemplate.update("delete from sample_element");
		simpleJdbcTemplate.update("delete from sample");
		simpleJdbcTemplate.update("delete from patient_archive");
		simpleJdbcTemplate.update("delete from patient_element");
        simpleJdbcTemplate.update("delete from follow_up");
		simpleJdbcTemplate.update("delete from patient");
		super.tearDown();
	}

	public void testGetAllClinicalTables() {

        final List<ClinicalTable> tables = clinicalLoaderQueries.getAllClinicalTables();
        assertNotNull(tables);
		assertEquals(4, tables.size());

        final ClinicalTable followUpTable = tables.get(0);
        final ClinicalTable patientTable = tables.get(1);
		final ClinicalTable sampleTable = tables.get(2);
		final ClinicalTable tumorPathTable = tables.get(3);

        assertNotNull(patientTable);
        assertNotNull(sampleTable);
        assertNotNull(tumorPathTable);
        assertNotNull(followUpTable);

		assertEquals("bcr_patient_uuid", patientTable.getUuidElementName());
		assertEquals("bcr_sample_uuid", sampleTable.getUuidElementName());
		assertNull(tumorPathTable.getUuidElementName());
        assertNull(followUpTable.getUuidElementName());

		assertEquals("patient", patientTable.getElementNodeName());
		assertEquals("sample", sampleTable.getElementNodeName());
		assertEquals("tumor_pathology", tumorPathTable.getElementNodeName());

        assertFalse(patientTable.isDynamic());
        assertFalse(sampleTable.isDynamic());
        assertFalse(tumorPathTable.isDynamic());
        assertTrue(followUpTable.isDynamic());

        assertNull(patientTable.getDynamicIdentifierColumnName());
        assertEquals("lion", sampleTable.getDynamicIdentifierColumnName());
        assertNull(tumorPathTable.getDynamicIdentifierColumnName());
        assertEquals("follow_up_version", followUpTable.getDynamicIdentifierColumnName());
        
        assertEquals(new Long(1), patientTable.getClinicalTableId());
        assertEquals(new Long(2), sampleTable.getClinicalTableId());
        assertEquals(new Long(3), tumorPathTable.getClinicalTableId());

        assertNull(patientTable.getParentTableId());
        assertEquals(new Long(1), sampleTable.getParentTableId());
        assertEquals(new Long(2), tumorPathTable.getParentTableId());
	}

	public void testGetClinicalTableForName() {
		final ClinicalTable retrievedTable = clinicalLoaderQueries
				.getClinicalTableForElementName("patient", null);
		assertNotNull(retrievedTable);
		assertEquals("bcr_patient_barcode",
				retrievedTable.getBarcodeElementName());
		assertEquals("bcr_patient_uuid", retrievedTable.getUuidElementName());
		assertEquals("PATIENT", retrievedTable.getTableName());
		assertEquals("PATIENT_ELEMENT", retrievedTable.getElementTableName());
		assertEquals("PATIENT_ARCHIVE",
				retrievedTable.getArchiveLinkTableName());
		assertEquals("patient", retrievedTable.getElementNodeName());
	}

	public void testGetClinicalTableForNameNoBarcode() {
		final ClinicalTable tumorPathTable = clinicalLoaderQueries
				.getClinicalTableForElementName("tumor_pathology", 2L);
		assertNotNull(tumorPathTable);
		assertEquals("TUMORPATHOLOGY", tumorPathTable.getTableName());
		// this table has no barcode column
		assertNull(tumorPathTable.getBarcodeColumName());
		assertNull(tumorPathTable.getUuidElementName());
		assertEquals("tumor_pathology", tumorPathTable.getElementNodeName());
	}

    public void testGetNonExistentClinicalTable() {

        final ClinicalTable retrievedTable = clinicalLoaderQueries.getClinicalTableForElementName("squirrel", null);
        assertNull(retrievedTable);
    }

    public void testGetClinicalTableForElementNameWrongParentId() {

        final ClinicalTable clinicalTable = clinicalLoaderQueries.getClinicalTableForElementName("sample", 2L);
        assertNull(clinicalTable);
    }

    public void testGetClinicalTableForElementNameWrongParentIdWhenNoParent() {

        final ClinicalTable clinicalTable = clinicalLoaderQueries.getClinicalTableForElementName("patient", 2L);
        assertNull(clinicalTable);
    }

	public void testGetId() {
		final long patientId = clinicalLoaderQueries.getId(patient);
		assertEquals(12345L, patientId);
	}

	public void testGetIdNoTableName() {
		patient.getClinicalTable().setTableName(null);
		boolean exceptionThrown = false;
		try {
			clinicalLoaderQueries.getId(patient);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public void testGetIdNotFound() {
		patient.setBarcode("squirrel");
		final long id = clinicalLoaderQueries.getId(patient);
		assertEquals(-1L, id);
	}

	public void testGetParentTable() {
		final ClinicalTable sampleTable = new ClinicalTable();
		sampleTable.setTableName("SAMPLE");
		final ClinicalTable parentTable = clinicalLoaderQueries
				.getParentTable(sampleTable);
		assertEquals("PATIENT", parentTable.getTableName());
		assertEquals("PATIENT_ID", parentTable.getIdColumnName());
	}

	public void testInsertPatientNoElements() {
		final long newId = clinicalLoaderQueries.insert(patient, -1L, 123,
				newElements);
		assertTrue(newId > 0);
		final Map<String, Object> results = simpleJdbcTemplate.queryForMap(
                "select uuid, patient_barcode from patient where patient_id=?",
                newId);
		assertEquals("TCGA-00-0001", results.get("PATIENT_BARCODE"));
		assertEquals("testing-testing-123", results.get("UUID"));

		// now check that link to archive was added
		final int linkCount = simpleJdbcTemplate
				.queryForInt(
						"select count(*) from patient_archive where archive_id=123 and patient_id=?",
						newId);
		assertEquals(1, linkCount);
	}

	public void testInsertSampleNoElements() {
		final ClinicalObject sample = new ClinicalObject();
		sample.setBarcode("sample-barcode");
		sample.setUuid("hi-I-am-a-shiny-new-uuid");
		final ClinicalTable sampleTable = clinicalLoaderQueries
				.getClinicalTableForElementName("sample", 1L);
		sample.setClinicalTable(sampleTable);
		final long sampleId = clinicalLoaderQueries.insert(sample, 12345L, 123,
				newElements);
		final Map<String, Object> results = simpleJdbcTemplate.queryForMap(
				"select uuid, sample_barcode, patient_id "
						+ "from sample where sample_id=?", sampleId);
		assertEquals("sample-barcode", results.get("SAMPLE_BARCODE"));
		assertEquals("12345", results.get("PATIENT_ID").toString());
		assertEquals("hi-I-am-a-shiny-new-uuid", results.get("UUID").toString());

		final int linkCount = simpleJdbcTemplate
				.queryForInt(
						"select count(*) from sample_archive where archive_id=123 and sample_id=?",
						sampleId);
		assertEquals(1, linkCount);
	}

	public void testInsertSampleWithElements() {
		try {
			final ClinicalObject sample = new ClinicalObject();
			sample.setBarcode("abc");
			sample.setUuid("take-me-to-your-leader");
			sample.addAttribute("weight", "almost nothing");
			sample.addAttribute("sample_type", "test");
			final ClinicalTable sampleTable = clinicalLoaderQueries
					.getClinicalTableForElementName("sample", 1L);
			sample.setClinicalTable(sampleTable);
			final long sampleId = clinicalLoaderQueries.insert(sample, 12345L,
					123, newElements);
			final List<Map<String, Object>> elementResults = simpleJdbcTemplate
					.queryForList(
							"select clinical_xsd_element_id, element_value "
									+ "from sample_element where sample_id=? order by clinical_xsd_element_id",
							sampleId);
			assertEquals(2, elementResults.size());
			assertEquals("3",
					elementResults.get(0).get("CLINICAL_XSD_ELEMENT_ID")
							.toString());
			assertEquals("almost nothing",
					elementResults.get(0).get("ELEMENT_VALUE"));
			assertEquals("4",
					elementResults.get(1).get("CLINICAL_XSD_ELEMENT_ID")
							.toString());
			assertEquals("test", elementResults.get(1).get("ELEMENT_VALUE"));
		} finally {
			simpleJdbcTemplate.update("delete from sample_archive");
			simpleJdbcTemplate.update("delete from sample");
			simpleJdbcTemplate.update("delete from sample_element");
		}
	}

	public void testInsertSampleWithProtectedElements() {
		try {
			final ClinicalObject sample = new ClinicalObject();
			sample.setBarcode("abc");
			sample.setUuid("take-me-to-your-leader");
			sample.addAttribute("weight", "almost nothing");
			sample.addAttribute("bcr_not_expected_element", "some");
			final ClinicalTable sampleTable = clinicalLoaderQueries
					.getClinicalTableForElementName("sample", 1L);
			sample.setClinicalTable(sampleTable);
			final long sampleId = clinicalLoaderQueries.insert(sample, 12345L,
					123, newElements);
			final List<Map<String, Object>> elementResults = simpleJdbcTemplate
					.queryForList(
							"select clinical_xsd_element_id, element_value "
									+ "from sample_element where sample_id=? order by clinical_xsd_element_id",
							sampleId);
			assertEquals(1, elementResults.size());
			assertEquals("3",
					elementResults.get(0).get("CLINICAL_XSD_ELEMENT_ID")
							.toString());
			assertEquals("almost nothing",
					elementResults.get(0).get("ELEMENT_VALUE"));
		} finally {
			simpleJdbcTemplate.update("delete from sample_archive");
			simpleJdbcTemplate.update("delete from sample");
			simpleJdbcTemplate.update("delete from sample_element");
		}
	}

    public void testInsertDynamicObject() {

        final String deleteFromFollowUpQuery = "delete from follow_up";
        final String followUpCountQuery = "select count(*) from follow_up";
        final String selectFollowUpByIdQuery = "select follow_up_id, patient_id, follow_up_version from follow_up where follow_up_id=?";

        try {
            final ClinicalTable clinicalTable = new ClinicalTable();
            clinicalTable.setDynamic(true);
            clinicalTable.setDynamicIdentifierColumnName("follow_up_version");
            clinicalTable.setTableName("FOLLOW_UP");
            clinicalTable.setIdColumnName("follow_up_id");

            final String dynamicIdentifier = "follow_up_v2.9";

            final ClinicalObject clinicalObject = new ClinicalObject();
            clinicalObject.setDynamicIdentifier(dynamicIdentifier);
            clinicalObject.setClinicalTable(clinicalTable);

            getSimpleJdbcTemplate().update(deleteFromFollowUpQuery);

            final int followUpCountBeforeCall = getSimpleJdbcTemplate().queryForInt(followUpCountQuery);
            assertEquals(0, followUpCountBeforeCall);

            final long parentId = 12345;
            final long archiveId = 123;
            final long clinicalObjectId = clinicalLoaderQueries.insert(clinicalObject, parentId, archiveId, newElements);

            assertTrue(clinicalObjectId > 0);
            
            final int followUpCountAfterCall = getSimpleJdbcTemplate().queryForInt(followUpCountQuery);
            assertEquals(1, followUpCountAfterCall);

            final List<Map<String, Object>> result = getSimpleJdbcTemplate().queryForList(selectFollowUpByIdQuery,clinicalObjectId);
            assertNotNull(result);
            assertEquals(1, result.size());

            final Map<String, Object> resultMap = result.get(0);
            assertNotNull(resultMap);
            assertEquals(dynamicIdentifier, resultMap.get("follow_up_version"));
            assertEquals(""+parentId, resultMap.get("patient_id").toString());
            assertEquals(""+clinicalObjectId, resultMap.get("follow_up_id").toString());

        } finally {
            getSimpleJdbcTemplate().update(deleteFromFollowUpQuery);
        }
    }

    public void testGetPatientForBarcode() {
        final ClinicalObject patient = clinicalLoaderQueries
                .getClinicalObjectForBarcode("patient", "TCGA-00-0001", null);
        assertNotNull(patient);
        assertEquals("PATIENT", patient.getClinicalTable().getTableName());
    }

    public void testGetClinicalObjectForBarcodeWrongPatientId() {

        final ClinicalObject patient = clinicalLoaderQueries.getClinicalObjectForBarcode("sample", "TCGA-00-0001-00A", 2L);
        assertNull(patient);
    }

    public void testGetClinicalObjectForBarcodeWrongPatientIdWhenNoParent() {

        final ClinicalObject patient = clinicalLoaderQueries.getClinicalObjectForBarcode("patient", "TCGA-00-0001", 1L);
        assertNull(patient);
    }

	public void testGetObjectForBarcodeWithAttributes() {
		final ClinicalObject sample = clinicalLoaderQueries
				.getClinicalObjectForBarcode("sample", "TCGA-00-0001-00A", 1L);
		assertNotNull(sample);
		assertEquals("42", sample.getValue("weight"));
		assertEquals("populated_test", sample.getValue("sample_type"));
	}

	public void testUpdatePatientNewElements() {
		// all new elements
		final ClinicalObject onePatient = clinicalLoaderQueries
				.getClinicalObjectForBarcode("patient", "TCGA-00-0001", null);
		onePatient.addAttribute("gender", "female");
		onePatient.addAttribute("age_at_diagnosis", "103");
		clinicalLoaderQueries.update(onePatient, 123, newElements);

		final List<Map<String, Object>> savedAttributes = simpleJdbcTemplate
				.queryForList("select * from patient_element where patient_id=12345 order by clinical_xsd_element_id");
		assertEquals(2, savedAttributes.size());
		assertEquals("5", savedAttributes.get(0).get("CLINICAL_XSD_ELEMENT_ID")
				.toString());
		assertEquals("female", savedAttributes.get(0).get("ELEMENT_VALUE"));
		assertEquals("6", savedAttributes.get(1).get("CLINICAL_XSD_ELEMENT_ID")
				.toString());
		assertEquals("103", savedAttributes.get(1).get("ELEMENT_VALUE"));

		// verify that patient_archive link is there
		final int count = simpleJdbcTemplate
				.queryForInt("select count(*) from patient_archive where patient_id=12345 and archive_id=123");
		assertEquals(1, count);				
	}

	public void testUpdatePatientNotExpectedElements() {
		// all new elements
		final ClinicalObject onePatient = clinicalLoaderQueries
				.getClinicalObjectForBarcode("patient", "TCGA-00-0001", null);
		onePatient.addAttribute("bcr_not_expected_element", "103");
		clinicalLoaderQueries.update(onePatient, 123, newElements);
		final List<Map<String, Object>> savedAttributes = simpleJdbcTemplate
				.queryForList("select * from patient_element where patient_id=12345 order by clinical_xsd_element_id");
		assertEquals(0, savedAttributes.size());
	}

	public void testUpdatePatientNewElement() {
		// all new elements
		final ClinicalObject onePatient = clinicalLoaderQueries
				.getClinicalObjectForBarcode("patient", "TCGA-00-0001", null);
		onePatient.addAttribute("a_new_element", "555");
		clinicalLoaderQueries.update(onePatient, 123, newElements);
		assertEquals(1, newElements.size());
		final List<Map<String, Object>> savedAttributes = simpleJdbcTemplate
				.queryForList("select * from patient_element where patient_id=12345 order by clinical_xsd_element_id");
		assertEquals(1, savedAttributes.size());
		
		// verify that new element is not protected
		final int isElementProtected = 
				simpleJdbcTemplate.queryForInt (" select IS_PROTECTED from CLINICAL_XSD_ELEMENT " +
						"where CLINICAL_XSD_ELEMENT_ID = ?",savedAttributes.get(0).get("CLINICAL_XSD_ELEMENT_ID"));
		assertEquals(isElementProtected,0);
	}

	public void testInsertPatientInvalidArchive() {
		boolean exceptionThrown = false;
		try {
			clinicalLoaderQueries.insert(patient, -1L, 555, newElements);
		} catch (DataIntegrityViolationException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public void testUpdatePatientInvalidElements() {
		// some attributes don't exist in clinical_xsd_element table
		final ClinicalObject existingPatient = clinicalLoaderQueries
				.getClinicalObjectForBarcode("patient", "TCGA-00-0001", null);
		existingPatient.addAttribute("squirrel", "acorn");
		boolean exceptionThrown = false;
		try {
			clinicalLoaderQueries.update(existingPatient, 123, newElements);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		// assertTrue(exceptionThrown);
		assertEquals(1, newElements.size());
	}

	public void testUpdateSample() {

		// update values for some elements, delete others
		final ClinicalObject existingSample = clinicalLoaderQueries
				.getClinicalObjectForBarcode("sample", "TCGA-00-0001-00A", 1L);
		// first assert the values from the db are as expected
		assertEquals(existingSample.getValue("weight"), "42");
		assertEquals(existingSample.getValue("sample_type"), "populated_test");
		assertFalse(existingSample.getAttributeNames().contains(
				"freezing_method"));

		Map<String, String> newAttributes = new HashMap<String, String>();
		newAttributes.put("freezing_method", "lots of ice");
		newAttributes.put("sample_type", "new value");
		// note weight was removed
		existingSample.setAttributes(newAttributes);

		clinicalLoaderQueries.update(existingSample, 123, newElements);
		final List<Map<String, Object>> savedAttributes = simpleJdbcTemplate
				.queryForList("select * from sample_element where sample_id=9876 order by clinical_xsd_element_id");
		// should just be sample type, then freezing method
		assertEquals(2, savedAttributes.size());
		assertEquals("new value", savedAttributes.get(0).get("ELEMENT_VALUE")
				.toString());
		assertEquals("lots of ice", savedAttributes.get(1).get("ELEMENT_VALUE")
				.toString());

		// make sure the other sample in the db didn't have its attributes
		// updated too with these values!
		final ClinicalObject existingSample2 = clinicalLoaderQueries
				.getClinicalObjectForBarcode("sample", "TCGA-00-0002-00A", 1L);
		assertEquals("16", existingSample2.getValue("weight"));
		assertEquals("different_value", existingSample2.getValue("sample_type"));
	}

	public void testElementRepresentsClinicalTable() {
		assertTrue(clinicalLoaderQueries
				.elementRepresentsClinicalTable("patient"));
		assertFalse(clinicalLoaderQueries
				.elementRepresentsClinicalTable("squirrel"));
	}

	public void test() {
		assertTrue(clinicalLoaderQueries
				.clinicalXsdElementExists("age_at_diagnosis"));
		assertFalse(clinicalLoaderQueries.clinicalXsdElementExists("acorn"));
	}

	public void testAddArchiveLink() {
		clinicalLoaderQueries.addArchiveLink(patient, 123);
		final String patientArchiveQuery = "select count(*) from patient_archive where patient_id=12345 and archive_id=123";
		int rows = simpleJdbcTemplate.queryForInt(patientArchiveQuery);
		assertEquals(1, rows);

		// now add again and make sure it's still only there once
		clinicalLoaderQueries.addArchiveLink(patient, 123);
		rows = simpleJdbcTemplate.queryForInt(patientArchiveQuery);
		assertEquals(1, rows);
	}

	public void testInsertXsdElement() {
		String expected = "Y";
		int protect = 1;
		long elementId = clinicalLoaderQueries.insertClinicalXsdElement(
				"a_new_element", protect, "a_new_element", "String", expected);
		assertEquals(elementId,
				clinicalLoaderQueries.getXsdElementId("a_new_element"));
	}

	public void testGetXsdElementId() {
		long elementId = clinicalLoaderQueries
				.getXsdElementId("bcr_patient_uuid");
		assertEquals(elementId, 8l);
	}

	public void testGetXsdElementIdNotFound() {
		long elementId = clinicalLoaderQueries.getXsdElementId("dummy element");
		assertEquals(elementId, -2);

	}

	public void testGetXsdElementProtected() {
		long elementId = clinicalLoaderQueries
				.getXsdElementId("bcr_not_expected_element");
		assertEquals(elementId, -1);
	}

    public void testGetDynamicClinicalTables() {
        List<ClinicalTable> dynamicTables = clinicalLoaderQueries.getDynamicClinicalTables();
        assertEquals(1, dynamicTables.size());
        assertEquals("FOLLOW_UP", dynamicTables.get(0).getTableName());
    }

    public void testGetIdDynamic() {
        ClinicalObject followUpObject = new ClinicalObject();
        ClinicalTable followUpTable = clinicalLoaderQueries.getClinicalTableById(4);
        ClinicalTable patientTable = clinicalLoaderQueries.getClinicalTableById(1);
        followUpObject.setClinicalTable(followUpTable);
        followUpObject.setParentId(12345);
        followUpObject.setDynamicIdentifier("follow_up_v2.4");
        followUpObject.setParentTable(patientTable);

        assertEquals(2020, clinicalLoaderQueries.getId(followUpObject));
    }

    public void testGetIdDynamicNotFound() {
        ClinicalObject followUpObject = new ClinicalObject();
        ClinicalTable followUpTable = clinicalLoaderQueries.getClinicalTableById(4);
        ClinicalTable patientTable = clinicalLoaderQueries.getClinicalTableById(1);
        followUpObject.setClinicalTable(followUpTable);
        followUpObject.setParentId(12345);
        followUpObject.setDynamicIdentifier("follow_up_vSquirrel");
        followUpObject.setParentTable(patientTable);

        assertEquals(-1, clinicalLoaderQueries.getId(followUpObject));
    }

}
