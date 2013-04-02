package TCGA::DBRO::Util;
use strict;
use warnings;
use base 'Exporter';

our @EXPORT = qw(r_numerically numerically Log2 Max z_scores);

# input : array ref of values
# output : array ref of z-score-transformed values in order
sub z_scores {
  my ($vals) = @_;
  my ($mu, $mu2, $sigma);
  my $n = 0;
  my @ret;
  for my $x (@$vals) {
    next unless defined $x;
    $n++;
    $mu+=$x;
  }
  $mu /= $n;
  for my $x (@$vals) {
    $mu2 += ($x - $mu)*($x - $mu);
  }
  $sigma = sqrt( $mu2 / ($n-1) );

  for my $x (@$vals) {
    push @ret, defined $x ? ($x-$mu)/$sigma : undef;
  }
  return \@ret;
}


sub LOG2 { log(2) }
sub LOG10 { log(10) }

######################################################################
sub r_numerically { $b <=> $a };
sub   numerically { $a <=> $b };

######################################################################
sub Log2 {
  my ($x) = @_;
  return log($x)/LOG2;
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
sub round {
  my ($x) = @_;
  if ($x < 0) {
    return int($x - 0.5);
  } elsif ($x > 0) {
    return int($x + 0.5);
  } else {
    return 0;
  }
}

=head1 NAME

TCGA::DBRO::Util - Utilities for data translation and transformation

=head1 SYNOPSIS

 $z_scores =TCGA::DBRO::Util->z_scores(\@values);

=head1 DESCRIPTION

=cut

1;
