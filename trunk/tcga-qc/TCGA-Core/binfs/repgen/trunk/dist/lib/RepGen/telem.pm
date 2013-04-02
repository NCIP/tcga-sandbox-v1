# $Id: telem.pm 7628 2010-07-26 16:45:32Z jensenma $
package RepGen::telem;

=NAME

RepGen::telem - repgen.pl configuration for telemetry reports

=SYNOPSIS

Autoincluded  in repgen.pl when specified at command line:
 
 perl repgen.pl --module telem ...
  
This exports all globals as described below to the script.

=DESCRIPTION

Describe the filetype fields and report field order here.

=cut

use strict;
use warnings;
use lib '..';
use base qw(Exporter RepGen::Configure);


our @EXPORT = qw( %config %filters %fields @db_fields
                  @output_fields
                  $default_dsn $default_user $default_pwd
                  $TOO_MANY);
our (%config, %filters, %fields, @db_fields, @output_fields,
     $default_dsn, $default_user, $default_pwd,
     $TOO_MANY, $VERSION);

# defaults from base class
$VERSION = $RepGen::Configure::VERSION;
$default_dsn = $RepGen::Configure::default_dsn;
$default_user = $RepGen::Configure::default_user;
$default_pwd = $RepGen::Configure::default_pwd;
$TOO_MANY = $RepGen::Configure::TOO_MANY;

%filters = (
    sample => sub { $_ ? m/\tTCGA-/ : 0 },
    exchange => sub { $_ ? m/TCGA-/ : 0 }
    );
%fields = (
    sample => [qw( RecDate PubDate AliquotBC PatientBC SampleBC 
                   BcrCtrID Ctr)],
    exchange => [qw(RecDate PubDate AliquotBC PatientBC SampleBC 
                    BcrCtrID Ctr)]
    );

#@db_fields = qw(TumAbbrev Ctr CtrType);
@db_fields = qw(TumAbbrev CtrType);

@output_fields = qw( RecDate PubDate AliquotBC PatientBC SampleBC TumAbbrev Ctr CtrType);

# set parse parameters for  fields:

# sample accessions file:
@config{map { "sample.".$_ } @{$fields{sample}}} = 
    (
     { 
	 header => "Received Date",
	 regexp => qr/^(\d\d\d\d\-\d\d\-\d\d)T/,
	 token => 5,
	 match_posn => 1
     },
     {
	 header => "Published Date", 
	 regexp => qr/^(\d\d\d\d\-\d\d\-\d\d)T/,
	 token => 4,
	 match_posn => 1
     },
     {
	 header => 'Aliquot Barcode',
	 token => 9
     },
     {
	 header => 'Patient Barcode',
	 regexp => qr/(TCGA-\w{2}-\w{4})-\w{2}[A-Z]-\w{2}[A-Z]-\w{4}-\w{2}/, 
	 token => 9,
	 match_posn => 1
     },
     { 
	 header => 'Sample Barcode',
	 regexp => qr/(TCGA-\w{2}-\w{4}-\w{2}[A-Z])-\w{2}[A-Z]-\w{4}-\w{2}/, 
	 token => 9,
	 match_posn => 1
     },
     { 
	 header => 'BcrCtrID',
	 regexp => qr/TCGA-\w{2}-\w{4}-\w{2}[A-Z]-\w{2}[A-Z]-\w{4}-(\w{2})/,
	 token => 9, 
	 match_posn => 3
     },
     {
	 header => 'RptdCtr',
	 constant => ''
     }
    );
# exchange.tab file
# example filename token:
# TCGA-04-1347-11A-01W-0489-09_IlluminaGA-DNASeq_exome.bam

@config{map { "exchange.".$_ } @{$fields{exchange}}} = 
  (
   { 
    header => "Received Date",
    token => 4
   },
   {
    header => "Published Date", 
    constant => "BAM (no published date)"
   },
   {
    header => 'Aliquot Barcode',
    regexp =>  qr/^(TCGA-[0-9a-z-]+)/i, 
    token => 2,
    match_posn => 1
   },
   {
    header => 'Patient Barcode',
    regexp => qr/(TCGA-\w{2}-\w{4})-\w{2}[A-Z]-\w{2}[A-Z]-\w{4}-\w{2}/, 
    token => 2,
    match_posn => 1
   },
   { 
    header => 'Sample Barcode',
    regexp => qr/(TCGA-\w{2}-\w{4}-\w{2}[A-Z])-\w{2}[A-Z]-\w{4}-\w{2}/, 
    token => 2,
    match_posn => 1
   },
   { 
    header => 'BcrCtrID',
    regexp => qr/TCGA-\w{2}-\w{4}-\w{2}[A-Z]-\w{2}[A-Z]-\w{4}-(\w{2})/,
    token => 2,
    match_posn => 1
   },
   {
       header => 'Center',
       token => 0
   }

  );

# set up parameters for the looked-up fields
@config{@db_fields} =
    (
     {
	 header => 'Disease',
	 sql => 
	     "SELECT DISTINCT ti.tumor_abbreviation ".
	     "FROM biospecimen_barcode bb,".
	     " biospecimen_to_tumor btt,tumor_info ti ".
	     "WHERE bb.barcode=? AND ".
	     " bb.biospecimen_id=btt.biospecimen_id AND ".
	     " btt.tumor_id=ti.id",
	 lookup_fld => 'AliquotBC'
     },
     {
     	 header => 'Center',
     	 sql => "select a.short_name from center_info a, center_bcr_center_map b where a.id=b.center_id and b.bcr_center_id = ?",
     	 lookup_fld => 'BcrCtrID'
     },
     {
	 header => 'Center Type',
	 sql => "SELECT b.center_type FROM center_bcr_center_map b WHERE b.bcr_center_id=?",
	 lookup_fld => 'BcrCtrID'
     }

    );

1;
