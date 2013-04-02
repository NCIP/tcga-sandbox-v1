#$Id: db.pm 14148 2011-11-16 05:07:44Z jensenma $
package TCGA::BCFactory::db;
use strict;
use warnings;
use base "TCGA::BCFactory";
use TCGA::BCFactory::db::config;
use DBI;

=head1 NAME

TCGA::BCFactory::db - TCGA Oracle DB backend for TCGA::BCFactory

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut

sub new {
  my $class = shift;
  bless {
	 '_dbh' => undef,
	 '_sth' => {}
	}, $class;
}

sub query {
  my $self = shift;
  my ($method,$bc_obj,$data) = @_;
  my (%result, $query, $is_valid);
  my ($bc, $uuid) = ($data->{$$bc_obj}->{barcode}, $data->{$$bc_obj}->{uuid});
  for ($method) {
    do { # default
      for $query (qw(metadata_by_aliquot disease_from_id)) {
	$self->sth($query)->execute($bc || 0, $uuid || 0);
	my @ret = $self->sth($query)->fetchrow_array;
	if (@ret) {
	  @result{@{$RESULT_FIELDS{$query}}}  = @ret;
	  for my $fld (@{$RESULT_FIELDS{$query}}) {
	    $data->{$$bc_obj}{$fld} = $result{$fld};
	    1;
	  }
	  $data->{$$bc_obj}{type} = 'aliquot' if ($query eq 'metadata_by_aliquot');
	  $is_valid = $data->{$$bc_obj}{_is_valid} = 1;
	  last;
	}
      }
      if ($is_valid) {
	$self->_type_query($bc_obj,$data) unless $data->{$$bc_obj}{type};
	return $data->{$$bc_obj}{$method};
      }
      else {
	# query failed
	$data->{$$bc_obj}{_is_valid} = 0;
	return;
      }
    };
  }
}

# query disease schema : get the item type based on the table whose
# query succeeds

sub _type_query {
  my $self = shift;
  my ($bc_obj, $data) = @_;
  my ($type,$item_type,@ret, %result);
  my ($bc, $uuid) = ($data->{$$bc_obj}->{barcode}, $data->{$$bc_obj}->{uuid});
  for $type (qw(patient sample portion analyte aliquot)) {
    $self->sth("${type}_barcode",$bc_obj->disease)->execute($bc || 0, $uuid || 0);
    @ret = $self->sth("${type}_barcode",$bc_obj->disease)->fetchrow_array;
    if (@ret) {
      $item_type = $type;
      last;
    }
  }
  return unless @ret;
  @result{@{$RESULT_FIELDS{"${item_type}_barcode"}}}  = @ret;
  for my $fld (@{$RESULT_FIELDS{"${item_type}_barcode"}}) {
    $data->{$$bc_obj}{$fld} = $result{$fld};
    1;
  }
  $data->{$$bc_obj}{type} = $item_type;
}

sub sth {
  my $self = shift;
  my ($stmt,$dis) = @_;
  return unless $STMT{$stmt};
  my $s = $STMT{$stmt};
  $s = sprintf($STMT{$stmt},$dis) if defined $dis;
  $self->{_sth}{$stmt} ||= $self->dbh->prepare($s);
}

sub dbh {
  my $self = shift;
  $ENV{ORACLE_PASS} or die "Set the env var 'ORACLE_PASS'";
  $self->{_dbh} && return $self->{_dbh};
  $self->{_dbh} = DBI->connect($CONNECT_STRING, $USERN."/".$ENV{ORACLE_PASS},"");
  $self->{_dbh}->{RaiseError} = 1;
  $self->{_dbh};
}

1;
