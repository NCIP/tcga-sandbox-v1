#-*-perl-*-
use Test::More qw(no_plan);
use Test::Script;
use File::Spec;
my $TESTDIR = -e 't' ? 'bin' : File::Spec->catdir('..','bin');

script_compiles(File::Spec->catfile($TESTDIR,'chkdash.pl'),'chkdash.pl compiles');
script_compiles(File::Spec->catfile($TESTDIR,'bcrx2j.pl'), 'bcrx2j.pl compiles');
