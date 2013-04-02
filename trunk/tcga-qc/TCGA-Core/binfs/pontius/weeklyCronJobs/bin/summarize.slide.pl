#!/usr/bin/perl

use strict;
use File::Basename;
require $ENV{HOME}."/lib/tools_dcc.pl";

## get all svs files and count how many are in the slide db
## also tally how many int he slide db have slides
my @cancers = GetCancers();
my %db_slide;

my $DIROUT=$ARGV[0];

open(FOUT, "> $DIROUT/svsfile.vs.slidetable.txt");

my %db_slide;

foreach my $d(@cancers){

    my $cmd = "select s.slide_id, s.slide_barcode, s.uuid, archive_info.deploy_location ".
	"from tcga$d.slide s, tcga$d.slide_archive sa, tcga$d.archive_info  ".
	"where s.slide_id = sa.slide_id ".
	"AND archive_info.is_latest =1 ".
	"AND archive_info.deploy_status = 'Available' ".
	"AND sa.archive_id = archive_info.archive_id ";

    
    #my $cmd = "select slide_id, slide_barcode, uuid from tcga$d.slide";
    my @results=GetOracleResponse($cmd);
    foreach my $line(@results){
	my ($a,$b, $c, $x)=split(/\t/, $line);
	$db_slide{$a}{UUID}=uc($c);
	$db_slide{$a}{BARCODE}=$b;
	$db_slide{$a}{DISEASE}=$d;
	$db_slide{$a}{DEPLOY_LOCATION}=$x;
	
    }    
}

## now get archives and load filenames
my %archive_slide;
my %latest = Archive2LatestLocation();
foreach my $archive(keys %latest){
    #print $archive."\n";
    next unless($archive=~m/images/);
    foreach my $dir(keys %{$latest{$archive}}){
	$dir=~s/.tar.gz//;
	$dir=~s/.tar.$//;
	foreach my $file(<$dir/*svs>){
	    my $b=basename $file;
	    $archive_slide{$b}{ARCHIVE}=$dir;
	}
    }
}

##
## now tally
##
## search for archives that match db
foreach my $a(keys %db_slide){
    my $uuid=$db_slide{$a}{UUID};
    my $barcode=$db_slide{$a}{BARCODE};
    #print $a."\t".$uuid."\t".$barcode."\n";
    foreach my $b(keys %archive_slide){
	if((index($b, $uuid) >= 0) ||
	   (index($b, $barcode) >= 0)){
	    ## initialize both
	    $archive_slide{$b}{DBENTRY}=$a;
	    push @{$db_slide{$a}{ARCHIVE}}, $b;
	    #print "$b\t$uuid\t$barcode\tMATCH\n";
	}
    }
}
## tally db entries with slides, slides with db entries and without
my %counts;


foreach my $a(keys %db_slide){
    if (exists $db_slide{$a}{ARCHIVE}){
	#printf(FOUT "%s\t%s\t%s\t%s\t%s\n", "INDB_HAS_SLIDE", $db_slide{$a}{UUID},  $db_slide{$a}{BARCODE}, $a, $db_slide{$a}{DEPLOY_LOCATION});
    } else{
	printf(FOUT "%s\t%s\t%s\t%s\t%s\n", "INDB_NO_SLIDE", $db_slide{$a}{UUID},  $db_slide{$a}{BARCODE}, $a, $db_slide{$a}{DEPLOY_LOCATION});
    }
}

foreach my $a(keys %archive_slide){
    if(exists $archive_slide{$a}{DBENTRY}){
	#printf(FOUT "HAS_SLIDE_INDB\t%s\t%s\n",  $a, $archive_slide{$a}{ARCHIVE});
    }else{
	printf(FOUT "HAS_SLIDE_NOTINDB\t%s\t%s\n",  $a, $archive_slide{$a}{ARCHIVE});
    }
}

close(FOUT);
