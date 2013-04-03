/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Superclass for both anomaly filter and pathway filter
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
//todo  many of the enum values should have defaults - would make testing easier
public class FilterSpecifier implements IsSerializable {

    public enum GeneListOptions {
        All, Region, List
    }

    public enum PatientListOptions {
        All, List
    }

    public enum ListBy {
        Genes("genes"),
        Patients("patients"),
        Pathways("pathways");

        private String stringValue;

        ListBy(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    public ListBy getListBy(String listByParam) {
        for (ListBy listBy : ListBy.values()) {
            if ((listBy.getStringValue()).equalsIgnoreCase(listByParam)) {
                return listBy;
            }
        }
        return null;
    }

    protected ListBy listBy;
    protected List<ColumnType> ctypes;
    protected String patientList;
    protected String geneList;
    protected String disease;
    protected List<FilterChromRegion> chromRegions;
    protected GeneListOptions geneListOptions;
    protected PatientListOptions patientListOptions;

    public FilterSpecifier() {
        chromRegions = new ArrayList<FilterChromRegion>();
    }

    //copy ctor
    public FilterSpecifier(FilterSpecifier orig) {
        listBy = orig.listBy;
        patientList = orig.patientList;
        geneList = orig.geneList;
        disease = orig.disease;
        geneListOptions = orig.geneListOptions;
        patientListOptions = orig.patientListOptions;

        //deep clone arrays 
        ctypes = new ArrayList<ColumnType>(orig.ctypes.size());
        for (ColumnType ctype : orig.ctypes) {
            ctypes.add(ctype);
        }

        chromRegions = new ArrayList<FilterChromRegion>(orig.chromRegions.size());
        for (FilterChromRegion fcr : orig.chromRegions) {
            chromRegions.add(fcr);
        }
    }

    public ListBy getListBy() {
        return listBy;
    }

    public void setListBy(ListBy listBy) {
        this.listBy = listBy;
    }

    public List<ColumnType> getColumnTypes() {
        return ctypes;
    }

    public List<ColumnType> getPickedColumns() {
        List<ColumnType> picked = new ArrayList<ColumnType>();
        for (ColumnType ctype : ctypes) {
            if (ctype.isPicked()) {
                picked.add(ctype);
            }
        }
        return picked;
    }

    public void setColumnTypes(List<ColumnType> ctypes) {
        this.ctypes = ctypes;
    }

    public List<ColumnType> getCtypes() {
        return ctypes;
    }

    public void setCtypes(List<ColumnType> ctypes) {
        this.ctypes = ctypes;
    }

    public PatientListOptions getPatientListOptions() {
        return patientListOptions;
    }

    public void setPatientListOptions(PatientListOptions patientListOptions) {
        this.patientListOptions = patientListOptions;
    }

    public String getPatientList() {
        return patientList;
    }

    public void setPatientList(String patientList) {
        this.patientList = patientList;
    }

    public GeneListOptions getGeneListOptions() {
        return geneListOptions;
    }

    public void setGeneListOptions(GeneListOptions geneListOptions) {
        this.geneListOptions = geneListOptions;
    }

    public String getGeneList() {
        return geneList;
    }

    public void setGeneList(String geneList) {
        this.geneList = geneList;
    }

    public List<FilterChromRegion> getChromRegions() {
        return chromRegions;
    }

    public void addChromRegion(FilterChromRegion region) {
        chromRegions.add(region);
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
