/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtilsI;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for FilePackager.
 *
 * @author David Nassau
 */
@RunWith(JMock.class)
public class FilePackagerFastTest {
    private Mockery context = new JUnit4Mockery();

    private static final int HOWMANYFILES = 5;
    private static final String TEST_DOWNLOADFILE = "testdownload.txt";
    private static final String SAMPLE_INFO = "mock_sampleinfo.txt";
    private static final String DISEASE = "GBM";
    private static final String THIS_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/filePackager").getPath() + File.separator;
    private static final String SCRATCH_FOLDER = THIS_FOLDER + "scratch/";
    private List<DataFile> downloadFiles;
    private FilePackagerFactory fpFactory;
    private FilePackager packager;
    private static final long EXISTING_CACHE_FILE_SIZE = 1000000;
    private static final long MISSING_CACHE_FILE_SIZE = 3000000;
    private static final long LEVEL_3_FILE_SIZE = 5000;
    private static final long PERMANENT_FILE_SIZE = 200000;

    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String appContextFile = "samples/applicationContext-unittest.xml";
    private static ApplicationContext appContext;
    private MailErrorHelper mockMailHelper = context.mock(MailErrorHelper.class);
    private DAMUtilsI mockDamUtilsI = context.mock(DAMUtilsI.class);

    @BeforeClass
    public static void oneTimeSetUp() {
        appContext = new ClassPathXmlApplicationContext(appContextFile);
    }

    @Before
    public void setUp() throws Exception {
        loadFileInfoLists();
        //initialize mock DAO with test file info

        final DataAccessMatrixQueries damQueries = new DataAccessMatrixQueriesMockImpl(THIS_FOLDER + SAMPLE_INFO);
        ((DataAccessMatrixQueriesMockImpl) damQueries).setTestDownloadFile(THIS_FOLDER + TEST_DOWNLOADFILE);
        ((DataAccessMatrixQueriesMockImpl) damQueries).setDataFilePath(SCRATCH_FOLDER);
        ((DataAccessMatrixQueriesMockImpl) damQueries).setDamUtils(mockDamUtilsI);
        fpFactory = (FilePackagerFactory) appContext.getBean("filePackagerFactory");
        fpFactory.setNotProtectedArchiveLogicalPath(SCRATCH_FOLDER);
        fpFactory.setNotProtectedArchivePhysicalPath(SCRATCH_FOLDER);
        fpFactory.setProtectedArchiveLogicalPath(SCRATCH_FOLDER);
        fpFactory.setProtectedArchivePhysicalPath(SCRATCH_FOLDER);
        fpFactory.setErrorMailSender(mockMailHelper);
        packager = (FilePackager) appContext.getBean("filePackager");
        packager.setDataAccessMatrixQueries(damQueries);
        packager.setTempfileDirectory(SCRATCH_FOLDER);
    }

    private void loadFileInfoLists() {
        emptyScratchFolder();
        downloadFiles = new ArrayList<DataFile>();
        for (int i = 0; i < HOWMANYFILES; i++) {
            DataFile fi = new DataFileLevelOne();
            fi.setFileId("f" + i);
            fi.setFileName("file" + i);
            fi.setProtected(false);
            fi.setPlatformTypeId("platform" + i);
            fi.setCenterId("center" + i);
            fi.setBarcodes(new ArrayList<String>());
            downloadFiles.add(fi);
        }
    }

    private List<DataFile> getLevel2Files() {
        emptyScratchFolder();
        final List<DataFile> level2Files = new ArrayList<DataFile>();
        for (int i = 0; i < HOWMANYFILES; i++) {
            DataFile level2File = new DataFileLevelTwo();
            level2File.setFileId("f" + i);
            level2File.setFileName("level2_file" + i);
            level2File.setProtected(false);
            level2File.setPlatformTypeId("platform" + i);
            level2File.setCenterId("center" + i);
            level2File.setBarcodes(new ArrayList<String>());
            level2Files.add(level2File);
        }
        // set the first file to be cached file
        DataFile cachedFile = level2Files.get(0);
        cachedFile.setPermanentFile(true);
        cachedFile.setPath(SCRATCH_FOLDER + cachedFile.getFileName());
        return level2Files;
    }

    private void emptyScratchFolder() {
        File scratch = new File(SCRATCH_FOLDER);
        if (!scratch.exists()) {
            //noinspection ResultOfMethodCallIgnored
            scratch.mkdir();
        }
        File[] existingFiles = scratch.listFiles();
        for (final File existingFile : existingFiles) {
            //noinspection ResultOfMethodCallIgnored
            existingFile.delete();
        }
    }

    @Test
    public void testFilePackager() throws Exception {
        final FilePackagerBean packagerBean = fpFactory.createFilePackagerBean(DISEASE, downloadFiles, null, false, false,
                UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), null);
        //packager.setArchiveLinkSite("http://localhost:8080/tcga1");  //awaiting merge from 2.0_bugfix
        //skip the queuing part for now - have to hook up quartz in the unit test
        packager.runJob(packagerBean);
        assertTrue(packagerBean.isDone());
        assertFalse(packagerBean.isFailed());
        assertTrue(packagerBean.getLinkText().endsWith(packagerBean.getArchiveLogicalName() + ".tar.gz"));
        File fzip = new File(packagerBean.getArchivePhysicalName() + ".tar.gz");
        assertTrue(fzip.exists());
        checkGzip(packagerBean.getArchivePhysicalName() + ".tar.gz");
    }

    @Test
    public void cacheFileNotExists() throws Exception {
        final FilePackagerBean packagerBean = fpFactory.createFilePackagerBean(DISEASE, getLevel2Files(), null, false, false,
                UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), null);

        packager.runJob(packagerBean);
        emptyScratchFolder();
    }

    private void checkGzip(final String gzipName) throws IOException {

        InputStream in = null;
        OutputStream out = null;

        try {
            File f_in = new File(gzipName);
            assertTrue(f_in.exists());
            String tarName = gzipName.substring(0, gzipName.lastIndexOf('.'));
            File f_out = new File(tarName);
            if (f_out.exists()) {
                //noinspection ResultOfMethodCallIgnored
                f_out.delete();
            }
            //noinspection IOResourceOpenedButNotSafelyClosed
            in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(f_in)));
            //noinspection IOResourceOpenedButNotSafelyClosed
            out = new BufferedOutputStream(new FileOutputStream(f_out));
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            out.flush();
            File f_tar = new File(tarName);
            assertTrue(f_tar.exists());
            checkTar(f_tar);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    private void checkTar(final File f_tar) throws IOException {

        FileReader origReader = null;
        TarArchiveInputStream tarIn = null;

        try {
            //we're going to open each of the included files in turn and compare to our tiny
            //original input file.  So first, need to read the original file into a string.
            StringBuilder origBuf = new StringBuilder();
            char[] cbuf = new char[1024];
            //noinspection IOResourceOpenedButNotSafelyClosed
            origReader = new FileReader(THIS_FOLDER + TEST_DOWNLOADFILE);
            int iread;
            while ((iread = origReader.read(cbuf)) != -1) {
                for (int i = 0; i < iread; i++) {
                    origBuf.append(cbuf[i]);
                }
            }
            String origText = origBuf.toString();
            //noinspection IOResourceOpenedButNotSafelyClosed
            tarIn = new TarArchiveInputStream(new FileInputStream(f_tar));
            TarArchiveEntry entry;
            int i = 0;
            entry = tarIn.getNextTarEntry();
            assertEquals("file_manifest.txt", entry.getName());
            while ((entry = tarIn.getNextTarEntry()) != null) {
                //compare to input file
                File expectedName = new File("platform" + i + "/center" + i + "/Level_1/f" + i + ".idat");
                assertEquals(expectedName, new File(entry.getName()));
                byte[] content = new byte[2056];
                OutputStream byteOut = new ByteArrayOutputStream(2056);
                //noinspection ResultOfMethodCallIgnored
                tarIn.read(content);
                byteOut.write(content);
                byteOut.close();
                assertEquals(origText, byteOut.toString().trim());
                i++;
            }
            assertEquals(i, HOWMANYFILES);
        } finally {
            IOUtils.closeQuietly(origReader);
            IOUtils.closeQuietly(tarIn);
        }
    }

    @Test
    public void testBigFileMode() throws IOException {
        final File tempTarFile = new File(THIS_FOLDER + "/fakeBigTar.tar.gz");
        try {
            TarArchiveOutputStream tarArchiveOutputStream = packager.makeTarGzOutputStream(tempTarFile);
            TarArchiveEntry fakeEntry = new TarArchiveEntry("test");
            fakeEntry.setSize(9999999999L);
            tarArchiveOutputStream.putArchiveEntry(fakeEntry);

            // if the TarArchiveOutputStream cannot handle entries of this size, it will fail.
            // so if it doesn't then the test passes.  I can't think of anything to assert...

        } finally {
            // delete temp tar file made during test
            tempTarFile.deleteOnExit();
        }
    }

    //test 100 character limit

    @Test
    public void testLongFileNames() throws IOException {
        List<DataFile> files = new ArrayList<DataFile>();
        DataFile file = new DataFileLevelOne(); //new DataFile();
        file.setFileId("f1");
        file.setFileName("123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_");
        file.setProtected(false);
        file.setPlatformTypeId("platform1");
        file.setCenterId("center1");
        file.setPath(THIS_FOLDER + "dummyDataFile.txt");
        files.add(file);
        final FilePackagerBean packagerBean = fpFactory.createFilePackagerBean(DISEASE, files, null, false, false,
                UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), null);
        packager.setFilePackagerBean(packagerBean);
        packager.setManifestTempfileName(THIS_FOLDER + "dummyManifest.txt");
        packager.makeArchive();
    }

    @Test
    public void testSendMail() throws Exception {
        final MailSender mockMailSender = context.mock(MailSender.class);
        context.checking(new Expectations() {{
            one(mockMailSender).send(with("fake"), with(any(String.class)), with("Download Available"), with(expectedMessageBody(1)), with(false));

        }});
        packager.setMailSender(mockMailSender);
        final List<DataFile> files = new ArrayList<DataFile>();
        final DataFile file = new DataFileLevelTwo();
        file.setFileId("f1");
        file.setFileName("squirrel");
        file.setProtected(false);
        file.setPlatformTypeId("platform1");
        file.setCenterId("center1");
        file.setPath(THIS_FOLDER + "dummyDataFile.txt");
        files.add(file);
        final FilePackagerBean packagerBean = fpFactory.createFilePackagerBean(DISEASE, files, null, false, false,
                UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), null);
        packagerBean.setEmail("fake");
        packager.runJob(packagerBean);
        assertTrue(packagerBean.isDone());
        assertFalse(packagerBean.isFailed());
    }

    private Matcher<String> expectedMessageBody(final int hours) {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.startsWith("The archive you created is available at ") &&
                        s.contains("It will be available for download for " + hours +
                                " hours, after which it will be deleted from our servers.");
            }

            public void describeTo(final Description description) {
                description.appendText("message body has right format, and " + hours + " hours to deletion listed");

            }
        };
    }

    @Test
    public void testOddballCharacterFilenames() throws Exception {
        final MailSender mockMailSender = context.mock(MailSender.class);
        context.checking(new Expectations() {{
            one(mockMailSender).send("dummy@dum.dum", null, "DAM: filename corrected", "Strange characters in filename.\n" +
                    "\"this is/a bad\\file name\" was changed to \"this_is_a_bad_file_name\"", false);
        }});
        packager.setMailSender(mockMailSender);
        packager.setFailEmail("dummy@dum.dum");
        final List<DataFile> files = new ArrayList<DataFile>();
        DataFile file = new DataFileLevelTwo();
        file.setFileId("f1");
        file.setFileName("this is/a bad\\file name");
        file.setProtected(false);
        file.setPlatformTypeId("platform1");
        file.setCenterId("center1");
        file.setPath(THIS_FOLDER + "dummyDataFile.txt");
        files.add(file);
        file = new DataFileLevelTwo();
        file.setFileId("f2");
        file.setFileName("this_is_a_good_file_name");
        file.setProtected(false);
        file.setPlatformTypeId("platform1");
        file.setCenterId("center1");
        file.setPath(THIS_FOLDER + "dummyDataFile.txt");
        files.add(file);
        final FilePackagerBean packagerBean = fpFactory.createFilePackagerBean(DISEASE, files, null, false, false,
                UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), null);
        packager.runJob(packagerBean);
        assertTrue(packagerBean.isDone());
        assertFalse(packagerBean.isFailed());
    }

    @Test
    public void testSetSelectedFiles() {
        final List<DataFile> selectedFiles = new ArrayList<DataFile>();

        // file 1 is a cache file that is already generated (exists)
        final DataFile existingCacheFile = new DataFileLevelTwo();
        existingCacheFile.setPath(TEST_DATA_FOLDER + "portal"
                + File.separator + "filePackager"
                + File.separator + "existingCacheFile.txt");
        existingCacheFile.setPermanentFile(true);
        existingCacheFile.setSize(EXISTING_CACHE_FILE_SIZE);

        // file 2 is a cache file that doesn't exist yet
        final DataFile missingCacheFile = new DataFileLevelTwo();
        missingCacheFile.setPath("thisDoesNotExist");
        missingCacheFile.setPermanentFile(true);
        missingCacheFile.setSize(MISSING_CACHE_FILE_SIZE);

        // file 3 has to be generated (not a cache file)
        final DataFile fileToGenerate = new DataFileLevelThree();
        fileToGenerate.setPermanentFile(false);
        fileToGenerate.setSize(LEVEL_3_FILE_SIZE);

        // file 4 is a permanent file
        final DataFile permanentFile = new DataFileLevelOne();
        permanentFile.setPermanentFile(true);
        permanentFile.setSize(PERMANENT_FILE_SIZE);

        selectedFiles.add(existingCacheFile);
        selectedFiles.add(missingCacheFile);
        selectedFiles.add(fileToGenerate);
        selectedFiles.add(permanentFile);

        final FilePackagerBean packagerBean = new FilePackagerBean();
        packagerBean.setSelectedFiles(selectedFiles);

        // missing cache file should have cache file to generate set
        assertEquals("thisDoesNotExist", missingCacheFile.getCacheFileToGenerate());
        assertNull(missingCacheFile.getPath());
        assertFalse(missingCacheFile.isPermanentFile());

        assertEquals(EXISTING_CACHE_FILE_SIZE + MISSING_CACHE_FILE_SIZE + LEVEL_3_FILE_SIZE + PERMANENT_FILE_SIZE,
                packagerBean.getEstimatedUncompressedSize());
        // priority adjusted size will exclude the existing cache file and the permanent file
        assertEquals(MISSING_CACHE_FILE_SIZE + LEVEL_3_FILE_SIZE,
                packagerBean.getPriorityAdjustedEstimatedUncompressedSize());
    }
}
