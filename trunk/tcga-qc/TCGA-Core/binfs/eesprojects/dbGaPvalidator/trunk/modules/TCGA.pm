####################################################################################################
#	TCGA.pm
#	Data and routines associated with TGCA project.
####################################################################################################
# $id$
package TCGA;
use strict;
use warnings;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT = 	qw( $studyName %studyAnnoCat %studyItemType %studyDisease %studyAnnoFields @mskHeading );
#our @EXPORT_OK = qw( %dStudy $studyName );
use vars 		qw( $studyName %studyAnnoCat %studyItemType %studyDisease %studyAnnoFields @mskHeading );

our $studyName = "tcga";

# from TCGADEV:DISEASE: DISEASE_ABBREVIATION, DISEASE_NAME
our %studyDisease =	(
	"BLCA"	=>	"Bladder Urothelial Carcinoma",
	"BRCA"	=>	"Breast invasive carcinoma",
	"CESC"	=>	"Cervical squamous cell carcinoma and endocervical adenocarcinoma",
	"COAD"	=>	"Colon adenocarcinoma",
	"DLBC"	=>	"Lymphoid Neoplasm Diffuse Large B-cell Lymphoma",
	"ESCA"	=>	"Esophageal carcinoma ",
	"GBM"	=>	"Glioblastoma multiforme",
	"HNSC"	=>	"Head and Neck squamous cell carcinoma",
	"KIRC"	=>	"Kidney renal clear cell carcinoma",
	"KIRP"	=>	"Kidney renal papillary cell carcinoma",
	"LAML"	=>	"Acute Myeloid Leukemia",
	"LCLL"	=>	"Chronic Lymphocytic Leukemia",
	"LGG"	=>	"Brain Lower Grade Glioma",
	"LIHC"	=>	"Liver hepatocellular carcinoma",
	"LNNH"	=>	"Lymphoid Neoplasm Non-Hodgkins Lymphoma",
	"LUAD"	=>	"Lung adenocarcinoma",
	"LUSC"	=>	"Lung squamous cell carcinoma",
	"OV"	=>	"Ovarian serous cystadenocarcinoma",
	"PAAD"	=>	"Pancreatic adenocarcinoma",
	"PRAD"	=>	"Prostate adenocarcinoma",
	"READ"	=>	"Rectum adenocarcinoma",
	"SALD"	=>	"Sarcoma",
	"SKCM"	=>	"Skin Cutaneous Melanoma",
	"STAD"	=>	"Stomach adenocarcinoma",
	"THCA"	=>	"Thyroid carcinoma",
	"UCEC"	=>	"Uterine Corpus Endometrioid Carcinoma",
);

our @studyAnnoCat = (
	"Tumor tissue origin incorrect",
	"Tumor type incorrect",
	"Genotype mismatch",
	"Subject withdrew consent",
	"Subject identity unknown",
	"Prior malignancy",
	"Neoadjuvant therapy",
	"Qualification metrics changed",
	"Pathology outside specification",
	"Molecular analysis outside specification",
	"Duplicate item",
	"Sample compromised",
	"Clinical data insufficient",
	"Item does not meet study protocol",
	"Item in special subset",
	"Qualified in error",
	"Item is noncanonical",
	"New notification type",
	"Tumor class but appears normal",
	"Normal class but appears diseased",
	"Item may not meet study protocol",
	"New observation type",
	"Duplicate case",
	"Center QC failed",
	"Item flagged DNU",
	"General",
	"Permanently missing item or object",
	"Normal tissue origin incorrect",
	"Administrative Compliance",
);

# from TCGADEV:ANNOTATION_CATEGORY: CATEGORY_DISPLAY_NAME, CATEGORY_DESCRIPTION
our %studyAnnoCat = (
	'Tumor tissue origin incorrect'				=>	"Redaction:Tumor tissue origin incorrect",
	'Tumor type incorrect'						=>	"Redaction:Tumor type incorrect",
	'Genotype mismatch'							=>	"Redaction:Genotype mismatch",
	'Subject withdrew consent'					=>	"Redaction:Subject withdrew consent",
	'Subject identity unknown'					=>	"Redaction:Subject identity unknown",
	'Prior malignancy'							=>	"Notification:Prior malignancy",
	'Neoadjuvant therapy'						=>	"Notification:Neoadjuvant therapy",
	'Qualification metrics changed'				=>	"Notification:Qualification metrics changed",
	'Pathology outside specification'			=>	"Notification:Pathology outside specification",
	'Molecular analysis outside specification'	=>	"Notification:Molecular analysis outside specification",
	'Duplicate item'							=>	"Notification:Duplicate item",
	'Sample compromised'						=>	"Notification:Sample compromised",
	'Clinical data insufficient'				=>	"Notification:Clinical data insufficient",
	'Item does not meet study protocol'			=>	"Notification:Item does not meet study protocol",
	'Item in special subset'					=>	"Notification:Item in special subset",
	'Qualified in error'						=>	"Notification:Qualified in error",
	'Item is noncanonical'						=>	"Notification:Item is noncanonical",
	'New notification type'						=>	"Notification:New notification type",
	'Tumor class but appears normal'			=>	"Observation:Tumor class but appears normal",
	'Normal class but appears diseased'			=>	"Observation:Normal class but appears diseased",
	'Item may not meet study protocol'			=>	"Observation:Item may not meet study protocol",
	'New observation type'						=>	"Observation:New observation type",
	'Duplicate case'							=>	"Redaction:Duplicate case",
	'Center QC failed'							=>	"CenterNotification:Center QC failed",
	'Item flagged DNU'							=>	"CenterNotification:Item flagged DNU",
	'General'									=>	"Observation:General",
	'Permanently missing item or object'		=>	"Permanently missing item or object",
	'Normal tissue origin incorrect'			=>	"Normal tissue origin incorrect",
	'Administrative Compliance'					=>	"Redaction:Administrative Compliance",
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

our %studyAnnoFields = (
	'itemType'				=>	\%studyItemType,
	'disease'				=>	\%studyDisease,
	'annotationCategory'	=>	\%studyAnnoCat,
);

our @mskHeading = qw( sample chrom loc.start loc.end num.mark num.informative seg.mean pval l.lcl l.ucl r.pval r.lcl r.ucl );

1;
