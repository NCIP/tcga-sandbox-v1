package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileCollection;

import java.util.Date;

/**
 * DAO for file_collection and file_to_collection
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FileCollectionQueries {

    /**
     * Saves a file collection.  If it already exists with the given parameters, will not add again.
     *
     * @param collectionName the name of the file collection
     * @param isIdentifiable if the collection contains files with identifiable data
     * @param diseaseAbbreviation disease abbreviation or null
     * @param centerType center type or null
     * @param centerDomain center name or null
     * @param platformName platform name or null
     * @return FileCollection bean that was saved
     */
    public FileCollection saveCollection(String collectionName, boolean isIdentifiable, String diseaseAbbreviation, String centerType, String centerDomain, String platformName);


    /**
     * Saves files to collection.  Will create file info record first if needed.
     *
     * @param collection
     * @param fileLocation
     * @param fileDate
     */
    public void saveFileToCollection(FileCollection collection, String fileLocation, Date fileDate);
}
