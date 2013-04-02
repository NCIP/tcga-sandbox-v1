/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ClinicalMetaQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TissueSourceSiteQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.util.DataDictionaryGenerator;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.util.DataFileGenerator;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates dbGap submission files for TCGA clinical data.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DbGapSubmissionGenerator {
    public static final String FIELD_SEPARATOR = "\t";
    public static final String NEWLINE = "\n";

    private static final String MANIFEST_HEADER = "Submitted File Name\tFile Type\tFile Description\tFile Size (in kb)\tComments";
    private static final String MANIFEST_FILENAME = "tcga_manifest.txt";
    private static final String UNDERSCORE = "_";
    private static final String TCGA = "tcga";
    private static final String TCGA_NAME = "TCGA";
    private static final String DATA_DICTIONARY_EXTENSION = "dd";
    private static final String TXT_EXTENSION = ".txt";

    private static final String FILE_TYPE_DATA_DICTIONARY = "data dictionary";
    private static final String FILE_TYPE_SUBJECT_DATA = "subject data";
    private static final String FILE_TYPE_PHENOTYPE_DATA = "phenotype data";
    private static final String SPACE = " ";
    private static final String DATA_DICTIONARY_FOR = "Data dictionary for ";

    private static final String BASE_FILENAME_SUBJECTS = "subj";
    private static final String BASE_FILENAME_SAMPLES = "samp";
    private static final String BASE_FILENAME_SLIDES = "slide";
    private static final String BASE_FILENAME_SUBJ_SAMP = "subj_samp";
    private static final double BYTES_PER_KB = 1024d;

    /**
     * Enum class representing a dbGap file to generate.  These are currently hard-coded to reflect the IDs of the corresponding
     * rows in the clinical_file table in the database.
     */
    public enum DbGapFile {
        Subjects(14, false, BASE_FILENAME_SUBJECTS, "subjects file"),
        SubjectsInfo(22, true, BASE_FILENAME_SUBJECTS + "_info", "subjects phenotype file"),
        Samples(15, true, BASE_FILENAME_SAMPLES, "samples file"),
        Slides(16, true, BASE_FILENAME_SLIDES, "sample slides file"),
        SubjectsToSamples(17, false, BASE_FILENAME_SUBJ_SAMP, "subject/sample map file"),
        SubjectsDrugs(18, true, BASE_FILENAME_SUBJECTS + "_drugs", "subjects drug regimens"),
        SubjectsExaminations(19, true, BASE_FILENAME_SUBJECTS + "_exams", "subjects examinations"),
        SubjectsRadiations(20, true, BASE_FILENAME_SUBJECTS + "_rad", "subjects radiation treatments"),
        SubjectsSurgeries(21, true, BASE_FILENAME_SUBJECTS + "_surg", "subjects surgeries");

        int fileId;
        boolean isDiseaseSpecific;
        String filename;
        String description;

        /**
         * Constructs a DbGapFile.
         *
         * @param fileId the id of the corresponding clinical_file row in the database
         * @param isDiseaseSpecific true if the file should be generated once per disease, false if once per submission
         * @param name the name of the file (to be appended to other parts to get full filename)
         * @param description the descriptive name of the file, for the manifest
         */
        DbGapFile(final int fileId, final boolean isDiseaseSpecific, final String name, final String description) {
            this.fileId = fileId;
            this.isDiseaseSpecific = isDiseaseSpecific;
            this.filename = name;
            this.description = description;
        }

        public int getFileId() {
            return fileId;
        }

        public boolean isDiseaseSpecific() {
            return isDiseaseSpecific;
        }

        public String getFilename() {
            return filename;
        }

        public String getDescription() {
            return description;
        }

    }

    private String location;

    // if other files need to be generated for submission, add them here
    private DataFileGenerator dataFileGenerator;
    private DataDictionaryGenerator dataDictionaryGenerator;
    private Collection<String> diseaseTypes;

    public DbGapSubmissionGenerator(final Map<String, ClinicalMetaQueries> clinicalMetaQueries,
                                    final Map<String, DbGapQueries> dbGapQueries) {
        dataDictionaryGenerator = new DataDictionaryGenerator(clinicalMetaQueries);
        dataFileGenerator = new DataFileGenerator(clinicalMetaQueries, dbGapQueries);
        setDiseaseTypes(dbGapQueries.keySet());
    }

    public DataDictionaryGenerator getDataDictionaryGenerator() {
        return dataDictionaryGenerator;
    }

    public DataFileGenerator getDataFileGenerator() {
        return dataFileGenerator;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public void setDiseaseTypes(final Collection<String> diseaseTypes) {
        this.diseaseTypes = diseaseTypes;
    }

    /**
     * Runs the submission generator.  Generates all data files and corresponding data dictionaries, as well as the
     * manifest.
     *
     * @throws IOException if there is an error writing the files
     * @return the number of files generated
     */
    public int run() throws IOException {
        // for each file type, generate a data dictionary and a data file
        StringBuilder manifestContent = new StringBuilder();
        manifestContent.append(MANIFEST_HEADER).append(NEWLINE);
        int fileCount = 0;
        for (final DbGapFile file : DbGapFile.values()) {
            if (file.isDiseaseSpecific) {
                for (final String disease : diseaseTypes) {
                    generateFiles(file, disease, manifestContent);
                    fileCount+=2;
                }
            } else {
                generateFiles(file, null, manifestContent);
                fileCount+=2;
            }
        }
        writeFile(new File(location, MANIFEST_FILENAME), manifestContent.toString());
        fileCount++;

        return fileCount;
    }

    private void generateFiles(final DbGapFile dbGapFile, final String disease, final StringBuilder manifestContent)
            throws IOException {
        // get data dictionary content
        String dataDictionary = dataDictionaryGenerator.generateDataDictionary(dbGapFile, disease);
        // write content to file
        String dataDictionaryFilename = new StringBuilder().append(TCGA).append(UNDERSCORE).append(dbGapFile.filename).
                append(dbGapFile.isDiseaseSpecific ? UNDERSCORE + disease : "").append(UNDERSCORE).append(DATA_DICTIONARY_EXTENSION).append(TXT_EXTENSION).toString();
        File dataDictionaryFile = new File(location, dataDictionaryFilename);
        writeFile(dataDictionaryFile, dataDictionary);

        // add data dictionary to manifest
        manifestContent.append(dataDictionaryFilename).append(FIELD_SEPARATOR).append(FILE_TYPE_DATA_DICTIONARY).
                append(FIELD_SEPARATOR).append(DATA_DICTIONARY_FOR).append(TCGA_NAME).append(SPACE).append(dbGapFile.isDiseaseSpecific ? disease + SPACE : "").
                append(dbGapFile.description).
                append(FIELD_SEPARATOR).append(getFileSize(dataDictionaryFile)).append(FIELD_SEPARATOR).append(NEWLINE);

        String data = dataFileGenerator.generateDataFile(dbGapFile, disease);
        String dataFilename = new StringBuilder().append(TCGA).append(UNDERSCORE).append(dbGapFile.filename).
                append(dbGapFile.isDiseaseSpecific() ? UNDERSCORE + disease : "").append(TXT_EXTENSION).toString();
        File dataFile = new File(location, dataFilename);
        writeFile(dataFile, data);

        manifestContent.append(dataFilename).append(FIELD_SEPARATOR).
                append(dbGapFile == DbGapFile.Subjects ? FILE_TYPE_SUBJECT_DATA : FILE_TYPE_PHENOTYPE_DATA).
                append(FIELD_SEPARATOR).append(TCGA_NAME).append(SPACE).append(dbGapFile.isDiseaseSpecific ? disease + SPACE : "").append(dbGapFile.description).
                append(FIELD_SEPARATOR).append(getFileSize(dataFile)).append(FIELD_SEPARATOR).append(NEWLINE);
    }

    private String getFileSize(final File file) {
        long sizeInKb = Math.round(file.length()/ BYTES_PER_KB);
        if (sizeInKb < 1) {
            sizeInKb = 1;
        }
        return String.valueOf(sizeInKb);
    }

    private void writeFile(final File file, final String content) throws IOException {
        PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );
        out.write(content);
        out.close();
    }


    /**
     * Main method for running the DbGap submission generator
     * @param args command-line arguments: 
     */
    public static void main(final String[] args) {
        if (args.length != 5) {
            System.out.println("Parameters must be: 1) location to generate 2) location of database properties file 3) common URL 4) common username 5) common password");
            System.exit(-1);
        }
        // 0. get location as first argument
        String generationLocation = args[0];
        if (generationLocation == null) {
            generationLocation = ".";
        }
        generationLocation += "/dbgap-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        File generationDirectory = new File(generationLocation);
        if (!generationDirectory.exists() && !generationDirectory.mkdirs()) {
            System.out.println("Unable to make directory " + generationLocation);
            System.exit(-2);
        }
        try {
            Map<String, DataSource> dataSources = makeDataSources(args[1]);
            DataSource commonDataSource = makeCommonDataSource(args[2], args[3], args[4]);
            DbGapSubmissionGenerator generator = makeGenerator(generationLocation, dataSources, commonDataSource);
            int numFiles = generator.run();
            System.out.println("Generated " + numFiles + " files in '" + generationLocation + ". Exiting.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit( 1 );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit( 0 );
    }

    protected static DataSource makeCommonDataSource(final String commonUrl, final String commonUsername, final String commonPassword) {
        return new SingleConnectionDataSource(commonUrl, commonUsername, commonPassword, false);
    }

    protected static Map<String, DataSource> makeDataSources(final String propertiesFileLocation)
            throws IOException, ClassNotFoundException {
        // file has one line per disease database, tab-separated, with columns: disease abbrev, URL, username, password
        Class.forName( "oracle.jdbc.OracleDriver" );
        Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
        File propertiesFile = new File(propertiesFileLocation);
        final BufferedReader in = new BufferedReader(new FileReader(propertiesFile));
        String str;
        while(( str = in.readLine() ) != null) {
            String[] line = str.split("\\t");
            String disease = line[0];
            String url = line[1];
            String username = line[2];
            String password = line[3];
            dataSources.put(disease, new SingleConnectionDataSource(url, username, password, false));
        }
        return dataSources;
    }  

    protected static DbGapSubmissionGenerator makeGenerator(
            final String generationLocation, final Map<String, DataSource> datasources, final DataSource commonDataSource) {

        final TissueSourceSiteQueriesJDBCImpl tssQueries = new TissueSourceSiteQueriesJDBCImpl();
        tssQueries.setDataSource(commonDataSource);
        final AnnotationQueriesJDBCImpl annotationQueries = new AnnotationQueriesJDBCImpl();
        annotationQueries.setDataSource(commonDataSource);

        final Map<String, ClinicalMetaQueries> clinicalQueriesMap = new HashMap<String, ClinicalMetaQueries>();
        final Map<String, DbGapQueries> dbGapQueriesMap = new HashMap<String, DbGapQueries>();
        for (final String disease : datasources.keySet()) {
            DataSource datasource = datasources.get(disease);
            ClinicalMetaQueriesJDBCImpl clinicalMetaQueries = new ClinicalMetaQueriesJDBCImpl();
            clinicalMetaQueries.setDataSource(datasource);
            clinicalQueriesMap.put(disease, clinicalMetaQueries);
            DbGapQueriesJDBCImpl dbGapQueries = new DbGapQueriesJDBCImpl(clinicalMetaQueries);
            dbGapQueries.setDataSource(commonDataSource);
            dbGapQueries.setAnnotationQueries(annotationQueries);
            dbGapQueries.setTissueSourceSiteQueries(tssQueries);
            dbGapQueriesMap.put(disease, dbGapQueries);
        }
        DbGapSubmissionGenerator generator = new DbGapSubmissionGenerator(clinicalQueriesMap, dbGapQueriesMap);
        generator.setLocation(generationLocation);
        return generator;
    }
}
