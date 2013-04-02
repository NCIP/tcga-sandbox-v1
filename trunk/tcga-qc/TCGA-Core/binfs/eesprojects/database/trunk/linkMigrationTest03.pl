#! /usr/bin/perl -w
# $Id: linkMigrationTest03.pl 18005 2013-01-07 20:30:45Z snyderee $
# $Revision: 18005 $
# $Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $
####################################################################################################
my $revision = '$Revision: 18005 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "linkMigrationTest02.pl";
our	$VERSION		= "v1.0.0 ($revision)";
our	$start_date		= "Mon Nov 19 16:21:29 EST 2012";
our	$rel_date		= '$Date: 2013-01-07 15:30:45 -0500 (Mon, 07 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

linkMigrationTest02.pl ($Revision: 18005 $)

=head1 SYNOPSIS

linkMigrationTest02.pl [-options]

-head1 OPTIONS

 -C <file>	JSON file containing database connection information
 -c <method>	Database connection method to use (as listed above)
 -l <int>	Limit processing to <int> records
 -o <fname>	File to write debugging output (results of sqlite query)
 -s		Save XML files retrieved from database
 -d		Print debugging information
 -v		Print verbose debugging information

=head1 DESCRIPTION

I<linkMigrationTest01.pl> is specifically designed to test the migration of shared links to archives
into the TCGA v2.0 database per requirements stated in <a href="https://tracker.nci.nih.gov/browse/DCC-81>DCC-81</a>.

=head1 ACCEPTANCE TESTS

=head2 Result a:

In the database, the 'virtual_archive' table is populated with ONE corresponding record.
The timestamp and information populated should correspond to the submitted archive.

=head2 Result b:

The 'file_item' table is populated. The columns 'file_id' and 'shared_link' should have unique values.
-The 'shared_link' value should contain a unique id that is 1-11 characters long, alphanumeric,
and containing both uppercase/lowercase letters. Every file will have a shared_link.

=head2 Result c:

The 'file_item_archive' table is populated, and is linked to the 'file_item' and 'virtual archive' tables.



=head1 SETUP

=head2 Required Modules

In addition to modules available at CPAN, the following in-house modules are required:

    MyUsage
    EESnyder
    migrationDB

All are available from the SVN repositories $binfrepos/eesprojects/modules and database.

=head2 Required Files

The following connection file should be located in $HOME/src/oracle:

    TCGA_connections.json

Both files can be found in the SVN respository $binfrepos/eesprojects/database/trunk/.

=cut
####################################################################################################
#	Testbed:
#	Cmdline:
####################################################################################################
#	History:
#	v1.0.0:		Build generic query program
####################################################################################################
use strict;
use warnings;
use English qw( -no_match_vars );  # Avoids regex performance penalty
use MyUsage;
use EESnyder;
use DBI;
use JSON::XS;
use DBD::Oracle;
use migrationDB;
use XML::Diff;

my $diff = XML::Diff->new();
my $DEFAULT_QUERY_PATH = "$ENV{'HOME'}/src/sql";
my $CONNECTION_PATH = "$ENV{'HOME'}/src/oracle";
my $MAX_DIFF_LENGTH = 80;

my @MON = qw( jan feb mar apr may jun jul aug sep oct nov dec );
for( my $i=0; $i<@MON; $i++){ $MON[$i] = uc $MON[$i];}

my @sFields = qw( file_id disease filename archivename twofilepath );
my %sTests = 	(
		#			'like'	=>	{	'filename'	=>	'%.xml',
		#						},
				);

my @t0 = ( time, (times)[0] );									# start execution timer
our %opts	= ();												# init cmdline arg hash
my $LINK_STRICT = 0;

my %usage 	= (													# init paras for getopts
	'B' => {
		'type'     => "boolean",
		'usage'    => "print program banner to STDOUT",
		'required' => 0,
		'init'     => 1,
	},
	'C' => {
		'type'     => "string",
		'usage'    => "JSON file containing connection information",
		'required' => 1,
		'init'     => "$CONNECTION_PATH/TCGA_connections.json",
	},
	'D' => {
		'type'     => "boolean",
		'usage'    => "print copious debugging information",
		'required' => 0,
		'init'     => 0,
	},
	'L' => {
		'type'     => "boolean",
		'usage'    => "enforce strict shared-link format checking",
		'required' => 0,
		'init'     => 0,
	},
	'M' => {
		'type'     => "boolean",
		'usage'    => "show man page for $pgm_name",
		'required' => 0,
		'init'     => 0,
	},
	'V' => {
		'type'     => "boolean",
		'usage'    => "print version information",
		'required' => 0,
		'init'     => 0,
	},
	'c' => {
		'type'     => "string",
		'usage'    => "Database connection method to use",
		'required' => 1,
		'init'     => 'prototype',
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
	'l' => {
		'type'     => "integer",
		'usage'    => "limit for number of records processed",
		'required' => 0,
		'init'     => 0,
	},
	'o' => {
		'type'     => "string",
		'usage'    => "output file name",
		'required' => 0,
		'init'     => "input",
	},
	'p' => {
		'type'     => "string",
		'usage'    => "query file path",
		'required' => 1,
		'init'     => $DEFAULT_QUERY_PATH,
	},
	'v' => {
		'type'     => "boolean",
		'usage'    => "verbose execution information",
		'required' => 0,
		'init'     => 0,
	},
);

my %dbattrib = (	RaiseError	=>	1,
					AutoCommit	=>	0,
					LongReadLen	=>	1500000,		# longest XML CNTL file 1.19 Mb; longest normal: 298 kb
					LongTruncOk	=>	0,
				);
my $fsep = "\t";

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
$LINK_STRICT = 1 if $opts{'L'};						# make link format checking strict
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
print STDERR $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};
my %uniq = ();										# for testing the uniqueness of IDs
my @uniq_keys = qw ( f.file_id f.shared_link );

my @queries = (
	#"select distinct f.file_id, f.file_name, f.file_location, f.date_file_stored, f.shared_link, a.archive_name, a.shared_link
	#	from file_item f, file_item_archive fa, virtual_archive a
	#	where a.archive_name = ARCHIVE_NAME and a.archive_id = fa.archive_id
	#	and fa.file_id = f.file_id and f.file_name = 'MANIFEST.txt'",
	"select distinct f.file_id, f.file_name, f.file_location, f.date_file_stored, f.shared_link, a.archive_name, a.shared_link
		from file_item f, file_item_archive fa, virtual_archive a
		where a.archive_name = 'ARCHIVENAME' and a.archive_id = fa.archive_id
		and fa.file_id = f.file_id",
	"select archive_name, archive_id, shared_link from virtual_archive where archive_name = 'ARCHIVENAME'",
);
my @args2print = qw( archivename date_comparison disease file_id filename tpf_date f.link_fmtchk f.shared_link a.link_fmtchk a.shared_link );

####################################################################################################
################################## Put Main Between Here ... #######################################
# print "local: " . $migrationDB::local_filedb . "\nremote: " . $migrationDB::remote_filedb . "\n";

# read migration data from SQLite DB into LoH
my $sql				= &build_query	(	\@sFields,						# create query string
										\%sTests
									);
my ( $dLoH, $lkeys )= &queryMigDB	( 	$sql,							# execute query of SQLite DB and populate LoH
										$migrationDB::local_filedb,
										$migrationDB::remote_filedb,
										\@sFields
									);
&print_LoH_as_table( $dLoH, $opts{'o'}, $fsep, $lkeys ) if $opts{'o'};	# print table of retrieved data for debugging purposes
&parse_date_from_twofilepath( $dLoH );
my $archives = &make_unique_list_from_LoH( $dLoH, "archivename" );
my $dHoH = &make_HoH_from_LoH( $dLoH, "archivename", "filename" );

my ( $dbh ) = &tcgaDBconnect( \%dbattrib );								# make the DB connection
my ( @qstrings, @qvals );
for( my $i = 0; $i < @queries; $i++ ){
	$qstrings[$i] = $queries[$i];
	$qvals[$i] = &get_query_return_values( $qstrings[$i] );
	print "query_strings[$i] = \"" . $qstrings[$i] . "\"\n" if $opts{'d'};
}
my $i = 0;
my %rLoH = ();																# LoH for results
foreach my $arch ( @$archives ){											# loop through archivenames from
	my $subquery = "";														# SQLite DB query
	last if ( $opts{'l'} and $i >= $opts{'l'} );
	$subquery = $qstrings[0];
	$subquery =~ s/ARCHIVENAME/$arch/;
	my $sth = $dbh->prepare( $subquery );
	print "subquery: $subquery\n" if $opts{'d'};
	my $sharedLinks = &execute_query( $subquery, $dbh, $sth );
	my $resultsLoH = &parse_query_results( $sharedLinks, $qvals[0] );
	foreach my $h ( @$resultsLoH ){
		my $rHash = $dHoH->{$h->{'a.archive_name'}}{$h->{'f.file_name'}};
		&inspect_results( $rHash, $h, \%uniq );
		unless ( $i ){
			&print_hash_by_line( $rHash, "key", \@args2print );
		}
		&print_hash_by_line( $rHash, "value", \@args2print );
		$i++;
	}



#In the database, the 'virtual_archive' table is populated with ONE corresponding record.
#The timestamp and information populated should correspond to the submitted archive.


#The 'file_item' table is populated. The columns 'file_id' and 'shared_link' should have unique values.
#-The 'shared_link' value should contain a unique id that is 1-11 characters long, alphanumeric,
#and containing both uppercase/lowercase letters. Every file will have a shared_link.


#The 'file_item_archive' table is populated, and is linked to the 'file_item' and 'virtual archive' tables.

}
$dbh->disconnect;

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub print_hash_by_line {
	my ( $h, $choice, $arglist ) = @_;

	my $keylist;
	if( $arglist ){
		$keylist = $arglist;
	} else {
		$keylist = [ sort keys %$h ];
	}

	my $line = "";
	if ( $choice eq "key" ){
		foreach my $key ( @$keylist ){
			$line .= "$key\t";
		}
	} elsif ( $choice eq "value" ){
		foreach my $key ( @$keylist ){
			if ( defined $h->{$key} ){
				$line .= "$h->{$key}\t";
			}
		}
	} else {
		die "Unidentified choice in &print_hash_by_line().\n";
	}
	$line =~ s/\t$/\n/;
	print $line;
}
####################################################################################################
sub parse_date_from_twofilepath{
	my ( $LoH ) = @_;

	my @keys = qw( month day year fid arch_name );
	foreach my $h ( @$LoH ){
		my $path = $h->{'twofilepath'};
		my %info = ();
		if( $path =~ m#/\w+/\w+/(\d+)/(\d+)/(\d+)/(\d+)/(.+)# ){
			$info{'year'}	= $1;
			$info{'month'}	= $2;
			$info{'day'}	= $3;
			$info{'fid'}	= $4;
			$info{'fname'}	= $5;
		} else {
			die "Cannot parse information from twofilepath string: \"$path\".\n";
		}
		if( $info{'fid'} == $h->{'file_id'} ){
			$h->{'tpf_date'} = sprintf("%04d-%02d-%02d", $info{'year'}, $info{'month'}, $info{'day'} );
		} else {
			die "File ID: \"" . $h->{'file_id'} . "\" does not match value from twofilepath: \"" . $info{'fid'} . "\".\n";
		}
	}
}
####################################################################################################
sub substitute_query {
	my ( $qstr, $aHash, $arg ) = @_;

	my $arg2 = uc( $arg );											# substitute uppercase argument
	$qstr =~ s/$arg2/'$aHash->{$arg}'/g;
	print $qstr . "\n" if $opts{'v'};
	return( $qstr );
}
####################################################################################################
sub tcgaDBconnect {
	my ( $dbattrib ) = @_;

	my $connects = &read_json_file( $opts{'C'}, "containing DB connection information" );
	my $c;
	if ( exists $connects->{ $opts{'c'} } ){
		$c = $connects->{ $opts{'c'} };
	} else {
		die "Cannot find \"" . $opts{'c'} . "\" in connection list: " . $opts{'C'} . ".\n";
	}
	$c->{'dsn'} = "dbi:Oracle:" . $c->{'host'} . ":" . $c->{'port'} . "/" . $c->{'dbname'};
	my  $dbh = DBI->connect($c->{'dsn'}, $c->{'user'}, $c->{'passwd'}, $dbattrib );

	print "error status: " . $? . "\n" if ( $? );
	$dbh->{RaiseError} = 1;
	$dbh->func( 100000, 'dbms_output_enable' );

	return( $dbh );
}
####################################################################################################
sub execute_query {
	my ( $query, $dbh, $sth ) = @_;

	my $rv = $sth->execute;
	if ( $? ){
		warn "Execute error status: " . $? . "; function returns: \"$rv\"\n";
	}
	my @records = ();
	while( my @row = $sth->fetchrow_array ){
		push( @records, \@row );
	}
	$sth->finish;
	return( \@records );
}
####################################################################################################
sub parse_query_results {
	my ( $results, $qvals ) = @_;

	my @records = ();
	foreach my $record ( @$results ){
		my %hash = ();
		for( my $i = 0; $i < @$qvals; $i++ ){
			$hash{ $qvals->[$i] } = $record->[$i];
		}
		push( @records, \%hash );
	}
	return( \@records );		# LoH
}
####################################################################################################
sub get_query_return_values{
	my ( $str ) = @_;

	my $select = $str;
#	$select =~ s/^\s*select (distinct|unique)* *(.+) from.+$/$1/s;
	$select =~ s/^select distinct //s;
	$select =~ s/from.+$//s;
	$select =~ s/\s+$//s;
	print "select = \"$select\"\n" if $opts{'d'};
	my ( @vals ) = split( /\s*,\s*/, $select );
	return( \@vals );
}
####################################################################################################
sub	inspect_results{
	my ( $ref, $query, $uniq ) = @_;			# ref = reference data from sqlite DB
												# query = results from query of prototype database
	$ref->{'date_comparison'} = &compare_dates( $ref->{'tpf_date'}, $query->{'f.date_file_stored'}, $ref );
	$ref->{'f.link_fmtchk'} = &check_link_format( $query->{'f.shared_link'} );
	$ref->{'a.link_fmtchk'} = &check_link_format( $query->{'a.shared_link'} );
	$ref->{'a.shared_link'} = $query->{'a.shared_link'};
	$ref->{'f.shared_link'} = $query->{'f.shared_link'};
	$ref->{'f.file_id'} = $query->{'f.file_id'};

	foreach my $key ( @uniq_keys ){
		if ( exists $uniq->{$key}{$ref->{$key}} ){
			print "WARNING: $key value \"" . $ref->{$key} . "\" occurs multiple times\n";
		} else {
			$uniq->{$key}{$ref->{$key}} = 1;
		}
	}



}
####################################################################################################
sub compare_dates {								# ref: 1776-07-04; query: 04-JUL-76
												#		76-JUL-04
	my @date;
	for( my $i = 0; $i < 2; $i++ ){
		@{ $date[$i] } = split( /-/, $_[$i] );
	}
	$date[0][0] =~ s/^\d\d(\d\d)$/$1/;			# extract last 2 digits of reference year
	$date[0][1] = $MON[ $date[0][1] - 1];		# convert 2-digit month to 3-letter abbreviation
	if( $date[0][0] == $date[1][2] and
		$date[0][1] eq $date[1][1] and
		$date[0][2] == $date[1][0] ){
		return( "dates_match" );
	} else {
		return( "DATES_DIFFER" );
	}
}
####################################################################################################
sub check_link_format {

	unless ( defined $_[0] ){
		return( "LINK_UNDEFINED" );
	}
	if ( $_[0] =~ m/null/i ){
		return( "LINK_NULLSTRING" );
	}
	unless( $_[0] ){
		return( "LINK_NULL" );
	}
	my $remark = "";
	if ( $LINK_STRICT ){
		$remark .= "alpha_" if ( $_[0] =~ m/[A-Z]/i );
		$remark .= "num_" if ( $_[0] =~ m/[0-9]/ );
		if ( $_[0] =~ m/[A-Z]/ and $_[0] =~ m/[a-z]/ ){
			$remark .= "mixed-case_";
		} else {
			$remark .= "upper-case_" if ( $_[0] =~ m/[A-Z]/ );
			$remark .= "lower-case_" if ( $_[0] =~ m/[a-z]/ );
		}
		$remark .= length( $_[0] );
		$remark .= "char";

		if ( $remark ne "alpha_num_mixed-case_11char" ){
			$remark = uc $remark;
		}
	} else {
		my $len = length $_[0];
		if  ( 	(	$len < 12 	) and
				(	$len > 8    ) and
				(	$_[0] =~ m/^[A-z0-9]+$/ )
			){
			$remark = "link_ok";
		} else {
			$remark = "BAD_LINK";
		}
	}
	return( $remark );
}
####################################################################################################
sub process_results {

}
####################################################################################################
sub make_unique_list_from_LoH{
	my ( $LoH, $field_name ) = @_;

	my %field_hash = ();
	foreach my $h ( @$LoH ){
		if ( exists $h->{$field_name} ){
			$field_hash{ $h->{$field_name} } = 1;
		} else {
			die "ERROR: \"$field_name\" is not a valid key for LoH in make_unique_list_from_LoH().\n";
		}
	}
	my @field_list = ( sort keys %field_hash );
	return( \@field_list );
}
####################################################################################################
sub make_HoH_from_LoH {
	my ( $LoH, @keys ) = @_;

	my %HoH = ();
	foreach my $sh ( @$LoH ){
		$HoH{ $sh->{$keys[0]} }{ $sh->{$keys[1]} } = $sh;
	}
	return( \%HoH );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
