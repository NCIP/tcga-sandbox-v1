package TCGA::CNV::RefData::BDB;
use TCGA::CNV::Config;
use TCGA::CNV::Segment;
use base 'TCGA::CNV::RefData';
use File::Spec;
use POSIX qw(O_CREAT O_RDWR);
use DB_File;
use Tie::IxHash;
use strict;
use warnings;

my $TABLES = {}; # link up to the superclass?
my $NUM_GENES = 0;

sub new {
  my $class = shift;
  $TCGA::CNV::RefData::TABLES = $TABLES;
  $class->SUPER::new(@_);
}
# override the "read files" methods
sub _ReadBinFile {
  my $self = shift;
  my ($bins_f, $bins_id) = ($self->bins_f, $self->bins_id);
  my (%chr_binstart, %chr_binstop, @bin2chr, @bin2start,@bin2stop,@bin2mid);

  for (qw(chr_binstart chr_binstop)) {
    eval "tie \%$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  for (qw( bin2chr bin2stop bin2start bin2mid)) {
    eval "tie \@$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }

  $TABLES->{bins}{$bins_id}{chr_bins}  = { start => \%chr_binstart, stop => \%chr_binstop };
  $TABLES->{bins}{$bins_id}{bin2chr}   = \@bin2chr;
  $TABLES->{bins}{$bins_id}{bin2start} = \@bin2start;
  $TABLES->{bins}{$bins_id}{bin2stop}  = \@bin2stop;
  $TABLES->{bins}{$bins_id}{bin2mid}   = \@bin2mid;
  $TABLES->{bins}{$bins_id}{BIN_SIZE}  = $bin2stop[$chr_binstart{1}] - $bin2start[$chr_binstart{1}] + 1;
  return 1;
}

sub _ReadGeneFile {
  my $self = shift;
  my ($do_exon) = @_;
  my ($genes_id, $genes_f, $bins_id, $bins_f) = ($self->genes_id, $self->genes_f, $self->bins_id, $self->bins_f);
  my ($bins, $BIN_SIZE);
  my (%bin2gene, %gene_index, %gene2seg);
  my (@bin2gene,@gene_order, @genelist,@gene2exon_length,@exon_order);
  $DB_HASH->{"cachesize"} = 100000000;
  # tie read/write hashes
  for (qw(bin2gene gene_index gene2seg)) {
    eval "tie \%$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  # tie read/write arrays
  for (qw(gene_order genelist)) {
    eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  if ($do_exon) {
    for (qw(gene2exon_length exon_order)) {
      eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
      die "$_ : $@" if $@;
    }
  }
  $DB::single=1;
  $TABLES->{genes}{$genes_id}{gene_order} = \@gene_order;
  $TABLES->{genes}{$genes_id}{gene_order_h} = \%gene_index;
  $TABLES->{genes}{$genes_id}{genelist} = \@genelist;
  $TABLES->{genes}{$genes_id}{bin2gene} = \%bin2gene;
  if ($do_exon) {
    $TABLES->{genes}{$genes_id}{gene2exon_length} = \@gene2exon_length;
    $TABLES->{genes}{$genes_id}{exon_order} = \@exon_order;
  }

  return 1;
}
sub _ReadCombinedCNVFile {
  my $self = shift;
  my ($inf) = $self->cnvs_f;
  return unless $self->cnvs_f;
  tie my %ccnv, 'DB_File', _catfile_ref("$inf.bdb"), O_RDWR or die "$inf.bdb : $!";
  $TABLES->{cnvs}{combined} = \%ccnv;
  return 1;
}
sub createBinsBDBFiles {
  my $class = shift;
  my ($bins_f, $bins_id) = @_;
  my $inf = _catfile_ref("$bins_f.dat");
  open my $infh, $inf or die "cannot open $bins_f: $!";
  my (%chr_binstart, %chr_binstop, @bin2chr, @bin2start,@bin2stop,@bin2mid);

  for (qw(chr_binstart chr_binstop)) {
    eval "tie \%$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_CREAT|O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  for (qw( bin2chr bin2stop bin2start bin2mid)) {
    eval "tie \@$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_CREAT|O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  while (<$infh>) {
    chomp;
    if (/^##/) {
      my ($dummy, $chr, $first_bin, $last_bin) = split /\t/;
      $chr_binstart{$chr} = $first_bin;
      $chr_binstop{$chr}  = $last_bin;
    } else {
      my ($bin, $chr, $start, $stop) = split /\t/;
      $bin2chr[$bin] = $chr;
      $bin2start[$bin] = $start;
      $bin2stop[$bin] = $stop;
      $bin2mid[$bin] = int(($start + $stop) / 2);
    }
  }
  return;
}

sub createGenesBDBFiles {
  my $class = shift;
  my ($genes_f, $genes_id, $bins_f, $bins_id, $do_exon) = @_;
  my $genes_inf = _catfile_ref("$genes_f.dat");

  my (%bin2gene, %chr_binstart, %chr_binstop, %gene_index,%gene2seg);
  my (@bin2stop,@bin2start,@gene_order,@gene2exon_length,@exon_order,@genelist);
  # tie readonly hashes
  for (qw(chr_binstart chr_binstop)) {
    eval "tie \%$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  # tie readonly arrays
  for (qw( bin2stop bin2start)) {
    eval "tie \@$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  # tie read/write hashes
  for (qw(bin2gene gene_index gene2seg)) {
    eval "tie \%$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_CREAT|O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  # tie read/write arrays
  for (qw(gene_order genelist)) {
    eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_CREAT|O_RDWR or die \$!";
    die "$_ : $@" if $@;
  }
  if ($do_exon) {
    for (qw(gene2exon_length exon_order)) {
      eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_CREAT|O_RDWR";
    }
  }

  my $BIN_SIZE = $bin2stop[$chr_binstart{1}] - $bin2start[$chr_binstart{1}] + 1;
  open(my $infh, $genes_inf) or die "cannot open $genes_inf : $!";
  my %genelist;
  tie %genelist, 'Tie::IxHash';
  my $gi = 0;
  while (<$infh>) {
    chomp;
    my ($gene, $chr, $start, $stop, $exons) = split /\t/;
    my ($seg,$start_bin,$stop_bin);
    # not a Segment object
    # a colon-separated list of the Segment fields:
    # chromosome:start coord:stop coord:gene name
  $DB::single=1;
    $seg = join(":",$chr,$start,$stop,$gene);
    $genelist{$gene}++;
    $gene_order[$gi] = $seg;
    # call %gene_order now %gene_index
    # store the indices as colon separated values

    $gene_index{$gene} = (defined $gene_index{$gene} ? join(":",$gene_index{$gene}, $gi) : $gi );

    $start_bin = $chr_binstart{$chr}  +
      int($start / $BIN_SIZE);
    $stop_bin  = $chr_binstart{$chr}  +
      int($stop  / $BIN_SIZE);
    # bin numbers are just the indices for @bin2gene
    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      $bin2gene{$bin} = ($bin2gene{$bin} ? join("\;",$bin2gene{$bin},$gene) : $gene);
    }

    if ($do_exon) {
      for my $exon (split(",", $exons)) {
	my ($estart, $estop) = split("-", $exon);
	my $eseg = join(":",$chr,$estart,$estop, "$gene,$exon");
	$gene2exon_length[@gene_order -1] ||= 0;
	$gene2exon_length[@gene_order -1] += $estop-$estart+1;
	# not a Segment object
	# a colon-separated list of the Segment fields:
	# chromosome:start coord:stop coord:gene name
	# segments separated by semicolon
	$exon_order[@gene_order-1] = $exon_order[@gene_order-1] ? join("\;", $exon_order[@gene_order-1], $eseg) : $eseg;
      }
    }
    $gi++;
  }
  @genelist = keys %genelist;

}

sub createCombinedCnvBDBFiles {
  my $self = shift;
  my ($inf) = @_;
  open my $infh, _catfile_ref($inf) or die "can't open $inf: $!";
  tie my %ccnv, 'DB_File', _catfile_ref("$inf.bdb"), O_CREAT|O_RDWR or die $!;
  while (<$infh>) {
    chomp;
    my ($gene, $chr, $start) = split /\t/;
    $ccnv{"$gene,$chr,$start"} = 1;
  }
}

### API overrides

sub bin2gene {
  my $self = shift;
  my ($bin) = @_;
  my $genes_id = $self->genes_id;
  my $s =$TABLES->{genes}{$genes_id}{bin2gene}{$bin};
  return unless $s;
  return [split(/;/,$s)];
}

sub binsize {
  my $self = shift;
  my $bins_id = $self->bins_id;
  return $TABLES->{bins}{$bins_id}{BIN_SIZE};
}

sub genes_in_bin {
  my $self = shift;
  my ($bin) = @_;
  my $genes_id = $self->genes_id;
  my $s = $TABLES->{genes}{$genes_id}{bin2gene}{$bin};
  return unless $s;
  return split(/;/,$s);
}

sub gene_indexes {
  my $self = shift;
  my ($gene) = @_;
  my $genes_id = $self->genes_id;
  my $s = $TABLES->{genes}{$genes_id}{gene_order_h}{$gene};
  return unless $s;
  return split(/:/,$s);
}

sub num_genes_indexed { scalar shift->genelist }

sub gene_as_segment {
  my $self = shift;
  my ($gene_index) = @_;
  my $genes_id = $self->genes_id;
  my $seg = $TABLES->{genes}{$genes_id}{gene_order}[$gene_index];
  return unless $seg;
  return TCGA::CNV::Segment->new($seg);
}

sub exons_as_segments {
  my $self = shift;
  my ($gene_index) = @_;
  my $genes_id = $self->genes_id;
  my $s = $TABLES->{genes}{$genes_id}{exon_order}[$gene_index];
  return unless $s;
  my @segs = split /;/, $s;
  my @ret;
  push @ret, TCGA::CNV::Segment->new($_) for @segs;
  return @ret;
}

###

sub _catfile_ref {
  my ($fn) = @_;
  return File::Spec->catfile($REFDATA_DIR,$fn);
}

sub refdata {shift->{_refdata}};
=head1 NAME

TCGA::CNV::RefData::BDB - Berkeley DB backend for genome reference TCGA::CNV::RefData object

=head1 SYNOPSIS

=head1 DESCRIPTION

This is a subclass of TCGA::CNV::RefData that uses hashes and arrays tied to BDB disk databases via L<DB_File>. It should allow us to leave most of the reference data on disk, and preserve working memory. The superclass stores everything in in-memory hashes and arrays. BDB is fast and so performance should be minimally affected.

=cut

1;
