/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Bam loader class containing the ingestion code
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class LoaderBAM {

    protected final Log logger = LogFactory.getLog(getClass());
    private SimpleJdbcTemplate jdbcTemplate;
    private WorkbookSettings workbookSettings = new WorkbookSettings();
    private SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
    protected List<BAMFile> fileBAMs = new LinkedList<BAMFile>();
    private BAMDatatype unknown = new BAMDatatype();


    @Autowired
    private LookupForBAM lookupForBAM;

    @Autowired
    private DataFieldMaxValueIncrementer fileBAMSequence;
    
    @Resource(name = "fileBAMDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    public void start() {
        logger.info("START LOADING ....");
        readTextFile();
        if(fileBAMs.size() > 0) {
            loadData();
        }
        logger.info("DONE.");
    }

    protected boolean readTextFile() {
        boolean validTextFile = true;
        String bamFile = (BAMLoaderConstants.isExtendedBAMFile())? BAMLoaderConstants.extendedBAMFilePath:BAMLoaderConstants.BAMFilePath;
        logger.info("Reading File "+BAMLoaderConstants.BAMFilePath+" ....");

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(bamFile));
            String line;
            int rowCount = 0;
            final Date dccReceivedDate = new Date();
            while((line = bufferedReader.readLine()) != null ){
                rowCount++;
                // ignore the first line
                if(line.contains("barcode")){
                    continue;
                }

                try{
                    String[] rowData = line.split("\t");
                    final BAMFile bf = getBamFileBean(rowData);
                    bf.setDccDateReceived(dccReceivedDate);
                    fileBAMs.add(bf);
                }catch(Exception e){
                    logger.error(" Row "+rowCount+" contains invalid data "+ e.getMessage(),e);
                    validTextFile = false;
                }
            }
            logger.info("Total number of Rows: "+fileBAMs.size());

        } catch (IOException ioe) {
            logger.error(ioe.toString());
            validTextFile = false;
        }
        return validTextFile;
    }

    private  BAMFile getBamFileBean(final String[] rowData) throws Exception{
        int maxColumns = (BAMLoaderConstants.isExtendedBAMFile()) ?10 :7;

        if(rowData.length != maxColumns){
            throw new Exception("Contains data for  "+ rowData.length+" columns");
        }
        BAMFile bf = new BAMFile();
        int index =  (BAMLoaderConstants.isExtendedBAMFile()) ?1 :0;

        bf.setBiospecimenId(getAliquotId(rowData[index++]));
        bf.setFileNameBAM(rowData[index++]);
        bf.setDiseaseId(Integer.parseInt(rowData[index++]));
        bf.setCenterId(Integer.parseInt(rowData[index++]));
        try {
            bf.setFileSizeBAM(Long.parseLong(rowData[index++]));
        } catch (NumberFormatException nfe) {
            logger.info(nfe.toString());
            bf.setFileSizeBAM(0L);
        }

        bf.setDateReceived(format.parse(rowData[index++]));
        bf.setDatatypeBAMId(Integer.parseInt(rowData[index++]));

        if(BAMLoaderConstants.isExtendedBAMFile()) {
            bf.setAnalysisId(rowData[0]);
            bf.setAnalyteCode(rowData[index++]);
            bf.setLibraryStrategy(rowData[index]);
        }

        return bf;
    }


    @Transactional
    protected void loadData() {
        try {
            logger.info("cleaning data in DB ...");
            jdbcTemplate.update(BAMLoaderConstants.BAM_TO_ALIQUOT_DELETE);
            jdbcTemplate.update(BAMLoaderConstants.BAM_DELETE);
            logger.info("Inserting data in DB ....");
            final List<Object[]> parametersBAM = new ArrayList<Object[]>();
            final List<Object[]> parameterBAMToAliquots = new ArrayList<Object[]>();

            final String insertSql =  (BAMLoaderConstants.isExtendedBAMFile()) ?  BAMLoaderConstants.EXTENDED_BAM_INSERT:BAMLoaderConstants.BAM_INSERT;
            for (final BAMFile bf : fileBAMs) {
                bf.setFileBAMId(fileBAMSequence.nextLongValue());
                parametersBAM.add(getBAMFileObjectArray(bf));
                if (bf.getBiospecimenId() != 0) {
                    parameterBAMToAliquots.add(new Object[]{bf.getBiospecimenId(), bf.getFileBAMId()});
                }
                if(parametersBAM.size() >= BAMLoaderConstants.BATCH_SIZE){
                    jdbcTemplate.batchUpdate(insertSql, parametersBAM);
                    jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_TO_ALIQUOT_INSERT, parameterBAMToAliquots);
                    logger.info("Inserted "+parametersBAM.size()+" records ...");
                    parameterBAMToAliquots.clear();
                    parametersBAM.clear();

                }
            }
            jdbcTemplate.batchUpdate(insertSql, parametersBAM);
            jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_TO_ALIQUOT_INSERT, parameterBAMToAliquots);
            logger.info("Inserted "+parametersBAM.size()+" records ...");
            logger.info(" Total records inserted : "+fileBAMs.size());
        } catch (DataIntegrityViolationException e) {
            logger.info(e);
        }
    }

    private Object[] getBAMFileObjectArray(final BAMFile bf) {
        final List<Object> bamData = new ArrayList<Object>();

        if((BAMLoaderConstants.isExtendedBAMFile())) {
            bamData.add(bf.getAnalysisId());
        }
        bamData.add(bf.getFileBAMId());
        bamData.add(bf.getFileNameBAM());
        bamData.add(bf.getDiseaseId());
        bamData.add(bf.getCenterId());
        bamData.add(bf.getFileSizeBAM());
        bamData.add(bf.getDateReceived());
        bamData.add(bf.getDatatypeBAMId());

        if((BAMLoaderConstants.isExtendedBAMFile())) {
            bamData.add(bf.getAnalyteCode());
            bamData.add(bf.getDccDateReceived());
        }

        return bamData.toArray();
    }

    private Integer getCenterId(String shortName,String centerType){
        for (CenterShort c: lookupForBAM.getCenters()){
            if (c.getShortName().equals(shortName) && c.getCenterType().equals(centerType)){
                return c.getCenterId();
            }
        }
        return 0;
    }

    private Integer getDiseaseId(String diseaseName){
        for (Tumor t : lookupForBAM.getDiseases()) {
            if (t.getTumorName().equals(diseaseName)) {
                return t.getTumorId();
            }
        }
        return 0;
    }

    private Integer getDatatypeBAMId(String datatypeBAM){
        for (BAMDatatype dt : lookupForBAM.getDatatypeBAMs()) {
            if (dt.getDatatypeBAM().equals(datatypeBAM)) {
                return dt.getDatatypeBAMId();
            }
            if ("Unknown".equals(dt.getDatatypeBAM())){
                unknown = dt;
            }
        }
        return unknown.getDatatypeBAMId();
    }

    private Long getAliquotId(String barcode){
        for (AliquotShort a : lookupForBAM.getAliquots()) {
            if (a.getBarcode().equals(barcode)) {
                return a.getAliquotId();
            }
        }
        logger.warn("Barcode  "+barcode+ " is not available in DCC database ");
        return 0L;
    }

    public void setLookupForBAM(LookupForBAM lookupForBAM) {
        this.lookupForBAM = lookupForBAM;
    }

    public void setFileBAMSequence(DataFieldMaxValueIncrementer fileBAMSequence) {
        this.fileBAMSequence = fileBAMSequence;
    }

    public SimpleJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
}//End of Class
