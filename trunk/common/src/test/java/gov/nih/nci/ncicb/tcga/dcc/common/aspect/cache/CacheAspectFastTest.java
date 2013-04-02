/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Test class to test the Cache Aspect and Annotations
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class CacheAspectFastTest extends AbstractDependencyInjectionSpringContextTests {

    private SlowService service;

    private Cache cache;
 
    public void setCache(final Cache cache) {
        this.cache = cache;
    }

    public void setService(final SlowService service) {
        this.service = service;
    }


    public void testCacheTest(){
       assertNotNull(service);
       assertEquals("Oh man! , I am a slow process",service.snailMethod());
       String key = "gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.SlowService.snailMethod()";
       Element el = cache.get(key); 
       assertNotNull(el);
       assertEquals("Oh man! , I am a slow process",el.getValue());
       assertEquals(3,service.getMyInt(1,2));
       assertEquals(11,service.getMyInt(5,6));
       String key1 = "gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.SlowService.getMyInt(Integer=1;Integer=2;)";
       String key2 = "gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.SlowService.getMyInt(Integer=5;Integer=6;)";
       Element el1 = cache.get(key1);
       Element el2 = cache.get(key2);
       assertEquals(3,el1.getValue());
       assertEquals(11,el2.getValue());
       service.byebyeCache();
       assertEquals(0,cache.getKeys().size()); 
    }


    //TODO: write some test metrics to be sure the second time we hit a method from the slowService we get the cache

    protected String[] getConfigLocations() {
        return new String[] { "classpath:gov/nih/nci/ncicb/tcga/dcc/common/" +
                "aspect/cache/applicationContext-test.xml" };
    }

}//End of Class
