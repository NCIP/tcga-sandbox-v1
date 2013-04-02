#!/usr/bin/perl


use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";


my $study = $ARGV[0];
unless($study){
    print "Syntax:\n./awg_dcc_update.pl diseaseabbreviation\n";
    die;
}
my @list =  AWG_summary($study);

my $fout = "TCGA_DCC_$study.txt";
open(FOUT, "> $fout");
foreach my $line(@list){
    print FOUT $line."\n";
}
close(FOUT);

