-- Qclive properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableEmail','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromAddress','ncidccteam@mail.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromName','TCGA Data Portal','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.subjectPrefix','[DEV] TCGA Data Portal:','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromAddress','NCIdccteam@mail.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultReplyTo','dccnotify@list.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultCcAddress','dccnotify@list.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromName','DCC Processing','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.enableEmail','true','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.subjectPrefix','[DEV TCGA-DCC]','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.ncbiDownloadDirectory','//tcgafiles/ftp_auth/ncbidownload','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.ncbiCronTriggerTimer','0 59 22 ? * SUN-SAT','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.gscMaterializedViewRefreshTriggerTimer','0 59 23 ? * SUN-SAT','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.serverUrl','https://tcga-data-dev.nci.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerDirectory','//tcgafiles/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerEnabled','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mvJobRunner.active','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLogger.writeToDb','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.mount.root','/tcgafiles','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.debugLevel','DEBUG','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.waitBeforeProcessingArchive','20000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.publicDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/anonymous','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.privateDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.emailBCC','chenjw@mail.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.delayBeforeStartingQCLiveChecking','60000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.delayBetweenFileDoneUploadingCheck','60000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.timeToWaitForMD5','60000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.depositDirectoryPollInterval','60000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveNumberOfQueues','2','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveUploaderNumberOfQueues','4','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveWaitHours','1','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveInitialWaitMinutes','3','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveClinicalLoaderWaitMinutes','1','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveBiotabWaitMinutes','1','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveClinicalLoaderThreadCount','2','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabGeneratorDelayMinutes','1','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabGenerationCronExpression','0 0 3 ? * MON,WED,FRI','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabSchedulerThreadPool','10','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.md5ValidationRetryPeriod','60000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.md5ValidationRetryAttempts','3','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveSubmissionWindowHours','3','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archiveLoggerLocal','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.name','Ari Kahn, Ph.D.','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.email','arik@mail.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.phone','512-306-2007','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.dataUseDisclaimerLocation','/local/content/tcga/qclive/conf/DATA_USE_DISCLAIMER.txt','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLive.Active','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLive.validateXML','true','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.dirsToWatch','/tcgafiles/ftp_auth/deposit_ftpusers/baylor,/tcgafiles/ftp_auth/deposit_ftpusers/brawo,/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad,/tcgafiles/ftp_auth/deposit_ftpusers/cgclbl,/tcgafiles/ftp_auth/deposit_ftpusers/cgcunc,/tcgafiles/ftp_auth/deposit_ftpusers/gscbroad,/tcgafiles/ftp_auth/deposit_ftpusers/jhmi,/tcgafiles/ftp_auth/deposit_ftpusers/mskc,/tcgafiles/ftp_auth/deposit_ftpusers/stanf,/tcgafiles/ftp_auth/deposit_ftpusers/tcgabcr,/tcgafiles/ftp_auth/deposit_ftpusers/washu,/tcgafiles/ftp_auth/deposit_ftpusers/gccusc,/tcgafiles/ftp_auth/deposit_ftpusers/bcgsc,/tcgafiles/ftp_auth/deposit_ftpusers/gscucsc,/tcgafiles/ftp_auth/deposit_ftpusers/nwchbcr,/tcgafiles/ftp_auth/deposit_ftpusers/cgcmda','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bulkReceivedWorkingDirectory','','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archiveOfflineRoot','/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad/offline','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedArchiveRoot','/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad/failed','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.schedulerShouldUseDatabase','true','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.enabled','true','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.threadCount','4','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.switchOnTime','18:00','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.switchOffTime','09:00','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.tcgaVcfVersion','1.1','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.to','dccnotify@list.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.subject','[DEV] Exception in QC Processing','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mailHost','mailfwd.nih.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.uuidsRequiredInXml','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotElementXPath','//aliquots/aliquot','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotBarcodeElementName','bcr_aliquot_barcode','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotUuidElementName','bcr_aliquot_uuid','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipDayElementName','day_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipMonthElementName','month_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipYearElementName','year_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.dayOfPrefix','day_of_','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.monthOfPrefix','month_of_','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.yearOfPrefix','year_of_','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.allowLocalSchema','false','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.clinicalExclusionPatterns','day_of_,month_of,year_of_,barcode,uuid','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesToValidate','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement,shipment,creation','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesToCompare','last_followup>=initial_pathologic_diagnosis','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.basisDateNameForClinical','initial_pathologic_diagnosis','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.basisDateNameForBiospecimen','index','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.elapsedElementPrefix','days_to_','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.birthDateElementName','birth','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.cdeForDatesToObscure','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesNotToObscure','creation,shipment,dcc_upload,form_completion','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.ageAtPrefix','age_at_','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.ageAtBasisDateCDE','','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.additionalFiles','manifest.txt,description.txt,changes_dcc.txt,readme_dcc.txt,dcc_altered_files.txt','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.cutoffAgeAtInitialDiagnosis','90','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.daysToBirthLowerBound','-32872','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.bcrPatientBarcodeElementName','bcr_patient_barcode','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionPath','//portions/shipment_portion','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.bcrShipmentPortionUuidElementName','bcr_shipment_portion_uuid','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.centerIdElementName','center_id','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.plateIdElementName','plate_id','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionBcrAliquotBarcodeElementName','shipment_portion_bcr_aliquot_barcode','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipDayElementName','shipment_portion_day_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipMonthElementName','shipment_portion_month_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipYearElementName','shipment_portion_year_of_shipment','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdDomainPattern','tcga-data\.nci\.nih\.gov','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdPrefixPattern','bcr','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdVersionPattern','2\.5(\.\d*)?','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.FirstGen','1.0','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.NextGen','2.3','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.RNASeq','RNASeq v1.0','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.validClinicalPlatforms','bio','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.level2CacheGenerator.scheduler.autoStartup','true','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.level2CacheGenerator.scheduler.threadCount','2','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bioTabTemplateFiles','${tcga.dcc.bioTabTemplateFiles}','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biospecimenMetadataPlatformsCronExpression','0 0 3 * * ?','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.protectedCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.publicCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bioTabTemplateFilesRootDir','/local/content/tcga/qclive/conf/schema/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.cacheFileDistroDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.tmp_cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/tmp/','','qclive','dev');

-- dam properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableDamUI','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableEmail','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromName','TCGA Data Portal','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.subjectPrefix','[DEV] TCGA Data Portal:','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.archivePhysicalPathPrefix','/tcgafiles','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.downloadLinkSite','https://tcga-data-dev.nci.nih.gov/tcgafiles','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.WSStatusCheckUrlBase','http://tcga-data-dev.nci.nih.gov/tcga/damws/jobstatus','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.jobStatusUrl','https://tcga-data-dev.nci.nih.gov/tcga/jobStatus.htm','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filePackagerQueueCount','2','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.hoursTillDeletion','1','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.notProtectedArchiveLogicalPath','ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.notProtectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.protectedArchiveLogicalPath','ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.protectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.tempfileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.emailTiming','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.failedEmailTo','tcgadccteam@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.threads.bigjobs','4','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.threads.smalljobs','4','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.smalljobmaxbytes','943718400','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.downloadSizeLimitGigs','70','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.filedeletionhours','1','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerDirectory','/tcgafiles/','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLogger.writeToDb','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultCcAddress','tcgadccteam@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromName','DCC Processing','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.enableEmail','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.subjectPrefix','[DEV] NCICB-DCC:','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.mount.root','tcgafiles','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.emailBCC','dccnotify@list.nih.gov','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enable.threadedBufferedWriter','false','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.damRefreshCronTrigger.Timer','0 59 1 * * ?','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.controlDiseaseAbbreviation','CNTL','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.subject','[DEV] Error in DAM Processing','','dam','dev');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.queue.autoStartup','true','','dam','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.queue.startupDelay','60','','dam','dev');

-- databrowser properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.cssURL','http://cmap.nci.nih.gov/Pathways/pathways.css','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.site','http://cmap.nci.nih.gov/cmapcgi/BioCartaSVG.pl','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.imagelocation','/tcgafiles/ftp_auth/temp-svg','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.tempfilelocation','/tcgafiles/ftp_auth/temp-svg','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.tooltipfile','http://tcga-portal-dev.nci.nih.gov/tcga-portal/level4_tooltips.txt','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.numberofwaits','20','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.waittimemilliseconds','500','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.waittimetodeleteinmilliseconds','30000','','databrowser','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','databrowser','dev');

-- datareports properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.enableEmail','true','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultFromName','DCC Processing','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultFromAddress','tcgadccteam@list.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultReplyTo','tcgadccteam@list.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultCcAddress','tcgadccteam@list.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.emailBCC','tcgadccteam@list.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.subjectPrefix','[DEV] NCICB-DCC:','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.failedmailto','whitmore@mail.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.server.httpColonSlashSlashHostnameAndPort','https://tcga-data-dev.nci.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.name','Ari Kahn, Ph.D.','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.email','arik@mail.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.phone','512-306-2007','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.error.to','whitmore@mail.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.error.subject','[DEV] Exception in QC Processing','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.mailHost','mailfwd.nih.gov','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.sampleSummaryRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.projectCaseDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.statsDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','datareports','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.pipelineReportJsonFilesPath','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/web/news/','','datareports','dev');

-- annotations properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'application.url','https://tcga-data-dev.nci.nih.gov/annotations','','annotations','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'rss.feed.num.months','3','','annotations','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.annotations.cacheRefreshCronFrequencyTimer','0 0 3 ? * *','','annotations','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.dcc.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','annotations','dev');

-- uuid properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.enableEmail','false','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.mailHost','mailfwd.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromName','DCC Processing','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromAddress','ncidccteam@mail.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultCcAddress','tcgadccteam@list.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.emailBCC','tcgadccteam@list.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultReplyTo','tcgadccteam@list.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.subjectPrefix','[DEV] NCICB-DCC:','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.failedmailto','dccnotify@list.nih.gov','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.maxAllowedUUIDs','100','','uuid','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.uuidBrowserRefreshCronFrequencyTimer','0 59 2 * * ?','','uuid','dev');

-- properties common to all apps

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.authentication.server.url','ldaps://ncids4a.nci.nih.gov:636/ou=nci,o=nih','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.user.search.base','','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.user.search.filter','(cn={0})','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.debugLevel','debug','','common','dev');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.to','dccnotify@list.nih.gov','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mailHost','mailfwd.nih.gov','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'dcc.error.warningEnabled','true','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.nci.support.email','ncicb@pop.nci.nih.gov','','common','dev');

commit;





