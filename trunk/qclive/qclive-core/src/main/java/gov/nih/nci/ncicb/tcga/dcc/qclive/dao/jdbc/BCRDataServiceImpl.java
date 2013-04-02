package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation for BCRDataService queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class BCRDataServiceImpl implements BCRDataService{

    private BCRIDProcessor commonBcrIdProcessor;
    private BCRIDProcessor diseaseBcrIdProcessor;

    private ShippedBiospecimenQueries commonShippedBiospecimenQueries;
    private ShippedBiospecimenQueries diseaseShippedBiospecimenQueries;

    @Override
    public void handleShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens,
                                          final Integer shippedItemId,
                                          final Archive archive,
                                          final File xmlFile){
        addShippedBiospecimens(shippedBiospecimens,shippedItemId);
        final List<Long> shippedBiospecimenIds = new ArrayList<Long>();
        // make a list of just the IDs
        for (final ShippedBiospecimen shippedPortion : shippedBiospecimens) {
            shippedBiospecimenIds.add(shippedPortion.getShippedBiospecimenId());
        }

        // add file relationships in common and disease
        Long fileId = archive.getFilenameToIdToMap().get(xmlFile.getName());
        addShippedBiospecimensFileRelationship(shippedBiospecimenIds,  fileId);

        // add archive relationships in common and disease
        addShippedBiospecimensArchiveRelationship(shippedBiospecimenIds,  archive.getId());


    }


    @Override
    public void addBioSpecimenToFileAssociations( final List<BiospecimenToFile> biospecimenToFileList, final Tumor disease){
        commonBcrIdProcessor.addBioSpecimenToFileAssociations(biospecimenToFileList, disease);
        diseaseBcrIdProcessor.addBioSpecimenToFileAssociations(biospecimenToFileList, disease);

    }

    @Override
    public List<ShippedBiospecimen> findAllShippedPortionsInFile(final File file) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException{
        return commonBcrIdProcessor.findAllShippedPortionsInFile(file);
    }

    @Override
    public Integer getShippedItemId(final String shippedItemType){
        return commonShippedBiospecimenQueries.getShippedItemId(shippedItemType);
    }

    @Override
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens,Integer shippedItemId){
        commonShippedBiospecimenQueries.addShippedBiospecimens(shippedBiospecimens, shippedItemId);
        diseaseShippedBiospecimenQueries.addShippedBiospecimens(shippedBiospecimens, shippedItemId);
        addShippedBiospecimenElements(shippedBiospecimens);

    }

    @Override
    public void addShippedBiospecimensFileRelationship(final List<Long> biospecimenIds, final Long fileId){
        commonShippedBiospecimenQueries.addFileRelationships(biospecimenIds,  fileId);
        diseaseShippedBiospecimenQueries.addFileRelationships(biospecimenIds, fileId);

    }
    @Override
    public void addArchiveRelationship(final Long biospecimenId, final Long archiveId){
        commonShippedBiospecimenQueries.addArchiveRelationship(biospecimenId,  archiveId);
        diseaseShippedBiospecimenQueries.addArchiveRelationship(biospecimenId, archiveId);

    }

    @Override
    public void addShippedBiospecimensArchiveRelationship(final List<Long> biospecimenIds, final Long archiveId){
        commonShippedBiospecimenQueries.addArchiveRelationships(biospecimenIds,  archiveId);
        diseaseShippedBiospecimenQueries.addArchiveRelationships(biospecimenIds, archiveId);

    }

    @Override
    public List<String[]> findAllAliquotsInFile(final File file) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException{
        return commonBcrIdProcessor.findAllAliquotsInFile(file);
    }

    @Override
    public void handleAliquotBarcode(final BCRID bcrId,  final Archive archive, final File xmlFile) throws ParseException, UUIDException{
        final  int[] biospecimenToArchiveId = {-1};
        final Tumor disease = archive.getTheTumor();
        final Center center = archive.getTheCenter();

        commonBcrIdProcessor.storeBcrBarcode(bcrId, false,biospecimenToArchiveId, disease, center);
        diseaseBcrIdProcessor.storeBcrBarcode(bcrId, true, biospecimenToArchiveId, disease, center);
        final ShippedBiospecimen shippedBiospecimen = makeShippedBiospecimenFromAliquot(bcrId);
        // this method will update if it already exists rather than trying to insert again
        handleShippedBiospecimens(Arrays.asList(shippedBiospecimen),
                null,
                archive,
                xmlFile);
    }
    @Override
    public void storeBarcode( final BCRID barcode, final Integer bcrIdFromCommon, final Tumor disease ) throws ParseException, UUIDException{
        commonBcrIdProcessor.storeBarcode( barcode, false, -1, disease );
        diseaseBcrIdProcessor.storeBarcode( barcode, true, bcrIdFromCommon, disease );

    }

    private void addShippedBiospecimenElements(final List<ShippedBiospecimen> shippedBiospecimens){
        // make list of all elements for all biospecimens...
        List<ShippedBiospecimenElement> allElements = new ArrayList<ShippedBiospecimenElement>();
        for (final ShippedBiospecimen shippedBiospecimen : shippedBiospecimens) {
            allElements.addAll(shippedBiospecimen.getShippedBiospecimenElements());
        }
        // add them all at once (batch add/update)
        commonShippedBiospecimenQueries.addShippedBiospecimenElements(allElements);
        diseaseShippedBiospecimenQueries.addShippedBiospecimenElements(allElements);

    }

    protected ShippedBiospecimen makeShippedBiospecimenFromAliquot(final BCRID aliquot) throws ParseException {

        ShippedBiospecimen shippedBiospecimen = new ShippedBiospecimen();
        shippedBiospecimen.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        shippedBiospecimen.setShippedBiospecimenId((long) aliquot.getId());
        shippedBiospecimen.setUuid(aliquot.getUUID());
        shippedBiospecimen.setBarcode(aliquot.getFullID());
        shippedBiospecimen.setBatchNumber(aliquot.getBatchNumber());

        shippedBiospecimen.setProjectCode(aliquot.getProjectName());
        shippedBiospecimen.setTssCode(aliquot.getSiteID());
        shippedBiospecimen.setParticipantCode(aliquot.getPatientID());
        shippedBiospecimen.setSampleSequence(aliquot.getSampleNumberCode());
        shippedBiospecimen.setSampleTypeCode(aliquot.getSampleTypeCode());
        shippedBiospecimen.setPortionSequence(aliquot.getPortionNumber());
        shippedBiospecimen.setAnalyteTypeCode(aliquot.getPortionTypeCode());
        shippedBiospecimen.setPlateId(aliquot.getPlateId());
        shippedBiospecimen.setBcrCenterId(aliquot.getBcrCenterId());
        shippedBiospecimen.setShippedDate(DateUtils.makeDate(aliquot.getShippingDate()));

        return shippedBiospecimen;
    }

    @Override
    public void addBioSpecimenBarcodes( final List<BCRID> bcrIds, final Tumor disease) throws UUIDException {
        commonBcrIdProcessor.addBioSpecimenBarcodes(bcrIds, disease);
        diseaseBcrIdProcessor.addBioSpecimenBarcodes(bcrIds, disease);

    }

    @Override
    public List<Integer> getBiospecimenIds(final List<String> barcodes){
        return commonBcrIdProcessor.getBiospecimenIds(barcodes);
    }

    @Override
    public List<Long> getShippedBiospecimenIds(final List<String> uuids) {
        return commonShippedBiospecimenQueries.getShippedBiospecimenIds(uuids);
    }

    @Override
    public BCRID parseAliquotBarcode(final String barcode) throws ParseException{
        return commonBcrIdProcessor.parseAliquotBarcode(barcode);
    }

    public void setCommonBcrIdProcessor(BCRIDProcessor commonBcrIdProcessor) {
        this.commonBcrIdProcessor = commonBcrIdProcessor;
    }

    public void setDiseaseBcrIdProcessor(BCRIDProcessor diseaseBcrIdProcessor) {
        this.diseaseBcrIdProcessor = diseaseBcrIdProcessor;
    }

    public void setCommonShippedBiospecimenQueries(ShippedBiospecimenQueries commonShippedBiospecimenQueries) {
        this.commonShippedBiospecimenQueries = commonShippedBiospecimenQueries;
    }

    public void setDiseaseShippedBiospecimenQueries(ShippedBiospecimenQueries diseaseShippedBiospecimenQueries) {
        this.diseaseShippedBiospecimenQueries = diseaseShippedBiospecimenQueries;
    }
}
