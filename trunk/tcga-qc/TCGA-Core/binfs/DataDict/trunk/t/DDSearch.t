# -*-perl-*-
use Test::More qw(no_plan);
use strict;
use warnings;

use_ok('DataDict::DDSearch');
ok my $dd = DataDict::DDSearch->new();
ok $dd->load_dict("t/samples/dd2.3.11.xml");
is $dd->_get_entry(3135408)->att('cde'), 3135408, 'get entry by cde';
is $dd->_get_entry_by_elt_name('tobacco_smoking_history_indicator')->att('cde'), 2181650, 'get entry by xml elt name';
is $dd->get_cde_by_elt_name('tobacco_smoking_history_indicator'), 2181650, 'get cde by xml elt name';
is $dd->_get_entry_by_elt_name('tobacco_smoking_history_indicator'), $dd->_get_entry_by_elt_name('patient_smoking_history_category'), 'same entry, diff xml names';
like $dd->get_best_definition(2783887), qr/Microscopic composition/, 'single cde';
like $dd->get_best_definition('specimen_histologic_type_type_histologictype'), qr/Microscopic composition/, 'singe xml elt name';

my $h = $dd->get_best_definition('time_between_excision_and_freezing', 2673765);

like $h->{'time_between_excision_and_freezing'}, qr/Time, in minutes/, 'defn for first arg (an elt)';
like $h->{2673765}, qr/Is the tumor/, 'defn for second arg (a cde)';

$h = $dd->get_best_definition('primary_therapy_outcome_success');
