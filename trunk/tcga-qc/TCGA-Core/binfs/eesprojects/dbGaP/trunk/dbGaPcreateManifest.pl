#! /usr/bin/perl -w
####################################################################################################
my $revision = '$Revision: 18034 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "dbGaPcreateManifest.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Wed Sep 14 13:12:16 EDT 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

dbGaPcreateManifest.pl

=head1 SYNOPSIS

dbGaPcreateManifest.pl <file_regex1> [<file_regex2>, ...]

=head1 USAGE

I<dbGaPcreateManifest.pl> will create a tcga_manifest.txt file such as that required for TCGA
submissions to dbGaP for the files identified by the supplied regular expression(s).  Multiple
regexes can be combined to identify all the required files.  This can be useful if it is necessary
to avoid certain other files in a given directory.  However, in most cases, /tcga_.*.txt/ should
be sufficient.  Do not use a leading './' in the regex, as would be the case using find(1).
If the filename "tcga_manifest.txt" is encountered in the directory, it will be ignored and
overwritten.

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
use EESnyder;
use MyUsage;
use TCGA;


my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

my $MANIFEST_FILENAME = "tcga_manifest.txt";
my @headings = ( "Submitted File Name", "File Type", "File Description", "File Size (in kb)", "Comments" );
# my @headings = ( "Submitted File Name", "File Size (kb)",  "Submitted File Name",  "File Size (kb)", "File Type", "File Description");
my @reject = ( $MANIFEST_FILENAME );
my %fileDesc =	(	'subj'					=>	"Subject ID, disease study and consent information",
					'subj_consent'			=>	"Subject ID, disease study and consent information",
					'subj_info'				=>	"Subject phenotype information",
					'subj_samp'				=>	"Table relating subject IDs to aliquot IDs and disease_study",
					'subj_samp_study'		=>	"Table relating subject IDs to aliquot IDs and disease_study",
					'slide'					=>	"Interpretation of diagnostic images from tissue sections",
					'subj_rad'				=>	"Information concerning radiation treatment regimen",
					'subj_drugs'			=>	"Information on pharmacological treatment regimen",
					'samp'					=>	"Data on samples and derived biospecimens",
				);

my $kilo = 1024;												# definition of 1000 when determining kilobytes

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
		'usage'    => "use this dir as source for filenames",
		'required' => 0,
		'init'     => '.',
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

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

my $dir = $opts{'f'};

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
opendir(my $dh, $dir ) || die "can't open directory \"$dir\": $!";
my @allFiles = readdir( $dh );
closedir $dh;
my $nfiles = @allFiles;
print join( ", ", @allFiles ) . "\n$nfiles\n";

my @matchFiles = ();
foreach my $regex ( @ARGV ){
	push ( @matchFiles, grep { /^$regex$/ && -f "$dir/$_" } @allFiles );
}
my ( $manifestFiles, $fileCount ) = &uniq_strings( \@matchFiles, \@reject );

print "manifest: \"" . join( "\", \"", @$manifestFiles ) . "\"\n" . "file count: $fileCount\n";

my %dd2ds = ();
foreach ( @$manifestFiles ){
	if ( m/^(tcga_(\w+)_([A-Z]{3,4}))_dd.txt/ ){						# if it is a study file ...
		$dd2ds{$1}{'ds'} = $1 . ".txt";
		$dd2ds{$1}{'dd'} = $_;
		if ( exists $studyDisease{$3} ){
			$dd2ds{$1}{'study'} = $3;
			$dd2ds{$1}{'longStudy'} = $studyDisease{$3};
		}
		if ( exists $fileDesc{$2} ){
			$dd2ds{$1}{'ds_desc'} = $fileDesc{$2};
			$dd2ds{$1}{'dd_desc'} = "Data dictionary file for $_";
			$dd2ds{$1}{'ds_comments'} = "";
			$dd2ds{$1}{'dd_comments'} = "";
		}
	}elsif ( m/^(tcga_(\w+))_dd.txt/ ){									# if it is a TCGA MASTER file ...
		my $m = "MASTER";
		$dd2ds{$1}{'ds'} = $1 . ".txt";
		$dd2ds{$1}{'dd'} = $_;
		if ( exists $studyDisease{$m} ){
			$dd2ds{$1}{'study'} = $m;
			$dd2ds{$1}{'longStudy'} = $studyDisease{$m};
		}
		if ( exists $fileDesc{$2} ){
			$dd2ds{$1}{'ds_desc'} = $fileDesc{$2};
			$dd2ds{$1}{'dd_desc'} = "Data dictionary file for $_";
			$dd2ds{$1}{'ds_comments'} = "";
			$dd2ds{$1}{'dd_comments'} = "";
		}
	} else {
	}
}
foreach my $tag ( keys %dd2ds ){
	my $ls = `ls -s $dd2ds{$tag}{'ds'}`;
	if ( $ls =~ m/^(\d+)/ ){
		$dd2ds{$tag}{'ds_size'} = $1;
		$dd2ds{$tag}{'ds_ftype'} = "Subject/phenotype data";
	}
	$ls = `ls -s $dd2ds{$tag}{'dd'}`;
	if ( $ls =~ m/^(\d+)/ ){
		$dd2ds{$tag}{'dd_size'} = $1;
		$dd2ds{$tag}{'dd_ftype'} = "Data dictionary";
	}
}

&make_manifest( \%dd2ds, \@headings );

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub make_manifest {
	my ( $dd2ds, $heads ) = @_;

	open( FILE, ">$MANIFEST_FILENAME" ) or die "Cannot open file: \"$MANIFEST_FILENAME\" for writing.\n";

	print FILE join("\t", @$heads ) . "\n";
	foreach ( keys %$dd2ds ){
		foreach my $key ( qw( ds ds_ftype ds_desc ds_size ds_comments ) ){
			if( exists $dd2ds->{$_}{$key} ){
				print FILE $dd2ds->{$_}{$key};
			}
			print FILE "\t";
		}
		print FILE "\n";
		foreach my $key ( qw( dd dd_ftype dd_desc dd_size dd_comments ) ){
			if( exists $dd2ds->{$_}{$key} ){
				print FILE $dd2ds->{$_}{$key};
			}
			print FILE "\t";
		}
		print FILE "\n";
	}
	close( FILE );

}
####################################################################################################
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
