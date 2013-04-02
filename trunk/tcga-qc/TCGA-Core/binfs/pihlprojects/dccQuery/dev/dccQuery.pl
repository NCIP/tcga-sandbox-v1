#!/usr/bin/perl
#dccQuery tool for command line interaction with the V2 web services

use strict;
use warnings;

use XML::Twig;
use WWW::Mechanize;
use Getopt::Std;
use File::Temp qw(tempfile);

my $result_num;
my %shared_links; #hash with result number as key and shared link as data
my %shared_status; #hash with result number as key and status as data
my %archive_hash; #hash with result number as key and archive name as data
my %filename_hash; #hash with result number as key and file name as data
my $counter = 0;
my $maxgroup = 1; #for downloading group queries, this sets the max number of results per group
my %grouptype; #This will set the directory structure for downloads by group


#parse the command line to get the server and query
my ($baseURL,$server,$user,$pass,$query) = doARGV(\@ARGV);
#print("URL:\t$baseURL\nServer:\t$server\nUser:\t$user\nPass:\t$pass\nQuery:\t$query\n");
#exit;



#Look at the query and see if it is normal, facet or group
my $parsetype = "file";
if($query =~m/facet/){
	$parsetype = 'facet';
}
elsif($query =~m/group/){
	$parsetype = 'group';
}

#turn off host verification since it causes Preview to hang.
my $ua = WWW::Mechanize->new('ssl_opts' =>{'verify_hostname'=>0});

#set up the temp file to hold the results.  Not strictly needed here, but someone may drop a big query
my $dir = "\.";
my ($fh,$filename) = tempfile(DIR=>$dir, SUFFIX=>'.tmp',UNLINK=>1);

$ua->get($baseURL.$query);
if($ua->success()){
	$ua->save_content($filename);
($result_num,$maxgroup) = fileParse($parsetype,$filename,\%shared_links,\%shared_status,\%archive_hash,\%filename_hash,$counter);
}
else{
	die "Query failed\n";
}

close $fh;

#Now ask if they want to download All, or any of the files

if($parsetype eq "facet"){
	# Faceting doesn't really lead to a download, so just quit
	print("Downloading files from a facet query not currently supported\n");
	exit;
}
else{
	fileDownload($parsetype,\%shared_links,\%shared_status,\%archive_hash,\%filename_hash,$baseURL,$query,$result_num,$user,$pass,$server,$maxgroup,\%grouptype);
}

###########################################
#                                         #
#       Subroutines                       #
#                                         #
###########################################

sub doARGV{
	#This grabs whatever the user put on the command line, determines what server and puts the rest of it into a single string
	my %options = ();
	getopts("dau:p:",\%options);
	my $args = shift;
	my $query = "";
	#my $baseurl;
	my @data;
	my $user = "none";
	my $pass = "none";
	
	if(exists $options{a}){
		push(@data,"https://tcgav2-data-sqa2.nci.nih.gov/data-browser/search/tcga/?q=");
		push(@data,"https://tcgav2-data-sqa2.nci.nih.gov/");
	}
	elsif(exists $options{d}){
		push(@data,"https://tcgav2-data-proto.nci.nih.gov/data-browser/search/tcga/?q=");
		push(@data,"https://tcgav2-data-proto.nci.nih.gov/");
	}
	else{
		print("Usage: dccQuery.pl [-d|-a]-u username -p password <query string>\n-d: Use Preview server\n-a: Use QA2 server\n");
		die;
	}
	if(exists $options{u}){
		$user = $options{u};
	}
	if(exists $options{p}){
		$pass = $options{p};
	}
	push(@data,$user);
	push(@data,$pass);
	for(my $i = 0; $i< scalar @{$args}; $i++){
		if($i == 1){
			$query = @$args[$i];
		}
		else{
			$query = $query." ".@$args[$i];
		}
	}
	push(@data,$query);
	return(@data);
}

sub fileParse{
	#This just parses out the basic file XML and prints it.
	my $parsetype = shift;
	my $xml = shift;
	my $links = shift;
	my $status = shift;
	my $archives = shift;
	my $files = shift;
	my $counter = shift;
	
	my $query;
	my $resp_count;
	my $value;
	my $attvalue;
	my $maxparse = 1;
	my $groupvalue;
	my $root;
	my $handlers;
	my @values;
	
	if($parsetype eq "file"){
		$root = {'response' => 1};
		$handlers = { 'lst/lst/str' => sub{$query = $_ -> text; print("Query:\t$query\n");},
					'result' => sub{$resp_count = $_->att('numFound'); print("Result Count:\t$resp_count\n");},
					'result/doc/str' => sub{nodeHandler(@_,$links,$status,$archives,$files,$counter,"true");},
					'result/doc' => sub{$counter++;print("\n");}
					};
		
	}
	elsif($parsetype eq "group"){
		$root = {'response' => 1};
		$handlers = { 'lst/lst/str' => sub{$query = $_ -> text; $attvalue = $_->att('name'); if ($attvalue eq "q"){print("Query:\t$query\n")};},
				'lst/lst/arr/lst/str' => sub{$groupvalue = $_ -> text;},
				'lst/lst/arr/lst/result' => sub{$resp_count = $_->att('numFound'); print("Group:\t$groupvalue\tResult Count:\t$resp_count\n");print("RespCount:\t$resp_count\nMaxParse:\t$maxparse\n");if($resp_count > $maxparse){$maxparse = $resp_count};},
				'lst/lst/arr/lst/result/doc/str' => sub{$value = $_ -> text;$attvalue=$_->att('name');print("$attvalue:\t$value\n");},
				'lst/lst/arr/lst/result/doc' => sub{print("\n");}
				};
	}
	elsif($parsetype eq "facet"){
		$root = {'response' => 1};
		$handlers = { 'lst/lst/str' => sub{$query = $_ -> text; $attvalue = $_->att('name'); if ($attvalue eq "q"){print("Query:\t$query\n")};},
				'result' => sub{$resp_count = $_->att('numFound'); print("Result Count:\t$resp_count\n");},
				'lst/lst/lst/int' => sub{$value = $_ -> text;$attvalue=$_->att('name');print("$attvalue:\t$value\n");},
				};
	}
	
	
	my $parser = new XML::Twig(TwigRoots => $root, TwigHandlers=> $handlers, error_context => 1);
	$parser->parsefile($xml);
	push(@values,$resp_count);
	push(@values,$maxparse);
	return @values;
}

sub nodeHandler{
	my($parser, $node, $link_hash, $status_hash, $archive_hash, $file_hash, $counter,$print) = @_;
	
	my $value = $node -> text;
	my $attribute = $node->att('name');
	if($print eq "true"){
		print("$attribute:\t$value\n");
	}
	
	if($attribute eq 'shared_link'){
		$link_hash->{$counter} = $value;
	}
	elsif($attribute eq 'access_level'){
		$status_hash->{$counter} = $value;
	}
	elsif($attribute eq 'archive_name'){
		$archive_hash->{$counter} = $value;
	}
	elsif($attribute eq 'file_name'){
		$file_hash->{$counter} = $value;
	}
	elsif($attribute eq 'groupValue'){
		return $value;
	}
}

sub downloadThis{
	my $server = shift;
	my $links = shift;
	my $status = shift;
	my $files = shift;
	my $user = shift;
	my $pass = shift;
	my $downloaddir = shift;
	
	my $URL;
	
	#Now do the heavy downloading
	my $publicURL = $server."ws/resource/file/retrieve/key/";
	my $privateURL = $server."ws/resource/file/retrieve/protected/key/";
	my $ua = WWW::Mechanize->new('ssl_opts' =>{'verify_hostname'=>0});
	
	foreach my $key (sort keys %$links){
		#All unmigrated files have a download link that starts with #
		if($links->{$key} =~m/#/){
			print("Aborting download of non-migrated file $files->{$key}\n");
			next;
		}
		#check if public or private file
		if($status->{$key} eq "Public"){
			$URL=$publicURL.$links->{$key};
		}
		elsif($status->{$key} eq "Private"){
			if($user eq "none"){
				print("Download of $status->{$key} file $files->{$key} aborted due to no credentials\n");
				print("Usage:\tdccDownload.pl -u username -p password\n");
				next;
			}
			else{
				$URL=$privateURL.$links->{$key};
				$ua->credentials($user,$pass);
			}
		}
		else{die "Error in public/private assessment\n";}
		
		#Check if the directory already exists
		my $dir = $downloaddir->{$key};
		if(-d $dir){
			print("Saving to ".$dir."/".$files->{$key}."\n");
			$ua->mirror($URL,$dir."/".$files->{$key});
		}
		else{
			print("Making directory $dir\n");
			mkdir($dir) or die "Can't create directory";
			print("Saving to ".$dir."/".$files->{$key}."\n");
			$ua->mirror($URL,$dir."/".$files->{$key});
		}
	}
}

sub redoQuery{

#Example query:  days_to_death:[100 TO 110]

	my $parsetype = shift;
	my $baseURL = shift;
	my $query = shift;
	my $startrecord = shift;
	my $stoprecord = shift;
	my $link_hash = shift;
	my $status_hash = shift;
	my $archive_hash = shift;
	my $file_hash = shift;
	#my $group = shift;
	my $querytype = shift;
	my $maxrecord = shift;
	my $grouptype = shift;
		
	my $content;
	my $URL;
	my $group;
	
	print("Parsetype:\t$parsetype\nQuerytype:\t$querytype\nMaxgroup:\t$maxgroup\n");
	if($parsetype eq "file"){
		$query = $query."&start=$startrecord&rows=$stoprecord";
	}
	elsif($parsetype eq "group"){
		if($querytype eq "full"){
			$query = $query."&group.limit=$maxgroup";
		}
		elsif($querytype eq "partial"){
				$query = $query."&group.limit=$stoprecord&group.offset=$startrecord";
		}
	}
	print("Query is $baseURL"."$query\n");
	
	#Clear out the hashes
	%$link_hash = ();
	%$status_hash = ();
	%$archive_hash = ();
	%$file_hash = ();
	%$grouptype = ();
	my $root;
	my $handlers;
	
	my $dir = "\.";
	my ($fh,$filename) = tempfile(DIR=>$dir, SUFFIX=>'.tmp',UNLINK=>1);

	
	my $ua = WWW::Mechanize->new('ssl_opts' =>{'verify_hostname'=>0});
	$URL = $baseURL.$query;
	
	#Do the query (will this blow up memory?)
		$ua->get($URL);
		if($ua->success()){
			$ua->save_content($filename);
			#Set up the counter and create a new parser
			my $bigcounter = 0;
			if($parsetype eq "file"){
				$root = {'response' => 1};
				$handlers = {'result/doc/str' => sub{nodeHandler(@_,$link_hash,$status_hash,$archive_hash,$file_hash,$bigcounter,"false");},
								'result/doc' => sub{$bigcounter++;}
								};
			}
			elsif($parsetype eq "group"){
				$root = {'response' => 1};
				$handlers = {'lst/lst/arr/lst/result/doc/str' => sub{nodeHandler(@_,$link_hash,$status_hash,$archive_hash,$file_hash,$bigcounter,"false");},
							'lst/lst/arr/lst/str'=> sub{$group = nodeHandler(@_,$link_hash,$status_hash,$archive_hash,$file_hash,$bigcounter,"false");},
							'lst/lst/arr/lst/result/doc' => sub{$grouptype->{$bigcounter}=$group;$bigcounter++;}
							};
			}
			#Now parse the content
			my $parser = new XML::Twig(TwigRoots => $root, TwigHandlers=> $handlers, error_context => 1);
			$parser->parsefile($filename);
	
	}
		else{
			die "Error in full query\n";
		}
		close $fh;
}

sub fileDownload{
	my $parsetype = shift;
	my $links = shift;
	my $status = shift;
	my $archives = shift;
	my $files = shift;
	my $baseURL = shift;
	my $query = shift;
	my $counter = shift;
	my $user = shift;
	my $pass = shift;
	my $server = shift;
	#my $group = shift; 
	my $maxgroup = shift;
	my $grouptype = shift;
	my $start;
	my $stop;
	
	#Foce a flush
	$| = 1;
	print("Enter index of a file to download (0=all, a range separated by a dash, or 'q' to quit):");
	my $answer = <STDIN>;
	chomp($answer);
	
	if($answer eq 'q'){
		print("Quitting\n");
		exit;
	}
	elsif($answer eq '0'){
		print("Downloading all files\n");
		#download everything
		#First off, redo the query to get ALL the shared links
		if($parsetype eq "file"){
			redoQuery($parsetype,$baseURL,$query,0,$counter,$links,$status,$archives,$files,"null",$maxgroup,$grouptype);
		}
		elsif($parsetype eq "group"){
			redoQuery($parsetype,$baseURL,$query,0,$counter,$links,$status,$archives,$files,"full",$maxgroup,$grouptype);
			my $count = scalar %$grouptype;
			print("Grouptype count:\t$count\n");		
		}
	}
	elsif($answer =~m/-/){
		#Parse the start and stop
		my @temp = split(/-/,$answer);
		$start = $temp[0];
		$stop = $temp[1];
		if($parsetype eq "file"){
			$stop = $stop - $start;
			$stop++;
		}
		if($parsetype eq "file"){
			redoQuery($parsetype,$baseURL,$query,$start,$stop,$links,$status,$archives,$files,"null",$maxgroup,$grouptype);
		}
		elsif($parsetype eq "group"){
			redoQuery($parsetype,$baseURL,$query,$start,$stop,$links,$status,$archives,$files,"partial",$maxgroup,$grouptype);		
		}		
	}
	else{
		print("Unrecognized answer, terminating program\n");
		exit;
	}
	
	if($parsetype eq "file"){
		downloadThis($server,$links,$status,$files,$user,$pass,$archives);
	}
	elsif($parsetype eq "group"){
		downloadThis($server,$links,$status,$files,$user,$pass,$grouptype);
	}
}