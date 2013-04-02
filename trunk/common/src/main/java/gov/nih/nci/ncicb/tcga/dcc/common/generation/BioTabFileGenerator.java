package gov.nih.nci.ncicb.tcga.dcc.common.generation;


import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BioTabDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileCollection;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileCollectionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.BcrFileType;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.TcgaBcrDataTemplate;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.TemplateType;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Class which generates clinical bio-tab files
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BioTabFileGenerator implements FileGenerator {

    private final Log logger = LogFactory.getLog(getClass());
    
    // template map indexed by biotab filetype
    private static final ThreadLocal<Map<String, TcgaBcrDataTemplate>> templatesMap = 
            new ThreadLocal<Map<String, TcgaBcrDataTemplate>>();

    // bio tab data indexed by biotab filetype
    private static final ThreadLocal<Map<String, BioTabDataBean>> bioTabDataMap = 
            new ThreadLocal<Map<String, BioTabDataBean>>();

    // generated bio tab files list indexed by compressed file name
    private static final ThreadLocal<Map<String, List<String>>> generatedBioTabFilesMap = 
            new ThreadLocal<Map<String, List<String>>>();

    public static final String BIOTAB = "biotab";
    
    private JAXBContext jaxbContext;
    private String bioTabFilesHomeDir;
    private String bioTabFilesTmpDir;
    private String templateFilesDir;
    private String packageName;
    private FileArchiveQueries fileArchiveQueries;
    private FileCollectionQueries fileCollectionQueries;
    private BcrXMLFileParser bcrXMLFileParser;
    private BioTabDataProcessorUtil bioTabDataProcessorUtil;

    public void init() throws JAXBException {
        // initialize JAXB Object;
        jaxbContext = JAXBContext.newInstance(getPackageName());
    }

    /**
     * Generates bio tab files for the given disease. It generates bio tab files
     * in the temp dir and then move those files into bio tab files dir.
     * 
     * @param diseaseAbbreviation
     * @return returns list of generated bio tab file names
     * @throws FileGeneratorException
     */
    public List<String> generate(String diseaseAbbreviation) throws FileGeneratorException {

        DiseaseContextHolder.setDisease(diseaseAbbreviation);
        // file path is created with diseaseAbbreviation. To avoid case issues convert it into
        // lower case
        diseaseAbbreviation = diseaseAbbreviation.toLowerCase();
        List<String> bioTabFileNames;
        final long startTime = System.currentTimeMillis();
        String xmlFileToBeProcessed = "";
        try {
            logger.debug("Started generating bio-tab files for " + diseaseAbbreviation);
            initializeThreadLocalData();
            // Get the XML files in patient id order
            final Map<String, List<String>> xmlFileNamesByCenter = fileArchiveQueries.getClinicalXMLFileLocations();
            transformXMLtoTabDelimitedData(xmlFileNamesByCenter,true);
            // Now generate center specific bio tab files and general bio tab files (which contains all center data)
            bioTabFileNames = createBioTabFilesInOpenAccessDir(diseaseAbbreviation);

            //remove files with older bio-tab names
            removeBioTabFilesWithOlderBioTabFilenames(diseaseAbbreviation,xmlFileNamesByCenter.keySet());
            logger.debug("Completed generating bio-tab files for " + diseaseAbbreviation + " Time spent :" + (System.currentTimeMillis() - startTime) / 60 + " Secs.");
        } catch (Exception exp) {
            final String errorMsg = getErrorMessage(exp, xmlFileToBeProcessed);
            logger.error("Exception " + errorMsg, exp);
            throw new FileGeneratorException(errorMsg, exp);
        } finally {
            cleanupThreadLocalData();
        }

        return bioTabFileNames;
    }

    /**
     * Generates the following bio tab files for the given XML files.
     * <p>
     * For each biotab filetype - biotab file which contains data for all the
     * xml files - biotab file which contains data for center specific xml files
     * It generates bio tab files in the temp dir and then move those files into
     * bio tab files dir.
     * 
     * @param diseaseAbbreviation
     * @param bcrXMLFiles
     * @return list of generated files
     * @throws FileGeneratorException
     */
    public List<String> generate(String diseaseAbbreviation, final List<String> bcrXMLFiles) throws FileGeneratorException {

        List<String> bioTabFileNames;
        String xmlFileToBeProcessed = "";

        diseaseAbbreviation = diseaseAbbreviation.toLowerCase();
        try {
            initializeThreadLocalData();

            final Map<String,List<String>> xmlFilesForAllCenters = new HashMap<String, List<String>>();
            xmlFilesForAllCenters.put("all_centers",bcrXMLFiles);
            transformXMLtoTabDelimitedData(xmlFilesForAllCenters,false);

            // Now generate center specific bio tab files and general bio tab files (which contains all center data)
            bioTabFileNames = createBioTabFilesInTempDir(diseaseAbbreviation);

        } catch (Exception exp) {
            final String errorMsg = getErrorMessage(exp, xmlFileToBeProcessed);
            logger.error("Exception " + errorMsg, exp);
            throw new FileGeneratorException(errorMsg, exp);
        } finally {
            cleanupThreadLocalData();
        }

        return bioTabFileNames;

    }

    /**
     * Transforms XML data into tab delimited data and store them in temporary files.
     * 
     * @param xmlFileNamesByCenter
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void transformXMLtoTabDelimitedData(final Map<String, List<String>> xmlFileNamesByCenter, final boolean createCenterFiles) 
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

       String xmlFileToBeProcessed = "";
       for (String centerName : xmlFileNamesByCenter.keySet()) {

           for (final String xmlFile : xmlFileNamesByCenter.get(centerName)) {
               xmlFileToBeProcessed = xmlFile;
               try {
                   // for each center extract the data from xml file
                   // and transform to bio tab data
                   transformXMLToBioTabData(xmlFile);
                   // store the bio tab data
                   storeBioTabData(centerName, false);
               } catch (IOException e) {
                   // If file is not found ignore that
                   if (e instanceof FileNotFoundException) {
                       logger.error(" Parsing error " + e.getMessage());
                   } else {
                       throw e;
                   }
               }
           }
           storeBioTabData(centerName, true);
       }
       
       // store the data in temp files
       createBioTabTempFiles(createCenterFiles);
    }


    private void initializeThreadLocalData() throws JAXBException, FileNotFoundException, ParserConfigurationException {
        templatesMap.set(createTemplates());
        bioTabDataMap.set(new HashMap<String, BioTabDataBean>());
        generatedBioTabFilesMap.set(new HashMap<String, List<String>>());
        getBcrXMLFileParser().initializeParser();
    }

    private void cleanupThreadLocalData() {
        templatesMap.remove();
        bioTabDataMap.remove();
        generatedBioTabFilesMap.remove();
        getBcrXMLFileParser().cleanupParser();
    }

    /**
     * Creates templates which will be used to parse xml files
     *
     * @return A Map of biotab filetype to the {@link TcgaBcrDataTemplate} that holds a template
     *         of what the biotab should look like.
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    private Map<String, TcgaBcrDataTemplate> createTemplates() throws JAXBException, FileNotFoundException {

        final Map<String, TcgaBcrDataTemplate> templateMap = new HashMap<String, TcgaBcrDataTemplate>();
        InputStream inputStream = null;

        final File[] templateFiles = getTemplateFiles();
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        for (final File templateFile : templateFiles) {
            try {
                // noinspection IOResourceOpenedButNotSafelyClosed
                inputStream = new FileInputStream(templateFile);
                final TcgaBcrDataTemplate tcgaBcrDataTemplate = (TcgaBcrDataTemplate) unmarshaller.unmarshal(inputStream);
                templateMap.put(tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType(), tcgaBcrDataTemplate);
            }
            finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return templateMap;
    }


    /**
     * Parse the xml file and store the data in tab delimited format.
     *
     * @param xmlFile
     * @throws SAXException
     * @throws IOException
     */
    private void transformXMLToBioTabData(final String xmlFile) 
            throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {

        final Document document = bcrXMLFileParser.getDocument(xmlFile);

        final Map<String, TcgaBcrDataTemplate> templateByBioTabFileType = getTemplates();

        // for each template parse the xml file and extract the data
        for (final String bioTabFileType : templateByBioTabFileType.keySet()) {
            final TcgaBcrDataTemplate tcgaBcrDataTemplate = templateByBioTabFileType.get(bioTabFileType);
            // check whether xml file matches template bcr file type
            if (matchesBcrFileType(tcgaBcrDataTemplate.getTcgaBcrData().getBcrFileType(), xmlFile)) {

                final Map<String, Map<String, String>> elementDataByKeyName = bcrXMLFileParser.parseDocument(document, tcgaBcrDataTemplate, xmlFile);
                // Now we extracted all the data for the given bio-tab template from the xml file
                // transform the extracted data into biotab data
                final TemplateType templateType = tcgaBcrDataTemplate.getType();
                // for dynamic templates each key data should go in a separate file.
                if (templateType != null &&
                        templateType.equals(TemplateType.DYNAMIC)) {
                    for(final String key: elementDataByKeyName.keySet()) {
                        final String originalBiotabFileType = tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType();
                        final Map<String, Map<String, String>> dynamicFileData = new HashMap<String, Map<String,String>> ();
                        dynamicFileData.put(key, elementDataByKeyName.get(key));
                        tcgaBcrDataTemplate.getTcgaBcrData().setBiotabFileType(
                                tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType().replace(TemplateType.DYNAMIC.value(),
                                        key));

                        transformToBioTabData(elementDataByKeyName, tcgaBcrDataTemplate);
                        //restore thr original value so that the dynamic biotab file type will get replaced again.
                        tcgaBcrDataTemplate.getTcgaBcrData().setBiotabFileType(originalBiotabFileType);

                    }

                }else{

                    transformToBioTabData(elementDataByKeyName, tcgaBcrDataTemplate);
                }
            }
        }
    }

    /**
     * Create bio tab temp files and move it to the corresponding disease dir.
     *
     * @param diseaseAbbreviation
     * @return A List of biotab filenames that are created
     * @throws IOException
     */
    public List<String> createBioTabFilesInOpenAccessDir(final String diseaseAbbreviation) throws IOException {
        final List<String> bioTabFileNames = new ArrayList<String>();
        // Move temp files into the corresponding disease dir
        moveBioTabFilesToDiseaseDir(diseaseAbbreviation);

        // compress the generated files
        createCompressedFiles();

        for (final List<String> generatedFiles : getGeneratedBioTabFilesMap().values()) {
            bioTabFileNames.addAll(generatedFiles);
        }
        return bioTabFileNames;
    }


    /**
     * Creates bio tab file sin temp dir. Creates the entire biotab file not specific to centers.
     *
     * @return
     * @throws IOException
     */
    public List<String> createBioTabFilesInTempDir(final String diseaseAbbreviation) throws IOException {
        final List<String> bioTabFileNames = new ArrayList<String>();
        final Map<String, BioTabDataBean> bioTabDataByFileType = getBioTabDataMap();
        for (final String bioTabFileType : bioTabDataByFileType.keySet()) {
            final BioTabDataBean bioTabDataBean = bioTabDataByFileType.get(bioTabFileType);
            if (bioTabDataBean.getBioTabData().getCenterNameList().size() > 0) {
                final String actualBioTabFileName = getBioTabFileName(bioTabFilesTmpDir, bioTabFileType, "", diseaseAbbreviation);
                FileUtil.move(bioTabDataBean.getBioTabData().getBioTabFileName(), actualBioTabFileName);
                bioTabFileNames.add(actualBioTabFileName);
            }
        }
        
        return bioTabFileNames;
    }

    /**
     * Transforms the extracted xml data into tab delimited bio tab data and
     * stores in bio tab data bean.
     * 
     * @param elementDataByKeyName
     * @param tcgaBcrDataTemplate
     */
    public void transformToBioTabData(final Map<String, Map<String, String>> elementDataByKeyName, final TcgaBcrDataTemplate tcgaBcrDataTemplate) {

        BioTabDataBean bioTabDataBean = getBioTabDataBean(tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType());
        if (bioTabDataBean == null) {
            bioTabDataBean = createBioTabDataBean(tcgaBcrDataTemplate);
        }
        
        // sort the keys
        final List<String> keys = new ArrayList(elementDataByKeyName.keySet());
        Collections.sort(keys);

        // Store each entry. Each entry is stored in one row.
        for (final String key : keys) {
            Map<String, String> bioTabDataByColumnHeader = elementDataByKeyName.get(key);
            bioTabDataByColumnHeader = bioTabDataProcessorUtil.transformDayMonthYearToDate(bioTabDataByColumnHeader);
            // transform the data into tab delimited data in the order specified
            // in the biotab data bean column headers
            final List<String> additionalColumns = new ArrayList<String>(bioTabDataByColumnHeader.keySet());
            // get additional columns
            additionalColumns.removeAll(bioTabDataBean.getBioTabData().getColumnHeaders());
            Collections.sort(additionalColumns);
            if (additionalColumns.size() > 0) {
                bioTabDataBean.getBioTabData().addColumnHeaders(additionalColumns);
            }
            // transform additional columns data into tab delimited data
            createTabDelimitedData(bioTabDataBean, bioTabDataByColumnHeader);

            // add \n
            bioTabDataBean.getBioTabData().setEndOfLine();
        }

    }


    /**
     * Creates a BioTabDataBean using the template file and the disease name and stores the bean in
     * the biotab data map.
     *
     * @param tcgaBcrDataTemplate
     * @return A {@link BioTabDataBean} containing the data for a biotab file
     */
    private BioTabDataBean createBioTabDataBean(final TcgaBcrDataTemplate tcgaBcrDataTemplate) {
        final BioTabDataBean bioTabDataBean = new BioTabDataBean();
        final List<String> columnHeaders = tcgaBcrDataTemplate.getTcgaBcrData().getBiotabData().getDataColumnsOrder().getColumnName();
        bioTabDataBean.getBioTabData().addColumnHeaders(columnHeaders);
        getBioTabDataMap().put(tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType(), bioTabDataBean);
        return bioTabDataBean;
    }

    /**
     * Creates tab delimited bio tab data.
     * 
     * @param bioTabDataBean
     * @param bioTabDataByColumnHeader
     */
    private void createTabDelimitedData(final BioTabDataBean bioTabDataBean, final Map<String, String> bioTabDataByColumnHeader) {
        for (final String columnHeader : bioTabDataBean.getBioTabData().getColumnHeaders()) {
            String data = bioTabDataByColumnHeader.get(columnHeader);
            data = (data == null) ? "null" : data;
            if (bioTabDataBean.getBioTabData().getColumnHeaders().contains(columnHeader)) {
                bioTabDataBean.getBioTabData().addTabDelimitedData(data);
            }
        }
    }

    /**
     * Dump the data into temp file when the buffer is full or flushData is set to true.
     *
     * @param centerName
     * @param flushData
     * @throws IOException
     */
    private void storeBioTabData(final String centerName, final Boolean flushData) throws IOException {
        final Map<String, BioTabDataBean> bioTabDataByFileType = getBioTabDataMap();
        for (final BioTabDataBean bioTabDataBean : bioTabDataByFileType.values()) {
            // Dump the data into temp file when the buffer is full
            if (flushData) {
                storeBioTabData(bioTabDataBean.getBioTabData(), centerName);
            }
        }
    }

    /**
     * Writes the {@link BioTabDataBean.BioTabData} to a the file identified by
     * the filename corresponding to the centerName.
     * 
     * @param bioTabData
     * @param centerName
     * @throws IOException
     */
    private void storeBioTabData(final BioTabDataBean.BioTabData bioTabData,
                                 final String centerName) throws IOException {

        if (bioTabData.getData().length() > 0) {
            String tempFileName = bioTabData.getBioTabFileName(centerName);
            if (tempFileName == null) {
                tempFileName = getTempFileName(centerName);
                bioTabData.setBioTabFileName(centerName, tempFileName);
            }
            // dump data
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(tempFileName, true);
                fileWriter.write(bioTabData.getData().toCharArray());
            } finally {
                // clean the buffer
                bioTabData.clearData();
                closeFileWriter(fileWriter);
            }
        }
    }

    /**
     * For each bio tab data type, create center specific bio tab data temp
     * files and bio tab (which contains all data) temp files.
     * 
     * @throws IOException
     */
    private void createBioTabTempFiles(boolean createCenterFiles) throws IOException {
        final Map<String, BioTabDataBean> bioTabDataByFileType = getBioTabDataMap();
        for (final BioTabDataBean bioTabDataBean : bioTabDataByFileType.values()) {
            createBioTabTempFiles(bioTabDataBean.getBioTabData(),createCenterFiles);
        }
    }


    /**
     * Create bio tab temp file which contains data from all the centers and
     * center specific bio tab file.
     * 
     * @param bioTabData
     * @throws IOException
     */
    private void createBioTabTempFiles(final BioTabDataBean.BioTabData bioTabData, final boolean createCenterFiles) throws IOException {
        // create bio tab files only when there is some data
        if (bioTabData.getCenterNameList().size() > 0) {
            // create temp file which contains all the center data
            final String bioTabFileName = getTempFileName("");

            // Bio tab temp file which contains data from all the centers
            FileChannel bioTabFile = null;
            // Bio tab temp file which contains data for specific center
            FileChannel centerBioTabFile = null;
            // Bio tab data file
            FileChannel source = null;
            int offset = 0;

            FileOutputStream bioTabFileOutputStream = null;
            FileInputStream sourceFileInputStream = null;
            FileOutputStream centerBioTabFileOutputStream = null;
            final List<String> deleteFiles = new ArrayList<String>();
            try {
                //noinspection IOResourceOpenedButNotSafelyClosed
                bioTabFileOutputStream = new FileOutputStream(bioTabFileName, true);
                //noinspection ChannelOpenedButNotSafelyClosed
                bioTabFile = bioTabFileOutputStream.getChannel();
                // write the header
                final String header = getHeaderWithNewLine(bioTabData.getColumnHeadersAsString());
                bioTabFile.write(ByteBuffer.wrap((header).getBytes()));

                offset = header.length();

                // For each center, create center bio tab file to store center data and
                // append the same data into bio tab file.

                for (final String centerName : bioTabData.getCenterNameList()) {
                    // Get the bio tab data file name which holds the biotab data for specific center
                    final String centerBioTabDataFileName = bioTabData.getBioTabFileName(centerName);

                    //noinspection IOResourceOpenedButNotSafelyClosed
                    sourceFileInputStream = new FileInputStream(centerBioTabDataFileName);
                    //noinspection ChannelOpenedButNotSafelyClosed
                    source = sourceFileInputStream.getChannel();
                    //append the biotab data into biotab file
                    bioTabFile.transferFrom(source, offset, source.size());
                    offset += source.size();
                    if(createCenterFiles){
                        // create temp center bio tab file which contains only center data
                        final String centerBioTabFileName = getTempFileName(centerName);

                        //noinspection IOResourceOpenedButNotSafelyClosed
                        centerBioTabFileOutputStream = new FileOutputStream(centerBioTabFileName);
                        //noinspection ChannelOpenedButNotSafelyClosed
                        centerBioTabFile = centerBioTabFileOutputStream.getChannel();
                        // write the header
                        centerBioTabFile.write(ByteBuffer.wrap((header).getBytes()));

                        source = source.position(0);
                        // write the data
                        centerBioTabFile.transferFrom(source, header.length(), source.size());

                        // close the channels
                        IOUtils.closeQuietly(source);
                        IOUtils.closeQuietly(centerBioTabFile);

                        // Set the center biotab filename
                        bioTabData.setBioTabFileName(centerName, centerBioTabFileName);
                    }
                    deleteFiles.add(centerBioTabDataFileName);
                }
                // set the bio tab filename
                bioTabData.setBioTabFileName(bioTabFileName);

            } finally {
                IOUtils.closeQuietly(bioTabFileOutputStream);
                IOUtils.closeQuietly(sourceFileInputStream);
                IOUtils.closeQuietly(centerBioTabFileOutputStream);
                IOUtils.closeQuietly(bioTabFile);
                IOUtils.closeQuietly(source);
                IOUtils.closeQuietly(centerBioTabFile);
               // remove the temp files
                for (final String fileToBeDeleted : deleteFiles) {
                    new File( fileToBeDeleted).delete();
                }
            }
        }
    }

    /**
     * Move temp files to the corresponding disease dir.
     *
     * @param diseaseAbbreviation
     * @return
     * @throws IOException
     */
    private void moveBioTabFilesToDiseaseDir(final String diseaseAbbreviation) throws IOException {
        // Move the temp files to proper center and disease dir
        final Map<String, BioTabDataBean> bioTabDataByFileType = getBioTabDataMap();
        for (final String bioTabFileType : bioTabDataByFileType.keySet()) {
            final BioTabDataBean bioTabDataBean = bioTabDataByFileType.get(bioTabFileType);
            moveBioTabFiles(bioTabDataBean.getBioTabData(), diseaseAbbreviation, bioTabFileType);
        }
    }

    /**
     * Saves the Collection of files and each file by centerName.
     *
     * @param bioTabData
     * @param diseaseAbbreviation
     * @param bioTabFileType
     * @throws IOException
     */
    private void moveBioTabFiles(final BioTabDataBean.BioTabData bioTabData,
                                 final String diseaseAbbreviation,
                                 final String bioTabFileType) throws IOException {

        if (bioTabData.getCenterNameList().size() > 0) {

            // save collection
            FileCollection biotabCollection = 
                    fileCollectionQueries.saveCollection(
                            BIOTAB, 
                            false,
                            diseaseAbbreviation.toUpperCase(), 
                            "BCR", 
                            null, 
                            null);

            String bioTabFileName = 
                    moveBioTabFile(
                            bioTabData.getBioTabFileName(),
                            "",
                            diseaseAbbreviation,
                            bioTabFileType, 
                            biotabCollection);


            storeBioTabFileName(bioTabFileName, "", diseaseAbbreviation);

            for (final String centerName : bioTabData.getCenterNameList()) {
                FileCollection biotabCollectionCenter = 
                        fileCollectionQueries.saveCollection(
                                BIOTAB, 
                                false,
                                diseaseAbbreviation.toUpperCase(), 
                                "BCR", 
                                centerName, 
                                null);
                
                bioTabFileName = 
                        moveBioTabFile(
                                bioTabData.getBioTabFileName(centerName),
                                centerName,
                                diseaseAbbreviation,
                                bioTabFileType, biotabCollectionCenter);
                
                storeBioTabFileName(bioTabFileName, centerName, diseaseAbbreviation);
            }
        }
    }

    private String moveBioTabFile(
            final String bioTabDataTempFileName, 
            final String centerName, 
            final String diseaseAbbreviation,
            final String bioTabFileType, 
            final FileCollection fileCollection) throws IOException {

        final String bioTabFileName = getBioTabFileName(bioTabFileType, centerName, diseaseAbbreviation);
        FileUtil.move(bioTabDataTempFileName, bioTabFileName);
        fileCollectionQueries.saveFileToCollection(fileCollection, new File(bioTabFileName).getAbsolutePath(), new Date());

        return bioTabFileName;
    }

    private void storeBioTabFileName(final String fileName, final String centerName, final String diseaseAbbreviation) throws IOException {
        final String compressedFileName = getCompressedFileName(centerName, diseaseAbbreviation);
        List<String> generatedFiles = getGeneratedBioTabFilesMap().get(compressedFileName);
        if (generatedFiles == null) {
            generatedFiles = new ArrayList<String>();
            getGeneratedBioTabFilesMap().put(compressedFileName, generatedFiles);
        }
        generatedFiles.add(fileName);
    }

    private String getHeaderWithNewLine(String header) {
        return header + "\n";
    }

    private void removeBioTabFilesWithOlderBioTabFilenames(final String diseaseAbbreviation, final Set<String> centers) {
        try {
            for (final String centerName : centers) {
                deleteBioTabFiles(centerName, diseaseAbbreviation);
            }
            deleteBioTabFiles("", diseaseAbbreviation);

        }
        catch (IOException ie) {
            logger.error(ie);
        }
    }

    private void deleteBioTabFiles(final String centerName, final String diseaseAbbreviation) throws IOException {
        final List<String> bioTabFilenamesToBeDeleted = 
                Arrays.asList(
                        "*clinical_aliquot*.txt", 
                        "*clinical_analyte*.txt", 
                        "*clinical_portion*.txt",
                        "*clinical_protocol*.txt", 
                        "*clinical_sample*.txt", 
                        "*clinical_shipment_portion*.txt", 
                        "*clinical_slide*.txt");
        
        final String bioTabFileDir = getBioTabDir(centerName, diseaseAbbreviation);

        for (final String filter : bioTabFilenamesToBeDeleted) {
            final File dir = new File(bioTabFileDir);
            final FileFilter fileFilter = new WildcardFileFilter(filter);
            final File[] files = dir.listFiles(fileFilter);
            for (final File fileToBeDeleted : files) {
                fileToBeDeleted.delete();
            }
        }
    }

    private String getTempFileName(final String centerName) {
        final StringBuffer fileName = new StringBuffer(bioTabFilesTmpDir);
        final File tmpDir = new File(fileName.toString());
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        fileName.append(File.separator)
                .append(centerName)
                .append("_")
                .append(UUID.randomUUID())
                .append(".txt");
        return fileName.toString();
    }

    protected String getBioTabFileName(
            final String bioTabFileType, 
            final String centerName, 
            final String diseaseAbbreviation) throws IOException {
        final String bioTabDir = getBioTabDir(centerName, diseaseAbbreviation);

        return getBioTabFileName(bioTabDir, bioTabFileType, centerName, diseaseAbbreviation);
    }

    protected String getBioTabFileName(final String bioTabDir,
                                       final String bioTabFileType,
                                       final String centerName,
                                       final String diseaseAbbreviation) throws IOException {

        final StringBuilder bioTabFile = new StringBuilder(bioTabDir);
        bioTabFile.append(File.separator);
        if (!centerName.isEmpty()) {
            bioTabFile.append(centerName).append("_");
        }
        bioTabFile.append(bioTabFileType).append("_").append(diseaseAbbreviation).append(".txt");

        return bioTabFile.toString();
    }

    private String getBioTabDir(final String centerName, final String diseaseAbbreviation) throws IOException {
        final StringBuilder fileName = new StringBuilder();
        fileName.append(bioTabFilesHomeDir).append(File.separator).append(diseaseAbbreviation).append(File.separator).append("bcr").append(File.separator);
        if (!centerName.isEmpty()) {
            fileName.append(centerName).append(File.separator).append(BIOTAB);
        }
        else {
            fileName.append(BIOTAB);
        }
        fileName.append(File.separator).append("clin");
        // create the dir if it doesn't exist
        File file = new File(fileName.toString());
        if (!file.exists()) {
            file.mkdirs();
        }

        return fileName.toString();
    }

    /**
     * Creates compressed file for each set of bio tab data. Each compressed file contains the corresponding set of clinical cache files.
     *
     * @throws IOException
     */
    private void createCompressedFiles() throws IOException {
        final Map<String, List<String>> generatedBioTabFilesByCompressedFileName = getGeneratedBioTabFilesMap();
        for (final String compressedFileName : generatedBioTabFilesByCompressedFileName.keySet()) {
            FileUtil.createCompressedFiles(generatedBioTabFilesByCompressedFileName.get(compressedFileName), compressedFileName);
        }
    }

    private String getCompressedFileName(final String centerName, final String diseaseAbbreviation) throws IOException {
        final StringBuilder compressedFileName = new StringBuilder();
        compressedFileName.append(getBioTabDir(centerName, diseaseAbbreviation));
        compressedFileName.append(File.separator);
        if (!centerName.isEmpty()) {
            compressedFileName.append(centerName).append("_");
        }
        compressedFileName.append("clinical_").append(diseaseAbbreviation).append(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        return compressedFileName.toString();

    }


    private boolean matchesBcrFileType(final List<BcrFileType> bcrFileTypes, final String xmlFileName) {
        for (final BcrFileType bcrFileType : bcrFileTypes) {
            if (xmlFileName.contains(bcrFileType.value())) {
                return true;
            }
        }
        return false;
    }

    private void closeFileWriter(final Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }

    }

    private Map<String, TcgaBcrDataTemplate> getTemplates() {
        return templatesMap.get();
    }

    private BioTabDataBean getBioTabDataBean(final String bioTabFileType) {
        return bioTabDataMap.get().get(bioTabFileType);
    }

    private Map<String, BioTabDataBean> getBioTabDataMap() {
        return bioTabDataMap.get();
    }

    private Map<String, List<String>> getGeneratedBioTabFilesMap() {
        return generatedBioTabFilesMap.get();
    }

    private String getErrorMessage(final Exception exp, final String xmlFileName) {
        return xmlFileName + ":" + exp.getMessage();
    }

    public String getBioTabFilesHomeDir() {
        return bioTabFilesHomeDir;
    }

    public void setBioTabFilesHomeDir(String bioTabFilesHomeDir) {
        this.bioTabFilesHomeDir = bioTabFilesHomeDir;
    }

    public String getBioTabFilesTmpDir() {
        return bioTabFilesTmpDir;
    }

    public void setBioTabFilesTmpDir(String bioTabFilesTmpDir) {
        this.bioTabFilesTmpDir = bioTabFilesTmpDir;
    }

    public String getTemplateFilesDir() {
        return templateFilesDir;
    }

    public void setTemplateFilesDir(String templateFilesDir) {
        this.templateFilesDir = templateFilesDir;
    }

    public File[] getTemplateFiles() {
        final File templateDir = new File(getTemplateFilesDir());
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().endsWith(".xml") && !name.startsWith(".");
            }
        };
        return templateDir.listFiles(filter);

    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public FileArchiveQueries getFileArchiveQueries() {
        return fileArchiveQueries;
    }

    public void setFileArchiveQueries(FileArchiveQueries fileArchiveQueries) {
        this.fileArchiveQueries = fileArchiveQueries;
    }

    public BcrXMLFileParser getBcrXMLFileParser() {
        return bcrXMLFileParser;
    }

    public void setBcrXMLFileParser(BcrXMLFileParser bcrXMLFileParser) {
        this.bcrXMLFileParser = bcrXMLFileParser;
    }

    public void setFileCollectionQueries(final FileCollectionQueries fileCollectionQueries) {
        this.fileCollectionQueries = fileCollectionQueries;
    }

    public BioTabDataProcessorUtil getBioTabDataProcessorUtil() {
        return bioTabDataProcessorUtil;
    }

    public void setBioTabDataProcessorUtil(BioTabDataProcessorUtil bioTabDataProcessorUtil) {
        this.bioTabDataProcessorUtil = bioTabDataProcessorUtil;
    }
    
}
