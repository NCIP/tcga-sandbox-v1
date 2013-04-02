# -*- perl -*-
# $Id: 001_load.t 17675 2012-09-18 03:11:28Z jensenma $
# t/001_load.t - check module loading and create testing directory

use Test::More tests => 36;
use lib '../lib';
BEGIN { 
    use_ok( 'TCGA::Barcodes');
}

my $bcf = TCGA::BCFactory->new('db');
isa_ok ($bcf, 'TCGA::BCFactory');

# also check failing barcodes
ok $bc = $bcf->get_barcode('TCGA-BL-A0C8-01A-11D-A10R-02'), "make a barcode object with a known good barcode";
isa_ok( $bc, 'TCGA::Barcode');
isa_ok( $bc, 'TCGA::Barcode::db');

my $db_connected = $bc->dbh->ping;

SKIP : {
    skip "db not connected", 28 unless $db_connected;
    is $bc->barcode, 'TCGA-BL-A0C8-01A-11D-A10R-02', "correct barcode";
    is $bc->disease, "BLCA", "correct disease type";
    is $bc->batch, 86, "correct batch";
    is $bc->center, 'HMS', "correct receiving center";
    is $bc->uuid, "ab020f79-ba66-457c-9d77-ddd87c799538", "correct uuid";
    is $bc->item, "aliquot", "correct type";
    
    ok $bc = $bcf->get_barcode('ab020f79-ba66-457c-9d77-ddd87c799538'), "try with uuid";
    isa_ok( $bc, 'TCGA::Barcode');
    is $bc->uuid, "ab020f79-ba66-457c-9d77-ddd87c799538", "correct uuid";
    is $bc->disease, "BLCA", "correct disease type";
    is $bc->batch, 86, "correct batch";
    is $bc->center, 'HMS', "correct receiving center";
    is $bc->barcode,'TCGA-BL-A0C8-01A-11D-A10R-02', "correct barcode";
    is $bc->item, "aliquot", "correct type";
    
# validity
    ok $bc->is_valid, "valid barcode is valid";
    $bc = $bcf->get_barcode('TCGA-A3-3306-11A-01D-0858-01');
    ok $bc->is_valid, "valid barcode before explicit query";
    
    $bc = $bcf->get_barcode('TCGA-99-9999');
    ok !$bc->is_valid, "invalid barcode is not valid";
    
    $bc = $bcf->get_barcode('TCGA-A3-3306-11A');
    is $bc->item, 'sample', "correct type (sample)";
    $bc = $bcf->get_barcode('TCGA-A3-3306-11A-01');
    is $bc->item, 'portion', "correct type (portion)";
    $bc = $bcf->get_barcode('TCGA-A3-3306-11A-01D');
    is $bc->item, 'analyte', "correct type (analyte)";
    $bc = $bcf->get_barcode('TCGA-A3-3306');
    ok $bc->is_valid, "a valid patient barcode is valid";
    is $bc->item, 'participant', "correct type (pt)";

    my @desc = $bc->get_descendants;
    is scalar(grep { $_->item eq 'aliquot' } @desc), 17, 'got correct number of aliquots for TCGA-A3-3306';
    1;
}
    
# oneliner
ok defined TCGA::BCFactory->new('ws')->get_barcode('TCGA-AB-2930-03A-01W-0761-09')->is_valid;

# functional interface
is barcode('TCGA-BL-A0C8-01A-11D-A10R-02'), 'TCGA-BL-A0C8-01A-11D-A10R-02', "correct barcode";
is disease('TCGA-BL-A0C8-01A-11D-A10R-02'), "BLCA", "correct disease type";
is batch('TCGA-BL-A0C8-01A-11D-A10R-02'), 86, "correct batch";
is center('TCGA-BL-A0C8-01A-11D-A10R-02'), 'HMS', "correct receiving center";
is uuid('TCGA-BL-A0C8-01A-11D-A10R-02'), "ab020f79-ba66-457c-9d77-ddd87c799538", "correct uuid";
is item('TCGA-BL-A0C8-01A-11D-A10R-02'), "aliquot", "correct type";
ok is_valid('TCGA-B6-A2IU'), "valid barcode is valid";

1;
