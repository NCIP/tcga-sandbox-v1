#$Id: XSDParser.pm 9607 2011-01-29 03:37:02Z jensenma $
package DataDict::XSDParser;
use strict;
use warnings;

# object for parsing element definitions out of a BCR XSD
# pretty dependent on the conventions that are current
# in schemas as of version 2.3

=head1 NAME

DataDict::XSDParser - parse element defs and schema info from a BCR XML schema

=head1 SYNOPSIS

$p = DataDict::XSDParser->new();
$p->parse_xsd('TCGA_BCR.Biospecimen.xsd');
print join(",", $p->topic, $p->xml_elt_ns, $p->xsd_current_ver), "\n";
while ($elt = $p->next_elt) {
  print join("\t", $elt->xml_elt_name, $elt->xml_tier_level, $elt->xsd_intro_ver)}),"\n";
}

# schema elements without CDE:
$num_no_cde = scalar $p->orphans
for $orph ($p->orphans) {
 printf "%s has no CDE id\n", $orph->xml_elt_name;
}

=cut

use XML::Twig;
use Scalar::Util qw(looks_like_number);
use Carp;

our $AUTOLOAD;
our $VERBOSE;
my $CURRENT_FILE;
my $CURRENT_NS = '';
my $ELEMENTS = [];
my $ORPHANS = []; # elts without cde ids
my $SCHEMA_INFO = {};
my @schema_getters = qw(topic disease xml_elt_ns xsd_current_ver);
my @getters = qw(file num_elts);

sub new {
  my $class = shift;
  my $twig = XML::Twig->new(
    twig_handlers => {
		      'xs:schema' => \&parse_schema_tag,
		      'xs:element' => \&parse_element
		     }
			   );
  bless { twig => $twig, _elt_idx => 0 }, $class;
}

sub parse_xsd {
  my $self = shift;
  my $xsd_file = shift;
  # check file
  croak("'$xsd_file' is unavailable") unless (-e $xsd_file && -r $xsd_file);
  # set accumulators
  $CURRENT_FILE = $xsd_file;
  $ELEMENTS = $self->{elts} = [];
  $ORPHANS = $self->{orphans} = [];
  $SCHEMA_INFO = $self->{schema_info} = {};
  $self->{twig}->parsefile($xsd_file) or croak("'$xsd_file' cannot be parsed");
  $self->{file} = $xsd_file;
  $self->{num_elts} = scalar @$ELEMENTS;
  $self->{_parsed} = 1; # set parsed flag
  if ($VERBOSE) {
    warn "Schema element '".$self->xml_elt_ns."}".$_->xml_elt_name."' has no CDE id in $xsd_file"for (@{$self->{orphans}});
  }
  return 1;
}

sub parse { shift->parse_xsd(@_) };

sub is_parsed { return $_[0]->{_parsed}; }

sub next_elt {
  my $self = shift;
  return unless $self->is_parsed;
  return if $self->{_elt_idx} >= @{$self->{elts}};
  return ${$self->{elts}}[($self->{_elt_idx})++];
}

sub reset_elts { shift->{_elt_idx} = 0; 1; }
sub num_elts { scalar @{shift->{elts}} }
sub orphans { @{ shift->{orphans} } };

sub parse_schema_tag {
  if ($_[0] && (ref($_[0]) eq 'DataDict::XSDParser')) {
    croak("This internal method cannot be called from an object");
  }
#  $DB::single =1;
  $SCHEMA_INFO->{xsd_current_ver} = $_->att('version');
  my $ns = $CURRENT_NS = $SCHEMA_INFO->{xml_elt_ns} = $_->att('targetNamespace');
  if (!defined $ns) {
    warn "Namespace not defined in schema '$CURRENT_FILE'" if $VERBOSE;
    return;
  }
  my ($tokens) = $ns =~ m{xml/((?:[[:alpha:]]+/*)+)/*[0-9.]*$};
  my @tokens = split /\//, $tokens;
  $SCHEMA_INFO->{topic} = join(" ",@tokens);
  # kludgy convention for getting disease
  if ($tokens[0] =~ /clinical/i) {
    $SCHEMA_INFO->{disease} = uc $tokens[1] if (length($tokens[1]) == 4);
  }
}

sub parse_element {
  if ($_[0] && (ref($_[0]) eq 'DataDict::XSDParser')) {
    croak("This internal method cannot be called from an object");
  }
  return unless $_->att('name');
  # this element is a definition; proceed...
  my $elt = DataDict::XSDParser::Elt->new();
  $elt->xml_elt_name($_->att('name'));
  my @atts = $_->get_xpath(".//xs:attribute");
 ATT:
  foreach my $att (@atts) {
    my $att_name = $att->att('name');
    ($att_name eq 'cde') && do {
      $elt->public_id($att->att('default')) if looks_like_number($att->att('default'));
      next ATT;
    };
    ($att_name eq 'xsd_ver') && do {
      $elt->xsd_intro_ver($att->att('default'));
      next ATT;
    };
    ($att_name eq 'tier') && do {
      $elt->xml_tier_level($att->att('default'));
      next ATT;
    };
  }
  # fix missing tier level:
  $elt->xml_tier_level(0) if !defined($elt->xml_tier_level) || !length($elt->xml_tier_level);
  if (defined $elt->cde) {
    push @{$ELEMENTS}, $elt;
  }
  else {
    push @{$ORPHANS}, $elt;
  }
}

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  if (grep /^${method}$/, @schema_getters) {
    return unless $self->is_parsed;
    return $self->{schema_info}{$method};
  } 
  elsif (grep /^${method}$/,@getters) {
    return $self->{$method};
  }
  else {
    croak("Method '$method' unrecognized");
  }
}

sub DESTROY {
  my $self = shift;
  delete $self->{schema_info};
  delete $self->{elts};
  delete $self->{twig};
}

1;

package DataDict::XSDParser::Elt;
use strict;
use warnings;
use Carp;

our $AUTOLOAD;
my @props = qw( xml_elt_name xsd_intro_ver public_id 
		xml_tier_level );
sub new {
  my $class = shift;
  bless {},$class;
}

#cde alias
sub cde { shift->public_id(@_) }

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  croak("Method '$method' unrecognized") unless grep /^$method$/,@props;
  return $self->{$method} = shift if @_;
  return $self->{$method};
}

sub DESTROY {}
1;
