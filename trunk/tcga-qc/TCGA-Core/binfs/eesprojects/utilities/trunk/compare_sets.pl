#! /usr/bin/perl -w
# $Id: compare_sets.pl 18049 2013-01-24 16:53:56Z snyderee $
####################################################################################################
my $revision = '$Revision: 18049 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "compare_sets.pl";
our	$VERSION		= "v1.0.1 (dev)";
our	$start_date		= "Mon Dec 31 19:58:56 EST 2012";
our $rel_date 		= '$Date: 2013-01-24 11:53:56 -0500 (Thu, 24 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

compare_sets.pl

=head1 SYNOPSIS

compare_sets.pl [-options] file1 file2

=head1 OPTIONS

 -H           ignore heading row
 -c           print columns from input files not used for set comparison
 -f <string>  field(s) of files to be used for set comparison (-fn => n for both; -fn,m => n for 1st, m for 2nd)
 -h           print "help" information
 -n           create NO output files
 -o           print output on one line
 -r <string>  root for output file names
 -s <string>  input/output record separator
 -v           print verbose execution information
 -d           print debugging information

=head1 DESCRIPTION

I<compare_sets.pl> ....

=cut
####################################################################################################
#	Testbed:
#	Cmdline:
####################################################################################################
#	History:
#	v1.0.1:	Add ability to print columns not involved in comparison in output.
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
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
	'H' => {
		'type'     => "boolean",
		'usage'    => "ignore heading row",
		'required' => 0,
		'init'     => 0,
	},
	'V' => {
		'type'     => "boolean",
		'usage'    => "print version information",
		'required' => 0,
		'init'     => 0,
	},
	'c' => {
		'type'     => "boolean",
		'usage'    => "print columns from input files not used for set comparison",
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
		'usage'    => "field(s) of files to be used for set comparison (-fn => n for both; -fn,m => n for 1st, m for 2nd)",
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
		'type'     => "boolean",
		'usage'    => "create NO output files",
		'required' => 0,
		'init'     => 0,
	},
	'o' => {
		'type'     => "boolean",
		'usage'    => "print output on one line",
		'required' => 0,
		'init'     => 0,
	},
	'r' => {
		'type'     => "string",
		'usage'    => "root file name",
		'required' => 0,
		'init'     => "",
	},
	's' => {
		'type'     => "string",
		'usage'    => "field separator",
		'required' => 1,
		'init'     => "\t",
	},
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
);

my @infiles = qw( file1 file2 );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
my $fsep = $opts{'s'} if $opts{'s'};
print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $froot = '';
if ( $opts{'r'} ){
	$froot = $opts{'r'} . ".";
}
my @fields = ();
if ( $opts{'f'} ){
	if ( $opts{'f'} =~ m/,/ ){
		@fields = split( /,/, $opts{'f'} );
		if ( @fields > 2 ){
			warn "WARNING: using fields $fields[0] and $fields[1], ignoring remainder.\n";
		}
	} else {
		@fields = ( $opts{'f'}, $opts{'f'} );
	}
	for( my $i = 0; $i < @fields; $i++ ){
		$fields[$i]--;
	}
} else {
	@fields = ( 0, 0 );
}
my @fnames = @ARGV;
my $heading = "";
my %file = ();
my %rows = ();
for( my $i = 0; $i < 2; $i++ ){
	( $file{ $fnames[$i] }, $rows{ $fnames[$i] } ) = &read_file( $fnames[$i], $fields[$i] );
}

my $lc = List::Compare->new( $file{$fnames[0]}, $file{$fnames[1]} );

my %outfile = ();
$outfile{	$fnames[0]				}	= $file{$fnames[0]};
$outfile{	$fnames[1]				}	= $file{$fnames[1]};
$outfile{	$froot . 'missing'		}	= $lc->get_Lonly_ref;
$outfile{	$froot . 'new'			} 	= $lc->get_Ronly_ref;
$outfile{	$froot . 'intersect'	} 	= $lc->get_intersection_ref;

my @output_order = ( @fnames, $froot . 'missing', $froot . 'intersect', $froot . 'new' );
foreach my $fname ( @output_order ){
	my 	$item_count = @{$outfile{$fname}};
	printf("%7d\t%s", $item_count, $fname );
	print "\t\tunique to $fnames[0]" if ( $opts{'v'} && $fname =~ m/\.missing$/ );
	print "\t\tunique to $fnames[1]" if ( $opts{'v'} && $fname =~ m/\.new$/ );
	print "\n" unless $opts{'o'};
	unless ( grep ( /^$fname$/, @fnames ) ){
		my @row_hashes;
		@row_hashes = ( $rows{ $fnames[0] } ) if $fname =~ m/missing/;
		@row_hashes = ( $rows{ $fnames[1] } ) if $fname =~ m/new/;
		@row_hashes = ( $rows{ $fnames[0] }, $rows{ $fnames[1] } ) if $fname =~ m/intersect/;

		&create_file( $outfile{$fname}, $fname, \@row_hashes ) unless $opts{'n'};
	}
}
#################################      ... and Here         ########################################
####################################################################################################
print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;
####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub create_file {
	my ( $file_ptr, $fname, $rowH ) = @_;

	open( FILE, ">$fname" ) or die "Cannot open file: \"$fname\" for writing.\n";
	if ( @$file_ptr ){
		if ( $opts{'c'} ){
			my $output = "";
			foreach my $item ( @$file_ptr ){
				foreach my $cols ( @$rowH ){
					$output .= $cols->{$item} . "\t";
				}
				chop $output;
				$output .= "\n";
			}
			print FILE $output;
		} else {
			print FILE join( "\n", @{$file_ptr} ) . "\n";
		}
	}
	close( FILE );
	return(1);
}
####################################################################################################
sub read_file {
	my ( $fname, $field ) = @_;

	my $heading = "";
	my @data = ();				# contains records for set comparison and keys to the %row hash
	my %row = ();				# complete unparsed data row keyed on value of column used for comparison
	open( FILE, "$fname" ) or die "Cannot open file: \"$fname\" for reading.\n";
	while( <FILE> ){
		chomp;
		next if /^\s*$/;
		next if /^#/;
		my @F = split( /$fsep/, $_ );
		push( @data, $F[$field] );
		if ( exists $row{ $F[$field]} ){
			warn "redundant key: \"$F[$field]\", data for the following row will be lost:\n$_\n";
		} else {
			$row{ $F[$field] } = $_;
		}
	}
	close( FILE );
	if ( $opts{'H'} ){
		$heading = shift( @data );
	}
	return( \@data, \%row );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
