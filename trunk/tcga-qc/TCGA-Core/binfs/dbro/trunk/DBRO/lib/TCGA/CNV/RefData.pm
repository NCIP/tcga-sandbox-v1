package TCGA::CNV::RefData;
use strict;
use warnings;
use lib '../..';
use TCGA::CNV::Config;
use TCGA::CNV::Segment;
use File::Spec;
our $TABLES = {};

sub new {
  my $class = shift;
  my ($bins_f, $bins_id, $genes_f, $genes_id, $cnvs_f) = @_;
  my $this = {
	 _bins_f => $bins_f,
	 _bins_id => $bins_id,
	 _genes_f => $genes_f,
	 _genes_id => $genes_id,
	 _cnvs_f => $cnvs_f
	     };
  bless $this, $class;
  $this->_init;
}

sub _init {
  my $self = shift;
  my ($do_exon) = shift;
  $self->bins;
  $self->genes($do_exon);
  $self->combined_cnvs;
  return $self;
}


# if creating a new bins table, set $file to the filename (without extension)

sub bins {
  my $self = shift;
  my ($bins_id, $bins_f) = ($self->bins_id, $self->bins_f);
  if (!defined $TABLES->{bins}{$bins_id}) {
    die "Bins table with id '$bins_id' is not defined" unless (defined $bins_f);
    $self->_ReadBinFile($bins_f,$bins_id);
  }
  return $TABLES->{bins}{$bins_id};
}

sub genelist {
  my $self = shift;
  my ($do_exon) = @_;
  my ($genes_f, $genes_id) = ($self->genes_f, $self->genes_id);
  if (!defined $TABLES->{genes}{$genes_id}{genelist}) {
    die "Gene list with id '$genes_id' is not defined" unless (defined $genes_f);
    $self->_ReadGeneFile($do_exon);
  }
  return $TABLES->{genes}{$genes_id}{genelist};
}

sub genes {
  my $self = shift;
  my ($do_exon) = @_;
  my ($genes_id, $genes_f, $bins_id, $bins_f) =
    ($self->genes_id, $self->genes_f, $self->bins_id, $self->bins_f);
  if (!defined $TABLES->{genes}{$genes_id}) {
    die "Genes table with id '$genes_id' is not defined" unless (defined $genes_f);
    $self->_ReadGeneFile($do_exon);
  }
  return $TABLES->{genes}{$genes_id};
}

sub combined_cnvs {
  my $self = shift;
  my $cnv_f = $self->cnvs_f;
  if (!defined $TABLES->{cnvs}{combined}) {
    $self->_ReadCombinedCNVFile($cnv_f);
  }
  return $TABLES->{cnvs}{combined} if $TABLES->{cnvs};
  return;
}

# API

sub chrbin_limits {
  my $self = shift;
  my ($chr) = @_;
  my $bins_id = $self->bins_id;
  return ($TABLES->{bins}{$bins_id}{chr_bins}{start}{$chr}, 
	  $TABLES->{bins}{$bins_id}{chr_bins}{stop}{$chr});
}

sub bin2gene {
  my $self = shift;
  my ($bin) = @_;
  my $genes_id = $self->genes_id;
  return $TABLES->{genes}{$genes_id}{bin2gene}{$bin};
}

sub binsize {
  my $self = shift;
  my ($bins_id) = $self->bins_id;
  return $TABLES->{bins}{$bins_id}{BIN_SIZE};
}
  
sub genes_in_bin {
  my $self = shift;
  my ($bin) = @_;
  my $genes_id = $self->genes_id;
  return keys %{$TABLES->{genes}{$genes_id}{bin2gene}{$bin}};
}

sub gene_indexes {
  my $self = shift;
  my ($gene) = @_;
  return unless $gene;
  my $genes_id = $self->genes_id;
  return @{$TABLES->{genes}{$genes_id}{gene_order_h}{$gene}};
}

sub num_genes_indexed {
  my $self = shift;
  my $genes_id = $self->genes_id;
  return scalar @{$TABLES->{genes}{$genes_id}{gene_order}};
}

sub gene_as_segment {
  my $self = shift;
  my ( $gene_index) = @_;
  my $genes_id = $self->genes_id;
  return $TABLES->{genes}{$genes_id}{gene_order}[$gene_index]; # TCGA::CNV::Segment object
}

sub exons_as_segments {
  my $self = shift;
  my ($gene_index) = @_;
  my $genes_id = $self->genes_id;
  return @{$TABLES->{genes}{$genes_id}{exon_order}[$gene_index]}; # array of TCGA::CNV::Segment objects
}

sub exon_length {
  my $self = shift;
  my ($gene_index) = @_;
  my $genes_id = $self->genes_id;
  return $TABLES->{genes}{$genes_id}{gene2exon_length}[$gene_index];
}

sub is_cnv {
  my $self = shift;
  my ($seg) = @_;
  return  $TABLES->{cnvs}{combined}{join(",", $seg->Id, $seg->Chr, $seg->Left)};
}

# accessors

sub bins_f { shift->{_bins_f} }
sub bins_id { shift->{_bins_id} }
sub genes_f { shift->{_genes_f} }
sub genes_id { shift->{_genes_id} }
sub cnvs_f { shift->{_cnvs_f} }

######################################################################
# sub ReadCNVFile {
#   my ($f, $bins) = @_;
#   my %bin2cnv;
#   my $BIN_SIZE = 200000; # FAKE!
#   open(INF, $f) or die "cannot open $f";
#   while (<INF>) {
#     s/[\r\n]+//;
#     my ($label, $chr, $start, $stop) = split /\t/;
#     my $cnv_seg = TCGA::CNV::Segment->new($chr, $start, $stop, "cnv");

#     my $start_bin = $$bins{chr_bins}{start}{$chr}  +
#         int($start / $BIN_SIZE);
#     my $stop_bin  = $$bins{chr_bins}{start}{$chr}  +
#         int($stop  / $BIN_SIZE);
#     for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
#       push @{ $bin2cnv{$bin} }, $cnv_seg;
#     }
#   }
#   close INF;
# }

######################################################################
sub _ReadBinFile {
  my $self = shift;
  my ($f, $id) = ($self->bins_f, $self->bins_id);
  $f = File::Spec->catfile($REFDATA_DIR,"$f.dat");
  my (%chr_bins, @bin2chr, @bin2start, @bin2stop, @bin2mid);

  open(INF, $f) or die "cannot open $f";
  while (<INF>) {
    chomp;
    if (/^##/) {
      my ($dummy, $chr, $first_bin, $last_bin) = split /\t/;
      $chr_bins{start}{$chr} = $first_bin;
      $chr_bins{stop}{$chr}  = $last_bin;
    } else {
      my ($bin, $chr, $start, $stop) = split /\t/;
      $bin2chr[$bin] = $chr;
      $bin2start[$bin] = $start;
      $bin2stop[$bin] = $stop;
      $bin2mid[$bin] = int(($start + $stop) / 2);
    }
  }
  close INF;

  $TABLES->{bins}{$id}{chr_bins}  = \%chr_bins;
  $TABLES->{bins}{$id}{bin2chr}   = \@bin2chr;
  $TABLES->{bins}{$id}{bin2start} = \@bin2start;
  $TABLES->{bins}{$id}{bin2stop}  = \@bin2stop;
  $TABLES->{bins}{$id}{bin2mid}   = \@bin2mid;
  $TABLES->{bins}{$id}{BIN_SIZE}  = $bin2stop[$chr_bins{start}{1}] -
      $bin2start[$chr_bins{start}{1}] + 1;
  return;
}

######################################################################
# $genes_id is 'genome' or 'mirna'
# avoid binning calculation (if only a genelist is required
# by not passing $bins_id, $bins_f
sub _ReadGeneFile {
  my $self = shift;
  my ($do_exon) = @_;
  my ($genes_f, $genes_id, $bins_f, $bins_id) = 
    ($self->genes_f, $self->genes_id, $self->bins_f, $self->bins_id);
  my ($bins, $BIN_SIZE);
  if (defined $bins_id) {
    $bins = $self->bins($bins_id, $bins_f);
    $BIN_SIZE = $$bins{BIN_SIZE};
  }
  $genes_f = File::Spec->catfile($REFDATA_DIR,"$genes_f.dat");
  my (%bin2gene, %gene2seg);
  my (@gene_order, %gene_order, @gene2exon_length, @exon_order);

  open(INF, $genes_f) or die "cannot open $genes_f";
  while (<INF>) {
    chomp;
    my ($gene, $chr, $start, $stop, $exons) = split /\t/;
    my ($seg,$start_bin,$stop_bin);
    $seg = TCGA::CNV::Segment->new($chr, $start, $stop, $gene);
    push @gene_order, $seg;
    push @{ $gene_order{$gene} }, $#gene_order;
    if (defined $bins_id) {
      $start_bin = $$bins{chr_bins}{start}{$chr}  +
        int($start / $BIN_SIZE);
      $stop_bin  = $$bins{chr_bins}{start}{$chr}  +
        int($stop  / $BIN_SIZE);
      for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
	$TABLES->{genes}{$genes_id}{bin2gene}{$bin}{$gene} = 1;
	push @{ $TABLES->{genes}{$genes_id}{gene2seg}{$gene} }, $seg;
      }
    }
    if ($do_exon) {
      for my $exon(split(",", $exons)) {
	my ($estart, $estop) = split("-", $exon);
	my $eseg = TCGA::CNV::Segment->new($chr, $estart, $estop, "$gene,$exon");
	$gene2exon_length[$#gene_order] += $eseg->Length;
	push @{ $exon_order[$#gene_order] }, $eseg;
      }
    }
      
  }
  close INF;
  $TABLES->{genes}{$genes_id}{gene_order} = \@gene_order;
  $TABLES->{genes}{$genes_id}{gene_order_h} = \%gene_order;
  $TABLES->{genes}{$genes_id}{genelist} = [ map {$_->Id} @gene_order ];
  if ($do_exon) {
    $TABLES->{genes}{$genes_id}{gene2exon_length} = \@gene2exon_length;
    $TABLES->{genes}{$genes_id}{exon_order} = \@exon_order;
  }


}

sub _ReadCombinedCNVFile {
  my $self = shift;
  my ($f) = $self->cnvs_f;
  return unless $self->cnvs_f;
  open(INF, File::Spec->catfile($REFDATA_DIR,$f)) or die $!;
  while (<INF>) {
    chomp;
    my ($gene, $chr, $start) = split /\t/;
    $TABLES->{cnvs}{combined}{"$gene,$chr,$start"} = 1;
  }
  close INF;
}

=head1 NAME

TCGA::CNV::RefData - Class to provide level 0 lookup tables for CNV classes

=head1 SYNOPSIS

=head1 DESCRIPTION

This class collects all Carl's routines that put refdata (genome2gene, binned coordinates,
etc) into hashes for script lookup.

=cut
1;
