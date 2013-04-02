#! /bin/perl -w
# $Id: scanBCR_2.5_XSD.pl 16570 2012-04-30 21:13:57Z snyderee $
# $Revision: 16570 $
# $Date
####################################################################################################
our	$pgm_name		= ".pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Mon Mar 05 16:48:25 EST 2012";
our	$rel_date		= '$Date: 2012-04-30 17:13:57 -0400 (Mon, 30 Apr 2012) $';
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
use XML::Twig;
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

&find( \&wanted, @ARGV );


##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
# &wanted()
# The routine executed on all the files found by File::Find
####################################################################################################
sub wanted {
	my $file = $File::Find::name;
	my $dir  = $File::Find::dir;

	print "file = \"", $file, "\";\ndir = \"", $dir, "\"\n";

	$file = "file://$file";
	my $t = XML::Twig->new( pretty_print => "indented" );
	$t->parseurl( $file );
	my @elements = $t->root->children("xs:element");
	foreach (@elements) {
		print $_->att("name"), "\n";
		my $r = $_->first_descendant("xs:restriction");
		$r->print if $r;
		#my $x = $_->first_descendant("xs:extension");
		#$x->print if $x;
		#my $e = $_->first_descendant("xs:enumeration");
		#$e->print if $e;
		#my $a = $_->first_descendant("xs:attribute");
		#$a->print if $a;
	}
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
