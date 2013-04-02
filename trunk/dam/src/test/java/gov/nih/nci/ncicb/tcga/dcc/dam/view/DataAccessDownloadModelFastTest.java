/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.common.util.BCRXMLFileReader;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * JUnit test for the DataAccessDownloadModel class
 * <p/>
 * Author: David Nassau
 */
public class DataAccessDownloadModelFastTest {

    private static final String DISEASE_TYPE = "GBM";
    private static final String XSD_FILE = "DataAccessDownloadModel_schema.xsd";
    private List<DataFile> fileInfos;
    private DataFile clinicalFile;
    private DataAccessDownloadModel model;
    private static final String SAMPLES_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void setUp() throws Exception {
        populateFileInfoList();

        clinicalFile = new DataFileClinical();
        clinicalFile.setCenterId("BCR");
        clinicalFile.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
        clinicalFile.setFileId("55555");
        clinicalFile.setSize(56789);
        clinicalFile.setProtected(false);
        clinicalFile.setFileName("patients.txt");

        model = new DataAccessDownloadModel(DISEASE_TYPE);
    }

    @Test
    public void testAddClinicalFileInfo() {
        model.addFileInfo(Arrays.asList(clinicalFile));
        DataAccessDownloadModel.TreeNode root = model.getRootNode();
        // expected structure is:
        // root
        //  |- "-999" (folder) [platform type]
        //      |- "BCR" (folder) [center]
        //         |- "C" (folder) [level]
        //            |- "patients.txt" (file) [file]
        assertEquals(1, root.getChildren().size()); //
        assertEquals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, root.getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).isFolder());

        assertEquals(1, root.getChildren().get(0).getChildren().size());
        assertEquals(DataAccessMatrixQueries.GENERIC_BCR_CENTER, root.getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(0).isFolder());

        assertEquals(1, root.getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals(DataAccessMatrixQueries.LEVEL_CLINICAL, root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).isFolder());

        assertEquals(1, root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals("patients.txt", root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertFalse(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).isFolder());
    }

    @Test
    public void testGetXmlForTreeClinical() {
        model.addFileInfo(Arrays.asList(clinicalFile));
        final String modelXML = model.getXmlForTree();
        assertNotNull(modelXML);
        // actual XML is missing the "level" folder because clinical data doesn't have levels at the moment
        assertEquals("<tree id='0' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='null' >" +
                "<item id='1' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='-999' >" +
                "<item id='2' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='BCR' >" +
                "<item id='4' child='0' im1='leaf.gif' text='null::patients.txt (55.458 KiB)' >" +
                "</item></item></item></tree>",
                modelXML);
    }

    @Test
    public void testAddSampleAnnotationFileInfo() {
        model.addFileInfo(makeSampleAnnotation());
        DataAccessDownloadModel.TreeNode root = model.getRootNode();
        // expected structure is:
        // root
        //  |- "METADATA" (folder) [platform type]
        //      |- "SAMPLE ANNOTATION" (folder) [center]
        //         |- "M" (folder) [level]
        //            |- "sampleAnnotation.txt" (file) [file]
        //      |- "BI/CNP" (folder) [center]
        //         |- "M" (folder) [level]
        //            |- "BI_SNP.txt" (file) [file]
        //            |- "BI_SNP_sdrf.txt" (file) [file]

        assertEquals(1, root.getChildren().size()); //
        assertEquals("METADATA", root.getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).isFolder());

        assertEquals(2, root.getChildren().get(0).getChildren().size());
        assertEquals("SAMPLE ANNOTATION", root.getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(0).isFolder());
        assertEquals("BI/CNP", root.getChildren().get(0).getChildren().get(1).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(1).isFolder());

        assertEquals(1, root.getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals(DataAccessMatrixQueries.LEVEL_METADATA, root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).isFolder());
        assertEquals(DataAccessMatrixQueries.LEVEL_METADATA, root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getFileOrFolderName());
        assertTrue(root.getChildren().get(0).getChildren().get(1).getChildren().get(0).isFolder());

        assertEquals(1, root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals("sampleAnnotation.txt", root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertFalse(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).isFolder());
        assertEquals(2, root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().size());
        assertEquals("BI_SNP.txt", root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0).getFileOrFolderName());
        assertFalse(root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0).isFolder());
        assertEquals("BI_SNP_sdrf.txt", root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(1).getFileOrFolderName());
        assertFalse(root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(1).isFolder());
    }

    @Test
    public void testGetXmlForSampleAnnotation() throws Exception {
        model.addFileInfo(makeSampleAnnotation());
        final String modelXML = model.getXmlForTree();
        assertNotNull(modelXML);
        assertEquals("<tree id='0' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='null' >" +
                "<item id='1' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='METADATA' >" +
                "<item id='2' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='SAMPLE ANNOTATION' >" +
                "<item id='4' child='0' im1='leaf.gif' text='selected_samples::sampleAnnotation.txt (0.106 KiB)' >" +
                "</item></item><item id='5' child='1' im1='folderOpen.gif' im2='folderClosed.gif' text='BI/CNP' >" +
                "<item id='7' child='0' im1='leaf.gif' text='selected_samples::BI_SNP.txt (4.013 KiB)' >" +
                "</item><item id='8' child='0' im1='leaf.gif' text='selected_samples::BI_SNP_sdrf.txt (4.989 KiB)' >" +
                "</item></item></item></tree>",
                modelXML);
    }

    protected List<DataFile> makeSampleAnnotation() {
        final List<DataFile> res = new ArrayList<DataFile>();
        final DataFileMetadata annotationFile = new DataFileMetadata();
        final DataFileMetadata dataFile1 = new DataFileMetadata();
        final DataFileMetadata dataFile2 = new DataFileMetadata();
        final List<String> selectedSamples = new LinkedList<String>();
        selectedSamples.add("TCGA-01-1234-01");
        selectedSamples.add("TCGA-02-5678-01");
        annotationFile.setPlatformTypeId("METADATA");
        annotationFile.setCenterId("SAMPLE ANNOTATION");
        annotationFile.setDisplaySample("selected_samples");
        annotationFile.setProtected(false);
        annotationFile.setSize(109);
        annotationFile.setFileName("sampleAnnotation.txt");
        annotationFile.setSamples(selectedSamples);
        annotationFile.setPatientsFromSamples(selectedSamples);
        dataFile1.setPlatformTypeId("METADATA");
        dataFile1.setCenterId("BI/CNP");
        dataFile1.setDisplaySample("selected_samples");
        dataFile1.setProtected(false);
        dataFile1.setSize(4109);
        dataFile1.setFileName("BI_SNP.txt");
        dataFile1.setSamples(selectedSamples);
        dataFile1.setPatientsFromSamples(selectedSamples);
        dataFile2.setPlatformTypeId("METADATA");
        dataFile2.setCenterId("BI/CNP");
        dataFile2.setDisplaySample("selected_samples");
        dataFile2.setProtected(false);
        dataFile2.setSize(5109);
        dataFile2.setFileName("BI_SNP_sdrf.txt");
        dataFile2.setSamples(selectedSamples);
        dataFile2.setPatientsFromSamples(selectedSamples);
        res.add(dataFile1);
        res.add(dataFile2);
        res.add(0, annotationFile);
        return res;
    }

    // test methods below probably need to be rewritten to be more flexible...

    private void populateFileInfoList() {
        fileInfos = new ArrayList<DataFile>();
        DataFile fileInfo = new DataFileLevelOne(); //new DataFile();
        fileInfo.setPlatformTypeId("1");
        fileInfo.setCenterId("1");
        fileInfo.setProtected(false);
        fileInfo.setFileName("file1.idat");
        fileInfo.setFileId("11111");
        fileInfo.setSize(123456); //bytes
        fileInfos.add(fileInfo);
        fileInfo = new DataFileLevelOne();
        fileInfo.setPlatformTypeId("1");
        fileInfo.setCenterId("1");
        fileInfo.setProtected(false);
        fileInfo.setFileName("file2.idat");
        fileInfo.setFileId("22222");
        fileInfo.setSize(23456); //bytes
        fileInfos.add(fileInfo);
        fileInfo = new DataFileLevelOne();
        fileInfo.setPlatformTypeId("1");
        fileInfo.setCenterId("2");
        fileInfo.setProtected(false);
        fileInfo.setFileName("file3.idat");
        fileInfo.setFileId("33333");
        fileInfo.setSize(34567); //bytes
        fileInfos.add(fileInfo);
        fileInfo = new DataFileLevelTwo();
        fileInfo.setPlatformTypeId("2");
        fileInfo.setCenterId("3");
        fileInfo.setProtected(false);
        fileInfo.setFileName("file4.idat");
        fileInfo.setFileId("44444");
        fileInfo.setSize(45678); //bytes
        fileInfos.add(fileInfo);
        //Assumption is that input objects are sorted by platform, center, level, sample.
    }

    @Test
    public void testModel() throws Exception {
        final DataAccessDownloadModel model = new DataAccessDownloadModel(DISEASE_TYPE);
        assertEquals(DISEASE_TYPE, model.getDiseaseType());
        model.addFileInfo(fileInfos);
        final Document xmlDoc = validateXml(model);
        checkXmlOutputAgainstInput(xmlDoc);
        checkXmlFileSizes(model, xmlDoc);
        checkFileSelection(model, xmlDoc);
    }

    //validates output against schema
    private Document validateXml(final DataAccessDownloadModel model) throws Exception {
        final String xsdFileName = SAMPLES_DIR + "portal/" + XSD_FILE;
        final File xsdFile = new File(xsdFileName);
        final String modelXML = model.getXmlForTree();
        BCRXMLFileReader xmlReader;
        try {
            xmlReader = new BCRXMLFileReader(modelXML, xsdFile);
            //no exception means it validated OK against the schema
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);  //so we don't have to declare umpteen types of exception in the test class, we just wrap any exception
        }
        return xmlReader.getDocument();
    }

    private void checkXmlOutputAgainstInput(final Document xmlDoc) {
        final int[] nodeIdx = new int[1];
        nodeIdx[0] = 0;
        checkLeafNodesAgainstInput(xmlDoc.getDocumentElement(), nodeIdx);
        checkHierarchy(xmlDoc.getDocumentElement());
    }

    //make sure between each pair of nodes, the least common ancestor is where it should be.
    //This makes sure the hierarchy has not become corrupted.
    private void checkHierarchy(final Element rootnode) {
        //for each pair of input items
        for (int i = 0; i < fileInfos.size() - 1; i++) {
            for (int ii = i + 1; ii < fileInfos.size(); ii++) {
                final Element node1 = findNodeForFileName(fileInfos.get(i).getFileName(), rootnode);
                assertNotNull(node1);
                final Element node2 = findNodeForFileName(fileInfos.get(ii).getFileName(), rootnode);
                assertNotNull(node2);
                assertFalse(node1 == node2);
                final int ancestorLevel = findLowestCommonAncestorLevel(node1, node2, rootnode);
                assertFalse(ancestorLevel == 0); //can't be the root
                switch (ancestorLevel) {
                    case 1:
                        //one level below root: the platforms must be different
                        assertFalse(fileInfos.get(i).getPlatformTypeId().equals(fileInfos.get(ii).getPlatformTypeId()));
                        break;
                    case 2:
                        //platforms must be same, centers must be different
                        assertTrue(fileInfos.get(i).getPlatformTypeId().equals(fileInfos.get(ii).getPlatformTypeId()));
                        assertFalse(fileInfos.get(i).getCenterId().equals(fileInfos.get(ii).getCenterId()));
                        break;
                    case 3:
                        //platforms same, centers same, levels different
                        assertTrue(fileInfos.get(i).getPlatformTypeId().equals(fileInfos.get(ii).getPlatformTypeId()));
                        assertTrue(fileInfos.get(i).getCenterId().equals(fileInfos.get(ii).getCenterId()));
                        assertFalse(fileInfos.get(i).getLevel().equals(fileInfos.get(ii).getLevel()));
                        break;
                    case 4:
                        //platforms same, centers same, levels same
                        assertTrue(fileInfos.get(i).getPlatformTypeId().equals(fileInfos.get(ii).getPlatformTypeId()));
                        assertTrue(fileInfos.get(i).getCenterId().equals(fileInfos.get(ii).getCenterId()));
                        assertTrue(fileInfos.get(i).getLevel().equals(fileInfos.get(ii).getLevel()));
                        break;
                    default:
                        //nothing beyond 4 can happen
                        fail();
                }
            }
        }
    }

    //returns number of levels below the root the common parent is. If the common parent IS the root, it's 0
    private int findLowestCommonAncestorLevel(final Element node1, final Element node2, final Element rootnode) {
        Element node1Ancestor = (Element) node1.getParentNode();
        Element node2Ancestor = (Element) node2.getParentNode();
        while (node1Ancestor != node2Ancestor) {
            node1Ancestor = (Element) node1Ancestor.getParentNode();
            node2Ancestor = (Element) node2Ancestor.getParentNode();
        }
        //found the common ancestor - how many levels up to rootnode from here?
        int ret = 1;
        while (node1Ancestor != rootnode) {
            node1Ancestor = (Element) node1Ancestor.getParentNode();
            ret++;
        }
        return ret;
    }

    private Element findNodeForFileName(final String fileName, final Element elem) {
        Element ret = null;
        if (elem.getAttribute("text").contains(fileName)) {
            ret = elem;
        } else {
            if (elem.hasChildNodes()) {
                final NodeList children = elem.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    final Node child = children.item(i);
                    if (child instanceof Element) {
                        ret = findNodeForFileName(fileName, (Element) child);
                        if (ret != null) {
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    //makes sure all the original input node appear, in the correct order
    //But does not check hierarchy - a separate method does that.
    private void checkLeafNodesAgainstInput(final Element elem, final int[] nodeIdx) {
        if (elem.hasChildNodes()) {
            final NodeList children = elem.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Element) {
                    checkLeafNodesAgainstInput((Element) child, nodeIdx);
                }
            }
        } else {
            final DataFile fileInfo = fileInfos.get(nodeIdx[0]);
            nodeIdx[0]++;
            final String nodetext = elem.getAttribute("text");
            assertTrue(nodetext.contains(fileInfo.getFileName()));
            if (fileInfo.isProtected()) {
                assertTrue(nodetext.contains("Protected"));
                assertEquals(elem.getAttribute("aCol"), DataAccessDownloadModel.COLOR_PROTECTED);
            }
        }
    }

    //check the file sizes we get back against the original input sizes
    private void checkXmlFileSizes(final DataAccessDownloadModel model, final Document xmlDoc) {
        //retrieve all file sizes from the model
        final Map<String, Long> sizes = model.getFileSizes();
        for (final String id : sizes.keySet()) {
            final long size = sizes.get(id);
            //we have to search the xml to find the file name corresponding to this ID
            final String fileName = lookupFileNameFromId(id, xmlDoc.getDocumentElement());
            assertNotNull(fileName);
            //find the original fileInfo object for this file name
            DataFile theFileInfo = null;
            for (final DataFile fileInfo : fileInfos) {
                if (fileInfo.getFileName().equals(fileName)) {
                    theFileInfo = fileInfo;
                    break;
                }
            }
            assertNotNull(theFileInfo);
            assertEquals(size, theFileInfo.getSize());
        }
    }

    //simulates a user selecting tree nodes - make sure we get the right file IDs back
    private void checkFileSelection(final DataAccessDownloadModel model, final Document xmlDoc) {
        final List<String> treeIds = new ArrayList<String>(); //numeric tree IDs we will look up from the xml
        final List<String> fids = new ArrayList<String>(); //file Ids which we already know
        //select one file
        //look up the tree Id in the XML corresponding to the file name
        //(the tree id is what we send back to the model)
        String treeId = lookupIdFromFileName(fileInfos.get(0).getFileName(), xmlDoc.getDocumentElement());
        assertNotNull(treeId);
        treeIds.add(treeId);
        fids.add(fileInfos.get(0).getFileId());
        _checkFileSelection(treeIds, fids, model);
        treeId = lookupIdFromFileName(fileInfos.get(2).getFileName(), xmlDoc.getDocumentElement());
        assertNotNull(treeId);
        treeIds.add(treeId);
        fids.add(fileInfos.get(2).getFileId());
        _checkFileSelection(treeIds, fids, model);
        treeId = lookupIdFromFileName(fileInfos.get(3).getFileName(), xmlDoc.getDocumentElement());
        assertNotNull(treeId);
        treeIds.add(treeId);
        fids.add(fileInfos.get(3).getFileId());
        _checkFileSelection(treeIds, fids, model);
    }

    private void _checkFileSelection(final List<String> treeIds, final List<String> expectedFileIds,
                                     final DataAccessDownloadModel model) {
        String selectedIds = "";
        for (int i = 0; i < treeIds.size(); i++) {
            if (i > 0) {
                selectedIds += ",";
            }
            selectedIds += treeIds.get(i);
        }
        final List<DataFile> returnedFileInfo = model.getFileInfoForSelectedTreeNodes(selectedIds);
        final List<String> returnedIds = new ArrayList<String>();
        for (final DataFile fi : returnedFileInfo) {
            returnedIds.add(fi.getFileId());
        }
        assertEquals(treeIds.size(), returnedIds.size());
        for (int i = 0; i < treeIds.size(); i++) {
            assertTrue(returnedIds.contains(expectedFileIds.get(i)));
            assertTrue(expectedFileIds.contains(returnedIds.get(i)));
        }
    }

    private String lookupFileNameFromId(final String id, final Element elem) {
        String ret = null;
        if (elem.getAttribute("id").equals(id)) {
            ret = elem.getAttribute("text");
            int pos = ret.indexOf("::");
            if (pos >= 0) {
                ret = ret.substring(pos + 2);
            }
            pos = ret.indexOf(" (");
            if (pos >= 0) {
                ret = ret.substring(0, pos);
            }
        } else {
            if (elem.hasChildNodes()) {
                final NodeList children = elem.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    final Node child = children.item(i);
                    if (child instanceof Element) {
                        ret = lookupFileNameFromId(id, (Element) child);
                        if (ret != null) {
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private String lookupIdFromFileName(final String fileName, final Element rootelem) {
        String ret = null;
        final Element foundElem = findNodeForFileName(fileName, rootelem);
        if (foundElem != null) {
            ret = foundElem.getAttribute("id");
        }
        return ret;
    }
}
