use strict;
use DBI;
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $dbServer;
my $dbUsername;
my $dbPassword;
my $anomalyTypeId;
my $diseaseId;
my $geneticElementTypeId;

&GetOptions('D=s' => \$dbServer, 'U=s' => \$dbUsername, 'P=s' => \$dbPassword, 'T=s' => \$anomalyTypeId, 's=s' => \$diseaseId, 'e=s' => \$geneticElementTypeId );
my $inputFile = shift @ARGV;
die "usage: perl l4_load.pl -D oracleDb -U username -P password -T anomaly_type_id -s disease_id -e geneticElementTypeId inputFile\n" unless defined $dbServer && defined $dbUsername && defined $dbPassword && defined $inputFile && defined $anomalyTypeId && defined $diseaseId && defined $geneticElementTypeId;

die "ERROR: input file '$inputFile' was not found\n" unless -e $inputFile;

print "About to connect to the database...\n";

# connect to the database
my $dbh = DBI->connect("DBI:Oracle:".$dbServer, $dbUsername, $dbPassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . DBI->errstr;

print "Got database connection, about to open input file $inputFile...\n";

open (IN, $inputFile) or die "ERROR: Could not open '$inputFile'\n";

print "Opened input file, about to insert new anomaly data set...\n";

# insert a new anomaly data set
my $insert = "INSERT INTO L4_anomaly_data_set(anomaly_data_set_id, anomaly_type_id) VALUES(l4_anomaly_data_set_id_seq.nextval, $anomalyTypeId)";
$dbh->do($insert);
print "$insert\n";

print "Did insert, trying to get new ID...\n";
my $results = $dbh->selectall_arrayref("SELECT MAX(anomaly_data_set_id) from L4_anomaly_data_set WHERE anomaly_type_id=$anomalyTypeId");
my $dataSetId = $results->[0]->[0];
die "Could not get new data set id ($dataSetId) ???\n" unless defined $dataSetId;

print "Ok.  Preparing to insert data for data set $dataSetId...\n";

my %barcodes; # key is barcode, val is id
my %genes; # key is gene name, val is ARRAY REF of ids

my $getGEsForName = $dbh->prepare("SELECT genetic_element_id from L4_genetic_element WHERE genetic_element_name=?");
my $insertGE = $dbh->prepare("INSERT INTO L4_genetic_element(genetic_element_id, genetic_element_name, genetic_element_type_id, start_pos, stop_pos, chromosome, in_cnv_region) VALUES(l4_genetic_element_id_seq.nextval, ?, ?, ?, ?, ?, ?)");
my $fetchGE = $dbh->prepare("SELECT genetic_element_id  from L4_genetic_element WHERE genetic_element_name=? and chromosome=? and start_pos=? and stop_pos=?");


my $selectDatasetGE = $dbh->prepare("SELECT count(*) FROM L4_data_set_genetic_element WHERE anomaly_data_set_id=? and genetic_element_id=?");
my $insertDatasetGE = $dbh->prepare("INSERT INTO L4_data_set_genetic_element(L4_DATA_SET_GENETIC_ELEMENT_ID, anomaly_data_set_id, genetic_element_id) VALUES(L4_DATA_SET_GE_ID_SEQ.NEXTVAL, ?, ?)");

my $selectSample = $dbh->prepare("SELECT sample_id FROM L4_sample WHERE barcode=?");
my $insertSample = $dbh->prepare("INSERT INTO L4_sample(sample_id, barcode, patient, patient_id, disease_id) VALUES(l4_sample_id_seq.nextval, ?, ?, ?, ?)");
my $selectPatient = $dbh->prepare("SELECT patient_id from L4_patient WHERE patient=?");
my $insertPatient = $dbh->prepare("INSERT INTO L4_patient(patient_id, patient) VALUES(l4_patient_id_seq.nextval, ?)");

my $selectDatasetSample = $dbh->prepare("select count(*) from L4_data_set_sample where anomaly_data_set_id=? and sample_id=?");
my $insertDatasetSample = $dbh->prepare("INSERT INTO L4_data_set_sample(L4_DATA_SET_SAMPLE_ID, anomaly_data_set_id, sample_id, is_paired) VALUES(L4_DATA_SET_SAMPLE_ID_SEQ.NEXTVAL, ?, ?, ?)");

my $insertAnomaly = $dbh->prepare("INSERT INTO L4_anomaly_value(anomaly_value_id, anomaly_value, anomaly_data_set_id, genetic_element_id, sample_id) VALUES(L4_ANOMALY_VALUE_ID_SEQ.nextval, ?, ?, ?, ?)");

my $insertCount = 0;
while (<IN>) {
  chomp;
  my ($barcode, $gene, $value) = split("\t");
  if ($geneticElementTypeId == 1) {
    $gene = uc($gene);
    $value =~ s/[^\d\.\-\*_]//;
  }
  if ($barcode =~ /(TCGA-\d\d-\d\d\d\d)-\d\d\w-\d\d\w-\d\d\d\d-\d\d/) {
    # get or insert sample id
    my $patient = $1;
    my $sampleId = &getSampleId($barcode, $patient);
    die "Failed to get sample id for $barcode\n" unless defined $sampleId;
    my $count = &getResult($selectDatasetSample, $dataSetId, $sampleId);
    if ($count == 0) {
      $insertDatasetSample->execute($dataSetId, $sampleId, 0); # GE data is not paired
      #print "Added DSS for $dataSetId and $sampleId\n";
    }
    
    # get all gene ids
    my $geneIds = &getGeneIds($gene);
    die "Did not find gene $gene\n" unless defined $geneIds and scalar @$geneIds > 0;
    for my $geneId (@$geneIds) {
      $count = &getResult($selectDatasetGE, $dataSetId, $geneId);
      if ($count == 0) {
	$insertDatasetGE->execute($dataSetId, $geneId);
      }
      $insertAnomaly->execute($value, $dataSetId, $geneId, $sampleId);
      $insertCount++;
      if ($insertCount % 100000 == 0) {
	print "$insertCount anomaly values inserted ...\n";
	$dbh->commit();
      }
    }
    
  } else {
    die "Bad barcode format: $barcode\n";
  }
}

$dbh->commit();

$getGEsForName->finish;
$selectDatasetGE->finish;
$insertGE->finish;
$fetchGE->finish;
$insertDatasetGE->finish;
$selectSample->finish;
$insertSample->finish;
$selectDatasetSample->finish;
$insertDatasetSample->finish;
$insertAnomaly->finish;
$insertPatient->finish;

$dbh->disconnect;

print "Done!  Inserted $insertCount values for anomaly data set $dataSetId\n"; 
 
sub getGeneIds {
  my $gene = shift;
  if (defined $genes{$gene}) {
    return $genes{$gene};
  }

  $getGEsForName->execute($gene);
  my $results = $getGEsForName->fetchall_arrayref;
  my @ids;
  foreach my $row (@$results) {
    push(@ids, $row->[0]);
  }

  if (scalar @ids == 0) {
    # insert a gene with start, stop and chrom as -1 for now... not sure what else to do!
    $insertGE->execute($gene, $geneticElementTypeId, -1, -1, '?', 0);
    my $newGE = &getResult($fetchGE, $gene, "?", -1, -1);
    die "Could not insert new GE for $gene\n" unless defined $newGE;
    #print "Added new ge $gene\n";
    push(@ids, $newGE);
  }

  $genes{$gene} = \@ids;
  return \@ids;
}

sub getSampleId {
  my ($barcode, $patient) = @_;
  if (defined $barcodes{$barcode}) {
    return $barcodes{$barcode};
  }
  my $patientId = &getResult($selectPatient, $patient);
  if (! defined $patientId) {
    $insertPatient->execute($patient);
    $patientId = &getResult($selectPatient, $patient);
    #print "Added new patient $patient\n";
  }

  my $sampleId = &getResult($selectSample, $barcode);
  if (! defined $sampleId) {
    $insertSample->execute($barcode, $patient, $patientId, $diseaseId);
    $sampleId = &getResult($selectSample, $barcode);
    #print "Added new sample $barcode\n";
  } else {
    print "Sample already exists $barcode\n";
  }
  
  $barcodes{$barcode} = $sampleId;

  return $sampleId;
}


sub getResult {
  my $sth = shift;
  $sth->execute(@_);
  my $row = $sth->fetchrow_arrayref;
  return $row->[0];
}


