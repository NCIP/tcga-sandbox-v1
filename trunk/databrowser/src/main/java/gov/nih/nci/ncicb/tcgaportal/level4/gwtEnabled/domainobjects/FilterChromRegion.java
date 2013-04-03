package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Object representing a chromosome region for the filter specifier.
 *
 * @author David Nassau
 *         Last updated by: $Author: nassaud $
 * @version $Rev: 5360 $
 */
public class FilterChromRegion implements IsSerializable {

    private String chromosome = null;
    private long start = -1;
    private long stop = -1;

    public FilterChromRegion() {
    }

    public FilterChromRegion(String chromosome, long start, long stop) {
        this.chromosome = chromosome;
        this.start = start;
        this.stop = stop;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    /**
     * @param chrom the region chromosome to check -- may be "?"
     * @param start the region start to check -- if chrom is "?" may be -1, otherwise must be specified
     * @param stop  the region stop to check -- if chrom is "?" may be -1, otherwise must be specified
     * @return true if this overlaps at all with region, false if not
     */
    public boolean overlapsWith(String chrom, long start, long stop) {
        // if don't know chromosome, can't say is overlapping
        if (chrom == null || chrom.equals("?") || chrom.length() == 0 || this.getChromosome() == null) {
            return false;
        }

        // can't determine if don't know exact region
        if (start == -1 || stop == -1) {
            return false;
        }

        if (chrom.equals(this.getChromosome())) {
            if (this.getStart() != -1 && this.getStop() != -1) {
                if (start >= this.getStart() && start <= this.getStop()) {
                    // region start is between this start and stop
                    return true;
                } else if (stop >= this.getStart() && stop <= this.getStop()) {
                    return true;
                }
            } else if (this.getStart() != -1) {
                // start specified, stop not
                if (start >= this.getStart() || stop >= this.getStart()) {
                    return true;
                }
            } else if (this.getStop() != -1) {
                // stop specified, start not
                if (start <= this.getStop()) {
                    return true;
                }
            } else {
                // this represents a whole chromosome
                return true;
            }
        }

        return false;
    }

    public String toString() {
        //"Chr " + chrAnnotation + ": " + startAnnotation + " - " + stopAnnotation
        StringBuilder sb = new StringBuilder();
        sb.append("Chr ");
        if (chromosome != null && chromosome.length() > 0) {
            sb.append(chromosome).append(": ");
            if (start > 0) {
                sb.append(start);
            } else {
                sb.append("<start>");
            }
            sb.append(" to ");
            if (stop > 0) {
                sb.append(stop);
            } else {
                sb.append("<end>");
            }
        }
        return sb.toString();
    }
}
