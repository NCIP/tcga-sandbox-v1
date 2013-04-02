use strict;
use DBI;

my %schemas; 
my $dbServer;

my $passwordsFile = shift @ARGV or die "Need passwords file!\n";
open(IN, $passwordsFile) or die "Could not open $passwordsFile\n";
while(<IN>) {
  chomp;
  if (/^server=(\w+)/) {
    $dbServer = $1;
  } elsif (/^(\w+)=(\w+)\/(.+)$/) {
    my $schema = $1;
    my $username = $2;
    my $password = $3;
    $schemas{$schema}->{username} = $username;
    $schemas{$schema}->{password} = $password;
  } else {
    die "Password file line should be like [disease]=[username]/[password] or common=[username]/[password] or server=[server] ($_)\n";
  }
}
close IN;

my $dbh_common = DBI->connect("DBI:Oracle:".$dbServer, $schemas{common}->{username}, $schemas{common}->{password}, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to common db" . DBI->errstr;

# 1. get all disease names that have latest archives
my $diseaseRows = $dbh_common->selectall_arrayref('select distinct disease_abbreviation from disease d, archive_info ai where ai.disease_id=d.disease_id and ai.is_latest=1');
foreach my $diseaseRow (@$diseaseRows) {
  my $disease = $diseaseRow->[0];
  print "Starting $disease...";
  my $dbh_disease = DBI->connect("DBI:Oracle:".$dbServer, $schemas{$disease}->{username}, $schemas{$disease}->{password}, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to $disease db" . DBI->errstr;

  my $fileInfoRows = $dbh_common->selectall_arrayref(qq~select distinct fi.file_id, file_location_url
  from file_info fi, file_to_archive fa, archive_info ai, disease d
  where d.disease_id=ai.disease_id and ai.archive_id=fa.archive_id and fa.file_id=fi.file_id and ai.is_latest=1 and d.disease_abbreviation='$disease'~);
  my $count = 0;
  foreach my $fileInfoRow (@$fileInfoRows) {
    my $fileId = $fileInfoRow->[0];
    my $fileUrl = $fileInfoRow->[1];

    $fileUrl =~ s/\/tcgafiles\//\/tcga_prod\//;
    my $md5result = `md5sum $fileUrl`;
    my @md5sum = split(/\s+/, $md5result);
    my $md5sum = $md5sum[0];

    my $updateSql = "update file_info set md5='$md5sum' where file_id=$fileId";
    $dbh_common->do($updateSql);
    $dbh_disease->do($updateSql);
    $count++;
  }
  $dbh_common->commit;
  $dbh_disease->commit;
  $dbh_disease->disconnect;
  print "done! Updated $count file_info rows\n";
}
$dbh_common->commit;
$dbh_common->disconnect;
