/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractMafFileHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates all maf files in an archive.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public class MafFileValidator extends AbstractMafFileHandler<Boolean> {

    public static final String MULTI_VALUE_SEPARATOR = ";";

    protected HashMap<String,Boolean> sampleCodes = new HashMap<String,Boolean>();   
    protected static final String VALIDATION_STATUS_VALID = "Valid";
    protected static final String VALIDATION_STATUS_WILDTYPE = "Wildtype";
    protected static final String MUTATION_STATUS_GERMLINE = "Germline";
    protected static final String MUTATION_STATUS_SOMATIC = "Somatic";
    protected static final String MUTATION_STATUS_LOH = "LOH";
    protected static final String VALIDATION_STATUS_UNKNOWN = "Unknown";
    
    // a map of fields and their regexp to use to check the value -- must match ENTIRE value
    private final Map<String, Pattern> requiredMafExpressions = new HashMap<String, Pattern>();
    private final Map<String, String> requiredFieldDescriptions = new HashMap<String, String>();
    private final List<String> multiValueAllowedFields = new ArrayList<String>();
    private final List<String> barcodeFields = new ArrayList<String>();
    private final List<String> uuidFields = new ArrayList<String>();
    private final List<ConditionalRequirement> conditions = new ArrayList<ConditionalRequirement>();
    private ChromInfoUtils chromInfoUtils;
    private SampleTypeQueries sampleTypeQueries;
    private ShippedBiospecimenQueries shippedBiospecimenQueries;
	private BarcodeTumorValidator barcodeTumorValidator;
	private static final String VARIANT_TYPE_INS = "Ins";
    private static final String VARIANT_TYPE_DEL = "Del";
    private static final String MAF_FILE_EXTENSION = "maf";
    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;
    private ThreadLocal<Set<String>> barcodeList = new ThreadLocal<Set<String>>();
    private ThreadLocal<Set<String>> uuidList = new ThreadLocal<Set<String>>();
    
    public MafFileValidator() {
        initConditions();
        setupRequiredValidation();
    }

    // subclasses should override -- ugh we need an abstract parent class...
    protected void initSpecSpecificConditions() {
        // THESE ARE ALL FOR MAF SPEC 1.0 ONLY

        //If variant_Type is "ins" Reference_Allele should always be "-",
        conditions.add(new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantInsSymbol(), FIELD_REFERENCE_ALLELE, Pattern.compile("[^\\-]"), "contain anything but '-' characters", true));
        // and either of Tumor_Seq_Allele1 and Tumor_Seq_Allele2 should have "-".
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantInsSymbol(), FIELD_TUMOR_SEQ_ALLELE1, Pattern.compile("-"), "contain at least one '-'", false),
                new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantInsSymbol(), FIELD_TUMOR_SEQ_ALLELE2, Pattern.compile("-"), "contain at least one '-'", false)));
        //If variant_Type is "del", then Reference_Allele can't contain "-",
        conditions.add(new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantTypeDelSymbol(), FIELD_REFERENCE_ALLELE, Pattern.compile("-"), "contain any '-' characters", true));
        // and either Tumor_Seq_Allele1 or Tumor_Seq_Allele2 should  contain "-".
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantTypeDelSymbol(), FIELD_TUMOR_SEQ_ALLELE1, Pattern.compile("-"), "contain at least one '-'", false),
                new ConditionalRequirement(FIELD_VARIANT_TYPE, getVariantTypeDelSymbol(), FIELD_TUMOR_SEQ_ALLELE2, Pattern.compile("-"), "contain at least one '-'", false)));

        //If validation_status is "wildtype", then Tumor_Seq_Allele1=Tumor_Seq_Allele2
        //and Tumor_Seq_Allele1=Reference_Allele)
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_WILDTYPE, FIELD_TUMOR_SEQ_ALLELE1, FIELD_TUMOR_SEQ_ALLELE2, false));
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_WILDTYPE, FIELD_TUMOR_SEQ_ALLELE1, FIELD_REFERENCE_ALLELE, false));

        // if mutation_status is germline, tumor_seql_allele1 must be equal to match_norm_seq_allele1
        conditions.add(new ConditionalRequirement(FIELD_MUTATION_STATUS, MUTATION_STATUS_GERMLINE, FIELD_TUMOR_SEQ_ALLELE1,
                FIELD_MATCH_NORM_SEQ_ALLELE1, false));
        conditions.add(new ConditionalRequirement(FIELD_MUTATION_STATUS, MUTATION_STATUS_GERMLINE, FIELD_TUMOR_SEQ_ALLELE2,
                FIELD_MATCH_NORM_SEQ_ALLELE2, false));
        // if mutation_status is somatic and validation status is valid, then Match_Norm_Validation_Allele1 should equal Reference_Allele
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID}, FIELD_MATCH_NORM_VALIDATION_ALLELE1,
                FIELD_REFERENCE_ALLELE, false));
        // if mutation_status is somatic and validation status is valid, then Match_Norm_Validation_Allele2 should equal Reference_Allele
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID}, FIELD_MATCH_NORM_VALIDATION_ALLELE2,
                FIELD_REFERENCE_ALLELE, false));
        // if mutation_status is somatic and validation status is valid, then either Tumor_Seq_Allele1 or Tumor_Seq_Allele2 should NOT match Reference_Allele
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID}, FIELD_TUMOR_SEQ_ALLELE1, FIELD_REFERENCE_ALLELE, true),  // not
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_SOMATIC, VALIDATION_STATUS_VALID}, FIELD_TUMOR_SEQ_ALLELE2, FIELD_REFERENCE_ALLELE, true))); // not

        //If Mutation_Status == LOH AND Validation_Status==Unknown, then
        //Tumor_Seq_Allele1  ==  Tumor_Seq_Allele2 and
        //Match_Norm_Seq_Allele1 != Match_Norm_Seq_Allele2 and
        //Tumor_Seq_Allele1 = (Match_Norm_Seq_Allele1 or Match_Norm_Seq_Allele2)
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_UNKNOWN}, FIELD_TUMOR_SEQ_ALLELE1, FIELD_TUMOR_SEQ_ALLELE2, false));
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_UNKNOWN}, FIELD_MATCH_NORM_SEQ_ALLELE1, FIELD_MATCH_NORM_SEQ_ALLELE2, true));  // not
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_UNKNOWN}, FIELD_TUMOR_SEQ_ALLELE1, FIELD_MATCH_NORM_SEQ_ALLELE1, false),
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_UNKNOWN}, FIELD_TUMOR_SEQ_ALLELE1, FIELD_MATCH_NORM_SEQ_ALLELE2, false)));
        //If Mutation_Status == LOH AND Validation_Status==Valid, then
        //Tumor_Validation_Allele1 ==  Tumor_Validation_Allele2 and
        //Match_Norm_Validation_Allele1 != Match_Norm_Validation_Allele2 and
        //Tumor_Validation_Allele1 == (Match_Norm_Validation_Allele1 or Match_Norm_Validation_Allele2).
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID}, FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_TUMOR_VALIDATION_ALLELE2, false));
        conditions.add(new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID}, FIELD_MATCH_NORM_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE2, true));  // not
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID}, FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE1, false),
                new ConditionalRequirement(new String[]{FIELD_MUTATION_STATUS, FIELD_VALIDATION_STATUS}, new String[]{MUTATION_STATUS_LOH, VALIDATION_STATUS_VALID}, FIELD_TUMOR_VALIDATION_ALLELE1, FIELD_MATCH_NORM_VALIDATION_ALLELE2, false)));
        // Tumor_Seq_Allele1 != Reference_Allele OR  Tumor_Seq_Allele2 != Reference_Allele
        conditions.add(new ConditionalRequirement(ConditionalOperator.OR,
                new ConditionalRequirement(new String[]{}, new String[]{}, FIELD_TUMOR_SEQ_ALLELE1, FIELD_REFERENCE_ALLELE, true),
                new ConditionalRequirement(new String[]{}, new String[]{}, FIELD_TUMOR_SEQ_ALLELE2, FIELD_REFERENCE_ALLELE, true)));

    }

    private void initConditions() {
        barcodeFields.add(FIELD_TUMOR_SAMPLE_BARCODE);
        barcodeFields.add(FIELD_MATCHED_NORM_SAMPLE_BARCODE);
        uuidFields.add(FIELD_TUMOR_SAMPLE_UUID);
        uuidFields.add(FIELD_MATCHED_NORM_SAMPLE_UUID);
        // if Validation_Status is 'Valid' then Tumor_Validation_Allele1 must match be made only of ACGT and -
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE1,
                Pattern.compile("[^TCGA\\-]"), "contain characters other than 'A', 'C', 'G', 'T', and '-' ", true));
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_TUMOR_VALIDATION_ALLELE2,
                Pattern.compile("[^TCGA\\-]"), "contain characters other than 'A', 'C', 'G', 'T', and '-' ", true));
        // if Validation_Status is 'Valid' then Verification_Status must not be wildtype
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_VALID, FIELD_VERIFICATION_STATUS,
                Pattern.compile("^wildtype$"), "be 'Wildtype'", true));
        // if Validation_Status is wildtype, then Verification_Status must not be Verified
        conditions.add(new ConditionalRequirement(FIELD_VALIDATION_STATUS, VALIDATION_STATUS_WILDTYPE, FIELD_VERIFICATION_STATUS,
                Pattern.compile("^Verified$"), "be 'Verified'", true));

        initSpecSpecificConditions();                                     
    }

    protected String getVariantTypeDelSymbol() {
        return VARIANT_TYPE_DEL;
    }

    private void setupRequiredValidation() {
        //Common validations to both MAF and MAF2
        requiredMafExpressions.put(FIELD_HUGO_SYMBOL, Pattern.compile("\\S+"));
        requiredFieldDescriptions.put(FIELD_HUGO_SYMBOL, "may not be blank");
        requiredMafExpressions.put(FIELD_ENTREZ_GENE_ID, Pattern.compile("\\d+")); // number
        requiredFieldDescriptions.put(FIELD_ENTREZ_GENE_ID, "must be an integer number");
        requiredMafExpressions.put(FIELD_MATCHED_NORM_SAMPLE_BARCODE, QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN);
        requiredFieldDescriptions.put(FIELD_MATCHED_NORM_SAMPLE_BARCODE, "must be a full aliquot barcode");
        requiredMafExpressions.put(FIELD_TUMOR_SAMPLE_BARCODE, QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN);
        requiredFieldDescriptions.put(FIELD_TUMOR_SAMPLE_BARCODE, "must be a full aliquot barcode");
        requiredMafExpressions.put(FIELD_VALIDATION_STATUS, Pattern.compile("Valid|Wildtype|Unknown|\\S?"));
        requiredFieldDescriptions.put(FIELD_VALIDATION_STATUS, "must be Valid, Wildtype, Unknown, or blank");
        requiredMafExpressions.put(FIELD_CHROMOSOME, Pattern.compile("\\S+"));
        requiredFieldDescriptions.put(FIELD_CHROMOSOME, "must be one of: X, Y, M, 1-22, or full name of unassigned fragment");
        setupMafSpecificChecks();
    }

    protected void setupMafSpecificChecks() {
        getRequiredMafExpressions().put(FIELD_CENTER, Pattern.compile("\\S+"));
        getRequiredFieldDescriptions().put(FIELD_CENTER, "may not be blank");
        getRequiredMafExpressions().put(getFieldStartPosition(), Pattern.compile("\\d+")); // number
        getRequiredFieldDescriptions().put(getFieldStartPosition(), "must be an integer number");
        getRequiredMafExpressions().put(getFieldEndPosition(), Pattern.compile("\\d+"));  // number
        getRequiredFieldDescriptions().put(getFieldEndPosition(), "must be an integer number");
        getRequiredMafExpressions().put(FIELD_NCBI_BUILD, Pattern.compile("\\d+\\.?\\d*"));  // decimal number e.g. 36.1
        getRequiredFieldDescriptions().put(FIELD_NCBI_BUILD, "must be a decimal number e.g. 36.1");
        getRequiredMafExpressions().put(FIELD_DBSNP_RS, Pattern.compile("\\S+"));
        getRequiredFieldDescriptions().put(FIELD_DBSNP_RS, "may not be blank");
        getRequiredMafExpressions().put(FIELD_DBSNP_VAL_STATUS, Pattern.compile("byCluster|bySubmitter|byFrequency|by2hit2allele|byHapmap|none|unknown"));
        getRequiredFieldDescriptions().put(FIELD_DBSNP_VAL_STATUS, "must be byCluster, bySubmitter, byFrequency, by2hit2allele, byHapmap, none, or unknown");
        getRequiredMafExpressions().put(FIELD_STRAND, Pattern.compile("\\+|\\-"));
        getRequiredFieldDescriptions().put(FIELD_STRAND, "must be + or -");
        getRequiredMafExpressions().put(FIELD_VARIANT_CLASSIFICATION, Pattern.compile("Missense_Mutation|Nonsense_Mutation|Silent|Splice_Site_SNP|Frame_Shift_Ins|Frame_Shift_Del|In_Frame_Del|In_Frame_Ins|Splice_Site_Indel"));
        getRequiredFieldDescriptions().put(FIELD_VARIANT_CLASSIFICATION, "must be Missense_Mutation, Nonsense_Mutation, Silent, Splice_Site_SNP, Frame_Shift_Ins, Frame_Shift_Del, In_Frame_Del, In_Frame_Ins, or Splice_Site_Indel");
        getRequiredMafExpressions().put(FIELD_VARIANT_TYPE, Pattern.compile("SNP|Ins|Del"));
        getRequiredFieldDescriptions().put(FIELD_VARIANT_TYPE, "must be SNP, Ins, or Del");
        getRequiredMafExpressions().put(FIELD_REFERENCE_ALLELE, Pattern.compile("nt|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_REFERENCE_ALLELE, "must either be 'nt', '-', or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_TUMOR_SEQ_ALLELE1, Pattern.compile("nt|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_TUMOR_SEQ_ALLELE1, "must either be 'nt', '-', or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_TUMOR_SEQ_ALLELE2, Pattern.compile("nt|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_TUMOR_SEQ_ALLELE2, "must either be 'nt', '-', or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_SEQUENCING_PHASE, Pattern.compile("\\d+")); // a number
        getRequiredFieldDescriptions().put(FIELD_SEQUENCING_PHASE, "must be an integer number");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_SEQ_ALLELE1, Pattern.compile("\\S?|nt|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_SEQ_ALLELE1, "must either be blank, 'nt', '-', or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_MATCH_NORM_SEQ_ALLELE2, Pattern.compile("\\S?|nt|-|[ACGT\\-]+"));
        getRequiredFieldDescriptions().put(FIELD_MATCH_NORM_SEQ_ALLELE2, "must either be blank, 'nt', '-', or be composed of A, C, G, T, and '-'");
        getRequiredMafExpressions().put(FIELD_VERIFICATION_STATUS, Pattern.compile("Verified|Wildtype|Unknown"));
        getRequiredFieldDescriptions().put(FIELD_VERIFICATION_STATUS, "must be Verified, Wildtype, or Unknown");
        getRequiredMafExpressions().put(FIELD_MUTATION_STATUS, Pattern.compile("Germline|Somatic|LOH|Unknown"));
        getRequiredFieldDescriptions().put(FIELD_MUTATION_STATUS, "must be Germline, Somatic, LOH, or Unknown");
    }

    public Map<String, Pattern> getRequiredMafExpressions() {
        return getRequiredMafExpressions(false);
    }
    
    public Map<String, Pattern> getRequiredMafExpressions(final boolean isCenterConvertedToUUID) {
    	
    	if(isCenterConvertedToUUID) {
    		final Map<String, Pattern> requiredMafExpressionsWithUUID = new HashMap<String, Pattern>();
    		requiredMafExpressionsWithUUID.putAll(requiredMafExpressions);
    		requiredMafExpressionsWithUUID.put(FIELD_TUMOR_SAMPLE_UUID, QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN);
    		requiredMafExpressionsWithUUID.put(FIELD_MATCHED_NORM_SAMPLE_UUID, QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN);
    		
    		return requiredMafExpressionsWithUUID;
    	}
    	
        return requiredMafExpressions;
    }
    
    public Map<String, String> getRequiredFieldDescriptions() {
        return getRequiredFieldDescriptions(false);
    }
    
    public Map<String, String> getRequiredFieldDescriptions(final boolean isCenterConvertedToUUID) {
    	
    	if(isCenterConvertedToUUID) {
    		final Map<String, String> requiredFieldDescriptionsWithUUID = new HashMap<String, String>();
    		requiredFieldDescriptionsWithUUID.putAll(requiredFieldDescriptions);
    		requiredFieldDescriptionsWithUUID.put(FIELD_TUMOR_SAMPLE_UUID, "must be a valid aliquot UUID");
    		requiredFieldDescriptionsWithUUID.put(FIELD_MATCHED_NORM_SAMPLE_UUID, "must be a valid aliquot UUID");
    		
    		return requiredFieldDescriptionsWithUUID;
    	}
    	
        return requiredFieldDescriptions;
    }

    public List<String> getMultiValueAllowedFields() {
        return multiValueAllowedFields;
    }

    /* This is a list of ConditionalRequirements that each row of the maf file has to meet */

    public List<ConditionalRequirement> getConditions() {
        return conditions;
    }

    public void setBarcodeValidator(final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    public QcLiveBarcodeAndUUIDValidator getBarcodeValidator() {
        return qcLiveBarcodeAndUUIDValidator;
    }

    public enum ConditionalOperator {
        AND, OR
    }

    /**
     * Class representing a conditional requirement.  The rule for each is: if the conditionalField's value is equal to
     * conditionalValue then field's value must match the pattern OR equal the value of the compareField.  If "reverse"
     * is true then the status is the reverse of the match (ie false if it matched, true if it didn't). If the
     * conditionalField's value is not equal to the conditionalValue, then the requirement is also satisfied.
     */
    public static class ConditionalRequirement {

        private String[] conditionalFields, conditionalValues;
        private String field, description, compareField;
        private Pattern pattern;
        private boolean reverse;
        private ConditionalRequirement[] requirements;
        private boolean or; // if true, requirements are OR, otherwise AND

        /**
         * Build a ConditionalRequirement out of other ConditionalRequirements
         *
         * @param operator     ConditionalOperator, either AND or OR
         * @param requirements sub-requirements
         */
        public ConditionalRequirement(final ConditionalOperator operator, final ConditionalRequirement... requirements) {
            this.requirements = requirements;
            this.or = (operator == ConditionalOperator.OR);

        }

        /**
         * Build a ConditionalRequirement with a Pattern.
         *
         * @param conditionalField the field name of the condition
         * @param conditionalValue the value for the condition
         * @param field            the field to check if the condition is true
         * @param pattern          the pattern to match the field value against
         * @param patternDescr     the description of the pattern
         * @param reverse          if true, is satisfied if pattern DOESN'T match
         */
        public ConditionalRequirement(
                final String conditionalField, final String conditionalValue, final String field,
                final Pattern pattern, final String patternDescr, final boolean reverse) {
            this.conditionalFields = new String[1];
            conditionalFields[0] = conditionalField;
            this.conditionalValues = new String[1];
            conditionalValues[0] = conditionalValue;
            this.field = field;
            this.pattern = pattern;
            this.description = patternDescr;
            this.reverse = reverse;
        }

        public ConditionalRequirement(
                final String[] conditionalFields, final String[] conditionalValues,
                final String field,
                final String compareField, final boolean reverse) {
            this.conditionalFields = conditionalFields;
            this.conditionalValues = conditionalValues;
            this.field = field;
            this.compareField = compareField;
            this.description = "be equal to " + compareField;
            this.reverse = reverse;
        }

        public ConditionalRequirement(
                final String conditionalField, final String conditionalValue, final String field,
                final String compareField, final boolean reverse) {
            this.conditionalFields = new String[1];
            conditionalFields[0] = conditionalField;
            this.conditionalValues = new String[1];
            conditionalValues[0] = conditionalValue;
            this.field = field;
            this.compareField = compareField;
            this.description = "be equal to " + compareField;
            this.reverse = reverse;
        }

        public boolean isSatisfied(final String[] row, final Map<String, Integer> fieldOrder) {
            if (requirements != null) {
                boolean satisfied = true;
                for (final ConditionalRequirement req : requirements) {
                    final boolean reqSatisfied = req.isSatisfied(row, fieldOrder);
                    if (or && reqSatisfied) {
                        return true;
                    } else {
                        satisfied = satisfied && reqSatisfied;
                    }
                }
                return satisfied;
            } else {
                boolean meetsCondition = true;
                for (int i = 0; i < conditionalFields.length; i++) {
                    if (!conditionalValues[i].equals(row[fieldOrder.get(conditionalFields[i])])) {
                        meetsCondition = false;
                    }
                }
                if (meetsCondition) {
                    final boolean satisfied;
                    if (pattern != null) {
                        // if there is a pattern, compare the field value to the pattern
                        satisfied = pattern.matcher(row[fieldOrder.get(field)]).find();
                    } else {
                        // if there is a compareField, compare the field's value to its value
                        satisfied = row[fieldOrder.get(compareField)].equals(row[fieldOrder.get(field)]);
                    }
                    // if reverse, return true if pattern/equality failed to match, otherwise return true if it did
                    return reverse ? !satisfied : satisfied;
                } else {
                    // if condition doesn't match, don't check anything
                    return true;
                }
            }
        }

        public String errorString(final String[] row, final Map<String, Integer> fieldOrder) {
            if (requirements != null) {
                return errorString(row, fieldOrder, false);
            } else {
                return errorString(row, fieldOrder, true);
            }
        }

        private String errorString(
                final String[] row, final Map<String, Integer> fieldOrder,
                final boolean withCondition) {
            if (requirements != null) {
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < requirements.length; i++) {
                    sb.append(requirements[i].errorString(row, fieldOrder, withCondition));
                    if (i < requirements.length - 1) {
                        sb.append(or ? " OR " : " AND ");
                    }
                }
                if (requirements.length > 0) {
                    sb.append(" ").append(requirements[0].conditionString());
                }
                return sb.toString();
            } else {
                return field + " (" + row[fieldOrder.get(field)] + ")" + (reverse ? " must not " : " must ") + description + (withCondition ? " " + conditionString() : "");
            }
        }

        private String conditionString() {
            if (conditionalFields != null && conditionalFields.length > 0) {
                StringBuilder condition = new StringBuilder("when ");
                for (int i = 0; i < conditionalFields.length; i++) {
                    if (i > 0) {
                        condition.append(" and ");
                    }
                    condition.append(conditionalFields[i]).append(" is '").append(conditionalValues[i]).append("'");
                }
                return condition.toString();
            } else {
                return "";
            }
        }
    }

    public void setBarcodeTumorValidator(final BarcodeTumorValidator barcodeTumorValidator) {
        this.barcodeTumorValidator = barcodeTumorValidator;
    }

    @Override
    protected Boolean doWork(final File file, final QcContext context) throws ProcessorException {
        try {
            return validate(file, context);
        }
        catch (IOException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
            throw new ProcessorException(new StringBuilder().append("Error reading maf file ").append(file.getName()).toString());
        }
    }

    private void initThreadLocals(){
        barcodeList.set(new HashSet<String>());
        uuidList.set(new HashSet<String>());
    }

    private void cleanupThreadLocals(){
        barcodeList.remove();
        uuidList.remove();
    }

    private Set<String> getBarcodeList(){
        return barcodeList.get();
    }
    
    private Set<String> getUUIDList(){
        return uuidList.get();
    }

    /**
     * Validates a maf file.  Will add errors found.
     *
     * @param mafFile the file to validate
     * @param context the qc context
     * @return whether validation passed or not
     * @throws IOException if the file could not be opened/read
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *                     if an error happens
     */
    protected boolean validate(final File mafFile, final QcContext context) throws IOException, ProcessorException {

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            // initialize threadlocal
            initThreadLocals();

            boolean ok = true;
            context.setFile(mafFile);


            // set sampleTypes
            for (SampleType sample:sampleTypeQueries.getAllSampleTypes()){
                sampleCodes.put(sample.getSampleTypeCode(), sample.getIsTumor());
            }

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

            // get order of fields from header
            final Map<String, Integer> fieldOrder = mapFieldOrder(headers);
            // make sure the field order is correct
            for (int i = 0; i < getMafFieldList().size(); i++) {
                if (fieldOrder.get(getMafFieldList().get(i)) == null) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MISSING_REQUIRED_COLUMN_ERROR,
                            getMafFieldList().get(i)));
                    // if a column is missing, just return false now
                    return false;
                } else if (fieldOrder.get(getMafFieldList().get(i)) != i) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MAF_FILE_PROCESSING_ERROR,
                            mafFile.getName(),
                            new StringBuilder().append("Expected column '").append(getMafFieldList().get(i)).append("' to be at index '").
                                    append((i + 1)).append("' but found at '").append((fieldOrder.get(getMafFieldList().get(i)) + 1)).append("'").toString()));
                    // if order is wrong, make error but ok to continue rest of checks
                    ok = false;
                }
            }

            String line;
            int initialErrorCount = context.getErrorCount();
            while ((line = bufferedReader.readLine()) != null) {
                lineNum++;
                if (!StringUtils.isBlank(line.trim()) && !StringUtils.startsWith(line, COMMENT_LINE_TOKEN)) {
                    final String[] row = line.split("\\t");

                    if (row.length >= fieldOrder.size()) {
                        boolean rowOk = validateRow(row, fieldOrder, lineNum, context, mafFile.getName());

                        ok = ok && rowOk;
                    } else {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                                mafFile.getName(),
                                lineNum,
                                new StringBuilder().append("Improper format: expected '" ).append(fieldOrder.size()).append("' fields but found '").
                                        append(row.length).append("'").toString()));
                        ok = false;
                    }
                }
                if (context.getErrorCount() - initialErrorCount > 100) {
                    context.addError("More than 100 errors found, halting MAF validation");
                    break;
                }

            }

            // validate remaining ids (barcodes or UUIDs)
            if(context.isStandaloneValidator()){
                ok = batchValidate(null, context, mafFile.getName(), true, true);
            }

            if (!ok) {
                context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
            }

            return ok;

        } finally {
                cleanupThreadLocals();
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    /*
    * Validates a row of the maf file.  Will add errors if found.
    */

    private boolean validateRow(final String[] row,
                                final Map<String, Integer> fieldOrder,
                                final int rowNum,
                                final QcContext context,
                                final String fileName) throws ProcessorException {
        boolean rowOk = true;
        // iterate through mafFields and check each one against the pattern
        final Map<String, Pattern> requiredMafExpressions = getRequiredMafExpressions(context.isCenterConvertedToUUID());
        final Map<String, String> requiredFieldDescriptions = getRequiredFieldDescriptions(context.isCenterConvertedToUUID());
        for (final String field : requiredMafExpressions.keySet()) {
            final String value = row[fieldOrder.get(field)].trim();
            final Pattern pattern = requiredMafExpressions.get(field);
            if (pattern != null) {
                if (getMultiValueAllowedFields().contains(field)) {
                    // split the value by ;
                    String[] values = value.split(MULTI_VALUE_SEPARATOR);
                    for (final String oneValue : values) {
                        if (!pattern.matcher(oneValue).matches()) {
                        	context.addError(MessageFormat.format(
                            		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                            		fileName, 
                            		rowNum,
                            		new StringBuilder().append("'")
                            		.append(oneValue)
                            		.append("' is an invalid value for '")
                            		.append(field).
                            		append(" - ")
                            		.append(requiredFieldDescriptions.get(field))
                            		.append("'").toString()));
                        }
                    }
                } else {
                    if (!pattern.matcher(value).matches()) {
                        // add an error if the value isn't formatted correctly
                    	context.addError(MessageFormat.format(
                        		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                        		fileName, 
                        		rowNum,
                        		new StringBuilder().append("'")
                        		.append(field)
                        		.append("' value '")
                        		.append(value)
                        		.append("' is invalid - ")
                        		.append(requiredFieldDescriptions.get(field)).toString()));
                        rowOk = false;
                    }
                }
            }
        }
        // only continue to more advanced validation if the basic format of the row passed... otherwise may run into weirdness with blank/badly formatted values
        if (rowOk) {
            for (final ConditionalRequirement condition : getConditions()) {
                if (!condition.isSatisfied(row, fieldOrder)) {
                	context.addError(MessageFormat.format(
                    		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                    		fileName, 
                    		rowNum,
                    		condition.errorString(row, fieldOrder)));
                    rowOk = false;
                }
            }
            // Now check things that can't be represented with generic ConditionalRequirements

            // these are the same for all maf spec versions... for now
            final int start = Integer.parseInt(row[fieldOrder.get(getFieldStartPosition())]);
            final int end = Integer.parseInt(row[fieldOrder.get(getFieldEndPosition())]);
            if (end < start) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                		fileName, 
                		rowNum,
                		new StringBuilder().append(getFieldStartPosition()).append(" should be less than or equal to ").append(getFieldEndPosition()).toString()));
                rowOk = false;
            }
            // check if start and end are valid chromosome coords
            final String chrom = row[fieldOrder.get(getFieldChrom())];
            final String build = row[fieldOrder.get(getFieldNCBIBuild())];
            if ( ! chromInfoUtils.isValidChromCoord(chrom, start, build) ) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                		fileName,
                		rowNum,
                		new StringBuilder().append(getFieldStartPosition()).append(" is not valid for chrom ").append( chrom )));
                rowOk = false;
            }
            if ( ! chromInfoUtils.isValidChromCoord(chrom, end, build) ) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                		fileName,
                		rowNum,
                		new StringBuilder().append(getFieldEndPosition()).append(" is not valid for chrom ").append( chrom )));
                rowOk = false;
            }

            // call method to check things for the maf version
            rowOk = runMafSpecificSpecialChecks(row, fieldOrder, rowNum, context, fileName, start, end) && rowOk;

            final String tumorBarcode = row[fieldOrder.get(FIELD_TUMOR_SAMPLE_BARCODE)];
            final String normalBarcode = row[fieldOrder.get(FIELD_MATCHED_NORM_SAMPLE_BARCODE)];

            // Assert that the Ids (barcodes or UUIDs) in the current row are valid for the archive tumor type and patient
            if(!context.isCenterConvertedToUUID()) {

            	rowOk &= validateTumorTypeForRowIds(barcodeFields, fieldOrder, row, rowNum, context, fileName);
            	if(rowOk) {
            		rowOk &= validateTumorAndNormalIdsForPatient(
            				tumorBarcode,
            				normalBarcode,
            				context,
            				fileName,
            				rowNum);
            	}
            }
            else {
                final String tumorUuid = row[fieldOrder.get(FIELD_TUMOR_SAMPLE_UUID)];
                final String normalUuid = row[fieldOrder.get(FIELD_MATCHED_NORM_SAMPLE_UUID)];

            	rowOk &= validateTumorTypeForRowIds(uuidFields, fieldOrder, row, rowNum, context, fileName);
            	if(rowOk) { 
            		rowOk &= validateTumorAndNormalIdsForPatient(
            				tumorUuid,
            				normalUuid,
            				context,
            				fileName,
            				rowNum);
                    rowOk &= validateAreAliquots(tumorUuid, normalUuid, context, fileName, rowNum);
                    rowOk &= validateUuidBarcodeMapping(context, tumorBarcode, tumorUuid, normalBarcode, normalUuid, fileName, rowNum);
            	}
            }
        }
        
        if (!rowOk) {
            context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
        }
        
        return rowOk;
    }

    private boolean validateUuidBarcodeMapping(final QcContext context, final String tumorBarcode, final String tumorUuid,
                                               final String normalBarcode, final String normalUuid,
                                               final String fileName, final Integer rowNum) {
        boolean valid = true;
        if (!qcLiveBarcodeAndUUIDValidator.validateUUIDBarcodeMapping(tumorUuid, tumorBarcode)) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                    fileName,
                    rowNum, "tumor barcode (" + tumorBarcode + ") doesn't map to given tumor UUID (" + tumorUuid + ")"));
        }
        if (!qcLiveBarcodeAndUUIDValidator.validateUUIDBarcodeMapping(normalUuid, normalBarcode)) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                    fileName,
                    rowNum, "normal barcode (" + normalBarcode + ") doesn't map to given normal UUID (" + normalUuid + ")"));
        }
        return valid;
    }

    protected boolean validateAreAliquots(final String tumorUuid, final String normalUuid,
                                          final QcContext context, final String fileName, final int rowNum) {
        boolean valid = true;
        if (!qcLiveBarcodeAndUUIDValidator.isAliquotUUID(tumorUuid)) {
            valid = false;
            context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum, "tumor sample UUID does not represent an aliquot"));
        }

        if (!qcLiveBarcodeAndUUIDValidator.isAliquotUUID(normalUuid)) {
            valid = false;
            context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum, "normal sample UUID does not represent an aliquot"));
        }
        return valid;
    }

    private boolean validateTumorTypeForRowIds(
    		final List<String> rowidFields, 
    		final Map<String, Integer> fieldOrder, 
    		final String[] row, 
    		final int rowNum, 
    		final QcContext context, 
    		final String fileName) throws ProcessorException {
    	
    	boolean isValid = true;
    	
    	for(final String rowIdField : rowidFields) {
            final String rowIdForField = row[fieldOrder.get(rowIdField)];
            if(context.isStandaloneValidator()) {
            	isValid &= batchValidate(rowIdForField, context, fileName, true, false);
            }
            else  {
            	if(QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(rowIdForField).matches()) {
            		isValid &= validateTumorTypeForBarcode(rowIdForField, rowNum, context, fileName);
            	}
            	else
            		if(QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(rowIdForField).matches()) {
            			isValid &= validateTumorTypeForUUID(rowIdForField, rowNum, context, fileName);
            	}
            }
        }
    	
    	return isValid;
    	
    }
    
    private boolean validateTumorTypeForBarcode(
    		final String barcode, final int rowNum, final QcContext context, final String fileName) throws ProcessorException {
    	
    	boolean isValid = true;
    	
    	isValid &= qcLiveBarcodeAndUUIDValidator.validate(barcode, context, fileName, true);
    	
    	if(barcodeTumorValidator != null) {
            if (!barcodeTumorValidator.barcodeIsValidForTumor(barcode, context.getArchive().getTumorType())) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("Barcode is not part of disease set for ").append(context.getArchive().getTumorType()).toString()));
                isValid = false;
            }
        }
    	
    	return isValid;
    }
    
    private boolean validateTumorTypeForUUID(final String uuid, final int rowNum, final QcContext context, final String fileName) {
    	
    	boolean isValid = qcLiveBarcodeAndUUIDValidator.validateUuid(uuid, context, fileName, true);

        if (!qcLiveBarcodeAndUUIDValidator.isMatchingDiseaseForUUID(uuid, context.getArchive().getTumorType())) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                    fileName,
                    rowNum,
                    new StringBuilder().append("UUID ").append(uuid).append(" is not part of disease set for ").append(context.getArchive().getTumorType()).toString())
            );
            isValid = false;

        }
    	
    	return isValid;
    }
    
    /**
     * Performs batch validation on a list of tumor and normal Ids (barcodes or UUIDs).
     * 
     * @param id - barcode or UUID
     * @param context - QC context
     * @param fileName - MAF file name containing the barcode or UUID
     * @param mustExist - boolean indicating whether or not the id exists in the database
     * @param lastBatch - flag indicating whether or not a batch validation should be performed on the final list of ids
     * @return true if the id is valid, false otherwise
     */
    protected boolean batchValidate(
    		final String id, final QcContext context, final String fileName, final boolean mustExist, boolean lastBatch) {
    	
        boolean isValid = true;
        
        // Add the id (barcode or UUID) to the batch list
        if (id != null) {
        	if(QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(id).matches()) {
        		getBarcodeList().add(id);
        	}
        	else
        		if(QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(id).matches()) {
        		getUUIDList().add(id);
        	}
        }

        if(lastBatch) {
        	final List<String> idList = new ArrayList<String>();

            if(!context.isCenterConvertedToUUID()) {
        		idList.addAll(getBarcodeList());
        	}
        	else {
        		idList.addAll(getUUIDList());
        	}
        	
        	isValid = qcLiveBarcodeAndUUIDValidator.batchValidate(idList, context, fileName, mustExist);

            // there is no batch method for this so do them one at a time,
            // but make sure to only check each UUID once for efficiency
            if (context.isCenterConvertedToUUID()) {
                final Set<String> idSet = new HashSet<String>();
                idSet.addAll(idList);
                for (final String uuid : idSet) {
                    if (!qcLiveBarcodeAndUUIDValidator.isMatchingDiseaseForUUID(uuid, context.getArchive().getTumorType())) {
                        context.addError(
                                new StringBuilder().append(fileName).append(": ").
                                append("UUID ").append(uuid).append(" is not part of disease set for ").
                                        append(context.getArchive().getTumorType()).toString()
                        );
                        isValid = false;
                    }
                }
            }
        	
            getBarcodeList().clear();
            getUUIDList().clear();
        }

        return isValid;
    }
    
    /**
     * Validates that the meta-data (TSS codes, Participant codes, and SampleType codes) match for both 
     * tumor and normal Ids (barcodes or UUIDs).
     * 
     * @param tumorId - tumor barcode or UUID
     * @param normalId - normal barcode or UUID
     * @param context - QcContext
     * @param fileName - MAF file name containing the barcodes or UUIDs
     * @param rowNum - row number that contains the Ids being validated
     * @return true if the meta-data between the tumor and normal Ids match, false otherwise
     */
    protected boolean validateTumorAndNormalIdsForPatient(
    		final String tumorId,
    		final String normalId,
    		final QcContext context,
    		final String fileName,
    		final int rowNum) {
    	
    	boolean isValid = true;
    	MetaDataBean tumorIdMetaData = null;
    	MetaDataBean normalIdMetaData = null;
    	final Map<String, MetaDataBean> idMetaData = getMetaDataForIds(tumorId, normalId);

    	if(idMetaData.isEmpty()) {
    		context.addError(MessageFormat.format(
    				MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
    				fileName,
    				rowNum,
    				new StringBuilder()
    				.append("Both tumor Id '").append(tumorId).append("'")
    				.append(" and normal Id '").append(normalId).append("' ")
    				.append("must match the same Id type (barcode or UUID) pattern")));
    		
    		return false;
    	}
    	
    	tumorIdMetaData = idMetaData.get(tumorId);
    	normalIdMetaData = idMetaData.get(normalId);
	    	
    	// Perform validations
    	if (sampleCodes.get(tumorIdMetaData.getSampleCode()) != null && sampleCodes.get(normalIdMetaData.getSampleCode()) != null) {
    		
	    	if(!sampleCodes.get(tumorIdMetaData.getSampleCode())) {
	    		isValid = false;    		
	    		context.addError(MessageFormat.format(
	                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
	                    fileName,
	                    rowNum,
	                    new StringBuilder().append("The sample type of '")
	                    .append(tumorIdMetaData.getSampleCode())
	                    .append("' for tumor Id '").append(tumorId)
	                    .append("' is not a valid sample type code for tumor.")));
	    	}
	    	
	    	if(sampleCodes.get(normalIdMetaData.getSampleCode())) {
	    		isValid = false;    		
	    		context.addError(MessageFormat.format(
	                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
	                    fileName,
	                    rowNum,
	                    new StringBuilder().append("The sample type of '")
	                    .append(normalIdMetaData.getSampleCode())
	                    .append("' for non tumor Id '").append(normalId)
	                    .append("' is not a valid sample type code for non tumor.")));
	    	}
	    	
	    	if(!tumorIdMetaData.getTssCode().equals(normalIdMetaData.getTssCode())) {
	    		isValid = false;    		
	    		context.addError(MessageFormat.format(
	                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
	                    fileName,
	                    rowNum,
	                    new StringBuilder().append("The TSS code for both the tumor Id '")
	                    .append(tumorId)
	                    .append("' and the normal Id '")
	                    .append(normalId)
	                    .append("' must match.")));
	    	}
	    	
	    	if(!tumorIdMetaData.getParticipantCode().equals(normalIdMetaData.getParticipantCode())) {
	    		isValid = false;    		
	    		context.addError(MessageFormat.format(
	                    MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
	                    fileName,
	                    rowNum,
	                    new StringBuilder().append("The Participant Code for both the tumor Id '")
	                    .append(tumorId)
	                    .append("' and the normal Id '")
	                    .append(normalId)
	                    .append("' must match.")));
	    	}
    	}
    	else {
    	    if(!context.isNoRemote()) {
        		isValid = false;
        		context.addError(MessageFormat.format(
                        MessagePropertyType.MAF_FILE_VALIDATION_ERROR,
                        fileName,
                        rowNum,
                        new StringBuilder().append("Unknown sample type code encountered, check your Ids and try again.")));
    	    }
    	}
    	
    	return isValid;
    }
    
    private Map<String, MetaDataBean> getMetaDataForIds(final String tumorId, final String normalId) {
    	
    	final Map<String, MetaDataBean> idMetaData = new HashMap<String, MetaDataBean>();
    	MetaDataBean tumorIdMetaDataBean = null;
    	MetaDataBean normalIdMetaDataBean = null;
    	
    	@SuppressWarnings("serial")
		final List<String> idList = new ArrayList<String>() {{
    		add(tumorId); 
    		add(normalId);
		}};
    		
    	if(getNumMatchesForIdList(idList, QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN) == idList.size()) {
    		
    		final Matcher tumorBarcodeMatcher = (QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(tumorId));
    		tumorBarcodeMatcher.find();
        	final Matcher normalBarcodeMatcher = (QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(normalId));
        	normalBarcodeMatcher.find();
        	
        	// Set the tumor meta-data for tumor barcode
        	tumorIdMetaDataBean = new MetaDataBean();
        	tumorIdMetaDataBean.setTssCode(tumorBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.TSS_GROUP));
        	tumorIdMetaDataBean.setParticipantCode(tumorBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.PATIENT_GROUP));
        	tumorIdMetaDataBean.setSampleCode(tumorBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP));
        	idMetaData.put(tumorId, tumorIdMetaDataBean);
        	
        	// Set the normal meta-data for normal barcode
        	normalIdMetaDataBean = new MetaDataBean();
        	normalIdMetaDataBean.setTssCode(normalBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.TSS_GROUP));
        	normalIdMetaDataBean.setParticipantCode(normalBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.PATIENT_GROUP));
        	normalIdMetaDataBean.setSampleCode(normalBarcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP));
        	idMetaData.put(normalId, normalIdMetaDataBean);
    	}
    	else
    		if(getNumMatchesForIdList(idList, QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN) == idList.size()) {
    			tumorIdMetaDataBean = shippedBiospecimenQueries.retrieveUUIDMetadata(tumorId);
        		normalIdMetaDataBean = shippedBiospecimenQueries.retrieveUUIDMetadata(normalId);
        		
        		if(tumorIdMetaDataBean != null && normalIdMetaDataBean != null) {
        			idMetaData.put(tumorId, tumorIdMetaDataBean);
        			idMetaData.put(normalId, normalIdMetaDataBean);
        		}
    		}
    		
    	return idMetaData;
    }
    
    private int getNumMatchesForIdList(final List<String> idList, final Pattern pattern) {
    	int numMatches = 0;
    	for(final String id : idList) {
    		if(pattern.matcher(id).matches()) {
    			numMatches += 1;
    		}
    	}
    	
    	return numMatches;
    }
    
    protected boolean runMafSpecificSpecialChecks(final String[] row, final Map<String, Integer> fieldOrder, final int rowNum,
                                                  final QcContext context, final String fileName, final int start, final int end) {

        //If variant_Type is "ins", then end_position - start_position should always = 1
        if (row[fieldOrder.get(FIELD_VARIANT_TYPE)].equals(getVariantInsSymbol())) {
            if (end - start != 1) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                		fileName, 
                		rowNum,
                		new StringBuilder().append(getFieldEndPosition()).append(" should only be 1 greater than ").append(getFieldStartPosition()).
                		append(" when ").append(FIELD_VARIANT_TYPE).append(" is '").append(getVariantInsSymbol()).append("'").toString()));
                return false;
            }
        } else {
            //If variant_Type is not "ins"  then end_position - start_position +1
            // should equal to the string length of Reference_Allele and one of Tumor_Seq_Allele.
            final int length = end - start + 1;
            if (row[fieldOrder.get(FIELD_REFERENCE_ALLELE)].length() != length) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                		fileName, 
                		rowNum,
                		new StringBuilder().append(FIELD_REFERENCE_ALLELE).append(" length should be equal to ").append(getFieldEndPosition()).
                		append(" - ").append(getFieldStartPosition()).append(" + 1 (").append(length).append(") when ").append(FIELD_VARIANT_TYPE).
                		append(" is not '").append(getVariantInsSymbol()).append("'").toString()));
                return false;
            }
            if (row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE1)].length() != length &&
                    row[fieldOrder.get(FIELD_TUMOR_SEQ_ALLELE2)].length() != length) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.MAF_FILE_VALIDATION_ERROR, 
                		fileName, 
                		rowNum,
                		new StringBuilder().append("Either ").append(FIELD_TUMOR_SEQ_ALLELE1).append(" or ").append(FIELD_TUMOR_SEQ_ALLELE2).
                		append(" length must be equal to ").append(getFieldEndPosition()).append(" - ").append(getFieldStartPosition()).
                		append(" + 1  (").append(length).append(") when ").append(FIELD_VARIANT_TYPE).append(" is not '").append(getVariantInsSymbol()).
                		append("'").toString()));
                return false;
            }
        }
        return true;
    }

    public ChromInfoUtils getChromInfoUtils() {
        return chromInfoUtils;
    }

    public void setChromInfoUtils(final ChromInfoUtils chromInfoUtils) {
        this.chromInfoUtils = chromInfoUtils;
    }
    
    protected String getVariantInsSymbol() {
        return VARIANT_TYPE_INS;
    }

    protected String getFieldEndPosition() {
        return FIELD_END_POSITION;
    }

    protected String getFieldStartPosition() {
        return FIELD_START_POSITION;
    }

    protected String getFieldChrom() {
        return FIELD_CHROMOSOME;
    }

    protected String getFieldNCBIBuild() {
        return FIELD_NCBI_BUILD;
    }

    public String getName() {
        return "MAF file validation";
    }
    
    public void setSampleTypeQueries(final SampleTypeQueries sampleTypeQueries) {
		this.sampleTypeQueries = sampleTypeQueries;
	}
    
    public void setShippedBiospecimenQueries(final ShippedBiospecimenQueries shippedBiospecimenQueries) {
		this.shippedBiospecimenQueries = shippedBiospecimenQueries;
	}
}
