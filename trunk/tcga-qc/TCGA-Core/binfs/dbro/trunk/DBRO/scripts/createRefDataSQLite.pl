#!/usr/bin/perl
use lib "../lib";
use TCGA::CNV::RefData::SQLite;
use TCGA::CNV::Config;
use IPC::Run3;
use File::Spec;
use Getopt::Long;
use Pod::Usage;
use strict;
use warnings;

my ($db_f, $id, $file);

my ($bins_f,$bins_id, $genes_f, $genes_id, $overwrite, $cnv_f, $do_exon);
my ($dbh);

GetOptions( "db-f:s" => \$db_f,
            "bins-f:s" => \$bins_f,
	    "bins-id:s" => \$bins_id,
	    "genes-f:s" => \$genes_f,
	    "genes-id:s" => \$genes_id,
	    "do-exon!" => \$do_exon,
	    "overwrite!" => \$overwrite,
	    "cnv-f:s" => \$cnv_f) or pod2usage(1);

unless ($db_f) {
    pod2usage(1);
}

unless (db_exists($db_f)) {
  my ($in,$out,$err);
  my $sql = "../sql/refdata_create.sql";
  open my $sqlh, $sql or die $!;
  run3 ['sqlite3', _catfile_ref($db_f)], $sqlh,\$out,\$err ;
  die $err if $err;
}

my $refdata = TCGA::CNV::RefData::SQLite->new($db_f, $bins_f, $bins_id,$genes_f, $genes_id, $cnv_f);

if ($bins_f) {
  if (!$bins_id) {
    print STDERR "Must specify bins-id\n";
    exit(1);
  }
# create bins table
    if (table_exists($refdata,'bins') && !$overwrite) {
	print STDERR "Bins table exists in '$db_f' (use -overwrite)\n";
	exit(1);
    }
    $refdata->createBinsSQLiteDB($bins_f, $bins_id);
}

if ($genes_f) { # create genes db
    if (!$genes_id) {
	print STDERR "Must specify genes-id\n";
	exit(1);
    }
    if (!table_exists($refdata,'bins')) {
	print STDERR "Existing bins table must be present to create genes table\n";
	exit(1);
    }
    if (table_exists($refdata,'genes') && !$overwrite) {
	print STDERR "Genes table  exists (use --overwrite)\n";
	exit(1);
    }
    $refdata->createGenesSQLiteDB($genes_f, $genes_id, $bins_f, $bins_id, $do_exon);
}

if ($cnv_f) { # create combined cnv db
    if (table_exists($refdata, 'ccnv') && !$overwrite) {
	print STDERR "CNV table exists in '$db_f' (use --overwrite)\n";
	exit(1);
    }
    $refdata->createCombinedCnvSQLiteDB($cnv_f);
}

sub db_exists {
    # "exists" if the file is present
  my ($db_f,$id) = @_;
  return -e join('/',$TCGA::CNV::Config::REFDATA_DIR,$db_f);
}

sub table_exists {
    # "exists" if the table is present and there are >0 rows
    my ($refdata,$table) = @_;
    my $dbh = $refdata->{_dbh};
    my $s = "PRAGMA table_info($table)";
    my $sth = $dbh->prepare($s);
    $sth->execute();
    if (defined $sth->fetch->[0]) {
	$sth = $dbh->prepare("select count(*) from $table");
	$sth->execute;
	return $sth->fetch->[0];
    }
    return;
}

sub _catfile_ref {
  my ($fn) = @_;
  return File::Spec->catfile($REFDATA_DIR,$fn);
}

=head1 NAME

createRefDataSQLite.pl - creates SQLite DB version of gene reference tables

=head1 SYNOPSIS

createRefDataSQLite.pl --db-f [database filename] --bins-f [bins filestem] --bins-id [tag] --genes-f [genes filestem] --genes-id [tag] --cnv-f [cnv data file] [ --do-exon --overwrite ]

dat files should be located in $TCGA::CNV::Config::REFDATA_DIR
output is placed in $TCGA::CNV::Config::REFDATA_DIR

--do-exon : load exon tables  (off by default)
--overwrite : replace the current affected tables (off by default)

=head1 DESCRIPTION

This is a utility for use with the L<TCGA::DBRO> package.

The script assumes the schema defined in $DISTRO/sql/refdata_create.sql. 

=cut
