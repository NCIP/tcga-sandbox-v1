/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Sep 30, 2008
 * Time: 1:12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSetStats extends DataSet {  //todo  delete, no longer used
    private String dataValue;

    public void setDataValue( String value ) {
        dataValue = value;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDisease( String disease ) {
        setSample( disease );
    }

    public String getDisease() {
        return getSample();
    }
}
