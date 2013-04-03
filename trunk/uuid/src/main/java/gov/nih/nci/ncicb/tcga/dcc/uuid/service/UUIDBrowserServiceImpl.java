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
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.uuid.dao.UUIDBrowserDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ABBREVIATION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ANALYTE_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ANALYTE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BARCODE_FIELD;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BARCODE_SEARCH_RADIO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BCR_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER_TYPE_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CODE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CREATE_DATE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DATE_FORMAT_US_STRING;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DISEASE_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DOMAIN;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ELEMENT_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.NAME;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PARTICIPANT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PARTICIPANT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATFORM_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PORTION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PORTION_ID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.RECEIVING_CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SAMPLE_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SAMPLE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SHORT_LETTER_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SLIDE_LAYER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.STUDY_NAME;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TISSUE_SOURCE_SITE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TRUE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TSS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATED_AFTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATED_BEFORE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATE_AFTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATE_BEFORE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATE_DATE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_FIELD;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_SEARCH_RADIO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_TYPE_COMBO;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.VIAL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.VIAL_ID;

/**
 * Service layer implementation of the uuid browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class UUIDBrowserServiceImpl implements UUIDBrowserService {

    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    private UUIDBrowserDAO uuidBrowserDAO;
    @Autowired
    private UUIDCommonService uuidCommonService;
    @Autowired
    private UUIDService uuidService;

    @PostConstruct
    private void initAllUUIDBrowserCache() {
        getAllBiospecimenMetadata();
        getCenters();
        getDiseases();
        getPortionAnalytes();
        getSampleTypes();
        getPlatforms();
        getTissueSourceSites();
        getUuidTypes();
    }

    @Override
    public List<Center> getCenters() {
        return uuidBrowserDAO.getCenters();
    }

    @Override
    public List<Center> getCentersWithBCRCode() {
        return uuidBrowserDAO.getCentersWithBCRCode();
    }

    @Override
    public List<Tumor> getDiseases() {
        return uuidBrowserDAO.getDiseases();
    }

    @Override
    public List<PortionAnalyte> getPortionAnalytes() {
        return uuidBrowserDAO.getPortionAnalytes();
    }

    @Override
    public List<SampleType> getSampleTypes() {
        return uuidBrowserDAO.getSampleTypes();
    }

    @Override
    public List<Platform> getPlatforms() {
        return uuidBrowserDAO.getPlatforms();
    }

    @Override
    public List<TissueSourceSite> getTissueSourceSites() {
        return uuidBrowserDAO.getTissueSourceSites();
    }

    @Override
    public List<UUIDType> getUuidTypes() {
        return uuidBrowserDAO.getUuidTypes();
    }

    @Override
    public Map<String, Object> parseUploadFile(final InputStream inputStream) throws IOException {
        final Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        final List<String> uuidList = new ArrayList<String>();
        Boolean hasBarcode = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;
            int lineNumber = 1;
            int barcodeNumber = 1;
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("TCGA")) {
                    hasBarcode = true;
                    ++barcodeNumber;
                    uuidList.add(strLine.trim());
                } else if (uuidService.isValidUUID(strLine.trim())) {
                    uuidList.add(strLine.trim());
                } else {
                    throw new IOException("The uuid " + strLine + " at line: " + lineNumber + " is not valid.");
                }
                ++lineNumber;
            }
            resMap.put("uuidList", uuidList);
            resMap.put("hasBarcode", hasBarcode);
            resMap.put("allBarcode", lineNumber == barcodeNumber);
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return resMap;
    }

    @Override
    public List<BiospecimenMetaData> getAllBiospecimenMetadata() {
        return uuidBrowserDAO.getUUIDRows();
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetadataFromBarcode(final String barcode) {
        return uuidBrowserDAO.getBiospecimenMetaDataRowsFromBarcode(barcode);
    }

    @Override
    public List<String> getExistingBarcodes(final List<String> barcodes) {
        return uuidBrowserDAO.getExistingBarcodes(barcodes);
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetadataFromUUID(final String uuid) {
        return uuidBrowserDAO.getBiospecimenMetaDataRowsFromUUID(uuid);
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetadataFromMultipleUUID(final List<String> uuids) {
        return uuidBrowserDAO.getBiospecimenMetaDataRowsFromMultipleUUID(uuids);
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetaDataFromMultipleBarcode(final List<String> barcodes) {
        return uuidBrowserDAO.getBiospecimenMetaDataRowsFromMultipleBarcode(barcodes);
    }

    @Override
    public Map<String, Comparator> getUUIDBrowserComparator() {
        return uuidCommonService.getComparatorMap(BiospecimenMetaData.class, UUID_BROWSER_COLS);
    }

    @Override
    public List<BiospecimenMetaData> processMultipleBiospecimenMetadata(final List<String> input,
                                                                        final Boolean hasBarcode,
                                                                        final Boolean allBarcode) {
        if (input != null && input.size() > 0) {
            if (allBarcode) {
                return getBiospecimenMetaDataFromMultipleBarcode(input);
            }
            if (hasBarcode) {
                final Set<BiospecimenMetaData> biospecimenMetaDataSet = new HashSet<BiospecimenMetaData>();
                final Map<String, List<String>> resMap = filterUUIDFromBarcodeList(input);
                biospecimenMetaDataSet.addAll(getBiospecimenMetadataFromMultipleUUID(resMap.get("uuidList")));
                biospecimenMetaDataSet.addAll(getBiospecimenMetaDataFromMultipleBarcode(resMap.get("barcodeList")));
                return new ArrayList(biospecimenMetaDataSet);
            } else {
                return getBiospecimenMetadataFromMultipleUUID(input);
            }
        } else {
            return getAllBiospecimenMetadata();
        }
    }

    /**
     * separate barcode from uuid in the input list of string
     *
     * @param list list of string
     * @return map of list of string
     */
    protected Map<String, List<String>> filterUUIDFromBarcodeList(final List<String> list) {
        final Map<String, List<String>> resMap = new LinkedHashMap<String, List<String>>();
        final List<String> uuidList = new ArrayList<String>();
        final List<String> barcodeList = new ArrayList<String>();
        for (final String str : list) {
            if (str != null && str.startsWith("TCGA")) {
                barcodeList.add(str);
            } else if (str != null) {
                uuidList.add(str);
            }
        }

        resMap.put("uuidList", uuidList);
        resMap.put("barcodeList", barcodeList);
        return resMap;
    }

    @Override
    public List<BiospecimenMetaData> getSearchBiospecimenMetadataList(final List<BiospecimenMetaData> list, final String uuidRadio,
                                                                      final String uuid, final String barcodeRadio, final String barcode) {
        final StringBuilder strLog = new StringBuilder();
        strLog.append("Search used: uuidBrowser:").append(uuid).append(" barcode:");
        logger.debug(strLog);
        final List<Predicate> uuidBrowserPredicateList = new LinkedList<Predicate>();
        if (TRUE.equalsIgnoreCase(uuidRadio)) {
            uuidBrowserPredicateList.add(selectUUID(uuid, true));
        }
        if (TRUE.equalsIgnoreCase(barcodeRadio)) {
            uuidBrowserPredicateList.add(commonBarcodePredicate(barcode, true));
        }
        final Predicate uuidBrowserPredicates = PredicateUtils.allPredicate(uuidBrowserPredicateList);
        List<BiospecimenMetaData> fList = (List<BiospecimenMetaData>) CollectionUtils.select(list, uuidBrowserPredicates);
        return fList;
    }

    protected boolean getBarcodeRegexMatch(final String barcode, final String matchTest) {
        final String regex = barcode.replace("*", "[\\w-]*");
        final Matcher m = Pattern.compile(regex).matcher(matchTest);
        return m.matches();
    }

    private Predicate commonBarcodePredicate(final String barcode, final boolean includeBlank) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                if (StringUtils.isBlank(barcode)) {
                    return includeBlank;
                }
                if (barcode.contains("*")) {
                    return getBarcodeRegexMatch(barcode, ((BiospecimenMetaData) o).getBarcode());
                } else {
                    return barcode.equalsIgnoreCase(((BiospecimenMetaData) o).getBarcode());
                }
            }
        };
    }

    public Predicate selectUUID(final String uuid, final boolean includeBlank) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                if (StringUtils.isBlank(uuid)) {
                    return includeBlank;
                }
                return uuid.equalsIgnoreCase(((BiospecimenMetaData) o).getUuid());
            }
        };
    }

    public Predicate selectParentUUID(final String uuid, final boolean includeBlank) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                if (StringUtils.isBlank(uuid)) {
                    return includeBlank;
                }
                return uuid.equalsIgnoreCase(((BiospecimenMetaData) o).getParentUUID());
            }
        };
    }

    @Override
    public List<BiospecimenMetaData> getAllBiospecimenMetadataParents(final String uuid) {
        final List<BiospecimenMetaData> res = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> source = getAllBiospecimenMetadata();
        final List<BiospecimenMetaData> participantList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> sampleList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> portionList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> analyteSlideList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> aliquotList = new LinkedList<BiospecimenMetaData>();
        final String participantUUID = getRootParticipantUUID(uuid);
        processParticipantUUID(participantUUID, source, participantList, sampleList, portionList, analyteSlideList,
                aliquotList);
        res.addAll(participantList);
        res.addAll(sampleList);
        res.addAll(portionList);
        res.addAll(analyteSlideList);
        res.addAll(aliquotList);
        return res;
    }

    @Override
    public List<BiospecimenMetaData> getAllBiospecimenMetadataParentsNoAliquot(final String uuid) {
        final List<BiospecimenMetaData> res = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> source = getAllBiospecimenMetadata();
        final List<BiospecimenMetaData> participantList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> sampleList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> portionList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> analyteSlideList = new LinkedList<BiospecimenMetaData>();
        final String participantUUID = getRootParticipantUUID(uuid);
        processParticipantUUID(participantUUID, source, participantList, sampleList, portionList, analyteSlideList);
        res.addAll(participantList);
        res.addAll(sampleList);
        res.addAll(portionList);
        res.addAll(analyteSlideList);
        return res;
    }

    @Override
    public List<ExtJsFilter> getAllUuidTypes() {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        String uuidTypeID, uuidTypeText;

        final List<String> uuidTypeWithSpecialStatus = Arrays.asList(
                new String[]{"Radiation", "Drug", "Examination", "Surgery"});

        for (UUIDType uuidType : uuidBrowserDAO.getUuidTypes()) {
            uuidTypeID = uuidType.getUuidType();
            uuidTypeText = uuidType.getUuidType();
            if (uuidTypeWithSpecialStatus.contains(uuidTypeText)) {
                uuidTypeText = "(" + uuidType.getUuidType() + ")";
            }
            resList.add(new ExtJsFilter(uuidTypeID, uuidTypeText));
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllDiseases(final String column) {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (Tumor disease : uuidBrowserDAO.getDiseases()) {
            if (ABBREVIATION.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(disease.getTumorName(), disease.getTumorName()));
            } else if (STUDY_NAME.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(disease.getTumorName(), disease.getTumorDescription()));
            }
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllPlatforms() {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (Platform platform : uuidBrowserDAO.getPlatforms()) {
            resList.add(new ExtJsFilter(platform.getPlatformAlias(), platform.getPlatformAlias()));
        }
        return resList;
    }

    @Override
    public Set<ExtJsFilter> getAllCenters(final String column) {
        Set<ExtJsFilter> resList = new LinkedHashSet<ExtJsFilter>();
        for (final Center center : uuidBrowserDAO.getCentersWithBCRCode()) {
            if (ABBREVIATION.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(center.getCenterName(), center.getShortName()));
            } else if (NAME.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(center.getCenterName(), center.getCenterDisplayName()));
            } else if (DOMAIN.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(center.getCenterName(), center.getCenterName()));
            } else if (CODE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(center.getCenterName() + "~" + center.getBcrCenterId(),
                        center.getBcrCenterId()));
            }
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllBCRs(final String column) {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (final Center center : uuidBrowserDAO.getCenters()) {
            if (BCR.equalsIgnoreCase(center.getCenterType())) {
                if (ABBREVIATION.equalsIgnoreCase(column)) {
                    resList.add(new ExtJsFilter(center.getCenterDisplayText(), center.getShortName()));
                } else if (NAME.equalsIgnoreCase(column)) {
                    resList.add(new ExtJsFilter(center.getCenterDisplayText(), center.getCenterDisplayName()));
                } else if (DOMAIN.equalsIgnoreCase(column)) {
                    resList.add(new ExtJsFilter(center.getCenterDisplayText(), center.getCenterName()));
                }
            }
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllSampleTypes(final String column) {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (SampleType sampleType : uuidBrowserDAO.getSampleTypes()) {
            if (TYPE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(sampleType.getDefinition(), sampleType.getDefinition()));
            } else if (CODE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(sampleType.getDefinition(), sampleType.getSampleTypeCode()));
            } else if (SHORT_LETTER_CODE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(sampleType.getDefinition(), sampleType.getShortLetterCode()));
            }
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllPortionAnalytes(final String column) {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (PortionAnalyte portionAnalyte : uuidBrowserDAO.getPortionAnalytes()) {
            if (TYPE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(portionAnalyte.getDefinition(), portionAnalyte.getDefinition()));
            } else if (CODE.equalsIgnoreCase(column)) {
                resList.add(new ExtJsFilter(portionAnalyte.getDefinition(), portionAnalyte.getPortionAnalyteCode()));
            }
        }
        return resList;
    }

    @Override
    public List<ExtJsFilter> getAllTissueSourceSites() {
        List<ExtJsFilter> resList = new LinkedList<ExtJsFilter>();
        for (TissueSourceSite tss : uuidBrowserDAO.getTissueSourceSites()) {
            resList.add(new ExtJsFilter(tss.getTissueSourceSiteId(), tss.getName()));
        }
        return resList;
    }

    @Override
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
                                                                final List<String> slideLayer) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_STRING);
        final StringBuilder strLog = new StringBuilder();
        strLog.append("Metadata used: uuidTypeCombo:").append(uuidTypeCombo).append(" platformCombo:")
                .append(platformCombo).append(" participant:").append(participant)
                .append(" batch:").append(batch).append(" diseaseCombo:").append(diseaseCombo)
                .append(" sampleCombo:").append(sampleCombo).append(" vial:").append(vial)
                .append(" portion:").append(portion).append(" analyteCombo:").append(analyteCombo)
                .append(" plate:").append(plate).append(" bcrCombo:").append(bcrCombo)
                .append(" tissueSourceSite:").append(tissueSourceSite).append(" centerCombo:").append(centerCombo)
                .append(" centerTypeCombo:").append(centerTypeCombo).append(" updateAfter:").append(updateAfter)
                .append(" updateBefore:").append(updateBefore).append(" createAfter:").append(createAfter)
                .append(" createBefore:").append(createBefore).append(" slideLayer:").append(slideLayer);
        logger.debug(strLog);
        final List<Predicate> metadataPredicateList = new LinkedList<Predicate>();
        metadataPredicateList.add(uuidCommonService.genDatePredicate(BiospecimenMetaData.class, UPDATE_DATE, false, updateAfter, dateFormat));
        metadataPredicateList.add(uuidCommonService.genDatePredicate(BiospecimenMetaData.class, UPDATE_DATE, true, updateBefore, dateFormat));
        metadataPredicateList.add(uuidCommonService.genDatePredicate(BiospecimenMetaData.class, CREATE_DATE, false, createAfter, dateFormat));
        metadataPredicateList.add(uuidCommonService.genDatePredicate(BiospecimenMetaData.class, CREATE_DATE, true, createBefore, dateFormat));
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, uuidTypeCombo, UUID_TYPE, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, platformCombo, PLATFORM, true);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, participant, PARTICIPANT_ID, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, batch, BATCH, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, diseaseCombo, DISEASE, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, sampleCombo, SAMPLE_TYPE, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, vial, VIAL_ID, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, portion, PORTION_ID, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, analyteCombo, ANALYTE_TYPE, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, plate, PLATE_ID, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, bcrCombo, BCR, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, tissueSourceSite, TISSUE_SOURCE_SITE, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, slideLayer, SLIDE_LAYER, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, receivingCenterCombo(centerCombo,
                centerTypeCombo), RECEIVING_CENTER, false);
        uuidCommonService.genORPredicateList(BiospecimenMetaData.class, metadataPredicateList, getCenterCodeFromCenterCombo(centerCombo),
                "centerCode", false);
        final Predicate uuidBrowserPredicates = PredicateUtils.allPredicate(metadataPredicateList);
        List<BiospecimenMetaData> fList = (List<BiospecimenMetaData>) CollectionUtils.select(list, uuidBrowserPredicates);
        return fList;
    }

    /**
     * get the center code from the values of the centercombo for filtering
     *
     * @param centerCombo list of string
     * @return list of string
     */
    protected List<String> getCenterCodeFromCenterCombo(final List<String> centerCombo) {
        final Set<String> res = new HashSet<String>();
        if (centerCombo == null) {
            return null;
        }
        for (final String center : centerCombo) {
            if (center != null && center.contains("~")) {
                final String centerCode = center.substring(center.indexOf("~") + 1, center.length());
                res.add(centerCode);
            }
        }
        return res.size() > 0 ? new ArrayList<String>(res) : null;
    }

    /**
     * takes the list of center and centerType as input from the controller
     * and return a center value formatted to be understood by the filter predicates
     *
     * @param centerCombo
     * @param centerTypeCombo
     * @return list of center in the format centerName (centerType)
     */
    protected List<String> receivingCenterCombo(final List<String> centerCombo, final List<String> centerTypeCombo) {
        List<String> res = new LinkedList<String>();
        if (centerCombo == null) {
            return null;
        }
        for (String center : centerCombo) {
            if (center != null && center.contains("~")) {
                //We have a bcr center id code selection
                center = center.substring(center.indexOf("~") + 1, center.length());
                for (final Center c : getCentersWithBCRCode()) {
                    if (center.equalsIgnoreCase(c.getBcrCenterId())) {
                        if (centerTypeCombo != null) {
                            for (final String type : centerTypeCombo) {
                                if (!type.equalsIgnoreCase(c.getCenterType())) {
                                    /* We have a conflict of centerType, we return a fake
                                error center that does not exist and will return no
                                values in the search */
                                    res.add("Error centerType conflict");
                                } else {
                                    res.add(c.getCenterDisplayText());
                                }
                            }
                        } else {
                            res.add(c.getCenterDisplayText());
                        }
                        //the bcr center id is unique so we can stop the loop
                        break;
                    }
                }
            } else {
                if (centerTypeCombo == null || centerTypeCombo.size() == 0) {
                    res.add(center + " (CGCC)");
                    res.add(center + " (GSC)");
                } else {
                    for (final String type : centerTypeCombo) {
                        res.add(center + " (" + type + ")");
                    }
                }
            }
        }
        return res;
    }

    protected String getRootParticipantUUID(final String uuid) {
        String res = null;
        final List<BiospecimenMetaData> resList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> source = getAllBiospecimenMetadata();
        CollectionUtils.select(source, selectUUID(uuid, false), resList);
        for (final BiospecimenMetaData uuidBrowse : resList) {
            if (uuidBrowse != null) {
                res = uuidBrowse.getParentUUID();
            }
        }
        if (res == null) {
            return uuid;
        } else {
            res = getRootParticipantUUID(res);
        }
        return res;
    }

    private void processParticipantUUID(final String uuid, final List<BiospecimenMetaData> source, final List<BiospecimenMetaData> participantList,
                                        final List<BiospecimenMetaData> sampleList, final List<BiospecimenMetaData> portionList,
                                        final List<BiospecimenMetaData> analyteSlideList, final List<BiospecimenMetaData> aliquotList) {
        CollectionUtils.select(source, selectUUID(uuid, false), participantList);
        for (BiospecimenMetaData uuidParticipant : participantList) {
            CollectionUtils.select(source, selectParentUUID(uuidParticipant.getUuid(), false), sampleList);
        }
        if (sampleList != null) {
            for (final BiospecimenMetaData uuidSample : sampleList) {
                CollectionUtils.select(source, selectParentUUID(uuidSample.getUuid(), false), portionList);
            }
            if (portionList != null) {
                for (final BiospecimenMetaData uuidPortion : portionList) {
                    CollectionUtils.select(source, selectParentUUID(uuidPortion.getUuid(), false), analyteSlideList);
                }
                if (analyteSlideList != null) {
                    for (final BiospecimenMetaData uuidAnalyteSlide : analyteSlideList) {
                        CollectionUtils.select(source, selectParentUUID(uuidAnalyteSlide.getUuid(), false), aliquotList);
                    }
                }
            }
        }
    }

    private void processParticipantUUID(final String uuid, final List<BiospecimenMetaData> source, final List<BiospecimenMetaData> participantList,
                                        final List<BiospecimenMetaData> sampleList, final List<BiospecimenMetaData> portionList,
                                        final List<BiospecimenMetaData> analyteSlideList) {
        CollectionUtils.select(source, selectUUID(uuid, false), participantList);
        for (BiospecimenMetaData uuidParticipant : participantList) {
            CollectionUtils.select(source, selectParentUUID(uuidParticipant.getUuid(), false), sampleList);
        }
        if (sampleList != null) {
            for (final BiospecimenMetaData uuidSample : sampleList) {
                CollectionUtils.select(source, selectParentUUID(uuidSample.getUuid(), false), portionList);
            }
            if (portionList != null) {
                for (final BiospecimenMetaData uuidPortion : portionList) {
                    CollectionUtils.select(source, selectParentUUID(uuidPortion.getUuid(), false), analyteSlideList);
                }
            }
        }
    }

    @Override
    public List<BiospecimenMetaData> processSecondTab(final String searchParams, final List<BiospecimenMetaData> uuidBrowserList) {
        List<String> uuidTypeCombo, platformCombo, participant, batch, diseaseCombo, sampleCombo, vial, portion,
                analyteCombo, plate, bcrCombo, tissueSourceSite, centerCombo, centerTypeCombo;
        String updateAfter, updateBefore;
        List<BiospecimenMetaData> uuidBrowserSearchList;
        uuidTypeCombo = uuidCommonService.processJsonMultipleFilter(UUID_TYPE_COMBO, searchParams);
        platformCombo = uuidCommonService.processJsonMultipleFilter(PLATFORM_COMBO, searchParams);
        participant = uuidCommonService.processJsonMultipleFilter(PARTICIPANT, searchParams);
        batch = uuidCommonService.processJsonMultipleFilter(BATCH, searchParams);
        diseaseCombo = uuidCommonService.processJsonMultipleFilter(DISEASE_COMBO, searchParams);
        sampleCombo = uuidCommonService.processJsonMultipleFilter(SAMPLE_COMBO, searchParams);
        vial = uuidCommonService.processJsonMultipleFilter(VIAL, searchParams);
        portion = uuidCommonService.processJsonMultipleFilter(PORTION, searchParams);
        analyteCombo = uuidCommonService.processJsonMultipleFilter(ANALYTE_COMBO, searchParams);
        plate = uuidCommonService.processJsonMultipleFilter(PLATE, searchParams);
        bcrCombo = uuidCommonService.processJsonMultipleFilter(BCR_COMBO, searchParams);
        tissueSourceSite = uuidCommonService.processJsonMultipleFilter(TISSUE_SOURCE_SITE, searchParams);
        centerCombo = uuidCommonService.processJsonMultipleFilter(CENTER_COMBO, searchParams);
        centerTypeCombo = uuidCommonService.processJsonMultipleFilter(CENTER_TYPE_COMBO, searchParams);
        updateAfter = uuidCommonService.processJsonSingleFilter(UPDATE_AFTER, searchParams);
        updateBefore = uuidCommonService.processJsonSingleFilter(UPDATE_BEFORE, searchParams);
        uuidBrowserSearchList = getBiospecimenMetadataList(uuidBrowserList, uuidTypeCombo,
                platformCombo, participant, batch, diseaseCombo, sampleCombo, vial, portion, analyteCombo, plate, bcrCombo,
                tissueSourceSite, centerCombo, centerTypeCombo, updateAfter, updateBefore, null, null, null);
        return uuidBrowserSearchList;
    }

    @Override
    public String buildWSUrl(final HttpServletRequest request, final String filter, final String type) {
        final StringBuilder url = new StringBuilder(request.getContextPath());
        url.append("/uuidws/metadata/" + type + "?");
        addParamToUrl(url, ELEMENT_TYPE, uuidCommonService.breakoutJsonFilter(UUID_TYPE_COMBO, filter));
        addParamToUrl(url, PLATFORM, uuidCommonService.breakoutJsonFilter(PLATFORM_COMBO, filter));
        addParamToUrl(url, PARTICIPANT, uuidCommonService.breakoutJsonFilter(PARTICIPANT, filter));
        addParamToUrl(url, BATCH, uuidCommonService.breakoutJsonFilter(BATCH, filter));
        addParamToUrl(url, DISEASE, uuidCommonService.breakoutJsonFilter(DISEASE_COMBO, filter));
        addParamToUrl(url, SAMPLE_TYPE, getSampleTypeCode(uuidCommonService.breakoutJsonFilter(SAMPLE_COMBO, filter)));
        addParamToUrl(url, VIAL, uuidCommonService.breakoutJsonFilter(VIAL, filter));
        addParamToUrl(url, PORTION, uuidCommonService.breakoutJsonFilter(PORTION, filter));
        addParamToUrl(url, ANALYTE_TYPE, getAnalyteTypeCode(uuidCommonService.breakoutJsonFilter(ANALYTE_COMBO, filter)));
        addParamToUrl(url, PLATE, uuidCommonService.breakoutJsonFilter(PLATE, filter));
        addParamToUrl(url, BCR, trimBCR(uuidCommonService.breakoutJsonFilter(BCR_COMBO, filter)));
        addParamToUrl(url, TSS, uuidCommonService.breakoutJsonFilter(TISSUE_SOURCE_SITE, filter));
        addParamToUrl(url, CENTER, filterCenterBcrCode(filter));
        addParamToUrl(url, CENTER_TYPE, uuidCommonService.breakoutJsonFilter(CENTER_TYPE_COMBO, filter));
        addParamToUrl(url, UPDATED_AFTER, uuidCommonService.breakoutJsonFilter(UPDATE_AFTER, filter));
        addParamToUrl(url, UPDATED_BEFORE, uuidCommonService.breakoutJsonFilter(UPDATE_BEFORE, filter));
        final String res = url.toString();
        return res.substring(0, res.length() - 1);
    }

    protected String filterCenterBcrCode(final String filter) {
        final StringBuilder res = new StringBuilder();
        final List<String> centers = uuidCommonService.processJsonMultipleFilter(CENTER_COMBO, filter);
        if (centers == null) {
            return null;
        }
        for (final String center : centers) {
            if (center != null && center.contains("~")) {
                res.append(center.substring(center.indexOf("~") + 1, center.length())).append(SEPARATOR);
            } else {
                res.append(center).append(SEPARATOR);
            }
        }
        res.setLength(res.length() - 1);
        return res.toString();
    }

    protected String trimBCR(final String bcr) {
        if (bcr != null) {
            return bcr.replace(" (BCR)", "");
        } else {
            return null;
        }
    }

    protected String getAnalyteTypeCode(final String analyteType) {
        final List<String> resList = new LinkedList<String>();
        if (!StringUtils.isBlank(analyteType)) {
            final String[] analyteTab = analyteType.split(SEPARATOR);
            for (int i = 0; i < analyteTab.length; i++) {
                final String tmp = analyteTab[i];
                for (final PortionAnalyte analyte : getPortionAnalytes()) {
                    if (tmp != null && tmp.equalsIgnoreCase(analyte.getDefinition())) {
                        resList.add(analyte.getPortionAnalyteCode());
                    }
                }
            }
        } else {
            return null;
        }
        return StringUtils.join(resList.toArray(), ",");
    }

    protected String getSampleTypeCode(final String sampleType) {
        final List<String> resList = new LinkedList<String>();
        if (!StringUtils.isBlank(sampleType)) {
            final String[] sampleTab = sampleType.split(SEPARATOR);
            for (int i = 0; i < sampleTab.length; i++) {
                final String tmp = sampleTab[i];
                for (SampleType sample : getSampleTypes()) {
                    if (tmp != null && tmp.equalsIgnoreCase(sample.getDefinition())) {
                        resList.add(sample.getSampleTypeCode());
                    }
                }
            }
        } else {
            return null;
        }
        return StringUtils.join(resList.toArray(), ",");
    }

    private void addParamToUrl(StringBuilder url, String paramName, String paramValue) {
        if (!StringUtils.isBlank(paramValue)) {
            url.append(paramName).append("=").append(paramValue).append("&");
        }
    }

    @Override
    public List<BiospecimenMetaData> processFirstTab(final String searchParams, final List<BiospecimenMetaData> uuidBrowserList) {
        String uuidSearchRadio, barcodeSearchRadio, uuid = null, barcode = null;
        List<BiospecimenMetaData> uuidBrowserSearchList;
        uuidSearchRadio = uuidCommonService.processJsonSingleFilter(UUID_SEARCH_RADIO, searchParams);
        barcodeSearchRadio = uuidCommonService.processJsonSingleFilter(BARCODE_SEARCH_RADIO, searchParams);
        if (TRUE.equalsIgnoreCase(uuidSearchRadio)) {
            uuid = uuidCommonService.processJsonSingleFilter(UUID_FIELD, searchParams);
        }
        if (TRUE.equalsIgnoreCase(barcodeSearchRadio)) {
            barcode = uuidCommonService.processJsonSingleFilter(BARCODE_FIELD, searchParams);
        }
        uuidBrowserSearchList = getSearchBiospecimenMetadataList(uuidBrowserList, uuidSearchRadio,
                uuid, barcodeSearchRadio, barcode);
        return uuidBrowserSearchList;
    }

    public List<BiospecimenMetaData> getSampleChildrenList(final String sampleUuid, final List<BiospecimenMetaData> source) {
        final List<BiospecimenMetaData> resList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> sampleList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> portionList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> analyteSlideList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> aliquotList = new LinkedList<BiospecimenMetaData>();
        CollectionUtils.select(source, selectUUID(sampleUuid, false), sampleList);
        for (BiospecimenMetaData uuidSample : sampleList) {
            CollectionUtils.select(source, selectParentUUID(uuidSample.getUuid(), false), portionList);
        }
        if (portionList != null) {
            for (BiospecimenMetaData uuidPortion : portionList) {
                CollectionUtils.select(source, selectParentUUID(uuidPortion.getUuid(), false), analyteSlideList);
            }
            if (analyteSlideList != null) {
                for (BiospecimenMetaData uuidAnalyteSlide : analyteSlideList) {
                    CollectionUtils.select(source, selectParentUUID(uuidAnalyteSlide.getUuid(), false), aliquotList);
                }
            }
        }
        resList.addAll(sampleList);
        resList.addAll(portionList);
        resList.addAll(analyteSlideList);
        resList.addAll(aliquotList);
        return resList;
    }

    public List<BiospecimenMetaData> getPortionChildrenList(final String portionUuid, final List<BiospecimenMetaData> source) {
        final List<BiospecimenMetaData> resList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> portionList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> analyteSlideList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> aliquotList = new LinkedList<BiospecimenMetaData>();
        CollectionUtils.select(source, selectUUID(portionUuid, false), portionList);
        for (BiospecimenMetaData uuidPortion : portionList) {
            CollectionUtils.select(source, selectParentUUID(uuidPortion.getUuid(), false), analyteSlideList);
        }
        if (analyteSlideList != null) {
            for (BiospecimenMetaData uuidAnalyteSlide : analyteSlideList) {
                CollectionUtils.select(source, selectParentUUID(uuidAnalyteSlide.getUuid(), false), aliquotList);
            }
        }
        resList.addAll(portionList);
        resList.addAll(analyteSlideList);
        resList.addAll(aliquotList);
        return resList;
    }

    public List<BiospecimenMetaData> getAnalyteSlideChildrenList(final String analyteSlideUuid, final List<BiospecimenMetaData> source) {
        final List<BiospecimenMetaData> resList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> analyteSlideList = new LinkedList<BiospecimenMetaData>();
        final List<BiospecimenMetaData> aliquotList = new LinkedList<BiospecimenMetaData>();
        CollectionUtils.select(source, selectUUID(analyteSlideUuid, false), analyteSlideList);
        for (BiospecimenMetaData uuidAnalyteSlide : analyteSlideList) {
            CollectionUtils.select(source, selectParentUUID(uuidAnalyteSlide.getUuid(), false), aliquotList);
        }
        resList.addAll(analyteSlideList);
        resList.addAll(aliquotList);
        return resList;
    }

}//End of Class
