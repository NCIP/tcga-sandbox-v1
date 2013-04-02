#! /usr/bin/perl -w
# $Id: build_dataDicts.pl 18047 2013-01-24 16:48:55Z snyderee $
# $Revision: 18047 $
# $Date: 2013-01-24 11:48:55 -0500 (Thu, 24 Jan 2013) $
####################################################################################################
my $revision = '$Revision: 18047 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "build_dataDicts.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Tue May 08 15:43:53 EDT 2012";
our	$rel_date		= '$Date: 2013-01-24 11:48:55 -0500 (Thu, 24 Jan 2013) $';
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

I<build_dataDicts.pl> creates data dictionary files based on data file(s) provided on the command-
line and a master data dictionary file provided with the -d option (a default value is provided).

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
		'init'     => "VARNAME,USAGE",
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
	$dd_file{ $file } = &create_dataDict( $file, $mDD, $fH, \@cancer );
}

##################################      ... and Here         #######################################
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

	my %findHash = ();
	foreach my $longKey ( keys %$mDD_HoH ){
		my ( $vn, $use ) = split( /\./, $longKey );
		if ( exists $findHash{ $vn } ){
			push( @{ $findHash{ $vn } }, $use );
		} else {
			$findHash{ $vn } = [ $use ];
		}
	}
	return( $mDD_HoH, \%findHash );
}
####################################################################################################
sub create_dataDict {
	my ( $file, $mDD, $fH, $cancers ) = @_;

	my @ddKeys = qw( VARNAME VARDESC TYPE );
	open( FILE, "$file" ) or die "Cannot open TCGA clin/biospec datafile: $file for reading.\n";
	my $head = <FILE>;
	close( FILE );
	chomp $head;
	my @varnames = split( /$fsep/, $head );
	my @filewords = split( /_/, $file );
	push ( @filewords, @$cancers );
	for( my $i = 0; $i < @filewords; $i++ ){
		$filewords[$i] =~ s/^(.{$trunC}).+$/$1/;					# truncate words at 3 letters
	}

	my $newFileName = $file;
	$newFileName =~ s/\.txt$/_dd.txt/;

	open( FILE, ">$newFileName" ) or die "Cannot open file: $newFileName for output.\n";
	print FILE join( "\t", 	@ddKeys ) . "\t" . "VALUES\n";
	foreach my $vn ( sort @varnames ){
		my $max_intersect = 0;
		my $use_intersect = "";
		foreach my $use ( @{$fH->{ $vn }} ){
			my @usewords = split( /\//, $use );
			for( my $i = 0; $i < @usewords; $i++ ){
				$usewords[$i] =~ s/^(.{$trunC}).+$/$1/;					# truncate words at 3 letters
			}
			my $lc = List::Compare->new( \@usewords, \@filewords );
			my @intersect = $lc->get_intersection;
			$use_intersect = $use unless $use_intersect;
			if ( @intersect > $max_intersect ){
				$max_intersect = @intersect;
				$use_intersect = $use;
			}
		}
		my $use_max_intersect_key = "$vn.$use_intersect";
		my $master = $mDD->{ $use_max_intersect_key };
		if ( $use_intersect ){
			foreach ( @ddKeys ){
				print FILE $master->{$_} . "\t";
			}
			print FILE join( "\t", @{$master->{'VALUES'} }) . "\n";
		} else {
			print FILE "$vn\n";
		}
	}
	close( FILE );
}
####################################################################################################
#		&check_for_null_VARDESC( \@varnames, $mDD, $file );
####################################################################################################
sub check_for_null_VARDESC {
	my ( $varnames, $mDD, $file ) = @_;

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
