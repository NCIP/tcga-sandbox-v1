if [ $HOSTNAME == 'ncias-d771-v.nci.nih.gov' ]; then
export ORACLE_HOME=/app/oracle/product/11.2.0/client_1
else
export ORACLE_HOME=/app/oracle/product/10gClient/
fi



DATE=`date | sed 's/ /_/g' | sed 's/...\:..*//'` 
BIN=/h1/pontiusj/Projects/archiveConsistency/bin
HERE=/h1/pontiusj/Projects/archiveConsistency/
LIB=/h1/pontiusj/lib/
OUTDIR=$HERE/output.$DATE

mkdir $OUTDIR
echo $DATE
echo $BIN
echo $OUTDIR


## general data integrity
echo "Runing lastest.dates.pl"
$BIN/latest.dates.pl   $OUTDIR/
$BIN/sdrf.vs.level.pl  $OUTDIR/sdrf
$BIN/didnotload.pl     $OUTDIR/
$BIN/zerosize.pl       $OUTDIR/zerosizedfiles.txt
$BIN/is.latest.pl      $OUTDIR/
$BIN/duplicate.md5.pl  $OUTDIR/md5
$BIN/md5.dates.pl      $OUTDIR/md5

## summary of platforms per working group
$BIN/perAWG.pl $OUTDIR/

## update for Encylopedia entry of Platforms
$LIB/GuessReferenceAssemblies.pl $OUTDIR/
$LIB/list_TCGA_Headers.pl        $OUTDIR/

## slides
$BIN/tiff_info.pl               $OUTDIR/
$BIN/summarize.slide.pl          $OUTDIR/
$BIN/slidebarcodeconsistency.pl  $OUTDIR/svs
$BIN/slidebarcode.portion.consistency.pl $OUTDIR/svs
$BIN/retrieveDuplicateSVS.pl     $OUTDIR/


## interpretive data checks
$BIN/segment.length.pl $OUTDIR/

## summary of all changes
$BIN/summarize.differences.pl $OUTDIR $HERE/output.current/ $HERE/update.summaries

rm -rf $HERE/output.current
ln -s $OUTDIR $HERE/output.current




