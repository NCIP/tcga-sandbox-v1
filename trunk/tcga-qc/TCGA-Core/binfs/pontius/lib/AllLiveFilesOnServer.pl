#!/usr/bin/perl

use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";
use File::Basename;
##
## get list of all files in latest and available archives.
##

my %latestArchives =  Archive2LatestLocation();
my %archive2revision;
open(FOUT, "> serverarchive_files.txt");
foreach my $archive(keys %latestArchives){

    ##################
    ## get revision for eventual checks
    my @a=split(/\./, $archive);
    my $revision=$a[-2];
    my $n=(scalar @a)-3;
    my $b=join(".",@a[0..$n]);
    $archive2revision{$b}=$revision;

    ####################
    foreach my $tar(keys %{$latestArchives{$archive}}){
	my $dir=$tar;
	$dir=~s/.tar.gz//;
	$dir=~s/.tar$//;
	foreach my $file(<$dir/*>){
	    print FOUT $archive."\t".$file."\n";
	}
    }
}
close(FOUT);



