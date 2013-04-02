package TCGA::EXPR::ExprToGene;
use strict;
use warnings;

use TCGA::EXPR::ExprIO;
use TCGA::CNV::RefData;
use TCGA::CNV::Config;
use TCGA::DBRO::Util;
use Carp qw(carp croak);
use Scalar::Util qw(looks_like_number);

our $VERBOSE = 1;

sub new {
  my $class = shift;
  my %keep;
  my ($refdata) = @_;
  unless ($refdata && $refdata->isa('TCGA::CNV::RefData')) {
    die "RefData object required";
  }
  bless {
	 _samples => undef,
	 _keep => \%keep,
	 _refdata => $refdata,
	 _patient => undef,
	 _tumor_ratio => {},
	 _tumor_z => {}
	 }, $class;
}

# note here that there is an attempt to pair tumors and normals
# however, the level3 data (at least for Agilent) is already given
# as the log2(tumor/normal) in the raw data. The algorithm below
# will not modify this.

# do the platform splitting on the fly

sub analyzeExprFile {
  my $self = shift;
  my ($f) = @_;
  return unless $f;
  my $xprio = ref $f ? $f : TCGA::EXPR::ExprIO->new($f);
  my %values;

  # also need to split data by platform

  while ( my ($barcode,$platform_id,$platform, $gene, $value, $sample_type) = $xprio->next_rec ) {

    if ($self->getSamples && ! $self->keep($barcode)) {
      print STDERR "discarding $barcode\n" if $VERBOSE;
      next;
    }

    if (! looks_like_number($value)) {
      print STDERR "$barcode value '$value' is not numerical, skipping\n" if $VERBOSE;
      next;
    }

    ## barcode metadata for pairing later
    # uses barcode for its own metadata -- this will break later
    my $pt = substr($barcode, 0, 12);
    $self->{_patient}{$platform}{$pt}{$barcode} = $sample_type;

    # where is the log2? Already done in the level 3 expr data
    $values{$platform}{$barcode}{$gene} = $value;
  }

  foreach my $plat ( keys %{$self->{_patient}} ) {
    my $genes_with_values = {};
    foreach my $pt ( keys %{$self->{_patient}{$plat}} ) {
	print STDERR "."; # progress, on period per pt
      my $bcset = $self->{_patient}{$plat}{$pt};
      my @tumor_aliqs = grep { $bcset->{$_} =~ /$TUMOR_TYPE_MATCHER/ } keys %$bcset;
      next unless @tumor_aliqs;
      # warn if multiple aliquots, and take the first one (first pass)
      carp ">1 tumor aliquots for '$pt', taking first one" if @tumor_aliqs > 1;
      my $tumor_bc = $tumor_aliqs[0];
      foreach my $gene (@{$self->refdata->genelist}) {
	my $tumor_ratio = $values{$plat}{$tumor_bc}{$gene};
	if (!defined $tumor_ratio) {
	  print STDERR "no expr value for '$tumor_bc' for gene $gene\n" if $VERBOSE;
	  next
	}
	push @{$genes_with_values->{$tumor_bc}}, $gene;
	$self->{_tumor_ratio}{$plat}{$tumor_bc}{$gene} = $tumor_ratio;
      }
      1;
    }

    # z score over genes within tumor barcodes
    foreach my $tumor_bc (keys %{$self->{_tumor_ratio}{$plat}}) {
      my @values = map { $self->{_tumor_ratio}{$plat}{$tumor_bc}{$_} } @{$genes_with_values->{$tumor_bc}};
      my @z_scores = @{z_scores(\@values)};
      foreach my $gene (@{$genes_with_values->{$tumor_bc}}) {
	$self->{_tumor_z}{$plat}{$tumor_bc}{$gene} = shift @z_scores;
      }
    }
    print STDERR "z\n";
    1;
  }
}

# getters

sub getSamples { shift->{_samples} }

sub barcodes {
    my $self = shift;
    my ($platform) = @_;
    return unless $platform;
    return unless $self->{_tumor_ratio} && $self->{_tumor_ratio}{$platform};
    return keys %{$self->{_tumor_ratio}{$platform}};
}

sub tumor_zs { shift->tumor_z(@_)}
sub tumor_z {
  my $self = shift;
  my ($platform, $barcode) = @_;
  return unless $platform && $barcode;
  return $self->{_tumor_z}{$platform}{$barcode}
}

sub tumor_ratios { shift->tumor_ratio(@_) }
sub tumor_ratio {
  my $self = shift;
  my ($platform, $barcode) = @_;
  return unless $platform && $barcode;
  return $self->{_tumor_ratio}{$platform}{$barcode}
}

sub setSamples {
  my $self = shift;
  my $samples = shift;
  if (defined $samples && !ref $samples) {
    $self->_ReadSampleFile($samples);
  }
  elsif (ref $samples eq 'ARRAY') {
    $self->{_samples} = $samples;
  }
  return $self->{_samples};
}

sub keep {
  my $self = shift;
  my $bc = shift;
  return unless $self->{_keep} and $bc;
  return $self->{_keep}{$bc};
}

sub platforms {
  my $self = shift;
  if ( $self->{_patient} ) {
    return keys %{$self->{_patient}};
  }
  return;
}

sub refdata { shift->{_refdata} }

sub _ReadSampleFile {
  my $self = shift;
  my ($f) = @_;
  open(INF, $f) or die "$f: $!";
  while (<INF>) {
    chomp;
    $self->{_keep}{$_} = 1;
    push @{$self->{_samples}}, $_;
  }
  close INF;
}

## some explicit cleanup

sub DESTROY {
  my $self = shift;
  do_undef($self);
  return;
}

sub do_undef {
  my $ref = shift;
  return unless defined $ref && ref($ref);
  for (ref $ref) {
    /^TCGA::CNV::Segment$/ && do {
      undef $ref;
      last;
    };
    /^TCGA::CNV::RefData/ && do {
      undef $ref;
      last;
    };
    /^TCGA::EXPR|HASH/ && do {
      foreach my $k ( keys %$ref ) {
	do_undef($ref->{$k});
	undef $ref->{$k}
      }
      undef $ref;
      last;
    };
    /ARRAY/ && do {
      foreach my $elt ( @$ref ) {
	do_undef($elt);
	undef $elt;
      }
      undef $ref;
      last;
    };
    do {
      die "do_undef : huh??";
    };
  }
}



1;
