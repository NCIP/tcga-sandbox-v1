#!/usr/bin/perl


################################################################
### search all live archives for string match in archive name
################################################################
use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";


my @match = @ARGV;
print $match[0]."\n";

my @files = SearchLive(@match);
foreach my $f(@files){
    print $f."\n";
}
