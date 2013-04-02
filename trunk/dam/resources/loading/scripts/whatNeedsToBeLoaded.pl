use strict;
use DBI;
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

# maint account for production DCC db
my $commonUser = "commonmaint";
my $commonPass = "comm7983ash";


my $oracleDb = "tcgaprd2";
my $pwdFile;
my $dbh;

# get options
&GetOptions('F=s' => \$pwdFile);
die "Required: -F pwdFile \n" unless defined $pwdFile;

my $dbh_dcc;
$dbh_dcc = DBI->connect("DBI:Oracle:".$oracleDb, $commonUser, $commonPass, { RaiseError => 1, AutoCommit => 1 } ) || die "ERROR: Cannot connect to the dccCommon database" . $DBI::errstr;
my $diseaseAbbrev = shift;

my $pwd;
# get the password from the $pwdFile for $disease
open(PWDS, $pwdFile);
while(<PWDS>) {
  chomp;
  s/\r//;
  s/\n//;

  my @line = split(/\t/);
  my $disease = $line[0];
  my $oraclePassword = $line[1];
  my $oracleUsername = "$disease"."maint";
  # connect to disease schema
  $dbh = DBI->connect("DBI:Oracle:".$oracleDb, $oracleUsername, $oraclePassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . $DBI::errstr;


  # 1. get all latest level 2 and 3 archives for this disease
  my $latestArchives = $dbh_dcc->selectall_arrayref("select distinct archive_name, domain_name, platform_name, serial_index, revision, fi.level_number from archive_info ai, disease d, file_info fi, center c, platform p, file_to_archive fa where ai.disease_id=d.disease_id and d.disease_abbreviation=? and ai.is_latest=1 and ai.archive_id = fa.archive_id and fa.file_id = fi.file_id and (level_number=2 or level_number=3) and ai.center_id=c.center_id and ai.platform_id=p.platform_id", { Slice => {}}, $disease);
  my $size = @$latestArchives;
  print "\n $disease \n";
  print "latestArchives size: $size \n";
  # 2. check for experiment entries for these archives	
  print "Archives that aren't loaded:\n\n";					 
  foreach my $archive (@$latestArchives) {
    my $baseName = $archive->{DOMAIN_NAME} . '_' . $disease . '.' . $archive->{PLATFORM_NAME};
    my $archiveCompare = $archive->{ARCHIVE_NAME} . '%';
    my $query = "select count(*) from experiment e, data_set d where e.base_name=? and e.data_deposit_batch=? and e.data_revision=? and e.experiment_id=d.experiment_id and d.data_level=?";
    my $results = $dbh->selectall_arrayref($query, undef, $baseName, $archive->{SERIAL_INDEX}, $archive->{REVISION}, $archive->{LEVEL_NUMBER});
    if ($results->[0]->[0] == 0) {
      print "$archive->{ARCHIVE_NAME}\n";
    } 
  }

}
close PWDS;
$dbh->disconnect;
$dbh_dcc->disconnect;

