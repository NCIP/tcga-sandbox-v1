#
# This script, give database credentials, a platform name and file location, will
# load probes for a platform into the probe table.
#
# NOTE: the probe table must already have a new partition added for the new platform
#
#
use strict;
use DBI;
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $oracleDb;
my $oracleUsername;
my $oraclePassword;
my $platformName;
my $probeFile;
my $platformId;
my $probesExist;
my $dbh;

# get options
&GetOptions('D=s' => \$oracleDb, 'U=s', \$oracleUsername, 'P=s' => \$oraclePassword);
die "Required: -D db -U username -P password\n" unless defined $oracleDb and defined $oracleUsername and defined $oraclePassword;

# parameter 1: platform name for which we are loading probes
$platformName = shift @ARGV or die "Required 1st parameter: platform name\n";

# parameter 2: platform probes file
$probeFile = shift @ARGV or die "Required 2nd parameter: probe file\n";


# connect to the database
$dbh = DBI->connect("DBI:Oracle:".$oracleDb, $oracleUsername, $oraclePassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . $DBI::errstr;

$platformId = getPlatformId();
$probesExist = getExistingProbeCount();
print("probes exist = $probesExist\n");
# prepare statements needed
my $insertProbeStmt = $dbh->prepare("insert into probe(probe_id,platform_id,probe_name,chromosome,start_position,end_position,ncbi_gene_id) values (probe_id_seq.NEXTVAL,?,?,?,?,?,?)");
my $updateProbeStmt = $dbh->prepare("update probe set chromosome = ?, start_position = ?, end_position = ?, ncbi_gene_id = ? where platform_id = ? and probe_name = ?");
my $recordCount=0;
# loop thru probe file and insert or update the probe table as appropriate
open(PROBE_FILE, $probeFile) or die "Could not open $probeFile\n";

   # 1. first line contains headers - these can be ignored
   my $line = <PROBE_FILE>;
   print(" first line: $line \n");
   # 2. go through each line and insert or update the appropriate values in the probe
   while(<PROBE_FILE>) {
	chomp;
	#s/\r//;
	#s/\n//;
	my @line = split(/\t/);	
	# get all the values from the row
	my $probeName  = $line[0];
	my $chromosome = $line[1];
	my $chrStart   = $line[2];
	my $chrStop    = $line[3];
	my $ncbiGene   = $line[4]; 
	# 3. insert or update the values in the probe table
        # if the probes don't already exist in the database, insert them
        if ($probesExist == 0) {
	     print("inserting record for \n probeName:$probeName,Chromosome: $chromosome, chr Start: $chrStart, chr Stop:$chrStop, ncbiGene: $ncbiGene \n");
	     $insertProbeStmt->execute($platformId, $probeName, $chromosome, $chrStart, $chrStop, $ncbiGene);
	     $recordCount = $recordCount + 1;
	# otherwise, update them
	} else {
	     $updateProbeStmt->execute($chromosome, $chrStart, $chrStop, $ncbiGene,$platformId, $probeName); 
	}
    }
	
close(PROBE_FILE);

$dbh->commit();
$dbh->disconnect();
print "Done loading probes.\n\n $recordCount probes added.\n";


sub getPlatformId {
    # find platform id
    my $platformSql = "select platform_id from platform p where p.platform_name = ?";
    my $platformInfo = $dbh->selectall_arrayref($platformSql, undef, $platformName);
    my $pId = $platformInfo->[0]->[0];
    die "Could not find platform for $platformName\n" unless defined $pId;
    return $pId;
}

sub getExistingProbeCount {
    # see if there are probes for platform id
    my $probeSql = "select 1 from probe where platform_id = ? and rownum=1";
    my $probeInfo = $dbh->selectall_arrayref($probeSql, undef, $platformId);
    my $count = $probeInfo->[0]->[0];
    unless (defined $count) {
       $count = 0;
    }
    return $count;
}