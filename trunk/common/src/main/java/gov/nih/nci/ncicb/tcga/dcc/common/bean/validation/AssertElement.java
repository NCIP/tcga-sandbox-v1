/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * JSR-303 custom constraint annotation can be used to mark delimited string
 * field and method variables of a bean for validation. The specific validator
 * type must be specified in the declaration of this annotation and can only be
 * one of the validator types defined by {@link AssertionType}. For example:
 * 
 * <blockquote>
 * 
 * <pre>
 * &#064AssertElement(AssertionType.INTEGER)
 * private String integerString;
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * The <code>AssertionType.INTEGER</code> in this example tells the custom
 * validator implementation {@link ElementConstraintValidator} to assert that
 * each delimited value for <tt>integerString</tt> resolves to an integer.
 * 
 * <p>
 * The default delimiter used to parse delimited strings is a comma. However,
 * this can be overridden by using the <tt>delimiter</tt> parameter:
 * 
 * <blockquote>
 * 
 * <pre>
 * &#064AssertElement(AssertionType.INTEGER, delimiter=":")
 * private String integerString;
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * @author nichollsmc 
 * Last updated by: nichollsmc
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ElementConstraintValidator.class)
@Documented
public @interface AssertElement {

    String message() default "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertElement.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    AssertionType value();

    String delimiter() default ",";
}
