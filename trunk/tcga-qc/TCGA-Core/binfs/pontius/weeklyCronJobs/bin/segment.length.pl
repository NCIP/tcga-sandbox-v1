#!/usr/bin/perl

###############################################
## some level_3 archives represent segments.
## this scripts flags segment lengths of zero
###############################################

use strict;
use File::Basename;
require $ENV{HOME}."/lib/tools_dcc.pl";
my $FILE_OUT=$ARGV[0];

## list of headers that indicate chromosome coordinates
my %headers;
$headers{START}{"loc.start"}=1;
$headers{STOP}{"loc.end"}=1;
$headers{START}{Start}=1;
$headers{STOP}{End}=1;
$headers{START}{start}=1;
$headers{STOP}{end}=1;

my $SIZE_CUTOFF=1;
my %list = Archive2LatestLocation();



open(FOUT, ">$FILE_OUT");
foreach my $archive(keys %list){
    my @tars=(keys %{$list{$archive}});
    next unless(($tars[0] =~ m/cna/) ||
		($tars[0] =~ m/snp/));
    next unless($tars[0] =~ m/Level_3/);
    my $dir=$tars[0];
    $dir=~s/.tar.gz//;
    $dir=~s/.tar//;
    foreach my $file(<$dir/*>){
	next if($file=~m/MANIFEST/);
	next if($file=~m/README/);
	next if($file=~m/DESCRIPTION/);
	next if($file=~m/CHANGES_DCC.txt/);

	open(FIN, $file);
	my $line=<FIN>;
	unless(($line=~m/start/) ||
	       ($line=~m/loc.start/) ||
	       ($line=~m/Start/)){
	    print "$file\n$line";
	    die;
	}
	chomp $line;	   	
	my @cols=split(/\t/, $line);
	my $index_start;
	my $index_stop;
	for(my $i=0; $i< scalar @cols; $i++){
	    if($headers{START}{$cols[$i]}==1){
		$index_start=$i;
	    }elsif($headers{STOP}{$cols[$i]}==1){
		$index_stop=$i;
	    }	
	}

	if($index_start == 0){
	    print $file."----\n";
	    next;
	}
	## get values and flag short segments
	while($line=<FIN>){
	    chomp $line;
	    my @a=split(/\t/, $line);
	    if(($a[$index_stop] -$a[$index_start]) < $SIZE_CUTOFF){
		printf( FOUT "%s\t%s\t%s\n", $archive, $line, basename($file));
	    }
	}
	close(FIN);
    }
}
close(FOUT);
