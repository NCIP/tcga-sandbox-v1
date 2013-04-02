package gov.nih.nci.ncicb.tcga.dcc.common.service;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Class description
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileTypeLookupFromMap implements FileTypeLookup {
    private Map<String, String> types = new HashMap<String, String>();
    private String archiveName;
    private String centerName;
    private String platformName;

    public FileTypeLookupFromMap(String archiveName, String centerName, String platformName) {
        this.archiveName = archiveName;
        this.centerName = centerName;
        this.platformName = platformName;
    }

    //assumption now is that only level2 files are added
    public void addFileType(String filename, String type) {
        types.put(filename, type);
    }

    public String lookupFileType( final String filename, final String center, final String platform ) {
        // is this necessary?  this would mean the lookup is somehow matched to the wrong archive
        if (!platformName.equals( platform )) {
            return null;
        }
        if (!centerName.equals( center )) {
            return null;
        }
        return types.get(filename);
    }

    public Map<String, String> getFileToTypeMap() {
        return types;
    }

    public String getArchiveName() {
        return archiveName;
    }

}
