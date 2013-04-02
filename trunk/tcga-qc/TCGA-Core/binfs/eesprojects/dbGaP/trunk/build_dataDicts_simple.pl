#! /bin/perl -w
# $Id: build_dataDicts_simple.pl 17758 2012-10-09 23:30:56Z snyderee $
# $Revision: 17758 $
# $Date
####################################################################################################
my $revision = '$Revision: 17758 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "build_dataDicts.pl";
our $VERSION        = "v1.2.0 ($revision)";
our	$start_date		= "Tue May 08 15:43:53 EDT 2012";
our	$rel_date		= '$Date: 2012-10-09 19:30:56 -0400 (Tue, 09 Oct 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

build_dataDicts.pl

=head1 SYNOPSIS

build_dataDicts.pl

=head1 USAGE

I<build_dataDicts.pl> creates data dictionary files based on data file(s) provided on the command-line
and a master data dictionary file provided with the -d option (a default value is provided).

=cut
####################################################################################################
#	Testbed:
#	Cmdline:
####################################################################################################
#	History:
#	v1.2.0:		Simplify code getting rid of the "long key" and just using VARNAME instead
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use List::Compare;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash
my $trunC = 3;

my %usage 	= (													# init paras for getopts
	'B' => {
		'type'     => "boolean",
		'usage'    => "print program banner to STDOUT",
		'required' => 0,
		'init'     => 1,
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
		'usage'    => "cancer type(s)",
		'required' => 0,
		'init'     => 0,
	},
	'd' => {
		'type'     => "string",
		'usage'    => "master data dictionary file",
		'required' => 1,
		'init'     => "/home/eesnyder/projects/nih/XSD/dataDict/dbGaP_DataDict_current.txt",
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'k' => {
		'type'     => "string",
		'usage'    => "comma delimited list of key col #s or headings",
		'required' => 1,
		'init'     => "VARNAME",
	},
	'm' => {
		'type'     => "integer",
		'usage'    => "length of matching words",
		'required' => 1,
		'init'     => $trunC,
	},
	's' => {
		'type'     => "string",
		'usage'    => "field separator",
		'required' => 0,
		'init'     => "\t",
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

my @infiles = qw( datafile(s) );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
my $fsep = $opts{'s'};								# field separator
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my @cancer = split(/[,;.|]/, lc( $opts{'c'} ) );				# TCGA cancer abbreviations
my ( $mDD, $fH ) = &read_mDD( $opts{'d'}, $opts{'k'} );			# load master data dictionary file
my $keyString = $opts{'k'};										# columns from mDD to use as hash key
my @files = grep( !/_dd\.txt/, @ARGV );							# filter out _dd files if present
my %dd_file = ();
foreach my $file ( @files ){
	$dd_file{ $file } = &create_dataDict( $file, $mDD, \@cancer );
}

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_mDD {
	my ( $fname, $keyString ) = @_;

	open( FILE, "$fname" ) or die "Cannot open master data dictionary file, \"$fname\", for reading.\n";
	my $mDD_HoH = &read_table_in2_HoH_fromRRfh( \*FILE, $fsep, $fname, $keyString );
	close( FILE );
	return( $mDD_HoH );
}
####################################################################################################
sub create_dataDict {
	my ( $file, $mDD, $cancers ) = @_;

	my @ddKeys = qw( VARNAME VARDESC TYPE );
	open( FILE, "$file" ) or die "Cannot open TCGA clin/biospec datafile: $file for reading.\n";
	my $head = <FILE>;
	close( FILE );
	chomp $head;
	my @varnames = split( /$fsep/, $head );

	my $newFileName = $file;
	$newFileName =~ s/\.txt$/_dd.txt/;
	&check_for_null_VARDESC( \@varnames, $mDD, $file );
	open( FILE, ">$newFileName" ) or die "Cannot open file: $newFileName for output.\n";
	print FILE join( "\t", 	@ddKeys ) . "\t" . "VALUES\n";
	foreach my $vn ( sort @varnames ){
		my $master;
		if (	exists $mDD->{ $vn } 	&&
				defined $mDD->{ $vn }	){
			$master = $mDD->{ $vn };
			foreach ( @ddKeys ){
				print FILE $master->{$_} . "\t";
			}
			print FILE join( "\t", @{$master->{'VALUES'} }) . "\n";
		}
	}
	close( FILE );
}
####################################################################################################
sub check_for_null_VARDESC {
	my ( $varnames, $mDD, $file ) = @_;
#	return;
	my @nullList = ();
	foreach my $vn ( @$varnames ){
		print $mDD->{$vn}{'VARDESC'} . "\n";
		print "$mDD, $vn\n";
		if(	(	$mDD->{$vn}{'VARDESC'} =~ m/null/i  ) ||
			(	$mDD->{$vn}{'VARDESC'} eq "" 		) ){
			print $mDD->{$vn}{'VARDESC'} . "\n";
			push( @nullList, $vn );
		}
	}
	my $nullCount = @nullList;
	if ( $nullCount ){
		warn	"ERROR: There are $nullCount variables in $file will NULL descriptions.\n";
		warn	"The variables are:\n" . join( "\n", @nullList ) . "\n\n";
		die		"Please fix data dictionary file:\n" . $opts{'d'} . "\n";
	}
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
