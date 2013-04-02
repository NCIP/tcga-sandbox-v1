#! /usr/bin/perl -w
####################################################################################################
our	$pgm_name		= "extractCNVdata.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Tue Nov 15 17:02:11 EST 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

extractCNVdata.pl

=head1 SYNOPSIS

extractCNVdata.pl -s <subject_list> <SDRF_file>

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
use Cwd qw( abs_path getcwd );
use File::Copy;
use MyUsage;
use EESnyder;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

my $PATIENT_BARCODE_REGEXP = 'TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}';
my $DEFAULT_OUTPUT_DIRECTORY = $ENV{'HOME'} . "/tmp/newCNV/";
my @ACCESSORY_FILES = qw( DESCRIPTION.txt README_DCC.txt CHANGES_DCC.txt );
my @MAGE_FILES = qw( .sdrf.txt .idf.txt );

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
		'type'     => "string",
		'usage'    => "fully-qualified path to new directory for processed archive files",
		'required' => 0,
		'init'     => $DEFAULT_OUTPUT_DIRECTORY,
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	's' => {
		'type'     => "string",
		'usage'    => "subject list (contains subject barcodes, one per line; or sample barcodes from which subjects will be derived)",
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

my @infiles = qw( <SDRF_file>  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $sdrf_file = abs_path(shift @ARGV);
print "sdrf file: $sdrf_file\n" if $opts{'v'};
my $archive_dir = $sdrf_file;
$archive_dir =~ s/[^\/]+\/*$//;										# MAGE-TAB dir
my $mage_dir = $archive_dir;
print "mage dir: $archive_dir\n";
$mage_dir =~ s/.+\/([^\/]+)\/*$/$1/;
print "mage dir: $mage_dir\n";
$archive_dir =~ s/[^\/]+\/*$//;										# dir containing archive dirs
print "archive dir: $archive_dir\n";
my $all_archives = &read_archive_dirs( $archive_dir );				# get list of archive (dir) names

my $subjfile = $opts{'s'};

my $sdrf = &read_table_in2_LoH_fromfile( $sdrf_file );				# read SDRF file
my $subjects = &read_subject_list( $subjfile );						# read subject file-09-08/TCGA_489.subj -d ~/tmp/scratch/CNVarch
my $output_dir = &create_output_directory( $opts{'d'} );			# create output directory
print "output dir: $output_dir\n";

# cross ref subjects with archive/filenames
my ( $fileList, $dirList ) = &find_subj_files( $subjects, $sdrf );
push( @$dirList, $mage_dir );										# include MAGE-TAB dir

# create new archive dirs as needed and copy files to appropriate directory
&create_output_dirs( $output_dir, $dirList );
my $current_dir = getcwd;
&copy_subj_files( $fileList, $output_dir, $archive_dir, $dirList, $sdrf_file, $subjects );
# create new SDRF with only the relevant subjects and files




##################################      ... and Here         #######################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub copy_subj_files {
	my ( $files, $outdir, $archdir, $dir_list, $sdrf_file, $subjects ) = @_;

	foreach my $file ( @$files ){
		copy( "$archdir/$file", "$outdir/$file" );
	}
	foreach my $dir ( @$dir_list ){
		foreach my $file ( @ACCESSORY_FILES ){
			my $acc_file = "$archdir/$dir/$file";
			if ( -e $acc_file ){
				copy( $acc_file, "$outdir/$dir/$file" );
			} else {
				print "$acc_file does not exist; nothing to copy.\n" if $opts{'v'};
			}
		}
		print "dir: $dir\n";
		if ( $dir =~ m/mage-tab/i ){
			my $sdrf_tail = $sdrf_file;
			$sdrf_tail =~ s/^.+\/([^\/]+\/*)$/$1/;
			&make_new_sdrf("$archdir/$dir/$sdrf_tail", "$outdir/$dir/$sdrf_tail", $subjects );
			my $idf_tail = $sdrf_tail;
			$idf_tail =~ s/sdrf/idf/;
			copy( "$archdir/$dir/$idf_tail", "$outdir/$dir/$idf_tail" );
		}
		chdir( "$outdir/$dir" );
		`md5sum * > MANIFEST.txt`;
	}
}
####################################################################################################
sub make_new_sdrf {
	my ( $sdrf_in, $sdrf_out, $subjects ) = @_;

	open( FILE, "$sdrf_in" ) or die "Cannot open file: \"$sdrf_in\" for reading.\n";
	open( OUT,  ">$sdrf_out" ) or die "Cannot open file: \"$sdrf_out\" for writing.\n";
	my $i = 0;
	while( <FILE> ){
		if ( $i++ ){
			foreach my $sub ( @$subjects ){
				if ( m/$sub/ ){
					print OUT $_;
					last;
				}
			}
		} else {
			print OUT $_;											# write first (header) line
		}
	}
	close( FILE );
	close( OUT );
}
####################################################################################################
sub create_output_dirs {
	my ( $output_root, $dirs ) = @_;

	foreach my $dir ( @$dirs ){
		my $new_dir = "$output_root/$dir" ;
		mkdir $new_dir;
		print "new dir: $new_dir\n";
	}
}
####################################################################################################
sub find_subj_files {
	my ( $subj, $sdrf ) = @_;
	my $dir_key = 'Comment [TCGA Archive Name]';
	my $fname_key = 'Derived Array Data File';
	my $barcode_key = 'Sample Name';
	my @subjdat = ();												# list of data files for subjects
	my %used_dirs = ();												# which arch dirs get used and how many times

	foreach ( @$sdrf ){												# loop over SDRF records
		print "$_->{$barcode_key}\n" if $opts{'v'};
		foreach my $subj_bc ( @$subj ){								# loop over subject barcode list
			if ( $_->{$barcode_key} =~ m/^$subj_bc/ ){				# if subject BC is substr of sample BC
				if( exists $used_dirs{ $_->{$dir_key}} ){			# if arch dir has been seen before
					$used_dirs{ $_->{$dir_key} }++;					# increment dir count
				} else {											# otherwise
					$used_dirs{ $_->{$dir_key} } = 1;				# initialize
				}
				my $datfile = "$_->{$dir_key}/$_->{$fname_key}";	# join data dir with file name
				push( @subjdat, $datfile );							# add to files list
				print "$datfile\n" if $opts{'v'};
			}
		}
	}
	my @used_dirs = ();
	foreach ( sort keys %used_dirs ){
		push( @used_dirs, $_ );
		print "$_: $used_dirs{$_}\n";
	}
	return( \@subjdat, \@used_dirs );
}
####################################################################################################
sub read_archive_dirs {
	my ( $archive_dir ) = @_;

	my @archive_list = ();
	my $mage_dir = "";
	opendir( DIR, "$archive_dir" ) or die "Cannot open directory: \"$archive_dir\" for reading.\n";
	foreach ( readdir( DIR ) ){
		next if m/^\.+$/;
		if ( -d $_ ){
			if ( m/mage-tab/i ){
				$mage_dir = $_;
				next;
			}
			if ( m/Level_3/ ){
				print "archive dir: $_\n";
				push( @archive_list, $_ );
			}
		}
	}
	close( DIR );
	return( \@archive_list );
}
####################################################################################################
sub read_subject_list {
	my ( $file )  = @_;

	open( FILE, "$file" ) or die "Cannot open subject file: \"$file\" for reading.\n";
	my %subj_bc = ();
	while( <FILE> ){
		chomp;
		next if /^#+\s*/;
		next if /^\s+$/;
		if ( m/^($PATIENT_BARCODE_REGEXP)/ ){
			if ( exists( $subj_bc{ $1 }) ){
				$subj_bc{$1}++;
			} else {
				$subj_bc{$1} = 1;
			}
		}
	}
	my @subj_bc = sort keys %subj_bc;
	return( \@subj_bc );
}
####################################################################################################
sub create_output_directory {
	my ( $path ) = @_;

	my @dirs = split( /\//, $path );
	my $test_path = "";
	foreach ( @dirs ){
		next unless ( $_ );
		my $tmp_test_path = "$test_path/$_";
		unless ( -d $tmp_test_path ) {
			if ( -w $test_path ){
				mkdir( $tmp_test_path );
			} else {
				die "Cannot create directory: \"$tmp_test_path\".\n";
			}
		}
		$test_path .= "/$_";
	}
	print "output dir: $test_path\n" if $opts{'v'};
	return( $test_path );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
