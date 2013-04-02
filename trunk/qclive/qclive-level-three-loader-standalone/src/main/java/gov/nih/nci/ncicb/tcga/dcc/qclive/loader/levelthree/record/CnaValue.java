/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.group.validation.GenomeWustlEdu;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.group.validation.HmsHarvardEdu;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.group.validation.HudsonAlphaOrg;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.group.validation.MskccOrg;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Bean that represents a record for the CNA_VALUE database table.
 * <p>
 * The properties of this bean are also annotated with JSR-303 constraint
 * annotations, allowing for bean validation if required.
 * 
 * @author nichollsmc
 */
public class CnaValue {

	@NotEmpty
	@Size(max = 50)
	private String  chromosome;

	@NotEmpty
	@Digits(integer = 38, fraction = 0)
	private String  chrStart;

	@NotEmpty
	@Digits(integer = 38, fraction = 0)
	private String  chrStop;

	@NotNull
	@Digits(integer = 38, fraction = 0)
	private Integer dataSetId;

	private Integer hybridizationRefId;

	@NotEmpty(groups = { GenomeWustlEdu.class, HudsonAlphaOrg.class, MskccOrg.class })
	private String  hybridizationRefName;

	@NotEmpty(groups = { GenomeWustlEdu.class, HmsHarvardEdu.class, MskccOrg.class })
	@Digits(integer = 7, fraction = 0)
	private String  numMark;

	@NotEmpty
	@Size(max = 50)
	private String  segMean;

	/**
	 * Convenience method for copying instances of {@link CnaValue}.
	 * 
	 * @param source
	 *            an instance of {@link CnaValue} to copy
	 */
	public CnaValue copy() {
        CnaValue copy = new CnaValue();
        PropertyDescriptor sourceDescriptor;
        PropertyDescriptor copyDescriptor;
        final Field[] fields = getClass().getDeclaredFields();
        for (final Field field : fields) {
        	try {
        		sourceDescriptor = new PropertyDescriptor(field.getName(), getClass());
        		copyDescriptor = new PropertyDescriptor(field.getName(), getClass());
        		copyDescriptor.getWriteMethod().invoke(copy, sourceDescriptor.getReadMethod().invoke(this));
        	}
        	catch (Exception e) {
        		throw new RuntimeException(e.getMessage());
        	}
        }

		return copy;
	}

	/**
	 * @return the chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}

	/**
	 * @param chromosome
	 *            the chromosome to set
	 */
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	/**
	 * @return the chrStart
	 */
	public String getChrStart() {
		return chrStart;
	}

	/**
	 * @param chrStart
	 *            the chrStart to set
	 */
	public void setChrStart(String chrStart) {
		this.chrStart = chrStart;
	}

	/**
	 * @return the chrStop
	 */
	public String getChrStop() {
		return chrStop;
	}

	/**
	 * @param chrStop
	 *            the chrStop to set
	 */
	public void setChrStop(String chrStop) {
		this.chrStop = chrStop;
	}

	/**
	 * @return the dataSetId
	 */
	public Integer getDataSetId() {
		return dataSetId;
	}

	/**
	 * @param dataSetId
	 *            the dataSetId to set
	 */
	public void setDataSetId(Integer dataSetId) {
		this.dataSetId = dataSetId;
	}

	/**
	 * @return the hybridizationRefId
	 */
	public Integer getHybridizationRefId() {
		return hybridizationRefId;
	}

	/**
	 * @param hybridizationRefId
	 *            the hybridizationRefId to set
	 */
	public void setHybridizationRefId(Integer hybridizationRefId) {
		this.hybridizationRefId = hybridizationRefId;
	}

	/**
	 * @return the hybridizationRefName
	 */
	public String getHybridizationRefName() {
		return hybridizationRefName;
	}

	/**
	 * @param hybridizationRefName
	 *            the hybridizationRefName to set
	 */
	public void setHybridizationRefName(String hybridizationRefName) {
		this.hybridizationRefName = hybridizationRefName;
	}

	/**
	 * @return the numMark
	 */
	public String getNumMark() {
		return numMark;
	}

	/**
	 * @param numMark
	 *            the numMark to set
	 */
	public void setNumMark(String numMark) {
		this.numMark = numMark;
	}

	/**
	 * @return the segMean
	 */
	public String getSegMean() {
		return segMean;
	}

	/**
	 * @param segMean
	 *            the segMean to set
	 */
	public void setSegMean(String segMean) {
		this.segMean = segMean;
	}

}
