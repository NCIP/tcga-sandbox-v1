/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation;

import java.util.Map;

import javax.validation.Configuration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.engine.ValidatorFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * JSR-303 custom {@link ConstraintValidatorFactory} implementation that can be
 * used in place of the default implementation {@link ValidatorFactoryImpl}
 * provided by Hibernate Validator.
 * 
 * <p>
 * This factory provides Spring dependency injection support when creating
 * instances of {@link ConstraintValidator}s that declare injectable resources.
 * 
 * @author nichollsmc
 * Last updated by: nichollsmc
 */
public class IOCConstraintValidatorFactory implements InitializingBean, ConstraintValidatorFactory,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ValidatorFactory validatorFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration<?> configuration = Validation.byDefaultProvider().configure();
        this.validatorFactory = configuration.constraintValidatorFactory(this).buildValidatorFactory();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        Map beansByNames = applicationContext.getBeansOfType(key);
        if (beansByNames.isEmpty()) {
            try {
                return key.newInstance();
            }
            catch (InstantiationException ie) {
                throw new RuntimeException("Could not instantiate constraint validator class '"+ key.getName() + "'", ie);
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException("Could not instantiate constraint validator class '" + key.getName() + "'", iae);
            }
        }
        if (beansByNames.size() > 1) {
            throw new RuntimeException("Only one bean of type '" + key.getName() + "' is allowed in the application context");
        }
        return (T) beansByNames.values().iterator().next();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @return an instance of {@link javax.validation.Validator} attached to
     *         instances of this factory
     */
    public Validator getValidator() {
        return this.validatorFactory.getValidator();
    }
}
