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

package org.broadinstitute.sting.gatk.walkers;

import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;
import org.broadinstitute.sting.gatk.refdata.ReadMetaDataTracker;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.utils.baq.BAQ;

import java.io.PrintStream;

/**
 * Renders, in SAM/BAM format, all reads from the input data set in the order in which they appear
 * in the input file.  It can dynamically merge the contents of multiple input BAM files, resulting
 * in merged output sorted in coordinate order.  Can also optionally filter reads based on the --read-filter
 * command line argument.
 */
@BAQMode(QualityMode = BAQ.QualityMode.ADD_TAG, ApplicationTime = BAQ.ApplicationTime.ON_OUTPUT)
@Requires({DataSource.READS, DataSource.REFERENCE})
public class PrintReadsWalker extends ReadWalker<SAMRecord, SAMFileWriter> {
    /** an optional argument to dump the reads out to a BAM file */
    @Output(doc="Write output to this BAM filename instead of STDOUT")
    SAMFileWriter out;
    @Argument(fullName = "readGroup", shortName = "readGroup", doc="Exclude all reads with this read group from the output", required = false)
    String readGroup = null;
    @Argument(fullName = "platform", shortName = "platform", doc="Exclude all reads with this platform from the output", required = false)
    String platform = null; // E.g. ILLUMINA, 454
    @Argument(fullName = "number", shortName = "n", doc="Print the first n reads from the file, discarding the rest", required = false)
    int nReadsToPrint = -1;

    /**
     * The initialize function.
     */
    public void initialize() {
        if  ( platform != null )
            platform = platform.toUpperCase();
    }

    /**
     * The reads filter function.
     * @param ref the reference bases that correspond to our read, if a reference was provided
     * @param read the read itself, as a SAMRecord
     * @return true if the read passes the filter, false if it doesn't
     */
    public boolean filter(ReferenceContext ref, SAMRecord read) {
        // check the read group
        if  ( readGroup != null ) {
            SAMReadGroupRecord myReadGroup = read.getReadGroup();
            if ( myReadGroup == null || !readGroup.equals(myReadGroup.getReadGroupId()) )
                return false;
        }

        // check the platform
        if  ( platform != null ) {
            SAMReadGroupRecord readGroup = read.getReadGroup();
            if ( readGroup == null )
                return false;

            Object readPlatformAttr = readGroup.getAttribute("PL");
            if ( readPlatformAttr == null || !readPlatformAttr.toString().toUpperCase().contains(platform))
                return false;
        }

        // check if we've reached the output limit
        if ( nReadsToPrint == 0 ) {
            return false;          // n == 0 means we've printed all we needed.
        }
        else if (nReadsToPrint > 0) {
            nReadsToPrint--;       // n > 0 means there are still reads to be printed.
        }

        return true;
	}

    /**
     * The reads map function.
     * @param ref the reference bases that correspond to our read, if a reference was provided
     * @param read the read itself, as a SAMRecord
     * @return the read itself
     */
    public SAMRecord map( ReferenceContext ref, SAMRecord read, ReadMetaDataTracker metaDataTracker ) {
        return read;
    }

    /**
     * reduceInit is called once before any calls to the map function.  We use it here to setup the output
     * bam file, if it was specified on the command line
     * @return SAMFileWriter, set to the BAM output file if the command line option was set, null otherwise
     */
    public SAMFileWriter reduceInit() {
        return out;
    }

    /**
     * given a read and a output location, reduce by emitting the read
     * @param read the read itself
     * @param output the output source
     * @return the SAMFileWriter, so that the next reduce can emit to the same source
     */
    public SAMFileWriter reduce( SAMRecord read, SAMFileWriter output ) {
        output.addAlignment(read);
        return output;
    }

}
