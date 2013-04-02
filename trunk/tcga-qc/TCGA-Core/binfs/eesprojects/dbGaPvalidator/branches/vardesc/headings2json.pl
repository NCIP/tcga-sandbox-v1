#! /usr/bin/perl -w
#$Id: headings2json.pl 18048 2013-01-24 16:51:56Z snyderee $
####################################################################################################
our	$pgm_name		= "headings2json.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Wed Jun 22 14:11:02 EDT 2011";
our	$rel_date		= "Thu Mar  1 10:41:07 EST 2012";
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
use Cwd;
use JSON::XS;

my $json = new JSON::XS;
my $FILE_SPECS = "fileSpecs.json";
my $DATA_DICTS = "dataDicts.json";
my $NEW_FILE_DIR = "new_files";

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
	'N' => {
		'type'     => "string",
		'usage'    => "VARNAME for which to search; print with quotes",
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
	'n' => {
		'type'     => "string",
		'usage'    => "VARNAME for which to search; print without quotes",
		'required' => 0,
		'init'     => 0,
	},
	'q' => {
		'type'     => "boolean",
		'usage'    => "suppress removal of extraneous and unnecessary quotes in dataDict VARDESC",
		'required' => 0,
		'init'     => 0,
	},
	'r' => {
		'type'     => "string",
		'usage'    => "file with replacement dictionary data",
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
my %fileData = ();											# for regular data files
my %dataDict = ();											# for data dictionary files
foreach ( @$manifestLoH ){									# foreach line of manifest...
	unless( $_->{$MANIFEST_FILENAME} =~ s/\.$EXT$// ){ 		# strip extension, complain if expected ext not found
		die "File name: \"$_->{$MANIFEST_FILENAME}\"" .
				" does not have expected extension (.$EXT)\n";
	}
	if ( $_->{$MANIFEST_FILENAME} =~ s/_dd$// ){			# assign name of each file (less "_dd") in manifest as a hash key
		$dataDict{ $_->{$MANIFEST_FILENAME} } = undef;		# for data dictionary files
	} else {
		$fileData{ $_->{$MANIFEST_FILENAME} } = undef;		# assign name of each file in manifest as a hash key
	}														# for the data files
}

my $fileData = &popFileData( \%fileData ); 					# populate datafile hash
my $dataDict = &popDataDict( \%dataDict, $fileData );		# populate data dictionary hash


if ( $opts{'v'} ){
	print "\$dataDict = \"$dataDict\"\n";
	print "\\%dataDict = \"" . \%dataDict . "\"\n";
}

my ( $null_vardesc, $nullVardescByFile ) = &scanDataDict( $fileData );
&getNullVardescData( $nullVardescByFile, $fileData );

if ( $opts{'r'} ){
	my $replacementData = &readReplacementDataDictData( $opts{'r'} );
	&doReplacements( $fileData, $replacementData );
#	&deleteData4nulls( $fileData );					#I'm going to do this a different way, using 'dictOrder'
}

mkdir( $NEW_FILE_DIR );
&create_dd_files( $fileData );
&create_data_files( $fileData );

&printNullVardescData( $nullVardescByFile, $fileData );
&write_file_by_name( "nullVardesc", $null_vardesc );		# print result of scan for null VARDESCs
&print_json( $DATA_DICTS, $dataDict );						# Print out .json file containing %dataDict
&print_json( $FILE_SPECS, $fileData );						# Print out .json file containing %fileData


exit( 0 );

##################################      ... and Here         #######################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
# deleteData4nulls
####################################################################################################
sub deleteData4nulls {
	my ( $fileData ) = @_;

	foreach my $file ( keys %$fileData ){						# foreach filename key
		my @headings = ();
		if ( exists $fileData->{$file}{'data'} ){		# presence of key => it is a DD file
			@headings = @{$fileData->{$file}{'data'}};
			my $dHead = join( "\t", @headings ) ;
			#if ( $rhead eq $dHead ){							# check that repl headings match file headings
			#	my @dictHashDeleteList = ();					# contains indices of dictHash that need to be deleted
			#	foreach my $replKey ( keys %{$repl->{'dict'}} ){	# loop through VARNAMEs that need replacing
			#		if ( exists $fileData{ $file }{ 'dict' }{ $replKey } ){
			#			$fileData{ $file }{ 'dict' }{ $replKey } = $repl->{'dict'}{ $replKey }; # replace fileData hash with new one
			#		}
			#	}
			#}
		}
	}

}
####################################################################################################
# doReplacements
####################################################################################################
sub doReplacements {
	my ( $dataDict, $repl ) = @_;

#	fix dataDictionary
	my $rhead = join( "\t", @{$repl->{'dictHeadings'}});
	foreach my $file ( keys %$fileData ){						# foreach filename key
		my @headings = ();
		if ( exists $fileData->{$file}{'dictHeadings'} ){		# presence of key => it is a DD file
			@headings = @{$fileData->{$file}{'dictHeadings'}};
			my $dHead = join( "\t", @headings ) ;
			if ( $rhead eq $dHead ){							# check that repl headings match file headings
				my %dictDeleteHash = ();					# contains keys of dictHash that need to be deleted
				foreach my $replKey ( keys %{$repl->{'dict'}} ){	# loop through VARNAMEs that need replacing
					if ( exists $fileData{ $file }{ 'dict' }{ $replKey } ){
						$fileData{ $file }{ 'dict' }{ $replKey } = $repl->{'dict'}{ $replKey }; # replace fileData hash with new one
					}
				}
				foreach my $dictKey ( keys %{ $fileData->{$file}{'dict'} } ){		# loop through dict HoH
					if ( !defined $fileData->{ $file }{ 'dict' }{ $dictKey }{'VARDESC'} ||
								  $fileData->{ $file }{ 'dict' }{ $dictKey }{'VARDESC'} =~ m/null/i ){
						delete $fileData->{ $file }{ 'dict' }{ $dictKey };
						$dictDeleteHash{ $dictKey } = 1;
					}
				}
				$fileData->{$file}{'nullHeadings'} = \%dictDeleteHash;
			}
		}
	}
}
####################################################################################################
# readReplacementDataDictData
####################################################################################################
sub readReplacementDataDictData {
	my ( $file ) = @_;

	my %rData = ();
	open( FILE, "$file" ) or die "Cannot open file: \"$file\" for reading.\n";
	my $ln = 0;												# (non-comment) line number
	my $rec = 0;											# record number
	my @headings = ();
	while( <FILE> ){
		chomp;
		next if /^#.*/;
		if ( $ln++ ){
			my $subsequentLine = $_;
			my @dataFields = split( /$DELIM/, $subsequentLine, @headings );	# provide LIMIT so trailing null fields are not ignored
			if ( @dataFields < @headings ){
				my $fields = @dataFields;
				my $heads = @headings;
				die "ERROR: field count ( $fields ) is less than heading count ( $heads ) where:\n" .
					"file = $file" . "_dd\n" .
					"data fields = \"" . join( "\", \"", @dataFields ) . "\".\n" .
					"headings    = \"" . join( "\", \"", @headings   ) . "\".\n" ;
			}
			for (my $i = 0; $i < @headings; $i++ ){			# populate the hash for the data record in question
				if ( $dataFields[$i] =~ m/^null$/i ){
					$dataFields[$i] = "";
				}
				if ( $dataFields[$i] =~ s/^"([^"]*)"$/$1/ ){
					warn "Unnecessary quotes removed from \"$dataFields[$i]\" in file = $file.\n" if $opts{'d'};
				}
#				$rData{"dict"}[$rec]{ $headings[$i] } = $dataFields[$i];
				$rData{"dict"}{ $dataFields[0] }{ $headings[$i] } = $dataFields[$i];
			}
			$rec++;											# increment record number
		} else {
			my $firstline = $_;
			@headings = split( /$DELIM/, $firstline );
			$rData{"dictHeadings"} = \@headings;
		}
	}
	close( FILE );
	return( \%rData );
}
####################################################################################################
# create_dd_files
####################################################################################################
sub create_dd_files {
	my ( $fileData ) = @_;

	foreach my $file ( keys %$fileData ){					# foreach filename key
		my @headings = ();
		if ( exists $fileData->{$file}{'dictHeadings'} ){	# presence of key => it is a DD file
			my $fname = $NEW_FILE_DIR . "/" . $file . "_dd.$EXT";
			open( FILE, ">$fname" ) or die "Cannot create new data dictionary file \"$fname\"\n";
			@headings = @{$fileData->{$file}{'dictHeadings'}};
			print FILE join( "\t", @headings ) . "\n";	# 	print data dict headings
		}
		if ( exists $fileData->{$file}{'dict'} ){			# get the data for the data dict
			if( @headings ){
#				foreach my $dictKey ( keys %{ $fileData->{$file}{'dict'} } ){
				foreach my $dictKey ( @{ $fileData->{$file}{'dictOrder'} } ){
					my $i = @headings - 1;
					my $fldCnt = 0;
					foreach my $heading ( @headings ){
						next if !defined $fileData->{$file}{'dict'}{$dictKey}{ $heading };
						$fldCnt++;
						print FILE $fileData->{$file}{'dict'}{$dictKey}{ $heading };
						print FILE "\t" if $i--;			# print data for data dict
					}
					print FILE "\n" if $fldCnt;
				}
			} else {
				die "ERROR: in create_dd_files: if the dict headings exist, there should be a dict hash as well.\n";
			}
		}
		close( FILE );
	}
}
####################################################################################################
# create data files
####################################################################################################
sub create_data_files {
	my ( $fileData ) = @_;

	foreach my $file ( keys %$fileData ){					# foreach filename key
		my @headings = ();
		if ( exists $fileData->{$file}{'headings'} ){		# check presence of key
			my $fname = $NEW_FILE_DIR . "/" . $file . ".$EXT";
			open( FILE, ">$fname" ) or die "Cannot create new data file \"$fname\"\n";
			if ( $opts{'r'} ){													# 	if in replacement mode ...
				foreach my $head ( @{$fileData->{$file}{'headings'}} ){			#	loop through headings
					if ( !defined $fileData->{$file}{'nullHeadings'}{$head} ){	#	if not on nullHeading list (hash)
						push( @headings, $head );								#	add to working headings list
					}
				}
			} else {										# if !-r, do it a simpler way
				@headings = @{$fileData->{$file}{'headings'}};
			}
			print FILE join( "\t", @headings ) . "\n";		# print  headings
		}
		if ( exists $fileData->{$file}{'data'} ){			# get the data
			if( @headings ){
				foreach my $hash ( @{ $fileData->{$file}{'data'} } ){
					my $i = @headings - 1;
					foreach my $heading ( @headings ){
						print FILE $hash->{ $heading } ;	# print data for
						print FILE "\t" if $i--;
					}
					print FILE "\n";
				}
			} else {
				die "ERROR: in create_data_files: if the headings exist, there should be a data hash as well.\n";
			}
		}
		close( FILE );
	}
}
####################################################################################################
# getNullVarsdecData
####################################################################################################
sub getNullVardescData {
	my( $varnamesByFile, $fileData ) = @_;

	foreach my $file ( sort keys %$varnamesByFile ){
		for(my $i = 0; $i < @{$fileData->{$file}{'data'} }; $i++ ){
			my $data = $fileData->{$file}{'data'}[$i];
			foreach my $var ( @{ $varnamesByFile->{ $file } } ) {
				print "\"" . $data->{$var} . "\"\n" if ( $var eq $opts{'N'} );
				print        $data->{$var} .   "\n" if ( $var eq $opts{'n'} );
				if ( $data->{$var} eq "" ){
					if( exists $fileData->{$file}{'vardescNullData'}{$var} ){
						$fileData->{$file}{'vardescNullData'}{$var}++;
					} else {
						$fileData->{$file}{'vardescNullData'}{$var} = 1;
					}
				}
				if( exists $fileData->{$file}{'vardescTotal'}{$var} ){
					$fileData->{$file}{'vardescTotal'}{$var}++;
				} else {
					$fileData->{$file}{'vardescTotal'}{$var} = 1;
				}
			}
		}
	}
}
####################################################################################################
# printNullVardescData
####################################################################################################
sub printNullVardescData {
	my( $varnamesByFile, $fileData ) = @_;

	my $fname = "nullVars.tbl";
	open( FILE, ">$fname" ) or die "Cannot open file \"$fname\" for writing.\n";
	my $hline = "-" x 70 . "\n";
	my $head = sprintf("%9s\t%5s\t\t%5s\t%s\n", qw( NullCount Total Ratio VARNAME ) );
	print FILE $hline . $head . $hline;

	foreach my $file ( sort keys %$varnamesByFile ){
		print FILE "$file:\n$hline";
		foreach my $var ( sort keys %{$fileData->{$file}{'vardescTotal'}} ){
			my $count = $fileData->{$file}{'vardescNullData'}{$var} || 0;
			my $total = $fileData->{$file}{'vardescTotal'}{$var};
			my $ratio = $count / $total;
			printf FILE ("%9d\t%5d\t=\t%5.3f\t%s\n", $count, $total, $ratio, $var );
#			print "$var\n";
		}
		print FILE $hline;
	}
	close( FILE );
}
####################################################################################################
# scanDataDict
####################################################################################################
sub scanDataDict {
	my ( $dataDict ) = @_;

	my $dataDictReport = "";
	my %nullVarnamesByFile = ();
	foreach my $file ( keys %$dataDict ){
		my @varnames = ();
		$nullVarnamesByFile{$file} = ();
		foreach my $rec ( keys %{ $dataDict->{$file}{'dict'} } ){
			if ( $dataDict->{$file}{'dict'}{ $rec }{'VARDESC'} eq "" ){
				push( @varnames, $dataDict->{$file}{'dict'}{ $rec }{'VARNAME'} );
				push( @{$nullVarnamesByFile{$file}}, $dataDict->{$file}{'dict'}{ $rec }{'VARNAME'} );

			}
		}
		if ( @varnames ){
			$dataDictReport .= "\n$file:\n" .
			join( "\n", @varnames ) .
			"\n";
		} else {
			delete $nullVarnamesByFile{$file};
		}
	}
	return( $dataDictReport, \%nullVarnamesByFile );
}
####################################################################################################
# Process the regular data files, extracting headings in %fileData
####################################################################################################
sub popFileData {
	my ( $fileData ) = @_;

	foreach my $file ( sort keys ( %$fileData ) ) {
		my $fname = "$file.$EXT";
		open( FILE, "$fname" ) or die "Cannot open file: \"$fname\" for reading.\n";
		my $ln = 0;
		my $rec = 0;
		my $firstline = "";
		my @headings = ();
		while( <FILE> ){
			chomp;
			next if /^#.*/;
			if ( $ln++ ){
				my $subsequentLine = $_;
				my @dataFields = split( /$DELIM/, $subsequentLine );
				if ( @dataFields != @headings ){
					my $fields = @dataFields;
					my $heads = @headings;
					die "ERROR: field count ( $fields ) doesn\'t match heading count ( $heads ) where:\n" .
						"file = $file" . "_dd\n" .
						"data fields = \"" . join( "\", \"", @dataFields ) . "\".\n" .
						"headings    = \"" . join( "\", \"", @headings   ) . "\".\n" ;
				}
				for (my $i = 0; $i < @headings; $i++ ){			# populate the hash for the data record in question
					#if ( $dataFields[$i] =~ m/^null$/i ){
					#	$dataFields[$i] = "";
					#}
					if ( ! defined $dataFields[$i]  ){
						$dataFields[$i] = "null";
					}
					if ( $opts{'q'} ){
						if ( $dataFields[$i] =~ s/^"([^"]*)"$/$1/ ){
							warn "Unnecessary quotes removed from \"$dataFields[$i]\" in file = $fname.\n" if $opts{'d'};
						}
					}
					$fileData->{$file}{"data"}[$rec]{ $headings[$i] } = $dataFields[$i];
				}
				$rec++;											# increment record number
			} else {
				my $firstline = $_;
				@headings = split( /$DELIM/, $firstline );
				$fileData->{$file}{"headings"} = \@headings;
			}
		}
		close( FILE );
	}
	return( $fileData );
}
####################################################################################################
# Process the data dictionary files, extracting heads and data into %dataDict
####################################################################################################
sub popDataDict{
	my ( $dataDict, $fileData ) = @_;
	foreach my $file ( sort keys ( %$dataDict ) ) {
		my $fname = $file . "_dd.$EXT";
		open( FILE, "$fname" ) or die "Cannot open file: \"$fname\" for reading.\n";
		my $ln = 0;												# (non-comment) line number
		my $rec = 0;											# record number
		my @headings = ();
		@{ $fileData->{ $file }{'dictOrder'} } = ();
		while( <FILE> ){
			chomp;
			next if /^#.*/;
			if ( $ln++ ){
				my $subsequentLine = $_;
				my @dataFields = split( /$DELIM/, $subsequentLine, @headings );	# provide LIMIT so trailing null fields are not ignored
				if ( @dataFields < @headings ){
					my $fields = @dataFields;
					my $heads = @headings;
					die "ERROR: field count ( $fields ) is less than heading count ( $heads ) where:\n" .
						"file = $file" . "_dd\n" .
						"data fields = \"" . join( "\", \"", @dataFields ) . "\".\n" .
						"headings    = \"" . join( "\", \"", @headings   ) . "\".\n" ;
				}
				for (my $i = 0; $i < @headings; $i++ ){			# populate the hash for the data record in question
					#if ( $dataFields[$i] =~ m/^null$/i ){
					#	$dataFields[$i] = "";
					#}
					if ( ! defined $dataFields[$i]  ){
						$dataFields[$i] = "null";
					}
					if ( ! $opts{'q'} ){
						if ( $dataFields[$i] =~ s/^"([^"]*)"$/$1/ ){
							warn "Unnecessary quotes removed from \"$dataFields[$i]\" in file = $fname.\n" if $opts{'d'};
						}
					}
#					$dataDict->{$file}{"data"}[$rec]{ $headings[$i] } = $dataFields[$i];
					$fileData->{$file}{"dict"}{ $dataFields[0] }{ $headings[$i] } = $dataFields[$i];
				}
				push( @{ $fileData->{$file}{'dictOrder'} }, $dataFields[0] );		# original order of VARNAMEs in file
			} else {
				my $firstline = $_;
				@headings = split( /$DELIM/, $firstline );
				$fileData->{$file}{"dictHeadings"} = \@headings;
			}
		}
		close( FILE );
	}
	return( $fileData );
}
####################################################################################################
#	print json from perl hash
####################################################################################################
sub print_json {
	my ( $fname, $hash ) = @_;

	open( FILE, ">$fname" ) or die "Cannot open file: \"$fname\" for writing.\n";
	print FILE $json->pretty->encode( $hash );				# Print out .json file containing %fileData
	close( FILE );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
