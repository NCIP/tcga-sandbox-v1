#! /bin/perl -w
####################################################################################################
our	$pgm_name		= "dbGaP_archive_cleaner.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Thu Jun 30 14:02:22 EDT 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

dbGaP_archive_cleaner.pl

=head1 SYNOPSIS

dbGaP_archive_cleaner.pl <archive_directory>

=head1 USAGE

I<dbGaP_archive_cleaner.pl> prepares a subject/phenotype archive for submission to dbGaP by
removing "empty" files, i.e., files containing header information only-- no data.  A data dictionary
file, with a name in the form tcga_\w+_dd.txt, will contain multiple lines describing the column
headers of the corresponding data file, with the name tcga_\w+.txt, where \w+ is the same in each
file. If the data file contains only the header line, the file is removed, along with the
corresponding _dd.txt file.  The resulting "cleaned" archive file list will be reflected in the
archive's manifest (tcga_manifest.txt), with the previous version saved as tcga_manifest.txt.bak.

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
use File::Copy 'cp';
use List::Compare;

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

my $FILE_EXT = "txt";
my $MANIFEST_FILE = "tcga_manifest.$FILE_EXT";
my $fsep = "\t";
my $FILENAME_FIELD = "Submitted File Name";					# key for col containing file names

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################

my $working_dir = shift @ARGV;
chdir( $working_dir );
my ( $mf_LoH, $mf_H, $mf_headings ) = &read_manifest();
my @file_names = ();
foreach ( @$mf_LoH ){
	push( @file_names, $_->{$FILENAME_FIELD} );						# get file names from manifest
}
$RS = undef;														# undefine record separator
my @condemned_files = ();											# files to be deleted
my @dd_files = grep( /_dd.$FILE_EXT$/, @file_names ) ;
foreach my $file ( @dd_files ){									# for each data dictionary file
	my $dd_file = $file;
	$file =~ s/_dd\.$FILE_EXT$/.$FILE_EXT/;							# get corresponding data file name
	open( FILE, $file ) or
		die "Cannot open file: \"$file\" for reading.\n";
	my $file_contents = <FILE>;										# read entire file
	close( FILE );
	my $line_count = ( $file_contents =~ s/\n/\n/g );				# count newlines by substitution
	print "file: $file; line count = $line_count\n" if $opts{'v'};
	if ( $line_count == 1 ){
		push( @condemned_files, $file, $dd_file );
	}
}
$RS = "\n";															# restore record separator

my $condemned_file_count = @condemned_files;
print "files to delete: $condemned_file_count\n" if $opts{'v'};

my $lc = List::Compare->new( \@file_names, \@condemned_files );
my @retained_files = $lc->get_Lonly;

my $mf_text = join( "\t", @$mf_headings );							# print manifest file header
foreach my $file ( @retained_files ){
	foreach my $hk ( @$mf_headings ){								# foreach manifest heading
		if ( defined $mf_H->{$file}{ $hk } ){
			$mf_text .= $mf_H->{$file}{ $hk };						# add to manifest.txt output string
		}
		if ( $hk ne $mf_headings->[$#$mf_headings] ){
			$mf_text .= "\t";										# add field delimiter unless at end of line
		} else {
			$mf_text .= "\n";										# add newline
		}
	}
}

print "retained files: " . join(", ", @retained_files ) . "\n" if $opts{'v'};

unlink $MANIFEST_FILE;
open( FILE, ">$MANIFEST_FILE" ) or die "Cannot open file: \"$MANIFEST_FILE\" for writing.\n";
print FILE $mf_text;
close( FILE );
unlink @condemned_files;
exit;

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_manifest {

	if ( -f $MANIFEST_FILE ){
		cp( $MANIFEST_FILE, "$MANIFEST_FILE.bak" );
		print "Backup copy of $MANIFEST_FILE created.\n" if $opts{'v'};
	} else {
		die "File: \"$MANIFEST_FILE\" is not a plain file or does not exist.\n";
	}

	open( FILE, $MANIFEST_FILE ) or die "Cannot open manifest file: \"$MANIFEST_FILE\" for reading.\n";
	my @mf_headings = split( /$fsep/, <FILE> );				# head headings from first line into list
	$#mf_headings =~ s/\n//;									# remove \n from last element
	close( FILE );
	open( FILE, $MANIFEST_FILE ) or die "Cannot open manifest file: \"$MANIFEST_FILE\" for reading.\n";
	my $mf_LoH = &read_table_in2_LoH_fromfh( \*FILE, $fsep );
	close( FILE );
	my %mhash = ();
	foreach ( @$mf_LoH ){
		print $_->{$FILENAME_FIELD} . "\n" if $opts{'v'};
		$mhash{$_->{$FILENAME_FIELD}} = $_;
	}
	return( $mf_LoH, \%mhash, \@mf_headings );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
