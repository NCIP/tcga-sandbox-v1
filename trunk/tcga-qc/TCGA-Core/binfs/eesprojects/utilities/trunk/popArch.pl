#! /usr/bin/perl -w
# $Id: popArch.pl 17880 2012-11-16 02:20:26Z snyderee $
# $Revision: 17880 $
# $Date
####################################################################################################
our	$pgm_name		= "popArch.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Thu Dec 15 20:31:53 EST 2011";
our	$rel_date		= '$Date: 2012-11-15 21:20:26 -0500 (Thu, 15 Nov 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

$pgm_name

=head1 SYNOPSIS

popArch.pl file.popArch

=head1 USAGE

I<$pgm_name> populates a directory with mock (empty) files and directories to resemble a TCGA
archive directory based on a file created from that directory by:

ls -1RF > file.popArch

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
	'r' => {
		'type'     => "string",
		'usage'    => "replicate file structure in indicated directory",
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

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $infile;
if ( @ARGV ){
	$infile = shift @ARGV;
} else {
	$infile = "/tmp/archfile.$$";
	open( ARCHFILE, ">$infile" ) or die "Cannot open file: \"$infile\" for writing.\n";
	print ARCHFILE `ls -1RF`;
	close( ARCHFILE );
}
if ( $opts{'r'} ){
	cwd( $opts{'r'} );
	&make_dirs_n_files( $infile );
}
my $file_tree = &create_file_tree( $infile );
my $latest = &find_latest_files( $infile );
foreach my $dir ( @$latest ){
	foreach my $file ( @{$file_tree->{$dir}{'file'}} ){
		print "$dir/$file\n";
	}
}
unlink( $infile ) if ( $infile =~ m/^\/tmp/ );


##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub find_latest_files {
	my ( $infile ) = @_;

	open( FILE, "$infile" ) or die "Cannot open popArch input file: \"$infile\" for reading.\n";
	my @arch = ();
	while ( <FILE> ){
		chomp;
		next if m/^$/;
		next if m/^total\s+\d+/;
		if( m/^\.\/(.+):$/ ){
			push( @arch, $1 );
		}
	}
	my $latest = &find_latest_archives( \@arch );
	seek( FILE, 0, 0 );
	my $current = "";
	while ( <FILE> ){
		chomp;
		next if m/^$/;
		next if m/^total\s+\d+/;
		if( m/^\.\/(.*):$/ ){
			$current = "";
			if ( defined( $latest->{ $1 } ) ){
				$current = $1;
				$latest->{ $current }{'files'} = [];
			} else {
				$current = "";
			}
		} elsif( $current ){
			if ( s/\s+([\w\.-]+\.\w+)$/$1/){
				push( @{ $latest->{ $current }{'files'} }, $_ );
			} else {
				die "Cannot parse file name from: \"$_\".\n";
			}
		} else {
		}
	}
	close( FILE );

	my @arch_names = ();
	foreach ( sort { 	$latest->{$b}{'ver'}	<=>	$latest->{$a}{'ver'} or
						$latest->{$b}{'prefix'}	cmp	$latest->{$a}{'prefix'} } keys %$latest ){
		my $name = $_ . $latest->{$_}{'ver'} . "." . $latest->{$_}{'part'};
		push( @arch_names, $name );
	}
	return( \@arch_names );
}
####################################################################################################
sub create_file_tree {
	my ( $infile ) = @_;

	open( FILE, "$infile" ) or die "Cannot open popArch input file: \"$infile\" for reading.\n";
	my %arch_tree = ();
	my $dir = "";
	my $file;
	while ( <FILE> ){
		chomp;
		next if m/^$/;
		next if m/^total\s+\d+/;
		if( m/^\.\/(.+):$/ ){
			$dir = $1;
		}
		next unless $dir;
		if ( m/^(.+)\/$/ ){
			push ( @{ $arch_tree{ $dir }{'dir'} }, $1 );
		} elsif( m/^(\w.+)$/ ) {
			push ( @{ $arch_tree{ $dir }{'file'} }, $1 );
		} elsif( m/^\..+:$/ ) {
			# do nothing
		} else {
			die "problems parsing: \"$_\".\n";
		}
	}
	close( FILE );
	return( \%arch_tree );
}
####################################################################################################
####################################################################################################
sub make_dirs_n_files {
	my ( $infile ) = @_;
	my ( $path );

	open( FILE, ">$infile" ) or die "Cannot open popArch input file: \"$infile\" for writing.\n";
	while ( <FILE> ){
		chomp;
		next if m/^$/;
		next if m/^total\s+\d+/;

		if( m/^\.\/(.*):$/ ){
			$path = $1;
		} else {
			if ( m/^([A-z].+\/)$/ ){
				mkdir "$path/$1";
			} else {
				open ( F, ">$path/$_" ) or die "Cannot open file: \"$path/$_\" for writing.\n";
				close F;
			}
		}
	}
	close( FILE );
}
####################################################################################################
sub find_latest_archives {
	my ( $archive ) = @_;

	my %arch = ();
	foreach ( @$archive ){
		if ( s/(\d+)\.(\d+)$// ){
			if ( 	!defined $arch{$_}{'ver'} or
					$arch{ $_ }{'ver' } < $1 ){				# if a new version
				$arch{ $_ }{'ver' } = $1;					# set version number
				$arch{ $_ }{'part'} = $2;				# if so, save latest part
				if ( m/(.+)\.(\d+)\./ ){
					$arch{ $_ }{'num'} = $2;
					$arch{ $_ }{'prefix'} = $1;
				} else {
					die "Cannot parse archive number from: \"$_\".\n";
				}
			} else {										# if version has not changed
				if ( 	!defined $arch{$_}{'part'} or
						$arch{ $_ }{'part'} < $2 ){			# check if the part hash
					$arch{ $_ }{'part'} = $2;				# if so, save latest part
				}
			}
		}
	}
	return( \%arch );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
