#!/bin/csh

## Database of Genomic Variants
## http://projects.tcag.ca/variation

set SOURCE="dgv"
set BIN_SIZE=1000

goto start

wget -O dgv.txt "http://projects.tcag.ca/variation/downloads/variation.hg18.v9.txt"

start:

./DGV.pl dgv.txt | cut -f2,3,4 | \
  perl -ne 'chop;print join("\t", "dgv",$_,"1","2.0")."\n"' | \
  ./CNVByBins.pl \
    -label    $SOURCE \
    -segf     - \
    -binsize  $BIN_SIZE \
    -floor    -0.20 \
    -ceiling  0.20 \
    -minfreq  0.05 \
    > $SOURCE.cnv

./OverlapExonsAndCNVs.pl \
  ../BIN_MAPPING/bins20K.dat \
  ../BIN_MAPPING/exons.dat \
  $SOURCE.cnv | \
  sort -u > $SOURCE.exon_overlap

./OverlapGenesAndCNVs.pl \
  ../BIN_MAPPING/bins20K.dat \
  ../BIN_MAPPING/genome2gene.dat \
  $SOURCE.cnv | \
  sort -u > $SOURCE.gene_overlap

./OverlapGenesAndCNVs.pl \
  ../BIN_MAPPING/bins20K.dat \
  ../miRNA_meta_data/genome2mirna.dat \
  $SOURCE.cnv | \
  sort -u > $SOURCE.mirna_overlap
