package org.broadinstitute.sting.utils.codecs.vcf;

import org.apache.log4j.Logger;
import org.broad.tribble.Feature;
import org.broad.tribble.FeatureCodec;
import org.broad.tribble.NameAwareCodec;
import org.broad.tribble.TribbleException;
import org.broad.tribble.readers.LineReader;
import org.broad.tribble.util.ParsingUtils;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

import java.util.*;


public abstract class AbstractVCFCodec implements FeatureCodec, NameAwareCodec, VCFParser {

    protected final static Logger log = Logger.getLogger(VCFCodec.class);
    protected final static int NUM_STANDARD_FIELDS = 8;  // INFO is the 8th column

    protected VCFHeaderVersion version;

    // we have to store the list of strings that make up the header until they're needed
    protected VCFHeader header = null;

    // a mapping of the allele
    protected Map<String, List<Allele>> alleleMap = new HashMap<String, List<Allele>>(3);

    // for ParsingUtils.split
    protected String[] GTValueArray = new String[100];
    protected String[] genotypeKeyArray = new String[100];
    protected String[] infoValueArray = new String[1000];

    // for performance testing purposes
    public static boolean validate = true;

    // a key optimization -- we need a per thread string parts array, so we don't allocate a big array over and over
    // todo: make this thread safe?
    protected String[] parts = null;
    protected String[] genotypeParts = null;

    // for performance we cache the hashmap of filter encodings for quick lookup
    protected HashMap<String,LinkedHashSet<String>> filterHash = new HashMap<String,LinkedHashSet<String>>();

    // a mapping of the VCF fields to their type, filter fields, and format fields, for quick lookup to validate against
    TreeMap<String, VCFHeaderLineType> infoFields = new TreeMap<String, VCFHeaderLineType>();
    TreeMap<String, VCFHeaderLineType> formatFields = new TreeMap<String, VCFHeaderLineType>();
    Set<String> filterFields = new HashSet<String>();

    // we store a name to give to each of the variant contexts we emit
    protected String name = "Unknown";

    protected int lineNo = 0;

    protected Map<String, String> stringCache = new HashMap<String, String>();


    /**
     * @param reader the line reader to take header lines from
     * @return the number of header lines
     */
    public abstract Object readHeader(LineReader reader);

    /**
     * create a genotype map
     * @param str the string
     * @param alleles the list of alleles
     * @param chr chrom
     * @param pos position
     * @return a mapping of sample name to genotype object
     */
    public abstract Map<String, Genotype> createGenotypeMap(String str, List<Allele> alleles, String chr, int pos);


    /**
     * parse the filter string, first checking to see if we already have parsed it in a previous attempt
     * @param filterString the string to parse
     * @return a set of the filters applied
     */
    protected abstract Set<String> parseFilters(String filterString);

    /**
     * create a VCF header
     * @param headerStrings a list of strings that represent all the ## entries
     * @param line the single # line (column names)
     * @return the count of header lines
     */
    protected Object createHeader(List<String> headerStrings, String line) {

        headerStrings.add(line);

        Set<VCFHeaderLine> metaData = new TreeSet<VCFHeaderLine>();
        Set<String> auxTags = new LinkedHashSet<String>();
        // iterate over all the passed in strings
        for ( String str : headerStrings ) {
            if ( !str.startsWith(VCFHeader.METADATA_INDICATOR) ) {
                String[] strings = str.substring(1).split(VCFConstants.FIELD_SEPARATOR);
                int arrayIndex = 0;
                for (VCFHeader.HEADER_FIELDS field : VCFHeader.HEADER_FIELDS.values()) {
                    try {
                        if (field != VCFHeader.HEADER_FIELDS.valueOf(strings[arrayIndex]))
                            throw new TribbleException.InvalidHeader("we were expecting column name '" + field + "' but we saw '" + strings[arrayIndex] + "'");
                    } catch (IllegalArgumentException e) {
                        throw new TribbleException.InvalidHeader("unknown column name '" + strings[arrayIndex] + "'; it does not match a legal column header name.");
                    }
                    arrayIndex++;
                }
                if ( arrayIndex < strings.length ) {
                    if ( !strings[arrayIndex].equals("FORMAT") )
                        throw new TribbleException.InvalidHeader("we were expecting column name 'FORMAT' but we saw '" + strings[arrayIndex] + "'");
                    arrayIndex++;
                }

                while (arrayIndex < strings.length)
                    auxTags.add(strings[arrayIndex++]);

            } else {
                if ( str.startsWith("##INFO=") ) {
                    VCFInfoHeaderLine info = new VCFInfoHeaderLine(str.substring(7),version);
                    metaData.add(info);
                    infoFields.put(info.getName(), info.getType());
                } else if ( str.startsWith("##FILTER=") ) {
                    VCFFilterHeaderLine filter = new VCFFilterHeaderLine(str.substring(9),version);
                    metaData.add(filter);
                    filterFields.add(filter.getName());
                } else if ( str.startsWith("##FORMAT=") ) {
                    VCFFormatHeaderLine format = new VCFFormatHeaderLine(str.substring(9),version);
                    metaData.add(format);
                    formatFields.put(format.getName(), format.getType());
                } else {
                    int equals = str.indexOf("=");
                    if ( equals != -1 )
                        metaData.add(new VCFHeaderLine(str.substring(2, equals), str.substring(equals+1)));
                }
            }
        }

        header = new VCFHeader(metaData, auxTags);
        return header;
    }

    /**
     * the fast decode function
     * @param line the line of text for the record
     * @return a feature, (not guaranteed complete) that has the correct start and stop
     */
    public Feature decodeLoc(String line) {
        return reallyDecode(line);
    }

    /**
     * decode the line into a feature (VariantContext)
     * @param line the line
     * @return a VariantContext
     */
    public Feature decode(String line) {
        return reallyDecode(line);
    }

    private Feature reallyDecode(String line) {
        try {
            // the same line reader is not used for parsing the header and parsing lines, if we see a #, we've seen a header line
            if (line.startsWith(VCFHeader.HEADER_INDICATOR)) return null;

            // our header cannot be null, we need the genotype sample names and counts
            if (header == null) throw new IllegalStateException("VCF Header cannot be null when decoding a record");

            if (parts == null)
                parts = new String[Math.min(header.getColumnCount(), NUM_STANDARD_FIELDS+1)];

            int nParts = ParsingUtils.split(line, parts, VCFConstants.FIELD_SEPARATOR_CHAR, true);

            // if we have don't have a header, or we have a header with no genotyping data check that we have eight columns.  Otherwise check that we have nine (normal colummns + genotyping data)
            if (( (header == null || (header != null && !header.hasGenotypingData())) && nParts != NUM_STANDARD_FIELDS) ||
                 (header != null && header.hasGenotypingData() && nParts != (NUM_STANDARD_FIELDS + 1)) )
                throw new IllegalArgumentException("There aren't enough columns for line " + line + " (we expected " + (header == null ? NUM_STANDARD_FIELDS : NUM_STANDARD_FIELDS + 1) +
                        " tokens, and saw " + nParts + " )");

            return parseVCFLine(parts);
        } catch (TribbleException e) {
            throw new TribbleException.InvalidDecodeLine(e.getMessage(), line);
        }
    }

    protected void generateException(String message) {
        throw new TribbleException.InvalidDecodeLine(message, lineNo);
    }

    /**
     * parse out the VCF line
     *
     * @param parts the parts split up
     * @return a variant context object
     */
    private VariantContext parseVCFLine(String[] parts) {
        // increment the line count
        lineNo++;

        // parse out the required fields
        String contig = getCachedString(parts[0]);
        long pos = Long.valueOf(parts[1]);
        String id = null;
        if ( parts[2].length() == 0 )
            generateException("The VCF specification requires a valid ID field");
        else if ( parts[2].equals(VCFConstants.EMPTY_ID_FIELD) )
            id = VCFConstants.EMPTY_ID_FIELD;
        else
            id = new String(parts[2]);
        String ref = getCachedString(parts[3].toUpperCase());
        String alts = getCachedString(parts[4].toUpperCase());
        Double qual = parseQual(parts[5]);
        String filter = getCachedString(parts[6]);
        String info = new String(parts[7]);

        // get our alleles, filters, and setup an attribute map
        List<Allele> alleles = parseAlleles(ref, alts, lineNo);
        Set<String> filters = parseFilters(filter);
        Map<String, Object> attributes = parseInfo(info, id);

        // find out our current location, and clip the alleles down to their minimum length
        long loc = pos;
        // ref alleles don't need to be single bases for monomorphic sites
        if ( alleles.size() == 1 ) {
            loc = pos + alleles.get(0).length() - 1;
        } else if ( !isSingleNucleotideEvent(alleles) ) {
            ArrayList<Allele> newAlleles = new ArrayList<Allele>();
            loc = clipAlleles(pos, ref, alleles, newAlleles);
            alleles = newAlleles;
        }

        // do we have genotyping data
        if (parts.length > NUM_STANDARD_FIELDS) {
            attributes.put(VariantContext.UNPARSED_GENOTYPE_MAP_KEY, new String(parts[8]));
            attributes.put(VariantContext.UNPARSED_GENOTYPE_PARSER_KEY, this);
        }

        VariantContext vc = null;
        try {
            vc =  new VariantContext(name, contig, pos, loc, alleles, qual, filters, attributes);
        } catch (Exception e) {
            generateException(e.getMessage());
        }

        // did we resort the sample names?  If so, we need to load the genotype data
        if ( !header.samplesWereAlreadySorted() )
            vc.getGenotypes();

        // Trim bases of all alleles if necessary
        return createVariantContextWithTrimmedAlleles(vc);
    }

    /**
     *
     * @return the type of record
     */
    public Class getFeatureType() {
        return VariantContext.class;
    }

    /**
     * get the name of this codec
     * @return our set name
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of this codec
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return a cached copy of the supplied string.
     *
     * @param str string
     * @return interned string
     */
    protected String getCachedString(String str) {
        String internedString = stringCache.get(str);
        if ( internedString == null ) {
            internedString = new String(str);
            stringCache.put(internedString, internedString);
        }
        return internedString;
    }

    /**
     * parse out the info fields
     * @param infoField the fields
     * @param id the indentifier
     * @return a mapping of keys to objects
     */
    private Map<String, Object> parseInfo(String infoField, String id) {
        Map<String, Object> attributes = new HashMap<String, Object>();

        if ( infoField.length() == 0 )
            generateException("The VCF specification requires a valid info field");

        if ( !infoField.equals(VCFConstants.EMPTY_INFO_FIELD) ) {
            int infoValueSplitSize = ParsingUtils.split(infoField, infoValueArray, VCFConstants.INFO_FIELD_SEPARATOR_CHAR);
            for (int i = 0; i < infoValueSplitSize; i++) {
                String key;
                Object value;

                int eqI = infoValueArray[i].indexOf("=");
                if ( eqI != -1 ) {
                    key = infoValueArray[i].substring(0, eqI);
                    String str = infoValueArray[i].substring(eqI+1, infoValueArray[i].length());

                    // lets see if the string contains a , separator
                    if ( str.contains(",") )
                        value = Arrays.asList(str.split(","));
                    else
                        value = str;
                } else {
                    key = infoValueArray[i];
                    value = true;
                }

                attributes.put(key, value);
            }
        }

        attributes.put(VariantContext.ID_KEY, id);
        return attributes;
    }

    /**
     * create a an allele from an index and an array of alleles
     * @param index the index
     * @param alleles the alleles
     * @return an Allele
     */
    protected static Allele oneAllele(String index, List<Allele> alleles) {
        if ( index.equals(VCFConstants.EMPTY_ALLELE) )
            return Allele.NO_CALL;
        int i = Integer.valueOf(index);
        if ( i >= alleles.size() )
            throw new TribbleException.InternalCodecException("The allele with index " + index + " is not defined in the REF/ALT columns in the record");
        return alleles.get(i);
    }


    /**
     * parse genotype alleles from the genotype string
     * @param GT         GT string
     * @param alleles    list of possible alleles
     * @param cache      cache of alleles for GT
     * @return the allele list for the GT string
     */
    protected static List<Allele> parseGenotypeAlleles(String GT, List<Allele> alleles, Map<String, List<Allele>> cache) {
        // cache results [since they are immutable] and return a single object for each genotype
        List<Allele> GTAlleles = cache.get(GT);

        if ( GTAlleles == null ) {
            StringTokenizer st = new StringTokenizer(GT, VCFConstants.PHASING_TOKENS);
            GTAlleles = new ArrayList<Allele>(st.countTokens());
            while ( st.hasMoreTokens() ) {
                String genotype = st.nextToken();
                GTAlleles.add(oneAllele(genotype, alleles));
            }
            cache.put(GT, GTAlleles);
        }

        return GTAlleles;
    }

    /**
     * parse out the qual value
     * @param qualString the quality string
     * @return return a double
     */
    protected static Double parseQual(String qualString) {
        // if we're the VCF 4 missing char, return immediately
        if ( qualString.equals(VCFConstants.MISSING_VALUE_v4))
            return VariantContext.NO_NEG_LOG_10PERROR;

        Double val = Double.valueOf(qualString);

        // check to see if they encoded the missing qual score in VCF 3 style, with either the -1 or -1.0.  check for val < 0 to save some CPU cycles
        if ((val < 0) && (Math.abs(val - VCFConstants.MISSING_QUALITY_v3_DOUBLE) < VCFConstants.VCF_ENCODING_EPSILON))
            return VariantContext.NO_NEG_LOG_10PERROR;

        // scale and return the value
        return val / 10.0;
    }

    /**
     * parse out the alleles
     * @param ref the reference base
     * @param alts a string of alternates to break into alleles
     * @param lineNo  the line number for this record
     * @return a list of alleles, and a pair of the shortest and longest sequence
     */
    protected static List<Allele> parseAlleles(String ref, String alts, int lineNo) {
        List<Allele> alleles = new ArrayList<Allele>(2); // we are almost always biallelic
        // ref
        checkAllele(ref, true, lineNo);
        Allele refAllele = Allele.create(ref, true);
        alleles.add(refAllele);

        if ( alts.indexOf(",") == -1 ) // only 1 alternatives, don't call string split
            parseSingleAltAllele(alleles, alts, lineNo);
        else
            for ( String alt : alts.split(",") )
                parseSingleAltAllele(alleles, alt, lineNo);

        return alleles;
    }

    /**
     * check to make sure the allele is an acceptable allele
     * @param allele the allele to check
     * @param isRef are we the reference allele?
     * @param lineNo  the line number for this record
     */
    private static void checkAllele(String allele, boolean isRef, int lineNo) {
	if ( allele == null || allele.length() == 0 )
	    generateException("Empty alleles are not permitted in VCF records", lineNo);

        if ( isSymbolicAllele(allele) ) {
            if ( isRef ) {
                generateException("Symbolic alleles not allowed as reference allele: " + allele, lineNo);
            }
        } else {
            // check for VCF3 insertions or deletions
            if ( (allele.charAt(0) == VCFConstants.DELETION_ALLELE_v3) || (allele.charAt(0) == VCFConstants.INSERTION_ALLELE_v3) )
                generateException("Insertions/Deletions are not supported when reading 3.x VCF's. Please" +
                        " convert your file to VCF4 using VCFTools, available at http://vcftools.sourceforge.net/index.html", lineNo);

            if (!Allele.acceptableAlleleBases(allele))
                generateException("Unparsable vcf record with allele " + allele, lineNo);

            if ( isRef && allele.equals(VCFConstants.EMPTY_ALLELE) )
                generateException("The reference allele cannot be missing", lineNo);
        }
    }

    /**
     * return true if this is a symbolic allele (e.g. <SOMETAG>) otherwise false
     * @param allele the allele to check
     * @return true if the allele is a symbolic allele, otherwise false
     */
    private static boolean isSymbolicAllele(String allele) {
        return (allele != null && allele.startsWith("<") && allele.endsWith(">") && allele.length() > 2);
    }

    /**
     * parse a single allele, given the allele list
     * @param alleles the alleles available
     * @param alt the allele to parse
     * @param lineNo  the line number for this record
     */
    private static void parseSingleAltAllele(List<Allele> alleles, String alt, int lineNo) {
        checkAllele(alt, false, lineNo);

        Allele allele = Allele.create(alt, false);
        if ( ! allele.isNoCall() )
            alleles.add(allele);
    }

    protected static boolean isSingleNucleotideEvent(List<Allele> alleles) {
        for ( Allele a : alleles ) {
            if ( a.length() != 1 )
                return false;
        }
        return true;
    }

    private static void generateException(String message, int lineNo) {
        throw new TribbleException.InvalidDecodeLine(message, lineNo);
    }

    private static int computeForwardClipping(List<Allele> unclippedAlleles, String ref) {
        boolean clipping = true;
        // Note that the computation of forward clipping here is meant only to see whether there is a common
        // base to all alleles, and to correctly compute reverse clipping,
        // but it is not used for actually changing alleles - this is done in function
        // createVariantContextWithTrimmedAlleles() below.

        for (Allele a : unclippedAlleles) {
            if (a.isSymbolic()) {
                continue;
            }
            if (a.length() < 1 || (a.getBases()[0] != ref.getBytes()[0])) {
                clipping = false;
            }
        }
        return (clipping) ? 1 : 0;

    }

    /**
     * clip the alleles, based on the reference
     *
     * @param position the unadjusted start position (pre-clipping)
     * @param ref the reference string
     * @param unclippedAlleles the list of unclipped alleles
     * @param clippedAlleles output list of clipped alleles
     * @return a list of alleles, clipped to the reference
     */
    protected static long clipAlleles(long position, String ref, List<Allele> unclippedAlleles, List<Allele> clippedAlleles) {

        // Note that the computation of forward clipping here is meant only to see whether there is a common
        // base to all alleles, and to correctly compute reverse clipping,
        // but it is not used for actually changing alleles - this is done in function
        // createVariantContextWithTrimmedAlleles() below.

        int forwardClipping = computeForwardClipping(unclippedAlleles, ref);

        int reverseClipped = 0;
        boolean clipping = true;
        while (clipping) {
            for (Allele a : unclippedAlleles) {
                if (a.isSymbolic()) {
                    continue;
                }
                if (a.length() - reverseClipped <= forwardClipping || a.length() - forwardClipping == 0)
                    clipping = false;
                else if (a.getBases()[a.length()-reverseClipped-1] != ref.getBytes()[ref.length()-reverseClipped-1])
                    clipping = false;
            }
            if (clipping) reverseClipped++;
        }

        for (Allele a : unclippedAlleles) {
            if (a.isSymbolic()) {
                clippedAlleles.add(a);
            } else {
                clippedAlleles.add(Allele.create(Arrays.copyOfRange(a.getBases(),0,a.getBases().length-reverseClipped),a.isReference()));
            }
        }

        // the new reference length
        int refLength = ref.length() - reverseClipped;

        return position+Math.max(refLength - 1,0);
    }

    public static VariantContext createVariantContextWithTrimmedAlleles(VariantContext inputVC) {
        // see if we need to trim common reference base from all alleles
        boolean trimVC;

        // We need to trim common reference base from all alleles in all genotypes if a ref base is common to all alleles
        Allele refAllele = inputVC.getReference();
        if (!inputVC.isVariant())
            trimVC = false;
        else if (refAllele.isNull())
            trimVC = false;
        else {
            trimVC = (computeForwardClipping(new ArrayList<Allele>(inputVC.getAlternateAlleles()),
                    inputVC.getReference().getDisplayString()) > 0);
         }

        // nothing to do if we don't need to trim bases
        if (trimVC) {
            List<Allele> alleles = new ArrayList<Allele>();
            Map<String, Genotype> genotypes = new TreeMap<String, Genotype>();

            // set the reference base for indels in the attributes
            Map<String,Object> attributes = new TreeMap<String,Object>(inputVC.getAttributes());
            attributes.put(VariantContext.REFERENCE_BASE_FOR_INDEL_KEY, new Byte(inputVC.getReference().getBases()[0]));

            Map<Allele, Allele> originalToTrimmedAlleleMap = new HashMap<Allele, Allele>();

            for (Allele a : inputVC.getAlleles()) {
                if (a.isSymbolic()) {
                    alleles.add(a);
                    originalToTrimmedAlleleMap.put(a, a);
                } else {
                    // get bases for current allele and create a new one with trimmed bases
                    byte[] newBases = Arrays.copyOfRange(a.getBases(), 1, a.length());
                    Allele trimmedAllele = Allele.create(newBases, a.isReference());
                    alleles.add(trimmedAllele);
                    originalToTrimmedAlleleMap.put(a, trimmedAllele);
                }
            }

            // detect case where we're trimming bases but resulting vc doesn't have any null allele. In that case, we keep original representation
            // example: mixed records such as {TA*,TGA,TG}
            boolean hasNullAlleles = false;

            for (Allele a: originalToTrimmedAlleleMap.values()) {
                if (a.isNull())
                    hasNullAlleles = true;
                if (a.isReference())
                    refAllele = a;
             }

             if (!hasNullAlleles)
               return inputVC;
           // now we can recreate new genotypes with trimmed alleles
            for ( Map.Entry<String, Genotype> sample : inputVC.getGenotypes().entrySet() ) {

                List<Allele> originalAlleles = sample.getValue().getAlleles();
                List<Allele> trimmedAlleles = new ArrayList<Allele>();
                for ( Allele a : originalAlleles ) {
                    if ( a.isCalled() )
                        trimmedAlleles.add(originalToTrimmedAlleleMap.get(a));
                    else
                        trimmedAlleles.add(Allele.NO_CALL);
                }
                genotypes.put(sample.getKey(), Genotype.modifyAlleles(sample.getValue(), trimmedAlleles));

            }
            return new VariantContext(inputVC.getSource(), inputVC.getChr(), inputVC.getStart(), inputVC.getEnd(), alleles, genotypes, inputVC.getNegLog10PError(), inputVC.filtersWereApplied() ? inputVC.getFilters() : null, attributes);

        }

        return inputVC;
    }
}
