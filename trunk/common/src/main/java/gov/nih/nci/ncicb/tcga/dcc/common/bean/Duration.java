/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.util.Calendar;

/**
* Defines the duration : day/week/month for UUID reports 
*
* @author Namrata Rane Last updated by: $Author: $
* @version $Rev: $
*/
public enum Duration {
    Day(1),
    Week(2),
    Month(3);

    private int range;

    Duration(final int range) {
        this.range = range;
   }

    public int getRange() {
        return range;
    }

    public void setRange(final int range) {
        this.range = range;
    }

    // depending on the range, adjust the calendar date that is passed
    public Calendar getAdjustedDate(final Calendar cal){
         if(this.range == Day.getRange()) {
            cal.add(Calendar.DATE, -1);
        }else if (this.range == Week.getRange()) {
            cal.add(Calendar.DATE, -7);
        }else if (this.range == Month.getRange()) {
            cal.add(Calendar.MONTH, -1);
        }
        return cal;
    }
}
