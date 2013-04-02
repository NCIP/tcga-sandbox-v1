#!/usr/bin/perl
# A replacement for MAFFinder that uses a SQLite database
use warnings;
use strict;

use lib "/h1/pihltd/pihlprojects/MAFFinder/modules";
use lib "/Users/pihltd/Documents/pihlprojects/MAFFinder/modules"; #this is only needed for development, can be turned off
BEGIN {push @INC, "/h1/pihltd/pihlprojects/MAFFinder/modules";}
local $SIG{__DIE__} = sub{my @messages = @_;foreach(@messages){printOut(\*LOG,$_)} }; #redirect any die through printOut

#File Location Translations
#Jamboree Filesystem: /tcgafiles/ftp_auth/deposit_ftpusers/tcgajamboree   Web:https://tcga-data-secure.nci.nih.gov/tcgafiles/tcgajamboree
#Protected Filesystem: /tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor  Web: https://tcga-data-secure.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/
#Anonymous Filesystem: /tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/  Web:https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/


use DBI;
use XML::Twig;
use File::HomeDir;
use Confluence;
use File::Find;
use File::stat; #used to enable mtime
use RPC::XML;

###############################
#                             #
#    Config Variables         #
#                             #
###############################
my $dbfile = File::HomeDir->my_home."/.maf/maffiles.db";
my $working_dir = File::HomeDir->my_home."/.maf/";
my $credfile = File::HomeDir->my_home."/.perlinfo/info.xml";
my $logfile = File::HomeDir->my_home."/.maf/maf_log.txt";
my $public_wiki_space = "TCGA";
#my $public_wiki_page = "New TCGA MAF Files Report";
my $public_wiki_page = "TCGA MAF Files";
my $private_wiki_space = "TCGAproject";
my $private_wiki_page = "Full MAF Report";

###############################
#                             #
#    Program Variables        #
#                             #
###############################
#Get the credentials needed for database access and wiki access
my ($dbuser, $dbpass,$nciuser,$ncipass) = getCreds($credfile);
my %existing_files; #key: filesystem location data:deploy date
my $newfiles = "false";
my %new_db_file_hash;  #New files found in the database.  key:location data:deploy date (not used)
my %new_filesystem_file_hash;  #New files found on the filesystem.  Key:location  Data:irrelevant
my $full_content ="";  #holds ALL the content that will end up on the wiki
my $tab_content = ""; #holds a tab delimited version of content;
my %master_disease_list; #from the 1.0 production database, has all diseases.  Key: disease abbreviation  Data: disease name
my @filesystems = ('/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/','/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/', '/tcgafiles/ftp_auth/deposit_ftpusers/tcgajamboree');
my $section_header;
my $latest;
my $content_source;
my $current_date;
my $file_date;

###############################
#                             #
#    Main Program             #
#                             #
###############################
#open various log files
open (LOG, ">", $logfile) or die "Can't open log file $logfile\n";

#Create the database if it doesn't exist
unless(-e $dbfile){
	printOut(\*LOG,"Creating database file $dbfile");
	initDB($dbfile);
}

#Populate the master disease list
masterDiseaseList(\%master_disease_list,$dbuser,$dbpass);

#get the current date
($current_date,$file_date) = getCurrentDate();

#Get the files that this program already knows from the SQLite db
printOut(\*LOG, "Getting existing MAF files from SQLite");
getKnown(\%existing_files,$dbfile);

#Get the MAF files from Oracle.  Updates changed records and adds new records
printOut(\*LOG, "Getting MAF files from production Oracle");
$newfiles = getOracle(\%existing_files,\%new_db_file_hash,$dbuser,$dbpass,$dbfile);

#Get tumor and normal counts for the files that are new
printOut(\*LOG, "Getting tumor and normal counts for new files");
sampleCount(\%new_db_file_hash, $dbfile);

#Search the file system for MAF files

#Update again so anything new in the database isn't duplicated by a file search.
printOut(\*LOG,"Second update for known files");
getKnown(\%existing_files,$dbfile);


foreach my $start (@filesystems){
	if($start =~m/anonymous/){
		printOut(\*LOG,"Searching public file system");
	}
	elsif($start =~m/tcga4ye0/){
		printOut(\*LOG,"Searching protected file system");
	}
	elsif($start =~m/jamboree/){
		printOut(\*LOG, "Searching Jamboree file system");
	}
	#Search the file system for MAF files
	$newfiles = getFilesystem(\%existing_files,\%new_filesystem_file_hash,$newfiles,$start,$dbfile);
}
if($newfiles eq "true"){
	#add the new files to the SQLite database
	printOut(\*LOG,"Adding new filesystem MAF files to database");
	addFilesystemFiles(\%master_disease_list,\%new_filesystem_file_hash,$dbfile);
	#add sample counts
	printOut(\*LOG,"Adding new filesystem sample counts to database");
	sampleCount(\%new_filesystem_file_hash,$dbfile);
}

#Search the wiki for MAF files
printOut(\*LOG,"Searching wiki");
getWiki(\%existing_files,\%master_disease_list,$dbfile);




#Start the wiki printing process
$full_content = printHeader($current_date);
my $diseasesql = qq(SELECT filename,tumor_count,normal_count,status,archive_name,piistatus,deploy_date,md5,filesystem,url,version FROM maf_files WHERE disease = ? AND source = ? AND latest = ?);
#Build content..
#First is database files that are current
$content_source = "database";
$latest = 1;
$section_header = "Current MAF Files";
printOut(\*LOG,"Adding current database content");
($full_content,$tab_content) = createContent($full_content,$tab_content,$diseasesql,$content_source,$latest,$dbfile,$section_header);
my $current_filename = $working_dir.$file_date."_current_maf_files.txt";
printTab($current_filename,$tab_content);
$tab_content = "";  #zero out the tab content

$latest = 0;
$section_header = "Obsolete MAF Files";
printOut(\*LOG,"Adding obsolete database content");
($full_content,$tab_content) = createContent($full_content,$tab_content,$diseasesql,$content_source,$latest,$dbfile,$section_header);
my $obsolete_filename = $working_dir.$file_date."_obsolete_maf_files.txt";
printTab($obsolete_filename,$tab_content);
$tab_content="";

$content_source = "filesystem";
$latest = 3;
$section_header = "File System Only MAF Files";
printOut(\*LOG,"Adding File system only content");
($full_content,$tab_content) = createContent($full_content,$tab_content,$diseasesql,$content_source,$latest,$dbfile,$section_header);
$tab_content = "";

#if there is new stuff, print the public page.  Jamboree data is never included here
if($newfiles eq 'true'){
	printOut(\*LOG,"New files have been found, printing public page");
	printWiki($dbfile,$nciuser,$ncipass,$full_content,$public_wiki_space,$public_wiki_page);
	addAttachment($current_filename,$nciuser,$ncipass,$public_wiki_space,$public_wiki_page);
	addAttachment($obsolete_filename,$nciuser,$ncipass,$public_wiki_space,$public_wiki_page);
}
else{
	printOut(\*LOG,"No new MAF files, skipping public page print");
}

$content_source = "jamboree";
$latest = 4;
$section_header = "Jamboree MAF Files";
printOut(\*LOG,"Adding Jamboree content");
($full_content,$tab_content) = createContent($full_content,$tab_content,$diseasesql,$content_source,$latest,$dbfile,$section_header);
$tab_content = "";

$content_source = 'wiki';
$latest = 5;
$section_header = "MAF files from the Wiki";
printOut(\*LOG,"Adding Wiki content");
($full_content,$tab_content) = createContent($full_content,$tab_content,$diseasesql,$content_source,$latest,$dbfile,$section_header);
$tab_content="";

printOut(\*LOG,"Printing Full MAF Report");
printWiki($dbfile,$nciuser,$ncipass,$full_content,$private_wiki_space,$private_wiki_page);
addAttachment($current_filename,$nciuser,$ncipass,$private_wiki_space,$private_wiki_page);
addAttachment($obsolete_filename,$nciuser,$ncipass,$private_wiki_space,$private_wiki_page);
#SQLite print routine for testing
#printDB($dbfile);
###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################

sub getCreds{
	my $creds = shift;
	my $dbuser;
	my $dbpass;
	my $nciuser;
	my $ncipass;
	
	my $roots = {'info' => 1};
	my $handlers = { 'tcgauser' => sub{$dbuser = $_ -> text},
				'tcgapass' => sub{$dbpass = $_ -> text},
				'nciuser' => sub{$nciuser = $_ -> text},
				'ncipass' => sub{$ncipass = $_ -> text}
				};

	my $parser = new XML::Twig(TwigRoots => $roots, TwigHandlers => $handlers, error_context => 1);
	$parser->parsefile($creds);
	
	my @credarray = ($dbuser, $dbpass,$nciuser,$ncipass);
	return @credarray;

	
}

sub getKnown{
	my $existing_files = shift;
	my $dbfile = shift;
	my $filesystem;
	my $date;
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql = qq(SELECT filesystem, deploy_date FROM maf_files;);
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth->execute();
	$sth -> bind_columns(\$filesystem,\$date);
	while($sth -> fetch()){
		$existing_files->{$filesystem} = $date;
	}
	$dbh->disconnect;
}

sub getOracle{
#This updates existing records if the deploy date has changed and adds new records if they don't exist.
	my $existing_files = shift;
	my $new_file_hash = shift;
	my $dbuser = shift;
	my $dbpass = shift;
	my $dbfile = shift;
	my $newfiles = "false";
	my $disease;
	my $disease_name;
	my $filename;
	my $location;
	my $status;
	my $latest;
	my $archive;
	my $date;
	my $md5;
	my $piistatus;
	my $url;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";

	#This query should get all MAF files that the database knows about.
	my $sql = qq(select d.disease_abbreviation, d.disease_name, fi.file_name, a.deploy_location, a.deploy_status, a.is_latest, a.archive_name, a.date_added, NVL(fi.md5,'Unknown')
	from file_info fi, archive_info a, file_to_archive fa, disease d
	where  fi.file_id = fa.file_id
	and fa.archive_id = a.archive_id
	and a.disease_id = d.disease_id
	and fi.file_name like '%maf');

	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute();

	$sth -> bind_columns(undef,\$disease,\$disease_name,\$filename,\$location,\$status,\$latest,\$archive,\$date, \$md5);
	while($sth -> fetch()){
		#location points to the archive, not the directory.  Need it to point to the MAF file
		$location = getFullPath($location,$filename);
		$piistatus = checkPII($location);
		$url = createURL($location);
		my @tempdate = split(/ /,$date); #dump the fine details of the database date.
		$date = $tempdate[0];
		#Figure out if we know about this file already
		if(exists $existing_files->{$location}){
			if($existing_files->{$location} eq $date){
				#location and date match, don't do anything since we already know about it
			}
			else{
				#location matches, but date doesn't so an update happened
				$newfiles = "true";
				updateSQLite("UPDATE",$disease,$disease_name,$filename,$location,$status,$latest,$archive,$date,$md5,$piistatus,"database",$url,$dbfile);
				$new_file_hash->{$location} = $date;
			}
		} #End of if exists
		else{
			#If the file doesn't already exist, it must be new and we need to add it to the database
			$newfiles = "true";	
			updateSQLite("INSERT",$disease,$disease_name,$filename,$location,$status,$latest,$archive,$date,$md5,$piistatus,"database",$url,$dbfile);
			$new_file_hash->{$location} = $date;
		}
	}
	$dbh->disconnect;
	return $newfiles;
}

sub getFilesystem{
	my $existing_files = shift;
	my $new_file_hash = shift;
	my $newfiles = shift;
	my $searchdir = shift;
	my $dbfile = shift;
	my @file_array;
	
	find(sub{ push @file_array, $File::Find::name if( -f and /\.maf$/)}, $searchdir);
	
	#filter out the known files
	foreach (@file_array){
		if(exists $existing_files->{$_}){
			next;  #we know of this file, don't do anything
		}
		else{
			$newfiles = "true";
			$new_file_hash->{$_} = 1;
		}
	}
	
	return $newfiles
}

sub printOut{
	my $fh = shift;
	my $entry = shift;
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = $yearOffset + 1900;
	$month++;
	print($fh "$month/$dayOfMonth/$year $hour:$minute:$second\t$entry\n");
}

sub initDB{
	my $dbfile = shift;
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	$dbh->do("CREATE TABLE maf_files(id INTEGER PRIMARY KEY, filesystem TEXT,filename TEXT,tumor_count INTEGER, 
		normal_count INTEGER,archive_name TEXT,status TEXT, deploy_date TEXT, md5 TEXT,url TEXT,
		disease TEXT, disease_name TEXT, latest INTEGER, piistatus TEXT, source TEXT, version TEXT);");
	$dbh->disconnect;
}

sub updateSQLite{
	my $function = shift;
	my $disease = shift;
	my $disease_name = shift;
	my $filename = shift;
	my $location = shift;
	my $status = shift;
	my $latest = shift;
	my $archive = shift;
	my $date = shift;
	my $md5 = shift;
	my $piistatus = shift;
	my $source = shift;
	my $url = shift;
	my $dbfile = shift;
	my $sql;
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	if($function eq "INSERT"){
		$sql = qq($function INTO maf_files (filesystem, filename, archive_name,status,deploy_date,md5,disease,disease_name,latest,piistatus,source,url) VALUES (?,?,?,?,?,?,?,?,?,?,?,?));
	}
	elsif($function eq "UPDATE"){
		$sql = qq($function INTO maf_files (filesystem, filename, archive_name,status,deploy_date,md5,disease,disease_name,latest,piistatus,source,url) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) WHERE filesystem = $location);
	}
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth->execute($location,$filename,$archive,$status,$date,$md5,$disease,$disease_name,$latest,$piistatus,$source,$url);
	
	$dbh->disconnect;
	
}

sub sampleCount{
	#gets sample counts and MAF version
	my $new_file_hash = shift;
	my $dbfile = shift;
		
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	
	foreach my $location (keys %$new_file_hash){
		my %tumorcount;
		my %normalcount;
		my $t;
		my $n;
		my $mafversion = "unknown";
		if(-e $location){ #test to see if the file exists before tyring to open it
			open (FILE, $location) or die "Can't open file $location: $!\n";
				while(<FILE>){
					chomp($_);
					if($_ =~m/#version/){
						chomp($_);
						my $startloc = index($_,'version');
						$mafversion = substr($_,$startloc);
					}
					elsif($_ =~m/##/){
						next; #get rid of header
					}
					elsif($_ =~m/Center/){
						next; #get rid of header
					}
					else{
						my @counttemp = split(/\t/,$_);
						$tumorcount{$counttemp[15]} = "1";
						$normalcount{$counttemp[16]} = "1";
					}
				} #while FILE
		} #if file exists
		else{
			
		}#else if file exists
		$t = scalar keys %tumorcount;
		$n = scalar keys %normalcount;
		my $sql = qq(UPDATE maf_files SET tumor_count=?, normal_count=?, version=? WHERE filesystem = ?);
		my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
		$sth->execute($t,$n,$mafversion,$location);
		
	}# end of foreach location
	
	$dbh->disconnect;
}

sub printDB{
	my $dbfile = shift;
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql = qq(SELECT * FROM maf_files);
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth->execute();
	while(my($filesystem,$filename,$tumor_count,$normal_count,$archive_name,$status,$deploy_date, $md5,$url,$disease, $disease_name, $latest) = $sth -> fetchrow_array()){
		print("$filesystem\t$filename\t$tumor_count\t$normal_count\t$archive_name\t$status\t$deploy_date\t$md5\t$url\t$disease\t$disease_name\t$latest\n");
		print("$_\n");
	}
}

sub getFullPath{
	#Returns the full file system path to the file
	my $location = shift;
	my $file = shift;
	
	#Bust up the location string
	my @path_array = split(/\//, $location);
	my $arch_name = pop(@path_array);
	
	#the deploy location is a .tar.gz so remove the last 7 characters from the archive name
	$arch_name = substr($arch_name,0,-7);
	
	#Now rebuild the full path to the MAF file
	my $full = "";
	foreach my $entry (@path_array){
		$full = $full.$entry."/"
	}
	$full = $full.$arch_name."/".$file;
	return $full
}

sub createContent{
	my $content = shift;
	my $tab_content = shift;
	my $querysql = shift;
	my $content_source = shift;
	my $latest = shift;
	my $dbfile = shift;
	my $section_header = shift;
	my $table_columns = "<tr><th>MAF File Name</th><th>Tumor Samples:Normal Samples</th><th>Deploy Status</th><th>MAF Version</th><th>Archive Name</th><th>Protection Status</th><th>Deploy Date</th><th>MD5</th><th>Deploy Location</th></tr>";
	my $tab_table_columns = "Disease\tMAF File Name\tTumor Samples:Normal Samples\tDeploy Status\tMAF Version\tArchive Name\tProtection Status\tDeploy Date\tMD5\tDeploy Location\n";
	my %disease_list;
	my $disease;
	my $disease_name;
	my $filename;
	my $tumor_count;
	my $normal_count;
	my $status;
	my $archive_name;
	my $piistatus;
	my $deploy_date;
	my $md5;
	my $filesystem;
	my $fileurl;
	my %available_disease;
	my $version;
	my @content_array;
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
		
	$content = $content."<h1>$section_header</h1>";
	$tab_content = $tab_content.$tab_table_columns;
	
	#First get a listing of all the diseases
	my $sql = qq(select disease,disease_name from maf_files where source = ? and latest = ?);
	my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth->execute($content_source,$latest);
	$sth -> bind_columns(\$disease,\$disease_name);
	while($sth -> fetch()){
		$disease_list{$disease} = $disease_name;
	}
	#Now build content alphabetically by disease
	foreach my $disease_key (sort keys %disease_list){
		$content = $content."<h3>$disease_key:  $disease_list{$disease_key}</h3><table>";
		$content = $content.$table_columns;
		$sth = $dbh->prepare($querysql) or die "Couldn't prepare statement: " . $dbh->errstr;
		$sth ->execute($disease_key,$content_source,$latest);
		$sth->bind_columns(\$filename,\$tumor_count,\$normal_count,\$status,\$archive_name,\$piistatus,\$deploy_date,\$md5,\$filesystem,\$fileurl,\$version);
		while($sth->fetch()){
			if($content_source eq 'wiki'){
				$content = $content."<tr><td>$filename</td><td>n/a</td><td>$status</td><td>$version</td><td>$archive_name</td><td>$piistatus</td><td>$deploy_date</td><td>$md5</td><td><a href=\"$fileurl\">$filesystem</a></td></tr>";
				$tab_content = $tab_content."$disease_key\t$filename\tn/a\t$status\t$version\t$archive_name\t$piistatus\t$deploy_date\t$md5\t$fileurl\n";

			}
			elsif($tumor_count eq "0"){
				$content = $content."<tr><td>$filename</td><td>n/a</td><td>$status</td><td>$version</td><td>$archive_name</td><td>$piistatus</td><td>$deploy_date</td><td>$md5</td><td><a href=\"$fileurl\">$filesystem</a></td></tr>";
				$tab_content = $tab_content."$disease_key\t$filename\tn/a\t$status\t$version\t$archive_name\t$piistatus\t$deploy_date\t$md5\t$fileurl\n";

			}
			else{
				$content = $content."<tr><td>$filename</td><td>$tumor_count:$normal_count</td><td>$status</td><td>$version</td><td>$archive_name</td><td>$piistatus</td><td>$deploy_date</td><td>$md5</td><td><a href=\"$fileurl\">$filesystem</a></td></tr>";
				$tab_content = $tab_content."$disease_key\t$filename\t$tumor_count:$normal_count\t$status\t$version\t$archive_name\t$piistatus\t$deploy_date\t$md5\t$fileurl\n";
			}
		}
		$content = $content."</table>\n";
	}
	$dbh->disconnect;
	push(@content_array,$content);
	push(@content_array,$tab_content);
	return(@content_array);
}

sub printWiki{
	my $dbfile = shift;
	my $nciuser = shift;
	my $ncipass = shift;
	my $content = shift;
	my $space = shift;
	my $title = shift;

	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";
	my $wiki = Confluence->new($url, $nciuser, $ncipass);
	printOut(\*LOG,"Printing on space $space with title $title");
	my $result = $wiki->getPage($space,$title);
	my $id = $result->{"id"};
	my $version = $result->{"version"};
	my $parent = $result->{"parentId"};
	
	my %wikihash;
	$wikihash{"content"} = $content;
	$wikihash{"id"} = $id;
	$wikihash{"space"} = $space;
	$wikihash{"title"} = $title;
	$wikihash{"version"} = $version;
	$wikihash{"parentId"} = $parent;
	
	$wiki = Confluence->new($url, $nciuser, $ncipass);
	$wiki->storePage(\%wikihash);
	$wiki->logout();
}

sub addAttachment{
	#see:https://confluence.atlassian.com/display/DISC/Perl+XML-RPC+client (comment by Eric Sorenson is particularly helpful)
	my $filename = shift;
	my $nciuser = shift;
	my $ncipass = shift;
	my $space = shift;
	my $title = shift;
	my $content_type = "text/plain";
	
	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";
	my $wiki = Confluence->new($url, $nciuser, $ncipass);
	
	printOut(\*LOG,"Adding attachment on space $space with title $title");
	my $result = $wiki->getPage($space,$title);
	my $id = $result->{"id"};
	my $version = $result->{"version"};
	my $parent = $result->{"parentId"};
	
	my @temp = split(/\//,$filename);
	my $name = pop @temp;
	
	my %attachment_hash;
	$attachment_hash{"fileName"} = $name;
	$attachment_hash{"contentType"} = $content_type;
	$attachment_hash{"comment"} = "Uploaded from MAFFinderDB.pl";
	
	my $fh;
	open($fh,$filename) or die "Can't open $filename:$!\n";
	my $stuff = new RPC::XML::base64($fh);
	$wiki->addAttachment($id,\%attachment_hash,$stuff);
	
	$wiki->logout();
	close $fh;
}

sub getCurrentDate{
	#this returns a date formatted for wiki and tab file name use.  NOT to be confused with getDate which gets the date of individual MAF files.
	my @months = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = 1900 + $yearOffset;
	my $theDate = "$months[$month] $dayOfMonth, $year";
	my $file_date = $months[$month]."_".$dayOfMonth."_".$year;
	my @date_array = ($theDate,$file_date);
	
	return @date_array;
}

sub printHeader{
	my $theDate = shift;
	my $pagecontent = qq(<ac:macro ac:name="section">
  						<ac:parameter ac:name="border">true</ac:parameter>
  						<ac:rich-text-body>
  							<ac:macro ac:name="column">
      						<ac:parameter ac:name="width">25%</ac:parameter>
     						 <ac:rich-text-body>
       							 <ac:macro ac:name="toc">
       							 </ac:macro>
     						 </ac:rich-text-body>
   							 </ac:macro>
   							 <ac:macro ac:name="column">
      						<ac:parameter ac:name="width">50%</ac:parameter>
     						 <ac:rich-text-body>
       							 <p><strong>Updated on  $theDate</strong></p><p>This page is a summary of all the <a href="https://wiki.nci.nih.gov/x/M5ZXAg">Mutation Annotation Format (MAF)</a> files available at TCGA through the Data Portal or FTP access, and is a step towards allowing the TCGA community to understand what MAF files exist. See <a href="http://bit.ly/mut-data-flow">TCGA MAF Dashboard</a> for more information.  The files in the Current section are those that are considered the most up to date.  However, some MAF files are being maintained outside of the normal TCGA system, and those are in the Not in database sections.  The Old MAF files section represents a historical record of past MAF file submissions. In some cases, these files are obsolete and no longer supported and in others they have been superseded by later submissions.  In addition to the MAF files available through the portal, there are MAF files associated with TCGA publications, and these are available for download from the publication pages. <strong>Be aware that MAF files change on a regular basis, so this summary may differ from what you find.</strong>  For a quick tutorial on the different ways to access MAF files, visit <a href="https://wiki.nci.nih.gov/x/24q4Aw">Accessing MAF Files.</a></p><p></p><p>Any files marked as "Controlled" in the <strong>Access Type</strong> column will require appropriate access privileges.  See <a href="http://tcga-data.nci.nih.gov/tcga/tcgaAccessTiers.jsp">Access Tiers on the Data Portal</a> for instructions on how to gain access to controlled data.  Files marked as "Open" have no access restrictions.</p><p></p><p>The Tumor and Normal sample counts are the number of unique barcodes for each found in that MAF file.</p><p></p><p>Tab-delimited versions of this page are saved as Attachments</p>
     						 </ac:rich-text-body>
   							 </ac:macro>
  						</ac:rich-text-body>
						</ac:macro>
  						);
	return $pagecontent;
}

sub checkPII{
	my $location = shift;
	my $piistatus;
	
	if($location =~m/anonymous/){
		$piistatus = "Public";
	}
	elsif($location =~m/tcga4yeo/){
		$piistatus = "Protected";
	}
	elsif($location =~m/tcgajamboree/){
		$piistatus = 'Protected'
	}
	else{
		$piistatus = 'Unknown';
	}
	
	return $piistatus;
}

sub createURL{
	my $filesystem = shift;
	my $url;
	if($filesystem =~m/tcga4yeo/){
		$url = "https://tcga-data-secure.nci.nih.gov".$filesystem;
	}
	elsif($filesystem =~m/anonymous/){
		$url = "https://tcga-data.nci.nih.gov".$filesystem;
	}
	elsif($filesystem =~m/tcgajamboree/){
		#Jamboree Filesystem: /tcgafiles/ftp_auth/deposit_ftpusers/tcgajamboree   Web:https://tcga-data-secure.nci.nih.gov/tcgafiles/tcgajamboree
		my $startloc = index($filesystem,'/tcgajamboree');
		$filesystem = substr($filesystem,$startloc);
		$url = "https://tcga-data-secure.nci.nih.gov/tcgafiles".$filesystem
	}
	return $url
}

sub masterDiseaseList{
	my $master_disease_list = shift;
	my $dbuser = shift;
	my $dppass = shift;
	my $disease_abbrev;
	my $disease_name;
	
		my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: $DBI::errstr";

	#This query should get all MAF files that the database knows about.
	my $sql = qq(select disease_abbreviation,disease_name from disease);

	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute();

	$sth -> bind_columns(\$disease_abbrev,\$disease_name);
	while($sth -> fetch()){
		$master_disease_list->{$disease_abbrev} = $disease_name;
	}
	$dbh -> disconnect;
}

sub getDate{
	#This takes a full path to a file and returns the modification date.  NOT TO BE CONFUSED WITH getCurrentDate
	my $file=shift;
	my @abbr = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
	my $date_string = stat($file)->mtime;
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime($date_string);
	$year += 1900;
	$mon += 1;
	$date_string = $mday."-".$abbr[$mon]."-".$year;
	return $date_string;
}

sub addFilesystemFiles{
	my $master_disease_list = shift;
	my $new_files = shift;
	my $dbfile = shift;
	my $disease = "UNK";
	my $disease_name = "Unknown";
	my $filename;
	my $date;
	my $piistatus;
	my $latest = 3;
	my $md5 = "unknown";
	my $source = "filesystem";
	my $archive_name;
	my $url;
	my $status = "Filesystem only";
	
	foreach my $location (keys %$new_files){
		#find out which disease
		foreach my $abbrev (sort keys %$master_disease_list){
			if ($location =~m/$abbrev/){
				$disease = $abbrev;
				$disease_name = $master_disease_list->{$abbrev};
				last;
			}
			else{
				$disease = "UNK";
				$disease_name = "Unknown"
			}
		} #end foreach abbrev
		
		#parse the file name and archive name from the location
		my @tempnames = split(/\//,$location);
		$filename = pop(@tempnames);  #the file name should be the last in the array
		$archive_name = pop(@tempnames);
		
		#get the date
		$date = getDate($location);
		
		#get PII status
		$piistatus = checkPII($location);
		
		#get the URL
		$url = createURL($location);
		
		if($location =~m/tcgajamboree/){
			#There are a bunch of things we dont' know about jamboree files
			$latest = "4";
			$source = "jamboree";
			$archive_name = "Unknown";
			$status = "Jamboree Only";
		}
		
		#Add the record to SQLite
		updateSQLite("INSERT",$disease,$disease_name,$filename,$location,$status,$latest,$archive_name,$date,$md5,$piistatus,$source,$url,$dbfile);
	} #end of foreach location
	
}

sub getWiki{
	#This gets all the maf-ish files that are stored as attachements on the TCGA Members wiki space
	#This data is very different from the others since it is a result of a search on the wiki and information is limited
	#For wiki files, a URL is used in place of a location since they don't have a filesystem location
	
	my $existing_files = shift;
	my $master_disease_list = shift;
	my $dbfile = shift;
	
	my $function = "INSERT";
	my $disease = 'UNK';
	my $disease_name = 'Unknown';
	my $filename;
	my $location;
	my $status = 'wiki';
	my $latest = '5';
	my $archive = 'none';
	my $date = 'n/a';
	my $md5 = 'n/a';
	my $piistatus = 'unknown';
	my $source = 'wiki';
	my $tumor_count = "-1";
	my $normal_count = "-1";
	my $maf_version = "unknown";
	
	
	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";
	my $wiki = Confluence->new($url, $nciuser, $ncipass);
	my $query = ".maf";
	my %params = ('space'=>'TCGAM', 'type'=>'attachment');

	my $content = $wiki->search($query, \%params, RPC::XML::int->new(9000));
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql = qq(INSERT INTO maf_files (filesystem, filename, archive_name,status,deploy_date,md5,disease,disease_name,latest,piistatus,source,url,tumor_count,normal_count,version) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?));
	
	foreach (@$content){
		foreach my $key (keys %$_){
			my $filename = $_->{"title"};
			my $location = $_->{"url"};
		
			#Figure out if we've seen this before
			if(exists $existing_files->{$location}){
				last;  #we know of this file, don't do anything
			}
			else{
				#find out which disease
				foreach my $abbrev (sort keys %$master_disease_list){
					if ($filename =~m/$abbrev/){
						$disease = $abbrev;
						$disease_name = $master_disease_list->{$abbrev};
						last;
					}
					else{
						$disease = "UNK";
						$disease_name = "Unknown"
					}
				} #end foreach abbrev
				#Add the record to SQLite
				my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
				$sth->execute($location,$filename,$archive,$status,$date,$md5,$disease,$disease_name,$latest,$piistatus,$source,$url,$tumor_count,$normal_count,$maf_version);
			}	#End else if exists	
		}#End foreach keys
	}#End foreach content	
	$dbh->disconnect;
} #End subroutine

sub printTab{
	my $filename = shift;
	my $tab_content = shift;
	
	open(TAB,">",$filename) or die "Can't open file $filename: $!\n";
	print(TAB $tab_content);
	close TAB;
}