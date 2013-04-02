#!/usr/bin/perl


use strict;
require $ENV{HOME}."/lib/tools_dcc.pl";



my @a=GetLiveFiles();
foreach my $line(@a){
    my($file, $file_id)=split(/\t/, $line);
    unless(-s $file){
	print "FILEABSENT\t$file\t$file_id\n";
    }
}
