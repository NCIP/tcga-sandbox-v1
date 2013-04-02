#-*-perl-*-
use Test::More qw(no_plan);
use Test::Exception;
use File::Spec;
use Storable;
use strict;
use warnings;
use lib '../lib';

$ENV{REFDATA_DIR} = ( -e '../refdata' ? '../refdata' : 'refdata' );

my %test_gene = (
		 gene_id => 28464,
		 gene => 'PTEN',
		 chrom => 10,
		 start => 89613174,
		 stop => 89718512
		 );
my %test_chrom = (
		  chrom => '10',
		  first_bin => 8397,
		  last_bin => 9073
		 );
my $test_num_genes = 23386;
my $exp_binsize = 200000;

use_ok( 'TCGA::CNV::RefData' );
use_ok( 'TCGA::CNV::RefData::SQLite' );
use_ok( 'TCGA::CNV::SegToGene' );

$TCGA::CNV::SegToGene::VERBOSE = 0;
my $TESTDATA = ( -e '../t/samples' ? '../t/samples' : 't/samples' );
my $SLOWTEST = 1 unless $ENV{FAST_TESTS_ONLY};

my $refobj = TCGA::CNV::RefData::SQLite->new('refdata-051212.db');
isa_ok $refobj, 'TCGA::CNV::RefData';
isa_ok $refobj, 'TCGA::CNV::RefData::SQLite';

is $exp_binsize, $refobj->binsize, 'binsize correct';
is $test_num_genes, $refobj->num_genes_indexed, 'num genes correct';
ok my $gseg = $refobj->gene_as_segment($test_gene{gene_id}), 'gene_as_segment';
is $test_gene{gene_id},$gseg->Id, 'seg Id ok';
is $test_gene{chrom}, $gseg->Chr, 'seg Chr ok';
is $test_gene{start}, $gseg->Left, 'seg Left ok';
is $test_gene{stop}, $gseg->Right, 'seg Right ok';
ok $refobj->is_cnv($gseg), 'ccnv contains test gene ok';
$gseg->{id} = 50000;
ok !$refobj->is_cnv($gseg), 'ccnv does not contains fake gene ok';
ok my @a = $refobj->chrbin_limits(10), 'chrbin_limits call';
is $a[0], $test_chrom{first_bin}, 'first bin ok';
is $a[1], $test_chrom{last_bin}, 'last bin ok';

ok my $analysis = TCGA::CNV::SegToGene->new($refobj);
 ok my $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA, "PAAD.cna.level4.test.txt"));

#ok my $segio = TCGA::CNV::SegIO->new(File::Spec->catfile($TESTDATA, "PAAD.cna.level4.txt")); # long test

SKIP : {
    skip "slow tests disabled", 4 unless $SLOWTEST;
    lives_ok { $analysis->analyzeSegFile($segio) } "analyzeSegFile succeeds";
    lives_ok { $analysis->_ProcessTumorNormalPairs }  "_ProcessTumorNormalPairs succeeds";
    is scalar(grep(/^NOCNV$/, map { $_->{cnv_status} } @{$analysis->{_tumor_ratios}})),21158,"Correct number of NOCNV";
    is scalar(grep(/^CNV$/, map { $_->{cnv_status} } @{$analysis->{_tumor_ratios}})),25534,"Correct number of CNV";
}

undef($analysis);
