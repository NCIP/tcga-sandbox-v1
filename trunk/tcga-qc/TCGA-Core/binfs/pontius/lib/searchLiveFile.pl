#!/usr/bin/perl


################################################################
### search all live archives for string match in archive name
################################################################
use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";
use File::Find;

my @match = @ARGV;
my %files = GetLatest(1);
my @filematches;
foreach my $tar(keys %files){
    my $dir=$tar;
    $dir=~s/.tar.gz//;
    find(\&find_here, $dir);
}

my $output = join ".", @match, "lst";
open(FOUT, "> $output");
foreach my $file(@filematches){
    print FOUT $file."\n";
}
close(FOUT);

sub find_here {
    my $file = $File::Find::name;
    $file =~ s/\\/\//g;  
    foreach my $t(@match){
	return if (index($file, $t) < 0);
    }
    push @filematches, $file;     
}
