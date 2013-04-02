#!/bin/csh

set ROOT=/lpg/LPGCommon/schaefec/TCGA

set CENTER=BROAD
set center=broad
set CWD=`pwd`

set DISEASES = `cat $ROOT/DISEASES.txt`

set FINDKEEP="$ROOT/Annotations/FindKeepers.pl"
set ANNOTS="$ROOT/Annotations/annotations.txt"
set PICK_ALIQUOT="$ROOT/Annotations/PickOneAliquot.pl"

set BIN_FILE="$ROOT/BIN_MAPPING/bins200K.dat"

set GENOME_FILE="$ROOT/BIN_MAPPING/genome2gene.dat"
set MIRNA_FILE="$ROOT/miRNA_meta_data/genome2mirna.dat"

set COMBINED_CNV="$ROOT/CNV/combined.cnv"
set GENE_CNV_OVERLAP="$ROOT/CNV/combined.gene_overlap"
set MIRNA_CNV_OVERLAP="$ROOT/CNV/combined.mirna_overlap"

set MIN_SEG=200
set MIN_OVERLAP_BP=1

foreach DISEASE ( $DISEASES )

  set HERE=$CWD/$DISEASE
  cd $HERE
  set DATA=$HERE/SNP/BI__Genome_Wide_SNP_6/Level_3/broad.mit.edu__Genome_Wide_SNP_6__snp_analysis.seg.txt
  set disease=`echo $DISEASE | tr '[A-Z]' '[a-z]'`

  # keep only Broad unamplified DNA aliquots

  cut -f1 $DATA |sort -u | \
    $FINDKEEP $ANNOTS - | \
    egrep 'TCGA\-\w\w\-\w\w\w\w\-\w\w\w\-\w\wD\-\w\w\w\w\-01' | \
    perl -ne 'if(/^TCGA-\w\w-\w\w\w\w-0[0-9][A-Z]/){print}' | \
    $PICK_ALIQUOT | \
    sort -u > tumor_keepers.txt

  ## do NOT take -11- (normal tissue) samples
  ## too many look unreliable

  cut -f1 $DATA |sort -u | \
    $FINDKEEP $ANNOTS - | \
    egrep 'TCGA\-\w\w\-\w\w\w\w\-\w\w\w\-\w\wD\-\w\w\w\w\-01' | \
    perl -ne 'if(/^TCGA-\w\w-\w\w\w\w-10[A-Z]/){print}' | \
    $PICK_ALIQUOT | \
    sort -u > normal_keepers.txt

  $ROOT/SNP_6_data/CBSSeg2Gene.pl \
    -samples $HERE/tumor_keepers.txt \
    -seg $DATA \
    -genes $GENOME_FILE \
#    -cnv $COMBINED_CNV \
    -minseg $MIN_SEG \
    -overlap $MIN_OVERLAP_BP \
    -small_bins $BIN_FILE \
    -o $HERE/"$center"_gene_tumor.out >& "$center"_gene_tumor.log

  $ROOT/SNP_6_data/CBSSeg2Gene.pl \
    -samples $HERE/normal_keepers.txt \
    -seg $DATA \
    -genes $GENOME_FILE \
#    -cnv $COMBINED_CNV \
    -minseg $MIN_SEG \
    -overlap $MIN_OVERLAP_BP \
    -small_bins $BIN_FILE \
    -o $HERE/"$center"_gene_normal.out >& "$center"_gene_normal.log

  $ROOT/SNP_6_data/ComputePairedGeneValues.pl \
      "$center"_gene_tumor.out  "$center"_gene_normal.out \
      $GENE_CNV_OVERLAP  > "$center"_gene_tumor.dat

  $ROOT/SNP_6_data/CBSSeg2Gene.pl \
    -samples $HERE/tumor_keepers.txt \
    -seg $DATA \
    -genes $MIRNA_FILE \
#    -cnv $COMBINED_CNV \
    -minseg $MIN_SEG \
    -overlap $MIN_OVERLAP_BP \
    -small_bins $BIN_FILE \
    -o $HERE/"$center"_mirna_tumor.out >& "$center"_mirna_tumor.log

  $ROOT/SNP_6_data/CBSSeg2Gene.pl \
    -samples $HERE/normal_keepers.txt \
    -seg $DATA \
    -genes $MIRNA_FILE \
#    -cnv $COMBINED_CNV \
    -minseg $MIN_SEG \
    -overlap $MIN_OVERLAP_BP \
    -small_bins $BIN_FILE \
    -o $HERE/"$center"_mirna_normal.out >& "$center"_mirna_normal.log

  $ROOT/SNP_6_data/ComputePairedGeneValues.pl \
      "$center"_mirna_tumor.out "$center"_mirna_normal.out \
      $MIRNA_CNV_OVERLAP > "$center"_mirna_tumor.dat

  cd $CWD

end
