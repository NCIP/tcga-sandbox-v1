#! /usr/bin/perl -w
####################################################################################################
my $revision = '$Revision: 18048 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our $rel_date       = '$Date: 2013-01-24 11:51:56 -0500 (Thu, 24 Jan 2013) $';
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
    warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
our $pgm_name       = "dbGaPvalidator.pl";
our $VERSION        = "v1.1.0 ($revision)";
our $start_date     = "Tue Jul 12 16:44:32 EDT 2011";
####################################################################################################
#   Eric E. Snyder (c) 2011
#   National Cancer Institute [C] SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#   USA
####################################################################################################
=head1 NAME

dbGaPvalidator.pl

=head1 SYNOPSIS

dbGaPvalidator.pl <archive_directory>

=head1 USAGE

I<dbGaPvalidator.pl> validates a directory containing subject and/or phenotype files in a
tab-delimited format according to the specification at
https://wiki.nci.nih.gov/download/attachments/43352313/Submission_Guide_Instructions.doc.

=head1 IMPLEMENTATION DETAILS

=head2 Data Structures

Principle:  The hash key "data" always refers to data read in from the tcga_*.txt files, in this
case a list of hashes, allowing access to data in row order using a key to access the specific
column.  The "specs" key in $dataDict references the same data but as a HoH, using VARNAME as a
key to access the rows.

The key "headings" refers to information read from fileSpec.json and always points to an array
ordered as the rows in the original file.

=begin text
$allData = (	tcga_samp_BLCA_dd	=>	{ 	headings	=>	[	VARNAME, VARDESC, TYPE, VALUES ],
											data		=>	[	{	VARNAME	=>	"bcr_sample_barcode",
																	TYPE	=>	"string",
																	VALUES	=>	[],
																	VARDESC	=>	"The unique identifier for an individual....",
																},
																{	VARNAME	=>	"bcr_analyte_barcode",
																	TYPE	=>	"string",
																	VALUES	=>	[],
																	VARDESC	=>	"The unique identifier for an individual analyte....",
																},
																...
															],
										},
				tcga_samp_BLCA		=>	{	headings	=>	[	bcr_sample_barcode, bcr_analyte_barcode, bcr_aliquot_barcode,
																aliquot_amount, biospecimen_barcode_bottom, ...
															],
											data		=>	[	{	bcr_sample_barcode	=>	"TCGA-BL-A0C8-01A",
																	bcr_analyte_barcode	=>	"TCGA-BL-A0C8-01A-11D",
																	bcr_aliquot_barcode	=>	"TCGA-BL-A0C8-01A-11D-A10W-05",
																},
																{	bcr_sample_barcode	=>	"TCGA-BL-A0C8-10A",
																	bcr_analyte_barcode	=>	"TCGA-BL-A0C8-10A-01D",
																	bcr_aliquot_barcode	=>	"TCGA-BL-A0C8-10A-01W-A12W-08",
																},
																...
															],
										},
				...,
			);
=end text
=head2 dataDict

=begin text
$dataDict =	(	tcga_samp_BLCA		=>	{	headings	=>	[	VARNAME, VARDESC, TYPE, VALUES ],
											specs		=>	{	bcr_sample_barcode	=>	{	VARNAME	=>	"bcr_sample_barcode",
																							TYPE	=>	"string",
																							VALUES	=>	[],
																							VARDESC	=>	"The unique identifier for an individual tissue sample ....",
																						},
																bcr_analyte_barcode	=>	{	VARNAME	=>	"bcr_analyte_barcode",
																							TYPE	=>	"string",
																							VALUES	=>	[],
																							VARDESC	=>	"The unique identifier for an individual analyte....",
																						},
																...,
															},
											data		=>	[	{	VARNAME	=>	"bcr_sample_barcode",
																	TYPE	=>	"string",
																	VALUES	=>	[],
																	VARDESC	=>	"The unique identifier for an individual....",
																},
																{	VARNAME	=>	"bcr_analyte_barcode",
																	TYPE	=>	"string",
																	VALUES	=>	[],
																	VARDESC	=>	"The unique identifier for an individual analyte....",
																},
																...
															],
										}
			);
=end text

=head3 $fileData

=begin text
$fileData =	(	tcga_samp_BLCA		=>	{	headings	=>	[	bcr_sample_barcode, bcr_analyte_barcode, bcr_aliquot_barcode,
																aliquot_amount, biospecimen_barcode_bottom, ...
															],
											data		=>	[	{	bcr_sample_barcode	=>	"TCGA-BL-A0C8-01A",
																	bcr_analyte_barcode	=>	"TCGA-BL-A0C8-01A-11D",
																	bcr_aliquot_barcode	=>	"TCGA-BL-A0C8-01A-11D-A10W-05",
																},
																{	bcr_sample_barcode	=>	"TCGA-BL-A0C8-10A",
																	bcr_analyte_barcode	=>	"TCGA-BL-A0C8-10A-01D",
																	bcr_aliquot_barcode	=>	"TCGA-BL-A0C8-10A-01W-A12W-08",
																},
																...
															],
										),
			);
=end text

=head3 $reqFiles

=begin text
Contains file headings read directly from fileSpecs.json.

$reqFiles =	(	tcga_samp_BLCA_dd	=>	{ 	headings	=>	[	VARNAME, VARDESC, TYPE, VALUES ],
										},
				tcga_samp_BLCA		=>	{	headings	=>	[	bcr_sample_barcode, bcr_analyte_barcode, bcr_aliquot_barcode,
																aliquot_amount, biospecimen_barcode_bottom, ...
															],
										},
			);
=end text

=cut
####################################################################################################
#   Testbed:	/home/eesnyder/projects/nih/dbGaP/validator/t/
#   Cmdline:	dbGaPvalidator.pl samples
####################################################################################################
#   History:
#   v1.0.0:
####################################################################################################
use strict;
use warnings;
# use Test::More qw(tests 14);
use File::Spec;
use Module::Build;
use Tie::IxHash;
use Cwd 'abs_path';
use JSON::XS;
use Array::Compare;
use List::Compare;
use Data::Types qw(:all);
use Term::ReadKey;
use Date::Handler;

use MyUsage;
use EESnyder;
my $ArrayComp = Array::Compare->new();

my $json = new JSON::XS;

my $FILE_SPECS			= "fileSpecs.json";
my $FSPECS_GENERATOR	= "headings2json.pl";
my $JSON_DUMP_FILE		= "tmp.json";
my $MANIFEST			= "tcga_manifest";
my $FILENAME_FIELD		= "Submitted File Name";				# key for col containing file names

my $EXT					= "txt";								# file extension
my $DELIM				= "\t";									# field delimiter
my $TEST				= "";

my %REGEX = (
	bcr_aliquot_barcode => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-\d{2}[A-Z]{1}-\d{2}[A-Z]{1}-[A-Z0-9]{4}-\d{2}$/,
	bcr_analyte_barcode => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-\d{2}[A-Z]{1}-\d{2}[A-Z]{1}$/,
	bcr_patient_barcode => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}$/,
	bcr_portion_barcode => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-\d{2}[A-Z]{1}-\d{2}$/,
	bcr_sample_barcode  => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-\d{2}[A-Z]{1}$/,
	bcr_slide_barcode   => qr/^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-\d{2}[A-Z]{1}-\d{2}-[T|M|B]S[A-Z0-9]$/,
);
my @t0 = ( time, (times)[0] );                                 # start execution timer
my %opts    = ();                                               # init cmdline arg hash

my %usage   = (                                                 # init paras for getopts
    'B' => {
        'type'     => "boolean",
        'usage'    => "print program banner to STDOUT",
        'required' => 0,
        'init'     => 1,
    },
    'C' => {
        'type'     => "boolean",
        'usage'    => "do case-insensitive check of enumerated values",
        'required' => 0,
        'init'     => 0,
    },
    'D' => {
        'type'     => "boolean",
        'usage'    => "print copious debugging information",
        'required' => 0,
        'init'     => 0,
    },
    'F' => {
        'type'     => "string",
        'usage'    => "field delimiter",
        'required' => 0,
        'init'     => $DELIM,
    },
    'J' => {
        'type'     => "boolean",
        'usage'    => "create JSON file containing all archive data",
        'required' => 0,
        'init'     => 0,
    },
    'U' => {
        'type'     => "boolean",
        'usage'    => "be ultra verbose",
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
    'm' => {
        'type'     => "string",
        'usage'    => "archive\'s manifest file (w/o extension)",
        'required' => 1,
        'init'     => $MANIFEST,
    },
    's' => {
        'type'     => "string",
        'usage'    => "full-qualified location of file header specification file",
        'required' => 0,
        'init'     => $FILE_SPECS,
    },
    'v' => {
        'type'     => "boolean",
        'usage'    => "give verbose output identifying successes, too",
        'required' => 0,
        'init'     => 0,
    },
);
my @infiles = qw( <archive_dir> );
my $banner = &Usage( \%usage, \%opts, \@infiles );		# read cmdline parameters
print $banner unless $opts{'B'};						# print program banner w/parameters to STDOUT
print $pgm_name ."_$VERSION\n" .
	"Start date:    $start_date\n" .
	"End date:  $rel_date\n\n" if $opts{'V'};
my $delim = $opts{'F'} if $opts{'F'};					# set working field delimiter
my $fileSpecs = $opts{'s'};								# set name of file specification file
my $manifest = $opts{'m'};								# set archive's manifest name
my $json_dump_file = '';
if ( $opts{'J'} ){
	$json_dump_file = $JSON_DUMP_FILE;					# set name for JSON dump file
}
####################################################################################################
################################## Put Main Between Here ... #######################################

#	loop over directories specified on cmdline and validate contents

my $line = "-" x 80 . "\n";										# create a horizontal line for headings
my %dataTypeHash = ();											# accumulator for unique var TYPEs
my $errcnt = 0;													# accumulated errors
foreach my $dir ( @ARGV ){										# foreach directory to be validated
	chdir( $dir );												# change into directory to start processing
	my $time = `date`;											# get date and time
	my $abs_path = abs_path();									# get fully qualified path to cwd
	print "$line$abs_path\n$time$line";							# print name of directory/archive being processed
	my $reqFiles = &read_json_specs( $fileSpecs );				# get pre-defined file specifications from fileSpec.json
	my (	$manifestLoH,										# LoH of manifest,
			$allData,								 			# initially an undef hash of dd & data fnames
		) = &read_manifest( $reqFiles );						# read the manifest containing list of file names
	my %dataDict = ();											# headings and data for data dictionary files
	my %fileData = ();											# headings and data for all file types
	foreach my $ftype ( sort keys %$allData ) {					# go through files from manifest (dd & data treated same here)
		my $fname = "$ftype.$EXT";								# make a filename from the file type
		my $fileLoH;											# master hash containing file data
		my $heads;
		if ( $ftype =~ m/_dd$/){								# if file is a data dictionary ...
			( $fileLoH, $heads ) =
				&read_table_in2_LoH_fromRRfile($fname);			# read the data file into a LoH allowing multi-valued element
		} else {												# if file is a data file
			( $fileLoH, $heads ) =
				&read_table_in2_LoH_fromfile( $fname );			# read the data file into a LoH
		}
		my $err	= &check_file_headings(
							$reqFiles->{$ftype}{"headings"},	# reference headings from manifest
							[ keys %{$fileLoH->[0]} ],			# headings read from dd or data files
							$ftype,								# data/dd file name root
					);

		my $headings = $reqFiles->{$ftype}{"headings"};			# retrieve a ptr to list of file headings from fileSpecs

		$allData->{$ftype}{"headings"} = $headings;				# container for all file data for general checking
		$allData->{$ftype}{"data"} = $fileLoH;					# add file data to spec data struct

		if ( $ftype =~ m/_dd$/){								# if file is a data dictionary ...
			my $dtype = $ftype; $dtype =~ s/_dd$//;				# save DD info using filetype (w/o "_dd")
			$dataDict{$dtype}{"headings"} = $headings;			# for checks where DD role is important
			$dataDict{$dtype}{"data"} = $fileLoH;
		} else {
			$fileData{$ftype}{"headings"} = $headings;			# for checks where data is checked against DD
			$fileData{$ftype}{"data"} = $fileLoH;				# add file data to spec data struct
		}
	}
	&process_encoded_values( \%dataDict );						# remove encoding from encoded value records
	&populate_dataDict_specs( \%dataDict );						# add "spec" key to dataDict to facilitate type/value checking

	if ( $json_dump_file ){
		open( FILE, ">$json_dump_file" );
		print FILE $json->pretty->encode( \%fileData );
		close(FILE);
	}

#	compare structure of data from sample files to the JSON spec

	foreach my $ftype ( keys %$allData ){						# loop through all filetypes specified in fileSpec.json
		my $lc = List::Compare->new( '--unsorted', $allData->{$ftype}{"headings"}, $reqFiles->{$ftype}{"headings"} );

#		if( $ArrayComp->compare( $allData->{$ftype}{"headings"} , $reqFiles->{$ftype}{"headings"} ) ){
		if ( $lc->get_symdiff == 0 ){
			print "$ftype arrays are identical\n" if $opts{'v'};
		} else {
			print "Headings for file $ftype.$EXT do not match JSON prototype.\n";
			$errcnt++;
		}
		if ( $opts{'V'} ){
			print "allData = " . join( ", ", @{$allData->{$ftype}{"headings"} } ) . "\n";
			print "reqFiles = " . join( ", ", @{$reqFiles->{$ftype}{"headings"} } ) . "\n\n";
		}
	}


	foreach my $ftype ( keys %dataDict ){												# foreach file type
		my @varnames = ();
		foreach ( @{$dataDict{$ftype}{"data"}} ){										# get var names that will appear in data file
			push( @varnames, $_->{"VARNAME"} );											# from data dictionary file
		}
		print "$ftype variables: " . join( ", ", @varnames ) . "\n" if $opts{'V'};

		unless ( -f "$ftype.$EXT"){
			print "Data file: \"$ftype.$EXT\" does not exist.\n";						# if file does not exist, dont contine checking fields
			next;
		}
		foreach my $var ( @varnames ){													# foreach variable name
			unless ( exists $fileData{ $ftype }{"data"}[0]{$var} ){						# check that it exists in data file
				print "heading: \"" . $var .
						"\" not found in file: \"$ftype.$EXT\".\n";
				$errcnt++;
			} else {
				print "heading: \"" . $var .
						"\" *was* found in file: \"$ftype.$EXT\".\n" if $opts{'V'};
			}
		}
		foreach my $record ( @{$fileData{ $ftype }{"data"}} ){							# foreach data file record
			foreach my $var ( @varnames ){												# foreach data field
				print "record{$var}: $record->{$var}\n" if $opts{'U'};
				if ( exists $REGEX{$var} ){												# if there is a REGEX for that variable type
					if ( $record->{$var} =~ m/$REGEX{$var}/ ){							# check that it matches
						print "\"$record->{$var}\" matches \"$var\" REGEX prototype in file $ftype.$EXT.\n" if $opts{'V'};
					} else {
						print "\"$record->{$var}\" DOES NOT MATCH \"$var\" REGEX prototype in file $ftype.$EXT.\n";
						$errcnt++;
					}
				}
				my $err = &check_variable_type(	$record->{$var},
												$dataDict{$ftype}{"specs"}{$var}{"TYPE"},
												$var,
												"$ftype.$EXT" );
				if ( @{ $dataDict{$ftype}{"specs"}{$var}{"VALUES"} } ){					# check whether value is a member of an enum list
					my $regex;
					if ( defined $record->{$var} ){
						$regex = "$record->{$var}";
						$regex =~ s/([\(\)+.?*])/\\$1/g;								# escape special characters in query
					} else {
						$regex = "null";
					}
					if( grep( /^$regex$/i, @{$dataDict{$ftype}{"specs"}{$var}{"VALUES"} } ) ) {
						&print_list_finding( 	"$ftype.$EXT", $var, $record->{$var},
												"was found in enumerated list",
												$dataDict{$ftype}{"specs"}{$var}{"VALUES"},
												\*STDERR ) if $opts{'V'};
					} else {
						&print_list_finding( 	"$ftype.$EXT", $var, $record->{$var},
												"NOT FOUND found in enumerated list",
												$dataDict{$ftype}{"specs"}{$var}{"VALUES"},
												\*STDOUT );

						# try to classify errors
						# look for unnecessary quotes in DD file

						if ( grep( /"/, @{$dataDict{$ftype}{"specs"}{$var}{"VALUES"} } ) ){
							print "Ectopic quotes in DD file:\t$ftype\_dd.$EXT\t$var\n";
						}

						# data contains multiple comma-separated values to match against enumerated list

						my @dlist = split( /\s*,\s*/, $record->{$var} );
						my $lc = List::Compare->new(	'--unsorted',
														$dataDict{$ftype}{"specs"}{$var}{"VALUES"},
														\@dlist );
						if ( $lc->get_intersection ){
							print "HOWEVER: Intersection between value and dictionary:\n\"" .
									join( "\", \"", $lc->get_intersection ) . "\".\n";
							if ( $lc->get_intersection == @dlist ){
								print "All data values were found in list.\n";
							} else {
								print "...but not all data values were found in list.\n";
							}
						}
						print "\n";
						$errcnt++;
					}
				}

				$dataTypeHash{$dataDict{$ftype}{"specs"}{ $var }{"TYPE"}} = 1 ;		# record possible variable types
																					# ensure file data meets var type and value specs
			}
		}
	}
	print "\n";
	chdir( ".." );			# return to original directory
}
print "Error count: $errcnt\n";
print "dataTypeList:\n\t" . join( "\n\t", ( keys %dataTypeHash ) ) . "\n";
##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_json_specs {
	my ( $specs ) = @_;

	if ( not -f $specs or -z $specs ){									# if not a regular file or it's empty...
		$specs = &get_fileSpecs( $specs );
	} else {
		print "Using file: \"$specs\" for expected file data-field information.\n" if $opts{'v'};
	}
	$RS = undef;
	open( FILE, "$specs" ) or die "Fatal Error: Cannot open fileSpecs file: \"$specs\".\n";
	my $reqFiles = decode_json( <FILE> );
	$RS = "\n";
	close( FILE );
	return( $reqFiles );
}
####################################################################################################
#	read manifest and check against file requirements determined previously from fileSpecs.json
####################################################################################################
sub read_manifest {
	my ( $reqFiles ) = @_;

	my ( $manifestLoH, $headings ) = &read_table_in2_LoH_fromfile( "$manifest.$EXT" );	# read manifest into LoHs keyed on col name
	my %fileData = ();													# hash for filename roots
	foreach ( @$manifestLoH ){											# foreach line of manifest...
		unless( $_->{$FILENAME_FIELD} =~ s/\.$EXT$// ){ 				# strip extension, complain if expected ext not found
			print "File name: \"$_->{$FILENAME_FIELD}\"" .				# complain if expected ext not found
					" does not have expected extension (.$EXT)\n";
			die "FATAL ERROR\n";
		}
		$fileData{ $_->{$FILENAME_FIELD} } = undef;						# assign name of each file in manifest as a hash key
	}
	if ( $opts{'V'} ){
		print "manifest keys:\n\t" . join( "\n\t", sort keys ( %fileData ) )."\n";
	}
	foreach ( sort keys %fileData ) {									# check filetypes from manifest against JSON
		unless ( exists $reqFiles->{$_} ){								# file must exist in JSON spec
			print "Required file: \"$_.$EXT\" not found.\n";
		} else {
			print "Required file: \"$_.$EXT\" *was* found.\n" if $opts{'V'};
		}
	}
	print "\n" if $opts{'V'};
	return( $manifestLoH, \%fileData );
}
####################################################################################################
sub populate_dataDict_specs {
	my ( $dd ) = @_;

	foreach my $ftype ( keys %$dd ){
		foreach my $fieldHash ( @{ $dd->{$ftype}{"data"} } ) {
			$dd->{$ftype}{"specs"}{ $fieldHash->{"VARNAME"} } = $fieldHash;
		}
	}
}
####################################################################################################
sub print_list_finding {
	my ( $file, $recordName, $recordValue, $verdict, $enumList, $fh ) = @_;


	my $op =	"DATA_FILE:	\"$file\"\n" .
				"RECORD_NAME:	\"$recordName\"\n" .
				"RECORD_VALUE:	\"$recordValue\"\n" .
				"VERDICT:	$verdict\n" .
				"LIST:		" .
				"\"" . join( "\", \"", @$enumList ) . "\"\n";

	print $fh $op;
	return;
}
####################################################################################################
sub	process_encoded_values{
	my ( $dd ) = @_;

	foreach my $ftype ( keys %$dd ){
		foreach my $fieldHash ( @{ $dd->{$ftype}{"data"} } ) {
			if ( $fieldHash->{"TYPE"} =~ m/^encoded value$/i ) {
				for( my $i = 0; $i < @{$fieldHash->{"VALUES"}}; $i++ ){
					if ( $fieldHash->{"VALUES"}[$i] =~ s/=(.+)$// ){
						print "coded value " . $fieldHash->{"VALUES"}[$i] . " = \"" . $1 ."\"\n";
					} else {
						print "VALUES array element $i: " . $fieldHash->{"VALUES"}[$i] . "\n";
					}
				}
			}
		}
	}
}
####################################################################################################
#	check_file_headings()
#	Check the list of file headings read from fileSpec.json against those read in from dd files
####################################################################################################
sub check_file_headings {
	my ( $refHeadings, $fileHeadings, $ftype ) = @_;

	my $lc = List::Compare->new( $refHeadings, $fileHeadings );
	my @intersection = $lc->get_intersection;
	my $strikes = 0;										# ways that file heads do not agree with reference
	if(	@$refHeadings != @$fileHeadings ){
		print "$ftype heading count is not equal to reference count.\n" .
			  "Reference: " . @$refHeadings . "\n" .
			  "File:      " . @$fileHeadings . "\n";
		my %headCount = ();
		foreach ( @$refHeadings ){
			if ( exists $headCount{$_} ){
				$headCount{$_}++;
			} else {
				$headCount{$_} = 1;
			}
		}
		foreach ( keys %headCount ){
			if ( $headCount{$_} > 1 ){
				print "Heading: \"$_\" present $headCount{$_} times in heading list.\n";
			}
		}
		$strikes++;
	}
	if ( @intersection != @$refHeadings ){
		print "$ftype headings do not match reference list\n";
		$strikes++;
	}
	if ( $strikes ) {
		print "$ftype headings are discordant with reference (strikes: $strikes)\n" .
			"reference: \"" . join("\", \"", $lc->get_Lonly ) . "\"\n" .
			"from file: \"" . join("\", \"", $lc->get_Ronly ) . "\"\n";
	}
	my $errcnt = ( $strikes? 1: 0 );
	return( $errcnt );
}
####################################################################################################
sub get_fileSpecs {
	my ( $specs ) = @_;

	print STDERR "The required JSON file description file \"$specs\" does not exist.\n" ;
	if( ! -f "$MANIFEST.$EXT" ){
		die "I would like to help but \"$MANIFEST.$EXT\" does not exist either.";
	}

	print STDERR "Would you like to generate that file from \"$MANIFEST.$EXT\" now?";
	ReadMode("cbreak");
	my $char = "";
	while( $char !~ m/[YyNn]/ ){
		print STDERR "Please answer: " if $char;
		print STDERR " (y/n)\n";
		$char = ReadKey( 0 );
	}
	if ( $char =~ m/[Yy]/ ){
		print "Executing $FSPECS_GENERATOR ...";
		`$FSPECS_GENERATOR $MANIFEST.$EXT`;
		print "done.\n";
		if ( $? ){
			print "Error: $FSPECS_GENERATOR terminated with non-zero status;\n" .
					"Go figure out what is wrong.\n";
			exit(0xfff);
		}
	} else {
		print "Well...you've got to have a $FILE_SPECS file.\n" .
				"We cannot continue without it.\n" .
				"Your only alternative is to rerun the program with the -s <file> option,\n" .
				"specifying an alternative specification file.\n";
		exit( 0 );
	}
	return( $FILE_SPECS );
}
####################################################################################################
sub check_variable_type {
	my ( $value, $type, $rec, $file ) = @_;

	return ( 0 ) if ( $value eq "null" );						# null appears to be okay
	$type = lc $type;
	if ( $type eq "string" ) {
		if ( is_string( $value ) ){
			print "\"$value\" is a string.\n" if $opts{'U'};
			return( 0 );
		} else {
			print "$rec: \"$value\" is NOT a string in $file.\n";
			return( 1 );
		}
	} elsif ( $type eq "integer" ){
		if ( is_int( $value ) ){
			print "\"$value\" is an integer.\n" if $opts{'U'};
			return( 0 );
		} else {
			print "$rec: \"$value\" is NOT an integer in $file.\n";
			return( 1 );
		}
	} elsif( $type eq "decimal" ) {
		if ( is_decimal( $value ) ){
			print "\"$value\" is a decimal.\n" if $opts{'U'};
			return( 0 );
		} else {
			print "$rec: \"$value\" is NOT an decimal in $file.\n";
			return( 1 );
		}
	} elsif( $type eq "encoded value" ) {
		if ( is_int( $value ) ){
			print "\"$value\" is an integer encoded value.\n" if $opts{'U'};
			return( 0 );
		} else {
			print "$rec: \"$value\" is NOT an integer encoded value in $file.\n";
			return( 1 );
		}
	} else {
		print "$rec has unknown data type: \"$type\" in $file.\n";
	}
}
####################################################################################################
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
