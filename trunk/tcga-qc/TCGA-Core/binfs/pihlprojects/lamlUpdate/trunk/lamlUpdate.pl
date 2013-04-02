#!/usr/bin/perl
use warnings;
use strict;

use JSON;
use File::Find;

my $startdir = "/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/laml/bcr/genome.wustl.edu/bio/clin/genome.wustl.edu_LAML.bio.Level_1.25.10.0/";
my @biospecimen_files;

find(sub{ push @biospecimen_files, $File::Find::name if( -f and /^genome.wustl.edu_biospecimen./)}, $startdir);

foreach (@biospecimen_files){
	print("$_\n");
}
