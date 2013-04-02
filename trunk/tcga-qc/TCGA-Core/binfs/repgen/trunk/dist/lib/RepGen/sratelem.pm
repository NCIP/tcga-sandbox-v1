# $Id: sratelem.pm 7628 2010-07-26 16:45:32Z jensenma $
package RepGen::sratelem;

=NAME

RepGen::sratelem - repgen.pl configuration for SRA telemetry reports

=SYNOPSIS

Autoincluded  in repgen.pl when specified at command line:
 
 perl repgen.pl sratelem ...
  
This exports all globals as described below to the script.

=DESCRIPTION

Describe the filetype fields and report field order here.

global 
______
%config
%filters
%fields
%na_text

=cut

use strict;
use warnings;
use lib '..';
use base qw(Exporter RepGen::Configure);


our @EXPORT = qw( %config %filters %fields %na_text 
                  @db_fields
                  @output_fields
                  $default_dsn $default_user $default_pwd
                  $TOO_MANY);
our (%config, %filters, %fields, %na_text, @db_fields, @output_fields,
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
    );
%fields = (
    sample => [qw( RecDate PubDate AliquotBC PatientBC SampleBC BcrCtrID)]
    );
%na_text = (
    sample => { PubDate => 'no publication date in SRA' }
);

@db_fields = qw(TumAbbrev Ctr CtrType);

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
	 match_posn => 1
     }
    );

# set up parameters for the looked-up fields
# try this: use an attribute 'tbl' that contains a hash that directly
# relates the lookup field to the values. If 'sql' is under, default to the
# lookup table in 'tbl'
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
#      sql => "select a.short_name from center_info a, center_bcr_center_map b where a.id=b.center_id and b.bcr_center_id = ?",
      tbl => {
	      '01' =>	'BI', 	
	      '02' =>	'HMS', 	
	      '03' =>	'LBL', 	
	      '04' =>	'MSKCC', 	
	      '05' =>	'JHU_USC',
	      '06' =>	'HAIB', 	
	      '07' =>	'UNC',	
	      '08' =>	'BI',
	      '09' =>	'WUSM',
	      '10' =>	'BCM',	
	      '12' =>	'BCM', 
	      '13' =>	'BCGSC', 	
	      '14' =>	'BI', 	
	      '15' =>	'ISB', 	
	      '16' =>	'LBL', 	
	      '17' =>	'MSKCC', 	
	      '18' =>	'UCSC', 	
	      '19' =>	'MDA'
	     }, # as defined on wiki 07/19/10
     	 lookup_fld => 'BcrCtrID'
     },
     {
      header => 'Center Type',
#      sql => "SELECT b.center_type FROM center_bcr_center_map b WHERE b.bcr_center_id=?",
      tbl => {
	      '01' => 	'CGCC',
	      '02' => 	'CGCC',
	      '03' => 	'CGCC',
	      '04' => 	'CGCC',
	      '05' => 	'CGCC',
	      '06' => 	'CGCC',
	      '07' => 	'CGCC',
	      '08' => 	'GSC', 
	      '09' => 	'GSC',
	      '10' => 	'GSC',
	      '12' => 	'CGCC',
	      '13' => 	'CGCC',
	      '14' => 	'GDAC',
	      '15' => 	'GDAC',
	      '16' => 	'GDAC',
	      '17' => 	'GDAC',
	      '18' => 	'GDAC',
	      '19' => 	'GDAC'
       },
      lookup_fld => 'BcrCtrID'
     }

    );

1;
