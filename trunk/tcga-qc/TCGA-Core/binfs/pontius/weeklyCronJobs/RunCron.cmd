if [ $HOSTNAME == 'ncias-d771-v.nci.nih.gov' ]; then
export ORACLE_HOME=/app/oracle/product/11.2.0/client_1
else
export ORACLE_HOME=/app/oracle/product/10gClient/
fi

DATE=`date | sed 's/ /_/g' | sed 's/...\:..*//'` 

export HOME=/h1/pontiusj/
export BIN=/h1/pontiusj/Projects/archiveConsistency/bin.cronjobs
export HERE=/h1/pontiusj/Projects/archiveConsistency/
export LIB=/h1/pontiusj/lib/
export OUTDIR=$HERE/output.$DATE
export FILE_ARCHIVE2TICKET=/h1/pontiusj/Projects/archiveConsistency/cumulative_results/archive2ticket.txt

mkdir $OUTDIR
echo $DATE
echo $BIN
echo $OUTDIR

## general data integrity
echo "DATA INTEGRITY------------------------------"
## compare file_info table in db with server
$BIN/AllFiles.pl > $OUTDIR/AllFiles.pl

## compare date stamps of tar files, expanded archives and md5sum files
$BIN/latest.dates.pl   $OUTDIR/latest.date.inconsistencies.txt

## compare sdrf contents and archive contents
$BIN/sdrf.vs.level.pl  $OUTDIR/sdrf

## list of archives that seem stuck at DCC, ie largest revision number but is not current archive
$BIN/invalid_archives.pl     $OUTDIR/invalid_archives.txt

## get files of size zero in all current archives
$BIN/zerosize.pl       $OUTDIR/zerosize.txt

## get md5sum file with carriage return in filename
$BIN/duplicate.md5.pl  $OUTDIR/md5.duplicatefiles.txt
$BIN/archive.dates.pl      $OUTDIR/

## summary of platforms per working group
$BIN/perAWG.pl $OUTDIR/


echo "SUMMARY UPDATES------------------------------"
## update for Encylopedia entry of Platforms
$LIB/GuessReferenceAssemblies.pl $OUTDIR/

$LIB/list_TCGA_Headers.pl        $OUTDIR/

## slides
$BIN/tiff_info.pl               $OUTDIR/
$BIN/summarize.slide.pl          $OUTDIR/
$BIN/slidebarcode.portion.consistency.pl $OUTDIR/svs
$BIN/retrieveDuplicateSVS.pl     $OUTDIR/

echo "INTERPRETIVE CHECKS------------------------------"
## interpretive data checks
## note, this takes forever and all archives seem to have segments of length zero
##$BIN/segment.length.pl $OUTDIR/segment.length.txt


echo "PENDING TICKETS------------------------------"
## summary of all changes
$BIN/list.pending.tickets.pl $OUTDIR $OUTDIR

rm -rf $HERE/output.current
ln -s $OUTDIR $HERE/output.current




