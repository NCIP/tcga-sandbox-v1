/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BiospecimenHelper;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.MafInfoQueries;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Processes all maf files in an archive.  Note that this does not perform validation -- see MafFileValidator for that.
 * <p/>
 * NOTE: saves info only to disease-specific schema.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class MafFileProcessor extends AbstractMafFileHandler<File> {

    private MafInfoQueries mafInfoQueries;
    private FileInfoQueries fileInfoQueries;
    private BCRDataService bcrDataService;

    private int batchSize = BATCH_SIZE;


    @Override
    protected File doWork(final File mafFile, final QcContext context) throws ProcessorException {
        // make sure the disease is set
        if (context.getArchive() != null) {
            DiseaseContextHolder.setDisease(context.getArchive().getTumorType());
        }

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            // open file
            fileReader = new FileReader(mafFile);
            bufferedReader = new BufferedReader(fileReader);

            int lineNum = 0;

            // find first non-blank line not starting with #, this is the header
            String headerLine = bufferedReader.readLine();
            lineNum++;
            while (StringUtils.isEmpty(headerLine.trim()) || StringUtils.startsWith(headerLine, COMMENT_LINE_TOKEN)) {
                headerLine = bufferedReader.readLine();
                lineNum++;
            }

            final List<String> headers = Arrays.asList(headerLine.split("\\t"));

            context.setFile(mafFile);
            final Map<String, Integer> fieldOrder = mapFieldOrder(headers);
            // need to find out the file id for this maf file
            final Long mafFileId = fileInfoQueries.getFileId(mafFile.getName(), context.getArchive().getId());
            if (mafFileId == null || mafFileId == -1) {
                context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                throw new ProcessorException(new StringBuilder().append("File '").append(mafFile.getName()).append("' was not found in the database").toString());
            }
            if (isAddMafInfo(mafFileId)) {
                HashMap<String, BCRID> biospecimens = new HashMap<String, BCRID>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lineNum++;
                    if (!StringUtils.isBlank(line.trim()) && !StringUtils.startsWith(line, COMMENT_LINE_TOKEN)) {
                        final String[] row = line.split("\\t");

                        try {
                            processRow(row, fieldOrder, mafFileId, biospecimens, context, mafFile);
                            //  If exceeds batch size store it in the database
                            if (biospecimens.size() >= getBatchSize()) {
                                try {
                                    insertBiospecimenToFileRelationships(biospecimens, context, mafFile);
                                } catch (UUIDException ue) {
                                    throw new ProcessorException(ue.getMessage(), ue);
                                }
                                biospecimens.clear();
                            }
                        } catch (DataAccessException e) {
                            // catch DB errors per line
                            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                            context.addError(MessageFormat.format(
                                    MessagePropertyType.MAF_FILE_PROCESSING_ERROR,
                                    mafFile.getName(),
                                    new StringBuilder().append("Mutation information from file at line '").append(lineNum).append("' was not successfully added. Root cause: ").append(e.getMessage()).toString()));
                        }
                    }
                }
                // process remaining biospecimens
                if (biospecimens.size() > 0) {
                    try {
                        insertBiospecimenToFileRelationships(biospecimens, context, mafFile);
                    } catch (UUIDException ue) {
                        context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                        throw new ProcessorException(ue.getMessage(), ue);
                    } catch (DataAccessException e) {
                        context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                        throw new ProcessorException(e.getMessage(), e);
                    }
                    biospecimens.clear();
                }
            }
        } catch (IOException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Error reading maf file ").append(mafFile.getName()).toString());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }

            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return mafFile;
    }

    protected boolean isAddMafInfo(final Long mafFileId) {
        boolean bRet = true;
        if(mafInfoQueries.fileIdExistsInMafInfo(mafFileId)) {
            final FileInfo fi = fileInfoQueries.getFileForFileId(mafFileId);
            if(fi != null) {
                final Archive archive = fileInfoQueries.getLatestArchiveContainingFile(fi);
                if(archive != null) {
                    bRet = false;
                }
            }
            if(bRet == true) {
                mafInfoQueries.deleteMafInfoForFileId(mafFileId);
            }
        }
        return bRet;
    }

    protected void setOtherMafInfoFields(final MafInfo mafInfo, final String[] row, final Map<String, Integer> fieldOrder,
                                         final long mafFileId) throws ProcessorException {
        // does nothing, all fields are in processRow.  But subclasses should override!
    }

    protected String getEndPositionName() {
        return FIELD_END_POSITION;
    }

    protected String getStartPositionName() {
        return FIELD_START_POSITION;
    }

    private void processRow(
            final String[] row, final Map<String, Integer> fieldOrder,
            final long mafFileId, final HashMap<String, BCRID> bcrIds, final QcContext context, final File mafFile) throws ProcessorException {
        final MafInfo mafInfo = getMafInfo();
        mafInfo.setCenterID(context.getArchive().getTheCenter().getCenterId());
        mafInfo.setFileID(mafFileId);
        mafInfo.setChromosome(row[fieldOrder.get(FIELD_CHROMOSOME)]);
        mafInfo.setDbsnpRS(row[fieldOrder.get(FIELD_DBSNP_RS)]);
        mafInfo.setDbSNPValStatus(row[fieldOrder.get(FIELD_DBSNP_VAL_STATUS)]);
        mafInfo.setEndPosition(Integer.valueOf(row[fieldOrder.get(getEndPositionName())]));
        mafInfo.setEntrezGeneID(Integer.valueOf(row[fieldOrder.get(FIELD_ENTREZ_GENE_ID)]));
        mafInfo.setHugoSymbol(row[fieldOrder.get(FIELD_HUGO_SYMBOL)]);
        mafInfo.setMatchNormalSampleBarcode(row[fieldOrder.get(FIELD_MATCHED_NORM_SAMPLE_BARCODE)]);
        mafInfo.setMatchNormSeqAllele1(row[fieldOrder.get(FIELD_MATCH_NORM_SEQ_ALLELE1)]);
        mafInfo.setMatchNormSeqAllele2(row[fieldOrder.get(FIELD_MATCH_NORM_SEQ_ALLELE2)]);
        mafInfo.setMatchNormValidationAllele1(row[fieldOrder.get(FIELD_MATCH_NORM_VALIDATION_ALLELE1)]);
        mafInfo.setMatchNormValidationAllele2(row[fieldOrder.get(FIELD_MATCH_NORM_VALIDATION_ALLELE2)]);
        mafInfo.setMutationStatus(row[fieldOrder.get(FIELD_MUTATION_STATUS)]);
        mafInfo.setNcbiBuild(row[fieldOrder.get(FIELD_NCBI_BUILD)]);
        mafInfo.setReferenceAllele(row[fieldOrder.get(FIELD_REFERENCE_ALLELE)]);
        mafInfo.setStartPosition(Integer.valueOf(row[fieldOrder.get(getStartPositionName())]));
        mafInfo.setStrand(row[fieldOrder.get(FIELD_STRAND)]);
        mafInfo.setTumorSampleBarcode(row[fieldOrder.get(FIELD_TUMOR_SAMPLE_BARCODE)]);
        mafInfo.setTumorSeqAllele1(row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE1)]);
        mafInfo.setTumorSeqAllele2(row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE2)]);
        mafInfo.setTumorValidationAllele1(row[fieldOrder.get(FIELD_TUMOR_VALIDATION_ALLELE1)]);
        mafInfo.setTumorValidationAllele2(row[fieldOrder.get(FIELD_TUMOR_VALIDATION_ALLELE2)]);
        mafInfo.setValidationStatus(row[fieldOrder.get(FIELD_VALIDATION_STATUS)]);
        mafInfo.setVariantClassification(row[fieldOrder.get(FIELD_VARIANT_CLASSIFICATION)]);
        mafInfo.setVariantType(row[fieldOrder.get(FIELD_VARIANT_TYPE)]);
        mafInfo.setVerificationStatus(row[fieldOrder.get(FIELD_VERIFICATION_STATUS)]);

        // set uuids values in maf info bean
        if (fieldOrder.get(FIELD_TUMOR_SAMPLE_UUID) != null) {
            mafInfo.setTumorSampleUUID(row[fieldOrder.get(FIELD_TUMOR_SAMPLE_UUID)]);
        }
        if (fieldOrder.get(FIELD_MATCHED_NORM_SAMPLE_UUID) != null) {
            mafInfo.setMatchNormalSampleUUID(row[fieldOrder.get(FIELD_MATCHED_NORM_SAMPLE_UUID)]);
        }

        setOtherMafInfoFields(mafInfo, row, fieldOrder, mafFileId);
        final long fileId = context.getArchive().getFilenameToIdToMap().get(mafFile.getName());
        final long mafId = mafInfoQueries.addMaf(mafInfo);
        mafInfo.setId(mafId);

        insertBiospecimenInMap(bcrIds, mafInfo.getTumorSampleBarcode(), mafInfo.getTumorSampleUUID(), context, mafFile);
        insertBiospecimenInMap(bcrIds, mafInfo.getMatchNormalSampleBarcode(), mafInfo.getMatchNormalSampleUUID(), context, mafFile);
    }

    // depending on the context isCenterConvertedToUUID will either put UUID or barcode in map
    private void insertBiospecimenInMap(final HashMap<String, BCRID> bcrIds,
                                        final String barcodeString,
                                        final String uuid,
                                        final QcContext context,
                                        final File mafFile) {

        try {
            final BCRID bcrId;

            final String key;
            if (context.isCenterConvertedToUUID()) {
                key = uuid;
            } else {
                key = barcodeString;
            }

            if (bcrIds.get(key) == null) {
                bcrId = bcrDataService.parseAliquotBarcode(barcodeString);
                bcrId.setUUID(uuid);
                bcrIds.put(key, bcrId);
            }

        } catch (ParseException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            context.addError(MessageFormat.format(
                    MessagePropertyType.MAF_FILE_PROCESSING_ERROR,
                    mafFile.getName(),
                    "Barcode format in " + mafFile.getName() + " is invalid: '"));
        }

    }

    private void insertBiospecimenToFileRelationships(final HashMap<String, BCRID> barcodesOrUuids,
                                                      final QcContext context,
                                                      final File mafFile) throws UUIDException {

        final List<Integer> biospecimenIds;
        final List<Long> shippedBiospecimenIds;

        // if center is converted to UUID use the uuids to look up the biospecimen ID
        if (context.isCenterConvertedToUUID()) {
            final List<String> uuids = new ArrayList<String>();
            for (final BCRID bcrId : barcodesOrUuids.values()) {
                uuids.add(bcrId.getUUID());
            }

            shippedBiospecimenIds = bcrDataService.getShippedBiospecimenIds(uuids);
            biospecimenIds = new ArrayList<Integer>();
            for (final Long shippedBiospecimenId : shippedBiospecimenIds) {
                biospecimenIds.add(shippedBiospecimenId.intValue());
            }

        } else {
            biospecimenIds = BiospecimenHelper.getBiospecimenIds(new ArrayList<BCRID>(barcodesOrUuids.values()), bcrDataService);
            shippedBiospecimenIds = new ArrayList<Long>();
            for (final Integer id : biospecimenIds) {
                shippedBiospecimenIds.add(id.longValue());
            }

        }

        // add biospecimen to file relationship into common and disease database
        BiospecimenHelper.insertBiospecimenFileRelationship(biospecimenIds,
                context.getArchive().getFilenameToIdToMap().get(mafFile.getName()),
                bcrDataService,
                context.getArchive().getTheTumor());

        // now shipped biospecimen to file...
        bcrDataService.addShippedBiospecimensFileRelationship(shippedBiospecimenIds, context.getArchive().getFilenameToIdToMap().get(mafFile.getName()));
    }

    protected MafInfo getMafInfo() {
        return new MafInfo();
    }

    public String getName() {
        return "maf file processor";
    }

    public void setMafInfoQueries(final MafInfoQueries mafInfoQueries) {
        this.mafInfoQueries = mafInfoQueries;
    }

    public void setFileInfoQueries(final FileInfoQueries fileInfoQueries) {
        this.fileInfoQueries = fileInfoQueries;
    }

    public BCRDataService getBcrDataService() {
        return bcrDataService;
    }

    public void setBcrDataService(BCRDataService bcrDataService) {
        this.bcrDataService = bcrDataService;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }
}
