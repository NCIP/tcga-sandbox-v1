#!/usr/bin/perl




use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";
my $prefix = $ARGV[0];
my @studies = GetCancers();

open(FOUT,  "> $prefix.slidebarcode.vs.portion.txt");
foreach my $study (@studies){
    print $study."------------\n";
    my $cmd = GetCommand($study);
    my @output = GetOracleResponse($cmd);
    foreach my $line (@output){
	my ($slidebarcode, $portionbarcode, $archive)=split(/\t/,$line);

	## skip normal solid tissue checks
	next if($slidebarcode =~ m/TCGA-..-....-11/);
	my $inconsistencies = SamplePortionvsSlideBarcode($portionbarcode, $slidebarcode);
	if($inconsistencies eq "NONE"){
	    #print "OK$study\t$portionbarcode\t$slidebarcode\t$inconsistencies\t$archive\n";
	} else{
	    print FOUT "$study\t$portionbarcode\t$slidebarcode\t$inconsistencies\t$archive\n";
	    #print "$study\t$portionbarcode\t$slidebarcode\n";
	}
    }
}
close(FOUT);


sub GetCommand{
    my $study=$_[0];
    my $cmd="SELECT slide.slide_barcode,portion.portion_barcode, archive_info.deploy_location ".
	"FROM TCGA$study.SLIDE, TCGA$study.SLIDE_ARCHIVE, TCGA$study.ARCHIVE_INFO, tcga$study.portion ".
	"WHERE ".
	"slide.slide_id = SLIDE_ARCHIVE.slide_ID ".
	"AND SLIDE_ARCHIVE.archive_ID = ARCHIVE_INFO.ARCHIVE_ID ".
	"AND ARCHIVE_INFO.is_latest = 1 ".
	"AND portion.portion_id = slide.portion_id ".
	"";
    
    return $cmd;
}
