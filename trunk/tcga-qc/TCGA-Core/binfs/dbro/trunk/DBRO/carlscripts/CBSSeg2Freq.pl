#!/usr/local/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/CBSSeg2Freq.pl,v $
## $Revision: 1.2 $
## $Date: 2009/05/08 17:02:34 $

BEGIN {
  my @path_elems = split("/", $0);
  pop @path_elems;
  push @INC, join("/", @path_elems);
  push @INC, 
}

use strict;
use Segment;

my ($seg_f, $sample_f, $freq_f, $kill_xy, $amp_cap, $del_cap);
my (%keep);
my (%cn_freq, %base_pairs, %n_segs, %total_cn, %total_l2r);

my $BASE = 2;

ReadOptions();

if (! defined $seg_f) {
  die "must specify segmented data file";
}

if (! defined $freq_f) {
  die "must specify frequency output file";
}

if (defined $sample_f) {
  ReadSampleFile($sample_f);
}

ReadSeg($seg_f);

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
sub ReadSeg {
  my ($f) = @_;

  open(INF, $f) or die "cannot open $f";

  while (<INF>) {

    if (! /^TCGA/) {
#      next;
    }
    s/[\r\n]+//;

    my ($barcode, $chr, $start, $stop, $markers, $mean) = split /\t/;

#    if (length($barcode) > 20) {
#      $barcode = substr($barcode, 0, 20);
#    }

    if (defined $sample_f && ! defined $keep{$barcode}) {
#print STDERR "discarding $barcode\n";
      next;
    }

    if ($chr eq 23) {
      $chr = "X";
    } elsif ($chr eq 24) {
      $chr = "Y";
    }

    if ($chr eq "X" || $chr eq "Y") {
      if ($kill_xy) {
        next;
      }
    }

    $n_segs{$barcode}++;
    my $seg_length = $stop - $start + 1;
    $base_pairs{$barcode} += $seg_length;

    if (defined $amp_cap && $mean > $amp_cap) {
      $mean = $amp_cap;
    } elsif (defined $del_cap && $mean < $del_cap) {
      $mean = $del_cap;
    }
    $total_l2r{$barcode} += ($seg_length * $mean);
    my $cn = ($BASE**$mean)*2;
    $total_cn{$barcode} += ($seg_length * $cn); 
    $cn = sprintf("%.1f", $cn);
    $cn_freq{$barcode}{$cn}++;
  }

  close INF;

  open(FREQ, ">$freq_f") or die "cannot open $freq_f";
  for my $barcode (keys %cn_freq) {
    print FREQ join("\t",
        "nsegs",
        $barcode,
        $n_segs{$barcode}
    ) . "\n";
    print FREQ join("\t",
        "mean_cn",
        $barcode,
        sprintf("%.2f", $total_cn{$barcode}/$base_pairs{$barcode})
    ) . "\n";
    print FREQ join("\t",
        "mean_l2r",
        $barcode,
        sprintf("%.2f", $total_l2r{$barcode}/$base_pairs{$barcode})
    ) . "\n";
    for my $cn (sort numerically keys %{ $cn_freq{$barcode} }) {
      print FREQ join("\t",
          "histogram",
           $barcode,
           $cn,
           $cn_freq{$barcode}{$cn}
      ) . "\n";
    }
  }
  close FREQ;
}

######################################################################
sub ReadOptions {

  use Getopt::Long;

  if (@ARGV < 1) {
    print STDERR join("\n\t",
      "options:",
      "seg: name of file containing segmented copy number data",
      "samples: name of file specifying samples to include",
      "freq: name of file to store cn frequencies",
      "ampcap: value at which to cap amplifications",
      "delcap: value at which to cap deletions",
      "killxy: ignore chr X, Y"
    ) . "\n";
    exit;
  }

  GetOptions (
    "seg:s"        => \$seg_f,
    "samples:s"    => \$sample_f,
    "freq:s"       => \$freq_f,
    "killxy"       => \$kill_xy,
    "ampcap:f"     => \$amp_cap,
    "delcap:f"     => \$del_cap
  ) or die "exiting";

}
