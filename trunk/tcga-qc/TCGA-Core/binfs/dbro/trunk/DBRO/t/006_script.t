#-*-perl-*-
use Test::More qw(no_plan);
use Test::Script;
use strict;
use warnings;
use lib "../lib";

my $numtests = 8;


    script_compiles( 'bin/dbroET.pl', 'dbroET compiles');
script_compiles( 'bin/lev3q.pl', 'lev3q.pl compiles');
    script_compiles( 'scripts/convertRefFlat.pl', 'convertefFlat.pl compiles');
    script_compiles( 'scripts/createRefDataSQLite.pl', 'createRefDataSQLite.pl compiles');
    script_compiles( 'scripts/createRefData.pl', 'createRefData.pl compiles');
    script_runs( ['bin/dbroET.pl', '--input-headers', '--datatype', 'cna'], 'dbroET runs');
TODO : {
    local $TODO= "provide appropriate arguments";
    script_runs( 'scripts/convertRefFlat.pl', 'convertefFlat.pl runs');
    script_runs( 'scripts/createRefDataSQLite.pl', 'createRefDataSQLite.pl runs');
    script_runs( 'scripts/createRefData.pl', 'createRefData.pl runs');
}
