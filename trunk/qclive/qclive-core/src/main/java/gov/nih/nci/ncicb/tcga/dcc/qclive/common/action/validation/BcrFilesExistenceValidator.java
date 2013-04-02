package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which validates whether the previous archive clinical files
 * does exists in the new archive
 * This is applicable only for BCR archives
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BcrFilesExistenceValidator extends AbstractProcessor<Archive, Boolean> {
    private ArchiveQueries archiveQueries;
    private PlatformQueries platformQueries;
    private ManifestParser manifestParser;
	private ShippedBiospecimenQueries shippedBiospecimenQueries;

	// pattern to make sure that BCR XML files conform to.
	private static final String XML_FILE_NAME_REGEXP = "^.+(TCGA)-([A-Za-z0-9]{2})-([A-Za-z0-9]{4})\\.xml$";
    public final static Pattern XML_FILE_NAME_PATTERN = Pattern.compile(XML_FILE_NAME_REGEXP);

    public static String BCR_REMOVED_FILES = "BCR_REMOVED_FILES.txt";


    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);

        // Validate only for BCR archives
        if (isBCRArchive(archive)) {
            // get list of XML files from latest archive stored in the database
            List<String> latestArchiveXMLFileList = getLatestArchiveXMLFileListFromDB(archive);
            // get list of XML files from current archive
            List<String> currentArchiveXMLFileList = getCurrentArchiveXMLFileList(archive);

            // if currentArchive does not contain all the archives from previous archive throw exception                                                           
            if (!currentArchiveXMLFileList.containsAll(latestArchiveXMLFileList)) {

                // get the missing archive list
                latestArchiveXMLFileList.removeAll(currentArchiveXMLFileList);

            	//check to make sure the missing files are not redacted
                List <String> unredactedFiles =  removeRedactedFiles(latestArchiveXMLFileList,archive,context);

                if (unredactedFiles.size() > 0){
	                StringBuilder missingFileList = new StringBuilder("Following XML Files from previous archive were missing in this archive :");
	                for (final String filename : unredactedFiles) {
	                    missingFileList.append(filename)
	                            .append(",");
	                }
	                missingFileList.deleteCharAt(missingFileList.length() - 1);
	                throw new ProcessorException(MessageFormat.format(
	                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
	                		archive,
	                		missingFileList.toString()));
                }
            }
        }

        return true;
    }

    /**
     * Removes redacted files from the list
     * @param latestArchiveXMLFileList list of file from which to remove redacted files
     * @param archive Archive to use for error handling
     * @return A list with redacted files removed
     * 			if the list is empty , then all files were redacted.
     * @throws ProcessorException
     */
    protected List<String> removeRedactedFiles (final List<String> latestArchiveXMLFileList,Archive archive, final QcContext context) throws ProcessorException{

    	Map <String,String> participantCodeNameMap = new HashMap<String,String>();
    	List <String> unredactedFiles = new ArrayList<String>();
    	if (latestArchiveXMLFileList != null && latestArchiveXMLFileList.size() > 0){

    		// parse out participant codes
	    	for (String xmlFile:latestArchiveXMLFileList){
	    		// make sure the file matches a BCR file pattern
	    		Matcher xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher(xmlFile);
	        	if(xmlFileNameMatcher.matches()){
	        		String[] fileParts = xmlFile.split("\\.");
		    		String[] codeList = fileParts[fileParts.length - 2].split("-");
		    		participantCodeNameMap.put(codeList[codeList.length - 1],xmlFile);
	            }else{
	            	throw new ProcessorException(MessageFormat.format(
	                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
	                		archive, " File " + xmlFile + " does not follow BCR XML file name pattern "));
	            }
	    	}

	    	// get all redacted participants for the codeset
	    	List <String> matchingParticipants =
	    		new ArrayList(shippedBiospecimenQueries.getRedactedParticipants(participantCodeNameMap.keySet()));

	    	try{
	    		// 	get codes from an optional BCR_REMOVED_FILES.txt file
	    		matchingParticipants.addAll(getParticipantsFromMetadataFile(archive, latestArchiveXMLFileList,context));
	    	}catch (IOException e) {
	    		throw new ProcessorException(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                		archive, " Error while reading " + BCR_REMOVED_FILES));
			}

	    	// figure out which participants are not matching
	    	for (String code:participantCodeNameMap.keySet()){
	    		if (!matchingParticipants.contains(code)){
	    			unredactedFiles.add(participantCodeNameMap.get(code));
	    		}
	    	}
    	}
    	return unredactedFiles;
    }


    /**
     * Looks for an optional metadata file , and if it is there retrieves
     * participant codes from the file. If the file does not exist , or
     * the file has no records, an empty list
     * exist
     * @param  archive object that contains the metadata file
     * @throws ProcessorException if a file name does not match BCR file name pattern
     * @throws IOException if there is an error in opening / reading the file
     *
     */
    protected List<String> getParticipantsFromMetadataFile(final Archive archive, final List<String> latestArchiveXMLFileList, final QcContext context ) throws IOException, ProcessorException{
    	List <String> participantCodesFromFile = new ArrayList<String>();
        final List <String> redactedFilesNotInLatestArchive = new ArrayList<String>();
    	// check if the file exists
    	File bcrRemovedFiles = new File(archive.getDeployDirectory(), BCR_REMOVED_FILES);
    	// The file is optional , if it is then skip the logic below
    	if (bcrRemovedFiles.exists()){
	    	FileReader fReader = new FileReader(bcrRemovedFiles);
	        BufferedReader in = null;
	        try {
	            in = new BufferedReader(fReader);
	            // 2. read each line
	            String xmlFileName;
	            int lineNum = 1;
	            while ((xmlFileName = in.readLine()) != null) {
	                // ignore blank lines
	                if (StringUtils.isNotEmpty(xmlFileName)) {
	                	Matcher xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher(xmlFileName);

	                	if(xmlFileNameMatcher.matches()){
	    	        		String[] fileParts = xmlFileName.split("\\.");
	    		    		String[] codeList = fileParts[fileParts.length - 2].split("-");
	    		    		participantCodesFromFile.add(codeList[codeList.length - 1]);
	                	}else{
	                		throw new ProcessorException(MessageFormat.format(
	    	                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
	    	                		archive, " File " + xmlFileName + " does not follow BCR XML file name pattern . The file was referenced " +
	    	                				"in " + BCR_REMOVED_FILES ));
	                	}
                        if(!latestArchiveXMLFileList.contains(xmlFileName)) {
                           redactedFilesNotInLatestArchive.add(xmlFileName);
                        }
	                }
	                lineNum++;
	            }
	        } finally {
	            if (in != null) {
	                in.close();
	                in = null;
	            }
	            fReader.close();
	            fReader = null;
	        }
    	}
        if (0 < redactedFilesNotInLatestArchive.size()) {
            final StringBuilder warningMessage = new StringBuilder("The following files not in the latest archive, were listed in ").append(BCR_REMOVED_FILES).append("\n");
            for (final String filename : redactedFilesNotInLatestArchive) {
                warningMessage.append(filename).append("\n");
            }
            context.addWarning(warningMessage.toString());

        }
    	return participantCodesFromFile;
    }

    private boolean isBCRArchive(final Archive archive) {
        final String platformName = archive.getPlatform();
        Platform platform = platformQueries.getPlatformForName(platformName);
        if (platform.getCenterType().equals(Experiment.TYPE_BCR)) {
            return true;
        }
        return false;
    }

    private List<String> getLatestArchiveXMLFileListFromDB(final Archive archive) {

        final Long latestArchiveId = archiveQueries.getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
        // if latest archive exists, get the xml files associated with this archive
        final List<String> xmlFileList = new ArrayList<String>();
        if (latestArchiveId != null) {
            final List<FileInfo> fileInfoList = archiveQueries.getFilesForArchive(latestArchiveId);
            for (FileInfo fileInfo : fileInfoList) {
                if (fileInfo.getFileName().endsWith(FILE_EXTENSION_XML)) {
                    xmlFileList.add(fileInfo.getFileName());
                }
            }
        }
        return xmlFileList;
    }

    protected List<String> getCurrentArchiveXMLFileList(final Archive archive) throws ProcessorException {
        final List<String> currentArchiveXMLFileList = new ArrayList<String>();
        final File manifest = new File(archive.getDeployDirectory(), MANIFEST_NAME);
        try {
            final Map<String, String> manifestEntries = manifestParser.parseManifest(manifest);
            for (String filename : manifestEntries.keySet()) {
                if (filename.endsWith(FILE_EXTENSION_XML)) {
                    currentArchiveXMLFileList.add(filename);
                }
            }

        } catch (IOException ie) {
            throw new ProcessorException(
                    new StringBuilder("Error reading the manifest file ")
                            .append(manifest)
                            .append(ie.getMessage()).toString());

        } catch (ParseException pe) {
            throw new ProcessorException(
                    new StringBuilder("Error parsing the manifest file ")
                            .append(manifest)
                            .append(pe.getMessage()).toString());

        }
        return currentArchiveXMLFileList;
    }

    public String getName() {
        return "Previous bcr archive files existence validation";
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries(ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public PlatformQueries getPlatformQueries() {
        return platformQueries;
    }

    public void setPlatformQueries(PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public ManifestParser getManifestParser() {
        return manifestParser;
    }

    public void setManifestParser(ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    public void setShippedBiospecimenQueries(
			ShippedBiospecimenQueries shippedBiospecimenQueries) {
		this.shippedBiospecimenQueries = shippedBiospecimenQueries;
	}

}
