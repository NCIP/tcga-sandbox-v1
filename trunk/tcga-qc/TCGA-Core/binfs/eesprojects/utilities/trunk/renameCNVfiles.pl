#! /usr/bin/perl -w
####################################################################################################
our	$pgm_name		= "renameCNVfiles.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Sat Oct 08 16:59:16 EDT 2011";
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
# use List::Uniq ':all';

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
	's' => {
		'type'     => "string",
		'usage'    => "SDRF file",
		'required' => 1,
		'init'     => 0,
	},
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
);

my @infiles = qw( datafiles );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
my $sdrf_file = $opts{'s'};							# filename with IDs

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $sdrf = &read_table_in2_LoH_fromfile( $sdrf_file );

my $dir_key = 'Comment [TCGA Archive Name]';
my $fname_key = 'Derived Array Data File';
my $barcode_key = 'Sample Name';

my @filelist = ();
my %file2barcode = ();
foreach ( @$sdrf ){
	my $fname = "$_->{$dir_key}/$_->{$fname_key}";
	push( @filelist, $fname );
	$file2barcode{ $fname } =  $_->{$barcode_key};
}

@filelist = uniq( @filelist );

print "files: " . join( "\n", @filelist ) . "\n" if $opts{'v'};

foreach my $file ( @filelist ){
	if ( ! -f $file ){
		warn "File: \"$file\" not found.\n" if $opts{'v'};
		next;
	} else {
		print "FILE FOUND: $file\n" if $opts{'v'};
	}
	open( FILE, "$file" ) or die "Cannot open data file: \"$file\" for reading.\n";

	my $key_line;
	$key_line = <FILE>;
	chomp $key_line;
	my @key = split( /\t/, $key_line );

	my $val_line = <FILE>;
	chomp $val_line;
	my @val = split( /\t/, $val_line );

	#print "keys: " . join( "\", \"", @key ) . "\"\n";
	#print "vals: " . join( "\", \"", @val );
	close( FILE );
	if( @key != @val ){
		die "ERROR: Number of keys and values mismatched in file:\n$file\nExiting.\n";
	}
	my %dat = ();
	for(my $i = 0; $i < @key; $i++ ){
		$dat{ $key[$i] } = $val[$i];
	}
	my $dir = $file;
	$dir =~ s/\/.+$//;
#	print $dir . "\t" . $file2barcode{ $file } . "\t" . $dat{"BARCODE"} . "\n";
	my $cmd = 	"cp $file $dir/$dat{'BARCODE'}";
	print "$cmd\n";
	 `$cmd`;

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
