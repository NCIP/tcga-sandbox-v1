#! /usr/bin/perl -w
####################################################################################################
# $Id: migrationDB.pl 18005 2013-01-07 20:30:45Z snyderee $
# $Revision: 18005 $
# $Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $
####################################################################################################
my $revision = '$Revision: 18005 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= ".pl";
our	$VERSION		= "v1.0.0 ($revision)";
our	$start_date		= "Fri Nov 02 11:58:29 EDT 2012";
our	$rel_date		= '$Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $';
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
####################################################################################################
#	Eric E. Snyder (c) 2012
#	National Cancer Institute [C] SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

migrationDB.pl

=head1 SYNOPSIS

migrationDB.pl [-u]

=head1 USAGE

The program migrationDB.pl builds a query from @sFields and @sTests and executes it on the SQLite
database file.  The program uses a local copy of the database which can be updated from ncias-p802
using the "-u" option.

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
use DBI;
use DBD::SQLite;
use File::Copy;

my $fsep = "\t";												# field separator
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
	'o' => {
		'type'     => "string",
		'usage'    => "output file name",
		'required' => 0,
		'init'     => "",
	},
	'u' => {
		'type'     => "boolean",
		'usage'    => "update SQLite database from image on server",
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

my @sFields = qw( file_id disease filename filesize twofilepath );
my %sTests = 	(	'like'	=>	{	'filename'	=>	'%.xml',
								},
					#'>'		=>	{	'filesize'	=>	100000,
					#			},
				);

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my $remote_filedb	= 'snyderee@ncias-p802:/h1/arik/migration/migration_db';
my $local_filedb	= '/home/eesnyder/projects/nih/TCGA/DB/SQLite/migration_db';
&update_SQLite_DB( $local_filedb, $remote_filedb ) if $opts{'u'};
my $sql				= &build_query( \@sFields, \%sTests );
my ( $dLoH, $keys )	= &queryMigDB( $sql, $local_filedb, $remote_filedb, \@sFields );
&print_LoH_as_table( $dLoH, $opts{'o'}, $fsep, $keys );

##################################      ... and Here         #######################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub check_SQLite_DB {
	my ( $local, $remote ) = @_;

	unless ( -e $local && -s $local ){
		die "SQLite DB file: \"$local\" does not exist or is of zero size.\n" .
			"Try running program with the \"-u\" option first to update the database file.\n";
	}
	my $dbh = DBI->connect("dbi:SQLite:dbname=$local");
	if ( $dbh->err ){
		warn "Database call returned warning: \"" . $dbh->errstr . "\".\n";
	}
	return ( $dbh );
}
####################################################################################################
sub build_query {
	my ( $fields, $tests ) = @_;

	my $sql = 'select ' . join( ", ", @$fields ) . ' from migrated_files where ';
	foreach my $comp ( keys %$tests ){
		while ( my ( $key, $val ) = each %{$tests->{$comp}} ) {
			if( $comp eq "like" ){
				$sql .= "$key $comp \"$val\"";
			} else {
				$sql .= "$key $comp $val";
			}
			$sql .= " and ";
		}
	}
	$sql =~ s/and +$//;
	print "query = \"$sql\"\n" if $opts{'v'};
	return( $sql );
}
####################################################################################################
sub queryMigDB {
	my ( $sql, $local_filedb, $remote_filedb, $sFields ) = @_;

	my $dbh = &check_SQLite_DB( $local_filedb, $remote_filedb );
	my $sth = $dbh->prepare( $sql );
	$sth->execute;
	my @LoH = ();
	while ( my $row = $sth->fetch ) {
		my %hash = ();
		for( my $i = 0; $i < @{$sFields}; $i++ ){
			$hash{ $sFields->[$i] } = $row->[$i]
		}
		push( @LoH, \%hash );
		if ( $opts{'v'} ){
			my $row_string = join( "\t", @{$row} );
			print "$row_string\n";
		}
	}
	return( \@LoH, $sFields );
}
####################################################################################################
sub update_SQLite_DB {
	my ( $local, $remote ) = @_;

	if ( -e $local ){
		unlink( "$local.bak") if ( -e "$local.bak" );
		move( $local, "$local.bak" ) if ( -e $local );
	}
	`scp $remote $local`;
	if ( -e $local && -s $local ){							# file exists and is of non-zero size
		unlink( "$local.bak") if ( -e "$local.bak" );
	} else{
		die "Failed to copy SQLite database: \"$remote\" to \"$local\".\n";
	}
	exit( 0 );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
