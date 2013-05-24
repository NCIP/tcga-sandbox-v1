/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

import java.util.Date;
import java.util.List;

/**
 * Bam bean representing the root element of the CGHub bam data.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamXmlResultSet {

    private Date fetchDate;
    private List<BamXmlResult> bamXmlResultList;

    public Date getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate(Date fetchDate) {
        this.fetchDate = fetchDate;
    }

    public List<BamXmlResult> getBamXmlResultList() {
        return bamXmlResultList;
    }

    public void setBamXmlResultList(List<BamXmlResult> bamXmlResultList) {
        this.bamXmlResultList = bamXmlResultList;
    }
}//End of class