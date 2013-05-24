/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides single interface for utility methods for the DAM queries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMUtils implements DAMUtilsI {

    public static final String DATA_TYPE_ID = "data_type_id";
    public static final String FTP_DISPLAY = "ftp_display";
    public static final String NAME = "name";
    public static final String CNA = "CNV (CN Array)";
    public static final String SNP = "snp";
    public static final String TRANSCRIPTOME = "transcriptome";
    public static final String EXON = "exon";
    public static final String MIRNA = "mirna";
    public static final String METHYLATION = "methylation";
    public static final String MIRNASEQ = "miRNASeq";
    public static final String MIRNASEQ_QUANTIFICATION = "Quantification-miRNA";
    public static final String MIRNASEQ_ISOFORM = "Quantification-miRNA Isoform";
    public static final String RNA_SEQ = "rnaseq";
    public static final String RNA_SEQ_EXON = "Quantification-Exon";
    public static final String RNA_SEQ_GENE = "Quantification-Gene";
    public static final String RNA_SEQ_JUNCTION= "Quantification-Junction";
    public static final String RNA_SEQ_V2 = "RNASeqV2";
    public static final String EXPGENE = "ExpGene";
    public static final String CNA_SNP = "CNA_SNP";
    public static final String MIRNA_SEQ_TYPE = "miRNASeq";
    public static final String RNA_SEQ_TYPE = "RNASeq";
    public static final String PROTEIN_EXP = "Expression-Protein";
    public static final String LOW_PASS = "CNV (Low Pass DNASeq)";

    protected final Log logger = LogFactory.getLog(getClass());
    private static DAMUtils instance;
    private DAMDiseaseQueries damDiseaseQueries;

    private TumorQueries tumorQueries;
    private PlatformQueries platformQueries;
    private CenterQueries centerQueries;
    private DataTypeQueries dataTypeQueries;
    private Map<String,String> dataTypeIdByName;


    public static DAMUtils getInstance() {
        synchronized (DAMUtils.class) {
            if (instance == null) {
                instance = new DAMUtils();
            }
            return instance;
        }
    }

    private DAMUtils() {
        dataTypeIdByName = new HashMap<String,String>();
        // add data type names. data type names are defined in data_type table.
        dataTypeIdByName.put(CNA,null);
        dataTypeIdByName.put(SNP,null);
        dataTypeIdByName.put(LOW_PASS, null);
        dataTypeIdByName.put(TRANSCRIPTOME,null);
        dataTypeIdByName.put(EXON,null);
        dataTypeIdByName.put(MIRNA,null);
        dataTypeIdByName.put(METHYLATION,null);
        dataTypeIdByName.put(MIRNASEQ_QUANTIFICATION,null);
        dataTypeIdByName.put(MIRNASEQ_ISOFORM,null);
        dataTypeIdByName.put(RNA_SEQ_EXON,null);
        dataTypeIdByName.put(RNA_SEQ_GENE,null);
        dataTypeIdByName.put(RNA_SEQ_JUNCTION,null);
        dataTypeIdByName.put(MIRNASEQ, null);
        dataTypeIdByName.put(RNA_SEQ, null);
        dataTypeIdByName.put(RNA_SEQ_V2, null);
        dataTypeIdByName.put(PROTEIN_EXP, null);

    }

    @PostConstruct
    public void setDataTypeIds() {
        final List<Map<String, Object>> dataTypes = (List<Map<String, Object>>) getAllDataTypes();
        for (int i = 0; i < dataTypes.size(); i++) {
            Map<String, Object> dataTypeMap = dataTypes.get(i);
            // It was designed in a way that the data type can be matched with either the ftp_display or name. It is weird.
            // I don't know why it was designed in this way
            String dataTypeName = "";
            if(dataTypeIdByName.keySet().contains(dataTypeMap.get(FTP_DISPLAY))){
                dataTypeName = dataTypeMap.get(FTP_DISPLAY).toString();
            }else if (dataTypeIdByName.keySet().contains(dataTypeMap.get(NAME))){
                dataTypeName = dataTypeMap.get(NAME).toString();
            }
            dataTypeIdByName.put(dataTypeName, dataTypeMap.get(DATA_TYPE_ID).toString());
        }
    }

    /**
     * get list of allowed datatype ids from the database according to dataTypeGroup:
     * CNA_SNP, ExpGene, Methylation, miRNASeq, or Expression-Protein
     *
     * @param dataTypeGroup
     * @return list of allowed datatype ids
     */
    public List<String> getLevel3AllowedDataTypes(String dataTypeGroup) {
        List<String> allowedDataTypesId = new ArrayList<String>();
        if (CNA_SNP.equals(dataTypeGroup)) {
            allowedDataTypesId.add(dataTypeIdByName.get(CNA));
            allowedDataTypesId.add(dataTypeIdByName.get(SNP));
            allowedDataTypesId.add(dataTypeIdByName.get(LOW_PASS));
        } else if (EXPGENE.equals(dataTypeGroup)) {
            allowedDataTypesId.add(dataTypeIdByName.get(TRANSCRIPTOME));
            allowedDataTypesId.add(dataTypeIdByName.get(EXON));
            allowedDataTypesId.add(dataTypeIdByName.get(MIRNA));
        } else if (METHYLATION.equals(dataTypeGroup)) {
            allowedDataTypesId.add(dataTypeIdByName.get(METHYLATION));
        } else if (MIRNA_SEQ_TYPE.equals(dataTypeGroup)) {
            allowedDataTypesId.add(dataTypeIdByName.get(MIRNASEQ_QUANTIFICATION));
            allowedDataTypesId.add(dataTypeIdByName.get(MIRNASEQ_ISOFORM));
             allowedDataTypesId.add(dataTypeIdByName.get(MIRNASEQ));
        } else if (RNA_SEQ_TYPE.equals(dataTypeGroup)) {
            allowedDataTypesId.add(dataTypeIdByName.get(RNA_SEQ_EXON));
            allowedDataTypesId.add(dataTypeIdByName.get(RNA_SEQ_GENE));
            allowedDataTypesId.add(dataTypeIdByName.get(RNA_SEQ_JUNCTION));
            allowedDataTypesId.add(dataTypeIdByName.get(RNA_SEQ));
            allowedDataTypesId.add(dataTypeIdByName.get(RNA_SEQ_V2));
         } else if (PROTEIN_EXP.equals(dataTypeGroup)) {
             allowedDataTypesId.add(dataTypeIdByName.get(PROTEIN_EXP));
        }else{
            return null;
        }
        return allowedDataTypesId;
    }



    /**
     * @return a List of all diseases, as Disease beans
     */
    public List<Disease> getDiseases() {
        return damDiseaseQueries.getDiseases();
    }

    public String getDataTypeId(final String dataTypeName){
        return dataTypeIdByName.get(dataTypeName);
    }


    public void setDataTypeId(final String dataTypeName, final String dataTypeId){
        dataTypeIdByName.put(dataTypeName,dataTypeId);
    }
    /**
     * @param abbrev the disease abbreviation
     * @return the Disease bean for the abbreviation, or null if not found
     */
    public Disease getDisease(final String abbrev) {
        return damDiseaseQueries.getDisease(abbrev);
    }

    public List<Disease> getActiveDiseases() {
        return damDiseaseQueries.getActiveDiseases();
    }

    public Collection<Map<String, Object>> getAllTumors() {
        return tumorQueries.getAllTumors();
    }

    public Collection<Map<String, Object>> getAllCenters() {
        return centerQueries.getAllCenters();
    }

    public Collection<Map<String, Object>> getAllPlatforms() {
        return platformQueries.getAllPlatforms();
    }

    public Platform getPlatformWithAlias(final String platformAlias) {
        return platformQueries.getPlatformWithAlias(platformAlias);
    }

    /**
     * Return the <code>Platform</code> with the given Id
     *
     * @param platformId the <code>Platform</code> Id
     * @return the <code>Platform</code> with the given Id
     */
    public Platform getPlatformById(final Integer platformId) {
        return platformQueries.getPlatformById(platformId);
    }

    public Collection<Map<String, Object>> getAllDataTypes() {
        return dataTypeQueries.getAllDataTypes();
    }

    @Override
    public Map<String, List<DataSet>> groupDataSetsByDisease(final List<DataSet> dataSets) {
        final Map<String, List<DataSet>> datasetsGroupedByDisease = new HashMap<String, List<DataSet>>();
        if(dataSets == null) {
            return datasetsGroupedByDisease; // return the empty map
        }
        for(final DataSet dataset : dataSets) {
            List<DataSet> datasetsForDisease = datasetsGroupedByDisease.get(dataset.getDiseaseType());
            if(datasetsForDisease == null) {
                datasetsForDisease = new ArrayList<DataSet>();
            }
            datasetsForDisease.add(dataset);
            datasetsGroupedByDisease.put(dataset.getDiseaseType(), datasetsForDisease);
        }
        return datasetsGroupedByDisease;
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setDataTypeQueries(final DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    public void setDamDiseaseQueries(final DAMDiseaseQueries damDiseaseQueries) {
        this.damDiseaseQueries = damDiseaseQueries;
    }

}
