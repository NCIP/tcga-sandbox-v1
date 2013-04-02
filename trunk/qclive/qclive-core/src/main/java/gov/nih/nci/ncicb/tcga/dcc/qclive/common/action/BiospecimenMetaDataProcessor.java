/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ControlQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.JAXBUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.UnmarshalResult;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Analyte;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Controls;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Drug;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Examination;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Patient;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Portion;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Radiation;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Sample;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.ShipmentPortion;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Slide;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Surgery;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.TcgaBcr;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.BiospecimenXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import javax.xml.bind.JAXBIntrospector;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class retrieves the meta-data from all BCR XML files and persists this data to the
 * database.
 *
 * @author Matt Nicholls
 *         Last updated by: sgirshik
 */
public class BiospecimenMetaDataProcessor extends AbstractProcessor<Archive, Archive> {

    private final Log logger = LogFactory.getLog(BiospecimenMetaDataProcessor.class);
    private BarcodeUuidResolver barcodeUuidResolver;
    private UUIDHierarchyQueries uuidHierarchyQueries;
    private ControlQueries controlQueries;
    private ControlQueries diseaseControlQueries;

    @Override
    protected Archive doWork(Archive archive, QcContext context) throws ProcessorException {
        context.setArchive(archive);
        if (isCorrectArchiveType(archive)) {
            File[] files = getFilesForExtension(archive);
            logger.debug("Files found: " + files);
            files = removeAuxiliaryXmlFiles(files);
            logger.debug("Files after removing Auxiliary xml: " + files);
            final Map<File, Archive> results = new HashMap<File, Archive>();
            for (final File file : files) {
                final Archive result = processFile(file, context);
                results.put(file, result);
            }
            return getReturnValue(results, context);
        } else {
            return getDefaultReturnValue(archive);
        }
    }

    protected File[] removeAuxiliaryXmlFiles(File[] allXmlFiles) {
        if (allXmlFiles == null) {
            return null;
        }
        BCRUtilsImpl bcrUtils = new BCRUtilsImpl();
        ArrayList<File> noAuxList = new ArrayList<File>();
        for (File fileInXmlArray : allXmlFiles) {
            if (!(bcrUtils.isAuxiliaryFile(fileInXmlArray))) {
                noAuxList.add(fileInXmlArray);
            }
        }
        return noAuxList.toArray(new File[]{});
    }

    protected Archive getReturnValue(final Map<File, Archive> results, final QcContext context) {
        return context.getArchive();
    }

    protected Archive getDefaultReturnValue(final Archive archive) {
        return archive;
    }

    protected boolean isCorrectArchiveType(final Archive archive)
            throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException {
        return archive.getExperimentType().equals(Experiment.TYPE_BCR);
    }

    protected File[] getFilesForExtension(Archive archive) {
        return DirectoryListerImpl.getFilesByExtension(archive.getDeployDirectory(), BiospecimenXmlValidator.XML_EXTENSION);
    }

    protected Archive processFile(final File xmlFile, final QcContext context) throws ProcessorException {

        try {
            handleBiospecimenMetaData(xmlFile, context);
        } catch (Exception e) {
            logger.error(" Error processing " + xmlFile + ". ", e);
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);

            if ((e instanceof DataAccessException) || !(e instanceof RuntimeException)) {
                final StringBuilder errorMsg =
                        new StringBuilder("Error processing '")
                                .append(xmlFile)
                                .append("'. ");
                if (e instanceof DataAccessException) {
                    errorMsg.append(" Unexpected database error.");
                } else {
                    errorMsg.append(e.getMessage());
                }
                throw new ProcessorException(errorMsg.toString());
            } else {
                throw (RuntimeException) e;
            }
        }

        return context.getArchive();
    }

    /**
     * Extracts biospecimen meta-data from a clinical XML file and returns a list of {@link BiospecimenMetaData}
     * objects. Each object represents a single XML meta-data element.
     *
     * @param xmlFile   - the XML file to extract biospecimen meta-data from
     * @param qcContext - qcContext
     * @return a list of {@link BiospecimenMetaData} objects, with each object representing a single meta-data element
     * @throws UUIDException      if an error occurs while attempting to generate a UUID for a specific meta-data
     *                            element when not provided in the XML
     * @throws ProcessorException if errors occur while attempting extract meta-data
     */
    protected List<BiospecimenMetaData> handleBiospecimenMetaData(final File xmlFile, final QcContext qcContext)
            throws UUIDException, ProcessorException {
        logger.debug("starting handleBiospecimenMetadata");
        final Archive archive = qcContext.getArchive();
        // Unmarshal the XML file into a JAXB generated object
        TcgaBcr tcgaBcr;
        UnmarshalResult unmarshalResult;
        JAXBIntrospector jaxbIntrospector;
        try {
            unmarshalResult = JAXBUtil.unmarshal(xmlFile, TcgaBcr.class, true, false);
            tcgaBcr = (TcgaBcr) unmarshalResult.getJaxbObject();
            jaxbIntrospector = JAXBUtil.getJAXBIntrospector(TcgaBcr.class);
            logger.debug("JAXB Unmarshalling of BiospecimenMetadata done");
        } catch (Exception e) {
            logger.info(e);
            throw new ProcessorException(e.getMessage());
        }

        Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode;
        List<BiospecimenMetaData> biospecimenMetaData = null;
        if (tcgaBcr != null) {
            final Patient patient = tcgaBcr.getPatient();
            if (patient != null) {
                biospecimenMetaDataByUUIDORBarcode = new LinkedHashMap<String, BiospecimenMetaData>();
                final BiospecimenMetaData patientMetaData = new BiospecimenMetaData();
                final String uuid = JAXBUtil.getJAXBObjectValue(patient.getBcrPatientUuid());
                patientMetaData.setBarcode(patient.getBcrPatientBarcode().getValue());
                setUUID(patientMetaData, uuid, archive);
                patientMetaData.setUuidType(jaxbIntrospector.getElementName(patient).getLocalPart());
                patientMetaData.setBcr(archive.getTheCenter().getCenterId().toString());
                patientMetaData.setBatch(archive.getSerialIndex());
                patientMetaData.setDisease(archive.getTheTumor().getTumorName());
                patientMetaData.setParticipantId(patient.getPatientId().getValue());
                patientMetaData.setTissueSourceSite(patient.getTissueSourceSite().getValue());
                biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? patientMetaData.getBarcode() : patientMetaData.getUuid()
                        , patientMetaData);
                logger.debug("Patient metadata done");
                // Get the transform of JAXB object into a list of meta-data entity objects
                getSampleMetaData(patient, patientMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                logger.debug("Sample metadata done");
                getDrugMetaData(patient, patientMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                logger.debug("Drug metadata done");
                getRadiationMetaData(patient, patientMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                logger.debug("Radiation metadata done");
                getExaminationMetaData(patient, patientMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                logger.debug("Examination metadata done");
                getSurgeryMetaData(patient, patientMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                logger.debug("Surgery metadata done");
                biospecimenMetaData = new ArrayList<BiospecimenMetaData>(biospecimenMetaDataByUUIDORBarcode.values());
                // Persist the meta-data                
                if (!biospecimenMetaDataByUUIDORBarcode.isEmpty()) {

                    // Get the existing meta data from the data base and warn the user if the meta data is changed.
                    final Map<String, BiospecimenMetaData> existingBiospecimenMetaData = uuidHierarchyQueries.getMetaData(new ArrayList<String>(biospecimenMetaDataByUUIDORBarcode.keySet()));
                    logger.debug("Get existing metadata: " + existingBiospecimenMetaData);
                    logger.debug("Persisting metadata starting");
                    uuidHierarchyQueries.persistUUIDHierarchy(biospecimenMetaData);
                    logger.debug("Persisting metadata done");
                    final List<BiospecimenMetaData> updatedMetaData = new ArrayList<BiospecimenMetaData>();

                    // get the updatedMetadata
                    for (final String existingUUIDOrBarcode : existingBiospecimenMetaData.keySet()) {
                        updatedMetaData.add(biospecimenMetaDataByUUIDORBarcode.get(existingUUIDOrBarcode));
                    }
                    // remove the metadata that are not changed
                    updatedMetaData.removeAll(existingBiospecimenMetaData.values());
                    if (updatedMetaData.size() > 0) {
                        final StringBuffer warning = new StringBuffer();

                        for (final BiospecimenMetaData metaData : updatedMetaData) {
                            warning.append("Meta data was updated: ");
                            // get the existing meta data. Key can be either uuid or barcode
                            BiospecimenMetaData existingMetaData = existingBiospecimenMetaData.get(metaData.getUuid());
                            existingMetaData = (existingMetaData == null) ? existingBiospecimenMetaData.get(metaData.getBarcode()) : existingMetaData;
                            warning.append(existingMetaData.getMetaDataString())
                                    .append("-->")
                                    .append(metaData.getMetaDataString());
                            qcContext.addWarning(warning.toString());
                            warning.delete(0, warning.length());
                        }

                    }

                }
            }
            processControls(tcgaBcr);
        }

        return biospecimenMetaData;
    }

    /**
     * persist each control object from the TcgaBcr root
     *
     * @param tcgaBcr
     */
    protected void processControls(final TcgaBcr tcgaBcr) {
        final Controls controls = tcgaBcr.getControls();
        if (controls != null) {
            for (final Control control : controls.getControl()) {
                controlQueries.persistControl(control);
                logger.debug("Persist control done");
                controlQueries.updateControlForShippedBiospecimen(control);
                logger.debug("Update shippedBiospecimen for common schema done");
                diseaseControlQueries.updateControlForShippedBiospecimen(control);
                logger.debug("Update shippedBiospecimen for disease schema done");
            }
        }
    }

    private void getSampleMetaData(final Patient patient, final BiospecimenMetaData patientMetaData,
                                   final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                   final Archive archive) throws UUIDException, ProcessorException {
        if (patient.getSamples() != null) {
            List<Sample> samples = patient.getSamples().getSample();
            if (samples != null) {
                // Get sample meta-data for the patient
                for (final Sample sample : samples) {
                    BiospecimenMetaData sampleMetaData = BiospecimenMetaData.newInstance(patientMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(sample.getBcrSampleUuid());
                    sampleMetaData.setBarcode(sample.getBcrSampleBarcode().getValue());
                    setUUID(sampleMetaData, uuid, archive);
                    sampleMetaData.setUuidType(jaxbIntrospector.getElementName(sample).getLocalPart());
                    sampleMetaData.setSampleType(sample.getSampleTypeId().getValue());
                    sampleMetaData.setVialId(sample.getVialNumber().getValue());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? sampleMetaData.getBarcode() : sampleMetaData.getUuid(), sampleMetaData);

                    getPortionMetaData(sample, sampleMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                    logger.debug("Portion metadata done");
                    getShipmentPortionMetaData(sample, sampleMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                    logger.debug("Shipment Portion metadata done");
                }
            }
        }
    }

    private void getPortionMetaData(final Sample sample, final BiospecimenMetaData sampleMetaData,
                                    final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                    final Archive archive) throws UUIDException, ProcessorException {
        if (sample.getPortions() != null) {
            List<Portion> portions = sample.getPortions().getPortion();
            if (portions != null) {
                // Get portion meta-data for the sample
                for (final Portion portion : portions) {
                    BiospecimenMetaData portionMetaData = BiospecimenMetaData.newInstance(sampleMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(portion.getBcrPortionUuid());
                    portionMetaData.setBarcode(portion.getBcrPortionBarcode().getValue());
                    setUUID(portionMetaData, uuid, archive);
                    portionMetaData.setUuidType(jaxbIntrospector.getElementName(portion).getLocalPart());
                    portionMetaData.setPortionId(portion.getPortionNumber().getValue());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? portionMetaData.getBarcode() : portionMetaData.getUuid(), portionMetaData);
                    getAnalyteMetaData(portion, portionMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                    logger.debug("Analyte metadata done");
                    getSlideMetaData(portion, portionMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                    logger.debug("Slide metadata done");
                }
            }
        }
    }

    private void getShipmentPortionMetaData(final Sample sample, final BiospecimenMetaData sampleMetaData,
                                            final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                            final Archive archive) throws ProcessorException, UUIDException {
        if (sample.getPortions() != null) {
            List<ShipmentPortion> shipmentPortions = sample.getPortions().getShipmentPortion();
            if (shipmentPortions != null) {
                // Get shipment portion meta-data for the sample
                for (final ShipmentPortion shipmentPortion : shipmentPortions) {
                    BiospecimenMetaData shipmentPortionMetaData = BiospecimenMetaData.newInstance(sampleMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(shipmentPortion.getBcrShipmentPortionUuid());
                    shipmentPortionMetaData.setBarcode(shipmentPortion.getShipmentPortionBcrAliquotBarcode().getValue());
                    setUUID(shipmentPortionMetaData, uuid, archive);
                    shipmentPortionMetaData.setUuidType(jaxbIntrospector.getElementName(shipmentPortion).getLocalPart());
                    shipmentPortionMetaData.setPortionId(shipmentPortion.getPortionNumber().getValue().toString());
                    shipmentPortionMetaData.setPlateId(shipmentPortion.getPlateId().getValue());
                    shipmentPortionMetaData.setReceivingCenter(shipmentPortion.getCenterId().getValue());
                    shipmentPortionMetaData.setCenterCode(shipmentPortion.getCenterId().getValue());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? shipmentPortionMetaData.getBarcode() : shipmentPortionMetaData.getUuid(), shipmentPortionMetaData);
                }
            }
        }
    }

    private void getAnalyteMetaData(final Portion portion, final BiospecimenMetaData portionMetaData,
                                    final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                    final Archive archive) throws ProcessorException, UUIDException {
        if (portion.getAnalytes() != null) {
            List<Analyte> analytes = portion.getAnalytes().getAnalyte();
            if (analytes != null) {
                // Get portion meta-data for the sample
                for (final Analyte analyte : analytes) {
                    BiospecimenMetaData analyteMetaData = BiospecimenMetaData.newInstance(portionMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(analyte.getBcrAnalyteUuid());
                    analyteMetaData.setBarcode(analyte.getBcrAnalyteBarcode().getValue());
                    setUUID(analyteMetaData, uuid, archive);
                    analyteMetaData.setUuidType(jaxbIntrospector.getElementName(analyte).getLocalPart());
                    analyteMetaData.setAnalyteType(analyte.getAnalyteTypeId().getValue());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? analyteMetaData.getBarcode() : analyteMetaData.getUuid(), analyteMetaData);
                    getAliquotMetaData(analyte, analyteMetaData, biospecimenMetaDataByUUIDORBarcode, jaxbIntrospector, archive);
                    logger.debug("Aliquot metadata done");
                    logger.info("BiospecimenMetaDataProcessor: Processed analyte with UUID " + analyteMetaData.getUuid() + " and parent " + analyteMetaData.getParentUUID());

                }
            }
        }
    }

    private void getAliquotMetaData(final Analyte analyte, final BiospecimenMetaData analyteMetaData,
                                    final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                    final Archive archive) throws ProcessorException, UUIDException {
        if (analyte.getAliquots() != null) {
            List<Aliquot> aliquots = analyte.getAliquots().getAliquot();
            if (aliquots != null) {
                // Get aliquot meta-data for the analyte
                for (final Aliquot aliquot : aliquots) {
                    BiospecimenMetaData aliquotMetaData = BiospecimenMetaData.newInstance(analyteMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(aliquot.getBcrAliquotUuid());
                    aliquotMetaData.setBarcode(aliquot.getBcrAliquotBarcode().getValue());
                    setUUID(aliquotMetaData, uuid, archive);
                    aliquotMetaData.setUuidType(jaxbIntrospector.getElementName(aliquot).getLocalPart());
                    aliquotMetaData.setPlateId(aliquot.getPlateId().getValue());
                    aliquotMetaData.setReceivingCenter(aliquot.getCenterId().getValue());
                    aliquotMetaData.setCenterCode(aliquot.getCenterId().getValue());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? aliquotMetaData.getBarcode() : aliquotMetaData.getUuid(), aliquotMetaData);
                }
            }
        }
    }

    private void getSlideMetaData(final Portion portion, final BiospecimenMetaData portionMetaData,
                                  final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                  final Archive archive) throws UUIDException, ProcessorException {
        if (portion.getSlides() != null) {
            List<Slide> slides = portion.getSlides().getSlide();
            if (slides != null) {
                // Get slide meta-data for the sample
                for (final Slide slide : slides) {
                    BiospecimenMetaData slideMetaData = BiospecimenMetaData.newInstance(portionMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(slide.getBcrSlideUuid());
                    String slideBarcode = slide.getBcrSlideBarcode().getValue();
                    slideMetaData.setBarcode(slideBarcode);
                    slideMetaData.setSlide(getSlideForBarcode(slideBarcode));
                    setUUID(slideMetaData, uuid, archive);
                    slideMetaData.setUuidType(jaxbIntrospector.getElementName(slide).getLocalPart());
                    slideMetaData.setSlideLayer(slide.getSectionLocation().getValue().toLowerCase());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? slideMetaData.getBarcode() : slideMetaData.getUuid(), slideMetaData);
                    logger.info("BiospecimenMetaDataProcessor: Processed slide with UUID " + slideMetaData.getUuid() + " and parent " + slideMetaData.getParentUUID());
                }
            }
        }
    }

    private void getDrugMetaData(final Patient patient, final BiospecimenMetaData patientMetaData,
                                 final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                 final Archive archive) throws ProcessorException, UUIDException {
        if (patient.getDrugs() != null) {
            List<Drug> drugs = patient.getDrugs().getDrug();
            if (drugs != null) {
                // Get drug meta-data for the patient
                for (final Drug drug : drugs) {
                    BiospecimenMetaData drugMetaData = BiospecimenMetaData.newInstance(patientMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(drug.getBcrDrugUuid());
                    drugMetaData.setBarcode(drug.getBcrDrugBarcode().getValue());
                    setUUID(drugMetaData, uuid, archive);
                    drugMetaData.setUuidType(jaxbIntrospector.getElementName(drug).getLocalPart());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? drugMetaData.getBarcode() : drugMetaData.getUuid(), drugMetaData);
                }
            }
        }
    }

    private void getRadiationMetaData(final Patient patient, final BiospecimenMetaData patientMetaData,
                                      final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                      final Archive archive) throws ProcessorException, UUIDException {
        if (patient.getRadiations() != null) {
            List<Radiation> radiations = patient.getRadiations().getRadiation();
            if (radiations != null) {
                // Get radiation meta-data for the patient
                for (final Radiation radiation : radiations) {
                    BiospecimenMetaData radiationMetaData = BiospecimenMetaData.newInstance(patientMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(radiation.getBcrRadiationUuid());
                    radiationMetaData.setBarcode(radiation.getBcrRadiationBarcode().getValue());
                    setUUID(radiationMetaData, uuid, archive);
                    radiationMetaData.setUuidType(jaxbIntrospector.getElementName(radiation).getLocalPart());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? radiationMetaData.getBarcode() : radiationMetaData.getUuid(), radiationMetaData);
                }
            }
        }
    }

    private void getExaminationMetaData(final Patient patient, final BiospecimenMetaData patientMetaData,
                                        final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                        final Archive archive) throws ProcessorException, UUIDException {
        if (patient.getExaminations() != null) {
            List<Examination> examinations = patient.getExaminations().getExamination();
            if (examinations != null) {
                // Get examination meta-data for the patient
                for (final Examination examination : examinations) {
                    BiospecimenMetaData examinationMetaData = BiospecimenMetaData.newInstance(patientMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(examination.getBcrExaminationUuid());
                    examinationMetaData.setBarcode(examination.getBcrExaminationBarcode().getValue());
                    setUUID(examinationMetaData, uuid, archive);
                    examinationMetaData.setUuidType(jaxbIntrospector.getElementName(examination).getLocalPart());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? examinationMetaData.getBarcode() : examinationMetaData.getUuid(), examinationMetaData);
                }
            }
        }
    }

    private void getSurgeryMetaData(final Patient patient, final BiospecimenMetaData patientMetaData,
                                    final Map<String, BiospecimenMetaData> biospecimenMetaDataByUUIDORBarcode, final JAXBIntrospector jaxbIntrospector,
                                    final Archive archive) throws ProcessorException, UUIDException {
        if (patient.getSurgeries() != null) {
            List<Surgery> surgeries = patient.getSurgeries().getSurgery();
            if (surgeries != null) {
                // Get surgery meta-data for the patient
                for (final Surgery surgery : surgeries) {
                    BiospecimenMetaData surgeryMetaData = BiospecimenMetaData.newInstance(patientMetaData);
                    final String uuid = JAXBUtil.getJAXBObjectValue(surgery.getBcrSurgeryUuid());
                    surgeryMetaData.setBarcode(surgery.getBcrSurgeryBarcode().getValue());
                    setUUID(surgeryMetaData, uuid, archive);
                    surgeryMetaData.setUuidType(jaxbIntrospector.getElementName(surgery).getLocalPart());
                    biospecimenMetaDataByUUIDORBarcode.put((uuid == null || uuid.isEmpty()) ? surgeryMetaData.getBarcode() : surgeryMetaData.getUuid(), surgeryMetaData);
                }
            }
        }
    }

    /**
     * Sets the UUID for a {@link BiospecimenMetaData} object.
     * <p/>
     * <p/>
     * If no UUID is provided (null or empty string), a lookup of the UUID will be performed using the
     * barcode from the meta-data object and the returned value will be used in its place.
     *
     * @param biospecimenMetaData - an instance of {@link BiospecimenMetaData}
     * @param uuid                - a string representing the UUID to set, can be null
     * @param archive             - the archive
     * @throws UUIDException if an error occurs while attempting to do a UUID lookup
     */
    protected void setUUID(final BiospecimenMetaData biospecimenMetaData, final String uuid, final Archive archive) throws UUIDException {

        // Set the parent UUID
        biospecimenMetaData.setParentUUID(biospecimenMetaData.getUuid());

        // Set the child UUID
        final String scrubbedUUID = (uuid == null ? uuid : uuid.trim());
        final Barcode barcode =
                barcodeUuidResolver.resolveBarcodeAndUuid(
                        biospecimenMetaData.getBarcode(), scrubbedUUID, archive.getTheTumor(), archive.getTheCenter(), true);
        biospecimenMetaData.setUuid(barcode.getUuid());
    }

    /**
     * Utility method for extracting the slide value from a slide barcode provided by slide elements in
     * biospecimen XML.
     * <p/>
     * <p/>
     * For example if the slide barcode is TCGA-00-0110-01A-01-BS1, the slide value will be BS1.
     *
     * @param slideBarcode - a string representing the slide barcode
     * @return a string representing the slide value parsed from the slide barcode
     * @throws ProcessorException if the slide barcode is null or empty
     */
    protected String getSlideForBarcode(final String slideBarcode) throws ProcessorException {
        if (slideBarcode != null && !slideBarcode.isEmpty()) {
            return slideBarcode.substring(slideBarcode.lastIndexOf('-') + 1);
        } else {
            throw new ProcessorException("Could not parse slide barcode '" + slideBarcode + "' for slide value because the barcode is either null or empty.");
        }
    }

    @Override
    public String getName() {
        return "biospecimen XML metadata processor";
    }

    /**
     * @return the barcodeUuidResolver
     */
    public BarcodeUuidResolver getBarcodeUuidResolver() {
        return barcodeUuidResolver;
    }

    /**
     * @param barcodeUuidResolver the barcodeUuidResolver to set
     */
    public void setBarcodeUuidResolver(final BarcodeUuidResolver barcodeUuidResolver) {
        this.barcodeUuidResolver = barcodeUuidResolver;
    }

    /**
     * @return the uuidHierarchyQueries
     */
    public UUIDHierarchyQueries getUuidHierarchyQueries() {
        return uuidHierarchyQueries;
    }

    /**
     * @param uuidHierarchyQueries the uuidHierarchyQueries to set
     */
    public void setUuidHierarchyQueries(final UUIDHierarchyQueries uuidHierarchyQueries) {
        this.uuidHierarchyQueries = uuidHierarchyQueries;
    }

    /**
     * @param controlQueries the controlQueries to set
     */
    public void setControlQueries(ControlQueries controlQueries) {
        this.controlQueries = controlQueries;
    }

    /**
     * @param diseaseControlQueries
     */
    public void setDiseaseControlQueries(ControlQueries diseaseControlQueries) {
        this.diseaseControlQueries = diseaseControlQueries;
    }
}
