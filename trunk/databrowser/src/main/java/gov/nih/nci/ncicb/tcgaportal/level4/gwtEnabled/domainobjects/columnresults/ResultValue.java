/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
   Abstract superclass for all result values returned to the client.
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public abstract class ResultValue implements Serializable {

    //value can be anything as long as serializable. String, Integer, Boolean etc are ok.
    private Map<String, Serializable> valueAnnotation;
    protected Results resultParent;

    public ResultValue() {
    }

    /**
     * Store a reference to the parent object, so we can look up display flags set by the client
     * which determines what toString() returns
     *
     * @param resultParent
     */
    public void setResultParent(Results resultParent) {
        this.resultParent = resultParent;
    }

    /**
     * Returns a value annotation
     *
     * @param key
     * @return
     */
    public Serializable getValueAnnotation(String key) {
        Serializable ret = null;
        if (valueAnnotation != null) {
            ret = valueAnnotation.get(key);
        }
        return ret;
    }

    /**
     * Returns all value-annotations as a map. If none were set, returns a null.
     *
     * @return
     */
    public Map<String, Serializable> getValueAnnotations() {
        return valueAnnotation;
    }

    /**
     * Sets all value-annotations
     *
     * @param valueAnnotation
     */
    public void setValueAnnotations(Map<String, Serializable> valueAnnotation) {
        this.valueAnnotation = valueAnnotation;
    }

    /**
     * Adds a single value-annotation.
     *
     * @param key
     * @param value
     */
    public void addValueAnnotation(String key, Serializable value) {
        if (this.valueAnnotation == null) {
            valueAnnotation = new HashMap<String, Serializable>();
        }
        valueAnnotation.put(key, value);
    }

    /**
     * Returns a sortable value, which must be either a Comparable or a Number.
     * This is necessary because some subclasses (e.g. AnomalyResultRatio) do not have a single return
     * value, but several return values. Using this method, we can designate a single value as the
     * one on which sorting will work.
     *
     * @return
     */
    public abstract Object getSortableValue();
}
