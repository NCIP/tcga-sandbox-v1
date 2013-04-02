#$Id: Validator.pm 9606 2011-01-29 03:34:39Z jensenma $
package DataDict::Validator;
use strict;
use warnings;

our $SCHEMA_LOCATION = "http://tcga-data.nci.nih.gov/docs/xsd/tcga.nci.nih.gov_BCR_DataDictionary.xsd";

=head1 NAME

DataDict::Validator - a simple schema validator for a TCGA data dictionary

=head1 SYNOPSIS

$v = DataDict::Validator->new();
$v->validate('dictionary.xml');

# use a local schema copy
$DataDict::Validator::SCHEMA_LOCATION = "dd.xsd";
$v = DataDict::Validator->new();
$v->validate('dictionary.xml');

=head1 DESCRIPTION

Creates a simple schema validator based on L<XML::LibXML::Schema>. Validates a dictionary
against the latest TCGA data dictionary schema.

Requires L<XML::LibXML> package.

=cut

BEGIN {
  unless ( eval "require XML::LibXML; 1" ) {
    die __PACKAGE__." requires XML::LibXML; install it from CPAN";
  }
}

sub new {
  my $class = shift;
  my $schema = XML::LibXML::Schema->new( location => $SCHEMA_LOCATION );
  bless { schema => $schema }, $class;
}

sub validate { 
  my $self = shift;
  my $file = shift;
  my $doc = XML::LibXML->new->parse_file($file);
  $self->{schema}->validate($doc);
}


1;
