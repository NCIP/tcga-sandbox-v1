#!/usr/bin/perl

############################################################
#
# find odd md5 files, that end with carriage return
#
############################################################
use strict;
require "/h1/pontiusj/lib/tools_dcc.pl";

my $FOUT=$ARGV[0];
my %is_latest = Archive2LatestLocation();
open(FOUT, "> $FOUT");
foreach my $archive(keys %is_latest){    
    foreach my $tarfile(keys %{$is_latest{$archive}}){    
	###################################################
	## check duplicate md5 files
	my @f=(<$tarfile.md5*>);
	if((scalar @f) > 1){
	    foreach my $f(@f){
		print FOUT $f."\n";
	    }
	}    
    }
}
close(FOUT);
