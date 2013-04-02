#-*-perl-*-
# $Id: 001_repgen.t 7166 2010-06-07 15:35:21Z jensenma $
use strict;
use warnings;

$ENV{PERL5LIB} = '.'; # protect from previous installs
use lib 'lib';
use lib 't/lib';
use Test::More tests => 20;
use IPC::Open3; # use open3() to test run the repgen.pl script...
use DBI;
use Symbol;

my $SCRIPT_PATH = "bin/repgen.pl";
my @LIBS = ("-I../lib", "-Ilib", "-I../t/lib", "-It/lib");
my $db_tests = 6;

use RepGen::Configure;
# module includes
use_ok('RepGen::telem');
use_ok('RepGen::test');

# options 
ok -f $SCRIPT_PATH, "script path ok";
like exec_script("--version")->[0], qr/version $VERSION/, "--version correct";
like exec_script("--help")->[0], qr/usage/i, "--help gets help";
is_deeply( [(split("\n", exec_script(qw(test --avail-filetypes))->[1]))[1..2] ], [qw(--test1-files --test2-files)], "--avail-filetypes");
is_deeply( [(split("\n", exec_script(qw(test --avail-fields))->[1]))[1..6]], [qw(id status type size date disease)], "--avail-fields" );
like exec_script(qw(narg --schloob))->[1], qr/not available/, "bad config module borks";
like exec_script(qw(test --test1-file t/data/accns.test1 --sort-by schurlb))->[1], qr/sort field unrecognized/, "bad sort field borks";
# make report
for my $filter (qr/^SR/, qr/^TCGA/) {
    my @rpt = split( "\n", exec_script(qw( test --test1-files t/data/accns.test1 --test2-files t/data/exch.test2))->[0] );
    my (@a,@b);
    shift @rpt;
    for (grep /$filter/, @rpt) {
	push @a, (split /\t/)[0];
    }
    my $sort_ok = 1;
    @b = sort @a;
    for my $i (0..$#a) {
	$sort_ok = 0 if $a[$i] ne $b[$i];
	last unless $sort_ok;
    }
    ok $sort_ok, "sorted on first field (default)";
    @rpt = @a = @b = ();
    $sort_ok = 1;
    @rpt = split( "\n", exec_script(qw( test --test1-files t/data/accns.test1 --test2-files t/data/exch.test2 --sort-by date))->[0] );
    shift @rpt;
    for (grep /$filter/, @rpt) {
	push @a, (split /\t/)[4];
    }
    @b = sort @a;
    $sort_ok = 1;
    for my $i (0..$#a) {
	$sort_ok = 0 if $a[$i] ne $b[$i];
	last unless $sort_ok;
    }
    ok $sort_ok, "sorted on --sort-by date";
}
1;


## db tests
my ($dsn,$user,$pwd) = ($RepGen::Configure::default_dsn,
			$RepGen::Configure::default_user,
			$RepGen::Configure::default_pwd);

my $DB_OK ||= ($dsn && $user && $pwd);

ok($DB_OK, "dsn defaults set");
SKIP : {
    skip "No dsn defaults set in Configure module", $db_tests unless $DB_OK;
    $DB_OK &&= eval "require DBD::Pg; 1";
    diag('No DBD::Pg driver available in your perl distribution') unless $DB_OK;
    SKIP : {
	skip "No DBD::Pg available; skipping", 1 unless $DB_OK;
	my $dbh = DBI->connect($dsn,$user,$pwd);
	$DB_OK &&= $dbh;
	ok($DB_OK, "db connection successful");
	$dbh->disconnect if $DB_OK;
    }
    SKIP : {
	skip "DB connection could not be established; skipping", $db_tests-1 unless $DB_OK;
	my @rpt = split( /\n/, exec_script(qw(test --test2-files t/data/exch.test2))->[0] );
	diag("Doing db tests");
	my %diseases;
	for (grep /^TCGA/, @rpt) {
	    $diseases{ (split /\t/)[5] }++;
	}
	is_deeply( [sort keys %diseases], [sort qw(N/A LAML OV GBM)], "tumor abbrevs retrieved from db" );
	# try nolookup
	@rpt = split( /\n/, exec_script(qw(test --nolookup --test2-files t/data/exch.test2))->[0] );	
	%diseases = ();
	for (grep /^TCGA/, @rpt) {
	    $diseases{ (split /\t/)[5] }++;
	}
	is_deeply( [keys %diseases], [qw(N/A)], "db lookups disabled by --nolookup" );
	# full telem report creation
	@rpt = split( /\n/, exec_script(qw(telem --sample-files t/data/SRA_Accessions_TCGA --exchange-files t/data/exchange.tab --sort-by RecDate))->[0] );
	%diseases = ();
	my %centers = ();
	for (grep /TCGA/, @rpt) {
	    $diseases{ (split /\t/)[5] }++;
	    $centers{ (split /\t/)[6] }++;
	}
	cmp_ok ( scalar @rpt, '>', 1470, "telem report: plenty of lines");
	is_deeply( [sort keys %diseases], [sort qw(N/A LAML OV GBM)], "telem report: tumor abbrevs retrieved from db" );
	is_deeply( [sort keys %centers], [sort qw(HAIB BCM WUSM)], "telem report: center abbrevs retrieved from db" );

	1;
    }
}



sub exec_script {
    # execute script with parameters given in @_, using IPC::Open3::open3
    my ($outh, $out, $err);
    my $errh = gensym;
    open3(undef, $outh, $errh, "perl", @LIBS, $SCRIPT_PATH, @_);
    $out = join('',<$outh>);
    $err = join('', <$errh>);
    return [$out, $err];
}
