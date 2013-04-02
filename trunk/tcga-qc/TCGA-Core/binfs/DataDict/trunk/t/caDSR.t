# -*-perl-*-
#$Id: caDSR.t 9481 2011-01-19 04:50:42Z jensenma $
use strict;
use warnings;
use Test::More tests => 11;
use Test::Exception;

use lib '..';

use_ok('DataDict::caDSR');
my $q;
ok ($q = DataDict::caDSR->new(), 'object created');
isa_ok($q,'DataDict::caDSR');
dies_ok {$q->cde(942)} 'cde is getter only';
dies_ok {$q->data('fllob')} 'data is getter only';
ok($q->narb(3),'arbitrary property set');
is($q->narb, 3, 'property correct');
ok($q->query_by_cde(64171), "query success");
is $q->cde, 64171, 'cde set';
like $q->caDSRlongName, qr/Small Vessel/, "caDSR getter works(1)";
like $q->CDEBrowserLink, qr/http:\/\//, "caDSR getter works(2)";

1;
