package org.broadinstitute.sting.gatk.datasources.providers;

import org.broadinstitute.sting.gatk.iterators.StingSAMIterator;

import java.util.Collection;
import java.util.Arrays;

import net.sf.samtools.SAMRecord;
/**
 * User: hanna
 * Date: May 22, 2009
 * Time: 12:06:54 PM
 * BROAD INSTITUTE SOFTWARE COPYRIGHT NOTICE AND AGREEMENT
 * Software and documentation are copyright 2005 by the Broad Institute.
 * All rights are reserved.
 *
 * Users acknowledge that this software is supplied without any warranty or support.
 * The Broad Institute is not responsible for its use, misuse, or
 * functionality.
 */

/**
 * A view into the reads that a provider can provide. 
 */
public class ReadView implements View, Iterable<SAMRecord> {
    /**
     * The iterator into the reads supplied by this provider.
     */
    private StingSAMIterator reads;

    /**
     * Create a new view of the reads given the current data set.
     * @param provider Source for the data.
     */
    public ReadView( ReadShardDataProvider provider ) {
        reads = provider.getReadIterator();
    }

    /**
     * Other reads and loci conflict with this view.
     * @return Array of reads and loci.
     */
    public Collection<Class<? extends View>> getConflictingViews() {
        return Arrays.<Class<? extends View>>asList(ReadView.class, LocusView.class);
    }

    /**
     * Close the view over these reads.  Note that this method closes just
     * the view into the reads, not the reads themselves.
     */
    public void close() {
        // Don't close the reads.  The provider is responsible for this.
        // Just dispose of the pointer.
        reads = null;
    }

    /**
     * Gets an iterator into the reads supplied by this provider.
     * @return Iterator into the reads that this provider covers.
     */
    public StingSAMIterator iterator() {
        return reads;    
    }
}
