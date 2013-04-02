# -*-perl-*-

use Test::More 'no_plan';
use strict;
use warnings;
use File::Spec;

use lib '../lib';
BEGIN{
    use_ok( 'TCGA::EXPR::ExprIO' );
    use_ok( 'TCGA::CNV::RefData' );
    use_ok( 'TCGA::EXPR::ExprToGene');
}

my $TESTDATA = ( -e '../t/samples' ? '../t/samples' : 't/samples' );
my $SLOWTEST = 1 unless $ENV{FAST_TESTS_ONLY};
$TCGA::CNV::Config::REFDATA_DIR = ( -e '../refdata' ? '../refdata' : 'refdata');
$TCGA::EXPR::ExprToGene::VERBOSE=0;
my @dcc_headers = qw(barcode uuid participant_code sample_type_code center_id platform_id   platform_name entrez_gene_symbol expression_value);

ok my $xprio = TCGA::EXPR::ExprIO->new(File::Spec->catfile($TESTDATA,'GBM.expgene.level4.test.txt'));
ok $xprio = TCGA::EXPR::ExprIO->new(File::Spec->catfile($TESTDATA,'GBM.expgene.level4.test.txt.gz')), "auto gunzip";
is ${$xprio->{_hdrs}}[0],"barcode";
ok $xprio = TCGA::EXPR::ExprIO->new(File::Spec->catfile($TESTDATA,'GBM.expgene.level4.test.txte'), \@dcc_headers);

ok my $refdata = TCGA::CNV::RefData->new('bins10K','small','genome2gene','genome');
ok my $x2g = TCGA::EXPR::ExprToGene->new($refdata);
ok $xprio = TCGA::EXPR::ExprIO->new(File::Spec->catfile($TESTDATA,'KIRP.expgene.level4.test.txt'), \@dcc_headers);

SKIP : {
    skip "slow tests disabled", 5 unless $SLOWTEST;
    $x2g->analyzeExprFile($xprio);
    is_deeply( [$x2g->platforms], [qw(AgilentG4502A_07_3)], "kirp platforms");
    is ( scalar values %{$x2g->tumor_ratios('AgilentG4502A_07_3','TCGA-B3-4103-01A-01R-1193-07')}, 16008, "correct # of values" );
    is ( scalar values %{$x2g->tumor_zs('AgilentG4502A_07_3','TCGA-B3-4103-01A-01R-1193-07')}, 16008, "correct # of z values" );
    undef($x2g);
}

# 16008 = number of genes in the data file for this barcode, with
# non-null values, that are also present in the genome2gene.dat
# reference (i.e., independently calculated)


1;
