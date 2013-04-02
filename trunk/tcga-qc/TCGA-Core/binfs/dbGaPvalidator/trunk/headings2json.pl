#! /bin/perl -w
####################################################################################################
our	$pgm_name		= "headings2json.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Wed Jun 22 14:11:02 EDT 2011";
our	$rel_date		= "Fri Jun 24 13:04:37 EDT 2011";
####################################################################################################
#	Eric E. Snyder (c) 2010
#	Virgnia Bioinformatics Institute
#	Virginia Polytechnic Institute and State University
#	Blacksburg, VA 24061-0447
#	USA
####################################################################################################
#	Program reads "manifest.txt" file from dbGaP submission directory and generates "fileSpecs.json"
#	which contains the headings for each data file and data dictionary file mentioned in the
#	manifest.
####################################################################################################
#	Testbed:	/home/eesnyder/projects/nih/dbGaP/validator/t/samples_2011-06-22
#	Cmdline:	headings2json.pl tcga_manifest.txt
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;


use File::Spec;
use Module::Build;
use Tie::IxHash;
use Cwd;
use JSON::XS;
use Array::Compare;

my $json = new JSON::XS;
my $FILE_SPECS = "fileSpecs.json";

my $MANIFEST_FILENAME = "Submitted File Name";					# key for col containing file names

my $EXT			= "txt";										# file extension
my $DELIM		= "\t";											# field delimiter
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
		'usage'    => "print verbose debugging information",
		'required' => 0,
		'init'     => 0,
	},
);

my @infiles = qw( tcga_manifest.txt );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

	print $pgm_name ."_$VERSION\n" .
		"Start date:	$start_date\n" .
		"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################

my $manifest_file = shift @ARGV;
my $manifestLoH = &read_table_in2_LoH_fromfile( $manifest_file );
my %fileData = ();
foreach ( @$manifestLoH ){									# foreach line of manifest...
	unless( $_->{$MANIFEST_FILENAME} =~ s/\.$EXT$// ){ 		# strip extension, complain if expected ext not found
		print "File name: \"$_->{$MANIFEST_FILENAME}\"" .
				" does not have expected extension (.$EXT)\n";
	}
	$fileData{ $_->{$MANIFEST_FILENAME} } = undef;			# assign name of each file in manifest as a hash key
}
print "keys:" . join( ", ", keys ( %fileData ) )."\n" if $opts{'v'};
foreach my $file ( sort keys %fileData ) {
	open( FILE, "$file.$EXT" ) or die "Cannot open file: \"$file.$EXT\" for reading.\n";
	my $ln = 0;
	my $firstline = "";
	while( <FILE> ){
		chomp;
		next if /^#/;
		unless ( $ln++ ){
			$firstline = $_;
			last;
		}
	}
	my @headings = split( /$DELIM/, $firstline );
	$fileData{$file}{"headings"} = \@headings;
	close( FILE );
}
open( FILE, ">$FILE_SPECS" ) or die "Cannot open file: \"$FILE_SPECS\" for writing.\n";
print FILE $json->pretty->encode( \%fileData );
close( FILE );
exit( 0 );

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
#### end of template ###############################################################################
####################################################################################################
