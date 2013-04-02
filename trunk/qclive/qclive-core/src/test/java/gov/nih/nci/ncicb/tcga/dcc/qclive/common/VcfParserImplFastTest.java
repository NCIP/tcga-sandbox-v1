package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

/**
 * Test for VCF parser.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfParserImplFastTest {

    private VcfParserImpl vcfParser;
    private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String GOOD_TEST_FILE = SAMPLE_DIR + "qclive/vcf/valid.vcf";
    private static final String MALFORMED_TEST_FILE = SAMPLE_DIR + "qclive/vcf/malformed.vcf";
    private static final String MISSING_HEADER_TEST_FILE = SAMPLE_DIR + "qclive/vcf/missingDataHeader.vcf";
    private static final String BAD_DATA_TEST_FILE = SAMPLE_DIR + "qclive/vcf/badData.vcf";
    private static final String NO_HEADERS_TEST_FILE = SAMPLE_DIR + "qclive/vcf/noHeaders.vcf";
    private static final String MULTIPLE_COLUMN_HEADERS_FILE = SAMPLE_DIR + "qclive/vcf/multipleColumnHeaders.vcf";
    private static final String DUPLICATE_COLUMN_HEADER_KEYS_FILE = SAMPLE_DIR + "qclive/vcf/duplicateColumnHeaderKeys.vcf";
    private static final String EMPTY_HEADER_LINE_FILE = SAMPLE_DIR + "qclive/vcf/emptyHeaderLine.vcf";        
    
    @Test
    public void testParse() throws IOException, ParseException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
    	assertTrue(vcfParser.parse()); 
    	
        VcfFile vcfInfo = vcfParser.getVcfFile();
        Assert.assertNotNull(vcfInfo);

        // check header parsing
        assertEquals(22, vcfInfo.getNumberOfHeaders());

        VcfFileHeader firstHeader = vcfInfo.getHeader(1);
        assertEquals("fileformat", firstHeader.getName());
        assertEquals("VCFv4.0", firstHeader.getValue());
        assertEquals(new Integer(1), firstHeader.getLineNumber());

        assertEquals("fileDate", vcfInfo.getHeader(2).getName());
        assertEquals("20110627", vcfInfo.getHeader(2).getValue());
        assertEquals(new Integer(2), vcfInfo.getHeader(2).getLineNumber());

        // check SAMPLE headers
        List<VcfFileHeader> sampleHeaders = vcfInfo.getHeadersForType("SAMPLE");
        assertEquals(2, sampleHeaders.size());

        // simple value is null since this has value map
        assertNull(sampleHeaders.get(0).getValue());

        assertEquals(new Integer(6), sampleHeaders.get(0).getLineNumber());
        // Check parsing of headers with map values
        Map<String, String> firstSampleHeaderMap = sampleHeaders.get(0).getValueMap();
        assertNotNull(firstSampleHeaderMap);

        // the value of this header is:
        //ID=NORMAL,Individual=TCGA-00-0000,Description="Normal",file=test.bam,platform=Illumina,Source=dbGAP,Accession=.
        assertEquals(7, firstSampleHeaderMap.keySet().size());
        assertEquals("NORMAL", firstSampleHeaderMap.get("ID"));
        assertEquals("TCGA-00-0000", firstSampleHeaderMap.get("Individual"));
        assertEquals("\"Normal\"", firstSampleHeaderMap.get("Description"));
        assertEquals("test.bam", firstSampleHeaderMap.get("file"));
        assertEquals("Illumina", firstSampleHeaderMap.get("platform"));
        assertEquals("dbGAP", firstSampleHeaderMap.get("Source"));
        assertEquals(".", firstSampleHeaderMap.get("Accession"));

        assertEquals(1, vcfInfo.getHeadersForType(VcfFile.HEADER_TYPE_FILTER).size());
        assertEquals(1, vcfInfo.getFilterHeaders().size());


        // check data headers
        assertEquals(11, vcfInfo.getColumnHeader().size());
        assertEquals("CHROM", vcfInfo.getColumnHeader().get(0));
        assertEquals("QUAL", vcfInfo.getColumnHeader().get(5));

        // check data reader/parser
        String[] dataLine1 = vcfParser.getNextDataLine();

        assertArrayEquals(new String[]{"1", "59498", "id1", "G", "<DEL:ME:ALU>", "37", "CA", "VT=SNP;VAS=2", "GT:GQ:DP:AD:FA:MQ60:SS:BQ",
                "0/0:.:468:./1:0.002:0:0:7", "0/1:37:543:./2:0.004:0:0:7" },
                dataLine1);

        assertEquals(11, dataLine1.length);
        assertNotNull(vcfParser.getNextDataLine()); // 2nd data line
        assertNotNull(vcfParser.getNextDataLine()); // 3rd data line
        assertNotNull(vcfParser.getNextDataLine()); // 4th data line
        assertNull(vcfParser.getNextDataLine()); // no more data

        assertEquals(0, vcfParser.getErrors().size());        
        assertEquals(0,vcfParser.getCurrentLineNumber().intValue());
        assertNotNull(vcfParser.getVcfIds());
        assertEquals(4, vcfParser.getVcfIds().size());
        
        vcfParser.close();
    }

    @Test
    public void testMalformedHeaders() throws IOException {
        // test various problems with ## header lines
    	
    	vcfParser = new VcfParserImpl(new File(MALFORMED_TEST_FILE)); 
        assertFalse(vcfParser.parse());    	        
        VcfFile vcfInfo = vcfParser.getVcfFile();

        assertEquals(16, vcfInfo.getNumberOfHeaders());

        assertEquals(5, vcfParser.getErrors().size());
        assertEquals("[malformed.vcf] Header line 1 is improperly formatted: missing '='", vcfParser.getErrors().get(0));
        assertEquals("[malformed.vcf] Header reference: Value map on line 3 is improperly formatted near 'source'", vcfParser.getErrors().get(1));
        assertEquals("[malformed.vcf] Header line 4 is improperly formatted: missing '='", vcfParser.getErrors().get(2));
        assertEquals("[malformed.vcf] Header SAMPLE: Missing closing quotation marks for value of Description on line 6", vcfParser.getErrors().get(3));
        assertEquals("[malformed.vcf] Header on line 13 must start with ##", vcfParser.getErrors().get(4));

        // 13th line is missing ## but we still parse it correctly
        assertEquals("FORMAT", vcfInfo.getHeader(13).getName());
        assertEquals("DP", vcfInfo.getHeader(13).getValueMap().get("ID"));

        // make sure still get all data rows even though header is missing

    }

    @Test
    public void testEmptyLineHeader() throws IOException {
        // test when an otherwise valid file has a completely empty ## header line
    	vcfParser = new VcfParserImpl(new File(EMPTY_HEADER_LINE_FILE)); 
        assertFalse(vcfParser.parse());    
    	        
        VcfFile vcfInfo = vcfParser.getVcfFile();
        assertEquals(20, vcfInfo.getNumberOfHeaders());
        assertEquals(1, vcfParser.getErrors().size());
        assertEquals("[emptyHeaderLine.vcf] Header line 6 is blank", vcfParser.getErrors().get(0));
    }

    @Test
    public void testMissingDataHeader() throws IOException {
        // column header is missing
    	vcfParser = new VcfParserImpl(new File(MISSING_HEADER_TEST_FILE)); 
        assertFalse(vcfParser.parse());     	        

        assertEquals("[missingDataHeader.vcf] Missing column header line", vcfParser.getErrors().get(0));


        int dataLineCount = 0;
        String[] data;
        while((data = vcfParser.getNextDataLine()) != null) {
            dataLineCount++;
            if (dataLineCount == 1) {
                // make sure first data line still accessible even though headers missing
                assertArrayEquals(new String[]{"1", "59498", ".", "G", "A", "37", "CA", "VT=SNP;VAS=2", "GT:GQ:DP:AD:FA:MQ60",
                        "0/0:.:468:./1:0.002:0", "0/1:37:543:./2:0.004:0" },
                        data);
            }
        }

        assertEquals(3, dataLineCount);
        assertEquals(vcfParser.getErrors().toString(), 1, vcfParser.getErrors().size());
    }

    @Test
    public void testBadDataRows() throws IOException {
    	
    	vcfParser = new VcfParserImpl(new File(BAD_DATA_TEST_FILE)); 
        vcfParser.parse();      	        

        int dataLineCount = 0;
        while(vcfParser.getNextDataLine() != null) {
            dataLineCount++;
        }

        assertEquals(3, dataLineCount); // headers are not returned as data even though they occur in data block

        assertEquals(3, vcfParser.getErrors().size());
        assertEquals("[badData.vcf] Line 17 did not contain expected number of columns", vcfParser.getErrors().get(0));
        assertEquals("[badData.vcf] Header line found in file body at line 19", vcfParser.getErrors().get(1));
        assertEquals("[badData.vcf] Header line found in file body at line 21", vcfParser.getErrors().get(2));
    }

    @Test
    public void testParseNoHeaders() throws IOException {
        // this file has no headers at all
    	vcfParser = new VcfParserImpl(new File(NO_HEADERS_TEST_FILE)); 
        vcfParser.parse();      	            	  
        assertEquals(1, vcfParser.getErrors().size());
        assertEquals("[noHeaders.vcf] Missing column header line", vcfParser.getErrors().get(0));
        assertEquals(0, vcfParser.getVcfFile().getNumberOfHeaders());
    }

    @Test
    public void testParseValueMapSimple() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse();   
        Map<String, String> map = vcfParser.parseValueMap(1, "a=b,c=d,e=f", "DUMMY");
        assertEquals(3, map.size());
        assertEquals("b", map.get("a"));
        assertEquals("d", map.get("c"));
        assertEquals("f", map.get("e"));
    }

    @Test
    public void testParseValueMapQuotes() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse();  
        Map<String, String> map = vcfParser.parseValueMap(1, "ID=someId,Type=Test,Description=\"This is a quoted string\"", "DUMMY");
        assertEquals(3, map.size());
        assertEquals("\"This is a quoted string\"", map.get("Description"));
    }

    @Test
    public void testParsevalueMapNotMap() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse();  
        Map<String, String> map = vcfParser.parseValueMap(1, "Hello", "DUMMY");
        assertEquals(0, map.size());
        assertEquals("[valid.vcf] Header DUMMY: Value map on line 1 is improperly formatted near 'Hello'", vcfParser.getErrors().get(0));
    }

    @Test
    public void testParseValueMapEmpty() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse(); 
        Map<String, String> map = vcfParser.parseValueMap(1, "",  "DUMMY");
        assertEquals(0, map.size());
        // no errors, just nothing in map!
    }

    @Test
    public void testParseValueMapNoValue() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse(); 
        Map<String, String> map = vcfParser.parseValueMap(1, "a=b,c=,d=e",  "DUMMY");
        assertEquals(3, map.size());
        assertEquals("", map.get("c"));
    }

    @Test
    public void testParseValueMapWeird() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse(); 
        Map<String, String> map = vcfParser.parseValueMap(1, "cat==dog",  "DUMMY");
        assertEquals(1, map.size());
        assertEquals("=dog", map.get("cat"));
        assertEquals(1, vcfParser.getErrors().size());
        assertEquals("[valid.vcf] Header DUMMY: Value for 'cat' on line 1 contains '=' but is not double-quoted", vcfParser.getErrors().get(0));
    }

    @Test
    public void testParseValueMapMultiDoubleQuotes() throws IOException {
    	vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE)); 
        vcfParser.parse(); 
        Map<String, String> map = vcfParser.parseValueMap(1, "ID=NORMAL,SampleName=TCGA-06-0881-10A-01W,Individual=TCGA-06-0881,Description=\"Normal\",File=TCGA-06-0881-10A-01W-0421-09.bam,Platform=Illumina,Source=dbGAP,Accession=1234,Genomes=<Germline,Tumor>,Mixture=<0.1,0.9>,Genome_Description=<\"Germline contamination\",\"Tumor genome\">",  "DUMMY");
        assertEquals(11, map.size());
        assertEquals("NORMAL", map.get("ID"));
        assertEquals("TCGA-06-0881-10A-01W", map.get("SampleName"));
        assertEquals("TCGA-06-0881", map.get("Individual"));
        assertEquals("\"Normal\"", map.get("Description"));
        assertEquals("TCGA-06-0881-10A-01W-0421-09.bam", map.get("File"));
        assertEquals("Illumina", map.get("Platform"));
        assertEquals("dbGAP", map.get("Source"));
        assertEquals("1234", map.get("Accession"));
        assertEquals("<Germline,Tumor>", map.get("Genomes"));
        assertEquals("<0.1,0.9>", map.get("Mixture"));
        assertEquals("<\"Germline contamination\",\"Tumor genome\">", map.get("Genome_Description"));
        assertEquals(0, vcfParser.getErrors().size());
    }

    @Test
    public void testParseMultipleColumnHeaders() throws IOException {
        // file has multiple column header lines, should use the last one...
    	VcfParser vcfParser = new VcfParserImpl(new File(MULTIPLE_COLUMN_HEADERS_FILE)); 
    	assertFalse(vcfParser.parse());      
    	        
        assertEquals(7, vcfParser.getErrors().size());
        assertEquals("[multipleColumnHeaders.vcf] Multiple column headers found; there should only be one", vcfParser.getErrors().get(6));
        assertEquals(11, vcfParser.getVcfFile().getColumnHeader().size());
        assertEquals("PRIMARY", vcfParser.getVcfFile().getColumnHeader().get(10));
    }

    @Test
    public void testParseDuplicateHeaderKeys() throws Exception {
    	VcfParser vcfParser = new VcfParserImpl(new File(DUPLICATE_COLUMN_HEADER_KEYS_FILE));
        assertTrue(vcfParser.parse());
        assertEquals(1, vcfParser.getErrors().size());
        assertEquals("[duplicateColumnHeaderKeys.vcf] Header PEDIGREE: Key 'Name_0' on line 17 is a duplicate key", vcfParser.getErrors().get(0));
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testIncorectParserInit(){
    	new VcfParserImpl(null);
    }

    @Test
    public void testParseHeaders() throws IOException {
        VcfParser vcfParser = new VcfParserImpl(new File(GOOD_TEST_FILE));
        assertTrue(vcfParser.parseHeaders());
        assertEquals(22, vcfParser.getVcfFile().getNumberOfHeaders());
        assertEquals(0, vcfParser.getVcfIds().size());
    }
    
}
