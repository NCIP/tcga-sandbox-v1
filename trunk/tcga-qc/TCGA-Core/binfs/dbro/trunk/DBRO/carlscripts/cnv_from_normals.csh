#!/bin/csh

set ROOT=/lpg/LPGCommon/schaefec/TCGA
set COMMON=/lpg/LPGCommon/schaefec/COMMON

set DISEASES = `cat $ROOT/DISEASES.txt`

set SOURCE=normals
set BIN_SIZE=1000
setenv MAX_SEGS 1000

rm -f all.ok.segs

foreach DISEASE ( $DISEASES )

  set DATA=$ROOT/SNP_6_data/$DISEASE/SNP/BI__Genome_Wide_SNP_6/Level_3/broad.mit.edu__Genome_Wide_SNP_6__snp_analysis.seg.txt

  $COMMON/FilterMapColumn.pl \
    -data $DATA \
    -col 1 \
    -filter  $ROOT/SNP_6_data/$DISEASE/normal_keepers.txt | \
  ./CBSSeg2Freq.pl \
    -killxy \
    -ampcap 1.5 \
    -delcap -1.5 \
    -seg - \
    -freq $DISEASE.freq

  ## using 1000 as max number of segs

  egrep '^nsegs' $DISEASE.freq | \
    cut -f2- | \
    perl -ne 'chop;($b,$n)=split /\t/;if($n<=$ENV{MAX_SEGS}){print "$b\n"}' | \
    sort -u > $DISEASE.nsegs.ok

  egrep '^mean_l2r' $DISEASE.freq | \
    cut -f2- | \
    perl -ne 'chop;($b,$n)=split /\t/;if($n>=-0.10&&$n<=0.10){print "$b\n"}' | \
    sort -u > $DISEASE.mean.ok

  join $DISEASE.nsegs.ok $DISEASE.mean.ok | \
  $COMMON/FilterMapColumn.pl \
    -data $DATA \
    -col 1 \
    -filter - \
  >> all.ok.segs

end

./CNVByBins.pl \
  -label    $SOURCE \
  -segf     all.ok.segs \
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
