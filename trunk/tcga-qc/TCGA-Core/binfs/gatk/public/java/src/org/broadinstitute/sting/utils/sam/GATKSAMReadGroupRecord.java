package org.broadinstitute.sting.utils.sam;

import net.sf.samtools.*;

/**
 * @author ebanks
 * GATKSAMReadGroupRecord
 *
 * this class extends the samtools SAMReadGroupRecord class and caches important
 * (and oft-accessed) data that's not already cached by the SAMReadGroupRecord class
 *
 */
public class GATKSAMReadGroupRecord extends SAMReadGroupRecord {

    // the SAMReadGroupRecord data we're caching
    private String mSample = null;
    private String mPlatform = null;

    // because some values can be null, we don't want to duplicate effort
    private boolean retrievedSample = false;
    private boolean retrievedPlatform = false;


    public GATKSAMReadGroupRecord(SAMReadGroupRecord record) {
        super(record.getReadGroupId(), record);
    }

    ///////////////////////////////////////////////////////////////////////////////
    // *** The following methods are overloaded to cache the appropriate data ***//
    ///////////////////////////////////////////////////////////////////////////////

    public String getSample() {
        if ( !retrievedSample ) {
            mSample = super.getSample();
            retrievedSample = true;
        }
        return mSample;
    }

    public void setSample(String s) {
        super.setSample(s);
        mSample = s;
        retrievedSample = true;
    }

    public String getPlatform() {
        if ( !retrievedPlatform ) {
            mPlatform = super.getPlatform();
            retrievedPlatform = true;
        }
        return mPlatform;
    }

    public void setPlatform(String s) {
        super.setPlatform(s);
        mPlatform = s;
        retrievedPlatform = true;
    }
}