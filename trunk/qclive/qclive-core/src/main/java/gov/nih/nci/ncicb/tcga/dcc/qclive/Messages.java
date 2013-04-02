package gov.nih.nci.ncicb.tcga.dcc.qclive;

/**
 * Class which provides message details
 *
 * @deprecated This class has been deprecated. Message formats are now being maintained in 
 * {@link gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormatType}
 * 
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Deprecated
public interface Messages {
    public static final String ARCHIVE_NAME_INVALID_SERIAL_INDEX_MSG = "Invalid serial index. Serial index {0} is already assigned to {1} center  {2} disease";
    public static final String ARCHIVE_NAME_DUPLICATE_SERIAL_INDEX_MSG = "Duplicate serial index. Serial index {0} already exists in {1} center  {2} disease";
    public static final String LEVEL2_DATA_GENERATION_ERR_MSG = "Unexpected error while generating Level2 data cache files for {0}-{1}-{2}-{3}, Error details: {4}\n";
    public static final String FILE_COMPRESS_ERR_MSG = "Unexpected error while compressing cache file {0}. Error details: {1}\n";
    public static final String LEVEL2_DATA_GENERATION_ERR = "Level2 Data Generation Error";

}
