## This script will accept a disease abbreviation for a parameter and run the biotab checker for 
## all latest clinical archives for that disease.
use strict;
use DBI;

# variables (disease and center passed as parameters)
my $disease = shift @ARGV or die "Disease abbreviation needed as 1st parameter\n";
my $bcr = shift @ARGV or die "Bcr domain name needed as 2nd parameter\n";
my $env = shift @ARGV or die "Environment needed as 3rd parameter (prod or stage or qa or dev)\n";
$disease = lc $disease;
$bcr = lc $bcr;
$env = lc $env;
my $bioPath ;
my $archivePath ;
my $oracleUsername;
my $oraclePassword;
my $oracleDb;

if ($env eq "dev") {
  $bioPath = " /tcgafiles_dev/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/biotab/clin";
  $archivePath = " /tcgafiles_dev/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/CENTER/bio/clin";
  $oracleUsername = "dcccommondev";
  $oraclePassword = "dcc58920dev";
  $oracleDb = "tcgadev";
 }elsif ($env eq "qa") {
  $bioPath = " /tcgafiles_qa/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/biotab/clin";
  $archivePath = " /tcgafiles_qa/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/CENTER/bio/clin";
  $oracleUsername = "dcccommon";
  $oraclePassword = "dcc8294qa";
  $oracleDb = "tcgaqa"; 
 } elsif ($env eq "stage") {
  $bioPath = " /tcgafiles_stage/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/biotab/clin";
  $archivePath = " /tcgafiles_stage/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/CENTER/bio/clin";
  $oracleUsername = "commonmaint";
  $oraclePassword = "comm234hg";
  $oracleDb = "tcgastg"; 
 } elsif ($env eq "prod") {
  $bioPath = " /tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/biotab/clin";
  $archivePath = " /tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/DISEASE/bcr/CENTER/bio/clin";
  $oracleUsername = "commonmaint";
  $oraclePassword = "comm7983ash";
  $oracleDb = "tcgaprd2"; 
 } else {
   die "Bad env passed\n";
}
$bioPath =~ s/DISEASE/$disease/;
$archivePath =~ s/DISEASE/$disease/;
$archivePath =~ s/CENTER/$bcr/;

my $checkCommand;

print "bio path $bioPath \n";
print "archive path $archivePath \n";


my $dbh;
my $status="Available";
$disease = uc $disease;
my $archiveSelect = "select archive_name from archive_info a,disease d, center c, platform p where a.is_latest=1 and a.deploy_status='$status' and a.disease_id=d.disease_id and d.disease_abbreviation = '$disease' and a.center_id=c.center_id and c.domain_name='$bcr' and c.center_type_code='BCR' and a.platform_id=p.platform_id and p.platform_name = 'bio'";
$dbh = DBI->connect("DBI:Oracle:".$oracleDb, $oracleUsername, $oraclePassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . $DBI::errstr;
my $archives = $dbh->selectall_arrayref($archiveSelect, { Slice => {} });

for my $row (@$archives) {
   my $archive = $row->{ARCHIVE_NAME};
   my $alout = $archive."_aliquot_check.txt";
   my $anout = $archive."_analyte_check.txt";
   my $poout = $archive."_portion_check.txt";
   my $saout = $archive."_sample_check.txt";
   my $slout = $archive."_slide_check.txt";
   my $paout = $archive."_patient_check.txt";
   my $drout = $archive."_drug_check.txt";
   my $exout = $archive."_examination_check.txt";
   my $prout = $archive."_protocol_check.txt";
   my $raout = $archive."_radiation_check.txt";
   my $suout = $archive."_surgery_check.txt";
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_aliquot_all_$disease.txt $archivePath/$archive/*biospecimen*.xml > ok.out 2> ./checkerResults/$alout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_analyte_all_$disease.txt $archivePath/$archive/*biospecimen*.xml > ok.out 2> ./checkerResults/$anout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_portion_all_$disease.txt $archivePath/$archive/*biospecimen*.xml > ok.out 2> ./checkerResults/$poout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_sample_all_$disease.txt $archivePath/$archive/*biospecimen*.xml > ok.out 2> ./checkerResults/$saout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_slide_all_$disease.txt $archivePath/$archive/*biospecimen*.xml > ok.out 2> ./checkerResults/$slout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_patient_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$paout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_drug_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$drout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_examination_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$exout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_protocol_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$prout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_radiation_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$raout";
   system($checkCommand);
   $checkCommand = "checkXmlBtab.pl --noverbose --strict --diff $bioPath/clinical_surgery_all_$disease.txt $archivePath/$archive/*clinical*.xml > ok.out 2> ./checkerResults/$suout";
   system($checkCommand);
}

#finish
$dbh->commit();
$dbh->disconnect();
print "Done.\n\n";



