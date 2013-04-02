#!/usr/bin/perl
#-*-perl-*-
use Test::More qw(no_plan);
use Pod::Usage;
use strict;
use warnings;

# check for null VARDESC fields in the input file

my $filename = shift;
pod2usage(2) unless $filename;
open my $f, $filename or die $!;

my $l = <$f>;
chomp $l;
my @hdr = split /\t/, $l;

is $hdr[1], 'VARDESC', 'infile is a dbGaP dd'; 
my $i=1;

while (<$f>) {
    $i++;
    chomp;
    my %data;
    @data{@hdr} = split /\t/;
    ok length($data{VARDESC}), "line $i VARDESC not empty";
    unlike $data{VARDESC}, qr/^null$/i, "line $i VARDESC not 'null'";
}

=head1 NAME

check4null.t - Check that dbGaP data dict contains no null VARDESC definitions

=head1 SYNOPSIS

 $ ./check4null.t where_the_files_are/tcga_slide_OV_dd.txt

=head1 AUTHOR

Mark A. Jensen ( mark -dot- jensen -at- nih -dot- gov )

=head1 LICENSE

This software is (c) 2012 SRA International, Inc.
This software is available under the terms of the caBIG license.

=cut

1;
