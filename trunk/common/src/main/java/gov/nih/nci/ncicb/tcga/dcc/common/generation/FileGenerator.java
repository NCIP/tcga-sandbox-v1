package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import java.util.List;

/**
 * Interface which provides APIs to generate files
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FileGenerator {
    /**
     * generates the following bio tab files for the given disease.
     * For each biotab filetype
     * - protected file (contains protected data for all the centers)
     * - public file (contains public data for all the centers)
     * - center specific public file (contains public data for specific center)
     * - center specific protected file (contains protected data for specific center)
     * It generates bio tab files in the temp dir and then move those files into bio tab files dir
     *
     * @param diseaseAbbreviation
     * @return returns list of generated bio tab  file names
     * @throws java.io.IOException
     */
    public List<String> generate(String diseaseAbbreviation) throws FileGeneratorException;

     /**
     * generates the following bio tab files for the given XML files.
     * For each biotab filetype
     * - biotab file which contains data for all the xml files
     * - biotab file which contains data for center specific  xml files
     * It generates bio tab files in the temp dir and then move those files into bio tab files dir
     * @param diseaseAbbreviation
     * @param bcrXMLFiles
     * @return  list of generated files
     * @throws FileGeneratorException
     */
    public List<String> generate(String diseaseAbbreviation,final List<String> bcrXMLFiles) throws FileGeneratorException;
}
