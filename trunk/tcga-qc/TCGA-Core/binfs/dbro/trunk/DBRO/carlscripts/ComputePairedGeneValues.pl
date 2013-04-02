#!/usr/local/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/ComputePairedGeneValues.pl,v $
## $Revision: 1.1 $
## $Date: 2009/02/26 14:27:39 $

use strict;

my ($tumorf, $normalf, $cnvoverlapf) = @ARGV;

my (%normal2bc, %gene2order, %normal2gene, %cnv);
my $next_gi = 0;

use constant AMP_CAP_L2R => 2.0000;
use constant AMP_MIN_L2R => 1.0000;
use constant DEL_CAP_L2R => -2.0000;
use constant DEL_MAX_L2R => 0.9000;

ReadCNV($cnvoverlapf);
ReadNormal($normalf);
ReadTumor($tumorf);

######################################################################
sub ReadCNV {
  my ($f) = @_;
  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($gene, $chr, $start) = split /\t/;
    $cnv{"$gene,$chr,$start"} = 1;
  }
  close INF;
}
######################################################################
sub ReadNormal {
  my ($f) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($chr, $barcode, $gene, $left, $right, $length, $gene_coverage,
      $gene_l2r, $gene_extreme) = split /\t/;
    my $patient = substr($barcode, 0, 12);
    $normal2bc{$patient} = $barcode;
    my $term = "$gene,$chr,$left";
    if (! defined $gene2order{$term}) {
      $gene2order{$term} = $next_gi;
      $next_gi++;
    }
    my $gi = $gene2order{$term};
    $normal2gene{$patient}[$gi] = $gene_extreme;
  }
  close INF;
}

######################################################################
sub ReadTumor {
  my ($f) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($chr, $barcode, $gene, $left, $right, $length, $gene_coverage,
      $gene_l2r, $gene_extreme) = split /\t/;
    my $patient = substr($barcode, 0, 12);
    my $term = "$gene,$chr,$left";
    my $cnv = "NOCNV";
    if (defined $cnv{$term}) {
      $cnv = "CNV";
    }
    my $is_paired = "UNPAIRED";
    my $extreme = $gene_extreme;
    if (defined $normal2gene{$patient}) {
      $is_paired = "PAIRED";
      my $gi = $gene2order{"$gene,$chr,$left"};
      my $normal_extreme = 0;
      if (defined $normal2gene{$patient}[$gi]) {
        $normal_extreme = $normal2gene{$patient}[$gi];
      }
      $extreme = sprintf("%.4f", $gene_extreme - $normal_extreme);
    }
    my $capped_extreme = $extreme;
    if ($extreme > AMP_CAP_L2R) {
      $capped_extreme = AMP_CAP_L2R;
    } elsif ($extreme < DEL_CAP_L2R) {
      $capped_extreme = DEL_CAP_L2R;
    } elsif ($extreme > DEL_MAX_L2R && $extreme < AMP_MIN_L2R) {
      $capped_extreme = 0;
    }
    $extreme = sprintf("%.4f", $extreme);
    $capped_extreme = sprintf("%.4f", $capped_extreme);
    print join("\t", $barcode, $gene, $chr, $left, $right,
        $extreme, $capped_extreme, $is_paired, $cnv) . "\n";
  }
  close INF;
}
