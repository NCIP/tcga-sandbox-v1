#! /usr/bin/perl -w
# $Id: removeColsWithNullData.pl 18047 2013-01-24 16:48:55Z snyderee $
# $Revision: 18047 $
# $Date
####################################################################################################
my $revision = '$Revision: 18047 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "removeColsWithNullData.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Thu Sep 13 13:14:11 EDT 2012";
our	$rel_date		= '$Date: 2013-01-24 11:48:55 -0500 (Thu, 24 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
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

I<$pgm_name> Take a list of files, examine contents of columns and remove columns that are all "null"
or a user-specified string.

=cut
####################################################################################################
#	Testbed:	/home/eesnyder/projects/nih/CRC/subPrep/jw_crc_clinical/scratch
#	Cmdline:	tcga_samp_CRC.txt
####################################################################################################
#	History:
#	v1.0.0:
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
	'f' => {
		'type'     => "string",
		'usage'    => "input/output field separator",
		'required' => 1,
		'init'     => "\t",
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'n' => {
		'type'     => "string",
		'usage'    => "definition of null string (e.g., \"null\" or \"NULL\")",
		'required' => 1,
		'init'     => "null",
	},
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
);

####################################################################################################
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7
####################################################################################################

#my $nr_headings = [ qw( a260_a280_ratio amount analyte_type analyte_type_id bcr_aliquot_barcode
#						bcr_analyte_barcode biospecimen_barcode_bottom center_id concentration day_of_shipment
#						experimental_protocol_type gel_image_file month_of_shipment normal_tumor_genotype_match
#						pcr_amplification_successful plate_column plate_id plate_row ratio_28s_18s rinvalue
#						sample_type source_center spectrophotometer_method year_of_shipment ) ];
#
#
#$nr_headings = [ qw(	bcr_analyte_barcode bcr_aliquot_barcode sample_type sample_type_id amount
#						center_id concentration day_of_shipment month_of_shipment
#						plate_column plate_id plate_row source_center year_of_shipment a260_a280_ratio amount
#						analyte_type analyte_type_id concentration gel_image_file spectrophotometer_method
#						normal_tumor_genotype_match pcr_amplification_successful experimental_protocol_type
#						ratio_28s_18s rinvalue ) ];

my @infiles = qw( data_file );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $nullStr = $opts{'n'};
my $fsep = $opts{'f'};
foreach my $fname ( @ARGV ){
	my ( $data, $headings ) = &read_table_in2_LoH_fromfile( $fname );
	my ( $nn_headings ) = &remove_null_columns( $data, $headings, $nullStr ); 			# non-null data & headings
	my ( $nr_data, $nr_headings ) = &remove_redundant_columns( $data, $nn_headings, $nullStr );	# non-redundant data & headings
	&print_LoH_as_table( $nr_data, "$fname.clean", $fsep, $nr_headings ); # $nr_headings );
}

##################################      ... and Here         #######################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
####################################################################################################
#	remove_redundant_columns()
####################################################################################################
sub remove_redundant_columns {
	my ( $dat, $heads, $null ) = @_;

	my @nrHeadings = ();
	my @rHeadings = ();
	my $i = 2;
	for( my $j = 0; $j < @$heads; $j++ ){
		my $candidate = $heads->[$j] . "_[$i]";
		for( my $k = $j; $k < @$heads; $k++ ){
			if( $candidate eq $heads->[$k] ){
				print "MATCHING:  " . $heads->[$j] . " vs. " . $heads->[$k] . "\n";
				if ( &count_mismatched_columns( $dat, $heads->[$j], $heads->[$k] ) ){
				} else {
					print $heads->[$j] . " and " . $heads->[$k] . " are identical\n";
					push( @rHeadings, $heads->[$k] );
				}
			}
		}
	}
	&remove_columns_from_LoH( $dat, \@rHeadings );

	my $j = 0;
	for( my $i = 0; $i < @$heads; $i++ ){
		if ( $heads->[$i] eq $rHeadings[$j] ){
			$j++;
		} else {
			push( @nrHeadings, $heads->[$i] );
		}
	}

	#my $lc = List::Compare->new('--unsorted', \@rHeadings, $heads );
	#my @nrh = $lc->get_complement;

	#foreach my $h ( @$heads ){
	#	my $r = $h;
	#	$r .= "_[2]";
	#	if( grep( /^$r$/, @nrh ) ){
	#		print "redundant: $h\n";
	#	} else {
	#		push( @nrHeadings, $h );
	#	}
	#}

	print "NR-headings: " . join( "\n", @nrHeadings ) . "\n";
	print "R-headings: " . join( "\n", @rHeadings ) . "\n";

	return( $dat, \@nrHeadings );
}
####################################################################################################
#	remove_columns_from_LoH()
####################################################################################################
sub remove_columns_from_LoH {
	my ( $dat, $heads ) = @_;

	foreach my $hash ( @$dat ){
		foreach my $key ( @$heads ){
			if( exists $hash->{$key} ){
				delete $hash->{$key};
			}
		}
	}
	foreach my $key ( @$heads ){
		print "key: $key removed\n";
	}
}
####################################################################################################
#	count_mismatched_columns()
####################################################################################################
sub count_mismatched_columns {
	my ( $dat, $h1, $h2 ) = @_;

	my $mismatches = 0;
	foreach ( @$dat ){
		if( $_->{$h1} ne $_->{$h2} ){
			$mismatches++;
		}
	}
	return( $mismatches );
}
####################################################################################################
#	remove_null_columns()
####################################################################################################
sub remove_null_columns {
	my ( $dat, $heads, $null_string ) = @_;

	my @non_null_headings = ();
	my @null_headings = ();
	my $i = 0;
	foreach my $h ( @$heads ){
		$i++;
		my $counter = 0;								# counter for non-null fields
		unless ( defined $dat->[0]{$h} ){
			print "Heading \"$h\" contains no data.\n";
			next;
		}
		foreach my $d ( @$dat ){
			$counter++ if ( $d->{$h} ne $null_string );
		}
		if ( $counter ){
			push( @non_null_headings, $h );
		} else {
			print "Heading[$i]:\t\"$h\" contains only null data; removing.\n";
			push( @null_headings, $h );
		}
	}
	&extract_named_cols_from_LoH( $dat, \@null_headings );
	return( \@non_null_headings );
}
####################################################################################################
sub extract_named_cols_from_LoH {
	my ( $dat, $null_headings ) = @_;

	my @newdat = ();
	foreach my $d ( @$dat ){
		foreach my $h ( @$null_headings ){
			delete $d->{$h};
		}
	}
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
