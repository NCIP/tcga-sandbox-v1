#-*-cperl-*-
use Test::More qw(no_plan);
use Test::Script;
use strict;
use warnings;

use lib '../lib';


my $the_script = "bin/sif2json.pl";
my $igc_sample_xls = "t/samples/igc_sample_sif.xls";
my $nch_sample_xls = "t/samples/nch_sample_sif.xls";
script_compiles $the_script, 'script compiles';

script_runs [$the_script, $nch_sample_xls, 'NCH'], 'script runs on nch';
script_runs [$the_script, $igc_sample_xls, 'IGC'], 'script runs on igc';
