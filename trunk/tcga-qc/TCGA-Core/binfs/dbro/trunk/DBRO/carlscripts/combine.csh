#!/bin/csh

set ROOT=/lpg/LPGCommon/schaefec/TCGA

cat normals.cnv dgv.cnv > combined.cnv

./OverlapExonsAndCNVs.pl \
  $ROOT/BIN_MAPPING/bins20K.dat \
  $ROOT/BIN_MAPPING/exons.dat \
  combined.cnv | \
  sort -u > combined.exon_overlap

./OverlapGenesAndCNVs.pl \
  $ROOT/BIN_MAPPING/bins20K.dat \
  $ROOT/BIN_MAPPING/genome2gene.dat \
  combined.cnv | \
  sort -u > combined.gene_overlap

./OverlapGenesAndCNVs.pl \
  $ROOT/BIN_MAPPING/bins20K.dat \
  $ROOT/miRNA_meta_data/genome2mirna.dat \
  combined.cnv | \
  sort -u > combined.mirna_overlap

