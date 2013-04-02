#!/bin/sh
export PERL5LIB=$PERL5LIB:/h1/jensenma/lib/perl5:/h1/jensenma/lib64/perl5:/h1/jensenma/lib/perl5/site_perl:/h1/jensenma/lib64/perl5/site_perl
export ORACLE_HOME=/app/oracle/product/11.2.0/client_1
export LD_LIBRARY_PATH=/app/oracle/product/11.2.0/client_1/lib
DATE=$(date +"%m-%d-%Y")
REPORT_DIR="/h1/pihltd/cghubtelemetry"
DB_DIR="/h1/pihltd/.bam"
DBFILE="telemetry.db"
ERROR="bam_telemetry_ERRORS.txt"
LOG="bam_telemetry_LOG.txt"

#Create the report
perl /h1/pihltd/pihlprojects/bamTelemetry/branches/dev/bamReport.pl --database $DB_DIR/$DBFILE --report $REPORT_DIR/bam_DEV_telemetry_$DATE.txt

echo "Development report complete" | mutt -s "bamDBReport DEV complete"  todd.pihl@nih.gov
