/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

/**
 * Validates the MD5 of a file.  Expects the MD5 is stored in a file with the same name as the file plus ".md5"
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class MD5Validator extends AbstractProcessor<File, Boolean> {

    private static final MD5ChecksumCreator CHECKSUM_CREATOR = new MD5ChecksumCreator();
    public static final String STATUS_PENDING = "Pending"; // md5 mismatch, retry again

    /**
     * @return the name
     */
    public String getName() {
        return "MD5 validation";
    }

    public String getDescription(final File input) {
        StringBuilder descr = new StringBuilder(getName());
        if (input != null) {
            descr.append(" on ");
            if (input.getName().lastIndexOf(File.separator) != -1) {
                descr.append(input.getName().substring(getName().lastIndexOf(File.separator) + 1));
            } else {
                descr.append(input.getName());
            }
        }
        return descr.toString();
    }

    /**
     * This does the main work of the step.  Checks the MD5 of the input file, assuming the expected MD5 is in
     * a file of the same name plus ".md5"
     *
     * @param input the File to check
     * @return if the MD5 was the same as expected
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error or if the validation fails (eg MD5 doesn't match)
     */
    protected Boolean doWork(final File input, final QcContext context) throws ProcessorException {
        final File submittedMD5 = new File(input + ".md5");
        StringBuilder retryReason;
        if (!submittedMD5.exists()) {
            retryReason = new StringBuilder().append("MD5 file not found for ").append(input.getName());
            retryMD5Validation(context, retryReason);
        }
        BufferedReader md5Reader = null;
        InputStreamReader inReader = null;
        FileInputStream fileStream = null;
        try {
            final String archiveChecksum = getChecksum(input);
            fileStream = new FileInputStream(submittedMD5);
            inReader = new InputStreamReader(fileStream);
            md5Reader = new BufferedReader(inReader);
            String md5Hash = md5Reader.readLine();
            if (md5Hash == null || md5Hash.trim().equals("") || md5Hash.length() < 32) {
                throw new ProcessorException("Submitted MD5 file does not contain a valid MD5 hash value");                
            }
            md5Hash = md5Hash.substring(0, 32);
            if (!md5Hash.equals(archiveChecksum)) {
                retryReason = new StringBuilder().append("MD5 checksum for ").append(input.getName()).append("(").append(archiveChecksum).append(") does not match the submitted MD5 value (").
                        append(md5Hash).append(")");
                retryMD5Validation(context, retryReason);
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new ProcessorException(new StringBuilder().append("Error generating MD5 checksum for ").append(input.getName()).toString(), e);
        }
        catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("Error reading submitted MD5 from ").append(submittedMD5.getName()).toString());
        } finally {
            try {
                if (md5Reader != null) {
                    md5Reader.close();
                }
                if (inReader != null) {
                    inReader.close();
                }
                if (fileStream != null) {
                    fileStream.close();
                }

            } catch (IOException e) {
                // failure to close the file doesn't mean the processing failed
            }
        }
        return true;
    }

    // for "extract and override" (testing) purposes
    protected String getChecksum(final File input) throws IOException, NoSuchAlgorithmException {
        return MD5Validator.getFileMD5(input);
    }

    /**
     * Static helper method to get the MD5 for a given file.
     *
     * @param file the file
     * @return the md5 value
     * @throws IOException              if there is an error reading the file
     * @throws NoSuchAlgorithmException if there is an error getting the MD5
     */
    public static String getFileMD5(final File file) throws IOException, NoSuchAlgorithmException {
        return MD5ChecksumCreator.convertStringToHex(CHECKSUM_CREATOR.generate(file));
    }

    private Boolean retryMD5Validation(final QcContext context, final StringBuilder reason) throws ProcessorException {
        context.setMd5ValidationStatus(STATUS_PENDING);
        throw new ProcessorException(reason.toString());
    }
}
