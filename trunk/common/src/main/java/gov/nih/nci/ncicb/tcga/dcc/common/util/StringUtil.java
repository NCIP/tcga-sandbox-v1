package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.MD5_EXTENSION;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.PROTECTED_DIR;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.PUBLIC_DIR;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;

/**
 * Utility class for text data
 *
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
public class StringUtil {


    //
    // Regular expressions for leading and trailing whitespace
    //
    private static final String LEADING_WHITESPACE = "^\\s+";
    private static final String TRAILING_WHITESPACE = "\\s+$";

    //
    // Pre-compiled Pattern Hashtable (RegExp -> Pattern), for performance improvement
    //
    private static final Hashtable<String, Pattern> PATTERN_HASHTABLE = new Hashtable<String, Pattern>();

    static {
        PATTERN_HASHTABLE.put(LEADING_WHITESPACE, Pattern.compile(LEADING_WHITESPACE));
        PATTERN_HASHTABLE.put(TRAILING_WHITESPACE, Pattern.compile(TRAILING_WHITESPACE));
    }

    /**
     * Creates a place holder string for the given max number of parameters.
     * <p/>
     * E.g. If maxParameter = 2 the return String will be "?,?"
     *
     * @param maxParameter    the number of parameters
     * @param caseSensitivity
     * @return a place holder string for the given max number of parameters
     */
    public static String createPlaceHolderString(int maxParameter,
                                                 final CaseSensitivity caseSensitivity) {

        final StringBuilder placeHolderString = new StringBuilder();

        for (int i = 0; i < maxParameter; i++) {

            switch (caseSensitivity) {
                case CASE_SENSITIVE:
                    placeHolderString.append("?,");
                    break;
                case LOWER_CASE:
                    placeHolderString.append("lower(?),");
                    break;
                case UPPER_CASE:
                    placeHolderString.append("upper(?),");
                    break;
            }
        }

        placeHolderString.deleteCharAt(placeHolderString.length() - 1);

        return placeHolderString.toString();

    }

    /**
     * Creates a place holder string for the given max number of parameters.
     * <p/>
     * E.g. If maxParameter = 2 the return String will be "?,?"
     *
     * @param maxParameter the number of parameters
     * @return a place holder string for the given max number of parameters
     */
    public static String createPlaceHolderString(int maxParameter) {
        return createPlaceHolderString(maxParameter, CaseSensitivity.CASE_SENSITIVE);
    }

    public static String truncate(String word, int length) {
        String res = "";
        if (word != null) {
            if (word.length() > length) {
                res = word.substring(0, length) + "...";
            } else res = word;
        }
        return res;
    }

    /**
     * Return <code>true</code> if the given input has leading whitespace, <code>false</code> otherwise
     *
     * @param input the input to test
     * @return <code>true</code> if the given input has leading whitespace, <code>false</code> otherwise
     */
    public static boolean hasLeadingWhitespace(final String input) {

        return PATTERN_HASHTABLE.get(LEADING_WHITESPACE).matcher(input).find();
    }

    /**
     * Return <code>true</code> if the given input has trailing whitespace, <code>false</code> otherwise
     *
     * @param input the input to test
     * @return <code>true</code> if the given input has trailing whitespace, <code>false</code> otherwise
     */
    public static boolean hasTrailingWhitespace(final String input) {

        return PATTERN_HASHTABLE.get(TRAILING_WHITESPACE).matcher(input).find();
    }

    /**
     * Return the <code>Exception</code>'s stack trace as a <code>String</code>
     *
     * @param e the <code>Exception</code> holding the stack trace
     * @return a <code>String</code> holding the entire stack trace
     */
    public static String stackTraceAsString(Exception e) {

        String result = null;

        PrintWriter printWriter = null;
        try {
            final Writer writer = new StringWriter();
            //noinspection IOResourceOpenedButNotSafelyClosed
            printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            result = writer.toString();
        } finally {
            IOUtils.closeQuietly(printWriter);
        }

        return result;
    }

    public static String convertListToDelimitedString(final List<String> list,
                                                      final char delimiter) {
        final StringBuilder delimitedString = new StringBuilder();
        for (String str : list) {
            delimitedString.append(str)
                    .append(delimiter);
        }
        delimitedString.deleteCharAt(delimitedString.length() - 1);
        return delimitedString.toString();
    }

    /**
     * Return <code>true</code> if the item is contained in the list (case-insensitive match), <code>false</code> otherwise.
     *
     * @param list the list to search in
     * @param item the item to match (case-insensitive)
     * @return <code>true</code> if the item is contained in the list (case-insensitive match), <code>false</code> otherwise
     */
    public static boolean containsIgnoreCase(final List<String> list, final String item) {

        boolean result = false;

        final Iterator<String> stringIterator = list.iterator();
        while (result == false && stringIterator.hasNext()) {
            if (stringIterator.next().equalsIgnoreCase(item)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Take a list and return a copy for which each item is normalized:
     * <p/>
     * - no leading and trailing whitespace
     * - no duplicates items
     * - no <code>null</code> items
     * <p/>
     * If the given list is <code>null</code>, then return <code>null</code>.
     *
     * @param list the list to normalize
     * @return a normalized copy of the list
     */
    public static List<String> normalize(final List<String> list) {

        List<String> result = null;

        if (list != null) {

            result = new ArrayList<String>();

            for (final String listItem : list) {

                if (listItem != null) {

                    final String trimmedListItem = listItem.trim();

                    if (!result.contains(trimmedListItem)) {
                        result.add(trimmedListItem);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Create an insert statement for the given table and columns, with as many placeholders as columns
     *
     * @param tableName   the table name
     * @param columnNames a {@link List} of the column names
     * @return the insert statement
     */
    public static String createInsertStatement(final String tableName,
                                               final List<String> columnNames) {

        final StringBuilder stringBuilder = new StringBuilder("insert into ");
        stringBuilder.append(tableName)
                .append(" (")
                .append(convertListToDelimitedString(columnNames, ','))
                .append(") values (")
                .append(createPlaceHolderString(columnNames.size()))
                .append(")");

        return stringBuilder.toString();
    }

    /**
     * Return the public deploy location equivalent of the given protected deploy location.
     *
     * @param protectedDeployLocation the protected deploy location
     * @return the public deploy location equivalent of the given protected deploy location
     */
    public static String getPublicDeployLocation(final String protectedDeployLocation) {

        String result = null;

        if (protectedDeployLocation != null && protectedDeployLocation.contains(PROTECTED_DIR)) {

            result = protectedDeployLocation.replace(PROTECTED_DIR, PUBLIC_DIR);
        }

        return result;
    }

    /**
     * Return <code>true</code> if the given archive location is a valid deploy location for tar or tar.gz protected archives.
     *
     * @param tarOrTarGzProtectedDeployLocation
     *         the archive location to validate
     * @return <code>true</code> if the given archive location is a valid deploy location for tar or tar.gz protected archives
     */
    public static boolean isValidTarOrTarGzProtectedDeployLocation(final String tarOrTarGzProtectedDeployLocation) {

        boolean result = false;

        if (tarOrTarGzProtectedDeployLocation != null) {

            final String pathToLowerCase = tarOrTarGzProtectedDeployLocation.toLowerCase();
            result = (pathToLowerCase.endsWith(UNCOMPRESSED_ARCHIVE_EXTENSION) || pathToLowerCase.endsWith(COMPRESSED_ARCHIVE_EXTENSION))
                    && tarOrTarGzProtectedDeployLocation.toLowerCase().contains(PROTECTED_DIR);
        }

        return result;
    }

    /**
     * Return the given path appended with the md5 extension, or <code>null</code> if the given path is <code>null</code>.
     *
     * @param tarOrTarGzProtectedDeployLocation
     *         the path to a tar or tar.gz protected deploy location
     * @return the given path appended with the md5 extension, or <code>null</code> if the given path is <code>null</code>
     */
    public static String getMd5ProtectedDeployLocation(final String tarOrTarGzProtectedDeployLocation) {
        return tarOrTarGzProtectedDeployLocation == null ? null : tarOrTarGzProtectedDeployLocation + MD5_EXTENSION;
    }

    /**
     * Return the path to the exploded directory from the given archive deploy location, or <code>null</code> if it could not be determined.
     *
     * @param tarOrTarGzProtectedDeployLocation
     *         the path to a tar or tar.gz protected deploy location
     * @return the path to the exploded directory from the given archive deploy location, or <code>null</code> if it could not be determined.
     */
    public static String getExplodedProtectedDeployLocation(final String tarOrTarGzProtectedDeployLocation) {

        String result = null;

        if (tarOrTarGzProtectedDeployLocation != null) {

            if (tarOrTarGzProtectedDeployLocation.endsWith(COMPRESSED_ARCHIVE_EXTENSION)) {
                result = tarOrTarGzProtectedDeployLocation.substring(0, tarOrTarGzProtectedDeployLocation.lastIndexOf(COMPRESSED_ARCHIVE_EXTENSION));
            } else if (tarOrTarGzProtectedDeployLocation.endsWith(UNCOMPRESSED_ARCHIVE_EXTENSION)) {
                result = tarOrTarGzProtectedDeployLocation.substring(0, tarOrTarGzProtectedDeployLocation.lastIndexOf(UNCOMPRESSED_ARCHIVE_EXTENSION));
            }
        }

        return result;
    }

    /**
     * Enum to use for building queries depending on case sensitivity need.
     */
    public static enum CaseSensitivity {
        CASE_SENSITIVE,
        LOWER_CASE,
        UPPER_CASE
    }

    /**
     * replace all whitespaces into space character
     *
     * @param str
     * @return transformed string
     */
    public static String spaceAllWhitespace(final String str) {
        if (str == null || str.length() <= 0) {
            return str;
        }
        final StringBuilder sb = new StringBuilder(str);
        int index = 0;
        while (sb.length() > index) {
            if (Character.isWhitespace(sb.charAt(index)) &&
                    sb.charAt(index) != ' ') {
                sb.replace(index, index + 1, " ");
            } else {
                index++;
            }
        }
        return sb.toString();
    }
}
