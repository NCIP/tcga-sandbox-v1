/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a REST web service to add a new <code>DccAnnotation</code>
 * and get the result back in XML or JSON format
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/addannotation")
public class AnnotationAddWS {

    /**
     * HTML new line
     */
    private static final String HTML_NEW_LINE = "<br/>";

    private final Log log = LogFactory.getLog(getClass());

    @InjectParam
    private AnnotationService annotationService;

    @InjectParam("securityUtil")
    private SecurityUtil securityUtil;

    /**
     * Add a new <code>DccAnnotation</code> with the following values and return it in XML format:
     *
     * @param diseaseAsString            the disease (can be the disease Id, name or description)
     * @param itemTypeAsString           the item type (can be the item type Id, name or description)
     * @param itemAsString               the item (one of the supported barcodes or an UUID)
     * @param annotationCategoryAsString the annotation category (can be the annotation category Id or name)
     * @param annotationNoteAsString     the annotation note
     * @param useStrictItemValidationAsString
     *                                   set to "true" to validate the annotation's item against the database values or "false" otherwise
     * @return the new <code>DccAnnotation</code> in XML format
     */
    @GET
    @Path("/xml/")
    @Produces(MediaType.APPLICATION_XML)
    public DccAnnotation addDccAnnotationToXml(
            @QueryParam("disease") final String diseaseAsString,
            @QueryParam("itemType") final String itemTypeAsString,
            @QueryParam("item") final String itemAsString,
            @QueryParam("annotationCategory") final String annotationCategoryAsString,
            @QueryParam("annotationNote") final String annotationNoteAsString,
            @QueryParam("useStrictItemValidation") @DefaultValue("true") final String useStrictItemValidationAsString) {

        return addAnnotation(diseaseAsString, itemTypeAsString, itemAsString, annotationCategoryAsString, annotationNoteAsString, useStrictItemValidationAsString);
    }

    /**
     * Add a new <code>DccAnnotation</code> with the following values and return it in JSON format:
     *
     * @param diseaseAsString            the disease (can be the disease Id, name or description)
     * @param itemTypeAsString           the item type (can be the item type Id, name or description)
     * @param itemAsString               the item (one of the supported barcodes or an UUID)
     * @param annotationCategoryAsString the annotation category (can be the annotation category Id or name)
     * @param annotationNoteAsString     the annotation note
     * @param useStrictItemValidationAsString
     *                                   set to "true" to validate the annotation's item against the database values or "false" otherwise
     * @return the new <code>DccAnnotation</code> in JSON format
     */
    @GET
    @Path("/json/")
    @Produces(MediaType.APPLICATION_JSON)
    public DccAnnotation addDccAnnotationToJson(
            @QueryParam("disease") final String diseaseAsString,
            @QueryParam("itemType") final String itemTypeAsString,
            @QueryParam("item") final String itemAsString,
            @QueryParam("annotationCategory") final String annotationCategoryAsString,
            @QueryParam("annotationNote") final String annotationNoteAsString,
            @QueryParam("useStrictItemValidation") @DefaultValue("true") final String useStrictItemValidationAsString) {

        return addAnnotation(diseaseAsString, itemTypeAsString, itemAsString, annotationCategoryAsString, annotationNoteAsString, useStrictItemValidationAsString);
    }

    /**
     * Add a new <code>DccAnnotation</code> in the database with the following values:
     *
     * @param diseaseAsString            the disease (can be the disease Id, name or description)
     * @param itemTypeAsString           the item type (can be the item type Id, name or description)
     * @param itemAsString               the item (one of the supported barcodes or an UUID)
     * @param annotationCategoryAsString the annotation category (can be the annotation category Id or name)
     * @param annotationNoteAsString     the annotation note
     * @param useStrictItemValidationAsString
     *                                   set to "true" to validate the annotation's item against the database values or "false" otherwise
     * @return the new <code>DccAnnotation</code>
     */
    private DccAnnotation addAnnotation(final String diseaseAsString,
                                        final String itemTypeAsString,
                                        final String itemAsString,
                                        final String annotationCategoryAsString,
                                        final String annotationNoteAsString,
                                        final String useStrictItemValidationAsString) {

        DccAnnotation result = null;

        final boolean useStrictItemValidation = new Boolean(useStrictItemValidationAsString);

        // Retrieving Ids and validating inputs
        final Integer diseaseId = getDiseaseId(diseaseAsString);
        final DccAnnotationItemType dccAnnotationItemType = getDccAnnotationItemType(itemTypeAsString);
        final Long itemTypeId = dccAnnotationItemType.getItemTypeId();
        validateItem(itemAsString, dccAnnotationItemType.getItemTypeName());
        final Long annotationCategoryId = getAnnotationCategoryId(annotationCategoryAsString);
        validateAnnotationNoteAsString(annotationNoteAsString);

        // Note: if the web service user is not authenticated at that point, that is not a problem because as soon as the protected resource is called
        // (at the DAO level), Spring will display a login page and re-call the web service after the user authenticated
        // at which point the 'user' field will be properly set
        final String user = getSecurityUtil().getAuthenticatedPrincipalLoginName();

        try {
            result = annotationService.addAnnotation(diseaseId, itemTypeId, itemAsString, annotationCategoryId, annotationNoteAsString, user, useStrictItemValidation);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {

            final String errorMessage = new StringBuilder("Error while adding new annotation: ")
                    .append(e.getMessage())
                    .toString();

            WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.INTERNAL_SERVER_ERROR);
        } catch (final BeanException be) {
            final String errorMessage = new StringBuilder("Error while adding new annotation: ")
                    .append(be.getMessage())
                    .toString();

            WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * Return the disease Id for the given disease
     *
     * @param diseaseAsString the disease for which to get the Id
     * @return the disease Id
     */
    private Integer getDiseaseId(final String diseaseAsString) {

        Integer result = null;

        final List<Tumor> activeDiseases = annotationService.getActiveDiseases();
        final StringBuilder errorMessage = new StringBuilder();

        if (StringUtils.isBlank(diseaseAsString)) {
            //No disease provided, update error message
            errorMessage.append("Please provide a disease.");

        } else {
            // Look for a match in active diseases
            final Iterator<Tumor> tumorIterator = activeDiseases.iterator();
            Tumor tumor;

            while (result == null && tumorIterator.hasNext()) {

                tumor = tumorIterator.next();

                if (diseaseIdMatch(diseaseAsString, tumor)
                        || diseaseNameMatch(diseaseAsString, tumor)
                        || diseaseDescriptionMatch(diseaseAsString, tumor)) {
                    result = tumor.getTumorId();
                }
            }

            if (result == null) {

                // No match was found, update error message
                errorMessage.append("Disease '")
                        .append(diseaseAsString)
                        .append("' does not exist.");
            }
        }

        //If no match was found, throw WebApplicationException
        if (result == null) {
            throwWebApplicationException(errorMessage, getActiveDiseasesDisplay(activeDiseases));
        }

        return result;
    }

    /**
     * Return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> Id, <code>false</code> otherwise
     *
     * @param diseaseAsString the string to match
     * @param tumor           the <code>Tumor</code>
     * @return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> Id, <code>false</code> otherwise
     */
    private boolean diseaseIdMatch(final String diseaseAsString, final Tumor tumor) {

        return tumor.getTumorId() != null
                && tumor.getTumorId().toString().equals(diseaseAsString);
    }

    /**
     * Return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> name, <code>false</code> otherwise
     *
     * @param diseaseAsString the string to match
     * @param tumor           the <code>Tumor</code>
     * @return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> name, <code>false</code> otherwise
     */
    private boolean diseaseNameMatch(final String diseaseAsString, final Tumor tumor) {

        return tumor.getTumorName() != null
                && tumor.getTumorName().equals(diseaseAsString);
    }

    /**
     * Return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> description, <code>false</code> otherwise
     *
     * @param diseaseAsString the string to match
     * @param tumor           the <code>Tumor</code>
     * @return <code>true</code> if <code>diseaseAsString</code> matches the given <code>Tumor</code> description, <code>false</code> otherwise
     */
    private boolean diseaseDescriptionMatch(final String diseaseAsString, final Tumor tumor) {

        return tumor.getTumorDescription() != null
                && tumor.getTumorDescription().equals(diseaseAsString);
    }

    /**
     * Return the <code>DccAnnotationItemType</code> which Id or name matches the given parameter, <code>null</code> if no match found
     *
     * @param dccAnnotationItemTypeAsString the <code>DccAnnotationItemType</code> name or Id
     * @return the <code>DccAnnotationItemType</code> which Id or name matches the given parameter, <code>null</code> if no match found
     */
    private DccAnnotationItemType getDccAnnotationItemType(final String dccAnnotationItemTypeAsString) {

        DccAnnotationItemType result = null;

        final List<DccAnnotationItemType> itemTypes = annotationService.getItemTypes();
        final StringBuilder errorMessage = new StringBuilder();

        if (StringUtils.isBlank(dccAnnotationItemTypeAsString)) {
            //Prepare error message
            errorMessage.append("Please provide an item type.");

        } else {
            //Prepare error message in case no match is found
            errorMessage.append("Item type '")
                    .append(dccAnnotationItemTypeAsString)
                    .append("' does not exist.");

            // Look for a match in item types
            final Iterator<DccAnnotationItemType> dccAnnotationItemTypeIterator = itemTypes.iterator();
            DccAnnotationItemType dccAnnotationItemType;

            while (result == null && dccAnnotationItemTypeIterator.hasNext()) {

                dccAnnotationItemType = dccAnnotationItemTypeIterator.next();

                if (dccAnnotationItemTypeIdMatch(dccAnnotationItemTypeAsString, dccAnnotationItemType)
                        || dccAnnotationItemTypeNameMatch(dccAnnotationItemTypeAsString, dccAnnotationItemType)
                        || dccAnnotationItemTypeDescriptionMatch(dccAnnotationItemTypeAsString, dccAnnotationItemType)) {
                    result = dccAnnotationItemType;
                }
            }
        }

        //If no match was found, throw WebApplicationException
        if (result == null) {
            throwWebApplicationException(errorMessage, getItemTypesDisplay(itemTypes));
        }

        return result;
    }

    /**
     * Return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> Id,
     * <code>false</code> otherwise
     *
     * @param dccAnnotationItemTypeAsString the string to match
     * @param dccAnnotationItemType         the <code>DccAnnotationItemType</code>
     * @return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> Id,
     *         <code>false</code> otherwise
     */
    private boolean dccAnnotationItemTypeIdMatch(final String dccAnnotationItemTypeAsString,
                                                 final DccAnnotationItemType dccAnnotationItemType) {

        return Long.toString(dccAnnotationItemType.getItemTypeId()).equals(dccAnnotationItemTypeAsString);
    }

    /**
     * Return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> name,
     * <code>false</code> otherwise
     *
     * @param dccAnnotationItemTypeAsString the string to match
     * @param dccAnnotationItemType         the <code>DccAnnotationItemType</code>
     * @return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> name,
     *         <code>false</code> otherwise
     */
    private boolean dccAnnotationItemTypeNameMatch(final String dccAnnotationItemTypeAsString,
                                                   final DccAnnotationItemType dccAnnotationItemType) {

        return dccAnnotationItemType.getItemTypeName() != null
                && dccAnnotationItemType.getItemTypeName().equals(dccAnnotationItemTypeAsString);
    }

    /**
     * Return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> description,
     * <code>false</code> otherwise
     *
     * @param dccAnnotationItemTypeAsString the string to match
     * @param dccAnnotationItemType         the <code>DccAnnotationItemType</code>
     * @return <code>true</code> if <code>dccAnnotationItemTypeAsString</code> matches the given <code>DccAnnotationItemType</code> description,
     *         <code>false</code> otherwise
     */
    private boolean dccAnnotationItemTypeDescriptionMatch(final String dccAnnotationItemTypeAsString,
                                                          final DccAnnotationItemType dccAnnotationItemType) {

        return dccAnnotationItemType.getItemTypeDescription() != null
                && dccAnnotationItemType.getItemTypeDescription().equals(dccAnnotationItemTypeAsString);
    }

    /**
     * Return the annotation category Id for the given annotation category
     *
     * @param annotationCategoryAsString the annotation category for which to get the Id
     * @return the annotation category Id
     */
    private Long getAnnotationCategoryId(final String annotationCategoryAsString) {

        Long result = null;

        final List<DccAnnotationCategory> annotationCategories = annotationService.getAnnotationCategories();
        final StringBuilder errorMessage = new StringBuilder();

        if (StringUtils.isBlank(annotationCategoryAsString)) {
            //Prepare error message
            errorMessage.append("Please provide an annotation category.");

        } else {
            //Prepare error message in case no match is found
            errorMessage.append("Annotation category '")
                    .append(annotationCategoryAsString)
                    .append("' does not exist.");

            // Look for a match in annotation categories
            final Iterator<DccAnnotationCategory> dccAnnotationCategoryIterator = annotationCategories.iterator();
            DccAnnotationCategory dccAnnotationCategory;

            while (result == null && dccAnnotationCategoryIterator.hasNext()) {

                dccAnnotationCategory = dccAnnotationCategoryIterator.next();

                if (dccAnnotationCategoryIdMatch(annotationCategoryAsString, dccAnnotationCategory)
                        || dccAnnotationCategoryNameMatch(annotationCategoryAsString, dccAnnotationCategory)) {
                    result = dccAnnotationCategory.getCategoryId();
                }
            }
        }

        //If no match was found, throw WebApplicationException
        if (result == null) {
            throwWebApplicationException(errorMessage, getAnnotationCategoriesDisplay(annotationCategories));
        }

        return result;
    }

    /**
     * Return <code>true</code> if <code>annotationCategoryAsString</code> matches the given <code>DccAnnotationCategory</code> Id,
     * <code>false</code> otherwise
     *
     * @param annotationCategoryAsString the string to match
     * @param dccAnnotationCategory      the <code>DccAnnotationCategory</code>
     * @return <code>true</code> if <code>annotationCategoryAsString</code> matches the given <code>DccAnnotationCategory</code> Id,
     *         <code>false</code> otherwise
     */
    private boolean dccAnnotationCategoryIdMatch(String annotationCategoryAsString, DccAnnotationCategory dccAnnotationCategory) {
        return Long.toString(dccAnnotationCategory.getCategoryId()).equals(annotationCategoryAsString);
    }

    /**
     * Return <code>true</code> if <code>annotationCategoryAsString</code> matches the given <code>DccAnnotationCategory</code> name,
     * <code>false</code> otherwise
     *
     * @param annotationCategoryAsString the string to match
     * @param dccAnnotationCategory      the <code>DccAnnotationCategory</code>
     * @return <code>true</code> if <code>annotationCategoryAsString</code> matches the given <code>DccAnnotationCategory</code> name,
     *         <code>false</code> otherwise
     */
    private boolean dccAnnotationCategoryNameMatch(final String annotationCategoryAsString,
                                                   final DccAnnotationCategory dccAnnotationCategory) {

        return dccAnnotationCategory.getCategoryName() != null
                && dccAnnotationCategory.getCategoryName().equals(annotationCategoryAsString);
    }

    /**
     * Validate the given annotationNoteAsString
     *
     * @param annotationNoteAsString the annotation note as a <code>String</code>
     */
    private void validateAnnotationNoteAsString(final String annotationNoteAsString) {

        if (StringUtils.isBlank(annotationNoteAsString)) {

            final StringBuilder errorMessage = new StringBuilder("Please provide an annotation note");
            throwWebApplicationException(errorMessage);
        }
    }

    /**
     * Return a list of active diseases for display
     *
     * @param activeDiseases the list of active diseases
     * @return a list of active diseases for display
     */
    private StringBuilder getActiveDiseasesDisplay(final List<Tumor> activeDiseases) {

        final StringBuilder result = new StringBuilder("Active diseases are: (Abbreviation | Description | Id)")
                .append(HTML_NEW_LINE)
                .append(HTML_NEW_LINE);

        for (final Tumor tumor : activeDiseases) {
            result
                    .append(tumor.getTumorName())
                    .append(" | ")
                    .append(tumor.getTumorDescription())
                    .append(" | ")
                    .append(tumor.getTumorId())
                    .append(HTML_NEW_LINE);
        }

        return result;
    }

    /**
     * Return a list of item types for display
     *
     * @param itemTypes the list of item types
     * @return a list of item types for display
     */
    private StringBuilder getItemTypesDisplay(final List<DccAnnotationItemType> itemTypes) {

        final StringBuilder result = new StringBuilder("Item types are: (Name | Description | Id)")
                .append(HTML_NEW_LINE)
                .append(HTML_NEW_LINE);

        for (final DccAnnotationItemType dccAnnotationItemType : itemTypes) {
            result
                    .append(dccAnnotationItemType.getItemTypeName())
                    .append(" | ")
                    .append(dccAnnotationItemType.getItemTypeDescription())
                    .append(" | ")
                    .append(dccAnnotationItemType.getItemTypeId())
                    .append(HTML_NEW_LINE);
        }

        return result;
    }

    /**
     * Return a list of annotation categories for display
     *
     * @param annotationCategories the list of annotation categories
     * @return a list of annotation categories for display
     */
    private StringBuilder getAnnotationCategoriesDisplay(final List<DccAnnotationCategory> annotationCategories) {

        final StringBuilder result = new StringBuilder("Annotation categories are: (Name | Id)")
                .append(HTML_NEW_LINE)
                .append(HTML_NEW_LINE);

        for (final DccAnnotationCategory dccAnnotationCategory : annotationCategories) {
            result
                    .append(dccAnnotationCategory.getCategoryName())
                    .append(" | ")
                    .append(dccAnnotationCategory.getCategoryId())
                    .append(HTML_NEW_LINE);
        }

        return result;
    }

    /**
     * Validate the given input according to the <code>AnnotationItemType</code> (passed by name) that it is supposed to match.
     * Throw a <code><WebApplicationException/code> if there is no match.
     *
     * @param input                     the input to validate
     * @param dccAnnotationItemTypeName the <code>DccAnnotationItemType</code> name that is expected
     */
    private void validateItem(final String input, final String dccAnnotationItemTypeName) {

        if (input == null || "".equals(input)) { //not using StringUtils.isBlank() here since whitespace should be validated as is
            //No annotation item type provided
            throwWebApplicationException(new StringBuilder("Please provide an item."));
        }

        boolean matchFound = false;
        final CommonBarcodeAndUUIDValidatorImpl commonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl();
        final StringBuilder noMatchErrorMessage = new StringBuilder("The item '")
                .append(input)
                .append("' does not match ");

        if ("Aliquot".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateAliquotBarcodeFormat(input);
            noMatchErrorMessage.append("an Aliquot barcode.");

        } else if ("Analyte".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateAnalyteBarcodeFormat(input);
            noMatchErrorMessage.append("an Analyte barcode.");

        } else if ("Patient".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validatePatientBarcodeFormat(input);
            noMatchErrorMessage.append("a Patient barcode.");

        } else if ("Portion".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validatePortionBarcodeFormat(input);
            noMatchErrorMessage.append("a Portion barcode.");

        } else if ("Sample".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateSampleBarcodeFormat(input);
            noMatchErrorMessage.append("a Sample barcode.");

        } else if ("Slide".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateSlideBarcodeFormat(input);
            noMatchErrorMessage.append("a Slide barcode.");

        } else if ("UUID".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateUUIDFormat(input);
            noMatchErrorMessage.append("an UUID barcode.");

        } else if ("Shipped Portion".equals(dccAnnotationItemTypeName)) {
            matchFound = commonBarcodeAndUUIDValidator.validateShipmentPortionBarcodeFormat(input);
            noMatchErrorMessage.append("a Shipped Portion barcode");

        } else {
            // Item type not supported

            final StringBuilder notSupportedErrorMessage = new StringBuilder("Item type '")
                    .append(dccAnnotationItemTypeName)
                    .append("' is not supported.");

            final StringBuilder expectedValuesDisplay = new StringBuilder("Supported item types are:")
                    .append(HTML_NEW_LINE)
                    .append(HTML_NEW_LINE);

            final String[] supportedItemTypes = {"Aliquot", "Analyte", "Patient", "Portion", "Sample", "Slide", "UUID"};
            for (final String supportedItemType : supportedItemTypes) {
                expectedValuesDisplay
                        .append(supportedItemType)
                        .append(HTML_NEW_LINE);
            }

            throwWebApplicationException(notSupportedErrorMessage, expectedValuesDisplay);
        }

        if (!matchFound) {
            throwWebApplicationException(noMatchErrorMessage);
        }
    }

    /**
     * Throw a <code>WebApplicationException</code> with:
     * - a <code>PRECONDITION_FAILED</code> HTTP status code
     * - the given error message appended with the expected values (if it is null, nothing will be appended)
     *
     * @param errorMessage          the start of the error message
     * @param expectedValuesDisplay the expected values to append to the error message
     */
    private void throwWebApplicationException(final StringBuilder errorMessage, final StringBuilder expectedValuesDisplay) {

        if (expectedValuesDisplay != null) {

            errorMessage
                    .append(HTML_NEW_LINE)
                    .append(HTML_NEW_LINE)
                    .append(expectedValuesDisplay);
        }

        throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, errorMessage.toString()));
    }

    /**
     * Throw a <code>WebApplicationException</code> with:
     * - a <code>PRECONDITION_FAILED</code> HTTP status code
     * - the given error message
     *
     * @param errorMessage the start of the error message
     */
    private void throwWebApplicationException(final StringBuilder errorMessage) {
        throwWebApplicationException(errorMessage, null);
    }

    /*
     * Getter / Setter
     */

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    public SecurityUtil getSecurityUtil() {
        return securityUtil;
    }

    public void setSecurityUtil(final SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}
