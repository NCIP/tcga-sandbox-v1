#!/bin/csh

#echo -n "Userid "
#set USER = $<
#echo -n "Password "
#set PASS = $<

set ROOT=/lpg/LPGCommon/schaefec/TCGA

set PLATFORM=Genome_Wide_SNP_6
set CENTER=BI
set LEVEL=3
set FLATTENDIR=false

set DISEASES = `cat $ROOT/DISEASES.txt`

foreach DISEASE ( $DISEASES )

  $ROOT/DAM_WebService/DAMWS_Parser.pl \
    -disease $DISEASE \
    -platform $PLATFORM \
    -center $CENTER \
    -level $LEVEL \
    -flattendir $FLATTENDIR \
  >& $DISEASE.$PLATFORM.log &

end
