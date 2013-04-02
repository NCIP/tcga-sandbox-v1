#! /bin/perl -w
# $Id: check_links.pl 17922 2012-11-30 01:35:25Z snyderee $
# $Revision: 17922 $
# $Date: 2012-11-29 20:35:25 -0500 (Thu, 29 Nov 2012) $
####################################################################################################
our $pgm_name       = "check_links.pl";
our $VERSION        = "v1.0.0 (dev)";
our	$start_date		= "Thu Nov 29 12:00:04 EST 2012";
our $rel_date       = "";
####################################################################################################
#	Parses output from linkMigrationTest03.pl looking for bad links
####################################################################################################
use strict;
use warnings;
use Getopt::Std;
use EESnyder;
use English;
our( $opt_D, $opt_F, $opt_d, $opt_f, $opt_j, $opt_h, $opt_n, $opt_w );

getopts('DF:c:df:jnw:');
my ( @links, @errlist );
my $checkLinks = 1;
$checkLinks = 0 if $opt_D;
if ( $checkLinks ){
	@links = qw( f.link_fmtchk a.link_fmtchk );
	@errlist = qw ( LINK_UNDEFINED LINK_NULLSTRING LINK_NULL BAD_LINK );
} else {
	@links = qw( date_comparison );
	@errlist = qw ( DATES_DIFFER );
}
my $fname = shift @ARGV;
my ( $dLoH, $headings ) = &read_table_in2_LoH_fromfile( $fname, '\t' );
print "Read file $fname.\n" if $opt_d;
my ( $path, $root, $ext );
if ( $fname !~ m#/# and $fname =~ m#^(.+)\.(\w+)$# ){
	$path = ""; $root = $1; $ext = $2;
} elsif ( $fname =~ m#^(.*)/([^/]+)\.(\w+)$# ){
	$path = $1; $root = $2; $ext = $3;
} else {
	die "Cannot parse components of file name: \"$fname\".\n";
}
print "path = $path; root = $root; ext = $ext\n" if $opt_d;

foreach my $arg ( @errlist ){
	foreach my $lnk ( @links ){
		my $fname = $root . "_$lnk" . "_$arg" . ".$ext";
		my $output = "";
		foreach my $row ( @$dLoH ){
			if ( $row->{ $lnk } eq $arg ){
				my $line = "";
				foreach my $head ( @$headings ){
					$line .= $row->{ $head } if ( defined $row->{ $head } );
					$line .= "\t";
				}
				$line =~ s/\t$/\n/;
				$output .= $line;
			}
		}
		if ( $output ){
			open( FILE, ">$fname" ) or die "Cannot open file: \"$fname\" for writing.\n";
			print FILE $output;
			close( FILE );
		}

	}
}
