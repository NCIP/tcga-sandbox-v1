/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * AnnotationAddWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationAddWSFastTest {

    private AnnotationAddWS annotationAddWS;

    private Mockery mockery = new JUnit4Mockery();
    private AnnotationService mockAnnotationService;
    private SecurityUtil mockSecurityUtil;

    @Before
    public void setUp() {

        mockAnnotationService = mockery.mock(AnnotationService.class);
        mockSecurityUtil = mockery.mock(SecurityUtil.class);

        annotationAddWS = new AnnotationAddWS();
        annotationAddWS.setAnnotationService(mockAnnotationService);
        annotationAddWS.setSecurityUtil(mockSecurityUtil);
    }

    @Test
    public void testAddDccAnnotationToXmlDiseaseNotProvided() {
        testAddDccAnnotationDiseaseNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonDiseaseNotProvided() {
        testAddDccAnnotationDiseaseNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlDiseaseNoMatch() {
        testAddDccAnnotationDiseaseNoMatch(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonDiseaseNoMatch() {
        testAddDccAnnotationDiseaseNoMatch(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlMatchItemTypeNotProvided() {
        testAddDccAnnotationItemTypeNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonItemTypeNotProvided() {
        testAddDccAnnotationItemTypeNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlItemTypeNoMatch() {
        testAddDccAnnotationItemTypeNoMatch(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonItemTypeNoMatch() {
        testAddDccAnnotationItemTypeNoMatch(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlItemNotProvided() {
        testAddDccAnnotationItemNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonItemNotProvided() {
        testAddDccAnnotationItemNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlItemNoMatch() {
        testAddDccAnnotationItemNoMatch(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonItemNoMatch() {
        testAddDccAnnotationItemNoMatch(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlItemNotSupported() {
        testAddDccAnnotationItemNotSupported(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonItemNotSupported() {
        testAddDccAnnotationItemNotSupported(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlAnnotationCategoryNotProvided() {
        testAddDccAnnotationAnnotationCategoryNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonAnnotationCategoryNotProvided() {
        testAddDccAnnotationAnnotationCategoryNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlAnnotationCategoryNoMatch() {
        testAddDccAnnotationAnnotationCategoryNoMatch(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonAnnotationCategoryNoMatch() {
        testAddDccAnnotationAnnotationCategoryNoMatch(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlNoteNotProvided() throws AnnotationQueries.AnnotationQueriesException {
        testAddDccAnnotationNoteNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonNoteNotProvided() throws AnnotationQueries.AnnotationQueriesException {
        testAddDccAnnotationNoteNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXml() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotation(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJson() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotation(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationToXmlStrict() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationStrict(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationToJsonStrict() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationStrict(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddShippedPortion() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation expectedAnnotation = new DccAnnotation();

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));

            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));

            one(mockAnnotationService).getAnnotationCategories();
            will(returnValue(getPretendAnnotationCategories()));

            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("squirrel"));

            one(mockAnnotationService).addAnnotation(1, 9L, "TCGA-00-1111-01A-20-3333-44", 2L, "Testing", "squirrel", true);
            will(returnValue(expectedAnnotation));
        }});
        final DccAnnotation returnedAnnotation = annotationAddWS.addDccAnnotationToJson(
                            "TumorName1",
                            "Shipped Portion",
                            "TCGA-00-1111-01A-20-3333-44",
                            "Category 2",
                            "Testing",
                            "true"
                    );
        assertEquals(expectedAnnotation, returnedAnnotation);
    }

    @Test
    public void testAddInvalidShippedPortion() {
        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));

            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));

        }});

        try {
            annotationAddWS.addDccAnnotationToJson(
                    "TumorName1",
                    "Shipped Portion",
                    "squirrel",
                    "Category 2",
                    "Testing",
                    "true"
            );

            fail("exception was not thrown");
        } catch (WebApplicationException e) {
            checkWebErrorMessage(e, new String[]{"HTTP STATUS 412", "The item 'squirrel' does not match a Shipped Portion barcode"});
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString is has no match
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationDiseaseNoMatch(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationDiseaseNoMatch(returnType, "TumorName4", "<p>Disease 'TumorName4' does not exist.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationDiseaseNotProvided(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationDiseaseNoMatch(returnType, null, "<p>Please provide a disease.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has no match
     *
     * @param returnType                  the <code>ReturnType</code> that will decide which method gets called
     * @param diseaseAsString             the disease as String (must have no match)
     * @param expectedErrorMessageContent the expected error message content
     */
    private void testAddDccAnnotationDiseaseNoMatch(final WebServiceUtil.ReturnType returnType,
                                                    final String diseaseAsString,
                                                    final String expectedErrorMessageContent) {

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(diseaseAsString, null, null, null, null, "false");
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(diseaseAsString, null, null, null, null, "false");
                    break;
            }

            fail("WebApplicationException wasn't thrown.");

        } catch (final WebApplicationException e) {

            final String[] expectedContents = {"HTTP STATUS 412", expectedErrorMessageContent};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has no match
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationItemTypeNoMatch(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationItemTypeNoMatch(returnType, "ItemTypeName4", "<p>Item type 'ItemTypeName4' does not exist.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationItemTypeNotProvided(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationItemTypeNoMatch(returnType, null, "<p>Please provide an item type.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has no match
     *
     * @param returnType                  the <code>ReturnType</code> that will decide which method gets called
     * @param itemTypeAsString            the item type as String (must have no match)
     * @param expectedErrorMessageContent the expected error message content
     */
    private void testAddDccAnnotationItemTypeNoMatch(final WebServiceUtil.ReturnType returnType,
                                                     final String itemTypeAsString,
                                                     final String expectedErrorMessageContent) {

        final String diseaseAsString = "2";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(diseaseAsString, itemTypeAsString, null, null, null, "false");
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(diseaseAsString, itemTypeAsString, null, null, null, "false");
                    break;
            }

            fail("WebApplicationException wasn't thrown.");

        } catch (final WebApplicationException e) {

            final String[] expectedContents = {"HTTP STATUS 412", expectedErrorMessageContent};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString is not supported
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationItemNotSupported(final WebServiceUtil.ReturnType returnType) {

        final String itemAsStringNotSupported = "TCGA-NOT-SUPPORTED";
        testAddDccAnnotationItemNoMatch(returnType, "Imaginary", itemAsStringNotSupported, "<p>Item type 'Imaginary' is not supported.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString has no match
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationItemNoMatch(final WebServiceUtil.ReturnType returnType) {

        final String itemAsStringNoMatch = "TCGA-NO_MATCH";
        final String[] supportedItemTypes = {"Aliquot", "Analyte", "Patient", "Portion", "Sample", "Slide", "UUID"};

        for (final String supportedItemType : supportedItemTypes) {
            testAddDccAnnotationItemNoMatch(returnType, supportedItemType, itemAsStringNoMatch, "does not match");
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationItemNotProvided(final WebServiceUtil.ReturnType returnType) {

        final String[] supportedItemTypes = {"Aliquot", "Analyte", "Patient", "Portion", "Sample", "Slide", "UUID"};
        final String expectedErrorMessageContent = "<p>Please provide an item.</p>";

        for (final String supportedItemType : supportedItemTypes) {
            testAddDccAnnotationItemNoMatch(returnType, supportedItemType, null, expectedErrorMessageContent);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     *
     * @param returnType                  the <code>ReturnType</code> that will decide which method gets called
     * @param itemTypeAsString            the item type as String (must have a name match)
     * @param itemAsString                the item as String - barcode or UUID (must have no match)
     * @param expectedErrorMessageContent the expected error message content
     */
    private void testAddDccAnnotationItemNoMatch(final WebServiceUtil.ReturnType returnType,
                                                 final String itemTypeAsString,
                                                 final String itemAsString,
                                                 final String expectedErrorMessageContent) {

        final String diseaseAsString = "2";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(diseaseAsString, itemTypeAsString, itemAsString, null, null, "false");
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(diseaseAsString, itemTypeAsString, itemAsString, null, null, "false");
                    break;
            }

            fail("WebApplicationException wasn't thrown.");

        } catch (final WebApplicationException e) {

            final String[] expectedContents = {"HTTP STATUS 412", expectedErrorMessageContent};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString matches
     * - annotationCategoryAsString has no match
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationAnnotationCategoryNoMatch(final WebServiceUtil.ReturnType returnType) {

        final String annotationCategoryAsStringNoMatch = "Category 4";
        testAddDccAnnotationCategoryNoMatch(returnType, annotationCategoryAsStringNoMatch,
                "<p>Annotation category '" + annotationCategoryAsStringNoMatch + "' does not exist.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString matches
     * - annotationCategoryAsString is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationAnnotationCategoryNotProvided(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationCategoryNoMatch(returnType, null, "<p>Please provide an annotation category.<br/>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has a name match
     * - itemAsString matches
     *
     * @param returnType                  the <code>ReturnType</code> that will decide which method gets called
     * @param annotationCategoryAsString  the annotation category as String (must have no match)
     * @param expectedErrorMessageContent the expected error message content
     */
    private void testAddDccAnnotationCategoryNoMatch(final WebServiceUtil.ReturnType returnType,
                                                     final String annotationCategoryAsString,
                                                     final String expectedErrorMessageContent) {

        final String diseaseAsString = "2";
        final String itemTypeAsString = "Patient";
        final String itemAsString = "TCGA-06-0939";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
            one(mockAnnotationService).getAnnotationCategories();
            will(returnValue(getPretendAnnotationCategories()));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(
                            diseaseAsString,
                            itemTypeAsString,
                            itemAsString,
                            annotationCategoryAsString,
                            null,
                            "false"
                    );
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(
                            diseaseAsString,
                            itemTypeAsString,
                            itemAsString,
                            annotationCategoryAsString,
                            null,
                            "false"
                    );
                    break;
            }

            fail("WebApplicationException wasn't thrown.");

        } catch (final WebApplicationException e) {

            final String[] expectedContents = {"HTTP STATUS 412", expectedErrorMessageContent};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has an Id match
     * - itemAsString matches
     * - annotationCategoryAsString has an Id match
     * - annotationNoteAsString is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     *
     */
    private void testAddDccAnnotationNoteNotProvided(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException {

        testAddDccAnnotationCategoryNoteInvalid(returnType, null, "<p>Please provide an annotation note</p>");
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has an Id match
     * - itemAsString matches
     * - annotationCategoryAsString has an Id match
     *
     * @param returnType                  the <code>ReturnType</code> that will decide which method gets called
     * @param annotationNoteAsString      the annotation note as String (must be invalid)
     * @param expectedErrorMessageContent the expected error message content
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     *
     */
    private void testAddDccAnnotationCategoryNoteInvalid(final WebServiceUtil.ReturnType returnType,
                                                         final String annotationNoteAsString,
                                                         final String expectedErrorMessageContent)
            throws AnnotationQueries.AnnotationQueriesException {

        final String diseaseAsString = "2";
        final String itemTypeAsString = "3"; // Patient
        final String itemAsString = "TCGA-06-0939";
        final String annotationCategoryAsString = "1";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
            one(mockAnnotationService).getAnnotationCategories();
            will(returnValue(getPretendAnnotationCategories()));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(
                            diseaseAsString,
                            itemTypeAsString,
                            itemAsString,
                            annotationCategoryAsString,
                            annotationNoteAsString,
                            "false"
                    );
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(
                            diseaseAsString,
                            itemTypeAsString,
                            itemAsString,
                            annotationCategoryAsString,
                            annotationNoteAsString,
                            "false"
                    );
                    break;
            }

            fail("WebApplicationException wasn't thrown.");

        } catch (final WebApplicationException e) {

            final String[] expectedContents = {"HTTP STATUS 412", expectedErrorMessageContent};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has an Id match
     * - itemAsString matches
     * - annotationCategoryAsString has an Id match
     * - annotationNoteAsString is valid
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     *
     */
    private void testAddDccAnnotation(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        // Expected Annotation values
        final String expectedAnnotationId = "1";
        final String expectedDiseaseAsString = "1";
        final String expectedItemTypeAsString = "3"; // (Patient)
        final String expectedItemAsString = "TCGA-06-0939"; // Patient barcode
        final String expectedAnnotationCategoryAsString = "2";
        final String expectedAnnotationNoteAsString = "Test Note";
        final String expectedUsername = "TestUsername";

        final DccAnnotation expectedDccAnnotation = getPretendDccAnnotation(
                expectedAnnotationId,
                expectedDiseaseAsString,
                expectedItemTypeAsString,
                expectedItemAsString,
                expectedAnnotationCategoryAsString,
                expectedAnnotationNoteAsString,
                expectedUsername
        );

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
            one(mockAnnotationService).getAnnotationCategories();
            will(returnValue(getPretendAnnotationCategories()));
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(expectedUsername));
            one(mockAnnotationService).addAnnotation(
                    Integer.valueOf(expectedDiseaseAsString),
                    Long.valueOf(expectedItemTypeAsString),
                    expectedItemAsString,
                    Long.valueOf(expectedAnnotationCategoryAsString),
                    expectedAnnotationNoteAsString,
                    expectedUsername,
                    false);
            will(returnValue(expectedDccAnnotation));
        }});

        DccAnnotation actualDccAnnotation = null;

        switch (returnType) {
            case XML:
                actualDccAnnotation = annotationAddWS.addDccAnnotationToXml(
                        expectedDiseaseAsString,
                        expectedItemTypeAsString,
                        expectedItemAsString,
                        expectedAnnotationCategoryAsString,
                        expectedAnnotationNoteAsString,
                        "false"
                );
                break;
            case JSON:
                actualDccAnnotation = annotationAddWS.addDccAnnotationToJson(
                        expectedDiseaseAsString,
                        expectedItemTypeAsString,
                        expectedItemAsString,
                        expectedAnnotationCategoryAsString,
                        expectedAnnotationNoteAsString,
                        "false"
                );
                break;
        }

        assertNotNull("DccAnnotation null", actualDccAnnotation);

        assertNotNull("DccAnnotationItem's null", actualDccAnnotation.getItems());
        assertEquals("Unexpected number of DccAnnotationItem's", 1, actualDccAnnotation.getItems().size());

        final DccAnnotationItem firstDccAnnotationItem = actualDccAnnotation.getItems().get(0);

        assertEquals("Unexpected Disease Id",
                expectedDiseaseAsString,
                String.valueOf(firstDccAnnotationItem.getDisease().getTumorId()));

        assertEquals("Unexpected Item type Id",
                expectedItemTypeAsString,
                String.valueOf(firstDccAnnotationItem.getItemType().getItemTypeId()));

        assertEquals("Unexpected Item",
                expectedItemAsString,
                firstDccAnnotationItem.getItem());

        assertEquals("Unexpected Category Id",
                expectedAnnotationCategoryAsString,
                String.valueOf(actualDccAnnotation.getAnnotationCategory().getCategoryId()));

        assertEquals("Unexpected Created By",
                expectedUsername,
                actualDccAnnotation.getCreatedBy());

        assertEquals("Unexpected Note",
                expectedAnnotationNoteAsString,
                actualDccAnnotation.getNotes().get(0).getNoteText());
    }

    /**
     * Will unit test addDccAnnotationToXml() or addDccAnnotationToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     * - diseaseAsString has an Id match
     * - itemTypeAsString has an Id match
     * - itemAsString matches
     * - annotationCategoryAsString has an Id match
     * - annotationNoteAsString is valid
     * - item validation is set to strict
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     *
     */
    private void testAddDccAnnotationStrict(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        // Expected Annotation values
        final String expectedAnnotationId = "1";
        final String expectedDiseaseAsString = "1";
        final String expectedItemTypeAsString = "3"; // (Patient)
        final String expectedItemAsString = "TCGA-XY-0939"; // Patient barcode
        final String expectedAnnotationCategoryAsString = "2";
        final String expectedAnnotationNoteAsString = "Test Note";
        final String expectedUsername = "TestUsername";
        final String useStrictItemValidationAsString = "true";

        final String expectedExceptionMessage = "code XY does not exist";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getActiveDiseases();
            will(returnValue(getPretendActiveDiseases()));
            one(mockAnnotationService).getItemTypes();
            will(returnValue(getPretendItemTypes()));
            one(mockAnnotationService).getAnnotationCategories();
            will(returnValue(getPretendAnnotationCategories()));
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(expectedUsername));
            one(mockAnnotationService).addAnnotation(
                    Integer.valueOf(expectedDiseaseAsString),
                    Long.valueOf(expectedItemTypeAsString),
                    expectedItemAsString,
                    Long.valueOf(expectedAnnotationCategoryAsString),
                    expectedAnnotationNoteAsString,
                    expectedUsername,
                    true);
            will(throwException(new AnnotationQueries.AnnotationQueriesException(expectedExceptionMessage)));
        }});

        try {
            switch (returnType) {
                case XML:
                    annotationAddWS.addDccAnnotationToXml(
                            expectedDiseaseAsString,
                            expectedItemTypeAsString,
                            expectedItemAsString,
                            expectedAnnotationCategoryAsString,
                            expectedAnnotationNoteAsString,
                            useStrictItemValidationAsString
                    );
                    break;
                case JSON:
                    annotationAddWS.addDccAnnotationToJson(
                            expectedDiseaseAsString,
                            expectedItemTypeAsString,
                            expectedItemAsString,
                            expectedAnnotationCategoryAsString,
                            expectedAnnotationNoteAsString,
                            useStrictItemValidationAsString
                    );
                    break;
            }

            fail("WebApplicationException should have been thrown");

        } catch(final WebApplicationException e) {
            
            final String[] expectedContents = {"HTTP STATUS 500", expectedExceptionMessage};
            checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Return a list of active diseases for these tests
     *
     * @return a list of active diseases for these tests
     */
    private List<Tumor> getPretendActiveDiseases() {

        final List<Tumor> result = new LinkedList<Tumor>();

        result.add(getPretendTumor(1));
        result.add(getPretendTumor(2));
        result.add(getPretendTumor(3));

        return result;
    }

    /**
     * Return a new <code>Tumor</code> with the given Id for these tests
     * - set the name and description as well
     *
     * @param tumorId the <code>Tumor</code> Id
     * @return a new <code>Tumor</code> with the given Id for these tests
     */
    private Tumor getPretendTumor(final int tumorId) {

        final Tumor result = new Tumor();

        result.setTumorId(tumorId);
        result.setTumorName("TumorName" + tumorId);
        result.setTumorDescription("TumorDescription" + tumorId);

        return result;
    }

    /**
     * Return a list of <code>DccAnnotationItemType</code> for these tests
     *
     * @return a list of <code>DccAnnotationItemType</code> for these tests
     */
    private List<DccAnnotationItemType> getPretendItemTypes() {

        final List<DccAnnotationItemType> result = new LinkedList<DccAnnotationItemType>();

        result.add(getPretendDccAnnotationItemType(1L, "Aliquot"));
        result.add(getPretendDccAnnotationItemType(2L, "Analyte"));
        result.add(getPretendDccAnnotationItemType(3L, "Patient"));
        result.add(getPretendDccAnnotationItemType(4L, "Portion"));
        result.add(getPretendDccAnnotationItemType(5L, "Sample"));
        result.add(getPretendDccAnnotationItemType(6L, "Slide"));
        result.add(getPretendDccAnnotationItemType(7L, "UUID"));
        result.add(getPretendDccAnnotationItemType(8L, "Imaginary"));
        result.add(getPretendDccAnnotationItemType(9L, "Shipped Portion"));

        return result;
    }

    /**
     * Return a new <code>DccAnnotationItemType</code> with the given Id and name for these tests
     * - set the description as well
     *
     * @param itemTypeId   the <code>DccAnnotationItemType</code> Id
     * @param itemTypeName the <code>DccAnnotationItemType</code> name
     * @return a new <code>DccAnnotationItemType</code> with the given Id and name for these tests
     */
    private DccAnnotationItemType getPretendDccAnnotationItemType(final Long itemTypeId, final String itemTypeName) {

        final DccAnnotationItemType result = new DccAnnotationItemType();

        result.setItemTypeId(itemTypeId);
        result.setItemTypeName(itemTypeName);
        result.setItemTypeDescription("The " + itemTypeName);

        return result;
    }


    /**
     * Return a list of <code>DccAnnotationCategory</code> for these tests
     *
     * @return a list of <code>DccAnnotationCategory</code> for these tests
     */
    private List<DccAnnotationCategory> getPretendAnnotationCategories() {

        final List<DccAnnotationCategory> result = new LinkedList<DccAnnotationCategory>();

        result.add(getPretendDccAnnotationCategory(1L));
        result.add(getPretendDccAnnotationCategory(2L));
        result.add(getPretendDccAnnotationCategory(3L));

        return result;
    }

    /**
     * Return a new <code>DccAnnotationCategory</code> with the given Id
     * - set the name as well
     *
     * @param categoryId the <code>DccAnnotationCategory</code> Id
     * @return a new <code>DccAnnotationCategory</code> with the given Id
     */
    private DccAnnotationCategory getPretendDccAnnotationCategory(final Long categoryId) {

        final DccAnnotationCategory result = new DccAnnotationCategory();

        result.setCategoryId(categoryId);
        result.setCategoryName("Category " + categoryId);

        return result;
    }

    /**
     * Return a new <code>DccAnnotation</code> for these tests
     * with the following values:
     *
     * @param annotationIdAsString         the annotation Id, as String
     * @param diseaseIdAsString            the disease Id, as String
     * @param itemTypeIdAsString           the item type Id, as String
     * @param item                         the item, as String
     * @param annotationCategoryIdAsString the annotation category Id, as String
     * @param annotationNoteTxt            the annotation note text, as String
     * @param userName                     the login of the user who created the annotation, as String
     * @return a new <code>DccAnnotation</code> for these tests
     */
    private DccAnnotation getPretendDccAnnotation(final String annotationIdAsString,
                                                  final String diseaseIdAsString,
                                                  final String itemTypeIdAsString,
                                                  final String item,
                                                  final String annotationCategoryIdAsString,
                                                  final String annotationNoteTxt,
                                                  final String userName) {

        final DccAnnotation result = new DccAnnotation();

        final DccAnnotationCategory dccAnnotationCategory = new DccAnnotationCategory();
        dccAnnotationCategory.setCategoryId(Long.valueOf(annotationCategoryIdAsString));

        final Tumor tumor = new Tumor();
        tumor.setTumorId(Integer.valueOf(diseaseIdAsString));

        final DccAnnotationItemType dccAnnotationItemType = new DccAnnotationItemType();
        dccAnnotationItemType.setItemTypeId(Long.valueOf(itemTypeIdAsString));

        result.setId(Long.valueOf(annotationIdAsString));
        result.setAnnotationCategory(dccAnnotationCategory);
        
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setDisease(tumor);
        dccAnnotationItem.setItemType(dccAnnotationItemType);
        dccAnnotationItem.setItem(item);
        
        result.addItem(dccAnnotationItem);
        
        result.addNote(annotationNoteTxt, userName);
        result.setCreatedBy(userName);

        return result;
    }

    /**
     * Check the given <code>WebApplicationException</code> for expected content
     *
     * @param e                the <code>WebApplicationException</code>
     * @param expectedContents the expected content
     */
    public static void checkWebErrorMessage(final WebApplicationException e, final String[] expectedContents) {

        final String webErrorMessage = WebServiceUtil.getWebErrorMessage(e);

        for (final String expectedContent : expectedContents) {
            assertTrue(new StringBuilder("No such content: ")
                    .append(expectedContent)
                    .append(" [was:")
                    .append(webErrorMessage)
                    .append("]")
                    .toString(),
                    webErrorMessage.contains(expectedContent));
        }
    }
}
