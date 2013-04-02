#!/usr/bin/perl
use warnings;
use strict;
use lib "/h1/pihltd/pihlprojects/MAFFinder/modules";
#use lib "/h1/pihltd/pihlprojects/MAFFinder/branches/dev/modules";
#use lib "/Users/pihltd/Documents/pihlprojects/MAFFinder/branches/dev/modules"; #this is only needed for development, can be turned off
use lib "/Users/pihltd/Documents/pihlprojects/MAFFinder/modules"; #this is only needed for development, can be turned off
#BEGIN {push @INC, "/h1/pihltd/pihlprojects/MAFFinder/branches/dev/modules";}
BEGIN {push @INC, "/h1/pihltd/pihlprojects/MAFFinder/modules";}
local $SIG{__DIE__} = sub{my @messages = @_;foreach(@messages){printOut(\*LOG,$_)} }; #redirect any die through printOut

#printUsage() unless exists($ENV{'TCGAUSER'});
#printUsage() unless exists($ENV{'TCGAPASS'});
#printUsage() unless exists($ENV{'NCIUSER'});
#printUsage() unless exists($ENV{'NCIPASS'});

#This program surveys the database and the file system for MAF files and then produces a report that can be 
#used to populate a wiki page
#

#Common currency data structure is a hash:
#The key is the disease:disease_name and the array is an array of $filename.":".$location.":".$status.":".$archive.":".$latest.":".$date;

use DBI;
use File::Find;
use File::stat;
use RPC::XML;
use Confluence;
use XML::Twig;
use File::HomeDir;

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
my $creds = File::HomeDir->my_home."/.perlinfo/info.xml";
my $parser = new XML::Twig(TwigRoots => $roots, TwigHandlers => $handlers, error_context => 1);
$parser->parsefile($creds);

my $logfile = File::HomeDir->my_home."/.maf/maf_log.txt";
open (LOG, ">", $logfile) or die "Can't open log file $logfile\n";

#my $dbuser = $ENV{'TCGAUSER'};
#my $dbpass = $ENV{'TCGAPASS'};
#my $nciuser = $ENV{'NCIUSER'};
#my $ncipass = $ENV{'NCIPASS'};

my %public_filesystem_mafs;
my %protected_filesystem_mafs;
my %current_maf;
my %old_maf;
my %db_file_list;
my @public_array;
my @private_array;
my %public_filesystem_only;
my %protected_filesystem_only;
my %public_filesystem_only_count;
my %protected_filesystem_only_count;
my %sample_counts;
my %current_line_count;
my %old_line_count;
my $oldheader = qq(<h1> Old MAF files</h1><p>The files listed in these tables are either obsolete (no longer being supported) or have been superseded by newer updates.  We're including these files here because they are maintained on the filesystem and are accessible to the TCGA community.</p>);
my $currentheader = qq(<h1>Current MAF Files</h1><p>The tables below shows the status of the current MAF files on deposit.</p>);
my $table_columns = "<tr><th>MAF File Name</th><th>Line Count</th><th>Deploy Status</th><th>Archive Name</th><th>Protection Status</th><th>Deploy Date</th><th>MD5</th><th>Deploy Location</th></tr>";
my $dojamboree = "n";
my $dodb = "n";
my $dofiles = "n";
my @jamboree_array;
my %jamboree_mafs;
my %jamboree_counts;
my $printstyle = 'direct';
my $printtarget = 'direct';
my %wiki_mafs; #Info from the wiki search is VERY limited, so in this hash the key is the filename and the data is the location.
my $wiki_header = "<tr><th>MAF File Name</th><th>Wiki location</th></tr>";
my $dowiki = "n";
my $wikiboth = "no";
my $new_content = "no";
my %full_file_list;

my $content;
my $full_content;

if($#ARGV >=0){
	for(my $i=0; $i<=$#ARGV; $i++){
		if($ARGV[$i] eq "-j"){
			$dojamboree = "y";
		}
		elsif($ARGV[$i] eq "-d"){
			$dodb = "y";
		}
		elsif($ARGV[$i] eq "-f"){
			$dofiles = "y";
		}
		elsif($ARGV[$i] eq "-m"){
			$dowiki = "y";
		}
		elsif($ARGV[$i] eq "-p"){
			$dodb = "y";
			$dofiles = "y";
		}
		elsif($ARGV[$i] eq "-a"){
			$dodb = "y";
			$dofiles = "y";
			$dojamboree = "y";
			$dowiki = "y"
		}
		elsif($ARGV[$i] eq "-w"){
			$printstyle = "wiki";
			$printtarget = "wiki";
		}
		elsif($ARGV[$i] eq "-wd"){
			$printstyle = "wiki";
			$printtarget = "wikidemo";
		}
		elsif($ARGV[$i] eq "-wa"){
			$printstyle = "wiki";
			$printtarget = "wikidemo";
			$wikiboth = "yes";
		}
		else{
			printUsage();
			exit;
		}
	}
}
else{
	printUsage();
	exit;
}

#Find all the MAF files the database knows about.  Note that db_file_list is a simple hash
printOut(\*LOG, "Getting file from database");
getFiles(\%current_maf, \%old_maf, \%db_file_list, \%full_file_list);

#For any disease with a current MAF file, get the number of Tumors and Normal samples (NOT PAIRED AT THIS POINT)
if($dodb eq "y"){
	printOut(\*LOG, "Determining tumor and normal sample counts");
	getCases(\%current_maf,\%sample_counts);
}

#Find out how many lines there are in the file.  Should only be one or two off of the number of mutations
if($dodb eq "y"){
	printOut(\*LOG, "Determinig number of lines");
	lineCount(\%current_maf,"y",\%current_line_count);
	lineCount(\%old_maf,"y", \%old_line_count);
}

#Scan the anonymous file system for existing *.maf files and convert from array to hash
if($dofiles eq "y"){
	printOut(\*LOG, "Searching anonymous file system");
	findFileSystemFiles('/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/',\@public_array);
	printOut(\*LOG, "Searching tcga4yeo file system");
	findFileSystemFiles('/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/',\@private_array);
}
if($dojamboree eq "y"){
	printOut(\*LOG, "Searching jamboree file system");
	findFileSystemFiles('/tcgafiles/ftp_auth/deposit_ftpusers/tcgajamboree',\@jamboree_array);
	foreach(@jamboree_array){
		$full_file_list{$_} = 1;
	}
}
if($dowiki eq "y"){
	printOut(\*LOG, "Searching TCGA Members wiki");
	getWiki(\%wiki_mafs, \%full_file_list);
}

#Create a hash like the ones getFiles produces
if($dofiles eq "y"){
	printOut(\*LOG, "Building public hash file");
	buildHash(\@public_array,\%public_filesystem_mafs);
	printOut(\*LOG, "Building protected hash file");
	buildHash(\@private_array, \%protected_filesystem_mafs);
}
if($dojamboree eq "y"){
	printOut(\*LOG, "Building jamboree hash file");
	buildHash(\@jamboree_array, \%jamboree_mafs);
}

#Compare the filesystem files to the database files and return only those on the filesystem
if($dofiles eq "y"){
	printOut(\*LOG, "Sorting out unique file system MAF files");
	compareFiles(\%public_filesystem_mafs, \%db_file_list, \%public_filesystem_only, \%full_file_list);
	compareFiles(\%protected_filesystem_mafs, \%db_file_list, \%protected_filesystem_only, \%full_file_list);
}

#Find out how many lines are in the filesystem only files
if($dofiles eq "y"){
	lineCount(\%public_filesystem_only,"n", \%public_filesystem_only_count);
	lineCount(\%protected_filesystem_only,"n",\%protected_filesystem_only_count);
}
if($dojamboree eq "y"){
	lineCount(\%jamboree_mafs, "n", \%jamboree_counts);
}
#----------------Check and see if there is any need to print---------------#
$new_content = newCheck(\%full_file_list);
if($new_content eq "no"){
	printOut(\*LOG, "No new content, public page will not be printed");
}
else{
	printOut(\*LOG,"Print parameters:\nWikiboth:\t$wikiboth\nPrintStyle:\t$printstyle\nPrintTarget\t$printtarget");
}

#-----Start building the strings for printing-------------#
$content = printHeader($printtarget);
$full_content = $content;

if($dodb eq "y"){
	printOut(\*LOG,"Building database content");
	print("Printing target is $printtarget\n");
	my $tempcontent = printTable($currentheader, $table_columns, \%current_maf,"y", \%sample_counts, \%current_line_count, $printtarget);
	$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
if($dofiles eq "y"){
	printOut(\*LOG,"Building public filesystem content");
	 my $tempcontent = printTable("<h1>Public MAF Files not in database</h1>", $table_columns, \%public_filesystem_only, "n", \%public_filesystem_only_count,$printtarget);
	$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
if($dojamboree eq "y"){
	printOut(\*LOG,"Building Jamboree content");
	my $tempcontent = printTable("<h1> MAF Files on Jamboree</h1>", $table_columns, \%jamboree_mafs, "n",\%jamboree_counts,$printtarget);
	#$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
if($dofiles eq "y"){
	printOut(\*LOG,"Building protected filesystem content");
	my $tempcontent = printTable("<h1>Protected MAF Files not in database</h1>", $table_columns, \%protected_filesystem_only, "n", \%protected_filesystem_only_count,$printtarget);
	$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
if($dowiki eq "y"){
	printOut(\*LOG,"Building wiki content");
	my $tempcontent = "<h1>MAF files on the TCGA Members Wiki</h1>".$wiki_header;
	foreach my $file (keys %wiki_mafs){
		$tempcontent = $tempcontent."<tr><td><a href=\"".$wiki_mafs{$file}."\">".$file."</a></td></tr>";
	}
	#$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
printOut(\*LOG,"Building publication content");
my $tempcontent1 = printPubs($printtarget);
$content = $content.$tempcontent1;
$full_content = $full_content.$tempcontent1;
if($dodb eq "y"){
	my $tempcontent = printTable($oldheader, $table_columns, \%old_maf, "n", \%old_line_count,$printtarget);
	$content = $content.$tempcontent;
	$full_content = $full_content.$tempcontent;
}
if($printstyle eq "wiki"){
	if($new_content eq "yes"){ #only print the public bit if there is new data
		if($wikiboth eq "no"){
			printOut(\*LOG,"Printing public wiki only");
			printWiki($content, $nciuser, $ncipass, $printtarget);
		}
	}
	if($wikiboth eq "yes"){
		if($new_content eq "yes"){ #only print the public bit if there is new data
			$printtarget = "wiki";
			printOut(\*LOG,"Printing public wiki");
			printWiki($content, $nciuser, $ncipass, $printtarget);
		}
	}
	$printtarget = "wikidemo";
	printOut(\*LOG,"Printing full report\n");
	printWiki($full_content, $nciuser, $ncipass, $printtarget); #always print the private page
}


###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################

sub getFiles{
	#Returns a hash of arrays.  The key is the disease:disease_name and the array is an array of $filename.":".$location.":".$status.":".$archive.":".$latest.":".$date;
	my $current_hash = shift;
	my $old_hash = shift;
	my $file_list = shift;
	my $full_file_list = shift;
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
	my @counter;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: 
$DBI::errstr";

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

	#sort the MAF files into their respective diseases
	while($sth -> fetch()){
		my $key = $disease.":".$disease_name;
		
		#work on the date
		my @temp = split(/ /,$date);
		$date = $temp[0];
		
		#Determine if public or protected
		if($location =~m/tcga4yeo/){
			$piistatus = 'Protected';
		}
		elsif($location =~m/anonymous/){
			$piistatus = 'Public';
		}
		else{
			$piistatus = "Unkown"
		}
		
		#Construct a list of the full paths to MAF files.  These will be used later to compare to a search of the filesystem.
		my $full_file_path = getFullPath($location, $filename);
		$file_list->{$full_file_path} = "db_file";
		$full_file_list->{$full_file_path} = 1;

		my $data = $filename.":".$status.":".$archive.":".$piistatus.":".$date.":".$md5.":".$location;

		if($latest ==1){
			if(exists($current_hash->{$key})){
				push(@{$current_hash->{$key}}, $data);
				@counter = @{$current_hash->{$key}};
			}
			else{
				@temp = ();
				push(@temp, $data);
				$current_hash->{$key} = [@temp];
				@counter = @{$current_hash->{$key}};
			}
		}
		else{
			if(exists($old_hash->{$key})){
				push(@{$old_hash->{$key}}, $data);
				@counter = @{$old_hash->{$key}};
			}
			else{
				@temp = ();
				push(@temp, $data);
				$old_hash->{$key} = [@temp];
				@counter = @{$old_hash->{$key}};
			}
		}
	} #end while
	$sth->finish;
	$dbh->disconnect;
}

sub getCases{
	#find out how many cases there are per disease
	my $file_hash = shift;
	my $counts = shift;
	
	my $disease;
	my $cases;
	my $sample_type;
	my $sth;
	
	my $dbh = DBI->connect("dbi:Oracle:host= ncidb-tcgas-p;sid=TCGAPRD;port=1652","$dbuser/$dbpass","") || die "Database connection not made: 
$DBI::errstr";
	
	my $sql = qq(select distinct d.disease_abbreviation as disease,count(distinct(b.participant)) as cases ,
	case
	when b.sample_type_code  in ('10','11','12','13','14') then 'Normal'
	when b.sample_type_code  in ('01','02','03','04','05','06','07') then 'Tumor'
	else 'Cell Line' end as sample_type
	from shipped_biospecimen_breakdown b,  tss_to_disease t, disease d
	where b.is_viewable=1
	and   b.tss_code=t.tss_code
	and   t.disease_id=d.disease_id
	and d.disease_abbreviation = ?
	group by d.disease_abbreviation,case
	when b.sample_type_code  in ('10','11','12','13','14') then 'Normal'
	when b.sample_type_code  in ('01','02','03','04','05','06','07') then 'Tumor'
	else 'Cell Line' end
	order by disease);

	foreach my $key(keys %$file_hash){
		#This should get the counts for just the diseases with MAF files
		my @temp = split(/:/,$key);
		my $abbrev = $temp[0];
		$sth = $dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
		$sth -> execute($abbrev);
		$sth -> bind_columns(undef,\$disease,\$cases,\$sample_type);
		while ($sth -> fetch()){
			$key = $disease.":".$sample_type;
			#the next two if statements throw out everythign not normal or tumor
			if($sample_type =~m/Normal/){
				$counts->{$key} = $cases;
			}
			elsif($sample_type =~m/Tumor/){
				$counts->{$key} = $cases;
			}
		}
	}
	$sth->finish;
	$dbh->disconnect;
}

sub printTable{

	my $samples;
	my $disease_abbrev;
	my $disease_long;
	my $count_tumor_key;
	my $count_normal_key;
	my $countkey;
	my $lines;
	my $content;
	
	my $section_header = shift;
	my $table_header = shift;
	my $working_hash = shift;
	my $disease_print = shift;
	if($disease_print eq "y"){
		$samples = shift;
	}
	my $line_count_hash = shift;
	my $target = shift;
	my $counter = keys %$line_count_hash;
	
	if($target eq "direct"){	
		print($section_header);
	}
	$content = $section_header;
	foreach my $key(keys %$working_hash){
		my @temp = split(/:/, $key);
		$disease_abbrev = $temp[0];
		$disease_long = $temp[1];
		$count_tumor_key = $disease_abbrev.':Tumor';
		$count_normal_key = $disease_abbrev.':Normal';
		if($target eq "direct"){
			print("<h3><p> $disease_abbrev:  $disease_long</p></h3>");
		}
		$content = $content."<h3>$disease_abbrev:  $disease_long</h3>";
		if($disease_print eq "y"){
			if($target eq "direct"){
				print("<p>Tumor Samples: $samples->{$count_tumor_key} Normal Samples: $samples->{$count_normal_key} (Note that these counts are for the study and the MAF file may have fewer)</p>");
				print("<tr>");
			}
			$content = $content."<p>Tumor Samples: $samples->{$count_tumor_key} Normal Samples: $samples->{$count_normal_key} (Note that these counts are for the study and the MAF file may have fewer)</p>";
			$content = $content."<tr>";
		}
		if($target eq "direct"){
			print($table_header);
		}
		$content = $content."<table>".$table_header;
		my @temp2 = @{$working_hash->{$key}};
		foreach my $row (@temp2){
			$content = $content."<tr>";
			my @temp3 = split(/:/, $row);
			
			#take a quick detour and insert the line count into the temp3 array
			my $file = $temp3[0];
			my $path = $temp3[6];
			if($disease_print eq "y"){ #this is used if the file is pulled from the database.
				$countkey = getFullPath($path,$file);
			}
			else{ #this is used if the files are pulled from the filesystem
				$countkey = $path;
			}
			
			#the following if/else is needed because for some of the obsolete files in the database, the deploy location is a
			# .tar.gz but for others correct, so if the key doesn't exist, generate a new one.
			if(exists $line_count_hash->{$countkey}){
				$lines = $line_count_hash->{$countkey};
			}
			else{
				$countkey = getFullPath($path,$file);
				$lines = $line_count_hash->{$countkey};
			}
			
			#Now that the key is sorted out, splice should make sense
			splice(@temp3,1,0,$lines);
			
			#we now return you to your regularly scheduled printing
			foreach my $value (@temp3){
				if($target eq "direct"){
					print("<td>$value</td>");
				}
				$content=$content."<td>$value</td>";
			}
			if($target eq "direct"){
				print("</tr>");
			}
			$content = $content."</tr>";
		}# end of foreach my $row
		$content = $content."</table>"
	}
	return $content;
}

sub lineCount{
#This needs to distinguish between database files (need to build full path) and filesystem (already have full path)
	my $file_hash = shift;
	my $havepath = shift; #expect a y if database, n if filesystem
	my $count_hash = shift;
	my $linecount;
	my @temp;
	my $filename;
	my $deploy_location;
	my $fullpath;
	my @linearray;
	
	foreach my $key (keys %$file_hash){
			@linearray = @{$file_hash->{$key}};
			foreach (@linearray){
				if ($havepath eq "n"){
					@temp = split(/:/, $_);
					$fullpath = pop(@temp);
				}
				else{
					@temp = split(/:/, $_);
					$filename = $temp[0];
					$deploy_location = $temp[6];
					$fullpath = getFullPath($deploy_location, $filename);
				}
				if(-e $fullpath){ #test to see if the file exists before tyring to open it
					open (FILE, $fullpath) or die "Can't open file $fullpath: $!\n";
					my @filearray = <FILE>;
					$linecount = @filearray;
				} #if
				else{
					$linecount = 'n/a';
					$fullpath = $deploy_location;
				}#else
				$count_hash->{$fullpath} = $linecount;				
			} #foreach linearray
	}#foreach key
}

sub getFullPath{
	my $location = shift;
	my $file = shift;
	
	#Bust up the location string
	my @path_array = split(/\//, $location);
	my $arch_name = pop(@path_array);
	my @archive_array = split(/\./,$arch_name);
	
	# The deploy location is a .tar.gz so to get the archive name, remove the gz, then the tar
	pop(@archive_array);
	pop(@archive_array);
	
	#Rebuild the archive name
	my $new_name = "";
	foreach my $bit(@archive_array){
		$new_name = $new_name.'.'.$bit;
	}
	$new_name = substr($new_name,1); #get rid of the leading .
	
	#Now rebuild the full path to the MAF file
	my $full = "";
	foreach my $entry (@path_array){
		$full = $full.$entry."/"
	}
	$full = $full.$new_name."/".$file;
	return $full
}

sub findFileSystemFiles{
	my $searchdir = shift;
	my $file_array = shift;
	#-f tests for if a file, /\.maf$/ tests if it ends in .maf, and if both those are true, push into the file hash
	find(sub{ push @$file_array, $File::Find::name if( -f and /\.maf$/)}, $searchdir);
}

sub compareFiles{
	my $filesystem = shift;
	my $db = shift; #This is a simple hash with full file names as key
	my $survivor = shift; #This also needs to be a hash of arrays.  Key is disease, but array contains full file paths
	my $full_file_list = shift; #add anything that is only on the file system to this
	my $key2;
	my @temparray;
	my @temp2;
	my @temp4;

	
	foreach my $key(keys %$filesystem){
		@temparray = @{$filesystem->{$key}};
		foreach (@temparray){
			@temp2 = split(/:/, $_);
			$key2 = pop(@temp2);
			if(exists($db->{$key2})){
				next;
			} #if
			else{
				$survivor->{$key} = [@temparray];
				$full_file_list->{$key} = 1;
			} #else
		} #foreach data
	} #foreach key
}

sub buildHash{
	my $working_array = shift;
	my $working_hash = shift;
	my $keycount = keys %$working_hash;
	my $status = "Filesystem Only";
	my $date = "TBD";
	my $disease;
	my $disease_name;
	my $piistatus;
	my $key;
	my $data;
	my @temparray;
	my @temp2;
	my $md5value = "unknown"; #May need to create a routine to calculate md5, but this will do for now.
	
	foreach (@$working_array){
		my $location = $_;
		my @temp = split(/\//,$_);
		my $filename = pop(@temp);
		my $archive_name = pop(@temp);
		
		#Sort out what disease.  Need to think of a better way to do this other than brute force
		#
		# General file name format: <institute>_<Disease>.<general crap>.maf
		# There are some significant exceptions to this rule however
		#
		if($filename =~m/OV/){
			$disease = "OV";
			$disease_name = "Ovarian serous cystadenocarcinoma";
		}
		elsif($filename =~m/GBM/){
			$disease = "GBM";
			$disease_name = "Glioblastoma multiforme";
		}
		elsif($filename =~m/LAML/){
			$disease = "LAML";
			$disease_name = "Acute Myeloid Leukemia";
		}
		elsif($filename =~m/BLCA/){
			$disease = "BLCA";
			$disease_name = "Bladder Urothelial Carcinoma";
		}
		elsif($filename =~m/LGG/){
			$disease = "LGG";
			$disease_name = "Brain Lower Grade Glioma";
		}
		elsif($filename =~m/BRCA/){
			$disease = "BRCA";
			$disease_name = "Breast invasive carcinoma";
		}
		elsif($filename =~m/CESC/){
			$disease = "CESC";
			$disease_name = "Cervical squamous cell carcinoma and endocervical adenocarcinoma";
		}
		elsif($filename =~m/LCLL/){
			$disease = "LCLL";
			$disease_name = "Chronic Lymphocytic Leukemia";
		}
		elsif($filename =~m/COAD/){
			$disease = "COAD";
			$disease_name = "Colon adenocarcinoma";
		}
		elsif($filename =~m/ESCA/){
			$disease = "ESCA";
			$disease_name = "Esophageal carcinoma";
		}
		elsif($filename =~m/HNSC/){
			$disease = "HNSC";
			$disease_name = "Head and Neck squamous cell carcinoma";
		}
		elsif($filename =~m/KIRC/){
			$disease = "KIRC";
			$disease_name = "Kidney renal clear cell carcinoma";
		}
		elsif($filename =~m/KIRP/){
			$disease = "KIRP";
			$disease_name = "Kidney renal papillary cell carcinoma";
		}
		elsif($filename =~m/LIHC/){
			$disease = "LIHC";
			$disease_name = "Liver hepatocellular carcinoma";
		}
		elsif($filename =~m/LUAD/){
			$disease = "LUAD";
			$disease_name = "Lung adenocarcinoma";
		}
		elsif($filename =~m/LUSC/){
			$disease = "LUSC";
			$disease_name = "Lung squamous cell carcinoma";
		}
		elsif($filename =~m/DLBC/){
			$disease = "DLBC";
			$disease_name = "Lymphoid Neoplasm Diffuse Large B-cell Lymphoma";
		}
		elsif($filename =~m/LNNH/){
			$disease = "LNNH";
			$disease_name = "Lymphoid Neoplasm Non-Hodgkins Lymphoma";
		}
		elsif($filename =~m/PAAD/){
			$disease = "PAAD";
			$disease_name = "Pancreatic adenocarcinoma";
		}elsif($filename =~m/PRAD/){
			$disease = "PRAD";
			$disease_name = "Prostate adenocarcinoma";
		}
		elsif($filename =~m/READ/){
			$disease = "READ";
			$disease_name = "Rectum adenocarcinoma";
		}
		elsif($filename =~m/SALD/){
			$disease = "SALD";
			$disease_name = "Sarcoma";
		}
		elsif($filename =~m/SKCM/){
			$disease = "SKCM";
			$disease_name = "Skin Cutaneous Melanoma";
		}
		elsif($filename =~m/STAD/){
			$disease = "STAD";
			$disease_name = "Stomach adenocarcinoma";
		}
		elsif($filename =~m/THCA/){
			$disease = "THCA";
			$disease_name = "Thyroid carcinoma";
		}
		elsif($filename =~m/UCEC/){
			$disease = "UCEC";
			$disease_name = "Uterine Corpus Endometrioid Carcinoma";
		}
		else{
			$disease = "WTF";
			$disease_name = "Whiskey Tango Foxtrot";
		}
		
		#Figure out protected status
		if($location =~m/tcga4yeo/){
			$piistatus = 'Protected';
		}
		elsif($location =~m/anonymous/){
			$piistatus = 'Public';
		}
		else{
			$piistatus = "Unkown"
		}
		
		#get the modification date;
		$date = getDate($location);
		
		#build the key and data
		$key = $disease.":".$disease_name;
		$data = $filename.":".$status.":".$archive_name.":".$piistatus.":".$date.":".$md5value.":".$location;
		
		#Now build the hash structure
		if(exists $working_hash->{$key}){
			push(@{$working_hash->{$key}}, $data);
		}
		else{
			@temp2 = ();
			push(@temp2, $data);
			$working_hash->{$key} = [@temp2];
		}
	}
}

sub getDate{
	#This takes a full path to a file and returns the modification date.
	my $file=shift;
	my @abbr = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
	my $date_string = stat($file)->mtime;
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime($date_string);
	$year += 1900;
	$mon += 1;
	$date_string = $mday."-".$abbr[$mon]."-".$year;
	return $date_string;
}

sub getWiki{
	#This gets all the maf-ish files that are stored as attachements on the TCGA Members wiki space
	#This hash is very different from the others since it is a result of a search on the wiki and information is limited
	my $hash = shift;
	my $full_file_list = shift;
	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";
	my $wiki = Confluence->new($url, $nciuser, $ncipass);
	my $query = ".maf";
	my %params = ('space'=>'TCGAM', 'type'=>'attachment');

	my $content = $wiki->search($query, \%params, RPC::XML::int->new(9000));
	foreach (@$content){
	foreach my $key (keys %$_){
		my $title = $_->{"title"};
		my $location = $_->{"url"};
		if($title =~m/.maf$/){
			$hash->{$title} = $location;
			$full_file_list->{$title} = 1;
		}
		elsif($title =~m/.maf.xlsx$/){
			$hash->{$title} = $location;
			$full_file_list->{$title} = 1;
		}
		elsif($title =~m/.maf.xls$/){
			$hash->{$title} = $location;
			$full_file_list->{$title} = 1;
		}
	}
}
	
}

sub printHeader{
	my $target = shift;
	my $pagecontent;
	my @months = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = 1900 + $yearOffset;
	my $theDate = "$months[$month] $dayOfMonth, $year";
	if($target eq "direct"){
		print  qq(<ac:macro ac:name="section">
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
       							 <p><strong>Updated on  $theDate</strong></p><p>This page is a summary of all the <a href="https://wiki.nci.nih.gov/x/M5ZXAg">Mutation Annotation Format (MAF)</a> files available at TCGA through the Data Portal or FTP access, and is a step towards allowing the TCGA community to understand what MAF files exist. See [TCGA MAF Dashboard|http://bit.ly/mut-data-flow] for more information.  The files in the Current section are those that are considered the most up to date.  However, some MAF files are being maintained outside of the normal TCGA system, and those are in the Not in database sections.  The Old MAF files section represents a historical record of past MAF file submissions. In some cases, these files are obsolete and no longer supported and in others they have been superseded by later submissions.  In addition to the MAF files available through the portal, there are MAF files associated with TCGA publications, and these are available for download from the publication pages. <strong>Be aware that MAF files change on a regular basis, so this summary may differ from what you find.</strong>  For a quick tutorial on the different ways to access MAF files, visit <a href="https://wiki.nci.nih.gov/x/24q4Aw">Accessing MAF Files.</a></p><p></p><p>Any files marked as "Controlled" in the <strong>Access Type</strong> column will require appropriate access privileges.  See <a href="http://tcga-data.nci.nih.gov/tcga/tcgaAccessTiers.jsp">Access Tiers on the Data Portal</a> for instructions on how to gain access to controlled data.  Files marked as "Open" have no access restrictions.</p><p></p><p>The Tumor and Normal sample counts given for the current MAF files represents the counts from the entire study.  It is possible that not all samples are represented in the MAF files for that disease.</p>
     						 </ac:rich-text-body>
   							 </ac:macro>
  						</ac:rich-text-body>
						</ac:macro>
  						);
	
	}
	$pagecontent = qq(<ac:macro ac:name="section">
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
       							 <p><strong>Updated on  $theDate</strong></p><p>This page is a summary of all the <a href="https://wiki.nci.nih.gov/x/M5ZXAg">Mutation Annotation Format (MAF)</a> files available at TCGA through the Data Portal or FTP access, and is a step towards allowing the TCGA community to understand what MAF files exist. See [TCGA MAF Dashboard|http://bit.ly/mut-data-flow] for more information.  The files in the Current section are those that are considered the most up to date.  However, some MAF files are being maintained outside of the normal TCGA system, and those are in the Not in database sections.  The Old MAF files section represents a historical record of past MAF file submissions. In some cases, these files are obsolete and no longer supported and in others they have been superseded by later submissions.  In addition to the MAF files available through the portal, there are MAF files associated with TCGA publications, and these are available for download from the publication pages. <strong>Be aware that MAF files change on a regular basis, so this summary may differ from what you find.</strong>  For a quick tutorial on the different ways to access MAF files, visit <a href="https://wiki.nci.nih.gov/x/24q4Aw">Accessing MAF Files.</a></p><p></p><p>Any files marked as "Controlled" in the <strong>Access Type</strong> column will require appropriate access privileges.  See <a href="http://tcga-data.nci.nih.gov/tcga/tcgaAccessTiers.jsp">Access Tiers on the Data Portal</a> for instructions on how to gain access to controlled data.  Files marked as "Open" have no access restrictions.</p><p></p><p>The Tumor and Normal sample counts given for the current MAF files represents the counts from the entire study.  It is possible that not all samples are represented in the MAF files for that disease.</p>
     						 </ac:rich-text-body>
   							 </ac:macro>
  						</ac:rich-text-body>
						</ac:macro>
  						);
	return $pagecontent;
}

sub printPubs{
	my $target = shift;
	my $content;
	$content= qq({anchor:pub_mafs}
	h1. Publication MAF Files
	h3. OV: Ovarian serous cystadenocarcinoma
	[Integrated Genomic Analyses of Ovarian Carcinoma|http://tcga-data.nci.nih.gov/docs/publications/ov_2011/] (Nature, Volume 474 Number 7353, June 30, 2011)\n
	h3. GBM: Glioblastoma multiforme
	[Comprehensive genomic characterization defines human glioblastoma genes and core pathways|http://tcga-data.nci.nih.gov/docs/publications/gbm_2008/] (Nature, Volume 455 Number 7209, September 4, 2008)\n);

if($target eq "direct"){
	print qq({anchor:pub_mafs}
	h1. Publication MAF Files
	h3. OV: Ovarian serous cystadenocarcinoma
	[Integrated Genomic Analyses of Ovarian Carcinoma|http://tcga-data.nci.nih.gov/docs/publications/ov_2011/] (Nature, Volume 474 Number 7353, June 30, 2011)\n
	h3. GBM: Glioblastoma multiforme
	[Comprehensive genomic characterization defines human glioblastoma genes and core pathways|http://tcga-data.nci.nih.gov/docs/publications/gbm_2008/] (Nature, Volume 455 Number 7209, September 4, 2008)\n);
}

	return $content;
}

sub printUsage{
	print("Usage:\n -j include Jamboree\n -d include database files\n -f include filesystem files\n -m include TCGA Members wiki files\n -p include database and filesystem (-d -f)\n -a include all(-d -f -j -m) \n");
	print("-w to print directly to MAF wiki page\n-wd print directly to test MAF wiki page under Todd's Mess\n-wa print both MAF wiki page and full MAF report page\n");
	print("Database username and password must be set in the TCGAUSER and TCGAPASS environment variables respectfully\n");
	print("Confluence username and password must be set in the NCIUSER and NCIPASS environment variables respectfully\n");
}

sub printWiki{
	my $content = shift;
	my $user =shift;
	my $pass = shift;
	my $target = shift;
	my $space;
	my $title;
	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";

	my $wiki = Confluence->new($url, $user, $pass);
	
	if($target eq "wikidemo"){
		$space = "TCGAproject";
		$title = "Full MAF Report";
		printOut(\*LOG,"Printing to demo page Full MAF Report\n");
	}
	elsif($target eq "wiki"){
		$space = "TCGA";
		$title = "TCGA MAF Files";
	}
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
	
	$wiki = Confluence->new($url, $user, $pass);
	$wiki->storePage(\%wikihash);
	$wiki->logout();
}

sub newCheck{
	my $new = shift;
	my $result = "no";
	my $file = File::HomeDir->my_home."/.maf/maf.txt";
	my %old;
	open(FILE, $file) or die "Can't open file $file: $!\n";
	#print("Should be reading maf.txt next");
	while(<FILE>){
		chomp $_;
		#print("Saving old $_\n");
		$old{$_} = 1;
	}
	close(FILE);
	foreach my $key (keys %$new){
		chomp $key;
		#print("Comparing $key and $old{$key}\n");
		if(exists $old{$key}){
			next;
		}
		else{
			$result = "yes";
		}
	}
	
	if($result eq "yes"){
		open(OUT, ">", $file);
		foreach my $key (keys %$new){
			print(OUT $key."\n");
		}
		close(OUT);
	}
	return $result;
}

sub printOut{
	my $fh = shift;
	my $entry = shift;
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = $yearOffset + 1900;
	$month++;
	print($fh "$month/$dayOfMonth/$year $hour:$minute:$second\t$entry\n");
}