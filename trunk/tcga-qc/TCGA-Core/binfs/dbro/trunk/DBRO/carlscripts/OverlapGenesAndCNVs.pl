#!/usr/local/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/OverlapGenesAndCNVs.pl,v $
## $Revision: 1.2 $
## $Date: 2009/05/08 17:03:27 $

BEGIN {
  my @path_elems = split("/", $0);
  pop @path_elems;
  push @INC, join("/", @path_elems);
}

use strict;
use Segment;

my ($binf, $genef, $cnvf) = @ARGV;

my (%bins, %bin2cnv, $BIN_SIZE);

ReadBinFile($binf, \%bins);
ReadCNVFile($cnvf, \%bins);
ReadGeneFile($genef, \%bins);

######################################################################
sub ReadBinFile {
  my ($f, $bins) = @_;

  my (%chr_bins, @bin2chr, @bin2start, @bin2stop, @bin2mid,
      %bin2gene, %gene2seg);

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    if (/^##/) {
      my ($dummy, $chr, $first_bin, $last_bin) = split /\t/;
      $chr_bins{start}{$chr} = $first_bin;
      $chr_bins{stop}{$chr}  = $last_bin;
    } else {
      my ($bin, $chr, $start, $stop) = split /\t/;
      $bin2chr[$bin] = $chr;
      $bin2start[$bin] = $start;
      $bin2stop[$bin] = $stop;
      $bin2mid[$bin] = int(($start + $stop) / 2);
    }
  }
  close INF;

  $$bins{chr_bins}  = \%chr_bins;
  $$bins{bin2chr}   = \@bin2chr;
  $$bins{bin2start} = \@bin2start;
  $$bins{bin2stop}  = \@bin2stop;
  $$bins{bin2mid}   = \@bin2mid;
  $$bins{BIN_SIZE}  = $bin2stop[$chr_bins{start}{1}] -
      $bin2start[$chr_bins{start}{1}] + 1;
  $$bins{bin2gene}  = \%bin2gene;
  $$bins{gene2seg}  = \%gene2seg;
  $BIN_SIZE = $$bins{BIN_SIZE};
}

######################################################################
sub ReadGeneFile {
  my ($f, $bins) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($gene, $chr, $start, $stop) = split /\t/;
    my $seg = new Segment($chr, $start, $stop, $gene);
    my $start_bin = $$bins{chr_bins}{start}{$chr}  +
        int($start / $BIN_SIZE);
    my $stop_bin  = $$bins{chr_bins}{start}{$chr}  +
        int($stop  / $BIN_SIZE);
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      for my $cnv_seg (@{ $bin2cnv{$bin} }) {
        my $ov = $cnv_seg->Overlap($seg)->Length;
        if ($ov > 0) {
          my $left = $cnv_seg->Left;
          my $right = $cnv_seg->Right;
          print "$gene\t$chr\t$start\t$stop\t$left\t$right\t$ov\n";
        }
      }
    }

  }
  close INF;
}

######################################################################
sub ReadCNVFile {
  my ($f, $bins) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($label, $chr, $start, $stop) = split /\t/;
    my $cnv_seg = new Segment($chr, $start, $stop, "cnv");

    my $start_bin = $$bins{chr_bins}{start}{$chr}  +
        int($start / $BIN_SIZE);
    my $stop_bin  = $$bins{chr_bins}{start}{$chr}  +
        int($stop  / $BIN_SIZE);
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      push @{ $bin2cnv{$bin} }, $cnv_seg;
    }
  }
  close INF;
}
