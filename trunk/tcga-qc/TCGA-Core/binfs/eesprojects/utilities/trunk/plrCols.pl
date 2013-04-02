#! /usr/bin/perl -w
# $Id: plrCols.pl 17937 2012-12-13 17:23:37Z snyderee $
####################################################################################################
our $VERSION = "1.0.3";
#	Mon Nov 21 14:10:30 EST 2011
#	Eric E. Snyder (eric.snyder@nih.gov)
####################################################################################################
=head1 NAME

plrCols.pl $Revision: 17937 $

=head1 SYNOPSIS

plrCols.pl [-options: CF:N:c:df:jnw:] input_matrix

=head1 OPTIONS

 -C		filter comments and blank lines
 -F <fsep>	input field separator (default: <tab>)
 -N <string>	substitute argument for null fields, e.g. -N null, -N""
 -d		not used
 -f <string>	input record list, e.g. "1-5,7,9,21-24" (default: "1-4")
 -j		right justify all columns
 -h		print help information
 -n		number output lines
 -w <int>	output column width (default: 50)

=head1 DESCRIPTION

I<plrCols.pl> transposes a table/matrix, printing selected input rows in fixed-width columns,
optionally filtering comment and blank lines.

=cut
####################################################################################################
use strict;
use warnings;
use Pod::Usage;
use Getopt::Std;

our( 	$opt_C,		# filter comments and blank lines
		$opt_F,		# input field separator
		$opt_N,		# substitute argument for null fields, e.g. -N null, -N
		$opt_O,		# output field separator
		$opt_d,		# not used
		$opt_f,		# input record list
		$opt_j, 	# right justify all columns
		$opt_h,		# print help information
		$opt_n,		# number output lines
		$opt_w,		# output column width
	);
$opt_w = 50;												# default field width
$opt_f = "1-4";												# default fields
getopts('CF:N:O:c:df:hjnw:');
$opt_C = !$opt_C;											# toggle $opt_c; filtering of comments and blank lines

$0 =~ s/^.+\///;											# remove leading path from program name
if( $opt_h or !scalar( @ARGV ) ){
	die pod2usage(	-verbose	=> 2,
					-output		=> \*STDOUT,
					-exitval	=>	1
				);
}

# get the parameters and data for the table
my $fs = '	';
$fs = $opt_F if $opt_F;
my $ofs = "";
$ofs = $opt_O if $opt_O;

my ( $range_list ) = &calc_range_list( $opt_f );				# list of lines to print

# my $infile = shift @ARGV;
my $output = &get_output( $opt_f, $ARGV[0], $range_list);

my $col_width = 0;
$col_width = $opt_w if $opt_w;
my $cols = scalar( @$output );

# populate the table

my @table = ();									# table[row][column]
my $nfields = 0;
for( my $i = 0; $i < @$output; $i++ ){
	my @row = split( /$fs/, $output->[$i], -1 );
	if ( $i ){
		if ( @row != $nfields ){
			my $mfields = @row;
			die "Number of fields in row $i ($mfields) is not equal to the number of fields in the previous row ($nfields);\n".
				"All rows must have the same number of fields.\n";
		}
	}
	$nfields = @row;
	for( my $j = 0; $j < @row; $j++ ){
		$table[$i][$j] = $row[$j];
	}
}

# print out the table with indices switched

my $table_out = "";
for( my $i = 0; $i < $nfields; $i++ ){
	$table_out .= sprintf("%03d\t", $i+1 ) if $opt_n;					# print first column of line numbers
	for( my $j = 0; $j < @$output; $j++ ){
		my $fld = $table[$j][$i];
		my $len = 0;
		if ( $fld ){
			$len = length( $fld );
		} else {														# field is null or undef-- print alternative value for null fields
			if( $opt_N ){												# if it exists
				$fld = $opt_N;
			} else {													# if it doesn't
				$fld = "";												# set value to null
			}
		}
		if( $col_width == 0 ){
			# don't mess with $fld, no padding or truncation
		}elsif ( $len > $col_width ){
			$fld = substr( $fld, 0, $col_width );
		} else {
			if ( $opt_j ){
				$fld = sprintf("%$col_width"."s", $fld );
			} else {
				$fld = sprintf("%-$col_width"."s", $fld );
			}
		}
		$table_out .= $ofs if( $j );
		$table_out .= $fld;
	}
	$table_out .= "\n";
}
print $table_out;


####################################################################################################
#	calc_range_list
####################################################################################################
sub calc_range_list {
	my ( $line_string ) = @_;

	return( [()] ) unless $line_string;									# return a NULL array ref if line_string MT
	if ( $line_string =~ m/[^0-9,-]/ ){
		die "$0: Line description string contains disallowed characters.\n";
	}

	my @ranges = split( ",", $line_string );
	my @range_list;
	foreach ( @ranges ){
		my @int;
		if ( m/^(\d+)-(\d+)$/ ){
			push( @int, $1 );
			push( @int, $2 );
		} elsif ( m/^(\d+)$/ ){
			push( @int, $1 );
		} else {
			die "$0: malformed range expression\n";
		}
		push( @range_list, \@int );
	}
	return( \@range_list );
}
####################################################################################################
# print lines as specified by @range_list
####################################################################################################
sub get_output {
	my ( $line_string, $fname, $range_list ) = @_;

	my @output = ();
	open( FILE, $fname ) or die "Cannot open file: \"$_\" for reading.\n";
	my @file;
	while( <FILE> ){
		chomp;
		if ( $opt_C ){
			next if /^#/;								# skip comments
			next if /^\s*$/;							# and blank lines
			s/\cM//g;									# remove Control-M
		}
		push( @file, $_ );
	}
	if ( @$range_list ){
		&validate_range_list( scalar( @file ) , $line_string );
	} else {
		$range_list = [ [ (1, scalar( @file ) )] ];
	}
	foreach my $int ( @$range_list ){
		if ( @$int == 1 ){
			push( @output, $file[ $int->[0] - 1 ] );
		} elsif ( @$int == 2 ){
			if ( $int->[0] <= $int->[1] ){
				for ( my $i = $int->[0]; $i <= $int->[1]; $i++ ){
					push( @output, $file[ $i - 1 ] );
				}
			} elsif( $int->[0] > $int->[1] ){
				for ( my $i = $int->[0]; $i >= $int->[1]; $i-- ){
					push( @output, $file[ $i - 1 ] );
				}
			} else {
				die "$0:  there is something strange about: \"$int->[0]\" and \"$int->[1]\"\n";
			}
		} else {
			die "$0: problem with range list data structure\n";
		}
	}
	close( FILE );
	return( \@output );
}
####################################################################################################
sub validate_range_list {
	my ( $max_lines, $range_string ) = @_;

	my @line_numbers = split( /[,-]/, $range_string );
	foreach ( @line_numbers ){
		if ( $_ > $max_lines ){
			die "$0: range parameter(s) greater than number of lines in file\n";
		}
	}
}
