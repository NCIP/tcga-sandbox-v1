####################################################################################################
#	TCGA.pm
#	Data and routines associated with TGCA project.
####################################################################################################
# $Id: TCGA.pm 18048 2013-01-24 16:51:56Z snyderee $
package TCGA;
use strict;
use warnings;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT = 	qw( $studyName %studyAnnoCat %studyAnnoClassCat2CatOnly %studyAnnoCat2class %studyItemType
					%studyDisease %studyAnnoFields @mskHeading @dataDictionaryHeadings %itemBarcodeRegex
					@annotationLoaderPreferredHeadingOrder );

use vars 		qw( $studyName %studyAnnoCat %studyAnnoClassCat2CatOnly %studyAnnoCat2class %studyItemType
					%studyDisease %studyAnnoFields @mskHeading @dataDictionaryHeadings %itemBarcodeRegex
					@annotationLoaderPreferredHeadingOrder );

our $studyName = "tcga";

# from TCGADEV:DISEASE: DISEASE_ABBREVIATION, DISEASE_NAME
# tail +2 ~/projects/nih/annotations/tables/DISEASE.tsv | perl -F'\t' -ane 'chomp @F; print "\t\"$F[0]\"\t=>\t\"$F[1]\",\n"' | sort
our %studyDisease =	(
	"BLCA"	=>	"Bladder Urothelial Carcinoma",
	"BRCA"	=>	"Breast invasive carcinoma",
	"CESC"	=>	"Cervical squamous cell carcinoma and endocervical adenocarcinoma",
	"CNTL"	=>	"Controls",
	"COAD"	=>	"Colon adenocarcinoma",
	"DLBC"	=>	"Lymphoid Neoplasm Diffuse Large B-cell Lymphoma",
	"ESCA"	=>	"Esophageal carcinoma ",
	"GBM"	=>	"Glioblastoma multiforme",
	"HNSC"	=>	"Head and Neck squamous cell carcinoma",
	"KICH"	=>	"Kidney Chromophobe",
	"KIRC"	=>	"Kidney renal clear cell carcinoma",
	"KIRP"	=>	"Kidney renal papillary cell carcinoma",
	"LAML"	=>	"Acute Myeloid Leukemia",
	"LCLL"	=>	"Chronic Lymphocytic Leukemia",
	"LGG"	=>	"Brain Lower Grade Glioma",
	"LIHC"	=>	"Liver hepatocellular carcinoma",
	"LUAD"	=>	"Lung adenocarcinoma",
	"LUSC"	=>	"Lung squamous cell carcinoma",
	"MESO"	=>	"Mesothelioma",
	"OV"	=>	"Ovarian serous cystadenocarcinoma",
	"PAAD"	=>	"Pancreatic adenocarcinoma",
	"PRAD"	=>	"Prostate adenocarcinoma",
	"READ"	=>	"Rectum adenocarcinoma",
	"SARC"	=>	"Sarcoma",
	"SKCM"	=>	"Skin Cutaneous Melanoma",
	"STAD"	=>	"Stomach adenocarcinoma",
	"THCA"	=>	"Thyroid carcinoma",
	"UCEC"	=>	"Uterine Corpus Endometrioid Carcinoma",
	"MASTER"=>	"TCGA master study configuration"
);

# from TCGADEV:ANNOTATION_CATEGORY: CATEGORY_DISPLAY_NAME, CATEGORY_DESCRIPTION
# tail +2 ~/projects/nih/annotations/tables/ANNOTATION_CATEGORY.tsv | perl -F'\t' -ane 'chomp @F; print "\t\"$F[0]\"\t\t\t=>\t\"$F[1]\",\n"' | sort
our %studyAnnoCat = (
	"Administrative Compliance"					=>	"Redaction:Administrative Compliance",
	"Alternate sample pipeline"					=>	"Notification:Alternate sample pipeline",
	"Case submitted is found to be a recurrence after submission"
												=>	"Notification:Case submitted is found to be a recurrence after submission",
	"Center QC failed"							=>	"CenterNotification:Center QC failed",
	"Clinical data insufficient"				=>	"Notification:Clinical data insufficient",
	"Duplicate case"							=>	"Redaction:Duplicate case",
	"Duplicate item"							=>	"Notification:Duplicate item",
	"General"									=>	"Observation:General",
	"Genotype mismatch"							=>	"Redaction:Genotype mismatch",
	"History of acceptable prior treatment related to a prior/other malignancy"
												=>	"Notification:History of acceptable prior treatment related to a prior/other malignancy",
	"History of unacceptable prior treatment related to a prior/other malignancy"
												=>	"Notification:History of unacceptable prior treatment related to a prior/other malignancy",
	"Item does not meet study protocol"			=>	"Notification:Item does not meet study protocol",
	"Item flagged DNU"							=>	"CenterNotification:Item flagged DNU",
	"Item in special subset"					=>	"Notification:Item in special subset",
	"Item is noncanonical"						=>	"Notification:Item is noncanonical",
	"Item may not meet study protocol"			=>	"Observation:Item may not meet study protocol",
	"Molecular analysis outside specification"	=>	"Notification:Molecular analysis outside specification",
	"Neoadjuvant therapy"						=>	"Notification:Neoadjuvant therapy",
	"New notification type"						=>	"Notification:New notification type",
	"New observation type"						=>	"Observation:New observation type",
	"Normal class but appears diseased"			=>	"Observation:Normal class but appears diseased",
	"Normal tissue origin incorrect"			=>	"Notification:Normal tissue origin incorrect",
	"Pathology outside specification"			=>	"Notification:Pathology outside specification",
	"Permanently missing item or object"		=>	"Notification:Permanently missing item or object",
	"Prior malignancy"							=>	"Notification:Prior malignancy",
	"Qualification metrics changed"				=>	"Notification:Qualification metrics changed",
	"Qualified in error"						=>	"Notification:Qualified in error",
	"Sample compromised"						=>	"Notification:Sample compromised",
	"Subject identity unknown"					=>	"Redaction:Subject identity unknown",
	"Subject withdrew consent"					=>	"Redaction:Subject withdrew consent",
	"Synchronous malignancy"					=>	"Notification:Synchronous malignancy",
	"Tumor class but appears normal"			=>	"Observation:Tumor class but appears normal",
	"Tumor tissue origin incorrect"				=>	"Redaction:Tumor tissue origin incorrect",
	"Tumor type incorrect"						=>	"Redaction:Tumor type incorrect",
	"WGA Failure"								=>	"Notification:WGA Failure",
);

# !tail +2 $annotations/ANNOTATION_CATEGORY.tsv | perl -F'\t' -ane 'chomp @F;$f=$F[1];$f=~s/^.[^:]+://;print "\t\"$F[1]\"\t\t\t=>\t\"$f\",\n";'
our %studyAnnoClassCat2CatOnly = (
	"CenterNotification:Center QC failed"							=>	"Center QC failed",
	"CenterNotification:Item flagged DNU"							=>	"Item flagged DNU",
	"Notification:Alternate sample pipeline"						=>	"Alternate sample pipeline",
	"Notification:Case submitted is found to be a recurrence after submission"
																	=>	"Case submitted is found to be a recurrence after submission",
	"Notification:Clinical data insufficient"						=>	"Clinical data insufficient",
	"Notification:Duplicate item"									=>	"Duplicate item",
	"Notification:History of acceptable prior treatment related to a prior/other malignancy"
																	=>	"History of acceptable prior treatment related to a prior/other malignancy",
	"Notification:History of unacceptable prior treatment related to a prior/other malignancy"
																	=>	"History of unacceptable prior treatment related to a prior/other malignancy",
	"Notification:Item does not meet study protocol"				=>	"Item does not meet study protocol",
	"Notification:Item in special subset"							=>	"Item in special subset",
	"Notification:Item is noncanonical"								=>	"Item is noncanonical",
	"Notification:Molecular analysis outside specification"			=>	"Molecular analysis outside specification",
	"Notification:Neoadjuvant therapy"								=>	"Neoadjuvant therapy",
	"Notification:New notification type"							=>	"New notification type",
	"Notification:Normal tissue origin incorrect"					=>	"Normal tissue origin incorrect",
	"Notification:Pathology outside specification"					=>	"Pathology outside specification",
	"Notification:Permanently missing item or object"				=>	"Permanently missing item or object",
	"Notification:Prior malignancy"									=>	"Prior malignancy",
	"Notification:Qualification metrics changed"					=>	"Qualification metrics changed",
	"Notification:Qualified in error"								=>	"Qualified in error",
	"Notification:Sample compromised"								=>	"Sample compromised",
	"Notification:Synchronous malignancy"							=>	"Synchronous malignancy",
	"Notification:WGA Failure"										=>	"WGA Failure",
	"Observation:General"											=>	"General",
	"Observation:Item may not meet study protocol"					=>	"Item may not meet study protocol",
	"Observation:New observation type"								=>	"New observation type",
	"Observation:Normal class but appears diseased"					=>	"Normal class but appears diseased",
	"Observation:Tumor class but appears normal"					=>	"Tumor class but appears normal",
	"Redaction:Administrative Compliance"							=>	"Administrative Compliance",
	"Redaction:Duplicate case"										=>	"Duplicate case",
	"Redaction:Genotype mismatch"									=>	"Genotype mismatch",
	"Redaction:Subject identity unknown"							=>	"Subject identity unknown",
	"Redaction:Subject withdrew consent"							=>	"Subject withdrew consent",
	"Redaction:Tumor tissue origin incorrect"						=>	"Tumor tissue origin incorrect",
	"Redaction:Tumor type incorrect"								=>	"Tumor type incorrect",
);

# !tail +2 $annotations/ANNOTATION_CATEGORY.tsv | perl -F'\t' -ane 'chomp @F;$F[1]=~m/^([^:]+):(.+)$/;print "\t\"$2\"\t\t\t=>\t\"$1\",\n";' | sort
our %studyAnnoCat2class = (
	"Administrative Compliance"														=>	"Redaction",
	"Alternate sample pipeline"														=>	"Notification",
	"Case submitted is found to be a recurrence after submission"					=>	"Notification",
	"Center QC failed"																=>	"CenterNotification",
	"Clinical data insufficient"													=>	"Notification",
	"Duplicate case"																=>	"Redaction",
	"Duplicate item"																=>	"Notification",
	"General"																		=>	"Observation",
	"Genotype mismatch"																=>	"Redaction",
	"History of acceptable prior treatment related to a prior/other malignancy"		=>	"Notification",
	"History of unacceptable prior treatment related to a prior/other malignancy"	=>	"Notification",
	"Item does not meet study protocol"												=>	"Notification",
	"Item flagged DNU"																=>	"CenterNotification",
	"Item in special subset"														=>	"Notification",
	"Item is noncanonical"															=>	"Notification",
	"Item may not meet study protocol"												=>	"Observation",
	"Molecular analysis outside specification"										=>	"Notification",
	"Neoadjuvant therapy"															=>	"Notification",
	"New notification type"															=>	"Notification",
	"New observation type"															=>	"Observation",
	"Normal class but appears diseased"												=>	"Observation",
	"Normal tissue origin incorrect"												=>	"Notification",
	"Pathology outside specification"												=>	"Notification",
	"Permanently missing item or object"											=>	"Notification",
	"Prior malignancy"																=>	"Notification",
	"Qualification metrics changed"													=>	"Notification",
	"Qualified in error"															=>	"Notification",
	"Sample compromised"															=>	"Notification",
	"Subject identity unknown"														=>	"Redaction",
	"Subject withdrew consent"														=>	"Redaction",
	"Synchronous malignancy"														=>	"Notification",
	"Tumor class but appears normal"												=>	"Observation",
	"Tumor tissue origin incorrect"													=>	"Redaction",
	"Tumor type incorrect"															=>	"Redaction",
	"WGA Failure"																	=>	"Notification",
);

# from TCGADEV:ANNOTATION_ITEM_TYPE: TYPE_DISPLAY_NAME, TYPE_DESCRIPTION
our %studyItemType = (
	'Shipped Portion'	=>	"A shipped portion",
	'Aliquot'			=>	"An aliquot",
	'Analyte'			=>	"An analyte",
	'Patient'			=>	"A patient",
	'Portion'			=>	"A portion",
	'Sample'			=>	"A sample",
	'Slide'				=>	"A slide",
);

our @annotationLoaderPreferredHeadingOrder = 	(	"longDisease", "disease", "item", "itemType",
													"annotationCategory", "annotationNote"
												);

our %studyAnnoFields = (
	'itemType'				=>	\%studyItemType,
	'disease'				=>	\%studyDisease,
	'annotationCategory'	=>	\%studyAnnoCat,
);

our @dataDictionaryHeadings = qw( VARNAME VARDESC TYPE VALUES );

our @mskHeading = qw( sample chrom loc.start loc.end num.mark num.informative seg.mean pval l.lcl l.ucl r.pval r.lcl r.ucl );

our %itemBarcodeRegex = (
	'Aliquot'				=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]-\d{2}[A-Z]-[0-9A-Z]{4}-\d{2}',
	'Analyte'				=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]-\d{2}[A-Z]',
	'Patient'				=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}',
	'Portion'				=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]-\d{2}',
	'Sample'				=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]',
	'Shipped Portion'		=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]-\d{2}-[0-9A-Z]{4}-\d{2}',
	'Slide'					=>	'TCGA-[0-9A-Z]{2}-[0-9A-Z]{4}-\d{2}[A-Z]-\d{2}-[MBTD][SX][0-9A-Z]\d{0,1}',
);

our $uuid_regex_case_insensitive = '[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}';
our $uuid_regex_lowercase		 = '[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}';
# 3D6B2D82-2F6D-40E6-8138-4DBF33DE8BEF
# 12345678 1234 1234 1234 123456789012
#if( /^.+\.([A-Fa-f0-9]{8}\-[A-Fa-f0-9]{4}\-[A-Fa-f0-9]{4}\-[A-Fa-f0-9]{4}\-[A-Fa-f0-9]{12})/$1/ ){print $_}else{print "-" x 80 . "\n"}

1;
