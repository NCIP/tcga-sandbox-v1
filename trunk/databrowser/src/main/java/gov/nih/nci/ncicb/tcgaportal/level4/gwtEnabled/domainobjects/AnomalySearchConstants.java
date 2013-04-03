package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;

/**
 * Constants used in the client, especially for result annotations.
 *
 * @author David Nassau
 *         Last updated by: $Author: nassaud $
 * @version $Rev: 5951 $
 */

public interface AnomalySearchConstants {
    //row-level annotations
    String ROWANNOTATIONKEY_CNV = "cnv";
    String ROWANNOTATIONKEY_REGION_CHROM = "chr";
    String ROWANNOTATIONKEY_REGION_START = "start";
    String ROWANNOTATIONKEY_REGION_STOP = "stop";
    String ROWANNOTATIONKEY_BCGENE = "bcg";
    String ROWANNOTATIONKEY_URL = "url";
    String ROWANNOTATIONKEY_METHYLATION_PROBE = "methyl";
    String ROWANNOTATIONKEY_MIRNA = "mirna";
    
    //used for pathway gene list, so we can filter out those that matched the search criteria
    String ROWANNOTATIONKEY_MATCHED_SEARCH = "matched_search";

    //pathways
    String ROWANNOTATIONKEY_PATHWAYID = "pid";
    String ROWANNOTATIONKEY_PATHWAY_FISHER = "pathway_fisher";

    //field-level, or value, annotations
    String VALUEANNOTATIONKEY_PAIRED = "paired";
    String VALUEANNOTATIONKEY_CORRELATION_PVALUE = "crlp";

    //bitmask flags to set in Results.addDisplayFlag
    int RESULTSDISPLAYFLAG_RATIO = Integer.parseInt("00000001", 2); //this is how we hardcode binary in java
    int RESULTSDISPLAYFLAG_PERCENT = Integer.parseInt("00000010", 2);
    int RESULTSDISPLAYFLAG_AVERAGE = Integer.parseInt("00000100", 2);
    int RESULTSDISPLAYFLAG_MAX = Integer.parseInt("00001000", 2);

    //tooltip keys
    String TOOLTIPKEY_RESULTS_GENESYMBOL = "results_geneSymbol";
    String TOOLTIPKEY_RESULTS_BIOCARTA = "results_biocartaGene";
    String TOOLTIPKEY_RESULTS_MIRNASYMBOL = "results_mirnaSymbol";
    String TOOLTIPKEY_RESULTS_METHYLATIONSYMBOL = "results_methylSymbol";
    String TOOLTIPKEY_RESULTS_CNV = "results_cnv";
    String TOOLTIPKEY_RESULTS_LOCATION = "results_location";
    String TOOLTIPKEY_RESULTS_CNGENE = "results_CopyNumber_Gene";
    String TOOLTIPKEY_RESULTS_CNMIRNA = "results_CopyNumber_miRNA";
    String TOOLTIPKEY_RESULTS_EXPGENE = "results_Expression_Gene";
    String TOOLTIPKEY_RESULTS_EXPMIRNA = "results_Expression_miRNA";
    String TOOLTIPKEY_RESULTS_MUTATION = "results_Mutation";
    String TOOLTIPKEY_RESULTS_CORRELATION = "results_Correlation";
    String TOOLTIPKEY_RESULTS_METHYLATION = "results_Methylation";
    String TOOLTIPKEY_RESULTS_PATIENTID = "results_PatientID";
    String TOOLTIPKEY_RESULTS_PATHWAYSIGNIF = "results_PathwaySignificance";
    String TOOLTIPKEY_RESULTS_DOWNLOADDATAFILES = "downloadDataFiles";
    String TOOLTIPKEY_RESULTS_EXPORTDATA = "exportData";
    String TOOLTIPKEY_RESULTS_PATIENTCOPYCHECKEDTOSEARCH = "patientCopyCheckedToSearch";
    String TOOLTIPKEY_RESULTS_GENECOPYCHECKEDTOSEARCH = "geneCopyCheckedToSearch";
    String TOOLTIPKEY_RESULTS_PATHWAYNAME = "results_PathwayName";
    String TOOLTIPKEY_RESULTS_PIVOTFROMGENE = "results_pivotFromGene";
    String TOOLTIPKEY_RESULTS_PIVOTFROMPATIENT = "results_pivotFromPatient";

    String TOOLTIPKEY_FILTER_GENECN = "geneCopyNumber";
    String TOOLTIPKEY_FILTER_GENECNMIRNA = "geneCopyNumberMirna";
    String TOOLTIPKEY_FILTER_GENEEXP = "geneExpression";
    String TOOLTIPKEY_FILTER_GENEEXPMIRNA = "geneExpressionMirna";
    String TOOLTIPKEY_FILTER_GENEMETH = "geneMethylation";
    String TOOLTIPKEY_FILTER_GENEMUT = "geneMutations";
    String TOOLTIPKEY_FILTER_GENECORRELATIONS = "geneCorrelations";
    String TOOLTIPKEY_FILTER_PATIENTCN = "patientCopyNumber";
    String TOOLTIPKEY_FILTER_PATIENTCNMIRNA = "patientCopyNumberMirna";
    String TOOLTIPKEY_FILTER_PATIENTEXP = "patientExpression";
    String TOOLTIPKEY_FILTER_PATIENTEXPMIRNA = "patientExpressionMirna";
    String TOOLTIPKEY_FILTER_PATIENTMETH = "patientMethylation";
    String TOOLTIPKEY_FILTER_PATIENTMUT = "patientMutations";
    String TOOLTIPKEY_FILTER_PATHWAYCN = "pathwayCopyNumber";
    String TOOLTIPKEY_PATHWAYCNMIRNA = "pathwayCopyNumberMirna";
    String TOOLTIPKEY_FILTER_PATHWAYEXP = "pathwayExpression";
    String TOOLTIPKEY_FILTER_PATHWAYEXPMIRNA = "pathwayExpressionMirna";
    String TOOLTIPKEY_FILTER_PATHWAYMETH = "pathwayMethylation";
    String TOOLTIPKEY_FILTER_PATHWAYMUT = "pathwayMutations";
    String TOOLTIPKEY_FILTER_PATHWAYCORRELATIONS = "pathwayCorrelations";
    String TOOLTIPKEY_FILTER_DISEASE = "disease";
    String TOOLTIPKEY_FILTER_GENES = "genes";
    String TOOLTIPKEY_FILTER_PATIENTS = "patients";
    String TOOLTIPKEY_FILTER_ADDBUTTON = "addButton";

    String HEADER_GENE = "Gene";
    String HEADER_BIOCARTA_ID = "Biocarta ID";
    String HEADER_MI_RNA = AnomalyType.GeneticElementType.miRNA.toString();
    String HEADER_METHYLATION_REGION = AnomalyType.GeneticElementType.MethylationProbe.toString();
    String HEADER_CNV = "CNV";
    String HEADER_CHROMOSOME = "Chromosome";
    String HEADER_START = "Start";
    String HEADER_STOP = "Stop";
    String HEADER_TOTAL = "Total - ";
    String HEADER_AFFECTED = "Affected - ";
    String HEADER_RATIO = "Ratio - ";
    String HEADER_P_VALUE = "P-value";
    String HEADER_PATHWAY = "Pathway";
    String HEADER_PATIENT = "Participant";

}
