# -*-perl-*-
#$Id: OCDump.t 9612 2011-01-29 04:17:20Z jensenma $
use strict;
use warnings;
use Test::More qw(no_plan);
use lib '../lib';

use_ok('DataDict::OCDump');

ok my $o = DataDict::OCDump->new(), 'create object';
isa_ok($o, 'DataDict::OCDump');
ok $o->parse('t/samples/oc_questions_and_cdes_v2.txt'), 'parse oc text dump';
is $o->num_records, 445, "got all records";
ok my $r = $o->next_record, 'iterator returns';
isa_ok($r, 'DataDict::OCDump::Record');
is($r->cde, 1515, 'iterator tied correctly');
is($o->next_record->cde, 1611, 'iterator iterates correctly');
while ($o->next_record) {}
is($o->next_record->cde, 1515, "iterator iterates to completion");

1;

