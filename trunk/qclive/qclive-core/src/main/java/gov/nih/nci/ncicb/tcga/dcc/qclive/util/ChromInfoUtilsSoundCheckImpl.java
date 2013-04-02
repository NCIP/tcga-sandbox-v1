package gov.nih.nci.ncicb.tcga.dcc.qclive.util;


import java.util.regex.Pattern;

/**
 * Implementation of ChromInfoUtils used by SoundCheck which does a minimal validation of the chromosome name:
 *
 * - chromosome name must not be blank or have leading/trailing whitespace
 * - coordinate must be an integer >= 1
 * - genome build is ignored
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfoUtilsSoundCheckImpl extends ChromInfoUtilsImpl {

    private static final Pattern PATTERN = Pattern.compile("[^\\s]+");

    @Override
    public void init() {
        //Do nothing
    }

    /**
     * Return <code>true</code> if the given chromosome name and coordinate are valid, <code>false</code> otherwise.
     *
     * - Chromosome name is valid if it is not blank and has no leading/trailing whitespace
     * - Coordinate is valid if it is an integer >= 1
     * - Genome build is ignored
     *
     * @param chromosomeName the chromosome name
     * @param coordinate the coordinate
     * @param genomeBuildName the genome build name
     * @return <code>true</code> if the given chromosome name and coordinate are valid, <code>false</code> otherwise
     */
    @Override
    public boolean isValidChromCoord(final String chromosomeName, final int coordinate, final String genomeBuildName) {
        return isValidChromValue(chromosomeName) && coordinate >= 1;
    }

    /**
     * Return <code>true</code> if the given chromosome name and coordinate are valid, <code>false</code> otherwise.
     *
     * Chromosome name is valid if it is not blank and has no leading/trailing whitespace.
     *
     * @param chromosomeName the chromosome name to validate
     * @return <code>true</code> if the given chromosome name and coordinate are valid, <code>false</code> otherwise
     */
    @Override
    public boolean isValidChromValue(final String chromosomeName) {
        return PATTERN.matcher(chromosomeName).matches();
    }
}
