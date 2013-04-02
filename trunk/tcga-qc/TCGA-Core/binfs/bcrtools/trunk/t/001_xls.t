#-*-perl-*-
use Test::More qw(no_plan);
use File::Spec;
use JSON;
use lib '../lib';
use strict;
use warnings;

my $TESTDIR = -e 't' ? File::Spec->catdir('t','samples') : File::Spec->catdir('..','t','samples');

use_ok('TCGA::BCR::DashboardConfig');
use_ok('TCGA::BCR::ReadXlsDashboard');

my $p;
ok $p = TCGA::BCR::ReadXlsDashboard->new();
isa_ok($p, 'TCGA::BCR::ReadXlsDashboard');
ok !$p->parse(File::Spec->catfile($TESTDIR,'boog')),'non-xls file returns null';

ok $p->parse(File::Spec->catfile($TESTDIR,'igc-062712.xls')),'igc spreadsheet';
cmp_ok scalar $p->book->worksheets(), '>', 0, 'got some xls worksheets';

is scalar @{$p->_find_table('case_summary_by_disease')}, 2, 'found case_summary_by_disease table';
is scalar @{$p->_find_table('case_summary_by_tss')}, 2,'found case_summary_by_tss table';
is @{$p->_find_table('case_by_shipment')}, 2, 'found case_by_shipment table';

ok $p->_get_table('case_by_shipment'), 'get case_by_shipment table';
$DB::single=1;
ok $p->_get_table('case_summary_by_disease'), 'get case_summary_by_disease table';
ok $p->_get_table('case_summary_by_tss'), 'get case_summary_by_tss table';

SKIP : {
    skip "xlsx long test", 6 unless 1;
    ok $p->parse(File::Spec->catfile($TESTDIR,'nch-062712.xlsx')),'nch spreadsheet';
    cmp_ok scalar $p->book->worksheets(), '>', 0, 'got some xlsx worksheets';

    is scalar @{$p->_find_table('case_summary_by_disease')}, 2, 'found case_summary_by_disease table';
    is scalar @{$p->_find_table('case_summary_by_tss')}, 2,'found case_summary_by_tss table';
    is @{$p->_find_table('case_by_shipment')}, 2, 'found case_by_shipment table';

    ok $p->_get_table('case_by_shipment'), 'get case_by_shipment table';
    ok $p->_get_table('case_summary_by_disease'), 'get case_summary_by_disease table';
    ok $p->_get_table('case_summary_by_tss'), 'get case_summary_by_tss table';
}

ok $p->get_table('case_summary_by_tss'), 'get_table';

1;
