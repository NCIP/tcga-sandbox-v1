package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for VcfFile bean, for methods that aren't simple getters and setters.
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileFastTest {
    private VcfFile vcf;
    private VcfFileHeader header1, header2, header3;

    @Before
    public void setup() {
        vcf = new VcfFile();

        header1 = new VcfFileHeader("type1");
        header2 = new VcfFileHeader("type1");
        header3 = new VcfFileHeader("type2");

        vcf.setHeaders(Arrays.asList(header1, header2, header3));
    }

    @Test
    public void testGetHeader() throws Exception {
        assertEquals(header1, vcf.getHeader(1));
        assertEquals(header2, vcf.getHeader(2));
        assertEquals(header3, vcf.getHeader(3));
        assertNull(vcf.getHeader(4));
    }

    @Test
    public void testGetNumHeaders() throws Exception {
        assertEquals(3, vcf.getNumberOfHeaders());
    }

    @Test
    public void testGetHeadersForType() {
        assertEquals(2, vcf.getHeadersForType("type1").size());
        assertEquals(header1, vcf.getHeadersForType("type1").get(0));
        assertEquals(header2, vcf.getHeadersForType("type1").get(1));
        assertEquals(header3, vcf.getHeadersForType("type2").get(0));
        assertEquals(0, vcf.getHeadersForType("something").size());
    }

    @Test
    public void testGetFormatHeaders() {
        header1 = new VcfFileHeader(VcfFile.HEADER_TYPE_FORMAT);
        vcf.addHeader(header1);
        assertEquals(1, vcf.getFormatHeaders().size());
        assertEquals(header1, vcf.getFormatHeaders().get(0));
    }

    @Test
    public void testGetInfoHeaders() {
        header2 = new VcfFileHeader(VcfFile.HEADER_TYPE_INFO);
        vcf.addHeader(header2);
        assertEquals(1, vcf.getInfoHeaders().size());
        assertEquals(header2, vcf.getInfoHeaders().get(0));
    }

    @Test
    public void testGetFilterHeaders() {
        header3 = new VcfFileHeader(VcfFile.HEADER_TYPE_FILTER);
        vcf.addHeader(header3);
        assertEquals(1, vcf.getFilterHeaders().size());
        assertEquals(header3, vcf.getFilterHeaders().get(0));
    }

    @Test
    public void testSetHeadersNull() {
        vcf.setHeaders(null);
        assertEquals(0, vcf.getNumberOfHeaders());
        assertNotNull(vcf.getHeadersForType("something"));
    }

    @Test
    public void testGetSampleColumnsGood() {
        List<String> columnLine = Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE","SAMPLEONE","SAMPLETWO");
        List<String> sampleColumns = VcfFile.getSamplesColumns(columnLine);
        assertEquals(2, sampleColumns.size());
    }

    @Test
    public void testGetSampleColumnsNone() {
        List<String> columnLine = Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE");
        List<String> sampleColumns = VcfFile.getSamplesColumns(columnLine);
        assertEquals(0, sampleColumns.size());
    }

    @Test
    public void testGetSampleColumnsOne() {
        List<String> columnLine = Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE", "TEN");
        List<String> sampleColumns = VcfFile.getSamplesColumns(columnLine);
        assertEquals(1, sampleColumns.size());
    }
}
