use strict;

my $file = shift @ARGV;
my $appendTo = shift @ARGV;

open(IN, $file) or die "Dead.\n";
open(OUT, ">>$appendTo") or die "Can't.\n";

<IN>; # header
while(<IN>) {
  s/\n//;
  s/\r//;
  my @line = split(/\t/);
  my $center = $line[0];
  if ($center eq 'wugsc') {
    $center = 'WashU';
  } elsif ($center eq 'bi') {
    $center = 'Broad';
  } elsif ($center eq 'bcm') {
    $center = 'Baylor';
  }

  my $date = $line[4];
  my $bamFile = $line[2];
  
  if ($bamFile =~ /(((TCGA-\w\w-\w\w\w\w)-\w\w)[A-Z0-9\-]+)[_\.].+/) {
    my $aliquot = $1;
    my $patient = $2;
    my $sample = $3;

    print OUT "$date\tBAM (unknown date)\t$aliquot\t$sample\t$patient\t$center\n";
  } else {
    die "Didn't match: $bamFile\n";
  }
}
close IN;
