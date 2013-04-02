/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import gov.nih.nci.ncicb.tcga.dccws.Archive;
import gov.nih.nci.ncicb.tcga.dccws.Center;
import gov.nih.nci.ncicb.tcga.dccws.FileInfo;
import gov.nih.nci.ncicb.tcga.dccws.Platform;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for <code>RemoteValidationHelperCaCoreImpl</code>
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemoteValidationHelperCaCoreImplFastTest {
	
    private final Mockery mockery = new JUnit4Mockery();
	private RemoteValidationHelperCaCoreImpl remoteValidationHelperCaCoreImpl;
	private ApplicationService mockApplicationService;
	
	@Before
	public void setUp() throws Exception {
		
		mockApplicationService = mockery.mock(ApplicationService.class);
		remoteValidationHelperCaCoreImpl = new RemoteValidationHelperCaCoreImpl(mockApplicationService);
	}
	
	@Test
	public void testGetCenterTypeForPlatformWhenCenterTypeNotFound() {
		
		try {
			final List<Platform> emptyPlatformList = new LinkedList<Platform>();
			
	        mockery.checking(new Expectations() {{
	            one(mockApplicationService).query(with(any(HQLCriteria.class)));
	            will(returnValue(emptyPlatformList));
	        }});
	        
			final String platformName = "TestPlatform";
			remoteValidationHelperCaCoreImpl.getCenterTypeForPlatform(platformName);
			fail("ApplicationException should have been raised");
			
		} catch (final ApplicationException e) {
			
			final String expectedErrorMessage = "Center type could not be determined. Please run the validator with the -centertype flag.";
			assertEquals("Unexpected error message", expectedErrorMessage, e.getMessage());
		}
	}

    @Test
    public void testGetCenterId() throws ApplicationException {

        final Center center1 = new Center();
        center1.setId(1);

        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(any(HQLCriteria.class)));
            will(returnValue(Arrays.asList(center1)));
        }});
        final Integer centerId = remoteValidationHelperCaCoreImpl.getCenterId("centerDomain", "BCR");
        assertEquals(new Integer(1), centerId);
    }

    @Test
    public void testGetCenterIdNotFound() throws ApplicationException {
        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(any(HQLCriteria.class)));
            will(returnValue(null));
        }});
        final Integer centerId = remoteValidationHelperCaCoreImpl.getCenterId("centerDomain", "BCR");
        assertNull(centerId);
    }

    @Test
    public void testProjectExists() throws ApplicationException {

        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.Project where id = ?", Arrays.asList("TCGA"))));
            will(returnValue(Arrays.asList("TCGA")));
        }});
        assertTrue(remoteValidationHelperCaCoreImpl.projectExists("TCGA"));
    }

    @Test
    public void testTssCodeExists() throws ApplicationException {
        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.TissueSourceSite where id = ?", Arrays.asList("AV"))));
            will(returnValue(Arrays.asList("This is a result which is not null so the tss code exists")));
        }});
        assertTrue(remoteValidationHelperCaCoreImpl.tssCodeExists("AV"));
    }

    @Test
    public void testSampleTypeExists() throws ApplicationException {
        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.SampleType where id = ?", Arrays.asList("11"))));
            will(returnValue(Arrays.asList("sample type object")));
        }});
        assertTrue(remoteValidationHelperCaCoreImpl.sampleTypeExists("11"));
    }

    @Test
    public void testPortionAnalyteExists() throws ApplicationException {
        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.PortionAnalyte where id = ?", Arrays.asList("D"))));
            will(returnValue(Arrays.asList("yes")));
        }});
        assertTrue(remoteValidationHelperCaCoreImpl.portionAnalyteExists("D"));
    }

    @Test
    public void testBcrCenterIdExists() throws ApplicationException {
         mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.BcrCenter where id = ?", Arrays.asList("08"))));
            will(returnValue(Arrays.asList("BCR Center 08")));
        }});
        assertTrue(remoteValidationHelperCaCoreImpl.bcrCenterIdExists("08"));
    }

    @Test
    public void testGetArchiveDataFiles() throws ApplicationException {
        final Archive archive = new Archive();
        archive.setId(1);

        final Collection<FileInfo> files = new ArrayList<FileInfo>();
        final FileInfo file1 = new FileInfo();
        file1.setName("file1");
        file1.setId(10);
        files.add(file1);
        final FileInfo file2 = new FileInfo();
        file2.setId(20);
        file2.setName("file2");
        files.add(file2);

        mockery.checking(new Expectations() {{
            one(mockApplicationService).query(with(expectedHQLCriteria("from gov.nih.nci.ncicb.tcga.dccws.FileInfo f where dataLevel > 0 and ? member of f.archiveCollection",
                    Arrays.asList(archive))));
            will(returnValue(files));
        }});

        final List<FileInfo> dataFiles = remoteValidationHelperCaCoreImpl.getArchiveDataFiles(archive);
        assertEquals(2, dataFiles.size());
        assertEquals(file1, dataFiles.get(0));
        assertEquals(file2, dataFiles.get(1));
    }

    private Matcher<HQLCriteria> expectedHQLCriteria(final String expectedQuery, final List expectedParameters) {
        return new TypeSafeMatcher<HQLCriteria>() {
            @Override
            public boolean matchesSafely(final HQLCriteria criteria) {
                return criteria.getHqlString().equals(expectedQuery) && criteria.getParameters().equals(expectedParameters);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("query " + expectedQuery + " with parameters " + expectedParameters.toString());
            }
        };
    }


}
