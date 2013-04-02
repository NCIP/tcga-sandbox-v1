/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG(TM)
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Jul 8, 2008
 * Time: 3:38:31 PM
 * A generic object to store entity/count for dcc queries
 */
public class GenCount {

    private String entity = null;
    private String condition = null;
    private String condition2 = null;
    private String condition3 = null;
    private String condition4 = null;
    private String condition5 = null;
    private String condition6 = null;
    private String condition7 = null;
    private String condition8 = null;
    private String condition9 = null;
    private String condition10 = null;
    private String condition11 = null;
    private String condition12 = null;
    private String count = null;
    private Integer countAsNumber = 0;

    public String getEntity() {
        return entity;
    }

    public void setEntity( final String entity ) {
        this.entity = entity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition( final String condition ) {
        this.condition = condition;
    }

    public String getCount() {
        return count;
    }

    @Deprecated // Migrate to setCountAsNumber
    public void setCount( final String count ) {
        setCountAsNumber(count);
        this.count = count;
    }

    public String getCondition2() {
        return condition2;
    }

    public void setCondition2( final String condition2 ) {
        this.condition2 = condition2;
    }

    public String getCondition3() {
        return condition3;
    }

    public void setCondition3( final String condition3 ) {
        this.condition3 = condition3;
    }

    public String getCondition4() {
        return condition4;
    }

    public void setCondition4( final String condition4 ) {
        this.condition4 = condition4;
    }

    public String getCondition5() {
        return condition5;
    }

    public void setCondition5( final String condition5 ) {
        this.condition5 = condition5;
    }

    public String getCondition6() {
        return condition6;
    }

    public void setCondition6( final String condition6 ) {
        this.condition6 = condition6;
    }

    public String getCondition7() {
        return condition7;
    }

    public void setCondition7( final String condition7 ) {
        this.condition7 = condition7;
    }

    public String getCondition8() {
        return condition8;
    }

    public void setCondition8( final String condition8 ) {
        this.condition8 = condition8;
    }

    public String getCondition9() {
        return condition9;
    }

    public void setCondition9( final String condition9 ) {
        this.condition9 = condition9;
    }

    public String getCondition10() {
        return condition10;
    }

    public void setCondition10( final String condition10 ) {
        this.condition10 = condition10;
    }

    public String getCondition11() {
        return condition11;
    }

    public void setCondition11( final String condition11 ) {
        this.condition11 = condition11;
    }

    public String getCondition12() {
        return condition12;
    }

    public void setCondition12( final String condition12 ) {
        this.condition12 = condition12;
    }

    public Integer getCountAsNumber() {
        return countAsNumber;
    }

    public void setCountAsNumber( final Integer countAsNumber ) {
        this.countAsNumber = countAsNumber;
    }

    public void setCountAsNumber( final String countAsNumber ) {
        if(countAsNumber != null) {
            try {
                this.countAsNumber = Integer.parseInt( countAsNumber );
            }
            catch(NumberFormatException e) {
                this.countAsNumber = null;
            }
        } else {
            this.countAsNumber = null;
        }
    }
}
