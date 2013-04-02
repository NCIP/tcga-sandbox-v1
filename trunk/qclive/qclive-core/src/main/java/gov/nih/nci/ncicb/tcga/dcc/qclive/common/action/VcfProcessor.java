/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adds associations between VCF files and the aliquots they represent.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfProcessor extends AbstractArchiveFileProcessor<Archive>{
    private FileInfoQueries fileInfoQueries;
    private BarcodeUuidResolver barcodeUuidResolver;
    private ShippedBiospecimenQueries shippedBiospecimenQueries;
    private BCRDataService bcrDataService;
    private CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator;


    /**
     * Gets the name of the processor, in descriptive English.
     *
     * @return the descriptive name of this processor
     */
    @Override
    public String getName() {
        return "VCF processor";
    }

    /**
     * Just return the archive.
     *
     * @param results the results of each processFile call
     * @param context the qc context
     * @return the archive from the context
     */
    @Override
    protected Archive getReturnValue(final Map<File, Archive> results, final QcContext context) {
        return context.getArchive();
    }

    /**
     * Process the VCF file.  Parses out the aliquots and adds association between them and the file in the DB.
     *
     * @param file    the VCF file to process
     * @param context the qc context
     * @return archive from the context
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error while processing the file
     */
    @Override
    protected Archive processFile(final File file, final QcContext context) throws ProcessorException {
        VcfParser vcfParser = new VcfParserImpl(file);
        try {
            if (vcfParser.parseHeaders()) {

                final VcfFile vcfFile = vcfParser.getVcfFile();
                final List<Long> biospecimenIds = getBiospecimenIds(vcfFile, context);

                if (biospecimenIds != null && biospecimenIds.size() > 0) {

                    // get file id
                    final Long vcfFileId = fileInfoQueries.getFileId(file.getName(), context.getArchive().getId());

                    // now shipped biospecimen to file...
                    bcrDataService.addShippedBiospecimensFileRelationship(biospecimenIds, vcfFileId);
                } else {
                    context.addWarning("Unable to add relationship between VCF file " + file.getName() + " and aliquots");
                }


            } else {
                throw new ProcessorException("Unable to parse VCF file " + file.getName());
            }
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage());
        } catch (UUIDException e) {
            throw new ProcessorException(e.getMessage());
        }


        return context.getArchive();
    }

    private List<Long> getBiospecimenIds(final VcfFile vcfFile, final QcContext qcContext) throws UUIDException {
        final List<String> uuids = new ArrayList<String>();
        final List<VcfFileHeader> sampleHeaders = vcfFile.getHeadersForType(VcfFile.HEADER_TYPE_SAMPLE);

        for (final VcfFileHeader sampleHeader : sampleHeaders) {
            String uuid = sampleHeader.getValueFor("SampleUUID");
            if (uuid == null) {
                String barcodeOrUuid = sampleHeader.getValueFor("SampleName");
                if (barcodeAndUUIDValidator.validateUUIDFormat(barcodeOrUuid)) {
                    uuids.add(barcodeOrUuid);
                } else {
                    // need to look up UUID from this
                    final Barcode barcodeUuidDetail = barcodeUuidResolver.resolveBarcodeAndUuid(barcodeOrUuid, null, qcContext.getArchive().getTheTumor(),
                            qcContext.getArchive().getTheCenter(), false);
                    uuids.add(barcodeUuidDetail.getUuid());
                }
            } else {
                uuids.add(uuid);
            }
        }

        // now find the biospecimen IDs for those UUIDs
        return uuids.size() > 0 ? shippedBiospecimenQueries.getShippedBiospecimenIds(uuids) : null;
    }

    /**
     * Figures out what to return if this is the wrong kind of archive for this processing.
     *
     * @param archive the input archive
     * @return the archive
     */
    @Override
    protected Archive getDefaultReturnValue(final Archive archive) {
        return archive;
    }

    /**
     * @return the file extension of the files this class processes (.vcf)
     */
    @Override
    protected String getFileExtension() {
        return ".vcf";
    }

    /**
     * Runs on GSC and CGCC archives.
     * 
     * @param archive the input archive
     * @return true if this processor can process this archive, false if not
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if the archive type cannot be determined or there is some other error
     */
    @Override
    protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
        return archive.getExperimentType().equals(Experiment.TYPE_CGCC) ||
                archive.getExperimentType().equals(Experiment.TYPE_GSC);
    }

    public void setShippedBiospecimenQueries(final ShippedBiospecimenQueries shippedBiospecimenQueries) {
        this.shippedBiospecimenQueries = shippedBiospecimenQueries;
    }

    public void setBarcodeUuidResolver(final BarcodeUuidResolver barcodeUuidResolver) {
        this.barcodeUuidResolver = barcodeUuidResolver;
    }

    public void setFileInfoQueries(final FileInfoQueries fileInfoQueries) {
        this.fileInfoQueries = fileInfoQueries;
    }

    public void setBcrDataService(final BCRDataService bcrDataService) {
        this.bcrDataService = bcrDataService;
    }

    public void setBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator) {
        this.barcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }
}
