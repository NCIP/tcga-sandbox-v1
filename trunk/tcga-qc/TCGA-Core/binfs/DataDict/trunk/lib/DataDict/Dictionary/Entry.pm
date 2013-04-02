#$Id: Entry.pm 9732 2011-02-10 01:29:42Z jensenma $
package DataDict::Dictionary::Entry;
use strict;
use warnings;

=head1 NAME

DataDict::Dictionary::Entry - model a BCR data dictionary entry

=head1 SYNOPSIS

=head1 DESCRIPTION

Idea: fill in a dictionary entry using a flat set of getter/setter methods,
the object takes care of creating the XML (in the form of an
accessible L<XML::Twig::Elt> object).

In fact, the backend storage of this object is the XML::Twig::Elt itself.

=cut

use XML::Twig;
use Carp;

our $AUTOLOAD;
our $VERBOSE;
# lists af valid attributes and elements
# getter/setters that require special handling will have
# a defined method, the rest will fall through to 
# general methods defined in AUTOLOAD

our @ATTRIBUTES = qw( id:twig_elt name:twig_elt cde:twig_elt 
		  public_id:caDSRInfo );

our @ELEMENTS = qw( tags studies caDSRInfo 
                caDSRlongName caDSRshortName caDSRdefinition 
                caDSRalternateDefinition caDSRlatestVersion
		caDSRvalueDomainHref 
                TCGAInfo TCGAxmlElts
                CRFInfo CRFquestionText CRFdataEltLabel 
		CRFcaBIGdefinition CRFadditionalInstructions
                CRFentryAlternatives TCGAadditionalExplanation);

our @XML_ATTRIBUTES = qw( xml_elt_name xml_elt_ns xml_tier_level
			  xsd_current_ver xsd_intro_ver );


our @SPECIALS = qw(CDEBrowserLink);

# new will create a complete and correctly ordered skeleton of a dictEntry
# elt. Order of sequences should be based on the schema; now this has to be
# done 'by hand'.
# 
# if new is called with a dictEntry twig, will build an object based on this entity
sub new {
  my $class = shift;
  my $twig_elt = shift;
  $twig_elt ||= XML::Twig::Elt->new('dictEntry');
  croak("Arg must be a dictEntry XML::Twig::Elt") unless
    (
     ref($twig_elt) && 
     $twig_elt->isa('XML::Twig::Elt') && 
     ($twig_elt->tag eq 'dictEntry')
    );
  my $obj = _build_object_from_twig($twig_elt);
  bless $obj, $class;
}


sub print_xml { shift->{twig_elt}->print(@_) }
sub print { shift->print_xml(@_) }

# get the XML::Twig::Elt corresponding to the desired element
sub twig_elt {
  my ($self, $elt_name) = @_;
  return $self->{twig_elt} unless $elt_name;
  if ( grep /^$elt_name$/, @ELEMENTS ) {
    return $self->{$elt_name};
  }
  return;
}
  
# special handlers

sub name {
  my $self = shift;
  my $value = shift;
  if (defined $value) {
    warn "Entry name is defined by a convention; set xml_elt_name instead";
    return;
  }
  return $self->{twig_elt}->att('name');
}

sub CDEBrowserLink {
  my $self = shift;
  my $value = shift;
  return unless defined $self->{caDSRInfo};
  if (defined $value) {
    $self->{caDSRInfo}->set_att('xlink:href' => $value);
    return $value;
  }
  else {
    return $self->{caDSRInfo}->att('xlink:href');
  }
}

sub caDSRvalueDomainHref {
  my $self = shift;
  my $value = shift;
  return unless defined $self->{caDSRvalueDomainHref};
  if (defined $value) {
    $self->{caDSRvalueDomainHref}->set_att('xlink:href' => $value);
    $self->{caDSRvalueDomainHref}->set_att('xlink:type' => 'simple');
    return $value;
  }
  else {
    return $self->{caDSRvalueDomainHref}->att('xlink:href');
  }
}

sub CRFInfo { # stub
  my $self = shift;
  my $value = shift;
  if (defined $value) {
  }
  else {
  }
}

sub tags {
  my $self = shift;
  my @values = @_;
  return unless defined $self->{tags};
  @values = @{$values[0]} if ref $values[0] eq 'ARRAY';
  my @current_tags;
  @current_tags = map {$_->text} $self->{tags}->children('tag');
  if (@values) {
    foreach my $val (@values) {
      unless (grep /^$val$/, @current_tags) {
	$self->{tags}->insert_new_elt(last_child => 'tag',$val);
      }
    }
    return @values;
  }
  else {
    return @current_tags;
  }
}

sub studies {
  my $self = shift;
  my @values = @_;
  return unless defined $self->{studies};
  @values = @{$values[0]} if ref $values[0] eq 'ARRAY';
  my @current_studies;
  @current_studies = map {$_->text} $self->{studies}->children('study');
  if (@values) {
    foreach my $val (@values) {
      unless (grep /^$val$/, @current_studies) {
	$self->{studies}->insert_new_elt(last_child => 'study',$val);
      }
    }
    return @values;
  }
  else {
    return @current_studies;
  }
}

# with a hashref argument, insert one XMLeltInfo elt
# with no args, return an array of hashes
sub TCGAxmlElts {
  my $self = shift;
  my $value = shift;
  return unless defined $self->{TCGAxmlElts};
  if (defined $value) {
    my $elt = XML::Twig::Elt->new('XMLeltInfo');
    $elt->set_att($_, $value->{$_}) for @XML_ATTRIBUTES;
    $elt->paste('last_child' => $self->{TCGAxmlElts});
    # set entry name by a convention 
    unless ($self->{twig_elt}->att('name')) {
      my $name = $value->{xml_elt_name};
      $name =~ s/_/ /g;
      $name =~ s/(\w+)/\u$1/g;
      $self->{twig_elt}->set_att('name' => $name);
    }
    return ($value);
  }
  else {
    my @ret;
    foreach my $xml_elt ($self->{TCGAxmlElts}->children('XMLeltInfo')) {
      my %h;
      $h{$_} = $xml_elt->att($_) for @XML_ATTRIBUTES;
      push @ret, \%h;
    }
    return @ret;
  }
}

sub CRFentryAlternatives {
  my $self = shift;
  my $value = shift;
  return unless defined $self->{CRFentryAlternatives};
  my $alts = $self->{CRFentryAlternatives};
  if (defined $value) {
    # replacement elt:
    $alts = XML::Twig::Elt->new('CRFentryAlternatives');
    for ($value) {
      ref && do { # assume an arrayref
	$alts->insert_new_elt('last_child' => 'entryAlternative',$_) for @$value;
	last;
      };
      /_text_/ && do {
	$alts->insert_new_elt('entryFreeText', { freeText => 'true' });
	last;
      };
      /^xs:/ && do {
	$alts->insert_new_elt('entryAlternativeXMLType', $value);
	last;
      };
      # default assume a regexp
      do {
	$alts->insert_new_elt('entryAlternativeRegexp', $value);
	last
      };
    }
    $alts->replace($self->{CRFentryAlternatives});
    $self->{CRFentryAlternatives} = $alts;
    return $value;
  }
  else {
    my $elt = $alts->first_child;
    for ($elt->tag) {
      /regexp/i && do {
	return qr/$$elt{text}/;
      };
      /xmltype/i && do {
	return $elt->text;
      };
      /freetext/i && do {
	return '_text_';
      };
      # default : enumerated alternatives
      do {
	my @ret = map { $_->text } $alts->children;
	return \@ret;
      };
    }
  }
}

sub public_id {
  my $self = shift;
  my $value = shift;
  if (defined $value) {
    $self->{twig_elt}->set_att('cde', $value);
    $self->{caDSRInfo}->set_att('public_id', $value);
    return $value;
  }
  else {
    return $self->{caDSRInfo}->att('public_id');
  }
}


# alias for public_id...
sub cde { shift->public_id(@_) }

# internal (private)

sub _build_object_from_twig {
  if ($_[0] && (ref($_[0]) eq 'DataDict::Dictionary::Entry')) {
    croak("This internal method cannot be called from an object");
  }
  my $twig_elt = shift;
  # pull apart, create placeholder elts if nec, and put back together in order
#  $DB::single=1;
  my %ret;
  foreach (@ELEMENTS) {
    ($ret{$_}) = $twig_elt->get_xpath(".//$_");
    if (!defined $ret{$_}) {
      $ret{$_} = XML::Twig::Elt->new($_);
    }
  }
  foreach (@ELEMENTS) {
    $ret{$_}->cut;
  }
  # note paste() defaults to first_child position...
  # tags below are in schema order, but prefaced by 'reverse'
  $ret{twig_elt} = $twig_elt;

  # dictEntry sequence
  $ret{$_}->paste($twig_elt) for reverse qw( tags studies caDSRInfo TCGAInfo );
  # caDSRInfo sequence
  $ret{$_}->paste($ret{caDSRInfo}) for 
    reverse qw( caDSRlongName caDSRshortName caDSRdefinition
		caDSRalternateDefinition caDSRlatestVersion 
                caDSRvalueDomainHref );
  # TCGAInfo sequence
  $ret{$_}->paste( $ret{TCGAInfo} ) for 
    reverse qw( TCGAxmlElts CRFInfo CRFquestionText CRFdataEltLabel 
                CRFcaBIGdefinition
		CRFadditionalInstructions CRFentryAlternatives 
		TCGAadditionalExplanation );
  return \%ret;
}
  
sub AUTOLOAD {
  my $self = shift;
  my $value = shift;
  croak("No class methods are available") unless ref $self;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  
  if (my ($token) = grep(/^${method}:/, @ATTRIBUTES)) {
    my ($att_name,$elt_name) = split /:/,$token;
    return unless defined $self->{$elt_name};
    if (defined $value) {
      $self->{$elt_name}->set_att($att_name, $value);
      return $value;
    }
    else {
      return $self->{$elt_name}->att($att_name);
    }
  }
  elsif (grep(/^$method$/, @ELEMENTS)) {
    return unless defined $self->{$method};
    if (defined $value) {
      $self->{$method}->set_text($value);
      return $value;
    }
    else {
      return $self->{$method}->text;
    }
  }
  else {
    croak("Method '$method' unrecognized");
  }

}


sub DESTROY {
  my $self = shift;
  foreach (keys %$self) {
    delete $self->{$_};
  }
}

1;
