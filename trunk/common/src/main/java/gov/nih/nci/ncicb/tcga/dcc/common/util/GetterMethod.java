/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * very small class that have one method which gives the java.reflect.Method of a getter of a bean
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class GetterMethod {

    public static Method getGetter(Class bean,String property) throws NoSuchMethodException {
       return bean.getMethod("get" + StringUtils.capitalize(property));
    }

}//End of Class
