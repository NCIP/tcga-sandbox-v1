#!/usr/bin/perl

## validate tiffinfo for each svs file
use strict;
use File::Basename;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $DIR_OUT = $ARGV[0];
my $DIR_LIB = $ENV{HOME}."/lib/";
my $file_tifflist = $DIR_LIB."tiff.ok.txt";

my %latest = Archive2LatestLocation(1);
my %file_oktiff;
open(FIN, $file_tifflist) || die "Could not open $file_tifflist\n";
while(my $line=<FIN>){
    chomp $line;
    $file_oktiff{$line}=1;
}
close(FIN);


open(FOUT,         "> $DIR_OUT/ok_tifflist.txt");
open(FOUT_err,     "> $DIR_OUT/tiff_info_err.lst");
open(FOUT_warning, "> $DIR_OUT/tiff_info_warning.lst");


foreach my $archive(keys %latest){
    foreach my $dir(keys %{$latest{$archive}}){
	next unless($dir =~ m/image/);
	$dir=~s/.tar.gz//;	
	$dir=~s/.tar//;
      SVS:    foreach my $svs(<$dir/*svs>){	

	  if($file_oktiff{$svs}){
	      next;
	  }
	  my $size = (-s $svs);
	  next if ($size > 2000000000);

	  my $b=basename $svs;
	  my $fout = $DIR_OUT."/$b.tiff.out";
	  
	  eval system("tiffinfo $svs &> $fout");
	  if($?){
	      print FOUT_err $svs."\n";
	  } elsif(-s $fout){
	      my $warning=0;
	      open(FIN, $fout);
	      while(my $line=<FIN>){
		  if(($line=~m/Error/)||
		     ($line=~m/MissingRequired/) ||
		     ($line=~m/Invalid/)){
		      print FOUT_err $svs."\n";
		      close(FIN);
		      next SVS;
		  }elsif($line=~m/Warning/){
		      $warning=1;
		  }
	      }
	      close(FIN);
	      if($warning==1){
		  print FOUT_warning $svs."\n";
	      }else{
		  print FOUT $svs."\n";
	      }
	  } else{
	      print FOUT_err $svs."\n";	
	  }
      }
    }
}
close(FOUT_lst);
close(FOUT);
    
