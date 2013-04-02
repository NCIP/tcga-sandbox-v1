#!/usr/bin/perl

use strict;
use File::Basename;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $prefix=$ARGV[0];
my $DEBUG=0;

my %latest = Archive2LatestLocation();

open(FOUTtotal, "> $prefix.total.txt");
open(FOUTf,     "> $prefix.missingfile.txt");
open(FOUTs,     "> $prefix.missingsdrf.txt");


my %archivelist;

foreach my $arch(keys %latest){	
    foreach my $tar(keys %{$latest{$arch}}){	   

	my $platformdir = dirname $tar;
	next if($platformdir =~ m/pathology/);
	next if($platformdir =~ m/biotab/);
	next if($platformdir =~ m/clin/);
	next if($platformdir =~ m/images/);
	next if($platformdir =~ m/tracerel/);		

	#################################

	$platformdir=~s/tcga4yeo/\*/;
	$platformdir=~s/anonymous/\*/;
	if($tar=~m/Level/){
	    $archivelist{$platformdir}{LEVEL}{$tar}=1;
	}elsif($tar=~m/mage-tab/){
	    $archivelist{$platformdir}{magetab}{$tar}=1;
	}
	
    }
}    

#############################
## process each platform

foreach my $p(keys %archivelist){
    my %allfiles;
    my $n=(keys %{$archivelist{$p}{LEVEL}});
    my $m=(keys %{$archivelist{$p}{magetab}});
    if(($m==0) ||($n==0) ){	    
	if($m==0){
	    print FOUTf "NO_MAGETAB_FOR\t$p\n";
	}
	if($n==0){
	    print FOUTf "NO_LEVEL_DIRS_FOR\t$p\n";
	}
	next;	
    }
    
    ## now get all Levelfiles for this platform	    
    foreach my $dir(keys %{$archivelist{$p}{LEVEL}}){
	#print $dir." IS IN LEVEL\n";
	$dir=~s/.tar.gz//;
	$dir=~s/.tar$//;
	my %tmp = GetLevelFiles($dir);
	foreach my $f(keys %tmp){
	    #print $f." EXISTS $p\n";
	    $allfiles{$f}{"LEVEL"}++;
	}
    }	
    
    foreach my $dir(keys %{$archivelist{$p}{magetab}}){
	$dir=~s/.tar.gz//;
	$dir=~s/.tar$//;
	foreach my $sdrf(<$dir/*sdrf.txt>){
	    my @f_all=Sdrf2FileList($sdrf);
	    my $d_ori=dirname $sdrf;
	    my $d=dirname $d_ori;
	    foreach my $f(@f_all){
		print FOUTtotal $f."\t$sdrf\n";
		my $file=$d."/".$f;
		$file=~s/tcga4yeo/*/;
		$file=~s/anonymous/*/;
		$allfiles{$file}{"SDRF"}{$sdrf}++;
	    }
	}
    }
    
    
    foreach my $file(keys %allfiles){
	next if(FileSkip($file) > 0);
	unless(exists $allfiles{$file}{"SDRF"}){
	    #print "MISSINGSDRF\t$file\n";
	    print FOUTs "MISSINGSDRF\t$file\n";
	}
	unless(exists $allfiles{$file}{"LEVEL"}){
	    foreach my $z(keys %{$allfiles{$file}{"SDRF"}}){
		#print "MISSINGFILE\t$file\tNO_SDRF\n";
		print FOUTf "MISSINGFILE\t$file\t".$z."\n";
	    }		    
	}
    }	
}
close(FOUTtotal);
close(FOUTf);
close(FOUTs);



sub FileSkip{

    my $file=$_[0];
    if(($file=~m/README/) ||
       ($file=~m/DESCRIPTION/) ||
       ($file=~m/MANIFEST/)    ||
       ($file=~m/CHANGES/)     ||
       ($file=~m/DISCLAIMER/)  ||
       ($file=~m/.vcf$/)       ||
       ($file=~m/.tif$/)){
	return 1;
    }
    return 0;

}


sub GetLevelFiles{
    my $dir=$_[0];    
    my %info;

    ## get for both anonymous and tcga4yeo
    foreach my $file(<$dir/*>){	
	next if(FileSkip($file));
	## 
	$file=~ s/tcga4yeo/*/;
	$file=~s/anonymous/*/;
	$info{$file}=1;
    }    
    return %info;
}


