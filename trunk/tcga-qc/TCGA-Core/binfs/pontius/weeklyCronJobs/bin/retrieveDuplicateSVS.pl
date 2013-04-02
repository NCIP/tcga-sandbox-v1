#!/usr/bin/perl

use strict;
use File::Compare;

require $ENV{HOME}."/lib/tools_dcc.pl";
my $DEBUG=0;
my $DIR_OUT=$ARGV[0];
my %latest = Archive2LatestLocation();
my %sizes;

foreach my $archive(keys %latest){
    foreach my $tar(keys %{$latest{$archive}}){
	print $tar."\n" if ($DEBUG==1);
	my $dir=$tar;
	$dir=~s/.tar.gz//;
	foreach my $file(<$dir/*svs>){
	    my $size = -s $file;
	    push @{$sizes{$size}}, $file;
	}
    }
}


open(FOUT, "> $DIR_OUT/duplicateSizes.txt") || die "Could not write to $DIR_OUT\n";
foreach my $size(keys %sizes){
    my @list = @{$sizes{$size}};
    next if((scalar @list) < 2);
    for(my $i=0; $i< scalar @list; $i++){
	for(my $j=$i+1; $j< scalar @list; $j++){
	    if(compare($list[$i], $list[$j]) == 0 ){
	        print FOUT "$size\t$list[$i]\t$list[$j]\n";		
	    }
	}
    }
}
close(FOUT);

