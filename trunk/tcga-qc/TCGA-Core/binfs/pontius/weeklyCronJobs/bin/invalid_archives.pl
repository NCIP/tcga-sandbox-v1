#!/usr/bin/perl


#########################################################################################
## get list of all archives and see if there is a most recent archive per platform/batch
## or if some got stuck in processing
#########################################################################################


#########################################################################################
## Review of Shelley of these combinations it was determined: ok vs TODO
# TODO:HIGHEST.REVISION.BUT	0:Available	23 cases
# TODO:HIGHEST.REVISION.BUT	0:In_Review	4 cases
# OK:HIGHEST.REVISION.BUT	0:Invalid	48 cases
# OK:HIGHEST.REVISION.BUT	0:Obsolete	243 cases
# OK:HIGHEST.REVISION.BUT	0:Superceded	9 cases
# TOD0:HIGHEST.REVISION.BUT	0:Uploaded	8 cases
# TODO:HIGHEST.REVISION.BUT	0:Validated	1 cases
# TODO:HIGHEST.REVISION.BUT	1:Obsolete	5 cases
# OK:NOT.HIGHEST.REVISION.BUT	0:Available	42 cases
# TODO:NOT.HIGHEST.REVISION.BUT	1:Available	17 cases
# TODO:NOT.HIGHEST.REVISION.BUT	1:Obsolete	5 cases
#########################################################################################

use strict;
use File::Basename;
require $ENV{HOME}."/lib/tools_dcc.pl";

my $FILE_OUT = $ARGV[0];

my $TAG1="NOT.HIGHEST.REVISION.BUT";
my $TAG2="HIGHEST.REVISION.BUT";

my @features = ("IS_LATEST", "DEPLOY_STATUS", "DEPLOY_LOCATION");
my %platform2revision2archive;
my %archive2latest;
my %archive2status;

my %no_problem = LoadOKArchives();
my @lines =  GetArchiveInfo(\@features);
foreach my $line(@lines){    
    #print $line."\n";
    my ($archive1, $archive2, $latest, $status, $path)=split(/\t/, $line);            
    $status=~s/ /_/g;

    my @list;
    push @list, $archive1;


    next if(length($archive1) < 1);
    if(length($archive2) > 1){
	push @list, $archive2;
    }
    
    ####################################################################################
    ## for the archivetype,get revision and all directories for this archive revision
    foreach my $archivedir(@list){
	my ($path_platform, $batch, $revision)=Tar2BatchRevision($archivedir);    
	
	my $platform=basename $path_platform;
	$platform.=".".$batch;

	#print "$archivedir\n$path_platform $batch $revision\n";
	#die;
	#next;
	## catch duplications
	if($platform2revision2archive{$platform}{$revision}{$archivedir}==1){
	    print "ALREADYINITIALIZED $platform\t$revision\t$archivedir\n";
	}
	$platform2revision2archive{$platform}{$revision}{$archivedir}=1;

	if(exists $archive2latest{$archivedir}){
	    print "ERROR2 DUPLICATE ENTRIES IN DB FOR $archivedir\n";
	}
	$archive2latest{$archivedir} = $latest;

	if(exists $archive2status{$archivedir}){
	    print "ERROR3 DUPLICATE ENTRIES IN DB FOR $archivedir\n";
	}
	$archive2status{$archivedir} = $status;
    }
}


open(FOUT, "> $FILE_OUT");

#sort platform names
my @sorted = sort {$a cmp $b} (keys %platform2revision2archive); 
foreach my $platform(@sorted){
    
    my @revisions = sort {$b<=>$a} (keys %{$platform2revision2archive{$platform}});
    my $latestrevision = $revisions[0];
    
  ARCHIVE:    foreach my $archive(keys %{$platform2revision2archive{$platform}{$latestrevision}}){

      
      my $flagLatestAvailable=0;      
      my $status = $archive2latest{$archive}.":".$archive2status{$archive};
      my $revisionList = join "," , @revisions;	    
      
      ##################################################################################
      ## SKIP CASES WHERE ARCHIVE IS HIGHEST LATEST AND AVAILABLE UNLESS NOT ON SERVER
      if ($status eq "1:Available"){
	  unless(-s $archive){
	      print FOUT "MISSING.FROM.SERVER\t$status\t$archive\n";
	  }
	  next ARCHIVE;
      }

      ########################################################################
      ## Search for Available and Latest for this platform (in other revisions)  
      foreach my $r(@revisions){
	  next if ($r eq $latestrevision);
	  foreach my $archive_previous(keys %{$platform2revision2archive{$platform}{$r}}){	      
	      next if(($archive2latest{$archive_previous} == 0) &&
		      (
		       ($archive2status{$archive_previous} eq "Invalid") ||
		       ($archive2status{$archive_previous} eq "Obsolete") ||
		       ($archive2status{$archive_previous} eq "Removed") ||
		       ($archive2status{$archive_previous} eq "Superceded") ||
		       ($archive2status{$archive_previous} eq "Available")
		       )
		      );

	      ## list archives that outnumber Available and Latest for this platform
	      if(($archive2latest{$archive_previous} == 1) ||
		 ($archive2status{$archive_previous} eq "Available")){		 
		  $flagLatestAvailable=1;      		  
	      }		  

	      my $status2 = $archive2latest{$archive_previous}.":".$archive2status{$archive_previous};

	      if (exists $no_problem{$platform}{$TAG1}{$status2}){
		  print $status."BEING SKIPPED\n";		  
		  next;
	      }

	      ##########################################################################################
	      ## skip over cases that have been investigated, these are ok
	      if(
		 (($platform eq "nationwidechildrens.org_BRCA.tissue_images.Level_1.56") && ($r==0))||
		 (($platform eq "nationwidechildrens.org_BRCA.tissue_images.Level_1.72") && ($r==0))||
		 (($platform eq "nationwidechildrens.org_UCEC.bio.Level_1.186") && ($r==11))||
		 (($platform eq "nationwidechildrens.org_UCEC.bio.Level_1.49") && ($r==19))){
		  next;
	      }
	      ##
	      ##########################################################################################
	      printf( FOUT "%s\t%s\t%s\t%s\t%s\n", $TAG1, $status2,
		      $platform.".".$r, $archive_previous,
		      $revisionList		      
		      );
	  }
      }      

      ## TODO: if previous revision is latest and available, don't worry if more recent archive is 0:Invalid or 0:Available
      if(($status eq "0:Superceded") || ($status eq "0:Obsolete") || ($status eq "0:Invalid")){
	  $status.=".SHOULDBEOK";
      }

      ########################################################
      ## list highest archive that is not latest and available            
      if (exists $no_problem{$platform}{$TAG2}){
	  print $status."BEING SKIPPED\n";
      }

      printf( FOUT "%s\t%s\t%s.%s\t%s\t%s\n", $TAG2,
	      $status,$platform, $latestrevision,$revisionList);
      
  }
}
close(FOUT);


sub LoadOKArchives{
    ## manually annotated exceptions
    my %info;
    ## this 
    $info{"broad.mit.edu_OV.Genome_Wide_SNP_6.Level_1.13"}{$TAG1}{"1:Available"}=1;
    return %info;
}
