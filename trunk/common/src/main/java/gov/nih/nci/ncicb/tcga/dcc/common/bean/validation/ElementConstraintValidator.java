/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.DispatcherValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * JSR-303 custom {@link ConstraintValidator}. This validator fulfills the
 * implementation of the custom JSR-303 constraint annotation
 * {@link AssertElement}, and can be used with any of the validators defined in
 * the {@link AssertionType} enum.
 *
 * @author nichollsmc
 *         Last updated by: nichollsmc
 */
@Component
public class ElementConstraintValidator implements ConstraintValidator<AssertElement, String> {

    private BeanPropertyValidator validator;

    private String delimiter;

    @Autowired
    private DispatcherValidator dispatcherValidator;

    @Override
    public void initialize(AssertElement constraintAnnotation) {
        this.validator = dispatcherValidator.getInjectedValidator(
                constraintAnnotation.value().getValidator());
        this.delimiter = constraintAnnotation.delimiter();
    }

    public void setDispatcherValidator(DispatcherValidator dispatcherValidator) {
        this.dispatcherValidator = dispatcherValidator;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {

        // Parse the value string for the list of elements to validate
        List<String> elements = asList(value);

        // Return true if the list of elements is empty (the value string
        // contained nothing but whitespace or was null)
        if (elements.isEmpty()) {
            return true;
        }

        // Validate the element(s). If the element(s) fail validation, get the
        // message property key from the validator that
        // will be used to format the constraint violation message and return
        // false
        for (String element : elements) {
            if (!validator.validate(element)) {
                constraintContext.disableDefaultConstraintViolation();
                constraintContext.buildConstraintViolationWithTemplate(
                        validator.getMessagePropertyKey()).addConstraintViolation();
                return false;
            }
        }

        // All elements passed validation
        return true;
    }

    /**
     * Parses a value string into a list of strings. Uses the default string
     * {@link gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants#SEPARATOR} as
     * the delimiter.
     *
     * @param value - the value string to be parsed into a list of strings
     * @return list of strings, if the value string is null an empty list will
     *         be returned
     */
    private List<String> asList(String value) {
        return asList(value, delimiter);
    }

    /**
     * Parses a value string into a list of strings. Uses the default string
     * {@link gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants#SEPARATOR} as
     * the delimiter.
     * <p/>
     * <p/>
     * Provides the same behavior as
     * {@link ElementConstraintValidator#asList(String)} but allows a delimiter
     * string to be specified.
     *
     * @param value     - the value string to be parsed into a list of strings
     * @param delimiter - the delimiter used to split the value string into separate
     *                  elements
     * @return list of strings, if the value string is null an empty list will
     *         be returned
     */
    private List<String> asList(String value, String delimiter) {

        if (value == null)
            return new ArrayList<String>();

        if (delimiter == null || delimiter.trim().isEmpty()) {
            throw new IllegalArgumentException("Delimiter '" + delimiter
                    + "' cannot be blank or empty");
        }

        final String[] elements = value.split(delimiter);
        final List<String> scrubbedElements = new ArrayList<String>();
        if (elements.length > 0) {
            String tmp = null;
            for (String element : elements) {
                tmp = element.trim();
                if (!tmp.isEmpty()) {
                    scrubbedElements.add(tmp);
                }
            }
        }

        return scrubbedElements;
    }
}
