package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.util.TreeSet;

/**
 * Represents a data file that should be created by pasting together all columns from the constituent data files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataFileLevelTwoConsolidated extends DataFileLevelTwo {
    private TreeSet<DataFileLevelTwo> constituentDataFiles;

    public void addConstituentDataFile(final DataFileLevelTwo dataFileLevelTwo) {
        if (constituentDataFiles == null) {
            constituentDataFiles = new TreeSet<DataFileLevelTwo>();
        }
        constituentDataFiles.add(dataFileLevelTwo);
    }

    public void setConstituentDataFiles(final TreeSet<DataFileLevelTwo> constituentDataFiles) {
        this.constituentDataFiles = constituentDataFiles;
    }


    public TreeSet<DataFileLevelTwo> getConstituentDataFiles() {
        return constituentDataFiles;
    }


    @Override
    public boolean isProtected() {
       if (constituentDataFiles != null) {
           for (final DataFileLevelTwo dataFile : constituentDataFiles) {
               if (dataFile.isProtected()) {
                   // if any constituent files are protected, then this is too
                   return true;
               }
           }
       }
        // if no constituent files are protected, then this is not either
        return false;
    }

    @Override
    public boolean isConsolidated() {
        return true;
    }

    @Override
    public String getDisplaySample() {
        return "selected_samples";
    }
}
