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
        //readExcel();
        readTextFile();
        if(fileBAMs.size() > 0) {
            loadData();
        }
        logger.info("DONE.");
    }

    protected boolean readTextFile() {
        boolean validTextFile = true;
        logger.info("Reading File "+BAMLoaderConstants.BAMFilePath+" ....");

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(BAMLoaderConstants.BAMFilePath));
            String line;
            int rowCount = 0;
            while((line = bufferedReader.readLine()) != null ){
                rowCount++;
                // ignore the first line
                if(line.startsWith("barcode")){
                    continue;
                }

                try{
                    String[] rowData = line.split("\t");
                    if(rowData.length != 7){
                        throw new Exception("Contains data for  "+ rowData.length+" columns");
                    }
                    BAMFile bf = new BAMFile();
                    bf.setDateReceived(format.parse(rowData[5]));
                    bf.setFileNameBAM(rowData[1]);
                    bf.setCenterId(Integer.parseInt(rowData[3]));
                    bf.setDiseaseId(Integer.parseInt(rowData[2]));
                    bf.setDatatypeBAMId(Integer.parseInt(rowData[6]));
                    bf.setBiospecimenId(getAliquotId(rowData[0]));
                    try {
                        bf.setFileSizeBAM(Long.parseLong(rowData[4]));
                    } catch (NumberFormatException nfe) {
                        logger.info(nfe.toString());
                        bf.setFileSizeBAM(0L);
                    }
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

    protected void readExcel() {
        logger.info("Reading Excel File ....");
        workbookSettings.setSuppressWarnings(true);
        try {
            Workbook workbook = Workbook.getWorkbook(new File(BAMLoaderConstants.BAMFilePath), workbookSettings);
            Sheet sheet = workbook.getSheet(0);
            final int size = sheet.getRows();
            for (int i = 1; i < size; i++) {
                BAMFile bf = new BAMFile();
                Cell[] cellTab = sheet.getRow(i);
                bf.setDateReceived(format.parse(cellTab[1].getContents()));
                bf.setFileNameBAM(cellTab[2].getContents());
                bf.setCenterId(getCenterId(cellTab[0].getContents(), cellTab[7].getContents()));
                bf.setDiseaseId(getDiseaseId(cellTab[6].getContents()));
                bf.setDatatypeBAMId(getDatatypeBAMId(cellTab[8].getContents()));
                bf.setBiospecimenId(getAliquotId((cellTab[3].getContents())));
                try {
                    bf.setFileSizeBAM(Long.parseLong(cellTab[9].getContents()));
                } catch (NumberFormatException nfe) {
                    logger.info(nfe.toString());
                    bf.setFileSizeBAM(0L);
                }
                fileBAMs.add(bf);
                logger.info(bf.toString());
            }
            logger.info("Total number of Rows: "+fileBAMs.size());

        } catch (IOException ioe) {
            logger.info(ioe.toString());
        } catch (BiffException be) {
            logger.info(be.toString());
        } catch (ParseException pe) {
            logger.info(pe.toString());
        }
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
            for (final BAMFile bf : fileBAMs) {
                bf.setFileBAMId(fileBAMSequence.nextLongValue());
                parametersBAM.add(new Object[]{bf.getFileBAMId(), bf.getFileNameBAM(),
                        bf.getDiseaseId(), bf.getCenterId(), bf.getFileSizeBAM(),
                        bf.getDateReceived(), bf.getDatatypeBAMId()});
                if (bf.getBiospecimenId() != 0) {
                    parameterBAMToAliquots.add(new Object[]{bf.getBiospecimenId(), bf.getFileBAMId()});
                }
                if(parametersBAM.size() >= BAMLoaderConstants.BATCH_SIZE){
                    jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_INSERT, parametersBAM);
                    jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_TO_ALIQUOT_INSERT, parameterBAMToAliquots);
                    logger.info("Inserted "+parametersBAM.size()+" records ...");
                    parameterBAMToAliquots.clear();
                    parametersBAM.clear();

                }
            }
            jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_INSERT, parametersBAM);
            jdbcTemplate.batchUpdate(BAMLoaderConstants.BAM_TO_ALIQUOT_INSERT, parameterBAMToAliquots);
            logger.info("Inserted "+parametersBAM.size()+" records ...");
            logger.info(" Total records inserted : "+fileBAMs.size());
        } catch (DataIntegrityViolationException e) {
            logger.info(e);
        }
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
