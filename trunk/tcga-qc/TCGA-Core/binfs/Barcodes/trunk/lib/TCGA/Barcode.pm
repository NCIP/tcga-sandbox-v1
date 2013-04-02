#$Id: Barcode.pm 17668 2012-09-14 20:19:54Z jensenma $
package TCGA::Barcode;
use strict;
use warnings;

our $AUTOLOAD;
our @available_accessors = qw( item
  barcode uuid batch disease study center analyte_type
  bcr case tss sample portion analyte plate tissue_type
  factory vial
);

my $OBJECT_ID = 1;
our $BARCODE_DATA = {};
our $BARCODE_INDEX = {};

sub new {
    my ($class, $barcode, $factory, $backend, $backend_args) = @_;
    # if we made an object for this identifier already, return that...
    return $BARCODE_INDEX->{$barcode} if $BARCODE_INDEX->{$barcode};
    # otherwise, build a new one
    my ($object_id, $object);
    if ($backend) {
      unless (eval "require TCGA::Barcode::$backend; 1") {
	die "Backend '$backend' unknown or unavailable";
      }
      $class .= "::$backend";
      $object = $class->new($backend_args);
    } 
    else {
      $object_id = $OBJECT_ID++;
      $object = bless \$object_id, $class;
    }

    for ($barcode) {
      /^TCGA-/ && do {
	$BARCODE_DATA->{$$object}{barcode} = $barcode;
	last;
      };
      /^[0-9a-f-]+$/i && do {
	$BARCODE_DATA->{$$object}{uuid} = lc $barcode;
	last;
      };
      do {
	die "Barcode '$barcode' is not a TCGA barcode or uuid";
      };
    }
    $BARCODE_DATA->{$$object}{factory} = $factory;
    $BARCODE_INDEX->{$barcode} = $object;
}

sub barcode {
  my $self = shift;
  my $bc = $BARCODE_DATA->{$$self}->{barcode};
  return $bc if $bc;
#  $bc = $self->factory->query('barcode', $self, $BARCODE_DATA);
  $bc = $self->query('barcode');
  $BARCODE_INDEX->{$bc} = $self if defined $bc;
  return $bc;
}

sub uuid  {
  my $self = shift;
  my $uuid = $BARCODE_DATA->{$$self}->{uuid};
  return $uuid if $uuid;
#  $uuid = $self->factory->query('uuid', $self, $BARCODE_DATA);
  $uuid = $self->query('uuid');
  $BARCODE_INDEX->{$uuid} = $self if defined $uuid;
  return $uuid;
}

# is_valid : does the barcode or uuid exist, according to the backend

sub is_valid {
  my $self = shift;
  return $BARCODE_DATA->{$$self}{_is_valid} if defined $BARCODE_DATA->{$$self}{_is_valid};
  if ( defined $self->barcode && defined $self->uuid ) {
    return $BARCODE_DATA->{$$self}{_is_valid} = 1;
  }
  else {
    return $BARCODE_DATA->{$$self}{_is_valid} = 0;
  }
}

sub query {
    warn "This object has not been associated with a backend; no query performed.";
    return;
}

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*://;

  return unless grep /^$method$/, @available_accessors;

  my $memo = $BARCODE_DATA->{$$self}{$method};
  if (defined $memo) {
    return $memo;
  }
  else {
#    $memo = $self->factory->query($method, $self, $BARCODE_DATA);
    $memo = $self->query($method);
    return unless defined $memo; # return null if not found
    return $BARCODE_DATA->{$$self}{$method} = $memo;
  }

}

=head1 NAME

TCGA::Barcode - Object for obtaining and storing TCGA identifiers and metadata

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut


1;
