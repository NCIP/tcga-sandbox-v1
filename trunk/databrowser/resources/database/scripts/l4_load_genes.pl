use strict;
use DBI;
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $dbServer;
my $dbUsername;
my $dbPassword;
my $geneticElementTypeId; # 1 is gene, 2 is miRNA, 3 is methylation target

&GetOptions('D=s' => \$dbServer, 'U=s' => \$dbUsername, 'P=s' => \$dbPassword, 'e=i' => \$geneticElementTypeId );
my $inputFile = shift @ARGV;
die "usage: perl l4_load.pl -D oracleDb -U username -P password -e geneticElementTypeId inputFile\n" unless defined $dbServer && defined $dbUsername && defined $dbPassword && defined $inputFile && defined $geneticElementTypeId;

die "ERROR: input file '$inputFile' was not found\n" unless -e $inputFile;

print "About to connect to the database...\n";

# connect to the database
my $dbh = DBI->connect("DBI:Oracle:".$dbServer, $dbUsername, $dbPassword, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . DBI->errstr;

# inserts a new gene
my $insertGeneStmt = $dbh->prepare('INSERT INTO L4_genetic_element(genetic_element_name, genetic_element_type_id, chromosome, start_pos, stop_pos, in_cnv_region) VALUES(?, ?, ?, ?, ?, ?)');

# pass in: gene name, chromosome, start, start, stop, stop, start, stop
# will find genes with same name and chromosome with overlapping coordinates
my $overlapMatchStmt = $dbh->prepare('SELECT * from L4_genetic_element where genetic_element_name=? AND chromosome=? and ((start_pos<=? and stop_pos>?) or (start_pos<? and stop_pos>=?) or (start_pos>=? and stop_pos<=?))');

# updates positions of existing gene
my $updateGeneStmt = $dbh->prepare('UPDATE L4_genetic_element SET start_pos=?, stop_pos=? WHERE genetic_element_id=?');

# updates CNV value of existing gene
my $updateCNV = $dbh->prepare('UPDATE L4_genetic_element SET in_cnv_region=? WHERE genetic_element_id=?');

# format of file:
# gene_name   chrom  start   stop   in_cnv

# for each gene name, look for matches in DB with same name
open (IN, $inputFile) or die "Failed to open $inputFile\n";

my $newCount = 0;
my $updateCount = 0;
my $sameCount = 0;

while (<IN>) {
  chomp;

  my ($gene, $chromosome, $start, $stop, $cnv) = split("\t");
  my $in_cnv = $cnv =~ /^CNV/;

  $gene = uc($gene);
  my $geneMatches = &findGeneMatches($gene, $chromosome, $start, $stop);
  if (scalar @$geneMatches == 0) {
    &insertGene($gene, $chromosome, $start, $stop, $in_cnv);
    $newCount++;
  } elsif (scalar @$geneMatches > 1) {
    # this should not happen I hope...
    # if it does, print out all the matches and die
    print "!!! Gene $gene had multiple matches:\n";
    foreach my $match (@$geneMatches) {
      print "\t".$match->{genetic_element_id}." at ".$match->{start_pos}." - ".$match->{stop_pos}."\n";      
    }
    die "Dying...\n";
  } else {
    # only one match, so update
    if ($geneMatches->[0]->{start_pos} != $start ||
	$geneMatches->[0]->{stop_pos} != $stop) {
      &updateGene($geneMatches->[0]->{genetic_element_id}, $start, $stop);
      print "Gene $gene (".$geneMatches->[0]->{genetic_element_id}.") updated to $start-$stop (from ".$geneMatches->[0]->{start_pos}." - ".$geneMatches->[0]->{stop_pos}.")\n";
      $updateCount++;
    } else {
      if ($geneMatches->[0]->{in_cnv_region} == 1 && !$in_cnv) {
         $updateCNV->execute($geneMatches->[0]->{genetic_element_id}, $in_cnv ? 1 : 0);
         print "Updated CNV status for $gene \n";
      }
      print "Gene $gene location unchanged\n";
      
      $sameCount++;
    }
  }
}


#my $q = "select ge1.genetic_element_name as gene1, ge1.start_pos as gene1_start, ge1.stop_pos as gene1_stop, ge2.genetic_element_name as gene2, ge2.start_pos as gene2_start, ge2.stop_pos as gene2_stop from L4_genetic_element ge1, L4_genetic_element ge2 where ((ge1.start_pos >= ge2.start_pos and ge1.start_pos <= ge2.stop_pos) or (ge1.stop_pos>=ge2.start_pos and ge1.stop_pos<=ge2.stop_pos)) and ge1.genetic_element_id != ge2.genetic_element_id and ge1.genetic_element_name!=ge2.genetic_element_name and ge1.chromosome=ge2.chromosome and ge1.chromosome != '?'"

$insertGeneStmt->finish;
$overlapMatchStmt->finish;
$updateGeneStmt->finish;
$dbh->commit();

print "$newCount new genes, $updateCount updated genes, and $sameCount genes unchanged\n";



sub findGeneMatches {
  my ($gene, $chrom, $start, $stop) = @_;

  # look for matches...
  $overlapMatchStmt->execute($gene, $chrom, $start, $start, $stop, $stop, $start, $stop);
  return $overlapMatchStmt->fetchall_arrayref({genetic_element_id=>1, start_pos=>1, stop_pos=>1, chromosome=>1, in_cnv_region=>1 }); 
}


sub insertGene {
  my ($gene, $chrom, $start, $stop, $in_cnv) = @_;
  $insertGeneStmt->execute($gene, $geneticElementTypeId, $chrom, $start, $stop, ($in_cnv ? 1 : 0));
  print "Added new gene: $gene [$chrom $start-$stop]\n";
}

sub updateGene {
  my ($geneId, $start, $stop) = @_;
  $updateGeneStmt->execute($geneId, $start, $stop);
}
