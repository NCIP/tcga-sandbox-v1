#! /usr/bin/perl -w
# $Id: xmlMigrationTest01.pl 17912 2012-11-27 01:31:37Z snyderee $
# $Revision: 17912 $
# $Date: 2012-11-26 20:31:37 -0500 (Mon, 26 Nov 2012) $
####################################################################################################
my $revision = '$Revision: 17912 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "xmlMigrationTest01.pl";
our	$VERSION		= "v1.0.0 ($revision)";
our	$start_date		= "Mon Nov  5 17:38:37 EST 2012";
our	$rel_date		= '$Date: 2012-11-26 20:31:37 -0500 (Mon, 26 Nov 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

xmlMigrationTest01.pl ($Revision: 17912 $)

=head1 SYNOPSIS

xmlMigrationTest01.pl [-options]

-head1 OPTIONS

 -C <file>	JSON file containing database connection information
 -c <method>	Database connection method to use (as listed above)
 -l <int>	Limit processing to <int> records
 -o <path>	Default location for query files
 -q <file>	File containing an SQL query for TCGA database
 -s		Save XML files retrieved from database
 -d		Print debugging information
 -v		Print verbose debugging information

=head1 DESCRIPTION

I<xmlMigrationTest01.pl> is specifically designed to test the migration of XML data into the TCGA v2.0
database per requirements stated in <a href="https://tracker.nci.nih.gov/browse/DCC-204>DCC-204</a>.
The program first retrieves a list of migrated XML files and identifiers from Ari Kahn's SQLite
database.  It then works through the list of XML files, comparing each one to the version stored
in the Oracle database using XML::Diff.  If the two XML strings contain equivalent data, even if
they differ in data order or use of whitespace or formatting, the program will report a difference
of "none".  The data is written to STDOUT in the following format (tabs added, filenames abbreviated
for clarity):

 disease	file_id	fileXMLname				fileXMLsize	dbXMLname			dbXMLsize	XMLdiff	fileXMLsize-dbXMLsize
 BRCA		617414	/repos/nch.org_clin.TCGA-C8-A1HI.xml	25365		nch.org_clin.TCGA-C8-A1HI.xml	24811		none	554
 BRCA		617681	/repos/nch.org_clin.TCGA-EW-A1OX.xml	25784		nch.org_clin.TCGA-EW-A1OX.xml	25225		none	559
 BRCA		617686	/repos/nch.org_clin.TCGA-EW-A1P4.xml	25612		nch.org_clin.TCGA-EW-A1P4.xml	25053		none	559

=head1 SETUP

=head2 Required Modules

In addition to modules available at CPAN, the following in-house modules are required:

    MyUsage
    EESnyder
    migrationDB

All are available from the SVN repositories $binfrepos/eesprojects/modules and database.

=head2 Required Files

The following query file should be located in $HOME/src/sql or in a location specified using the
-p option:

    retrieveGenericXMLbyFileID.sql

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
use Tie::IxHash;

my $diff = XML::Diff->new();
my $DEFAULT_QUERY_PATH = "$ENV{'HOME'}/src/sql";
my $CONNECTION_PATH = "$ENV{'HOME'}/src/oracle";
my $MAX_DIFF_LENGTH = 80;
my $DIFF_TARGET = '<?xml version="1.0"?>
<xvcs:diffgram xmlns:xvcs="http://www.xvcs.org/"/>
';
my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

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
#		'init'     => 'dev2_commonQA',
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
		'usage'    => "output file for debugging use",
		'required' => 0,
		'init'     => 0,
	},
	'p' => {
		'type'     => "string",
		'usage'    => "query file path",
		'required' => 1,
		'init'     => $DEFAULT_QUERY_PATH,
	},
	'q' => {
		'type'     => "string",
		'usage'    => "query file (default path = $DEFAULT_QUERY_PATH)",
		'required' => 1,
		'init'     => "retrieveGenericXMLbyFileID.sql",
	},
	's' => {
		'type'     => "boolean",
		'usage'    => "save XML files retrieved from database",
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

my %querySub = 	(	clinical	=>	"clinical_xml",
					biospecimen	=>	"biospecimen_xml",
					auxilliary	=>	"other_xml",
				);

my %dbattrib = (	RaiseError	=>	1,
					AutoCommit	=>	0,
					LongReadLen	=>	1500000,		# longest XML CNTL file 1.19 Mb; longest normal: 298 kb
					LongTruncOk	=>	0,
				);

my @sFields	= qw( file_id disease filename twofilepath );
my %sTests	=   (   'like'  =>  {   'filename'  =>  '%.xml',
                                },
                    #'>'        =>  {   'filesize'  =>  100000,
                    #           },
                );
my %keys2headings;
# my $tie = tie( %keys2headings, Tie::IxHash,
my $keys2headings = Tie::IxHash->new(
	disease			=> "disease",
	file_id 		=> "file_id",
	twofilepath		=> "fileXMLname",
	fileXMLsize		=> "fileXMLsize",
	filename		=> "dbXMLname",
	dbXMLsize		=> "dbXMLsize",
	diff			=> "diff",
);

my $fsep = "\t";
my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
if ( $opts{'d'} and ! $opts{'o'} ){
	die "If using -d option, you must supply an output file name with the -o option.\n";
}
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
print STDERR $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

my $qfile = "";										# name of file containing SQL query
if ( $opts{'q'} =~ m/^\w+/ ){
	$qfile = "$DEFAULT_QUERY_PATH/" . $opts{'q'};	# if -q starts w/letter, assume path should be prepended
} elsif( $opts{'q'} =~ m/[\/\.]/ ){
	$qfile = $opts{'q'};							# if -q starts with "." or "/", assume user is supplying path
} else {
	print "opts{'q'} = " . $opts{'q'} . ", strange, but let\'s try it.\n";
}

####################################################################################################
################################## Put Main Between Here ... #######################################
# print "local: " . $migrationDB::local_filedb . "\nremote: " . $migrationDB::remote_filedb . "\n";

# read migration data from SQLite DB into LoH
my $sql				= &build_query	(	\@sFields,			# create query string
										\%sTests
									);
my ( $dLoH, $keys )	= &queryMigDB	( 	$sql,							# execute query of SQLite DB and populate LoH
										$migrationDB::local_filedb,
										$migrationDB::remote_filedb,
										\@sFields
									);
&print_LoH_as_table( $dLoH, $opts{'o'}, $fsep, $keys ) if $opts{'d'};	# print table of retrieved data for debugging purposes

# prepare to query TCGA database containing XML documents
my ( $dbh ) = &tcgaDBconnect( \%dbattrib );								# make the DB connection
my $query = &read_query( $qfile );										# read the query from query file
my %qstring = ();
foreach my $qtype ( keys %querySub ){									# define query string based on type of XML doc:
	$qstring{ $qtype } = $query;										# biospecimen, clinical, etc.
	$qstring{ $qtype } =~ s/GENERIC_XML/$querySub{$qtype}/g;
	print "query_string{ $qtype } = \"" . $qstring{$qtype} . "\"\n" if $opts{'d'};
}
my $i = -1;
foreach my $argHash ( @$dLoH ){											# loop through list of docs/db records defined
	my $subquery = "";													# by SQLite DB query
	last if ( $opts{'l'} and ( $i >= ($opts{'l'}-1) ) );
	$i++;
	my $dbXML;
	foreach my $qtype ( keys %querySub ){
		$subquery = &substitute_query( $qstring{ $qtype }, $argHash, "file_id" );
		if( $argHash->{'filename'} =~ m/$qtype/ ){
			print "subquery: $subquery\n" if $opts{'d'};
			my $sth = $dbh->prepare( $subquery );
			$dbXML = &execute_query( $subquery, $dbh, $sth, 0 );
		} else {
			my $sth = $dbh->prepare( $subquery );
			my $ectopic_dbXML = "";
			$ectopic_dbXML = &execute_query( $subquery, $dbh, $sth, 0 );
			if( $ectopic_dbXML ){
				print "Unexpected result from query:\n$subquery\nfile_id: " .
						$argHash->{'file_id'} . "not expected in database table: \"" . $querySub{$qtype} . "\".\n";
				my $ectofname = $argHash->{'file_id'} . "_$qtype.xml";
				&print_string2file( $ectopic_dbXML, $ectofname );
			}
		}
	}
	my $note = "";
	if ( $dbXML ){
		$argHash->{'dbXMLsize'} = length $dbXML;
	} else {
		$argHash->{'dbXMLsize'} = -1;
		$note .= "Cannot load XML from database";
	}

	my ( $fileXML ) = &read_file_in2_string( $argHash->{'twofilepath'} );
	if ( $fileXML ){
		$argHash->{'fileXMLsize'} = length( $fileXML );
	} else {
		$argHash->{'fileXMLsize'} = "-1";
		if ( $note ){
			$note .= " OR file";
		} else {
			$note .= "Cannot load XML from file";
		}
	}

	if ( $note ){
		$argHash->{'note'} = $note;
		&print_hash_by_line( $argHash, "data", $keys2headings );
		next;
	}
	my $diffgram = $diff->compare	(	-old => $fileXML,
										-new => $dbXML,
									);
	if ( $diffgram eq $DIFF_TARGET ){									# if file and dbXML are identical,
		$argHash->{'diff'} = "none";									# set the diff message to "none".
	} else {															# if NOT identical,
		$argHash->{'diff'} = $argHash->{'file_id'} . "_diff.xml";		# set msg to name of file containing XMLdiff
		my $xfname = $argHash->{'file_id'} . ".xml";
		&print_string2file( $dbXML, $xfname );							# save dbXML as file
	}
	unless ( $i ){
		&print_hash_by_line( $argHash, "headings", $keys2headings );
	}
	&print_hash_by_line( $argHash, "data", $keys2headings );
}
$dbh->disconnect;

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub print_hash_by_line {
	my ( $h, $choice, $k2h ) = @_;

		my $line = "";
		if ( $choice eq "headings" ){
			foreach my $columnHeading ( $k2h->Values ){
				$line .= "$columnHeading\t";
			}
		} elsif ( $choice eq "data" ){
			foreach my $key ( $k2h->Keys ){
				$line .= "$h->{$key}\t";
			}
		} else {
			die "Unidentified choice in &print_hash_by_line(): \"$choice\".\n";
		}
		$line =~ s/\t$/\n/;
		print $line;
}
####################################################################################################
####################################################################################################
sub read_query {
	my ( $fname ) = @_;

	open( FILE, $fname ) or die "Cannot open query file: \"$fname\" for reading.\n";
	my $query_str = "";
	while( <FILE> ){
		chomp;						# remove "\n"
		next if /^--/;				# SQL comment delimiter
		next if /^\s*$/;			# blank line
		$query_str .= "$_\n";
	}
	close( FILE );
	return( $query_str );
}
####################################################################################################
sub substitute_query {
	my ( $qstr, $aHash, $arg ) = @_;

	my $arg2 = uc( $arg );											# substitute uppercase argument
	$qstr =~ s/$arg2/$aHash->{$arg}/g;
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
	my ( $query, $dbh, $sth, $createFile ) = @_;

	my $rv = $sth->execute;
	if ( $? ){
		warn "Execute error status: " . $? . "; function returns: \"$rv\"\n";
	}
	my $outfile;
	my ( $fid, $xml );
	while( my @row = $sth->fetchrow_array ){
		( $fid, $xml ) = @row;
		if ( $createFile ){
			$outfile = "$fid.xml";
			open( FILE, ">$outfile" ) or die "Cannot open: \"$outfile\" for writing.\n";
			print FILE $xml;
			close ( FILE );
		}

	}
	$sth->finish;
	return( $xml );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
