#! /usr/bin/perl -w
# $Id: deleteObsoleteArchiveDirs.pl 17880 2012-11-16 02:20:26Z snyderee $
# $Revision: 17880 $
# $Date
####################################################################################################
our	$pgm_name		= "listFilesFromLatestArchives.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Fri Feb 17 17:43:29 EST 2012";
our	$rel_date		= '$Date: 2012-11-15 21:20:26 -0500 (Thu, 15 Nov 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

$pgm_name

=head1 SYNOPSIS

$pgm_name is used to list the (fully-qualified) names of the files in the most recent (highest
numbered) archive directories based on standard TCGA/DCC naming conventions.

=head1 USAGE

I<$pgm_name> <path>

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
use File::Find;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

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
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
	'x' => {
		'type'     => "string",
		'usage'    => "target file extension",
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

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my %arch = ();

my $wd = `pwd`;
chomp $wd;
print "working directory = \"$wd\"\n" if $opts{'d'};
find( \&wanted, @ARGV );

print "-" x 80 . "\n" if $opts{'d'};
my @filecardList = ();
foreach my $dir ( sort keys %arch ){
	print "$dir version $arch{$dir}\n" if $opts{'d'};
	my $filecard;
	if ( $opts{'x'} ) {
		$filecard = "$dir.$arch{$dir}.*/*.$opts{'x'}";
	} else {
		$filecard = "$dir.$arch{$dir}.*/*";
	}
	push( @filecardList , $filecard );
}

##################################      ... and Here         #######################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub wanted {
	if ( -d ){
		if ( m/^((.+\.[a-z]{3}_.+\.Level_\d\.(\d+))\.(\d+)\.(\d+))$/ ){
			my $fullName = $1;										# intgen.org_GBM.bio.Level_1.2.41.0
			my $numName  = $2;										# intgen.org_GBM.bio.Level_1.2
			my $archNum  = $3;										# 2
			my $archVer  = $4;										# 41
			my $archSeq  = $5;										# 0
			my $archName = $File::Find::dir . "/$numName" ;					# arch name w/o version or number
			my $dirName  = $File::Find::dir . "/$fullName" ;					# full archive dir path
			print "1:	$1\n2:	$2\n3:	$3\n4:	$4\n5:	$5\n\n";
			print "$wd/$dirName	" if $opts{'d'};
			if ( -d "$wd/$dirName" ){
				print "\n$1\n$3 $4 $5" if $opts{'d'};
				if ( exists $arch{ $archName } ){
					if ( $archVer > $arch{ $archName } ){
						$arch{ $archName } = $4;
					}
				} else {
					$arch{ $archName } = $4;
				}
			} else {
				print "..." if $opts{'d'};
			}
			print "\n" if $opts{'d'};
		}
	}
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
