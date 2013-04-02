#! /bin/perl -w
####################################################################################################
our	$pgm_name		= "dbGaPcreateManifest.pl";
our	$VERSION		= "v1.0.0 (dev)";
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
use TCGA ;
use Tree::Nary;


my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

my $MANIFEST_FILENAME = "tcga_manifest.txt";
my @heading = ( "Submitted File Name", "File Type", "File Description", "File Size (in kb)", "Comments" );
my @reject = ( $MANIFEST_FILENAME );

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

foreach my $key ( keys %studyDisease ){
	print "studyDisease{$key} = " . $studyDisease{$key} . "\n";
}
&make_manifest( $manifestFiles );

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub make_manifest {
	my ( $files ) = @_;

#	open( FILE, ">$MANIFEST_FILENAME" ) or die "Cannot open file: \"$MANIFEST_FILENAME\" for writing.\n";

	my $root_node = new Tree::Nary ( $studyName );
	foreach my $file ( @$files ){
		my @line = ();
		if ( @line = &parse_file_name( $file, $root_node ) ){

		}
	}
}
####################################################################################################
sub parse_file_name {
	my ( $name, $node ) = @_;

	print "$name: $node\n";
	my $j = 0;
	my @field = split ( /[_.]/, $name);
	for( my $i = 1; $i < @field; $i++ ){
		my $n = new Tree::Nary( $field[$i] );
		$node->append($node, $i, $n );
		print "n = \"$n\"\n";
	}


}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
