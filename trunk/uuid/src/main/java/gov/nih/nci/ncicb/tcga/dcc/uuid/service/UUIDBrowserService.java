/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.ExtJsFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * interface representing the service layer of the UUID browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface UUIDBrowserService {

    /**
     * get list of center bean
     *
     * @return list of centers
     */
    public List<Center> getCenters();

    /**
     * get list of center bean with bcr code
     *
     * @return list of centers
     */
    public List<Center> getCentersWithBCRCode();

    /**
     * get list of disease bean
     *
     * @return list of diseases
     */
    public List<Tumor> getDiseases();

    /**
     * get list of portion analytes bean
     *
     * @return list of portion analytes
     */
    public List<PortionAnalyte> getPortionAnalytes();

    /**
     * get list of sample type bean
     *
     * @return list of sample types
     */
    public List<SampleType> getSampleTypes();

    /**
     * get list of platform beans
     *
     * @return list of platforms
     */
    public List<Platform> getPlatforms();

    /**
     * get llist of tissue source sites beans
     *
     * @return list of tissue source sites
     */
    public List<TissueSourceSite> getTissueSourceSites();

    /**
     * get lilst of uuid types beans
     *
     * @return list of uuid types
     */
    public List<UUIDType> getUuidTypes();

    /**
     * get a list of uuid browser bean from a barcode
     *
     * @param barcode
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetadataFromBarcode(final String barcode);

    /**
     * Returns list of barcodes that exists in the database
     *
     * @param barcodes list of barcodes to validate
     * @return list of barcodes that exists in the database
     */

    public List<String> getExistingBarcodes(final List<String> barcodes);

    /**
     * get a list of uuid browser bean from a uuid
     *
     * @param uuid
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetadataFromUUID(final String uuid);

    /**
     * get a list of uuid browser bean from a list of uuids
     *
     * @param uuids
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetadataFromMultipleUUID(final List<String> uuids);

    /**
     * get a list of uuid browser bean from a list of barcodes
     *
     * @param barcodes
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetaDataFromMultipleBarcode(List<String> barcodes);

    /**
     * get comparator for uuidBrowser columns
     *
     * @return map of column/comparators
     */
    public Map<String, Comparator> getUUIDBrowserComparator();

    /**
     * build list of BiospecimenMetadata from a list of uuids and/or barcodes
     *
     * @param input      list of uuids and/or barcodes
     * @param hasBarcode
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> processMultipleBiospecimenMetadata(final List<String> input,
                                                                        final Boolean hasBarcode,
                                                                        final Boolean allBarcode);

    /**
     * parse a text file for uuids and/or barcodes
     *
     * @param inputStream
     * @return map of uuids and/or barcodes list with boolean of barcode existence in the input file
     * @throws IOException
     */
    public Map<String, Object> parseUploadFile(final InputStream inputStream) throws IOException;

    /**
     * get all uuidBrowser available
     *
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getAllBiospecimenMetadata();

    /**
     * search uuidBrowser bean from the search criteria
     *
     * @param list
     * @param uuidRadio
     * @param uuid
     * @param barcodeRadio
     * @param barcode
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getSearchBiospecimenMetadataList(final List<BiospecimenMetaData> list,
                                                                      final String uuidRadio,
                                                                      final String uuid, final String barcodeRadio,
                                                                      final String barcode);

    /**
     * get all the parent uuids from a uuid
     *
     * @param uuid
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getAllBiospecimenMetadataParents(final String uuid);

    /**
     * get all the parent uuids from a uuid
     *
     * @param uuid
     * @return list of BiospecimenMetaData
     */
    public List<BiospecimenMetaData> getAllBiospecimenMetadataParentsNoAliquot(final String uuid);

    /**
     * get all uuid types for web use
     *
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllUuidTypes();

    /**
     * get all diseases for web use
     *
     * @param column
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllDiseases(final String column);

    /**
     * get all platform for web use
     *
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllPlatforms();

    /**
     * get all center for web use
     *
     * @param column
     * @return list of ExtJsFilter
     */
    public Set<ExtJsFilter> getAllCenters(final String column);

    /**
     * get all bcr for web use
     *
     * @param column
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllBCRs(final String column);

    /**
     * get all sample type for web use
     *
     * @param column
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllSampleTypes(final String column);

    /**
     * get all portion analytes type for web use
     *
     * @param column
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllPortionAnalytes(final String column);

    /**
     * get all tissue source sites for web use
     *
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getAllTissueSourceSites();

    /**
     * get the filtered metadata search according to criteria
     *
     * @param list
     * @param uuidTypeCombo
     * @param platformCombo
     * @param participant
     * @param batch
     * @param diseaseCombo
     * @param sampleCombo
     * @param vial
     * @param portion
     * @param analyteCombo
     * @param plate
     * @param bcrCombo
     * @param tissueSourceSite
     * @param centerCombo
     * @param centerTypeCombo
     * @param updateAfter
     * @param updateBefore
     * @param createAfter
     * @param createBefore
     * @param slide
     * @return list of UUIDBrowser
     */
    public List<BiospecimenMetaData> getBiospecimenMetadataList(final List<BiospecimenMetaData> list,
                                                                final List<String> uuidTypeCombo, final List<String> platformCombo,
                                                                final List<String> participant, final List<String> batch,
                                                                final List<String> diseaseCombo, final List<String> sampleCombo,
                                                                final List<String> vial, final List<String> portion,
                                                                final List<String> analyteCombo, final List<String> plate,
                                                                final List<String> bcrCombo, final List<String> tissueSourceSite,
                                                                final List<String> centerCombo, final List<String> centerTypeCombo,
                                                                final String updateAfter, final String updateBefore,
                                                                final String createAfter, final String createBefore,
                                                                final List<String> slide);

    /**
     * process search uuidBrowser with criteria from the first tab of the web view
     *
     * @param searchParams
     * @param uuidBrowserList
     * @return list of uuidBrowser
     */
    public List<BiospecimenMetaData> processFirstTab(final String searchParams, final List<BiospecimenMetaData> uuidBrowserList);

    /**
     * process search uuidBrowser with criteria from the second tab of the web view
     *
     * @param searchParams
     * @param uuidBrowserList
     * @return list of uuidBrowser
     */
    public List<BiospecimenMetaData> processSecondTab(final String searchParams, final List<BiospecimenMetaData> uuidBrowserList);

    /**
     * build web service url from filter
     *
     * @param request
     * @param filter
     * @param type
     * @return url
     */
    public String buildWSUrl(final HttpServletRequest request, final String filter, String type);

    /**
     * get sample children uuids
     *
     * @param sampleUuid
     * @param source
     * @return list of uuidbrowser
     */
    public List<BiospecimenMetaData> getSampleChildrenList(final String sampleUuid, final List<BiospecimenMetaData> source);

    /**
     * get portion children uuids
     *
     * @param portionUuid
     * @param source
     * @return list of uuidbrowser
     */
    public List<BiospecimenMetaData> getPortionChildrenList(final String portionUuid, final List<BiospecimenMetaData> source);

    /**
     * get analyte & slide children uuids
     *
     * @param analyteSlideUuid
     * @param source
     * @return list of uuidbrowser
     */
    public List<BiospecimenMetaData> getAnalyteSlideChildrenList(final String analyteSlideUuid, final List<BiospecimenMetaData> source);

}