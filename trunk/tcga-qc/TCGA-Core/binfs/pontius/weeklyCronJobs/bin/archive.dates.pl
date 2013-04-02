#!/usr/bin/perl

############################################################
#
# FOUTmissing = archive in DB but tar or md5 missing from expanded archive
#
############################################################

use File::Find;
use File::Basename;
use strict;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $DIR_OUT=$ARGV[0];
$DIR_OUT.="/";
my %is_latest = Archive2LatestLocation();
open(FOUTmissingmd5,        "> $DIR_OUT/latest.md5missing.txt");
open(FOUToldmd5,            "> $DIR_OUT/oldmd5.txt");
open(FOUToldtar,            "> $DIR_OUT/oldtar.txt");
open(FOUToldmanifest,       "> $DIR_OUT/oldmanifest.txt");

open(FOUTflagged,           "> $DIR_OUT/flagged.md5.txt");

open(FOUTmissing_from_manifest,"> $DIR_OUT/MISSING_FROM_MANIFEST.txt");
open(FOUTmissingmanifest,      "> $DIR_OUT/manifestmissing.txt");
open(FOUT,                     "> $DIR_OUT/archiveDBvsServer.txt");
#################################################
## check for archive on server
foreach my $platform(keys %is_latest){
    foreach my $archive(keys %{$is_latest{$platform}}){
	##
	## test presense of archive on server
	##
	unless(-s $archive){
	    print FOUT "NOTONSERVER $archive\n";
	}

	#####################################################################
	## flag cases where a mirror archive is on server but not in DB
	##
	if($archive=~m/anonymous/){
	    $archive=~s/anonymous/tcga4yeo/;
	}elsif($archive=~m/tcga4yeo/){
	    $archive=~s/tcga4yeo/anonymous/;
	}
	if(-s $archive){
	    unless($is_latest{$platform}{$archive}){
		## archive is on server but not in DB
		print FOUT "NOTINDB $archive\n";
	    }
	}	
	##
	#####################################################################
    }
}
close(FOUT);



foreach my $archive(keys %is_latest){
  FILE: foreach my $tarfile(keys %{$is_latest{$archive}}){
      ############################################################
      ## can't do anything with clinical tar files ?
      if ($tarfile=~m/clinical/){	
	  next FILE;
      } 
      
      my $md5 = $tarfile.".md5";
      my $dir = $tarfile;
      $dir=~s/.tar.gz$//;
      $dir=~s/.tar$//;      
      my $manifest = $dir."/MANIFEST.txt";                

      ##################################################
      ## check missing files
      unless(-e $md5){
	  printf(FOUTmissingmd5 "%s\n", $md5);
      }

      
      
      ############################################################
      ## get timestamps for tar.gz, directory and md5
      my %timestamps;
      foreach my $f($tarfile, $md5, $manifest){
	  $timestamps{$f} = -M $f;
      }
      foreach my $file(<$dir/*>){
	  $timestamps{$file}= -M $file;
      }
      
      ############################################################
      ##
      ## check md5 vs all other files
      ##
      my %lines_flagged;
      foreach my $f(keys %timestamps){
	  next if($f eq $md5);
	  
	  if( (1+$timestamps{$f} < $timestamps{$md5})){	 
	      printf( FOUToldmd5 "%d\t%d\t%s\n",$timestamps{$f}, $timestamps{$md5}, $f);
	  } 
	  
	  ## flag any huge differences, ie delays in making md5,or redoing md5
	  if($timestamps{$f} > 1+$timestamps{$md5}){	  	     
	      my $output = sprintf( "%s\tOLDERTHANMD5\n",$f);
	      $lines_flagged{$output}++;
	  } 	 
      }
      
      ############################################################
      ##
      ## check tar vs all other files
      ##
      foreach my $f(keys %timestamps){
	  next if($f eq $tarfile);
	  if(1+$timestamps{$f} < $timestamps{$tarfile}){	  	     
	      printf( FOUToldtar "%s\n", $f);
	  } 
	  
	  ## flag any huge differences, ie delays in making md5,or redoing md5
	  if($timestamps{$f} > 1+$timestamps{$tarfile}){	  	     
	      my $output = sprintf( "%s\tOLDERTHANTAR\n",$f);
	      $lines_flagged{$output}++;
	  } 
      }
      
      foreach my $l(keys %lines_flagged){
	  print FOUTflagged $l;
      }
      
      ############################################################
      ##
      ## check manifest vs all other files in dir
      ##
      foreach my $f(keys %timestamps){
	  next if($f eq $tarfile);
	  next if($f eq $md5);
	  next if($f eq $manifest);
	  if(1+$timestamps{$f} < $timestamps{$manifest}){	  	     
	      printf( FOUToldmanifest "%s\n", $f);
	  } 
      }
      
      #####################################################################################################
      ## 3. FLAG MANIFEST FILES THAT DO NOT MATCH ARCHIVE CONTENTS
      ## for directories that have a MANIFEST file, check that its contents match the directory contents
      ##
      my %stamps;
      if(-s $manifest){
	  ## get files on manifest
	  my %filelist_manifest;
	  open(FIN, "cat $manifest | tr -s '\r' '\n' |" );
	  while(my $line=<FIN>){
	      chomp $line;
	      my @a=split(/\s+/, $line);	     
	      $filelist_manifest{$a[-1]}++;
	  }
	  close(FIN);
	  
	  ## get files in directory
	  my %filelist_dir;
	  foreach my $f(<$dir/*>){
	      my $b=basename $f;
	      $filelist_dir{$b}++;
	  }
	  
	  
	  ## check that all files on manifest are in dir
	  foreach my $f(keys %filelist_manifest){
	      
	      unless(exists $filelist_dir{$f}){		  
		  print FOUTmissing $f."\tIN_MANIFEST_NOT_IN_DIR\t$dir\n";
	      }
	  }
	  
	  foreach my $f(keys %filelist_dir){
	      next if($f eq "MANIFEST.txt");
	      unless(exists $filelist_manifest{$f}){
		  print FOUTmissing_from_manifest $f."\tIN_DIR_NOT_IN_MANIFEST\t$manifest\n";
	      }
	  }
      }
  }
}
close(FOUTmissingmd5);
close(FOUTmissingtar);
close(FOUToldmd5);
close(FOUToldtar);
close(FOUToldmanifest);
close(FOUTflagged);
close(FOUTmissing_from_manifest);

