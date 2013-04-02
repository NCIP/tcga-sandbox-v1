#$Id$
package TCGA::Barcode::db;
use lib '../..';
use strict;
use warnings;
no warnings qw(once);
use base "TCGA::Barcode";
use TCGA::Barcode::db::config;
use DBI;

=head1 NAME

TCGA::Barcode::db - TCGA Oracle DB query backend for TCGA::Barcode object

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen - mark.jensen -at- nih -dot- gov

=cut

sub new {
  my $class = shift;
  my $object_id = $TCGA::Barcode::OBJECT_ID++;
  my ($query, $result) = @_;
  $TCGA::Barcode::BARCODE_DATA->{$object_id}{db} = 
      {
	 '_dbh' => undef,
	 '_sth' => {}
      };
  my $obj =  bless \$object_id, $class;
  if ($query && $result) {
    $obj->_load_object($query,$result);
  }
  return $obj;
}

# $self->_load_object($query_name, \@result)
sub _load_object {
  my $self = shift;
  my ($query, $result) = @_;
  die 'Args: $query_name, \@db_row_result' unless ($query && $result && ref $result && (ref $result eq 'ARRAY'));
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my %result;
  @result{@{$RESULT_FIELDS{$query}}}  = @$result;
  for my $fld (@{$RESULT_FIELDS{$query}}) {
    $objdata->{$fld} = $result{$fld};
    1;
  }
  $objdata->{item} = lc $objdata->{item}; # norm to lower case
  $objdata->{_is_valid} = 1; # set valid (and return 1)
}

sub query {
  my $self = shift;
  my ($method) = @_;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my ($bc, $uuid) = @{$objdata}{qw(barcode uuid)};
  for ($method) {
    $self->sth('metadata')->execute($bc,$uuid);
    my @ret = $self->sth('metadata')->fetchrow_array;
    if (@ret) {
      $self->_load_object('metadata',\@ret);
      return $objdata->{$method};
    }
    else {
      # query failed
      $objdata->{_is_valid} = 0;
      return;
    }
  }
}

# return an array of objects corresponding to the descendants of 
# the object...
sub get_descendants {
  my $self = shift;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my ($bc, $uuid) = @{$objdata}{qw(barcode uuid)};
  my @desc;
  unless ($uuid) { # get uuid from barcode, if nec.
    $self->sth('uuid_from_barcode')->execute($bc);
    my $a = $self->sth('uuid_from_barcode')->fetch;
    if ($a) {
     $uuid = $objdata->{uuid} = $$a[0];
     $objdata->{_is_valid} = 1;
    }
    else {
      $objdata->{_is_valid} = 0;
      return;
    }
  }
  # got uuid now
  $self->sth('metadata_by_uuid')->execute($uuid);
  while ( my @ret = $self->sth('metadata_by_uuid')->fetchrow_array ) {
    next if grep(/$uuid/,@ret); # skip self
    my $obj = __PACKAGE__->new('metadata_by_uuid',\@ret);
    push @desc, $obj;
  }
  return @desc;
}

# query disease schema : get the item type based on the table whose
# query succeeds

sub _type_query {
  my $self = shift;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self};
  my ($type,$item_type,@ret, %result);
  my ($bc, $uuid) = @$objdata{qw( barcode uuid )};
  for $type (qw(patient sample portion analyte aliquot)) {
    $self->sth("${type}_barcode",$self->disease)->execute($bc || 0, $uuid || 0);
    @ret = $self->sth("${type}_barcode",$self->disease)->fetchrow_array;
    if (@ret) {
      $item_type = $type;
      last;
    }
  }
  return unless @ret;
  @result{@{$RESULT_FIELDS{"${item_type}_barcode"}}}  = @ret;
  for my $fld (@{$RESULT_FIELDS{"${item_type}_barcode"}}) {
    $objdata->{$fld} = $result{$fld};
    1;
  }
  $objdata->{type} = $item_type;
}

sub sth {
  my $self = shift;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self}{db};
  my ($stmt,$dis) = @_;
  return unless $STMT{$stmt};
  my $s = $STMT{$stmt};
  $s = sprintf($STMT{$stmt},$dis) if defined $dis;
  $objdata->{_sth}{$stmt} ||= $self->dbh->prepare($s);
}

sub dbh {
  my $self = shift;
  my $objdata = $TCGA::Barcode::BARCODE_DATA->{$$self}{db};
  $ENV{ORACLE_PASS} or die "Set the env var 'ORACLE_PASS'";
  $objdata->{_dbh} && return $objdata->{_dbh};
  $objdata->{_dbh} = DBI->connect($CONNECT_STRING, $USERN."/".$ENV{ORACLE_PASS},"");
  $objdata->{_dbh}->{RaiseError} = 1;
  $objdata->{_dbh};
}

1;
