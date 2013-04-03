/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ANALYTE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CENTER_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ELEMENT_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PARTICIPANT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PORTION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SAMPLE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SLIDE_LAYER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TSS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATED_AFTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UPDATED_BEFORE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.VIAL;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.UUIDBrowserWebService;

import java.io.StringWriter;

import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean that is used by the {@link UUIDBrowserWebService} resource for storing
 * and validating query/path parameters.
 * 
 * <p>
 * This bean uses JSR-303 compliant annotations for validation.
 * 
 * @author Matt Nicholls 
 * 		   Last updated by: nichollsmc
 * @version
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UUIDBrowserWSQueryParamBean {

	@AssertElement(AssertionType.ANALYTE_TYPE)
	@QueryParam(ANALYTE_TYPE)
	private String analyteType;

	@AssertElement(AssertionType.INTEGER)
	@QueryParam(BATCH)
	private String batch;

	@AssertElement(AssertionType.BCR)
	@QueryParam(BCR)
	private String bcr;

	@AssertElement(AssertionType.CENTER)
	@QueryParam(CENTER)
	private String center;

	@QueryParam(CENTER_TYPE)
	private String centerType;

	@QueryParam(DISEASE)
	private String disease;

	@QueryParam(ELEMENT_TYPE)
	private String elementType;

	@QueryParam(PARTICIPANT)
	private String participant;

	@QueryParam(PLATE)
	private String plate;

	@QueryParam(PLATFORM)
	private String platform;

	@QueryParam(PORTION)
	private String portion;

	@AssertElement(AssertionType.SAMPLE_TYPE)
	@QueryParam(SAMPLE_TYPE)
	private String sampleType;

	@AssertElement(AssertionType.SLIDE_LAYER)
	@QueryParam(SLIDE_LAYER)
	private String slideLayer;

	@QueryParam(TSS)
	private String tss;

	@AssertElement(AssertionType.DATE_FORMAT)
	@QueryParam(UPDATED_AFTER)
	private String updatedAfter;

	@AssertElement(AssertionType.DATE_FORMAT)
	@QueryParam(UPDATED_BEFORE)
	private String updatedBefore;

	@QueryParam(VIAL)
	private String vial;

	@AssertElement(AssertionType.BARCODE)
	@QueryParam("barcode")
	private String barcode;

	@AssertElement(AssertionType.UUID)
	@QueryParam("uuid")
	private String uuid;

	/**
	 * Overridden {@link Object#toString()} method that returns a human-readable
	 * XML representation of a {@link UUIDBrowserWSQueryParamBean} instance and
	 * its contents.
	 */
	@Override
	public String toString() {

		JAXBContext context;
		Marshaller marshaller;
		StringWriter stringWriter = new StringWriter();

		try {
			context = JAXBContext.newInstance(this.getClass());
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(this, stringWriter);
		} 
		catch (JAXBException je) {
			throw new RuntimeException("Marshalling bean instance failed. Reason: " + je.getMessage());
		}

		return stringWriter.toString();
	}

	/**
	 * @return the analyteType
	 */
	public String getAnalyteType() {
		return analyteType;
	}

	/**
	 * @param analyteType the analyteType to set
	 */
	public void setAnalyteType(String analyteType) {
		this.analyteType = analyteType;
	}

	/**
	 * @return the batch
	 */
	public String getBatch() {
		return batch;
	}

	/**
	 * @param batch the batch to set
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}

	/**
	 * @return the bcr
	 */
	public String getBcr() {
		return bcr;
	}

	/**
	 * @param bcr the bcr to set
	 */
	public void setBcr(String bcr) {
		this.bcr = bcr;
	}

	/**
	 * @return the center
	 */
	public String getCenter() {
		return center;
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(String center) {
		this.center = center;
	}

	/**
	 * @return the centerType
	 */
	public String getCenterType() {
		return centerType;
	}

	/**
	 * @param centerType the centerType to set
	 */
	public void setCenterType(String centerType) {
		this.centerType = centerType;
	}

	/**
	 * @return the disease
	 */
	public String getDisease() {
		return disease;
	}

	/**
	 * @param disease the disease to set
	 */
	public void setDisease(String disease) {
		this.disease = disease;
	}

	/**
	 * @return the elementType
	 */
	public String getElementType() {
		return elementType;
	}

	/**
	 * @param elementType the elementType to set
	 */
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	/**
	 * @return the participant
	 */
	public String getParticipant() {
		return participant;
	}

	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(String participant) {
		this.participant = participant;
	}

	/**
	 * @return the plate
	 */
	public String getPlate() {
		return plate;
	}

	/**
	 * @param plate the plate to set
	 */
	public void setPlate(String plate) {
		this.plate = plate;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * @param platform the platform to set
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/**
	 * @return the portion
	 */
	public String getPortion() {
		return portion;
	}

	/**
	 * @param portion the portion to set
	 */
	public void setPortion(String portion) {
		this.portion = portion;
	}

	/**
	 * @return the sampleType
	 */
	public String getSampleType() {
		return sampleType;
	}

	/**
	 * @param sampleType the sampleType to set
	 */
	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	/**
	 * @return the slideLayer
	 */
	public String getSlideLayer() {
		return slideLayer;
	}

	/**
	 * @param slideLayer the slideLayer to set
	 */
	public void setSlideLayer(String slideLayer) {
		this.slideLayer = slideLayer;
	}

	/**
	 * @return the tss
	 */
	public String getTss() {
		return tss;
	}

	/**
	 * @param tss the tss to set
	 */
	public void setTss(String tss) {
		this.tss = tss;
	}

	/**
	 * @return the updatedAfter
	 */
	public String getUpdatedAfter() {
		return updatedAfter;
	}

	/**
	 * @param updatedAfter the updatedAfter to set
	 */
	public void setUpdatedAfter(String updatedAfter) {
		this.updatedAfter = updatedAfter;
	}

	/**
	 * @return the updatedBefore
	 */
	public String getUpdatedBefore() {
		return updatedBefore;
	}

	/**
	 * @param updatedBefore the updatedBefore to set
	 */
	public void setUpdatedBefore(String updatedBefore) {
		this.updatedBefore = updatedBefore;
	}

	/**
	 * @return the vial
	 */
	public String getVial() {
		return vial;
	}

	/**
	 * @param vial the vial to set
	 */
	public void setVial(String vial) {
		this.vial = vial;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
