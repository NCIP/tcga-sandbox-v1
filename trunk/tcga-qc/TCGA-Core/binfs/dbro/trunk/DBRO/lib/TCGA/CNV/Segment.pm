package TCGA::CNV::Segment;
use strict;
use warnings;

## $Source: /cgap/schaefec/cvsroot/TCGA/Segment.pm,v $
## $Revision: 1.1 $
## $Date: 2009/02/26 14:19:43 $

######################################################################
sub new {
  my ($self, $chr, $left, $right, $id, $gene_name) = @_;
  my $s = {};
  if ($chr && ($chr =~ /:/)) { # has colon, is a BDB-stored segment:
    ($chr,$left,$right,$id) = split(":",$chr);
  }
  $s->{chr} = $chr;
  $s->{left} = $left;
  $s->{right} = $right;
  $s->{name} = $gene_name;
  if (defined $id) {
    $s->{id} = $id;
  }
  return bless $s;
}

######################################################################
sub Min {
  my ($a, $b) = @_;
  if ($a < $b) {
    return $a;
  } else {
    return $b;
  }
}

######################################################################
sub Max {
  my ($a, $b) = @_;
  if ($a > $b) {
    return $a;
  } else {
    return $b;
  }
}

######################################################################
sub CreateNullSegment {
  my ($self) = @_;
  return TCGA::CNV::Segment->("", 1, 0);
}

######################################################################
sub IsNull {
  my ($self) = @_;
  if ($self->{left} > $self->{right}) {
    return 1;
  } else {
    return 0;
  }
}

######################################################################
sub Chr {
  my ($self) = @_;
  return $self->{chr};
}

######################################################################
sub Left {
  my ($self) = @_;
  return $self->{left};
}

######################################################################
sub Right {
  my ($self) = @_;
  return $self->{right};
}

######################################################################
sub Id {
  my ($self) = @_;
  return $self->{id};
}

sub Name { shift->{name} }

######################################################################
sub Pred {
  my ($self, $s) = @_;

  if ($self->IsNull || $s->IsNull) {
    die "Pred: null segment";
  }
  my $chr = $self->{chr};
  if ($chr ne $s->{chr}) {
    die "Pred: incompatible chromosomes $self->{chr}, and $s->{chr}";
  }
  ## is $self a pred of $s?
  if (
      $self->{left}  <= $s->{left} &&
      $self->{right} >= $s->{left}) {
    return 1;
  } else {
    return 0;
  }
}

######################################################################
sub Succ {
  my ($self, $s) = @_;

  if ($self->IsNull || $s->IsNull) {
    die "Succ: null segment";
  }
  my $chr = $self->{chr};
  if ($chr ne $s->{chr}) {
    die "Succ: incompatible chromosomes $self->{chr}, and $s->{chr}";
  }
  ## is $self a succ of $s?
  if (
      $self->{right} >= $s->{right} &&
      $self->{left}  <= $s->{right}) {
    return 1;
  } else {
    return 0;
  }
}

######################################################################
sub Overlap {
  my ($self, $s) = @_;

  my $chr = $self->{chr};
  if ($chr ne $s->{chr}) {
    die "Overlap: incompatible chromosomes $self->{chr}, and $s->{chr}";
  }
  if ($self->IsNull || $s->IsNull) {
    return CreateNullSegment;
  }
  my $max_left  = Max($self->Left, $s->Left);
  my $min_right = Min($self->Right, $s->Right);
  return new TCGA::CNV::Segment($chr, $max_left, $min_right);
}

######################################################################
sub Length {
  my ($self) = @_;
  return $self->{right} - $self->{left} + 1;
}

######################################################################
1;
######################################################################




