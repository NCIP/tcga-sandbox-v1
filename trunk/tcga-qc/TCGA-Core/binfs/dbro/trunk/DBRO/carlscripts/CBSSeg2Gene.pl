#!/usr/local/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/CBSSeg2Gene.pl,v $
## $Revision: 1.3 $
## $Date: 2009/05/08 17:02:34 $

BEGIN {
  my @path_elems = split("/", $0);
  pop @path_elems;
  push @INC, join("/", @path_elems);
}

use strict;
use Segment;

my %bins = ("small" => {});
my ($small_bin_f, $seg_f, $sample_f, $out_f, $bin_gene_f,
    $freq_f, $cnv_f);
my ($min_seg, $min_overlap) = (1, 1);
my ($BIN_SIZE);
my (%bin2cnv);
my (%gene_coverage_array, %gene_l2r_array,
    %exon_coverage_array, %exon_l2r_array,
    %gene_extreme_array, %exon_extreme_array);
my (%all_barcodes, %keep);
my (%gene_order, @gene_order);
my (@exon_order, @gene2exon_length);

my @chr_order = (
      "1",   "2",  "3",  "4",  "5",  "6",  "7",  "8",  "9", "10",
      "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
      "21", "22", "X", "Y"
  );
my %chr_order;
for (my $i = 0; $i < @chr_order; $i++) {
  $chr_order{$chr_order[$i]} = $i;
}

my $BASE = 2;

ReadOptions();

if (! defined $out_f) {
  die "must specify output file";
}
if (! defined $small_bin_f) {
  die "must specify small bin file";
}
if (! defined $seg_f) {
  die "must specify segmented data file";
}
if (defined $sample_f) {
  ReadSampleFile($sample_f);
}

ReadBinFile($small_bin_f, \%bins, "small");

if (defined $cnv_f) {
  ReadCNVFile($cnv_f, $bins{small});
}

ReadGeneFile($bin_gene_f, $bins{small});

open (OUT, ">$out_f") or die "cannot open $out_f";
ReadSeg($seg_f);
close OUT;

######################################################################
sub r_numerically { $b <=> $a };
sub   numerically { $a <=> $b };

######################################################################
use constant LOG2 => log(2);
use constant LOG10 => log(10);

######################################################################
sub Log2 {
  my ($x) = @_;
  return log($x)/LOG2;
}

######################################################################
sub Max {
  my ($a, $b) = @_;
  if ($a > $b) {
    return $a;
  } else {
    return $b;
  }
}

######################################################################
sub round {
  my ($x) = @_;

  if ($x < 0) {
    return int($x - 0.5);
  } elsif ($x > 0) {
    return int($x + 0.5);
  } else {
    return 0;
  }
}

######################################################################
#sub ReadExonFile {
#  my ($f) = @_;
#
#  open(INF, $f) or die "cannot open $f";
#  while (<INF>) {
#    s/[\r\n]+//;
#    my ($gene, $chr, $start, $stop, $exon) = split /\t/;
#    my $seg = new Segment($chr, $start, $stop, "$gene,$exon");
#    $gene2exon_length{$gene} += $seg->Length;
#    push @{ $gene2exon{$gene}{$chr} }, $seg;
#  }
#  close INF;
#}

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

######################################################################
sub ReadGeneFile {
  my ($f, $bins) = @_;

  my $BIN_SIZE = $$bins{BIN_SIZE};
  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    my ($gene, $chr, $start, $stop, $exons) = split /\t/;
    my $seg = new Segment($chr, $start, $stop, $gene);
    my $start_bin = $$bins{chr_bins}{start}{$chr}  +
        int($start / $BIN_SIZE);
    my $stop_bin  = $$bins{chr_bins}{start}{$chr}  +
        int($stop  / $BIN_SIZE);
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
#print "#adding\t$gene\t$bin\t$chr\t$start_bin\t$stop_bin\t$BIN_SIZE\n";
      $$bins{bin2gene}{$bin}{$gene} = 1;
      push @{ $$bins{gene2seg}{$gene} }, $seg;
    }
    push @gene_order, $seg;
    push @{ $gene_order{$gene} }, $#gene_order;
    for my $exon(split(",", $exons)) {
      my ($estart, $estop) = split("-", $exon);
      my $eseg = new Segment($chr, $estart, $estop, "$gene,$exon");
      $gene2exon_length[$#gene_order] += $eseg->Length;
      push @{ $exon_order[$#gene_order] }, $eseg;
    }
  }
  close INF;
}

######################################################################
sub ReadSampleFile {
  my ($f) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\r\n]+//;
    $keep{$_} = 1;
  }
  close INF;
}

######################################################################
sub ReadBinFile {
  my ($f, $bins, $id) = @_;

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

  $$bins{$id}{chr_bins}  = \%chr_bins;
  $$bins{$id}{bin2chr}   = \@bin2chr;
  $$bins{$id}{bin2start} = \@bin2start;
  $$bins{$id}{bin2stop}  = \@bin2stop;
  $$bins{$id}{bin2mid}   = \@bin2mid;
  $$bins{$id}{BIN_SIZE}  = $bin2stop[$chr_bins{start}{1}] -
      $bin2start[$chr_bins{start}{1}] + 1;
  $$bins{$id}{bin2gene}  = \%bin2gene;
  $$bins{$id}{gene2seg}  = \%gene2seg;
  $BIN_SIZE = $$bins{$id}{BIN_SIZE};
}

######################################################################
sub Output {
  my ($barcode) = @_;

  for (my $i = 0; $i < @gene_order; $i++) {
#    for my $barcode (keys %all_barcodes) {
      my ($gene_coverage, $exon_coverage, $gene_l2r, $exon_l2r);
      my ($gene_extreme, $exon_extreme);
      if (defined $gene_coverage_array{$barcode}[$i]) {
        $gene_coverage = $gene_coverage_array{$barcode}[$i];
        $gene_l2r = $gene_l2r_array{$barcode}[$i] / $gene_coverage;
      }
      if (defined $exon_coverage_array{$barcode}[$i]) {
        $exon_coverage = $exon_coverage_array{$barcode}[$i];
        $exon_l2r = $exon_l2r_array{$barcode}[$i] / $exon_coverage;
      }
      if (defined $gene_extreme_array{$barcode}[$i]) {
        $gene_extreme = $gene_extreme_array{$barcode}[$i];
      }
      if (defined $exon_extreme_array{$barcode}[$i]) {
        $exon_extreme = $exon_extreme_array{$barcode}[$i];
      }
      if (! defined $gene_coverage) {
        $gene_coverage = sprintf("%.4f", 0);
        $gene_l2r = sprintf("%.4f", 0);
      } else {
        $gene_coverage = sprintf("%.4f", $gene_coverage);
        $gene_l2r = sprintf("%.4f", $gene_l2r);
      }
      if (! defined $exon_coverage) {
        $exon_coverage = sprintf("%.4f", 0);
        $exon_l2r = sprintf("%.4f", 0);
      } else {
        $exon_coverage = sprintf("%.4f", $exon_coverage);
        $exon_l2r = sprintf("%.4f", $exon_l2r);
      }
      if (! defined $gene_extreme) {
        $gene_extreme = sprintf("%.4f", 0);
      } else {
        $gene_extreme = sprintf("%.4f", $gene_extreme);
      }
      if (! defined $exon_extreme) {
        $exon_extreme = sprintf("%.4f", 0);
      } else {
        $exon_extreme = sprintf("%.4f", $exon_extreme);
      }
      my $gseg = $gene_order[$i];
      print OUT join("\t",
          $gseg->Chr,
          $barcode,
          $gseg->Id,
          $gseg->Left,
          $gseg->Right,
          $gseg->Length,
          $gene_coverage,
          $gene_l2r,
          $gene_extreme,
          $exon_coverage,
          $exon_l2r,
          $exon_extreme
          ) . "\n";
#    }
  }
  undef %gene_coverage_array;
  undef %gene_l2r_array;
  undef %gene_extreme_array;
  undef %exon_coverage_array;
  undef %exon_l2r_array;
  undef %exon_extreme_array;
}

######################################################################
sub ReadSeg {
  my ($f) = @_;

  my ($chr_bins, $bin2chr, $bin2start, $bin2stop, $bin2mid, $bin2gene,
      $gene2seg, $BIN_SIZE) = 
      ($bins{small}{chr_bins},
       $bins{small}{bin2chr},
       $bins{small}{bin2start},
       $bins{small}{bin2stop},
       $bins{small}{bin2mid},
       $bins{small}{bin2gene},
       $bins{small}{gene2seg},
       $bins{small}{BIN_SIZE}
      );

  my $last_barcode;

  open(INF, $f) or die "cannot open $f";

  while (<INF>) {

    if (! /^TCGA/) {
#      next;
    }
    s/[\r\n]+//;

##    my ($barcode, $chr, $start, $stop, $markers, $informative_markers,
##        $mean) = split /\t/;
    my ($barcode, $chr, $start, $stop, $markers, $mean) = split /\t/;

    if (defined $sample_f && ! defined $keep{$barcode}) {
print STDERR "discarding $barcode\n";
      next;
    }

    if (defined $last_barcode && $last_barcode !~ /barcode/i &&
        $barcode ne $last_barcode) {
      Output($last_barcode);
    }

    $last_barcode = $barcode;

    $all_barcodes{$barcode} = 1;

    if ($chr eq 23) {
      $chr = "X";
    } elsif ($chr eq 24) {
      $chr = "Y";
    }

    if (! defined $chr_order{$chr}) {
print STDERR "bad chromosome $chr\n";
      next;
    }

    my $digest;
    if (defined $cnv_f) {
      $digest = DigestSegByCNV($chr_bins, $chr, $start, $stop);
    } else {
      $digest = [ new Segment($chr, $start, $stop) ];
    }
    ProcessSegSet($digest, $chr_bins, $bin2gene, $gene2seg,$chr,
        $barcode, $mean);
  }
  Output($last_barcode);

  close INF;
}


######################################################################
sub ProcessSegSet {
  my ($set, $chr_bins, $bin2gene, $gene2seg, $chr, $barcode, $mean) = @_;

  for my $seg (@{ $set }) {
    my ($start, $stop) = ($seg->Left, $seg->Right);
    if ($seg->Length < $min_seg) {
      print STDERR "short segment $chr:$start-$stop for $barcode\n";
      next;
    }

    if ($mean =~ /[0-9]/ && $mean !~ /[^0-9eE\-\. ]/) {
    } else {
      next;
    }

    my $start_bin = $$chr_bins{start}{$chr}  + int($start / $BIN_SIZE);
    my $stop_bin  = $$chr_bins{start}{$chr}  + int($stop  / $BIN_SIZE);

    my %genes;
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      for my $gene (keys %{ $$bin2gene{$bin} }) {
        $genes{$gene} = 1;
      }
    }
    my %gis;
    for my $gene (keys %genes) {
      for my $gi (@{ $gene_order{$gene} }) {
        if ($seg->Chr eq $gene_order[$gi]->Chr &&
            $seg->Overlap($gene_order[$gi])->Length > 0) {
	    $gis{$gi} = 1;
        }
      }
    }

    for my $gi (keys %gis) {

      my ($exon_overlap, $exon_overlapr);
      for my $exon (@{ $exon_order[$gi] }) {
        my $ov = $seg->Overlap($exon);
        my $olength = $ov->Length;
        if ($olength > 0) {
          $exon_overlap += $olength;
        }
      }
      if ($exon_overlap > 0) {
        $exon_overlapr = $exon_overlap/$gene2exon_length[$gi];
      }
      if (defined $exon_overlapr) {
        $exon_coverage_array{$barcode}[$gi] += $exon_overlapr;
        $exon_l2r_array{$barcode}[$gi] += $mean * $exon_overlapr;
        if (! defined $exon_extreme_array{$barcode}[$gi] ||
            abs($mean) > $exon_extreme_array{$barcode}[$gi]) {
          $exon_extreme_array{$barcode}[$gi] = $mean;
        }
      }

      my $gseg = $gene_order[$gi];
      my ($gene_overlap, $gene_overlapr);
      my $ov = $seg->Overlap($gseg);
      $gene_overlap = $ov->Length;
      if ($gene_overlap < $min_overlap) {
        next;
      }
      $gene_overlapr = $gene_overlap/$gseg->Length;
      $gene_coverage_array{$barcode}[$gi] += $gene_overlapr;
      $gene_l2r_array{$barcode}[$gi] += $mean * $gene_overlapr;
      if (! defined $gene_extreme_array{$barcode}[$gi] ||
          abs($mean) > $gene_extreme_array{$barcode}[$gi]) {
        $gene_extreme_array{$barcode}[$gi] = $mean;
      }
    }
  }
}

######################################################################
sub DigestSegByCNV {
  my ($chr_bins, $chr, $start, $stop) = @_;

  my (@cnvs, @keep);

  my $seg = new Segment($chr, $start, $stop, "");      

  my $start_bin = $$chr_bins{start}{$chr}  + int($start / $BIN_SIZE);
  my $stop_bin  = $$chr_bins{start}{$chr}  + int($stop  / $BIN_SIZE);

  my %counted;
  for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
    for my $cnv (@{ $bin2cnv{$bin} }) {
      my ($left, $right) = ($cnv->Left, $cnv->Right);
      ## possibly multiple bins for a cnv, but add a cnv only once
#      if (! defined $counted{"$left,$right"}) {
      if (! defined $counted{$left}{$right}) {
        if ($cnv->Overlap($seg)->Length > 0) {
#          $counted{"$left,$right"} = 1;
          $counted{$left}{$right} = 1;
#          push @cnvs, $cnv;
        }
      }
    }
  }

  ## we already know that every surviving cnv overlaps the seg
  my $i = $start;
  for my $left (sort numerically keys %counted) {
    if ($i >= $stop || $left > $stop) {
      last;
    }
    for my $right (sort r_numerically keys %{ $counted{$left} }) {
      if ($left > $i) {
        my $cseg = new Segment($chr, $i, $left - 1);
        if ($cseg->Length >= $min_seg) {
          push @keep, $cseg;
        }
      }
      $i = $right + 1;
      last;    ## we took max of right, so go to next left
    }
  }
  if ($stop - $i + 1 >= $min_seg) {
    push @keep, new Segment($chr, $i, $stop);
  }
  return \@keep;
}

######################################################################
sub ReadOptions {

  use Getopt::Long;

  if (@ARGV < 1) {
    print STDERR join("\n\t",
      "options:",
      "small_bins: name of small bin definition file",
      "seg: name of file containing segmented copy number data",
      "samples: name of file specifying samples to include",
      "o: name of output file",
      "genes: name of file mapping small bins to genes",
#      "exons: name of file mapping bins to exons",
      "cnv: name of file defining regions of copy number variation",
      "minseg: minimum length for a segment to be considered",
      "overlap: minimum overlap for a segment to be considered"
    ) . "\n";
    exit;
  }

  GetOptions (
    "small_bins:s" => \$small_bin_f,
    "seg:s"        => \$seg_f,
    "samples:s"    => \$sample_f,
    "o:s"          => \$out_f,
    "genes:s"      => \$bin_gene_f,
#    "exons:s"      => \$exon_f,
    "cnv:s"        => \$cnv_f,
    "minseg:s"     => \$min_seg,
    "overlap:s"    => \$min_overlap
  ) or die "exiting";

}
