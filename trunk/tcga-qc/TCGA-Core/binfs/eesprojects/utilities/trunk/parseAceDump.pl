#! /usr/bin/perl -w
# $Id: parseAceDump.pl 18049 2013-01-24 16:53:56Z snyderee $
# $Revision: 18049 $
# $Date
####################################################################################################
our	$pgm_name		= "parseAceDump.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Wed May 23 10:29:11 EDT 2012";
our	$rel_date		= '$Date: 2013-01-24 11:53:56 -0500 (Thu, 24 Jan 2013) $';
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

$pgm_name

=head1 USAGE

I<$pgm_name> ....

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
my $dumpFileName = shift @ARGV;

open( FILE, "$dumpFileName" ) or die "Cannot open file: $dumpFileName for reading.\n";
my $class;
my $instance;
my %data = ();
while( <FILE> ){
	chomp;
	next if m/^\s+\/\//;
	if ( m/^$/ ){
		$class = "";
		$instance = "";
	}
	if ( m/^(\w+)\s:\s"(.+)"$/ ){
		$class = $1;
		$instance = $2;
	}
	if ( m/^(\w+)\s+"(.+)"$/ ){
		if ( exists $data{$class}{$instance}{$1} ){
			push ( @{ $data{$class}{$instance}{$1} }, $2 );
		} else {
			$data{$class}{$instance}{$1} = [ $2 ];
		}
	}
}
$class = 'Person';
foreach $instance ( keys %{$data{$class}} ){
	next unless ( $instance =~ m/Snyder, Eric Eugene/ );
	print "$class : \"$instance\"\n";
	foreach my $list ( keys $data{$class}{$instance} ){
		foreach my $record ( @{$data{$class}{$instance}{$list}} ){
			print "$list \"$record\"\n";
		}
	}
	print "\n";
}



exit(0) unless $opts{'v'};
foreach $class ( keys %data ){
	foreach $instance ( keys %{$data{$class}} ){
		print "$class : \"$instance\"\n";
		foreach my $list ( keys $data{$class}{$instance} ){
			foreach my $record ( @{$data{$class}{$instance}{$list}} ){
				print "$list \"$record\"\n";
			}
		}
		print "\n";
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
