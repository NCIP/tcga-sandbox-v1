package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Interface for bcr data DAO queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface BCRDataService {
    /**
     * Adds shippedbiospecimen and the corresponding file and archvie relationships into common and disease db
     * @param shippedBiospecimens
     * @param shippedItemId
     * @param archive
     * @param xmlFile
     */

    public void handleShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens,
                                          final Integer shippedItemId,
                                          final Archive archive,
                                          final File xmlFile);

    /**
        * Method for finding BCR IDs in an XML file.
        *
        * @param file the XML file to look through
        * @return a List of String arrays, where in each array, the first element is the aliquot barcode, and the second is
        *         the ship date, and the third is the UUID found in the file
        * @throws TransformerException if there is an error transforming
        * @throws XPathExpressionException if there is an error in an expression in the file
        * @throws IOException if the file can't be read or something
        * @throws javax.xml.parsers.ParserConfigurationException if the parser is not configured correctly
        * @throws org.xml.sax.SAXException if there is a parse problem
        */
    public List<String[]> findAllAliquotsInFile(File file) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException;

    /**
     * Adds  biospecimen to file relationships for all biospecimens in the list
     * @param biospecimenToFileList
     * @param disease
     */
    public void addBioSpecimenToFileAssociations( final List<BiospecimenToFile> biospecimenToFileList, final Tumor disease);

    /**
     * Gets all shippedbiospecimen for the given file
     * @param file
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws ParseException
     */
    public List<ShippedBiospecimen> findAllShippedPortionsInFile(File file) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException;

    /**
     * Gets shipped item id for the given shipped item type
     * @param shippedItemType
     * @return shipped item id
     */
    public Integer getShippedItemId(final String shippedItemType);

    /**
     * Adds shippedportions into shippedbiospecimen table
     * @param shippedPortions portions to add
     * @param shippedItemId item id, will look up if null
     */
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedPortions,final Integer shippedItemId);


    /**
     * Adds shipped biospecimen to file relationships for all IDs in the list, to the given file id
     * @param biospecimenIds list of shipped biospecimen ids
     * @param fileId file id
     */
    public void addShippedBiospecimensFileRelationship(List<Long> biospecimenIds, Long fileId);

    /**
     * Adds biospecimen barcodes
     * @param bcrIdList
     * @param disease
     * @throws UUIDException
     */
    public void addBioSpecimenBarcodes( final List<BCRID> bcrIdList, final Tumor disease) throws UUIDException ;


    /**
     * Returns biospecimen ids for the given barcodes
     * @param barcodes
     * @return list of biospecimen ids
     */
    public List<Integer> getBiospecimenIds(final List<String> barcodes);

    /**
     * Gets biospecimen ids for multiple uuids at once.
     * @param uuids the uuis to look up
     * @return shipped biospecimen ids corresponding to uuids
     */
    public List<Long> getShippedBiospecimenIds(final List<String> uuids);

    /**
     * Save the barcode to database
     * @param theBCRID BCRID
     * the bar code to database, false otherwise
     * @param bcrIdFromCommon biospecimen to archive association id, the method updates it to the new value if a new is inserted
     * @param disease the disease the barcode belongs to
     * @throws ParseException parsing exception
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException uuid exception
     */
    public void storeBarcode(final BCRID theBCRID, final  Integer bcrIdFromCommon, final Tumor disease) throws ParseException, UUIDException;

    /**
     * Save the aliquot barcode and the corresponding file and archive relations to database
     * @param bcrId
     * @param archive
     * @param xmlFile
     * @throws ParseException
     * @throws UUIDException
     */

    public void handleAliquotBarcode(final BCRID bcrId,  final Archive archive, final File xmlFile) throws ParseException, UUIDException;

    /**
     * Parses the given aliquot barcode and returns BCRID object
     * @param aliquotBarcode
     * @return  BCRID
     * @throws ParseException
     */

    public BCRID parseAliquotBarcode(String aliquotBarcode) throws ParseException;

    /**
     * Adds a relationship between a shipped biospecimen and an archive.  Will do nothing
     * if the relationship already exists.
     *
     * @param biospecimenId the shipped biospecimen id
     * @param archiveId the archive id
     */
    public void addArchiveRelationship(Long biospecimenId, Long archiveId);

    public void addShippedBiospecimensArchiveRelationship(final List<Long> biospecimenIds, final Long archiveId);

}
