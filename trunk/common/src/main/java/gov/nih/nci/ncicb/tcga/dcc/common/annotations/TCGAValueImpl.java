/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.annotations;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Class that defines the usage and configuration of the custom TCGAValue Annotation
 * it extends from the spring class PropertyPlaceholderConfigurer
 * which is used to setup a properties files in java
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAValueImpl extends PropertyPlaceholderConfigurer {

    private final static Logger log = Logger.getLogger(TCGAValueImpl.class);

    //Extending PropertyPlaceholderConfigurer from spring and overriding the processProperties
    // method to bind the TCGAValue annotation member :
    //key and defaultValue to the proper placeholder resolver of a properties (coming from a properties file)
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties)
            throws BeansException {
        super.processProperties(beanFactory, properties);

        for (String name : beanFactory.getBeanDefinitionNames()) {
            MutablePropertyValues mpv = beanFactory.getBeanDefinition(name).getPropertyValues();
            Class clazz = beanFactory.getType(name);
            log.debug("Configuring properties for annotations=" + name + "[" + clazz + "]");
            if (clazz != null) {
                //Method level annotation behavior
                for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(clazz)) {
                    Method setter = property.getWriteMethod();
                    Method getter = property.getReadMethod();
                    TCGAValue annotation = null;
                    if (setter != null && setter.isAnnotationPresent(TCGAValue.class)) {
                        annotation = setter.getAnnotation(TCGAValue.class);
                    } else if (setter != null && getter != null && getter.isAnnotationPresent(TCGAValue.class)) {
                        annotation = getter.getAnnotation(TCGAValue.class);
                    }
                    if (annotation != null) {
                        String value = resolvePlaceholder(annotation.key(), properties,
                                SYSTEM_PROPERTIES_MODE_FALLBACK);
                        processProperty(mpv, clazz, property, annotation, value);
                    }
                }
                //Field level annotation behavior
                for (Field field : clazz.getDeclaredFields()) {
                    log.debug("examining field=[" + clazz.getName() + "." + field.getName() + "]");
                    if (field.isAnnotationPresent(TCGAValue.class)) {
                        TCGAValue annotation = field.getAnnotation(TCGAValue.class);
                        PropertyDescriptor property = BeanUtils.getPropertyDescriptor(clazz, field.getName());

                        if (property== null) {
                             throw new InvalidPropertyException(clazz,field.getName(),"No Setter defined");
                        }
                        String value = resolvePlaceholder(annotation.key(), properties,
                                SYSTEM_PROPERTIES_MODE_FALLBACK);
                        processProperty(mpv, clazz, property, annotation, value);
                    }
                }
            }
        }
    }

    private void processProperty(
            final MutablePropertyValues mpv, final Class clazz, final PropertyDescriptor property,
            final TCGAValue annotation, String value) {
        if (value == null || "".equals(value)) {
            value = annotation.defaultValue();
        }
        mpv.addPropertyValue(property.getName(), value);
    }

}//End of class
