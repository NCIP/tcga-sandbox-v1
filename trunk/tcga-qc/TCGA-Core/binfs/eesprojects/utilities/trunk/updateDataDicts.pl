#! /bin/perl -w
# $Id: updateDataDicts.pl 17129 2012-06-15 05:16:38Z snyderee $
# $Revision: 17129 $
# $Date
####################################################################################################
our	$pgm_name		= "updateDataDicts.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Thu Apr 26 22:42:06 EDT 2012";
our	$rel_date		= '$Date: 2012-06-15 01:16:38 -0400 (Fri, 15 Jun 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

updateDataDicts.pl

=head1 SYNOPSIS

updateDataDicts.pl [-options] masterDD.txt *_dd.txt

=head1 USAGE

I<$pgm_name> ....

=head2 Master Data Dictionary File Format
schematag   VARNAME VARDESC TYPE    VALUES
clinical/pharmaceutical bcr_drug_barcode    null    string
clinical/pharmaceutical bcr_drug_uuid   null    string
clinical/luad/shared    eastern_cancer_oncology_group   the ECOG functional performance status of the patient/participant.(public CDE id 88)    string      0   1   2   3   4   5

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

my %STDHEAD = 	( 	std_dd 		=>	[	qw( VARNAME	VARDESC	TYPE VALUES )],
					stdu_dd		=>	[	qw( VARNAME VARDESC UNITS TYPE VALUES ) ],
				);
my %SCHEMA	=	(	clinical		=>	'subj',
					biospecimen		=>	'samp',
					radiation		=>	'rad',
					pharmaceutical	=>	'drugs',
					'_'				=>	'/',
				);
####################################################################################################
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7
####################################################################################################

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my ( $mDD, $mDDL ) = &read_mDD( shift @ARGV );
foreach my $file ( @ARGV ){
	my $ddL = &read_table_in2_LoH_fromRRfile( $file );
	link( $file, "$file.bak" );
	unlink( $file );
	&update_dd_file( $file, $ddL, $mDD, $mDDL );
}



##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_mDD{
	my ( $masterDDfile ) = @_;

	my $LoH = &read_table_in2_LoH_fromRRfile( $masterDDfile );
	&translate_schematag( $LoH );
	my %HoL = ();
	foreach ( @$LoH ){
		if( exists $HoL{ $_->{'VARNAME'} } ){
			push( @{$HoL{ $_->{'VARNAME'} }}, $_ );
		} else {
			$HoL{ $_->{'VARNAME'} } = ();
			push( @{$HoL{ $_->{'VARNAME'} }}, $_ );
		}
	}
	return( \%HoL, $LoH );
}

####################################################################################################
sub update_dd_file{
	my ( $file, $ddL, $mDD, $mDDL ) = @_;

	open( FILE, ">$file" ) or die "Cannot open file: \"$file\" for writing.\n";
	my @headings = keys $ddL->[0];
	@headings = &match_headings( [keys $ddL->[0]], $file );

	print FILE join( "\t", @headings ) . "\n";
	foreach my $hash ( @{ $ddL } ){
		print "\n";
		if ( exists $mDD->{ $hash->{'VARNAME'} } ){
			$hash = &find_mDD_replacement( $hash->{'VARNAME'}, $mDD->{ $hash->{'VARNAME'} }, $file );
		} else {
			print "No data dictionary entry for VARNAME: \"" . $hash->{'VARNAME'} . "\".\n";
		}
		my @line = ();
		foreach my $key ( @headings ){
			my $field;
			if ( $hash->{$key} =~ m/ARRAY\(0x[0-9a-f]+\)/ ){
				print "hash->{$key}: " . $hash->{$key} . "\t" . join("\t", @{$hash->{$key}} ). "\n";
				$field = join( "\t", @{ $hash->{ $key } } );
			}else{
				print "hash->{$key}: " . $hash->{$key} . "\n";
				$field = $hash->{ $key };
			}
			push( @line, $field );
		}
		print FILE join( "\t", @line ) . "\n";
	}

	close( FILE );
}
####################################################################################################
sub match_headings {
	my ( $list, $file ) = @_;

	my $equiv_flag = 0;
	foreach my $key ( keys %STDHEAD ){
		my $reflist = $STDHEAD{ $key };
		my $lc = List::Compare->new( $list, $reflist );
		if ( $lc->is_LequivalentR ){
			$equiv_flag++;
			return( @$reflist );
		}
	}
	unless ( $equiv_flag ){
		my $bad_headings = join( ", ", @$list );
		die "Headings of file: \"$file\" are unrecognized:\n$bad_headings\n";
	}
}
####################################################################################################
sub find_mDD_replacement {
	my ( $varname, $mDDL, $file ) = @_;

	if ( @$mDDL == 1 ){										# if there is only one choice, use it
		return( $mDDL->[0] );
	}

	my $study = "";
	my $description = "";
	if ( $file =~ m/^tcga_(\w+)_([A-Z]+)_dd\.txt$/ ){
		$description = $1;
		$study = lc( $2 );
	} else {
		die "Cannot parse filename: \"$file\", for study name or description.\n";
	}
	my @file_vector = ( split( /_/, $description ), $study );
#	my $top_hash = $mDDL->[0];
	my $top_hash;
	my $top_count = 0;
	my @top_vector = "";
	foreach my $hash ( @$mDDL ){
		my @schema_vector = split( /\//, $hash->{'schematag'} );
		my $lc = List::Compare->new( \@file_vector, \@schema_vector );
		my @intersect = $lc->get_intersection;
		if ( @intersect >= $top_count ){
			$top_hash = $hash;
			$top_count = @intersect;
			@top_vector = @schema_vector;
		}
	}
	print "top_count     = $top_count\n";
	print "file_vector   = ( " . join( ", ", @file_vector ) . ")\n";
	print "schema_vector = ( " . join( ", ", @top_vector ) . ")\n";
	print "\n";

	return( $top_hash );

}
####################################################################################################
sub translate_schematag {
	my ( $LoH ) = @_;

	foreach my $hash ( @$LoH ){
		foreach my $key ( keys %SCHEMA ){
			$hash->{'schematag'} =~ s/$key/$SCHEMA{$key}/;
		}
	}
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
