#!/usr/bin/perl
#Example script to automate downloading of files based on archive name and time frame

use strict;
use warnings;

use XML::Twig;
use WWW::Mechanize;

#my $queryURL = "https://tcgav2-data-proto.nci.nih.gov/data-browser/search/tcga/?q=disease_abbreviation:BRCA AND data_type:\"Expression:RNA%20Sequence:Exon\" AND submission_date:[NOW-6MONTHS TO NOW]";
my $queryURL = "http://tcgav2-data-proto.nci.nih.gov/data-browser/search/tcga?q=tumor_tissue_site:BRAIN";
#my $queryURL = "http://tcgav2-data-proto.nci.nih.gov/data-browser/search/tcga?q=level:2 AND disease_abbreviation:BRCA";

my $publicURL = "https://tcgav2-data-proto.nci.nih.gov/ws/resource/file/retrieve/key/";
my $protectedURL = "https://tcgav2-data-proto.nci.nih.gov/ws/resource/file/retrieve/protected/key/";

my $username = "none";
my $password = "none";

for(my $i=0;$i<=$#ARGV;$i++){
	if($ARGV[$i] eq "-u"){
		$i++;
		$username = $ARGV[$i];
	}
	elsif($ARGV[$i] eq "-p"){
		$i++;
		$password = $ARGV[$i];
	}

}

my %file_hash; #shared link is key, file name is data
my %status_hash; #shared link is key, status is data
my %archive_hash; #shared link is key, archive name is data

print("Querying DCC Web Services\n");
my $xml = getData($queryURL);

print("Parsing XML Returned\n");
parseXML($xml,\%file_hash,\%status_hash,\%archive_hash);

print("Starting file download\n");
downloadFiles(\%file_hash, \%archive_hash, \%status_hash, $publicURL, $protectedURL, $username, $password);
print("Downloads finished\n");

###########################################
#                                         #
#       Subroutines                       #
#                                         #
###########################################
sub parseXML{
	
	my $xml = shift;
	my $files = shift;
	my $status = shift;
	my $archive = shift;
	my $value;
	my $attr;
	my $archive_name;
	my $file_name;
	my $status_value;
	my $shared;
	
	my $root = {'response' => 1};
	my $handlers = {
				'result/doc/str' => sub{$value=$_->text; $attr = $_->att('name'); if($attr eq "archive_name"){$archive_name=$value}
										elsif($attr eq "file_name"){$file_name = $value}
										elsif($attr eq "access_level"){$status_value = $value}
										elsif($attr eq "shared_link"){$shared=$value}},
				'result/doc' => sub{$files->{$shared}=$file_name;$status->{$shared}=$status_value;$archive->{$shared}=$archive_name}
				};
	my $parser = new XML::Twig(TwigRoots => $root, TwigHandlers=> $handlers, error_context => 1);
	$parser->parse($xml);
}

sub downloadFiles{
	my $files = shift;
	my $archives = shift; 
	my $status = shift;
	my $publicURL = shift;
	my $privateURL = shift;
	my $user = shift;
	my $pass = shift;
	my $URL;
	
	#my $ua = LWP::UserAgent->new;
	#$ua->ssl_opts(verify_hostname=>0);
	my $ua = WWW::Mechanize->new('ssl_opts' =>{'verify_hostname'=>0});
	
	foreach my $key (keys %$files){
		#All unmigrated files have a download link that starts with #
		if($key =~m/#/){
			print("Aborting download of non-migrated file $files->{$key}\n");
			next;
		}
		#check if public or private file
		if($status->{$key} eq "Public"){
			$URL=$publicURL.$key;
		}
		elsif($status->{$key} eq "Private"){
			if($user eq "none"){
				print("Download of $status->{$key} file $files->{$key} aborted due to no credentials\n");
				print("Usage:\tdccDownload.pl -u username -p password\n");
				next;
			}
			else{
				$URL=$privateURL.$key;
				$ua->credentials($user,$pass);
			}
		}
		else{die "Error in public/private assessment\n";}
		
		#Check if the directory already exists
		my $dir = $archives->{$key};
		if(-d $dir){
			print("Starting download of $status->{$key} file $files->{$key}\n");
			print("Saving to ".$dir."/".$files->{$key}."\n");
			#$ua->mirror($request,$dir."/".$files->{$key});
			$ua->mirror($URL,$dir."/".$files->{$key});
		}
		else{
			print("Making directory $dir\n");
			mkdir($dir) or die "Can't create directory";
			print("Starting download of $status->{$key} file $files->{$key}\n");
			print("Saving to ".$dir."/".$files->{$key}."\n");
			$ua->mirror($URL,$dir."/".$files->{$key});
		}
	}
	
}

sub getData{
	my $query = shift;
	my $content;
	my $response;
	
	my $ua = WWW::Mechanize->new('ssl_opts' =>{'verify_hostname'=>0});

	$response = $ua->get($query);
	if($response->is_success){
		$content = $response->content;
		return $content;
	}
	else{
		die $response->status_line;
	}
}

