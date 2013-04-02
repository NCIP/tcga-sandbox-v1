#!/usr/bin/perl
use warnings;
use strict;

use Data::Dumper;
use WWW::Mechanize;
use JSON qw(decode_json);
use Test::JSON;
use Confluence;
use File::HomeDir;
use XML::Twig;

my $nciuser;
my $ncipass;

my $loginroots = {'info' => 1};
my $loginhandlers = { 'nciuser' => sub{$nciuser = $_ -> text},
				'ncipass' => sub{$ncipass = $_ -> text}
				};
my $creds = File::HomeDir->my_home."/.perlinfo/info.xml";
my $loginparser = new XML::Twig(TwigRoots => $loginroots, TwigHandlers => $loginhandlers, error_context => 1);
$loginparser->parsefile($creds);





my $data;
my $json_data;

#my %description_hash; #key:ticket data:Ticket description
#my %point_hash; #key:ticket data:Point estimate
#my %release_hash; #key:ticket data:release version
my @busted_array;
my %content_hash; #key:label:release data:built content

my %labels = ("annotations"=>"Annotations","biospecimen_metadata"=>"Biospecimen Metadata Browser",
			"case_reporting"=>"Case Reporting","pcod"=>"PCOD BCR Pipeline","search_clinical"=>"Identify Participant Data",
			"data_freeze"=>"Data freeze and publications","file_version"=>"File Version Display",
			"download"=>"Download Identified Files and Data","search_files"=>"Searching for and identify submitted files",
			"search_genomic"=>"Genomic Location Search","cms"=>"CMS","search_keyword"=>"Keyword Searching",
			"exp_metadata"=>"Experimental Metadata","2.0_reports"=>"Reports","submission_report"=>"Submission Statistics Report",
			"user_identification"=>"User Identification","standalone_validation"=>"Standalone Validator",
			"qclive"=>"QCLive","ccb"=>"Change Control Board");

#my %labels =  ("annotations"=>"Annotations","biospecimen_metadata"=>"Biospecimen Metadata Browser",
#				"case_reporting"=>"Case Reporting","pcod"=>"PCOD BCR Pipeline","search_clinical"=>"Identify Participant Data");
			

my @releases = ("Release_2.0","Release_2.1","Release_2.x");
#my @releases = ("Release_2.0");
#my $content = "<table><tr><td>Ticket</td><td>Description</td><td>Points</td><td>Release</td>\n";
my $header = "<table><tr><td>Ticket</td><td>Description</td><td>Points</td><td>Release</td></tr>\n";
my $content = "";
my $bigcontent = "";

foreach my $release (@releases){
	$bigcontent = $bigcontent."<h1>$release</h1>\n$header\n";
	print("Working on $release\n");
	foreach my $label (sort keys %labels){
		print("Getting data for $label\n");
		#$content = "";
		#$content = "<table><tr><td>Ticket</td><td>Description</td><td>Points</td><td>Release</td>\n";
		$json_data = "";
		$json_data = getJson($label,$release);
		if($json_data eq "busted"){
			print("Query failed for $label\n");
			push(@busted_array,$label);
			next;
		}
		else{
			print("Starting parse of $label\n");
			my %description_hash;
			my %point_hash;
			my %release_hash;
			my $result = parseJson($json_data,\%description_hash,\%point_hash,\%release_hash,$release);
			if($result eq "true"){
				print("Building content for $label\n");
				my $totalpoints = addPoints(\%point_hash);
				$content = buildContent(\%description_hash,\%point_hash,\%release_hash,\%labels,$label,$totalpoints);
				#$content = $content."</table>";
				$bigcontent = $bigcontent.$content;
				#$content_hash{$label.":".$release} = $content;
				#print $content;
				#print("\n");
			}
		}
	}#end of foreach label
	$bigcontent = $bigcontent."</table>";
} #end of foreach release

#$bigcontent = $bigcontent."</table>";
#foreach my $key (sort keys %content_hash){
#	my ($label,$release) = split(/:/,$key);
#	$bigcontent = $bigcontent.$label.$content_hash{$key}."\n";
#}

printWiki($bigcontent,$nciuser,$ncipass);

#print $bigcontent;
#print("\n");

##############################################
#                                            #
#                   Subroutines              #
#                                            #
##############################################

sub addPoints{
	my $point_hash = shift;
	my $totalpoints = 0;
	foreach my $key (keys %$point_hash){
		$totalpoints = $totalpoints + $point_hash->{$key};
	}
	return $totalpoints;
	
}
sub buildContent{
	my $description_hash = shift;
	my $point_hash = shift;
	my $release_hash = shift;
	my $labels = shift;
	my $label = shift;
	my $points = shift;
	#my $content = shift;
	my $content = "<tr><th><b>$labels->{$label}</b></th><th><b>JIRA label: $label</b></th><th><b>$points</b></th/><th></th></tr>\n";
	
	#$content = $content."<tr><b>$label</b></tr>";
	foreach my $ticket (keys %$description_hash){
		#print("Ticket:\t$ticket\nDescription:\t$description_hash->{$ticket}\nPoints:\t$point_hash->{$ticket}\nRelease:\t$release_hash->{$ticket}\n");
		$content = $content."<tr><td>$ticket</td><td>$description_hash->{$ticket}</td><td>$point_hash->{$ticket}</td><td>$release_hash->{$ticket}</td></tr>\n";
	}
	return $content;
	
}
sub parseJson{
	my $data = shift;
	my $description_hash = shift;
	my $point_hash = shift;
	my $release_hash = shift;
	my $release = shift;
	my $ticket;
	my $description;
	my $points;
	my $result = "true";
	
	my $decoded_json = decode_json($data);
	# first check a null response  {"startAt":0,"maxResults":50,"total":0,"issues":[]}
	if($decoded_json ->{"total"} == 0){
		#Don't do anything, there isn't anything to do
		$result = "false";
	}
	else{
		my $issues = $decoded_json->{"issues"}; #this is an array of hashes
		foreach(@{$issues}){
			$ticket = $_->{"key"};
			my $field_hash = $_->{"fields"};
			$description = $field_hash->{"summary"};
			$points = $field_hash->{"customfield_10042"};
			#print("Ticket:\t$ticket\nDescription:\t$description\nPoints:\t$points\n");
		
			$description_hash->{$ticket} = $description;
			$point_hash->{$ticket} = $points;
			$release_hash->{$ticket} = $release;
		
		}
	}
	
	return $result;
}

sub getJson{
	my $label = shift;
	my $release = shift;
	my $data;
	
	my $baseURL='https://tracker.nci.nih.gov/rest/api/2/search/?jql=';
	my $query = "project = TCGA-DCC and labels = \"$label\" AND labels = \"$release\"";
	#my $query = "project = TCGA-DCC and labels = \"$release\"";
	#print($baseURL.$query."\n");
	my $ua = WWW::Mechanize->new();
	
	for(my $x=0;$x<=5;$x++){
		$ua->get($baseURL.$query);
		#sleep(20);
		if($ua->success()){
			$data = $ua->content();
			if(Test::JSON::is_valid_json $data){
				$x=100;
			}
			else{
				$data = "busted";
			}
			#print(Dumper($data));
		}
		else{
			print("Bad request\n");
			die;
		}
	}
	
	return $data;
	
}


sub printWiki{
	my $content = shift;
	my $user =shift;
	my $pass = shift;
	my $space;
	my $title;
	my $url = "https://wiki.nci.nih.gov/rpc/xmlrpc";

	my $wiki = Confluence->new($url, $user, $pass);
	
	$space = "TCGAproject";
	$title = "JIRA Report";
	print("Printing to JIRA Report\n");


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