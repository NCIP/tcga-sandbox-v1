package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

/**
 * Interface for validating chromosome name, length and genome build.
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ChromInfoUtils {

    /**
     * Return the size of the chromosome given its name and genome build.
     *
     * @param chromosomeName  the chromosome name (case-insensitive)
     * @param genomeBuildName the genome build name (case-insensitive)
     * @return the size of the chromosome given its name and genome build
     * @throws UnknownChromException if the given chromosome name or genome build name do not exist
     */
    public int getChromSizeForGenomeBuild(final String chromosomeName, final String genomeBuildName) throws UnknownChromException;

    /**
     * Return <code>true</code> if the coordinate is valid for the given chromosome and genome build, <code>false</code> otherwise.
     * <p/>
     * A coordinate is valid if: 1 =< coordinate <= chromosome size
     *
     * @param chromosomeName  the chromosome name (case-insensitive)
     * @param coordinate      the coordinate
     * @param genomeBuildName the genome build name (case-insensitive)
     * @return <code>true</code> if the coordinate is valid for the given chromosome (case-insensitive), <code>false</code> otherwise
     */
    boolean isValidChromCoord(final String chromosomeName, final int coordinate, final String genomeBuildName);

    /**
     * Return <code>true</code> if the given chromosome name is valid, <code>false</code> otherwise.
     * <p/>
     * A chromosome name is valid if it appears in at least 1 genome build.
     *
     * @param chromosomeName the chromosome name to validate (case-insensitive)
     * @return <code>true</code> if the given chromosome name is valid, <code>false</code> otherwise
     */
    public boolean isValidChromValue(final String chromosomeName);

    public static class UnknownChromException extends Exception {

        /**
         * Constructs a UnknownChromException with the given message.
         *
         * @param message the exception message
         */
        public UnknownChromException(final String message) {
            super(message);
        }
    }
}
