# -*- perl -*-

use Test::More tests => 16;
use Test::Exception;
use File::Spec;
use lib '../lib';
use strict;
use warnings;

my $TESTDATA = ( -e '../t/samples' ? '../t/samples' : 't/samples' );
my $SLOWTEST = 1 unless $ENV{FAST_TESTS_ONLY};
my $DO_BDB_TESTS = 0 unless $ENV{DBRO_DO_BDB_TESTS};

BEGIN { 
    $ENV{REFDATA_DIR} = ( -e '../refdata' ? '../refdata' : 'refdata');
    use_ok( 'TCGA::CNV' );
    use_ok( 'TCGA::CNV::Segment' );
    use_ok( 'TCGA::CNV::RefData::BDB' );
    use_ok( 'TCGA::CNV::SegToGene' );
    use_ok( 'TCGA::CNV::SegIO' );
}

SKIP : {
    skip "BDB class tests disabled", 11 unless $DO_BDB_TESTS;
$TCGA::CNV::SegToGene::VERBOSE = 0;

my $seg = TCGA::CNV::Segment->new();
isa_ok($seg, 'TCGA::CNV::Segment');
my $refdata = TCGA::CNV::RefData::BDB->new('bins10K','small','genome2gene','genome');
isa_ok($refdata, 'TCGA::CNV::RefData::BDB');
isa_ok($refdata, 'TCGA::CNV::RefData');

is ref $refdata->bins,'HASH', "get hash ref back";
is ref $refdata->genes, 'HASH', "get hash ref back";


ok my $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA, "PAAD.cna.level4.test.txt"));

ok my $analysis = TCGA::CNV::SegToGene->new($refdata);

SKIP : {
    skip "slow tests disabled", 4 unless $SLOWTEST;
    lives_ok { $analysis->analyzeSegFile($segio) } "analyzeSegFile succeeds";
    lives_ok { $analysis->_ProcessTumorNormalPairs }  "_ProcessTumorNormalPairs succeeds";
    is scalar(grep(/^NOCNV$/, map { $_->[8] } @{$analysis->{_tumor_ratios}})),31264,"Correct number of NOCNV";
    is scalar(grep(/^CNV$/, map { $_->[8] } @{$analysis->{_tumor_ratios}})),13995,"Correct number of CNV";
}

undef($analysis);
}
1;
