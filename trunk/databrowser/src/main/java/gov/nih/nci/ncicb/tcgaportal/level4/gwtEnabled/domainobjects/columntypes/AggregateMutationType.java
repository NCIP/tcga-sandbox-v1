/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Variant of MutationType that stands for "all mutation types in list"
 *
 * @author Jessica Chen
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class AggregateMutationType extends MutationType {
    private Collection<MutationType> mutationTypes;

    public Collection<MutationType> getMutationTypes() {
        return mutationTypes;
    }

    public void setMutationTypes(Collection<MutationType> mutationTypes) {
        this.mutationTypes = mutationTypes;
    }

    public void addMutationType(MutationType mutationType) {
        if (mutationTypes == null) {
            mutationTypes = new ArrayList<MutationType>();
        }
        mutationTypes.add(mutationType);
    }

    public String getDisplayCenter() {
        StringBuilder centers = new StringBuilder();
        Set<String> addedCenters = new HashSet<String>();
        for (MutationType type : mutationTypes) {
            if (!addedCenters.contains(type.getDisplayCenter())) {
                centers.append(type.getDisplayCenter()).append(" & ");
                addedCenters.add(type.getDisplayCenter());
            }
        }
        if (centers.length() > 0) {
            return centers.toString().substring(0, centers.lastIndexOf("&"));
        } else {
            return null;
        }
    }

    public String getDisplayPlatformType() {
        // should all be the same
        if (mutationTypes.size() > 0) {
            return mutationTypes.iterator().next().getDisplayPlatformType();
        } else {
            return null;
        }
    }

    public String getDisplayPlatform() {
        // mutations all have same platform (will this always be true?)
        if (mutationTypes.size() > 0) {
            return mutationTypes.iterator().next().getDisplayPlatform();
        } else {
            return null;
        }
    }

    public Object cloneColumn() {
        AggregateMutationType column = (AggregateMutationType) super.cloneColumn();
        for (MutationType mutationType : getMutationTypes()) {
            MutationType type = (MutationType) mutationType.cloneColumn();
            column.addMutationType(type);
        }
        return column;
    }

    protected ColumnType instanceForClone() {
        return new AggregateMutationType();
    }
}
