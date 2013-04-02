#!/usr/bin/perl

use Pod::Usage;
use IO::Uncompress::Gunzip;
use strict;
use warnings;

# refFlat column headers
my @hdrs = qw(
 geneName
 name
 chrom
 strand
 txStart
 txEnd
 cdsStart
 cdsEnd
 exonCount
 exonStarts
 exonEnds
);

pod2usage(1) unless (@ARGV);

my $refflat = shift();
my $refh = IO::Uncompress::Gunzip->new($refflat) or die $!;

while (<$refh>) {
  chomp;
  my @outdata;
  my %indata;
  @indata{@hdrs} = split /\t/;
  $indata{chrom} =~ s/chr//;
  push @outdata, @indata{qw(geneName chrom txStart txEnd)};
  my @exstarts = split (/,/, $indata{'exonStarts'});
  my @exends = split(/,/, $indata{'exonEnds'});
  my @exons;
  for (my $i = $indata{exonCount};$i;$i--) {
    push @exons, shift(@exstarts).'-'.shift(@exends);
  }
  push @outdata, join(',',@exons);
  print join("\t",@outdata),"\n";
}

=head1 NAME

convertRefFlat.pl - convert UCSC refFlat into Carl's genome2gene

=head1 SYNOPSIS

$ convertRefFlat.pl refFlat.txt.gz > genome2gene.dat

=head1 DESCRIPTION

Carl's data browser genome location munger uses selected columns from
the refFlat file, and rearranges the exon coordinates. This script 
does the conversion.

The geneName is not a primary key for the refFlat files. Different sets of exons
can appear for the same gene (due presumably to alternative splicing), and 
different txStart and txStop pairs can appear for the same gene (ditto).

=head1 AUTHOR

Mark A. Jensen (mark -dot- jensen -at- nih -dot- gov)

=cut
