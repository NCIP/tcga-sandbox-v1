#!/usr/bin/perl
# Written by Todd Pihl, TCGA DCC, March 2012
use strict;
use warnings;

BEGIN {push @INC, "/h1/pihltd/usr/local/lib/perl5/site_perl/5.8.5";}
BEGIN {push @INC, "/h1/pihltd/usr/local/lib64/perl5/site_perl/5.8.5/x86_64-linux-thread-multi";}

local $SIG{__DIE__} = sub{my @messages = @_;foreach(@messages){printError($_)} }; #redirect any die through printError

# This script takes the output of a CGHub analysisAttributes query (should be provided as a file) and converts it into an
# Excel spreadsheet that can be fed into the BAM telemetry loader.
#
# Example CGHub query:  https://cghub.ucsc.edu/cghub/metadata/analysisAttributes?last_modified=[NOW-6MONTH+TO+NOW]
# CGHub query that should return the whole enchilada:  https://cghub.ucsc.edu/cghub/metadata/analysisAttributes?study=phs000178
#

#Required Output order:
#(See http://tcgadcc.com/browse/DCCT-870 attached file "final-all-bcs.txt")
# Aliquot Barcode
# Filename
# Disease Abbreviation (disease_id field in bam_file table in database)
# Center Name (center_id field in bam_file table in database)
# file size
# Upload Date
# Bam File Type (bam_datatype_id field in bam_file table in database)


#Added for providing working groups with their data
#MD5 value
#Aliquot UUID
#CGHub Analysis ID

#April 2012 Added QA checks with DCC data
# Disease
# Barcode/UUID validity (done in getBarcode)
# Molecule Type
# Center

use XML::Twig;
use DBI;
use Spreadsheet::WriteExcel;
use LWP::Simple;
use File::HomeDir;
use Time::HiRes;

#Make sure that the username and password are set before starting the parse
printUsage() unless exists($ENV{'TCGAUSER'});
printUsage() unless exists($ENV{'TCGAPASS'});

my $dbuser = $ENV{'TCGAUSER'};
my $dbpass = $ENV{'TCGAPASS'};

#Expected usages -f filename (file containing CGHUB analysisAttributes results) -t/-x Flag to print to tab delimited file or Excel file
my $cghubfile = "notset";
my $errorfile = "BAM_telemetry_ERROR.txt";
my $logfile = "BAM_telemetry_log.txt";
my $reportfile;
my $writevar = "notset";
my $writefile;
my $cleanfile = "false";
my $type = "unkown";

for(my $i =0; $i <=$#ARGV; $i++){
	if($ARGV[$i] eq "-f"){
		$i++;
		$cghubfile = $ARGV[$i];
	}
	elsif($ARGV[$i] eq "-t"){
		$writevar = "tab";
		$i++;
		$writefile = $ARGV[$i];
		$type = "tele";
	}
	elsif($ARGV[$i] eq "-x"){
		$writevar = "xls";
		$i++;
		$writefile = $ARGV[$i];
		$type = "tele";
	}
		elsif($ARGV[$i] eq "-dt"){
		$writevar = "awgtab";
		$i++;
		$writefile = $ARGV[$i];
		$type = "awg";
	}
	elsif($ARGV[$i] eq "-dx"){
		$writevar = "awgxls";
		$i++;
		$writefile = $ARGV[$i];
		$type = "awg";
	}
	elsif($ARGV[$i] eq "-c"){
		$cleanfile = "true";
	}
	elsif($ARGV[$i] eq "-r"){
		$i++;
		$reportfile = $ARGV[$i];
	}
	elsif($ARGV[$i] eq "-ws"){
		my $iniroots = {'ini' => 1};
		my $inihandlers = {'input' => sub{$cghubfile = $_ -> text},
							'error' => sub{$errorfile = $_ -> text},
							'log' => sub{$logfile = $_ -> text}
							};
		my $inifile = File::HomeDir->my_home."/.bam/bam.xml";
		my $iniparser = new XML::Twig(TwigRoots => $iniroots, TwigHandlers=> $inihandlers, error_context => 1);
		$iniparser -> parsefile($inifile);
	}
	else{
		printUsage();
	}
}

#check to make sure that all variables have actually been changed
if($cghubfile eq "notset"){
	printUsage();
}
if($writevar eq "notset"){
	printUsage();
}


#Set up the error, log and report files
if(-e $errorfile){
	open(ERROR,">>", $errorfile) or die "Can't open error file $errorfile: $!\n";
}
else{
	open(ERROR,">", $errorfile) or die "Can't open error file $errorfile: $!\n";
}
if(-e $logfile){
	open(LOG,">>", $logfile) or die "Can't open log file $logfile: $!\n";
}
else{
	open(LOG,">", $logfile) or die "Can't open log file $logfile: $!\n"
}
if(-e $reportfile){
	open(REPORT,">>", $reportfile) or die "Can't open log file $reportfile: $!\n";
}
else{
	open(REPORT,">", $reportfile) or die "Can't open log file $reportfile: $!\n"
}


#Remove the extra headers if requested
if($cleanfile eq "true"){
	printOut(\*LOG, "Cleaning file $cghubfile");
	$cghubfile = cleanXML($cghubfile);
	printOut(\*LOG, "Input file is now $cghubfile");
}

my %disease_abbr;
my %center;
my %center_type; #this is the center_id field from the database
my %center_code; #this is the center code displayed in the Data Portal
my %upload_date;
my %filename;
my %aliquotUUID;
my %aliquotBarcode;
my %participantUUID;
my %participantBarcode;
my %sampleUUID;
my %sampleBarcode;
my %analyte;
my %library;
my %filesize;
my %sampleLegacyBarcode;
my %md5;
my %bumlist; #This holds all the keys that have had some error
my %analysisid;
my %disease_type;
my %bam_file_type;
#
#Below are internal QA hashes
#
my %internalAccession;
my %internalFilename;
my %internalFiletype; #this should ALWAYS be bam
my %internalMD5;
my %accession;

my @key_array = ('0');
my $key = 0;
my $roots = {ResultSet => 1};

#############################################
#                                           #
#			Parsing parameters              #
#                                           #
#############################################
my $handlers = {'Result' => sub{ $key = getKey(@_, \@key_array)},
				'Result/analysis_id' => sub{ getData(@_,\%analysisid,$key)},
				'Result/upload_date' => sub{ getData(@_,\%upload_date,$key)},
				'Result/disease_abbr' => sub{ getData(@_,\%disease_abbr,$key)},
				'Result/center_name' => sub{ getData(@_,\%center,$key)},
				'Result/files/file/filename' => sub{ getData(@_,\%filename,$key)},
				'Result/aliquot_id' => sub{ getData(@_,\%aliquotUUID,$key)},
				'Result/participant_id' => sub{ getData(@_,\%participantUUID,$key)},
				'Result/sample_id' => sub{ getData(@_,\%sampleUUID,$key)},
				'Result/analyte_code' => sub{ getData(@_,\%analyte,$key)},
				'Result/library_strategy' => sub{ getData(@_,\%library,$key)},
				'Result/files/file/filesize' => sub{ getData(@_,\%filesize,$key)},
				'Result/files/file/checksum' => sub{ getData(@_,\%md5,$key)},
				'Result/legacy_sample_id' => sub{ getData(@_,\%sampleLegacyBarcode,$key)},
				'Result/sample_accession' => sub{ getData(@_,\%accession,$key)},
				'Result/analysis_xml/ANALYSIS_SET/ANALYSIS/TARGETS/TARGET' => sub{getAttributes(@_,\%internalAccession,"accession",$key)},
				'Result/analysis_xml/ANALYSIS_SET/ANALYSIS/DATA_BLOCK/FILES/FILE' => sub{getAttributes(@_,\%internalFilename,"filename",$key); getAttributes(@_,\%internalFiletype,"filetype",$key); getAttributes(@_,\%internalMD5,"checksum", $key)}
				};


my $parser = new XML::Twig(TwigRoots => $roots, TwigHandlers=> $handlers, error_context => 1);

printOut(\*LOG, "Starting parse with $cghubfile");
$parser->parsefile($cghubfile);

pop(@key_array); #clear out the extra key since I added one to start
printOut(\*LOG, "Parsing is done and there are $#key_array entries");
$parser->purge;

#Get the barcodes for all of the UUIDs in the CGHub data
printOut(\*LOG, "Getting Aliquot Barcodes");
#getBarcode(\%aliquotUUID, \%aliquotBarcode, \%bumlist); #This uses the web services but seems to fail
getBarcodeDB(\%aliquotUUID, \%aliquotBarcode, \%bumlist);

if($type eq "awg"){ # only need these for data freeze output
	printOut(\*LOG, "Getting participant barcodes");
	getBarcode(\%participantUUID, \%participantBarcode, \%bumlist);
	printOut(\*LOG, "Getting sample barcodes");
	getBarcode(\%sampleUUID, \%sampleBarcode, \%bumlist);
	#Figure out the center type.  If DNA, then GSC.  If RNA then GCC
	printOut(\*LOG, "Determining Center Type");
	getCenterType(\%analyte, \%center_type,\%aliquotBarcode, \%bumlist);
}

if($type eq "tele"){ #only needed for telemetry
	printOut(\*LOG, "Determining Center Type");
	setCenter(\%center, \%center_type, \%center_code, \@key_array);
	printOut(\*LOG, "Determining Disease Type");
	setDisease(\%disease_abbr, \%disease_type, \@key_array);
	printOut(\*LOG, "Determining BAM File Type");
	setBamFileDataType(\%filename, \%bam_file_type, \@key_array);
}

#Change the date format from what CGHub returns to what the report expects
printOut(\*LOG, "Changing date format");
alterDate(\%upload_date);

#Now do all the internal QA checks
printOut(\*LOG, "Performing internal QA Checks");
internalQA(\%accession, \%internalAccession, \@key_array, "Sample Accession");
internalQA(\%filename, \%internalFilename, \@key_array, "File Name");
internalQA(\%md5, \%internalMD5, \@key_array, "MD5 value");
internalBAM(\%internalFiletype, "File Type");

#Do the DCC-CGHub QA Check
printOut(\*LOG, "Performing DCC-CGHub QA Checks");
 #qaCheck(\%analysisid,\%filename, \@key_array, \%aliquotUUID, \%disease_abbr, \%center_code, \%analyte);
 qaCheck_dbVersion(\%analysisid,\%filename, \@key_array, \%aliquotUUID, \%disease_abbr, \%center_type, \%analyte);

#Print to either a tab delimited file or an Excel file
if($writevar eq "tab"){
	printOut(\*LOG, "Will be writing a tab telemetry file $writefile");
	printTab(\%aliquotBarcode,\%filename,\%disease_type,\%center_type,\%filesize,\%upload_date,\%bam_file_type,\%bumlist,$writefile, \@key_array);
}
elsif($writevar eq "xls"){
	printOut(\*LOG, "Will be writing a xls telemetry file $writefile");
	printXLS(\%aliquotBarcode,\%filename,\%disease_type,\%center_type,\%filesize,\%upload_date,\%bam_file_type,\%bumlist, $writefile, \@key_array);
}
elsif($writevar eq "awgtab"){
	printOut(\*LOG, "Will be writing a tab AWG file $writefile");
	printAWGTab(\%analysisid,\%center,\%upload_date,\%filename,\%aliquotBarcode,\%aliquotUUID,\%participantBarcode,\%sampleBarcode,\%disease_abbr,\%center_type,\%analyte,\%filesize,\%md5, \%bumlist, $writefile, \@key_array);
}
elsif($writevar eq "awgxls"){
	printOut(\*LOG, "Will be Writing a xls AWG file $writefile");
	printAWGXLS(\%analysisid,\%center,\%upload_date,\%filename,\%aliquotBarcode,\%aliquotUUID,\%participantBarcode,\%sampleBarcode,\%disease_abbr,\%center_type,\%analyte,\%filesize,\%md5, \%bumlist, $writefile, \@key_array);
}

printOut(\*LOG, "Process Finished");
close(LOG);
close(ERROR);
close(REPORT);
################################################
#              Subroutines                     #
################################################
sub getKey{
	#This subroutine grabs the ID attribute of a Result element and puts it in an array.  Should be unique within this dataset
	my ($parser, $result, $array) = @_;
	$key = ${$result->atts}{"id"};
	push(@$array, $key);
	return $key;
}

sub getData{
	#Pulls the data/text from the specified element
	my ($parser, $node, $hash, $key) = @_;
	$hash->{$key}=$node->text;
	$parser->purge;
}

sub getAttributes{
	my($parser, $node, $hash, $attvalue, $key) = @_;
	if(defined $node->att($attvalue)){
		$hash->{$key} = $node->att($attvalue);
	}
	else{
		$hash->{$key} = 'undef';
	}
}

sub getBarcodeDB{
	# The web service version of this seems to crack under load, so use the database
	my $uuid_hash = shift;
	my $barcode_hash = shift;
	my $bumlist = shift;
	my $tempUUID;
	my $tempBarcode;
	my $tempItem;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";

	#my $sql = qq(select barcode,uuid
	#				from barcode_history
	#				where uuid = ?);
	my $sql = qq(select uh.barcode,uh. uuid,ui.item_type
					from uuid_hierarchy uh, uuid_item_type ui
					where uh.item_type_id = ui.item_type_id
					and  uuid = ?);

	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	
	foreach my $key (keys %$uuid_hash){
		my $startUUID = $uuid_hash->{$key};
		my $length = length($startUUID);
		if($length == 36){
			$sth -> execute($startUUID);
			#$sth -> bind_columns(\$tempBarcode, \$tempUUID);
			$sth -> bind_columns(\$tempBarcode, \$tempUUID, \$tempItem);
			while($sth -> fetch()){
				if($tempItem ne "Aliquot"){
					$barcode_hash->{$key} = "UUID for $tempItem";
					$bumlist->{$key} = "Not an Aliquot UUID";
					next;
				}
				elsif($startUUID eq $tempUUID){
					$barcode_hash->{$key} = $tempBarcode;
				}
				else{
					$barcode_hash->{$key} = 'BUSTED';
					$bumlist->{$key} = "UUID mismatch between CGHub and database";
				}
				
			}#end of while sth fetch
		} #if length = 36
		else{
			$barcode_hash->{$key} = 'No UUID';
			$bumlist->{$key} = "No UUID in CGHub XML";
		}
	}
	$sth->finish;
	$dbh->disconnect;
}

sub getBarcode{
	#This uses the DCC web service to get the barcode associated with the UUID
	# Example bum UUID https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/xml/uuid/218ccc2a-1f03-4833-8ac3-3ce2c0e0f7bd
	#
	#Deprecated.  Since I have to use the database for the QA check, there is no reason to not use the DB for getting barcodes
	#
	my $uuid_hash = shift;
	my $barcode_hash = shift;
	my $bumlist = shift;
	my $tempUUID;
	my $tempBarcode;
	my $htmlerror;
	my $errorex;
	my $timer;
	my $elapsed;
	my $trigger = 0.18;
	my $baseURL = 'https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/xml/uuid/';
	my $roots = {'uuidMapping' => 1};
	my $handlers = { 'uuid' => sub{$tempUUID = $_ -> text},
					'barcode' => sub{$tempBarcode = $_ -> text}
					};
	my $subparser = new XML::Twig(TwigRoots => $roots, TwigHandlers => $handlers, error_context => 1);
	foreach my $key (keys %$uuid_hash){
		$timer = Time::HiRes::gettimeofday;
		my $startUUID = $uuid_hash->{$key};
		my $length = length($startUUID);
		if($length == 36){ #If it isn't 36, it isn't a UUID
			my $content = get($baseURL.$startUUID); #use LWP::Simple because it might get HTML back
			if($content =~m/HTTP/){
				printOut(\*ERROR, "HTTP Error: $content\n");
				$bumlist->{$key} = "UUID not found via web service";
				printOut(\*ERROR, "$key\t$bumlist->{$key}");
				printOut(\*REPORT, "$key\tUUID not found via web service");
			}
			else{
				$subparser ->parse($content);
				if($startUUID eq $tempUUID){
					$barcode_hash->{$key} = $tempBarcode;
				}
				else{
					$barcode_hash->{$key} = 'BUSTED';
					$bumlist->{$key} = "UUID mismatch between CGHub and database";
					printOut(\*ERROR, "$key\t$bumlist->{$key}");
					printOut(\*REPORT, "CGHUB:$startUUID\tDCC:$tempUUID\tUUID Mismatch between CGHub and DCC");
				}
			}
			$elapsed = Time::HiRes::gettimeofday-$timer;
				#Using an elapsed of .2, it turns out to be 3 queries per second and this takes an hour for a full CGHub download.  4 is preferred, 5 would be optimal
				#Should also look at doing 997 queries, then waiting until 3 minutes are up, then fire again.
				if($elapsed < $trigger){
					my $snooze = $trigger - $elapsed;
					Time::HiRes::sleep($snooze);
				}
		}
		else{
			$barcode_hash->{$key} = 'No UUID';
			$bumlist->{$key} = "No UUID in CGHub XML";
			printOut(\*ERROR, "$key\t$bumlist->{$key}");
			printOut(\*REPORT, "$key\tNo UUID in CGHub XML");
		}
		$subparser->purge;
	}
}

sub getCenterType{
#If the analyte is D,G,or W (basically DNA sequencing) the center code should be GSC.  If the analyte code is X,H or R (RNA sequencing), it should be GCC.  If neither it is declared WTF.  For now.
#Analyte codes taken from Code Tables Report- Portion Analyte	
	my $analyte_hash = shift;
	my $type_hash = shift;
	my $barcodes = shift;
	my $bumlist = shift;
	my $type;
	
	foreach my $key (keys %$analyte_hash){
		if($analyte_hash->{$key} eq 'D'){
			$type_hash->{$key} = 'GSC';
		}
		elsif ($analyte_hash->{$key} eq 'G'){
			$type_hash->{$key} = 'GSC';
		}
		elsif ($analyte_hash->{$key} eq 'W'){
			$type_hash->{$key} = 'GSC';
		}
		elsif ($analyte_hash->{$key} eq 'X'){
			$type_hash->{$key} = 'GSC';
		}
		elsif ($analyte_hash->{$key} eq 'H'){
			$type_hash->{$key} = 'CGCC';
		}
		elsif ($analyte_hash->{$key} eq 'R'){
			$type_hash->{$key} = 'CGCC';
		}
		elsif ($analyte_hash->{$key} eq 'T'){
			$type_hash->{$key} = 'CGCC';
		}
		else{
			#If the previous test fails, the aliquot barcode must have an Analyte as the 20th character
			my $tempcode = $barcodes->{$key};
			my $analytecode = substr($tempcode, 19, 1);
			if($analytecode eq 'D'){
				$type_hash->{$key} = 'GSC';
			}
			elsif($analytecode eq 'R'){
				$type_hash->{$key} = 'CGCC';
			}
			else{
				$bumlist->{$key} = "Cannot determine Center type";
				printOut(\*ERROR, "$key\t$bumlist->{$key}");
			}
			
		}
	}	
}

sub setBamFileDataType{
	#This approach is based ENTIRELY on the filename.  Library_strategy would be better, but is unreliable
	#anything falling through the cracks should be assinged 12
	#No 11 seem to exist
	my $filehash = shift;
	my $bam_file_type = shift;
	my $keyarray = shift;
	
	my %typeHash = ('454.bam' => 1,
					'mirna.bam' => 2,
					'trimmed.annotated.translated_to_genomic.bam' => 3,
					'rnaseq.bam' => 3,
					'whole.bam' => 4,
					'capture.bam' => 5,
					'SOLiD.bam' => 6,
					'SOLiD_whole_exome_extensions.bam' => 7,
					'capture.bam' => 8,
					'C484' => 8,
					'C282' => 8,
					'exome.bam' => 9,
					'exome_1.bam' => 9,
					'exome_2.bam' => 9,
					'exome.' => 9,
					'exome_HOLD_QC_PENDING.bam' => 9,
					'_whole_1.bam' => 10
					);
	foreach (sort @$keyarray){
		my $filekey = $_;
		foreach my $typekey (keys %typeHash){
			if($filehash->{$filekey} =~m/($typekey)/){
				$bam_file_type->{$filekey} = $typeHash{$typekey};
			}
		}
		if(exists $bam_file_type->{$filekey}){
			next;
		}
		else{
			$bam_file_type->{$filekey} = 12;
		}
	}
}

sub setDisease{
	my $disease_abbr = shift;
	my $disease_type = shift;
	my $keyarray = shift;
	my %disease_hash = ('GBM' =>1,
						'OV' => 2,
						'LUSC' => 3,
						'LUAD' => 4,
						'BRCA' => 5,
						'COAD' => 6,
						'KIRC' => 7,
						'KIRP' => 8,
						'STAD' => 9,
						'HNSC' => 10,
						'LIHC' => 11,
						'CESC' => 12,
						'LAML' => 13,
						'LCLL' => 14,
						'SKCM' => 15,
						'LNNH' => 18,
						'THCA' => 20,
						'LGG' => 21,
						'PRAD' => 22,
						'UCEC' => 23,
						'READ' => 24,
						'DLBC' => 26,
						'PAAD' => 27,
						'BLCA' => 28,
						'ESCA' => 29,
						'SARC' => 30
						);
	foreach(@$keyarray){
		my $diseasekey = $disease_abbr{$_};
		if(exists $disease_hash{$diseasekey}){
			$disease_type->{$_} = $disease_hash{$diseasekey};
		}
		else{
			$disease_type->{$_} = $diseasekey;
		}
	}
}

sub setCenter{
	# Some centers have more than one type, so for the purposes of this program, if there were more than one type per center 
	# the were used in the following order:  GSC > GCC > GDAC > BCR.  So if a center has a GSC, GCC and GDAC, all sequences 
	# are assumed to come from the GSC.
	# Also, dbGaP did not follow the short naming convention for TCGA, and CGHub inhereted the problem.  
	# So some centers have multiple entries.  In all cases the second entry is the dbGaP entry.
	
	my $center = shift;
	my $center_type = shift;
	my $center_code = shift;
	my $keyarray = shift;
	#NOTE: The center_hash codes are assigned based on the center_id field in the center database table
	my %center_hash = ( 'BCGSC' => 15,
						'BCCAGSC' => 15,
						'BCM' => 8,
						'BI' => 12,
						'HAIB' => 6,
						'HMS' => 3,
						'HMS-RK' => 3,
						'IGC' => 26,
						'ISB' => 17,
						'JHU_USC' => 2,
						'USC-JHU' => 2,
						'LBL' => 4,
						'MDA' => 25,
						'MSKCC' => 5,
						'NCH' => 27,
						'RG' => 22,
						'UCSC' => 29,
						'UNC' => 7,
						'UNC-LCCC' => 7,
						'USC' => 28,
						'VUMC' => 30,
						'WUSM' => 9,
						'WUGSC' => 9
						);
	#NOTE: The centercode_hash is assigned based on the codes in the center view of the Code Tables Report
	#https://tcga-data.nci.nih.gov/datareports/codeTablesReport.htm
	my %centercode_hash = ( 'BCGSC' => "13",
						'BCCAGSC' => "13",
						'BCM' => "10",
						'BI' => "08",
						'HAIB' => "06",
						'HMS' => "02",
						'HMS-RK' => "02",
						'IGC' => "22",
						'ISB' => "15",
						'JHU_USC' => "05",
						'USC-JHU' => "05",
						'LBL' => "03",
						'MDA' => "20",
						'MSKCC' => "04",
						'NCH' => "23",
						'RG' => "11",
						'UCSC' => "25",
						'UNC' => "07",
						'UNC-LCCC' => "07",
						'USC' => "2000",
						'VUMC' => "27",
						'WUSM' => "09",
						'WUGSC' => "09"
						);
	foreach (@$keyarray){
		my $centerkey = $center->{$_};
		if(exists $center_hash{$centerkey}){
			$center_type->{$_} = $center_hash{$centerkey};
			$center_code->{$_} = $centercode_hash{$centerkey}
		}
		else{
			$center_type->{$_} = $centerkey;
			$center_code->{$_} = $centerkey;
		}
	}
}

sub alterDate{
	#This simply reformats the date format from 2012-02-22T08:52:40Z to dd-MMM-yy
	my %month_hash = ('01'=>'JAN',
					'02'=>'FEB',
					'03'=>'MAR',
					'04'=>'APR',
					'05'=>'MAY',
					'06'=>'JUN',
					'07'=>'JUL',
					'08'=>'AUG',
					'09'=>'SEP',
					'10'=>'OCT',
					'11'=>'NOV',
					'12'=>'DEC'
					);
	my $date_hash = shift;
	foreach my $key (keys %$date_hash){
		my $tempdate = $date_hash->{$key};
		$tempdate = substr($tempdate, 0, 10);
		my @temp = split(/-/,$tempdate);
		$date_hash->{$key} = $temp[2]."-".$month_hash{$temp[1]}."-".substr($temp[0],2,2);
	}
}

sub cleanXML{
	#GCHub has a nasty habit of including multiple XML headers, so this will clean those out and write a new file
	# <?xml version="1.0" encoding="UTF-8"?> is the culprit that has to be removed
	my $source = shift;
	my $dest = "CLEANXML.".$source;
	open(SOURCE, $source) or die "Can't open file $source: $!\n";
	open(DEST,">", $dest) or die "Can't open file $dest: $!\n";
	while(<SOURCE>){
		if($_ =~m/\<\?xml version=\"1.0\" encoding=\"UTF-8\"\?\>/){
			s/\<\?xml version=\"1.0\" encoding=\"UTF-8\"\?\>//;
			print(DEST $_);
		}
		else{
			print(DEST $_);
		}
	}
	close(SOURCE);
	close(DEST);
	return $dest;
}

sub internalQA{
	my $cghub = shift;
	my $analysis = shift;
	my $keyarray = shift;
	my $label = shift;
	foreach(sort @$keyarray){
		if($cghub->{$_} eq $analysis->{$_}){
			next;
		}
		else{
			printOut(\*ERROR, "$label QA Mismatch: CGHub value: $cghub->{$_}\tAnalysisXML value: $analysis->{$_}");  
		}
	}
}

sub internalBAM{
	my $hash = shift;
	my $label = shift;
	foreach my $key (sort keys %$hash){
		if(defined $hash->{$key}){
			if ($hash->{$key} eq "bam"){
				next;
			}
			else{
				printOut(\*ERROR, "$label QA Issue:\tFileType is:\t$hash->{$key}");
			}
		}
		else{
			printOut(\*ERROR, "$label QA Issue\tMissing file type");
		}
	}
}

sub qaCheck_dbVersion{
	#performs the same function as qaCheck but gets data directly from the database since the biospecimen metadata web service
	#is taking 2 seconds per query.  (2 seconds)(10381 bam files)(2 queries/bam file) = 11.5 hours to do the QA via web service.  Ugh.
	my $analysis_id=shift;
	my $filename = shift;
	my $keys = shift;
	my $aliquot_id = shift;
	my $disease_abbr = shift;
	my $center = shift;
	my $analyte_code = shift;
	my $dccUUID;
	my $dccDisease;
	my $dccCenter;
	my $dccAnalyte;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";
	my $sql = qq(select uuid, disease_abbreviation, receiving_center_id, portion_analyte_code
					from UUID_HIERARCHY
					where uuid = ?);
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	
	#The print statement below is deliberately bypassing printOut
	print(REPORT "Timestamp\tCGHub Analysis ID\tDCC Aliquot UUID\tBAM File Name\tError Type\tDCC Value\tCGHub Value\n");
	foreach(@$keys){
		my $queryUUID = $aliquot_id->{$_};
		my $hashkey = $_;
		$sth -> execute($queryUUID);
		$sth -> bind_columns(\$dccUUID, \$dccDisease, \$dccCenter, \$dccAnalyte);
		while($sth->fetch()){
			if($dccUUID ne $aliquot_id->{$hashkey}){
				printOut(\*REPORT, "$analysis_id->{$hashkey}\t$dccUUID\t$filename->{$hashkey}\tUUID Mismatch\t$dccUUID\t$aliquot_id->{$hashkey}");
			}
			if ($dccDisease ne $disease_abbr->{$hashkey}){
				printOut(\*REPORT, "$analysis_id->{$hashkey}\t$dccUUID\t$filename->{$hashkey}\tDisease Mismatch\t$dccDisease\t$disease_abbr->{$hashkey}");
			}
			#print("Prior to check DCC has $dccCenter and CGHub has $center->{$hashkey}\n");
			if ($dccCenter ne $center->{$hashkey}){
				my $is_error = "true";
				$is_error = centerDupes($center->{$hashkey});
				if($is_error eq "true"){
					printOut(\*REPORT, "$analysis_id->{$hashkey}\t$dccUUID\t$filename->{$hashkey}\tCenter Mismatch\t$dccCenter\t$center->{$hashkey}");
				}
			}
			if ($dccAnalyte ne $analyte_code->{$hashkey}){
					printOut(\*REPORT, "$analysis_id->{$hashkey}\t$dccUUID\t$filename->{$hashkey}\tAnalyte Mismatch\t$dccAnalyte\t$analyte_code->{$hashkey}");
			}
		}#while
	}#foreach
	$sth->finish;
	$dbh->disconnect;
}

sub qaCheck{
	#This subroutine compares the Disease, Molecule Type and Receiving Center between DCC and CGHub
	#Unfortunately, this takes two web service calls to DCC to get the needed data
	#First call: https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/xml/uuid/ALIQUOT_UUID (example https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/xml/uuid/2d4b31ad-8790-426a-965b-25b74293156c)
	#Can get Disease, Receiving Center and Analyte URL from this
	#Second call: https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/xml/uuid/ANALYTE_UUID
	# Can get analyte type and description from this
	########################################################################################################
	#																									   #
	# Deprecated in favor of qaCheck_dbVersion because the web service was taking 2 seconds per query      #
	#																									   #
	########################################################################################################
	my $analysis_id=shift;
	my $filename = shift;
	my $keys = shift;
	my $aliquot_id = shift;
	my $disease_abbr = shift;
	my $center = shift;
	my $analyte_code = shift;
	my $dccUUID;
	my $dccDisease;
	my $dccCenter;
	my $dccAnalyte;
	my $analyteURL;
	
	my $trigger = 0.38;
	my $timer;
	my $elapsed;
	
	my $baseURL = "https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/xml/uuid/";
	my $root = {metadata => 1};
	my $first_handlers = {'tcgaElement/uuid' => sub{$dccUUID = $_ -> text},
							'tcgaElement/disease/abbreviation' => sub{$dccDisease = $_ -> text},
							'tcgaElement/aliquot/receivingCenter/code' => sub{$dccCenter = $_ ->text},
							'tcgaElement/analyte' => sub{$analyteURL = $_ -> att("href")} #need the href attribute;
							};
	my $second_handlers = {'tcgaElement/analyte/analyteType' => sub{$dccAnalyte = $_ -> text} #single letter code, should be same as analyte_code in CGHub XML
							};
	my $firstparser = new XML::Twig(TwigRoots => $root, TwigHandlers=> $first_handlers, error_context => 1);
	my $secondparser = new XML::Twig(TwigRoots => $root, TwigHandlers=> $second_handlers, error_context => 1);
	
	#This print deliberately bypasses printOut
	print(REPORT "Timestamp\tCGHub Analysis ID\tBAM File Name\tError Type\tDCC Value\tCGHub Value\n");
	foreach(@$keys){
		$timer = Time::HiRes::gettimeofday;
		my $firstparse_error = "no";
		my $cgAliquotUUID = $aliquot_id->{$_};
		my $first_content = get($baseURL.$cgAliquotUUID);
		if(length($first_content) == 0){
			printError("DCC Response error.  URL: $baseURL.$cgAliquotUUID");
		}
		elsif($first_content =~m/HTTP/){
			printError("HTTP Error: $first_content");
			printReport("$cgAliquotUUID\tUUID not found via QA web service");
			$firstparse_error = "yes";
		}
		else{
			$firstparser->parse($first_content);
		}
		if($firstparse_error eq "no"){
			my $second_content = get($analyteURL);
			if($second_content =~m/HTTP/){
				printError("HTTP Error: $second_content");
				printReport("$analyteURL not found via QA web service");
			}
			else{
				$secondparser->parse($second_content);
			}
		}
		$firstparser->purge;
		$secondparser->purge;
		if($dccUUID ne $aliquot_id->{$_}){
			printReport("$analysis_id->{$_}\t$filename->{$_}\tUUID Mismatch\t$dccUUID\t$aliquot_id->{$_}");
		}
		if ($dccDisease ne $disease_abbr->{$_}){
			printReport("$analysis_id->{$_}\t$filename->{$_}\tDisease Mismatch\t$dccDisease\t$disease_abbr->{$_}");
		}
		if ($dccCenter ne $center->{$_}){
			my $is_error = "true";
			$is_error = centerDupes($analyte_code->{$_});
			if($is_error eq "true"){
				printReport("$analysis_id->{$_}\t$filename->{$_}\tCenter Mismatch\t$dccCenter\t$center->{$_}");
			}
		}
		if ($dccAnalyte ne $analyte_code->{$_}){
				printReport("$analysis_id->{$_}\t$filename->{$_}\tAnalyte Mismatch\t$dccAnalyte\t$analyte_code->{$_}");
		}
		$elapsed = Time::HiRes::gettimeofday-$timer;
			if($elapsed < $trigger){
				my $snooze = $trigger - $elapsed;
				Time::HiRes::sleep($snooze);
			}
	}#foreach
}

sub centerDupes{
	#this subroutine corrects for any ERROR that happen because the DCC has center codes based on center type (GSC, GCC)
	#while CGHub doesn't distinguish between center types
	my $cghubcenter = shift;
	my $is_error = "true";
	#the dupe_hash is based on the codes in the Center view of the Code Tables Report
	#my %dupe_hash = ( "10" =>["10","12"],
	#					"12" =>["10","12"],
	#					"01" =>["01","08","14"],
	#					"08" =>["01","08","14"],
	#					"14" =>["01","08","14"],
	#					"03" =>["03","16"],
	#					"16" =>["03","16"],
	#					"19" =>["19","20","24","26"],
	#					"20" =>["19","20","24","26"],
	#					"24" =>["19","20","24","26"],
	#					"26" =>["19","20","24","26"],
	#					"04" =>["04","17"],
	#					"17" =>["04","17"],
	#					"25" =>["18","25"],
	#					"18" =>["18","25"],
	#					"09"=>["09","21","30"],
	#					"21"=>["09","21","30"],
	#					"30"=>["09","21","30"]
	#					);
	
	#the version below is based on the database center_id value in the center table
	#my %dupe_hash = ( "8"=>["8","23"],
	#				"23"=>["8","23"],
	#				"1"=>["1","12","16"],
	#				"12"=>["1","12","16"],
	#				"16"=>["1","12","16"],
	#				"14"=>["14","26"],
	#				"26"=>["14","26"],
	#				"4"=>["4","18"],
	#				"18"=>["4","18"],
	#				"21"=>["21","25"],
	#				"25"=>["21","25"],
	#				"5"=>["5","19"],
	#				"19"=>["5","19"],
	#				"11"=>["11","27"],
	#				"27"=>["11","27"],
	#				"20"=>["20","29"],
	#				"29"=>["20","29"],
	#				"9"=>["9","13","24"],
	#				"13"=>["9","13","24"],
	#				"24"=>["9","13","24"]
	#				);
		my %dupe_hash = ( 8=>[8,23],
					23=>[8,23],
					1=>[1,12,16],
					12=>[1,12,16],
					16=>[1,12,16],
					14=>[14,26],
					26=>[14,26],
					4=>[4,18],
					18=>[4,18],
					21=>[21,25],
					25=>[21,25],
					5=>[5,19],
					19=>[5,19],
					11=>[11,27],
					27=>[11,27],
					20=>[20,29],
					29=>[20,29],
					9=>[9,13,24],
					13=>[9,13,24],
					24=>[9,13,24]
					);
					#print("Looking at $cghubcenter\n");
	if(exists $dupe_hash{$cghubcenter}){
		my @temp = @{$dupe_hash{$cghubcenter}};
		foreach (@temp){
			#if($cghubcenter eq $_){
			if($cghubcenter == $_){
				$is_error = "false";
			}
		}
	}
	return $is_error;
}

sub printUsage{
	print("Expected usage:\n -f filename OR -ws to use ~/.bam/bam.xml config file.  Note that the file MUST be an analysisAttributes output from CGHub\n");
	print("-c Will remove extra XML headers from source file\n");
	print(" [-t|-x] filename  For Telemetry output, this flag sets the output to tab delmited (-t) or Microsoft Excel (-x) format and prints to filename\n");
	print(" [-dt|-dx] filename  For AWG output, this flag sets the output to tab delmited (-dt) or Microsoft Excel (-dx) format and prints to filename\n");
	print(" -r reportfile  Sets the file that the QA report will be written to\n");
	print("The TCGAUSER and TCGAPASS environment variables must contain the database username and password, respectively\n");
	exit;
}

sub printTab{

#input order (\%aliquotBarcode,\%filename,\%disease_type,\%center_type,\%filesize,\%upload_date,\%bam_file_type,\%bumlist, $writefile, \@key_array);

	my $aliquotBarcode = shift;
	my $filename = shift;
	my $disease_abbr = shift;
	my $center_type = shift;
	my $filesize = shift;
	my $upload_date = shift;
	my $bam_file_type = shift;
	my $bumlist = shift;
	my $writefile = shift;
	my $key_array = shift;

	
	open(OUTPUT,">",$writefile) or die "Can't open file $writefile: $!\n";

	print(OUTPUT "barcode\tfilename\tdisease_abbreviation\tcenter_name\tfilesize\tupload_date\tbam_file_type\n");
	
	foreach( @$key_array){
		if(exists $bumlist->{$_}){
			next;
		}
		else{
			print(OUTPUT "$aliquotBarcode->{$_}\t$filename->{$_}\t$disease_abbr->{$_}\t$center_type->{$_}\t$filesize->{$_}\t$upload_date->{$_}\t$bam_file_type->{$_}\n");
			#print("$aliquotBarcode->{$_}\t$filename->{$_}\t$disease_abbr->{$_}\t$center_type->{$_}\t$filesize->{$_}\t$upload_date->{$_}\t$bam_file_type->{$_}\n");
		}
	}
	#print(ERROR "File Name\tComment\n");
	#printError("File Name\tComment\n");
	printOut(\*ERROR, "File Name\tComment\n");
	foreach my $bumkey (sort keys %bumlist){
		#printError("$filename->{$bumkey}\t$bumlist->{$bumkey}\n");
		printOut(\*ERROR, "$filename->{$bumkey}\t$bumlist->{$bumkey}\n");
	}
	close(OUTPUT);
}

sub printXLS{
	my $aliquotBarcode = shift;
	my $filename = shift;
	my $disease_abbr = shift;
	my $center_type = shift;
	my $filesize = shift;
	my $upload_date = shift;
	my $bam_file_type = shift;
	my $bumlist = shift;
	my $writefile = shift;
	my $key_array = shift;
	
	my $workbook = Spreadsheet::WriteExcel->new($writefile);
    my $worksheet   = $workbook->add_worksheet("Data");
    my $bumsheet = $workbook->add_worksheet("ERROR");
    my $row = 0;
    my $column = 0;
    
    my @header = ("barcode","filename","disease_abbreviation","center_name","filesize","upload_date","bam_file_type");
	$worksheet->write_row($row,$column,\@header);
	$row++;
	
	foreach(sort @$key_array){
		if(exists $bumlist->{$_}){
			next;
		}
		else{
			my @temp = ("$aliquotBarcode->{$_}","$filename->{$_}","$disease_abbr->{$_}","$center_type->{$_}","$filesize->{$_}","$upload_date->{$_}","$bam_file_type->{$_}");
			$worksheet->write_row($row,$column,\@temp);
			$row++;
		}
	}
	
	$row = 0;
	$column = 0;
	@header = ("File Name", "Comment");
	$bumsheet->write_row($row, $column,\@header);
	$row++;
	
	foreach my $bumkey (sort keys %$bumlist){
		my @temp = ("$filename->{$bumkey}",$bumlist->{$bumkey});
		$bumsheet->write_row($row,$column,\@temp);
		$row++;
	}
	$workbook -> close() or die "Error closing $workbook: $!";
}

sub printAWGTab{

	my $analysisid = shift;
	my $center = shift;
	my $upload_date = shift;
	my $filename = shift;
	my $aliquotBarcode = shift;
	my $aliquotUUID = shift;
	my $participantBarcode = shift;
	my $sampleBarcode = shift;
	my $disease_abbr = shift;
	my $center_type = shift;
	my $analyte = shift;
	my $filesize = shift;
	my $md5 = shift;
	my $bumlist = shift;
	my $writefile = shift;
	my $key_array = shift;
	
	open(OUTPUT,">",$writefile) or die "Can't open file $writefile: $!\n";
	my $errorfile = $writefile.".ERROR.txt";
	open(ERROR, ">", $errorfile) or die "Can't open file $errorfile: $!\n";

	print(OUTPUT "Analysis ID\tCenter\tReceived Date\tBAM File\tAliquot Barcode\tAliquot UUID\tPatient Barcode\tSample Barcode\tDisease\tCenter Type\tData Type\tFileSize\tMD5\n");
	foreach(sort @$key_array){
		if(exists $bumlist->{$_}){
			next;
		}
		else{
			print(OUTPUT "$analysisid->{$_}\t$center->{$_}\t$upload_date->{$_}\t$filename->{$_}\t$aliquotBarcode->{$_}\t$aliquotUUID->{$_}\t$participantBarcode->{$_}\t$sampleBarcode->{$_}\t$disease_abbr->{$_}\t$center_type->{$_}\t$analyte->{$_}\t$filesize->{$_}\t$md5->{$_}\n");
		}
	}
	print(ERROR "File Name\tComment\n");
	foreach my $bumkey (sort keys %bumlist){
		print(ERROR "$filename->{$bumkey}\t$bumlist->{$bumkey}\n");
		#printOut(\*ERROR. "$filename->{$bumkey}\t$bumlist->{$bumkey}\n");
	}
	close(OUTPUT);
	close(ERROR);
}

sub printAWGXLS{
	my $analysisid = shift;
	my $center = shift;
	my $upload_date = shift;
	my $filename = shift;
	my $aliquotBarcode = shift;
	my $aliquotUUID = shift;
	my $participantBarcode = shift;
	my $sampleBarcode = shift;
	my $disease_abbr = shift;
	my $center_type = shift;
	my $analyte = shift;
	my $filesize = shift;
	my $md5 = shift;
	my $bumlist = shift;
	my $writefile = shift;
	my $key_array = shift;
	
	my $workbook = Spreadsheet::WriteExcel->new($writefile);
    my $worksheet   = $workbook->add_worksheet("Data");
    my $bumsheet = $workbook->add_worksheet("ERROR");
    my $row = 0;
    my $column = 0;
    
    my @header = ("Analysis ID","Center","Received Date","BAM File","Aliquot Barcode","Aliquot UUID","Patient Barcode","Sample Barcode","Disease","Center Type","Data Type","FileSize","MD5");
	$worksheet->write_row($row,$column,\@header);
	$row++;
	
	foreach (sort @$key_array){
		if(exists $bumlist->{$_}){
			next;
		}
		else{
			my @temp = ("$analysisid->{$_}","$center->{$_}","$upload_date->{$_}","$filename->{$_}","$aliquotBarcode->{$_}","$aliquotUUID->{$_}","$participantBarcode->{$_}","$sampleBarcode->{$_}","$disease_abbr->{$_}","$center_type->{$_}","$analyte->{$_}","$filesize->{$_}","$md5->{$_}");
			$worksheet->write_row($row,$column,\@temp);
			$row++;
		}
	}
	
	$row = 0;
	$column = 0;
	@header = ("File Name", "Comment");
	$bumsheet->write_row($row, $column,\@header);
	$row++;
	
	foreach my $bumkey (sort keys %$bumlist){
		my @temp = ("$filename->{$bumkey}",$bumlist->{$bumkey});
		$bumsheet->write_row($row,$column,\@temp);
		$row++;
	}
	$workbook -> close() or die "Error closing $workbook: $!";
}

sub printOut{
	my $fh = shift;
	my $entry = shift;
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = $yearOffset + 1900;
	$month++;
	print($fh "$month/$dayOfMonth/$year $hour:$minute:$second\t$entry\n");
}