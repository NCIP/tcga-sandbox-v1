#! /usr/bin/perl -w
####################################################################################################
our	$pgm_name		= "barcode_CNVfiles.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Thu Sep 22 16:49:01 EDT 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

$pgm_name

=head1 SYNOPSIS

fix_CNV_ids.pl -f ../CNVhash -d ../msk2/ */*


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
use TCGA;

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
		'type'     => "string",
		'usage'    => "directory for output files",
		'required' => 0,
		'init'     => './',
	},
	'f' => {
		'type'     => "string",
		'usage'    => "name of key-value file",
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

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

my $kvFile	= $opts{'f'};
my $opDir	= $opts{'d'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $idHash = &read_2col_data2hash_fromfile( $kvFile );
foreach my $key ( keys %$idHash ){
	my $k = $key;
	$k =~ s/CGH-v/CGH.v/;
	$idHash->{ $k } = $idHash->{$key};
	delete $idHash->{$key};
}
# &print_key_value_pairs( $idHash );

foreach my $fname ( @ARGV ){
	my $LoHtable = &read_table_in2_LoH_fromfile($fname );
	my $outfile = join( '	', @mskHeading ) . "\tBARCODE\n";
	foreach my $lineHash ( @$LoHtable ){
		$lineHash->{"BARCODE"} = $idHash->{ $lineHash->{'sample'} };
		foreach my $key ( @mskHeading ){
			$outfile .= "$lineHash->{$key}\t";
		}
		$outfile .= "$lineHash->{'BARCODE'}\n";
	}
	my $outfname = "$opDir/$fname";
	open( FILE, ">$outfname" ) or die "Cannot open \"$outfname\" for writing.\n";
	print FILE "$outfile";
	close( FILE );
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
