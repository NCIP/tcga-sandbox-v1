#!/usr/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/CNVByBins.pl,v $
## $Revision: 1.2 $
## $Date: 2009/05/08 17:02:57 $

BEGIN {
  my @path_elems = split("/", $0);
  pop @path_elems;
  push @INC, join("/", @path_elems);
}

use strict;

use Segment;

my ($samplef, $segf);
my ($binsize, $floor, $ceiling, $min_freq, $label);
my (%keep_samples, %chr2seg);
my ($n_samples, $min_count);

ReadOptions();
if (defined $samplef) {
  ReadSampleFile($samplef);
}
ReadSeg($segf);
Analyze();

######################################################################
sub Analyze {
  for (my $chr = 1; $chr <= 22; $chr++) {
    DoChr($chr);
  }
}

######################################################################
sub ReadSampleFile {
  my ($f) = @_;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    if (/^#/) {
      next;
    }
    s/[\n\r]+//;
    $keep_samples{$_} = 1;
  }
  close INF;
}

######################################################################
sub ReadSeg {
  my ($f) = @_;

  my %samples;

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    s/[\n\r]+//;
    my ($sample, $chr, $start, $stop, $markers, $cn) = split /\t/;
#    $sample = substr($sample, 0, 20);
    if (defined $samplef && ! defined $keep_samples{$sample}) {
      print STDERR "discarding sample $sample\n";
      next;
    } else {
      $samples{$sample} = 1;
    }
    if ($chr eq "23") {
      $chr = "X";
    } elsif ($chr eq "24") {
      $chr = "Y";
    }
    if ($chr eq "X" || $chr eq "Y") {
      next;
    }
    if ($cn >= $floor && $cn <= $ceiling) {
      next;
    }
    my $seg = new Segment($chr, $start, $stop, $sample);
    push @{ $chr2seg{$chr} }, $seg;
  }
  close INF;
  $n_samples = scalar(keys %samples);
  $min_count = int(($n_samples * $min_freq) + 0.5);
}

######################################################################
sub DoChr {
  my ($chr) = @_;

  my (%bin2sample, @bin2tally);
  for my $seg (@{ $chr2seg{$chr} }) {
    my ($chr, $start, $stop, $sample) =
        ($seg->Chr, $seg->Left, $seg->Right, $seg->Id);
    my $start_bin = Pos2ChrBin($start);
    my $stop_bin  = Pos2ChrBin($stop);
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      $bin2sample{$bin}{$sample} = 1;
    }
  }
  for my $bin (keys %bin2sample) {
    $bin2tally[$bin] = scalar(keys %{ $bin2sample{$bin} });
  }
  my ($start, $stop);
    ## have to go one beyond @bin2tally in case the last bin is part of
    ## an interesting region
  for (my $i = 1; $i <= @bin2tally; $i++) {
    if (defined $bin2tally[$i] && $bin2tally[$i] >= $min_count) {
      if (! defined $start) {
        $start = $i;
      }
      $stop = $i;
    } else {
      if (defined $start) {
        print join("\t",
          $label,
          $chr,
          ChrBin2Pos($start),
          ChrBin2Pos($stop) + $binsize - 1,
          "1",                  ## faux number markers
          "2.00"                ## faux mean log2ratio
        ) . "\n";
        undef $start;
        undef $stop;
      }
    }
  }
}

######################################################################
sub Pos2ChrBin {
  my ($pos) = @_;

  return (int($pos/$binsize) + 1);
}

######################################################################
sub ChrBin2Pos {
  my ($bin) = @_;

  return (($bin - 1) * $binsize);
}

######################################################################
#	-2.00	0.50
#	-1.90	0.54
#	-1.80	0.57
#	-1.70	0.62
#	-1.60	0.66
#	-1.50	0.71
#	-1.40	0.76
#	-1.30	0.81
#	-1.20	0.87
#	-1.10	0.93
#	-1.00	1.00
#	-0.90	1.07
#	-0.80	1.15
#	-0.70	1.23
#	-0.60	1.32
#	-0.50	1.41
#	-0.40	1.52
#	-0.30	1.62
#	-0.20	1.74
#	-0.10	1.87
#	0.00	2.00
#	0.10	2.14
#	0.20	2.30
#	0.30	2.46
#	0.40	2.64
#	0.50	2.83
#	0.60	3.03
#	0.70	3.25
#	0.80	3.48
#	0.90	3.73
#	1.00	4.00
#	1.10	4.29
#	1.20	4.59
#	1.30	4.92
#	1.40	5.28
#	1.50	5.66
#	1.60	6.06
#	1.70	6.50
#	1.80	6.96
#	1.90	7.46
#	2.00	8.00

######################################################################
sub ReadOptions {
  use Getopt::Long;

  GetOptions (
      "samplef:s"    => \$samplef,
      "label=s"      => \$label,
      "segf=s"       => \$segf,
      "binsize=i"    => \$binsize,
      "floor=f"      => \$floor,
      "ceiling=f"    => \$ceiling,
      "minfreq=f"    => \$min_freq
  );
}
