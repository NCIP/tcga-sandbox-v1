#! /bin/perl -w
# $Id$
# $Revision$
# $Date
####################################################################################################
our	$pgm_name		= "patchXSD.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Thu Jun 28 14:01:47 EDT 2012";
our	$rel_date		= '$Date$';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

patchXSD.pl

=head1 SYNOPSIS

patchXSD.pl

=head1 USAGE

I<patchXSD> ....

=cut
####################################################################################################
#	Testbed:	/home/eesnyder/projects/nih/XSD
#	Cmdline:	patchXSD.pl
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use LWP;
use Term::ReadKey;
use String::HexConvert ':all';

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

my $ua = LWP::UserAgent->new;
$ua->timeout( 60 );
$ua->agent( $pgm_name . "/$VERSION" );
$ua->from( $ENV{'USER'} . "@" . $ENV{'HOST'} );
$ua->show_progress( 1 );


my $PROTOCOL	 = "https";												# secure server
my $HOST		 = "ncias-p802.nci.nih.gov
";							# webservice host, developer server
my $PORT		 = "80";												# and port
my $REALM		 = "TCGA";

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

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
sub authenticate {
	my ( $host, $port, $protocol, $realm ) = @_;

	my $uname = &read_line( "Enter username: " );
	my $passwd = &read_line( "Enter password: ", "noecho" );
	print "\n";
	my $netloc = "$host:$port";
	$ua->credentials( $netloc, $realm, $uname, $passwd );
	my @return = $ua->credentials( $netloc, $realm );
	return( @return );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
