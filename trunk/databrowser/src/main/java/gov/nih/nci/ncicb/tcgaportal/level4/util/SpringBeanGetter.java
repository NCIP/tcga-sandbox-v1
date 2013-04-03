/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Singleton. Instantiates Spring framework in order to use spring xml-defined classes.
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class SpringBeanGetter {

    static SpringBeanGetter instance;

    public static synchronized SpringBeanGetter getInstance(String appcontextPath) throws BeansException {
        if (!appcontextPath.startsWith("/")) {  //(probably not needed)
            appcontextPath = "/" + appcontextPath;
        }
        appcontextPath = "file:" + appcontextPath;
        if (instance == null) {
            instance = new SpringBeanGetter(appcontextPath);
        }
        return instance;
    }

    private ApplicationContext appctxt;

    //ctor instantiates Spring framework. Should only happen once per server session
    private SpringBeanGetter(String appcontextPath) throws BeansException {
        appctxt = new FileSystemXmlApplicationContext(appcontextPath);
    }

    /**
     * Returns a spring bean identified by name
     *
     * @param name
     * @return
     */
    public Object getBean(String name) {
        return appctxt.getBean(name);
    }

}