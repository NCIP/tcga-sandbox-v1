/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

/**
 * Contains DOM tags to use in UI to enable selenium testing.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface SeleniumTags {
    static final String GENEMODE_BUTTON = "GeneModeBtn";
    static final String PATIENTMODE_BUTTON = "PatientModeBtn";
    static final String PATHWAYMODE_BUTTON = "PathwayModeBtn";
    static final String SEARCH_BUTTON = "SearchBtn";
    static final String ADDCN_BUTTON = "AddCnBtn";
    static final String ADDEXP_BUTTON = "AddExpBtn";
    static final String ADDMIRNA_BUTTON = "AddmiRNABtn";
    static final String ADDMETHYLATION_BUTTON = "AddMethylationBtn";
    static final String ADDMUT_BUTTON = "AddMutBtn";
    static final String ADDCORR_BUTTON = "AddCorrBtn";
    static final String GENELIST_TEXT = "GeneListText";
    static final String RATIOTHRESHOLD_TEXT = "RatioThresholdText";
    static final String CLEARGENELIST_BUTTON = "ClearGeneListBtn";

    static final String COPYTOFILTER_BUTTON = "CopyToFilterBtn";
    static final String SELECTALLGENES_CHECKBOX = "SelectAllGenesCb";
    static final String SELECTGENE_CHECKBOX_PREFIX = "SelectGeneCb_";
    static final String VIEWASPERCENT_CHECKBOX = "ViewAsPercentCb";
    static final String VIEWASRATIO_CHECKBOX = "ViewAsRatioCb";

    static final String NEXTPAGE_BUTTON = "NextPageBtn";
    static final String PREVPAGE_BUTTON = "PrevPageBtn";
    static final String FIRSTPAGE_BUTTON = "FirstPageBtn";
    static final String LASTPAGE_BUTTON = "LastPageBtn";
}
