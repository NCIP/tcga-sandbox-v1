/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Jul 7, 2008
 * Time: 3:46:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BiospecimenInfo {

    private String Classification;
    private String Count;
    private String Example;
    private String Definition;

    public String getClassification() {
        return Classification;
    }

    public void setClassification( final String classification ) {
        Classification = classification;
    }

    public String getCount() {
        return Count;
    }

    public void setCount( final String count ) {
        Count = count;
    }

    public String getExample() {
        return Example;
    }

    public void setExample( final String example ) {
        Example = example;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition( final String definition ) {
        Definition = definition;
    }
}
