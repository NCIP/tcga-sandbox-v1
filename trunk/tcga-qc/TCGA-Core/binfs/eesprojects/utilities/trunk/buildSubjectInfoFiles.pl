#! /usr/bin/perl -w
# $Id: buildSubjectInfoFiles.pl 17880 2012-11-16 02:20:26Z snyderee $
# $Revision: 17880 $
# $Date
####################################################################################################
our	$pgm_name		= "buildSubjInfoFiles.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Tue May 29 15:03:36 EDT 2012";
our	$rel_date		= '$Date: 2012-11-15 21:20:26 -0500 (Thu, 15 Nov 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

buildSubjectInfoFiles.pl

=head1 SYNOPSIS

buildSubjectInfoFiles.pl subject_list variable_list biotabfiles

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
use TCGA;

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
	'K' => {
		'type'     => "boolean",
		'usage'    => "print info on duplicate keys in data files",
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

####################################################################################################
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7
####################################################################################################
my $idkey = "barcode";
my $DUPLICATE_KEY_FILE = "duplicateKeys.new";
my @infiles = qw( subjects variables *.txt );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $subj_fname = shift @ARGV;
my $var_fname = shift @ARGV;
my @biotab_fnames = @ARGV;

my ( $subjL, $subjH ) = &read_subject_list( $subj_fname );
my ( $refVarL ) = &read_list_in2_L_fromfile( $var_fname, 0 );
my ( $ddFiles, $dsFiles ) = &sort_biotab_filenames( \@biotab_fnames );
my ( $f2subjL, $f2biotabH, $f2sampL ) = &read_biotabs( $dsFiles );
my ( $f2vars, $f2dd  ) = &read_dataDicts( $ddFiles );
my ( $myf2vars ) = &find_vars_in_dataDicts( $refVarL, $f2vars );
my ( $Mdd ) = &makeMasterDD( $myf2vars, $f2dd );
my ( $Mds ) = &makeMasterDS( $myf2vars, $f2sampL );

print "\n";

# my ( $ddFiles2vars ) = &files2vars( $myf2vars );



####################################################################################################
#	$f2vars =	(	'tcga_subj_info_GBM_dd.txt'		=>	[	'bcr_patient_barcode',
#															'chemo_therapy',
#															'days_to_birth',
#															...
#														],
#					'tcga_subj_drugs_GBM_dd.txt'	=>	[	'bcr_patient_barcode',
#															'regimen_indication',
#														],
#					...
#				);
####################################################################################################qw
#	$f2dd =	(	'tcga_samp_GBM_dd.txt'	=>	{	'a260_a280_ratio'		=>	{	'VARNAME'	=>	'a260_a280_ratio',
#																				'VARDESC'	=>	'The ratio of UV absorbance at 260nm...',
#																				'TYPE'		=>	'string',
#																				'VALUES'	=>	[],
#																			},
#												'amount'				=>	{	'VARNAME'	=>	'amount',
#																				'VARDESC'	=>	'The total amount in micrograms of...',
#																				'TYPE'		=>	'string',
#																				'VALUES'	=>	[],
#												...
#											},
#				'tcga_slide_GBM_dd.txt'	=>	{	'bcr_aliquot_barcode'	=>	{	'VARNAME'	=>	...,
#																				'VARDESC'	=>	'Aliquot barcode identifier',
#																				...,
#																			},
#											},
#				...,
#			);
####################################################################################################
##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub makeMasterDS {
	my ( $f2vars, $f2ds ) = @_;

	my @LoH = ();
	my @ddfnames = ( keys %$f2vars );		#remove nulls, should it use non-dd names
	foreach my $file ( @ddfnames ){
		foreach my $var ( @{ $f2vars->{ $file }} ){
			push( @LoH, $f2ds->{$file}{$var} ) ;
		}
	}

	return( \@LoH );
}
####################################################################################################
#	makeMasterDD()
####################################################################################################
sub makeMasterDD {
	my ( $f2vars, $f2dd ) = @_;

	my @LoH = ();
	my @ddHeads = @dataDictionaryHeadings;					# from TCGA.pm
	my @ddfnames = ( keys %$f2vars );
	foreach my $file ( @ddfnames ){
		foreach my $var ( @{$f2vars->{$file}} ){
			push( @LoH, $f2dd->{$file}{$var} ) ;
		}
	}
	my $studyName = $ddfnames[0];
	$studyName =~ s/^.+_([A-Z]+)_dd\.txt$/$1/;
	my $outfile = "tcga_subj_info_$studyName\_dd.new";		# hack a name for an output file so input isn't over written
	my $nHeads = @ddHeads;
	my %varCount = ();
	open( FILE, ">$outfile" ) or die "Cannot open file: \"$outfile\" for writing.\n";
	print FILE join( "\t", @ddHeads ) . "\n";
	foreach ( @LoH ){
		if( exists $varCount{ $_->{$ddHeads[0]} } ){
			# do nothing
		} else {
			$varCount{ $_->{$ddHeads[0]} } = 1;				# mark the counter
			for( my $i = 0; $i < $nHeads - 1; $i++ ){
				print FILE $_->{$ddHeads[$i]} . "\t";
			}
			if ( exists $_->{$ddHeads[$nHeads - 1]} ){
				print FILE join( "\t", @{$_->{$ddHeads[$nHeads - 1]}});
			}
			print FILE "\n";
		}
	}
	return( \@LoH );
}
####################################################################################################
#	files2vars()
#	Abandoned
####################################################################################################
sub files2vars {
	my ( $data ) = @_;

	my %dataH = ();
	foreach my $file ( keys %$data ){
		if ( defined $data->{$file} ){
			print "$file : " . join( ", ", @{ $data->{$file} } ) . "\n\n";
		}
		foreach my $var ( @{ $data->{$file}} ){
			if ( exists $dataH{$var} ){
				$dataH{ $var }++;
			} else {
				$dataH{ $var } = 1;
			}
		}
	}
	foreach my $var ( sort keys %dataH ){
		printf("%2d\t%s\n", $dataH{ $var }, $var );
	}
	return( \%dataH );
}
####################################################################################################
#	sort_biotab_filenames()
#	Make & return separate lists for dd and ds files.
####################################################################################################
sub sort_biotab_filenames {
	my ( $fnames ) = @_;

	my @ddFiles = ();
	my @dsFiles = ();

	foreach ( @$fnames ){
		if( m/_dd\.txt$/ ){
			push( @ddFiles, $_ );
		} else {
			push( @dsFiles, $_ );
		}
	}
	return( \@ddFiles, \@dsFiles );
}
####################################################################################################
sub find_vars_in_dataDicts {
	my ( $refVars, $f2vars ) = @_;

	my %myf2vars = ();
	foreach my $file ( keys %$f2vars ){
		my %varsFound = ();
		foreach my $ddVar ( @{$f2vars->{$file}} ){
			my $varname = '';
			( $varname ) = grep( /^$ddVar$/, @$refVars );
			if ( $varname ){
				$varsFound{ $varname } = 1;
			}
			$varsFound{ $ddVar } = 1 if ( $ddVar =~ m/barcode$/ );
		}
		my @myVars = ( keys %varsFound );
		if ( @myVars ){
			$myf2vars{ $file } = \@myVars;
		}
	}
	return( \%myf2vars );
}
####################################################################################################
sub read_dataDicts {
	my ( $fnames ) = @_;

	my %varnameHash = ();
	my %ddHash = ();
	foreach my $fname ( @$fnames ){										# foreach DS filename
		my $LoH = &read_table_in2_LoH_fromRRfile( $fname );
		my @vars = ();
		my $foundIt = 0;
		foreach ( @$LoH ){												# loop through hashes
			if ( exists $_->{'VARNAME'} ){
				push( @vars, $_->{'VARNAME'} );
				$foundIt++;
			}
		}
		if ( $foundIt ){
			$varnameHash{ $fname } = \@vars;
	#		$ddHash{ $fname } = $LoH;
			$ddHash{ $fname } = &LoH2HoH( $LoH, 'VARNAME' );
		} else {
			warn "VARNAME not found in data dictionary file: \"$fname\"\n";
		}
	}
	return( \%varnameHash, \%ddHash );
}
####################################################################################################
sub LoH2HoH {
	my ( $LoH, $key ) = @_;

	my %HoH = ();
	open( DUP, ">$DUPLICATE_KEY_FILE" ) or die "Cannot open file: \"$DUPLICATE_KEY_FILE\" for writing.\n";
	foreach ( @$LoH ){
		if ( exists $_->{ $key } ){
			if ( exists $HoH{ $_->{ $key } } ){
				print DUP "Duplicate key: \"" . $_->{$key} . "\"\n" .
						"	old value: \"" . join( ", ", values %{$HoH{ $_->{$key} } } ). "\"\n" .
						"	new value: \"" . join(", ", values %{$_} ) . "\"\n";
			} else {
				$HoH{ $_->{ $key } } = $_;
			}
		}
	}
	close( DUP );
	return( \%HoH );
}
####################################################################################################
sub read_biotabs {
	my ( $fnames ) = @_;

	my %subjH = ();
	my %sampH = ();
	my %biotabH = ();
	foreach my $fname ( @$fnames ){
		my $LoH = &read_table_in2_LoH_fromfile( $fname );
		my @subjL = ();
		my @sampL = ();
		my $fndSubj = 0;
		my $fndSamp = 0;
		foreach ( @$LoH ){
			if( exists $_->{'bcr_patient_barcode'} ){
				push( @subjL, $_->{'bcr_patient_barcode'} );
				$fndSubj++;
			}
			if( exists $_->{'bcr_sample_barcode'} ){
				push( @sampL, $_->{'bcr_sample_barcode'} );
				$fndSamp++;
			}
		}
		if ( $fndSubj ){
			$subjH{ $fname } = \@subjL;
			$biotabH{ $fname } = &LoH2HoH( $LoH, 'bcr_patient_barcode' );
		} else {
			warn "bcr_patient_barcode not found in file: \"$fname\".\n";
		}
		if ( $fndSamp ){
			$sampH{ $fname } = \@sampL;
			$biotabH{ $fname } = &LoH2HoH( $LoH, 'bcr_sample_barcode' );
		} else {
			warn "bcr_sample_barcode not found in file: \"$fname\".\n";
		}

	}
	return( \%subjH, \%biotabH, \%sampH );
}
####################################################################################################
sub read_subject_list {
	my ( $fname ) = @_;

	my @subjL = ();
	my %subjH = ();
	open( FILE, "$fname" ) or die "Cannot open subject file: \"$fname\" for reading.\n";
	while( <FILE> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		my @F = split( /\t/, $_ );
		next if ( $F[0] =~ m/bcr_patient_barcode/ );
		unless ( $F[0] =~ m/^TCGA-\w{2}-\w{4}$/ ){
			warn "Presumptive subject ID, \"$F[0]\", does not match TCGA barcode prototype.\n";
		}
		push( @subjL, $F[0] );
		$subjH{ $F[0] } = "";
	}
	return( \@subjL, \%subjH );
}

####################################################################################################
#### end of template ###############################################################################
####################################################################################################
