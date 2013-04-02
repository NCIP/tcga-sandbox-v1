#! /usr/bin/perl -w
# $Id: listFilesFromLatestArchives.pl 18027 2013-01-16 20:44:47Z snyderee $
# $Revision: 18027 $
# $Date
####################################################################################################
our	$pgm_name		= "listFilesFromLatestArchives.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Fri Feb 17 17:43:29 EST 2012";
our	$rel_date		= '$Date: 2013-01-16 15:44:47 -0500 (Wed, 16 Jan 2013) $';
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

=head1 OPTIONS

 -c           print list of wildcard expressions representing target files inside expanded archives (default = "FALSE")
 -h           print "help" information (default = "FALSE")
 -q           print file names and/or wildcards with fully-qualified path (default = "FALSE")
 -r <string>  (reject) filter out file names containing this string (default = "0")
 -s <string>  (select) include only file names containing this string (default = "0")
 -x <string>  target file extension (default = "0")


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
my %opts	= ();									 			# init cmdline arg hash

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
		'type'     => "boolean",
		'usage'    => "print list of wildcard expressions representing target files inside expanded archives",
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
		'usage'    => "list files using \"ls\" plus the specified options (e.g., \"-ll\", \"-lltr\"",
		'required' => 0,
		'init'     => 0,
	},
	'q' => {
		'type'     => "boolean",
		'usage'    => "print file names and/or wildcards with fully-qualified path",
		'required' => 0,
		'init'     => 0,
	},
	'r' => {
		'type'     => "string",
		'usage'    => "(reject) filter out file names containing this string",
		'required' => 0,
		'init'     => 0,
	},
	's' => {
		'type'     => "string",
		'usage'    => "(select) include only file names containing this string",
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

my @infiles = qw( <path> );
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
my @fcFileList = ();
if( $opts{'c'} ){
	for ( my $i = 0; $i < @filecardList; $i++ ){
		$filecardList[$i] = "$wd/$filecardList[$i]";
	}
	print join( "\n", @filecardList ) . "\n";
} else {
	foreach ( @filecardList ){
		push( @fcFileList, split( /\n/, `ls -1 $_` ) );
	}
	@fcFileList = grep(   /$opts{'s'}/, @fcFileList ) if $opts{'s'};
	@fcFileList = grep( ! /$opts{'r'}/, @fcFileList ) if $opts{'r'};
	if( $opts{'q'} ){
		for ( my $i = 0; $i < @fcFileList; $i++ ){
			$fcFileList[$i] = "$wd/$fcFileList[$i]";
		}
	}
	if ( $opts{'l'} ){
		my $filelist = join( " ", @fcFileList );
		print `ls -$opts{'l'} $filelist`;
	} else {
		print join( "\n", @fcFileList ) . "\n";
	}
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
			my $archName = $File::Find::dir . "/$2" ;					# arch name w/o version or number
			my $dirName  = $File::Find::dir . "/$1" ;					# full archive dir path
			print "$wd/$dirName	" if $opts{'d'};
			if ( -d "$wd/$dirName" ){
				print "\n$1\n$3 $4 $5" if $opts{'d'};
				if ( exists $arch{ $archName } ){
					if ( $4 > $arch{ $archName } ){
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
