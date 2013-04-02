#$Id: OCDump.pm 9605 2011-01-29 03:33:02Z jensenma $
package DataDict::OCDump;
use strict;
use warnings;

=head1 NAME

DataDict::OCDump - parse an OpenClinica-derived tab-delimited table

=head1 SYNOPSIS

=cut

use Scalar::Util qw(looks_like_number);
use HTML::Strip;
use Tie::IxHash;
use Carp;

our $AUTOLOAD;
our $VERBOSE;
# expected headers in the OC dump file
my @EXPECTED_HDRS = qw(
theCDE
theQuestion
itemName
itemDescription
response_type
response_label
response_values
raw_right_item_text
);
my $hs = HTML::Strip->new();

sub new {
  my $class = shift;
  my %h;
  tie %h, 'Tie::IxHash';
  my $obj = {
	     records => \%h
	    };
  bless $obj, $class;
}

# parse an oc dump text file
sub parse {
  my $self = shift;
  my $file = shift;
  open my $f, $file or die "'$file' : $!";
  $_ = <$f>; chomp;
  my @hdr = split /\t/;
  for my $hdr (@EXPECTED_HDRS) {
    croak("Expected OC dump file header '$_' is not present") 
      unless grep /^$hdr$/,@EXPECTED_HDRS;
  }
  while (<$f>) {
    chomp;
    my %h;
    @h{@hdr} = split /\t/;
    my $cde = $h{theCDE};
    croak("OC dump file cde '$cde' doesn't look like a cde") unless looks_like_number($cde);
    $self->{records}{$cde} = DataDict::OCDump::Record->new(_process_fields(\%h));
  }
  my %iter_hash;
  tie %iter_hash, 'Tie::IxHash';
  %iter_hash = %{$self->{records}};
  $self->{iterator} = sub { my ($k,$v) = each %iter_hash; return $v };
  return 1;
}

sub next_record { shift->{iterator}->() }

sub cdes { return keys %{shift->{records}} }

sub num_records { return scalar keys %{shift->{records}} }

# convert OC dump into datadict field values
sub _process_fields {
  my $rec = shift;
  my %cvt_rec;

# Form metadata -- not implemented yet
#  $cvt_rec{CRFname} =  undef;
#  $cvt_rec{CRFversion} = undef;
#  $cvt_rec{CRFquestionNumber} = value($r, 'question_number');
  $cvt_rec{public_id} = $rec->{theCDE};
  $cvt_rec{CRFquestionText} = $rec->{theQuestion};
  $cvt_rec{CRFdataEltLabel} = $rec->{itemName};
  my $rit = $rec->{raw_right_item_text};
  $rit = $hs->parse($rit); # strip html tags
  $hs->eof;
  $rit =~ s/\([0-9]+\)\s*//; # strip cde
  $rit =~ s/yes\/no/yesno/g;
  $rit =~ s/\/n/ /g;
  $rit =~ s/yesno/yes\/no/g;
   $cvt_rec{CRFcaBIGdefinition} = $rit;
  $cvt_rec{CRFentryAlternatives} = _parse_values($rec);

#  $rec{$_} ||= 'NA' for (keys %rec);
  return \%cvt_rec;
}

# parse out permissible values 
sub _parse_values {
  if ($_[0] && (ref($_[0]) =~ /DataDict::/)) {
    croak("This internal method cannot be called from an object");
  }
  my $rec = shift;
  return '_text_' if $rec->{response_type} eq 'text';
  if ( my $values_text = $rec->{response_values} ) {
    $values_text = $hs->parse($values_text);
    $hs->eof;
    $values_text =~ s/\/n//g;
    my @vals = split(/,/, $values_text);
    s/^\s+// for @vals;
    s/\s+$// for @vals;
    return \@vals
  }
  return;
}

1;

package DataDict::OCDump::Record;
use strict;
use warnings;

use Carp;

our $AUTOLOAD;
our $VERBOSE;
# valid getters:
my @ATTRIBUTES = qw( public_id );
my @ELEMENTS = qw( CRFquestionText CRFdataEltLabel 
		   CRFcaBIGdefinition CRFentryAlternatives
		   CRFadditionalInstructions );

sub new {
  my $class = shift;
  my $hash = shift;
  bless $hash || {}, $class;
}

sub cde { shift->public_id }

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  croak("Method '$method' unrecognized") unless grep /^$method$/, @ELEMENTS, @ATTRIBUTES;
  return $self->{$method};
}

sub DESTROY {
  my $self = shift;
  for (keys %$self) {
    delete $self->{$_}
  }
}
1;
