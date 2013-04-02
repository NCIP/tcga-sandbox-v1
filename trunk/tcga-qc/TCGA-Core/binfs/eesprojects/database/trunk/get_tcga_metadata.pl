#! /usr/bin/perl -w
# $Id: get_tcga_metadata.pl 18017 2013-01-12 02:25:58Z snyderee $
####################################################################################################
my $revision = '$Revision: 18017 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "get_tcga_metadata.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Thu Sep 27 14:22:24 EDT 2012";
our	$rel_date		= '$Date: 2013-01-11 21:25:58 -0500 (Fri, 11 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

get_tcga_metadata.pl

=head1 SYNOPSIS

get_tcga_metadata.pl

=head1 USAGE

The script I<get_tcga_metadata.pl> executes a SQL query on the TCGA production database to retrieve
information on cases and aliquots.

=cut
####################################################################################################
#	Testbed:
#	Cmdline:
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use DBI;
use Date::Handler;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash
my $CONNECTION_PATH = $ENV{'HOME'} . "/src/oracle";
my @headings = qw(	UUID BARCODE ELEMENT_TYPE DISEASE_STUDY PARTICIPANT_CODE SAMPLE_CODE VIAL
					CENTER_CODE CENTER_TYPE PLATE_ID BCR RECEIVING_CENTER BATCH_NUMBER SHIP_DATE
					TSS_CODE PLATFORMS DATA_TYPES );

my %usage 	= (													# init paras for getopts
	'B' => {
		'type'     => "boolean",
		'usage'    => "print program banner to STDOUT",
		'required' => 0,
		'init'     => 1,
	},
	'C' => {
		'type'     => "string",
		'usage'    => "JSON file containing connection information",
		'required' => 1,
		'init'     => "$CONNECTION_PATH/TCGA_connections.json",
	},
	'D' => {
		'type'     => "boolean",
		'usage'    => "print copious debugging information",
		'required' => 0,
		'init'     => 0,
	},
	'V' => {
		'type'     => "boolean",
		'usage'    => "print version information",
		'required' => 0,
		'init'     => 0,
	},
	'c' => {
		'type'     => "string",
		'usage'    => "connection name",
		'required' => 0,
		'init'     => 0,
	},
	'd' => {
		'type'     => "boolean",
		'usage'    => "print debugging information",
		'required' => 0,
		'init'     => 0,
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'l' => {
		'type'     => "string",
		'usage'    => "name of symbolic link to dated metadata file",
		'required' => 0,
		'init'     => 'metadata.current.txt',
	},
	'o' => {
		'type'     => "string",
		'usage'    => "output file name",
		'required' => 0,
		'init'     => '',
	},
	'q' => {
		'type'     => "string",
		'usage'    => "file containing SQL query",
		'required' => 1,
		'init'     => $ENV{'HOME'} . "/src/sql/tcga_metadata_query.sql",
	},
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
);

####################################################################################################
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7
####################################################################################################
my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
my $symlink = '';
$symlink = $opts{'l'} if $opts{'l'};				# create a symbolic link to the dated output file

my $date = new Date::Handler(	{	date		=>	time(),
									time_zone	=>	"EST",
									locale		=>	"en_US",
								}
							);

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};



####################################################################################################
################################## Put Main Between Here ... #######################################
my $outfile = &generate_outfile_name();
print "output file: \"$outfile\".\n" if $opts{'d'};

my $connects = &read_json_file( $opts{'C'}, "containing DB connection information" );
my $c = $connects->{'prod_commonread'};
$c = $connects->{$opts{'c'}} if $opts{'c'};
$c->{'dsn'} = "dbi:Oracle:" . $c->{'host'} . ":" . $c->{'port'} . "/" . $c->{'dbname'};
my $query = &read_query( $opts{'q'} );
my  $dbh = DBI->connect($c->{'dsn'}, $c->{'user'}, $c->{'passwd'}, { RaiseError => 1, AutoCommit => 0 });
my $error = $?;
print "error status: $error\n" if $error;
print "dbh = $dbh\n" if $opts{'d'};
$dbh->{RaiseError} = 1;
$dbh->func( 500000, 'dbms_output_enable' );
my $sth = $dbh->prepare( $query );
my $rv = $sth->execute;
open( FILE, ">$outfile" ) or die "Cannot open: \"$outfile\" for writing.\n";
print FILE join( "\t", @headings ) . "\n";
my $nrows = 0;
while( my @row = $sth->fetchrow() ){
	my $outrow = '';
	for(my $i = 0; $i < @row; $i++ ){
		if( defined $row[$i] ){
			$outrow .= $row[$i];
		}
		$outrow .= "\t" unless ($i == (@row - 1));
	}
	print FILE "$outrow\n";
	$nrows++;
}
close ( FILE );
print STDERR "Wrote $nrows rows to $outfile.\n";
$sth->finish;
$dbh->disconnect;
if ( $symlink ){
	unlink( $symlink ) if ( -e $symlink );
	symlink( $outfile, $symlink );
}

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_query {
	my ( $qfile ) = @_;

	open( FILE, "$qfile" ) or die "Cannot open query file: \"$qfile\" for reading.\n";
	my $qstring = '';
	while( <FILE> ){
		next if /^#/;
		$qstring .= $_;
	}
	close( FILE );
	return( $qstring );
}
####################################################################################################
sub generate_outfile_name {

	my %fmt =	(	year	=>	"%04d",
					month	=>	"%02d",
					day		=>	"%02d",
				);

	my $dateH = $date->AsHash;
	my $outfile = "metadata.";
	foreach ( qw ( year month day ) ){
		$outfile .= sprintf( $fmt{$_}, $dateH->{$_} );
	}
	$outfile .= ".txt";
	$outfile = $opts{'o'} if $opts{'o'};
	return( $outfile );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
