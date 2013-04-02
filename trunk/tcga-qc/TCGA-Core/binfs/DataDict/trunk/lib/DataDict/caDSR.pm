#$Id: caDSR.pm 9732 2011-02-10 01:29:42Z jensenma $
package DataDict::caDSR;

# object for doing a caDSR web service query on a cde
# and storing the results the DD needs

=head1 NAME

DataDict::caDSR - object encapsulating csDSR web service queries

=head1 SYNOPSIS
use DataDict::caDSR;

@available_fields = @DataDict::caDSR::caDSRfields;
$q = DataDict::caDSR->new();
$q->query_by_cde(64171);
$browse_url = $q->CDEBrowserLink;

print $q->cde,"\n";
for (@available_fields) {
  print join("\t", $_, $q->$_);
}

=cut

use strict;
use warnings;
use XML::Twig;
use LWP::UserAgent;
use Scalar::Util qw(looks_like_number);
use Carp;

our $AUTOLOAD;
our $VERBOSE;
our @caDSRfields = qw(caDSRlongName caDSRshortName caDSRdefinition caDSRlatestVersion
                     caDSRalternateDefinition caDSRvalueDomainHref CDEBrowserLink);

my $CADSR_API_BASE_URL = "http://cadsrapi.nci.nih.gov/cadsrapi40";
my $CDE_BROWSER_BASE_URL = "http://freestyle.nci.nih.gov/freestyle/do/cdebrowser";
my @getters = qw(cde data);

sub new {
  my $class = shift;
  my $ua = LWP::UserAgent->new();
  $ua->from('TCGA-DCC-BINF-L@list.nih.gov');
  bless {
	 ua => $ua,
	 data => {}
	 }, $class;
}

sub query_by_cde {
  my ($self,$cde) = @_;
  croak("Requires integer CDE id argument, not '$cde'") unless looks_like_number($cde);
  my $url = sprintf("${CADSR_API_BASE_URL}/GetXML?query=DataElement[publicId=%d]",$cde);
  my $resp = $self->ua->get($url) or croak("GET on '$url' failed");
  croak("GET on '$url' returned '".$resp->status_line) unless $resp->is_success;
  $self->{cde} = $cde; # cde set only here (on a query)
  my %data;
  my $t = XML::Twig->new();
  $t->parse($resp->content);
  my $root = $t->root;
  # get latest version...
  my ($r) = $root->get_xpath('//queryResponse/class[last()]');
  if (defined $r) {
    my ($v) = $r->get_xpath('./field[@name="version"]');
    my $version = $data{caDSRlatestVersion} = ($v && $v->text);
    my ($n) = $r->get_xpath('./field[@name="preferredName"]');
    $data{caDSRshortName} = ($n && $n->text) || '';
    ($n) = $r->get_xpath('./field[@name="longName"]');
    $data{caDSRlongName} = ($n && $n->text) || '';
    my ($d) = $r->get_xpath('./field[@name="preferredDefinition"]');
    my $text = ($d && $d->text) || '';
    $text =~ s/\t|\n/ /g;
    $text =~ s/"/'/g;
    $data{caDSRdefinition} = '"'.$text.'"';
    $data{caDSRalternateDefinition} = $self->_get_alternate_definition($r);

    my ($dom) = $r->get_xpath('./field[@name="valueDomain"]');
    if (defined $dom && $dom->att('xlink:href') ) {
      $data{caDSRvalueDomainHref} = $dom->att('xlink:href');
    }
    else {
      $data{caDSRvalueDomainHref} = '';
    }
    # build cde browser url
    if ($version) {
      $data{CDEBrowserLink} = $CDE_BROWSER_BASE_URL."?publicId=$cde\&version=$version";
      1;
    }
    else {
      $data{CDEBrowserLink} = '';
    }
  }
  else {
    @data{@caDSRfields} = ('') x @caDSRfields;
    warn "no usable caDSR info for public id $cde" if $VERBOSE;
  }
  $self->{data} = \%data;
  return 1;
}

sub _get_alternate_definition {
    $DB::single=1;
  my $self = shift;
  my $twig = shift;
  my $href = ($twig->get_xpath('./field[@name="definitionCollection"]'))[0]->att('xlink:href');
  return unless $href;
  my $resp = $self->ua->get($href);
  return unless $resp->is_success;
  # take the first alternative for now
  my $r = XML::Twig->new();
  $r->parse($resp->content);
   my $elt = ($r->get_xpath('//queryResponse/class[1]/field[@name="text"]'))[0];
    return ($elt ? $elt->text : $elt);
}

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  if (grep /^${method}$/, @caDSRfields) {
    return $self->{data}{$method};
  }
  else {
    my $value = shift;
    if ( grep /^$method$/, @getters ) {
      croak "'$method' method is readonly" if defined $value;
    }
    return $self->{$method} = $value if defined $value;
    return $self->{$method};
  }
}

sub DESTROY {
  my $self = shift;
  delete $self->{ua};
  delete $self->{data};
}

1;
