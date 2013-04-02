# -*- perl -*-

# t/001_load.t - check module loading and create testing directory

use Test::More 'no_plan';
use Test::Exception;
use File::Spec;
use lib '../lib';
use strict;
use warnings;

my $TESTDATA = ( -e '../t/samples' ? '../t/samples' : 't/samples' );
my $SLOWTEST = 1 unless $ENV{FAST_TESTS_ONLY};


BEGIN { $ENV{REFDATA_DIR} = ( -e '../refdata' ? '../refdata' :
    'refdata'); use_ok( 'TCGA::CNV' ); use_ok( 'TCGA::CNV::Segment' );
    use_ok( 'TCGA::CNV::RefData' ); use_ok( 'TCGA::CNV::SegToGene' );
    use_ok( 'TCGA::CNV::SegIO' ); }

$TCGA::CNV::SegToGene::VERBOSE = 0;

my $seg = TCGA::CNV::Segment->new();
isa_ok($seg, 'TCGA::CNV::Segment');

ok my $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA,"CESC.cna.level4.test.txt"));
isa_ok($segio, 'TCGA::CNV::SegIO');
is $segio->type, 'DCC', "correct type (DCC)";
my %seg;
@seg{qw(barcode chromosome start stop num_markers segment_mean)} = $segio->next_seg;
is $seg{barcode}, 'TCGA-DS-A0VK-01A-21D-A10T-01', "correct barcode";
is $seg{segment_mean}, '0.5107', "correct mean";

ok $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA,"URAEI_p_TCGASNP_b85_N_GenomeWideSNP_6_H09_735102.seg.data.test.txt"));
is $segio->type, 'Broad', "correct type (Broad)";
@seg{qw(barcode chromosome start stop num_markers segment_mean)} = $segio->next_seg;
is $seg{barcode}, 'URAEI_p_TCGASNP_b85_N_GenomeWideSNP_6_H09_735102', "correct Broad ID";
is $seg{segment_mean}, '0.6217', "correct mean";

ok my $refdata = TCGA::CNV::RefData->new('bins10K','small','genome2gene','genome','combined.gene_overlap');
ok my $analysis = TCGA::CNV::SegToGene->new($refdata);

 ok $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA, "PAAD.cna.level4.test.txt"));

dies_ok { $analysis->_ProcessTumorNormalPairs } "_ProcessTumorNormalPairs fails before analyzeSegFile is run";
SKIP : {
    skip "slow tests disabled", 4 unless $SLOWTEST;
    lives_ok { $analysis->analyzeSegFile($segio) } "analyzeSegFile succeeds";
    lives_ok { $analysis->_ProcessTumorNormalPairs }  "_ProcessTumorNormalPairs succeeds";
    TODO : {
	local $TODO = "The sqlite refactor broke the previous 'gene_indexes' loop variable logic";
    is scalar(grep(/^NOCNV$/, map { $_->{cnv_status} } @{$analysis->{_tumor_ratios}})),31264,"Correct number of NOCNV";
    is scalar(grep(/^CNV$/, map { $_->{cnv_status} }  @{$analysis->{_tumor_ratios}})),13995,"Correct number of CNV";
    }
}

undef($analysis);
1;
