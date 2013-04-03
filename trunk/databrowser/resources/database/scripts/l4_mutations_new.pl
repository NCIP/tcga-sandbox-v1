use strict;
use DBI;

use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $dbServer;
my $dbUsername;
my $dbPassword;
my $diseaseId;
my $centerId; # in the portal db

&GetOptions('D=s' => \$dbServer, 'U=s' => \$dbUsername, 'P=s' => \$dbPassword, 'c=s' => \$centerId, 's=s' => \$diseaseId );

die "Usage: -D oracledb -U user -P password -c portal_center_id -s disease_id allSampleFile filename1 filename2 ...\n" unless defined $dbServer and defined $dbUsername and defined $dbPassword and defined $diseaseId and defined $centerId;

my $allSamplesFile = shift @ARGV;

die "ERROR: input file '$allSamplesFile' was not found\n" unless -e $allSamplesFile;

# connect to the database
print "Connecting to the database\n";
my $dbh = DBI->connect("DBI:Oracle:".$dbServer, $dbUsername, $dbPassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . DBI->errstr;


######################
my $insertAnomaly = $dbh->prepare("INSERT INTO L4_anomaly_value(anomaly_value, anomaly_data_set_id, genetic_element_id, sample_id) VALUES(?, ?, ?, ?)");
my $selectDatasetSample = $dbh->prepare("select count(*) from L4_data_set_sample where anomaly_data_set_id=? and sample_id=?");
my $insertDatasetSample = $dbh->prepare("INSERT INTO L4_data_set_sample(anomaly_data_set_id, sample_id, is_paired) VALUES(?, ?, ?)");
my $selectDatasetGE = $dbh->prepare("SELECT count(*) FROM L4_data_set_genetic_element WHERE anomaly_data_set_id=? and genetic_element_id=?");
my $insertDatasetGE = $dbh->prepare("INSERT INTO L4_data_set_genetic_element(anomaly_data_set_id, genetic_element_id) VALUES(?, ?)");

my $selectSample = $dbh->prepare("SELECT sample_id FROM L4_sample WHERE barcode=?");
my $insertSample = $dbh->prepare("INSERT INTO L4_sample(barcode, patient, disease_id) VALUES(?, ?, ?)");

my $insertGE = $dbh->prepare("INSERT INTO L4_genetic_element(genetic_element_name, genetic_element_type_id, start_pos, stop_pos, chromosome, in_cnv_region) VALUES(?, 1, ?, ?, ?, ?)");

my $fetchGEs = $dbh->prepare("SELECT genetic_element_id from L4_genetic_element WHERE genetic_element_name=?");

my $fetchMutations = $dbh->prepare("SELECT count(*) from L4_anomaly_value where genetic_element_id=? and sample_id=? and anomaly_data_set_id=?");
#########################

print "Making map of Anomaly Types\n";
my $results = $dbh->selectall_arrayref("select anomaly_type_id, anomaly_name from L4_anomaly_type where center_id=$centerId");

my %anomalyIdMap; # key is name, val is id
foreach my $row (@$results) {
  $anomalyIdMap{$row->[1]} = $row->[0];
  print "\tmapped $row->[1] to $row->[0] \n";
}

die "No anomaly types found?\n" unless scalar keys(%anomalyIdMap) > 0;

my %anomalyDatasets; # key is mutation type (anomaly name)

my %mutations; # key is "gene:patient" value is 1 if mutation exists for this combo
for my $mutationFile (@ARGV) {
  print "$mutationFile...\n";
  &loadMutationFile($mutationFile);
}

# now, load the ones that were not mutated
# for each gene/patient combo, look for value... add one if not there
print "Loading non-mutation gene/sample data\n";
open (IN, $allSamplesFile);
my $skipCount = 0;
my $loadCount = 0;
while (<IN>) {
  chomp;
  my ($gene, $patient) = split(/\t/);
  next unless defined $gene and defined $patient;
  if ($mutations{"$gene:$patient"} == 1) {
    $skipCount++;
    next; # skip those we just inserted with mutations
  }

  my $geneIds = &getGeneIds($gene);
  my $sampleId = &getSampleId($patient);
  die "Failed to make sample ID for $patient\n" unless defined $sampleId;
  die "No gene found for $gene\n" unless defined $geneIds and scalar @$geneIds > 0;

  # for each anomaly data set... for each gene...
  foreach my $datasetId (values %anomalyDatasets) {
    foreach my $geneId (@$geneIds) {
      &insertMutation($geneId, $sampleId, $datasetId, 0);
      $loadCount++;
      if ($loadCount % 1000 == 0) {
	print "$loadCount\n";
      }
    }
  }
}
close IN;
print "Loaded $loadCount total non-mutations, skipped $skipCount\n";

$insertAnomaly->finish;
$selectDatasetSample->finish;
$insertDatasetSample->finish;
$selectDatasetGE->finish;
$insertDatasetGE->finish;
$selectSample->finish;
$insertSample->finish;
$fetchGEs->finish;
$fetchMutations->finish;

$dbh->commit;

$dbh->disconnect;

#########################################################

sub loadMutationFile {
  my $file = shift;
  my $mutCount = 0;
  open(IN, $file) or die "Could not open $file\n";
  while (<IN>) {
    chomp;
    my ($gene, $chrom, $start, $stop, $mutationType, $barcode) = split("\t");
    $gene = uc($gene);

    # truncate barcode to patient level
    $barcode =~ /(TCGA-\d\d-\d\d\d\d).*/ or die "Unexpected barcode format: $barcode\n";
    my $patient = $1;

    my $sampleId = &getSampleId($patient);
    die "Failed to make sample ID for $patient\n" unless defined $sampleId;

    my $geneIds = &getGeneIds($gene);
    die "No gene found for $gene\n" unless defined $geneIds and scalar @$geneIds > 0;

    my $anomalyTypeId = $anomalyIdMap{$mutationType};
    if (! defined $anomalyTypeId) {
      print "Unexpected mutation type: $mutationType for $gene / $barcode\n - skipping\n";
      next;
    } 

    my $datasetId = $anomalyDatasets{$mutationType};  
    if (! defined $datasetId) {
      $datasetId = &makeDataSet($anomalyTypeId);
      die "Failed to make new dataset for $anomalyTypeId\n" unless defined $datasetId;
      $anomalyDatasets{$mutationType} = $datasetId;
    }
    for my $geneId (@$geneIds) {
      &insertMutation($geneId, $sampleId, $datasetId, 1);
    }

    $mutations{"$gene:$patient"} = 1;
    $mutCount++;
  }
  close IN;
  print "Loaded $mutCount mutations\n";
}


sub insertMutation {
  my $geneId = shift;
  my $sampleId = shift;
  my $datasetId = shift;
  my $isMutation = shift;

  if (! &mutationExists($geneId, $sampleId, $datasetId)) {
    $insertAnomaly->execute($isMutation, $datasetId, $geneId, $sampleId);
  }

  # check for dataset links to ge and sample
  if (&getId($selectDatasetSample, $datasetId, $sampleId) == 0) {
    $insertDatasetSample->execute($datasetId, $sampleId, 0);
  }

  if (&getId($selectDatasetGE, $datasetId, $geneId) == 0) {
    $insertDatasetGE->execute($datasetId, $geneId);
  }
}

sub mutationExists {
  my $geneId = shift;
  my $sampleId = shift;
  my $datasetId = shift;

  my $count = &getId($fetchMutations, $geneId, $sampleId, $datasetId);
  return $count > 0;
}


sub getId() {
  my $sth = shift;
  $sth->execute(@_);
  my $data = $sth->fetchall_arrayref;
  die "More than one row found in ID query (".join(" ", @_)."!\n" if scalar @$data > 1;
  return $data->[0]->[0];
}


sub getSampleId {
  my $patient = shift;

  # see if it is in the db already
  my $sampleId = &getId($selectSample, $patient);
  if (! defined $sampleId) {
    $insertSample->execute($patient, $patient, $diseaseId);
    $sampleId = &getId($selectSample, $patient);
  }
  return $sampleId;
}

sub getGeneIds {
  my $gene = shift;
  $fetchGEs->execute($gene);
  my $results = $fetchGEs->fetchall_arrayref;
  my @geneIds;
  foreach my $row (@$results) {
    push(@geneIds, $row->[0]);
  }

  if (scalar @geneIds == 0) {
    print "INSERTED GENE FOR $gene\n";
    $insertGE->execute($gene, -1, -1, '?', 0);
    $fetchGEs->execute($gene);
    $results = $fetchGEs->fetchrow_arrayref;
    push(@geneIds, $results->[0]);
  }

  return \@geneIds;
}

sub makeDataSet {
  my $anomalyTypeId = shift;

  $dbh->do("insert into L4_anomaly_data_set(anomaly_type_id) values($anomalyTypeId)");
  my $results = $dbh->selectall_arrayref("select max(anomaly_data_set_id) from L4_anomaly_data_set where anomaly_type_id=$anomalyTypeId");
  print "Data set for anomaly type $anomalyTypeId = ($results->[0]->[0])\n";
  return $results->[0]->[0];
}
