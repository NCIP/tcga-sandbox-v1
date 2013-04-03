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

&GetOptions('D=s' => \$dbServer, 'U=s' => \$dbUsername, 'P=s' => \$dbPassword, 'T=s' => \$anomalyTypeId, 's=s' => \$diseaseId, 'e=s' => \$geneticElementTypeId  );
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

my %genes; # key is gene, val is genetic element id
my %barcodes; # key is barcode, val is sample id
my $count = 0;
my $geneCount = 0;
my $patientCount = 0;

my $fetchGE = $dbh->prepare("SELECT genetic_element_id  from L4_genetic_element WHERE genetic_element_name=? and chromosome=? and start_pos=? and stop_pos=?");
my $insertGE = $dbh->prepare("INSERT INTO L4_genetic_element(genetic_element_id, genetic_element_name, genetic_element_type_id, start_pos, stop_pos, chromosome, in_cnv_region) VALUES(l4_genetic_element_id_seq.nextval, ?, ?, ?, ?, ?, ?)");
my $selectDatasetGE = $dbh->prepare("SELECT count(*) FROM L4_data_set_genetic_element WHERE anomaly_data_set_id=? and genetic_element_id=?");
my $insertDatasetGE = $dbh->prepare("INSERT INTO L4_data_set_genetic_element(L4_DATA_SET_GENETIC_ELEMENT_ID, anomaly_data_set_id, genetic_element_id) VALUES(L4_DATA_SET_GE_ID_SEQ.NEXTVAL, ?, ?)");

my $selectSample = $dbh->prepare("SELECT sample_id FROM L4_sample WHERE barcode=?");
my $insertSample = $dbh->prepare("INSERT INTO L4_sample(sample_id, barcode, patient, patient_id, disease_id) VALUES(l4_sample_id_seq.nextval, ?, ?, ?, ?)");
my $selectPatient = $dbh->prepare("SELECT patient_id from L4_patient WHERE patient=?");
my $insertPatient = $dbh->prepare("INSERT INTO L4_patient(patient_id, patient) VALUES(l4_patient_id_seq.nextval, ?)");

my $selectDatasetSample = $dbh->prepare("select count(*) from L4_data_set_sample where anomaly_data_set_id=? and sample_id=?");
my $insertDatasetSample = $dbh->prepare("INSERT INTO L4_data_set_sample(L4_DATA_SET_SAMPLE_ID, anomaly_data_set_id, sample_id, is_paired) VALUES(L4_DATA_SET_SAMPLE_ID_SEQ.nextval, ?, ?, ?)");

my $insertAnomaly = $dbh->prepare("INSERT INTO L4_anomaly_value(anomaly_value_id, anomaly_value, anomaly_data_set_id, genetic_element_id, sample_id) VALUES(L4_anomaly_value_id_SEQ.nextval, ?, ?, ?, ?)");

my $insertCount = 0;
while (<IN>) {
  chomp;
  my @line = split("\t");

  # TCGA-02-0014-01A-01D-0185-02    C9orf152        9       112001666       112010234       0.0014  0.0014  PAIRED  CNV
  # barcode gene chromosome start stop value capped-value PAIRED|UNPAIRED CNV|NOCNV
  my $barcode = $line[0];
  if ($barcode =~ /(TCGA-\d\d-\d\d\d\d)-\d\d\w-\d\d\w-\d\d\d\d-\d\d/) {
    my $patient = $1;
    my $gene = $line[1];
    if ($geneticElementTypeId == 1) {    
       $gene = uc($gene);
    }
    my $chromosome = $line[2];
    my $start = $line[3];
    my $stop = $line[4];
    my $value = $line[5];
    my $paired = $line[7];
    # special for this line, because end charater is wonky
    my $isCnv = $line[8] =~ /^CNV/;

    # 1. insert genetic_element
    # first see if we have seen it in this file before
    my $geneticElementId = $genes{"$gene:$chromosome:$start:$stop"};

    if (! defined $geneticElementId) {
      $fetchGE->execute($gene, $chromosome, $start, $stop);
      my $results = $fetchGE->fetchrow_arrayref();
      $geneticElementId = $results->[0];

      if (! defined $geneticElementId) {
	$insertGE->execute($gene, $geneticElementTypeId, $start, $stop, $chromosome, $isCnv ? 1 : 0);
	$geneticElementId = &getId($fetchGE, $gene, $chromosome, $start, $stop);
	$geneCount++;
      }
      $genes{"$gene:$chromosome:$start:$stop"} = $geneticElementId;
    }

    die "Could not get genetic element id for $gene?\n" unless defined $geneticElementId;

    # only add data if not X or Y since this is copynumber
    if ($chromosome !~ /[x|y]/i) {
      my $count = &getId($selectDatasetGE, $dataSetId, $geneticElementId);
      if ($count == 0) {
	  $insertDatasetGE->execute($dataSetId, $geneticElementId);
      }

      # 2. insert sample
      my $sampleId = $barcodes{$barcode};
      if (!defined $sampleId) {
	my $patientId = &getId($selectPatient, $patient);
	if (! defined $patientId) {
	  $insertPatient->execute($patient);
	  $patientId = &getId($selectPatient, $patient);
	}	
	$sampleId = &getId($selectSample, $barcode);
	if (!defined $sampleId) {
	  $insertSample->execute($barcode, $patient, $patientId, $diseaseId);
	  $sampleId = &getId($selectSample, $barcode);
	  $insertDatasetSample->execute($dataSetId, $sampleId, $paired eq 'PAIRED' ? 1 : 0);
	  $patientCount++;
	} else {
	  my $count = &getId($selectDatasetSample, $dataSetId, $sampleId);
	  if ($count == 0) {
	    $insertDatasetSample->execute($dataSetId, $sampleId, $paired eq 'PAIRED' ? 1 : 0);
	  }
	}
      }
      die "Could not get sample id for $barcode?\n" unless defined $sampleId;
      $barcodes{$barcode} = $sampleId;
      
      # 3. insert anomaly value
      $insertAnomaly->execute($value, $dataSetId, $geneticElementId, $sampleId);
      $insertCount++;
      if ($insertCount % 100000 == 0) {
	print "$insertCount anomaly values inserted ...\n";
	$dbh->commit();
      }
    } else {
      #print "Skipping gene on $chromosome chromosome\n";
    }

  } else {
    print "Barcode not in expected format: $barcode.  Skipping line.\n";
  }
}
close IN;
print "Done!  Inserted $insertCount values for anomaly data set $dataSetId\n";

# update data set with gene and patient counts
$dbh->commit();

$selectDatasetGE->finish;
$insertDatasetGE->finish;
$insertGE->finish;
$fetchGE->finish;
$selectSample->finish;
$insertSample->finish;
$selectDatasetSample->finish;
$insertDatasetSample->finish;
$insertAnomaly->finish;
$insertPatient->finish;

$dbh->disconnect;


sub getId {
  my $sth = shift;
  $sth->execute(@_);
  my $row = $sth->fetchrow_arrayref;
  return $row->[0];
}

