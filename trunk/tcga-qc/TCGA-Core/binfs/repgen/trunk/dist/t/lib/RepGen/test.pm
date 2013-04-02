#$Id: test.pm 7163 2010-06-07 03:37:05Z jensenma $
# test configuration class for repgen.pl
package RepGen::test;
use strict;
use warnings;
use lib 'lib';
use lib '../lib';
use lib '../../lib';
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
	    test1 => sub { !/^Accession/ },
	    test2 => sub { !/^center program/ }
);
%fields = (
	   test1 => [qw(id status date type)],
	   test2 => [qw(id status date type size)]
);
@db_fields = qw(disease);
@output_fields = qw(id status type size date disease);

@config{ map { "test1.".$_ } @{$fields{test1}}} = 
  (
   { header => "Id", token => 0 },
   { header => "Status", token => 2  },
   { header => "Date", token => 3, regexp => qr/^([0-9-]+)/, match_posn => 1 },
   { header => "Type", token => 8 }
  );

@config{ map { "test2.".$_ } @{$fields{test2}}} = 
  (
   { header => "Id", token => 2, regexp => qr/^(TCGA-[0-9a-z-]+)/i, match_posn => 1 },
   { header => "Status", constant => "submitted" },
   { header => "Date", token => 4 }, 
   { header => "Type", constant => "bam"},
   { header => "Size", token => 3}
);

@config{ @db_fields } = 
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
    lookup_fld => 'id'
   }
  );

1;




