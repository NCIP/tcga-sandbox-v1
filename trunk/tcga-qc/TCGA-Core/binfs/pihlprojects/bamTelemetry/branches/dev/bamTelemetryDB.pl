#!/usr/bin/perl
# SQLite version of bamTelemtery.pl
#This program has been modified so that it's sole function is to process the XML and load the database.  Producing files for the BAM
# Telemetry report and other uses will be handled in separate scripts that pull from the database.

# Example query
#  wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisFull?last_modified=[NOW-5DAYS+TO+NOW]&state=live" -O newCGHub.xml
#  wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisAttributes?study=phs000178&state=live" -O CGHubFull.xml
#

local $SIG{__DIE__} = sub{my @messages = @_;foreach(@messages){printOut(\*ERROR,$_)} }; #redirect any die through printOut

use strict;
use warnings;

use DBI;
use XML::Twig;
use Getopt::Long;
use File::HomeDir;

###############################
#                             #
#    Config Variables         #
#                             #
###############################
my $credfile = File::HomeDir->my_home."/.perlinfo/info.xml";

###############################
#                             #
#    Program Variables        #
#                             #
###############################
my %master_analysis_ids; #Key: CGHub analysis ID  Data: upload date
my $xmlfile; #CGHub XML file
my $help;
my ($dbuser, $dbpass) = getCreds($credfile);
my $dbfile;
my $errorfile;
my $logfile;


###############################
#                             #
#    Main Program             #
#                             #
###############################
printUsage() if (@ARGV <8 or ! GetOptions('help|?'=>\$help, 'x|xml=s'=>\$xmlfile, 'd|database=s'=>\$dbfile, 'e|error=s'=>\$errorfile, 'l|log=s'=>\$logfile) or defined $help);

unless(-e $dbfile){
	printOut(\*LOG,"Creating database $dbfile");
	initDB($dbfile);
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
printOut(\*LOG,"Getting existing analysis ID values");
getAnalysisID($dbfile,\%master_analysis_ids);
printOut(\*LOG,"Starting XML Parse");
parseCGHub($xmlfile,$dbfile,\%master_analysis_ids);
printOut(\*LOG, "Finished XML Parse");
###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################
sub parseCGHub{
	my $xmlfile = shift;
	my $dbfile = shift;
	my $master_analysis_ids = shift;
	
	my $analysis_id;
	my $state;
	my $upload_date;
	my $last_modified;
	my $disease_abbr;
	my $center_name;
	my $filename;
	my $aliquot_id;
	my $participant_id;
	my $sample_id;
	my $analyte_code;
	my $library_strategy;
	my $filesize;
	my $checksum;
	my $legacy_sample_id;
	my $sample_accession;
	my $test;
	my $hits;
	
	my $roots = {'ResultSet' => 1};
	my $handlers = { 'Hits' =>sub{$hits = $_->text;if($hits eq "0"){printOut(\*LOG,"No files to process, exiting");exit;} else{printOut(\*LOG,"Processing $hits files")}},
				'Result' => sub{$test = checkEntry($analysis_id,$last_modified,$master_analysis_ids);
								unless ($test eq "false"){addEntry($analysis_id,$state,$upload_date,$last_modified,$disease_abbr,$center_name,$filename,$aliquot_id,
								$participant_id,$sample_id,$analyte_code,$library_strategy,$filesize,$checksum,$legacy_sample_id,$dbfile,$test)};},
				'Result/analysis_id' => sub{ $analysis_id = $_->text},
				'Result/state' => sub{$state = $_->text},
				'Result/upload_date' => sub{ $upload_date = $_->text},
				'Result/last_modified' => sub{ $last_modified = $_->text},
				'Result/disease_abbr' => sub{ $disease_abbr = $_->text},
				'Result/center_name' => sub{ $center_name = $_->text},
				'Result/files/file/filename' => sub{$filename = $_->text},
				'Result/aliquot_id' => sub{ $aliquot_id = $_->text},
				'Result/participant_id' => sub{ $participant_id = $_->text},
				'Result/sample_id' => sub{ $sample_id = $_->text},
				'Result/analyte_code' => sub{ $analyte_code = $_->text},
				'Result/library_strategy' => sub{$library_strategy = $_->text},
				'Result/files/file/filesize' => sub{ $filesize = $_->text},
				'Result/files/file/checksum' => sub{$checksum = $_->text},
				'Result/legacy_sample_id' => sub{ $legacy_sample_id = $_->text},
				};


my $parser = new XML::Twig(TwigRoots => $roots, TwigHandlers=> $handlers, error_context => 1);

printOut(\*LOG, "Starting parse with $xmlfile");
$parser->parsefile($xmlfile);
}

sub checkEntry{
	#Checks the analysis_id and last_modified date to see if we know about this file
	my $analysis_id = shift;
	my $last_modified = shift;
	my $master_analysis_ids = shift;
	my $result = 'false';
	if(exists $master_analysis_ids->{$analysis_id}){
		if($master_analysis_ids->{$analysis_id} eq $last_modified){
			#we already know about this, go on
			$result = 'false';
		}
		else{
			#We know about it but it seems to have changed
			$result = 'UPDATE';
		}
	}
	else{
		#This is new
		$result = 'INSERT';
	}
	
	return $result;
}

sub getAnalysisID{
	#Gets the existing analysis IDs from the SQLITE database
	my $dbfile = shift;
	my $master_analysis_ids = shift;
	my $analysis_id;
	my $last_modified;
	
	my $sql = qq(SELECT analysis_id,last_modified FROM telemetry);
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	
	$sth->execute();
	$sth -> bind_columns(\$analysis_id,\$last_modified);
	while($sth -> fetch()){
		$master_analysis_ids->{$analysis_id} = $last_modified;
	}
	$dbh->disconnect;
}

sub addEntry{
	#Either adds new rows or updates existing rows to the SQLITE database
	my $analysis_id = shift;
	my $state = shift;
	my $upload_date = shift;
	my $last_modified = shift;
	my $disease_abbr = shift;
	my $center_name = shift;
	my $filename = shift;
	my $aliquot_id = shift;
	my $participant_id = shift;
	my $sample_id = shift;
	my $analyte_code = shift;
	my $library_strategy = shift;
	my $filesize = shift;
	my $checksum = shift;
	my $legacy_sample_id = shift;
	my $dbfile = shift;
	my $result = shift;
	my $tempBarcode;
	my $tempUUID;
	my $tempItem;
	my $sql;
	
	my $aliquot_barcode = getBarcode($aliquot_id,"Aliquot");
	my $participant_barcode = getBarcode($participant_id,"Participant");
	my $bam_file_type = setBamLibraryStrategy($library_strategy,$filename);
	my $center_type = getCenterType($analyte_code);
	my($center_id,$center_code) = setCenter($center_name);
	my $disease_id = setDisease($disease_abbr);
	
	if($result eq "INSERT"){
		$sql = qq($result INTO telemetry (analysis_id,state,upload_date,last_modified,disease_abbr,disease_id,center_name,filename,aliquot_uuid,aliquot_barcode,participant_uuid,participant_barcode,
		sample_uuid,analyte_code,library_strategy,filesize,checksum,legacy_sample_id,center_type,center_id,center_code,bam_file_type)
		 VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?));
	}
	elsif($result eq "UPDATE"){
		$sql = qq($result INTO telemetry (analysis_id,state,upload_date,last,modified,disease_abbr,disease_id,center_name,filename,aliquot_uuid,aliquot_barcode,participant_uuid,participant_barcode,
		sample_uuid,analyte_code,library_strategy,filesize,checksum,legacy_sample_id,center_type,center_id,center_code,bam_file_type)
		 VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) WHERE analysis_id = $analysis_id);
	}
	else{
		printOut(\*LOG, "SQL Query failure on result value $result");
		die;  # we got us a bad SQL query
	}
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth->execute($analysis_id,$state,$upload_date,$last_modified,$disease_abbr,$disease_id,$center_name,$filename,
			$aliquot_id,$aliquot_barcode,$participant_id,$participant_barcode,$sample_id,$analyte_code,$library_strategy,
			$filesize,$checksum,$legacy_sample_id,$center_type,$center_id,$center_code,$bam_file_type);
	
	$dbh->disconnect;
}

sub getBarcode{
	my $aliquot_id = shift;
	my $test = shift;
	my $result;
	my $tempBarcode;
	my $tempUUID;
	my $tempItem;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";
	my $sql = qq(select uh.barcode,uh. uuid,ui.item_type
					from uuid_hierarchy uh, uuid_item_type ui
					where uh.item_type_id = ui.item_type_id
					and  uuid = ?);

	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute($aliquot_id);
	$sth -> bind_columns(\$tempBarcode, \$tempUUID, \$tempItem);
	while($sth -> fetch()){
				if($tempItem ne $test){
					$result = "Not a $test";
					last;
				}
				elsif($aliquot_id eq $tempUUID){
					$result = $tempBarcode;
				}
				else{
					$result = "UUID Mismatch";
				}
				
			}#end of while sth fetch
	
	return $result;
	$dbh->disconnect;
}

sub initDB{
	my $dbfile = shift;
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql=qq(CREATE TABLE telemetry (analysis_id TEXT PRIMARY KEY, state TEXT,upload_date TEXT,last_modified TEXT,disease_abbr TEXT,disease_id INTEGER,
		center_name TEXT,filename TEXT, aliquot_uuid TEXT, aliquot_barcode TEXT,participant_uuid TEXT, participant_barcode TEXT,sample_uuid TEXT, analyte_code TEXT,
		library_strategy TEXT, filesize TEXT, checksum TEXT, legacy_sample_id TEXT,center_type TEXT,center_id TEXT,center_code TEXT,bam_file_type INTEGER));
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute();
	$dbh->disconnect;
}

sub getCreds{
	my $creds = shift;
	my $dbuser;
	my $dbpass;
	
	my $roots = {'info' => 1};
	my $handlers = { 'tcgauser' => sub{$dbuser = $_ -> text},
				'tcgapass' => sub{$dbpass = $_ -> text},
				};

	my $parser = new XML::Twig(TwigRoots => $roots, TwigHandlers => $handlers, error_context => 1);
	$parser->parsefile($creds);
	
	my @credarray = ($dbuser, $dbpass);
	return @credarray;

	
}

sub getCenterType{
#If the analyte is D,G,or W (basically DNA sequencing) the center code should be GSC.  If the analyte code is X,H or R (RNA sequencing), it should be GCC.  If neither it is declared WTF.  For now.
#Analyte codes taken from Code Tables Report- Portion Analyte	
	my $analyte_code = shift;
	my $barcode = shift;
	my $type;
	
		if($analyte_code eq 'D'){
			$type = 'GSC';
		}
		elsif ($analyte_code eq 'G'){
			$type = 'GSC';
		}
		elsif ($analyte_code eq 'W'){
			$type = 'GSC';
		}
		elsif ($analyte_code eq 'X'){
			$type = 'GSC';
		}
		elsif ($analyte_code eq 'H'){
			$type = 'CGCC';
		}
		elsif ($analyte_code eq 'R'){
			$type = 'CGCC';
		}
		elsif ($analyte_code eq 'T'){
			$type = 'CGCC';
		}
		else{
			#If the previous test fails, the aliquot barcode must have an Analyte as the 20th character
			my $analytecode = substr($barcode, 19, 1);
			if($analytecode eq 'D'){
				$type = 'GSC';
			}
			elsif($analytecode eq 'R'){
				$type = 'CGCC';
			}
			else{
				$type = 'WTF';
				printOut(\*ERROR, "$barcode failed center id");
			}
			
		}
		
	return $type;
}

sub setBamFileDataType{
	#This approach is based ENTIRELY on the filename.
	#anything falling through the cracks should be assinged 12
	#No 11 seem to exist
		
	my $filename = shift;
	my $file_type = -1;
	
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
		#print("Checking with $filename\n");
		foreach my $typekey (keys %typeHash){
			if($filename =~m/($typekey)/){
				$file_type = $typeHash{$typekey};
				#print("Match on $typekey assigning $file_type\n");
				last;
			}
			else{
				$file_type = 12;
				#print("Failed filename match\n");
			}
		}
	#print("Returning value $file_type\n");
	return $file_type;
}

sub setBamLibraryStrategy{
#This subroutine does the same ting as setFileDataType except it uses the library_strategy
#field from the CGHub metadata rather than using the file name

	my $library_strategy = shift;
	my $filename = shift;
	my $dcc_data_type;
	
	my %typeHash = ('WGS' => 4,
					'WXS'  => 9,
					'RNA-Seq' => 3,
					'WCS' => 4,
					'CLONE' => 4,
					'POOLCLONE' => 4,
					'AMPLICON' => 4,
					'CLONEEND' => 4,
					'FINISHING' => 4,
					'ChIP-Seq' => 12,
					'MNase-Seq' => 12,
					'DNaseHypersensitivity' => 12,
					'Bisulfite-Seq' => 12,
					'EST' => 9,
					'FL-cDNA' => 9,
					'CTS' => 12,
					'MRE-Seq' => 12,
					'MeDIP-Seq' => 12,
					'MDB-Seq' => 12,
					'miRNA-Seq' => 2,
					'VALIDATION' =>12
					);
		#OTHER is not in the typeHash since we need to parse miRNA out of it using filename
		if(exists $typeHash{$library_strategy}){
			$dcc_data_type = $typeHash{$library_strategy};
		}
		else{
			#Fall back on filenames to pick up others
			#print("Heading to file nameing with $filename\n");
			$dcc_data_type = setBamFileDataType($filename);
		}
	return $dcc_data_type;
}

sub setCenter{
	# Some centers have more than one type, so for the purposes of this program, if there were more than one type per center 
	# the were used in the following order:  GSC > GCC > GDAC > BCR.  So if a center has a GSC, GCC and GDAC, all sequences 
	# are assumed to come from the GSC.
	# Also, dbGaP did not follow the short naming convention for TCGA, and CGHub inhereted the problem.  
	# So some centers have multiple entries.  In all cases the second entry is the dbGaP entry.
	
	my $center = shift;
	my $center_id = -1;
	my $center_code = "-1";
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

		if(exists $center_hash{$center}){
			$center_id = $center_hash{$center};
			$center_code = $centercode_hash{$center};
		}

		
		my @temp = ($center_id,$center_code);
		return @temp;
}

sub setDisease{
	#Sets the disease code from the database
	my $disease_abbr = shift;
	my $disease_id;
	my $abbr;
	my $id;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";
	my $sql = qq(select disease_abbreviation, disease_id from disease where disease_abbreviation = ?);
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute($disease_abbr);
	$sth -> bind_columns(\$abbr, \$id);
	while($sth->fetch()){
		$disease_id = $id;
	}
	return $disease_id;
	$sth->finish;
	$dbh->disconnect;		
}

sub printOut{
	my $fh = shift;
	my $entry = shift;
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = $yearOffset + 1900;
	$month++;
	print($fh "$month/$dayOfMonth/$year $hour:$minute:$second\t$entry\n");
}

sub printUsage{
	print "Unknown option: @_\n" if ( @_ );
  	print "usage: program [--xml|-x CGHub XML File] [--database|-d Database File] [--error|-e Error File] [--log|-l log File] [--help|-?]\n";
  exit;
}