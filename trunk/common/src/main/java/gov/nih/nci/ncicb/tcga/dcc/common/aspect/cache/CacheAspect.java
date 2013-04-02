/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.annotation.Resource;


/**
 * The Aspect that wraps around any method annotated @Cached and provides caching.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Aspect
public class CacheAspect {
    private static final Object refreshDataLock = new Object();
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name="beanCache")
    private Cache cache;

    // Pointcut for all methods annotated with @Cached

    @Pointcut ("execution(@gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached * *.*(..))")
    private void cache() {}

    // Pointcut for all methods annotated with @ClearCache

    @Pointcut ("execution(@gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.ClearCache * *.*(..))")
    private void clearCache() {}

    @Around ("cache()")
    public Object aroundCachedMethods(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        // generate the key under which cached value is stored
        // will look like package.class.method(arg1=val1;arg2=val2;)
        StringBuilder keyBuff = new StringBuilder();

        // append name of the class
        keyBuff.append(thisJoinPoint.getTarget().getClass().getName());
        // append name of the method
        keyBuff.append(".").append(thisJoinPoint.getSignature().getName());
        keyBuff.append("(");
        // find method arguments
        for (final Object arg : thisJoinPoint.getArgs()) {
            // append argument type and value
            keyBuff.append(arg.getClass().getSimpleName() + "=" + arg + ";");
        }
        keyBuff.append(")");
        String key = keyBuff.toString();
        Element element = cache.get(key);
        if (element == null) {
            logger.info("["+Thread.currentThread().getId()+ "] Result not yet cached for "+key+"  let's proceed");
            // Synchronizing calls to only getUUIDRows API. Because calling multiple getUUIDRows  at the same time
            // might take more memory
            if(key.contains("UUIDBrowserDAOImpl.getUUIDRows()")){
                synchronized (refreshDataLock){
                    // doing this one more time so that the threads that are waiting in this lock, will
                    // use the cache instead of db
                    element = cache.get(key);
                    if(element == null) {
                        element = storeDataInCache(thisJoinPoint,key);
                    }
                }
            }else{
                element =storeDataInCache(thisJoinPoint,key);
            }
        }
        return (element != null) ? element.getValue() : null;
    }

    private Element storeDataInCache(final ProceedingJoinPoint thisJoinPoint, final String key) throws Throwable{
        Element element = null;
        Object result = thisJoinPoint.proceed();
        if (result != null) {
            logger.info("["+Thread.currentThread().getId()+ "] Save result to cache");
            element = new Element(key, result);
            cache.put(element);
        }
        return element;
    }


    @After ("clearCache()")
    public void afterClearCache() {
        logger.debug("Clear all cache");
        cache.removeAll();
    }

}//End of class
