package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidatorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean that holds VCF (variant Call Format) file information.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFile {

    public static final String HEADER_TYPE_INFO = "INFO";
    public static final String HEADER_TYPE_FORMAT = "FORMAT";
    public static final String HEADER_TYPE_FILTER = "FILTER";
    public static final String HEADER_TYPE_ALT = "ALT";

    public static final String HEADER_TYPE_CHROM = "CHROM";
    public static final String HEADER_TYPE_POS = "POS";
    public static final String HEADER_TYPE_ID = "ID";
    public static final String HEADER_TYPE_REF = "REF";
    public static final String HEADER_TYPE_QUAL = "QUAL";
    public static final String HEADER_TYPE_SAMPLE = "SAMPLE";
    public static final String[] VALID_COLUMN_LINE = {HEADER_TYPE_CHROM, HEADER_TYPE_POS, HEADER_TYPE_ID,
            HEADER_TYPE_REF, HEADER_TYPE_ALT, HEADER_TYPE_QUAL, HEADER_TYPE_FILTER, HEADER_TYPE_INFO};

    public static final String HEADER_TYPE_FILEFORMAT = "fileformat";
    public static final String HEADER_TYPE_TCGA_VERSION = "tcgaversion";

    private List<VcfFileHeader> headers = new ArrayList<VcfFileHeader>();
    private Map<String, List<VcfFileHeader>> headersByType = new HashMap<String, List<VcfFileHeader>>();
    private List<String> columnHeader;


    /**
     * Gets the header for a line of the file.  The first header line is at line number 1.
     * Note: column header is a different kind of header so will not be returned by this.
     * @param lineNum the line number from the file
     * @return the VcfHeader object or null if the line is not a header line
     */
    public VcfFileHeader getHeader(final int lineNum) {
        if (lineNum > getNumberOfHeaders()) {
            return null;
        } else {
            return headers.get(lineNum - 1);
        }
    }

    /**
     * Gets the number of headers. Will return 0 if headers are null.
     * Note this does not include the column header in the count!
     * @return the number of headers
     */
    public int getNumberOfHeaders() {
        return headers == null ? 0 : headers.size();
    }

    /**
     * Sets the VCF headers -- will replace any headers already there.
     * @param headers the list of headers for this vcf
     */
    public void setHeaders(List<VcfFileHeader> headers) {
        this.headers.clear();
        if (headers != null) {
            for (final VcfFileHeader header : headers) {
                addHeader(header);
            }
        }
    }

    /**
     * Adds a header to this vcf.
     *
     * @param header the header object
     */
    public void addHeader(final VcfFileHeader header) {
        if (headers == null) {
            headers = new ArrayList<VcfFileHeader>();
        }
        headers.add(header);
        List<VcfFileHeader> headers = headersByType.get(header.getName().toUpperCase());
        if (headers == null) {
            headers = new ArrayList<VcfFileHeader>();
            headersByType.put(header.getName().toUpperCase(), headers);
        }
        headers.add(header);
    }

    /**
     * Get the array of column header strings for this VCF.  (The column header is also known as the data header.)
     * @return the list of headers for the data
     */
    public List<String> getColumnHeader() {
        return columnHeader;
    }

    /**
     * Sets the column (data) headers for this vcf.  The array of strings represents headers for the data columns in the file
     * @param columnHeader an array of header labels
     */
    public void setColumnHeader(final List<String> columnHeader) {
        this.columnHeader = columnHeader;
    }

    /**
     * Gets all header objects for the given type/name.
     * Any header line that starts with "##NAME=" will be returned by passing in "NAME" as the parameter here.
     * @param headerType the header type/name to find
     * @return all headers with that type/name or an empty list if none
     */
    public List<VcfFileHeader> getHeadersForType(final String headerType) {
        List<VcfFileHeader> headers = headersByType.get(headerType.toUpperCase());
        if (headers == null) {
            return new ArrayList<VcfFileHeader>(0);
        } else {
            return headers;
        }
    }

    /**
     * Gets all headers for this VCF that have the type FILTER
     *
     * @return all FILTER headers
     */
    public List<VcfFileHeader> getFilterHeaders() {
        return getHeadersForType(VcfFile.HEADER_TYPE_FILTER);
    }

    /**
     * Gets all headers for this VCf that have the type FORMAT
     *
     * @return all FORMAT headers
     */
    public List<VcfFileHeader> getFormatHeaders() {
        return getHeadersForType(VcfFile.HEADER_TYPE_FORMAT);
    }

    /**
     * Gets all headers for this VCF that have the type INFO
     *
     * @return all INFO headers
     */
    public List<VcfFileHeader> getInfoHeaders() {
        return getHeadersForType(VcfFile.HEADER_TYPE_INFO);
    }

    /**
     * Return samples data or column headers from the column line (all data/headers after the FORMAT column),
     * or an empty array if none was found. Helper method utilized by vcf validation classes
     *
     * @param columnLine the column line
     * @return samples data from the data line or an empty array if none was found.
     */
    public static List<String> getSamplesColumns(final List<String> columnLine) {

        final int sampleNumber = columnLine.size() - VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() - 1; // column position is 0-based
        final List<String> result = new ArrayList<String>();

        for(int i=0; i<sampleNumber; i++) {
            result.add(columnLine.get(VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + (i+1)));
        }

        return result;
    }

}
