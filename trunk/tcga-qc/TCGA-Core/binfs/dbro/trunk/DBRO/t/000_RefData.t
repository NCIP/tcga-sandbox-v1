# -*- perl -*-

use Test::More 'no_plan';
use Test::Exception;
use File::Spec;
use lib '../lib';
use strict;
use warnings;

my $TESTDATA = ( -e '../t/samples' ? '../t/samples' : 't/samples' );
my $SLOWTEST = 1 unless $ENV{FAST_TESTS_ONLY};

BEGIN { 
    $ENV{REFDATA_DIR} = ( -e '../refdata' ? '../refdata' : 'refdata');
    use_ok( 'TCGA::CNV::RefData' );
}
SKIP: {
    skip "slow tests disabled", 4 unless $SLOWTEST;
    ok my $refdata = TCGA::CNV::RefData->new('bins200K','small','genome2gene','genome'),"load refdata to obj";
    isa_ok($refdata, 'TCGA::CNV::RefData');
    is ref $refdata->bins,'HASH', "get hash ref back";
    is ref $refdata->genes, 'HASH', "get hash ref back";
}
