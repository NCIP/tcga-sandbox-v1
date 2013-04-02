#$Id$
package TCGA::Barcode::ws;
use lib '../..';
use strict;
use warnings;
use base "TCGA::Barcode";
use TCGA::Barcode::ws::config;
use TCGA::ThrottledUserAgent;
use JSON;
my $URL_UUID_MATCHER = qr|.*/([0-9a-f-]+)$|;
my $RETRIES = 3;

=head1 NAME

TCGA::Barcode::ws - TCGA web service query backend for TCGA::Barcode object

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut

sub new {
  my $class = shift;
  my ($args) = @_;
  my $object_id = $TCGA::Barcode::OBJECT_ID++;
  $TCGA::Barcode::BARCODE_DATA->{$object_id}{ws} =
    {
	 '_ua' => TCGA::ThrottledUserAgent->new( wait => $args->{wait} || 150,
						 agent=>'TCGA::Barcodes/0.1' )
	};
  bless \$object_id, $class;
}

sub query {
  my $self = shift;
  my ($method) = @_;
  my ($response, $is_valid);
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my ($bc, $uuid) = @$objdata{qw(barcode uuid)};
  for ($method) {
    do { # default
      my $TRIES = $RETRIES;
      while ($TRIES) {
	defined $bc && ($response = $self->ua->get(join('/',$BCURL,$bc)));
	defined $uuid && ($response = $self->ua->get(join('/',$UURL,$uuid)));
	if ($response->is_success) {
	  my $result;
	  eval {
	    $result = decode_json($response->content);
	  };
	  if ($@) {
	    $TRIES--;
	    next;
	  }
	  if ($result->{validationError}) {
	    $is_valid = $objdata->{_is_valid} = 0;
	    last;
	  }
	  else { # query succeeded
	    eval {
	      $self->_parse_json($result);
	    };
	    if ($@) {
	      $TRIES--;
	      next;
	    }
	    $TRIES = 0;
	    $is_valid = $objdata->{_is_valid} = 1;
	  }
	}
	else { # not successful
	  $TRIES = 0;
	  for ($response->code) {
	    /422/ && do { # bad bc/uuid format
	      $is_valid = $objdata->{_is_valid} = 0;
	      last;
	    };
	    do { # default - other problem
	      $is_valid = $objdata->{_is_valid} = 0;
	      last;
	    }
	  }
	}
      }
      if ($is_valid) {
	return $objdata->{$method};
      }
      else {
	return;
      }
    };
  }
}

# parse the json 
sub _parse_json {
  my ($self, $json_hash) = @_;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my ($bc, $uuid) = @$objdata{qw(barcode uuid)};
  my %result;
  # todo -- parse out the result hash
  foreach my $k (keys %JSON_FIELDS) {
    $result{$k} = $JSON_FIELDS{$k} ? _get_multilevel($json_hash, $JSON_FIELDS{$k}) : 'na';
    1;
  }
  # load the object backend
  $result{item} = lc $result{item};
  foreach my $fld (@RESULT_FIELDS) {
    $objdata->{$fld} = $result{$fld} || 'NA';
  }
  # handle aliquot barcodes
  if ($result{item} =~ /aliquot/i) {
    my ($case_uuid) = $result{case_url} =~ $URL_UUID_MATCHER;
    my ($sample_uuid) = $result{sample_url} =~ $URL_UUID_MATCHER;
    my ($analyte_uuid) = $result{analyte_url} =~ $URL_UUID_MATCHER;
    my ($case, $sample, $analyte) = $self->factory->get($case_uuid, $sample_uuid, $analyte_uuid);
    $objdata->{case} = $case->case;
    $objdata->{tissue_type} = $sample->tissue_type;
    $objdata->{vial} = $sample->vial;
    $objdata->{analyte_type} = $analyte->analyte_type;
    1;
  }
  $objdata->{type} = $result{item};
  return 1;
}

# get multilevel hash values

sub _get_multilevel {
  my ($h, $idx) = @_;
  if (ref $idx) {
    my $r = $h;
    eval { $r = (ref $r eq 'HASH' ? $r->{$_} : undef)  for @$idx; };
    return ( $@ ? undef : $r );
  }
  else {
    return $h->{$idx};
  }
}

# user agent:
sub ua { 
  my $self = shift;
  return $TCGA::Barcode::BARCODE_DATA->{$$self}{ws}{_ua};
}

1;
