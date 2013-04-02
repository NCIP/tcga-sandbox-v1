package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class which provides the following services for Level2Data
 * <code> generateData </code> API generates cache files for the given platform/center/sourcefiletype
 * The caller should set disease context before calling this API.
 * <code> getFileName </code> API provides unique filename for platform/center/sourcefiletype
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataService implements JDBCCallback, Level2DataServiceI {
    public static final String FILE_EXTENSION = ".txt";

    private static int BUFFER_SIZE = 64 * 1024;
    private static final String TAB_CHAR = "\t";
    private static final String NEW_LINE_CHAR = "\n";
    private static final String HYBRIDIZATION_REF_COLUMN_HEADER = "Hybridization REF";
    private static final String COMP_ELEMENT_REF_COL_HEADER = "CompositeElement REF";
    private static final String CHROMOSOME_HEADER = "Chromosome";
    private static final String POSITION_HEADER = "Position";

    private static final ThreadLocal dataFileWriter = new ThreadLocal();
    private static final ThreadLocal dataBuffer = new ThreadLocal();

    private Level2DataQueries level2DataQueries;
    private int minExpectedRowsToUseHintQuery;

    private final Log logger = LogFactory.getLog(getClass());

    public Level2DataQueries getLevel2DataQueries() {
        return level2DataQueries;
    }

    public void setLevel2DataQueries(Level2DataQueries level2DataQueries) {
        this.level2DataQueries = level2DataQueries;
    }


    public int getMinExpectedRowsToUseHintQuery() {
        return minExpectedRowsToUseHintQuery;
    }

    public void setMinExpectedRowsToUseHintQuery(int minExpectedRowsToUseHintQuery) {
        this.minExpectedRowsToUseHintQuery = minExpectedRowsToUseHintQuery;
    }

    /**
     * Creates unique filename for level2data sourcefiletype
     *
     * @param diseaseAbbreviation
     * @param platformName
     * @param centerName
     * @param sourceFileType
     * @return filename
     */
    public static String getFileName(final String diseaseAbbreviation,
                                     final String platformName,
                                     final String centerName,
                                     final String sourceFileType) {
        StringBuilder fileName = new StringBuilder(diseaseAbbreviation);
        fileName.append("_")
                .append(ConstantValues.DATA_LEVEL_2_STR)
                .append("_")
                .append(centerName)
                .append("_")
                .append(platformName)
                .append("_")
                .append(sourceFileType)
                .append(FILE_EXTENSION);
        return fileName.toString();

    }

    /**
     * Generates cache file for level2 data for the given platformId, centerId and source file type
     * The caller should set disease context before calling this API.
     *
     * @param platformId
     * @param centerId
     * @param sourceFileType
     * @param dataFilename
     * @return generated cache file
     * @throws IOException
     * @throws DataException
     */
    public File generateDataFile(final int platformId,
                                 final int centerId,
                                 final String sourceFileType,
                                 final String dataFilename
    ) throws DataException {

        //get data set ids for the selected platformId,centerId,file types and latest archives
        List<Long> dataSetIds = level2DataQueries.getLevel2DataSetIds(platformId,
                centerId,
                sourceFileType);
        if (dataSetIds.size() == 0) {
            logger.info("No data set found. Cache is not updated for platform " + platformId + " centerId " + centerId + "sourceFileType " + sourceFileType);
            return null;
        }
        try {
            initializeLocalData(dataFilename);
            generateData(dataSetIds, platformId);
            return new File(dataFilename);
        } catch (Throwable e) {
            if (!(e instanceof DataException)) {
                e = new DataException(e.toString(), e);
            }

            throw (DataException) e;
        } finally {
            try {
                cleanupLocalData();
            } catch (IOException ie) {
                logger.error("Cleanup Error " + ie.toString());
            }
        }


    }

    /**
     * This is the main API which generates the cache file for the given datasetIds
     *
     * @param dataSetIds
     * @param platformId
     * @throws IOException
     * @throws DataException
     */
    private void generateData(final List<Long> dataSetIds,
                              final int platformId) throws IOException, DataException {
        // get hyb_data_groups from database
        // as all datasetids refers to same source file type, use the first data set id
        final List<String> hybDataGroupNames = level2DataQueries.getHybridizationDataGroupNames(dataSetIds.get(0));
        // get hybridization_ref_ids for the given dataSetIds
        final List<Long> hybRefIds = level2DataQueries.getHybridizationRefIds(dataSetIds);
        // get barcodes
        final Map<String, Long> barcodesHybRefIdMap = level2DataQueries.getBarcodesForHybrefIds(hybRefIds);

        final boolean willHaveProbeConstants = (level2DataQueries.getProbeCountForValidChromosome(platformId) > 0) ? true : false;
        writeHeaders(hybDataGroupNames, barcodesHybRefIdMap.keySet(), willHaveProbeConstants);

        final long expectedRowCount = getExpectedRowCount(platformId, dataSetIds.get(0), hybRefIds);
        final boolean useHint = (expectedRowCount < getMinExpectedRowsToUseHintQuery()) ? false : true;
        // As the data to be retrieved is huge, use call back API to process the data
        level2DataQueries.getHybridizationValue(platformId, useHint, hybRefIds, dataSetIds, hybDataGroupNames, barcodesHybRefIdMap, willHaveProbeConstants, this);
        // write the remaining data and flush the stream
        writeData("", true);
    }

    /**
     * Callback API to process the data
     *
     * @param data data[0] - probe details
     *             data[1] - hybrefid.groupname and hybvalue data map
     *             data[2] - hybdata groupname list
     *             data[3] - barcodes and hybrefid data map
     *             data[4] - probe count for valid chromosome available or not. - true/false
     * @throws DataException
     */
    public void processData(Object... data) throws DataException {

        try {
            // probe details
            final String[] probeInfo = (String[]) data[0];
            // Contains values for each hybrefid.groupname
            final Map<String, String> rowData = (Map<String, String>) data[1];
            final List<String> hybDataGroupNames = (List<String>) data[2];
            final Map<String, Long> barcodesHybRefIdMap = (Map<String, Long>) data[3];
            final boolean willHaveProbeConstants = (Boolean) data[4];

            final String TAB_CHAR = "\t";
            final String NEW_LINE_CHAR = "\n";

            // write in the order given by orderedBarcodes
            writeData(probeInfo[0], false);
            if (willHaveProbeConstants) {
                writeData(TAB_CHAR, false);
                writeData(probeInfo[1], false); // chromosome
                writeData(TAB_CHAR, false);
                writeData(probeInfo[2], false); // position
            }
            for (final String barcode : barcodesHybRefIdMap.keySet()) {
                for (final String dataGroup : hybDataGroupNames) {
                    writeData(TAB_CHAR, false);
                    // get the value for this barcode/datagroup combination
                    String key = barcodesHybRefIdMap.get(barcode) + "." + dataGroup;
                    String value = rowData.get(key);
                    if (value == null) {
                        value = "";
                    }
                    writeData(value, false);
                }
            }

            writeData(NEW_LINE_CHAR, false);
        } catch (Throwable e) {
            throw new DataException(e.getMessage(), e);
        }
    }


    private long getExpectedRowCount(final int platformId,
                                     final long dataSetId,
                                     final List<Long> hybRefIds) {
        int rowCount = level2DataQueries.getProbeCount(platformId);
        rowCount += level2DataQueries.getDataGroupsCount(dataSetId);
        rowCount += hybRefIds.size();
        return rowCount;
    }

    private void writeHeaders(final List<String> hybDataGroupNames,
                              final Set<String> orderedBarcodes,
                              final boolean willHaveProbeConstants) throws IOException {

        StringBuilder majorHeader = new StringBuilder(HYBRIDIZATION_REF_COLUMN_HEADER);
        StringBuilder minorHeader = new StringBuilder(COMP_ELEMENT_REF_COL_HEADER);

        if (willHaveProbeConstants) {
            majorHeader.append(TAB_CHAR + TAB_CHAR); // two constants
            minorHeader.append(TAB_CHAR).append(CHROMOSOME_HEADER).append(TAB_CHAR).append(POSITION_HEADER);
        }
        // write each barcode once per hyb data group
        // and write each hyb data group once per barcode
        for (final String barcode : orderedBarcodes) {
            for (final String hybDataGroupName : hybDataGroupNames) {
                majorHeader.append(TAB_CHAR).append(barcode);
                minorHeader.append(TAB_CHAR).append(hybDataGroupName);
            }
        }
        StringBuilder header = majorHeader;
        header.append(NEW_LINE_CHAR)
                .append(minorHeader.toString())
                .append(NEW_LINE_CHAR);
        writeData(header.toString(), false);
    }

    private void writeData(
            final String data,
            final Boolean doNotCheckBufferSize)
            throws IOException {
        StringBuilder dataBuffer = getDataBuffer();
        dataBuffer.append(data);
        // minimize I/O access
        if (doNotCheckBufferSize || dataBuffer.length() >= BUFFER_SIZE) {
            getDataFileWriter().write(dataBuffer.toString());
            getDataFileWriter().flush();
            dataBuffer.delete(0, dataBuffer.length());
        }

    }

    private void initializeLocalData(final String filename) throws IOException {

        dataFileWriter.set(new FileWriter(filename));
        dataBuffer.set(new StringBuilder());
    }

    private void cleanupLocalData() throws IOException {
        Writer writer = getDataFileWriter();
        if (writer != null) {
            writer.close();
        }
        dataFileWriter.remove();
        dataBuffer.remove();
    }

    private Writer getDataFileWriter() {
        return (Writer) dataFileWriter.get();
    }

    private StringBuilder getDataBuffer() {
        return (StringBuilder) dataBuffer.get();
    }

}
