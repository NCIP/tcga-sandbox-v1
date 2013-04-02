/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.WebApplicationException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class DAMWSUtil implements Serializable {
    private static final long serialVersionUID = 321502047212589452L;
    transient protected final Log logger = LogFactory.getLog(getClass());
    private int sizeLimitGigs;
    private String archivePhysicalPathPrefix;
    private String downloadLinkSite;


    public List<DataFile> removeMetadata(final List<DataFile> fileInfos) {
        List<DataFile> winnowedFiles = new ArrayList<DataFile>();
        for (final DataFile df : fileInfos) {
            if (!(df instanceof DataFileMetadata)) {
                winnowedFiles.add(df);
            }
        }
        return winnowedFiles;
    }

    public long checkTotalSize(final List<DataFile> fileInfo, final int size) {
        long total = 0;
        for (DataFile df : fileInfo) {
            total += df.getSize();
        }
        //1 Gb = 1073741824 bytes
        if (total > size * 1073741824L) {
            logger.debug("Total Size: " + total);
            //WebApplicationException is a runtime exception
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.REQUEST_ENTITY_TOO_LARGE,
                    "Requested Data size too big. Allowed: " + size + " GB, Requested: " +
                            (total / 1073741824L) + " GB"));
        } else return total;
    }

    public boolean isDownloadingProtected(final List<DataFile> selectedFileInfo) {
        boolean ret = false;
        for (final DataFile fileInfo : selectedFileInfo) {
            if (fileInfo.isProtected()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean hasValueIgnoreCase(Map map, String str) {
        for (Object o : map.values()) {
            if (o.toString().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public int getSizeLimitGigs() {
        return sizeLimitGigs;
    }

    public void setSizeLimitGigs(int sizeLimitGigs) {
        this.sizeLimitGigs = sizeLimitGigs;
    }

    public String getArchivePhysicalPathPrefix() {
        return archivePhysicalPathPrefix;
    }

    public void setArchivePhysicalPathPrefix(String archivePhysicalPathPrefix) {
        this.archivePhysicalPathPrefix = archivePhysicalPathPrefix;
    }

    public String getDownloadLinkSite() {
        return downloadLinkSite;
    }

    public void setDownloadLinkSite(String downloadLinkSite) {
        this.downloadLinkSite = downloadLinkSite;
    }

}//End of Class
