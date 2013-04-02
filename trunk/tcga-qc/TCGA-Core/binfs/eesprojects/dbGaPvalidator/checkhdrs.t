#!/usr/bin/perl
#-*-perl-*-
use Test::More qw(no_plan);
use Pod::Usage;
use strict;
use warnings;

my $filestem = shift;
pod2usage(2) unless $filestem;

my $ddfile = "${filestem}_dd.txt";
my $datafile = "${filestem}.txt";

ok -e $ddfile, "data dict file present";
ok -e $datafile, "data file present";

open my $ddh, $ddfile or die $!;
open my $datah, $datafile or die $!;

my $l = <$ddh>; chomp $l;
my @ddhdrs = split /\t/,$l;
$l = <$datah>; chomp $l;
my @data_varnames = split /\t/,$l;

is $ddhdrs[0], 'VARNAME', "$ddfile looks like dbGaP data dict";
my @dd_varnames;
while (<$ddh>) {
    chomp;
    my %flds;
    @flds{@ddhdrs} = split /\t/;
    push @dd_varnames, $flds{VARNAME};
}

is_deeply \@dd_varnames, \@data_varnames, "data dict and data file varnames are the same";

=head1 NAME

checkhdrs.t - Check that dbGaP data dict and data file VARNAMEs are the same

=head1 SYNOPSIS

 $ ./checkhdrs.t where_the_files_are/tcga_slide_OV 

 Argument is the file stem of a dbGaP data dictionary/data file pair
 present in the same directory. For the above example, the script 
 expect to find where_the_files_are/tcga_slide_OV.txt and 
 where_the_files_are/tcga_slide_OV_dd.txt.

=head1 AUTHOR

Mark A. Jensen ( mark -dot- jensen -at- nih -dot- gov )

=head1 LICENSE

This software is (c) 2012 SRA International, Inc.
This software is available under the terms of the caBIG license.

=cut

