#!/usr/bin/perl

use File::Find;
use File::Basename;
use strict;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $fout = $ARGV[0];


my %is_latest=Archive2LatestLocation();

## get all archives ie tar.gz
## and screen all files in expanded archive, to see if any are zerosize

####################################################
## get archives that have already been screened
## using file that is somewhat up to date
my $EXPIRATION=$ENV{FILE_EXPIRATION};

my %toskip;
my $previous_results="/h1/pontiusj/Projects/archiveConsistency/cumulative_results/zerosize.ok.txt";
if(-s $previous_results){
    if((-M $previous_results) < $EXPIRATION){
	open(FIN, $previous_results);
	while(my $line=<FIN>){
	    chomp $line;
	    $toskip{$line}=1;
	}
	close(FIN);
    }
}

open(FOUT,  "> $fout");
open(FOUT2, "> $previous_results");

foreach my $archive(keys %is_latest){
    foreach my $tarfile(keys %{$is_latest{$archive}}){
	my $dir=$tarfile;
	$dir=~s/.tar.gz//;
	$dir=~s/.tar//;
	
	## check size of files in directory
	## and printout zerosized files with time since modification
	my $flag=0;
	unless($toskip{$dir} > 0){
	    foreach my $file(<$dir/*>){
		next if($file =~ m/README_HIPAA_AGES/);
		unless(-s $file){	    
		    $flag++;
		    printf(FOUT "%s\n", $file);
		}	 
	    }     
	}
	unless($flag>0){
	    print FOUT2 $dir."\n";
	}
    }
}
close(FOUT);
close(FOUT2);

