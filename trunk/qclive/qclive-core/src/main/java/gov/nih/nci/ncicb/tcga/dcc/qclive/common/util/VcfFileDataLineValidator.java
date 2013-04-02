/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.Set;

/**
 * Validator for Variant Call Format files.
 * Validates the Data Line of a Vcf file
 * Separates the logic needed to validate various elements of a data line from the
 * VcfValidator
 * Note that the logic necessary to carry dependencies between datalines should be contained here.
 * A specific example of this is that an ID must be unique across all datalines
 *
 * @author srinivasand
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface VcfFileDataLineValidator {

    /**
     * Method validates a specified data line and returns a boolean to indicate whether validation was
     * successful or not. Any errors/warnings are placed in context.
     *
     * @param dataLine A single dataline
     * @param vcf A valid VcfFile that has been parsed and contains reference header information
     * @param lineNum The line number of the dataline being validated. The linenum is used for context logging.
     * @param context The <code>QcContext</code> that will have errors/warnings logged to it
     * @param vcfIds a set of ids in the vcf file
     * @param previousVcfIds a {@link Set} of all the VCF Ids encountered on the previous data lines
     * @return Boolean A boolean value to indicate whether validation was successful or not
     */
    public Boolean validateDataLine(final String[] dataLine,
                                    final VcfFile vcf,
                                    final Integer lineNum,
                                    final QcContext context,
                                    final Set<String> vcfIds,
                                    final Set<String> previousVcfIds);

    /**
     * Return <code>true</code> *AFTER* validateDataLine() has been called
     * if info data for the data line requires the 'geneAnno' INFO header
     *
     * @return <code>true</code> *AFTER* validateDataLine() has been called if info data for the data line requires the 'geneAnno' INFO header
     */
    public boolean isFoundInfoDataRequiringGeneAnnoInfoHeader();

    /**
     * Return <code>true</code> *AFTER* validateDataLine() has been called
     * if chrom data for the data line requires the 'assembly' header
     *
     * @return <code>true</code> *AFTER* validateDataLine() has been called if chrom data for the data line requires the 'assembly' header
     */
    public boolean isFoundChromDataRequiringAssemblyHeader();
       
    /**
     * Return <code>true</code> *AFTER* validateDataLine() has been called
     * if ALT column data for the data line requires the '##assembly' header
     *
     * @return <code>true</code> *AFTER* validateDataLine() has been called if ALT column data for the data line requires the '##assembly' header
     */
    public boolean isFoundAltDataRequiringAssemblyHeader();
}
