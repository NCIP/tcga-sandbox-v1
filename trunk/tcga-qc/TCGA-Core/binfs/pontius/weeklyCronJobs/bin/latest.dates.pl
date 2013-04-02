#!/usr/bin/perl



######################################################################################
#
# 1.compares content of expanded tar archive directory to their gzipped equivalent
# the tarfile contents are listed into an archive specific file at:
# /h1/pontiusj/lib/tar.contents.lsts
# 
# 2.output into file latest.dateinconsistencies.txt will list files that are missing from
# expanded or missing from tar file
#
######################################################################################



use strict;
use File::Basename;
require $ENV{HOME}."/lib/tools_dcc.pl";

my $OUTFILE=$ARGV[0];
my $DIR_TAR=dirname $OUTFILE;
my $DIR_LIB = $ENV{HOME}."/lib/";

my $template;
if($ARGV[1]){
    $template = $ARGV[1];
}

open(FOUT, "> $OUTFILE");
my %list = Archive2LatestLocation();

## get list of archives that have already been tested

my %toskip;
my $fout ="$DIR_LIB/consistent.archives.lst";
if(-s $fout){
    open(FIN, $fout); 
    while(my $line=<FIN>){
	chomp $line;
	$toskip{$line}=1;
    }
    close(FIN);
}



foreach my $archive(keys %list){
    foreach my $tar(keys %{$list{$archive}}){
	next if($toskip{$tar}==1);
	my $expanded =  $tar;
	$expanded =~ s/.tar.gz//;
	$expanded =~ s/.tar//;

	unless(-s $tar){
	    print FOUT "TORETRY:MISSING_TARFILE\t$tar\n";
	}
	
	unless(-s $expanded){	
	    print FOUT "MISSING_EXPANDED\t$tar\n";
	}
	
	#print "$tar is being processed\n";
	if((-s $expanded) &&
	   (-s $tar)){	    
	    #print FOUT "processing2 $tar\n";
	    my $d1=(-M $expanded);
	    my $d2=(-M $tar);
	    my @list = CompareTarFiles($tar, $expanded);
	    foreach my $f(@list){
		print FOUT "FILE_INCONSISTENCIES\t".$f."\n";
	    }
	}	
    }
}
close(FOUT);
    
&PrintSkip($fout);

sub CompareTarFiles{
    my $tar=$_[0];
    my $dir=$_[1];
    
    my @differences;    
    my $basename = basename $dir;
    my $fout = $DIR_LIB."/tar.contents.lsts/$basename.lst";
    unless(-s $fout){
	my $options = ($tar =~ m/.gz$/) ? "-tzvf "  : "-tvf" ;
	system("tar $options $tar > $fout");
	if($?){
	    ## TRY TWICE BEFORE GIVING UP
	    system("tar $options $tar > $fout");
	    if($?){		
		return "Can't expand $tar";
	    }
	}
   } 


    my %list;
    #print "Opening $fout\n";
    ## load tarlist
    open(FIN, $fout) || die "Could not open $fout\n";
    while(my $line=<FIN>){
	chomp $line;
	if($line=~m/^drwxr/){
	    print FOUT "SUBDIRECTORY_IN_TAR\t$line\t$tar\n";
	    next;
	}

	my @a=split(/\t/, $line);
	my $b = basename($a[-1]);
	$list{$b}{TAR}=1;	
    }

    ## load expandedlist
    #print "Loading files from $dir\n";
    foreach my $f(<$dir/*>){
	my $b=basename $f;
	$list{$b}{EXPANDED}=1;
	#print "$b is EXPANDED\n";
    }

    foreach my $b(keys %list){
	unless(($list{$b}{TAR}==1) &&
	       ($list{$b}{EXPANDED}==1)){
	    my $tag;
	    if($list{$b}{TAR}==1){
		$tag="INTAR\t$b\t$dir";
	    }elsif($list{$b}{EXPANDED}==1){
		$tag="INEXPANDED\t$b\t$dir";
	    }else{
		"ERROR $b\n";
		die;
	    }
	    push @differences, $tag;
	}
    }

    unless(scalar @differences > 0){
	$toskip{$tar}=1;
    }

    return @differences;
}



sub PrintSkip{
    my $f=$_[0];
    open(FOUT, "> $f");
    foreach my $tar(keys %toskip){
	print FOUT $tar."\n";
    }
    close(FOUT);
}
