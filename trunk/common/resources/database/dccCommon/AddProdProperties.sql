-- qclive-core properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enableEmail','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromAddress','NCIdccteam@mail.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromName','TCGA Data Portal','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.subjectPrefix','TCGA Data Portal:','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromAddress','NCIdccteam@mail.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultReplyTo','dccnotify@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultCcAddress','dccnotify@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromName','DCC Processing','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archive.enableEmail','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.subjectPrefix','[TCGA-DCC]','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.ncbiDownloadDirectory','//tcgafiles/ftp_auth/ncbidownload','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.ncbiCronTriggerTimer','0 59 22 ? * SUN-SAT','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.gscMaterializedViewRefreshTriggerTimer','0 59 23 ? * SUN-SAT','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3 2012','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.serverUrl','http://tcga-data.nci.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerDirectory','//tcgafiles/','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerEnabled','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mvJobRunner.active','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLogger.writeToDb','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.mount.root','/tcgafiles','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.debugLevel','INFO','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.waitBeforeProcessingArchive','60000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.publicDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/anonymous','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.privateDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.emailBCC','tcga-data-l@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.delayBeforeStartingQCLiveChecking','60000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.delayBetweenFileDoneUploadingCheck','900000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.timeToWaitForMD5','240000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.depositDirectoryPollInterval','960000','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveNumberOfQueues','2','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveUploaderNumberOfQueues','4','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveWaitHours','6','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveInitialWaitMinutes','45','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveClinicalLoaderWaitMinutes','1','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveBiotabWaitMinutes','1','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveClinicalLoaderThreadCount','2','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biotabGeneratorDelayMinutes','1','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biotabGenerationCronExpression','0 0 3 ? * MON,WED,FRI','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biotabSchedulerThreadPool','10','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.md5ValidationRetryPeriod','3600000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.md5ValidationRetryAttempts','3','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLiveSubmissionWindowHours','24','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archiveLoggerLocal','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biMonthly.poc.name','The TCGA DCC Bioinformatics Team','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biMonthly.poc.email','TCGA-DCC-BINF-L@LIST.NIH.GOV','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biMonthly.poc.phone','301-451-2219','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.dataUseDisclaimerLocation','/local/content/tcga/conf/qclive/prod/DATA_USE_DISCLAIMER.txt','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLive.Active','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.qcLive.validateXML','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.dirsToWatch','/tcgafiles/ftp_auth/deposit_ftpusers/nwchbcr,/tcgafiles/ftp_auth/deposit_ftpusers/brawo,/tcgafiles/ftp_auth/deposit_ftpusers/cgclbl,/tcgafiles/ftp_auth/deposit_ftpusers/cgcunc,/tcgafiles/ftp_auth/deposit_ftpusers/tcgabcr,/tcgafiles/ftp_auth/deposit_ftpusers/baylor,/tcgafiles/ftp_auth/deposit_ftpusers/gscbroad,/tcgafiles/ftp_auth/deposit_ftpusers/jhmi,/tcgafiles/ftp_auth/deposit_ftpusers/mskc,/tcgafiles/ftp_auth/deposit_ftpusers/stanf,/tcgafiles/ftp_auth/deposit_ftpusers/washu,/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad,/tcgafiles/ftp_auth/deposit_ftpusers/gccusc,/tcgafiles/ftp_auth/deposit_ftpusers/bcgsc,/tcgafiles/ftp_auth/deposit_ftpusers/gscucsc,/tcgafiles/ftp_auth/deposit_ftpusers/cgcmda','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bulkReceivedWorkingDirectory','','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archiveOfflineRoot','/tcga_arch/deposit_ftpusers','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.failedArchiveRoot','/tcga_arch/deposit_ftpusers/FAILED','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.schedulerShouldUseDatabase','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.autoloader.enabled','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.autoloader.threadCount','4','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.autoloader.switchOnTime','18:00','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.autoloader.switchOffTime','09:00','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.tcgaVcfVersion.regExp','1\.0','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.to','dccnotify@list.nih.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.subject','[PRODUCTION] Exception in QC Processing','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mailHost','mailfwd.nih.gov','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,PRAD:tcgaPRADDS,SALD:tcgaSALDDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.uuidsRequiredInXml','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.aliquotElementXPath','//aliquots/aliquot','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.aliquotBarcodeElementName','bcr_aliquot_barcode','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.aliquotUuidElementName','bcr_aliquot_uuid','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipDayElementName','day_of_shipment','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipMonthElementName','month_of_shipment','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipYearElementName','year_of_shipment','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.dayOfPrefix','day_of_','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.monthOfPrefix','month_of_','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.yearOfPrefix','year_of_','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.allowLocalSchema','false','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.clinicalExclusionPatterns','day_of_,month_of,year_of_,barcode,uuid','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.datesToValidate','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement,shipment,creation','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.datesToCompare','last_followup>=initial_pathologic_diagnosis','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.basisDateNameForClinical','initial_pathologic_diagnosis','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.basisDateNameForBiospecimen','index','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.elapsedElementPrefix','days_to_','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.birthDateElementName','birth','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.cdeForDatesToObscure','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.datesNotToObscure','creation,shipment,dcc_upload','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.ageAtPrefix','age_at_','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.ageAtBasisDateCDE','','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archive.additionalFiles','manifest.txt,description.txt,changes_dcc.txt,readme_dcc.txt,dcc_altered_files.txt','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.cutoffAgeAtInitialDiagnosis','90','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.daysToBirthLowerBound','-32872','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.bcrPatientBarcodeElementName','bcr_patient_barcode','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipmentPortionPath','//portions/shipment_portion','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.bcrShipmentPortionUuidElementName','bcr_shipment_portion_uuid','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.centerIdElementName','center_id','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.plateIdElementName','plate_id','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipmentPortionBcrAliquotBarcodeElementName','shipment_portion_bcr_aliquot_barcode','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipmentPortionShipDayElementName','day_of_shipment','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipmentPortionShipMonthElementName','month_of_shipment','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.shipmentPortionShipYearElementName','year_of_shipment','','qclive-core','');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.validXsdDomainPattern','tcga-data\\.nci\\.nih\\.gov','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.validXsdPrefixPattern','bcr','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bcr.validXsdVersionPattern','2\\.4(\\.\\d*)?','','qclive-core','');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mafVersion.FirstGen','1.0','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mafVersion.NextGen','2.2','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mafVersion.RNASeq','RNASeq v1.0','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.validClinicalPlatforms','bio','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.level2CacheGenerator.scheduler.autoStartup','true','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.level2CacheGenerator.scheduler.threadCount','2','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.cacheFileDistroDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.tmp_cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/tmp/','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.protectedCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.publicCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/','','qclive-core','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bioTabTemplateFilesRootDir','/local/content/tcga/conf/qclive/prod/schema/','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.bioTabTemplateFiles','','','qclive-core','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.biospecimenMetadataPlatformsCronExpression','0 0 3 * * ?','','qclive-core','');

-- dam backend properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enableDamUI','false','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enableEmail','true','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromName','TCGA Data Portal','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.subjectPrefix','TCGA Data Portal:','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.archivePhysicalPathPrefix','/tcgafiles','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.downloadLinkSite','http://tcga-data.nci.nih.gov/tcgafiles','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.WSStatusCheckUrlBase','http://tcga-data.nci.nih.gov/tcga/damws/jobstatus','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.jobStatusUrl','http://tcga-data.nci.nih.gov/tcga/jobStatus.htm','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filePackagerQueueCount','16','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.hoursTillDeletion','24','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.notProtectedArchiveLogicalPath','ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.notProtectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.protectedArchiveLogicalPath','ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.protectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.tempfileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.emailTiming','true','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.failedEmailTo','tcgadccteam@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.threads.bigjobs','10','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.threads.smalljobs','20','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.smalljobmaxbytes','943718400','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.downloadSizeLimitGigs','70','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.filedeletionhours','24','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerDirectory','/tcgafiles/','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLogger.writeToDb','true','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultCcAddress','tcgadccteam@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromName','DCC Processing','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archive.enableEmail','true','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.subjectPrefix',' NCICB-DCC:','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.mount.root','tcgafiles','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.debugLevel','error','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.emailBCC','dccnotify@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enable.threadedBufferedWriter','false','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.damRefreshCronTrigger.Timer','0 59 1 * * ?','','dam','backend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,PRAD:tcgaPRADDS,SALD:tcgaSALDDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS','','dam','backend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.to','dccnotify@list.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.subject',' Error in DAM Processing','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mailHost','mailfwd.nih.gov','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'dcc.error.warningEnabled','true','','dam','backend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.nci.support.email','ncicb@pop.nci.nih.gov','','dam','backend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','dam','backend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.queue.autoStartup','true','','dam','backend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.queue.startupDelay','60','','dam','backend');

-- dam frontend properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enableDamUI','true','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enableEmail','true','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.defaultFromName','TCGA Data Portal','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.subjectPrefix','TCGA Data Portal:','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.archivePhysicalPathPrefix','/tcgafiles','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.downloadLinkSite','http://tcga-data.nci.nih.gov/tcgafiles','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.WSStatusCheckUrlBase','http://tcga-data.nci.nih.gov/tcga/damws/jobstatus','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.jobStatusUrl','http://tcga-data.nci.nih.gov/tcga/jobStatus.htm','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filePackagerQueueCount','16','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.hoursTillDeletion','24','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.notProtectedArchiveLogicalPath','ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.notProtectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.protectedArchiveLogicalPath','ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.protectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.tempfileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.emailTiming','true','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.failedEmailTo','tcgadccteam@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.threads.bigjobs','10','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.threads.smalljobs','20','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.smalljobmaxbytes','943718400','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.downloadSizeLimitGigs','70','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.filedeletionhours','24','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerDirectory','/tcgafiles/','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.usageLogger.writeToDb','true','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultCcAddress','tcgadccteam@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.defaultFromName','DCC Processing','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.archive.enableEmail','true','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.subjectPrefix',' NCICB-DCC:','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.mount.root','tcgafiles','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.debugLevel','error','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.emailBCC','dccnotify@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.enable.threadedBufferedWriter','false','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.damRefreshCronTrigger.Timer','0 59 1 * * ?','','dam','frontend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,PRAD:tcgaPRADDS,SALD:tcgaSALDDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS','','dam','frontend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.to','dccnotify@list.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.error.subject',' Error in DAM Processing','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.mailHost','mailfwd.nih.gov','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'dcc.error.warningEnabled','true','','dam','frontend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.nci.support.email','ncicb@pop.nci.nih.gov','','dam','frontend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','dam','frontend');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.queue.autoStartup','false','','dam','frontend');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.portal.filepackager.queue.startupDelay','60','','dam','frontend');

-- databrowser properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.svg.cssURL','http://cmap.nci.nih.gov/Pathways/pathways.css','The pathways css url','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.svg.site','http://cmap.nci.nih.gov/cmapcgi/BioCartaSVG.pl','The url for the svg site','databrowser','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.svg.imagelocation','/local/content/tcga-qc/temp-svg','Apache has been set up with alias to this location, allowing browser to retrieve image files','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.tempfilelocation','/local/content/tcga-qc/temp-svg','Temp file location','databrowser','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.tooltipfile','http://tcga-data.nci.nih.gov/web/other/include/level4_tooltips.txt','Location of the tool tip file','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.export.numberofwaits','20','Number of seconds to wait for export','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.export.waittimemilliseconds','500','Wait time in milliseconds','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.export.waittimetodeleteinmilliseconds','30000','Delete wait time in milliseconds','databrowser','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.dataportal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,PRAD:tcgaPRADDS,SALD:tcgaSALDDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS','Disease schema data sources','databrowser','');

-- datareports-web properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.enableEmail','true','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.defaultFromName','DCC Processing','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.defaultFromAddress','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.defaultReplyTo','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.defaultCcAddress','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.emailBCC','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.subjectPrefix','NCICB-DCC:','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.failedmailto','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.server.httpColonSlashSlashHostnameAndPort','http://tcga-data.nci.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.biMonthly.poc.name','Ari Kahn, Ph.D.','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.biMonthly.poc.email','arik@mail.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.biMonthly.poc.phone','512-306-2007','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.error.to','tcgadccteam@list.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.error.subject','[PROD] Exception in QC Processing','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.mailHost','mailfwd.nih.gov','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.sampleSummaryRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.projectCaseDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.statsDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports-web','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.datareports.pipelineReportJsonFilesPath','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/news/','','datareports-web','');

-- uuid properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.enableEmail','true','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.mailHost','mailfwd.nih.gov','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.defaultFromName','DCC Processing','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.defaultFromAddress','ncidccteam@mail.nih.gov','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.defaultCcAddress','tcgadccteam@list.nih.gov','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.emailBCC','tcgadccteam@list.nih.gov','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.defaultReplyTo','tcgadccteam@list.nih.gov','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.subjectPrefix','NCICB-DCC:','','uuid','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.failedmailto','dccnotify@list.nih.gov','','uuid','');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.maxAllowedUUIDs','100','Maximum number of uuids that are allowed to be created at one time from UI','uuid','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.uuid.uuidBrowserRefreshCronFrequencyTimer','0 59 2 * * ?','refresh of uuid browser procedure','uuid','');


-- annotations properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'application.url','https://tcga-data.nci.nih.gov/annotations','The application url for annotations','annotations','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'ss.feed.num.months','3','The number of months to include for the rss feed','annotations','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'tcga.annotations.cacheRefreshCronFrequencyTimer','0 0 3 ? * *','The cron refresh frequency for the annotations cache','annotations','');

-- no-application properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'enable.general.email','true','A flag to indicate if general email is enabled','','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'debug.level','error','The level for debug','','');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'ldap_authentication_server_url','ldaps://cbioweb-ldap.nci.nih.gov:636/ou=nci,o=nih','URL for the LDAP authencitication server','','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'ldap_user_search_base','','','','');
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES(dcc_property_seq.NEXTVAL,'ldap_user_search_filter','(cn={0})','The user search filter for ldap','',''); 


commit;