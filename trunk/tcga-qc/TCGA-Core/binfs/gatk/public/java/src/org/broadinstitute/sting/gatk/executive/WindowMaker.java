package org.broadinstitute.sting.gatk.executive;

import org.broadinstitute.sting.gatk.datasources.reads.Shard;
import org.broadinstitute.sting.gatk.datasources.sample.SampleDataSource;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.gatk.iterators.*;
import org.broadinstitute.sting.gatk.ReadProperties;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;

import java.util.*;

import net.sf.picard.util.PeekableIterator;
import org.broadinstitute.sting.utils.GenomeLocParser;

/**
 * Buffer shards of data which may or may not contain multiple loci into
 * iterators of all data which cover an interval.  Its existence is an homage
 * to Mark's stillborn WindowMaker, RIP 2009.
 *
 * @author mhanna
 * @version 0.1
 */
public class WindowMaker implements Iterable<WindowMaker.WindowMakerIterator>, Iterator<WindowMaker.WindowMakerIterator> {
    /**
     * Source information for iteration.
     */
    private final ReadProperties sourceInfo;

    /**
     * Hold the read iterator so that it can be closed later.
     */
    private final StingSAMIterator readIterator;

    /**
     * The data source for reads.  Will probably come directly from the BAM file.
     */
    private final Iterator<AlignmentContext> sourceIterator;

    /**
     * Stores the sequence of intervals that the windowmaker should be tracking.
     */
    private final PeekableIterator<GenomeLoc> intervalIterator;

    /**
     * In the case of monolithic sharding, this case returns whether the only shard has been generated.
     */
    private boolean shardGenerated = false;

    /**
     * The alignment context to return from this shard's iterator.  Lazy implementation: the iterator will not find the
     * currentAlignmentContext until absolutely required to do so.   If currentAlignmentContext is null and advance()
     * doesn't populate it, no more elements are available.  If currentAlignmentContext is non-null, currentAlignmentContext
     * should be returned by next().
     */
    private AlignmentContext currentAlignmentContext;

    /**
     * Create a new window maker with the given iterator as a data source, covering
     * the given intervals.
     * @param iterator The data source for this window.
     * @param intervals The set of intervals over which to traverse.
     * @param sampleData SampleDataSource that we can reference reads with
     */

    public WindowMaker(Shard shard, GenomeLocParser genomeLocParser, StingSAMIterator iterator, List<GenomeLoc> intervals, SampleDataSource sampleData ) {
        this.sourceInfo = shard.getReadProperties();
        this.readIterator = iterator;

        this.sourceIterator = new LocusIteratorByState(iterator,sourceInfo,genomeLocParser,sampleData);
        this.intervalIterator = intervals.size()>0 ? new PeekableIterator<GenomeLoc>(intervals.iterator()) : null;
    }

    public Iterator<WindowMakerIterator> iterator() {
        return this;
    }

    public boolean hasNext() {
        return (intervalIterator != null && intervalIterator.hasNext()) || !shardGenerated;
    }

    public WindowMakerIterator next() {
        shardGenerated = true;
        return new WindowMakerIterator(intervalIterator != null ? intervalIterator.next() : null);
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from a window maker.");
    }

    public void close() {
        this.readIterator.close();
    }

    public class WindowMakerIterator extends LocusIterator {
        /**
         * The locus for which this iterator is currently returning reads.
         */
        private final GenomeLoc locus;

        /**
         * Signal not to advance the iterator because we're currently sitting at the next element.
         */
        private boolean atNextElement = false;

        public WindowMakerIterator(GenomeLoc locus) {
            this.locus = locus;
            advance();
        }

        public ReadProperties getSourceInfo() {
            return sourceInfo;
        }

        public GenomeLoc getLocus() {
            return locus;
        }

        public WindowMakerIterator iterator() {
            return this;
        }

        public boolean hasNext() {
            advance();
            return atNextElement;
        }

        public AlignmentContext next() {
            advance();
            if(!atNextElement) throw new NoSuchElementException("WindowMakerIterator is out of elements for this interval.");

            // Prepare object state for no next element.
            AlignmentContext toReturn = currentAlignmentContext;
            currentAlignmentContext = null;
            atNextElement = false;

            // Return the current element.
            return toReturn;
        }

        private void advance() {
            // No shard boundaries specified.  If currentAlignmentContext has been consumed, grab the next one.
            if(locus == null) {
                if(!atNextElement && sourceIterator.hasNext()) {
                    currentAlignmentContext = sourceIterator.next();
                    atNextElement = true;
                }
                return;
            }

            // Can't possibly find another element.  Skip out early.
            if(currentAlignmentContext == null && !sourceIterator.hasNext())
                return;

            // Need to find the next element that is not past shard boundaries.  If we travel past the edge of
            // shard boundaries, stop and let the next interval pick it up.
            while(sourceIterator.hasNext()) {
                // Seed the current alignment context first time through the loop.
                if(currentAlignmentContext == null)
                    currentAlignmentContext = sourceIterator.next();

                // Found a match.
                if(locus.containsP(currentAlignmentContext.getLocation())) {
                    atNextElement = true;
                    break;
                }
                // Whoops.  Skipped passed the end of the region.  Iteration for this window is complete.
                if(locus.isBefore(currentAlignmentContext.getLocation()))
                    break;

                // No more elements to examine.  Iteration is complete.
                if(!sourceIterator.hasNext())
                    break;

                // Advance the iterator and try again.
                currentAlignmentContext = sourceIterator.next();
            }
        }
    }
}
