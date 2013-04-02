#!/usr/bin/perl
use lib "../lib";
use TCGA::CNV::RefData::BDB;
use TCGA::CNV::Config;
use Getopt::Long;
use Pod::Usage;
use strict;
use warnings;

my ($db, $id, $file);

my ($bins_f,$bins_id, $genes_f, $genes_id, $overwrite, $cnv_f, $do_exon);

GetOptions( "bins-f:s" => \$bins_f,
	    "bins-id:s" => \$bins_id,
	    "genes-f:s" => \$genes_f,
	    "genes-id:s" => \$genes_id,
	    "do-exon!" => \$do_exon,
	    "overwrite!" => \$overwrite,
	    "cnv-f:s" => \$cnv_f) or pod2usage(1);

if ($bins_f) {
  if (!$bins_id) {
    print STDERR "Must specify bins-id\n";
    exit(1);
  }
  if ($genes_f) { # create genes db
    if (!$genes_id) {
      print STDERR "Must specify genes-id\n";
      exit(1);
    }
    if (db_exists($genes_f,$genes_id) && !$overwrite) {
      print STDERR "Genes db with id '$genes_id' exists (use --overwrite)\n";
      exit(1);
    }
    TCGA::CNV::RefData::BDB->createGenesBDBFiles($genes_f, $genes_id, $bins_f, $bins_id, $do_exon);
  }
  else { # create bins db
    if (db_exists($bins_f,$bins_id) && !$overwrite) {
      print STDERR "Bins db with id '$bins_id' exists (use -overwrite)\n";
      exit(1);
    }
    TCGA::CNV::RefData::BDB->createBinsBDBFiles($bins_f, $bins_id);
  }
}
else {
  if ($genes_f) {
    print STDERR "Existing bins db must be specified to create genes db\n";
    exit(1);
  }
  elsif ($cnv_f) { # create combined cnv db
    if (db_exists($cnv_f) && !$overwrite) {
      print STDERR "CNV db '$cnv_f' exists (use --overwrite)\n";
      exit(1);
    }
    TCGA::CNV::RefData::BDB->createCombinedCnvBDBFiles($cnv_f);
  }
  else {
    pod2usage(1);
  }
}

sub db_exists {
  my ($db,$id) = @_;
  opendir my $d, $TCGA::CNV::Config::REFDATA_DIR or die "$TCGA::CNV::Config::REFDATA_DIR : $!";
  my @files = readdir $d;
  my $matcher = "^$db\\.".($id ? "$id\\." : "");
  return scalar grep(/$matcher/,@files);
}

=head1 NAME

createRefData.pl - creates Berkeley DB versions of gene reference tables

=head1 SYNOPSIS

createRefData.pl --bins-f [bins filestem] --bins-id [tag] --genes-f [genes filestem] --genes-id [tag] --cnv-f [cnv data file] [ --do-exon --overwrite ]

dat files should be located in $TCGA::CNV::Config::REFDATA_DIR
output is placed in $TCGA::CNV::Config::REFDATA_DIR

--do-exon : calculate bdb files for exon data (off by default)
--overwrite : replace the current bdb files (off by default)

=head1 DESCRIPTION

This is a utility for use with the L<TCGA::DBRO> package.

=cut
