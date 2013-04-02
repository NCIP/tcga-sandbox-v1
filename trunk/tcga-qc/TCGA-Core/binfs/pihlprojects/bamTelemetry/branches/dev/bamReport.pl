#!/usr/bin/perl
# Creates a the BAM Telemetry report from the database created by bamTelemetryDB.pl

use strict;
use warnings;

use DBI;
use Getopt::Long;

###############################
#                             #
#    Config Variables         #
#                             #
###############################

###############################
#                             #
#    Program Variables        #
#                             #
###############################
my $dbfile;
my $reportfile;
my $help;
my $barcode;
my $filename;
my $disease;
my $center;
my $filesize;
my $date;
my $type;

###############################
#                             #
#    Main Program             #
#                             #
###############################
printUsage() if (@ARGV <4 or ! GetOptions('help|?'=>\$help, 'd|database=s'=>\$dbfile,'r|report=s'=>\$reportfile) or defined $help);

open (REPORT,">",$reportfile) or die "Can't open $reportfile: $!\n";
print(REPORT "barcode\tfilename\tdisease_abbreviation\tcenter_name\tfilesize\tupload_date\tbam_file_type\n");

my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
my $sql = qq(SELECT aliquot_barcode,filename,disease_id,center_id,filesize,upload_date,bam_file_type FROM telemetry WHERE state='live');

my $sth = $dbh->prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute();
$sth -> bind_columns(\$barcode, \$filename, \$disease, \$center, \$filesize, \$date, \$type);
	while($sth -> fetch()){
		$date = alterDate($date);
		print(REPORT $barcode."\t".$filename."\t".$disease."\t".$center."\t".$filesize."\t".$date."\t".$type."\n");
	}
close(REPORT);
$dbh->disconnect;


###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################
sub alterDate{
	#This simply reformats the date format from 2012-02-22T08:52:40Z to dd-MMM-yy
	my %month_hash = ('01'=>'JAN',
					'02'=>'FEB',
					'03'=>'MAR',
					'04'=>'APR',
					'05'=>'MAY',
					'06'=>'JUN',
					'07'=>'JUL',
					'08'=>'AUG',
					'09'=>'SEP',
					'10'=>'OCT',
					'11'=>'NOV',
					'12'=>'DEC'
					);
	my $date = shift;
	my $startloc = index($date,'T');
	$date = substr($date,0,$startloc);
	my @temp = split(/-/,$date);
	$date = $temp[2]."-".$month_hash{$temp[1]}."-".substr($temp[0],2,2);
	return $date;

}

sub printUsage{
	print "Unknown option: @_\n" if ( @_ );
  	print "usage: program [-d|--database database file] [-r|--report report file] [--help|-?]\n";
  	exit;

}