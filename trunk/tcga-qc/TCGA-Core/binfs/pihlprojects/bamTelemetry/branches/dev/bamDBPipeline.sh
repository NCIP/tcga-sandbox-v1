#!/bin/sh
export PERL5LIB=$PERL5LIB:/h1/jensenma/lib/perl5:/h1/jensenma/lib64/perl5:/h1/jensenma/lib/perl5/site_perl:/h1/jensenma/lib64/perl5/site_perl
export ORACLE_HOME=/app/oracle/product/11.2.0/client_1
export LD_LIBRARY_PATH=/app/oracle/product/11.2.0/client_1/lib
DATE=$(date +"%m-%d-%Y")
REPORT_DIR="/h1/pihltd/cghubtelemetry"
DB_DIR="/h1/pihltd/.bam"
OPT1="inc"
OPT2="full"
DBFILE="telemetry.db"
ERROR="bam_telemetry_ERRORS.txt"
LOG="bam_telemetry_LOG.txt"

if [ "$1" == "$OPT1" ]
then
XMLFILE="CGHubIncrement.xml"
#wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisDetail?last_modified=[NOW-2DAYS+TO+NOW]&state=live&study=phs000178" -O $REPORT_DIR/$XMLFILE
wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisDetail?last_modified=[NOW-2DAYS+TO+NOW]&study=phs000178" -O $REPORT_DIR/$XMLFILE
elif [ "$1" == "$OPT2" ]
then
rm $DB_DIR/$DBFILE
XMLFILE="CGHubFull.xml"
#wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisDetail?study=phs000178&state=live" -O $REPORT_DIR/$XMLFILE
wget --no-check-certificate "https://cghub.ucsc.edu/cghub/metadata/analysisDetail?study=phs000178" -O $REPORT_DIR/$XMLFILE
else 
echo "$1 is not a valid option"
exit
fi

#now run the database bit
perl /h1/pihltd/pihlprojects/bamTelemetry/branches/dev/bamTelemetryDB.pl --xml $REPORT_DIR/$XMLFILE --database $DB_DIR/$DBFILE --error $REPORT_DIR/$ERROR --log $REPORT_DIR/$LOG

echo "Development Database updated complete on $XMLFILE" | mutt -s "bamTelemtry DEV complete" -a $REPORT_DIR/$LOG todd.pihl@nih.gov
