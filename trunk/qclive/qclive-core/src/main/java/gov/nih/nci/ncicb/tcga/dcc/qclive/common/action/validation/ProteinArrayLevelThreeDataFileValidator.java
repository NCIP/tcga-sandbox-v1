package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
/**
 * Protein array file name validator. 
 * This validator validates level 3 data file names
 * @author girshiks
 */
public class ProteinArrayLevelThreeDataFileValidator extends AbstractArchiveFileProcessor<Boolean> {
	// when validating file names in an archive, ignore these files , ; delimited list
	public final static String ignoreList = "MANIFEST.txt;README.txt;DESCRIPTION.txt";

	// platform used to validate file name
	public final static String PROTEIN_ARRAY_PLATFORM = "MDA_RPPA_Core";	
	
	// data type for protein array level3 data files               
	public final static String PROTEIN_ARRAY_PLATFORM_DATA_TYPE = "protein_expression";
		
	//file extension of the archive to validate
	public final static String archiveFileExtension = ".txt";		
	
	@Override
    protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
        return Experiment.TYPE_CGCC.equals(archive.getExperimentType())
                && Archive.TYPE_LEVEL_3.equals(archive.getArchiveType())
                && archive.getPlatform() != null 
                && archive.getPlatform().contains(PROTEIN_ARRAY_PLATFORM);                
    }


	@Override
	public String getName() {
		 return "Protein array level3 file name validation";
	}


	@Override
	protected Boolean getReturnValue(Map<File, Boolean> results,QcContext context) {
		   return !(results.values().contains(false));
    }


	@Override
	protected Boolean processFile(File file, QcContext context)throws ProcessorException {		
		Boolean isValid = false;												
		if (file != null ){													
			if (StringUtils.isNotEmpty(ignoreList) && 
					ignoreList.toUpperCase(Locale.US).contains(file.getName().toUpperCase(Locale.US))){
				//if the filename is in the ignore list, skip it
				isValid = true;
			}else{
				isValid = isValidProteinArrayDataFileName(file.getName(),context.getArchive(), context);
			}
		}
	     				
		return isValid;
	}


	@Override
	protected Boolean getDefaultReturnValue(Archive archive) {
		 return true;
	}

	/**
	 *	Verifies is protein array data file name follows the specification.
	 *  This method validates files for archive Level3 and Level3
	 *  @param fileName file name of the archive to validate
	 *  @param archive archive object with information on the platform and level
	**/
	protected Boolean isValidProteinArrayDataFileName(final String fileName, final Archive archive, final QcContext qcContext){
		
		Boolean isValid = true;
		//regex for protein file name
		String proteinArrayPattern   = "(([A-Za-z0-9_.-])+)" + archive.getTheTumor().getTumorName() +
		"."+PROTEIN_ARRAY_PLATFORM +"."+ PROTEIN_ARRAY_PLATFORM_DATA_TYPE + "." + Archive.TYPE_LEVEL_3 
		+"." +"(([A-Za-z0-9_.-])+)"+".txt";		
		
		//pattern for the filename regex
		Pattern filePattern = Pattern.compile(proteinArrayPattern);			
		Matcher matcher = filePattern.matcher(fileName);
        if (!matcher.matches()) {
            qcContext.addError("protein array level 3 file '" + fileName + "' is not a valid filename");
        	isValid = false;
        }
        return isValid;        		
	}	
	
	@Override
	protected String getFileExtension() {		
		return archiveFileExtension;
	}		
}
