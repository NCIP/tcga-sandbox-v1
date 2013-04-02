#$Id: Dictionary.pm 9615 2011-01-31 03:06:44Z jensenma $
package DataDict::Dictionary;
use strict;
use warnings;

=head1 NAME

DataDict::Dictionary - a wrapper for a BCR data dictionary document

=head1 SYNOPSIS

=head1 DESCRIPTION

A L<XML::Twig> object provides the backend for the dictionary object. 
The twig is a scaffold for dictEntry elements, which are themselves
L<XML::Twig::Elt>s, exposed via the L<DataDict::Dictionary::Entry> 
class. 

=cut

use lib '..'; # may change?
use DataDict::Dictionary::Entry;
use XML::Twig;
use Tie::IxHash;
use Carp;

#accumulators
my $ENTRIES = {};

our $VERBOSE = 1;
my $DICT_NS_ATTS =  { 'xmlns' => 'http://tcga.nci.nih.gov/BCR/DataDictionary/1.0',
		      'xmlns:xlink' => 'http://www.w3.org/1999/xlink',
		      'xmlns:xsi' => 'http://www.w3.org/2001/XMLSchema-instance',
		      'xsi:schemaLocation' => 'http://www.w3.org/1999/xlink http://www.w3.org/1999/xlink.xsd'};

sub new {
  my $class = shift;
  my %h;
  tie %h, 'Tie::IxHash';
  my $twig = XML::Twig->new(twig_handlers => {dictEntry => \&parse_dictEntry});
  $twig->set_root( XML::Twig::Elt->new('dictionary', $DICT_NS_ATTS ) );
  bless { 
	 entries => \%h,
	 twig => $twig
	}, $class;
}

sub parse_xml {
  my $self = shift;
  my $dict_file = shift;
  croak("'$dict_file' is unavailable") unless (-e $dict_file && -r $dict_file);
  # set accumulator
  $ENTRIES = $self->{entries};
  $self->{twig}->parsefile($dict_file) or croak("'$dict_file' cannot be parsed");
  # add schema info if not present
  my $root = $self->{twig}->root;
  if (!$root->att('xmlns')) {
    $root->set_att( $_ => $DICT_NS_ATTS->{$_} ) for keys %$DICT_NS_ATTS;
  }
  $self->add_entry( values %{$self->{entries}} );
  return 1;
}

sub parse { shift->parse_xml(@_) }

sub twig { shift->{twig} }

sub cdes { keys %{shift->{entries}} }

 
sub print {
  my $self = shift;
  # strip completely empty leaves:
  for ($self->twig->get_xpath('//*')) {
    $_->delete if ($_->is_empty && $_->has_no_atts);
  }
  $self->twig->print(@_) 
}

sub get_entry_by_cde {
  my ($self,$cde) = @_;
  return $self->{entries}{$cde};
}

sub num_entries { return scalar keys %{shift->{entries}} }

sub add_entry {
  my $self = shift;
  my @entries = @_;
  for (@entries) {
    croak("Args must be DataDict::Dictionary::Entry objects") unless ref && $_->isa('DataDict::Dictionary::Entry');
    next if ref($_->twig_elt->twig) eq ref($self->twig); # elt already in twig
    if (!defined $_->cde) {
      warn "Entry named '".$_->name."' has no CDE public id. Skipping..." if $VERBOSE;
      next;
    }
    my $current_elt = $self->{entries}{$_->cde};
    if (defined $current_elt) {
      warn "Entry named '".$_->name."' is replacing a current entry" if $VERBOSE;
      $_->twig_elt->replace($current_elt);
      $current_elt->twig_elt->delete;
      $self->{entries}{$_->cde} = $_;
    }
    else {
      $self->{entries}{$_->cde} = $_;
      $_->twig_elt->paste( 'last_child' => $self->{twig}->root );
    }
  }
}

sub del_entry {
  my $self = shift;
  my @entries = @_;
  for (@entries) {
    croak("Args must be DataDict::Dictionary::Entry objects") unless ref && $_->isa('DataDict::Dictionary::Entry');
    if (!defined $_->cde) {
      warn "Entry named '".$_->name."' has no CDE public id. Skipping...";
      next;
    }
    delete $self->{entries}{$_->cde};
    $_->delete;
  }
}

# creates DataDict::Dictionary::Entry objects
sub parse_dictEntry {
  if ($_[0] && (ref($_[0]) eq 'DataDict::Dictionary')) {
    croak("This internal method cannot be called from an object");
  }
  my $entry = DataDict::Dictionary::Entry->new($_);
  if (!defined $entry->cde) {
    warn "Entry named '".$entry->name."' has no cde. Skipping...";
    return;
  }
  $ENTRIES->{$entry->cde} = $entry;
}

1;
