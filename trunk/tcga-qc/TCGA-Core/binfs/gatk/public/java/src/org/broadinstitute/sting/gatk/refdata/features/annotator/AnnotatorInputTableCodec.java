/*
 * Copyright (c) 2010 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.gatk.refdata.features.annotator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.broad.tribble.Feature;
import org.broad.tribble.exception.CodecLineParsingException;
import org.broad.tribble.readers.AsciiLineReader;
import org.broad.tribble.readers.LineReader;
import org.broadinstitute.sting.gatk.refdata.ReferenceDependentFeatureCodec;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.Utils;

public class AnnotatorInputTableCodec implements ReferenceDependentFeatureCodec<AnnotatorInputTableFeature> {

    private static Logger logger = Logger.getLogger(AnnotatorInputTableCodec.class);

    public static final String DELIMITER = "\t";

    private ArrayList<String> header;

    /**
     * The parser to use when resolving genome-wide locations.
     */
    private GenomeLocParser genomeLocParser;

    /**
     * Set the parser to use when resolving genetic data.
     * @param genomeLocParser The supplied parser.
     */
    public void setGenomeLocParser(GenomeLocParser genomeLocParser) {
        this.genomeLocParser =  genomeLocParser;
    }

    /**
     * Parses the header.
     *
     * @param reader
     *
     * @return The # of header lines for this file.
     */
    public Object readHeader(LineReader reader)
    {
        int[] lineCounter = new int[1];
        try {
            header = readHeader(reader, lineCounter);
        } catch(IOException e) {
            throw new IllegalArgumentException("Unable to read from file.", e);
        }
        return header;
    }

    public Class<AnnotatorInputTableFeature> getFeatureType() {
        return AnnotatorInputTableFeature.class;
    }

    @Override
    public Feature decodeLoc(String line) {
        StringTokenizer st = new StringTokenizer(line, DELIMITER);
        if ( st.countTokens() < 1 )
            throw new CodecLineParsingException("Couldn't parse GenomeLoc out of the following line because there aren't enough tokens.\nLine: " + line);

        GenomeLoc loc;
        String chr = st.nextToken();
        if ( chr.indexOf(":") != -1 ) {
            loc = genomeLocParser.parseGenomeLoc(chr);
        } else {
            if ( st.countTokens() < 3 )
                throw new CodecLineParsingException("Couldn't parse GenomeLoc out of the following line because there aren't enough tokens.\nLine: " + line);
            loc = genomeLocParser.createGenomeLoc(chr, Integer.valueOf(st.nextToken()), Integer.valueOf(st.nextToken()));
        }
        return new AnnotatorInputTableFeature(loc.getContig(), loc.getStart(), loc.getStop());
    }


    /**
     * Parses the line into an AnnotatorInputTableFeature object.
     *
     * @param line
     */
    public AnnotatorInputTableFeature decode(String line) {
        final ArrayList<String> header = this.header; //optimization
        final ArrayList<String> values = Utils.split(line, DELIMITER, header.size());

        if ( values.size() != header.size()) {
            throw new CodecLineParsingException(String.format("Encountered a line that has %d columns while the header has %d columns.\nHeader: " + header + "\nLine: " + values, values.size(), header.size()));
        }

        final AnnotatorInputTableFeature feature = new AnnotatorInputTableFeature(header);
        for ( int i = 0; i < header.size(); i++ ) {
            feature.putColumnValue(header.get(i), values.get(i));
        }

        GenomeLoc loc;
        if ( values.get(0).indexOf(":") != -1 )
            loc = genomeLocParser.parseGenomeLoc(values.get(0));
        else
            loc = genomeLocParser.createGenomeLoc(values.get(0), Integer.valueOf(values.get(1)), Integer.valueOf(values.get(2)));

        //parse the location
        feature.setChr(loc.getContig());
        feature.setStart((int)loc.getStart());
        feature.setEnd((int)loc.getStop());

        return feature;
    }

    /**
     * Returns the header.
     * @param source
     * @return
     * @throws IOException
     */
    public static ArrayList<String> readHeader(final File source) throws IOException {
        FileInputStream is = new FileInputStream(source);
        try {
            return readHeader(new AsciiLineReader(is), null);
        } finally {
            is.close();
        }
    }


    /**
     * Returns the header, and also sets the 2nd arg to the number of lines in the header.
     * @param source
     * @param lineCounter An array of length 1 or null. If not null, array[0] will be set to the number of lines in the header.
     * @return The header fields.
     * @throws IOException
     */
    private static ArrayList<String> readHeader(final LineReader source, int[] lineCounter) throws IOException {

        ArrayList<String> header = null;
        int numLines = 0;

        //find the 1st line that's non-empty and not a comment
        String line = null;
        while( (line = source.readLine()) != null ) {
            numLines++;
            if ( line.trim().isEmpty() || line.startsWith("#") ) {
                continue;
            }

            //parse the header
            header = Utils.split(line, DELIMITER);
            break;
        }

        // check that we found the header
        if ( header == null ) {
            throw new IllegalArgumentException("No header in " + source + ". All lines are either comments or empty.");
        }

        if(lineCounter != null) {
            lineCounter[0] = numLines;
        }

        logger.debug(String.format("Found header line containing %d columns:\n[%s]", header.size(), Utils.join("\t", header)));

        return header;
    }

}
