README for bamTelemetry.pl

Useage flags:

[-f filename| -ws]  The -f and -ws flags are mutually exclusive.  If the -f flag is used, the program expects that the next entry is
the name of the input file.  Note that this MUST be a response from a CGHub analysisAttributes query in XML format.  The default log
and error files when using -f are BAM_telemetry_log.txt and BAM_telemetry_errors.txt and will be placed in the directory the program
was started from.  If the -ws flag is used, the program will look in the ~/.bam/bam.xml file for the input, log and error files.  These
files are appeneded if they already exist.

Example bam.xml
<?xml version="1.0" encoding="utf-8"?>
<ini>
	<input>/path/to/CGHub_analysisAttribute_output.xml</input>
	<error>/path/to/errors.txt</error>
	<log>/path/to/log.txt</log>
</ini>


[-t filename |-x filename] This flag determines the output format for creating a Data Portal telemetry input file.  If the -t flag is 
used, the file will be output in tab-delimited format and if -x is used the file will be a Microsoft Excel .xls file.  

[-dt filename | -dx filename]  This flag sets the output to be for AWG data freeze format (not suitable for input into the Data Portal
telemetry report).  The -dt will create a tab delimited file and -dx will create a Microsoft Excel .xls file.


The whole process could be run from a bash script as shown below:

#!/bin/sh
DATE=$(date +"%m-%d-%Y")
BASEDIR="/h1/pihltd/cghubtelemetry"
wget https://cghub.ucsc.edu/cghub/metadata/analysisAttributes?study=phs000178 -O $BASEDIR/CGHub_analysisAttribute_output.xml
perl /h1/pihltd/pihlprojects/bamTelemetry/branches/dev/bamTelemetry.pl -ws -t $BASEDIR/bam_telemetry_$DATE.txt

Timing Note:
This program relies on the DCC UUID web service to translate UUIDs to barcodes.  Since the move to Sterling (March 2012),
this web service is limited to 1000 queries in 3 mintues.  Exceeding that rate shuts down any reponse.  In the program, the
"getBarcode" subroutine has a timer built into the procedure and the rate can be adjusted by altering the value of the 
"$trigger" variable.  In my hands, a rate of 0.2 (0.2 seconds per query) results in an actual rate of about 3 queries per second.
This will pass.  A rate of 0.15 results in 6-7 queries per second and tends to trip the block.  A rate of 0.18 (the theoretical max)
seems to run about 5-6 queries per second and passes the KIRC test (KIRC currently returns 1646 results and is a good test).