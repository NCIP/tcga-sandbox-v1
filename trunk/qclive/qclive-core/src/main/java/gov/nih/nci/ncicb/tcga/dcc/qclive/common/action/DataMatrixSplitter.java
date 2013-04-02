package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * If Level2 archive contains multiple aliquot data in a single data matrix file,
 * split them into separate data matrix file (for each aliquot)
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixSplitter extends AbstractProcessor<Archive, Archive> {
    private static final int BUFFER_SIZE = 5 * 1024;
    private final String ALIQUOT_HEADER = "Hybridization REF";
    private final String PROBE_HEADER = "Composite Element REF";

    private final Log logger = LogFactory.getLog(getClass());
    private static final ThreadLocal<BufferedReader> dataMatrixFileReader = new ThreadLocal<BufferedReader>();
    private static final ThreadLocal<Map<String, Writer>> splitDataMatrixFileWriter = new ThreadLocal<Map<String, Writer>>();
    private ManifestParser manifestParser;

    @Override
    public Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        // run only for level2 archives
        if (!archive.getArchiveType().equals(Archive.TYPE_LEVEL_2)) {
            return archive;
        }
        try {
            processDataMatrixFiles(archive, context);
        } catch (IOException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            throw new ProcessorException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            throw new ProcessorException(e.getMessage());
        } catch (ParseException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            throw new ProcessorException(e.getMessage());
        }
        return archive;
    }

    /**
     * If Level2 archive contains multiple aliquot data in a single data matrix file,
     * split them into separate data matrix file (for each aliquot)
     *
     * @param archive
     * @param context
     * @throws IOException
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     */

    private void processDataMatrixFiles(final Archive archive, final QcContext context) throws IOException, ParseException, NoSuchAlgorithmException {
        final List<File> dataMatrixFiles = getDataMatrixFiles(archive);
        final List<File> filesToBeRemoved = new ArrayList<File>();

        for (File dataMatrixFile : dataMatrixFiles) {
            // process only data matrix files
            try {
                initLocalData(dataMatrixFile);
                // Read first line which contains aliquots
                final List<String> aliquots = readNextLine();
                // Same aliquot gets repeated for each data column. So get unique aliquots.
                final Set<String> uniqueAliquots = getUniqueAliquots(aliquots);
                // Process only data matrix files which has more than one aliquot
                if (uniqueAliquots != null && uniqueAliquots.size() > 1) {
                    // get constant columns count
                    final Integer constantColumnsCount = getConstantColumnsCount(aliquots);
                    // split aliquots data into separate files
                    final Map<String, File> splitAliquotsFileName = splitDataMatrixFile(uniqueAliquots, constantColumnsCount,dataMatrixFile);
                    // update manifest with split file names
                    manifestParser.updateManifest(new ArrayList(splitAliquotsFileName.values()),
                            Arrays.asList(new File[]{dataMatrixFile}),
                            getArchiveManifestFile(archive));
                    // update change file list
                    context.aboutToChangeFile(dataMatrixFile, "Removed data matrix file after splitting into individual aliquot files ");
                    filesToBeRemoved.add(dataMatrixFile);
                    context.addSplitDataMatrixFiles(splitAliquotsFileName,dataMatrixFile.getName());
                }

            } finally {
                cleanupLocalData(dataMatrixFile);
            }
        }
        // remove multiple aliquot files
        removeMultipleAliquotsDataMatrixFiles(filesToBeRemoved);
    }

    /**
     * Get all files listed under level2 archive dir
     * @param archive
     * @return
     */
    protected List<File> getDataMatrixFiles(final Archive archive) {
        return Arrays.asList(DirectoryListerImpl.getFilesInDir(archive.getDeployDirectory()));
    }

    private void initLocalData(final File dataMatrixFile) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataMatrixFile));
        dataMatrixFileReader.set(bufferedReader);
        // linked hash map is used to maintain the order. do not changes this
        splitDataMatrixFileWriter.set(new LinkedHashMap<String, Writer>());
    }

    private void cleanupLocalData(final File dataMatrixFile) {
        try {
            final Map<String, Writer> splitDataMatrixFilesMap = getSplitDataMatrixFileWriter();
            splitDataMatrixFileWriter.remove();
            final Reader reader = getDataMatrixFileReader();
            dataMatrixFileReader.remove();
            reader.close();
            for (Writer writer : splitDataMatrixFilesMap.values()) {
                writer.close();
            }
        } catch (IOException ie) {
            logger.error("Error occured while closing the file " + dataMatrixFile, ie);
        }
    }

    /**
     * Splits multiple aliquots data matrix file into unique aliquot files.

     * @param aliquots
     * @param constantColumnsCount
     * @param dataMatrixFile
     * @return
     * @throws IOException
     */
    private Map<String, File> splitDataMatrixFile(final Set<String> aliquots,
                                                  final Integer constantColumnsCount,
                                                  final File dataMatrixFile) throws IOException {
        final List<String> columnsHeader = readNextLine();
        //get constant columns header
        final Set<String>constantColumnsHeader = getConstantColumnsHeader(columnsHeader, constantColumnsCount);
        //get data columns header
        final Set<String>dataColumnsHeader = getDataColumnsHeader(columnsHeader, constantColumnsHeader);

        // create new DataMatrix files
        final Map<String, File> splitDataMatrixFiles = createDataMatrixFiles(aliquots, dataMatrixFile);
        // create Headers
        createHeaders(splitDataMatrixFiles, constantColumnsHeader,dataColumnsHeader);
        // copy data
        List<String> tabDelimitedData = null;
        while ((tabDelimitedData = readNextLine()) != null) {
            copyData(splitDataMatrixFiles, tabDelimitedData, constantColumnsHeader.size(),dataColumnsHeader.size());
        }
        return splitDataMatrixFiles;
    }

    /**
     * get aliquots. If the file is not data matrix file then return null
     * @return
     * @throws IOException
     */

    private Set<String> getUniqueAliquots(final List<String> aliquots) throws IOException {
        if (aliquots == null ||
                !ALIQUOT_HEADER.equals(aliquots.get(0))) {
            return null;
        }
        // remove the aliquot header name
        aliquots.remove(0);
        //linked hash set is used to maintain the order. Do not change this.
        final Set<String> uniqueAliquotBarcodes = new LinkedHashSet<String>(aliquots);
        // remove blank headers
        uniqueAliquotBarcodes.remove("");
        return uniqueAliquotBarcodes;
    }

    /**
     * Get constant columns count.
     * The first line (Hybridization REF) contains blank value for constant columns followed by aliquot barcode.
     * @param aliquots
     * @return
     */

    private Integer getConstantColumnsCount(final List<String> aliquots){
        // First column (Composite Element REF) is always constant
        int constantColumnsCount = 1;
        //count number of empty strings
        for(int index = 0; aliquots.get(index).isEmpty() && index < aliquots.size();index++){
            constantColumnsCount++;
        }
        return constantColumnsCount;
    }

    /**
     * Get constant columns header
     * The second line (Composite Element REF) contains constant columns header followed by
     * data columns header
     * @param columnsHeader
     * @param constantColumnsCount
     * @return
     * @throws IOException
     */
    private Set<String> getConstantColumnsHeader(final List<String> columnsHeader,
                                                 final Integer constantColumnsCount) throws IOException {
        final Set<String> constantColumnsHeader = new LinkedHashSet<String>();
        // Second line should contain 'Composite Element REF' as first header. If it is not then throw an exception
        if (columnsHeader == null ||
                !PROBE_HEADER.equals(columnsHeader.get(0))) {
            throw new IOException("Header " + PROBE_HEADER + " not found. Invalid data matrix file");
        }

        for(int index=0; index < constantColumnsCount; index++){
            constantColumnsHeader.add(columnsHeader.get(index));
        }
        return constantColumnsHeader;
    }

    /**
     * Get Data columns header
     * The second line (Composite Element REF) contains constant columns header followed by
     * data columns header
     * @param columnsHeader
     * @param constantColumnsHeaders
     * @return
     * @throws IOException
     */
    private Set<String> getDataColumnsHeader(final List<String> columnsHeader,
                                             final Set<String> constantColumnsHeaders) throws IOException {
        //linked hash set is used to maintain the order. Do not change this.
        final Set<String> dataColumnsHeader = new LinkedHashSet<String>(columnsHeader);
        dataColumnsHeader.removeAll(constantColumnsHeaders);
        return dataColumnsHeader;
    }

    private BufferedReader getDataMatrixFileReader() {
        return dataMatrixFileReader.get();
    }

    private Map<String, Writer> getSplitDataMatrixFileWriter() {
        return splitDataMatrixFileWriter.get();

    }

    /**
     * Data is delimited by \t.
     * @return
     * @throws IOException
     */
    private List<String> readNextLine() throws IOException {
        final String line = getDataMatrixFileReader().readLine();
        if (line != null) {
            return new ArrayList<String>(Arrays.asList(line.split("\t",-1)));
        }
        return null;
    }

    /**
     * Create data matrix file for each aliquot
     * @param aliquots
     * @param multipleAliquotsDataMatrixFile
     * @return
     * @throws IOException
     */
    private Map<String, File> createDataMatrixFiles(final Set<String> aliquots,
                                                    final File multipleAliquotsDataMatrixFile) throws IOException {
        final Map<String, File> aliquotsFileName = new LinkedHashMap<String, File>();
        // remove .txt extension
        String dataMatrixFilePrefix = multipleAliquotsDataMatrixFile.getPath().substring(0, multipleAliquotsDataMatrixFile.getPath().lastIndexOf("."));
        for (String aliquot : aliquots) {
            final StringBuilder dataMatrixFileName = new StringBuilder(dataMatrixFilePrefix)
                    .append(".")
                    .append(aliquot)
                    .append(".txt");

            aliquotsFileName.put(aliquot, createDataMatrixFile(dataMatrixFileName.toString()));
        }
        return aliquotsFileName;
    }

    /**
     * Write headers for each aliquot file
     * @param splitDataMatrixFiles
     * @param constantColumnsHeader
     * @param dataColumnsHeader
     * @throws IOException
     */
    private void createHeaders(final Map<String, File> splitDataMatrixFiles,
                               final Set<String> constantColumnsHeader,
                               final Set<String> dataColumnsHeader) throws IOException {
        final StringBuilder majorHeader = new StringBuilder();
        final StringBuilder minorHeader = new StringBuilder();

        //remove the probe header
        List<String> constantColumnsHeaderList = new LinkedList(constantColumnsHeader);
        for (String aliquot : splitDataMatrixFiles.keySet()) {
            majorHeader.append(ALIQUOT_HEADER);
            minorHeader.append(constantColumnsHeaderList.get(0));
            for (final String columnHeader : constantColumnsHeaderList.subList(1,constantColumnsHeaderList.size())) {
                majorHeader.append("\t");
                minorHeader.append("\t")
                        .append(columnHeader);
            }
            for (final String columnHeader : dataColumnsHeader) {
                majorHeader.append("\t")
                .append(aliquot);
                minorHeader.append("\t")
                        .append(columnHeader);
            }

            createHeader(splitDataMatrixFiles.get(aliquot).getName(),
                    majorHeader.toString(),
                    minorHeader.toString());
            // cleanup headers
            majorHeader.delete(0, majorHeader.length());
            minorHeader.delete(0, minorHeader.length());
        }


    }

    private void createHeader(final String aliquotFileName,
                              final String majorHeader,
                              final String minorHeader) throws IOException {
        Writer writer = getDataMatrixFile(aliquotFileName);
        writer.write(majorHeader);
        writer.write("\n");
        writer.write(minorHeader);

    }

    /**
     * Copy data from multiple aliquot file into seperate aliquot files
     * @param splitDataMatrixFiles
     * @param aliquotsData
     * @param constantColumnsCount
     * @param dataColumnsCount
     * @throws IOException
     */
    private void copyData(final Map<String, File> splitDataMatrixFiles,
                          final List<String> aliquotsData,
                          final int constantColumnsCount,
                          final int dataColumnsCount) throws IOException {

        final StringBuilder aliquotDataToBeCopied = new StringBuilder();
        int aliquotCount = 0;
        for (String aliquot : splitDataMatrixFiles.keySet()) {
            // copy constant columns
            for(int index =0; index < constantColumnsCount; index++){
                aliquotDataToBeCopied.append(aliquotsData.get(index))
                .append("\t");
            }

            // copy data columns
            for (int index = constantColumnsCount; index < (dataColumnsCount+constantColumnsCount); index++) {
                try{
                aliquotDataToBeCopied.append(aliquotsData.get((aliquotCount * dataColumnsCount) + index))
                        .append("\t");
                }catch(Exception e){
                    logger.error(new StringBuilder("Error: Filename ")
                            .append(splitDataMatrixFiles.get(aliquot).getName() )
                            .append(" \n Data size : ")
                            .append(aliquotsData.size())
                            .append("\n Constant columns count: ")
                            .append(constantColumnsCount)
                            .append("\n Data columns count: ")
                            .append(dataColumnsCount)
                            .append("\n Index: ")
                            .append(index)
                            .append(" Data:")
                            .append(StringUtil.convertListToDelimitedString(aliquotsData,',')).toString());

                    throw new RuntimeException(e.getMessage());
                }

            }
            //remove the last '\t' character
            aliquotDataToBeCopied.deleteCharAt(aliquotDataToBeCopied.length()-1);
            getDataMatrixFile(splitDataMatrixFiles.get(aliquot).getName()).write("\n");
            getDataMatrixFile(splitDataMatrixFiles.get(aliquot).getName()).write(aliquotDataToBeCopied.toString());

            aliquotCount++;
            // clear data
            aliquotDataToBeCopied.delete(0, aliquotDataToBeCopied.length());

        }
    }

    protected File getArchiveManifestFile(final Archive archive) {
        return new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE);
    }

    private File createDataMatrixFile(final String fileName) throws IOException {

        File dataMatrixFile = null;

        dataMatrixFile = new File(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataMatrixFile), BUFFER_SIZE);
        getSplitDataMatrixFileWriter().put(dataMatrixFile.getName(), bufferedWriter);

        return dataMatrixFile;
    }

    private Writer getDataMatrixFile(final String fileName) throws IOException {
        return getSplitDataMatrixFileWriter().get(fileName);
    }

    private void removeMultipleAliquotsDataMatrixFiles(final List<File> filesToBeRemoved) throws IOException {

        // remove the data matrix file
        for (final File fileToBeRemoved : filesToBeRemoved) {
            if (!fileToBeRemoved.delete()) {
                throw new IOException("Couldn't delete mulitple aliquots file " + fileToBeRemoved.getPath());
            }
        }
    }

    public String getName() {
        return "Data Matrix splitter";
    }

    public ManifestParser getManifestParser() {
        return manifestParser;
    }

    public void setManifestParser(ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }
}
