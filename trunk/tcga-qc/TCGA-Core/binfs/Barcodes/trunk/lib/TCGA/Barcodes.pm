#$Id: Barcodes.pm 17669 2012-09-17 02:30:47Z jensenma $
package TCGA::Barcodes;
use strict;
use warnings;
use base 'Exporter';
use TCGA::BCFactory;
use TCGA::Barcode;

our $VERSION = 0.1;
our @EXPORT = qw(is_valid barcode uuid batch disease center item bcr case tss sample portion analyte plate);
our $AUTOLOAD;
our $FACTORY;
eval {
  $FACTORY = TCGA::BCFactory->new("ws");
};
if ($@) {
  print STDERR "Database not available : ", substr($@, 0, 25), "\n";
}

sub AUTOLOAD {
  my @ret;
  my $method = $AUTOLOAD;
  $method =~ s/.*://;
  unless (grep /^$method$/, @EXPORT) {
    die "Method '$method' not available";
  }
  for (@_) {
    my $bc = $FACTORY->get_barcode($_);
    push @ret, $bc->$method;
  }
  return (@ret == 1) ? $ret[0] : @ret;
}

=head1 NAME

TCGA::Barcodes - a set of packages for querying TCGA barcodes/uuids

=head1 SYNOPSIS

 use TCGA::Barcodes;
 my %data;

 $bcf = TCGA::BCFactory->new('db');
 $bc = $bcf->get_barcode('TCGA-BL-A0C8-01A-11D-A10R-02');
 print $bc->barcode, " is a known barcode\n" if ($bc->is_valid);
 @data(qw(type disease batch center)) =
   ( $bc->type, $bc->disease, $bc->batch, $bc->center );

 # a one-liner to see if a uuid is valid:

 $ perl -MTCGA::Barcodes -ne 'print TCGA::BCFactory->new("db")->get_barcode(shift)->is_valid || 0' ab020f79-ba66-457c-9d77-ddd87c799538

 # even shorter
 $ perl -MTCGA::Barcodes -ne 'print is_valid("ab020f79-ba66-457c-9d77-ddd87c799538")'

=head1 DESCRIPTION

L<TCGA::Barcodes> imports necessary underlying modules to perform either DCC 
database or DCC webservice-based queries on TCGA barcodes or UUIDs, returning
Perl objects containing identifier-associated metadata as properties.

L<TCGA::Barcodes> also exports a simple functional interface to allow Perl
oneliner checks on barcode or UUID metadata or validity.

=head1 EXPORTED FUNCTIONS

Each takes as an argument either a barcode or a UUID.

=over

=item is_valid()

=item barcode()

=item uuid()

=item batch()

=item disease()

=item center()

=item type()

=item bcr()

=item case()

=item tss()

=item sample()

=item portion()

=item analyte()

=item plate()

=back

=head1 SEE ALSO

L<TCGA::Barcode>, L<TCGA::BCFactory>

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut

1;
