#!/bin/perl -w
####################################################################################################
# $Id: migrationDB.pm 18005 2013-01-07 20:30:45Z snyderee $
# $Revision: 18005 $
# $Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $
####################################################################################################
package migrationDB;
require Exporter;
our @ISA = qw(Exporter);
our %opts;
our @EXPORT = qw(	check_SQLite_DB build_query queryMigDB );

my $revision = '$Revision: 18005 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "migrationDB.pm";
our	$VERSION		= "v1.0.0 ($revision)";
our	$start_date		= "Fri Nov 02 11:58:29 EDT 2012";
our	$rel_date		= '$Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $';
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
use strict;
use warnings;
use MyUsage;
use EESnyder;
use DBI;
use DBD::SQLite;
use File::Copy;

our $remote_filedb;
our $local_filedb;
if ( $ENV{'HOST'} =~ m/phantomii/ ){
	$remote_filedb	= 'snyderee@ncias-p802:/h1/arik/migration/migration_db';
	$local_filedb	= '/home/eesnyder/projects/nih/TCGA/DB/SQLite/migration_db';
} else {
	$remote_filedb  = '/h1/arik/migration/migration_db';
	$local_filedb   = $remote_filedb;
}
####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub check_SQLite_DB {
	my ( $local, $remote ) = @_;

	if ( $opts{'u'} ){
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
	} else {
		unless ( -e $local && -s $local ){
			die "SQLite DB file: \"$local\" does not exist or is of zero size.\n" .
				"Try running program with the \"-u\" option to update the database file.\n";
		}
	}
	my $dbh = DBI->connect("dbi:SQLite:dbname=$local");
	if ( $dbh->err ){
		warn "Database call returned warning: \"" . $dbh->errstr . "\".\n";
	}
	return ( $dbh );
}
####################################################################################################
sub read_query {
	my ( $fname ) = @_;

	open( FILE, $fname ) or die "Cannot open query file: \"$fname\" for reading.\n";
	my $query_str = "";
	while( <FILE> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		$query_str .= "$_\n";
	}
	close( FILE );
	return( $query_str );
}
####################################################################################################
sub build_query {
	my ( $fields, $tests ) = @_;

	my $sql = 'select ' . join( ", ", @$fields ) . ' from migrated_files ';
	$sql .= "where " if %$tests;
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

	print "sql = $sql\n" if $opts{'v'};
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

	my ( $local, $remote );
	if ( $ENV{'HOST'} =~ m/phantomii/ ){
		$remote	= 'snyderee@ncias-p802:/h1/arik/migration/migration_db';
		$local	= '/home/eesnyder/projects/nih/TCGA/DB/SQLite/migration_db';
	} else {
		$remote  = '/h1/arik/migration/migration_db';
		$local   = $remote_filedb;
	}

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
	print "SQLite database ($local) successfully updated.\n";
	exit( 0 );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
1;
