package TCGA::CNV::Util;
use strict;
use warnings;
use base 'Exporter';

our @EXPORT = qw(r_numerically numerically Log2 Max);

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

TCGA::CNV::Util - Utility functions

=head1 SYNOPSIS

use TCGA::CNV::Util;

=head1 DESCRIPTION

Exports some constants and utility math functions 

=cut
