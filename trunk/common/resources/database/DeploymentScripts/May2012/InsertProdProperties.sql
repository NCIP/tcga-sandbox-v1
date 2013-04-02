
-- annotations properties
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'application.url','https://tcga-data.nci.nih.gov/annotations','','annotations','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'rss.feed.num.months','3','','annotations','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.annotations.cacheRefreshCronFrequencyTimer','0 0 3 ? * *','','annotations','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.dcc.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','annotations','prod');

-- dam properties
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableDamUI','true','','dam','prod');


INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableEmail','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromName','TCGA Data Portal','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.subjectPrefix','TCGA Data Portal:','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.archivePhysicalPathPrefix','/tcgafiles','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.downloadLinkSite','https://tcga-data.nci.nih.gov/tcgafiles','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.WSStatusCheckUrlBase','http://tcga-data.nci.nih.gov/tcga/damws/jobstatus','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.jobStatusUrl','https://tcga-data.nci.nih.gov/tcga/jobStatus.htm','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filePackagerQueueCount','16','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.hoursTillDeletion','24','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.notProtectedArchiveLogicalPath','ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.notProtectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.protectedArchiveLogicalPath','ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.protectedArchivePhysicalPath','/ftp_auth/distro_ftpusers/tcga4yeo/userCreatedArchives/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.tempfileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.emailTiming','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.failedEmailTo','tcgadccteam@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.threads.bigjobs','10','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.threads.smalljobs','30','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.smalljobmaxbytes','943718400','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.downloadSizeLimitGigs','70','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.filedeletionhours','24','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerDirectory','/tcgafiles/','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLogger.writeToDb','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromAddress','ncidccteam@mail.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultReplyTo','tcgadccteam@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultCcAddress','tcgadccteam@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromName','DCC Processing','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.enableEmail','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.subjectPrefix','NCICB-DCC:','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.mount.root','tcgafiles','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.debugLevel','error','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.emailBCC','dccnotify@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enable.threadedBufferedWriter','false','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.damRefreshCronTrigger.Timer','0 59 1 * * ?','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.controlDiseaseAbbreviation','CNTL','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.to','dccnotify@list.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.subject','Error in DAM Processing','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mailHost','mailfwd.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'dcc.error.warningEnabled','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.nci.support.email','ncicb@pop.nci.nih.gov','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.queue.autoStartup','true','','dam','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.filepackager.queue.startupDelay','60','','dam','prod');

-- databrowser properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.cssURL','http://cmap.nci.nih.gov/Pathways/pathways.css','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.site','http://cmap.nci.nih.gov/cmapcgi/BioCartaSVG.pl','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.svg.imagelocation','/local/content/tcga/temp-svg','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.tempfilelocation','/local/content/tcga/temp-svg','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.tooltipfile','http://tcga-data.nci.nih.gov/web/other/include/level4_tooltips.txt','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.numberofwaits','20','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.waittimemilliseconds','500','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.export.waittimetodeleteinmilliseconds','30000','','databrowser','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dataportal.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','databrowser','prod');

-- datareports properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.enableEmail','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultFromName','DCC Processing','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultFromAddress','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultReplyTo','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.defaultCcAddress','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.emailBCC','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.subjectPrefix','NCICB-DCC:','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.failedmailto','whitmore@mail.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.server.httpColonSlashSlashHostnameAndPort','https://tcga-data.nci.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.name','Ari Kahn, Ph.D.','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.email','arik@mail.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.biMonthly.poc.phone','512-306-2007','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.error.to','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.error.subject','[PROD] Exception in QC Processing','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.mailHost','mailfwd.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.sampleSummaryRefreshCronFrequencyTimer','0 59 2 * * ?','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.projectCaseDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.statsDashboardRefreshCronFrequencyTimer','0 59 2 * * ?','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.datareports.pipelineReportJsonFilesPath','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/web/news/','','qclive','prod');

-- qclive properties
INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.enableEmail','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromAddress','NCIdccteam@mail.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultReplyTo','tcgadccteam@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.defaultFromName','TCGA Data Portal','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.portal.subjectPrefix','TCGA Data Portal:','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromAddress','NCIdccteam@mail.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultReplyTo','dccnotify@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultCcAddress','dccnotify@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.defaultFromName','DCC Processing','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.enableEmail','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.subjectPrefix','[TCGA-DCC]','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedmailto','dccnotify@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.ncbiDownloadDirectory','//tcgafiles/ftp_auth/ncbidownload','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.ncbiCronTriggerTimer','0 59 22 ? * SUN-SAT','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.gscMaterializedViewRefreshTriggerTimer','0 59 23 ? * SUN-SAT','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.piReportCronFrequencyTimer','0 0 7 ? * 2#1,2#3 2012','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.serverUrl','https://tcga-data.nci.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerDirectory','//tcgafiles/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerFileName','tcga_usage.log','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLoggerEnabled','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mvJobRunner.active','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.usageLogger.writeToDb','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.mount.root','/tcgafiles','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.debugLevel','INFO','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.waitBeforeProcessingArchive','60000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.publicDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/anonymous','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.privateDeployRoot','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.emailBCC','tcga-data-l@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.delayBeforeStartingQCLiveChecking','60000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.delayBetweenFileDoneUploadingCheck','300000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.timeToWaitForMD5','240000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.depositDirectoryPollInterval','300000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveNumberOfQueues','8','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveUploaderNumberOfQueues','8','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveWaitHours','6','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveInitialWaitMinutes','45','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveClinicalLoaderWaitMinutes','1','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveBiotabWaitMinutes','1','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveClinicalLoaderThreadCount','2','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabGeneratorDelayMinutes','1','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabGenerationCronExpression','0 0 3 ? * MON,WED,FRI','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biotabSchedulerThreadPool','10','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.md5ValidationRetryPeriod','3600000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.md5ValidationRetryAttempts','3','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLiveSubmissionWindowHours','24','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archiveLoggerLocal','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.name','The TCGA DCC Bioinformatics Team','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.email','TCGA-DCC-BINF-L@LIST.NIH.GOV','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biMonthly.poc.phone','301-451-2219','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.dataUseDisclaimerLocation','/local/content/tcga/qclive/conf/DATA_USE_DISCLAIMER.txt','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLive.Active','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.qcLive.validateXML','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.dirsToWatch','/tcgafiles/ftp_auth/deposit_ftpusers/nwchbcr,/tcgafiles/ftp_auth/deposit_ftpusers/brawo,/tcgafiles/ftp_auth/deposit_ftpusers/cgclbl,/tcgafiles/ftp_auth/deposit_ftpusers/cgcunc,/tcgafiles/ftp_auth/deposit_ftpusers/tcgabcr,/tcgafiles/ftp_auth/deposit_ftpusers/baylor,/tcgafiles/ftp_auth/deposit_ftpusers/gscbroad,/tcgafiles/ftp_auth/deposit_ftpusers/jhmi,/tcgafiles/ftp_auth/deposit_ftpusers/mskc,/tcgafiles/ftp_auth/deposit_ftpusers/stanf,/tcgafiles/ftp_auth/deposit_ftpusers/washu,/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad,/tcgafiles/ftp_auth/deposit_ftpusers/gccusc,/tcgafiles/ftp_auth/deposit_ftpusers/bcgsc,/tcgafiles/ftp_auth/deposit_ftpusers/gscucsc,/tcgafiles/ftp_auth/deposit_ftpusers/cgcmda,/tcgafiles/ftp_auth/deposit_ftpusers/gdacbroad','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bulkReceivedWorkingDirectory','/local/content/tcga/qclive/localprocessing','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archiveOfflineRoot','/tcga_arch/deposit_ftpusers','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.failedArchiveRoot','/tcga_arch/deposit_ftpusers/FAILED','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.quartzSchedulerWaitForJobsToCompleteOnShutdown','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.schedulerShouldUseDatabase','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.enabled','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.threadCount','4','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.switchOnTime','18:00','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.autoloader.switchOffTime','09:00','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.tcgaVcfVersion.regExp','1\.0','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.to','dccnotify@list.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.subject','[PRODUCTION] Exception in QC Processing','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mailHost','mailfwd.nih.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.diseaseDataSources','GBM:tcgaGBMDS,OV:tcgaOVDS,LUSC:tcgaLUSCDS,COAD:tcgaCOADDS,READ:tcgaREADDS,LAML:tcgaLAMLDS,BRCA:tcgaBRCADS,KIRC:tcgaKIRCDS,KIRP:tcgaKIRPDS,LUAD:tcgaLUADDS,UCEC:tcgaUCECDS,BLCA:tcgaBLCADS,CESC:tcgaCESCDS,HNSC:tcgaHNSCDS,LCLL:tcgaLCLLDS,LGG:tcgaLGGDS,LIHC:tcgaLIHCDS,LNNH:tcgaLNNHDS,DLBC:tcgaDLBCDS,PAAD:tcgaPAADDS,PRAD:tcgaPRADDS,SARC:tcgaSARCDS,SKCM:tcgaSKCMDS,STAD:tcgaSTADDS,THCA:tcgaTHCADS,ESCA:tcgaESCADS,CNTL:tcgaCNTLDS,KICH:tcgaKICHDS','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.uuidsRequiredInXml','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotElementXPath','//aliquots/aliquot','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotBarcodeElementName','bcr_aliquot_barcode','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.aliquotUuidElementName','bcr_aliquot_uuid','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipDayElementName','day_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipMonthElementName','month_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipYearElementName','year_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.dayOfPrefix','day_of_','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.monthOfPrefix','month_of_','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.yearOfPrefix','year_of_','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.allowLocalSchema','false','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.clinicalExclusionPatterns','day_of_,month_of,year_of_,barcode,uuid','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesToValidate','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement,shipment,creation','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesToCompare','last_followup>=initial_pathologic_diagnosis','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.basisDateNameForClinical','initial_pathologic_diagnosis','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.basisDateNameForBiospecimen','index','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.elapsedElementPrefix','days_to_','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.birthDateElementName','birth','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.cdeForDatesToObscure','birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.datesNotToObscure','creation,shipment,dcc_upload,form_completion','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.ageAtPrefix','age_at_','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.ageAtBasisDateCDE','','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.archive.additionalFiles','manifest.txt,description.txt,changes_dcc.txt,readme_dcc.txt,dcc_altered_files.txt','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.cutoffAgeAtInitialDiagnosis','90','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.daysToBirthLowerBound','-32872','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.bcrPatientBarcodeElementName','bcr_patient_barcode','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionPath','//portions/shipment_portion','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.bcrShipmentPortionUuidElementName','bcr_shipment_portion_uuid','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.centerIdElementName','center_id','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.plateIdElementName','plate_id','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionBcrAliquotBarcodeElementName','shipment_portion_bcr_aliquot_barcode','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipDayElementName','shipment_portion_day_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipMonthElementName','shipment_portion_month_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.shipmentPortionShipYearElementName','shipment_portion_year_of_shipment','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdDomainPattern','tcga-data\.nci\.nih\.gov','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdPrefixPattern','bcr','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bcr.validXsdVersionPattern','2\.5(\.\d*)?','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.FirstGen','1.0','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.NextGen','2.2','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mafVersion.RNASeq','RNASeq v1.0','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.validClinicalPlatforms','bio','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.level2CacheGenerator.scheduler.autoStartup','true','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.level2CacheGenerator.scheduler.threadCount','2','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.minExpectedRowsToUseHintQuery','96000000','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.cacheFileDistroDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.tmp_cachefileDirectory','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/cachefiles/tmp/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.protectedCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.publicCacheFilesRootDir','/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bioTabTemplateFilesRootDir','/local/content/tcga/qclive/conf/schema/','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.bioTabTemplateFiles','${tcga.dcc.bioTabTemplateFiles}','','qclive','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.biospecimenMetadataPlatformsCronExpression','0 0 3 * * ?','','qclive','prod');

-- uuid properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.enableEmail','false','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.mailHost','mailfwd.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromName','DCC Processing','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromAddress','ncidccteam@mail.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultCcAddress','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.emailBCC','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultReplyTo','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.subjectPrefix','NCICB-DCC:','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.failedmailto','dccnotify@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.maxAllowedUUIDs','100','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.uuid.uuidBrowserRefreshCronFrequencyTimer','0 59 2 * * ?','','uuid','prod');

-- uuid properties

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.enableEmail','false','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.mailHost','mailfwd.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromName','DCC Processing','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultFromAddress','ncidccteam@mail.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultCcAddress','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.emailBCC','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.defaultReplyTo','tcgadccteam@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.subjectPrefix','NCICB-DCC:','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.failedmailto','dccnotify@list.nih.gov','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.maxAllowedUUIDs','100','','uuid','prod');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name)
VALUES (dcc_property_seq.nextval,'tcga.uuid.uuidBrowserRefreshCronFrequencyTimer','0 59 2 * * ?','','uuid','prod');

-- properties common to multiple apps

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.authentication.server.url','ldaps://ncids4a.nci.nih.gov:636/ou=nci,o=nih','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.user.search.base','','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'ldap.user.search.filter','(cn={0})','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.error.to','dccnotify@list.nih.gov','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.mailHost','mailfwd.nih.gov','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'dcc.error.warningEnabled','true','','common','dev');

INSERT INTO dcc_property(property_id,property_name,property_value,property_description,application_name,server_name) 
VALUES (dcc_property_seq.nextval,'tcga.dcc.nci.support.email','ncicb@pop.nci.nih.gov','','common','dev');

commit;
