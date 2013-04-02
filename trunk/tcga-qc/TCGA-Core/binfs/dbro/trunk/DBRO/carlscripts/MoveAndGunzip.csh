#!/bin/csh

set ROOT=/lpg/LPGCommon/schaefec/TCGA
set DISEASES = `cat $ROOT/DISEASES.txt`
set PLATFORM=Genome_Wide_SNP_6

foreach DISEASE ( $DISEASES )
  if (! -e $DISEASE) then
    mkdir $DISEASE
  endif
  mv $DISEASE.$PLATFORM.tar.gz $DISEASE
  cd $DISEASE
  gunzip -f $DISEASE.$PLATFORM.tar.gz
  tar -xf $DISEASE.$PLATFORM.tar
  cd ..
end
