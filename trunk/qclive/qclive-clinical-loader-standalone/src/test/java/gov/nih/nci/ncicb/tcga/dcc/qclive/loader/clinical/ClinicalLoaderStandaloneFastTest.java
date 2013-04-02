/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;

/**
 * Test class for ClinicalLoaderStandalone
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ClinicalLoaderStandaloneFastTest {
	
	private static final String TEST_APPLICATON_CONTEXT = "standalone.clinical.applicationContext-test.xml";
	
	
	@Test
    public void testInit()  {
        ClinicalLoaderStandalone loader = new ClinicalLoaderStandalone();
        loader.setApplicationContextFile(TEST_APPLICATON_CONTEXT);
        try{
        	loader.init();
        }catch(LoaderException e){
        	Assert.fail();
        }
        
    }
	
	@Test (expected=BeanDefinitionStoreException.class)
    public void testInitBadConfigFile() throws LoaderException {
        ClinicalLoaderStandalone loader = new ClinicalLoaderStandalone();
        loader.setApplicationContextFile(TEST_APPLICATON_CONTEXT + "_BAD");
        loader.init();        
    }
	
	@Test 
	public void testCommandLineArguments() {
		try{
	        ClinicalLoaderStandalone loader = new ClinicalLoaderStandalone();
	        String[] archivesToLoad = {"test_archive","test_archive2","test_archive3"};
	        loader.main(archivesToLoad); 
	        Assert.fail();
		}catch (IllegalStateException e){
			// eat it
		}catch (BeanDefinitionStoreException e){
			// eat it as well
		}               
    }
}
