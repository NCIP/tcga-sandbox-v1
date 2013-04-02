#!/usr/bin/perl
use lib "../lib";
use TCGA::CNV::SegIO;
use TCGA::EXPR::ExprIO;
use TCGA::CNV::SegToGene;
use TCGA::EXPR::ExprToGene;
use TCGA::CNV::RefData::SQLite;
use Getopt::Long;
use Pod::Usage;
use Carp qw(croak);
use strict;
use warnings;

my ($help, $db_f, $disease, $datatype, $get_hdrs);
my $success = GetOptions ("help|?" => \$help,
			  "input-headers" => \$get_hdrs,
			  "db=s" => \$db_f,
			  "datatype=s" => \$datatype);

pod2usage(2) unless ($success && !$help);

my @datatypes = qw(exp cna);
my @dcc_exp_headers = qw(barcode uuid participant_code sample_type_code center_id platform_id   platform_name  entrez_gene_symbol expression_value);
my @dcc_cna_headers = qw(barcode uuid participant_code sample_type_code center_id platform_id platform_name cna_value_id  chromosome chr_start chr_stop num_mark seg_mean);
$datatype = lc $datatype;
pod2usage(2) unless ( $datatype =~ /cna|exp/ );

if ($get_hdrs) {
  my @hdrs = eval "\@dcc_${datatype}_headers";
  print join("\n",@hdrs), "\n";
  exit(0);
}

unless ( $ENV{REFDATA_DIR} && -d $ENV{REFDATA_DIR} ) {
    croak "Set env var 'REFDATA_DIR' to the location of the sqlite database";
}

my $infile = shift();
my $refdata = TCGA::CNV::RefData::SQLite->new($db_f);

$TCGA::CNV::SegToGene::VERBOSE = 0;
$TCGA::EXPR::ExprToGene::VERBOSE = 0;

if (! -e $infile) {
  print STDERR "$infile not present, skipping\n";
}
for ($datatype) {
  /^cna$/ && do {
    my $segobj = TCGA::CNV::SegToGene->new($refdata);
    my  $segio = TCGA::CNV::SegIO->new($infile, \@dcc_cna_headers);
    $segobj->analyzeSegFile($segio);
    $segobj->_ProcessTumorNormalPairs();
    my $outfile = "$infile.cna.txt";
    open my $f, ">$outfile" or die $!;
    # headers
    print $f join("\t", qw(gene_id gene_name tumor_barcode data_value cnv_status paired_status)), "\n";
    for my $rec (@{$segobj->{_tumor_ratios}}) {
	print $f join("\t",@{$rec}{qw(gene_id gene_name tumor_bc extreme cnv_status paired_status)}), "\n";
    }
    undef $segobj;
    last;
  };
  /^exp$/ && do {
    my $exprobj = TCGA::EXPR::ExprToGene->new($refdata);
    my $exprio = TCGA::EXPR::ExprIO->new($infile, \@dcc_exp_headers);
    $exprobj->analyzeExprFile($exprio);
    foreach my $platform ($exprobj->platforms) {
      next unless length($platform);
      my $outfile = "$infile.exp.$platform.ratio.txt";
      open my $f, ">$outfile" or die $!;
      print $f join("\t", qw(gene_name tumor_sample_barcode log_2_tumor_normal_ratio)), "\n";      
      for my $bc ($exprobj->barcodes($platform)) {
	my $tv = $exprobj->tumor_ratios($platform,$bc);
	for my $gene (keys %$tv) {
	  print $f join("\t",$gene, $bc, $tv->{$gene}),"\n";
	}
      }
      close $f;
      $outfile = "$infile.exp.$platform.z.txt";
      open $f, ">$outfile" or die $!;
      print $f join("\t", qw(gene_name tumor_sample_barcode z_score)), "\n";
      for my $bc ($exprobj->barcodes($platform)) {
	my $tz = $exprobj->tumor_z($platform,$bc);
	for my $gene (keys %$tz) {
	  print $f join("\t",$gene, $bc, $tz->{$gene}),"\n";
	}
      }
    }
    undef $exprobj;
    last;
  };
  do {
    die "what?";
  };
}

1;

=head1 NAME

dbroET.pl - Extract and transform level 3 data for the data browser

=head1 SYNOPSIS

[1] REFDATA_DIR=[location_of_db] dbroET.pl --db [reference_sqlite_db_file] --datatype [cna|exp] [input_level3_file]

Output: [input_level3_file].[datatype].[level4_type].txt
[level4_type] := cna | exp.[platform].ratio | exp.[platform].z

[2] dbroET.pl --input-headers --datatype [cna|exp]

Output : list of expected input headers (and column data)

=head1 DESCRIPTION

The script takes as input a text dump of level 3 database data for either copy
number array (CNA; i.e., seg) data or expression array (EXP) data.

For CNA data, the output file is tab delimited text, with columns in order
as follows:
gene_id gene_name tumor_barcode data_value cnv_status paired_status

For EXP data, two output files are generated:
(1) the ".ratio.txt" file, with columns in order as follows:
gene_name tumor_sample_barcode log_2_tumor_normal_ratio (the data value)

(2) the ".z.txt" file, with columns in order as follows:
gene_name tumor_sample_barcode z_score (the data value)


=head1 AUTHOR

Mark A. Jensen ( mark -dot- jensen -at- nih -dot- gov )
SRA International

=head1 COPYRIGHT

(c) 2012 SRA International
Licensed for use under the caBIG license v1.0.

=cut
