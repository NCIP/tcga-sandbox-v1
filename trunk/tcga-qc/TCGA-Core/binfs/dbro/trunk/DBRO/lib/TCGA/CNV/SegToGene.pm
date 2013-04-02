package TCGA::CNV::SegToGene;
use strict;
use TCGA::DBRO::Util;
use TCGA::CNV::Config;
use TCGA::CNV::Segment;
use TCGA::CNV::RefData;
use TCGA::CNV::SegIO;
use Scalar::Util qw(looks_like_number);
use File::Spec;
use Tie::IxHash;
use Carp qw(carp croak);

# new:
# $refd = TCGA::CNV::RefData->new('bins200K','small', 'genome2gene','genome');
# $obj = TCGA::CNV::SegToGene->new($refd);

our $VERBOSE = 1;
our $FEEDBACK1_EVERY = 1000;
our $FEEDBACK2_EVERY = 250;

sub new {
  my $class = shift;
  my ($refdata) = @_;
  unless ($refdata && $refdata->isa('TCGA::CNV::RefData')) {
    die "RefData object required";
  }
  my %keep;
  tie %keep, 'Tie::IxHash';
  bless { _samples => undef,
	  _keep => \%keep,
	  _refdata => $refdata,
	  _gene_coverage => {},
	  _gene_l2r => {},
	  _gene_extreme => {},
	  _exon_coverage => {},
	  _exon_l2r => {},
	  _exon_extreme => {},
	  _patients => undef,
	  _tumor_ratios => undef,
	}, $class;
}

sub refdata { shift->{_refdata} }

######################################################################
# skips exon calculations by default
# call $analysis->analyzeSegFile($file, 'exons') for exon mapping only, 
# call $analysis->analyzeSegFile($file, 'both') for both genes and exons
sub analyzeSegFile {
  my $self = shift;
  my ($f,$map_to) = @_;
  $map_to ||= 'genes';
  my $segio = ref $f ? $f : TCGA::CNV::SegIO->new($f);
  my @seg_data;


  while ( my ($barcode, $chr, $start, $stop, $markers, $mean, $sample_type) = 
	  $segio->next_seg )
    {
      my %data;
      if ($self->getSamples && ! $self->keep($barcode)) {
	print STDERR "discarding $barcode\n" if $VERBOSE;
	next;
      }
      ## barcode metadata for pairing later
      # uses barcode for its own metadata -- this will break later
      my $pt = substr($barcode, 0, 12);
#      print STDERR "|" unless $self->{_patients}{$pt}; 
      $self->{_patients}{$pt}{$barcode} = $sample_type;

      if ($chr eq 23) {
	$chr = "X";
      } elsif ($chr eq 24) {
	$chr = "Y";
      }
      if (! defined $CHR_ORDER{$chr}) {
	print STDERR "bad chromosome $chr\n" if $VERBOSE;
	next;
      }
      @data{qw(seg barcode mean sample_type)} =
	(TCGA::CNV::Segment->new($chr, $start, $stop),
	 $barcode, $mean, $sample_type);
      push @seg_data, \%data;
  }

  $self->_ProcessSegSet(\@seg_data, $map_to);

  print STDERR "\n";
}


######################################################################
# set $map_to = 'genes' to skip exon calculations
sub _ProcessSegSet {
  my $self = shift;
  my ($seg_data, $map_to) = @_;
#  my ($set, $chr, $barcode, $mean, $sample_type, $map_to) = @_;
  my $BIN_SIZE = $self->refdata->binsize;
  my $feedback_i=0;
  for my $data (@$seg_data) {
    my $seg = $data->{seg};
    my ($chr, $start, $stop) = ($seg->Chr, $seg->Left, $seg->Right);
    $feedback_i++;
    if (!($feedback_i % $FEEDBACK1_EVERY)) {
      print STDERR "|";
    }
    elsif (!($feedback_i % $FEEDBACK2_EVERY)) {
      print STDERR ".";
    }

    if ($seg->Length < $MIN_SEG) {
      if ($VERBOSE) {
	printf STDERR ("short segment %d:%d-%d for %s\n",$chr,$start,$stop,$data->{barcode});
      }
      next;
    }

    next unless looks_like_number($data->{mean});
    if ($self->refdata->isa('TCGA::CNV::RefData::SQLite')) { # SQLite implementation
      my @genes = $self->refdata->segment_genes($chr,$start,$stop);
      foreach my $gene_info (@genes) {
	my ($mean, $barcode) = @{$data}{qw(mean barcode)};
	my $gi = $gene_info->{id};
	my $len = $gene_info->{stop}-$gene_info->{start};
	my $ovr = ($gene_info->{stop} > $stop ? $stop : $gene_info->{stop})-
	          ($gene_info->{start} > $start ? $gene_info->{start} : $start);
	  $self->gene_coverage->{$barcode}[$gi] += $ovr/$len;
	  $self->gene_l2r->{$barcode}[$gi] += $mean * $ovr/$len;
	  if (! defined $self->gene_extreme->{$barcode}[$gi] ||
	      abs($mean) > $self->gene_extreme->{$barcode}[$gi]) {
	    $self->gene_extreme->{$barcode}[$gi] = $mean;
	  }
      }
      if ($map_to =~ /exon|both/) {
	carp "Not mapping exons at this time";
      }
    } # new code
    else {
      # find the genome bins that are covered by the segment:
      my ($mean, $barcode) = 
	@{$data}{qw(mean barcode)};
      my ($chr_start,$chr_stop) = $self->refdata->chrbin_limits($chr);
      my $start_bin = $chr_start  + int($start / $BIN_SIZE);
      my $stop_bin  = $chr_stop  + int($stop  / $BIN_SIZE);
      # identify the genes contained in the bins covered by the segment
      my %genes;
      for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
	for my $gene ($self->refdata->genes_in_bin($bin)) {
	  next unless $gene;
	  $genes{$gene} = 1;
	}
      }
      my %gis;

      # the bins narrow down the set of genes to consider; 
      # now exclude the genes that do not actually overlap
      # with the segment

      for my $gene (keys %genes) {
	for my $gi ($self->refdata->gene_indexes($gene)) {
	  my $gseg = $self->refdata->gene_as_segment($gi);
	  if ($seg->Chr eq $gseg->Chr && 
	      $seg->Overlap($gseg)->Length > 0) {
	    $gis{$gi} = 1;
	  }
	}
      }

      # increment the coverage of the genes,
      # update the extreme (largest in abs value) mean
      # for the gene, given this segment
      # the mean is evidently expected to be a log2
      # transformation of the original raw data
      # (do the same for each exon, if desired)
      for my $gi (keys %gis) {
	if ( $map_to =~ /gene|both/ ) {
	  my $gseg = $self->refdata->gene_as_segment($gi);
	  my $ov = $seg->Overlap($gseg);
	  my $gene_overlap = $ov->Length;
	  if ($gene_overlap < $MIN_OVERLAP) {
	    next;
	  }
	  my $gene_overlapr = $gene_overlap/$gseg->Length;
	  $self->gene_coverage->{$barcode}[$gi] += $gene_overlapr;
	  $self->gene_l2r->{$barcode}[$gi] += $mean * $gene_overlapr;
	  if (! defined $self->gene_extreme->{$barcode}[$gi] ||
	      abs($mean) > $self->gene_extreme->{$barcode}[$gi]) {
	    $self->gene_extreme->{$barcode}[$gi] = $mean;
	  }
	}
	if ($map_to =~ /exon|both/) {
	  my $exon_overlap = 0;
	  my $exon_overlapr = 0;
	  for my $exon ($self->refdata->exons_as_segments($gi)) {
	    my $ov = $seg->Overlap($exon);
	    my $olength = $ov->Length;
	    if ($olength > 0) {
	      $exon_overlap += $olength;
	    }
	  }
	  if ($exon_overlap) {
	    $exon_overlapr = $exon_overlap/$self->refdata->exon_length($gi);
	  }
	  if ($exon_overlapr) {
	    $self->exon_coverage->{$barcode}[$gi] += $exon_overlapr;
	    $self->exon_l2r->{$barcode}[$gi] += $mean * $exon_overlapr;
	    if (! defined $self->exon_extreme->{$barcode}[$gi] ||
		abs($mean) > $self->exon_extreme->{$barcode}[$gi]) {
	      $self->exon_extreme->{$barcode}[$gi] = $mean;
	    }
	  }
	}
      }
    } # old code

  }
}

# Carl's original (modifed from ComputePairedGeneValues.pl) would take
# "write_seg_results" from normals and tumors in separate files. Note
# that it ignores the exon-related data from write_seg_results. It in fact
# only makes use of gene_extreme, and only outputs tumor barcodes and 
# values. It assumes if tumor and normal samples for a patient is present
# then all genes are paired.
sub _ProcessTumorNormalPairs {
  my $self = shift;
#  my $genes = $self->genes;
  croak "Dependency: Run analyzeSegFile first" unless keys %{$self->{_patients}};
  $self->{_tumor_ratios} = [];
  foreach my $pt ( keys %{$self->{_patients}} ) {
    my $bcset = $self->{_patients}{$pt};
    
    my @tumor_aliqs = grep { $bcset->{$_} =~ /$TUMOR_TYPE_MATCHER/ } keys %$bcset;
    my @normal_aliqs = grep { $bcset->{$_} =~ /$NORMAL_TYPE_MATCHER/ } keys %$bcset;

    next unless @tumor_aliqs;

    # warn if multiple aliquots, and take the first one (first pass)
    carp ">1 tumor aliquots for '$pt', taking first one" if @tumor_aliqs > 1;
    carp ">1 normal aliquots for '$pt', taking first one" if @normal_aliqs > 1;
    my $tumor_bc = $tumor_aliqs[0];
    my $normal_bc = @normal_aliqs ? $normal_aliqs[0] : undef; # undef if no normals
    my $is_paired = defined $normal_bc;

    my $a = 0;
    for my $gi ($self->refdata->gene_indexes) {
      my $gseg = $self->refdata->gene_as_segment($gi);
      my $is_cnv = $self->refdata->is_cnv($gi);
      my $tumor_extreme = $self->gene_extreme->{$tumor_bc}[$gi];
      my $normal_extreme = (defined($normal_bc) ? $self->gene_extreme->{$normal_bc}[$gi] : 0 );
      if ( !defined $tumor_extreme || !defined $normal_extreme ) {
	print STDERR "no mean value for '$tumor_bc', '$normal_bc' for gene '".$gseg->Id."', index $gi\n" if $VERBOSE;
	next;
      }
       $a++;
      my $extreme = $tumor_extreme - $normal_extreme;
      my $capped_extreme = $extreme;
      if ($extreme > $AMP_CAP_L2R) {
	$capped_extreme = $AMP_CAP_L2R;
      } elsif ($extreme < $DEL_CAP_L2R) {
	$capped_extreme = $DEL_CAP_L2R;
      } elsif ($extreme > $DEL_MAX_L2R && $extreme < $AMP_MIN_L2R) {
	$capped_extreme = 0;
      }
      my %data;
      @data{qw(tumor_bc gene_id seg_chr seg_start 
               seg_stop gene_name  extreme capped_extreme 
               paired_status cnv_status)} = 
		 ($tumor_bc, $gseg->Id, $gseg->Chr, $gseg->Left, 
		  $gseg->Right, $gseg->Name, $extreme, $capped_extreme, 
		  $normal_bc ? "PAIRED" : "UNPAIRED",
		  $is_cnv ? "CNV" : "NOCNV" );
      push @{$self->{_tumor_ratios}}, \%data;
      
      1;
    }

  }
}

######################################################################

# this writes genes in order behind barcodes.
# in the original CBSSeg2Gene.pl format
sub write_seg_results {
  my $self = shift;
  foreach my $barcode ($self->analyzed_barcodes) {
    my $gene_coverage = $self->gene_coverage->{$barcode};
    my $gene_extreme = $self->gene_extreme->{$barcode};
    my $exon_coverage = $self->exon_coverage->{$barcode};
    my $exon_extreme = $self->exon_extreme->{$barcode};
    my $gene_l2r = $self->gene_l2r->{$barcode};
    my $exon_l2r = $self->exon_l2r->{$barcode};
    for my $i (0..$self->refdata->num_genes_indexed-1) {
      my $g_c = $gene_coverage->[$i] || 0;
      my $g_l = ($gene_coverage ? $gene_l2r->[$i]/$gene_coverage : 0);
      my $x_c = $exon_coverage->[$i] || 0;
      my $x_l = ($exon_coverage ? $exon_l2r->[$i]/$exon_coverage : 0);
      my $g_x = $gene_extreme->[$i] || 0;
      my $x_x = $exon_extreme->[$i] || 0;
      for ($g_c, $g_l, $x_c, $x_l, $g_x, $x_x) {
	$_ = sprintf("%.4f", $_);
      }
      my $gseg = $self->refdata->gene_as_segment($i);
      print join("\t",
		 $gseg->Chr,
		 $barcode,
		 $gseg->Id,
		 $gseg->Left,
		 $gseg->Right,
		 $gseg->Length,
		 $g_c, $g_l, $g_x, $x_c, $x_l, $x_x
		), "\n";
    }
  }
}

# this writes the tumor/normal log2 ratios as calculated by 
# _ProcessTumorNormalPairs in original ComputePairedGeneValues.pl
# format

sub write_tumor_ratio_results {
  my $self = shift;
  return unless $self->{_tumor_ratios};
  foreach my $rec (@{$self->{_tumor_ratios}}) {
    print join("\t", @$rec), "\n";
  }
}
######################################################################
# this subr is not used in data browser data.
# was part of Carl's original script
# sub _DigestSegByCNV {
#   my $self = shift;
#   my ($chr, $start, $stop) = @_;
#   my (@cnvs, @keep);
#   my $seg = TCGA::CNV::Segment->new($chr, $start, $stop, "");
#   my $bins = $self->bins;

#   my %bin2cnv; ###### FAKE!!!!

#   my $start_bin = $bins->{chr_bins}{start}{$chr}  + int($start / $bins->{BIN_SIZE});
#   my $stop_bin  = $bins->{chr_bins}{start}{$chr}  + int($stop  / $bins->{BIN_SIZE});

#   my %counted;
#   for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
#     for my $cnv (@{ $bin2cnv{$bin} }) {
#       my ($left, $right) = ($cnv->Left, $cnv->Right);
#       ## possibly multiple bins for a cnv, but add a cnv only once
#       if (! defined $counted{$left}{$right}) {
#         if ($cnv->Overlap($seg)->Length > 0) {
#           $counted{$left}{$right} = 1;
#         }
#       }
#     }
#   }

#   ## we already know that every surviving cnv overlaps the seg
#   my $i = $start;
#   for my $left (sort numerically keys %counted) {
#     if ($i >= $stop || $left > $stop) {
#       last;
#     }
#     for my $right (sort r_numerically keys %{ $counted{$left} }) {
#       if ($left > $i) {
#         my $cseg = TCGA::CNV::Segment->new($chr, $i, $left - 1);
#         if ($cseg->Length >= $MIN_SEG) {
#           push @keep, $cseg;
#         }
#       }
#       $i = $right + 1;
#       last;    ## we took max of right, so go to next left
#     }
#   }
#   if ($stop - $i + 1 >= $MIN_SEG) {
#     push @keep, TCGA::CNV::Segment->new($chr, $i, $stop);
#   }
#   return \@keep;
# }

######################################################################
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

sub getSamples { shift->{_samples} }

sub analyzed_barcodes { keys %{ shift->gene_coverage } }

sub combined_cnvs {
  my $self = shift;
  $self->{_combined_cnvs} = shift if @_;
  return $self->{_combined_cnvs};
}

sub gene_coverage { shift->{_gene_coverage} }
sub gene_l2r { shift->{_gene_l2r} }
sub gene_extreme { shift->{_gene_extreme} }
sub exon_coverage { shift->{_exon_coverage} }
sub exon_l2r { shift->{_exon_l2r} }
sub exon_extreme { shift->{_exon_extreme} }
sub sample_type { shift->{_sample_type} }

sub keep {
  my $self = shift;
  my $bc = shift;
  return unless $self->{_keep} and $bc;
  return $self->{_keep}{$bc};
}
######################################################################
sub _ReadSampleFile {
  my $self = shift;
  my ($f) = @_;
  open(INF, $f) or die "cannot open $f";
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
    /^TCGA::CNV|HASH/ && do {
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




=head1 NAME

TCGA::CNV::SegToGene - Map CNV segment data to genes and exons

=head1 SYNOPSIS

use TCGA::CNV::SegToGene;
# parameters $MIN_SEG, $MIN_OVERLAP set in TCGA::CNV::Config, 
# included by TCGA::CNV::SegToGene
my $s2g = TCGA::CNV::SegToGene->new('bins200K','genome2gene','genome','combined.gene_overlap');
$s2g->setSamples('samplefile.txt'); # optional
$s2g->analyzeSegFile('thesegfile.seg');
$s2g->_ProcessTumorNormalPairs;
$s2g->write_results; # to stdout

=head1 DESCRIPTION

From Carl Schaefer's CBSSeg2Gene.pl

options as standalone script were:
small_bins: name of small bin definition file (level 0 input)
seg: name of file containing segmented copy number data (data input)
samples: name of file specifying samples to include (data input)
o: name of output file (output)
genes: name of file mapping small bins to genes (level 0 input)
cnv: name of file defining regions of copy number variation (level 0 input)
minseg: minimum length for a segment to be considered (parameter)
overlap: minimum overlap for a segment to be considered (parameter)

=cut
1;
