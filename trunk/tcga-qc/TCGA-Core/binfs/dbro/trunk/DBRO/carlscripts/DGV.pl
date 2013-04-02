#!/usr/local/bin/perl

## $Source: /cgap/schaefec/cvsroot/TCGA/DGV.pl,v $
## $Revision: 1.1 $
## $Date: 2009/02/26 14:51:21 $

# maj - update for the DGV beta format (changes in flat file headers and enumerated values)
# 5/10/12

use strict;

use constant MIN_SIZE => 30;
use constant MIN_FREQ => 0.05;

##     1  VariationID
##     2  Landmark
##     3  Chr
##     4  Start
##     5  End
##     6  VariationType
##     7  LocusChr
##     8  LocusStart
##     9  LocusEnd
##    10  Reference
##    11  PubMedID
##    12  Method/platform
##    13  Gain
##    14  Loss
##    15  TotalGainLossInv
##    16  Frequency
##    17  SampleSize

my $head = <>;
chomp $head;
my @hdrs = split /\t/,$head;

while (<>) {
    chomp;
    my %data;
    @data{@hdrs} = split /\t/;
  my (
      $VariationID,
      $Landmark,
      $Chr,
      $Start,
      $End,
      $VariationType,
      $LocusChr,
      $LocusStart,
      $LocusEnd,
      $Reference,
      $PubMedID,
      $Method,
      $Gain,
      $Loss,
      $TotalGainLossInv,
      $Frequency,
      $SampleSize
      );

  if ($data{varianttype} ne "CNV") {
    next;
  }
  if ($data{samplesize} < MIN_SIZE) {
    print STDERR "#discarding small sample size: $SampleSize\n";
    next;
  }

# not doing the min frequency check ; latest dgv has insufficient info - maj
    
    print join("\t",@data{qw( variantid chr start end)}, $data{end}-$data{start}+1, 'NA',$data{samplesize}, $data{frequency}), "\n";

}
