#$Id: BCFactory.pm 17668 2012-09-14 20:19:54Z jensenma $
package TCGA::BCFactory;
use strict;
use warnings;
use TCGA::Barcode;

=head1 NAME

TCGA::BCFactory - Create TCGA identifier objects with metadata

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut

sub new {
    my ($class,$backend, $backend_args) = @_;
    bless { _backend => $backend,
	    _backend_args => $backend_args}, $class;
}

# create a(n array of) barcode objects from barcodes
# (or uuids eventually)
sub get {
    my $self = shift;
    my @ret;
    for my $bc (@_) {
	# do some basic barcode validation at this point
	my $object = TCGA::Barcode->new($bc,$self, $self->{_backend}, $self->{_backend_args});
	push @ret, $object;
    }
    return (@ret == 1) ? $ret[0] : @ret;
}

# verbose alias
sub get_barcode { shift->get(@_) }

# basic backend query delegator
# here, a stub
=head2 query

 Title: query
 Usage: $factory->query($method, $barcode_object, $memo_hash)
 Function: Each backend should provide this internal function.
 Returns: the value of the $method data element associated with the barcode/
  uuid of the barcode object
 Args: 
  $method : a query method name, representing a data element
  $barcode_object : a TCGA::Barcode object
  $memo_hash : a hash reference, generally to the $BARCODE_DATA hash
 
=cut
  
sub query {
    warn "The factory has not been associated with a backend; no query performed.";
    return;
}

1;
