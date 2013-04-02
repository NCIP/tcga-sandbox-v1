#! /usr/bin/perl -w
####################################################################################################
our	$pgm_name		= "ktuple_docdiff.pl";
our	$VERSION		= "v1.0.3 (dev)";
our	$start_date		= "Thu Apr  7 01:15:50 EDT 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2010
#	Virgnia Bioinformatics Institute
#	Virginia Polytechnic Institute and State University
#	Blacksburg, VA 24061-0447
#	USA
####################################################################################################
#	What does this program do?
#
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
use Statistics::Basic qw(:all nofill);

my $UNIQUE_FILE_WIDTH = 20;						# part of name kept and presumed unique
my $LABEL_WIDTH = 25;							# width of filename col in word/ktuple count table
my $COUNT_WIDTH = 10;							# width of data columns in above table
my $MIN_FILE_LENGTH = 10000;					# do not consider files < 10 kb in length

my @t0 = ( time, (times)[0] );					# start execution timer
my %opts	= ();								# init cmdline arg hash

my %usage 	= (									# init paras for getopts
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
	'K' => {
		'type'     => "boolean",
		'usage'    => "suppress calculation and printing of ktuple data",
		'required' => 0,
		'init'     => 0,
	},
	'S' => {
		'type'     => "boolean",
		'usage'    => "print stats on words and ktuples for indicidual files",
		'required' => 0,
		'init'     => 0,
	},
	'V' => {
		'type'     => "boolean",
		'usage'    => "print version information",
		'required' => 0,
		'init'     => 0,
	},
	'U' => {
		'type'     => "integer",
		'usage'    => "truncate file names to this number of characters",
		'required' => 0,
		'init'     => $UNIQUE_FILE_WIDTH,
	},
	'W' => {
		'type'     => "boolean",
		'usage'    => "suppress calculation and printing of word data",
		'required' => 0,
		'init'     => 0,
	},
	'c' => {
		'type'     => "boolean",
		'usage'    => "format output for computer (no space; tabs only)",
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
		'type'     => "string",
		'usage'    => "print output to named file",
		'required' => 0,
		'init'     => '',
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'k' => {
		'type'     => "integer",
		'usage'    => "max ktuple length",
		'required' => 1,
		'init'     => 3,
	},
	'p' => {
		'type'     => "integer",
		'usage'    => "digits past decimal in output matrix",
		'required' => 1,
		'init'     => 5,
	},
	'm' => {
		'type'     => "integer",
		'usage'    => "minimum file size in bytes",
		'required' => 1,
		'init'     => $MIN_FILE_LENGTH,
	},
	'w' => {
		'type'     => "integer",
		'usage'    => "output matrix cell width",
		'required' => 1,
		'init'     => 15,
	},
);

my @infiles = ();
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
		"Start date:	$start_date\n" .
		"End date:	$rel_date\n\n" if $opts{'V'};

my $outfile = $opts{'f'} ;
my $min_file_length = $opts{'m'};
my $unique_file_width	= $opts{'U'};

####################################################################################################
################################## Put Main Between Here ... #######################################

# prepare printf format strings for output

my ( $hsfmt, $hdfmt, $sfmt );
if ( $opts{'c'} ){								# if "computer" output format
	$hsfmt = "%s\t";							# count table string format (label part)
	$hdfmt = $hsfmt;							# count table data format (label part)
	$hsfmt .= ( "%s\t" ) x 4;					# count table heading format
	$hdfmt .= ( "%d\t" ) x 4;					# count table data format
	$hsfmt =~ s/\t$/\n/;						# replace last tab with newline
	$hdfmt =~ s/\t$/\n/;						# replace last tab with newline
	$sfmt = "%s\t";								# printf format string for labels
} else {
	$hsfmt = "%-" . $LABEL_WIDTH . "s\t";		# count table string format (label part)
	$hdfmt = $hsfmt;							# count table data format (label part)
	$hsfmt .= ( "%$COUNT_WIDTH" . "s\t" ) x 4;	# count table heading format
	$hdfmt .= ( "%$COUNT_WIDTH" . "d\t" ) x 4;	# count table data format
	$hsfmt =~ s/\t$/\n/;						# replace last tab with newline
	$hdfmt =~ s/\t$/\n/;						# replace last tab with newline
	$sfmt = "%" . $opts{'w'} . "s\t";			# printf format string for labels
}
# declare key variables for correlation calculation

my $ktuple = $opts{'k'};						# set ktuple length from cmdline parameter
my %ktHash = ();								# hash keyed on filename of counts keyed on ktuples
my %ktcuH = ();									# hash of unique ktuple counts keyed on filenames
my %ktctH = ();									# hash of ktuple counts keyed on filenames
my %wordHash = ();								# hash keyed on filename of counts keyed on words
my %wcuH = ();									# hash of unique word counts keyed on filenames
my %wctH = ();									# hash of word counts keyed on filenames
my %correls = ();								# hash of correlations keyed on "word" or "kt"

# read files from commandline

my @files = ();
foreach my $file ( @ARGV ){
	if ( -B $file ){
		print "File \"$file\" is not a regular file: removing from list.\n";
	} elsif ( -s $file < $min_file_length ){
		print "File \"$file\" is < $min_file_length bytes in length-- too small: removing from list.\n";
	} else {
		push( @files, $file );
	}
}
@ARGV = @files;

# check whether truncated filenames will be unique

my %fnames = ();		# file names
my @snames = ();		# shortened file names
foreach my $file ( @ARGV ){
	my $sfname = $file;
	$sfname =~ s/^(.{$unique_file_width}).*$/$1/;			# truncate file name
	if ( exists $fnames{ $sfname } ){
		print STDERR "filename 1: $file\n";
		print STDERR "filename 2: $fnames{ $sfname }\n";
		print STDERR "            $sfname\n";
		print STDERR "Cannot be distinguished in $unique_file_width characters.\n";
	}
	$fnames{ $sfname } = $file;
	push( @snames, $sfname );
}
if ( @ARGV != (keys %fnames ) ){
	print STDERR "There is a discrepancy between the number of files and keys.\n";
	die( "Cannot continue.\n" );
}
@ARGV = @snames;

# read words/ktuples from files to create normalized count hashes

foreach my $file ( @ARGV ){							# loop over filenames on cmdline
	open( FILE, "$fnames{$file}") or					# open file
		die "Cannot open \"$fnames{$file}\" for reading.\n"; 	# or die trying
	my @farray = ( <FILE> );							# read lines of file into array
	my $fstr = join('', @farray );						# join lines of file into long string
	( $wordHash{$file}, $wctH{$file}, $wcuH{$file} )	# populate hashes keyed on filename for word hash,
		= &count_words( $fstr );						# ktuple total and ktuple unique count
	( $ktHash{$file}, $ktctH{$file}, $ktcuH{$file} )	# populate hashes keyed on filename for ktuple hash,
		= &count_ktuples( $fstr, $ktuple );				# total word count and unique word count
	close( FILE );										# close filehandle
}
# print table of ktuple and word counts (total and unique)

print "\n";
unless ( $opts{'S'} ){
	my $fh = &safeOpen( "tbl", $outfile );
	printf $fh ( $hsfmt, "FileName", "ktCntTot", "ktCntUnq", "WordCntTot", "WordCntUnq" );
	foreach my $file ( @ARGV ){
		printf $fh ( $hdfmt, $file,  $ktctH{$file}, $ktcuH{$file}, $wctH{$file}, $wcuH{$file}, );
	}
	close( $fh ) if ( $fh  != \*STDOUT );
}
# calc matrices of correlations between word/ktuple counts between files

print "\n";
unless ( $opts{'K'} ){
	my @datMatKt = ();
	for( my $i = 0; $i < @ARGV; $i++ ){
		for( my $j = $i+1; $j < @ARGV; $j++ ){
			my $ktCorrel		=	&correl( $ktHash{ $ARGV[$i] }, $ktHash{ $ARGV[$j] } );
			$datMatKt[$i][$j]	=	$ktCorrel;
			push( @{ $correls{'kt'} }, $ktCorrel );
		}
	}
	print "k-tuple frequency correlations\n";
	my $fh = &safeOpen( "kt", $outfile );
	&print_matrix( \@datMatKt, $fh );							# print ktuple correlation matrix
	close( $fh ) if ( $fh != \*STDOUT );
	print "\n";
}
unless ( $opts{'W'} ){
	my @datMatWord = ();
	for( my $i = 0; $i < @ARGV; $i++ ){
		for( my $j = $i+1; $j < @ARGV; $j++ ){
			my $wordCorrel		=	&correl( $wordHash{ $ARGV[$i] }, $wordHash{ $ARGV[$j] } );
			$datMatWord[$i][$j]	=	$wordCorrel;
			push( @{ $correls{'word'} }, $wordCorrel );		# print word correlation matrix
		}
	}
	print "word frequency correlations\n";
	my $fh = &safeOpen( "word", $outfile );
	&print_matrix( \@datMatWord, $fh );
	close( $fh ) if ( $fh != \*STDOUT );
	print "\n";
}

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub count_ktuples {
	my ( $str, $k ) = @_;

	my @ktuples_array = ();
	my %ktuples = ();
	my $ktuple_count_unique = 0;
	my $ktuple_count_total = 0;
	$str =~ s/\W+//g;
	$str =~ s/\d+//g;
	$str =~ s/_//g;
	my $sstr = lc ( $str ) ;
	for( my $i = 0; $i < length( $sstr ); $i++ ){
		$ktuples{ substr( $sstr, $i, $k ) }++;
		$ktuple_count_total++;
	}
	$ktuple_count_unique = ( keys %ktuples );
	return( \%ktuples, $ktuple_count_total, $ktuple_count_unique );
}
####################################################################################################
sub count_words {
	my ( $str ) = @_;

	my %words = ();
	my $sstr = lc ( $str );
	$sstr =~ s/\d+/ /g;
	$sstr =~ s/_/ /g;
	my @str = split( /\W+/, $sstr );
	my $wcu = 0;
	my $wct = 0;
	foreach my $word ( @str ){
		$words{$word}++;
		$wct++;
	}
	$wcu = ( keys %words );
	return( \%words, $wct, $wcu );
}
####################################################################################################
#	(float)correl( \%x, \%y )
#	%x, %y hashes of word counts keyed on words
#	returns Pearson Correlation between the two word-count vectors; word list is from union of the
#	two hash key lists
####################################################################################################
sub correl {
	my ( $hash1, $hash2 ) = @_;

	my @words = ( keys %$hash1, keys %$hash2 );					# intersection of hash1 and 2 keys
	my %words = ();													# keys are non-redundant key list
	foreach my $word ( @words ){
		$words{ $word }++;
	}
	@words = sort keys %words;										# now a non-redundant key list

	my ( @vec1, @vec2 ) = ( (), () );
	foreach my $key ( @words ){									# foreach word
		if ( ! defined $hash1->{$key} ){							# if word not defined for hash1
			$hash1->{ $key } = 0;									# set to zero
		}
		if ( ! defined $hash2->{$key} ){							# if word not defined for hash2
			$hash2->{ $key } = 0;									# set to zero
		}
		if ( $opts{'d'} ){
			print "$key\t$hash1->{ $key }\t$hash2->{ $key }\n";
		}
		push( @vec1, $hash1->{ $key } );							# create vectors based on union
		push( @vec2, $hash2->{ $key } );							# word list
	}
	my $cor = correlation( \@vec1, \@vec2 );
	return( $cor );
}
####################################################################################################
sub print_matrix {
	my ( $dat, $fh ) = @_;

	my ( $fcfmt, $sfmt, $dfmt, $null );
	if ( $opts{'c'} ){
		$fcfmt = "%s\t";
		$sfmt = "%s\t";											# printf format string for labels
		$dfmt = "%f\t";											# printf format string for data
		$null = '';												# value for empty cells in matrix
	} else {
		$fcfmt = "%-20s\t";
		$sfmt = "%" . $opts{'w'} . "s\t";						# printf format string for labels
		$dfmt = "%" . $opts{'w'} . "." . $opts{'p'} . "f\t";	# printf format string for data
		$null = '-';											# value for empty cells in matrix
	}
	printf $fh ( $fcfmt, "" );
	for( my $i = 1; $i < @ARGV; $i++ ){
		printf $fh ( $sfmt, $ARGV[$i] );
	}
	print $fh "\n";

	for( my $i = 0; $i < @ARGV - 1; $i++ ){
		printf $fh ( $fcfmt, $ARGV[$i] );
		for( my $j = 0; $j < $i; $j++ ){
			printf $fh ( $sfmt, "-" );
		}
		for( my $j = $i+1; $j < @ARGV; $j++ ){
			printf $fh ( $dfmt, $dat->[$i][$j] );
		}
		print  $fh "\n";
	}
}
####################################################################################################
sub safeOpen {
	my ( $ext, $outfile ) = @_;

	my ( $fname, $fh );
	if ( $outfile ){
		$fname = "$outfile.$ext";
		open( $fh, ">$fname" ) or die "Cannot open \"$fname\" for writing.\n";
	} else {
		$fh = \*STDOUT;
	}
	return( $fh );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
