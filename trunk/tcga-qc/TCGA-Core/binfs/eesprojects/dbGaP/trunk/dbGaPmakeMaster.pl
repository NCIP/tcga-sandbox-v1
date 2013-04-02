#! /usr/bin/perl -w
# $Id: dbGaPmakeMaster.pl 18016 2013-01-12 02:24:21Z snyderee $
# $Revision: 18016 $
# $Date
####################################################################################################
my $revision = '$Revision: 18016 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "dbGaPmakeMaster.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Tue Sep 11 15:54:27 EDT 2012";
our	$rel_date		= '$Date: 2013-01-11 21:24:21 -0500 (Fri, 11 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	HHS/NIH/NCI|NHGRI/TCGA [C] SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

dbGaPmakeMaster.pl

=head1 SYNOPSIS

dbGaPmakeMaster.pl

=head1 USAGE

I<dbGaPmakeMaster.pl> creates the required files for a dbGaP MASTER update.  The files are:
    tcga_manifest.txt
    tcga_subj.txt
    tcga_subj_samp.txt
    tcga_subj_dd.txt
    tcga_subj_samp_dd.txt


=cut
####################################################################################################
#	Testbed:	./
#	Cmdline:	-V
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use TCGA;
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
	'F' => {
		'type'     => "string",
		'usage'    => "output field separator",
		'required' => 1,
		'init'     => "\t",
	},
	'O' => {
		'type'     => "boolean",
		'usage'    => "disable output filter (filtering CNTLs)",
		'required' => 0,
		'init'     => 0,
	},
	'R' => {
		'type'     => "string",
		'usage'    => "file containing redaction list",
		'required' => 0,
		'init'     => $ENV{'HOME'} . "/projects/nih/annotations/redactions_current.txt",
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
		'usage'    => "TCGA master metadata file",
		'required' => 1,
		'init'     => $ENV{'HOME'} . "/projects/nih/TCGA/metadata.current.txt",
	},
	'r' => {
		'type'     => "boolean",
		'usage'    => "filter out redactions",
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
my $file_extract_AND = 0;
my %masterDefs	=	(	'tcga_subj_consent'	=>	{	'extract'		=>	[	{	'ELEMENT_TYPE'	=>	'Aliquot',	},
																			{	'ELEMENT_TYPE'	=>	'Shipped Portion',	},
																		],
													'convert'		=>	[	{	'field'			=>	'BARCODE',
																				'to'			=>	'PATIENT',
																				'using'			=>	"^(" . $itemBarcodeRegex{'Patient'} . ")-.+\$",
																			},
																		],
													'renameValues'	=>	[	{	'field'			=>	'DISEASE_STUDY',
																				'using'			=>	{	'COAD'	=>	'CRC',
																										'READ'	=>	'CRC',
																									},
																			},
																		],
													'addField'		=>	{	'name'			=>	'CONSENT',
																			'value'			=>	'1',
																		},
													'uniq'			=>	'PATIENT',
													'outFields'		=>	[ qw( PATIENT DISEASE_STUDY CONSENT)],
													'outputFilter'	=>	[	{	'DISEASE_STUDY'	=>	'CNTL',	},
																		],
													'renameFields'	=>	{	'PATIENT'		=>	'bcr_patient_barcode',
																			'DISEASE_STUDY'	=>	'study',
																			'CONSENT'		=>	'consent',
																		},
												},
					'tcga_subj_samp_study' =>	{ 	'extract'		=>	[	{	'ELEMENT_TYPE'	=>	'Aliquot',	},
																			{	'ELEMENT_TYPE'	=>	'Shipped Portion',	},
																		],
													'convert'		=>	[	{	'field'			=>	'BARCODE',
																				'to'			=>	'PATIENT',
																				'using'			=>	"^(" . $itemBarcodeRegex{'Patient'} . ")-.+\$",
																			},
																		],
													'renameValues'	=>	[	{	'field'			=>	'DISEASE_STUDY',
																				'using'			=>	{	'COAD'	=>	'CRC',
																										'READ'	=>	'CRC',
																									},
																			},
																		],
													'outFields'		=>	[ qw( PATIENT BARCODE DISEASE_STUDY )],
													'outputFilter'	=>	[	{	'DISEASE_STUDY'	=>	'CNTL',	},
																		],
													'renameFields'	=>	{	'BARCODE'		=>	'bcr_aliquot_barcode',
																			'PATIENT'		=>	'bcr_patient_barcode',
																			'DISEASE_STUDY'	=>	'study',
																		},
												},
					);

my @infiles = qw( );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
if ( $opts{'O'} ){
	$masterDefs{'tcga_subj_consent'}{'outputFilter'} = [];
	$masterDefs{'tcga_subj_samp_study'}{'outputFilter'} = [];
}

my %inputFiles	=	(	'redact'	=>	{	'fname'	=>	$opts{'R'},
											'pname'	=>	"Redact list",
											'test'	=>	$opts{'r'},				# don't print unless
										},
						'meta'		=>	{	'fname'	=>	$opts{'m'},
											'pname'	=>	"Sample Data",
											'test'	=>	$opts{'m'},
										},
					);

my $ext = "txt";

####################################################################################################
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7
####################################################################################################

print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
my $fsep = $opts{'F'} if $opts{'F'};
my $masterFile = $opts{'m'} if $opts{'m'};

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
&print_inFiles( \%inputFiles );

my @fileDefs = ();
if ( @ARGV ){
	@fileDefs = @ARGV;
} else {
	@fileDefs = ( keys %masterDefs );
}
my @fileNames = ();
our $masterSize = 0;
our $extractSize = 0;
foreach my $mdef ( @fileDefs ){
	my $redactions = &read_list_in2_L_fromfile( $opts{'R'}, 0 ) if $opts{'r'};
	my ( $masterLoH, $masterHeadings ) = &read_table_in2_LoH_fromfile( $masterFile, $fsep );
	$masterSize = @{$masterLoH};
	my $extract = &create_file_extract( $masterLoH, $masterDefs{$mdef}, $masterHeadings );
	$extractSize = @{$extract};
	$extract = &uniq_extract( $extract, $masterDefs{ $mdef } );
	$extractSize = @{$extract};
	if ( $opts{'r'} ){
		my ( $extractPat, $extractSubj ) = &redact_extract( $extract, $redactions, 'PATIENT' ) if $opts{'r'};
		&print_extract( $extractPat, "$mdef.txt", $masterDefs{$mdef}, $fsep );
		if( $mdef eq 'tcga_subj_consent' ){
			my $nrecords = @$extractPat;
			print "Created file:	$mdef.txt ($nrecords records)\n";
			&print_extract( $extractPat, "$mdef.txt", $masterDefs{$mdef}, $fsep );
			`build_dataDicts.pl $mdef.txt`;
			push( @fileNames, "$mdef.txt", "$mdef"."_dd.txt" );
		}
		if ( $mdef eq 'tcga_subj_samp_study' ){
			my $nrecords = @$extractSubj;
			print "Created file:	$mdef.txt ($nrecords records)\n";
			&print_extract( $extractSubj, "$mdef.txt", $masterDefs{$mdef}, $fsep );
			`build_dataDicts.pl $mdef.txt`;
			push( @fileNames, "$mdef.txt", "$mdef"."_dd.txt" );
		}
	} else {
		my $nrecords = @$extract;
		print "Created file:	$mdef.txt ($nrecords records)\n";
		&print_extract( $extract, "$mdef.txt", $masterDefs{$mdef}, $fsep );
		`build_dataDicts.pl $mdef.txt`;
		push( @fileNames, "$mdef.txt", "$mdef"."_dd.txt" );
	}
}
my $fileNames = join( " ", @fileNames );
`dbGaPcreateManifest.pl $fileNames`;
unless ( $? ){
	print "Created file:	tcga_manifest.txt\n";
}

##################################      ... and Here         #######################################
####################################################################################################
print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;
####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
####################################################################################################
#	print_inFiles()
#	take HoH describing input files, finding real name if symbolic link
####################################################################################################
sub print_inFiles {
	my ( $dat ) = @_;

	foreach my $file ( values %$dat ){
		next unless ( $file->{'test'} );
		my $str = `ls -l $file->{'fname'}`;
		my $lines = "";
		if( $str =~ m/ (\S+) -> (\S+)$/ ){
			$str = $2;

		} elsif ( $str =~ m/ (\S+)$/ ){
			$str = $1;
		}
		$lines = `wc -l $str`;
		$lines =~ s/^(\d+).+\n/$1/;
		print $file->{'pname'} . ":	$str ($lines lines)\n" if (1);
		printf("%20s %30s (lines %6d\n", $file->{'pname'}, $str, $lines ) if (0);
	}
}
####################################################################################################
#	uniq_extract()
####################################################################################################
sub uniq_extract {
	my ( $dat, $def ) = @_;

	return ( $dat ) unless exists $def->{'uniq'};
	my $key = $def->{'uniq'};

	my %uniqPat = ();
	foreach ( @$dat ){
		my $pat = $_->{$key};
		$uniqPat{ $pat } = $_;
	}
	my @tmp = values %uniqPat;
	my @data = sort { $a->{ $key } cmp $b->{ $key } } @tmp;

	return( \@data );
}
####################################################################################################
#	redact_extract()
####################################################################################################
sub redact_extract {
	my ( $dat, $redact, $key ) = @_;
	my %pat2hash = ();
	my @pats = ();
	my %sampsHoLoH = ();
	foreach ( @$dat ){
		my $pat = $_->{$key};
		push( @pats, $pat );
		$pat2hash{ $pat } = $_;
		if( exists $sampsHoLoH{ $pat } ){
			push( @{$sampsHoLoH{$pat}}, $_ );
		} else {
			@{$sampsHoLoH{$pat}} = ( $_ );
		}
	}
	my $lc = List::Compare->new( \@pats, $redact );
	my @cleanPats = $lc->get_Lonly;
	my @newPat = ();
	my @newSubjLoH = ();
	foreach my $pat ( @cleanPats ){
		push( @newPat, $pat2hash{$pat} );
		push( @newSubjLoH, @{$sampsHoLoH{ $pat }} );
	}
	return( \@newPat, \@newSubjLoH );
}
####################################################################################################
#	print_extract()
####################################################################################################
sub print_extract {
	my ( $dat, $fname, $def, $fsep ) = @_;

	open( FILE, ">$fname" ) or die "Cannot open file: \"$fname\" for output in &print_extract().\n";

	my $outline = '';
	foreach my $f ( @{ $def->{'outFields'} } ){
		$outline .= $def->{'renameFields'}{ $f } . $fsep;
	}
	$outline =~ s/$fsep$/\n/;
	print FILE $outline;


	foreach my $d ( @$dat ){
		$outline = '';
		my $skip = 0;
		foreach my $hash ( @{$def->{'outputFilter'}} ){
			foreach my $key ( keys %$hash ){
				if( $d->{$key} eq $hash->{$key} ){		#skip printing row if it contains an outputFilter value
					$skip++;
					last;
				}
			}
		}
		next if $skip;
		foreach my $f ( @{ $def->{'outFields'} } ){
			$outline .= $d->{ $f } . $fsep;
		}
		$outline =~ s/$fsep$/\n/;
		print FILE "$outline";
	}
	close( FILE );
}
####################################################################################################
sub create_file_extract {
	my ( $data, $def, $headings ) = @_;

	my @newData = ();
	my $filterKeys = scalar( @{$def->{'extract'}} );				# number of filter keys
	foreach ( @$data ){
		my $hits = 0;
		foreach my $hash ( @{$def->{'extract'}} ){
			foreach my $key ( keys %$hash ){
				$hits++ if $_->{$key} eq $hash->{$key};
			}
		}
		if ( $file_extract_AND ){										# require hits to all keys
			push( @newData, $_ ) if $hits == $filterKeys;
		} else {
			push( @newData, $_ ) if $hits;								# require hit to a key
		}
	}
	foreach my $data ( @newData ){
		foreach my $converter ( @{ $def->{'convert'} } ){
			my $tmpConv = $data->{ $converter->{'field'} };
			if( $tmpConv =~ s/$converter->{'using'}/$1/ ){
				$data->{$converter->{'to'}} = $tmpConv;
				print "conversion:" . $data->{$converter->{'field'}} . " -> $tmpConv\n" if $opts{'v'};
			}
		}
		foreach my $converter ( @{ $def->{'renameValues'} } ){
			my $tmpConv = $data->{ $converter->{'field'} };
			foreach my $sub ( keys %{ $converter->{'using'} } ){
				if ( $sub eq $tmpConv ){
					$data->{ $converter->{'field'} } = $converter->{'using'}{$sub};
				}
			}
		}
		if ( $def->{'addField'} ){
			$data->{$def->{'addField'}{'name'}} = $def->{'addField'}{'value'};
		}
	}
	return( \@newData );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
