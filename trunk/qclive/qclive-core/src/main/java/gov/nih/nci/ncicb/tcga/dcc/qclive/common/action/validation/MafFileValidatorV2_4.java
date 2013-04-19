package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.DataTypeName;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileName;
import gov.nih.nci.ncicb.tcga.dcc.common.util.PlatformName;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copied the base code from Maf2FileValidator.
 * Created with IntelliJ IDEA.
 * User: ramanr
 * Date: 4/3/13
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MafFileValidatorV2_4 extends MafFileValidator {
    private static final String SYMBOL_MAF2_VARIANT_TYPE_INS = "INS";
    private static final String SYMBOL_MAF2_VARIANT_TYPE_DEL = "DEL";
    private static final String FIELD_MAF2_CENTER = "Center";
    private static final String VALIDATION_STATUS_UNTESTED = "Untested";
    private static final String VALIDATION_STATUS_INVALID = "Invalid";
    protected static final String VALIDATION_METHOD_NONE = "none";
    protected static final Pattern NO_BLANK_PATTERN = Pattern.compile("\\S+");
    private DataTypeQueries dataTypeQueries;
    private CenterQueries centerQueries;

    public MafFileValidatorV2_4() {
        super();
        getMultiValueAllowedFields().add(FIELD_SEQUENCE_SOURCE);
        getMultiValueAllowedFields().add(FIELD_DBSNP_VAL_STATUS);
        getMultiValueAllowedFields().add(FIELD_MAF2_CENTER);
    }

    @Override
    protected void initConditions() {

        getBarcodeFields().add(FIELD_TUMOR_SAMPLE_BARCODE);
        getBarcodeFields().add(FIELD_MATCHED_NORM_SAMPLE_BARCODE);
        getUuidFields().add(FIELD_TUMOR_SAMPLE_UUID);
        getUuidFields().add(FIELD_MATCHED_NORM_SAMPLE_UUID);

        // if Validation_Status is 'Valid' then Tumor_Validation_Allele1 must match be made only of ACGT and -
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE1,
                Pattern.compile("[^TCGA\\-]"), "contain characters other than 'A', 'C', 'G', 'T', and '-' ", true));
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE2,
                Pattern.compile("[^TCGA\\-]"), "contain characters other than 'A', 'C', 'G', 'T', and '-' ", true));

        initSpecSpecificConditions();
    }

    @Override
    protected void initSpecSpecificConditions() {
        // If Validation_Status == 'Untested', then Validation_method == none
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_UNTESTED, FIELD_VALIDATION_METHOD, Pattern.compile(VALIDATION_METHOD_NONE),
                "be none", false));
        // If Validation_Status == 'Valid', then Mutation_Status == Germline|Somatic|LOH|Post-transcriptional modification|Unknown
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_MUTATION_STATUS,
                Pattern.compile("Germline|Somatic|LOH|Post-transcriptional modification|Unknown"), "be Germline, Somatic, LOH, Post-transcriptional modification or Unknown", false));
        // If Validation_Status == 'Valid', then Tumor_Validation_Allele1, Tumor_Validation_Allele2, Match_Norm_Validation_Allele1, Match_Norm_Validation_Allele2 cannot be null
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE1,
                NO_BLANK_PATTERN, "not be blank", false));
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE2,
                NO_BLANK_PATTERN, "not be blank", false));
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_MATCH_NORM_VALIDATION_ALLELE1,
                NO_BLANK_PATTERN, "not be blank", false));
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_MATCH_NORM_VALIDATION_ALLELE2,
                NO_BLANK_PATTERN, "not be blank", false));

        // If Mutation_Status == 'Germline' and Validation_Status == 'Valid', then Tumor_Validation_Allele1 == Match_Norm_Validation_Allele1
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_GERMLINE, VALIDATION_STATUS_VALID},
                FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE1, false));
        // and Tumor_Validation_Allele2 == Match_Norm_Validation_Allele2
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_GERMLINE, VALIDATION_STATUS_VALID},
                FIELD_TUMOR_VALIDATION_ALLELE2, FIELD_MATCH_NORM_VALIDATION_ALLELE2, false));

        // If Mutation_Status == 'Somatic' and Validation_Status == 'Valid', then Match_Norm_Validation_Allele1 == Match_Norm_Validation_Allele2 == Reference_Allele
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID},
                FIELD_MATCH_NORM_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE2, false));
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID},
                FIELD_MATCH_NORM_VALIDATION_ALLELE1, FIELD_REFERENCE_ALLELE, false));
        // and (Tumor_Validation_Allele1 or Tumor_Validation_Allele2) != Reference_Allele
        // *** logic in condition is structured as: (Tumor_Validation_Allele1 != Reference_Allele) or (Tumor_Validation_Allele2 != Reference_Allele)
        getConditions().add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID},
                        FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_REFERENCE_ALLELE, true), // not
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID},
                        FIELD_TUMOR_VALIDATION_ALLELE2, FIELD_REFERENCE_ALLELE, true))); // not

        // If Mutation_Status == 'LOH' and Validation_Status=='Valid', then Tumor_Validation_Allele1 == Tumor_Validation_Allele2
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID},
                FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_TUMOR_VALIDATION_ALLELE2, false));
        // and Match_Norm_Validation_Allele1 != Match_Norm_Validation_Allele2
        getConditions().add(new ConditionalRequirement(
                new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS},
                new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID},
                FIELD_MATCH_NORM_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE2, true)); // reversed (not equal)
        // and Tumor_Validation_Allele1 == (Match_Norm_Validation_Allele1 or Match_Norm_Validation_Allele2).
        // *** logic in condition is structured as: (Tumor_Validation_Allele1 == Match_Norm_Validation_Allele1) or (Tumor_Validation_Allele1 == Match_Norm_Validation_Allele2)
        getConditions().add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID},
                        FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE1, false),
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID},
                        FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE2, false)));

        //if (validation_status='Invalid') then  Tumor_Validation_Allele1 = Tumor_Validation_Allele2 = reference_allele
        getConditions().add(new ConditionalRequirement(ConditionalOperator.AND,
                new ConditionalRequirement(
                        new String[]{ FIELD_VALIDATION_STATUS},
                        new String[]{ VALIDATION_STATUS_INVALID},
                        FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_TUMOR_VALIDATION_ALLELE2, false),
                new ConditionalRequirement(
                        new String[]{ FIELD_VALIDATION_STATUS},
                        new String[]{ VALIDATION_STATUS_INVALID},
                        FIELD_TUMOR_VALIDATION_ALLELE2, FIELD_REFERENCE_ALLELE, false)));

        // if (validation_status='Invalid') then Tumor_Validation_Allele1, Tumor_Validation_Allele2, Match_Norm_Validation_Allele1, Match_Norm_Validation_Allele2 cannot be null
        getConditions().add(new ConditionalRequirement(ConditionalOperator.AND,
                new ConditionalRequirement(
                         FIELD_VALIDATION_STATUS,
                         VALIDATION_STATUS_INVALID,
                         FIELD_TUMOR_VALIDATION_ALLELE1,
                        NO_BLANK_PATTERN,
                        "not be blank", false),
                new ConditionalRequirement(
                        FIELD_VALIDATION_STATUS,
                        VALIDATION_STATUS_INVALID,
                        FIELD_TUMOR_VALIDATION_ALLELE2,
                        NO_BLANK_PATTERN,
                        "not be blank", false),
                new ConditionalRequirement(
                        FIELD_VALIDATION_STATUS,
                        VALIDATION_STATUS_INVALID,
                        FIELD_MATCH_NORM_VALIDATION_ALLELE1,
                        NO_BLANK_PATTERN,
                        "not be blank", false),
                new ConditionalRequirement(
                        FIELD_VALIDATION_STATUS,
                        VALIDATION_STATUS_INVALID,
                        FIELD_MATCH_NORM_VALIDATION_ALLELE2,
                        NO_BLANK_PATTERN,
                        "not be blank", false)));
        //if (validation_status='Invalid') then  Mutation status = None
        getConditions().add(new ConditionalRequirement(FIELD_VALIDATION_STATUS,
                VALIDATION_STATUS_INVALID,
                FIELD_MUTATION_STATUS,
                Pattern.compile("None"), "be 'None'", false));

    }

    public Boolean isMafProtected(final String mafFileName) {
        if (mafFileName != null && mafFileName.endsWith(".protected.maf")) {
            return true;
        } else if (mafFileName != null && mafFileName.endsWith(".somatic.maf")) {
            return false;
        }
        return false;
    }

    @Override
    protected Boolean checkSpecialProtectedMaf(final String[] row, final Map<String, Integer> fieldOrder, final int rowNum,
                                               final QcContext context, final String fileName) {
        boolean rowOk = true;
        final String mutationStatus = row[fieldOrder.get(FIELD_MUTATION_STATUS)];
        if (isMafProtected(fileName)) {
            if (!Pattern.compile("Germline|Somatic|LOH|None|Post-transcriptional modification|Unknown")
                    .matcher(mutationStatus).matches()) {
                context.addError(MessageFormat.format(MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName, rowNum, new StringBuilder().append("'").append(FIELD_MUTATION_STATUS)
                        .append("' value '").append(mutationStatus)
                        .append("' is invalid - must be Germline, Somatic, LOH, None, ")
                        .append("Post-transcriptional modification or Unknown").toString()));
                rowOk = false;
            }
        } else {
            if (!Pattern.compile("Somatic")
                    .matcher(mutationStatus).matches()) {
                context.addError(MessageFormat.format(MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName, rowNum, new StringBuilder().append("'").append(FIELD_MUTATION_STATUS)
                        .append("' value '").append(mutationStatus)
                        .append("' is invalid - must be Somatic").toString()));
                rowOk = false;
            }
        }
        return rowOk;
    }

    @Override
    protected boolean runMafSpecificSpecialChecks(final String[] row, final Map<String, Integer> fieldOrder, final int rowNum,
                                                  final QcContext context, final String fileName, final int start, final int end) {
        boolean rowOk = true;
        final String variantType = row[fieldOrder.get(FIELD_VARIANT_TYPE)];
        final int lengthRefAllele = getAlleleLength(row[fieldOrder.get(FIELD_REFERENCE_ALLELE)]);
        final int lengthTumorSeqAllele1 = getAlleleLength(row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE1)]);
        final int lengthTumorSeqAllele2 = getAlleleLength(row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE2)]);

        if (getVariantInsSymbol().equals(variantType)) {
            // If Variant_Type == "INS", then (End_position - Start_position + 1 == length (Reference_Allele) or End_position - Start_position == 1)
            if (end - start != 1 && lengthRefAllele != (end - start + 1)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantInsSymbol()).append(" then either ").
                                append(getFieldEndPosition()).append(" - ").append(getFieldStartPosition()).append(" = 1 or ").append(getFieldEndPosition()).append(" - ").
                                append(getFieldStartPosition()).append(" + 1 = length of ").append(FIELD_REFERENCE_ALLELE).toString()));
                rowOk = false;
            }
            // If Variant_Type == "INS" and end-start==1,
            // then (End_position - Start_position + 1 == length (Reference_Allele) ) or
            //  length (Reference_Allele) ==0 in case of "-" if neither of two case then error
            if ((end - start == 1) && (lengthRefAllele != 0 && lengthRefAllele != (end - start + 1))) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantInsSymbol()).append(" and ").
                                append(getFieldEndPosition()).append(" - ").append(getFieldStartPosition()).append(" = 1 then ( ").append(getFieldEndPosition()).
                                append(" - ").append(getFieldStartPosition()).append(" + 1 = length of ").append(FIELD_REFERENCE_ALLELE).append(" or ").
                                append(FIELD_REFERENCE_ALLELE).append(" should be '-' )").toString()));
                rowOk = false;
            }
            // and length(Reference_Allele) <= length(Tumor_Seq_Allele1 and Tumor_Seq_Allele2)
            if (lengthRefAllele > lengthTumorSeqAllele1 || lengthRefAllele > lengthTumorSeqAllele2) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantInsSymbol()).append(" then the length of ").
                                append(FIELD_REFERENCE_ALLELE).append(" must be less than or equal to the length of both ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" and ").
                                append(FIELD_TUMOR_SEQ_ALLELE2).toString()));
                rowOk = false;
            }
            // and length(Reference_Allele) < length(Tumor_Seq_Allele1 or Tumor_Seq_Allele2)
            if (lengthRefAllele >= lengthTumorSeqAllele1 && lengthRefAllele >= lengthTumorSeqAllele2) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantInsSymbol()).append(" then the length of ").
                                append(FIELD_REFERENCE_ALLELE).append(" must be less than the length of ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" or ").
                                append(FIELD_TUMOR_SEQ_ALLELE2).toString()));
                rowOk = false;
            }
        } else if (getVariantTypeDelSymbol().equals(variantType)) {

            // If Variant_Type == "DEL", then End_position - Start_position + 1 == length (Reference_Allele)
            if (lengthRefAllele != (end - start + 1)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantTypeDelSymbol()).append(" then ").
                                append(getFieldEndPosition()).append(" - ").append(getFieldStartPosition()).append(" + 1 should be equal to the length of the ").
                                append(FIELD_REFERENCE_ALLELE).toString()));
                rowOk = false;
            }
            // and length(Reference_Allele) >= length(Tumor_Seq_Allele1 and Tumor_Seq_Allele2)
            if (lengthRefAllele < lengthTumorSeqAllele1 || lengthRefAllele < lengthTumorSeqAllele2) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantTypeDelSymbol()).append(" then the length of ").
                                append(FIELD_REFERENCE_ALLELE).append(" must be greater than or equal to the length of both ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" and ").
                                append(FIELD_TUMOR_SEQ_ALLELE2).toString()));
                rowOk = false;
            }
            // and length(Reference_Allele) > length(Tumor_Seq_Allele1 or Tumor_Seq_Allele2)
            if (lengthRefAllele <= lengthTumorSeqAllele1 && lengthRefAllele <= lengthTumorSeqAllele2) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(getVariantTypeDelSymbol()).append(" then the length of ").
                                append(FIELD_REFERENCE_ALLELE).append(" must be greater than the length of ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" or ").
                                append(FIELD_TUMOR_SEQ_ALLELE2).toString()));
                rowOk = false;
            }
        } else if ("SNP".equals(variantType) || "DNP".equals(variantType) || "TNP".equals(variantType) || "ONP".equals(variantType)) {
            // If Variant_Type == 'SNP', then length(Reference_Allele and Tumor_Seq_Allele1 and Tumor_Seq_Allele2) == 1
            // If Variant_Type == 'DNP', then length(Reference_Allele and Tumor_Seq_Allele1 and Tumor_Seq_Allele2) == 2
            // If Variant_Type == 'TNP', then length(Reference_Allele and Tumor_Seq_Allele1 and Tumor_Seq_Allele2) == 3
            // If Variant_Type == 'ONP', then length(Reference_Allele) == length(Tumor_Seq_Allele1) == length(Tumor_Seq_Allele2) > 3
            int expectedLength = 1;
            if (variantType.equals("DNP")) {
                expectedLength = 2;
            } else if (variantType.equals("TNP")) {
                expectedLength = 3;
            } else if (variantType.equals("ONP")) {
                expectedLength = 4;
            }

            if (variantType.equals("ONP")) {
                if (lengthRefAllele < 4) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_REFERENCE_ALLELE).append(" length must be >3 (use SNP, DNP, or TNP)").toString()));
                    rowOk = false;
                }
                if (lengthTumorSeqAllele1 < 4) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" length must be >3 (use SNP, DNP, or TNP)").toString()));
                    rowOk = false;
                }
                if (lengthTumorSeqAllele2 < 4) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_TUMOR_SEQ_ALLELE2).append(" length must be >3 (use SNP, DNP, or TNP)").toString()));
                    rowOk = false;
                }
                if (lengthRefAllele != lengthTumorSeqAllele1 || lengthRefAllele != lengthTumorSeqAllele2) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_REFERENCE_ALLELE).append(", ").append(FIELD_TUMOR_SEQ_ALLELE1).append(", and ").
                                    append(FIELD_TUMOR_SEQ_ALLELE2).append(" values must be the same length").toString()));
                    rowOk = false;
                }
            } else {
                if (lengthRefAllele != expectedLength) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_REFERENCE_ALLELE).append(" length must be ").append(expectedLength).toString()));
                    rowOk = false;
                }
                if (lengthTumorSeqAllele1 != expectedLength) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).
                                    append(" then ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" length must be ").append(expectedLength).toString()));
                    rowOk = false;
                }
                if (lengthTumorSeqAllele2 != expectedLength) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).append(" then ").
                                    append(FIELD_TUMOR_SEQ_ALLELE2).append(" length must be ").append(expectedLength).toString()));
                    rowOk = false;
                }
            }

            // and (Reference_Allele and Tumor_Seq_Allele1 and Tumor_Seq_Allele2) !contains '-'  (for SNP, DNP, TNP, ONP)
            if (row[fieldOrder.get(FIELD_REFERENCE_ALLELE)].contains("-")) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).append(" then ").
                                append(FIELD_REFERENCE_ALLELE).append(" value must not ").append((expectedLength == 1 ? "be" : "contain")).append(" '-'").toString()));
                rowOk = false;
            }
            if (row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE1)].contains("-")) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).append(" then ").
                                append(FIELD_TUMOR_SEQ_ALLELE1).append(" value must not ").append((expectedLength == 1 ? "be" : "contain")).append(" '-'").toString()));
                rowOk = false;
            }
            if (row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE2)].contains("-")) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("if ").append(FIELD_VARIANT_TYPE).append(" is ").append(variantType).append(" then ").
                                append(FIELD_TUMOR_SEQ_ALLELE2).append(" value must not ").append((expectedLength == 1 ? "be" : "contain")).append(" '-'").toString()));
                rowOk = false;
            }
        }

        final String centerValue = row[fieldOrder.get(FIELD_MAF2_CENTER)];
        if (centerValue.contains(",")) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                    fileName,
                    rowNum,
                    new StringBuilder().append("multiple Center values must be delimited by semicolons").toString()
            ));
            rowOk = false;
        } else if (centerQueries != null) {
            final String[] centers = centerValue.split(MULTI_VALUE_SEPARATOR, -1);
            for (final String center : centers) {
                final Integer centerId = centerQueries.findCenterId(center, Experiment.TYPE_GSC);
                if (centerId == null) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                            fileName,
                            rowNum,
                            new StringBuilder().append("Center value '").append(center).append("' is not a valid GSC domain").toString()
                    ));
                    rowOk = false;
                }
            }
        }
        return rowOk;
    }

    private int getAlleleLength(final String allele) {
        if (allele.equals("-")) {
            return 0;
        } else {
            return allele.length();
        }
    }

    @Override
    protected List<String> getMafFieldList() {
        return getMafFieldList(false);
    }

    @Override
    protected String getFieldEndPosition() {
        return FIELD_MAF2_END_POSITION;
    }

    @Override
    protected String getFieldStartPosition() {
        return FIELD_MAF2_START_POSITION;
    }

    @Override
    protected String getVariantInsSymbol() {
        return SYMBOL_MAF2_VARIANT_TYPE_INS;
    }

    @Override
    protected String getVariantTypeDelSymbol() {
        return SYMBOL_MAF2_VARIANT_TYPE_DEL;
    }

    @Override
    protected void setupMafSpecificChecks() {
        getRequiredFieldDescriptions().put(FIELD_MAF2_CENTER, "must be in the format of genome.wustl.edu");
        getRequiredMafExpressions().put(FIELD_MAF2_START_POSITION, Pattern.compile("\\d+")); // number
        getRequiredFieldDescriptions().put(FIELD_MAF2_START_POSITION, "must be an integer number");
        getRequiredMafExpressions().put(FIELD_MAF2_END_POSITION, Pattern.compile("\\d+"));  // number
        getRequiredFieldDescriptions().put(FIELD_MAF2_END_POSITION, "must be an integer number");
        getRequiredMafExpressions().put(FIELD_NCBI_BUILD, Pattern.compile("hg18|hg19|GRCh37|GRCh37-lite|36|36.1|37", Pattern.CASE_INSENSITIVE));
        getRequiredFieldDescriptions().put(FIELD_NCBI_BUILD, "must be hg18, hg19, GRCh37, GRCh37-lite, 36, 36.1 or 37");
        getRequiredMafExpressions().put(FIELD_DBSNP_RS, Pattern.compile("(novel|rs[0-9]+|ss[0-9]+|\\S?)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_DBSNP_RS, "must be 'novel', dbSNP_ID, or blank");
        getRequiredMafExpressions().put(FIELD_DBSNP_VAL_STATUS, Pattern.compile("(by1000genomes|by2Hit2Allele|byCluster|byFrequency|byHapMap|byOtherPop|bySubmitter|alternate_allele|\\S?)?", Pattern.CASE_INSENSITIVE));
        getRequiredFieldDescriptions().put(FIELD_DBSNP_VAL_STATUS, "must be by1000genomes, by2Hit2Allele, byCluster, byFrequency, byHapMap, byOtherPop, bySubmitter, alternate_allele or blank");
        getRequiredMafExpressions().put(FIELD_STRAND, Pattern.compile("\\+"));
        getRequiredFieldDescriptions().put(FIELD_STRAND, "must be +");
        getRequiredMafExpressions().put(FIELD_VARIANT_CLASSIFICATION, Pattern.compile("Frame_Shift_Del|Frame_Shift_Ins|In_Frame_Del|In_Frame_Ins|Missense_Mutation|Nonsense_Mutation|Silent|Splice_Site|Nonstop_Mutation|3'UTR|3'Flank|5'UTR|5'Flank|IGR|Intron|RNA|Targeted_Region|Translation_Start_Site"));
        getRequiredFieldDescriptions().put(FIELD_VARIANT_CLASSIFICATION, "must be one of Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, Silent, Splice_Site, Nonstop_Mutation, 3'UTR, 3'Flank, 5'UTR, 5'Flank, IGR, Intron, RNA, Targeted_Region or Translation_Start_Site");
        getRequiredMafExpressions().put(FIELD_VARIANT_TYPE, Pattern.compile("SNP|INS|DEL|DNP|TNP|ONP|Consolidated"));
        getRequiredFieldDescriptions().put(FIELD_VARIANT_TYPE, "must be SNP, INS, DNP, TNP, ONP, Consolidated or DEL");
        getRequiredMafExpressions().put(FIELD_REFERENCE_ALLELE, Pattern.compile("A|C|G|T|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_REFERENCE_ALLELE, "must be A,C,G,T and/or -");
        getRequiredMafExpressions().put(FIELD_TUMOR_SEQ_ALLELE1, Pattern.compile("A|C|G|T|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_TUMOR_SEQ_ALLELE1, "must be A,C,G,T and/or -");
        getRequiredMafExpressions().put(FIELD_TUMOR_SEQ_ALLELE2, Pattern.compile("A|C|G|T|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_TUMOR_SEQ_ALLELE2, "must be A,C,G,T and/or -");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_SEQ_ALLELE1, Pattern.compile("(\\S?|-|[ACGT\\-]+)?"));  // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_SEQ_ALLELE1, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_SEQ_ALLELE2, Pattern.compile("(\\S?|-|[ACGT\\-]+)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_SEQ_ALLELE2, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_TUMOR_VALIDATION_ALLELE1, Pattern.compile("(\\S?|-|[ACGT\\-]+)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_TUMOR_VALIDATION_ALLELE1, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_TUMOR_VALIDATION_ALLELE2, Pattern.compile("(\\S?|-|[ACGT\\-]+)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_TUMOR_VALIDATION_ALLELE2, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_VALIDATION_ALLELE1, Pattern.compile("(\\S?|-|[ACGT\\-]+)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_VALIDATION_ALLELE1, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_VALIDATION_ALLELE2, Pattern.compile("(\\S?|-|[ACGT\\-]+)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_VALIDATION_ALLELE2, "must either be '-' for deleted, or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_VERIFICATION_STATUS, Pattern.compile("(Verified|Unknown|\\S?)?")); // match 0 or 1 times so blank allowed
        getRequiredFieldDescriptions().put(FIELD_VERIFICATION_STATUS, "must be Verified, Unknown, or blank");
        getRequiredMafExpressions().put(FIELD_VALIDATION_STATUS, Pattern.compile("Untested|Inconclusive|Valid|Invalid"));
        getRequiredFieldDescriptions().put(FIELD_VALIDATION_STATUS, "must be Untested, Inconclusive, Valid, or Invalid");
        getRequiredMafExpressions().put(FIELD_VALIDATION_METHOD, Pattern.compile("[A-Za-z0-9_;\\ ]+"));
        getRequiredFieldDescriptions().put(FIELD_VALIDATION_METHOD, "must be something like: Sanger_PCR_WGA, Sanger_PCR_gDNA, 454_PCR_WGA, 454_PCR_gDNA, Illumina GAIIx, SOLiD; separate multiple entries using semicolons. Blank is not allowed");
        getRequiredMafExpressions().put(FIELD_SEQUENCE_SOURCE, Pattern.compile("WGS|WGA|WXS|RNA-Seq|miRNA-Seq|ncRNA-Seq|WCS|CLONE|POOLCLONE|AMPLICON|CLONEEND|FINISHING|ChIP-Seq|MNase-Seq|DNase-Hypersensitivity|Bisulfite-Seq|EST|FL-cDNA|CTS|MRE-Seq|MeDIP-Seq|MBD-Seq|Tn-Seq|VALIDATION|FAIRE-seq|SELEX,RIP-Seq|ChIA-PET|Other"));
        getRequiredFieldDescriptions().put(FIELD_SEQUENCE_SOURCE, "must be one or more of WGS, WGA, WXS, RNA-Seq, miRNA-Seq, ncRNA-Seq, WCS, CLONE, " +
                "POOLCLONE, AMPLICON, CLONEEND, FINISHING, ChIP-Seq, MNase-Seq, DNase-Hypersensitivity, Bisulfite-Seq, EST, FL-cDNA, CTS, MRE-Seq, " +
                "MeDIP-Seq, MBD-Seq, Tn-Seq, VALIDATION, FAIRE-seq, SELEX, RIP-Seq, ChIA-PET, Other; separate multiple values using semicolons");
        getRequiredMafExpressions().put(FIELD_SEQUENCER, Pattern.compile("Illumina GAIIx|Illumina HiSeq|SOLID|454|ABI 3730xl|" +
                "Ion Torrent PGM|Ion Torrent Proton|PacBio RS|Illumina MiSeq|Illumina HiSeq 2500|454 GS FLX Titanium|AB SOLiD 4 System"));
        getRequiredFieldDescriptions().put(FIELD_SEQUENCER, "must be one or more of Illumina GAIIx, Illumina HiSeq, SOLID, 454, ABI 3730xl, " +
                "Ion Torrent PGM, Ion Torrent Proton, PacBio RS, Illumina MiSeq, Illumina HiSeq 2500, 454 GS FLX Titanium, AB SOLiD 4 System; " +
                "separate multiple entries with semicolon");
    }

    public CenterQueries getCenterQueries() {
        return centerQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    @Override
    protected void validateFilename(final String filename, final QcContext context) throws ProcessorException {


        // This validation is tied  with archive platform base data type. This validation should be done only in QClive
        // as some platforms (IlluminaGA_DNASeq_Cont) are dcc specific platforms and the client submitted archives
        // will not have those platforms.

        if(!context.isStandaloneValidator()) {
            final String archiveDataTYpeName = getDataTypeQueries().getBaseDataTypeNameForPlatform(context.getPlatformName());
            if (DataTypeName.PROTECTED_MUTATIONS.getValue().equals(archiveDataTYpeName)) {
                if (!filename.endsWith(FileName.PROTECTED_MAF_EXTENSION.getValue())) {
                    throw new ProcessorException("Failed processing maf file " + filename + ". File extension must be " + FileName.PROTECTED_MAF_EXTENSION);
                }
            } else if (DataTypeName.SOMATIC_MUTATIONS.getValue().equals(archiveDataTYpeName)) {
                if (!filename.endsWith(FileName.SOMATIC_MAF_EXTENSION.getValue())) {
                    throw new ProcessorException("Failed processing maf file " + filename + ". File extension must be " + FileName.SOMATIC_MAF_EXTENSION);
                }
            } else {
                throw new ProcessorException("Failed processing maf file " + filename + ". DCC does not support " + ((archiveDataTYpeName == null)?"unknown":archiveDataTYpeName)+ " maf files yet.");
            }
        }
        if (filename.endsWith(FileName.SOMATIC_MAF_EXTENSION.getValue())) {
            if (filename.contains(FileName.GERM_MAF_FILE.getValue()) ||
                    filename.contains(FileName.PROTECTED_MAF_FILE.getValue())) {
                throw new ProcessorException("Failed processing maf file " + filename + ". Somatic maf files must not have 'germ' or 'protected'  text in the filename.");

            }
        }

        if (filename.endsWith(FileName.PROTECTED_MAF_EXTENSION.getValue())) {
            if (filename.contains(FileName.SOMATIC_MAF_FILE.getValue())) {
                throw new ProcessorException("Failed processing maf file " + filename + ". Protected maf files must not have 'somatic' text in the filename.");

            }
        }

    }

    /**
     * Validate somatic maf files.
     * For somatic maf files
     * ((Validation_Status == "Valid") or (Verification_Status == "Verified") or
     * (Variant_Classification is {Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, Silent, Splice_Site, Translation_Start_Site, Nonstop_Mutation, RNA, Targeted_Region})

     * @param row
     * @param fieldOrder
     * @param rowNum
     * @param context
     * @param fileName
     * @return
     */
    @Override
    protected Boolean validatePublicMaf(final String[] row, final Map<String, Integer> fieldOrder, final int rowNum,
                                        final QcContext context, final String fileName) {
        if(!isMafProtected(fileName)) {
            if(!(VALIDATION_STATUS_VALID.equals( row[fieldOrder.get(FIELD_VALIDATION_STATUS)])||
                    VERIFICATION_STATUS_VERIFIED.equals(row[fieldOrder.get(FIELD_VERIFICATION_STATUS)]) ||
                Pattern.compile("Frame_Shift_Del|Frame_Shift_Ins|In_Frame_Del|In_Frame_Ins|Missense_Mutation|Nonsense_Mutation|Silent|Splice_Site|Translation_Start_Site|Nonstop_Mutation|RNA|Targeted_Region")
                            .matcher(row[fieldOrder.get(FIELD_VARIANT_CLASSIFICATION)]).matches())) {
                context.addError(MessageFormat.format(MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName, rowNum, new StringBuilder()
                        .append("Must be either")
                        .append(" Validation_Status(")
                        .append(row[fieldOrder.get(FIELD_VALIDATION_STATUS)])
                        .append(")== 'Valid' or ")
                        .append(" Verification_Status(")
                        .append(row[fieldOrder.get(FIELD_VERIFICATION_STATUS)])
                        .append(")== 'Verified' or")
                        .append(" Variant_Classification(")
                        .append(row[fieldOrder.get(FIELD_VARIANT_CLASSIFICATION)])
                        .append(")== {Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, Silent, Splice_Site, Translation_Start_Site, Nonstop_Mutation, RNA, Targeted_Region}")
                        .toString()));
                return false;
            }

        }
        return true;
    }

    public DataTypeQueries getDataTypeQueries() {
        return dataTypeQueries;
    }

    public void setDataTypeQueries(DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }
}
