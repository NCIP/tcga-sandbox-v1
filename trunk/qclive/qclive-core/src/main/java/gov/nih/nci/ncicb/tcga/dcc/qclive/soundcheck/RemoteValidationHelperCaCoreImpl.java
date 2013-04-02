/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import gov.nih.nci.ncicb.tcga.dccws.Archive;
import gov.nih.nci.ncicb.tcga.dccws.Center;
import gov.nih.nci.ncicb.tcga.dccws.FileInfo;
import gov.nih.nci.ncicb.tcga.dccws.Platform;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Helper for remote validation. Uses ApplicationService to remotely query the DCC database.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RemoteValidationHelperCaCoreImpl implements RemoteValidationHelper {
    private ApplicationService appService;

    public RemoteValidationHelperCaCoreImpl() throws Exception {
        appService = ApplicationServiceProvider.getApplicationService();
    }

    /**
     * Constructor that provides the <code>ApplicationService</code>
     * 
     * @param applicationService the <code>ApplicationService</code>
     * @throws Exception
     */
    public RemoteValidationHelperCaCoreImpl(final ApplicationService applicationService) throws Exception {
        this.appService = applicationService;
    }

    /**
     * This checks to see if a center with a certain name exists. Queries the "name" attribute of the Center object,
     * which corresponds to the "center_name" attribute of the "center_info" table.
     *
     * @param centerName the center name to check
     * @return whether a center with this name exists in the DCC database
     * @throws ApplicationException if there is an error doing the remote query
     */
    public boolean centerExists(final String centerName) throws ApplicationException {
        return objectExists( "gov.nih.nci.ncicb.tcga.dccws.Center", "name", centerName );
    }

    /**
     * Gets the center ID of the center with the given domain name and type.
     *
     * @param centerName the center domain name
     * @param centerType the center type
     * @return the center ID or null
     * @throws ApplicationException if there is an error connecting to the web service
     */
    public Integer getCenterId(final String centerName, final String centerType) throws ApplicationException {

        Integer centerId = null;
        final HQLCriteria criteria = new HQLCriteria( "from gov.nih.nci.ncicb.tcga.dccws.Center where name = ? and centerType.id = ?");
        criteria.setParameters(Arrays.asList(centerName, centerType));
        final Collection results = appService.query( criteria );
        if(results != null && results.size() > 0) {
            final Center centerObj = (Center) results.iterator().next();
            centerId = centerObj.getId();
        }
        return centerId;
    }

    /**
     * This checks to see if a platform with a certain name exists.  Queries the "name" attribute of the Platform object,
     * which corresponds to the "platform_name" attribute of the platform_info table.
     *
     * @param platformName the platform name to check
     * @return whether a platform with this name exists in the DCC database
     * @throws ApplicationException if there is an error doing the remote query
     */
    public boolean platformExists( final String platformName ) throws ApplicationException {
        return objectExists( "gov.nih.nci.ncicb.tcga.dccws.Platform", "name", platformName );
    }

    /**
     * This checks to see if an archive with a certain name exists.  Queries the "name" attribute of the Archive object,
     * which corresponds to the "archive_name" attribute of the archive_info table.
     *
     * @param archiveName the archive name to check
     * @return whether an archive with this name exists in the DCC database
     * @throws ApplicationException if there is an error doing the remote query
     */
    public boolean archiveExists( final String archiveName ) throws ApplicationException {
        return objectExists( "gov.nih.nci.ncicb.tcga.dccws.Archive", "name", archiveName );
    }

    /**
     * This gets the center type (current center types are CGCC, GSC, and BCR) for a given plaform.  Platforms that
     * are for characterization experiments are "CGCC" type, sequencing platforms are "GSC" and clinical data gathering
     * is "BCR".
     *
     * NOTE: What if future platforms produce characterization and sequencing data?  This will not work.  Aren't the
     * nextgen sequencers going to produce both? todo find out
     *
     * @param platformName the name of the platform to check
     * @return the center type (CGCC, GSC, or BCR) or null if platform not found
     * @throws ApplicationException if there is an error doing the remote query
     */
    public String getCenterTypeForPlatform(final String platformName) throws ApplicationException {

		final HQLCriteria criteria = new HQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.Platform where name = ?");
		criteria.setParameters(Arrays.asList(platformName));
		
		final Collection<Platform> platforms = appService.query(criteria);
		String centerType = null;
		
		if (platforms != null) {
            final Iterator<Platform> platformIterator = platforms.iterator();
            if (platformIterator.hasNext()) {
                final Platform platform = platformIterator.next();
                centerType = platform.getCenterType().getId();
            }
		}
		
		if(centerType == null) {
			throw new ApplicationException("Center type could not be determined. Please run the validator with the -centertype flag.");
		}
		
		return centerType;
    }

    /**
     * This checks if a given disease name (abbreviation) exists in the DCC database.
     *
     * @param diseaseName the disease name to check
     * @return whether such a disease exists in the DCC database
     * @throws ApplicationException if there is an error doing the remote query                              
     */
    public boolean diseaseExists( final String diseaseName ) throws ApplicationException {
        return objectExists( "gov.nih.nci.ncicb.tcga.dccws.Disease", "abbreviation", diseaseName );
    }

    /**
     * Check if the given archive is the latest version.  If the archive is not found, this will return false.
     *
     * @param archiveName the name of the archive to check
     * @return whether the archive is the latest version of this serial index
     * @throws ApplicationException if there is an error doing the remote query
     */
    public boolean isLatest( final String archiveName ) throws ApplicationException {
        final HQLCriteria criteria = new HQLCriteria( "from gov.nih.nci.ncicb.tcga.dccws.Archive where name = ?");
        criteria.setParameters( Arrays.asList( archiveName ));
        final Collection results = appService.query( criteria );
        boolean latest = false;
        if (results != null && results.size() > 0) {
            latest = ((Archive)(results.iterator().next())).getIsLatest() == 1;
        }
        return latest;
    }

    /**
     * Gets the name of the latest archive for the given parameters.
     *
     * @param diseaseName the disease name
     * @param centerName the center name
     * @param platformName the platform name
     * @param serialIndex the serial index
     * @return the name of the latest archive for this disease, center, platform, and serial index; or null if no such archive
     * @throws ApplicationException if there is an error running the remote query
     */
    public String getLatestArchive( final String diseaseName, final String centerName, final String platformName, final Integer serialIndex ) throws ApplicationException {

        String archiveName = null;
        final List<Archive> archives = getLatestArchives(diseaseName, centerName,  platformName,  serialIndex);
        if (archives.size() > 0) {
            archiveName = archives.get(0).getName();
        }
        return archiveName;
    }

    /**
     * Gets all the latest archives for this disease, center, and platform.
     *
     * @param diseaseName the disease name
     * @param centerName the center name
     * @param platformName the platform name
     * @return a List of Archive objects that represent the latest available archives for the given parameters
     * @throws ApplicationException if there is an error running the remote query
     */
    public List<Archive> getLatestArchives(final String diseaseName, final String centerName, final String platformName) throws ApplicationException {
        return getLatestArchives(diseaseName,  centerName, platformName, null);
    }

    private List<Archive> getLatestArchives(final String diseaseName, final String centerName, final String platformName, final Integer serialIndex) throws ApplicationException {
        // get latest archives; specify serial index if parameter not null
        String query = "from gov.nih.nci.ncicb.tcga.dccws.Archive " +
                "where isLatest=1 and disease.abbreviation=? and center.name=? and platform.name=?";
        if (serialIndex != null) {
            query += " and serialIndex=?";
        }
        final HQLCriteria criteria = new HQLCriteria( query );
        final List<Object> parameters = new ArrayList<Object>();
        parameters.add(diseaseName);
        parameters.add(centerName);
        parameters.add(platformName);
        if (serialIndex != null) {
            parameters.add(serialIndex);
        }
        criteria.setParameters( parameters );
        final Collection results = appService.query( criteria );
        final List<Archive> archives = new ArrayList<Archive>();
        for (final Object result : results) {
            archives.add((Archive) result);
        }
        return archives;
    }

    /**
     * Checks if a given file exists for a certain archive.
     *
     * @param fileName the name of the file
     * @param archiveName the name of the archive
     * @return if the file exists in the archive
     * @throws ApplicationException if there is an error running the remote query              
     */
    public boolean fileExists( final String fileName, final String archiveName ) throws ApplicationException {
        final Archive archive = (Archive) getObject( "gov.nih.nci.ncicb.tcga.dccws.Archive", "name", archiveName);
        final HQLCriteria criteria = new HQLCriteria( "from gov.nih.nci.ncicb.tcga.dccws.FileInfo f where name=? and ? member of f.archiveCollection");
        criteria.setParameters( Arrays.asList( fileName, archive ));
        final Collection results = appService.query( criteria );
        return results != null && results.size() > 0;
    }

    @Override
    public boolean projectExists(final String projectName) throws ApplicationException {
        return objectExists("gov.nih.nci.ncicb.tcga.dccws.Project", "id", projectName);
    }

    @Override
    public boolean tssCodeExists(final String tssCode) throws ApplicationException {
        return objectExists("gov.nih.nci.ncicb.tcga.dccws.TissueSourceSite", "id", tssCode);
    }

    @Override
    public boolean sampleTypeExists(final String sampleType) throws ApplicationException {
        return objectExists("gov.nih.nci.ncicb.tcga.dccws.SampleType", "id", sampleType);
    }

    @Override
    public boolean portionAnalyteExists(final String portionAnalyte) throws ApplicationException {
        return objectExists("gov.nih.nci.ncicb.tcga.dccws.PortionAnalyte", "id", portionAnalyte);
    }

    @Override
    public boolean bcrCenterIdExists(final String bcrCenterId) throws ApplicationException {
        return objectExists("gov.nih.nci.ncicb.tcga.dccws.BcrCenter", "id", bcrCenterId);
    }

    @Override
    public List<FileInfo> getArchiveDataFiles(final Archive archive) throws ApplicationException {
        final HQLCriteria criteria = new HQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.FileInfo f where dataLevel > 0 and ? member of f.archiveCollection");
        criteria.setParameters(Arrays.asList(archive));

        final List<FileInfo> archiveDataFiles = new ArrayList<FileInfo>();
        final Collection results = appService.query(criteria);
        if (results != null) {
            for (final Object result : results) {
                archiveDataFiles.add((FileInfo) result);
            }
        }
        return archiveDataFiles;
    }


    /*
     * Helper method to get an object based on a key name and value.
     */
    private Object getObject(final String objectType, final String keyName, final String keyValue) throws ApplicationException {
        // build the criteria
        final HQLCriteria criteria = new HQLCriteria( "from " + objectType + " where " + keyName + " = ?");
        // set the parameter as the value
        criteria.setParameters( Arrays.asList( keyValue ));
        final Collection results = appService.query( criteria );
        Object obj = null;
        if (results != null && results.size() > 0) {
            obj = results.iterator().next();
        }
        return obj;
    }

    /*
     * Helper method that does a query and returns if results were found or not.
     * todo: get the count without getting objects?  there is a way in HQL but I couldn't get it to work...
     */
    private boolean objectExists(final String objectType, final String attributeName, final String attributeValue) throws ApplicationException {
        final HQLCriteria objectCriteria = new HQLCriteria( new StringBuilder().append( "from " ).append( objectType ).
                append( " where " ).append( attributeName ).append( " = ?" ).toString() );
        objectCriteria.setParameters( Arrays.asList( attributeValue ));
        final Collection results = appService.query( objectCriteria );
        return results != null && results.size() > 0;
    }
}
