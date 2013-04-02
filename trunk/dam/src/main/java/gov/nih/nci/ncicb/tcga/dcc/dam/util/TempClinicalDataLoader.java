/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical.*;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical.*;
import org.xml.sax.SAXException;

/**
 * - written very procedurally.
 * - not caring about OOP, reuseability, or efficiency at this point
 *
 * Usage: java TempClinicalDataLoader xmlFileList tier username password
 * Where: xmlFile list lists each file to be loaded into the database, with each line having the format:
 * pathToFile\tarchiveName
 * tier is prod, stage, qa, or dev (maps to the correct database server)
 * username is database username
 * password is database password
 *
 * @author HickeyE
 * @version $id$
 */
public class TempClinicalDataLoader {

    private static final Map<String, String> urlSet = new HashMap<String, String>( 5 );
    static {
        urlSet.put( "prod", "jdbc:oracle:thin:@ncidb-tcga-p.nci.nih.gov:1562:TCGAPRD2" );
        urlSet.put( "stage", "jdbc:oracle:thin:@cbiodb520.nci.nih.gov:1521:TCGASTG" );
        urlSet.put( "qa", "jdbc:oracle:thin:@cbiodb530.nci.nih.gov:1521:TCGAQA" );
        urlSet.put( "dev", "jdbc:oracle:thin:@cbiodb540.nci.nih.gov:1521:TCGADEV" );
    }

    private static Pathology pathology;
    private static DiseaseSlide diseaseSlide;
    protected static Connection dbConnection;

    public static void main( String[] args ) {
        // first get the db connection properties
        String url = urlSet.get( args[1] );
        String user = args[2];
        String word = args[3];

        // make sure we have the Oracle driver somewhere
        try {
            Class.forName( "oracle.jdbc.OracleDriver" );
            Class.forName( "org.postgresql.Driver" );
        }
        catch(Exception x) {
            System.out.println( "Unable to load the driver class!" );
            System.exit( 0 );
        }
        // connect to the database
        try {
            dbConnection = DriverManager.getConnection( url, user, word );
            ClinicalBean.setDBConnection( dbConnection );
        }
        catch(SQLException x) {
            x.printStackTrace();
            System.exit( 1 );
        }

        final String xmlList = args[0];
        BufferedReader br = null;
        try {
            final Map<String, String> clinicalFiles = new HashMap<String, String>();
            final Map<String, String> biospecimenFiles = new HashMap<String, String>();
            final Map<String, String> fullFiles = new HashMap<String, String>();

            //noinspection IOResourceOpenedButNotSafelyClosed
            br = new BufferedReader( new FileReader( xmlList ) );

            // read the file list to get all the files to load
            while(br.ready()) {
                final String[] in = br.readLine().split( "\\t" );
                String xmlfile = in[0];
                String archive = in[1];

                if (xmlfile.contains("_clinical")) {
                    clinicalFiles.put(xmlfile, archive);
                } else if (xmlfile.contains("_biospecimen")) {
                    biospecimenFiles.put(xmlfile, archive);
                } else {
                    fullFiles.put(xmlfile, archive);
                }
            }

            Date dateAdded = Calendar.getInstance().getTime();

            // NOTE!!! This deletes all data before the load starts, assuming we are re-loading everything.
            // a better way would be to figure out what has changed and load that, or to be able to load multiple versions of the data in the schema
            emptyClinicalTables(user);

            // load any "full" files first -- in case some archives aren't split yet
            for (final String file : fullFiles.keySet()) {
                String archive = fullFiles.get(file);
                System.out.println( "Full file " + file + " in " +  archive);
                // need to re-instantiate the disease-specific beans for each file
                createDiseaseSpecificBeans(xmlList);
                String disease = getDiseaseName(archive);
                processFullXmlFile(file, archive, disease, dateAdded);

                // memory leak or something... have to commit and close all connections and re-get connection
                // after each file to keep from using too much heap space.  this troubles me, but I have never had
                // the time to figure out why it happens
                resetConnections(url, user, word);
            }

            // now process all clinical files, and insert patients and clinical data
            for (final String clinicalFile : clinicalFiles.keySet()) {
                createDiseaseSpecificBeans(xmlList);
                String archive = clinicalFiles.get(clinicalFile);
                System.out.println( "Clinical file " + clinicalFile + " in " +  archive);
                String disease = getDiseaseName(archive);
                processClinicalXmlFile( clinicalFile, archive, disease, dateAdded );
                resetConnections(url, user, word);
            }

            // now process biospecimen files
            for (final String biospecimenFile : biospecimenFiles.keySet()) {
                createDiseaseSpecificBeans(xmlList);
                String archive = biospecimenFiles.get(biospecimenFile);
                String disease = getDiseaseName(archive);
                System.out.println( "Biospecimen file " + biospecimenFile );
                processBiospecimenXmlFile(biospecimenFile, archive, disease, dateAdded);
                resetConnections(url, user, word);
            }

            // this sets relationships between these clinical tables and data browser tables, since we delete
            // and reload every time
            setForeignKeys();
            dbConnection.commit();
            dbConnection.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    private static String getDiseaseName(final String archiveName) {
        String disease = "";
        // this is dumb, the disease should be passed as a parameter!
        if(archiveName.contains( "GBM" )) {
            disease = "GBM";
        } else if(archiveName.contains( "OV" )) {
            disease = "OV";
        } else if(archiveName.contains( "LUSC" )) {
            disease = "LUSC";
        } else if (archiveName.contains("COAD")) {
            disease = "COAD";
        } else if (archiveName.contains("READ")) {
            disease = "READ";
        } else if (archiveName.contains("LAML")) {
            disease = "LAML";
        } else {
            System.out.println("Unknown disease type!");
            System.exit(1);
        }
        return disease;
    }

    private static void createDiseaseSpecificBeans(final String xmlList) {
        if(xmlList.contains( "GBM" )) {
            pathology = new GBMPathology();
            diseaseSlide = new GBMSlide();
        } else if(xmlList.contains( "OV" )) {
            pathology = new OvarianPathology();
            diseaseSlide = new OvarianSlide();
        }
        // other diseases don't have specialized path and slide tables
    }

    private static void resetConnections(final String url, final String user, final String word) throws SQLException {
        dbConnection.commit();
        ClinicalBean.cleanup();
        dbConnection.close();
        dbConnection = DriverManager.getConnection( url, user, word );
        ClinicalBean.setDBConnection( dbConnection );
    }

    private static void setForeignKeys() throws SQLException {
        String updateSql = "UPDATE l4_patient l SET l.clinical_patient_id = (SELECT p.patient_id FROM patient p WHERE p.bcrpatientbarcode = l.patient)";
        PreparedStatement stmt = dbConnection.prepareStatement(updateSql);
        stmt.execute();
        updateSql = "update hybridization_ref hr set hr.aliquot_id=(select a.aliquot_id from aliquot a where a.bcraliquotbarcode=hr.bestBarcode)";
        stmt = dbConnection.prepareStatement(updateSql);
        stmt.execute();
    }

    private static int processElement(String pathToElement, Document doc, XPath xpath, ClinicalBean bean, int parentId) throws XPathExpressionException, InstantiationException, IllegalAccessException {
        // check if element exists... if not, return -1
        NodeList nodes = (NodeList) xpath.evaluate( pathToElement, doc, XPathConstants.NODESET );
        if (nodes.getLength() < 1) {
            return -1;
        }

        // for each bean attribute, look for the value of the element with that name and set it in the bean
        for (String attribute : bean.getOrderedAttributes()) {
            String value = xpath.evaluate( pathToElement + "/" + attribute + "/text()", doc );
            bean.setAttribute( attribute, value );
        }

        // tell the bean to save itself to the database
        return bean.insertSelf( parentId );
    }

    // processes a set of elements under one parent (like DRUGS, SURGERIES, etc)
    private static ClinicalBean[] processElementGroup(String pathToParent, Document doc, XPath xpath, ClinicalBean bean, int parentId ) throws XPathExpressionException, IllegalAccessException, InstantiationException {
        // the bean knows its XML group name, if any
        if (bean.getXmlGroupName() != null) {
            pathToParent += "/" + bean.getXmlGroupName();
        }
        pathToParent += "/" + bean.getXmlElementName();
        // get all child nodes
        NodeList nodes = (NodeList) xpath.evaluate( pathToParent, doc, XPathConstants.NODESET );
        // make a bean for each child node
        ClinicalBean[] beans = new ClinicalBean[nodes.getLength()];
        for (int i=1; i<=nodes.getLength(); i++) {
            // clone the passed in bean, which has the correct specific class
            ClinicalBean b = bean.makeClone();
            // now process this specific child element
            processElement( pathToParent + "[" + i + "]", doc, xpath, b, parentId );
            beans[i-1] = b;
        }
        return beans;
    }

    private static void processFullXmlFile( final String xmlFile, final String archiveName,
                                            final String disease,
                                            final Date dateAdded )
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException,
            InstantiationException, IllegalAccessException {

        Patient.setDBConnection( dbConnection );

        // Create an XPath object from this xml file
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        // create the Patient bean for this file
        Patient p = new Patient();
        p.setAttribute( "ARCHIVE_NAME", archiveName );
        p.setAttribute( "DISEASE", disease );
        p.setDateAdded( dateAdded );
        String basePath = "//TCGA_BCR";

        // process all patients in this file (currently is only 1 but this should work even with multiples)
        ClinicalBean[] patients = processElementGroup( basePath, doc, xpath, p, 0 );
        // for each patient, process its clinical elements
        for (int patient_i=0; patient_i<patients.length; patient_i++) {
            Patient patient = (Patient) patients[patient_i];
            String patientPath = basePath + "/" + patient.getXmlElementName() + "[" + (patient_i+1) + "]";
            processSamples(doc, xpath, patient, patientPath);
            // patients > drugs
            processElementGroup( patientPath, doc, xpath, new Drug(), patient.getPatientID() );

            // patients > radiations
            processElementGroup( patientPath, doc, xpath, new Radiation(), patient.getPatientID() );

            // patients > examinations
            processElementGroup( patientPath, doc, xpath, new Examination(), patient.getPatientID() );

            // patients > surgeries
            processElementGroup( patientPath, doc, xpath, new Surgery(), patient.getPatientID() );
        }

    }

    private static void processSamples(
            final Document doc, final XPath xpath, final Patient patient, final String patientPath)
            throws XPathExpressionException, IllegalAccessException, InstantiationException {
        Sample s = new Sample();
        ClinicalBean[] samples = processElementGroup( patientPath, doc, xpath, s, patient.getPatientID() );
        // for each sample:
        for (int sample_i=0; sample_i<samples.length; sample_i++) {
            Sample sample = (Sample) samples[sample_i];
            String samplePath = patientPath + "/" + sample.getXmlGroupName() + "/" + sample.getXmlElementName() + "[" + (sample_i + 1) + "]";
            // tumor pathology
            TumorPathology tumorPath = new TumorPathology();
            processElement( samplePath + "/" + tumorPath.getXmlElementName(), doc, xpath, tumorPath, sample.getSampleID() );
            // tumorpathology has pathology
            if (pathology != null) {
                processElement( samplePath + "/" + tumorPath.getXmlElementName() + "/" + pathology.getXmlElementName(), doc, xpath, pathology, tumorPath.getTumorPathologyID() );
            }

            // portions
            processPortions(doc, xpath, sample, samplePath);
        }
    }

    private static void processPortions(
            final Document doc, final XPath xpath, final Sample sample, final String samplePath)
            throws XPathExpressionException, IllegalAccessException, InstantiationException {
        Portion port = new Portion();
        ClinicalBean[] portions = processElementGroup( samplePath, doc, xpath, port, sample.getSampleID() );
        for (int portion_i=0; portion_i< portions.length; portion_i++) {
            Portion portion = (Portion) portions[portion_i];
            String portionPath = samplePath + "/" + portion.getXmlGroupName() + "/" + portion.getXmlElementName() + "[" + (portion_i+1) + "]";

            // portions > slides
            processSlides(doc, xpath, portion, portionPath);

            // portions > analytes
            processAnalytes(doc, xpath, portion, portionPath);
        }
    }

    private static void processSlides(
            final Document doc, final XPath xpath, final Portion portion, final String portionPath)
            throws XPathExpressionException, IllegalAccessException, InstantiationException {
        ClinicalBean[] slides = processElementGroup( portionPath, doc, xpath, new Slide(), portion.getPortionID() );
        for (int slide_i=0; slide_i<slides.length; slide_i++) {
            Slide slide = (Slide) slides[slide_i];
            // this is not in the XML, just is a mapping table
            PortionSlide portionSlide = new PortionSlide();
            portionSlide.insertSelf( portion.getPortionID(), slide.getSlideID(), slide.getSectionLocation() );

            // disease slide
            if (diseaseSlide != null) {
                processElement( portionPath + "/" + slide.getXmlGroupName() + "/" + slide.getXmlElementName() +
                        "[" + (slide_i+1) + "]/" + diseaseSlide.getXmlElementName(),
                        doc, xpath, diseaseSlide, slide.getSlideID() );
            }
        }
    }

    private static void processAnalytes(
            final Document doc, final XPath xpath, final Portion portion, final String portionPath)
            throws XPathExpressionException, IllegalAccessException, InstantiationException {
        ClinicalBean[] analytes = processElementGroup( portionPath, doc, xpath, new Analyte(), portion.getPortionID() );
        for (int analyte_i=0; analyte_i<analytes.length; analyte_i++) {
            Analyte analyte = (Analyte) analytes[analyte_i];
            String analytePath = portionPath + "/" + analyte.getXmlGroupName() + "/" + analyte.getXmlElementName() + "[" + (analyte_i+1) + "]";
            // analytes > aliquots
            processElementGroup( analytePath, doc, xpath, new Aliquot(), analyte.getAnalyteID() );
            // analyte > protocol
            processElementGroup( analytePath, doc, xpath, new Protocol(), analyte.getAnalyteID() );
            // analyte > DNA or RNA
            // need to check which one it is? or
            DNA dna = new DNA();
            RNA rna = new RNA();
            if (processElement( analytePath + "/" + dna.getXmlElementName(), doc, xpath, dna, analyte.getAnalyteID() ) == -1) {
                processElement( analytePath + "/" + rna.getXmlElementName(), doc, xpath, rna, analyte.getAnalyteID() );
            }
        }
    }


    private static void processBiospecimenXmlFile( final String xmlFile, final String archiveName, final String disease,
                                        final Date dateAdded  )
            throws ParserConfigurationException, IOException, SAXException, SQLException, XPathExpressionException,
            InstantiationException, IllegalAccessException {

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        Patient patient = new Patient();
        Pattern filePattern = Pattern.compile("(TCGA-[A-Z0-9]{2}-[A-Z0-9]{4})");
        Matcher matcher = filePattern.matcher(xmlFile);
        matcher.find();
        String patientBarcode = matcher.group(1);
        if (patientBarcode == null) {
            System.out.println("Failed to get patient barcode from filename!");
            System.exit(-1);
        }
        patient.setPatientID(Patient.getPatientIdForBarcode(patientBarcode));
        if (patient.getPatientID() < 1) {
            System.out.println("Could not find PATIENT record for " + patientBarcode + " saving it now...");
            patient.setAttribute( "ARCHIVE_NAME", archiveName );
            patient.setAttribute( "DISEASE", disease );
            patient.setDateAdded( dateAdded );
            patient.setAttribute("BCRPATIENTBARCODE", patientBarcode);            
            patient.insertSelf(0);
        }
        String patientPath = "//" + patient.getXmlElementName() + "[1]";
        processSamples(doc, xpath, patient, patientPath);
    }

    private static void processClinicalXmlFile( final String xmlFile, final String archiveName, final String disease,
                                        final Date dateAdded )
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException,
            InstantiationException, IllegalAccessException {
        Patient.setDBConnection( dbConnection );

        // get all patient barcodes
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Patient p = new Patient();
        p.setAttribute( "ARCHIVE_NAME", archiveName );
        p.setAttribute( "DISEASE", disease );
        p.setDateAdded( dateAdded );
        String basePath = "//TCGA_BCR";
        ClinicalBean[] patients = processElementGroup( basePath, doc, xpath, p, 0 );
        // for each patient, process its samples
        for (int patient_i=0; patient_i<patients.length; patient_i++) {
            Patient patient = (Patient) patients[patient_i];

            Sample s = new Sample();
            String patientPath = basePath + "/" + patient.getXmlElementName() + "[" + (patient_i+1) + "]";

            // patients > drugs
            processElementGroup( patientPath, doc, xpath, new Drug(), patient.getPatientID() );

            // patients > radiations
            processElementGroup( patientPath, doc, xpath, new Radiation(), patient.getPatientID() );

            // patients > examinations
            processElementGroup( patientPath, doc, xpath, new Examination(), patient.getPatientID() );

            // patients > surgeries
            processElementGroup( patientPath, doc, xpath, new Surgery(), patient.getPatientID() );
        }        
    }

    private static void emptyClinicalTables(String username) throws SQLException {
        final ArrayList<String> tables = new ArrayList<String>();
        PreparedStatement stmt;
        tables.add( "DNA" );
        tables.add( "RNA" );
        tables.add( "PROTOCOL" );        
        tables.add( "ALIQUOT" );
        tables.add( "ANALYTE" );
        tables.add( "PORTION_SLIDE" );
        tables.add( "PORTION" );
        if (username.contains("gbm")) {
            tables.add( "GBMSLIDE" );
        }
        tables.add( "SLIDE" );
        if (username.contains("gbm")) {
            tables.add( "GBM_PATHOLOGY" );
        } else if (username.contains("ov")) {
            tables.add( "OVARIAN_PATHOLOGY" );
        }
        tables.add( "TUMORPATHOLOGY" );
        tables.add( "SAMPLE" );
        tables.add( "SURGERY" );
        tables.add( "DRUG_INTGEN" );
        tables.add( "EXAMINATION" );
        tables.add( "RADIATION" );
        tables.add( "PATIENT" );
        final String baseSQL = "delete from ";
        for(final String table : tables) {
            final String workingSQL = baseSQL + " " + table;
            System.out.println( "workingSQL = " + workingSQL );
            stmt = dbConnection.prepareStatement( workingSQL );
            final int c = stmt.executeUpdate();
            System.out.println( "deleted = " + c );
            stmt.close();
        }

        // also, reset sequences! TODO
        
    }
}


