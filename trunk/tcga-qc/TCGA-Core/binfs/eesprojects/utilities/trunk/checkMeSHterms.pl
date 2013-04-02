#! /bin/perl -w
# $Id: checkMeSHterms.pl 17801 2012-10-26 01:43:24Z snyderee $
# $Revision: 17801 $
# $Date
####################################################################################################
our	$pgm_name		= "checkMeSHterms.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Fri Dec 09 13:34:04 EST 2011";
our	$rel_date		= '$Date: 2012-10-25 21:43:24 -0400 (Thu, 25 Oct 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

checkMeSHterms.pl

=head1 SYNOPSIS

checkMeSHterms.pl -f<field_num>  <term_file>

=head1 USAGE

I<checkMeSHterms.pl> -f2 TCGA_diseases.csv

=cut
####################################################################################################
#	Testbed:	/home/eesnyder/projects/nih/OV/dbGaP_submission/MeSH_terms
#	Cmdline:	checkMeSHterms.pl -f2 TCGA_diseases.csv
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use LWP::UserAgent;
use XML::Parser;
use TCGA;
use String::HexConvert ':all';

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
	'f' => {
		'type'     => "integer",
		'usage'    => "field containing query terms",
		'required' => 1,
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
my $url		= 'http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=mesh&term=MYTERM%5Bmh%5D';
my $url2	= 'http://www.ncbi.nlm.nih.gov/sites/entrez?term=MYTERM%5Bml%5D&cmd=search&db=mesh';
my $parser	= XML::Parser->new(Style => 'Tree');
my $agent	= LWP::UserAgent->new;
my @infiles	= qw( <term_file> );
my $banner	= &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
foreach my $file ( @ARGV ){
	my $data = &read_table_in2_LoL_fromfile( $file, '\t' );
	foreach my $row ( @$data ){
		warn "Field $opts{'f'} out of bounds." if $opts{'f'} > @$row + 1;
		my $term	=	$row->[ $opts{'f'}-1 ];
		my $term2	=	ascii_to_hex( $term );
		$term2		=~	s/(.{2})/%$1/g;
		my $query	=	$url2;
		$query		=~	s/MYTERM/$term/;

		printf("%s %s (%s), ", $term, $query, $abbrev );
		#my $resp = $agent->get( $query );
		#my $rec = $parser->parse( $resp->{_content} );
		#for( my $i = 0; $i < @$rec; $i++ ){
		#	print $rec->[$i] . "\n";
		#	for( my $j = 0; $j < @{$rec->[$i]}; $j++ ){
		#		print "$rec->[$i][$j]\n";
		#	}
		#}
		print "\n";
	}
}


##################################      ... and Here         #######################################
####################################################################################################
print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;
####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
#### end of template ###############################################################################
####################################################################################################
