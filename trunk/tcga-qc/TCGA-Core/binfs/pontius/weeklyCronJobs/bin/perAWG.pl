#!/usr/bin/perl


use strict;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $DIR_OUT=$ARGV[0];
my @cancers = GetCancers();

foreach my $study(@cancers){    
    my @lines = AWG_summary($study);
    my $fout = $DIR_OUT."/".$study.".awg.lst";
    open(FOUT, "> $fout");
    foreach my $line(@lines){
	print FOUT $line."\n";
    }
    close(FOUT);
    
}
