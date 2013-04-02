/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * Represents a disease (tumor type) whose data can be displayed in the DAM.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Disease implements Comparable<Disease> {

    private final String abbreviation;
    private final String name;
    private final boolean isActive;

    /**
     * Constructor takes all variables.
     *
     * @param name         the disease full name
     * @param abbreviation the disease abbreviation
     * @param active       is this disease ready for display in the DAM?
     */
    public Disease(final String name, final String abbreviation, final boolean active) {
        this.abbreviation = abbreviation;
        this.isActive = active;
        this.name = name;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return new StringBuilder(getAbbreviation())
                .append(" - ")
                .append(getName())
                .toString();
    }

    @Override
    public int hashCode() {
        return 31 * abbreviation.hashCode() + name.hashCode() + ((isActive) ? 1 : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Disease)) {
            return false;
        }
        final Disease objectToBeCompared = (Disease) object;
        return ((this.abbreviation != null) ? (objectToBeCompared.getAbbreviation() != null) && this.abbreviation.equals(objectToBeCompared.getAbbreviation()) : (objectToBeCompared.getAbbreviation() == null)) &&
                ((this.name != null) ? (objectToBeCompared.getName() != null) && this.name.equals(objectToBeCompared.getName()) : (objectToBeCompared.getName() == null)) &&
                (isActive == objectToBeCompared.isActive());
    }


    public int compareTo(Disease objectToBeCompared) {
        return getAbbreviation().compareTo(objectToBeCompared.getAbbreviation());
    }


}

