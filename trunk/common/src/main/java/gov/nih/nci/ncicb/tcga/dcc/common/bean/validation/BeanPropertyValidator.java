/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation;

/**
 * Interface that defines the behavior of validator implementations used mainly
 * by JSR-303 {@link javax.validation.ConstraintValidator}s.
 * 
 * @author nichollsmc
 * Last updated by: nichollsmc
 */
public interface BeanPropertyValidator {

    /**
     * Validates the provided element, and returns a boolean indicating whether
     * or not the element passed validation.
     * 
     * @param element
     *            - the element to validate
     * @return true if the element is valid, false otherwise
     */
    public boolean validate(String element);

    /**
     * Returns the message property key used to retrieve the constraint
     * violation message that corresponds to this validator implementation.
     * 
     * <p>
     * The message property key returned by this method should correspond to one
     * of the property keys defined in the
     * <code>ValidatorMessages.properties</code> at the root of the classpath
     * (i.e. src/main/resources/ValidatorMessages.properties).
     * 
     * @return - the message property key
     */
    public String getMessagePropertyKey();
}
