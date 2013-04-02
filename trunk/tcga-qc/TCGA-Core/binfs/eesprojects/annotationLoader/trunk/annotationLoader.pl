#! /usr/bin/perl -w
# $Id: annotationLoader.pl 18014 2013-01-11 21:30:07Z snyderee $
####################################################################################################
my $revision = '$Revision: 18014 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our	$pgm_name		= "annotationLoader.pl";
our	$VERSION		= "v1.0.8 ($revision)";
our	$start_date		= "Mon Nov 28 14:15:30 EST 2011";
our $rel_date 		= '$Date: 2013-01-11 16:30:07 -0500 (Fri, 11 Jan 2013) $';
####################################################################################################
#	Eric E. Snyder (c) 2011
#	HHS/NIH/NCI|NHGRI TCGA/DCC [C]
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

annotationLoader.pl

=head1 SYNOPSIS

annotationLoader.pl -P -s 1 annotation_list.[csv|tsv|txt|xls]

=head1 OPTIONS

 -C           check file format only (default = "FALSE")
 -H <string>  provide csv list of headings for query output (-I for list of headings) (default = "0")
 -I           show list of valid query output headings/fields (default = "FALSE")
 -M           print program's "man" page (default = "FALSE")
 -P           use PRODUCTION server (instead of "dev") (default = "FALSE")
 -Q           use QA server (instead of "dev") (default = "FALSE")
 -R           try to convert user-supplied headings into working headings (default = "TRUE")
 -U           print URL before every web service call (default = "FALSE")
 -V           print version information (default = "FALSE")
 -e           search for exact string (default = "TRUE")
 -f <string>  text file (.[csv|tsv|txt]) field separator (default = "\t")
 -h           print "help" information (default = "FALSE")
 -k           print query results as key-value pairs (default = "FALSE")
 -p           print pretty json output (default = "FALSE")
 -q           query for items instead of loading them (default = "FALSE")
 -s <integer> number of seconds to sleep between web service calls (default = 0)

=head1 USAGE

I<annotationLoader.pl> is used to load/query TCGA biospecimen annotations into/from a DCC database
(development (by default), production (-P) or QA (-Q)).  The -q option changes the default dehavior
from "load" to "query".  Annotations are supplied to the program as a single Excel spreadsheet with
.xls extension or as a text file (with .txt, .tsv or .csv extension).  The actual delimiter for
text files is specified by the -f option, not the file extension, with the <tab> character as
default.  The input file must use the following five headings:

_B<Heading>             B<Content>
 item                biospecimen barcode
 itemType            biospecimen type (patient, sample, portion, shipped_portion, analyte, aliquot)
 study               TCGA study code (BRCA, GBM, LAML, OV, etc.)
 annotationCategory  see https://wiki.nci.nih.gov/x/_pT9AQ for controlled vocabulary
 annotationNote      free text field

Additional columns with other headings will be ignored.  However, every line must have the same
number of fields. The program has a limited ability to cope with mis-formatted or mis-spelled
column headings, ectopic whitespace and other file format problems-- but don't rely on it.

=cut
####################################################################################################
#	Testbed:
#	Cmdline:
####################################################################################################
#	History:
#	v1.0.1:
####################################################################################################
use strict;
use warnings;
use MyUsage;
use EESnyder;
use TCGA;
use Spreadsheet::ParseExcel::Simple;
use LWP;
use HTTP::Request;
use HTML::Form;
use List::Compare;
use Module::Build;
use Term::ReadKey;
use Config;
use JSON::XS;
use String::HexConvert ':all';
use Pod::Usage;

my @t0 = ( time, (times)[0] );									# start execution timer
my %opts	= ();												# init cmdline arg hash

my $ua = LWP::UserAgent->new;
$ua->timeout( 60 );
$ua->agent( $pgm_name . "/$VERSION" );
$ua->from( $ENV{'USER'} . "@" . $ENV{'HOST'} );
$ua->show_progress( 1 );

my $jcoder = JSON::XS->new->allow_nonref;

my $jheadings = [ qw( 	item itemType study annotationCategory annotationClassification noteText
						createdBy dateCreated approved status rescinded
					)
				];


my $PROTOCOL	 = "https";												# secure server
my $DEV_HOST	 = "tcga-data-dev.nci.nih.gov";							# webservice host, developer server
my $QA_HOST		 = "tcga-data-qa.nci.nih.gov";							# webservice host, QA server
my $PROD_HOST	 = "tcga-data.nci.nih.gov";								# webservice host, production server
my $PORT		 = "80";												# and port
my $REALM		 = "TCGA";
my $BIG_PARSE_FAILURE_WARNING = 1;										# if 0, dont print whole _content on error

my %usage 	= (															# init paras for getopts
	'B' => {
		'type'     => "boolean",
		'usage'    => "print program banner to STDOUT",
		'required' => 0,
		'init'     => 1,
	},
	'C' => {
		'type'     => "boolean",
		'usage'    => "check file format only",
		'required' => 0,
		'init'     => 0,
	},
	'D' => {
		'type'     => "boolean",
		'usage'    => "print copious debugging information",
		'required' => 0,
		'init'     => 0,
	},
	'H' => {
		'type'     => "string",
		'usage'    => "provide csv list of headings for query output (-I for list of headings)",
		'required' => 0,
		'init'     => 0,
	},
	'I' => {
		'type'     => "boolean",
		'usage'    => "show list of valid query output headings/fields",
		'required' => 0,
		'init'     => 0,
	},
	'M' => {
		'type'     => "boolean",
		'usage'    => "print program\'s \"man\" page",
		'required' => 0,
		'init'     => 0,
	},
	'P' => {
		'type'     => "boolean",
		'usage'    => "use PRODUCTION server (instead of \"dev\")",
		'required' => 0,
		'init'     => 0,
	},
	'Q' => {
		'type'     => "boolean",
		'usage'    => "use QA server (instead of \"dev\")",
		'required' => 0,
		'init'     => 0,
	},
	'R' => {
		'type'     => "boolean",
		'usage'    => "convert user-supplied headings into working headings",
		'required' => 0,
		'init'     => 1,
	},
	'U' => {
		'type'     => "boolean",
		'usage'    => "print URL before every web service call",
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
		'type'     => "boolean",
		'usage'    => "search for exact string",
		'required' => 0,
		'init'     => 1,
	},
	'f' => {
		'type'     => "string",
		'usage'    => "CSV field separator",
		'required' => 0,
		'init'     => '\t',
	},
	'h' => {
		'type'     => "boolean",
		'usage'    => "print \"help\" information",
		'required' => 0,
		'init'     => 0,
	},
	'k' => {
		'type'     => "boolean",
		'usage'    => "print query results as key-value pairs",
		'required' => 0,
		'init'     => 0,
	},
	'p' => {
		'type'     => "boolean",
		'usage'    => "print pretty json output",
		'required' => 0,
		'init'     => 0,
	},
	'q' => {
		'type'     => "boolean",
		'usage'    => "query for items instead of loading them",
		'required' => 0,
		'init'     => 0,
	},
	's' => {
		'type'     => "integer",
		'usage'    => "number of seconds to sleep between web service calls",
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
my @put_headings	= qw( disease itemType item annotationCategory annotationNote );
my @get_headings	= qw( item itemExact categoryId itemTypeId keyword annotationId annotatorUsername );
my @pnote_headings	= qw( dccAnnotationId noteTxt );

my %regex_headings	=	(	'cancer *type'		=> 'disease',
							'study'				=> 'disease',
							'tcga.*'			=> 'item',
							'item'				=> 'item',
							'item *type.*'		=> 'itemType',
							'anno\w+ *cat.+'	=> 'annotationCategory',
							'anno\w+ *notes*'	=> 'annotationNote',
						);

my %ws =	(	'get'		=>	{	'cmd'	=>	'searchannotations',
									'prm'	=>	\@get_headings,
								},
				'put'		=>	{	'cmd'	=>	'addannotation',
									'prm'	=>	\@put_headings,
								},
				'put_note'	=>	{	'cmd'	=>	'addannotationnote',
									'prm'	=>	\@pnote_headings,
								},
			);
my $rtn_method = "json?";

my %error_conditions	=	(	'nonunique'	=>	'Error while adding new annotation: The following annotation is not unique:" . "
												.*item identifier:(TCGA-[A-Za-z0-9]+)',
							);

my @infiles	= qw( annotations.xls );				# example input files
my $hline	= "-" x 80 . "\n";						# horizontal line
my $banner	= &Usage( \%usage, \%opts, \@infiles );	# read cmdline parameters
my $fsep;											# CSV input field separator
if ( $opts{'f'} ){
	$fsep = $opts{'f'};
}
if ( $opts{'H'} ){
	$jheadings = [ split( /,/, $opts{'H'} ) ];
}
if ( $opts{'I'} ){
	print "Valid query output headings:\n" .
			join( ",", @$jheadings ) . "\n\n";
	exit( 0 );
}
print $banner unless $opts{'B'};    				# print program banner w/parameters to STDOUT

unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
	warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
#									YYYY   MM    DD      HH:MM:SS   TZ          Day            Month
#                                    $1    $2    $3         $4      $5          $6              $7

# determine which server to use
my $HOST = $DEV_HOST;								# set default server
$HOST = $PROD_HOST if $opts{'P'};					# change to production, if requested with -P
$HOST = $QA_HOST if $opts{'Q'};						# change to QA, if requested with -Q
my $ws_login_url	= "$PROTOCOL://$HOST/annotations/webservice_login";
my $base_url 		= "$PROTOCOL://$HOST/annotations/resources";

print $pgm_name ."_$VERSION\n" .
	"Start date:	$start_date\n" .
	"End date:	$rel_date\n\n" if $opts{'V'};

####################################################################################################
################################## Put Main Between Here ... #######################################
my ( $uname, $passwd );
unless ( $opts{'q'} or $opts{'C'} ){		# if querying or checking, don't bother authenticating
	unless ( $uname && $passwd ){
		( $uname, $passwd ) = &authenticate( $HOST, $PORT, $PROTOCOL, $REALM );
	}
	if ( ! $passwd ){
		die "Cannot authenticate credentials for $HOST on port $PORT in realm $REALM";
	}
	$passwd =  ascii_to_hex( $passwd ) ;							# convert passwd to hexadecimal string
	$passwd =~ s/(.{2})/%$1/g;										# id each byte with % sign
	$base_url =~ s/^($PROTOCOL:\/\/)/$1$uname:$passwd@/;			# insert username/passwd into URL

}
foreach my $file ( @ARGV ){
	my $dataLoLoL;
	if ( $file =~ m/\.xlsx*$/ ){
		my $spreadsheets = Spreadsheet::ParseExcel::Simple->read( $file );
		$dataLoLoL = parse_spreadsheets( $spreadsheets );
	} elsif ( $file =~ m/.(csv|tsv|txt)$/ ){
		$dataLoLoL = &read_csv_file( $file );
	} else {
		warn "File \"$file\" might not be an Excel (.xls) or character-delimited filetype (it lacks a csv/tsv/txt/xls(x) extension).\n";
		warn "Trying anyway...\n";
		$dataLoLoL = &read_csv_file( $file );
	}
	my $data;
	if( $opts{'q'} ){
		$data = &check_query_LoLoL( $dataLoLoL );
	} else {
		$data = &check_LoLoL( $dataLoLoL );
	}
	next if $opts{'C'};
	my $i = 0;
	foreach my $sheet ( @$data ){
		my ( $jdata, $nulls, $errors );
		if ( $opts{'q'} ){
			( $jdata, $nulls, $errors ) = &query_ws( $sheet );
		} else {
			$errors = &add_ws( $sheet );
		}
		&process_jdata( $jdata, $file ) if $jdata;
		&process_errors( $errors, $file ) if $errors;
		&process_nulls( $nulls, $file ) if $nulls;
		$i++;
		print "done with sheet $i\n";
	}
}

##################################      ... and Here         ########################################
####################################################################################################

print "\nDone at ", time-$t0[0], " sec, ", (times)[0]-$t0[1], " cpu\n" ;

####################################################################################################
######################################## Subroutines ###############################################
####################################################################################################

####################################################################################################
#	parseSpreadsheets()
#	Take a pointer to a Excel Spreadsheet and returns a list of hashes keyed on field names.
####################################################################################################
sub parse_spreadsheets {
	my ( $xls ) = @_;

	my @sheets = ();
	foreach my $sheet ($xls->sheets) {								# read a sheet from the SS book
		my @ss = ();												# spreadsheets stored as LoLoL
		warn "Reading sheet \"$sheet\".\n" if $opts{'d'};			# each sheet is a LoL
		while ($sheet->has_data) {									# read a row from the sheet
			my @foo = $sheet->next_row;
			push( @ss, \@foo );										# add the row to the sheet
		}
		push( @sheets, \@ss );
	}
	return( \@sheets );
}
####################################################################################################
#	read_csv_file()
####################################################################################################
sub read_csv_file {
	my ( $file ) = @_;

	my @LoLoL = ();

	open( FILE, "$file") or die "Cannot open CSV file \"$file\" for reading\n";
	my @LoL = ();
	while ( <FILE> ){
		chomp;
		next if /^#/;
		s/"//g;															# remove quotes from fields
		s/\s+$//;														# remove trailing whitespace
		my @list = split( /$fsep/ );
		push( @LoL, \@list );
	}
	close( FILE );
	$LoLoL[0] = \@LoL;
	return( \@LoLoL );
}
####################################################################################################
#	check_LoLoL()
#	Confirm that spreadsheet data meets requirements
####################################################################################################
sub check_LoLoL {
	my ( $LoLoL )  = @_;

	print $hline . "Pre-upload data validation errors:\n" . $hline;

	my @out = ();																# LoLoH
	foreach my $LoL ( @$LoLoL ){												# foreach sheet
		my $i = 0;																# init line counter
		my @heading = ();
		my @sheet = ();
		foreach my $L ( @$LoL ){
			if ( $i ){															# if heading has been read and confirmed
				&print_list_in_quotes( $L, \*STDERR  ) if $opts{'d'};
				if ( @heading != @$L ){											# if there is a mismatch between heading and field count...
					print STDERR "Mismatch between number of fields" .
							" in heading vs. row number $i.\n";
					print STDERR "heading:	";
					print_list_in_quotes( \@heading, \*STDERR );
					print STDERR "row:		";
					print_list_in_quotes( $L, \*STDERR );
				}
				my %row = ();													# if good, init row hash
				for( my $j = 0; $j < @heading; $j++ ){							# populate hash with rows
					$row{ $heading[$j] } = $L->[$j];							# keyed on field headings
					$row{ $heading[$j] } =~ s/["']//g;							# clean up data fields (quotes and leading "string:")
				}
				my $annoCat = $row{'annotationCategory'};						# abbreviate annotation category value
				my $item	= $row{'item'};										# and item (barcode)
				if( exists $studyAnnoCat{ $annoCat } ){
					warn 	"Correct value for annotationCategory: \"$annoCat\"" .
							" for \"$item\"; no interpretation required.\n"	if $opts{'d'};
				} elsif( exists $studyAnnoClassCat2CatOnly{ $annoCat } ){
					warn	"AnnotationCategory modification required for \"$item\",\n" .
							"from:\t$annoCat\nto:\t" .
							$studyAnnoClassCat2CatOnly{ $annoCat } . "\n";
					$row{'annotationCategory'} = $studyAnnoClassCat2CatOnly{ $annoCat };
				} else {
					warn 	"Cannot match annotationCategory: \"$annoCat\"" .
							" for \"$item\" to the controlled vocabulary.\n";
				}
				unless ( &check_row( \%row, $i ) ){								# check contents of row against controlled vocab, 0 = good
					push( @sheet, \%row );										# add row hash to sheet
				}
				unless( $row{'item'} =~ m/$itemBarcodeRegex{ $row{ 'itemType' } }/ ){
					print $row{'item'} . " does NOT match regex for ". $row{'itemType'} . ": /" .
						$itemBarcodeRegex{ $row{ 'itemType' } }	. "/\n";
				}
				$i++;															# increment line counter
			} else {															# if heading has not been found yet ...
		#		print join( ", ", @$L  ). "\n";
				@heading = @$L;													# read row as if it were a heading
				for( my $z = 0; $z < @heading; $z++ ){
					$heading[$z] =~ s/["']//g;									# remove quotes
					if ( $opts{'R'} ){
						foreach my $regex ( keys %regex_headings ){				# use regexs to convert user headings into working headings
							last if ( $heading[$z] =~ s/^$regex$/$regex_headings{$regex}/i );
						}
					}
				}
				&print_list_in_quotes( \@heading, \*STDERR ) if $opts{'d'};
				my $lc = List::Compare->new( \@put_headings, \@heading );		# set up comparison with ref headings
				if ( $lc->get_intersection == @put_headings ){					# if there is a perfect intersection ...
					warn "Field list is complete and exact.\n" if $opts{'d'};
					$i++;														# set counter to true
				} elsif ( $lc->is_LsubsetR ){									# ref headings are a subset of those found ...
					warn "Spreadsheet contains extra fields--but that\'s okay.\n" if $opts{'d'};
					$i++;														# set counter to true
				} elsif ( $lc->is_RsubsetL ){
					warn "Spreadsheet header is missing the following fields:\n";
					warn "\"" . join( "\", \"", $lc->get_Ronly ) . "\"\n";
				} else {
					warn "Spreadsheet field names do not match specification.\n";  # don't set counter; try next line
					warn "\"" . join( "\", \"", @put_headings ) . "\"\n";
				}
			}
		}
		push( @out, \@sheet );
	}
	return( \@out );
}
####################################################################################################
#	check_query_LoLoL()
####################################################################################################
sub check_query_LoLoL {
	my ( $LoLoL ) = @_;

	my @out = ();												# LoLoH
	foreach my $LoL ( @$LoLoL ){								# foreach sheet
		my $i = 0;												# init line counter
		my @heading = ();
		my @sheet = ();
		foreach my $L ( @$LoL ){
			if ( $i ){												# if heading has been read and confirmed
				my %row = ();										# if good, init row hash
				for( my $j = 0; $j < @heading; $j++ ){				# populate hash with rows
					$row{ $heading[$j] } = $L->[$j];				# keyed on field headings
				}
				push( @sheet, \%row );								# add row hash to sheet
				$i++;												# increment line counter
			} else {												# if heading has not been found yet ...
				@heading = @$L;										# read row as if it were a heading
				if ( grep /^item$/, @heading ){
					$i++;
				}
			}
		}
		push( @out, \@sheet );
	}
	return( \@out );
}
####################################################################################################
#	query_ws()
#	Compose and execute a series of queries of the database using barcodes from spreadsheet data
####################################################################################################
sub query_ws {
	my ( $data ) = @_;

	my $query_url = "$base_url/$ws{'get'}{'cmd'}/";
	$query_url .= "$rtn_method";
	print "query_url = $query_url\n";
	my @errors = ();
	my %error = ();
	my @nulls = ();
	my @jdata = ();

	foreach my $row ( @{ $data } ){
		my $qurl = $query_url;
		$qurl .= "item=$row->{'item'}";
		$qurl .= "&itemExact=true" if $opts{'e'};
		print "qurl = $qurl\n" if $opts{'d'};
		my $resp = $ua->get( $qurl );
		if ( ( $resp->{'_rc'} == 200 ) and
			 ( $resp->{'_msg'} eq "OK" )
		   ){												# if HTTP returns error code
			if ( $resp->{'_content'} eq 'null' ){
				push( @nulls, $row->{'item'} );
				$resp->{'mydata'} = $row;
				$error{ $row->{'item'} } = $resp;
			}
		} else {
			push( @errors, $row->{'item'} );
			$resp->{'mydata'} = $row;
			$error{ $row->{'item'} } = $resp;
		}
		if ( $resp->{'_msg'} eq "Bad credentials" ){
			die "Username/password incorrect.  Try again.\n";
		}
		&print_key_value_pairs( $resp, \*STDOUT, "'", "\n" ) if $opts{'k'};
		my $jstruct = $jcoder->decode( $resp->{'_content'} );
		push( @jdata, $jstruct );
#		$resp->{'_content'} = (JSON::XS->new->pretty(1)->encode( JSON::XS::decode_json($resp->{'_content'}))) if $opts{'p'};
#		$resp->{'_content'} = $jcoder->pretty->encode( $jcoder->decode($resp->{'_content'})) if $opts{'p'};
		$resp->{'_content'} = $jcoder->pretty->encode( $jstruct ) if $opts{'p'};
		print $resp->{'_content'};
		print "\n";
		sleep( $opts{'s'} ) if $opts{'s'};
	}
	return( \@jdata, \@nulls, \%error );
}
####################################################################################################
#	add_ws()
#	Compose and execute a series of "add annotation" calls to web service.
####################################################################################################
sub add_ws {
	my ( $data ) = @_;

	my $add_url = "$base_url/$ws{'put'}{'cmd'}/$rtn_method";
	my @errors = ();
	my %error = ();

	foreach my $row ( @{ $data } ){
		my $aurl = $add_url;
		my $i = 0;
		foreach my $q ( @put_headings ){
			$aurl .= "&" if $i++;
			$aurl .= "$q=" . $row->{ $q };
		}
		$aurl =~ s/ /%20/g;
		#warn "aurl = $aurl\n" if $opts{'U'};
		#my $request = HTTP::Request->new( POST	=>	$aurl );
		#my $response = $ua->request( $request );
		#if ( $response->is_success ){
		#	my $text = JSON::XS::from_json( $response->content );
		#	my $pretty_json = JSON::XS->new->pretty(1)->encode( $text );
		#	print "$pretty_json\n";
		#} else {
		#	print $response->status_line . "\n";
		#	die if $response->status_line =~ m/Bad Credentials/;
		#
		#}

		print "webservice: $aurl\n" if $opts{'v'};

		my $resp = $ua->get( $aurl );
		if ( $resp->{'_rc'} == 200 and $resp->{'_msg'} eq "OK" ){			# if HTTP returns error code
			$resp->{'_content'} = (JSON::XS->new->pretty(1)->encode( JSON::XS::decode_json($resp->{'_content'}))) if $opts{'p'};
		} else {
			push( @errors, $row->{'item'} );
			$resp->{'mydata'} = $row;
			$error{ $row->{'item'} } = $resp;
		}
		if ( $resp->{'_msg'} eq "Bad credentials" ){
			die "Username/password incorrect.  Try again.\n";
		}
		sleep( $opts{'s'} ) if $opts{'s'};
	}
	return( \%error );
}
####################################################################################################
sub process_errors {
	my ( $error, $fname ) = @_;

	print $hline . "Server error details:\n$hline";
	print "Webservice problems with the following barcodes:\n" . join(", ", (keys %$error) ) . "\n$hline";
	foreach my $err ( keys %$error ){
		&print_key_value_pairs( $error->{$err}, \*STDOUT, "'", "\n" ) if $opts{'d'};
		if ( $error->{$err}{'_content'} =~ m/^<h2>([^<]+)<\/h2><br \/><p>([^<]+)</ ){
			print "$err\n";
			print "HTTP ERROR: $1\n";
			print "Error Message: $2\n\n";
			if ( $2 =~ m/The following annotation is not unique: / ){
				$error->{$err}{'status'} = "OK";
			} else {
				$error->{$err}{'status'} = "RESUBMIT";
			}
		} else {
			print $error->{$err}{'_msg'} . "\n";
			$error->{$err}{'status'} = "RESUBMIT";
			if ( $BIG_PARSE_FAILURE_WARNING ){
				warn "$pgm_name error: Failure to parse error->{_content}.\n";
				warn $error->{$err}{'_content'} . "\n";
			} else {
				warn "$pgm_name error: Failure to parse error->{_content}.\n";
				warn substr( $error->{$err}{'_content'}, 0, 100 );
				warn "..." if ( length( $error->{$err}{'_content'} ) > 100 );
				warn "\n";
			}
		}
	}

	$fname =~ s/\.(csv|tsv|txt|xls|xlsx)$//;
	$fname .= ".errlog";


	my @problem_barcode = ( sort keys %$error );
	if ( scalar @problem_barcode == 0 ){
		return;											# create no error log
	}
	open( FILE, ">$fname") or die "Cannot open file: \"$fname\" for writing error log.\n";
	print FILE join( "\t", sort keys %{$error->{$problem_barcode[0]}{'mydata'}} ) . "\n";
	foreach my $err ( @problem_barcode ){
	if ( $error->{$err}{'status'} ne "OK" ){
			my $i = 0;
			foreach my $bc ( sort keys %{$error->{$err}{'mydata'}} ){
				print FILE "\t" if $i++;
				print FILE $error->{$err}{'mydata'}{$bc};
			}
			print FILE "\n";
		}
	}
	close( FILE );
}
####################################################################################################
sub process_nulls {
	my ( $nulls, $file ) = @_;

	my $fname = $file;
	$fname =~ s/\.\w+$//;
	$fname .= ".nulls";
	open( FILE, ">$fname" ) or die "Cannot open \"nulls\" file: \"$fname\" for writing.\n";
	foreach my $null ( @$nulls ){
		printf FILE ("%s\n", $null );
	}
	close( FILE );
}
####################################################################################################
sub process_jdata {
	my ( $jdat, $fname ) = @_;

	my $jLoH = &prep_jdata( $jdat );
	$fname =~ s/\.\w+$/.jdat/;							# swap file extensions
	print_LoH_as_table( $jLoH, $fname, "\t", $jheadings );


}
####################################################################################################
####################################################################################################
sub prep_jdata{
	my ( $jdat ) = @_;

	my @LoH = ();
	foreach ( @$jdat ){
		my $jda = $_->{'dccAnnotation'};
		next unless $jda;
		my %h = ();
		$h{'annotationCategory'}		= $jda->{'annotationCategory'}{'categoryName'};
		$h{'annotationClassification'}	= $jda->{'annotationCategory'}{'annotationClassification'}{'annotationClassificationName'};
		$h{'approved'}					= $jda->{'approved'};
		$h{'createdBy'}					= $jda->{'createdBy'};
		$h{'dateCreated'}				= $jda->{'dateCreated'};
		$h{'id'}						= $jda->{'id'};
		$h{'study'}						= $jda->{'items'}{'disease'}{'abbreviation'};
		$h{'diseaseDescription'}		= $jda->{'items'}{'disease'}{'description'};
		$h{'itemType'}					= $jda->{'items'}{'itemType'}{'itemTypeName'};
		$h{'item'}						= $jda->{'items'}{'item'};
		$h{'noteAddedBy'}				= $jda->{'notes'}{'addedBy'};
		$h{'noteDateAdded'}				= $jda->{'notes'}{'dateAdded'};
		$h{'noteText'}					= $jda->{'notes'}{'noteText'};
		$h{'rescinded'}					= $jda->{'rescinded'};
		$h{'status'}					= $jda->{'status'};
		push( @LoH, \%h );
	}
	return( \@LoH );
}
####################################################################################################
sub authenticate {
	my ( $host, $port, $protocol, $realm ) = @_;

	my $uname = &read_line( "Enter username: " );
	my $passwd = &read_line( "Enter password: ", "noecho" );
	print "\n";
	my $netloc = "$host:$port";
	$ua->credentials( $netloc, $realm, $uname, $passwd );
	my @return = $ua->credentials( $netloc, $realm );
	return( @return );
}
####################################################################################################
#sub auth2 {
#	my ( $login_form ) = @_;
#
#	my $form = HTML::Form->parse( $login_form );
#	my @inputs = $form->find_input;
#	my $request;
#	foreach my $input ( @inputs ){
#		$input->value( $uname ) if ( $input->name =~ m/username/ );
#		$input->value( $passwd ) if ( $input->name =~ m/password/ );
#		$request = $input->click( $form ) if ( $input->name =~ m/submit/ );
#		print "input_name = \"" . $input->name. "\"\n";
#	}
#	my $feedback = $ua->request( $request );
#}
####################################################################################################
#	check_row( \%row )
####################################################################################################
sub check_row {
	my ( $row_hash, $row_number ) = @_;

	my $field_faults = 0;
	foreach my $field ( keys %$row_hash ){
		if ( defined $studyAnnoFields{ $field } ){							# if field has a controlled vocabulary
			if ( defined $studyAnnoFields{ $field }{ $row_hash->{ $field } } ){
				print "Value \"" . $row_hash->{ $field } . "\" is a valid \"$field\".\n" if $opts{'d'};
			} else {
				$field_faults++;
				print "row $row_number ( " . $row_hash->{'item'} . " )\n";
				print "Value \"" . $row_hash->{ $field } . "\" is NOT a valid \"$field\".\n";
				print "Valid values for $field are: \"" . join( "\", \"", keys %{ $studyAnnoFields{ $field } } ) . "\".\n\n";
			}
		}
	}
	return( $field_faults );
}
####################################################################################################
#	read_line( $prompt, $mode, $term );
#	Echo a text prompt to STDOUT.
#	Read a line (or character) of text, with or without echoing it back to user.
#	Return the text to calling routine.
#	$prompt	 = <any text>, for example: "Please enter username: "
#	$mode	 = <Term::ReadKey, ReadMode>, valid values are: "normal", "noecho", "cbreak", "raw", "ultra-raw".
#				[optional if $term not provided] default = "normal"
#	$term	 = <input termination character>, e.g.: "\n"
#				[optional], default = "\n"
####################################################################################################
sub read_line {
	my ( $prompt, $mode, $term ) = @_;
	$term = "\n" unless $term;
	$mode = "normal" unless $mode;
	$prompt = "" unless $prompt;
	my @normal_modes = qw( normal noecho raw ultra-raw );

	my $input = "";
	ReadMode( $mode );
	print STDERR $prompt;
	if ( grep( /^$mode$/, @normal_modes ) ){
		while( $_ = ReadKey( 0 ) ){
			$input .= $_;
			last if ( $input =~ s/$term// );
		}
	} elsif ( $mode eq "cbreak" ){
		while ( not defined ( ReadKey( -1 ))){
		}
	} else {
		die "Unknown read_line option: mode = \"$mode\"\n";
	}
	ReadMode 0;
	return( $input );
}
####################################################################################################
#### end of template ###############################################################################
####################################################################################################
