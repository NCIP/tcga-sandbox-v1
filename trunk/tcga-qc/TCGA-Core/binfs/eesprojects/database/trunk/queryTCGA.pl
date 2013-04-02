#! /usr/bin/perl -w
# $Id: queryTCGA.pl 17904 2012-11-21 21:13:46Z snyderee $
# $Revision: 17904 $
# $Date: 2012-11-21 16:13:46 -0500 (Wed, 21 Nov 2012) $
####################################################################################################
my $revision = '$Revision: 17904 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "queryTCGA.pl";
our	$VERSION		= "v1.0.0 ($revision)";
our	$start_date		= "Mon Oct 22 18:04:39 EDT 2012";
our	$rel_date		= '$Date: 2012-11-21 16:13:46 -0500 (Wed, 21 Nov 2012) $';
####################################################################################################
#	Eric E. Snyder (c) 2012
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

queryTCGA.pl

=head1 SYNOPSIS

queryTCGA.pl [-options] [argfiles | substitution values]
				-C <file>			JSON file containing database connection information
				-c <method>			Database connection method to use (as listed above)
				-o <string>			Root name for output file(s)
				-q <file>			File containing an SQL query
				-s <substitutions>	a comma-delimited list of strings that will be substituted by
									command-line arguments

=head1 EXAMPLES
	queryTCGA.pl -q sampleType.sql
	queryTCGA.pl -q retrieveClinicalXMLbyFileID.sql <file_id list>
	queryTCGA.pl -q sampleTypeCode.sql -s SAMPLE_TYPE_CODE 01 02 10
	queryTCGA.pl -q retrieveGenericXMLbyFileID.sql -s GENERIC,FILE_ID biospecimen 769545 clinical 873800 biospecimen 869851

=head1 DESCRIPTION

I<queryTCGA.pl> is a fairly general purpose tool for querying an Oracle database using SQL.

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
use MyUsage;
use EESnyder;
use JSON::XS;
use DBI;
use migrationDB;
use English;

my $DEFAULT_QUERY_PATH = "/home/eesnyder/src/sql/";
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
		'init'     => '/home/eesnyder/src/oracle/TCGA_connections.json',
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
	'c' => {
		'type'     => "string",
		'usage'    => "Database connection method to use",
		'required' => 1,
#		'init'     => 'dev2_commonQA',
		'init'     => 'prod_commonread',
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
		'usage'    => "output file",
		'required' => 0,
		'init'     => "",
	},
	'q' => {
		'type'     => "string",
		'usage'    => "query file (default path = $DEFAULT_QUERY_PATH)",
		'required' => 1,
		'init'     => "retrieveClinicalXMLbyFileID.sql",
	},
	's' => {
		'type'     => "string",
		'usage'    => "substitute values on command-line in SQL expression",
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

my %dbattrib = (	RaiseError	=>	1,
					AutoCommit	=>	0,
					LongReadLen	=>	1500000,		# longest XML CNTL file 1.19 Mb; longest normal: 298 kb
					LongTruncOk	=>	0,
				);

my @infiles = qw(  );
my $banner = &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT
print STDERR $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

my $qfile = "";										# name of file containing SQL query
if ( $opts{'q'} =~ m/^\w+/ ){
	$qfile = $DEFAULT_QUERY_PATH . $opts{'q'};		# if -q starts w/letter, assume path should be prepended
} elsif(	$opts{'q'} =~ m/^\./ or					# if -q starts with '.'
			$opts{'q'} =~ m/\// ){					# or contains a forward slash
	$qfile = $opts{'q'};							# assume user is supplying path
} else {
	print "opts{'q'} = " . $opts{'q'} . ", strange, but let\'s try it.\n";
}

####################################################################################################
################################## Put Main Between Here ... #######################################
my ( $dbh ) = &tcgaDBconnect( \%dbattrib );

my $query = &read_query( $qfile );
if ( @ARGV == 0 ){											# with no cmdline args
	my $sth = $dbh->prepare( $query );
	&execute_query( $query, $dbh, $sth );
	$sth->finish;
	$dbh->disconnect;
	exit;
}
if ( $opts{'s'} ){
	my @subs = split( /,/, $opts{'s'} );
	while( @ARGV ){
		my @vals = splice( @ARGV, 0, @subs );
		my $subquery = $query;
		for( my $i = 0; $i < @subs; $i++ ){
			$subquery =~ s/$subs[$i]/$vals[$i]/g;
		}
		my $sth = $dbh->prepare( $subquery );
		&execute_query( $subquery, $dbh, $sth );
	}

} else {
	foreach my $argfile ( @ARGV ){
		my ( $argsLoH, $headings ) = &read_table_in2_LoH_fromfile( $argfile );
		my $output = "";
		foreach my $argHash ( @$argsLoH ){
			my $subquery = &substitute_query( $query, $argHash );
			my $sth = $dbh->prepare( $subquery );
			&execute_query( $subquery, $dbh, $sth );
		}
	}
}
$dbh->disconnect;

##################################      ... and Here         ########################################
####################################################################################################

print STDERR "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################
sub read_json_specs {
	my ( $specs ) = @_;

	if ( not -f $specs or -z $specs ){									# if not a regular file or it's empty...
		die "ERROR: Cannot open file: \"$specs\" containing DB connection information.\n";
	} else {
		print "Using file: \"$specs\" for expected file data-field information.\n" if $opts{'v'};
	}
	$RS = undef;
	open( FILE, "$specs" ) or die "Fatal Error: Cannot open fileSpecs file: \"$specs\".\n";
	my $connectionSpecs = decode_json( <FILE> );
	$RS = "\n";
	close( FILE );
	return( $connectionSpecs );
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
	return( $query_str );
}
####################################################################################################
sub substitute_query {
	my ( $qstr, $aHash ) = @_;

	foreach my $arg ( keys %$aHash ){
		$qstr =~ s/$arg/$aHash->{$arg}/g;
	}
	print $qstr . "\n" if $opts{'v'};
	return( $qstr );
}
####################################################################################################
sub tcgaDBconnect {
	my ( $dbattrib ) = @_;

	my $connects = &read_json_specs( $opts{'C'} );
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
	my $output = "";
	while( my @row = $sth->fetchrow_array ){
		my $outrow = '';
		for(my $i = 0; $i < @row; $i++ ){
			if( defined $row[$i] ){
				if ( ref( $row[$i] ) eq "ARRAY" ){
					$outrow .= join("\t", @{$row[$i]} );
					print scalar @{$row[$i]} . "\n";
				} elsif ( ref( $row[$i] ) eq "HASH" ){
					foreach my $key ( keys %{$row[$i]} ){
						$outrow .= "$key:" . $row[$i]->{ $key } . "\t";
					}
				} elsif( ref( $row[$i] ) eq "" ){
					$outrow .= $row[$i];
				} else {
					warn "Cannot determine reference type for: \"" . $row[$i] ."\".\n";
				}
			}
			$outrow .= "\t" unless ($i == (@row - 1));
		}
		$output .= "$outrow\n";
	}
	if ( $opts{'o'} ){
		my $outfile = $opts{'o'};
		open( FILE, ">$outfile" ) or die "Cannot open: \"$outfile\" for writing.\n";
		print FILE $output;
		close ( FILE );
	} else {
		print $output;
	}
	$sth->finish;
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
