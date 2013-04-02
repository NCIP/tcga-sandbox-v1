#! /bin/perl -w
# $Id: filterDataSubNotifications.pl 17760 2012-10-11 15:48:33Z snyderee $
# $Revision: 17760 $
# $Date
####################################################################################################
our	$pgm_name		= "filterDataSubNotifications.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Tue Apr 10 15:00:17 EDT 2012";
our	$rel_date		= '$Date: 2012-10-11 11:48:33 -0400 (Thu, 11 Oct 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

filterDataSubNotifications.pl

=head1 SYNOPSIS

filterDataSubNotifications.pl <concatenated notification emails>

=head1 USAGE

I<filterDataSubNotifications.pl ....

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
my %keywords = ( 	'Processing of your submission' 			=>	1,
					'\[TCGA-DCC\] New Archive Available - '		=>	0,
				);

foreach my $file ( @ARGV ){
	open( FILE, "$file" ) or die "Cannot open \"$file\" for reading.\n";
	$RS = undef;
	my $fdata = <FILE>;
	my @letters = split( /\n+--\n+Sent by the DCC.*\n+/, $fdata );
	my $i = 0;
	foreach my $letter ( @letters ){
	#	print "record $i: \"$letter\"\n";
		my @lines = split( /\n+/, $letter );
		for ( my $l = 0; $l < @lines; $l++ ){
			my $line = $lines[$l];
			foreach my $keyword ( keys %keywords ){
				if ( $line =~ m/$keyword/ ){
					print $lines[$l+$keywords{$keyword}] . "\n";
				}
			}
		}
		my $hit = 0;
		$i++;
	}
}



##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
#### end of template ###############################################################################
####################################################################################################
