#!/bin/perl -w
####################################################################################################
#	BCRregex.pm
#	Tue Jun 14 12:55:00 EDT 2011
####################################################################################################

use strict;
require Exporter;
our @ISA = qw(Exporter );
our @EXPORT = qw(  );

our ( $pgm_name, $VERSION, $rel_date, $start_date );

####################################################################################################
    ALIQUOT_BARCODE_REGEXP  = "((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-((\d{2})([A-Z]{1}))-([A-Z0-9]{4})-(\d{2}))";
    ANALYTE_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-((\d{2})([A-Z]{1})))$";
    PATIENT_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4}))$";
    PORTION_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-(\d{2}))$";
    SAMPLE_BARCODE_REGEXP  = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1})))$";
    SLIDE_BARCODE_REGEXP   = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-(\d{2})-(([T|M|B]S)([A-Z0-9])))$";
    HEX_REGEXP = "[0-9a-fA-F]";
    DRUG_BARCODE_REGEXP   = "TCGA-.*";
    RADIATION_BARCODE_REGEXP   = "TCGA-.*";
    EXAMINATION_BARCODE_REGEXP   = "TCGA-.*";
    SURGERY_BARCODE_REGEXP   = "TCGA-.*";

    UUID_REGEXP = HEX_REGEXP + "{8}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{12}";
    UUID_REGEXP_STRICT = "^" + UUID_REGEXP + "$";



1;
