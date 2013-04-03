use strict;
use DBI;

use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $dbServer;
my $dbUsername;
my $dbPassword;
my $diseaseId;
my $centerId; # in the portal db
my $bcrCenterId;


&GetOptions('D=s' => \$dbServer, 'U=s' => \$dbUsername, 'P=s' => \$dbPassword, 'c=s' => \$centerId, 's=s' => \$diseaseId, 'b=s' => \$bcrCenterId );

die "Usage: -D oracledb -U user -P password -c portal_center_id -s disease_id -b bcr_center_id (i.e. '09') filename\n" unless defined $dbServer and defined $dbUsername and defined $dbPassword and defined $diseaseId and defined $centerId and defined $bcrCenterId;

my $inputFile = shift @ARGV;

die "ERROR: input file '$inputFile' was not found\n" unless -e $inputFile;

print "About to connect to the databases...\n";

# connect to the database
my $dbh = DBI->connect("DBI:Oracle:".$dbServer, $dbUsername, $dbPassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . DBI->errstr;

# get read-only connection to postgres prod, for looking up barcodes
my $dbh_pg = DBI->connect("dbi:Pg:dbname=atlas;host=cbiodb560.nci.nih.gov;port=5456", "tcgaread", "read298" ) or die $DBI::errstr ;

print "Got database connection, about to open input file...\n";

open (IN, $inputFile) or die "ERROR: Could not open '$inputFile'\n";

my $fetchGE = $dbh->prepare("SELECT genetic_element_id from L4_genetic_element WHERE genetic_element_name=? and chromosome=? and start_pos<=? and stop_pos>=?");
my $selectDatasetGE = $dbh->prepare("SELECT count(*) FROM L4_data_set_genetic_element WHERE anomaly_data_set_id=? and genetic_element_id=?");
my $insertDatasetGE = $dbh->prepare("INSERT INTO L4_data_set_genetic_element(anomaly_data_set_id, genetic_element_id) VALUES(?, ?)");

my $selectSample = $dbh->prepare("SELECT sample_id FROM L4_sample WHERE barcode=?");
my $insertSample = $dbh->prepare("INSERT INTO L4_sample(barcode, patient, disease_id) VALUES(?, ?, ?)");
my $selectDatasetSample = $dbh->prepare("select count(*) from L4_data_set_sample where anomaly_data_set_id=? and sample_id=?");
my $insertDatasetSample = $dbh->prepare("INSERT INTO L4_data_set_sample(anomaly_data_set_id, sample_id, is_paired) VALUES(?, ?, ?)");

my $insertAnomaly = $dbh->prepare("INSERT INTO L4_anomaly_value(anomaly_value, anomaly_data_set_id, genetic_element_id, sample_id) VALUES(?, ?, ?, ?)");
my $findGE = $dbh->prepare("select genetic_element_id from L4_genetic_element where genetic_element_name=? and start_pos<=? and stop_pos>=?");

# get anomaly_types for the given center
my $results = $dbh->selectall_arrayref("select anomaly_type_id, anomaly_name from L4_anomaly_type where center_id=$centerId");

my %anomalyIdMap; # key is name, val is id
foreach my $row (@$results) {
  $anomalyIdMap{$row->[1]} = $row->[0];
  print "mapped $row->[1] to $row->[0] \n";
}

my %anomalyDatasets; # key is mutation type (anomaly name)

my $header = <IN>;
my @headers = split("\t", $header);

my %foundValues; # data set id | ge id | patient  ... if already recorded, don't need to record again (db will complain actually)

for (<IN>) {
  chomp;
  # line is: gene chrom start stop mutationtype barcode
  my ($gene, $chrom, $start, $stop, $mutationType, $barcode) = split("\t");
  $gene =~ tr/[a-z]/[A-Z]/;

  # get full barcode for sample
  my $results = $dbh_pg->selectall_arrayref("select barcode from biospecimen_barcode where barcode like '$barcode%$bcrCenterId'");
  my $fullBarcode = $results->[0]->[0];
  die "Could not find full barcode for $barcode\n" unless defined $fullBarcode;
  $barcode = $fullBarcode;
  $barcode =~ /(TCGA-\d\d-\d\d\d\d)-\d\d\w-\d\d\w-\d\d\d\d-\d\d/;
  my $patient = $1;
  
  # get anomaly type id
  my $anomalyTypeId = $anomalyIdMap{$mutationType} or die "Do not have anomaly type id for $mutationType\n";

  # get/make data set id
  my $datasetId = $anomalyDatasets{$mutationType};  
  if (! defined $datasetId) {
    $datasetId = &makeDataSet($anomalyTypeId);
    die "Failed to make new dataset for $anomalyTypeId\n" unless defined $datasetId;
    $anomalyDatasets{$mutationType} = $datasetId;
  }

  # get gene id
  my $geneticElementId = &getId($fetchGE, $gene, $chrom, $start, $stop);
  if (! defined $geneticElementId) {
    print "Could not find gene for $gene, $chrom $start-$stop\n";
    next;
  }

  if (! $foundValues{"$datasetId|$geneticElementId|$patient"}) {
    # if have no record for this gene and patient in this dataset...

    # now get/insert sample row
    my $sampleId = &getId($selectSample, $barcode);
    if (! defined $sampleId) {
      $insertSample->execute($barcode, $patient, $diseaseId);
      $sampleId = &getId($selectSample, $barcode);
      $insertDatasetSample->execute($datasetId, $sampleId, 0);
    } else {
      my $count = &getId($selectDatasetSample, $datasetId, $sampleId);
      if ($count == 0) {
	$insertDatasetSample->execute($datasetId, $sampleId, 0);
      }
    }

    # insert link between dataset and gene
    my $count = &getId($selectDatasetGE, $datasetId, $geneticElementId);
    if ($count == 0) {
      $insertDatasetGE->execute($datasetId, $geneticElementId);
    }

    # insert anomaly value
    $insertAnomaly->execute(1, $datasetId, $geneticElementId, $sampleId);
    $foundValues{"$datasetId|$geneticElementId|$patient"} = 1;
  }
  }
}

# now need to insert 0 for all gene/patient combos that didn't have mutations...
my $allSampleGeneQuery = "select gene_name, barcode, avg(reference_acc_min), avg(reference_acc_max) from biospecimen_barcode bb, biospecimen_ncbi_trace bt, trace_info ti where bb.bcr_center_id='$bcrCenterId' and bb.sample_type='01' and bb.biospecimen_id=bt.biospecimen_id and bt.ncbi_trace_id=ti.ncbi_trace_id group by gene_name, barcode";

my $allSth = $dbh_pg->prepare($allSampleGeneQuery);
$allSth->execute();
while ( my @row = $allSth->fetchrow_array ) {
  my $gene = $row[0];
  my $barcode = $row[1];
  my $min = $row[2];
  my $max = $row[3];

  $barcode =~ /(TCGA-\d\d-\d\d\d\d)-\d\d\w-\d\d\w-\d\d\d\d-\d\d/;
  my $patient = $1;

  # look for genetic element that fits this criteria
  $findGE->execute($gene, $min, $max);
  my $row = $findGE->fetchrow_arrayref;
  my $geId = $row->[0];
  if (! defined $geId) {
    print "$gene\t$barcode\t$min\t$max\n";
  } else {
    # for each data set id in anomalyDatasets... check if there is a value already
    # if not insert 0
    for my $datasetId (values %anomalyDatasets) {
      if (! $foundValues{"$datasetId|$geId|$patient"}) {
	my $sampleId = &getId($selectSample, $barcode);
	if (! defined $sampleId) {
	  $insertSample->execute($barcode, $patient, $diseaseId);
	  $sampleId = &getId($selectSample, $barcode);
	  $insertDatasetSample->execute($datasetId, $sampleId, 0);
	} else {
	  my $count = &getId($selectDatasetSample, $datasetId, $sampleId);
	  if ($count == 0) {
	    $insertDatasetSample->execute($datasetId, $sampleId, 0);
	  }
	}	

	# insert value
	$insertAnomaly->execute(0, $datasetId, $geId, $sampleId);
	$foundValues{"$datasetId|$geId|$patient"} = 1;
      }
    }
  }
}

$dbh->commit();

$fetchGE->finish;
$selectSample->finish;
$findGE->finish;

$dbh->disconnect();

 

sub makeDataSet {
  my $anomalyTypeId = shift;

  $dbh->do("insert into L4_anomaly_data_set(anomaly_type_id) values($anomalyTypeId)");
  my $results = $dbh->selectall_arrayref("select max(anomaly_data_set_id) from L4_anomaly_data_set where anomaly_type_id=$anomalyTypeId");
  print "made data set for anomaly type $anomalyTypeId ($results->[0]->[0])\n";
  return $results->[0]->[0];
}

sub getId() {
  my $sth = shift;
  $sth->execute(@_);
  my $data = $sth->fetchall_arrayref;
  die "More than one row found in ID query (".join(" ", @_)."!\n" if scalar @$data > 1;
  return $data->[0]->[0];
}

sub getGeneId() {
  my $sth = shift;
  $sth->execute(@_);
  my $data = $sth->fetchall_arrayref;
  # look for highest id that matches, meaning most recently added one... is in later data sets.  ugh.
  my $maxId = $data->[0]->[0];
  foreach my $row (@$data) {
     if ($row->[0] > $maxId) {
        $maxId = $row->[0];
     }
  }
  return $maxId;
}
