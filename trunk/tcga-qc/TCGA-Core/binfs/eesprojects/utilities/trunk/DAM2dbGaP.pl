#! /bin/perl -w
# $Id: DAM2dbGaP.pl 17749 2012-10-06 01:24:25Z snyderee $
# $Revision: 17749 $
# $Date
####################################################################################################
our	$pgm_name		= "DAM2dbGaP.pl";
our	$VERSION		= "v1.1.0 (dev)";
our	$start_date		= "Tue Sep  4 16:19:14 EDT 2012";
our	$rel_date		= '$Date: 2012-10-05 21:24:25 -0400 (Fri, 05 Oct 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

DAM2dbGaP.pl

=head1 SYNOPSIS

DAM2dbGaP.pl METADATA/<GCC/GSC+platform>/<GCC/GSC>_<study>.<platform>.sdrf.txt ...

=head1 PURPOSE

The purpose of I<$pgm_name> is to take the output from a DAM query (of genotypes) and process the
data into a format suitable for submission to dbGaP.  At the moment, it does the following:
	1.	Modifies .sdrf file so that:
		a.  it contains only records corresponding to the actual files identified by DAM
		b.	duplicate headings are numbered with the addition of " [n]" to the original heading.
		c.	a new column [1] is added containing bcr_aliquot_barcode
		d.	at least one additional column is added after the first containing the name of the
			relevant file(s) located in the _data_ directory.
	2.	Creates a _submission_ directory, ../submission_<current_date>/, containing a mirror of
		the directory structure from the original DAM tar file.  This is not optimal but will do
		for now.
	3.	Creates two new files in the cwd:
		a.	extra_aliquots_in_sdrf: indicates identifiers in the .sdrf file that cannot be found
			in the corresponding data files
		b.	multi_hit_files: contains identifiers associated with multiple data files

=head1 RATIONALE

When a DAM query is executed with a list of samples or subjects, only those files are returned in
the resulting archive.  HOWEVER, if there is a corresponding .sdrf file, it will reflect ALL
samples run on the platform in question.  Therefore, the .sdrf file needs to be rebuilt so that
only the samples in the original query are present.

=cut
####################################################################################################
#	Testbed:	/home/eesnyder/projects/nih/OV/submission_prep/DAMprep_2012-06-07
#	Cmdline:	METADATA/HMS__HG-CGH-244A/hms.harvard.edu_OV.HG-CGH-244A.sdrf.txt
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use Cwd;
use Date::Handler;
use File::Path qw(make_path remove_tree);
use File::Copy;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash
my $date = new Date::Handler({ date => time, time_zone => 'US/Eastern', locale => 'en_US'});
my $date_tag = sprintf("%4d-%02d-%02d", $date->Year, $date->Month, $date->Day );

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
	'e' => {
		'type'     => "string",
		'usage'    => "extension for new SDRF file",
		'required' => 0,
		'init'     => "mod",
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'n' => {
		'type'     => "string",
		'usage'    => "create a new directory tree with the processed data",
		'required' => 1,
		'init'     => "../submission_$date_tag/",
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
#	platform information
####################################################################################################
my %platform = (
	'MSKCC__HG-CGH-244A'			=>	{	'fname'			=>	'mskcc.org__HG-CGH-244A__SampleName__copy_number_analysis.txt',
											'SampleName'	=>	'Sample Name [2]',
											'dir'			=>	'Copy_Number_Results/MSKCC__HG-CGH-244A/Level_3/',
											'copydir'		=>	'SNP/MSKCC__HG-CGH-244A/Level_3/segfiles/',
											'magedir'		=>	'SNP/MSKCC__HG-CGH-244A/Level_3/magetab/'
										},
	'BI__Genome_Wide_SNP_6'			=>	{	'fnameHg19'		=>	'broad.mit.edu__Genome_Wide_SNP_6__SampleName__snp_analysis.hg19.seg.txt',
											'fnameHg18'		=>	'broad.mit.edu__Genome_Wide_SNP_6__SampleName__snp_analysis.hg18.seg.txt',
											'fnameHg19nocnv'=>	'broad.mit.edu__Genome_Wide_SNP_6__SampleName__snp_analysis.nocnv_hg19.seg.txt',
											'fnameHg18nocnv'=>	'broad.mit.edu__Genome_Wide_SNP_6__SampleName__snp_analysis.nocnv_hg18.seg.txt',
											'SampleName'	=>	'Comment [Aliquot Barcode]',
#											'SampleName'	=>	'Comment [TCGA Barcode]',
											'dir'			=>	'SNP/BI__Genome_Wide_SNP_6/Level_3/',
											'copydir'		=>	'SNP/BI__Genome_Wide_SNP_6/Level_3/segfiles/',
											'magedir'		=>	'SNP/BI__Genome_Wide_SNP_6/Level_3/magetab/'
										},
	'HAIB__HumanHap550'				=>	{	'fnameTseg'		=>	'hudsonalpha.org__HumanHap550__SampleName__snp_analysis.seg.txt',
											'fnameTloh'		=>	'hudsonalpha.org__HumanHap550__SampleName__snp_analysis.loh.txt',
											'fnameN'		=>	'hudsonalpha.org__HumanHap550__SampleName__snp_analysis.segnormal.txt',
											'SampleName'	=>	'Normalization Name [3]',
											'dir'			=>	'SNP/HAIB__HumanHap550/Level_3/',
											'copydir'		=>	'SNP/HAIB__HumanHap550/Level_3/segfiles/',
											'magedir'		=>	'SNP/HAIB__HumanHap550/Level_3/magetab/'
										},
	'HAIB__Human1MDuo'				=>	{	'fnameTseg'		=>	'hudsonalpha.org__Human1MDuo__SampleName__snp_analysis.seg.txt',
											'fnameTloh'		=>	'hudsonalpha.org__Human1MDuo__SampleName__snp_analysis.loh.txt',
											'fnameN'		=>	'hudsonalpha.org__Human1MDuo__SampleName__snp_analysis.segnormal.txt',
											'SampleName'	=>	'Normalization Name [3]',
											'dataLevel'		=>	'Comment [TCGA Data Level] [4]',
											'dir'			=>	'SNP/HAIB__Human1MDuo/Level_3/',
											'copydir'		=>	'SNP/HAIB__Human1MDuo/Level_3/segfiles/',
											'magedir'		=>	'SNP/HAIB__Human1MDuo/Level_3/magetab/'
										},
	'HMS__HG-CGH-244A'				=>	{	'fname'			=>	'hms.harvard.edu__HG-CGH-244A__SampleName__copy_number_analysis.txt',
											'SampleName'	=>	'Sample Name [2]',
											'dir'			=>	'Copy_Number_Results/HMS__HG-CGH-244A/Level_3/',
											'copydir'		=>	'SNP/HMS__HG-CGH-244A/Level_3/segfiles/',
											'magedir'		=>	'SNP/HMS__HG-CGH-244A/Level_3/magetab/'
										},
	'HMS__HG-CGH-415K_G4124A'		=>	{	'fname'			=>	'hms.harvard.edu__HG-CGH-415K_G4124A__SampleName__copy_number_analysis.txt',
											'SampleName'	=>	'Sample Name [2]',
											'dir'			=>	'Copy_Number_Results/HMS__HG-CGH-415K_G4124A/Level_3/',
											'copydir'		=>	'Copy_Number_Results/HMS__HG-CGH-415K_G4124A/Level_3/segfiles/',
											'magedir'		=>	'Copy_Number_Results/HMS__HG-CGH-415K_G4124A/Level_3/magetab/',
										},
	'HMS__IlluminaHiSeq_DNASeqC'	=>	{	'fname'			=>	'hms.harvard.edu__IlluminaHiSeq_DNASeqC__SampleName__copy_number_analysis.txt',
											'SampleName'	=>	'Extract Name',
											'dir'			=>	'Copy_Number_Results/HMS__IlluminaHiSeq_DNASeqC/Level_3/',
											'copydir'		=>	'Copy_Number_Results/HMS__IlluminaHiSeq_DNASeqC/Level_3/segfiles/',
											'magedir'		=>	'Copy_Number_Results/HMS__IlluminaHiSeq_DNASeqC/Level_3/magetab/',
										},
	'MSKCC__CGH-1x1M_G4447A'		=>	{	'fname'			=>	'mskcc.org__CGH-1x1M_G4447A__SampleName__copy_number_analysis.txt',
											'SampleName'	=>	'Sample Name [2]',
											'dir'			=>	'Copy_Number_Results/MSKCC__CGH-1x1M_G4447A/Level_3/',
											'copydir'		=>	'Copy_Number_Results/MSKCC__CGH-1x1M_G4447A/Level_3/segfiles/',
											'magedir'		=>	'Copy_Number_Results/MSKCC__CGH-1x1M_G4447A/Level_3/magetab/',
										},
);

my @infiles = qw( METADATA/<GCC/GSC+platform>/<GCC/GSC>_<study>.<platform>.sdrf.txt );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $hline = '-' x 80 . "\n";												# horizontal line
my $sdrf_ext = $opts{'e'};													# get file extension for new SDRF file
my %cannotFindHoL = ();														# aliquots from SDRF that cannot be found in data files
my %multiHitHoL = ();														# aliquots from SDRF that get multiple hits
my @platformL = ();															# list of platform names
my $outdir = &create_output_directory( $opts{'n'} ) if $opts{'n'};			# make new directory tree for output
foreach my $sdrf_fname ( @ARGV ){											# loop over SDRF files on cmdline
	my $platname = $sdrf_fname;												# copy name with full path
	unless ( $platname =~ s/METADATA\/([A-Z]+__[^\/]+)\/.+$/$1/ ){			# extract platform from directory
		warn "Cannot parse platform from path to SDRF file: \"$platname\".\n";
		warn "Program must be executed in the root directory where DAM tar file was unpacked.  Skipping.\n";
		next;
	}
	if ( exists $platform{ $platname } ){									# check for platform name in internal DB
		print "platform name: $platname\n" if $opts{'d'};
	} else {
		warn "Cannot identify platform name: \"$platname\".  Skipping.\n";
		next;
	}
	my $plat = $platform{$platname} ;										# get pointer to platform info for this sdrf
	push( @platformL, $platname );											# add platform name to list of platforms

#	read files from platform data directory

	my $dir = $plat->{'dir'} ;												# get directory for data files
	opendir( MYDIR, $plat->{'dir'} )
		or die "Cannot open directory: \"".$plat->{'dir'}."\" for listing.\n";
	my @dfnames = grep !/^\./, readdir( MYDIR );							# read in data files from platform directory
	closedir( MYDIR );

# 	prepare new .sdrf file for platform

	my ( $sdrfLoH, $headings ) = &read_table_in2_LoH_fromfile( $sdrf_fname ); # read the SDRF file contents into LoH keyed on column headings
	my @fnameKeys = sort grep ( /fname/, keys %$plat );						# get prototype(s) for output file names
	my @header = ( @fnameKeys, @$headings );								# create new header consisting of datafile name(s) + SDRF headings
	unshift( @header, "bcr_aliquot_barcode" );								# make "bcr_aliquot_name" column 1

	my $new_sdrf_fname = "$outdir/$sdrf_fname";								# add submission_dir prefix to SDRF filename
#	my $new_sdrf_fname = "$sdrf_fname.$sdrf_ext" ;
	open( NEW_SDRF, ">$new_sdrf_fname")
		or die "Cannot open SDRF output file: \"$new_sdrf_fname\" for writing.\n";
	print NEW_SDRF join( "\t", @header ) . "\n";							# then create a new SDRF in new location based on required files

	my $idf_fname = $sdrf_fname;
	my $new_idf_fname = $new_sdrf_fname;
	if ( $idf_fname =~ s/\.sdrf\./.idf./ &&
		 $new_idf_fname =~ s/\.sdrf\./.idf./ ){
		copy( $idf_fname, $new_idf_fname );
	} else {
		die "Cannot convert SDRF filename (\"$sdrf_fname\") to IDF equivalent (\"$idf_fname\").\n"
	}
	my $data_outdir = "$outdir/" . $plat->{'dir'};							# directory for writing data files
	foreach my $hash ( @$sdrfLoH ){											# loop through SDRF file
		if ( exists $plat->{'dataLevel'} ){									# if data level is relevant
			next if $hash->{ $plat->{'dataLevel'} } ne "Level 3";			# make sure it equals 3, skip to next if it isn't
		}
		my $aliquot;
		if ( exists $hash->{ $plat->{'SampleName'} } ){						# check if the reference heading for SN actually exists
			$aliquot = $hash->{ $plat->{'SampleName'} };					# get bcr_aliquot_barcode
		} else {
			die "ERROR: Platform SampleName, \"" . $plat->{'SampleName'} .	# if not, crash and burn
				"\" not found in SDRF file headings.\n";
		}
		my @fields = ( $aliquot );											# first data field is bcr_aliquot_barcode
		my @fileHits = grep( /$aliquot/, @dfnames );						# make sure there is exactly one such aliquot in datafiles
		if ( @fileHits == 0 ){												# log if none (this happens when SDRF file has extra data)
			push( @{$cannotFindHoL{$platname}}, $aliquot );
			warn "Cannot find aliquot: \"$aliquot\" in actual file list.\n" if $opts{'d'};
			next;
		} elsif ( @fileHits > 1 ){											# log if more than one hit
			push( @{$multiHitHoL{$platname}}, $aliquot );
			warn "More that one hit to: \"$aliquot\" in \@dfnames: \"" .
				join( ", ", @fileHits ) . "\n" if $opts{'d'};
		}
		foreach my $hit_fname ( @fileHits ){
			if ( -r "$dir/$hit_fname" ){
				copy( "$dir/$hit_fname", "$data_outdir/$hit_fname" );
				print "$dir/$hit_fname, $data_outdir/$hit_fname\n" ;
			}
		}

		foreach my $key ( @fnameKeys ){										# loop through filename prototypes
			my $fname = $plat->{ $key };									# get filename string
			$fname =~ s/SampleName/$aliquot/;								# substitute in filename
			push( @fields, $fname );										# and add to the list of fields for current line
		}
		foreach my $key ( @$headings ){
			push( @fields, $hash->{ $key } );
		}
		print NEW_SDRF join("\t", @fields ) . "\n";
	}
	close( NEW_SDRF );
	open( OUT1, ">extra_aliquots_in_sdrf" ) or die "Cannot open extra_aliquots_in_sdrf for writing.\n";
	open( OUT2, ">multi_hit_files") or die "Cannot open multi_hit_files for writing.\n";
	foreach my $pform ( @platformL ){
		if ( exists $cannotFindHoL{ $pform } ){
			print OUT1 "\n$hline$pform\n$hline";
			print OUT1 join( "\n", @{$cannotFindHoL{ $pform }} ) . "\n";
		}
		if ( exists $multiHitHoL{ $pform }) {
			print OUT2 "\n$hline$pform\n$hline";
			print OUT2 join( "\n", @{$multiHitHoL{ $pform }} ) . "\n";
		}
	}
	close( OUT1 );
	close( OUT2 );
}
##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
#	create_output_directory()
#	Read directory structure under CWD and create mirror under ../submission_<date>.
####################################################################################################
sub create_output_directory{
	my ( $dir_root ) = @_;

	my $cwd = getcwd;											# get DAM2dbGaP.pl execution dir
	my $owd = $cwd;												# original working directory
	print "cwd = $cwd\n";
	my ( @dirs ) = &get_dirs ( $cwd );							# get subdirectories
	for( my $i = 0; $i < @dirs; $i++ ){							# and prepend fully-qualified path
		$dirs[$i] =~ s/^$cwd\/(.+)$/$1/;
		print "dirs[$i] = $dirs[$i]\n";

	}
#	chdir( ".." );
	$cwd = getcwd;
	print "cwd = $cwd\n";
	mkdir( "$dir_root");
	chdir( "$dir_root");
	my $output_dir = getcwd;									# create output directory
	print "output_dir = $output_dir\n";
	make_path( @dirs );											# and subdirectories
	chdir( $owd );
	print "cwd = " . getcwd . "\n";
	return( $output_dir );
}
####################################################################################################
#	get_dirs()
#	Get directory structure under DAM2dbGaP.pl executions directory.
####################################################################################################
sub get_dirs {
	my ( $root ) = @_;

	print "root = $root\n";
	opendir( MYDIR, $root ) or die "Cannot open directory: \"$root\" for listing.\n";
	my @dfnames = grep !/^\./, readdir( MYDIR );							# read in data files
	my @dirs = ();
	foreach ( @dfnames ){
		my $newdir = "$root/$_";
		push( @dirs, $newdir ) if ( -d $newdir );
	}
	closedir( MYDIR );
	my @tmpdirs = @dirs;
	foreach ( @tmpdirs ){

		push( @dirs, &get_dirs( $_ ) );
	}
	return( @dirs );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
