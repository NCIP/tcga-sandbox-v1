/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface PlatformQueries {

    /**
     * Gets the ID of the given platform, or -1 if platform not found.
     *
     * @param platformName the name of the platform
     * @return the platform ID or -1
     */
    public Integer getPlatformIdByName(String platformName);

    public Platform getPlatformForName(String platformName);

    /**
     * Return the Platform with the given alias
     *
     * @param platformAlias
     * @return the Platform with the given alias
     */
    public Platform getPlatformWithAlias(final String platformAlias);

    public Collection<Map<String, Object>> getAllPlatforms();

    /**
     * get list of all the platforms
     *
     * @return list of platforms
     */
    public List<Platform> getPlatformList();

    public String getPlatformNameById(Integer platformId);

    /**
     * Return the <code>Platform</code> with the given Id
     *
     * @param platformId the <code>Platform</code> Id
     * @return the <code>Platform</code> with the given Id
     */
    public Platform getPlatformById(final Integer platformId);
}
