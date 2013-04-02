#! /usr/bin/perl -w
# $Id: plrRows.pl 17880 2012-11-16 02:20:26Z snyderee $
####################################################################################################
our $pgm_name       = "plrRows.pl";
our $VERSION        = "v1.0.0 (dev)";
our	$start_date		= "Mon Oct 22 14:16:11 EDT 2012";
our $rel_date       = "";
####################################################################################################
use strict;
use warnings;
use Getopt::Std;
our( $opt_C, $opt_F, $opt_d, $opt_f, $opt_j, $opt_h, $opt_n, $opt_w );
getopts('CF:c:df:jnw:');

$0 =~ s/^.+\///;											# remove leading path from program name
if( $opt_h or !scalar( @ARGV ) ){
	die "\nusage:\n\t$0 [-f <int_list>] [-w <col_width>] [-F<field separator>] [-ndjh] filename\n\n";
}

# get the parameters and data for the table
my $fs = '	';
$fs = $opt_F if $opt_F;
my ( $range_list ) = &calc_range_list( $opt_f );				# list of lines to print

foreach my $fname ( @ARGV ){
	my $output = &get_output( $opt_f, $fname, $range_list);
	print join( "\n", @$output ) . "\n";
}


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
