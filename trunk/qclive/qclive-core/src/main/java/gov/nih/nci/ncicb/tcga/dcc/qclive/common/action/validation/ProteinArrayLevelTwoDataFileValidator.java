package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;

/**
 * Protein array file name validator.
 * This validator validates level 2 data file names
 *
 * @author srinivasand
 *         Last updated by: $Author$
 * @version $Rev$
 */
public final class ProteinArrayLevelTwoDataFileValidator extends AbstractArchiveFileProcessor<Boolean> {

    // <center_domain>_<disease_code>.<platformname>.
    // only need these three so no need to match the rest
    // make regex really loose so we can try to validate whatever is in the name, as long as the delimiters are there
    private static final Pattern PARTIAL_FILENAME_PATTERN = Pattern.compile("([^_]+)_([^\\.]+)\\.([^\\.]+)");

	private CenterQueries centerQueries;
	private PlatformQueries platformQueries;
	private TumorQueries diseaseQueries;

	// when validating file names in an archive, ignore these files , ; delimited list
	private final static String ignoreList = "MANIFEST.txt;README.txt;DESCRIPTION.txt";

	// data type for protein array level3 data files               
	private final static String PROTEIN_ARRAY_PLATFORM_DATA_TYPE = "SuperCurve";
		
	//file extension of the file to validate
	private final static String fileExtension = ".txt";
	
	private static final int INDEX_DOMAIN_NAME = 0;
	private static final int INDEX_TUMOR_TYPE = 1;
	private static final int INDEX_PLATFORM_NAME = 2;
	
    @Override
	public String getName() {
		 return "Protein array level2 file name validation";
	}

	@Override
	protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
		return !results.values().contains(false);
	}

	@Override
	protected Boolean processFile(final File file, final QcContext context) throws ProcessorException {
		boolean isValid = false;												
		if (file != null ){													
			if (StringUtils.isNotEmpty(ignoreList) && 
					ignoreList.toUpperCase(Locale.US).contains(file.getName().toUpperCase(Locale.US))){
				//if the filename is in the ignore list, skip it
				isValid = true;
			} else {
				isValid = isValidProteinArrayDataFileName(file.getName(),context.getArchive(), context);
			}
		}
		return isValid;
	}

	@Override
	protected Boolean getDefaultReturnValue(final Archive archive) {
		 return true;
	}

	@Override
	protected String getFileExtension() {
		return fileExtension;
	}

	@Override
	protected boolean isCorrectArchiveType(final Archive archive)
			throws ProcessorException {
        return Experiment.TYPE_CGCC.equals(archive.getExperimentType())
        && Archive.TYPE_LEVEL_2.equals(archive.getArchiveType())
        && archive.getPlatform() != null 
        && archive.getPlatform().contains(PROTEIN_ARRAY_PLATFORM);                
	}

	/**
	 *	Verifies that the protein array level 2 data file name follows the specification.
	 *  This method validates files for protein array level 2
	 * @param fileName file name of the archive to validate
	 * @param archive archive object with information on the platform and level
     * @param qcContext the QCLive execution context
     * @return whether the filename is valid for protein level 2 or not
     */
	protected Boolean isValidProteinArrayDataFileName(final String fileName, final Archive archive, final QcContext qcContext){
		boolean isValid = true;

		final String proteinArrayPattern = fileNameRegexPattern(archive.getTheTumor().getTumorName());
		final Pattern filePattern = Pattern.compile(proteinArrayPattern);			
		final Matcher matcher = filePattern.matcher(fileName);
        if (!matcher.matches()) {
            qcContext.addError("protein array level 2 file '" + fileName + "' is not a valid filename");
        	isValid = false;
        }

        // even if invalid, still try to extract parts to get more specific error message(s)
        final String[] filenameParts = extractFileNameParts(fileName);
        if (filenameParts != null) {
            // validate the domain name
            isValid = isValid && validateDomainName(filenameParts[INDEX_DOMAIN_NAME], filenameParts[INDEX_PLATFORM_NAME], fileName, qcContext);
            // validate the tumor type
            isValid = isValid && validateTumorType(filenameParts[INDEX_TUMOR_TYPE], fileName, qcContext);
        }

        return isValid;
	}	
	
    protected boolean validateDomainName(final String domainName, final String platformName, final String fileName, final QcContext qcContext) {
    	boolean isValid = true;
        final String centerType = getCenterType(platformName);
        if(centerType == null) {
            qcContext.addError("protein array level 2 file '" + fileName + "' has an invalid platform name '" + platformName + "'");
            isValid = false;
        } else {
            final Integer id = getCenterId(domainName, centerType);
            if(id == null || id == 0) {
                qcContext.addError("protein array level 2 file '" + fileName + "' has an invalid domain name '" + domainName + "'");
                isValid =  false;
            }
        }            
        return isValid;
    }
    
    protected boolean validateTumorType(final String tumorType, final String fileName, final QcContext qcContext) {
    	boolean isValid = true;
        if(diseaseQueries.getTumorForName(tumorType) == null) {
            qcContext.addError("protein array level 2 file '" + fileName + "' has an invalid disease name '" + tumorType + "'");
        	isValid =  false;
        }
        return isValid;
    }
    
    protected Integer getCenterId(final String domainName, final String centerType) {
        try {
        	if(domainName == null || centerType == null) {
        		return null;
        	}
            return centerQueries.findCenterId( domainName, centerType);
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    protected String getCenterType(final String platformName) {
        try {
        	if(platformName == null) {
        		return null;
        	}
            final Platform platform = platformQueries.getPlatformForName(platformName);
            return platform!=null? platform.getCenterType():null;
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    /**
     * Set the center queries interface to use for validating the center name in the filename.
     * @param centerQueries center queries DAO
     */
    public void setCenterQueries( final CenterQueries centerQueries ) {
        this.centerQueries = centerQueries;
    }

    /**
     * Sets the platform queries interface to use for validating the platform name in the filename.
     * @param platformQueries platform queries DAO
     */
    public void setPlatformQueries( final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }    

    /**
     * Sets the disease queries to use for validating the disease name in the filename.
     * @param diseaseQueries the disease queries DAO
     */
    public void setDiseaseQueries( final TumorQueries diseaseQueries ) {
        this.diseaseQueries = diseaseQueries;
    }

	private String[] extractFileNameParts(final String filename) {

		final Matcher matcher = PARTIAL_FILENAME_PATTERN.matcher(filename);
        String[] filenameParts = null;

    	if(matcher.find()) {
            filenameParts = new String[3];
            filenameParts[INDEX_DOMAIN_NAME] = matcher.group(INDEX_DOMAIN_NAME+1); // domain name
            filenameParts[INDEX_TUMOR_TYPE] = matcher.group(INDEX_TUMOR_TYPE+1); // tumor type
            filenameParts[INDEX_PLATFORM_NAME] = matcher.group(INDEX_PLATFORM_NAME+1); // platform name
    	}
        return filenameParts;
    }

    private String fileNameRegexPattern(final String tumorName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(([A-Za-z0-9_.-])+)");
        sb.append("_");
        sb.append(tumorName);
        sb.append("\\.");
        sb.append(PROTEIN_ARRAY_PLATFORM);
        sb.append("\\.");
        sb.append(PROTEIN_ARRAY_PLATFORM_DATA_TYPE);
        sb.append("\\.");
        sb.append(Archive.TYPE_LEVEL_2);
        sb.append("\\.");
        sb.append("(([A-Za-z0-9_.-])+)");
        sb.append(".txt");
        return sb.toString();
    }
}
